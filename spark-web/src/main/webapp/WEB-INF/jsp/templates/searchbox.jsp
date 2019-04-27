<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery/jquery.form.js"></script>
<spark:template page="../templates/bar.jsp" class="search-box">
	<div class="search-box">
		<form action="${ _action }" method="GET" >
		<h3>搜索</h3>
		<!-- #TEMPLATE-BODY# -->
		</form>
	</div>
</spark:template>