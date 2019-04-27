<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
[
	<c:forEach items="${validCards}" var="validCard" varStatus="validCardStatus">
		{
			"id" :  ${validCard.id},
			"sequence" : ${validCard.sequence},
			"title" :  "${fn:replace(validCard.title, "\"", "\\\"")}",
			"type" : 
				{
					"id" : ${validCard.type.id},
					"color" : "${validCard.type.color}" 
				},
			"space" : 
				{
					"id" : ${space.id},
					"prefixCode" : "${space.prefixCode}" 
				},
			"validForParent" : true,
			"childrenCount" : ${validCard.childrenSize} 
		}${ validCardStatus.last ? "" : "," }
	</c:forEach>
	
]
