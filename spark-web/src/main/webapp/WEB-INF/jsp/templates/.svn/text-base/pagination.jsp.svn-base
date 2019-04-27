<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
	<c:if test="${ _pagination.totalPages > 0 }">
	<div class="pagination">
		<ul>
			<li class="page-first"><a href="<spark:url value="" page="1" includeParams="true" />"><fmt:message key="pagination.first" /></a></li>
			<li class="page-prev"><a href="<spark:url value="" page="${ _pagination.page > 1 ? _pagination.page - 1 : 1 }" includeParams="true" />"><fmt:message key="pagination.prev" /></a></li>
			<c:choose>
			<c:when test="${ _pagination.totalPages <= 10 }">
				<c:forEach var="page" begin="1" end="${ _pagination.totalPages }">
			<li class="page ${ page == _pagination.page ? 'current' : '' }"><a href="<spark:url value="" page="${ page }" includeParams="true" />">${ page }</a></li>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:forEach var="page" begin="1" end="7">
			<li class="page ${ page == _pagination.page ? 'current' : '' }"><a href="<spark:url value="" page="${ page }" includeParams="true" />">${ page }</a></li>
				</c:forEach>
				<li class="ellipsis">...</li>
				<c:forEach var="page" begin="${ _pagination.totalPages - 2 }" end="${ _pagination.totalPages }">
			<li class="page ${ page == _pagination.page ? 'current' : '' }"><a href="<spark:url value="" page="${ page }" includeParams="true" />">${ page }</a></li>
				</c:forEach>
			</c:otherwise>
			</c:choose>
			<li class="page-next"><a href="<spark:url value="" page="${ _pagination.page < _pagination.totalPages ? _pagination.page + 1 : _pagination.totalPages }" includeParams="true" />"><fmt:message key="pagination.next" /></a></li>
			<li class="page-last"><a href="<spark:url value="" page="${ _pagination.totalPages }" includeParams="true" />"><fmt:message key="pagination.last" /></a></li>
		</ul>
	</div>
	</c:if>