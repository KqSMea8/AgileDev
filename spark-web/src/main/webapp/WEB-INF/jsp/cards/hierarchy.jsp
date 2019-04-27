<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/space.jsp"
	styles="js/tree/themes/classic/style.css"
	scripts="js/tree/jquery.tree.min.js, js/widgets/query.js, js/pages/cards/hierarchy.js"
	tab="product-cards">
<script type="text/javascript">
Spark.util.TabUtils.setTab("${space.prefixCode}" , Spark.constants.TAB_INDEX.PRODUCT_TAB);
$(function() { 
	var hierarchy = new Spark.pages.Hierarchy({ prefixCode: "${space.prefixCode}" });
	Spark.util.TabUtils.setLastView("${space.prefixCode}" , "/hierarchy#" + Spark.util.History.getHash());
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
			<spark:template page="../templates/toolbar.jsp" view="hierarchy"></spark:template>
			<div id="error-message" class="info-message" style="display:none">
				<span class="info-message-icon"></span><span class="info-message"></span>
			</div>
			<div id="hierarchy" class="hierarchy">
				<div class="viewbar">
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
							<span class="select-columns-icon"></span>
							<a id="select-columns" class="icon-link" id="select-columns" href="javascript:void(0)"><fmt:message key="button.view.manage-columns" /></a>
						</span>
					</div> <!-- end right -->
					<div class="clear-both"></div>
				</div>  <!-- end viewbar -->
				<div class="hierarchy-header">
					<div class="item">
						<div class="item-head" style="padding-left:6px; text-align:left;"><input type="checkbox" id="selectAllCheckbox"></div>
						<div class="item-body">
							<ul id="hierarchy-header">
								<li class="title"></li>
							</ul>
						</div>
					</div> <!-- end item -->
				</div> <!-- end hierarchy-header -->
			</div> <!-- end hierarchy -->
			<div id="loading-area"></div>
		</div>
</div>
<div id="batchDeleteDiaglogContainer" stype="display:none" class="warning-message"></div>

<!-- iCafe Project Picker -->
<spark:template page="../templates/plugins/icafe/project-picker.jsp" />

</spark:template>