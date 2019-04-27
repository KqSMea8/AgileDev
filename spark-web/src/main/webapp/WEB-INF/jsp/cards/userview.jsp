<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/pages/cards/userview.js?1109"></script>
<div class="container-header radius-small-top">
	<spark:acl hasPermission="${ADMIN}" domainObject="${space}">
		<span class="switch_to_spaceview_head"  id="add_space_view_menu"  title="<fmt:message key="spaceview.save.button"/>"></span>
	</spark:acl>
	<span class="add-icon" id="add_user_view_menu" title="<fmt:message key="userview.save.button"/>" ></span>
	<span><fmt:message key="userview.header"/></span>
</div>
<div class="container-body" id="container-body">
</div>

<script type="text/javascript">
	var _hasAdminRole = false;
	<spark:acl hasPermission="${ADMIN}" domainObject="${space}">
		_hasAdminRole = true;
	</spark:acl>
 	var viewCtrl = new Spark.pages.UserView({
 		containerDivId: "container-body",
 		userViewSaveDivId: "add_user_view_menu",
 		spaceViewSaveDivId:"add_space_view_menu",
 		prefixUrl : "<%= request.getContextPath() %>/spaces/${space.prefixCode}",
 		showAllUrl: "<%= request.getContextPath() %>/ajax/spaces/${space.prefixCode}/userviews/list",
 		viewUrl: "<%= request.getContextPath() %>/ajax/spaces/${space.prefixCode}/userviews" ,
 		spaceViewAddUrl: "<%= request.getContextPath() %>/spaces/${space.prefixCode}/spaceviews/new",
 		frag: "/spaces/${space.prefixCode}",
 		prefixCode: "${space.prefixCode}",
 		hasAdminRole: _hasAdminRole
 	});
</script>