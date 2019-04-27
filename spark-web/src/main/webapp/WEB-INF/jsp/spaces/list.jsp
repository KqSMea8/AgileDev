<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spark:template page="../templates/main.jsp" scripts="js/pages/space/spacegroup.js">
	<script>
	$(document).ready(function(){
		$('#home').pngFix();
	});

	$(function(){
		var hasFavorite = ("<c:out value="${ fn:length(spacegroupList[0].spaces) }"/>" > 0);
		var hasWorkingSpace = ("<c:out value="${ fn:length(spacegroupList[1].spaces) }"/>" > 0);
		var currentTab = hasFavorite ? 0 : ( hasWorkingSpace ? 1 : 2 );
		var spacegroup = new Spark.pages.Spacegroup({ currentTab : currentTab });
	});

	</script>
	<div class="spark-main">
		<div id="home">
			<div class="recent-history">
				<div class="container-header">
					<span><fmt:message key="label.recentHistory"/></span>
					
				</div>
				<div class="container-body">
					<ul>
						<c:forEach items="${ cards }" var="card">
							<li><img alt="" src="<%= request.getContextPath() %>/images/card_edit.png">
								<a href="<%= request.getContextPath() %>/spaces/${card.space.prefixCode}/cards/${card.sequence}"><c:out value="${ card.title }"/></a>
								<span class="date"><spark:date value="${ card.lastModifiedTime }" /></span>
							</li>
						</c:forEach>
					</ul>
				</div>
			</div>
			<div class="space-list">
				<div class="space-tabs radius-small-top">
					<ul>
					<c:forEach items="${ spacegroupList }" var="spacegroup" varStatus="s">
						<li id="spaceGroupTab<c:out value="${ s.count -1 }"/>"><a id="spacegroupTabSpan<c:out value="${ spacegroup.id }"/>" href="javascript:void(0);"><c:out value="${ spacegroup.name }"/></a></li>
					</c:forEach>
					</ul>
				</div>
				<div class="container-body" id="container-body"></div>
			</div>
		</div>
	</div>
	<div id="empty-result-message" class="info-message" style="display:none">
		<span class="info-message-icon"></span>
		<fmt:message key="space.emptyResult"/>
	</div>
</spark:template>