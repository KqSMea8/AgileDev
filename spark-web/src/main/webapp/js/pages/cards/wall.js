/**
 * 列表视图组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.Wall
 * @author zhangjing_pe
 */
(function() {
	
	var History = Spark.util.History;
	var Menu = Spark.widgets.Menu;
	var Alert = Spark.widgets.Alert;
	var $window = $(window);
	var msg = Spark.util.message;
	var spaceService = Spark.service.SpaceService;
	var cardService = Spark.service.CardService;
	var makeArray = Spark.util.makeArray;
		
	/**
	 * 墙视图组件构造器.
	 * @constructor
	 */
	Spark.pages.Wall = function(options) {
		if (!options || !options.prefixCode ) {
			throw new Error("Argument of Wall constructor must not be null.");
		}
		this.options = $.extend(this.options, options);
		this.widgets = {};
		this.widgets.wallContainer = $("table.card-wall-table tbody");
		
		var me = this;
		
		// 初始化hash
		this.query = new Spark.widgets.Query( History.getHash() );
		
		// 加载初始化数据
		spaceService.load(this.options.prefixCode, function(metadata) {
			me.metadata = metadata;
			
			// 如果与icafe关联则允许自定义显示project列
			var projects = metadata.space.projects;
			if (projects && !!projects.length) {
				me.options.selectableSystemColumns.push("project");
			}
			
			// 生成列选择菜单
			me.widgets.selectColumnsMenu = me._generateGroupbyColumnsMenu();
				
			me.show();
		});

		// 初始化hash监听
		History.bind(function(module, newState, oldState) {
			Spark.util.TabUtils.setLastView(me.options.prefixCode ,"/wall#" + Spark.util.History.getHash());
			//如果Hash值没有变化，则不重新发起请求
			if( me.currentHash == History.getHash()){
				return;
			}else{
				me.currentHash = History.getHash();
				//如果查询条件变化，则当前页重置
				if( module == "q"){
					me.options.page = 1;
					History.go("page", me.options.page);
				}
				
				me.show();
			};
			
		},false);
		//绑定分组变化的事件
		$window.bind("resize",function(e){
			me.updateTableStyle();
		});
		$window.bind("sidebarBtnClick",function(e){
			me.updateTableStyle();
		});
		return this;
	};
	
	Spark.pages.Wall.prototype = {
		
		/**
		 * 选项列表.
		 * 
		 * @property options
		 * @type Object
		 */
		options: {
			
			/**
			 * 空间别名.
			 * 
			 * @property prefixCode
			 * @type String
			 */
			prefixCode: null,
			
			/**
			 * 列表视图根元素.
			 * 
			 * @property list,
			 * @type String | HtmlElement
			 * @default #list
			 */
			wall: "#wall",
			
			/**
			 * 列表视图操作表头元素.
			 * 
			 * <p>
			 * 提供操作列表数据的页面元素
			 * </p>
			 * @property viewbar
			 * @type String | HtmlElement
			 * @default #viewbar
			 */
			viewbar: "#viewbar",
			
			/**
			 * 列表视图数据表头元素.
			 * 
			 * <p>
			 * 显示列表数据的字段名称元素
			 * </p>
			 * @property cardsHeader
			 * @type String | HtmlElement
			 * @default #cardsHeader
			 */
			cardsHeader: "#cards-header",
			
			/**
			 * 卡片墙视图数据th元素前缀.
			 * 
			 * <p>
			 * 显示列表数据的字段名称元素
			 * </p>
			 * @property cardsHeader
			 * @type String | HtmlElement
			 * @default #cardsHeader
			 */
			swimmingPoolHeadPrefix: "swimming-pool-th-",
			
			/**
			 * 卡片墙视图数据内容元素前缀.
			 * 
			 * <p>
			 * 显示列表数据的字段名称元素
			 * </p>
			 * @property cardsHeader
			 * @type String | HtmlElement
			 * @default #cardsHeader
			 */
			swimmingPoolBodyPrefix: "swimming-pool-body-",
			
			/**
			 * 列表视图信息元素
			 * <p>
			 * 当查询结果为空时显示用户提示信息
			 * </p>
			 * @property messagebar
			 * @type String | HtmlElment
			 * @default #empty-result-message
			 */
			messagebar: "#empty-result-message",
			
			/**
			 * 卡片宽度
			 * <p>
			 * 卡片实际宽度
			 * </p>
			 * @property cardWidth
			 * @type int
			 */
			cardWidth: 100,
			/**
			 * 卡片高度
			 * <p>
			 * 卡片实际高度
			 * </p>
			 * @property cardHeight
			 * @type int
			 */
			cardHeight:74,
			/**
			 * 卡片边框大小
			 * <p>
			 * 卡片边框大小
			 * </p>
			 * @property cardBorder
			 * @type int
			 */
			cardBorder:1,
			
			/**
			 * 卡片横向margin像素
			 * <p>
			 * 卡片横向margin像素
			 * </p>
			 * @property cardMargin
			 * @type int
			 */
			cardMargin:4,
			/**
			 * 卡片横向padding像素
			 * <p>
			 * 卡片横向padding像素
			 * </p>
			 * @property cardPadding
			 * @type int
			 */
			cardPadding:3,
			
			/**
			 * 卡片墙的高度
			 * <p>
			 * 卡片实际宽度
			 * </p>
			 * @property cardWidth
			 * @type int
			 */
			wallHeadHeight:30,
			
			/**
			 * 卡片墙表格的id.
			 * 
			 * <p>
			 * 显示列表数据的字段名称元素
			 * </p>
			 * @property cardsHeader
			 * @type String | HtmlElement
			 * @default #cardsHeader
			 */
			wallTable: "#wall-table",
			
			/**
			 * 系统字段国际化消息前缀.
			 * 
			 * @property columnMessagePrefix
			 * @type String
			 * @default "card."
			 */
			columnMessagePrefix: "card.",
			
			/**
			 * 可供用户选择的系统字段.
			 * 
			 * @property selectableSystemColumns
			 * @type Array
			 * @default [ "createdUser", "createdTime", "lastModifiedUser", "lastModifiedTime", "cardType" ]
			 */
			selectableSystemColumns: [ "createdUser", "createdTime", "lastModifiedUser", "lastModifiedTime", "cardType", "parent" ],
			
			/**
			 * 列表视图默认显示的字段名称.
			 * @property defaultVisibleColumns
			 * @type Array
			 * @default [ "sequence", "title", "cardType"]
			 */
			defaultVisibleColumns : [ "sequence", "title"],

			/**
			 * 是否为外部提供UI视图
			 * 如果为外部提供视图，则跳转需要打开新窗口，而且不展现操作列
			 */
			isExternal: false
		},
		
		/**
		 * 空间元数据.
		 * 
		 * @property metadata
		 * @type Object
		 */
		metadata : null,
		
		/**
		 * 卡片列表分页数据.
		 * 
		 * @property pagination
		 * @type Object 
		 */
		cards : null,
		
		/**
		 * 合法的卡片属性id，用于计算卡片是否允许拖动.
		 * 
		 * @property validCardTypes
		 * @type Object 
		 */
		validCardTypes:{},
		
		/**
		 * 分组的column
		 * @property groupby
		 * @type string
		 * @default null
		 */
		groupby: null,
		
		/**
		 * 当前类相关物件列表.
		 * 
		 * @property widgets
		 * @type Object
		 */
		widgets: null,
		
		/**
		 * 当前的查询条件对象.
		 * 
		 * @property query
		 * @type Object {Spark.widgets.Query}
		 */
		query: null,
		
		/**
		 * 当前Hash值
		 * 记录当前值避免hash变化回调多次会触发多次请求
		 * @property currentHash
		 * @type String
		 */
		currentHash: null,
		
		/**
		 * 与导出Excel相关的配置信息
		 * 在_generateExportToExcelButton方法里会使用
		 * 需要初始化为一个对象,而不是null
		 * @property exportOption
		 * @type Object (map)
		 * @author 蹲蹲~~
		 */
		exportOption : {},
		
		/**
		 * 根据URL初始化各种相关参数

		 * @private
		 * @method initialize
		 * @param url String
		 */
		_initialize : function ( url ){
			var params = Spark.util.getParametersAndBookmarks(url) || {};
			var page, size ;
			page = params["page"];
			if( page && page != this.options.page ){
				this.options.page = page;
			}
			size = params["size"];
			if(  size && size != this.options.size ){
				this.options.size = size;
			}
			this.query = new Spark.widgets.Query( History.getHash() );
			this.currentHash = History.getHash();
			this.groupby = this.query.groupby.groupby;
			
			//计算合法的cardType值
			for(var typeId in this.metadata.cardTypes){
				var cardType = this.metadata.cardTypes[typeId];
				for(var propertyId in cardType.cardProperties){
					var property = cardType.cardProperties[propertyId];
					if(property.localId == this.groupby){
						this.validCardTypes[cardType.id] = 1;
						break;
					}
				}
			}
		},
		
		/**
		 * 显示列表数据
		 * @method show
		 */
		show: function Wall_show(){
			//添加loading图标
			Spark.util.Load.add('loading-area');
			//根据URL初始化各参数
			this._initialize( window.location.href );
			
			//组装请求URL
			var url = Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/cards/wall?";
			var params = [], queryparams;
			queryparams = this.query.encodedSerialize();
			if ( queryparams ){
				params.push(queryparams);
			}
			url = url + params.join("&");
			var me = this;
			$("#card-list-container").css("display","none");
			
			//渲染选择的字段
			var id = this.query.groupby.groupby,text;
			if(id == null){
				text = msg("label.wall.groupby_notset");
			}else{
				text = this.metadata.cardProperties[id].name;
			}
			
			//设置分组条件不可修改
			me._setGroupbyLabel(text,false);
			
			$.ajax({
				url: url,
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				cache: false,
				success: function(cards, status) {
					$("#card-list-container").css("display","block");
					Spark.util.Load.remove('loading-area');
					if( cards.length > 0){
						$(me.options.messagebar).hide();
						$(me.options.wall).show();
						// 组装卡片数据
						cards = cardService.assemble(cards);
						me.cards = cards;
						me._render( cards, $(me.options.wall));
					}else{
						$(me.options.wall).hide();
						$(me.options.messagebar).show();
					}
					//设置分组字段可点击
					me._setGroupbyLabel(text,true);
				},
				error: function(xhr, status){
					if(xhr.status == 403){
						 Spark.util.handleAjaxError(xhr);
					}else{
						//TODO 这里的问题
						Alert.alert(msg("card.list.pagination.failure"));
					}
					Spark.util.Load.remove('loading-area');
					//设置分组字段可点击
					me._setGroupbyLabel(text,true);
				}
			});
		},
		
		/**
		 * 设置分组字段的显示.
		 * 
		 * @param text {String}分组字段的名称
		 * @param isClickable {boolean} 是否可以点击
		 * @method _setGroupbyLabel
		 * @private
		 */
		_setGroupbyLabel: function Wall__setGroupbyLabel(text,isClickable){
			if(isClickable){
				$("#disable-click-groupby").text("");
				$("#enable-click-groupby").text(text);
			}else{
				$("#enable-click-groupby").text("");
				$("#disable-click-groupby").text(text);
			}
		},
		
		/**
		 * 渲染列表内容页面.
		 * 
		 * @param cards {Object}卡片对象
		 * @param container {HTMLElement} HTML容器元素
		 * @method render
		 * @private
		 */
		_render: function Wall__render(cards, container ) {
			//渲染泳道数据
			this._renderSwimmingpool();
			//渲染卡片数据
			this._renderRecords();
			//渲染卡片数量
			this._renderPoolCardsNum();
			
			this.updateTableStyle();
			
			this.bindDroppable();
			
			/*$('.wall_body_inner').css('-moz-user-select', 'none'); //禁止fx選取文字
			$('.wall_body_inner').get(0).onselectstart = function(){return false;}; //禁止IE選取文字
			$('.wall-card').css('-moz-user-select', 'none'); //禁止fx選取文字
			$('.wall-card').get(0).onselectstart = function(){return false;}; //禁止IE選取文字
			*/
		},
		/**
		 * 渲染泳道数据
		 */
		_renderSwimmingpool: function Wall__renderSwimmingpool(){
			var tbody = this.widgets.wallContainer, card, i, row, actions, option;
			tbody.empty();	
			var cards = this.cards;
			if (!cards || !cards.length) {
				return;
			}
			row = $('<tr id="wall_head" align="center"></tr>').appendTo(tbody);
			row.append(this._generateWallTd(0,msg("label.wall.groupby_notset")));
			if (this.groupby != null && this.groupby != "0" ) {
				var optionMap = this.metadata.cardProperties[this.groupby].optionMap;
                var optionKeys = this.metadata.cardProperties[this.groupby].optionKeys;
                for (i=0;i<optionKeys.length;i++){
                    option = optionKeys[i];
                    row.append(this._generateWallTd(option,optionMap[option]));
                }
			}
		},
		
		/**
		 * 渲染列数据
		 * 
		 * @method _renderRecords
		 * @private
		 */
		_renderRecords: function Wall__renderRecords(){
			var cards = this.cards;
			var tbody = this.widgets.wallContainer, card, i, row, actions;
			var canBeMoved;
			var space = this.metadata.space;
			
			if (!cards || !cards.length) {
				return;
			}
			
			// 渲染
			var tdCode,appendToDefault;
			if(this.groupby == null || this.groupby == "0"){
				tdCode = "0";
				appendToDefault = true;
			}
			for (i = 0; i < cards.length; i++) {
				card = cards[i];
				canBeMoved = (this.validCardTypes[card.type.id] && (space.permissions.write == true || card.createdUser.id == Spark.constants.CURRENT_USER.id));
				var found = false;
				if(!appendToDefault){
					for(var propertyValue in card.propertyValues){
						if(card.propertyValues[propertyValue].cardProperty.localId == parseInt(this.groupby)){
							tdCode = card.propertyValues[propertyValue].value;
							if(tdCode){
								found = true;	
							}
							break;
						}
					}
					if(!found){
						tdCode = "0";
					}
					this._genrerateCardDiv(card.sequence,card.title,canBeMoved).appendTo($("#"+this.options.swimmingPoolBodyPrefix+tdCode));
				}else{
					this._genrerateCardDiv(card.sequence,card.title,canBeMoved).appendTo($("#"+this.options.swimmingPoolBodyPrefix+0));
				}
			}
			
		},
		/**
		 * 渲染每个泳道的卡片数量
		 * 
		 * @method _renderPoolCardsNum
		 * @private
		 */
		_renderPoolCardsNum : function Wall__renderPoolSize(){
			var me = this;
			$.each($(".pool_card_num_span"),function(index,value){
				var span = $(value),id=span.attr("poolid");
				span.text("("+$("#"+me.options.swimmingPoolBodyPrefix+id).children().length+")");				
			});
			
		},
		/**
		 * 获取卡片墙td的对象
		 * 
		 * @method _generateWallTd
		 * @private
		 */
		_generateWallTd: function Wall__generateWallTd(id,name){
			var td = $('<td></td>');
			var tdswimmingPool = $('<div class="pool_div" id="'+this.options.swimmingPoolHeadPrefix+ id +'" poolid="'+id+'"></div>').appendTo(td);
			tdswimmingPool.append($('<div class="wall_head">'+name+'</div>').append('<span id="card_num_span_'+id+'" class="pool_card_num_span" poolid="'+id+'"></span>'));
			$('<div class="wall_body" ></div>').appendTo(tdswimmingPool).append('<div class="wall_body_inner" id="'+this.options.swimmingPoolBodyPrefix+id+'"></div>').appendTo(tdswimmingPool);
			return td;
		},
		/**
		 * 渲染每个卡片的对象
		 * 
		 * @method _genrerateCardDiv
		 * @private
		 */
		_genrerateCardDiv : function Wall__generateCardDiv(sequence,title,moveable){
			var div = $('<div id="card-'+sequence+'" class="radius-small wall-card '+(moveable?"moveable":"notdraggable")+'" sequence="'+sequence+'"></div>');
			div.append(
						$('<span class="card-sequence"></span>')
							.append('<a href="'+sequence+'">#'+sequence+"</a>"));
			div.append('<span title="'+title+'">'+title+'</span>');
			return div;
		},
		/**
		 * 更新表格的宽度和高度.
		 * 
		 * @method updateTableStyle
		 * @private
		 */
		updateTableStyle:function Wall_updateTableStyle(){
			this._updateTableWidth();
			this._updateTableHeight();
			//更新最后一列的边框
			$(".wall_head:last").addClass("wall_head_last");
			$(".wall_body:last").addClass("wall_body_last");
		},
		/**
		 * 更新表格的宽度.
		 * 
		 * @method _updateTableWidth
		 * @private
		 */
		_updateTableWidth: function Wall___updateTableWidth(){

			//子元素个数，计算宽度
			var columnSize = $("#wall_head").children().length,tdSize = $(this.options.wallTable).width()/columnSize;
			var cardFullWidth = this.options.cardWidth + 2*this.options.cardBorder + 2*this.options.cardMargin + 2*this.options.cardPadding;
			if(tdSize < cardFullWidth){
				//减去卡片和td的border
				this._setCardWidth(tdSize - 2*this.options.cardBorder - 2*this.options.cardMargin - 2* this.options.cardMargin);
			}else{
				var size = Math.floor((tdSize - 2) / cardFullWidth);
				this._setCardWidth(this.options.cardWidth);
				$(".wall_body_inner").width(cardFullWidth * size);
			}
		},
		/**
		 * 更新表格的高度.
		 * 
		 * @method _updateTableWidth
		 * @private
		 */
		_updateTableHeight: function Wall_updateTableHeight(){
			$(".card-wall-table .wall_body").height("100%");
			//设置wall_body的高度，来设置数据较少的列能显示到窗口下方的完整的边框
			$(".card-wall-table .wall_body").height($($(".card-wall-table tr")).height()-this.options.wallHeadHeight);

		},
		
		_setCardWidth:function Wall_setCardWidth(width){
			var cards = $(".wall-card");
			cards.height(width*this.options.cardHeight/this.options.cardWidth);
			cards.width(width);
			return cards;
		},
			
		/**
		 * 生成自定义显示列下拉菜单按钮.
		 * 
		 * @method __generateGroupbyColumnsMenu
		 * @private
		 */
		_generateGroupbyColumnsMenu: function Wall__generateGroupbyColumnMenu() {
			var me = this;
			var i, ii, id, items = [], 
				properties = this.metadata.cardProperties,
				columns = this.query.getColumns(),
				selectableSystemColumns = this.options.selectableSystemColumns;
				
			// 点击分组的回调函数
			var callback = function(atag, id, selected) {
				me.onGroupby(null, id);
			};
			items.push({
				key: 0,
				txt: msg("label.wall.groupby_notset"), 
				props: 0,
				link: callback,
				defaultSelected: true
			});
			// 自定义字段
			for (id in properties) {
				if(properties[id].type=="list"){
					items.push({
						key: id,
						txt: properties[id].name, 
						props: id,
						link: callback,
						defaultSelected: !!columns[id]
					});
				}
			}
			
			// Chrome 排序bug FIXME 这里等于没排序
			items.sort(function(l, r) {
				return ((l.sort || 0) - (r.sort || 0)) || ((l.id || 0) -  (r.id || 0));
			});
			
			var target = $("#select-columns");	
			
			return new Menu({
				target: target,
				options: items,
				multi_select: false,
				css: "spark-menu-custom"
			});
		},
		/**
		 * 绑定拖动事件.
		 * 
		 * @method bindDroppable
		 * @private
		 */
		bindDroppable: function Wall_BindDroppable(){
			
			var me = this;
			
			$(".moveable").draggable({revert:true,revertDuration:0,zIndex:1 });
			
			$(".pool_div").droppable({
				accept: ".moveable",
				hoverClass: 'wall_body_dragging',
				drop: function(ev, ui) {
					var fromPropertyValue = $(ui.draggable).parents(".pool_div").attr("poolid");
					var toPropertyValue = $(this).attr("poolid");
					var cardSequence = $(ui.draggable).attr("sequence");
					var droppable = this;
					var loadingIconAddTo1 = "card_num_span_"+fromPropertyValue;
					var loadingIconAddTo2 = "card_num_span_"+toPropertyValue;
					
					if(fromPropertyValue != toPropertyValue){
						$(droppable).children(".wall_body").children(".wall_body_inner").prepend(ui.draggable);	
						me._updateTableHeight();
						
						var reload = function(){
							window.location.reload(true);
						}
						Spark.util.Load.add(loadingIconAddTo1);
						Spark.util.Load.add(loadingIconAddTo2);
						$.ajax({
							url : Spark.constants.CONTEXT + "/ajax/spaces/" + me.options.prefixCode + "/cards/" + cardSequence + "/changeProperty",
							type : "POST",
							data :  {"propertyId" : me.groupby, "propertyValue" :  toPropertyValue } ,
							cache: false,
							timeout: 10000,
							success: function(cards, status) {
								Spark.util.Load.remove(loadingIconAddTo1);
								Spark.util.Load.remove(loadingIconAddTo2);
								me._renderPoolCardsNum();
							},
							error: function(xhr, status){
								Spark.util.Load.remove(loadingIconAddTo1);
								Spark.util.Load.remove(loadingIconAddTo2);
								me._renderPoolCardsNum();
								if(status == "timeout"){
									Alert.alert(msg("card.walldrag.timeout"),"",reload);
								}else if(xhr.status == 403 || xhr.status == 404){
									if(xhr.responseText){
										Alert.alert(xhr.responseText);
									}else{
										Spark.util.handleAjaxError(xhr);										
									}
								}else{
									Alert.alert(msg("card.walldrag.failure"),"",reload);
									
								}
								
							}
						});
					}
					
				}
			});
		},
		
		onGroupby: function Wall_OnGroupby(e, id){
			if(id == "0"){
				this.query.clearGroupby();
			}else{
				this.query.setGroupby(id);
			}
			this.groupby = id;
			History.go(this.query.serializeGroupby(true));
		}
		
	}
	
})();