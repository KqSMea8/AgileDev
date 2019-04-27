/**
 * 列表视图组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.List
 * @author chenhui
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
	 * 列表视图组件构造器.
	 * @constructor
	 */
	Spark.pages.List = function(options) {
		if (!options || !options.prefixCode ) {
			throw new Error("Argument of List constructor must not be null.");
		}
		this.options = $.extend(this.options, options);
		this.widgets = {};
		this.widgets.listContainer = $("table.list tbody");
		
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
			
			// 生成"导出到Excel"按钮,added by 小蹲子 2010-10-19
			me._generateExportToExcelButton();
			
			// 生成"分配到项目"菜单
			me._generateAssignToProjectMenu();
			
			// 生成列选择菜单
			me.widgets.selectColumnsMenu = me._generateSelectColumnsMenu();
			
			// 生成"批量修改"按钮
			me._generateBatchUpdateButton();

			// 生成"批量删除"按钮
			me._generateBatchDeleteButton();
			
			me.show();
		});

		// 初始化hash监听
		History.bind(function(module, newState, oldState) {
			Spark.util.TabUtils.setLastView(me.options.prefixCode ,"/list#" + Spark.util.History.getHash());
			if( module == "c"){
				me.onColumnChange(History.getHash());
				return;
			}
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
		
		// 绑定事件
		$window.bind("columnAdd", function(e, propertyId) {
			me.onColumnAdd(e, propertyId);
		})
		$window.bind("columnRemove", function(e, propertyId) {
			me.onColumnRemove(e, propertyId);
		})
		return this;
	};
	
	Spark.pages.List.prototype = {
		
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
			list: "#list",
			
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
			 * 列表视图信息元素
			 * <p>
			 * 当查询结果为空时显示用户提示信息
			 * </p>
			 * @property messagebar
			 * @type String | HtmlElment
			 * @default #error-message
			 */
			messagebar: "#error-message",
			
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
			 * 列表当前页数.
			 * 
			 * @property page
			 * @type Number
			 * @default 1
			 */
			page: 1,
			
			/**
			 * 列表最大条数.
			 * 
			 * @property size
			 * @type Number
			 * @default 20
			 */
			size: 20,
			
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
		pagination : null,
		
		/**
		 * 卡片列表对象, 以卡片id为键, 卡片json对象为值.
		 * 
		 * @property cards
		 * @type Object 
		 */
		cards : {},
		
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
		},
		
		/**
		 * 显示列表数据
		 * @method show
		 */
		show: function List_show(){
			//添加loading图标
			Spark.util.Load.add('loading-area');
			//根据URL初始化各参数
			this._initialize( window.location.href );

			//组装请求URL
			var url = Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/cards/list?";
			var params = [], queryparams;
			params.push("page=" + this.options.page);
			params.push("size=" + this.options.size);
			queryparams = this.query.encodedSerialize();
			if ( queryparams ){
				params.push(queryparams);
			}
			url = url + params.join("&");
			var me = this;
			$.ajax({
				url: url,
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				cache: false,
				success: function(pagination, status) {
					Spark.util.Load.remove('loading-area');
					if( pagination.total > 0){
						$(me.options.messagebar).hide();
						$(me.options.list).show();
						// 组装卡片数据
						pagination.results = cardService.assemble(pagination.results);
						me.pagination = pagination;
						me._render( pagination, $(me.options.list));
						$.each(pagination.results, function(i, d){
							me.cards[d.sequence] = d;
						})
					}else{
						$(me.options.list).hide();
						$(me.options.messagebar).find(".info-message").text(msg("cards.emptyResult"));
						$(me.options.messagebar).show();
					}
				},
				error: function(xhr, status){
					if(xhr.status == 403){
						 Spark.util.handleAjaxError(xhr);
					}else{
						Alert.alert(msg("card.list.pagination.failure"));
					}
					Spark.util.Load.remove('loading-area');
				}
			});
		},
		
		/**
		 * 渲染列表内容页面.
		 * 
		 * @param pagination {Object}卡片分页对象
		 * @param container {HTMLElement} HTML容器元素
		 * @method render
		 * @private
		 */
		_render: function List__render(pagination, container ) {
			//设置默认排序字段
			if( this.query.isSortsEmpty() ){
				this.query.addSort("sequence" ,"desc");
			}
			
			//渲染列表数据
			this._renderRecords();
			
			//渲染分页数据
			this._renderPagination(pagination, container);

		},
		
		/**
		 * 增加列表视图中的默认显示字段
		 * @see defaultVisibleColumns
		 * @see _renderColumns
		 */
		_addDefaultVisibleColumns: function List__addDefaultVisibleColumns(){
			//设置默认显示字段
			if( this.query.isColumnsEmpty()){
				this.query.addColumn( this.options.defaultVisibleColumns);
			}
			if(  !this.query.getColumn("sequence") || !this.query.getColumn("title")){
				var columns = this.query.getColumns(), currentColumns = [];
				for (key in columns) {
					currentColumns.push(key);
				}
				this.query.clearColumns();
				this.query.addColumn( this.options.defaultVisibleColumns );
				this.query.addColumn( currentColumns );
			}
		},
		
		/**
		 * 清除默认显示字段
		 * <p>
		 * 为了在URL上保持一致，columns中不包括title和sequence，需要默认显示字段才可以在两边切换
		 * </p>
		 * @see hierarchy.js
		 * @see _renderColumns
		 */
		_removeDefaultVisibleColumns: function List__removeDefaultVisibleColumns(){
			for ( column in this.options.defaultVisibleColumns ){
				this.query.removeColumn(this.options.defaultVisibleColumns[column]);
			}
		},
		
		/**
		 * 渲染列数据
		 * 
		 * @method _renderRecords
		 * @private
		 */
		_renderRecords: function List__renderRecords(){
			var cards = this.pagination.results;
			
			var tbody = this.widgets.listContainer, card, i, row, actions;
			//移除所有列
			tbody.empty();	
			
			if (!cards || !cards.length) {
				return;
			}
			
			// 渲染
			for (i = 0; i < cards.length; i++) {
				card = cards[i];
				row = $('<tr id="card-' + card.sequence + '"></td>')
					.appendTo(tbody);
				//选择框列和卡片类型颜色列
				$('<td class="checkbox"><input type="checkbox" class="check-card" value="' + card.sequence + '" /></td>').appendTo(row);
				$('<td class="colorful">')
					.attr("style", "background-color:#" + card.type.color)
					.attr("title", card.type.name)
					.appendTo(row);
			
				//卡片操作列				
				actions = $('<td class="actions"></td>').appendTo(row);
				//为外部提供的UI提供操作列
				if ( !this.options.isExternal ){
					this._generateActionMenu(actions, card);
				}
			}
			
			// 渲染列
			this._renderColumns();
		},
		
		/**
		 * 渲染列.
		 * 
		 * @method _renderColumns
		 * @private
		 */
		_renderColumns: function List__renderColumns(){
			// 设置默认显示字段
			this._addDefaultVisibleColumns();
			
			var value, i, ii, j, jj, card, colAction, cardUrl, $column, cardUrl,
				columns = this._getSortedColumns(),
				cards = this.pagination.results,
				me = this
				;
			cardUrl = Spark.constants.CONTEXT + '/spaces/' + this.options.prefixCode + '/cards/'
			
			// 清除所有自定义列
			this.widgets.listContainer.find(".column").remove();
			
			for (i = 0, ii = cards.length; i < ii; i++) {
				card = cards[i];
				colAction = $("#card-" + card.sequence + " td.actions");
				for (j = 0, jj = columns.length; j < jj; j++) {
					col = columns[j];
					value = card.values[col];
					$column =  $('<td class="column"></td>').insertBefore(colAction);
					
					if( col == "sequence"){
						$('<a class="left" href="' + cardUrl + card.sequence + '"></a>')
						.text('#' + card.sequence)
						.attr("target", me.options.isExternal ? "_blank": "_self")
						.appendTo($column);
						//附件标识
						if ( card.validAttachments && card.validAttachments.length  > 0){
							$('<span class="attachment-icon left"></span>').appendTo($column);
						}
						$column.addClass("sequence").attr("nowrap","true");
					}else if ( col == "title"){
						$('<a href="' + cardUrl + card.sequence + '"></a>')
						.text(card.title)
						.attr("target", me.options.isExternal ? "_blank": "_self")
						.appendTo($column);
						$column.addClass("card-title");
					}else{
						$column.text( (value ? value.displayValue : "") );
					}
				}
			}
			//渲染列表表头数据
			this._renderColumnsHeader();
			// 清除默认显示字段
			this._removeDefaultVisibleColumns();
		},
		
		/**
		 * 渲染列表表头数据
		 * @method _renderColumnsHeader
		 * @private
		 */
		_renderColumnsHeader: function List__renderColumnsHeader(){
			var i, n, column, query = this.query, columns = this._getSortedColumns();
			
			if( !columns ){
				return;
			}
			
			var me = this;
			var tr = $(this.options.cardsHeader),th, colContainer, sortedColumn, currentColumn;
			//清空表头内容
			tr.empty();
			//渲染
			var selectAllTR = $('<th>').addClass("checkbox").css("text-align", "left").css("padding", "0").appendTo( tr );
			$("<input>").attr("type", "checkbox").click(function(){
				if($(this).attr("checked")){
					$(".check-card").attr("checked", true);
				}else{
					$(".check-card").attr("checked", false);
				}
			}).appendTo(selectAllTR);
			
			// 绑定颜色列
			$('<th class="colorful"></th>')
				.attr("title",msg("card.cardType"))
				.appendTo( tr );
				
			// 绑定其他列
			for (i = 0, n = columns.length; i < n; i++) {
				column = columns[i];
				th = $("<th class='sortable'>").appendTo(tr);
				colContainer = $("<div></div>")
					.attr("id", column)
					.appendTo( th )
					.bind ("click" , function( event ){
						var $this = $(this);
						var currentColumn = $this.attr( "id" );
						me.query.clearSorts();
						if( $this.hasClass("desc")){
							me.query.addSort(currentColumn, "asc");
						}else{
							me.query.addSort(currentColumn, "desc");
						}
						History.go( me.query.serializeSorts());
					});
					
				//设置当前排序字段样式
				sortedColumn = query.getSort(column);
				if( sortedColumn ){
					colContainer.addClass( sortedColumn.order );
				}
				$("<a><span>" + me._getColumnName(column) + "</span></a>").appendTo(colContainer);
			}
			//操作列头
			$('<th class="opertions-label">' + msg("label.operation") + '</th>').appendTo(tr);
		},
		
		
		/**
		 * 根据提供的ID获取列名.
		 * 
		 * @method _getColumnName
		 * @param id {String | Number} 字段名
		 * @return {String} 如果提供的ID为自定义字段则返回自定义字段名，否则返回国际化后的系统字段名称
		 * @private
		 */
		_getColumnName: function List__getColumnNamme(id) {
			if (!id) {
				return "";
			}
			
			if (!isNaN(id) || id.substring(0, 5) === "prop_") {
				return this.metadata.cardProperties[id].name;
			}
			else {
				return msg(this.options.columnMessagePrefix + id);
			}
		},
		
		/**
		 * 生成自定义显示列下拉菜单按钮.
		 * 
		 * @method _generateSelectColumnsMenu
		 * @private
		 */
		_generateSelectColumnsMenu: function List__generateSelectColumnMenu() {
			var i, ii, id, items = [], 
				properties = this.metadata.cardProperties,
				columns = this.query.getColumns(),
				selectableSystemColumns = this.options.selectableSystemColumns;
				
			// 回调函数
			var callback = function(el, id, selected) {
				$window.trigger(selected ? "columnAdd" : "columnRemove", id);
			};
			
			// 自定义字段
			for (id in properties) {
				items.push({
					key: id,
					txt: properties[id].name, 
					props: id,
					link: callback,
					defaultSelected: !!columns[id]
				});
			}
			
			// 系统字段
			for (i = 0, ii = selectableSystemColumns.length; i < ii; i++) {
				id = selectableSystemColumns[i];
				items.push({
					key: id,
					txt: this._getColumnName(id), 
					props: id,
					link: callback,
					defaultSelected: !!columns[id]
				});
			}
			
			// Chrome 排序bug FIXME 这里等于没排序
			items.sort(function(l, r) {
				return ((l.sort || 0) - (r.sort || 0)) || ((l.id || 0) -  (r.id || 0));
			});
			
			return new Menu({
				target: $("#select-columns"),
				options: items,
				multi_select: true,
				css: "multi-selector"
			});
		},
		
		/**
		 * 批量修改按钮.
		 * @method _generateBatchUpdateButton
		 * @private
		 */
		_generateBatchUpdateButton: function List__generateBatchUpdateButton() {
			var me = this;
			$("#batch-update").click(function(){
				me.onBatchUpdate();
			})
		},

		/**
		 * 批量删除按钮.
		 * @method _generateBatchUpdateButton
		 * @private
		 */
		_generateBatchDeleteButton: function List__generateBatchDeleteButton() { 
		
			var  container = $("#batchDeleteDiaglogContainer"), 
				checkboxes = this.widgets.listContainer.find("input.check-card:checkbox:checked"), me = this;
			
			var msgDIV = $("<div>").text(msg("card.batchDelete.confirm")).appendTo(container);
			$("<span>").addClass("warning-message-icon").appendTo(msgDIV);
			var cascadeDIV = $("<div>").css("padding-top", "10px").appendTo(container)
			var checkbox = $("<input>").attr("type", "checkbox").appendTo(cascadeDIV);
			var checkSpan = $("<span>").text(msg("card.batchDelete.cascade")).appendTo(cascadeDIV);

			var _buttons = {};
			_buttons[msg("button.cancel")]=function(){
				$(this).dialog('close');
			};
			_buttons[msg("button.ok")]=function(){ 
				$(this).dialog('close');
				var ids = [];
				me.widgets.listContainer.find("input.check-card:checkbox:checked").each(function() {
					ids.push(me.cards[this.value].id);
				});
				new Spark.widgets.Load().showLoading(msg('card.batchDelete'));
				$.ajax({
					url : Spark.constants.CONTEXT + "/ajax/spaces/" + me.options.prefixCode + "/cards/batchDelete",
					type : "POST",
					data :  {"cascade" : checkbox.attr("checked"), "ids" :  ids.join(",") } ,
					success : function(data){
						location.reload(true);
					},
					error : function(x, s, e){
						var  errorMsg = x.responseText ? x.responseText : Spark.util.formatResponseStatus(x.status);
						Alert.alert(errorMsg, msg("card.batchDelete"));
					}
				})
			};
					
			container.dialog({
				height: 280,
				width: 400,
				modal: true, 
				bgiframe :true,
				autoOpen: false,
				buttons: _buttons,
				title: msg('card.batchDelete')
			})			
			$("#batch-delete").click(function(){ 
				if (!me.widgets.listContainer.find("input.check-card:checkbox:checked").length) {
					Alert.alert(msg("card.batchUpdate.noSelected"), msg("card.batchDelete"));
					return;
				}
				container.dialog("open");
			})
		},
		
		/**
		 * 更新自定义显示列下拉菜单按钮状态.
		 * @method _updateSelectColumnsMenu
		 * @private
		 */
		_updateSelectColumnsMenu: function List__updateSelectColumnsMenu() {
			var id, columns = this.query.getColumns(),
				menu = this.widgets.selectColumnsMenu;
			
			if (!menu) {
				return;
			}
			
			// 清空所有状态
			menu.activeAllOptions(false);
			
			// 将已选中项设为选中状态
			for (id in columns) {
				menu.activeOptionByKey(id, true);
			}
		},
		
		/**
		 * 当增加一个字段显示时触发此回调方法.
		 * 
		 * @method onColumnAdd
		 * @param e {Event} 事件对象
		 * @param id {Number} 字段ID
		 */
		onColumnAdd: function List_onColumnAdd(e, id) {
			var query = this.query;
			query.addColumn(id);
			History.go(query.serializeColumns(true));
		},
		
		/**
		 * 当删除一个字段显示时触发此回调方法.
		 * 
		 * @method onColumnRemove
		 * @param e {Event} 事件对象
		 * @param id {Number} 字段ID
		 */
		onColumnRemove: function List_onColumnRemove(e, id) {
			var query = this.query;
			query.removeColumn(id);
			History.go(query.serializeColumns(true));
		},
		
		/**
		 * 当URL中的column定义变化时触发此方法.
		 * 
		 * @method onColumnChange
		 * @param queryState {String} 查询对象URL中的状态
		 */
		onColumnChange: function List_onColumnChange(queryState) {
			this.query = new Spark.widgets.Query(queryState);
			this._renderColumns();
			// 重新构造列选择菜单
			this._updateSelectColumnsMenu();
		},
		
		/**
		 * 当卡片被分配给一个项目时触发此方法.
		 * 
		 * @method onAssignToProject
		 * @param e {Event} 事件对象
		 * @param project {Object} 项目对象
		 */
		onAssignToProject: function List_onAssignToProject(e, project) {
			var container = this.widgets.listContainer,
				checkboxes = container.find("input.check-card:checkbox:checked");
			
			if (!checkboxes.length) {
				Alert.alert(msg("plugin.icafe.assign-to-project.select-nothing"));
				return;
			}
			
			var sequences = [];
			checkboxes.each(function() {
				sequences.push(this.value);
			});
			
			var successMessage, failureMessage;
			
			// 如果不给出project或给出为空，则认为是要去掉这些卡片上的项目
			if (project) {
				successMessage = msg("plugin.icafe.assign-to-project.success", sequences.length, project.name);
				failureMessage = msg("plugin.icafe.assign-to-project.failure", sequences.length, project.name);
			} 
			else {
				project = { id: "none", name: msg("plugin.icafe.assign-to-project.none-project") };
				successMessage = msg("plugin.icafe.assign-to-project.break-relationship.success", sequences.length);
				failureMessage = msg("plugin.icafe.assign-to-project.break-relationship.failure", sequences.length);
			}
			
			// TODO 需要增加loading提示
			
			var me = this;
			$.ajax({
				url: Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/projects/" + project.id + "/cards",
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				data: $.toJSON(sequences),
				success: function(data, status) {
					Alert.alert(successMessage, "", function() {
						// 因为可能会与右侧filter相关，因此重新加载数据
						me.show();
					});
				},
				error: function(request, status) {
					Alert.alert(msg("global.exception.errorMsg"));
				}
			});
		},
		
		/**
		 * 卡片批量修改.
		 * 
		 * @method onBatchUpdate
		 * @param e {Event} 事件对象
		 * @param project {Object} 项目对象
		 */
		onBatchUpdate: function List_onBatchUpdate(e) { 
			var container = this.widgets.listContainer,
				checkboxes = container.find("input.check-card:checkbox:checked"), me = this;
			
			if (!checkboxes.length) {
				Alert.alert(msg("card.batchUpdate.noSelected"), msg("card.batchUpdate"));
				return;
			}
			
			var cards = [], cardIds = [];
			checkboxes.each(function() {
				cardIds.push(me.cards[this.value].id);
				cards.push(me.cards[this.value]);
			});
			
			var commonPropertyIds = this._generateCommonProperties(cards);
			if(!commonPropertyIds.length){
				Alert.alert(msg("card.batchUpdate.noCommonProperties"), msg("card.batchUpdate"));
				return;
			}else{
				location.href = Spark.constants.CONTEXT + "/spaces/" + this.options.prefixCode + "/cards/batchUpdate?ids=" + cardIds.join(",");
			}
		},
		
		_generateCommonProperties : function List__generateCommonProperties(cards){
			var typeIds = [];
			$.each(cards, function(i, card){
				if($.inArray(card.type.id, typeIds) == -1){
					typeIds.push(card.type.id);
				}
			})
			
			var commonPropertyIds = [];
			Spark.service.SpaceService.load(this.options.prefixCode, function(metadata){
				$.each(metadata.cardProperties, function(i, p){
					commonPropertyIds.push(p.id);
				});
				$.each(metadata.cardTypes, function(i, t){
					if($.inArray(t.id, typeIds) != -1){
						var specificProperties = [];
						$.each(t.cardProperties, function(i, p){
							specificProperties.push(p.id);
						});
						commonPropertyIds = $.grep(commonPropertyIds, function(n, i){
							return $.inArray(n, specificProperties)!= -1;
						})
					}
				})
			})
			return commonPropertyIds;
		},
		
		
		/**
		 * 当用户点击"导入项目"按钮时.
		 * 
		 * @method onImportProject
		 * @param e {Event} 事件对象
		 */
		onImportProject: function List_onImportProject(e) {
			$window.triggerHandler("openProjectPicker");
		},
		
		 /**
		  * 渲染卡片对应的操作列表
		  * @param card {Object} 当前卡片
		  * @method getCardActions
		  * @private
		  */
		_generateActionMenu: function List__generateActionMenu(container, card){
			var childtypes = this.metadata.cardTypes[card.type.localId].allChildren;
			var html = [], items = [], i, cardtype, menuid, space = this.metadata.space;
			html.push('<ul class="operations">');
			//有写权限或者是卡片创建者，显示编辑操作
			if( space.permissions.write == true || card.createdUser.id == Spark.constants.CURRENT_USER.id){
				html.push('<li><span class="edit-icon"></span><a class="icon-link" href="' + card.sequence + '/edit">' + msg("button.edit") + '</a></li>');
			}
			//有创建权限，显示新建操作
			if( space.permissions.create == true && childtypes.length > 0 ){
				menuid = 'create-child-menu-' + card.id;
				html.push('<li><span class="add-icon"></span><a class="icon-link" id="' + menuid +  '" href="javascript:void(0)">' + msg("button.new.childcard") + "</a></li>");
			}
			html.push('</ul>');
			container.html(html.join(""));
			
			$(" ul.operations > li").addClass("operation");
			$(" li.operation > li > a").addClass("icon-link");
			
			for ( i in childtypes){
				cardtype = childtypes[i];
				items.push({
					txt: cardtype.name,
					props: cardtype.id,
					link: "new?cardTypeId=" + cardtype.id + "&parentId=" + card.id
				});
			}
			new Menu({
				target: $("#" + menuid),
				options: items
			});
			
		},
		
		/**
		 * 生成"导出到Excel"的按钮.
		 * @method _generateExportToExcelButton
		 * @private
		 * @author 大蹲子 2010-10-19
		 */
		_generateExportToExcelButton : function List__generateExportToExcelButton(){
			var me = this;
			var button = $("#export-to-excel").show();
			
			button.bind("click", function(){
				var sortedColumns;
				//获取字段信息
				if ( me.options.defaultVisibleColumns ){
					sortedColumns = me._getSortedColumns();
				}else{
					sortedColumns = me.options.defaultVisibleColumns.concat( me._getSortedColumns() );
				}
				
				if (!me.exportOption.initFinish){
					var _buttonsUpload = {};
					_buttonsUpload[msg("button.cancel")]=function(){
						$(this).dialog('close');
					};
					_buttonsUpload[msg("button.ok")]=function(){
						var hash = History.getHash(null, true);
						var columnType = Spark.util.getRadioValueByName("columnType");
						//$("[name='columnType']").val();
						var dataType = Spark.util.getRadioValueByName("dataType");
						//$("[name='dataType']").val();
						var url = Spark.constants.CONTEXT + "/spaces/" + me.options.prefixCode + "/emport/exportToExcel?" + hash;
						window.location.href = url + "&columnType=" + columnType + "&dataType=" + dataType;
						$("#exportToExcelParam").dialog("close");
					};
					$("#exportToExcelParam").dialog({
						height: 280,
						width: 400,
						modal: true, 
						bgiframe :true,
						autoOpen: false,
						buttons: _buttonsUpload,
						title: msg('button.view.export-to-excel')
					});
					$( "#columnTypeRadio" ).buttonset();
					$( "#dataTypeRadio" ).buttonset();
					me.exportOption.initFinish = true;
				}
				
				$("#exportToExcelParam").dialog("open");
			});
		},
		
		/**
		 * 生成"发布到iCafe项目"的菜单.
		 * @method _generateAssignToProjectMenu
		 * @private
		 */
		_generateAssignToProjectMenu: function List__generateAssignToProjectMenu() {
			var project, projects = this.metadata.space.projects, items = [], i, n;
			
			// 如果空间不包含任何项目则不显示选项
			if (!projects || !projects.length) {
				return;
			}
			
			var button = $("#assign-to-project").show();
			
			var me = this;
			
			// 加入空值选项
			items.push({
				txt: "[" + msg("plugin.icafe.assign-to-project.empty") + "]",
				link: function(e) {
					me.onAssignToProject(e, null);
				}
			});
			
			// 逐个加入所有项目
			for (i = 0, n = projects.length; i < n; i++) {
				project = projects[i];
				items.push({
					txt: project.name,
					props: project,
					link: function(e, project) {
						me.onAssignToProject(e, project);
					}
				});
			}
			
			// 加入"添加新项目"选项
			items.push({
				txt: "[" + msg("plugin.icafe.assign-to-project.import") + "]",
				link: function(e) {
					me.onImportProject(e);
				}
			});
			
			return new Menu({
				target: button,
				options: items
			});
		},
		
		/**
		 * 渲染页面的分页组件
		 * @param pagination {Object} 卡片分页对象
		 * @param container {HTMLElement} HTML容器元素
		 * @private
		 */
		_renderPagination: function List__renderPagination(pagination, container){
			var cards = pagination.results;
			
			// 初次执行绑定元素，否则清空元素
			var widgets = this.widgets;
			if (!widgets.paginationContainer) {
				widgets.paginationContainer = $('<div class="pagination"></div>').appendTo(container);
			} else {
				widgets.paginationContainer.empty();
			}
			
			// 如果没有卡片则停止渲染
			if( !cards || !cards.length){
				return;
			}
			
			var ulContainer, i, n, total = pagination.totalPages, current = pagination.page,
				_renderAnchor = this._renderPageAnchor;
			
			// 渲染分页信息
			ulContainer = $('<ul>').appendTo(widgets.paginationContainer);
			
			_renderAnchor('page-first', msg("pagination.first") , 1, ulContainer);
			_renderAnchor('page-prev', msg("pagination.prev"), (current > 1 ? current - 1 : 1), ulContainer);
			if( total <= 10 ){
				for( i = 1, n = total; i <= n; i++ ){
					_renderAnchor('page ' + (i == current ? 'current' : ''), i, i, ulContainer);
				}
			}else{
				//超过10页的页面显示方式:开始两页...左三页|当前页|右三页...最后两页
				var left, right;
				left = current - 3 > 0 ? current - 3 : 1;
				right = current + 3 > total ? total : current + 3;
				//开始两页
			    if ( left > 1 ){
			    	for ( i = 1, n = ( left > 3 ? 3 : left); i < n; i++){
				    	_renderAnchor('page ' + (i == current ? 'current' : ''), i, i, ulContainer);
			    	}
			    	if( left > 3){
				    	$('<li class="">...</li>').appendTo(ulContainer);
			    	}
			    }
				for( i= left ; i <= right ; i++ ){
					_renderAnchor('page ' + (i == current ? 'current' : ''), i, i, ulContainer);
				}
				//最后两页
				if ( right < total ){
					if ( right <=  total -3){
				    	$('<li class="">...</li>').appendTo(ulContainer);
					}
					for ( i = (right > total -1 ? right : total -1), n = total; i <= n; i++){
				    	_renderAnchor('page ' + (i == current ? 'current' : ''), i, i, ulContainer);
			    	}
		    	}
			}
			_renderAnchor('page-next', msg("pagination.next"), current < total ? current + 1 : total, ulContainer);
			_renderAnchor('page-last', msg("pagination.last"), total, ulContainer);
			
			// 渲染分页其他信息
			var html = [], from, to, 
				size = pagination.size,
				count = pagination.total;
			if( count > 0 ){
				from = ( current - 1) * size + 1;
				to = current * size > count ? count: current * size ;
				html.push( msg("pagination.list.show") + from + "-" + to + ", ");
			}
			html.push( msg("pagination.total") + count);
			$('<li class="result-count">').text(html.join("")).appendTo(ulContainer);
		},
		
		/**
		 * 渲染页面分页链接
		 * @param clazz 分页标签的样式class
		 * @param text 分页标签的显示内容
		 * @param page 分页标签代表的跳转页码
		 * @param container HTMLElment 标签
		 * @method _renderPageAnchor
		 * @private
		 * <p>
		 * <li class="clazz">
		 * 	<a href="javascript:void(0)">text</a>
		 * </li>
		 * </p>
		 */
		_renderPageAnchor: function List__renderPageAnchor(clazz ,text, page, container){
			$('<li>').addClass( clazz ).html(
						$('<a href="javascript:void(0)"></a>')
								.text(text)
								.bind("click", function (event) {
									History.go("page",page);
			})).appendTo(container);
		},
		
		/**
		 * 获取排序过的字段列表.
		 * @return 排序过的字段列表
		 * @method _getSortedColumns
		 * @private
		 */
		_getSortedColumns: function List__getSortedColumns() {
			var query = this.query;
			if (!query || !query.getColumns()) {
				return [];
			}
			
			var columns = makeArray(query.getColumns(), function(l, r) {
				return l.value.index - r.value.index;
			});
			
			var i, n, results = [];
			for (i = 0, n = columns.length; i < n; i++) {
				results.push(columns[i].key);
			}
			return results;
		}
		
	};
})();