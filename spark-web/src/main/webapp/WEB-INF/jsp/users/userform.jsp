<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/systemmanage.jsp" menu="user">
	<c:if test="${user.id == null}">
		<c:set var="method" value="post"/>
		<c:url var="actionPath" value="/users"></c:url>
		<c:set var="messageKey" value="user.create"></c:set>
	</c:if>
	<c:if test="${user.id != null}">
		<c:set var="method" value="put"/>
		<c:url var="actionPath" value="/users/${user.id}"></c:url>
		<c:set var="messageKey" value="user.edit"></c:set>
	</c:if >
	<div class="title-bar">
		<h3><fmt:message key="${messageKey}"/></h3>
	</div>
	<form:form action="${actionPath}" method="${method}" modelAttribute="user">
		<form:hidden path="id"/>
		<form:errors cssClass="error-message" path="*"/>
		<table cellspacing="0" class="details-table">
			<tbody>
				<c:if test="${user.id != null}">
				<tr>
					<th>ID:</th>
					<td>${user.id }</td>
				</tr>
				</c:if>
				<tr>
					<th><fmt:message key="user.userName"/>:<font color="red">*</font></th>
					<td><form:input path="username"/></td>
				</tr>
				<tr>
					<th><fmt:message key="user.name"/>:<font color="red">*</font></th>
					<td><form:input path="name"/></td>
				</tr>
				<tr>
					<th><fmt:message key="user.email"/>:</th>
					<td><form:input path="email"/></td>
				</tr>
				<tr>
					<th>uicid:</th>
					<td><form:input path="uicId"/></td>
				</tr>
				<tr>
					<th><fmt:message key="user.locked"/>:</th>
					<td>
						<form:checkbox path="locked" id="locked"/>
					</td>
				</tr>
			</tbody>
		</table>
		<button type="submit"><fmt:message key="button.save"/></button>
		<button type="button" onclick="location.href = '<c:url value="/users/list"/>'"><fmt:message key="button.back"/></button>
	</form:form>
</spark:template>