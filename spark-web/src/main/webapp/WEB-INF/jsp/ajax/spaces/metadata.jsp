<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
{
	"id": ${space.id},
	"name": <spark:out value="${space.name}" jsonValueStyle="true" default="\"\""/>,
	"prefixCode": <spark:out value="${space.prefixCode}" jsonValueStyle="true" default="\"\""/>,
	"description": <spark:out value="${space.description}" jsonValueStyle="true" default="\"\""/>,
	"type": "${space.type}",
	"cardTypes": [
		<c:forEach items="${space.cardTypes}" var="type" varStatus="typeStatus">
		{
			"id": ${type.id},
			"localId": ${type.localId},
			"name": <spark:out value="${type.name}" jsonValueStyle="true" default="\"\""/>,
			"recursive": ${type.recursive},
			<c:if test="${ type.parent != null }">
			"parent": {
				"id": ${type.parent.id},
				"localId": ${type.parent.localId}
			},
			</c:if>
			"cardProperties": [
				<c:forEach items="${type.cardProperties}" var="property" varStatus="propertyStatus">
				{
					"id": ${property.id},
					"localId": ${property.localId},
					"name": <spark:out value="${property.name}" jsonValueStyle="true" default="\"\""/>,
					"type": "${property.type}",
					"hidden": ${property.hidden},
					"sort": ${property.sort},
					<c:if test="${ property.type == 'list' }">
                    "optionKeys" : [
                        <c:forEach items="${ property.optionMap }" var="option" varStatus="optionStatus">
		            		"${ option.key }"${ optionStatus.last ? "" : "," }
		            	</c:forEach>
                    ],
		            "optionMap": {
		            	<c:forEach items="${ property.optionMap }" var="option" varStatus="optionStatus">
		            		"${ option.key }": <spark:out value="${option.value}" jsonValueStyle="true" default="\"\""/>${ optionStatus.last ? "" : "," }
		            	</c:forEach>
		            },
					</c:if>
					"info": <spark:out value="${property.info}" jsonValueStyle="true" default="\"\""/>
				}${ propertyStatus.last ? "" : "," }
				</c:forEach>
			]
		}${ typeStatus.last ? "" : "," }
		</c:forEach>
	],
	"projects": [
		<c:forEach items="${ space.projects }" var="project" varStatus="projectStatus">
		{
			"id": ${project.id},
			"icafeProjectId": ${project.icafeProjectId},
			"name": <spark:out value="${project.name}" jsonValueStyle="true" default="\"\""/>
		}${ projectStatus.last ? "" : "," }
		</c:forEach>
	],
	"permissions": {
		<c:forEach items="${ permissions }" var="permission" varStatus="permissionStatus">
			<c:choose>
			<c:when test="${ fn:contains(permission.pattern, 'R') }">"read": true</c:when>
			<c:when test="${ fn:contains(permission.pattern, 'C') }">"create": true</c:when>
			<c:when test="${ fn:contains(permission.pattern, 'W') }">"write": true</c:when>
			<c:when test="${ fn:contains(permission.pattern, 'D') }">"delete": true</c:when>
			<c:when test="${ fn:contains(permission.pattern, 'A') }">"admin": true</c:when>
			</c:choose>${ permissionStatus.last ? "" : "," }
		</c:forEach>
	},
	"spaceViews": {
		<c:forEach items="${ spaceViews }" var="spaceView" varStatus="spaceViewStatus">
			"${spaceView.id}":
			{
				"name": <spark:out value="${spaceView.name}" jsonValueStyle="true" default="\"\""/>,
				"url": <spark:out value="${spaceView.url}" jsonValueStyle="true" default="\"\""/>,
				"sort":  ${spaceView.sort != null ? spaceView.sort : -1} 
			}
		 ${ spaceViewStatus.last ? "" : "," }
		</c:forEach>
	},
	"cardProperties": [
		<c:forEach items="${space.cardProperties}" var="property" varStatus="propertyStatus">
		{
			"id": ${property.id},
			"localId": ${property.localId},
			"name": <spark:out value="${property.name}" jsonValueStyle="true" default="\"\""/>,
			"type": "${property.type}",
			"hidden": ${property.hidden},
			"sort": ${property.sort},
			<c:if test="${ property.type == 'list' }">
            "optionMap": {
            	<c:forEach items="${ property.optionMap }" var="option" varStatus="optionStatus">
            		"${ option.key }": <spark:out value="${option.value}" jsonValueStyle="true" default="\"\""/>${ optionStatus.last ? "" : "," }
            	</c:forEach>
            },
			</c:if>
			"info": <spark:out value="${property.info}" jsonValueStyle="true"  default="\"\""/>
		}${ propertyStatus.last ? "" : "," }
		</c:forEach>
	]
}
