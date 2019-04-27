<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/manage.jsp" menu="group" 
	scripts="js/autocomplete/jquery.autocomplete.pack.js"
	styles="js/autocomplete/jquery.autocomplete.css" >
<div id="group-list">
		<div class="title-bar">
			 <h3><fmt:message key="group.groupuser.edit"/>--${group.name}</h3>
		</div>
		<c:url var="backUrl" value="/spaces/${space.prefixCode}/groups/list"/>
		<form action="#" onsubmit="addUserLine();return false;">
		<table cellspacing="0" class="details-table" width="30%">
			<tbody>
				<tr>
					<th><fmt:message key="user.userName" /></th>
					<td>
					<input id="suggestInput" name="suggestInput" type="text" value=""/>	<span id="errorMsg" class="error-message"></span>
					</td>
				</tr>
				<tr>
					<td></td><td>
					<button type="submit"><fmt:message key="button.add"/></button>
					<button type="button" onclick="window.location='${backUrl}'"><fmt:message key="button.back"/></button></td>
				</tr>
			</tbody>
		</table>
		</form>
		<div class="title-bar">
			<h3><fmt:message key="group.groupuser.currentusers"/></h3>
		</div>
		<table class="list-table" cellspacing="0" id="groupusertable">
			<thead>
				<tr>
					<th class="col-first"><fmt:message key="user.userName" /></th>
					<th><fmt:message key="user.name" /></th>
					<th><fmt:message key="user.email" /></th>
					<th class="col-last"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${group.users}" var="user">
					<tr>
						<td class="col-first" align="center"><input type="hidden" id="ids" name="ids" value="${user.id }">${user.username}</td>
						<td align="center">${user.name}</td>
						<td align="center">${user.email}</td>
						<td class="col-last">
							<a href="javascript:void(0);" onclick="removeUserLine('${user.username}',this);"><fmt:message key="button.delete" /></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
</div>
<script type="text/javascript">
var selectUserInfo = "";
$().ready(function() { 
	$("#suggestInput").focus();
	Spark.thirdparty.userSuggest(
			$("#suggestInput"),
			function (user){
				$('#suggestInput').val(user.username); 
			}
		);
}); 
function addUserLine(){
	$('#errorMsg').html("");
	$.getJSON(Spark.constants.CONTEXT+"/ajax/groups/addGroupUser?username="+$("#suggestInput").val()+"&groupId=${group.id}", function(userInfo){
		if(!userInfo){
			$('#errorMsg').html('<fmt:message key="group.groupuser.usersuggest.nouserfound"/>');
		}else{
			//判断用户是否已被添加
			var hiddenIds = $("#groupusertable #ids");
			var duplicateUser = false;
			for(var i = 0;i<hiddenIds.length;i++){
				var hiddenId = hiddenIds[i];
				if(hiddenId.value==""+userInfo.id){
					duplicateUser = true;
					break;
				}
			}
			if(duplicateUser){
				$('#errorMsg').html(userInfo.username+'<fmt:message key="group.groupuser.usersuggest.duplicate"/>');
				return;
			}
			$("#suggestInput").val("");
			$("#suggestInput").focus();
			var html = [];
			var clickFunction = "removeUserLine('"+userInfo.username+"',this);";
			html.push("<tr>");
			html.push('<td class="col-first" align="center">');
			html.push('<input type="hidden" name="ids" id="ids" value="');
			html.push(userInfo.id);
			html.push('">');
			html.push(userInfo.username);
			html.push('</td>');
			html.push('<td align="center">');
			html.push(userInfo.name);
			html.push('</td><td align="center">');
			html.push(userInfo.mail);
			html.push('</td><td class="col-last"><a href="javascript:void(0);" onclick="');
			html.push(clickFunction);
			html.push('"><fmt:message key="button.delete"/></a></td></tr>');
			$(html.join("")).appendTo($("#groupusertable"))
		}  
	});
	
} 
function removeUserLine(username,jsObj){
	$.getJSON(Spark.constants.CONTEXT+"/ajax/groups/deleteGroupUser?username="+username+"&groupId=${group.id}", function(userInfo){
		if(userInfo==""){
			$('#errorMsg').html('<fmt:message key="group.groupuser.usersuggest.nouserfound"/>');
		}
		var obj = $(jsObj).parents("tr").eq(0).children().eq(0).children("input")[0];
		value = obj.value;
		$(jsObj).parents("tr").eq(0).remove();
	});
}
</script>
</spark:template>