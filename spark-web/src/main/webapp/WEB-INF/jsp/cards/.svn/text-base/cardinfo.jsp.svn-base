<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/pages/cards/discussion.js?20110212"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/tinymce/jscripts/tiny_mce/tiny_mce.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/jqueryfileupload/ajaxfileupload.js"></script>
	<script type="text/javascript"> 
	$(function() {
		$("#tabs").tabs();
		$(document).ready(function () {
			new Spark.pages.Discussion(document.getElementById('tabs-1'), '<%= request.getContextPath() %>', '${card.space.prefixCode}', '${card.id}');
			new Spark.pages.attachment.Manager({'prefixCode':'${card.space.prefixCode}','sequence':'${card.sequence }' <spark:acl hasPermission="${WRITE}" domainObject="${card}"> , 'hasPermision': true</spark:acl>});
		});
	});

	function showHistory(historyId) {
		window.location.href = Spark.constants.CONTEXT + "/spaces/${space.prefixCode}/cards/${card.sequence}/history/" + historyId;
	}
	</script>
	<div id="tabs" class="card-info-tabs">
		<ul>
			<li><a id="tabtitle-1" href="#tabs-1"><fmt:message key="card.show.discussion"/></a></li>
			<li><a id="tabtitle-2" href="#tabs-2"><fmt:message key="card.attachment.tab"/>(${ fn:length(card.validAttachments)})</a></li>
			<li><a id="tabtitle-3" href="#tabs-3"><fmt:message key="card.show.history"/>(${ fn:length(card.historyList)})</a></li>
		</ul>
		<div id="tabs-1" class="discussions"></div>
		<div id="tabs-2">
			 <spark:template page="attachment.jsp"></spark:template>
		</div>
		<div id="tabs-3">
			<c:forEach items="${ card.historyList }" var="history" >
				<div id="${ history.id }" class="card-history">
					<div class="date card-history-left">
						<c:set var="user" value="${ history.user == null ? '?' : history.user.name }"></c:set>
						<span>${user}</span>
						<span title='<fmt:formatDate value="${history.opTime}" pattern="yyyy-MM-dd HH:mm:ss" />'>
							&nbsp;&nbsp;<spark:date value="${ history.opTime }" />
						</span>
					</div>
					<div class="card-history-right">
						<a href="<c:url value="/spaces/${card.space.prefixCode}/cards/${card.sequence }/history/${history.id}"/>"><fmt:message key="button.view"/></a>
					</div>
					<div class="card-history-center">
						<ul>
						<c:forEach items="${ history.diffBean.infoList }" var="info">
							<li>-<c:out value="${ info }"/></li>
						</c:forEach>
						</ul>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>