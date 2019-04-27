<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
[
	<c:forEach items="${attachments}" var="attachment" varStatus="attachmentStatus">
		{
			"id" :  ${attachment.id},
			"originalName" : <spark:out value="${attachment.originalName}" jsonValueStyle="true" default="\"\""/>,
			"uploadTime" : <spark:out value="${attachment.uploadTime}" jsonValueStyle="true" default="\"\""/>,
			"uploadUserName" : "${attachment.uploadUser.name}" ,
			"uploadUserId" : "${attachment.uploadUser.id}" ,
			"note" : <spark:out value="${attachment.note}" jsonValueStyle="true" default="\"\""/>
		}${ attachmentStatus.last ? "" : "," }
	</c:forEach>
	
]
