<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/main.jsp">
	<div id="error">
		<div id="error-inner">
		<h1><fmt:message key="global.exception.errorMsg"/></h1>
		<div>Error Message：${exception.message}</div>
		<div class="error-footer"><a class="enter" href="<c:url value="/"/>">
			<fmt:message key="button.back-home"/></a>
		</div>
		</div>
	</div>
</spark:template>