/**
 * Spark全局Javascript文件.
 * 
 * @author GuoLin
 */

/**
 * Spark顶级名空间.
 * @namespace Spark
 * @type Object
 */
var Spark = Spark || {};

/**
 * Spark常量类名空间.
 * @namespace Spark.constants
 * @type Object
 */
Spark.constants = Spark.constants || {};

/**
 * Spark工具类名空间.
 * @namespace Spark.util
 * @type Object
 */
Spark.util = Spark.util || {};

/**
 * Spark小工具名空间.
 * @namespace Spark
 * @type Object
 */
Spark.widgets = Spark.widgets || {};

/**
 * Spark消息资源名空间.
 * @namespace Spark.messages
 * @type Object
 */
Spark.messages = Spark.messages || {};

/**
 * Spark第三方库名空间.
 * @namespace Spark.thirdparty
 * @type Object
 */
Spark.thirdparty = Spark.thirdparty || {};

/**
 * Spark服务名空间.
 * @namespace Spark.service
 * @type Object
 */
Spark.service = Spark.service || {};

/**
 * Spark页面名空间.
 * @namespace Spark.pages
 * @type Object
 */
Spark.pages = Spark.pages || {};

/**
 * Spark系统配置空间
 * @namespace Spark.configs
 * @type Object
 */
Spark.configs = Spark.configs || {};

