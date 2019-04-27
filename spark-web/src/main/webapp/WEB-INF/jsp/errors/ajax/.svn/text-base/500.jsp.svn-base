<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
<c:when test="${exception != null}">${exception.message}</c:when>
<c:otherwise><fmt:message key="global.exception.500"/> </c:otherwise>
</c:choose>