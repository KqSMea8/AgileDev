/**
 * 层级视图组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.Hierarchy
 * @author GuoLin
 */
(function() {
	
	// 定义别名，提高性能
	var $window = $(window);
	var Q = Spark.widgets.Query;
	var M = Spark.widgets.Menu;
	var H = Spark.util.History;
	var Alert = Spark.widgets.Alert;
	var makeArray = Spark.util.makeArray;
	var sizeof = Spark.util.sizeof;
	var msg = Spark.util.message;
	var encodeHTML = Spark.util.encodeHTML;
	var spaceService = Spark.service.SpaceService;
	var cardService = Spark.service.CardService;
	
	var OPEN_STATUS_KEY = "open";
	
	//读取url或cookie中保存的展开节点,并进行展开操作.
    var openCardSequences = Spark.util.History.getHash(OPEN_STATUS_KEY);
    if (!openCardSequences){
        openCardSequences = Spark.util.Cookie.get(OPEN_STATUS_KEY);
    }
	openCardSequences = openCardSequences ? openCardSequences.split(",") : [];
	
	/**
	 * 层级视图组件构造器.
	 * 
	 * @constructor
	 */
	Spark.pages.Hierarchy = function(options) {
		if (!options || !options.prefixCode) {
			throw new Error("Argument of Hierarchy constructor must not be null.");
		}
		this.options = $.extend({}, this.options, options);
		this.query = new Q(H.getHash());
		this.cards = [];
		this.widgets = {};
		
		var me = this;

		// 加载初始化数据
		spaceService.load(this.options.prefixCode, function(metadata) {
			me.metadata = metadata;
			
			// 如果与icafe关联则允许自定义显示project列
			var projects = metadata.space.projects;
			if (projects && projects.length) {
				me.options.selectableSystemColumns.push("project");
			}
			
			// 显示根卡片们
			me.showRoot();
			
			// 生成"分配到项目"菜单
			me._generateAssignToProjectMenu();
			
			// 生成自定义显示列的下拉菜单按钮
			me.widgets.selectColumnsMenu = me._generateSelectColumnsMenu();
			
			// 生成"批量修改"按钮
			me._generateBatchUpdateButton();
			
			// 生成"批量删除"按钮
			me._generateBatchDeleteButton();
		});
		
		// 绑定事件
		$window.bind("columnAdd", function(e, propertyId) {
			me.onColumnAdd(e, propertyId);
		})
		$window.bind("columnRemove", function(e, propertyId) {
			me.onColumnRemove(e, propertyId);
		})
		
		// 绑定URL hash变更监控事件
		H.bind(function(module, newState, oldState) {
			Spark.util.TabUtils.setLastView(me.options.prefixCode ,"/hierarchy#" + Spark.util.History.getHash());
		},false);
		
		
		H.bind("c", function() {
			me.onColumnChange(H.getHash());
		}, false);
		
		H.bind("q", function() {
			me.onQueryChange(H.getHash());
		}, false);
			
		return this;
	};
	
	Spark.pages.Hierarchy.prototype = {
		
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
			 * 层级视图根元素.
			 * 
			 * @property hierarchy
			 * @type String | HTMLElement
			 * @default "#hierarchy"
			 */
			hierarchy: "#hierarchy",
			
			/**
			 * 层级视图表头元素.
			 * 
			 * @property hierarchyHeader
			 * @type String | HTMLElement
			 * @default "#hierarchy-header"
			 */
			hierarchyHeader: "#hierarchy-header",
			
			/**
			 * 层级视图信息元素
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
			 * 标题列最小宽度.
			 * 
			 * @property titleMinWidth
			 * @type Number
			 * @default 300
			 */
			titleMinWidth: 300,
			
			/**
			 * 其他列最大宽度.
			 * XXX 可以考虑为不同类型的字段提供不同的默认值
			 * 
			 * @property columnMaxWidth
			 * @type Number
			 * @default 150
			 */
			columnMaxWidth: 150,
			
			/**
			 * 默认排序列.
			 * 
			 * @property defaultSort
			 * @type Object
			 */
			defaultSort: {

				/**
				 * 排序字段名称.
				 * 
				 * @property column
				 * @type String
				 * @default "sequence"
				 */
				column: "sequence",
				
				/**
				 * 是否正向排序.
				 * 
				 * @property asc
				 * @type Boolean
				 * @default false
				 */
				asc: false
				
			}
			
		},
		
		/**
		 * 空间元数据.
		 * 
		 * @property metadata
		 * @type Object
		 */
		metadata: null,
		
		/**
		 * 当前页面显示的所有卡片列表.
		 * <p>
		 * 列表的每个元素包括卡片和DOM元素两部分：
		 * { card: [卡片数据], el: [DOM元素] }
		 * </p>
		 * 
		 * @property cards
		 * @type Array
		 */
		cards: null,
		
		/**
		 * 卡片列表对象, 以卡片id为键, 卡片json对象为值.
		 * 
		 * @property cardMap
		 * @type Object 
		 */
		cardMap : {},
		
		/**
		 * 查询对象.
		 * 
		 * @property query
		 * @type Spark.widgets.Query
		 */
		query: null,
		
		/**
		 * 当前类相关物件列表.
		 * 
		 * @property widgets
		 * @type Object
		 */
		widgets: null,
		
		/**
		 * 渲染根卡片列表.
		 * 
		 * @method showRoot
		 */
		showRoot: function Hierarchy_showRoot() {
			Spark.util.Load.add('loading-area');
			var url = Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/cards/root?" + this.query.encodedSerialize();
			var me = this;
			
			// 清空当前卡片
			this.cards = [];
			
			$.ajax({
				url: url,
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				cache: false,
				success: function(cards) {
					Spark.util.Load.remove('loading-area');
					if( !cards || !cards.length){
						$(me.options.hierarchy).hide();
						$(me.options.messagebar).find(".info-message").text(msg("cards.emptyResult")).show();
						$(me.options.messagebar).show();
						return;
					}else{
						$(me.options.hierarchy).show();
						$(me.options.messagebar).hide();
						cards = cardService.assemble(cards);
						me._render(cards, $(me.options.hierarchy));
						$.each(cards, function(i, d){
							me.cardMap[d.sequence] = d;
						})
					}
				},
				error: function(xhr, status){
					Spark.util.Load.remove('loading-area');
					if(xhr.status == 403){
						Spark.util.handleAjaxError(xhr);
					}else{
						Alert.alert(msg("card.list.pagination.failure"));
					}
				}
			});
			
		},
		
		/**
		 * 根据指定的父卡片sequence显示子卡片列表.
		 * 
		 * @method showChildren
		 * @param parentSequence {Number} 父卡片序列号
		 */
		showChildren: function Hierarchy_showChildren(parentCard) {
			var url = Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/cards/" + parentCard.sequence + "/children?" + this.query.encodedSerialize();
			var me = this;
			$.ajax({
				url: url,
				type: "GET",
				contentType: "application/json",
				dataType: "json",
				cache: false,
				success: function(cards) {
					cards = cardService.assemble(cards);
					var container = $("#card-" + parentCard.sequence);
					Spark.util.Load.remove('card-load-' + parentCard.sequence);
					container.find('.simple').show();
					me._render(cards, container);
					parentCard.children = cards;
					parentCard.childrenLoaded = true;
					$.each(cards, function(i, d){
						me.cardMap[d.sequence] = d;
					})
				},
				error: function(xhr, status){
					if(xhr.status == 403){
						Spark.util.handleAjaxError(xhr);
					}else{
						Alert.alert(msg("card.list.pagination.failure"));
					}
				}
			});
			$("#card-" + parentCard.sequence).find('.simple').hide();
			Spark.util.Load.add('card-load-' + parentCard.sequence);
		},
		
		/**
		 * 渲染到页面.
		 * 
		 * @method _render
		 * @param cards {Array} 卡片列表
		 * @param container {HTMLElement} HTML容器元素
		 * @private
		 */
		_render: function Hierarchy__render(cards, container) {
			var card, i, item, html, me=this, expandLink;
			
			// 排序
			cards = this._sort(cards);
			
			// 清空
			container.find('ul.children').remove();
			
			var listContainer = $('<ul class="children">').appendTo(container);
			for (i = 0; i < cards.length; i++) {
				html = [];
				card = cards[i];
				item = $('<li id="card-' + card.sequence + '">').appendTo(listContainer);
				html.push('<div class="item" style="background-color:#' + card.type.color + '">')
				// selectable
				html.push('<div class="selectable"><input type="checkbox" class="check-card" value="' + card.sequence + '" /></div>');
				// item-head
				html.push('<div class="item-head"><h4>');
				html.push('<a href="' + card.sequence + '">' + '#' + card.sequence + '</a>');
				//附件标识
				if ( card.validAttachments && card.validAttachments.length  > 0){
					html.push('<span style="display:block;" class="attachment-icon"></span>');
				}
				html.push('</h4></div>');
				// item-body
				html.push('<div class="item-body"><ul>');
				html.push('<li class="title"><div><a href="' + card.sequence + '">' + encodeHTML(card.title) + '</a></div></li>');
				html.push('</ul></div>');
				
				html.push('</div>');
				item.html(html.join(""));
				
				// 将卡片和标签关系入库
				this.cards.push({ card: card, el: item });
	
				// 如果存在子节点
				if (card.childrenSize > 0) {
					expandLink = $('<a class="expand"><span class="blank">' + msg("card.label.children.count", card.childrenSize) + '</span></a>')
						.appendTo(item.find(".item-head"))
						.bind("click", { card: card }, function(event) {
							var $this = $(this);
							if ($this.hasClass("expand")) {
								me.expand(event.data.card);
								$this.removeClass("expand").addClass("collapse");
							} else {
								me.collapse(event.data.card);
								$this.removeClass("collapse").addClass("expand");
							}
						});
					$('<span id="card-load-' + card.sequence + '"></span>').appendTo(item.find(".item-head"));
				}
				
				// 渲染actions
				this._renderActions(item.find(".item-body"), card);
				
				//需要默认打开的节点,执行打开操作.
				if (card.childrenSize > 0 && Spark.has(openCardSequences, card.sequence)){
					expandLink.trigger("click");
				}	
			}
			
			this._renderColumns();
		},
		
		/**
		 * 渲染卡片对应的操作列表.
		 * 
		 * @method _renderActions
		 * @param container {HTMLElement} DOM元素容器
		 * @param card {Object} 待生成动作的卡片
		 * @private
		 */
		_renderActions: function Hierarchy__renderActions(container, card){
			var types = this.metadata.cardTypes[card.type.localId].allChildren,
				space = this.metadata.space,
				html = [], items = [], i, n, type, menuId;
			
			// 渲染HTML
			html.push('<div class="operations">');
			//有写权限或者是卡片创建者，显示编辑操作
			if( space.permissions.write || card.createdUser.id == Spark.constants.CURRENT_USER.id){
				html.push('<div class="operation"><span class="edit-icon"></span><a class="icon-link" href="' + card.sequence + '/edit">' + msg("button.edit") + '</a></div>');
			}
			//有创建权限，显示新建操作
			if (types.length &&space.permissions.create) {
				menuId = 'create-child-menu-' + card.id;
				html.push('<div class="operation"><span class="add-icon"></span><a class="icon-link" id="' + menuId +  '" href="javascript:void(0)">' + msg("button.new.childcard") + "</a></div>");
			}
			html.push('</div>');
			container.before(html.join(""));
			
			// 绑定菜单
			for (i = 0, n = types.length; i < n; i++) {
				type = types[i];
				items.push({
					txt: type.name,
					props: type.id,
					link: "new?cardTypeId=" + type.id + "&parentId=" + card.id
				});
			}
			
			new M({
				target: $("#" + menuId),
				options: items
			});
		},
		
		/**
		 * 渲染列.
		 * 
		 * @method _renderColumns
		 * @private
		 */
		_renderColumns: function Hierarchy__renderColumns() {
			var cards = this.cards, cols = this._getSortedColumns();
			
			if (!cols || !cards || !cards.length) {
				return;
			}
			
			var i, j, values, value, card, elCols, width;
			
			// 处理每个卡片
			for (i = 0; i < cards.length; i++) {
				card = cards[i];
				
				// 获取计算好的宽度对象
				width = this._caculateColumnsWidth(card);

				elCols = card.el.find(".item-body ul");
				
				// 设定title列宽
				elCols.find("li.title").width(width.title + "%");
				
				// 清除所有的自定义列(除了title)
				elCols.find("li.column").remove();
				
				// 渲染
				values = card.card.values;
				for (j = 0; j < cols.length; j++) {
					value = values[cols[j]];
					$('<li class="column"><div>' + (value && value.displayValue ? encodeHTML(value.displayValue) : "&nbsp;") + '</div></li>')
							.width(width.column + "%")
							.appendTo(elCols);
				}
			}
			
			// 渲染列头
			this._renderColumnsHeader();
		},
		
		/**
		 * 渲染列头.
		 * 
		 * @method _renderColumnsHeader
		 * @private
		 */
		_renderColumnsHeader: function Hierarchy__renderColumnsHeader() {
			var cols = this._getSortedColumns();
			
			if (!cols) {
				return;
			}
			
			var i, n, width = this._caculateColumnsWidth(),
				elHeader = $(this.options.hierarchyHeader);
			
			// 设置title列的宽度
			elHeader.find("li.title").width(width.title + "%");
			elHeader.find("li.title").text(msg(this.options.columnMessagePrefix + "title"));
			
			// 移除所有其他列
			elHeader.find("li.column").remove();
			
			// 渲染
			for (i = 0, n = cols.length; i < n; i++) {
				$('<li class="column">' + this._getColumnName(cols[i]) + '</li>').width(width.column + "%").appendTo(elHeader);
			}
			
			$("#selectAllCheckbox").click(function(){
				if($(this).attr("checked")){
					$(".check-card").attr("checked", true);
				}else{
					$(".check-card").attr("checked", false);
				}
			})
		},
		
		/**
		 * 展开节点.
		 * 
		 * @method expand
		 * @param card {Object} 待展开的卡片
		 */
		expand: function Hierarchy_expand(card) {
			if (!card.childrenLoaded) {
				this.showChildren(card);
			} else {
				$("#card-" + card.sequence + " > .children").show();
			}
			this._markOpenStatus(card.sequence, true);
		},
		
		/**
		 * 收起节点.
		 * 
		 * @method collapse
		 * @param card {Object} 待收起的卡片
		 */
		collapse: function Hierarchy_collapse(card) {
			$("#card-" + card.sequence + " > .children").hide();
			this._markOpenStatus(card.sequence, false);
		},
		
		/**
		 * 当增加一个字段显示时触发此回调方法.
		 * 
		 * @method onColumnAdd
		 * @param e {Event} 事件对象
		 * @param id {Number} 字段ID
		 */
		onColumnAdd: function Hierarchy_onColumnAdd(e, id) {
			var query = this.query;
			query.addColumn(id);
			H.go(query.serializeColumns(true));
		},
		
		/**
		 * 当删除一个字段显示时触发此回调方法.
		 * 
		 * @method onColumnRemove
		 * @param e {Event} 事件对象
		 * @param id {Number} 字段ID
		 */
		onColumnRemove: function Hierarchy_onColumnRemove(e, id) {
			var query = this.query;
			query.removeColumn(id);
			H.go(query.serializeColumns(true));
		},
		
		/**
		 * 当URL中的column定义变化时触发此方法.
		 * 
		 * @method onColumnChange
		 * @param queryState {String} 查询对象URL中的状态
		 */
		onColumnChange: function Hierarchy_onColumnChange(queryState) {
			this.query = new Q(queryState);
			this._renderColumns();
			
			// 重新构造列选择菜单
			this._updateSelectColumnsMenu();
		},
		
		/**
		 * 当URL中的query变化时触发此方法
		 * 
		 * @method onQueryChange
		 * @param queryState {String} 查询对象URL中的状态
		 */
		onQueryChange: function Hierarchy_onQueryChange(queryState){
			this.query = new Q(queryState);
			this.showRoot();
		},
		
		/**
		 * 当卡片被分配给一个项目时触发此方法.
		 * 
		 * @method onAssignToProject
		 * @param e {Event} 事件对象
		 * @param project {Object} 项目对象
		 */
		onAssignToProject: function Hierarchy_onAssignToProject(e, project) {
			var container = $(this.options.hierarchy),
				checkboxes = container.find("input.check-card:checkbox:checked");
			
			if (checkboxes.length == 0) {
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
						me.showRoot();
					});
				},
				error: function(request, status) {
					Alert.alert(failureMessage);
				}
			});
		},
		
		/**
		 * 当用户点击"导入项目"按钮时.
		 * 
		 * @method onImportProject
		 * @param e {Event} 事件对象
		 */
		onImportProject: function Hierarchy_onImportProject(e) {
			$window.triggerHandler("openProjectPicker");
		},
		
		/**
		 * 生成自定义显示列下拉菜单按钮.
		 * 
		 * @method _generateSelectColumnsMenu
		 * @return {Spark.widgets.Menu} 生成好的菜单
		 * @private
		 */
		_generateSelectColumnsMenu: function Hierarchy__generateSelectColumnMenu() {
			var i, ii, id, items = [], properties = this.metadata.cardProperties,
				columns = this.query.getColumns(),
				selectableSystemColumns = this.options.selectableSystemColumns;
				
			var me = this;
			
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
					txt: me._getColumnName(id), 
					props: id,
					link: callback,
					defaultSelected: !!columns[id]
				});
			}
			
			// Chrome 排序bug FIXME 这里等于没排序
			items.sort(function(l, r) {
				return ((l.sort || 0) - (r.sort || 0)) || ((l.id || 0) -  (r.id || 0));
			});
			
			return new M({
				target: $("#select-columns"),
				options: items,
				multi_select: true,
				css: "multi-selector"
			});
		},
		
		/**
		 * 更新自定义显示列下拉菜单按钮状态.
		 * 
		 * @method _updateSelectColumnsMenu
		 * @private
		 */
		_updateSelectColumnsMenu: function Hierarchy__updateSelectColumnsMenu() {
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
		 * 生成"发布到iCafe项目"的菜单.
		 * 
		 * @method _generateAssignToProjectMenu
		 * @return {Spark.widgets.Menu} 生成好的菜单
		 * @private
		 */
		_generateAssignToProjectMenu: function Hierarchy__generateAssignToProjectMenu() {
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

			return new M({
				target: button,
				options: items
			});
		},
		
		/**
		 * 批量修改按钮.
		 * @method _generateBatchUpdateButton
		 * @private
		 */
		_generateBatchUpdateButton: function Hierarchy__generateBatchUpdateButton() {
			var me = this;
			$("#batch-update").click(function(){
				me.onBatchUpdate();
			})
		},
		
		/**
		 * 更新自定义显示列下拉菜单按钮状态.
		 * @method _updateSelectColumnsMenu
		 * @private
		 */
		_updateSelectColumnsMenu: function Hierarchy__updateSelectColumnsMenu() {
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
		 * 卡片批量修改.
		 * 
		 * @method onBatchUpdate
		 * @param e {Event} 事件对象
		 * @param project {Object} 项目对象
		 */
		onBatchUpdate: function Hierarchy_onBatchUpdate(e) { 
			container = $(this.options.hierarchy),
				checkboxes = container.find("input.check-card:checkbox:checked"), 
				me = this;
			
			if (!checkboxes.length) {
				Alert.alert(msg("card.batchUpdate.noSelected"), msg("card.batchUpdate"));
				return;
			}
			
			var cards = [], cardIds = [];
			checkboxes.each(function() { 
				cardIds.push(me.cardMap[this.value].id);
				cards.push(me.cardMap[this.value]);
			});
			
			var commonPropertyIds = this._generateCommonProperties(cards);
			if(!commonPropertyIds.length){
				Alert.alert(msg("card.batchUpdate.noCommonProperties"), msg("card.batchUpdate"));
				return;
			}else{
				location.href = Spark.constants.CONTEXT + "/spaces/" + this.options.prefixCode + "/cards/batchUpdate?ids=" + cardIds.join(",");
			}
		},
		
		_generateCommonProperties : function Hierarchy__generateCommonProperties(cards){ 
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
		 * 批量删除按钮.
		 * @method _generateBatchUpdateButton
		 * @private
		 */
		_generateBatchDeleteButton: function Hierarchy__generateBatchDeleteButton() { 
		
			var  container = $("#batchDeleteDiaglogContainer"),
				cardContainer = $(this.options.hierarchy),
				checkboxes = cardContainer.find("input.check-card:checkbox:checked"),
				me = this;
			
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
				cardContainer.find("input.check-card:checkbox:checked").each(function() {
					ids.push(me.cardMap[this.value].id);
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
						var errorMsg = x.responseText ? x.responseText : Spark.util.formatResponseStatus(x.status);
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
				if (!cardContainer.find("input.check-card:checkbox:checked").length) {
					Alert.alert(msg("card.batchUpdate.noSelected"), msg("card.batchDelete"));
					return;
				}
				container.dialog("open");
			})
		},
		
		/**
		 * 根据提供的ID获取列名.
		 * 
		 * @method _getColumnName
		 * @param id {String | Number} 字段名
		 * @return {String} 如果提供的ID为自定义字段则返回自定义字段名，否则返回国际化后的系统字段名称
		 * @private
		 */
		_getColumnName: function Hierarchy__getColumnNamme(id) {
			if (!id) {
				return "";
			}
			
			if (!isNaN(id) || id.substring(0, 5) === "prop_") {
				try {
				return this.metadata.cardProperties[id].name;
				} catch (e) { alert(id); }
			}
			else {
				return msg(this.options.columnMessagePrefix + id);
			}
		},
		
		/**
		 * 计算title列和自定义字段列的显示宽度.
		 * 
		 * @method _caculateColumnsWidth
		 * @param currentCard {Object} 可选值，当前卡片对象，若不填写则取根卡片，必须包含el属性
		 * @return {Object} 返回对象包括，total(根卡片总宽度, 单位px)，title(title列宽度, 单位%)和column(其他列的宽度, 单位%)
		 */
		_caculateColumnsWidth: function Hierarchy__caculateColumnsWidth(currentCard) {
			var cards = this.cards, cols, colsize = sizeof(this.query.getColumns());
			if (!cards || !cards.length || !colsize) {
				return { title: 100, column: 0 };
			}
			
			// cards[0]意为第一个卡片总是根卡片
			var rootCard = cards[0], cols, total, title, col, colWidth;
			
			// 如果没有提供当前卡片，则取根卡片
			currentCard = currentCard || rootCard;
			
			var currentWidth = currentCard.el.find(".item-body ul").width();
			var rootWidth = rootCard.el.find(".item-body ul").width();
			
			// 根据根卡片的元素宽度计算所有自定义列应有的宽度px
			cols = rootWidth - this.options.titleMinWidth;
			
			// 计算自定义字段列的宽度，如果所有列宽度和过大则压缩全部列的宽度，1px为边框宽度
			colWidth = cols / colsize - 1;
			col = colWidth / currentWidth * 100;
			
			// 计算title的宽度
			title = (currentWidth - ((colWidth + 1) * colsize)) / currentWidth * 100;
			
			return { 
				title: title, 
				column: col
			};
		},
		
		/**
		 * 获取排序过的字段列表.
		 * 
		 * @return 排序过的字段列表
		 * @method _getSortedColumns
		 * @private
		 */
		_getSortedColumns: function Hierarchy__getSortedColumns() {
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
		},
		
		/**
		 * 对卡片进行排序.
		 * XXX 暂时仅支持默认排序
		 * 
		 * @param cards {Array} 卡片列表
		 * @return 排序完成的卡片
		 * @method _sort
		 * @private
		 */
		_sort: function Hierarchy__sort(cards) {
			var defaultSort = this.options.defaultSort, results,
				column = defaultSort.column, asc = defaultSort.asc;
			
			results = cards.sort(function(l, r) {
				var lv = l[column], rv = r[column];
				if (!lv && !rv) {
					return 0;
				} else if (lv && !rv) {
					return 1;
				} else if (!lv && rv) {
					return -1;
				} else if (lv == rv) {
					return 0;
				} else {
					return lv > rv ? 1 : -1;
				}
			});
			
			return asc ? results : results.reverse();
		},
		
		/**
		 * 获取所有打开状态为"true"的节点的数组
		 * 
		 * @return 卡片数组
		 */
		getOpenCards : function Hierarchy_getOpenCards(){
			var i, cards = this.cards, card, openCards = [];
			for (i=0; i<cards.length; i++){
				card = cards[i];
				if (card.card.openStatus){
					openCards.push(card);
				}
			}
			return openCards;
		},
		
		/**
		 * 在指定sequence的卡片上记录其"是否展开"的状态
		 * 
		 * @param sequence 需要改变状态的卡片的sequence
		 * @param status 需要改变为的状态值
		 */
		_markOpenStatus : function Hierarchy__markOpenStatus(sequence, status){
			var i, cards = this.cards, card;
			for (i=0; i<cards.length; i++){
				card = cards[i];
				if (card.card.sequence == sequence){
					card.card.openStatus = status;
				}
			}
			this._saveOpenStatus();
		},
		
		/**
		 * 计算当前层级视图的打开状态
         * 1.保存到cookie中
         * 2.添加到url中
		 * @return
		 */
		_saveOpenStatus : function Hierarchy__saveOpenStatus(){
			var i, card, openCards = this.getOpenCards(), cardSequences = [];
			for (i=0; i<openCards.length; i++){
				card = openCards[i];
				cardSequences.push(card.card.sequence);
			}
            cardSequences = cardSequences.join(",");
			Spark.util.Cookie.set(OPEN_STATUS_KEY, cardSequences);
            Spark.util.History.go(OPEN_STATUS_KEY, cardSequences);
		}
	};
})();