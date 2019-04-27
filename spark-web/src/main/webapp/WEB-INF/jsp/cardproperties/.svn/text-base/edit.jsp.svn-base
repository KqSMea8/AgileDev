<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/manage.jsp" menu="cardproperty">
<script type="text/javascript">
$(function() {
	<c:if test="${typeIds==null}">
	<c:forEach items="${ cardProperty.cardTypes }" var="cardType">
		$("#checkBox${ cardType.id }").attr("checked", true);
	</c:forEach>
	</c:if>
	<c:if test="${typeIds!=null}">
	<c:forEach items="${ typeIds }" var="type">
	$("#checkBox${ type }").attr("checked", true);
</c:forEach>
	</c:if>
});
</script>
	<c:url var="listUrl" value="/spaces/${ space.prefixCode }/cardproperties/list" />
	<c:url var="saveUrl" value="/spaces/${ space.prefixCode }/cardproperties/${ cardProperty.id }" />
	<div class="title-bar">
		<h3><fmt:message key="cardproperty.edit" /></h3>
	</div>
	<form:form action="${ saveUrl }" method="put" modelAttribute="cardProperty">
	<form:errors path="*" cssClass="error-message" element="div" />
		<table cellspacing="0" class="details-table">
			<tbody>
				<tr>
					<th><fmt:message key="cardproperty.fieldname"/></th>
					<td><form:input path="name"/></td>
				</tr>
				<tr>
					<th><fmt:message key="cardproperty.fieldtype"/></th>
					<td>
						<fmt:message key="cardproperty.type.${ cardProperty.type }" />
						<form:hidden path="type" />
					</td>
				</tr>
				<!-- 暂时注掉hidden的维护
				<tr>
					<th><fmt:message key="cardproperty.hidden"/></th>
					<td><form:checkbox path="hidden" /></td>
				</tr>
				 -->
			</tbody>
		</table>
	<div class="title-bar">
		<h4><fmt:message key="cardproperty.cardtypelist.edit" /></h4>  
	</div>
	<table cellspacing="0" class="details-table" id="listPorpertyTable">
	<tbody>
	<c:forEach items="${space.cardTypes }" var="cardType">
		<tr>
			<td align="right"><input type="checkBox" name="typeIds" id="checkBox${ cardType.id }" value="${ cardType.id }"/></td>
			<td><c:out value="${ cardType.name }"/></td>
		</tr>
	</c:forEach>
	</tbody>
	</table>
		<button type="submit"><fmt:message key="button.save"/></button>
		<button type="button" onclick="location.href = '${ listUrl }'"><fmt:message key="button.cancel"/></button>
	</form:form>

</spark:template>