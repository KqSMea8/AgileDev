package com.baidu.spark.web;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping(value = "/uploadfile")
public class FileUploadController {

	@RequestMapping(value = "/uploadimage", method = { RequestMethod.GET, RequestMethod.POST  })
	public void handleRequestInternal(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile imgFile = multipartRequest.getFile("fileToUpload");
		Calendar c = new GregorianCalendar();
		//图片的命名格式:年/月/时间.扩展名
		String fileName = c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH)+1) + "/" + new Long(new Date().getTime()).toString() + getFileExtName(imgFile.getOriginalFilename());
		fileName = fileName.toLowerCase();
		File file = new File(request.getSession().getServletContext().getRealPath("/") + "upload/" + fileName);
		file.mkdirs();
		imgFile.transferTo(file);
//		return new Message(fileName);
		response.setContentType("text/plain");
		response.getWriter().print(fileName);
	}
	
	private String getFileExtName(String fileName){
		int i = fileName.lastIndexOf(".");
		if (-1 == i){
			return "";
		}
		String extName = fileName.substring(i);
		return extName;
	}
}
