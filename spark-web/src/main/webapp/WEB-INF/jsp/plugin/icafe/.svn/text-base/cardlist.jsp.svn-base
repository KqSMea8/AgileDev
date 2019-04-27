<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../../templates/base.jsp"
	scripts="js/spark-service.js,js/pages/cards/list.js, js/widgets/query.js, js/pages/space/tab.js?1109,"
	tab="product-cards">
<div id="plugin-icafe-project" >
<c:if test="${project!=null}">
<script type="text/javascript">
//先准备URL中的查询参数，使其先于list.js完成url变化
(function(){
	var query = new Spark.widgets.Query(""), displayColumns = ["createdUser","lastModifiedTime"] ;
	query.addCondition("project", "equals", ${project.id});
	query.addColumn(displayColumns);
	Spark.util.History.go(query.serialize());
})();
$(function(){
	var list = new Spark.pages.List({ prefixCode: "${project.space.prefixCode}", isExternal: true });
});
</script>
<div id="product-cards">
	<div>
		<div id="empty-result-message" class="message-container" style="display:none">
			<span class="info-message-icon"></span>
			<fmt:message key="plugin.icafe.noResultInQueryList"/>
			<a class="enter" href="<c:url value="/"/>" target="_blank"></a>
		</div>
		<div id="list" class="card-list">
			<div id="viewbar" class="viewbar" style="background-color: white">
				<span class="right">
					<span class="select-columns-icon"></span>
					<a id="select-columns" class="icon-link" id="select-columns" href="javascript:void(0)"><fmt:message key="button.view.manage-columns" /></a>
				</span>
			</div>
			<table id="list-table" class="list">
				<thead>
					<tr id="cards-header" class="cards-header"></tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
</div>
</c:if>
<c:if test="${project==null}">
	<div class="message-container">
		<fmt:message key="plugin.icafe.noResultInQueryList" />
		<a class="enter" href="<c:url value="/"/>" target="_blank"></a>
	</div>
</c:if>
</div>
</spark:template>