package com.baidu.spark.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.baidu.spark.dao.AttachmentDao;
import com.baidu.spark.exception.AttachmentException;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.User;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.AttachmentService;
import com.baidu.spark.util.DateUtils;
import com.baidu.spark.util.SparkConfig;
import com.baidu.spark.util.SpringSecurityUtils;

/**
 * 附件服务实现类
 * 
 * @author shixiaolei
 * 
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

	/** 附件根路径 */
	private static final String basePath  = SparkConfig.getSparkConfig("attachment.basePath");
	/** 附件临时文件路径夹名 */
	private static final String TEMP_FOLDER_NAME = "temp";
	/** 附件临时文件路径 */
	private static final String tempPath  = basePath + System.getProperty("file.separator") + TEMP_FOLDER_NAME;
	/** 附件时间戳格式 */
	private static final String TIMESTAMP_FORMAT = "_yyyyMMddHHmmss";
	/** 附件操作类 */
	private AttachmentDao dao;
	/** 日志*/
	private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);
	
	/**
	 * 初始化附件文件夹,如果它们不存在,则创建一个
	 */
	static{
		ensureDirectoryExists(basePath);
		ensureDirectoryExists(tempPath);
	}
	
	private static void ensureDirectoryExists(String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				FileUtils.forceMkdir(file);
			} catch (IOException e) {
				logger.warn("fail to init folder {}", path, e);
				throw new AttachmentException(AttachmentException.OpType.INIT, e);
			}
		}
	}
	
	@Override
	public List<Attachment> getAttachments(Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getId());
		String hql = "from Attachment where card=? and status = ? order by uploadTime desc";
		return dao.find(hql, card, Attachment.NORMAL_STATUS);
	}
	 
	@Override
	public Attachment upload(MultipartFile mf, String note) {
		Assert.notNull(mf);
		Assert.hasText(mf.getOriginalFilename());
		Attachment attachment = saveBean(mf, note);
		saveFile(mf, attachment);
		return attachment;
	}
	
	@Override
	public Attachment saveToCard(Attachment attachment, Card card) {
		Assert.notNull(card);
		Assert.notNull(card.getId());
		Assert.notNull(attachment);
		Assert.notNull(attachment.getId());
		String realPath = generatePath(attachment, card);
		moveFromTempDirToCard(realPath, attachment);
		attachment.setPath(realPath);
		attachment.setCard(card);
		dao.update(attachment);
		return attachment;
	}

	@Override
	public Attachment replace(MultipartFile mf, String note ,Attachment oldAttachment) {
		Assert.notNull(mf);
		Assert.hasText(mf.getOriginalFilename());
		Assert.notNull(oldAttachment);
		Assert.notNull(oldAttachment.getId());
		Assert.isTrue(oldAttachment.isValid());
		//保存新附件
		Attachment attach = upload(mf, note);
		attach.setOldAttachment(oldAttachment);
		saveToCard(attach, oldAttachment.getCard());
		//置旧原附件
		oldAttachment.setStatus(Attachment.DELETED_STATUS);
		dao.update(oldAttachment);
		
		return attach;
	}
	
	@Override
	public void remove(Attachment attach) {
		Assert.notNull(attach);
		Assert.notNull(attach.getId());
		Assert.notNull(attach.getCard());
		attach.setStatus(Attachment.DELETED_STATUS);
		dao.update(attach);
	}
	
	@Override
	public Attachment getById(Long id) {
		return dao.get(id);
	}
	
	@Override
	public void transfer(Attachment attachment, OutputStream os) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			File file = getRealFile(attachment);
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(os);
			byte[] buffer = new byte[4096];
			int n = -1;
			while ((n = bis.read(buffer, 0, 4096)) > -1) {
				bos.write(buffer, 0, n);
			}
			bos.flush();
			os.flush();
		} catch (IOException e) {
			logger.warn("failed to download attachments", e);
			throw new AttachmentException(AttachmentException.OpType.DOWNLOAD, e);
		} finally {
			close(bis);
			close(bos);
			close(os);
		}
	}

	@Override
	public File getRealFile(Attachment attachment){
		return new File(basePath + System.getProperty("file.separator") + attachment.getPath());
	}

	private Attachment saveBean(MultipartFile mf, String note) {
		Attachment attachment = new Attachment();
		String originalName = mf.getOriginalFilename();
		attachment.setOriginalName(originalName);
		attachment.setUploadTime(new Date());
		attachment.setUploadUser((User)SpringSecurityUtils.getCurrentUser());
		attachment.setStatus(Attachment.NORMAL_STATUS);
		attachment.setNote(note);
		attachment.setType(generateType(originalName));
		attachment.setPath(generateTempPath(attachment));
		attachment.setName(generateName(originalName));
		dao.save(attachment);
		return attachment;
	}

	private void saveFile(MultipartFile mf, Attachment attachment) {
		File dest = getRealFile(attachment);
		try {
			mf.transferTo(dest);
		} catch (IllegalStateException e) {
			handleException(e);
		} catch (IOException e) {
			handleException(e);
		}
	}
	
	private String generateName(String originName) {
		String trimedName = originName.replaceAll(" ", "_");
		int index = trimedName.lastIndexOf(".") ; 
		int offset = index == -1 ? trimedName.length() : index;
		String timestamp = DateUtils.formatDate(new Date(), TIMESTAMP_FORMAT);
		return new StringBuilder(trimedName).insert(offset, timestamp).toString();
	}
	
	private String generateType(String originalName) {
		return originalName.contains(".")? originalName.substring(originalName.lastIndexOf(".") + 1) : "";
	}
	
	private String generatePath(Attachment attachment, Card card) {
		return card.getSpace().getPrefixCode() + card.getSpace().getId() + System.getProperty("file.separator")
				+ card.getSequence() + System.getProperty("file.separator") + attachment.getName();
	}
	
	private String generateTempPath(Attachment attachment){
		return TEMP_FOLDER_NAME + System.getProperty("file.separator") + attachment.getOriginalName()  
				+ attachment.getUploadUser().getId() + System.currentTimeMillis();
	}
	
	private void moveFromTempDirToCard(String path, Attachment attachment){
		try {
			File source = getRealFile(attachment);
			File dest = new File(basePath + System.getProperty("file.separator") + path);
			FileUtils.copyFile(source, dest);
			source.delete();
		} catch (IOException e) {
			handleException(e);
		}
	}
	
	private void close(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				logger.warn("failed to download attachments", e);
			}
		}
	}

	private void handleException(Exception e) {
		logger.warn("fail to save file", e);
		throw new AttachmentException(AttachmentException.OpType.UPLOAD, e);
	}

	@Autowired
	public void setDao(AttachmentDao dao) {
		this.dao = dao;
	}

}
