<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/manage.jsp" menu="projects">
<div id="group-list">
	<div class="title-bar">
		 <h3><fmt:message key="project.list"/></h3>
		 <div class="left">
		 	<a href="javascript:createMapping()"><fmt:message key="button.new"/></a>
		 </div>
	</div>
		<table class="list-table" cellspacing="0">
			<thead>
				<tr>
					<th class="col-first"><fmt:message key="project.id" /></th>
					<th><fmt:message key="project.name" /></th>
					<th class="col-last"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${projectList}" var="project">
					<tr>
						<td class="col-first">${project.icafeProjectId}</td>
						<td>${project.name}</td>
						<td class="col-last">
							<a href="${project.id}/delete" ><fmt:message key="button.delete" /></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
</div>
<script language="javascript">
	function createMapping(){
		$(window).triggerHandler("openProjectPicker");
	}
</script>

<!-- iCafe Project Picker -->
<spark:template page="../templates/plugins/icafe/project-picker.jsp" />

</spark:template>