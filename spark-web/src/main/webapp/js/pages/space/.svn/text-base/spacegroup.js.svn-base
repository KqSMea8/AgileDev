/**
 * 空间列表页js.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.space.spacegroup
 * @author Adun
 */
(function() {
	
	var $window = $(window);
	var Alert = Spark.widgets.Alert;
	var msg = Spark.util.message;

	/**
	 * 空间列表页对象的js类.
	 * @constructor
	 */
	Spark.pages.Spacegroup = function(options) {
		this.options = $.extend({}, this.options, options);
		this._init();
	};
	
	Spark.pages.Spacegroup.prototype = {
		/**
		 * 初始化页面中各种控件.
		 * @return
		 */
		_init : function pageInit () {
			var me = this;
			var spacegroupTabs = $("#[id^='" + me.options.tabIdPrefix + "']");
			if ( spacegroupTabs ){
				me._bindTabClick(spacegroupTabs);
				$(spacegroupTabs[ me.options.currentTab ? me.options.currentTab : 0 ])
					.trigger("click");
			}
		},
		
		/**
		 * 为页面中的每个Tab,绑定点击事件
		 * @param spacegroupTabs 所有的空间组的tab标签
		 * @return
		 */
		_bindTabClick : function AddTabClickEvent (spacegroupTabs) {
			var me = this;
			if ( !spacegroupTabs ){
				return;
			}
			//tab的总数
			var tabCount = spacegroupTabs.length;
			//遍历tab,对每一个tab进行事件绑定
			for (var i=0; i<tabCount; i++){
				(function (tabNo) {
					var tab = $(spacegroupTabs[tabNo]);
					tab.bind("click", function(){
						$("#" + me.options.tabContainer + me.options.currentTab).removeClass("selected");
						me.options.currentTab = tabNo;
						$("#" + me.options.tabContainer + me.options.currentTab).addClass("selected");
						me._getSpaceListView( tab );
					});
				})(i);
			}
		},
		
		/**
		 * tab点击后所触发的事件.刷新空间列表的显示
		 * @param tab 触发点击事件的tab
		 * @return
		 */
		_getSpaceListView : function getAndDisplaySpaceInChosenSpacegroup ( tab ) {
			var me = this;
			
			//获取空间组的id. tab的Id = 固定前缀 + 空间组Id. 从中提取空间组Id
			var spacegroupId = tab.attr("id").replace(me.options.tabIdPrefix, "");
			
			//获取空间列表,并在回调函数中将空间显示在页面上
			var url = Spark.constants.CONTEXT + "/ajax/spacegroup/list/" + spacegroupId;
			$.ajax({
				url: url,
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				cache: false,
				success: function(spaceList, status) {
					me._generateSpaceListView( spaceList );
				},
				error: function(xhr, status){
					if(xhr.status == 403){
						 Spark.util.handleAjaxError(xhr);
					}else{
						Alert.alert(msg("card.list.pagination.failure"));
					}
				}
			});
		},
		
		/**
		 * 根据ajax返回的spaceList,清空当前的空间列表,并将查询到的空间列表现在在页面上
		 * @param spaceList 查询到的空间列表
		 * @return
		 */
		_generateSpaceListView : function generateSpacesUseingSpaceList ( spaceList ){
			var me = this;
			
			var listContainer = $("#" + me.options.listContainerId);
			listContainer.empty();
			
			var spaceCount = spaceList.length;
			if( spaceCount == 0){
				//如果返回空间个数为0，则显示未找到结果的提示信息
				var emptyResultMessageDiv = $("#" + me.options.emptyResultMessageDivId).clone();
				listContainer.append( emptyResultMessageDiv );
				emptyResultMessageDiv.css( "display", "block" );
			}else{
				//将空间逐个显示在页面上
				var listTable = $("<table>").addClass("space-table").addClass("wordwrap").appendTo(listContainer);
				var currentSpace, picTd, contentTd, tr;
				for (var i = 0; i < spaceCount; i++) {
					currentSpace = spaceList[i];
					(function(space){
						var spaceTr = $("<tr>");
						picTd = $("<td>").addClass("space-icon").append( $("<div>").append( $("<img>").attr( "alt", "" ).attr( "src", Spark.constants.CONTEXT + "/images/space.png" ) ) );
						contentTd = $("<td>").append( 
								$("<div>").addClass( "space-container" )
									.append( $("<div>").addClass("space-operation").addClass( (space.isFavorite ? "" : "un") + "keep-star" ).bind( "click", function(){ me.favorite( $(this), spaceTr, space.prefixCode ); } ) ).attr("title",msg("spacegroup.message.choosestar"))
									.append( $("<div>").addClass( "space-item" ).bind( "click", function(){ window.location.href = Spark.constants.CONTEXT + "/spaces/" + space.prefixCode ; } ).attr("title",msg("spacegroup.message.enterspace"))
											.append( $("<h3>").append( $("<a>").attr( "href", Spark.constants.CONTEXT + "/spaces/" + space.prefixCode ).text( space.name ) ) )
											.append( $("<div>").addClass( "space-detail" )
													.append( $("<span>").addClass( "prefix-code" ).text( space.prefixCode ) )
													.append( " " )
													.append( $("<span>").addClass( "weak" ).text( space.description ) ) 
												)
										)
							);
						spaceTr.appendTo( listTable ).append( picTd ).append( contentTd );
					})(currentSpace);
				}
			}
		},
		
		/**
		 * 更改用户对指定空间的"是否收藏"属性
		 * @param picDiv 用户所点击的图片的div. 点击后需要修改此div的样式,来告知用户修改完成了.
		 * @param spaceTr 指定空间所在的div. 在"用户收藏"tab页, 用户取消收藏的话,需要删掉整个tr,向用户表示此空间已经不再收藏了.
		 * @param prefixCode 指定空间的prefixCode
		 * @return
		 */
		favorite : function doOrUndoFavorite ( picDiv, spaceTr, prefixCode) {
			var me = this;
			var url = Spark.constants.CONTEXT + "/ajax/spacegroup/favorite/" + prefixCode;
			$.ajax({
				url: url,
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				cache: false,
				success: function(favoriteState, status) {
					if ( me.options.currentTab == 0 ){
						//如果是在"用户收藏"页,移除整个tr
						if ( favoriteState == false ){
							spaceTr.remove();
						}
					}else{
						//如果不是在"用户收藏"页, 修改图片
						picDiv.removeClass( "unkeep-star" ).removeClass( "keep-star" ).addClass( (favoriteState ? "" : "un") + "keep-star" );
					}
				},
				error: function(xhr, status){
					if(xhr.status == 403){
						 Spark.util.handleAjaxError(xhr);
					}else{
						Alert.alert(msg("card.list.pagination.failure"));
					}
				}
			});
		},
		
		/**
		 * js对象的各个参数
		 */
		options : {
			
			tabContainer : 'spaceGroupTab',
			/**
			 * tab标签Id的前缀. tab的Id = 固定前缀 + 空间组Id.
			 */
			tabIdPrefix : 'spacegroupTabSpan',
			/**
			 * 存放空间列表的div容器的Id
			 */
			listContainerId : 'container-body',
			/**
			 * 如果查到列表为空,则需要显示的内容的div的Id
			 */
			emptyResultMessageDivId : 'empty-result-message',
			/**
			 * 页面初始化时,需要显示的tab的序号.
			 * 默认为0，即显示第一个tab.
			 */
			currentTab : 0
		}
	};
})();