<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spark:template page="../templates/manage.jsp" menu="cardtype">
	<c:url var="editUrl" value="/spaces/${space.prefixCode}/edit" />
	<div class="title-bar">
		<h3><fmt:message key="space.delete" /></h3>
	</div>
		<div class="delete-confirm">
		<h4><spring:message code="space.deleteconfirm" arguments="${space.name}"/></h4>
		<ul>
			<c:forEach items="${messages}" var="message">
				<li>-${message}</li>
			</c:forEach>
		</ul>
	<form:form action="${pageContext.request.contextPath}/spaces/${space.prefixCode}" method="delete" modelAttribute="cardType">
		<div><b><img src="<%= request.getContextPath() %>/images/warning.png" />
			<fmt:message key="cardtype.deleteconfirm.warning"/></b> 
			 <button type="submit"><fmt:message key="button.delete"/></button>
			 <button type="button" onclick="location.href = '${ editUrl }'"><fmt:message key="button.cancel"/></button>
		</div>
	</form:form>
	</div>
</spark:template>