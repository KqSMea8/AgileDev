/**
 * 查询页面组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.Query
 * @author Adun
 */
(function(){
	
	var msg = Spark.util.message, IMPOSSIBLE_PROPERTY_VALUE = "-99@#$9";
	
	/**
	 * 构造器
	 * @constructor
	 * @param spacePrefix 所在空间的prefixCode
	 * @param queryDivId 存放查询条件的Div
	 * @param cardParentDivId 存放卡片上级条件的div
	 */
	Spark.pages.Filter = function Filter(spacePrefix, queryDivId, cardParentDivId){
		var me = this;
		
		me.spacePrefix = spacePrefix;
		me.queryDiv = $("#" + queryDivId);
		me.cardParentDiv = $("#" + cardParentDivId);
		
		Spark.service.SpaceService.load(me.spacePrefix, function(result){
			me.metadata = result;
            
    		//根据url,初始化查询页面内的所有数据.
			me._initQueryWithUrl();
		});
	};
	
	Spark.pages.Filter.prototype = {
		/**
		 * 所属空间的简称
		 */
		spacePrefix : null,	
		
		/**
		 * 元数据
		 */
		metadata : null,

		/**
		 * 卡片类型的字段列表
		 */
		fieldArray : new Array(),
		
		/**
		 * 查询框对象
		 */
		queryDiv : null,

		/**
		 * 卡片上级选择器.
		 */
		_cardTreeDialog : null,
		
		/**
		 * 查询框的初始化方法
		 * 初始化一个空的queryDiv,并使用URL中的数据进行填充
		 * @return
		 */
		_initQueryWithUrl : function Filter__initQueryWithUrl(){
			var me = this;
			var h = Spark.util.History;
			
			var query = new Spark.widgets.Query(Spark.util.History.getHash());
			me.initCardParentDiv(query);
			me.initQueryDiv(query);
			
			var callback = function(newstate, oldstate){
				var query = new Spark.widgets.Query("q=" + newstate);
				me.initCardParentDiv(query);
				me.initQueryDiv(query);
			};
			h.bind("q", callback, false);
		},
		
		/**
		 * 根据URL解析出来的parent对象,读取其中包含的"卡片上级"查询条件,
		 * 并在页面中显示出来,
		 * 并绑定对此条件"点击选择"的事件
		 * @param query { Query } 解析后的URL对象.主要需要使用其中的"q",即query conditions
		 * @return
		 */
		initCardParentDiv : function Filter_initCardParentDiv(query){
			var me = this;
			//绑定选择上级卡片的事件.
			$(function (){
				//生成树
				var _rootUrl = Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix +"/cards/root";
				var _nodeUrlFun = function(NODE){ 
					var model = { sequence: $(NODE).attr("sequence") };
					var url =  Spark.util.substitute(Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix + "/cards/{sequence}/children", model);
					return url;
				};
				var _buttons = {};
				_buttons[Spark.util.message("button.clear")]= function(){
					me._cardTreeDialog.cardIdField.val(IMPOSSIBLE_PROPERTY_VALUE);
					me._cardTreeDialog.cardTitleField.text(Spark.util.message("listlink.empty"));
					me._cardTreeDialog.close();
				};
				_buttons[Spark.util.message("button.ok")] = function(){ 
					var node = me._cardTreeDialog.selectedNode; 
					if (node){
						me._cardTreeDialog.cardIdField.val(node.attr("cardId"));
						me._cardTreeDialog.cardTitleField.text(node.attr("title"));
						me._cardTreeDialog.close();
					}else{ 
						me._cardTreeDialog.showError(Spark.util.message("card.parent.filterempty"));	
					}
				};
				me._cardTreeDialog = new Spark.widgets.CardTreeSelector({
					containerDiv: "treeSelectorContainer",
					rootUrl : _rootUrl,
					nodeUrl : _nodeUrlFun,
					hasChildren : function(DATA){return DATA.childrenSize > 0},
					isEnable : function(DATA){return DATA.childrenSize > 0},
					isUnabledNodeVisiable : false,
					buttons: _buttons,
					title:Spark.util.message("label.card.tree")
				});
				
			});	
		},
		
		/**
		 * 初始化查询页面,获取cardType对象,并初始化需要显示的属性.
		 * @param query { Query } 解析后的URL对象.主要需要使用其中的"q",即query conditions
		 * @return
		 */
		initQueryDiv : function Filter_initQueryDiv(query){
			var me = this;
			me.clearQueryDiv();
			me.initFields(query);
		},
		
		/**
		 * 初始化可选字段变量.
		 * @param query { Query } 解析后的URL对象.主要需要使用其中的"q",即query conditions
		 * @return
		 */
		initFields : function Filter_initFields(query){
			var me = this;
			
			//根据cardType初始化可查询的列
			me.fieldArray = me.getFieldArray();

			//如果是刚刚打开页面,需要读取由url中取出的查询条件直接显示.
			if (query){
				conditionArray = query.getConditionArray();
				for (var i = 0; i < conditionArray.length; i ++){
					me.addFilter(conditionArray[i]);
				}
			}
			
			//初始化一个查询条件
			me.addFilter();
		},
		
		/**
		 * 清除现有的所有查询条件
		 */
		clearQueryDiv : function Filter_clearQueryDiv(){
			var me = this;
			
			var children = me.queryDiv.children();
			for (var i=children.length-1;i>=0;i--){
				var conditionDiv = $(children[i]);
				var fieldName = $(conditionDiv.children()[0]).val();
				conditionDiv.remove();
			}
		},
		
		/**
		 * 增加一个查询条件
		 * @param query { Query } 解析后的URL对象.主要需要使用其中的"q",即query conditions
		 * 如果存在query,则对此查询条件进行初始化
		 * @return
		 */
		addFilter : function Filter_addFilter(condition){
			var me = this;
			//一整行
			var newDiv = $("<div>").addClass("query-condition").appendTo(me.queryDiv);
			//查询的字段
			var fieldDiv = $("<span>").appendTo(newDiv).addClass("query-left");
			
			var fieldSelectLink = $("<a>").attr("href", "javascript:void(0);").appendTo(fieldDiv).text(msg("listlink.empty")).addClass("query-link");
			var fieldSelectKey = $("<input type='hidden'>").insertBefore(fieldSelectLink);
			
			//menu的绑定
			var callback = function(choice, key){
				fieldSelectLink.text(choice.text());
				fieldSelectKey.val(key);
				me.initField(fieldSelectKey);
			};
			var options = new Array();
			for (var i=0;i<me.fieldArray.length;i++){
				var field = me.fieldArray[i];
				options.push({ "txt" : field.name, "link" : callback, "props" : field.id });
			}
			new Spark.widgets.Menu({ "target" : fieldSelectLink, "options" : options });
			
			//如果具有查询数据,则进行初始化
			if (condition){
				var field = me._getFieldById(condition.property);
				if (field && field.id != "parent"){
					fieldSelectLink.text(field.name);
					fieldSelectKey.val(field.id);
					me.initField(fieldSelectKey, condition);
				}else{
					newDiv.remove();
				}
			}
			
			return newDiv;
		},
		
		/**
		 * 初始化当前选中字段.主要是处理可选的操作符和取值输入框的控件形式(文本,时间,下拉等)
		 * @param fieldSelect {jQuery} 选中的字段的值的控件.
		 * @param query { Query } 解析后的URL对象.主要需要使用其中的"q",即query conditions
		 * 如果存在query,则对此查询条件进行初始化
		 * @return
		 */
		initField : function Filter_initField(fieldSelect, condition){
			var me = this;
			
			var newDiv = fieldSelect.parent().parent();

			//如果是用户手动选择一个未选择过的条件框,则需要再为用户创建一个未初始化的放到下面.
			if (!condition && newDiv.children().length == 1){
				me.addFilter();
			}
			
			//清除查询操作符合查询值
			for (var i=newDiv.children().length-1;i>=1;i--){
				$(newDiv.children()[i]).remove();
			}
			
			//读取当前选中字段
			var field = me._getFieldById(fieldSelect.val());
			
			//用于保存用户选操作符的div
			var operationDiv = $("<span>").appendTo(newDiv).addClass("query-center");
			me._renderOperationDiv(field, operationDiv, condition);
			
			//用于保存用户所填值的div
			var valueDiv = $("<span>").addClass("query-right").appendTo(newDiv);
			me._renderValueDiv(field, valueDiv, condition);
			
			//显示"删除当前查询条件"的按钮用的span
			newDiv.append(
				$("<span>").addClass("condition-remove-icon").append(
					$("<a>").attr("href", "javascript:void(0);").bind("click",function(){
						newDiv.remove();
					}).append($("<img src='" + Spark.constants.CONTEXT + "/images/icon-remove.gif'>"))
				)
			);
		},
	
		/**
		 * 渲染查询条件中,操作符号字段所在的div
		 * @param field 当前选择的查询字段的字段类型对象
		 * @param operationDiv 操作符号字段所在的div
		 * @return
		 */
		_renderOperationDiv : function  Filter__renderOperationDiv(field, operationDiv, condition){
			var i;

			//根据field类型的不同,生成可用的操作类型的列表
			if (field.type == "text"){
				var operationOptions = [ operation_like, operation_equals ];
			}else if (field.type == "number"){
				var operationOptions = [ operation_equals, operation_lessthan, operation_morethan ];
			}else if (field.type == "date"){
				var operationOptions = [ operation_between ];
			}else if (field.type == "list"){
				var operationOptions = [ operation_equals, operation_notequals ];
			}else if (field.type == "user"){
				var operationOptions = [ operation_equals, operation_notequals ];
			}else if (field.type == "card"){
				var operationOptions = [ operation_equals ];
			}else{
				Spark.widgets.Alert.alert("field type not found: " + field.type);
				return;
			}
			
			//操作符号显示值控件
			var operationSelectLink = $("<a>").attr("href", "javascript:void(0);").appendTo(operationDiv).addClass("query-link");
			
			//操作符号的实际值控件
			var operationSelectKey = $("<input type='hidden'>").insertBefore(operationSelectLink);

			//操作符号显示值/实际值,默认选中的数据
			if (operationOptions.length > 0){
				operationSelectLink.text(operationOptions[0].txt);
				operationSelectKey.val(operationOptions[0].props);
			}
			
			//操作符号的回调函数及绑定
			var callback = function(choice, key){
				operationSelectLink.text(choice.text());
				operationSelectKey.val(key);
			};
			for (i = 0; i < operationOptions.length; i++){
				operationOptions[i].link = callback;
			}
			
			//如果存在查询条件,初始化数据
			if (condition){
				for (i=0;i<operationOptions.length;i++){
					if (operationOptions[i].props == condition.operation){
						operationSelectLink.text(operationOptions[i].txt);
						operationSelectKey.val(operationOptions[i].props);
					}
				}
			}
			
			//生成下拉框
			new Spark.widgets.Menu({ "target" : operationSelectLink, "options" : operationOptions });
		},
		
		/**
		 * 渲染查询条件中,查询输入值字段所在的div
		 * @param field 当前选择的查询字段的字段类型对象
		 * @param valueDiv 查询输入值字段所在的div
		 * @return
		 */
		_renderValueDiv : function Filter__renderValueDiv(field, valueDiv, condition){
			var me = this;
			if (field.type == "text"){
				var callback = function(editBox){
					valueKey.val(editBox.val());
					if (!editBox.val()){
						editBox.val(Spark.util.message("listlink.editlink.empty"));
					}
					valueField.text(editBox.val());
				};
				var valueKey = $("<input type='hidden'>");
				var valueField = $("<a>").attr("href", "javascript:void(0);").text(msg("listlink.editlink.empty")).addClass("query-link");
				valueDiv.append(valueKey).append(valueField);

				//如果存在查询条件,初始化数据
				if (condition){
					valueKey.val(condition.value);
					if (condition.value){
						valueField.text(condition.value);
					}else{
						valueField.text(msg("listlink.editlink.empty"));
					}
				}
				
				new Spark.widgets.EditLink({ "target" : valueField, "link" : callback });
			}else if (field.type == "number"){
				var callback = function(editBox){
					valueKey.val(editBox.val());
					if (!editBox.val()){
						editBox.val(Spark.util.message("listlink.editlink.empty"));
					}
					valueField.text(editBox.val());
				};
				var valueKey = $("<input type='hidden'>");
				var valueField = $("<a>").attr("href", "javascript:void(0);").text(msg("listlink.editlink.empty")).addClass("query-link");
				valueDiv.append(valueKey).append(valueField);
				
				//如果存在查询条件,初始化数据
				if (condition){
					valueKey.val(condition.value);
					if (condition.value){
						valueField.text(condition.value);
					}else{
						valueField.text(msg("listlink.editlink.empty"));
					}
				}
				
				new Spark.widgets.EditLink({ "target" : valueField, "link" : callback });
			}else if (field.type == "date"){
				var date_now = Spark.util.formatDate(new Date(), "default");
				var actualValue = $("<input type='hidden'>").val(date_now + "," + date_now);
				var a1 = $("#parentDatePicker").clone().text(date_now).addClass("query-link");
				var betweenLabel = $("<br>");
				var a2 = $("#normalDatePicker").clone().text(date_now).addClass("query-link");
				
				//如果存在查询条件,初始化数据
				if (condition && condition.value){
					var values = condition.value.split(",");
					if (values.length == 2){
						actualValue.val(condition.value);
						a1.text(values[0]);
						a2.text(values[1]);
					}
				}
				
				valueDiv.append(actualValue).append(a1).append(betweenLabel).append(a2);
			}else if (field.type == "list"){
				var valueSelectLink = $("<a>").attr("href", "javascript:void(0);").addClass("query-link");
				var valueSelectKey = $("<input type='hidden'>");
				
				//生成下拉选项
				var callback = function (choice, key){
					valueSelectLink.text(choice.text());
					valueSelectKey.val(key);
				};
				var options = new Array();
				options.push({ "txt" : Spark.util.message("listlink.select-empty"), "link" : callback, "props" : "" });
				for (var elem in field.values){
					options.push({ "txt" : field.values[elem], "link" : callback, "props" : elem });
				}
				
				valueSelectLink.text(Spark.util.message("listlink.empty"));
				
				//如果存在查询条件,初始化数据
				if (condition){
					for (var i=0;i<options.length;i++){
						if (options[i].props == condition.value){
							valueSelectLink.text(options[i].txt);
							valueSelectKey.val(options[i].props);
						}
					}
				}
				
				valueDiv.append(valueSelectKey).append(valueSelectLink);
				new Spark.widgets.Menu( { "target" : valueSelectLink, "options" : options } );
			}else if (field.type == "user"){
				var callback = function(editBox){
					valueKey.val(editBox.val());
					if (!editBox.val()){
						editBox.val(Spark.util.message("listlink.editlink.empty"));
					}
					valueField.text(editBox.val());
				};
				var decorator = function(editBox){
					return Spark.thirdparty.userSuggest(
						editBox,
						function (user){
							editBox.attr("userId", user.id);
						}
					);
				};
				var destroy = function(suggest, editBox){
					if (editBox){
						Spark.thirdparty.destroySuggest(editBox);
					}
				};
				var valueKey = $("<input type='hidden'>");
				var valueField = $("<a>").attr("href", "javascript:void(0);").text(Spark.util.message("listlink.editlink.empty")).addClass("query-link");
				valueDiv.append(valueKey).append(valueField);

				//如果存在查询条件,初始化数据
				if (condition){
					valueKey.val(condition.value);
					if (condition.value){
						valueField.text(condition.value);
					}else{
						valueField.text(Spark.util.message("listlink.editlink.empty"));
					}
				}
				
				new Spark.widgets.EditLink({ "target" : valueField, "link" : callback, "decorator" : decorator, "de_decorator" : destroy });
			}else if (field.type == "card"){
			    var valueKey = $("<input type='hidden'>");
				var valueField = $("<a>").attr("href", "javascript:void(0);")
					.text(Spark.util.message("listlink.editlink.empty")).addClass("query-link")
					.click(function(){
						me._cardTreeDialog.cardIdField = valueKey;
						me._cardTreeDialog.cardTitleField = valueField;
						me._cardTreeDialog.open();
					});
				valueDiv.append(valueKey).append(valueField);
				
				//如果存在查询条件,初始化数据
				if (condition){
					valueKey.val(condition.value);
					if (condition.value){
						var url = Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix + "/cards/" + condition.value;
						$.getJSON(url, function(card) {
							valueField.text(card.title);
						});
					}else{
						valueField.text(Spark.util.message("listlink.editlink.empty"));
					}
				}
			     
			} else{
				Spark.widgets.Alert.alert("field type not found: " + field.type);
			}
		},
		
		/**
		 * 通过fieldId去getFieldArray方法生成的结果中查找field对象
		 * 对象id的规则为:普通字段,使用数据库字段名作为id.自定义字段,使用"props_"加上自定义字段的id作为id.
		 * @param { String } 需要查找的id
		 * @return { Field } 查询到的字段.未查询到则返回null
		 */
		_getFieldById : function Filter__getFieldById(fieldId){
			var me = this;
			
			var field = null;
			for (var i=0;i<me.fieldArray.length;i++){
				var tmpField = me.fieldArray[i];
				if (tmpField.id == fieldId){
					field = tmpField;
					break;
				}
			}
			return field;
		},
		
		/**
		 * 根据this.cardType获取到所有可以用的查询字段
		 * 如果cardType未初始化或者等于-1,则只生成公共字段
		 * @return { Array<Field> } 生成的Field对象列表
		 */
		getFieldArray : function Filter_getFieldArray(){
			var me = this, i;
			
			var tmpFieldArray = new Array();

			/*//标题
			var field = new Spark.pages.Field();
			field.name = msg("card.title");
			field.id = "title";
			field.type = "text";
			tmpFieldArray.push(field);

			//内容
			field = new Spark.pages.Field();
			field.name = msg("card.detail");
			field.id = "detail";
			field.type = "text";
			tmpFieldArray.push(field);*/

			//TODO 这个顺序等同于显示顺序，故哪些属性放在前面、哪些使用分隔线进行分组，需在此处定义 。
			
            //卡片类型
            field = new Spark.pages.Field();
            field.name = msg("card.cardType");
            field.id = "cardType";
            field.type = "list";
            field.values = new Object();
            var cardTypes = me.metadata.cardTypes, cardType;
            if (cardTypes) {
				for (var elem in cardTypes){
					cardType = cardTypes[elem];
					field.values[cardType.localId] = cardType.name;
				}
				tmpFieldArray.push(field);
			}
			
			//递归上级
            field = new Spark.pages.Field();
            field.name = msg("card.parent");
            field.id = "ancestor";
            field.type = "card";
			tmpFieldArray.push(field);
			

			//每个自定义字段作为一个可用的查询条件
            var cardProperties = me.metadata.space.cardProperties;
			if (cardProperties){
				for (var i=0;i<cardProperties.length;i++){
					pro = cardProperties[i];
					field = new Spark.pages.Field();
					field.name = pro.name;
					field.id = pro.localId;
					field.type = pro.type;
					if (pro.type == "list"){
						if (null != pro.info && pro.info != ""){
							eval("field.values = " + pro.info);
						}else{
							field.values = null;
						}
					}
					tmpFieldArray.push(field);
				}
			}
			
			//创建人
			field = new Spark.pages.Field();
			field.name = msg("card.createdUser");
			field.id = "createdUser";
			field.type = "user";
			tmpFieldArray.push(field);
			
			//创建时间
			field = new Spark.pages.Field();
			field.name = msg("card.createdTime");
			field.id = "createdTime";
			field.type = "date";
			tmpFieldArray.push(field);
			
			//项目ID
			field = new Spark.pages.Field();
			field.name = msg("card.project");
			field.id = "project";
			field.type="list";
			field.values = new Object();
			var projects = me.metadata.space.projects, project;
			if (projects && !!projects.length) {
				for (i=0; i< projects.length; i++){
					project = projects[i];
					field.values[project.id] = project.name;
				}
				tmpFieldArray.push(field);
			}
			return tmpFieldArray;
		},

		/**
		 * 拼接查询条件,并提交查询操作
		 */
		submitSearchButton : function Filter_submitSearchButton(){
			var me = this;
			
			//拼接url
			var url = Spark.constants.CONTEXT + "/spaces/" + me.spacePrefix + "/cards/list";

			var query = new Spark.widgets.Query(Spark.util.History.getHash());
			query.clear();
			
			//遍历每个查询条件
			for (var i=0;i<me.queryDiv.children().length;i++){
				var conditionString = "";
				var conditionDiv = $(me.queryDiv.children()[i]);

				//查询字段
				var fieldKey = $($(conditionDiv.children()[0]).children()[0]);
				if (!fieldKey.val() || fieldKey.val() == ""){
					continue;
				}
				
				//操作符号字段
				var operationKey = $($(conditionDiv.children()[1]).children()[0]);
				
				//查询值字段
				var fieldValue = $($(conditionDiv.children()[2]).children()[0]);
				
				if( fieldValue.val() != IMPOSSIBLE_PROPERTY_VALUE){
					query.addCondition(fieldKey.val(), operationKey.val(), fieldValue.val());
				}
				
			}
			
			//如果queryString为空,则通知history清空q
			var queryString = query.serializeConditions();
			if (!queryString){
				queryString = "q=";
			}
			Spark.util.History.go(queryString);
		}
	};
	
	/**
	 * 可查询字段的对象.
	 */
	Spark.pages.Field = function() {
	};
	Spark.pages.Field.prototype = {
		name:null,
		id:null,
		values:null,
		type:null
	};

	//操作符字符串
	var operation_equals = { "txt" : msg("query.operation.equals"), "props" : "equals" };
	var operation_lessthan = { "txt" : msg("query.operation.lessthan"), "props" : "lessthan" };
	var operation_morethan = { "txt" : msg("query.operation.morethan"), "props" : "morethan" };
	var operation_like = { "txt" : msg("query.operation.like"), "props" : "like" };
	var operation_between = { "txt" : msg("query.operation.between"), "props" : "between" };
	var operation_notequals = { "txt" : msg("query.operation.notequals"), "props" : "notequals" };
	
})();