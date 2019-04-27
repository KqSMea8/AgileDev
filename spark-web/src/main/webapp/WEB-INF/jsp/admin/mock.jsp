<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	</head>
	<body>
	<c:if test="<%= com.baidu.spark.util.SparkConfig.isDebugMode()%>">
	<div class="title-bar">
		<h3><fmt:message key="admin.mock"></fmt:message></h3>
	</div>
	<div id="search-user">
		 <div>
		 	<form:form action="mock" method="PUT">
			  	<input id="username" name="username"/>
	           	<button type="botton" id="shadow"><fmt:message key="admin.shadow.play"/></button>
           	</form:form>
		 </div>
	</div>
	</c:if>
	</body>
</html>