<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<div id="project-picker" style="display: none;">
		<fmt:message key="project.name" />: <input id="project-picker-input" type="text"><input type="button" value="<fmt:message key="button.query" />" onclick="searchProject();">
		<div id="loadDiv"></div>
		<table class="list-table" cellspacing="0">
			<thead>
				<tr>
					<th class="col-first" style="width:20px"></th>
					<th class="col-last"><fmt:message key="project.name" /></th>
				</tr>
			</thead>
			<tbody id="project-picker-tbody">
			</tbody>
		</table>
	</div>

<script type="text/javascript">
$(function(){
	$("#project-picker").dialog({
		autoOpen: false,
		resizable: false,
		height:400,
		width:500,
		modal: true,
		buttons: {
			"<fmt:message key='button.cancel'/>": function() {
				$(this).dialog('close');
			},
			"<fmt:message key='button.ok'/>": function() {
				var options = $("#project-picker-tbody input[type=checkbox]");
				var projectInfoArray = new Array();
				for (var i=0;i<options.length;i++){
					var option = $(options[i]);
					if (option.attr("checked") == true){
						projectInfoArray.push(option.attr("projectInfo"));
					}
				}
				$.ajax({
					url : "<%= request.getContextPath() %>/ajax/spaces/${space.prefixCode}/projects/add",
					data : { "projectInfos" : projectInfoArray.join(",") },
					success : function() {
						window.location.reload();
					},
					error : function() {
						Spark.widgets.Alert.alert(Spark.util.message("global.exception.errorMsg"));
					}
				});
			}
		}
	});
});

// 绑定事件
$(window).bind("openProjectPicker", function() {
	$("#project-picker").dialog('open');
	$("#project-picker-input").focus();
});
$("#project-picker-input")
.keydown(function(event){
	if(event.keyCode=="13"){
		searchProject() ;
	}
});

function searchProject(){
	Spark.util.Load.add("loadDiv");
	var projectName = $("#project-picker-input").val();
	if (!projectName){
		Spark.widgets.Alert.alert(Spark.util.message("global.notice.input.project"));
		Spark.util.Load.remove('loadDiv');
		return;
	}
	$.post(
		"<%= request.getContextPath() %>/ajax/spaces/${space.prefixCode}/projects/suggestList",
		{ "projectName" : projectName },
		function ( projectList ) {
			var tbody = $("#project-picker-tbody");
			tbody.empty();
			for (var i=0;i<projectList.length;i++){
				var project = projectList[i];
				(function (project) {
					var option = $("<input type='checkbox'>").attr("projectInfo", project.projectId + ":" + encodeURIComponent(project.fullProjectName));
					var idTd = $("<td>").addClass("col-first").html(option);
					var nameTd = $("<td>").addClass("col-last").html(project.fullProjectName);
					$("<tr>").append(idTd).append(nameTd).appendTo(tbody);
				})(project);
			}
			Spark.util.Load.remove('loadDiv');
		}
	);
}
</script>
