<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="com.baidu.spark.util.SparkConfig;"%>
<spark:template page="../templates/space.jsp" styles="js/autocomplete/jquery.autocomplete.css" scripts="js/autocomplete/jquery.autocomplete.js">
	<script type="text/javascript"> 
	$(function() {
		$(".card-back").click(function() {   
				window.location.href =  "<%= request.getContextPath() %>/spaces/${card.space.prefixCode }/cards" + Spark.util.TabUtils.getLastView() ;
				return false;
		});
		$("#current-card-breadcrumb").text("> ${card.space.prefixCode}-${card.sequence}" );
	});
	</script>
	<div id="card-contents">
		<c:set var="color" value="${ card.type.color == null ? 'F8F8F8' : card.type.color }"></c:set>
		<div class="card" id="card">
				<div class="card-top" style="border-left: 4px solid #${color};">
					<div class="card-action right" style="text-align: right;">
						<spark:acl hasPermission="${WRITE}" domainObject="${card}">
							<a class="card-edit" href="<%= request.getContextPath() %>/spaces/${card.space.prefixCode }/cards/${card.sequence}/edit"><fmt:message key="button.edit" /></a>
						</spark:acl>
						<spark:acl hasPermission="${DELETE}" domainObject="${card}">
							<a class="card-delete" href="javascript:void(0)" onclick="onDeleteCard('${card.space.prefixCode}',${card.sequence});"><fmt:message key="button.delete" /></a>
						</spark:acl>
						<a class="card-back" href="javascript:void(0)"><fmt:message key="button.back" /></a>
					</div>
					<div class="card-title" >
						<c:if test="${ fn:length(card.validAttachments ) > 0 }">
								<div class="attachment-icon left"></div>
						</c:if>
						[${card.type.name}] <span class="title"><c:out value="${card.title}"/></span>
					</div>
					<div class="date-info">
							<div>
							<span>${card.createdUser.name} </span>
								<fmt:message key="card.show.createdAt"/><span>
								<spark:date value="${card.createdTime}" /></span>&nbsp;
								<span>${card.lastModifiedUser.name} </span><fmt:message key="card.show.lastModifiedAt"/>
								<span><spark:date value="${card.lastModifiedTime}" /></span>
							</div>
					</div>
				</div>
				<div class="card-middle">
						<div class="card-detail rich-content">${card.detail}</div>
						
						<div class="card-properties">
						<div class="properties">
							<c:forEach items="${ cardPropertyValueList }" var="property" >
								<span class="property">
									<span class="label">${ property.cardProperty.name }:</span>
									<span class="value"><spark:template page="property/${ property.cardProperty.type }.jsp" value="${ property }" showType="show" /></span>
								</span>
							</c:forEach>
								<%-- 只有当空间内存在icafe项目时才显示空间列表 --%>
								<c:if test="${ space.projects != null && fn:length(space.projects) > 0 }">
								<span class="property">
								 	<span class="label"><fmt:message key="card.project"/>: </span>
								 	<span class="value">
								 		<a href="<%=SparkConfig.getSparkConfig("server.icafe.projectInfoUrl") %>${ card.project.icafeProjectId }" target="_blank">${ card.project.name }</a>
									</span> 
								</span>
								</c:if>
								<%-- 上级卡片 --%>
								<c:if test="${card.parent!=null}">
								<span class="property">
									<span class="label"><fmt:message key="card.parent"/>:</span> 
									<span class="value"><a href="<%= request.getContextPath() %>/spaces/${card.space.prefixCode}/cards/${card.parent.sequence}">#${card.parent.sequence}-${card.parent.title} </a></span>
									</span>
								</c:if>
						</div>	
						</div>
					</div>
					<div class="card-bottom">
						<div class="card-action">
							<spark:acl hasPermission="${WRITE}" domainObject="${card}">
								<a class="card-edit" href="<%= request.getContextPath() %>/spaces/${card.space.prefixCode }/cards/${card.sequence}/edit"><fmt:message key="button.edit" /></a>
							</spark:acl>
							<spark:acl hasPermission="${DELETE}" domainObject="${card}">
								<a class="card-delete" href="javascript:void(0)" onclick="onDeleteCard('${card.space.prefixCode}',${card.sequence});"><fmt:message key="button.delete" /></a>
							</spark:acl>
							<a class="card-back" href="javascript:void(0)"><fmt:message key="button.back" /></a>
						</div>
					</div>
				</div>
				<spark:template page="cardinfo.jsp"></spark:template>
	</div>
	<spark:template page="../templates/card-delete-confirm.jsp" />
</spark:template>