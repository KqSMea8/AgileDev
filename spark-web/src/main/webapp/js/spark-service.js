/**
 * 空间元数据.
 * 
 * @namespace Spark.service
 * @class SpaceService
 * @author GuoLin
 * 
 */
(function() {
	
	Spark.service.SpaceService = {
		
		/**
		 * 已经加载完成的空间列表.
		 * @property spaceCache
		 * @type Object
		 * @default {}
		 */
		spaceCache: {},
		
		/**
		 * 回调函数队列.
		 * <p>
		 * 当同一页面有多处调用此服务时，若前一个服务的AJAX尚未返回后一个
		 * 服务就开始调用，则不会从缓存取数据，为了避免这种情况，将在调用
		 * 时将callback加入队列，在AJAX完成时按顺序进行回调
		 * </p>
		 * @property callbackQueue
		 * @type Object
		 * @default {}
		 */
		callbackQueue: {},
		
		/**
		 * 空间加载静态方法.
		 * @method Spark.service.SpaceService.load
		 * @param prefix {String} 空间别名
		 * @param callback {Function} 回调函数，用于在AJAX加载完成时进行回调
		 * @param forceReload {boolean} 是否强制重新加载
		 * @static
		 */
		load: function SpaceService_load(prefix, callback, forceReload) {
			var spaceCache = this.spaceCache;
			var me = this;
			
			// 将回调函数加入队列
			var callbackQueue = this.callbackQueue;
			if (!callbackQueue[prefix]) {
				callbackQueue[prefix] = [];
			}
			callbackQueue[prefix].push(callback);
			
			// 如果在加载中，则不进行其余操作
			if (spaceCache[prefix] === "LOADING" && !forceReload) {
				return;
			}
			
			// 如果已经加载过，则直接调用回调函数
			else if (spaceCache[prefix] && !forceReload) {
				this._executeCallbackQueue(prefix);
			}
			
			// 否则重新加载
			else {
				spaceCache[prefix] = "LOADING";  // 设定加载中标识
				var url = Spark.constants.CONTEXT + "/ajax/spaces/" + prefix;
				$.ajax({
					url: url,
					dataType: "json",
					success: function(data) {
						// 整理CardType上下级关系
						me._bindCardTypesRelationship(data);
						
						// 写缓存
						spaceCache[prefix] = data;
						
						// 调用回调函数
						me._executeCallbackQueue(prefix);
					},
					error: function(data) {
						spaceCache[prefix] = null;
						throw new Error("Space metadata load failed.");
					}
				});
			}
		},
		
		/**
		 * 构建卡片类型间的关系.
		 * @method _bindCardTypesRelationship
		 * @param space {Object} 空间元数据对象
		 * @return {Object} 整理完成后的空间元数据对象
		 * @private
		 */
		_bindCardTypesRelationship: function SpaceService__bindCardTypesRelationship(space) {
			if (!space.cardTypes || !space.cardTypes.length) {
				return space;
			}
			
			// 初始化并整理cardTypes
			var i, cardType, cardTypes = space.cardTypes, map = {}, parent, deep;
			for (i = 0; i < cardTypes.length; i++) {
				cardType = cardTypes[i];
				cardType.space = space;  // 绑定space
				cardType.deep = 0;  // 绑定深度
				cardType = this._bindCardPropertiesRelationship(cardType);  // 绑定cardProperties
				cardType.children = [];
				cardType.allChildren = [];
				map[cardType.localId] = cardType;
			}
			
			// 将children和parent层级关联起来
			for (i = 0; i < cardTypes.length; i++) {
				cardType = cardTypes[i];
				if (!cardType.parent || !cardType.parent.id) {
					continue;
				}
				parent = map[cardType.parent.localId];
				if (parent) {
					cardType.parent = parent;  // 绑定parent
					parent.children.push(cardType);  // 绑定children
				}
			}
			
			// 计算deep
			for (i = 0; i < cardTypes.length; i++) {
				deep = 0;
				parent = cardTypes[i].parent;
				while (parent) {
					deep++;
					parent = parent.parent;
				}
				cardTypes[i].deep = deep;
			}
			

			// 按照深度排序
			cardTypes.sort(function(l, r) {
				return ((l.deep || 0) - (r.deep || 0)) || ((l.id || 0) - (r.id || 0));
			});
			
			// 将allChildren(所有层级)关联起来
			for (i = 0; i < cardTypes.length; i++) {
				cardType = cardTypes[i];
				cardType.allChildren = cardType.allChildren.concat(cardType.children);  // 绑定allChildren
				
				// 如果存在上级，则将自己的所有子类型加入到父类型的子类型中
				parent = cardType.parent
				while (parent) {
					parent.allChildren = parent.allChildren.concat(cardType.children);  // 绑定allChildren
					parent = parent.parent;
				}
				
				// 可循环引用的类型本身也是其子类型(但此类型不应重复被赋给parent.allChildren)
				if (cardType.recursive) {
					cardType.allChildren = cardType.allChildren.concat(cardType);
				}
			}
			return space;
		},
		
		/**
		 * 构建卡片属性间的关系.
		 * @method _bindCardPropertiesRelationship
		 * @param cardType {Object} 卡片类型对象
		 * @return {Object} 整理完成后的卡片类型对象
		 * @private
		 */
		_bindCardPropertiesRelationship: function SpaceService__bindCardPropertiesRelationship(cardType) {
			if (!cardType.cardProperties || !cardType.cardProperties.length) {
				return cardType;
			}
			
			// 初始化并整理cardProperties
			var i, cardProperty, cardProperties = cardType.cardProperties;
			for (i = 0; i < cardProperties.length; i++) {
				cardProperties[i].cardType = cardType;  // 绑定cardType
			}
			
			// 按照sort排序
			cardProperties.sort(function(l, r) {
				return (l.sort || 0) - (r.sort || 0);
			});
			
			return cardType;
		},
		
		/**
		 * 根据空间对象生成最终的结果对象.
		 * @method _generateResult
		 * @param space {Object} 空间元数据对象
		 * @return {Object} 生成后的结果对象
		 * @private
		 */
		_generateResult: function SpaceService__generateResult(space) {
			var cardTypeMap = {}, cardPropertyMap = {};
			// 处理卡片类型
			var cardTypes = space.cardTypes;
			if (cardTypes && cardTypes.length) {
				var i, j, cardType, cardProperties, cardProperty;
				for (i = 0; i < cardTypes.length; i++) {
					cardType = cardTypes[i];
					cardTypeMap[cardType.localId] = cardType;
					
					// 处理卡片属性
					cardProperties = cardType.cardProperties;
					if (cardProperties && cardProperties.length) {
						for (j = 0; j < cardProperties.length; j++) {
							cardProperty = cardProperties[j];
							cardPropertyMap[cardProperty.localId] = cardProperty;
						}
					}
				}
			}
			
			return { 
				space: space, 
				cardTypes: cardTypeMap,
				cardProperties: cardPropertyMap
			};
		},
		
		/**
		 * 组装最终对象并调用回调函数.
		 * @method _executeCallbackQueue
		 * @param prefix {String} 空间别名
		 * @private
		 */
		_executeCallbackQueue: function SpaceService__executeCallbackQueue(prefix) {
			// 组装最终回调参数
			var result = this._generateResult(this.spaceCache[prefix]);
			
			// 从回调函数队列中取出回调函数并执行
			var callbacks = this.callbackQueue[prefix], callback;
			while (callbacks && callbacks.length) {
				callback = callbacks.shift();
				if (callback && (callback instanceof Function)) {
					callback.call(result, result);
				}
			}
		}
		
	};

})();


