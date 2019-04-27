<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<div class="container-header radius-small-top"><span><fmt:message key="label.card.search"/></span></div>
	<div class="container-body">
		<div id="treeSelectorContainer"  style="display:none;z-index:99;"></div>
		<div id="queryDiv" class="queryDiv"></div>
		<div class="query-button-container">
			<a class="search-button" href="javascript:void(query.submitSearchButton());"><fmt:message key="button.query"/></a>
			<a href="javascript:void(query.initQueryDiv());void(query.initCardParentDiv());void(query.submitSearchButton());"><fmt:message key="button.clear"/></a>
		</div>
	</div>
	<div style = "display:none">
		<a id="parentDatePicker" href="javascript:void(0);" onclick="this.focus()" onFocus="WdatePicker({ onpicked : function(){$(this).prev().val($(this).text()+','+$(this).next().next().text());$(this).next().next()[0].focus();}, isShowClear : false, dateFmt : 'yyyy-MM-dd', skin : 'whyGreen' })"></a>
		<a id="normalDatePicker" href="javascript:void(0);" onclick="this.focus()" onFocus="WdatePicker({ onpicked : function(){$(this).prev().prev().prev().val($(this).prev().prev().text()+','+$(this).text());}, isShowClear : false, dateFmt : 'yyyy-MM-dd', skin : 'whyGreen' })"></a>
	</div>
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/widgets/query.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/spark-treeSelector.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/pages/cards/filter.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/tree/jquery.tree.min.js"></script>
	<script language="javascript" src="<%= request.getContextPath() %>/js/autocomplete/jquery.autocomplete.js"></script>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/autocomplete/jquery.autocomplete.css" />
	
	<script type="text/javascript">
		var query;
		$(function(){
			query  = new Spark.pages.Filter("${ space.prefixCode }", "queryDiv", "cardParentDiv");
		});
	</script>