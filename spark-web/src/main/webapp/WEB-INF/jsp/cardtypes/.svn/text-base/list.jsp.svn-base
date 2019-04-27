<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/manage.jsp" menu="cardtype" 
	scripts="js/colorpicker/colorpicker.js"
	styles="js/colorpicker/css/colorpicker.css">
<div id="cardtype-list">
		<div class="title-bar">
			 <h3><fmt:message key="cardtype.list"/></h3>
			 <div class="left">
			 	<a href="new"><fmt:message key="button.new"/></a>
			 </div>
		</div>
		<table class="list-table" cellspacing="0">
			<thead>
				<tr>
					<th class="col-first"><fmt:message key="cardtype.name" /></th>
					<th><fmt:message key="cardtype.parentname" /></th>
					<th><fmt:message key="cardtype.recursive" /></th>
					<th style="width:40px;"><fmt:message key="label.color" /></th>
					<th class="col-last"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${cardTypeList}" var="cardType">
					<tr>
						<td class="col-first"><c:out value="${cardType.name}"/></td>
						<td>
							<c:choose>
								<c:when test="${cardType.parent == null}">
									-
								</c:when>
								<c:otherwise>
									<c:out value="${cardType.parent.name}"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>${cardType.recursive}</td>
						<td><div class="color-box" style="background-color:#${ cardType.color }"></div><input type="hidden" value="${cardType.localId}"/></td>
						<td class="col-last">
							<a href="${cardType.id}/edit"><fmt:message key="button.edit" /></a>
							|
							<a href="${cardType.id}/delete"><fmt:message key="button.delete" /></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
</div>
<script language="javascript">
$(function(){
	$(".color-box").colorbox({ 
		change: function(elem, newColor, orgColor) {
				$.ajax({
					url: Spark.constants.CONTEXT + "/ajax/spaces/${space.prefixCode}/cardtypes/" + elem.next().val() + "/color/" + newColor,
					type: "PUT",
					data: { "color": newColor },
					success: function() { elem.css("background-color", "#" + newColor); },
					error: function() { alert("save color error!"); }
				});
			}
		});
});
</script>
</spark:template>