/**
 * 卡片服务.
 * 
 * @namespace Spark.service
 * @class CardService
 * @author GuoLin
 * 
 */
(function() {
	
	// 定义别名，提高性能
	var formatDate = Spark.util.formatDate;

	Spark.service.CardService = {
		
		/**
		 * 日期格式类型.
		 * <p>
		 * 与Spark.util.formatDate中的格式类型匹配.
		 * </p>
		 * 
		 * @property dateFormatType
		 * @type String
		 * @default "longdate"
		 * @see Spark.util.formatDate
		 */
		dateFormatType: "longdate",
		
		/**
		 * 组装卡片数据.
		 * 
		 * @method assemble
		 * @param card {Object | Array} 卡片对象或卡片对象数组
		 * @return {Array} 组装完成的卡片对象数组
		 */
		assemble: function CardService_assemble(card) {
			
			// 数组的情况
			var i, ii, results = [];
			if (card instanceof Array) {
				for (i = 0, ii = card.length; i < ii; i++) {
					results.push(this.assemble(card[i]));
				}
				return results;
			}
			
			// 非数组的情况
			card = this._assembleValues(card);
			return card;
		},
		
		/**
		 * 组装卡片上的values字段.
		 * 
		 * @method _assembleValues
		 * @param card {Object} 卡片对象
		 * @return {Object} 组装完成的卡片对象
		 * @private
		 */
		_assembleValues: function CardService__assembleValues(card) {
			card.values = {};
			
			// 自定义字段
			if (card.propertyValues && card.propertyValues.length) {
				for (j = 0, jj = card.propertyValues.length; j < jj; j++) {
					value = card.propertyValues[j];
					
					// 重新包装日期显示格式
					if (value.value && value.cardProperty.type == "date") {
						value.displayValue = formatDate(value.value, this.dateFormatType);
					}
					card.values[value.cardProperty.localId] = value;
				}
			}
			
			// 系统字段
			for (prop in card) {
				if (prop == "id" || prop == "sequence" || prop == "title" || prop == "detail") {
					card.values[prop] = { value: card[prop], displayValue: card[prop] };
				}
				else if (prop == "createdTime" || prop == "lastModifiedTime") {
					card.values[prop] = { value: card[prop], displayValue: formatDate(card[prop], this.dateFormatType) };
				}
				else if (prop == "createdUser" || prop == "lastModifiedUser") {
					card.values[prop] = { value: card[prop], displayValue: card[prop].name };
				}
				else if (prop == "type") {
					card.values["cardType"] = { value: card[prop], displayValue: card[prop].name };
				}
				else if (prop == "parent") {
					card.values["parent"] = { value: card[prop], displayValue: card[prop].title }
				}
				else if (prop == "project") {
					card.values["project"] = { value: card[prop], displayValue: card[prop].name }
				}
			}
			return card;
		}
		
	}

})();