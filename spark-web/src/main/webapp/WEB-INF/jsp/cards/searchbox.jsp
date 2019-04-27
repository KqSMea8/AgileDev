<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/pages/cards/searchbox.js"></script>
<script type="text/javascript">
$(function(){
var searchbox = new Spark.pages.SearchBox();
});
</script>
<div class="search-box-container ">
	<div class="search-box">
		<input id="search_text" name="search_text"  type="text"/>
		<span id="search_button" class="search_button left"></span>
	</div>
</div>

