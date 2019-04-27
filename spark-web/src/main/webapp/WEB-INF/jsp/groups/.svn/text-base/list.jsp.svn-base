<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/manage.jsp" menu="group">
<div id="group-list">
		<div class="title-bar">
			 <h3><fmt:message key="group.list"/></h3>
			 <div class="left">
			 	<a href="new"><fmt:message key="button.new"/></a>
			 </div>
		</div>
		<table class="list-table" cellspacing="0">
			<thead>
				<tr>
					<th class="col-first"><fmt:message key="group.name" /></th>
					<th><fmt:message key="group.authorization" /></th>
					<th class="col-last"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${groupList}" var="group">
					<tr>
						<td class="col-first"><c:out value="${group.name}"/></td>
						<td>
						<c:forEach items="${permissions}" var="permission">
							<c:if test="${authorityMap[group.id]==permission.permissionSet.mask}">
								<fmt:message key="${permission.messageCode}"/>
							</c:if>
						</c:forEach>
						</td>
						<td class="col-last">
							<a href="${group.id}/edit"><fmt:message key="group.edit" /></a>
							<a href="${group.id}/groupuser"><fmt:message key="group.groupuser.edit" /></a>(${fn:length(group.users)})
							<a href="javascript:void(0);" onclick="deleteGroup(${group.id});"><fmt:message key="button.delete" /></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="warning">
			<div class="warning-content">
				<fmt:message key="group.authorization.explaination"/>
				<fmt:message key="group.authorization.warning"/>
			</div>
		</div>
</div>
	<div id="dialog-confirm" title="<fmt:message key="group.deleteconfirm.title"/>">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
		<fmt:message key="group.deleteconfirm.message" />
	</div>
<form:form action="" method="delete" id="deleteForm">
</form:form>
<script language="javascript">
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
				var groupId = $(this).dialog( "option" , "clickObj");
				var obj = $("#deleteForm")[0];
				obj.action=""+groupId;
				obj.submit();				
				$(this).dialog('close');
			}
		}
	});
});
function deleteGroup(groupId){
	if(groupId!=""&&groupId!="undefined"){
		$('#dialog-confirm').dialog("option" , "clickObj" , groupId );
		$('#dialog-confirm').dialog('open');	
	}
}
</script>
</spark:template>