(function() {

	/**
	 * 将字符串中的{xx}按照数据模型中的key/value值进行替换.
	 * 
	 * @method Spark.util.substitute
	 * @param text {String} 待替换的字符串
	 * @param obj {Object} 待替换的数据模型
	 * @return {String} 替换后的字符串
	 * @static
	 */
	Spark.util.substitute = function Spark_util_substitute(text, obj) {
		if (!text) {
			return text;
		}
		for (key in obj) {
			text = text.replace(new RegExp("\\{" + key + "\\}", "g"), obj[key]);
		}
		return text;
	};
	
	/**
	 * 获取一个对象的长度.
	 * 
	 * @method Spark.util.sizeof
	 * @param obj {Object} 待检测对象
	 * @return {Number} 如果对象为数组则返回数组大小，否则返回对象上的属性的个数(包括方法)
	 * @static
	 */
	Spark.util.sizeof = function Spark_util_sizeof(obj) {
		if (!obj) {
			return 0;
		}
		else if (obj instanceof Array) {
			return obj.length;
		}
		else {
			var p, size = 0;
			for (p in obj) {
				size++;
			}
			return size;
		}
	};
	
	/**
	 * 取出一个对象的所有属性和其值存入数组.
	 * 
	 * @method Spark.util.makeArray
	 * @param obj {Object} 目标对象
	 * @param sortCallback {Function} 可选，排序函数，若不填写则使用字符串比较
	 * @return {Array} 排序完成的数组，每个元素为包含key和value两个属性的对象
	 * @static
	 */
	Spark.util.makeArray = function Spark_util_makeArray(obj, sortCallback) {
		if (!obj) {
			return [];
		}
		
		var results = [], key;
		for (key in obj) {
			results.push({ key: key, value: obj[key] });
		}
		
		if (sortCallback && sortCallback instanceof Function) {
			results.sort(sortCallback);
		}
		else {
			results.sort(function(l, r) {
				var lkey = l.key.toString();
				var rkey = r.key.toString();
				return lkey == rkey ? 0 : lkey > rkey ? 1 : -1; 
			});
		}
		
		return results;
	};
	
	/**
	 * 将一个数组转化为一个map, 其中key为每个数组元素的指定属性, value为数组元素.
	 * 
	 * @method Spark.util.makeMap
	 * @param array {Array} 数组
	 * @param propertyName {string} 数组元素属性名称
	 * @return {Object} 转化完成后的map
	 * @static
	 */
	Spark.util.makeMap = function Spark_util_makeMap(array, propertyName) {
		var i, n, item, map = {};
		for (i = 0, n = array.length; i < n; i++) {
			item = array[i];
			map[item[propertyName]] = item;
		}
		return map;
	};
	
	/**
	 * 将一个元素的背景色变为高亮然后再变回来.
	 * @param {HTMLElement | jQuery} el 待高亮的元素
	 * @param {string} targetColor 目标高亮颜色，默认为略黄的白色
	 * @param {number} speed 变化速度，越大变化的越慢，默认为500
	 * @static
	 */
	Spark.util.light = function Spark_util_light(el, targetColor, speed) {
		el = $(el);
		targetColor = targetColor || "#fffff5";
		speed = speed || 400;
		
		var originalColor = el.css("backgroundColor");
		el.animate({ backgroundColor: targetColor }, speed);
		setTimeout(function() {
			el.animate({ backgroundColor: originalColor }, speed)
		}, 300);
	}
	
	/**
	 * 国际化方法.
	 * 
	 * @method Spark.util.message
	 * @param code {String} 国际化标识
	 * @param arguments {Array} 国际化参数(隐含可变参数)
	 * @return {String} 国际化完成后的字符串
	 * @static
	 */
	Spark.util.message = function Spark_util_message(code) {
		if (!Spark.messages) {
			return "";
		}
		var tokens = Array.prototype.slice.call(arguments).slice(1);
		var message = Spark.messages[code];
		var result = Spark.util.substitute(message, tokens);
		return result || "";
	};
	
	/**
	 * 格式化日期.
	 * 
	 * @method Spark.util.formateDate
	 * @param date {Date | Number} 日期对象或是毫秒数
	 * @param formatType {String} 格式类型(注意：不是格式字符串)，可选格式类型见Spark.util.formatDate.formats
	 * @return 格式化完成后的字符串
	 * @static
	 */
	Spark.util.formatDate = function Spark_util_formatDate(date, formatType) {
		var d = !isNaN(date) ? new Date(date) : ((date instanceof Date) ? date : new Date());
		var formatCode = arguments.callee.formats[formatType];
		if (!formatCode) {
			formatCode = arguments.callee.formats["default"];
		}
		var format = Spark.util.message(formatCode);
		return Spark.thirdparty._formatDate(d, format);
	};
	
	/**
	 * 格式化日期.
	 * 
	 * @method Spark.util.formateDate
	 * @param date {Date | Number} 日期对象或是毫秒数
	 * @param formatType {String} 格式类型(注意：不是格式字符串)，可选格式类型见Spark.util.formatDate.formats
	 * @return 格式化完成后的字符串
	 * @static
	 */
	Spark.util.formatDate.today = function Spark_util_formatDate_today(formatType) {
		return Spark.util.formatDate(new Date(), formatType);
	};
	
	Spark.util.formatDate.formats = {
		"default": "format.date.default",
		"shortdate": "format.date.short-date",
		"longdate": "format.date.long-date",
		"shorttime": "format.date.short-time",
		"longtime": "format.date.long-time",
		"shortdatetime": "format.date.short-datetime",
		"longdatetime": "format.date.long-datetime",
		"longtime-minute": "format.date.long-time-minute",
		"longdatetime-minute": "format.date.long-datetime-minute"
	};
	
	/**
	 * 格式化时间对象到'时间间隔'格式.
	 * 
	 * @method Spark.util.formatDateInterval
	 * @param date {Date | Number} 日期对象或是毫秒数
	 * @return 格式化完成的日期字符串
	 * @static
	 */
	Spark.util.formatDateInterval = function Spark_util_formatDateInterval(date) {
		var d = (date instanceof Date) ? date : !isNaN(date) ? new Date(parseInt(date)) : new Date(),
			now = new Date(), intervalDays, interval,
			msg = Spark.util.message, formatDate = Spark.util.formatDate;
		
		// 获取自然天数差异的函数
		var getIntervalDays = function(before, after) {
			var b = new Date(before.getTime()), a = new Date(after.getTime());
			b.setHours(0);
			b.setMinutes(0);
			b.setSeconds(0);
			b.setMilliseconds(0);
			a.setHours(0);
			a.setMinutes(0);
			a.setSeconds(0);
			a.setMilliseconds(0);
			return (a.getTime() - b.getTime()) / 86400000;
		};
		intervalDays = getIntervalDays(d, now);
		
		// 同一个自然天
		if (intervalDays === 0) {
			interval = now.getTime() - d.getTime();
			
			// 小于一秒钟
			if (interval < 1000) {
				return msg("tag.diffDateTag.ago.second", 1);
			}
			// 小于一分钟
			else if (interval < 60000) {
				return msg("tag.diffDateTag.ago.second", (interval / 1000) >> 0);
			}
			// 小于一小时
			else if (interval < 3600000) {
				return msg("tag.diffDateTag.ago.minute", (interval / 60000) >> 0);
			}
			// 否则必然在一个自然天内且大于一小时
			else {
				return msg("tag.diffDateTag.ago.today", formatDate(d, "longtime-minute"));
			}
		}
		// 前一个自然天
		else if (intervalDays === 1) {
			return msg("tag.diffDateTag.ago.yesterday", formatDate(d, "longtime-minute"));
		}
		// 超过两个自然天
		else {
			return formatDate(d, "longdatetime-minute");
		}
	};
	
	/**
	 * 对不安全的字符串进行HTML编码.
	 * 
	 * @method Spark.uilt.encodeHTML
	 * @param text {string} 待编码的字符串
	 * @return 安全的HTML字符串
	 * @static
	 */
	Spark.util.encodeHTML = function Spark_util_encodeHTML(text) {
		if (text === null || typeof text == "undefined" || !text.length) {
			return "";
		}
		
		if ($.browser.msie) {
			text = "" + text;
			return text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
		}
		else {
			var me = arguments.callee;
			me.text.data = text;
			return me.div.innerHTML;
		}
	};
	Spark.util.encodeHTML.div = document.createElement("div");
	Spark.util.encodeHTML.text = document.createTextNode("");
	Spark.util.encodeHTML.div.appendChild(Spark.util.encodeHTML.text);

	/**
	 * 对HTML进行解码.
	 * 
	 * @method Spark.util.decodeHTML
	 * @param html {string} 待解码的HTML字符串
	 * @return 解码后的文本
	 * @static
	 */
	Spark.util.decodeHTML = function Spark_util_decodeHTML(html) {
		if (html === null || typeof html == "undefined" || !html.length) {
			return "";
		}
		return html.split("&lt;").join("<").split("&gt;").join(">").split("&amp;").join("&");
	};

	/**
	 * 对于cookie的操作封装
	 * 支持 set get del三种操作
	 */
	Spark.util.Cookie = {
		
		/**
		 * 保存一条cookie信息.如果已经存同名的cookie,则覆盖.
		 * @param name {String} cookie名 
		 * @param value {String} cookie值.将在encode之后进行保存
		 * @param expDate {Date} 可选值.标示此cookie将会在此date时间失效.如果不填,默认为30天失效.
		 * @param path {string} Cookie存储路径
		 * @return
		 */
		set : function Spark_util_Cookie_set(name, value, expDate, path) { 
			var cookie;
			if(expDate){
				expDate = !(expDate instanceof Date) ? new Date(expDate) : expDate;
			}else{
		    	expDate = new Date(((new Date()).getTime() + 30 * 24 * 60 * 60 * 1000));
			}
		    cookie = name + "="+ encodeURIComponent(value) + ";expires=" + expDate.toGMTString();
		    if (path) {
		    	cookie += ";path=" + path;
		    }
		    document.cookie = cookie;
		},
		
		
		/**
		 * 读取一条cookie信息.如果不存在此cookie,则返回null
		 * @param name {String} cookie名称
		 * @return 返回该cookie的值.如果未找到此cookie,则返回null
		 */
		get : function Spark_util_Cookie_get(name){ 
			var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)")); 
		    return arr ? unescape(arr[2]) : null;
		},
		
		/**
		 * 删除一条cookie信息.如果未找到此信息,则啥都不干.
		 * @param name {String} cookie名称
		 * @return
		 */
		del : function Spark_util_Cookie_del(name) { 
		    var cval = this.get(name); 
		    if (cval != null) {
		    	var expDate = new Date(); 
		    	expDate.setTime(expDate.getTime() - 1); 
		    	document.cookie= name + "=" + cval + ";expires=" + expDate.toGMTString();
		    }
		},
		
		/**
		 * 保存一条cookie信息到指定目录.如果已经存同名的cookie,则覆盖.
		 * @param name {String} cookie名 
		 * @param value {String} cookie值.将在encode之后进行保存
		 * @param path {String} cookie路径.
		 * @param difDate {Date} 可选值.标示此cookie将会在隔了difDate之后时间失效.如果不填,默认为30天失效.
		 * @return
		 */
		setToPath: function Spark_util_Cookie_setToPath(name, value, path, difDate){ 
			var Days = difDate ? difDate :30;
			var expDate  = new Date();
			expDate.setTime(expDate.getTime() + Days*24*60*60*1000);
		    document.cookie = name + "="+ encodeURIComponent(value) + ";path=" + path +";expires=" + expDate.toGMTString();
		} 
		
	};
	
	/**
	 * Http Response的国际化函数.
	 * 
	 * @param status {Number} 国际化代码，4xx或5xx
	 * @return 格式化之后的Http Response国际化函数
	 */
	Spark.util.formatResponseStatus = function Spark_util_formatResponseStatus(status){
		switch(status){
			case  404:
				return Spark.util.message("response.status.404") ;
				break;
			case  500:
				return Spark.util.message("response.status.500") ;
				break;
			case  403:
				return Spark.util.message("response.status.403") ;
				break;
			case  401:
				return Spark.util.message("response.status.401") ;
				break;
			case  502:
				return Spark.util.message("response.status.502") ;
				break;
			case  501:
				return Spark.util.message("response.status.501") ;
				break;
			case  400:
				return Spark.util.message("response.status.400") ;
				break;
			default:
				return Spark.util.message("response.status.default") ;
		}
	};
	
	/**
	 * Ajax处理工具.
	 * 它被用于Jquery.ajax的error函数中.
	 * 如果传了第二个参数：Function类型的callback,则按照自定义回调的方式处理异常；
	 * 否则，则弹出一个Jquery风格的Alert窗口，显示国际化了的ResponseStatus.
	 * 
	 * @param x {Object} 它是一个XmlHttpRequest，即Jquery.ajax的error函数的第一个参数。
	 * @param callback {Function} 回调函数，可以为空。用于自定义处理error。
	 * 					该回调函数有三个参数：第一个为responseStatusCode，即4xx或5xx；
	 * 					第而个为responseStatus的国际化信息；
	 * 					第三个为ResponseText，即返回的HTML信息。
	 */
	Spark.util.handleAjaxError = function Spark_util_handleAjaxError(x, callback){ 
		if(!callback || !(callback instanceof Function)){ 
			Spark.widgets.Alert.responseStatusAlert(x.status);
		}else{
			callback.call(this, x.status, Spark.util.formatResponseStatus(x.status), x.responseText);
		}
	};
	
	
	/**
	 * 第三方日期格式化函数.
	 * 
	 * @method Spark.thirdparty._formatDate
	 * @param date {Date} 日期对象
	 * @param format {String} 格式字符串
	 * @return 格式化之后的日期字符串
	 * @private
	 */
	Spark.thirdparty._formatDate = function Spark_thirdparty__formatDate(date, format) {
		var str = format;
		var msg = Spark.util.message;
		var week = [ msg("format.date.week.sunday"), 
			msg("format.date.week.monday"),
			msg("format.date.week.tuesday"),
			msg("format.date.week.wednesday"),
			msg("format.date.week.thursday"),
			msg("format.date.week.friday"),
			msg("format.date.week.saturday") ];

		str = str.replace(/yyyy|YYYY/, date.getFullYear());
		str = str.replace(/yy|YY/, (date.getYear() % 100) > 9 ? (date.getYear() % 100).toString() : '0' + (date.getYear() % 100));

		str = str.replace(/MM/, date.getMonth() >= 9 ? (date.getMonth() + 1).toString() : '0' + (date.getMonth() + 1));
		str = str.replace(/M/g, date.getMonth());

		str = str.replace(/w|W/g, week[date.getDay()]);

		str = str.replace(/dd|DD/, date.getDate() > 9 ? date.getDate().toString() : '0' + date.getDate());
		str = str.replace(/d|D/g, date.getDate());

		str = str.replace(/hh|HH/, date.getHours() > 9 ? date.getHours().toString() : '0' + date.getHours());
		str = str.replace(/h|H/g, date.getHours());
		str = str.replace(/mm/, date.getMinutes() > 9 ? date.getMinutes().toString() : '0' + date.getMinutes());
		str = str.replace(/m/g, date.getMinutes());

		str = str.replace(/ss|SS/, date.getSeconds() > 9 ? date.getSeconds().toString() : '0' + date.getSeconds());
		str = str.replace(/s|S/g, date.getSeconds());

		return str;
	};

	/**
	 * 删除一个suggest
	 * @param input 文本框控件(jquery对象)
	 */
	Spark.thirdparty.destroySuggest = function Spark_thirdparty_destroySuggest(input) {
		input.unautocomplete();
	};
	
	/**
	 * 为一个input设置user的Suggest
	 * @param {jQuery} input 文本框控件(jquery对象)
	 * @param {Function} callback 后台返回数据行时,对此进行的页面回调操作
	 * @param {Object} options 允许用户复写默认参数
	 */
	Spark.thirdparty.userSuggest = function Spark_thirdparty_userSuggest(input, callback, options) {
		var defaultOptions = { 
			max: 30, //列表里的条目数 
			minChars: 1, //自动完成激活之前填入的最小字符 
			width: 200, //提示的宽度，溢出隐藏 
			scrollHeight: 300, //提示的高度，溢出显示滚动条 
			matchContains: false, //包含匹配，就是data参数里的数据，是否只要包含文本框里的数据就显示 
			autoFill: false, //自动填充 
			dataType: "json",
			delay: 500,
			cacheLength: 0,
			parse: function(data) {
				return $.map(data, function(row) {
					var username = row.username || "",
						value = {
							name: row.name || "",
							email: row.email || "",
							username: username,
							id: row.id
						};
					return {
						data: value,
						value: value,
						result: username
					};
				});
			},
			formatItem: function(row, i, max) { 
				return row.name + "," + row.username; 
			}
		};
		options = $.extend({}, defaultOptions, options || {});
		return input.autocomplete(Spark.constants.CONTEXT+"/ajax/users/autocomplete/false", options)
			.result(function(event, row, formatted) {
				if (callback && callback instanceof Function){
					callback(row);
				}
			});
	};
	
	/**
	 * 获取URL中的所有querystring参数和bookmark参数.
	 * 
	 * @method Spark.util.getParametersAndBookmarks
	 * @param url {String} URL地址
	 * @return {Object} 映射Map，key为参数名称，value为参数值
	 * @static
	 */
	Spark.util.getParametersAndBookmarks = function Spark_util_getParametersAndBookmarks(url) {
		if (!url) {
			return url;
		}
		
		if ((typeof url !== "string") && !(url instanceof String)) {
			url = url.toString();
		}

		var parameters = "", bookmarks = "", size = url.length,
			ps = url.indexOf("?"), bs = url.indexOf("#");
		if (ps < 0) {
			ps = size;
		}
		if (bs < 0) {
			bs = size;
		}

		// 仅parameter的情况
		if (ps == size && bs == size && url.indexOf("=") >= 0) {
			parameters = url;
		}
		// 不包含parameter和bookmark的情况
		else if (ps == size && bs == size) {
			return {};
		}
		// 可能包含parameter和bookmark的情况
		else {
			parameters = url.substring(ps + 1, (ps > bs ? size : bs));
			bookmarks = url.substring(bs + 1, (ps > bs ? ps : size));
		}
		
		var pair, i, key, value, results = {};
		var pairs = (parameters ? parameters.split("&") : []).concat(bookmarks ? bookmarks.split("&") : []);
		for (i = 0; i < pairs.length; i++) {
			pair = pairs[i].split("=");
			key = pair[0];
			value = pair[1];
			results[key] = value;
		}
		return results;
	}
	
	/**
	 * 显示tinyMCE富文本编辑器
	 * 
	 * @method Spark.thirdparty.tinyMCE
	 * @param elementId {String} 需要作为tinyMCE的容器的元素ID
	 * @static
	 */
	Spark.thirdparty.tinyMCE = function Spark_thirdparty_tinyMCE(elementId){
		tinyMCE.init({
			// General options
			mode : "exact",
			theme : "advanced",
			skin : "o2k7",
			skin_variant : "silver",
			elements : elementId,
			
			plugins : "table,advhr,advimage,advlink,emotions,preview,media,contextmenu,paste,fullscreen,advlist,autosave,upload,autoresize,appletpaste",

			theme_advanced_buttons1: "formatselect,bold,italic,underline,strikethrough,forecolor,backcolor,separator," +
	                "bullist,numlist,outdent,indent,separator," +
	                "table,row_before,row_after,delete_row,col_before,col_after,delete_col,pasteword,separator," +
	                "undo,redo,separator," +
	                "link,unlink,upload,image,media,code,emotions,separator,preview,fullscreen,separator,restoredraft",
	        theme_advanced_buttons2: "fontselect,fontsizeselect,hr,charmap,anchor,seperator," +
	        		"delete_table,row_props,cell_props,split_cells,merge_cells,seperator, " +
	        		"cleanup,paste,pastetext,removeformat",
	        theme_advanced_buttons3:"",
	        
	        
//	        plugins : "pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,wordcount,advlist,autosave,upload",
//
//			theme_advanced_buttons1 : "save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,styleselect,formatselect,fontselect,fontsizeselect,upload",
//			theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor",
//			theme_advanced_buttons3 : "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen",
//			theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,pagebreak,restoredraft",
	        
			theme_advanced_toolbar_location : "top",
			theme_advanced_toolbar_align : "left",
			theme_advanced_statusbar_location : "bottom",
			theme_advanced_resizing : false,

			//
			convert_urls: false,
			appletpaste_base : Spark.configs.pictureServer,
			
			// Example content CSS (should be your site CSS)
			// content_css : "css/content.css",

			// Drop lists for link/image/media/template dialogs
			template_external_list_url : "lists/template_list.js",
			external_link_list_url : "lists/link_list.js",
			external_image_list_url : "lists/image_list.js",
			media_external_list_url : "lists/media_list.js",
			autosave_ask_before_unload: false, //把autosave的弹框禁了==
			language: Spark.constants.LOCALE.substring(0, 2) || 'zh'
		});
	};
})();

