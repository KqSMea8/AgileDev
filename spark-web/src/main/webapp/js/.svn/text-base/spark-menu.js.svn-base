if (Spark == undefined){
	var Spark = {};
}else{
	Spark = Spark || {};
}
Spark.widgets = Spark.widgets || {};

/** 
* menu组件
 * @author Adun
 * 2010-07-08
 */
(function(){
	/**
	 * Spark.widgets.Menu对象
	 */
	Spark.widgets.Menu = function Menu(params){
		var me = this;
		/**
		 * 初始化各种字段
		 */
		//TODO Adun 可能会需要更进一步校验.
		if (!params) {
			throw new Error("Argument of Menu constructor must not be null.");
		}
		me.params = $.extend({ options : new Array(), _bindedEvents : new Array() }, me.params, params);
		
		//如果option是以params的形式传递进来的话,在此协助new出对象
		for (var i = me.params.options.length - 1; i >= 0; i--){
			var tmpObject = me.params.options[i];
			if (tmpObject instanceof Spark.widgets.MenuOption){
				continue;
			}
			var tmpOption = new Spark.widgets.MenuOption(tmpObject);
			me.params.options[i] = tmpOption;
		}

		//初始化ul
		me._initUl();
		
		//绑定事件
		me._triggerMenu();
		
		//变更状态
		me.params._initFinish = true;
	};
	
	Spark.widgets.Menu.prototype = {
		
		/**
		 * 参数列表
		 */
		params : {	
			/**
			 * 绑定此事件的dom对象
			 */
			target : null,
			
			/**
			 * 选项列表--一个Spark.widgets.MenuOption实例的列表
			 */
			options : undefined,
			
			/**
			 * 是否支持多选.如果支持,则点击选项后,不收起列表.
			 */
			multi_select : false,
			
			/**
			 * 操作名.默认为click.可以填写多个,空格分开.
			 */
			actionNames : 'mouseup',
			
			/**
			 * 用户对ul的自定义css
			 */
			css : null,
			
			/**
			 * 根据输入的选项列表生成的ul对象
			 */
			_ul : null,
			
			/**
			 * 是否已经完成了初始化
			 */
			_initFinish : false,
			
			/**
			 * 已经绑定的事件
			 */
			_bindedEvents : undefined
			
		},

		/**
		 * 删除menu对象
		 */
		destroy : function Menu_destroy(){
			var me = this;
			for (var i=0; i<me.params._bindedEvents.length; i++){
				me.params._bindedEvents[i].target.unbind(me.params._bindedEvents[i].actionName, me.params._bindedEvents[i].action);
			}
			me.params._ul.remove();
			me.params._ul = null;
		},
		
		/**
		 * 修改一个option的状态为指定值
		 * @param index option的序号
		 * @param newState option的新状态为 选中 还是 未选中
		 * @return
		 */
		activeOption : function Menu_activeOption(index, newState){
			var me = this;
			var option = me.params.options[index];
			if (null != option && option instanceof Spark.widgets.MenuOption){
				if (option.params.isSelected != newState){
					me.params._initFinish = false;
					option.params.aTag.trigger("click");
					me.params._initFinish = true;
				}
			}
		},
		
		/**
		 * 修改一个option的状态为指定值
		 * @param key option的标识关键字.仅处理找到的第一个key为指定值的option
		 * @param newState option的新状态为 选中 还是 未选中
		 * @return
		 */
		activeOptionByKey : function Menu_activeOptionByKey(key, newState){
			var me = this;
			for (var i=0;i<me.params.options.length;i++){
				if (me.params.options[i].params.key == key){
					me.activeOption(i, newState);
					break;
				}
			}
		},
		
		/**
		 * 修改所有option的状态为指定值
		 * @param newState option的新状态为 选中 还是 未选中
		 * @return
		 */
		activeAllOptions : function Menu_activeAllOptions(newState){
			var me = this;
			for (var i=0;i<me.params.options.length;i++){
				var option = me.params.options[i];
				if (option.params.isSelected != newState){
					me.params._initFinish = false;
					option.params.aTag.trigger("click");
					me.params._initFinish = true;
				}
			}
		},
		
		/**
		 * 初始化menu对象
		 * @return
		 */
		_initUl : function Menu__initUl(){
			var me = this;
			
			//增加ul
			me.params._ul = $("<ul>").addClass("spark-menu-basic");
			if (me.params.css){
				me.params._ul.addClass(me.params.css);
			}else{
				me.params._ul.addClass("spark-menu-custom");
			}
			
			//解析option,生成li加入到ul当中
			for (var i = 0; i < me.params.options.length; i++){
				var option = me.params.options[i];
				option.params.menu = me;
				var aTag = $("<a>").attr("href", "javascript:void(0);");
				(function (option, aTag){
					aTag.bind("click", function (event){
						option.triggerClick();
						event.stopPropagation();
					});
				}(option, aTag));
				if (me.params.css){
					aTag.addClass(me.params.css);
				}else{
					aTag.addClass("spark-menu-custom");
				}
				
				aTag.text(option.params.txt);
				option.params.aTag = aTag;
				$("<li>").append(aTag).appendTo(me.params._ul).addClass("spark-menu-li");
				
				if (me.params.multi_select && option.params.defaultSelected){
					aTag.trigger("click");
				}
			}
		},
	
		/**
		 * 给targe绑定触发事件
		 * @return
		 */
		_triggerMenu : function Menu__triggerMenu(){
			var me = this;
			//拆分需要响应的事件
			var actionNameArray = me.params.actionNames.split(" ");
			
			//将ul插入当前对象后面
			me.params._ul.insertAfter(me.params.target);
			
			//为target对象绑定事件,以触发ul的显示和隐藏
			for (var i = 0; i < actionNameArray.length; i++){
				var actionName = actionNameArray[i].trim();
				(function (actionName) {
					//记录鼠标是否位于当前控件的flag,用于mouseout事件的计算.即mouseover模式下,菜单展示后每隔一段时间会检查这两个flag,如果都为false则隐藏菜单.
					if (actionName == "mouseover"){
						me._mouseOverOptions = {};
						me.params.target.bind("mouseover", function(){
							me._mouseOverOptions.target = true;
						});
						me.params.target.bind("mouseout", function(){
							me._mouseOverOptions.target = false;
						});
						me.params._ul.bind("mouseover", function(){
							me._mouseOverOptions._ul = true;
						});
						me.params._ul.bind("mouseout", function(){
							me._mouseOverOptions._ul = false;
						});
					}
					
					var action = function(){
						setTimeout(function(){
							if (actionName == "mouseover"){
								flag = false;
								me._checkMouseOut();
							}else{
								flag = me.params._ul.css("display") == "block";
							}
							if (flag){
								me.params._ul.slideUp(0);
							}else{
								me.params._ul.slideDown(0);
								me.params._ul.css( { "top" : 0, "left" : 0 } ).position({
									my: "left top",
									at: "left bottom", 
									of: me.params.target,
									collision: "fit"
								});
								
								setTimeout(function(){
									var uiWidth = 0;
									for (var i = 0; i < me.params._ul.find('a').size(); i ++) {
										var tA = me.params._ul.find('a').get(i);
										var vSize = Math.max(parseInt(0 + $(tA).css('line-height'), 10), 
												parseInt(0 + $(tA).css('height'), 10), 
												parseInt(0 + $(tA).css('font-size'), 10));
										var row = tA.offsetHeight / vSize >> 0;
										if (row > 1) {
											vSize = parseInt(0 + $(tA).css('font-size'), 10) * row + 
													parseInt(0 + $(tA).css('padding-left'), 10) + 
													parseInt(0 + $(tA).css('padding-right'), 10) + 
													parseInt(0 + $(tA).css('border-left-width'), 10) + 
													parseInt(0 + $(tA).css('border-right-width'), 10);
											if (vSize > uiWidth)
												uiWidth = vSize;
										}
									}
									me.params._ul.width(Math.max(me.params._ul.width() || me.params._ul.outerWidth(), 
											uiWidth + parseInt(0 + me.params._ul.find('li').css('padding-left'), 10), 80));
								}, 0);
								//一段时间之后绑定事件,来响应点击页面其它地方关闭菜单.为了防止本身点击的事件对此产生影响
								setTimeout(function(){
									$(document).one("click", function(event){
										if (me.params._ul){
											if (me.params._ul.attr("display") != "none"){
												me.params._ul.slideUp(0); 
											}
										}
									});
								 }, 50);
							}
						}, 50);
					};
					me.params.target.bind(actionName, action);
					me._addBindedEvent(actionName, action);
				})(actionName);
			}
			
			return me;
		},
		

		/**
		 * 
		 * @return
		 */
		_checkMouseOut : function Menu__checkMouseOut(){
			var me = this;
			setTimeout (function(){
				if (me._mouseOverOptions.target == false && me._mouseOverOptions._ul == false){
					me.params._ul.slideUp(0); 
				}else{
					me._checkMouseOut();
				}
			}, 100);
		},
		
		/**
		 * 
		 */
		getAllOptions : function Menu_getAllOptions(getSelected){
			var me = this;
			var selectedOptions = new Array();
			for (var i=0;i<me.params.options.length;i++){
				var opt = me.params.options[i];
				if (!getSelected || opt.params.isSelected){
					selectedOptions.push(opt.params);
				}
			}
			return selectedOptions;
		},
		
		/**
		 * 增加一条已绑定事件的记录
		 */
		_addBindedEvent : function Menu__addBindedEvent(_actionName, _action, _target){
			var me = this;
			if (!_target){
				_target = me.params.target;
			}
			me.params._bindedEvents.push({ "actionName" : _actionName, "action" : _action, "target" : _target });
		}
	};
	
	/**
	 * Menu的单个option对象封装
	 */
	Spark.widgets.MenuOption = function (params){
		var me = this;
		
		if (!params) {
			throw new Error("Argument of MenuOption constructor must not be null.");
		}
		me.params = $.extend({}, me.params, params);
	};
	
	Spark.widgets.MenuOption.prototype = {
		
		params : {	
			/**
			 * 显示的内容
			 */
			txt : null,
			
			/**
			 * 该选项对应的key
			 */
			props : null,
			
			/**
			 * 一个文本或者一个函数.
			 */
			link : null,
			
			/**
			 * 该option所属的menu
			 */
			menu : null,
			
			/**
			 * 该option所对应的a标签
			 */
			aTag : null,
			
			/**
			 * 多选时,是否被选中
			 */
			isSelected : false,
			
			/**
			 * 多选时,是否被初始化为选中
			 */
			defaultSelected : false,
			
			/**
			 * 用于标识该option的key
			 */
			key : null
		},
		
		/**
		 * a标签被点击时所触发的事件.根据link的不同值触发不同操作
		 */
		triggerClick : function MenuOption_triggerClick(){
			var me = this;
			if (me.params.link instanceof Function){
				me.params.isSelected = !me.params.isSelected;
				if (me.params.isSelected){
					me.params.aTag.parent().addClass("option-selected");
				}else {
					me.params.aTag.parent().removeClass("option-selected");
				}
				if (me.params.menu.params._initFinish){
					me.params.link(me.params.aTag, me.params.props, me.params.isSelected);
				}
				if (!me.params.menu.params.multi_select){
					me.params.menu.params._ul.slideUp(0);
				}
			}else{
				setTimeout(function () {location.href = me.params.link;}, 0);
			}
		}
	};
	
	
	Spark.widgets.EditLink = function (params){
		if (!params){
			throw new Error("Argument of EditLink constructor must not be null.");
		}
		
		this.params = $.extend({}, this.params, params);
		this._init();
	};
	
	Spark.widgets.EditLink.prototype = {
		params : {
			/**
			 * 目标元素
			 */
			target : null,
			
			/**
			 * 用于编辑该字段的div
			 */
			editBox : null,
			
			/**
			 * 用于存放value的div
			 */
			valueField : null,
			
			/**
			 * 回调地址/函数
			 */
			link : null,
			
			/**
			 * 装饰器.对于输入框的装饰.比如suggest等
			 */
			decorator : null,
			
			/**
			 * 装饰器.对于输入框装饰的拆卸
			 */
			de_decorator : null,
			
			/**
			 * 样式class.
			 */
			css : null
		},
	
		/**
		 * 链接被点击时,打开悬浮div
		 */
		_init : function EditLink__init(){
			var me = this;
			me.params.target.bind("click", function (){
				var decoratorFlag = me.params.decorator && me.params.decorator instanceof Function;
				
				me.params.editBox = $("<input type='text'>").attr("isAlive", "1");
				if(me.params.css){
					me.params.editBox.addClass(me.params.css);
				} else {
					me.params.editBox.addClass("input-text-short");
				}
				if (decoratorFlag){
					me._decorator_result = me.params.decorator(me.params.editBox);
				}
				
				if (me.params.target.text() != Spark.util.message("listlink.editlink.empty") && me.params.target.text() != Spark.util.message("listlink.select-empty") ){
					me.params.editBox.val(me.params.target.text());
				}
				
				var cancelFunc = function(){
					if (null != me.params.editBox){
						if (me.params.de_decorator && me.params.de_decorator instanceof Function){
							me.params.de_decorator(me._decorator_result, me.params.editBox);
						}
						me.params.editBox.remove().attr("isAlive", "0");
						me.params.editBox = null;
						me.params.target.show();
					}
					$(document).trigger("click");
				};
				var confirmFunc = function(){
					if (me.params.editBox){
						me.params.link(me.params.editBox);
					}
					cancelFunc();
				};
				me.params.target.hide();
				me.params.editBox.insertAfter(me.params.target);
				me.params.editBox.keyup(function(e){
					switch(e.which){
					case 13:
					case 108:
						confirmFunc();
						break;
					case 27:
						cancelFunc();
						break;
					}
				}).focus();
				me.params.editBox.blur(function(){
					confirmFunc();
				})
				setTimeout(function(){
					$(document).one("click", function(){
						confirmFunc();
					});
				}, 50);
			});
		}
	};
	
})();