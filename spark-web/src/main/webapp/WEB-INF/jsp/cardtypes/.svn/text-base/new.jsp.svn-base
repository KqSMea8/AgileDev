<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/manage.jsp" menu="cardtype">
	<script type="text/javascript">
		$(function() {
			<c:if test="${propertyIds==null}">
			<c:forEach items="${ cardType.cardProperties }" var="property">
				$("#checkBox${ property.id }").attr("checked", true);
			</c:forEach>
			</c:if>
			<c:if test="${propertyIds!=null}">
			<c:forEach items="${ propertyIds }" var="property">
			$("#checkBox${ property }").attr("checked", true);
		</c:forEach>
			</c:if>
		});
	</script>
	<c:url var="url" value="/spaces/${space.prefixCode}/cardtypes"/> 
	<div class="title-bar">
		<h3><fmt:message key="cardtype.new"/></h3>
	</div>
	<form:form action="${url}" method="post" modelAttribute="cardType">
		<form:errors path="*" cssClass="error-message" element="div" />
		<form:hidden path="space.id"/>
		<table cellspacing="0" class="details-table">
				<tr>
					<th><fmt:message key="cardtype.name"/>:<font color="red">*</font></th>
					<td><form:input path="name"/></td>
				</tr>
				<tr>
					<th><fmt:message key="cardtype.parentname"/>:</th>
					<td>
						<form:select path="parent.id" >
							<form:option value=""  label=""></form:option>  
							<form:options items="${cardTypeList}" itemLabel="name" itemValue="id"/>
						</form:select>
					</td>
				</tr>
				<tr>
					<th><fmt:message key="cardtype.recursive"/>:</th>
					<td>
						<form:checkbox path="recursive"/>
					</td>
				</tr>
		</table>
		<div class="title-bar">
			<h4><fmt:message key="cardtype.cardpropertylist.edit" /></h4>  
		</div>
		<table cellspacing="0" class="details-table" id="listPorpertyTable">
		<tbody>
		<c:forEach items="${ space.cardProperties }" var="property">
			<tr>
				<td align="right"><input type="checkBox" name="propertyIds" id="checkBox${ property.id }" value="${ property.id }"/></td>
				<td><c:out value="${ property.name }"/></td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		<button type="submit"><fmt:message key="button.submit"/></button>
		<button type="button" onclick="location.href = '${ url }/list'"><fmt:message key="button.cancel"/></button>
	</form:form>
</spark:template>