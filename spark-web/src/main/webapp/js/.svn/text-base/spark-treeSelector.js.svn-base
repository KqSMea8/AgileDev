/**
 * 弹出窗口的树形选择器组件.
 * 
 * @namespace Spark.widgets
 * @class Spark.widgets.CardTreeSelector
 * @author shixiaolei
 */

(function(){
	/**
	 * constructure
	 * 树形选择器的父类
	 * @param options
	 */
	Spark.widgets.CardTreeSelector = function (options){
		if (!options.rootUrl) {
	 		throw new Error("Argument rootUrl of CardTreeSelector constructor must not be null.");
		}
		this.options = $.extend({},this.options, options);
		this._init();
		return this;
	};
	
	
	Spark.widgets.CardTreeSelector.prototype = {
			
		/**
		 * 选中的NODE的JQUERY对象
		 */
		selectedNode: null,
		
		/**
		 * 当前的对话框
		 */
		currentDialog: null,
		
		
		/**
		 * 选项列表.
		 * 
		 * @property options
		 * @type Object
		 */
		options :{
		
			/**
			 * 弹出对话框容器的DOM ID, 代码将在此容器内自动拼接弹出选择器的HTML代码.
			 * 
			 * @property containerDiv
			 * @type String
			 */
			containerDiv: "",
			
			/**
			 * 对话框标题
			 *@property dialogTitle
			 * @type String
			 * 
			 */
			dialogTitle: "",
			
			
			/**
			 * 根节点Ajax请求URL.
			 * 
			 * @property rootUrl
			 * @type String
			 */
			rootUrl : null,
			
			
			/**
			 * 打开中间节点前的预操作发送另一个请求，以获取层数据的AJAX地址.
			 * <p>
			 * 可以为函数或字符串，
			 * 如果是一个字符串，则中间节点直接按此字符串指定的AJAX地址请求中间节点的数据；
			 * 如果是一个函数，则是一个回调函数，
			 * 它有一个参数为NODE，即当前要展开的DOM节点，
			 * 用户一般会根据NODE中的数据属性，在函数里动态生成该节点的子节点AJAX请求地址，并返回该地址的字符串。
			 * </p>
			 * @property nodeUrl
			 * @type Function or String
			 */
			nodeUrl: null,
			
			
			/**
			 * 得到Json数据后进行处理的回调函数，往往是根据json数据，为DOM节点生成属性、标题等,
			 * 该回调函数有一个参数为data，即json的一条数据.
			 * 
			 * @property ondata
			 * @type function
			 */
			ondata : null,
			
			/**
			 * 判断一条Json数据是否有子节点的回调函数,
			 * 该回调函数有一个参数为data，即json的一条数据,返回true或false. 
			 * 
			 * @property hasChildren
			 * @type function
			 */
			hasChildren : function(data){return true},
			
			
			/**
			 * 判断一条Json数据是否有效,
			 * 该回调函数有一个参数为data，即json的一条数据,返回true或false. 
			 * 与 isUnabledNodeVisiable参数配合使用
			 * 
			 * @property isEnable
			 * @type function
			 */
			isEnable: function(data){return true},
			
			/**
			 * 当一个节点为无效节点时，是否显示.
			 * 如果是true,则将该节点显示成灰色，点击时什么也不触发;
			 * 如果是false,则不显示该节点.
			 * 
			 * @property isUnabledNodeVisiable
			 * @type function
			 */
			isUnabledNodeVisiable: true,
			
			/**
			 * 判断一条Json数据是否在上次回显节点的路径上,
			 * 该回调函数有一个参数为data，即json的一条数据,返回true或false. 
			 * 
			 * @property isInPath
			 * @type function
			 */
			isInPath : function(data){return false},
			
			/**
			 * 判断一条Json数据是否是上次选择的回显,
			 * 该回调函数有一个参数为data，即json的一条数据,返回true或false. 
			 * 
			 * @property isLastSelect
			 * @type function
			 */
			isLastSelect : function(data){return false},
			
			/**
			 * 弹出窗口上的按钮，用于选择节点后的操作.
			 * 
			 * @property buttons
			 * @type Array，其中每个元素是一个键值对，键为按钮标题，值为回调函数
			 */
			buttons:  null ,
			
			/**
			 * 当点击了无效节点时的提示信息
			 */
			invalidMessage: null,
			
			dataToNodeAttr:["id","sequence","title"],
		 	dataToNodeText:   "title" 
			
		},
		
		
 
		/**
		 * 打开对话选择框
		 * @return
		 */
		open : function(){ 
			var me = this, html=[]; 
			var div = $("#" + me.options.containerDiv);
			div.empty();
			$("div.treeSelectorDialog").remove();
			me.selectedNode = null;
			$('<div>').addClass("treeSelectorDialog").css("height","100%").css("overflow","hidden").appendTo(div);
			$('<div id="treeSelectorErrorMessageDiv">').addClass("error-message").appendTo(div.find("div.treeSelectorDialog"));
			$('<div>').addClass("card-list").css("height","100%").css("overflow","hidden").appendTo(div.find("div.treeSelectorDialog"));
			$('<div>').css("width","100%").css("height","100%").css("overflow","auto").addClass("column3-left").addClass("card-tree").appendTo(div.find("div.card-list"));
 			$('<div>').addClass("treeSelectorDiv").appendTo(div.find("div.card-tree"));
 			$.ajaxSetup({cache:false});
			me._buildTree();
			
			$(function(){
					me.currentDialog = div.find("div.treeSelectorDialog").dialog({
						height: 400,
						width: 550,
						modal: true, 
						bgiframe :true,
						buttons: me.options.buttons,
						title: me.options.title
					});				
			});
			me.currentDialog.dialog('open');
		},
		
		/**
		 * 打开错误提示信息
		 * @param msg
		 * @return
		 */
		showError:function(msg){ 
			$("#treeSelectorErrorMessageDiv").html(msg).show();
		},
		
		/**
		 * 关闭错误提示信息
		 * @param msg
		 * @return
		 */
		hideError:function(){ 
			$("#treeSelectorErrorMessageDiv").html("").hide();
		},
		
		/**
		 * 关闭对话框
		 * @return
		 */
		close: function(){
			this.currentDialog.dialog("destroy");
			$("div.treeSelectorDialog").remove();
 		},
		
		/**
		 * 初始化
		 * @return
		 */
		_init: function(){
			this.selectedNode = null;
		},
		
		/**
		 * 
		 * 构建选择树
		 * @return
		 */
		_buildTree: function(){ 
			var me = this, ooo= this;
			$(".treeSelectorDiv").tree({
				ui: { 
					theme_name: "checkbox" ,
					selected_parent_close : false 
				},
				data: {
					async: true,
					type: "json",
					opts: {
						url: me.options.rootUrl
					},
					state: "closed" 
				},
				callback: {
					ondata: me.options.ondata ? me.options.ondata : 	
						function(data) {   
							var i, card, nodes = [], _title, color;
							for (i = 0; i < data.length; i++) {
								var _class = null,  _state = null;
								card = data[i];
								if(!me.options.isUnabledNodeVisiable && ! me.options.isEnable(card)){
									continue;
								}
								color = card.type.color;
								if( color ){
									_title = '<span style="border-left:3px solid #' + color + '">' +  "#" + card.sequence + " :" + Spark.util.encodeHTML(card.title) + "</span>";
								}else{
									_title = "<span>" + "#" + card.sequence + " :" + Spark.util.encodeHTML(card.title)+ "</span>"; 
								}
								//没有子节点的节点
								if(! me.options.hasChildren(card)){
									_class = _class ?  _class + " leaf" : "leaf";  
									_state = "leaf";
								}
								//无效的节点
								if(! me.options.isEnable(card)){
									_class = _class ?  _class + " disable" : "disable";  
									_state= _state ? _state : "closed";
								}
								//上次选择的节点
								if( me.options.isLastSelect(card)){
									_class = _class ?  _class + " last-select" : "last-select";   
									_state= _state ? _state : "closed";
								}
								//路径上的节点
								else if( me.options.isInPath(card)){
									_class = _class ?  _class + " path" : "path";   
									_state= _state ? _state : "closed";
								}else {
									_class = _class ?  _class + " common" : "common";  
									_state= _state ? _state : "closed";
								} 
								nodes.push({
										attributes: { cardId: card.id, sequence: card.sequence, title: Spark.util.encodeHTML(card.title) ,"class" : _class},
										data: { title: _title },
										state: _state 
								});
							 
							}
							return nodes;
						}  ,
					beforeopen: function(NODE,  TREE_OBJ){
						if(me.options.nodeUrl instanceof String){
							TREE_OBJ.settings.data.opts.url = me.options.nodeUrl;
						}else if(me.options.nodeUrl instanceof Function){
							var mee = $(NODE).is("li") ? $(NODE) : $(NODE).parent();  
							var url = me.options.nodeUrl(mee);
							TREE_OBJ.settings.data.opts.url = url;
						}else{
							TREE_OBJ.settings.data.opts.url = '';
						}
					} ,
					onopen : function(NODE,TREE_OBJ) {  
						if(Spark.util.ieVersion() ==6 && !me.selectedNode){  
							TREE_OBJ.container.find("li.disable >a >span").css("color" , "#ccc0cc");
							TREE_OBJ.container.find("li.path >a >ins").css("background-position" , "0 -16px");
							TREE_OBJ.container.find("li.last-select >a >ins").css("background-position" , "0 -32px");
						}
					},	
					onload : function(TREE_OBJ) { 
						if(Spark.util.ieVersion() ==6 ){    
							TREE_OBJ.container.find("li.disable >a >span").css("color", "#ccc0cc");
							TREE_OBJ.container.find("li.path >a >ins").css("background-position", "0 -16px");
							TREE_OBJ.container.find("li.last-select >a >ins").css("background-position", "0 -32px");
						}
					},
					onchange : function (NODE, TREE_OBJ) {   
						  var mee = $(NODE).is("li") ? $(NODE) : $(NODE).parent();  
						  if(!mee.hasClass("disable")){  
							  	me.hideError() ; 
							  	me.selectedNode = $(NODE); 
							  	TREE_OBJ.container.find("a").removeClass("checked");  
							  	mee.children("a").addClass("checked"); 
							  	if(Spark.util.ieVersion() !=6){  
							  		TREE_OBJ.container.find("li").removeClass("path").removeClass("last-select");
							  	}else{
							  		TREE_OBJ.container.find("ins").css("background-position" , "0 0");
									TREE_OBJ.container.find("a.checked >ins").css("background-position" , "0px -32px");
							  	}
							  	
						  }else{
							  if(me.options.invalidMessage){ 
								  me.showError(me.options.invalidMessage) ; 
							  }
						  }
		            } 
				} 
			});
		} 
	};
	 	
})();
