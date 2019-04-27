<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spark:template page="../templates/manage.jsp" menu="cardproperty">
<script type="text/javascript">
function toggleHide(cb, id) {
	var url = "<%= request.getContextPath() %>/ajax/spaces/${ space.prefixCode }/cardproperties/" 
		+ id + "/" + (cb.checked ? "hide" : "show");
	$.ajax({
		url: url,
		type: "PUT",
		error: function() { cb.checked = !cb.checked }
	});
}
$(function() {
	window.orgOrder = [];
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
				var url = "<%= request.getContextPath() %>/ajax/spaces/${ space.prefixCode }/cardproperties/resort";
				$.ajax({
					url: url,
					type: "POST",
					contentType: "application/json",
					data: $.toJSON(order),
					success: function() { orgOrder = order; },
					error: function() { $(".list-table tbody").sortable("cancel"); }
				});
			}
		}
	}).disableSelection();
});
</script>

<div id="cardproperty-list">
	<div class="title-bar">
		<h3>${ cardType.name }<fmt:message key="cardproperty.list" /></h3>
		<div class="left">
			<a href="new"><fmt:message key="button.new" /></a>
		</div>
	</div>
	<table class="list-table" cellspacing="0">
		<thead>
			<tr>
				<th class="col-first"><fmt:message key="cardproperty.fieldname" /></th>
				<th><fmt:message key="cardproperty.fieldtype" /></th>
				<%-- 暂时注掉hidden的维护 <th><fmt:message key="cardproperty.hidden" /></th>--%>
				<th><fmt:message key="cardproperty.sort" /></th>
				<th class="col-last"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${ cardProperties }" var="cardProperty">
				<tr id="${ cardProperty.id }">
					<td class="col-first"><c:out value="${cardProperty.name}"/></td>
					<td><fmt:message key="cardproperty.type.${ cardProperty.type }" />
					<c:if test="${ cardProperty.type=='list' }">
						(<a href="${cardProperty.id }/listoption"><spring:message code="cardproperty.listproperty.optionsize" arguments="${fn:length(cardProperty.listKey) }"/></a>)
						</c:if>
					</td>
					<%-- 暂时注掉hidden的维护
					<td><input type="checkbox" name="hidden" onchange="toggleHide(this, ${ cardProperty.id })" ${ cardProperty.hidden ? 'checked="checked"' : '' } /></td>
					 --%>
					<td>
						<div class="dragable"><span class="icon"></span><span><fmt:message key="label.drag" /></span></div>
					</td>
					<td class="col-last">
						<a href="${ cardProperty.id }/edit"><fmt:message key="button.edit" /></a>
						<a href="${ cardProperty.id }/delete"><fmt:message key="button.delete" /></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</spark:template>