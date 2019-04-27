<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/manage.jsp" menu="spaceview">
 
	<c:url var="saveUrl" value="/spaces/${space.prefixCode}/spaceviews/${spaceView.id}"/>
	<c:url var="listUrl" value="/spaces/${space.prefixCode}/spaceviews/list"/>
	<div class="title-bar">
		<h3><fmt:message key="spaceview.edit"/></h3>
		<div class="right">
			<a href="${ listUrl }"><fmt:message key="button.back" /></a>
		</div>
	</div>
	<form:form action="${ saveUrl }" method="post" modelAttribute="spaceView" id="spaceViewForm">
		<form:hidden path="id"/>
		<form:hidden path="sort"/>
		<form:errors cssClass="error-message" path="*"/>
		<table cellspacing="0" class="details-table">
			<tbody>
				<tr>
					<td><fmt:message key="spaceview.name"/>:<font color="red">*</font></td>
					<td><form:input path="name"/></td>
				</tr>
				<tr>
					<td><fmt:message key="spaceview.url"/>:<font color="red">*</font></td>
					<td><form:textarea path="url"  /></td>
				</tr>
			</tbody>
		</table>
		<button type="button" onclick="javascript: checkAndSubmit();"><fmt:message key="button.save"/></button>
		<button type="button" onclick="location.href = '${ listUrl }'"><fmt:message key="button.cancel"/></button>
	</form:form>
</spark:template>
<div style='display:none;' id='spaceview-dialog-confirm' title='<fmt:message key="spaceview.edit"/>'>  
	<span class='ui-icon ui-icon-alert' style='float: left; margin: 0 7px 20px 0;'></span> 
	<div id='spaceview-dialog-confirm-content'><fmt:message key="spaceview.toCoverSameName"/></div> 
</div> 

<script type="text/javascript">
	$(function(){
		var _buttonsConfirm = {};
		_buttonsConfirm[Spark.util.message("button.cancel")]=function(){ $(this).dialog('close'); };
		_buttonsConfirm[Spark.util.message("button.ok")]=function(){ $(this).dialog('close');  doSubmit(); };
		$("#spaceview-dialog-confirm").dialog({
		 	autoOpen: false,
			resizable: false,
			modal: true,
			width: 350,
			height: 250,
			buttons: _buttonsConfirm
		});
	});
	function checkAndSubmit(){ 
		if($("#name").val().trim().length <1 || $("#name").val().trim().length > 20){
			Spark.widgets.Alert.alert(Spark.util.message("spaceview.inputrange"));
			return false;
		}else if($("#name").val() == '${spaceView.name}'){
			doSubmit();
		}else{
			$.ajax({
				url: "<%= request.getContextPath()%>/ajax/spaces/${space.prefixCode}/spaceviews/" + encodeURIComponent($("#name").val()) + "/isConflict",
				type: "GET",
				dataType: "json",
				success : function(isConfilict){
					if(isConfilict){  
						$("#spaceview-dialog-confirm").dialog('open');
					}else{
						doSubmit();
					}
				},
				error : function(){
					Spark.widgets.Alert.alert(Spark.util.message("spaceview.error"));
					return false;
				}
			})
		}
	}
	function doSubmit(){
		$("#spaceViewForm").submit();
	}
</script>