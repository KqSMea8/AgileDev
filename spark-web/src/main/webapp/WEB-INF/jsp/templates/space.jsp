<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/main.jsp" styles="${ _styles }" scripts="js/spark-service.js, js/pages/space/tab.js?1109, js/widgets/notification.js, ${ _scripts }">
		<div class="spark-topbar">
    		<div class="space-tabs radius-small-top" id="common-tabs">
				<ul id="favorite-tabs">
					<li id="tab-product"><a href="<%= request.getContextPath() %>/spaces/${ space.prefixCode }/cards/list"> <fmt:message key="tab.product-cards"/> </a></li>
					<c:forEach var="tab" items="${ space.views}" >
						<li id="tab-${ tab.id}"><a href="<%= request.getContextPath() %>/spaces/${ space.prefixCode }/spaceviews/${ tab.id}"> ${ tab.name} </a></li>
					</c:forEach>
					<c:if test="${fn:length(space.projects) > 0}">
						<li id="tab-project"><a href="<%= request.getContextPath() %>/spaces/${ space.prefixCode }/cards/roadmap"> <fmt:message key="tab.project-cards"/> </a></li>
					</c:if>
					<spark:acl hasPermission="${ADMIN}" domainObject="${space}">
						<li id="tab-admin"><a href="<%= request.getContextPath() %>/spaces/${ space.prefixCode }/edit"> <fmt:message key="tab.manage"/> </a></li>
					</spark:acl>
				</ul>
    		</div>
		</div>
		<div class="breadcrumb">
			<span class="home-icon"></span>
			<a class="first" href="<c:url value='/'/>" title="<fmt:message key="button.back-home"/>">Spark</a> 
			> <a href="<c:url value='/spaces/${space.prefixCode}/cards/list'/>"><c:out value="${ space.name }"></c:out></a>
			> <span id="current-tab-breadcrumb" class="title"></span>
			<span id="current-card-breadcrumb" class="title"></span>
			<span id="level-5" class="title"></span>
		</div>
		<div class="spark-main">
		<!-- #TEMPLATE-BODY# -->
		</div>
		<script language="javascript">
			(function(){  
				var tabctrl = new Spark.pages.Tab({
					prefixCode: "${ space.prefixCode }"  
				});
			})();
		</script>
</spark:template>
