package com.baidu.spark.service;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.multipart.MultipartFile;

import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.security.SparkPermissionEnum;
import com.baidu.spark.security.annotation.SecuredMethod;
import com.baidu.spark.security.annotation.SecuredObj;

/**
 * 附件服务接口
 * 
 * @author shixiaolei
 * 
 */
public interface AttachmentService {

	/**
	 * 接收上传的文件. 一方面保存物理文件到<strong>临时文件夹</strong>,另一方面创建Attachment持久化对象.
	 * @param mf 上传的文件
	 * @param note 备注
	 * @return 根据文件生成的持久化对象
	 */
	public Attachment upload(MultipartFile mf, String note);
	
	/**
	 * 将临时文件夹里的文件,转移到相应card的附件文件夹中;同时修改持久化对象的状态.
	 * @param attachment 持久化对象
	 * @param card 附件所属卡片
	 * @return 修改状态后的持久化对象
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.WRITE)
	public Attachment saveToCard(Attachment attachment, @SecuredObj Card card);
	
	/**
	 * 用传上来的MultipartFile存储到本地,并使用该文件<strong>替换</strong>指定的附件.
	 * <p>
	 * 所谓替换是指以下几件事： 
	 * <ol> 1.新建新上传附件Bean信息,存储并返回. </ol>
	 * <ol> 2.将被替换的附件Bean status字段值为1，表明已删除</ol>
	 * <ol> 3.此操作并<strong>不会</strong>将文件系统中的文件真正删除，而是通过2所述的status字段软删除.</ol>
	 * </p>
	 * @param mf 上传的MultipartFile
	 * @param oldAttachment  要替换的附件
	 * @param note 备注
	 * @return 附件Bean
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.WRITE)
	public Attachment replace(MultipartFile mf,  String note, 
			@SecuredObj(clazz = Card.class, idPropertyName = "card.id") Attachment oldAttachment);

	/**
	 * 删除指定附件.
	 * <p>
	 * 所谓删除是指,将被替换的附件Bean status字段值为1，表明已删除. 此操作并<strong>不会</strong>将文件系统中的文件真正删除，而是通过2所述的status字段软删除.
	 * </p>
	 * @param attach 指定附件
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.WRITE)
	public void remove(@SecuredObj(clazz = Card.class, idPropertyName = "card.id") Attachment attach);

	/**
	 * 得到指定卡片下的全部有效（即未被删除）的附件Bean.
	 * @param card  卡片
	 * @return 得到指定卡片下的全部有效（即未被删除）的附件Bean
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.READ)
	public List<Attachment> getAttachments(@SecuredObj Card card);

	/**
	 * 按照Id得到持久化的附件Bean
	 * @param id 附件ID
	 * @return 附件Bean
	 */
	public Attachment getById(Long id);
	
	/**
	 * 将附件以字节流的形式传输
	 * @param attachment 附件持久化对象
	 * @param os 输出字节流
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.READ)
	public void transfer(@SecuredObj(clazz = Card.class, idPropertyName = "card.id")Attachment attachment, OutputStream os);
	
	/**
	 * 得到附件持久化对象对应的实际文件
	 * @param attachment 附件持久化对象
	 * @return 文件
	 */
	@Secured("commonVoter")
	@SecuredMethod(permission=SparkPermissionEnum.READ)
	public File getRealFile(@SecuredObj(clazz = Card.class, idPropertyName = "card.id")Attachment attachment);

}
