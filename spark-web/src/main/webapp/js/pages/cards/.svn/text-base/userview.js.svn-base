/**
 * 查询页面组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.UserView
 * @author shixiaolei
 */
(function(){
 
	Spark.pages.UserView = function(options){
		var me = this;
		me.options = $.extend(me.options, options);
		me._initUI();
		return me;
	};
	
	Spark.pages.UserView.prototype = {
			
		/**
		 * 选项列表
		 */
		options: {
			/**
			 * 显示用户自定义查询列表的DIV DOM ID
			 * @type: string
			 */
			containerDivId: "",
			
			
			/**
			 * 添加用户自定义视图按钮所在的DIV
			 * @type: string
			 * 
			 */
			userViewSaveDivId: "",
			
			
			/**
			 * 空间级别视图按钮所在的DIV
			 * @type: string
			 * 
			 */
			spaceViewSaveDivId: "",
			
			
			/**
			 * 当前空间的前缀
			 *  @type: string
			 */
			prefixUrl: "",
			
			/**
			 * 展示空间内所有该用户查询收藏的请求地址
			 *  @type: string
			 */
			showAllUrl: "",
			
			
			/**
			 * 单个视图AJAX操作的URL，对其发送PUT、POST和DELETE，分别会触发增、改名和删除操作
			 *  @type: string
			 */
			viewUrl:"" ,
			
			/**
			 * 添加空间级别的视图的URL
			 *  @type: string
			 */
			spaceViewAddUrl : "",
			
			/**
			 * 不包含contextPath的前缀
			 *  @type: string
			 */
			frag:"" ,
			
			/**
			 * 空间前缀
			 *  @type: string
			 */
			prefixCode: "",
			
			/**
			 * 是否具有ADMIN的权限
			 */
			hasAdminRole: false
	 			
		},
		
		/** 渲染所有视图列表*/
		renderViews: function(data){
			var me = this; 
			$("#userViewDiv").html("");
			var i;
			for(i=0;i<data.length;i++){
				$("<div id='view_detail_" + i +"'></div>").addClass("fav-detail").appendTo($("#userViewDiv"));
				(function(x){
					me._renderView($("#view_detail_" + x),data[x] );
				})(i);
			}
		},
		
		/** 渲染单个视图*/
		_renderView: function(div, data){
			var me = this;
			if(me.options.hasAdminRole){
				$("<span>").attr("title",Spark.util.message("userview.switch_to_spaceview"))
					.addClass("switch_to_spaceview").appendTo(div).hide();
			}
			$("<span>").attr("title",Spark.util.message("button.delete"))
				.addClass("delete_view").appendTo(div).hide();
			$("<span>").attr("title",Spark.util.message("userview.changename"))
	 			.addClass("change_view_name").appendTo(div).hide();
			$("<span>").addClass("view-icon").appendTo(div);
			$("<span>" + Spark.util.encodeHTML(data.name) + "</span>" ).addClass("view_name_span").appendTo(div);
			div
				.mouseover(function(){  
					if(Spark.util.ieVersion()==6){
						div.css("background","#e0f0ff");
					}
					div.find("span.delete_view").show();
					div.find("span.change_view_name").show();
					if(me.options.hasAdminRole){
						div.find("span.switch_to_spaceview").show();
					}
				})
				.mouseout(function(){ 
					if(Spark.util.ieVersion()==6){
						div.css("background","white");
					}
					div.find("span.delete_view").hide();
					div.find("span.change_view_name").hide();
					if(me.options.hasAdminRole){
						div.find("span.switch_to_spaceview").hide();
					}
				});
		 	
			div.find("span.view_name_span").click(function(){ 
			//	window.location.href = me.options.prefixUrl + data.url;
				window.location.replace(me.options.prefixUrl + data.url);
			//	window.location.reload();
				new Spark.pages.Tab({prefixCode: me.options.prefixCode});
			});
			
			div.find("span.change_view_name").click(function(){
				div.find("span.view_name_span")
					.html("<input class='view_name_input' size='15' type='text' value='" + data.name + "'/>") ;
				div.find("input.view_name_input")
					.focus()
					.click(function(){
						return false;
					})
					.keydown(function(event){ 
						if(event.keyCode=="13"){  
							var _name =  div.find("input.view_name_input").val().trim(); 
							if(_name == data.name){
								div.find("span.view_name_span").html(Spark.util.encodeHTML(data.name));
							}else if(_name.length == 0 || _name.length > 20){
								Spark.widgets.Alert.alert(Spark.util.message("userview.nameinputrange"),
										Spark.util.message("userview.header"),
										function(){ 
											div.find("input.view_name_input").show();
											div.find("input.view_name_input").focus();
										}
								);
							}else{
								var reg = /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/;
								if(reg.test(_name)){
									$.ajax({
										url: me.options.viewUrl,
										dataType:"json",
										contentType:"application/json",
										type: "POST",
										data: '{"name" : "' + _name + '", "id":' + data.id + '}',
										success: function(newData){
											if(newData.length == 1 && newData[0].id == -1){
												Spark.widgets.Alert.alert(Spark.util.message("userview.namealreadyexist"),Spark.util.message("userview.header"),
													function(){
														div.find("input.view_name_input").show();
														div.find("input.view_name_input").focus();
													});
											}else{
												me.renderViews(newData);
											}
										}
									});
									return false;
								}else{
									Spark.widgets.Alert.alert(Spark.util.message("userview.invalidInput"),
											Spark.util.message("userview.header"),
											function(){ 
												div.find("input.view_name_input").show();
												div.find("input.view_name_input").focus();
											}
									);
								}
								
							}
						}else if(event.keyCode=="27"){
							div.find("span.view_name_span").html("<a href='javascript:void(0);'>" + Spark.util.encodeHTML(data.name) + "</a>");
						}
					}) 
					.blur(function(event){  
						div.find("span.view_name_span").html("<a href='javascript:void(0);'>" + Spark.util.encodeHTML(data.name) + "</a>");
					});
				return false;
			});
	 
			div.find("span.delete_view").click(function(){   
				var _buttonsConfirm = {};
				_buttonsConfirm[Spark.util.message("button.cancel")]=function(){
					$(this).dialog('close');
				};
				_buttonsConfirm[Spark.util.message("button.ok")]=function(){
					$(this).dialog('close');
					var oldUrl = data.url;
					$.ajax({
						url: me.options.viewUrl,
						type: "DELETE",
						dataType:"json",
						contentType:"application/json",
						data: '{"id" : ' +  data.id + '}',
						success: function(newData){
							me.renderViews(newData);
						}
					});
				};
				$("#userview-dialog-confirm-content").html(Spark.util.message("userview.deleteconfirm"));
				$(function(){
					$("#userview-dialog-confirm").dialog({
						autoOpen: false,
						resizable: false,
						modal: true,
						buttons: _buttonsConfirm
					});
				});
				$("#userview-dialog-confirm").dialog('open');
				return false;
			});  
			
			div.find("span.switch_to_spaceview").click(function(){   
				var _buttonsConfirm = {};
				_buttonsConfirm[Spark.util.message("button.cancel")]=function(){
					$(this).dialog('close');
				};
				_buttonsConfirm[Spark.util.message("button.ok")]=function(){
					$(this).dialog('close');
					window.location.href= me.options.spaceViewAddUrl + "?url=" +  encodeURIComponent(data.url)+ "&name=" + encodeURIComponent(data.name);
				};
				$("#userview-dialog-confirm-content").html(Spark.util.message("userview.switch_to_spaceview_confirm"));
				$(function(){
					$("#userview-dialog-confirm").dialog({
						autoOpen: false,
						resizable: false,
						modal: true,
						buttons: _buttonsConfirm
					});
				});
				$("#userview-dialog-confirm").dialog('open');
			}); 
		},
		
		/** 初始化UI */
		_initUI: function(){ 
			var me = this; 
			me.showAll();
			
			$("<div id='userViewInputDiv'></div>").css("display","none").appendTo($("#" + me.options.containerDivId));
			$("<div id='userViewDiv'></div>").appendTo($("#" + me.options.containerDivId));
			var userViewStr = [];
			userViewStr.push("<input type='hidden' id='view_url'  style='align:right;'/>");
			userViewStr.push("<ul>" + Spark.util.message("userview.nameinput") + ": <input type='text' size='15' id='view_name'  style='align:right;padding:2px; margin:2px;'/> </ul>");
			userViewStr.push("<a id='addButton'  href='javascript:void(0);'>" + Spark.util.message("button.add") + "</a>&nbsp;<a id='escButton'  href='javascript:void(0);'>" + Spark.util.message("button.cancel") +"</a>");
 
			userViewStr.push("<div style='display:none;' id='userview-dialog-confirm' title='" + Spark.util.message("userview.heade") + "'>");
			userViewStr.push("<span class='ui-icon ui-icon-alert' style='float: left; margin: 0 7px 20px 0;'></span>");
			userViewStr.push("<div id='userview-dialog-confirm-content'></div>");
			userViewStr.push("</div>");	
			userViewStr.push("<div id='aaa'></div>");
			$("#userViewInputDiv").html(userViewStr.join(""));
			
			var isAddButtonFocusd = false;
			$("#view_name")
				.keydown(function(event){
					if(event.keyCode=="13"){
						 me.addView() ;
					}else if(event.keyCode=="27"){
						$("#userViewInputDiv").hide();
					}
				});
			
			$("#addButton")
				.click(function(){
					me.addView() ;
				})
				.mouseover(function(){
					isAddButtonFocusd = true;
				})
				.mouseout(function(){
					isAddButtonFocusd = false;
				});
			
			
			$("#escButton").click(function(){
				$("#userViewInputDiv").hide();
			});
			
			$("#" + me.options.userViewSaveDivId).click(function (){
				$("#userViewInputDiv").show();
				var loc = window.location.href;
				var rel = loc.substring(loc.indexOf(me.options.frag) + me.options.frag.length);
				$("#view_url" ).val(rel);
				$("#view_name").val("");
				$("#view_name").focus();
		 	});
			
			$("#" + me.options.spaceViewSaveDivId).click(function (){
				var _buttonsConfirm = {};
				_buttonsConfirm[Spark.util.message("button.cancel")]=function(){
					$(this).dialog('close');
				};
				_buttonsConfirm[Spark.util.message("button.ok")]=function(){
					$(this).dialog('close');
					var loc = window.location.href;
					var rel = loc.substring(loc.indexOf(me.options.frag) + me.options.frag.length);
					window.location.href= me.options.spaceViewAddUrl + "?url=" + encodeURIComponent(rel);
				};
				$("#userview-dialog-confirm-content").html(Spark.util.message("spaceview.save_spaceview_confirm"));
				$(function(){
					$("#userview-dialog-confirm").dialog({
						autoOpen: false,
						resizable: false,
						modal: true,
						buttons: _buttonsConfirm
					});
				});
				$("#userview-dialog-confirm").dialog('open');
		 	});
		},
		
		/**
		 * 打开确认框
		 * @param content 要显示的提示内容
		 * @return
		 */ 
		_showConfirmDialog: function(content){
			$("#userview-dialog-confirm-content").html(content);
			$("#userview-dialog-confirm").dialog('open');
		},
		
		/** 显示全部视图收藏*/
		showAll: function(){
			var me = this;
			$.ajax({
				url: me.options.showAllUrl,
				type: "GET",
				success: function(data,status){
				 	me.renderViews(data);
				}
			}); 
		},
	 
		/** 添加一个视图收藏*/
		addView: function(){ 
			var me = this;
			var name = $("#view_name").val().trim(), url = $("#view_url").val().trim();
			if(name.length > 20 || name.length <= 0){
				Spark.widgets.Alert.alert(Spark.util.message("userview.nameinputrange"),Spark.util.message("userview.header"),
						function(){
						$("#view_name").show();
						$("#view_name").focus();
				});
				return false;
			}
			if(url.length > 2083 || url.length <= 0){
				Spark.widgets.Alert.alert(Spark.util.message("userview.urlinputrange"),Spark.util.message("userview.header"),
						function(){
						$("#view_name").show();
						$("#view_name").focus();
				});
				return false;
			}
			var reg = /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/;
			if(!reg.test(name)){
				Spark.widgets.Alert.alert(Spark.util.message("userview.invalidInput"),Spark.util.message("userview.header"),
						function(){
						$("#view_name").show();
						$("#view_name").focus();
				});
				return false;
			}
			$.ajax({
				url: me.options.viewUrl,
				type: "PUT",
				dataType:"json",
				contentType:"application/json",
				data: '{"name": "' +  name + '", "url": "' + url + '"}',
				success: function(data){
					if(data.length == 1 && data[0].id == -1){
						Spark.widgets.Alert.alert(Spark.util.message("userview.namealreadyexist"),Spark.util.message("userview.header"),
							function(){
								$("#view_name").show();
								$("#view_name").focus();
						});
					}else{
						$("#view_name").val("");
						$("#view_url").val("");
						$("#userViewInputDiv").hide();
						me.renderViews(data);
					}
				} 
			});	
			return false;
		} 
	}
})();