(function($){
	
	// 如果没有jQuery.fn.dialog则不实例化此类
 	if (!$ || !$.fn.dialog) {
 		return;
 	}
		 	
	/**
	 * Alert组件.
	 * 使用Jquery风格的Alert弹出框，它可以代替js默认的alert.
	 */
	Spark.widgets.Alert = {
			
		/**
		 *  
		 * 弹出alert组件.
		 * @method alert
		 * @param msg {Object} 内容
		 * @param title {String} 标题
		 */			
		alert : function Spark_widgets_Alert_alert(msg,title,callback){  
			if(title){
				$("#spark-alert-dialog" ).parent().find("span.ui-dialog-title").html(title);
			} 
			if(msg){
				$("#spark-alert-dialog-content" ).html(msg);
			} 
			if(callback){ 
				$( "#spark-alert-dialog" ).bind( "dialogclose", callback);
			}
			//$("#spark-alert-dialog" ).parent().find("button.ui-button").focus();
			$("#spark-alert-dialog" ).dialog("open");
		},
		
		/**
		 *  
		 * 弹出alert组件.
		 * @method alert
		 * @param msg {Object} 内容
		 * @param title {String} 标题
		 */			
		responseStatusAlert : function Spark_widgets_Alert_responseStatusAlert(status,title,callback){   
			if(title){
				$("#spark-alert-dialog" ).parent().find("span.ui-dialog-title").html(title);
			} 
			$("#spark-alert-dialog-content" ).html(Spark.util.formatResponseStatus(status));
			if(callback){ 
				$( "#spark-alert-dialog" ).bind( "dialogclose", callback);
			}
			//$("#spark-alert-dialog" ).parent().find("button.ui-button").focus();
			$("#spark-alert-dialog" ).dialog("open");
		},
			
 		
		/**
		 * Alert组件初始化
		 * @return
		 */
		 _initUI : function Spark_widgets_Alert__initUI(){ 
			var me =this;
			var _buttonsAlert = {};
			_buttonsAlert[Spark.util.message("button.ok")]=function(){
				$(this).dialog('close');
				$("#spark-alert-dialog" ).parent().find("span.ui-dialog-title").html(Spark.util.message("alert.defaultmsg"));
				$("#spark-alert-dialog-content").html(Spark.util.message("alert.defaultmsg"));
				$( "#spark-alert-dialog" ).unbind( "dialogclose");
				return false;
			};
			$("#spark-alert-dialog").dialog({
				autoOpen: false,
				resizable: false,
				bgiframe :true,
				modal: true,
				buttons: _buttonsAlert
			});
		}
	};
 	$(function(){
		Spark.widgets.Alert._initUI();
 	});
})(jQuery);

