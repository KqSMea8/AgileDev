<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spark:template page="../templates/space.jsp" >
<script>
	$(function(){
		/** 面包屑*/
		$("#current-card-breadcrumb").html("> <a href='../'>${currentCard.space.prefixCode}-${currentCard.sequence}</a>");
		$("#level-5").text("> " + Spark.util.message("card.history.view"));
	});
</script>
	<div id="attachment-history">
		<spark:template page="history.jsp" card="${ historyCard }" history="${ history }" isHistory="true"/>
	</div>
</spark:template>