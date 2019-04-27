<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/systemmanage.jsp" menu="group">
	<c:if test="${group.id == null}"><%--卡片id为空，创建新卡片 --%>
		<c:set var="method" value="post"/>
		<c:url var="actionPath" value="/globalgroups"></c:url>
	</c:if>
	<c:if test="${group.id != null}">
		<c:set var="method" value="put"/>
		<c:url var="actionPath" value="/globalgroups/${group.id}"></c:url>
	</c:if >
	<c:url var="listUrl" value="/globalgroups/list"/>
	<div class="title-bar">
		<h3>
		<c:if test="${group.id == null}"><fmt:message key="group.add"/></c:if>
		<c:if test="${group.id != null}"><fmt:message key="group.edit"/></c:if>
		</h3>
		<div class="right">
			<a href="${ listUrl }"><fmt:message key="button.back" /></a>
		</div>
	</div>
	
	<form:form action="${ actionPath }" method="${method}" modelAttribute="group">
		<form:hidden path="id"/>
		<form:errors cssClass="error-message" path="*" element="div"/>
		<table cellspacing="0" class="details-table">
			<tbody>
				<tr>
					<th><fmt:message key="group.name"/>:<font color="red">*</font></th>
					<td><c:if test="${group.owner ==null}">
						<form:input path="name"/>
						</c:if>
						<c:if test="${group.owner != null}">
						${group.name}<form:hidden path="name"/>
						</c:if></td>
				</tr>
				<tr>
					<th><fmt:message key="group.authorization"/>:</th>
					<td>
					<select name="permissionSet">
						<option value="0"></option>
					<c:forEach items="${permissions}" var="permission">
						<option value="${permission.permissionSet.mask }" <c:if test="${permission.permissionSet.mask==permissionSet}">selected</c:if>>
						<fmt:message key="${permission.messageCode}"/>
						</option>
					</c:forEach>
					</select>
					</td>
				</tr>
			</tbody>
		</table>
		<button type="submit"><fmt:message key="button.save"/></button>
		<button type="button" onclick="window.location.href = '${ listUrl }'"><fmt:message key="button.back"/></button>
	</form:form>
</spark:template>