/**
 * 查询语句分析器.
 * <p>
 * 用于接收URL中的查询参数并解析，
 * 或是将传入的查询对象组装成URL
 * </p>
 * 
 * @namespace Spark.widgets
 * @class Query
 * @author GuoLin
 */
(function() {
	
	var makeArray = Spark.util.makeArray;
	
	/**
	 * 查询语句分析器构造器.
	 * @constructor
	 */
	Spark.widgets.Query = function(url) {
		this.parse(url);
	}
	
	Spark.widgets.Query.prototype = {
		
		/**
		 * 条件字段查询器.
		 * @property conditions
		 * @type Query.Conditions
		 */
		conditions: null,
		
		/**
		 * 排序字段查询器.
		 * @property sorts
		 * @type Query.Sorts
		 */
		sorts: null,
		
		/**
		 * 自定义显示字段查询器.
		 * @property columns
		 * @type Query.Columns
		 */
		columns: null,
		
		/**
		 * 分组字段查询器.
		 * @property groupby
		 * @type Query.Groupby
		 */
		groupby: null,
		
		/**
		 * 分析URL.
		 * @method parse
		 * @param url {String} URL字符串
		 * @return {Object} 分析完成后的查询对象
		 */
		parse: function Query__parse(url) {
			var params = Spark.util.getParametersAndBookmarks(url) || {};
			
			this.conditions = new Spark.widgets.Query.Conditions(params);
			this.columns = new Spark.widgets.Query.Columns(params);
			this.sorts = new Spark.widgets.Query.Sorts(params);
			this.groupby = new Spark.widgets.Query.Groupby(params);
		},
		
		/**
		 * 序列化.
		 * @method serialize
		 * @param showEmpty {Boolean} 当结果为空时是否返回一个空值的参数(如：c=&s=&q=)
		 * @return {String} 序列化完成后的URL字符串
		 */
		serialize: function Query_serialize(showEmpty) {
			var serial, results = [];
			
			serial = this.serializeConditions(showEmpty);
			if (serial) {
				results.push(serial);
			}
			
			serial = this.serializeSorts(showEmpty);
			if (serial) {
				results.push(serial);
			}
			
			serial = this.serializeColumns(showEmpty);
			if (serial) {
				results.push(serial);
			}
			
			return results.join("&");
		},
		
		/**
		 * 序列化后进行编码，用于发送ajax请求.
		 * @method serialize
		 * @param showEmpty {Boolean} 当结果为空时是否返回一个空值的参数(如：c=&s=&q=)
		 * @return {String} 序列化完成后的URL字符串
		 */
		encodedSerialize: function Query_encodedSerialize(showEmpty) {
			var temp,serial, results = [];
			
			temp = this.conditions.serialize() || "";
			serial = (temp || showEmpty) ? this.conditions.options.key + "=" + encodeURIComponent(temp) : "";
			if (serial) {
				results.push(serial);
			}
			
			temp = this.sorts.serialize() || "";
			serial = (temp || showEmpty) ? this.sorts.options.key + "=" + encodeURIComponent(temp) : "";
			if (serial) {
				results.push(serial);
			}
			
			temp = this.columns.serialize() || "";
			serial = (temp || showEmpty) ? this.columns.options.key + "=" + encodeURIComponent(temp) : "";
			if (serial) {
				results.push(serial);
			}
			
			return results.join("&");
		},
		
		/**
		 * 是否整个Query对象都为空.
		 * @method isEmpty
		 * @return {boolean} 如果整个对象包含的所有属性均为空则返回true，否则返回false
		 */
		isEmpty: function Query_isEmpty() {
			return this.conditions.isEmpty() && this.columns.isEmpty() && this.sorts.isEmpty();
		},
		
		/**
		 * 清空所有字段内容.
		 * @method clear
		 */
		clear: function Query_clear() {
			this.clearConditions();
			this.clearSorts();
			this.clearColumns();
		},
		
		/**
		 * 获取所有条件的列表
		 * @return {Array} 将所有的查询条件打平放入Array中,并返回
		 */
		getConditionArray : function Query_getConditionArray(){
			return this.conditions.getConditionArray();
		},
		
		/**
		 * 获取条件字段列表.
		 * @method getConditions
		 * @param property {String} 可选参数，属性名称
		 * @return {Object} 如果填写了属性名称，则返回为此属性相关的查询条件数组，
		 *                  否则返回以属性名称为key，查询条件数组为value的map
		 */
		getConditions: function Query_getConditions(property) {
			if (property) {
				return this.conditions.conditions[property];
			}
			else {
				return this.conditions.conditions;
			}
		},
		
		/**
		 * 增加一个条件字段.
		 * @method addCondition
		 * @param property {String} 属性名称
		 * @param operation {String} 条件操作符
		 * @param value {String} 可选参数，比较值，不填写默认为空字符串""
		 */
		addCondition: function Query_addCondition(property, operation, value) {
			this.conditions.add(property, operation, value);
		},
		
		/**
		 * 移除一个条件字段.
		 * @method removeCondition
		 * @param property {String} 属性名称
		 * @param operation {String} 可选参数，条件操作符，若填写则仅移除匹配操作符的条件字段
		 * @param value {String} 可选参数，比较值，若填写则仅移除匹配值的条件字段
		 */
		removeCondition: function Query_removeCondition(property, operation, value) {
			this.conditions.remove(property, operation, value);
		},
		
		/**
		 * 条件字段是否为空.
		 * @method isConditionsEmpty
		 * @return {boolean} 为空返回true，否则返回false
		 */
		isConditionsEmpty: function Query_isConditionsEmpty() {
			return this.conditions.isEmpty();
		},
		
		/**
		 * 清空所有条件字段.
		 * @method clearConditions
		 */
		clearConditions: function Query_clearConditions() {
			this.conditions.clear();
		},
		
		/**
		 * 序列化条件字段.
		 * @method serializeConditions
		 * @param showEmpty {Boolean} 当结果为空时是否返回一个空值的参数(如：c=&s=&q=)
		 * @return {String} 序列化后的URL参数字符串
		 */
		serializeConditions: function Query_serializeConditions(showEmpty) {
			var serial = this.conditions.serialize() || "";
			return (serial || showEmpty) ? this.conditions.options.key + "=" + serial : "";
		},
		
		/**
		 * 获取排序字段.
		 * @method getSort
		 * @param property {String} 属性名称
		 * @return {Object} 排序字段对象
		 */
		getSort: function Query_getSort(property) {
			return this.getSorts()[property];
		},
		
		/**
		 * 获取排序字段列表.
		 * @method getSorts
		 * @return {Object} 排序字段列表
		 */
		getSorts: function Query_getSorts() {
			return this.sorts.sorts;
		},
		
		/**
		 * 添加一个排序字段.
		 * @method addSort
		 * @param property {String} 属性名称
		 * @param order {String} 可选值，倒序(desc)或是正序(asc)，若不填写则表示移除此排序字段
		 */
		addSort: function Query_addSort(property, order) {
			this.sorts.add(property, order);
		},
		
		/**
		 * 移除排序字段.
		 * @method removeSort
		 * @param property {String} 属性名称
		 */
		removeSort: function Query_removeSort(property) {
			this.sorts.remove(property);
		},
		
		/**
		 * 排序字段是否为空.
		 * @method isSortsEmpty
		 * @return {boolean} 为空返回true，否则返回false
		 */
		isSortsEmpty: function Query_isSortsEmpty() {
			return this.sorts.isEmpty();
		},
		
		/**
		 * 清空所有排序字段.
		 * @method clearSorts
		 */
		clearSorts: function Query_clearSorts() {
			this.sorts.clear();
		},
		
		/**
		 * 序列化排序字段.
		 * @method serializeSorts
		 * @param showEmpty {Boolean} 当结果为空时是否返回一个空值的参数(如：c=&s=&q=)
		 * @return {String} 序列化后的URL参数字符串
		 */
		serializeSorts: function Query_serializeSorts(showEmpty) {
			var serial = this.sorts.serialize() || "";
			return (serial || showEmpty) ? this.sorts.options.key + "=" + serial : "";
		},
		
		/**
		 * 获取自定义显示字段.
		 * @method getColumn
		 * @param property {String} 属性名称
		 * @return {Object} 自定义显示字段对象
		 */
		getColumn: function Query_getColumn(property) {
			return this.columns.columns[property];
		},
		
		/**
		 * 获取自定义显示字段.
		 * @method getColumns
		 * @return {Object} 自定义显示字段对象
		 */
		getColumns: function Query_getColumns() {
			return this.columns.columns;
		},
		
		/**
		 * 添加自定义显示字段.
		 * @method addColummn
		 * @param property {String} 属性名称
		 */
		addColumn: function Query_addColumn(property) {
			this.columns.add(property);
		},
		
		/**
		 * 移除自定义字段.
		 * @method removeColumn
		 * @param property {String} 属性名称
		 */
		removeColumn: function Query_removeColumn(property) {
			this.columns.remove(property);
		},
		
		/**
		 * 显示字段是否为空.
		 * @method isColumnsEmpty
		 * @return {boolean} 为空返回true，否则返回false
		 */
		isColumnsEmpty: function Query_isColumnsEmpty() {
			return this.columns.isEmpty();
		},
		
		/**
		 * 清空所有自定义字段.
		 * @method clearColumns
		 */
		clearColumns: function Query_clearColumns() {
			this.columns.clear();
		},
		
		/**
		 * 序列化显示字段.
		 * @method serializeColumns
		 * @param showEmpty {Boolean} 当结果为空时是否返回一个空值的参数(如：c=&s=&q=)
		 * @return {String} 序列化后的URL参数字符串
		 */
		serializeColumns: function Query_serializeColumns(showEmpty) {
			var serial = this.columns.serialize() || "";
			return (serial || showEmpty) ? this.columns.options.key + "=" + serial : "";
		},
		/**
		 * 设置分组字段
		 * @method setGroupby
		 * @param property {String} 属性名称
		 */
		setGroupby: function Query_setGroupBy(property){
			this.groupby.set(property);
		},
		
		/**
		 * 清除分组字段
		 * @method clearGroupby
		 * @param property {String} 属性名称
		 */
		clearGroupby: function Query_clearGroupBy(){
			this.groupby.clear();
		},
		
		/**
		 * 序列化分组字段.
		 * @method serializeSorts
		 * @param showEmpty {Boolean} 当结果为空时是否返回一个空值的参数(如：c=&s=&q=)
		 * @return {String} 序列化后的URL参数字符串
		 */
		serializeGroupby: function Query_serializeGroupby(showEmpty) {
			var serial = this.groupby.serialize() || "";
			return (serial || showEmpty) ? this.groupby.options.key + "=" + serial : "";
		}
		
	};
	
	/**
	 * 排序类.
	 * @param params {Object} URL上的参数map
	 * @param options {Object} 可选参数对象
	 * @class Sorts
	 * @constructor
	 */
	Spark.widgets.Query.Sorts = function Query_Sorts_constructor(params, options) {
		this.options = $.extend(this.options, options);
		this.sorts = {};

		if ((typeof params == "string") || (params instanceof String)) {
			this.parse(params);
		}
		else if (params[this.options.key]) {
			this.parse(params[this.options.key]);
		}
		
		return this;
	}
	
	Spark.widgets.Query.Sorts.prototype = {
		
		options: {
			
			/**
			 * 排序参数在URL中的参数名称.
			 * @property key
			 * @type String
			 * @default "s"
			 */
			key: "s"
			
		},
		
		/**
		 * 排序字段存储map.
		 * <p>
		 * 其中key是property名字，例如"prop_122", "content"
		 * value为"asc"或"desc"
		 * </p>
		 * @property sorts
		 * @type Object
		 */
		sorts: null,
		
		/**
		 * 加入一个排序字段.
		 * @method add
		 * @param property {String | Object | Array} 属性名称，或是对象，或是对象列表
		 * @param order {String} "asc"或是"desc"，若此参数不填写则表示移除此排序字段
		 */
		add: function Query_Sorts_add(property, order) {
			// 重载，仅有第一个参数
			if (arguments.length == 1 || typeof order === "undefined") {
				// 第一个参数是array的情况
				if (property instanceof Array) {
					for (var i = 0; i < property.length; i++) {
						this.add(property[i]);
					}
					return;
				}
				// 第一个参数是对象的情况
				else if (property.order) {
					order = property.order;
					property = property.property;
				}
				// 如果不填写第二个参数则移除
				else {
					this.remove(property);
					return;
				}
			}
			
			this.sorts[property] = { 
				property: property, 
				propertyId: propertyToId(property),
				order: (order.toLowerCase() == "asc") ? "asc" : "desc" 
			};
		},
		
		/**
		 * 移除一个排序字段.
		 * @method remove
		 * @param property {String} 属性名称，若为自定义字段以prop_开头接自定义属性ID
		 */
		remove: function Query_Sorts_remove(property) {
			delete this.sorts[property];
		},
		
		/**
		 * 是否为空.
		 * @method isEmpty
		 * @return {boolean} 如果为空返回true，否则返回false
		 */
		isEmpty: function Query_Sorts_isEmpty() {
			for (var p in this.sorts) {
				return false;
			}
			return true;
		},
		
		/**
		 * 清空所有排序字段.
		 * @method clear
		 */
		clear: function Query_Sorts_clear() {
			this.sorts = {};
		},
		
		/**
		 * 解析排序文本.
		 * @method parse
		 * @param sortText {String} 包含排序内容的文本
		 */
		parse: function Query_Sorts_parse(sortText) {
			var groups = [], r, re = /\[([^\r\n\[\]]*)\]/g;
			while (r = re.exec(sortText)) {
				groups.push(r[1]);
			}
			
			if (groups.length % 2 != 0) {
				throw new Error("Illegal sort expressions.");
			}
			
			for (var i = 0; i < groups.length; i += 2) {
				this.add(groups[i], groups[i + 1]);
			}
		},
		
		/**
		 * 序列化成URL参数字符串.
		 * @method serialize
		 * @return 序列化完成后的URL参数字符串
		 */
		serialize: function Query_Sorts_serialize() {
			var key, sort, sorts = this.sorts, results = [];
			for (key in sorts) {
				sort = sorts[key];
				results.push("[" + sort.property + "][" + sort.order + "]");
			}
			return results.join("+");
		}
		
	}
	
	/**
	 * 自定义显示字段类.
	 * @param params {Object} URL上的参数map
	 * @param options {Object} 可选参数对象
	 * @class Columns
	 * @constructor
	 */
	Spark.widgets.Query.Columns = function Query_Columns_constructor(params, options) {
		this.options = $.extend(this.options, options);
		this.columns = {};

		if ((typeof params == "string") || (params instanceof String)) {
			this.parse(params);
		}
		else if (params[this.options.key]) {
			this.parse(params[this.options.key]);
		}
		
		return this;
	}
	
	Spark.widgets.Query.Columns.prototype = {
		
		options: {
			
			/**
			 * 自定义字段显示在URL中的参数名称.
			 * @property key
			 * @type String
			 * @default "c"
			 */
			key: "c"
			
		},
		
		/**
		 * 当前显示字段.
		 * @property columns
		 * @type Object
		 */
		columns: null,
		
		/**
		 * 用于记录加入顺序.
		 * <p>
		 * 由于Chrome不会记录Object.property的顺序，
		 * 因此使用一个全局的index属性来记录加入的顺序，
		 * 可供客户端调用
		 * <strong>此值只增不减，不保证连续性</strong>
		 * </p>
		 * @property index
		 * @type Number
		 */
		index: 0,
		
		/**
		 * 添加一个显示字段.
		 * @method add
		 * @param property {String | Array} 属性名称或是属性名称列表
		 */
		add: function Query_Columns_add(property) {
			// 第一个参数是array的情况
			if (property instanceof Array) {
				for (var i = 0; i < property.length; i++) {
					this.add(property[i]);
				}
				return;
			}
			
			this.columns[property] = { 
				index: this.index++,
				property: property,
				propertyId: propertyToId(property)
			};
		},
		
		/**
		 * 移除一个显示字段.
		 * @method remove
		 * @param property {String} 属性名称，若为自定义字段以prop_开头接自定义属性ID
		 */
		remove: function Query_Columns_remove(property) {
			delete this.columns[property];
		},
		
		/**
		 * 是否为空.
		 * @method isEmpty
		 * @return {boolean} 如果为空返回true，否则返回false
		 */
		isEmpty: function Query_Columns_isEmpty() {
			for (var p in this.columns) {
				return false;
			}
			return true;
		},
		
		/**
		 * 清空所有自定义字段.
		 * @method clear
		 */
		clear: function Query_Columns_clear() {
			this.columns = {};
		},
		
		/**
		 * 解析自定义字段文本.
		 * @method parse
		 * @param columnText {String} 包含自定义字段内容的文本
		 */
		parse: function Query_Columns_parse(columnText) {
			var groups = [], r, re = /\[([^\r\n\[\]]*)\]/g;
			while (r = re.exec(columnText)) {
				groups.push(r[1]);
			}
			
			for (var i = 0; i < groups.length; i++) {
				this.add(groups[i]);
			}
		},
		
		/**
		 * 序列化成URL参数字符串.
		 * @method serialize
		 * @return 序列化完成后的URL参数字符串
		 */
		serialize: function Query_Columns_serialize() {
			var i, n, key, column, sortedColumns, results = [];
			
			// 排序
			sortedColumns = makeArray(this.columns, function(l, r) { 
				return l.value.index - r.value.index;
			});
			
			// 组装
			for (i = 0, n = sortedColumns.length; i < n; i++) {
				column = sortedColumns[i].value;
				results.push("[" + column.property + "]");
			}
			
			return results.join("+");
		}
		
	}
	
	/**
	 * 条件查询类.
	 * @param params {Object} URL上的参数map
	 * @param options {Object} 可选参数对象
	 * @class Conditions
	 * @constructor
	 */
	Spark.widgets.Query.Conditions = function(params, options) {
		this.options = $.extend(this.options, options);
		this.conditions = {};
		
		if ((typeof params == "string") || (params instanceof String)) {
			this.parse(params);
		}
		else if (params[this.options.key]) {
			this.parse(params[this.options.key]);
		}
		
		return this;
	}
	
	Spark.widgets.Query.Conditions.prototype = {
		
		options: {
			
			/**
			 * 条件参数在URL中的参数名称.
			 * @property key
			 * @type String
			 * @default "q"
			 */
			key: "q"
			
		},
		
		/**
		 * 当前条件字段.
		 * @property conditions
		 * @type Object
		 */
		conditions: null,
		
		/**
		 * 获取所有查询条件的列表
		 * @return
		 */
		getConditionArray : function Query_Conditions_getArray() {
			var conditionArray = new Array();
			if (this.conditions){
				for (var elem in this.conditions){
					if (this.conditions[elem] && this.conditions[elem].length){
						for (var i=0;i<this.conditions[elem].length;i++){
							conditionArray.push(this.conditions[elem][i]);
						}
					}
				}
			}
			return conditionArray;
		},
		
		/**
		 * 添加一个条件字段.
		 * @method add
		 * @param property {String} 属性名称，或是对象，或是对象列表
		 * @param operation {String} 条件操作符
		 * @param value {String} 值
		 */
		add: function Query_Conditions_add(property, operation, value) {
			// 重载，仅有第一个参数
			if (arguments.length == 1 || (typeof operation === "undefined" && typeof value == "undefined")) {
				// 第一个参数是array的情况
				if (property instanceof Array) {
					for (var i = 0; i < property.length; i++) {
						this.add(property[i]);
					}
					return;
				}
				// 第一个参数是对象的情况
				else {
					value = property.value;
					operation = property.operation;
					property = property.property;
				}
			}
			
			if (!property || !operation) {
				throw new Error("Parameters field and operation must not be null.");
			}
			value = value || "";
			
			var conditions = this.conditions;
			if (!conditions[property]) {
				conditions[property] = [];
			}
			conditions[property].push({
				property: property,
				propertyId: propertyToId(property),
				operation: operation,
				value: value
			});
		},
		
		/**
		 * 移除一个条件字段.
		 * @method remove
		 * @param property {String} 属性名称，若为自定义字段以prop_开头接自定义属性ID
		 * @param operation {String} 可选参数，条件操作符，若填写则仅移除属性之下匹配这个操作符的字段
		 * @param value {String} 可选参数，比较值，若填写则仅移除属性和条件操作符均匹配的情况下匹配此值的字段
		 */
		remove: function Query_Conditions_remove(property, operation, value) {
			if ((arguments.length == 1 || (typeof operation === "undefined" && typeof value == "undefined")) && 
					!((typeof property == "string") || (property instanceof String))) {
				value = property.value;
				operation = property.operation;
				property = property.property;
			}
			
			if (!property) {
				throw new Error("Parameters field must be specified.");
			}
			var conditions = this.conditions;
			if (!conditions[property]) {
				return;
			}
			
			if (!operation) {
				conditions[property] = [];
			}
			// 如果给出了操作符，则只移除操作符相关的
			else {
				var list = conditions[property], newList = [], condition;
				for (i = 0; i < list.length; i++) {
					condition = list[i];
					if (condition.operation != operation || (typeof value != "undefined" && condition.value != value)) {
						newList.push(condition);
					}
				}
				conditions[property] = newList;
			}
		},
		
		/**
		 * 是否为空.
		 * @method isEmpty
		 * @return {boolean} 如果为空返回true，否则返回false
		 */
		isEmpty: function Query_Conditions_isEmpty() {
			for (var p in this.conditions) {
				return false;
			}
			return true;
		},
		
		/**
		 * 清空所有条件字段.
		 * @method clear
		 */
		clear: function Query_Conditions_clear() {
			this.conditions = {};
		},
		
		/**
		 * 解析条件文本.
		 * @method parse
		 * @param conditionText {String} 包含条件内容的文本
		 */
		parse: function Query_Conditions_parse(conditionText) {
			var groups = [], r, re = /\[([^\r\n\[\]]*)\]/g;
			while (r = re.exec(conditionText)) {
				groups.push(r[1]);
			}
			
			if (groups.length % 3 != 0) {
				throw new Error("Illegal condition expressions.");
			}
			
			for (var i = 0; i < groups.length; i += 3) {
				this.add(groups[i], groups[i + 1], decodeURIComponent(groups[i + 2]));
			}
		},
		
		/**
		 * 序列化成URL参数字符串.
		 * @method serialize
		 * @return 序列化完成后的URL参数字符串
		 */
		serialize: function Query_Conditions_serialize() {
			var i, key, condition, conditionList, conditions = this.conditions, results = [];
			for (key in conditions) {
				conditionList = conditions[key];
				for (i = 0; i < conditionList.length; i++) {
					condition = conditionList[i];
					results.push("[" + condition.property + "][" + condition.operation + "][" + encodeURIComponent(condition.value) + "]");
				}
			}
			return results.join("+");
		}
		
	}
	
	/**
	 * 将property转换为自定义属性ID.
	 * @method propertyToId
	 * @param propertyName {String} 属性名称，例如content,prop_133
	 * @return {Number} 属性ID值，如果为自定义属性(以prop_开头)则转换为其自定义属性ID，否则返回null
	 */
	var propertyToId = function(propertyName) {
		var prefix = "prop_";
		return (propertyName.indexOf(prefix) == 0) ? parseInt(propertyName.substring(prefix.length)) : null
	}
	
	Spark.widgets.Query.Groupby = function Query_Groupby_constructor(params, options) {
		this.options = $.extend(this.options, options);

		if ((typeof params == "string") || (params instanceof String)) {
			this.parse(params);
		}
		else if (params[this.options.key]) {
			this.parse(params[this.options.key]);
		}
		
		return this;
	}
	
	Spark.widgets.Query.Groupby.prototype = {
		
		options: {
			
			/**
			 * 分组参数在URL中的参数名称.
			 * @property key
			 * @type String
			 * @default "g"
			 */
			key: "g"
			
		},
		
		/**
		 * 排序字段存储map.
		 * <p>
		 * 其中key是property名字，例如"prop_122", "content"
		 * </p>
		 * @property groupby
		 * @type Object
		 */
		groupby: null,
		
		/**
		 * 设置一个分组字段.
		 * @method set
		 * @param property {String} 属性名称
		 */
		set: function Query_Groupby_Set(property) {
			if (arguments.length == 1) {
				if ((typeof property == "string") || (property instanceof String)) {
					this.groupby = property;
				}
			}
		},
		
		/**
		 * 清空所有分组字段.
		 * @method clear
		 */
		clear: function Query_Groupby_clear() {
			this.groupby = null;
		},
		
		/**
		 * 解析分组文本.
		 * @method parse
		 * @param sortText {String} 包含分组内容的文本
		 */
		parse: function Query_Groupby_parse(groupbyText) {
			var groups = [], r, re = /\[([^\r\n\[\]]*)\]/g;
			while (r = re.exec(groupbyText)) {
				groups.push(r[1]);
			}
			
			if (groups.length != 1) {
				throw new Error("Illegal sort expressions.");
			}
			
			this.set(groups[0]);
		},
		
		/**
		 * 序列化成URL参数字符串.
		 * @method serialize
		 * @return 序列化完成后的URL参数字符串
		 */
		serialize: function Query_Groupby_serialize() {
			var results = [];
			if(this.groupby){
				results.push("[" + this.groupby+ "]");
			}
			return results.join("+");
		}
		
	}
})();