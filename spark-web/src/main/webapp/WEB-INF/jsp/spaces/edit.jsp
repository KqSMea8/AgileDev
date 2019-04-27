<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spark:template page="../templates/manage.jsp" menu="space">
		<div class="title-bar">
			<h3><fmt:message key="space.edit"/></h3>
		</div>
		<spark:template page="../templates/info.jsp"></spark:template>
		<c:url var="submitUrl" value="/spaces/${ space.prefixCode }" />
		<div class="spark-content">
		<form:form modelAttribute="spaceModel" method="put" action="${ submitUrl }" id="spaceForm" onsubmit="confirmPublicChange();return false;">
		<form:hidden path="id"/>
		<form:errors path="*" cssClass="error-message"></form:errors>
	   	<table cellspacing="0" class="details-table">
	        <tr>
	            <th><fmt:message key="space.name"/>:<font color="red">*</font></th>
	            <td><form:input path="name" /></td>
	        </tr>
	        <tr>
	            <th><fmt:message key="space.prefixCode"/>:<font color="red">*</font></th>
	            <td><form:input path="prefixCode"/>
	            	<span class="weak">(<fmt:message key="space.prefixCode.message"/>)</span>
	            </td>
	        </tr>
	        <tr>
	        	<th><fmt:message key="space.description"/>:</th>
	        	<td><form:textarea path="description"/>
	        	</td>
	        </tr>
	        <tr>
	        	<th><fmt:message key="space.isPublic"/>:</th>
	        	<td><input type="checkbox" id="isPublic" name="isPublic" value="1" onclick="javascript:changPublic();" <c:if test="${publicPermission>0}">checked</c:if>/>
	        	<fmt:message key="common.true"/><span 
	        	id="permissionSpan" style="margin-left:15px;display:none;"><fmt:message key="space.public.grantAs"/>：<select name="permission">
	        			<c:forEach items="${permissions}" var="permission">
							<option value="${permission.permissionSet.mask }" <c:if test="${publicPermission==permission.permissionSet.mask}">selected</c:if>
							><fmt:message key="${permission.messageCode}"/></option>
						</c:forEach>
	        		  </select>
	        	</span>
	        	</td>
	        </tr>
	    </table>
	        <button type="submit"><fmt:message key="button.save"/></button>
	        <button type="button" onclick="window.location='<c:url value="/spaces/${space.prefixCode}/delete"/>'">
	        	<fmt:message key="button.delete"/>
	        </button>
		</form:form>
		</div>
		<div id="dialog-confirm" title="<fmt:message key="space.setPublicConfirm.title"/>">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
		<c:if test="${publicPermission==null||publicPermission==0}">
		  <fmt:message key="space.setPublicConfirm.message" />
		</c:if>
		<c:if test="${publicPermission!=null&&publicPermission>0}">
		  <fmt:message key="space.setPrivateConfirm.message" />
		</c:if>
	</div>
<script type="text/javascript">
$(function() {
	$("#dialog-confirm").dialog({
		autoOpen: false,
		resizable: false,
		height:200,
		modal: true,
		buttons: {
			"<fmt:message key="button.cancel"/>": function() {
				$(this).dialog('close');
			},
			'<fmt:message key="button.ok"/>': function() {
				$("#spaceForm")[0].submit();
				$(this).dialog('close');
			}
		}
	});
});
function confirmPublicChange(){
	if($("#isPublic").attr("checked")==${publicPermission>0}){
		$("#spaceForm")[0].submit();
	}else{
		$('#dialog-confirm').dialog('open');
	}
}

function changPublic(){
	if($("#isPublic").attr("checked")){
		$("#permissionSpan").css("display","inline");
	}else{
		$("#permissionSpan").css("display","none");
	}
}
changPublic();
</script>
</spark:template>
	