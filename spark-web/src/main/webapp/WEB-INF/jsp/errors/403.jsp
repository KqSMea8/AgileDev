<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style type="text/css"> 
	div#error { margin:5% 10%;  padding:0px; width:80%; background:#E6E6E6;}
	div#error-inner {  padding:2em 10em;  min-height: 160px; vertical-align:middle;}
</style>
 
<div id="error">
	<div id="error-inner">
		<h3><fmt:message key="global.exception.403"/></h3>
		<a href="<%= request.getContextPath() %>/j_spring_security_logout"><fmt:message key="header.logout"/></a>
	</div>
</div>
 