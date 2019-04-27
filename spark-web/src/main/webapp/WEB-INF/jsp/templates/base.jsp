<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="com.baidu.spark.security.SparkSystemResource,com.baidu.spark.security.SparkPermission;"%>
<% 	request.setAttribute("sparkSystemResource",SparkSystemResource.getResource());
	request.setAttribute("READ",SparkPermission.READ.getMask());
	request.setAttribute("CREATE_CHILDREN",SparkPermission.CREATE_CHILDREN.getMask());
	request.setAttribute("WRITE",SparkPermission.WRITE.getMask());
	request.setAttribute("DELETE",SparkPermission.DELETE.getMask());
	request.setAttribute("ADMIN",SparkPermission.ADMIN.getMask());
%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title><fmt:message key="global.title" /></title>
		<link rel="shortcut icon" type="image/ico" href="<%= request.getContextPath() %>/favicon.ico" /> 
		<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/main.css" />
		<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/cupertino/jquery-ui-1.8.2.custom.min.css" />
		<c:if test="${ _styles != null }">
			<c:forTokens var="style" items="${ _styles }" delims=",">
			<c:if test="${ fn:length(fn:trim(style)) >0 }">  
				<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/${ fn:trim(style) }" />
			</c:if>
			</c:forTokens>
		</c:if>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery.json-2.2.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-ui-1.9m2.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/bgiframe/jquery.bgiframe.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery.hashchange.min.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/spark.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/spark-menu.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/spark-history.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/my97calendar/WdatePicker.js"></script>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/pngfix/jquery.pngFix.pack.js"></script>
		<c:if test="${ _scripts != null }">
			<c:forTokens var="script" items="${ _scripts }" delims=",">
			<c:if test="${ fn:length(fn:trim(script)) >0 }">  
				<script type="text/javascript" src="<%= request.getContextPath() %>/${ fn:trim(script) }"></script>
			</c:if>
			</c:forTokens>
		</c:if>
		<script type="text/javascript">
			// Context Path
			Spark.constants.CONTEXT = "<%= request.getContextPath() %>";
			// Current User
			<% com.baidu.spark.model.User spark_current_user = com.baidu.spark.util.SpringSecurityUtils.getCurrentUser(); %>
			Spark.constants.CURRENT_USER = {
				id : <%= spark_current_user.getId()%>,
				name : "<%= spark_current_user.getName()%>",
				username : "<%= spark_current_user.getUsername()%>",
				email : "<%= spark_current_user.getEmail()%>" 
			};
			// 国际化
			Spark.messages = <spark:message />;
			Spark.constants.LOCALE = "<%= request.getHeader ("Accept-Language") %>";
			if (!Spark.constants.LOCALE || Spark.constants.LOCALE == "null"){
				Spark.constants.LOCALE = "zh_CN";
			}
			// jQuery AJAX全局配置
			$.ajaxSetup({ 
				cache: false,
				error: function(xhr, status){
					 Spark.util.handleAjaxError(xhr);
				}
			});
			// js中需要用到的配置文件读取
			Spark.configs = {
				pictureServer : "<spark:config key="richtext.pictrue.server"/>"
			};
		</script>
	</head>

	<body>
		<!-- #TEMPLATE-BODY# -->
	</body>
</html>