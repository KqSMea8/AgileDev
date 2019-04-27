package com.baidu.spark.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baidu.spark.exception.AttachmentException;
import com.baidu.spark.exception.ResponseStatusException;
import com.baidu.spark.model.Attachment;
import com.baidu.spark.model.OpType;
import com.baidu.spark.model.Space;
import com.baidu.spark.model.card.Card;
import com.baidu.spark.service.AttachmentService;
import com.baidu.spark.service.CardBasicService;
import com.baidu.spark.service.CardHistoryService;
import com.baidu.spark.service.SpaceService;
import com.baidu.spark.util.WebUtils;

/**
 * 附件管理前端控制器
 * 
 * @author shixiaolei
 * 
 */
@Controller
@RequestMapping("spaces/{prefixCode}")
public class AttachmentController {

	private AttachmentService attachmentService;

	private CardBasicService cardService;

	private SpaceService spaceService;
	/** 卡片历史记录服务 */
	private CardHistoryService historyService;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 上传附件到临时文件夹
	 * @param attachment 
	 * @param note
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/attachments/upload", method = RequestMethod.POST)
	public void upload(@RequestParam String attachment, @RequestParam String note,
			HttpServletRequest request, HttpServletResponse response) {
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request; 
		MultipartFile attachment_file = multipartRequest.getFile(attachment);
		Attachment attach = null;
		try {
			attach = attachmentService.upload(attachment_file, note);
		} catch (AttachmentException e) {
			WebUtils.internalServerError("card.attachment.failedToUpload");
		}  
		getResponseWriter(response).print(attach.getId());
	}

	/**
	 * 替换附件
	 * @param response
	 * @param prefixCode
	 * @param sequence
	 * @param oldId
	 * @param attachmentReuploadFile
	 * @param note
	 */
	@RequestMapping(value = "/cards/{sequence}/attachments/{id}/replace", method = RequestMethod.POST)
	public void relace(HttpServletResponse response, @PathVariable String prefixCode,  
			@PathVariable Long sequence, @PathVariable("id") Long oldId,
			@RequestParam MultipartFile attachmentReuploadFile, @RequestParam String note) {
		
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		WebUtils.notFoundIfNull(card);
		Attachment oldAttachment = attachmentService.getById(oldId);
		WebUtils.notFoundIfNull(oldAttachment);
		
		Attachment attach = null;
		try {
			attach =  attachmentService.replace(attachmentReuploadFile, note,  oldAttachment);
		} catch (AttachmentException e) {
			WebUtils.internalServerError("card.attachment.failedToUpload");
		}  
		historyService.saveHistory(card, OpType.Change_Attachment, attach
				.getUploadUser());
		getResponseWriter(response).print(attach.getId());

	}

	/**
	 * 下载附件
	 * @param prefixCode
	 * @param sequence
	 * @param id
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/cards/{sequence}/attachments/{id}/download", method = RequestMethod.GET)
	public void download(@PathVariable String prefixCode,  @PathVariable Long sequence, @PathVariable Long id,
			HttpServletRequest request, HttpServletResponse response) {
		
		Space space = spaceService.getSpaceByPrefixCode(prefixCode);
		WebUtils.notFoundIfNull(space);
		Card card = cardService.getCardBySpaceAndSeq(space, sequence);
		WebUtils.notFoundIfNull(card);
		Attachment attach = attachmentService.getById(id);
		WebUtils.notFoundIfNull(attach);
		
		File file = attachmentService.getRealFile(attach);
		response.setContentType(generateMineType(request, attach));
		response.setHeader("Content-Disposition", "attachment;filename=\""
					+  encode(attach.getOriginalName()) + "\"");
		response.setContentLength((int) file.length()); // 设置下载内容大小
		try {
			OutputStream os = response.getOutputStream();
			attachmentService.transfer(attach, os);
		} catch (AttachmentException e) {
			WebUtils.internalServerError("card.attachment.failedToDownload");
		} catch (IOException e) {
			WebUtils.internalServerError("card.attachment.failedToDownload");
		} 
	}

	private String generateMineType(HttpServletRequest request, Attachment attach) {
		String path = attach.getPath();
		String mimetype = request.getSession().getServletContext().getMimeType(
				path);
		if (mimetype == null) {
			mimetype = "application/octet-stream;charset=utf-8";
		}
		// IE 的话就只能用 IE 才认识的头才能下载 HTML 文件, 否则 IE 必定要打开此文件!
		String ua = request.getHeader("User-Agent"); // 获取终端类型
		if (ua == null)
			ua = "User-Agent:Mozilla/4.0(compatible; MSIE 6.0;)";
		boolean isIE = ua.toLowerCase().indexOf("msie") != -1; // 是否为 IE
		if (isIE) {
			mimetype = "application/x-msdownload";
		}
		return mimetype;
	}
	
	private String encode(String str){
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.warn("No Such Encoding : utf-8", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "global.exception.noSuchEncoding" ,e);
		}
	}
	
	private PrintWriter getResponseWriter(HttpServletResponse response){
		try {
			response.setContentType("text/plain");
			return response.getWriter();
		} catch (IOException e) {
			logger.warn("fail to get writer in HttpServletResponse", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Autowired
	public void setAttachmentService(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@Autowired
	public void setCardService(CardBasicService cardService) {
		this.cardService = cardService;
	}

	@Autowired
	public void setSpaceService(SpaceService spaceService) {
		this.spaceService = spaceService;
	}
	
	@Autowired
	public void setHistoryService(CardHistoryService historyService) {
		this.historyService = historyService;
	}

}
