<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLDecoder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<spark:template page="../templates/manage.jsp" menu="spaceview">
<div id="cardtype-list">
		<div class="title-bar">
			 <h3><fmt:message key="spaceview.list"/></h3>
			 <div class="left">
			 	<a href="new"><fmt:message key="button.new"/></a>
			 </div>
		</div>
		<table class="list-table" cellspacing="0">
			<thead>
				<tr>
					<th class="col-first"><fmt:message key="spaceview.name" /></th>
					<th width="40%"><fmt:message key="spaceview.url" /></th>
					<th><fmt:message key="spaceview.sort" /></th>
					<th><fmt:message key="spaceview.username" /></th>
					<th><fmt:message key="spaceview.createdTime" /></th>
					<th class="col-last"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${views}" var="view">
					<tr id="${ view.id }">
						<td class="col-first"><c:out value="${view.name}"/></td>
						<td> <spark:decoder value="${view.url}"/>   </td>
						<td><div class="dragable"><span class="icon"></span><fmt:message key="label.drag" /></div></td>
						<td>${view.user.name}</td>
						<td><fmt:formatDate value="${view.createdTime}" pattern="yyyy-MM-dd" /></td>
						<td class="col-last">
							<a href="${view.id}/edit"><fmt:message key="button.edit" /></a>
							|
							<a href="javascript:void(0);" onclick="deleteView(${view.id});"><fmt:message key="button.delete" /></a> 
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
</div>
	<div id="dialog-confirm" title="<fmt:message key="spaceview.deleteconfirm.title"/>">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
		<fmt:message key="spaceview.deleteconfirm.content" />
	</div>

<form:form action="" method="delete" id="deleteForm">
</form:form>
</spark:template>

<script language="javascript">
$(function() {
	$("#dialog-confirm").dialog({
		autoOpen: false,
		resizable: false,
		height:200,
		width:400,
		modal: true,
		buttons: {
			"<fmt:message key="button.cancel"/>": function() {
				$(this).dialog('close');
			},
			'<fmt:message key="button.ok"/>': function() {
				var viewId = $(this).dialog( "option" , "clickObj"); 
				var obj = $("#deleteForm")[0];
				obj.action=""+ viewId;  
				obj.submit();				
				$(this).dialog('close');
			}
		}
	});
	
	var orgOrder = [];
	// Process sort
	$(".list-table tbody>*").each(function() {
		orgOrder.push(this.id);
	});
	$(".list-table tbody").sortable({ 
		handle: ".dragable", 
		placeholder: "sortable-placeholder", 
		start: function(event, ui) {
			var helper = ui.helper;
			var placeholder = ui.placeholder;
			if (helper.attr("nodeName") == "TR" && placeholder.attr("nodeName") == "TR") {
				var td, i, width, height = $(helper.children()[0]).height();
				for (i = 0; i < helper.children().length; i++) {
					placeholder.append($("<td>").height(height));
				}
				for (i = 0; i < helper.children().length; i++) {
					width = $(placeholder.children()[i]).width();
					$(helper.children()[i]).width(width);
				}
			}
		},
		beforeStop: function(event, ui) {
			var order = [];
			$(".list-table tbody>*").each(function() {
				var id = parseInt(this.id);
				if (id) {
					order.push(id);
				}
			});
			if (order.toString() != orgOrder.toString()) {
				var url = "<%= request.getContextPath() %>/ajax/spaces/${space.prefixCode}/spaceviews/resort";
				$.ajax({
					url: url,
					type: "POST",
					contentType: "application/json",
					data: $.toJSON(order),
					success: function() { orgOrder = order; location.reload(true);},
					error: function() { $(".list-table tbody").sortable("cancel"); }
				});
			}
		}
	}).disableSelection();
	
	
	
});
function deleteView(viewId){
	if(viewId!=""&&viewId!="undefined"){
		$('#dialog-confirm').dialog("option" , "clickObj" , viewId );
		$('#dialog-confirm').dialog('open');	
	}
}


</script>