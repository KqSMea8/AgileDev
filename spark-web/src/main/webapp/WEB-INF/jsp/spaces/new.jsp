<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spark:template page="../templates/main.jsp">
	<c:url var="url" value="/spaces"/>
	<div class="spark-main">
		<div class="title-bar">
			<h3><fmt:message key="space.new"/></h3>
		</div>
		<form:form action="${url}" modelAttribute="space" method="post" id="spaceForm" onsubmit="confirmPublicChange();return false;">
		<form:errors path="*" cssClass="error-message"></form:errors>
		<c:if test="${importValidationResult!=null}">
			<div class="error-message">
			<c:forEach items="${importValidationResult}" var="validation">
				<div>${validation.error }</div>
			</c:forEach>
			</div>
		</c:if>
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
	        <tr>
	        	<th><fmt:message key="space.copyFromExist"/>:</th>
	        	<td><select name="createFromSpaceId">
	        		<option value=""></option>
	        		<c:forEach items="${spaceList}" var="space">
					<option value="${space.id }" <c:if test="${createFromSpaceId==space.id }">selected</c:if>><c:out value="${space.name }"/></option>
	        		</c:forEach>
	        		</select>
	        	</td>
	        </tr>
	    </table>
	        <button type="submit"><fmt:message key="button.save"/></button>
	        <button type="button" onclick="window.location='<c:url value="/spaces/list"/>'">
	        	<fmt:message key="button.back"/>
	        </button>
		</form:form>
	</div>
	<div id="dialog-confirm" title="<fmt:message key="space.setPublicConfirm.title"/>" style="display:none;">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
		<c:if test="${publicPermission==null||publicPermission==0}">
		  <fmt:message key="space.setPublicConfirm.message" />
		</c:if>
		<c:if test="${publicPermission!=null&&publicPermission>0}">
		  <fmt:message key="space.setPrivateConfirm.message" />
		</c:if>
	</div>
<script language="text/javascript">
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
