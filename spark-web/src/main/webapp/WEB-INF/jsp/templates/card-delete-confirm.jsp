<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div id="delete-dialog-confirm" title="<fmt:message key="card.deleteconfirm"/>" style="display: none">
	<div class="warning-message">
			<span class="warning-message-icon"></span>
			<fmt:message key="card.deleteconfirm.warning"/>
			<c:if test="${card.childrenSize  > 0 }">
				<div class="bar-inner-wrapper"><spring:message code="card.deleteconfirm.message" arguments="${card.childrenSize}"/></div>
				<input type="checkbox" id="cascade-delete"><fmt:message key="card.batchDelete.cascade"/></input>
			</c:if>
	</div>
</div>
<script type="text/javascript">
function onDeleteCard( prefixCode, sequence ){
	var deleteUrl = '<%= request.getContextPath() %>/spaces/'+prefixCode + '/cards/' + sequence,
	redirectUrl = '<%= request.getContextPath() %>/spaces/' + prefixCode +'/cards' + Spark.util.TabUtils.getLastView();
	$("#delete-dialog-confirm").dialog({
		autoOpen: false,
		resizable: false,
		height:200,
		modal: true,
		buttons: {
			'<fmt:message key="button.cancel"/>': function() {
				$(this).dialog('close');
			},
			'<fmt:message key="button.ok"/>': function() {
				$.ajax({
					url: deleteUrl + "?cascade=" + ($("#cascade-delete").attr("checked")? $("#cascade-delete").attr("checked") : "false"),
					type: "DELETE",
					success: function(){
						$(this).dialog('close');
						window.location.href = redirectUrl ;
					}, 
					error : function(){
						Spark.widgets.Alert.alert(Spark.util.message("card.delete.failed"));
					}
				});
				
				return false;
			}
		}
	});
	$("#delete-dialog-confirm").dialog('open');
}
</script>
