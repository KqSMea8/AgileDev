<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/manage.jsp" menu="cardtype">
	<script type="text/javascript">
		$(function() {
			$("#dialog-confirm").dialog({
				autoOpen: false,
				resizable: false,
				height:200,
				modal: true,
				buttons: {
					"<fmt:message key="button.cancel"/>": function() {
						$('#recursive').attr("checked",'true');
						$(this).dialog('close');
					},
					'<fmt:message key="button.ok"/>': function() {
						$(this).dialog('close');
					}
				}
			});
		});
		function toggleConfirm(cb) {
			var needConfirm = ${cardType.recursive} == true && cb.checked == false;
			if( needConfirm ){
				$('#dialog-confirm').dialog('open');
			}
		}
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
	<c:url var="saveUrl" value="/spaces/${space.prefixCode}/cardtypes/${cardType.id}"/>
	<c:url var="listUrl" value="/spaces/${space.prefixCode}/cardtypes/list"/>
	<div class="title-bar">
		<h3><fmt:message key="cardtype.edit"/></h3>
	</div>
	<form:form action="${ saveUrl }" method="put" modelAttribute="cardType">
		<form:hidden path="id"/>
		<form:errors cssClass="error-message" path="*"/>
		<table cellspacing="0" class="details-table">
			<tbody>
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
						<form:checkbox path="recursive" id="recursive" onchange="toggleConfirm(this)"/>
					</td>
				</tr>
			</tbody>
		</table>
		
		<div class="title-bar">
			<h4><fmt:message key="cardtype.cardpropertylist.edit" /></h4>  
		</div>
		<table cellspacing="0" class="details-table" id="listPorpertyTable">
		<tbody>
		<c:forEach items="${ cardType.space.cardProperties }" var="property">
			<tr>
				<td align="right"><input type="checkBox" name="propertyIds" id="checkBox${ property.id }" value="${ property.id }"/></td>
				<td><c:out value="${ property.name }"/></td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
		<button type="submit"><fmt:message key="button.save"/></button>
		<button type="button" onclick="location.href = '${ listUrl }'"><fmt:message key="button.cancel"/></button>
	</form:form>
	<div id="dialog-confirm" title="<fmt:message key="cardtype.changeconfirm.recursive.title"/>">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
		<fmt:message key="cardtype.changeconfirm.recursive.message"/>
	</div>
</spark:template>