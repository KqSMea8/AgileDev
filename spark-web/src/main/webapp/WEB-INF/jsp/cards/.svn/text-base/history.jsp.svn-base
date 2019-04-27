<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
	$(function(){
		$("#tabs").tabs();
	});
</script>
	<div id="card-contents">
		<c:set var="color" value="${ _card.type.color == null ? 'F8F8F8' : _card.type.color }"></c:set>
		<div class="card" id="card">
				<div class="card-top" style="border-left: 4px solid #${color};">
					<div class="card-action right">
						<a class="card-back" href="<c:url value="/spaces/${_card.space.prefixCode }/cards/${_card.sequence }"/>"><fmt:message key="button.back" /></a>
					</div>
					<div class="card-title" >
						[${_card.type.name}] <span class="title"><c:out value="${_card.title}"/></span>
					</div>
					<div class="date-info">
							<div>
								<span>${_history.user.name} </span><fmt:message key="card.show.lastModifiedAt"/>
								<span><spark:date value="${_history.opTime}" /></span>
							</div>
					</div>
				</div>
				<div class="card-middle">
				<div class="card-detail rich-content">${ _card.detail}</div>
				
				<div class="card-properties">
				<div class="properties">
					<c:forEach items="${ _card.propertyValues }" var="property" >
					<span class="property" >
						<span class="label">${ property.cardProperty.name }:</span>
						<span class="history-value"><spark:template page="property/${ property.cardProperty.type }.jsp" value="${ property }" showType="show" /></span>
					</span>
					</c:forEach>
						<%-- 只有当空间内存在icafe项目时才显示空间列表 --%>
						<c:if test="${ space.projects != null && fn:length(space.projects) > 0 }">
						<span class="property" >
						 	<span class="label"><fmt:message key="card.project"/>: </span>
						 	<span class="history-value">${ _card.project.name }</span> 
						</span>
						</c:if>
						<%-- 上级卡片 --%>
						<c:if test="${ _card.parent!=null}">
							<span class="property">
								<span class="label"><fmt:message key="card.parent"/>:</span> 
								<span class="history-value">#${ _card.parent.sequence}-${ _card.parent.title}</span>
							</span>
						</c:if>
					</div>	
				</div>
			</div>
		</div>
		<div id="tabs" class="card-info-tabs">
			<ul>
				<li><a href="#tabs-1"><fmt:message key="card.attachment.tab"/></a></li>
			</ul>
			<div id="tabs-1">
				<c:forEach items="${ _card.validAttachments }" var="attach" >
					<div id='attachment_${attach.id}' class="card-attachment">
					 	<span class="card-attachment-user date">  
					 		${attach.uploadUser.name}
					 	</span>
						<span class="card-attachment-time date" title='<fmt:formatDate value="${attach.uploadTime}" pattern="yyyy-MM-dd HH:mm:ss" />'>  	
							<spark:date value="${attach.uploadTime}" /><fmt:message key="card.upload"/>
					 	</span> 
			 			<span class="card-attachment-center">${attach.note}
							<a href= '<%= request.getContextPath() %>/spaces/${currentCard.space.prefixCode}/cards/${currentCard.sequence}/attachments/${attach.id}/download' >${attach.originalName}</a>
			 			</span> 
		 			</div>
				</c:forEach>
			</div>
		</div>
	</div>