/**
 * 合并过来的一些工具.
 */
(function () {
	
	var i18n = Spark.util.message, util = Spark.util;
	
	// 获取IE版本.
	Spark.util.ieVersion = function Spark_util_ieVersion() {
		if ($.browser.msie) {
			var agent = navigator.userAgent.toLowerCase();
			if (agent.indexOf('msie 6') != -1)
				return 6;
			if (agent.indexOf('msie 7') != -1)
				return 7;
			if (agent.indexOf('msie 8') != -1)
				return 8;
		} 
		return 0;
	};
	
	/** 获取一个页面元素的页面绝对坐标. */
	Spark.util.offset = function Spark_util_offset(e) {
		if (!e)
			return [0, 0];
		var x = e.offsetLeft, y = e.offsetTop;
		while (e = e.offsetParent)
			x += e.offsetLeft, y += e.offsetTop;
		return [x, y];
	};
	
	/** 获取一屏的高度. */
	Spark.util.getVHeight = function Spark_util_getVHeight() {
		var height = self.innerHeight, mode = document.compatMode;
		if((mode || $.browser.msie) && !$.browser.opera)
			height = (mode == 'CSS1Compat' ? document.documentElement.clientHeight : document.body.clientHeight);
		return height;
	};
	
	/** 获取一屏的宽度. */
	Spark.util.getVWidth = function Spark_util_getVWidth() {
		var width = self.innerWidth, mode = document.compatMode;
		if((mode || $.browser.msie) && !$.browser.opera)
			width = (mode == 'CSS1Compat' ? document.documentElement.clientWidth : document.body.clientWidth);
		return width;
	};
	
	/** 整屏遮罩, 参数为透明度 */
	Spark.util.lock = function Spark_util_lock(alphi) {
		var bgLayer = document.getElementById('Spark_util_lock_bgLayer');
		if (!bgLayer) {
			bgLayer = document.createElement('div');
			bgLayer.setAttribute('id', 'Spark_util_lock_bgLayer');
			bgLayer.style.opacity = alphi ? alphi : '0.4';
			bgLayer.style.filter = 'alpha(opacity=' + (alphi ? alphi * 100 : 40) + ')';
			bgLayer.style.height = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight, Spark.util.getVHeight()) + 'px';
			document.body.appendChild(bgLayer);
		}
		bgLayer.style.display = 'block';
		var sels = document.getElementsByTagName('select');
		for (var i = 0, n = sels.length; i < n; i ++)
			sels[i].style.visibility = 'hidden';
	};
	
	/** 取消整屏遮罩 */
	Spark.util.unLock = function Spark_util_unLock() {
		document.getElementById('Spark_util_lock_bgLayer').style.display = 'none';
		var sels = document.getElementsByTagName('select');
		for (var i = 0, n = sels.length; i < n; i ++)
			sels[i].style.visibility = 'visible';
	};
	
	Spark.widgets.Load = function Spark_widgets_Load() {
		this.box = document.createElement('div');
		this.label = document.createElement('span');
		this.label.innerHTML = i18n("load.default.message");
	};
	Spark.widgets.Load.prototype = {
		lock: function () {
			util.lock();
		},
		unlock: function () {
			util.unLock();
		},
		setMessage: function (t) {
			this.label.innerHTML = t;
		},
		showLoading: function (msg) {
			this.lock();
			this.box = document.createElement('div');
			this.box.className = 'spark-loading';
			document.body.appendChild(this.box);
			
			msg && (this.label.innerHTML = msg);
			this.box.appendChild(this.label);
			
			this.box.style.left = (Spark.util.getVWidth() - this.box.offsetWidth >> 1) + 'px';
			this.box.style.top = (Spark.util.getVHeight() - this.box.offsetHeight >> 1) + 'px';
		},
		hideLoading: function () {
			this.unlock();
			if (this.box && this.box.parentNode)
				this.box.parentNode.removeChild(this.box);
			this.box = null;
		}
	};
	/** @descript: 小的load图标. */
	
	Spark.util.Load = {
		path: '/css/cupertino/images/ui-anim_basic_16x16.gif',
		loads: {},
		length: 0,
		init: function Spark_util_Load_init (path) {
			var img = new Image();
			this.path = (path || '../../..') + this.path;
			img.src = this.path;
		},
		
		/** @descript: 设置图片地址. */
		setIcon: function Spark_util_Load_setIcon(path) {
			this.path = path;
		},
		
		// 添加一个id到load显示处理列表中.
		add: function Spark_util_Load_add(e) {
			var curElem = document.getElementById(e);
			if (!curElem){
				return;
			}
			
			this.loads[e] = curElem;
			
			var loadIcon = document.createElement('img');
			loadIcon.id = e + Spark.util.Load.LOAD_ID;
			loadIcon.src = this.path;
			curElem.appendChild(loadIcon);
			
			this.length ++;
		},
		// 移除一个id到load显示处理列表中.
		remove: function Spark_util_Load_remove(e) {
			if (e && this.loads[e]) {
				var loadIcon = document.getElementById(e + Spark.util.Load.LOAD_ID);
				loadIcon.parentNode.removeChild(loadIcon);
				delete this.loads[e];
				this.length --;
			}
		}
	};
	
	Spark.util.Load.LOAD_ID = '_spark_load_icon';
	
	Spark.util.getRadioValueByName = function Spark_util_getRadioValueByName(radioName){
		var radios = document.getElementsByName(radioName);
		for (var i=0;i<radios.length;i++){
			if (true == radios[i].checked){
				return radios[i].value;
			}
		}
	};
	
	$(function(){
		Spark.util.Load.init(Spark.constants.CONTEXT);
	});
})();

