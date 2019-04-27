<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/manage.jsp" menu="cardproperty">
<script type="text/javascript">
/**
 * 添加一个指定类型的元素
 */
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
				var clickdom = $(this).dialog( "option" , "clickObj");
				$(clickdom).parents("tr").eq(0).remove();
				$(this).dialog('close');
			}
		}
	});
});
function addElementTo(type,appendToName,attrMap,cssStyle){
	var elem = $("<"+type+"/>");
	if(attrMap!=null){
		elem.attr(attrMap);
	}
	if(cssStyle != null){
		elem.css(cssStyle);
	}
	if(appendToName!=null){
		elem.appendTo(appendToName);
	}
	return elem;
}

function addListInput(){
	$("#templateLine").clone().attr("id","").appendTo($("#listPorpertyTable"));
}
function removeListInput(jsObj){
	var obj = $(jsObj).parents("tr").eq(0).children().eq(0).children("input")[0];
	value = obj.value;
	if(value!=""&&value!="undefined"){
		$('#dialog-confirm').dialog("option" , "clickObj" , jsObj );
		$('#dialog-confirm').dialog('open');	
	}else{
		$(jsObj).parents("tr").eq(0).remove();
	}
}
$(function() {
	$("#listPorpertyTable tbody").sortable({ 
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
		}
		
	}).disableSelection();
});
</script>
	<c:url var="saveUrl" value="/spaces/${ space.prefixCode }/cardproperties/${ cardProperty.id }" />
	<c:url var="listUrl" value="/spaces/${ space.prefixCode }/cardproperties/list" />
	<div class="title-bar">
		<h3><fmt:message key="cardproperty.listproperty.edit" />-${cardProperty.name }</h3>
	</div>
	<form:form action="#" method="put" modelAttribute="cardProperty">
	<form:errors path="*" cssClass="error-message" element="div" />
	<table cellspacing="0" class="details-table" id="listPorpertyTable">
	<tbody>
	<c:forEach items="${listKey}" var="key" varStatus="i">
		<tr>
			<td><input name="listKey" type="hidden" value="${key}"><input name="listValue" value="<c:out value="${listValue[i.count-1] }"/>">
			<span style="margin-left:3px"><a href="javascript:void(0)" onclick="removeListInput(this);"><fmt:message key="button.delete" /></a><c:if test="${key==''}">*</c:if></span></td>
			<td><div class="dragable"><span class="icon"></span><fmt:message key="label.drag" /></div></td>
		</tr>
	</c:forEach>
	</tbody>
	</table>
	<div style="margin: 6px;"><a href="javascript:void(0)" onclick="addListInput();"><fmt:message key="cardproperty.listproperty.optionlist.add"/></a></div>
		<button type="submit"><fmt:message key="button.save"/></button>
		<button type="button" onclick="location.href = '${ listUrl }'"><fmt:message key="button.cancel"/></button>
	</form:form>

	<table style="display:none">
		<tbody>
			<tr id="templateLine">
				<td><input type="hidden" name="listKey"><input name="listValue" value="">
				<span style="margin-left:3px"><a href="javascript:void(0)" onclick="removeListInput(this);"><fmt:message key="button.delete" /></a>*</span></td>
				<td><div class="dragable"><span class="icon"></span><fmt:message key="label.drag" /></div></td>
			</tr>
		</tbody>
	</table>
	<div id="dialog-confirm" title="<fmt:message key="cardproperty.listproperty.optionlist.deleteconfirm.title"/>">
		<p><span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
		<fmt:message key="cardproperty.listproperty.optionlist.deleteconfirm.message" />
	</div>
</spark:template>