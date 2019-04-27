<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@page import="com.baidu.spark.util.SpringSecurityUtils,com.baidu.spark.model.User,com.baidu.spark.security.SparkSystemResource,com.baidu.spark.security.SparkPermission;"%>
<spark:template page="../templates/base.jsp" styles="${ _styles }" scripts="${ _scripts }">
		<c:url var="homeUrl" value="/spaces/list"></c:url>
		<div class="spark-header">
			<div class="user-info">
				<fmt:message key="header.welcome"/>
				<span><%= SpringSecurityUtils.<User>getCurrentUser().getName() %></span>
				<spark:acl hasPermission="${CREATE_CHILDREN}" domainObject="${sparkSystemResource}">
					| <a href="<%= request.getContextPath() %>/spaces/new"><fmt:message key="space.new"/></a>
				</spark:acl>
				<spark:acl hasPermission="${ADMIN}" domainObject="${sparkSystemResource}">
					| <a href="<%= request.getContextPath() %>/users/list"><fmt:message key="system.config"/></a>
				</spark:acl>
		    	| <a href="<%= request.getContextPath() %>/spaces/sparkcase/cards/list"><fmt:message key="header.feedback"/></a>
		    	| <a href="<%= request.getContextPath() %>/j_spring_security_logout"><fmt:message key="header.logout"/></a>
			</div>
			<div class="logo" onclick="window.location = '${homeUrl}'" title="<fmt:message key='button.back-home'/>"></div>
		</div>
		<div class="spark-body" id="body-content">
			<!-- #TEMPLATE-BODY# -->
		</div>
		<div class="spark-footer">
		</div>
		<div id="spark-alert-dialog" style="display:none;z-index:99;"  title="<fmt:message key="alert.defaultmsg" />">
			<span class="ui-icon ui-icon-alert" style="float: left; margin: 0 7px 20px 0;"></span>
			<span id="spark-alert-dialog-content"><fmt:message key="alert.defaultmsg" /></span>
		</div>
	</spark:template>
