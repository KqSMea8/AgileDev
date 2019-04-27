<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spark:template page="../templates/main.jsp">
	<c:url var="url" value="/spaces/import"/>
	<div class="spark-main">
		<div class="title-bar">
			<h3><fmt:message key="space.copyFromExist.confirm"/></h3>
		</div>
		<form action="${url}" method="post">
		<input type="hidden" name="sourceSpaceId" value="${sourceSpace.id }"/>
		<input type="hidden" name="targetSpaceId" value="${targetSpace.id }"/>
	   	<table cellspacing="0" class="details-table">
	   		<c:forEach items="${options}" var="option">
	        <tr><td>
	        <c:if test="${option.type=='checkbox'}">
	        	<input type="checkbox" name="${option.inputName }" value="${option.checkedValue }" <c:if test="${option.defaultValue==option.checkedValue }">checked</c:if>><fmt:message key="${option.messageKey}"/>
	        </c:if>
	        </td></tr>
	        </c:forEach>
	    </table>
	        <button type="submit"><fmt:message key="button.submit"/></button>
	        <button type="button" onclick="window.location='<c:url value="/spaces/list"/>'">
	        	<fmt:message key="button.back"/>
	        </button>
		</form>
	</div>
	</spark:template>
