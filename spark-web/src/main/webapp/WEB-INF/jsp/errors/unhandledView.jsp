<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/main.jsp">
	<h4><fmt:message key="global.exception.errorMsg"/></h4>
	<fmt:message key="global.exception.illegalArgumentExceptionName"/>：<fmt:message key="${exception.message}"/>
</spark:template>