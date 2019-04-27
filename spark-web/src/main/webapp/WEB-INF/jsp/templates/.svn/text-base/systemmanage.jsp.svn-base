<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spark:template page="../templates/main.jsp">
<div class="column2-r-left">
	<div id="menu">
		<ul>
			<li ${ _menu == "user" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/users/list"><fmt:message key="system.users"/></a>
			</li>
		</ul>
		<ul>
			<li ${ _menu == "group" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/globalgroups/list"><fmt:message key="label.space.groups"/></a>
			</li>
		</ul>
		<ul>
			<li>
				<a href="<%= request.getContextPath() %>/spaces/initCardIndex"><fmt:message key="system.initCardIndex"/></a>
			</li>
		</ul>
		<ul>
			<li>
				<a href="<%= request.getContextPath() %>/users/sync"><fmt:message key="system.syncUser"/></a>
			</li>
		</ul>
		<ul>
			<li ${ _menu == "shadow" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/system/shadow"><fmt:message key="admin.shadow"/></a>
			</li>
		</ul>
	</div>
</div>
<div class="column2-r-main">
	<!-- #TEMPLATE-BODY# -->
</div>
</spark:template>