/**
 * 设置某个控件为focus.(为了兼容各个浏览器,采用延时30ms设置focus)
 */
function foc(elem){
	setTimeout(function(){
		elem.focus();
		elem.val(elem.val());
	}, 30);
}

/**
 * 字符串对象的trim方法定义
 * @return trim后的字符串
 */
String.prototype.trim=function(){
	return this.replace(/(^\s*)|(\s*$)/g, "");
};

/**
 * 返回一个列表对象中是否包含了指定的item.
 * 
 */
//TODO 修改函数所在的包.
Spark.has = function Spark_has(array, item){
	var i;
	for (i=0; i<array.length; i++){
		if (array[i] == item){
			return true;
		}
	}
	return false;
};

jQuery.fn.colorbox = function(options) {
	var settings = jQuery.extend({
		change: function(color, orgColor) {},
		move: function(color) { return true; }
	}, options);
	
	return this.each(function() {
		var me = $(this);
		var orgColor = me.css("background-color");
		me.ColorPicker({
			color: orgColor,
			onShow: function(cp) {
				$(cp).fadeIn(500); 
				return false;
			},
			onHide: function(cp) {
				$(cp).fadeOut(500);
				return false; 
			},
			onSubmit : function (hsb, hex, rgb){
				settings.change(me, hex, orgColor);
				me.ColorPickerHide();
				return false; 
			},
			onChange: function(hsb, hex, rgb) {
				if (!settings.move.call(me, hex)) {
					return;
				}
			}
		});	
	});
};
