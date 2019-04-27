<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<script>
$(function(){
	Spark.service.SpaceService.load("${space.prefixCode}",function(metadata){
		$("#create-card-button").button({
			icons: {
				primary: "ui-icon-plus",
				secondary: "ui-icon-triangle-1-s"
			}
		});

		var types = metadata.cardTypes, items = [];
		for ( i in types){
			cardtype = types[i];
			items.push({
				txt: cardtype.name,
				props: cardtype.id,
				link: "new?cardTypeId=" + cardtype.id
			});
		}
		new Spark.widgets.Menu({
			target: $("#create-card-button"),
			options: items
		});
	});
	//左侧栏收缩功能
	$("#sidebar-control").addClass( $(".column2-l-right") ? "sidebar-collapse": "sidebar-expand");
	$("#sidebar-control").click(function(){
		var sidebar = $(".column2-l-right");
		var mainContent = $(".column2-l-main");
		if (sidebar.css("display") == 'block'){
			mainContent.css("margin-right", parseInt(mainContent.css("margin-right"), 10) - sidebar.outerWidth());
			sidebar.hide();
			$("#sidebar-control").addClass("sidebar-expand").removeClass("sidebar-collapse");
			Spark.util.Cookie.set("right-bar-slided", "true");
		}else{
			mainContent.css("margin-right", parseInt(mainContent.css("margin-right"), 10) + sidebar.outerWidth());
			sidebar.show();
			$("#sidebar-control").addClass("sidebar-collapse").removeClass("sidebar-expand");
			Spark.util.Cookie.set("right-bar-slided", "false");
		}
		$(window).trigger("sidebarBtnClick");
	});
	
	//根据cookie值,来初始化右侧div是显示还是隐藏
	if (Spark.util.Cookie.get("right-bar-slided") == "true"){
		$("#sidebar-control").trigger("click");
	}

	//使不同视图之间可以相互切换
	var url = Spark.constants.CONTEXT + '/spaces/${space.prefixCode}/cards/',
	History = Spark.util.History;
	$("#list-view").click(function(){ var currentHash = window.location.hash;window.location.href = url + 'list' + currentHash; return false;});
	$("#hierarchy-view").click(function(){var currentHash = window.location.hash; window.location.href = url + 'hierarchy' + currentHash;return false;});
	$("#wall-view").click(function(){var currentHash = window.location.hash; window.location.href = url + 'wall' + currentHash;return false;});
});
</script>
<div class="toolbar">
	<div class="right"><a href="javascript:void(0);" id="sidebar-control"></a></div>
	<div>
		<ul class="right views">
			<li class="list-icon ${ _view == 'list' ? 'selected' : '' }">
					<a id="list-view" href="javascript:void(0);"><fmt:message key="label.view.list" /></a>
			</li>
			<li class="hierarchy-icon ${ _view == 'hierarchy' ? 'selected' : '' }">
					<a id="hierarchy-view" href="javascript:void(0);"><fmt:message key="label.view.hierarchy"/></a>
			</li>
			<li class="wall-icon ${ _view == 'wall' ? 'selected' : '' }">
					<a id="wall-view" href="javascript:void(0);"><fmt:message key="label.view.wall"/></a>
			</li>
		</ul>
		<div class="select-button">
				<spark:acl hasPermission="${CREATE_CHILDREN}" domainObject="${space}">
					<button id="create-card-button"><fmt:message key="button.new" /></button>
				</spark:acl>
		</div>
	</div>
</div>
