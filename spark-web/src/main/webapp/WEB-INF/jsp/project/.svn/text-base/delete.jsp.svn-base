<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spark:template page="../templates/manage.jsp" menu="projects">
	<c:url var="listUrl" value="/spaces/${ space.prefixCode }/projects/list" />
	<div class="title-bar">
		<h3><fmt:message key="project.delete" /></h3>
		<div class="right">
			<a href="${ listUrl }"><fmt:message key="button.back" /></a>
		</div>
	</div>
	<div class="delete-confirm">
		<h4><fmt:message key="project.deleteconfirm"/></h4>
		<ul>
			<c:forEach items="${messages}" var="message">
				<li>-${message}</li>
			</c:forEach>
		</ul>
		<form:form action="${pageContext.request.contextPath}/spaces/${ space.prefixCode }/projects/${ project.id }" method="delete" modelAttribute="project">
			<div><b><img src="<%= request.getContextPath() %>/images/warning.png" />
				<fmt:message key="cardtype.deleteconfirm.warning"/></b> 
				 <button type="submit"><fmt:message key="button.delete"/></button>
				 <button type="button" onclick="location.href = '${ listUrl }'"><fmt:message key="button.cancel"/></button>
			</div>
		</form:form>
	</div>
</spark:template>