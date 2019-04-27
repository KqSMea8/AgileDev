/**
 * 浏览器历史工具类.
 * <p>
 * 此工具类依赖于jquery.hashchange.js插件：http://benalman.com/projects/jquery-hashchange-plugin/
 * 同时此工具使用时，必须执行initialize()方法来启动hashchange事件绑定
 * </p>
 * 
 * @namespace Spark.util
 * @class History
 * @author GuoLin
 */
(function() {

	Spark.util.History = {
		
		/**
		 * 标识当前History是否已经准备好.
		 * @property ready
		 * @type boolean
		 * @default false
		 */
		ready: false,
		
		/**
		 * 当前状态.
		 * @property states
		 * @type Object
		 * @default {}
		 */
		states: {},
		
		/**
		 * 回调函数.
		 * <p>
		 * 包含专属module的回调函数和全局变更回调函数两部分
		 * </p>
		 * @property handlers
		 * @type Object
		 */
		handlers: {
			
			/**
			 * 专属module的回调函数列表.
			 * <p>
			 * 其key为module名称，value为回调函数
			 * </p>
			 * @property specific
			 * @type Object
			 * @default {}
			 */
			specific: {},
			
			/**
			 * 全局回调函数列表.
			 * @property generic
			 * @type Array
			 * @default []
			 */
			generic: []
			
		},
		
		/**
		 * 为指定的module绑定改变事件.
		 * @method bind
		 * @param module {String} 可选项，待绑定的module名称，若忽略此参数则第一个参数为handler
		 * @param handler {Function} 当module被改变时将触发的回调函数
		 * @param initiallyTrigger {boolean} 可选值，当绑定时，是否需要将根据当前状态立即触发一次，默认为是
		 */
		bind: function(module, handler, initiallyTrigger) {
			if (!module) {
				throw new Error("Argument module must be specified.");
			}
			
			// 重载
			if (arguments.length == 1) {
				handler = module;
				module = null;
			}
			else if (arguments.length == 2 && module instanceof Function) {
				initiallyTrigger = handler;
				handler = module;
				module = null;
			}
			
			// 断言
			if (!(handler instanceof Function)) {
				throw new Error("Illegal arguments.");
			}

			// 将函数绑定到相应的module上
			if (module) {
				module = encodeURIComponent(module);
				if (!this.handlers.specific[module]) {
					this.handlers.specific[module] = [];
				}
				this.handlers.specific[module].push(handler);
			}
			// 如果不提供module则所有变更都会触发回调
			else {
				this.handlers.generic.push(handler);
			}
			
			// 初始化触发一次
			initiallyTrigger = (typeof initiallyTrigger == "undefined") ? true : initiallyTrigger;
			if (initiallyTrigger) {
				this._initiallyTrigger(module, handler);
			}
		},
		
		/**
		 * 将指定的module改变到state.
		 * @method go
		 * @param module {String} module名称或是hash值
		 * @param state {String} module值
		 */
		go: function(module, state) {
			if (typeof module == "undefined") {
				throw new Error("Parameter module must be specified.");
			}
			
			// 如果参数为hash值
			if (arguments.length == 1) {
				var i, param, tokens = module.split("&");
				for (i = 0; i < tokens.length; i++) {
					param = tokens[i].split("=");
					if (param.length == 2) {
						this.go(param[0], param[1]);
					}
				}
				return;
			}
			
			module = encodeURIComponent(module);
			state = encodeURIComponent(state);
			
			var oldState = this.states[module];
						
			// 根据states重新生成
			var states = this._hashToStates(this._getHash());
			states[module] = state;
			this._setHash(this._statesToHash(states));
		},
		
		/**
		 * 触发一个指定module的hash变更事件，但并不改变现有hash.
		 * @param module {String} module值，代表hash中的参数key，例如#x=100的module值为x
		 * @param newState {String} 新状态
		 * @param oldState {String} 原始状态
		 */
		trigger: function(module, newState, oldState) {
			oldState = oldState && decodeURIComponent(oldState);
			newState = newState && decodeURIComponent(newState);
			
			// module相关回调函数
			var i;
			if (module) {
				var specificHandlers = this.handlers.specific[module];
				if (specificHandlers && specificHandlers.length) {
					for (i = 0; i < specificHandlers.length; i++) {
						specificHandlers[i].call(this, newState, oldState);
					}
				}
			}
			
			// 全局回调函数
			var genericHandlers = this.handlers.generic;
			for (i = 0; i < genericHandlers.length; i++) {
				genericHandlers[i].call(this, module, newState, oldState);
			}
		},
		
		/**
		 * 初始化函数.
		 * @method initialize
		 */
		initialize: function() {
			// 检查依赖
			if (!jQuery.hashchangeDelay) {
				throw new Error("jQuery hashchange plugin has not found. [http://benalman.com/projects/jquery-hashchange-plugin/]");
			}
			
			var me = this;
			$(document).ready(function() {
				me.onDomReady();
			});
		},
		
		/**
		 * 销毁事件绑定以及状态等内容.
		 * @method destory
		 */
		destory: function() {
			this.ready = false;
			this.states = {};
			this.handlers.specific = {};
			this.handlers.generic = [];
			$(window).unbind("hashchange");
		},
		
		/**
		 * 获取指定module的当前的hash.
		 * <p>
		 * <em>工具方法.</em>
		 * 与此方法获取到的hash值是经过decodeURIComponent的.
		 * </p>
		 * @method getHash
		 * @param module {String} 可选参数，module值，若不指定则以参数形式返回所有module值
		 * @param isEncode 可选参数, 如果为true,则所有的value值会在被执行一次encodeURIComponent之后再进行拼接.
		 * @return {Sting} 返回当前的hash值,若不存在则返回空字符串
		 */
		getHash: function(module, isEncode) {
			var i, states, tokens, moduleName, moduleValue, results = [], hash = this._getHash();
			if (!hash) {
			    return "";
			} 
			states = hash.split("&");
			for (i = 0; i < states.length; i++) {
				tokens = states[i].split("=");
				if (tokens.length !== 2) {
					continue;
				}
				moduleName = tokens[0];
				
				moduleValue = decodeURIComponent(tokens[1]);
				if (isEncode){
					moduleValue = encodeURIComponent(moduleValue);
				}
				
				if (module && moduleName === module) {
					return moduleValue;
				}
				else if (!module) {
					results.push(moduleName + "=" + moduleValue);
				}
			}
			return !module ? results.join("&") : null;
		},
		
		/**
		 * 当DOM加载完成后调用此方法.
		 * @method onDomReady
		 */
		onDomReady: function() {
			if (this.ready) {
				return;
			}
			this.ready = true;
			
			// 绑定事件
			var me = this;
			var $window = $(window);
			$window.bind("hashchange", function(e) { 
				me.onHashChange(e);
			});
			
			// 如果初始化存在hash则触发一次
			if (this._getHash()) {
				$window.trigger("hashchange");
			}
		},
		
		/**
		 * 当hashchange事件被触发时调用此方法.
		 * @method onHashChange
		 * @param e {Event} 事件对象
		 */
		onHashChange: function(e) {
			// 比对状态，有变更的才会trigger
			var module, states = this._hashToStates(this._getHash()), currentStates = this.states;
			for (module in states) {
				if (currentStates[module] != states[module]) {
					this.trigger(module, states[module], currentStates[module]);
					currentStates[module] = states[module];
				}
			}
		},
		
		/**
		 * 初始化调用函数.
		 * <p>
		 * 用于在函数绑定到module上时，根据当前状态值进行初始化触发.
		 * </p>
		 * @param module {String} 可选值，module值
		 * @param handler {Function} 将被触发的回调函数
		 */
		_initiallyTrigger: function(module, handler) {
			if (module instanceof Function) {
				handler = module;
				module = null;
			}
			
			var states = this.states;
			// specific
			if (module) {
				var currentState = states[module] && decodeURIComponent(states[module]);
				if (currentState) {
					handler.call(this, currentState);
				}
			}
			// generic
			else {
				var module, state;
				for (module in states) {
					state = states[module] && decodeURIComponent(states[module]);
					handler.call(this, module, state);
				}
			}
		},
		
		/**
		 * 获取当前浏览器的hash.
		 * @method _getHash
		 * @return {String} hash字符串
		 * @private
		 */
		_getHash: function() {
			var i, href;
			href = window.location.href;
			i = href.indexOf("#");
			return i >= 0 ? href.substr(i + 1) : null;
		},

		/**
		 * 设置当前浏览器地址栏中的hash.
		 * @method _setHash
		 * @param hash {String} 待设置的hash值
		 * @private
		 */
		_setHash: function(hash) {
			window.location.hash = hash;
		},
		
		/**
		 * 将hash转化为状态对象.
		 * @param hash {String} hash值
		 * @return {Object} 状态对象
		 * @private
		 * @static
		 */
		_hashToStates: function(hash) {
			if (!hash) {
				return [];
			}
			var i, states = [], tokens, params = hash.split("&");
			for (i = 0; i < params.length; i++) {
				tokens = params[i].split("=");
				if (tokens.length === 2) {
					states[tokens[0]] = tokens[1];
				}
			}
			return states;
		},
		
		/**
		 * 将状态对象转化为hash.
		 * @param states {Object} 状态对象
		 * @return {String} hash值
		 * @private
		 * @static
		 */
		_statesToHash: function(states) {
			var state, params = [];
			for (state in states) {
				params.push(state + "=" + states[state]);
			}
			return params.join("&");
		}
		
	}
	
	// 初始化
	Spark.util.History.initialize();

})();