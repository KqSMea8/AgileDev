<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/space.jsp"
	styles="js/tree/themes/classic/style.css"
	scripts="js/pages/cards/list.js, js/widgets/query.js"
	tab="product-cards">
<script type="text/javascript">
Spark.util.TabUtils.setTab("${space.prefixCode}" , Spark.constants.TAB_INDEX.PRODUCT_TAB);
$(function(){  
	var list = new Spark.pages.List({ prefixCode: "${space.prefixCode}"});
	Spark.util.TabUtils.setLastView("${space.prefixCode}" , "/list#" + Spark.util.History.getHash());
});
</script>
<div id="product-cards">
	<div class="column2-l-right">
		<div class="sidebar">
			<spark:template page="searchbox.jsp"></spark:template>
			<div class="favorites radius-large">
				<spark:template page="userview.jsp"></spark:template>
			</div>
			<div class="filter radius-large">
				<spark:template page="filter.jsp"></spark:template>
			</div>
		</div>
	</div>
	<div class="column2-l-main">
		<spark:template page="../templates/toolbar.jsp" view="list"></spark:template>
		<div id="error-message" class="info-message" style="display:none">
			<span class="info-message-icon"></span><span class="info-message"></span>
		</div>
		<div id="list" class="card-list">
			<div id="viewbar" class="viewbar">
				<div class="left">
					<a id="assign-to-project" href="javascript:void(0)" style="display: none;"><fmt:message key="plugin.icafe.assign-to-project" /></a>
					<spark:acl hasPermission="${WRITE}" domainObject="${space}">
						<a id="batch-update" href="javascript:void(0)"><fmt:message key="card.batchUpdate" /></a>
					</spark:acl>
					<spark:acl hasPermission="${DELETE}" domainObject="${space}">
						<a id="batch-delete" href="javascript:void(0)"><fmt:message key="card.batchDelete" /></a>
					</spark:acl>
				</div> <!-- end left -->
				<div class="right">
					<span class="viewbar-button">
						<span class="export-excel-icon"></span>
						<a id="export-to-excel" class="icon-link" href="javascript:void(0)"><fmt:message key="button.view.export-to-excel" /></a>
					</span>
					<span class="viewbar-button">
						<span class="select-columns-icon"></span>
						<a id="select-columns" class="icon-link" href="javascript:void(0)"><fmt:message key="button.view.manage-columns" /></a>
					</span >
				</div> <!-- end right -->
			</div> <!-- end viewbar -->
			<div id="card-list-container">
			<table id="list-table" class="list">
				<thead>
					<tr id="cards-header" class="cards-header"></tr>
				</thead>
				<tbody></tbody>
			</table>
			</div> <!-- end card-list-container -->
		</div> <!-- end list -->
	</div>
	<div id="loading-area"></div>
</div>

<%-- parameter confirm page for export-to-excel --%>
<div id="exportToExcelParam" style="display:none">
		<div id="columnTypeRadio" class="paramDiv">
			<div class="paramLabel">
				<label><fmt:message key="export.pick-column-title" />:</label>
			</div>
			<div>
				<input type="radio" name="columnType" id="columnTypePicked" value="picked" checked><label for="columnTypePicked"><fmt:message key="export.picked-columns" /></label>
				<input type="radio" name="columnType" id="columnTypeAll" value="all"><label for="columnTypeAll"><fmt:message key="export.all-columns" /></label>
			</div>
		</div>
		<div id="dataTypeRadio" class="paramDiv">
			<div class="paramLabel">
				<label><fmt:message key="export.pick-row-title" />:</label>
			</div>
			<div>
				<input type="radio" name="dataType" id="dataTypeQueried" value="queried" checked><label for="dataTypeQueried"><fmt:message key="export.queried-rows" /></label>
				<input type="radio" name="dataType" id="dataTypeAll" value="all"><label for="dataTypeAll"><fmt:message key="export.all-rows" /></label>
			</div>
		</div>
</div>
<div id="batchDeleteDiaglogContainer" stype="display:none" class="warning-message"></div>


<!-- iCafe Project Picker -->
<spark:template page="../templates/plugins/icafe/project-picker.jsp" />

</spark:template>