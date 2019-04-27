<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spark:template page="../templates/space.jsp" styles="${ _styles }" scripts="${ _scripts }" tab="manage">
<script type="text/javascript">
	Spark.util.TabUtils.setTab("${space.prefixCode}" , Spark.constants.TAB_INDEX.MANAGER_TAB);
</script>
<div class="column2-r-left">
	<div id="menu">
		<ul>
			<li ${ _menu == "space" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/spaces/${space.prefixCode}/edit"><fmt:message key="label.space.detail"/></a>
			</li>
			<li ${ _menu == "cardtype" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/spaces/${space.prefixCode}/cardtypes/list"><fmt:message key="label.space.cardtypes"/></a>
			</li>
			<li ${ _menu == "cardproperty" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/spaces/${space.prefixCode}/cardproperties/list"><fmt:message key="label.space.cardproperties"/></a>
			</li>
			<li ${ _menu == "group" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/spaces/${space.prefixCode}/groups/list"><fmt:message key="label.space.groups"/></a>
			</li>
			<li ${ _menu == "spaceview" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/spaces/${space.prefixCode}/spaceviews/list"><fmt:message key="label.space.spaceviews"/></a>
			</li>
			<li ${ _menu == "projects" ? 'class="selected"' : '' }>
				<a href="<%= request.getContextPath() %>/spaces/${space.prefixCode}/projects/list"><fmt:message key="label.space.projectmappings"/></a>
			</li>
		</ul>
	</div>
</div>
<div class="column2-r-main">
	<!-- #TEMPLATE-BODY# -->
</div>
</spark:template>