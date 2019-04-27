package com.baidu.spark.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用于显示错误用的前端控制器.
 * 
 * @author GuoLin
 *
 */
@Controller
@RequestMapping("/errors")
public class ErrorControler {

	@RequestMapping("/{status}")
	public String status(@PathVariable Integer status, HttpServletResponse response) {
		response.setStatus(status);
		return "/errors/" + String.valueOf(status);
	}
	
}
