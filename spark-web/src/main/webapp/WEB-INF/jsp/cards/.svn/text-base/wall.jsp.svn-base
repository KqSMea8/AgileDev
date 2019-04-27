<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/space.jsp"
	styles="js/tree/themes/classic/style.css"
	scripts="js/pages/cards/wall.js, js/spark-menu.js, js/widgets/query.js"
	tab="product-cards">
<script type="text/javascript">
Spark.util.TabUtils.setTab("${space.prefixCode}" , Spark.constants.TAB_INDEX.PRODUCT_TAB);
$(function(){  
	var list = new Spark.pages.Wall({ prefixCode: "${space.prefixCode}"});
	Spark.util.TabUtils.setLastView("${space.prefixCode}" , "/wall#" + Spark.util.History.getHash());
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
		<spark:template page="../templates/toolbar.jsp" view="wall"></spark:template>
		<div id="empty-result-message" class="info-message" style="display:none">
			<span class="info-message-icon"></span>
			<fmt:message key="cards.emptyResult"/>
		</div>
		<div id="wall" class="card-wall">
			<div id="viewbar" class="viewbar">
				<div class="right">
					<span class="viewbar-button">
						<span class="select-columns-icon"></span>
						<span class="icon-link"><fmt:message key="label.wall.groupby"/>
						<span id="disable-click-groupby"></span>
						<a id="select-columns" href="javascript:void(0)">
						<span id="enable-click-groupby"><fmt:message key="label.wall.groupby_notset" /></span></a></span>
					</span >
				</div>  <!-- end right -->
			</div> <!-- end viewbar -->
			<div id="card-list-container">
			<table id="wall-table" class="card-wall-table">
				<tbody></tbody>
			</table>
			</div>  <!-- end card-list-container -->
		</div> <!-- end wall -->
		<div id="loading-area"></div>
	</div>
</div>

<!-- iCafe Project Picker -->
<spark:template page="../templates/plugins/icafe/project-picker.jsp" />

</spark:template>