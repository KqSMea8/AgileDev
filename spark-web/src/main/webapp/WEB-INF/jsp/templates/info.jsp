<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.baidu.spark.util.WebUtils;"%>
<c:if test="${spark_web_info_message!=null}">
<div class="success-message">
	<div class="bar-inner-wrapper"><c:out value="${spark_web_info_message}"/></div>
</div>
</c:if>