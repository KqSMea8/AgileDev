<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spark:template page="../templates/space.jsp"
	scripts="js/pages/space/projects.js"
	tab="project-cards">

<script type="text/javascript">
	Spark.util.TabUtils.setTab("${space.prefixCode}" , Spark.constants.TAB_INDEX.PROJECT_TAB);
	$(function() {
		new Spark.pages.Project({ prefixCode: "${space.prefixCode}" });
	});
</script>

<div id="project-cards" class="column2-l-main">
	<div id="projects" class="projects"></div>
</div>
 
</spark:template>