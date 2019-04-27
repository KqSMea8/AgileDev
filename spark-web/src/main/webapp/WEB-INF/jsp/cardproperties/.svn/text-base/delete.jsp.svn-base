<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spark:template page="../templates/manage.jsp" menu="cardproperty">
	<c:url var="listUrl" value="/spaces/${ space.prefixCode }/cardproperties/list" />
	<div class="title-bar">
		<h3><fmt:message key="cardproperty.delete" /></h3>
		<div class="right">
			<a href="${ listUrl }"><fmt:message key="button.back" /></a>
		</div>
	</div>
	<form:form action="${ pageContext.request.contextPath }/spaces/${ space.prefixCode }/cardproperties/${ cardProperty.id }" method="delete" modelAttribute="cardProperty">
		<div class="delete-confirm">
			<h4><fmt:message key="cardproperty.deleteconfirm" /></h4>
			<ul>
				<li>- <spring:message code="cardproperty.deleteconfirm.cardpropertyvalue" arguments="${ fn:length(cardProperty.values) }" /></li>
			</ul>
		</div>
		<button type="submit"><fmt:message key="button.delete"/></button>
		<button type="button" onclick="location.href = '${ listUrl }'"><fmt:message key="button.cancel"/></button>
	</form:form>
</spark:template>