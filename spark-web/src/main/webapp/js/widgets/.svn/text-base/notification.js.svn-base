/**
 * 通知类.
 * 
 * @class Notification
 * @author GuoLin
 */
(function($) {
	
	// 别名
	var message = Spark.util.message;
	var light = Spark.util.light;
	var sizeof = Spark.util.sizeof;
	var makeMap = Spark.util.makeMap;
	var Cookie = Spark.util.Cookie;

	/**
	 * 构造器.
	 * @constructor
	 */
	Spark.widgets.Notification = function(options) {
		var buttons, name, callback, me = this;
		
		this.receivers = {};
		
		// 装配options
		this.options.title = message("notification.mail.dialog.title");
		options = $.extend({}, this.options, options || {});
		this.options = options;
		
		// 创造dialog
		this.dialog = this._generateDialog();
		this.dialog.el.dialog({
			modal: true,
			width: 600,
			height: 450,
			title: options.title,
			autoOpen: false,
			open: function(event, ui) {
				me.dialog.elInput.focus();
			},
			buttons: this._rebuildButtonCallbacks(options.buttons)
		});
		return this;
	};
	
	Spark.widgets.Notification.prototype = {
		
		options: {
			
			/**
			 * 对话框顶部标题.
			 * @property title
			 * @type string
			 */
			title: "",
			
			/**
			 * 自动提示功能的最少字数.
			 * @property minLength
			 * @type number
			 * @default 3
			 */
			minLength: 3,

			/**
			 * 对话框按钮Map，其中key为按钮值，value为按钮对应的函数.
			 * @property buttons
			 * @type Object
			 */
			buttons: null,
			
			/**
			 * 空间别名.
			 * <p>
			 * 如果不填写则常用联系人将为全局，否则为与空间相关.
			 * </p>
			 * @property prefixCode
			 * @type string
			 */
			prefixCode: null
			
		},
		
		/**
		 * 对话框中的元素集合.
		 * @property dialog
		 * @type Object
		 */
		dialog: {
		
			/**
			 * 对话框顶级元素.
			 * @property el
			 * @type HTMLElement
			 */
			el: null,
			
			/**
			 * 对话框用户选择元素.
			 * @property elChooseArea
			 * @type HTMLElement
			 */
			elChooseArea: null,

			/**
			 * 对话框用户输入框元素.
			 * @property elInput
			 * @type HTMLElement
			 */
			elInput: null,

			/**
			 * 对话框常用联系人元素.
			 * @property elFrequenters
			 * @type HTMLElement
			 */
			elFrequenters: null,
			
			/**
			 * 对话框已选择用户区域元素.
			 * @property elReceiversArea
			 * @type HTMLElement
			 */
			elReceiversArea: null,

			/**
			 * 对话框已选择用户列表元素.
			 * @property elReceivers
			 * @type HTMLElement
			 */
			elReceivers: null,
			
			/**
			 * 对话框用户所填文本信息元素
			 * @property elCustomMessage
			 * @type HTMLElement
			 */
			elCustomMessage: null
		},
		
		/**
		 * 已加入的收件人Map，其中key为email，value为此用户对应的li元素.
		 * @property receivers
		 * @type Object
		 */
		receivers: null,
		
		/**
		 * 显示通知对话框.
		 * @method show
		 */
		show: function() {
			this.dialog.el.dialog("open");
		},
		
		/**
		 * 添加一个用户.
		 * @method addReceivers
		 * @param {Object} receiver 用户对象，必须包含email和name两个属性
		 */
		addReceiver: function(receiver) {
			var el, email = receiver.email, receivers = this.receivers, 
				elReceiversArea = this.dialog.elReceiversArea, elReceivers = this.dialog.elReceivers, 
				me = this;
			
			// 清空输入框
			this.dialog.elInput.val("");

			// 如果已经存在则不添加
			if (receivers[email]) {
				light(receivers[email].el, "#ff0000");
				return;
			}
			
			/*// 如果第一个添加
			if (!sizeof(receivers)) {
				elReceiversArea.show();
			}*/
			
			// 添加用户列表元素
			el = $("<li>").appendTo(elReceivers);
			$("<span>").text(receiver.name).appendTo(el);
			$("<a>").attr("href", "javascript:void(0)").addClass("delete").click(function() {
				me.removeReceiver(receiver);
			}).appendTo(el);
			
			// 添加到用户列表
			receivers[email] = { el: el, receiver: receiver };
		},
		
		/**
		 * 移除用户.
		 * @method removeReceiver
		 * @param {Object} receiver 用户对象，必须包含email属性
		 */
		removeReceiver: function(receiver) {
			var email = receiver.email;
			this.receivers[email].el.remove();
			delete this.receivers[email];
			
			/*// 如果一个不剩
			if (!sizeof(this.receivers)) {
				this.dialog.elReceiversArea.hide();
			}*/
		},
		
		/**
		 * 添加常用联系人.
		 * @method storeFrequenters
		 * @param receivers {Array} 常用联系人数组
		 */
		addFrequenters: function(receivers) {
			var i, n, receiver,
				frequenter, frequenters = this.retriveFrequenters(),
				frequenterMap = makeMap(frequenters, "email"), newFrequenters = [];
			
			// 遍历待加入的联系人，与现有联系人合并，LRU算法
			for (i = 0, n = receivers.length; i < n; i++) {
				receiver = receivers[i];
				
				// 如果已存在，增加times
				frequenter = frequenterMap[receiver.email];
				if (frequenter) {
					frequenter.times++;
					frequenter.name = receiver.name;  // 以防名字被更新
				}
				// 否则增加一个用户
				else {
					if (frequenters.length + newFrequenters.length >= 8) {  // 仅保存前8个常用联系人
						if (frequenters.length > 0) {
							frequenters.pop();
						} else {
							newFrequenters.pop();
						}
					}
					newFrequenters.push({
						name: receiver.name,
						email: receiver.email,
						times: 1
					});
				}
			}
			
			this._storeFrequenters(frequenters.concat(newFrequenters));
		},
		
		
		/**
		 * 获取常用联系人.
		 * @method retriveFrequenters
		 * @return {Array} 常用联系人列表，按照最常用到最不常用排序
		 */
		retriveFrequenters: function() {
			var frequenters = [], i, n, 
				cookie = Cookie.get("frequenters"), tokens, token;
			if (!cookie) {
				return [];
			}
			tokens = decodeURIComponent(cookie).split(",");
			for (i = 0, n = tokens.length; i < n; i++) {
				token = tokens[i].split(":");
				frequenters.push({
					name: token[0],
					email: token[1],
					times: token[2]
				});
			}
			return frequenters.sort(function(l, r) {
				return r.times - l.times;
			});
		},
		
		/**
		 * 保存常用联系人.
		 * @method _storeFrequenters
		 * @param frequenters {Array} 常用联系人
		 * @private
		 */
		_storeFrequenters: function(frequenters) {
			var i, n, frequenter, values = [], expiration, path, 
				prefixCode = this.options.prefixCode;
			for (i = 0, n = frequenters.length; i < n; i++) {
				frequenter = frequenters[i];
				values.push(encodeURIComponent(frequenter.name + ":" + frequenter.email + ":" + frequenter.times));
			}
			expiration = new Date((new Date()).getTime() + 31536000 * 1000);  // 过期时间一年
			path = prefixCode ? (Spark.constants.CONTEXT + "/spaces/" + prefixCode + "/") : "/";
			Cookie.set("frequenters", values.join(","), expiration, path);
		},
		
		/**
		 * 生成初始化对话框.
		 * @method _generateDialog
		 * @return {Object} 生成后的对话框各元素集合
		 * @see dialog属性
		 * @private
		 */
		_generateDialog: function() {
			var elDialog, elChooseArea, elInput, elReceiversArea, elReceivers, elFrequenters, 
				frequenters, frequenter, elCustomMessage, customMessage, i, n, me = this;
			
			// 生成容器
			elDialog = $("<div>").addClass("notification-dialog");
			
			//右侧容器
			elDialogRight = $("<div>").addClass("right-part").addClass("right").appendTo( elDialog );
			
			// 常用联系人区
			elFrequenterArea = $("<div>").addClass("frequenter-area").appendTo(elDialogRight);
			frequenters = this.retriveFrequenters();
			$("<h3>").text(message("notification.mail.dialog.frequenters")).appendTo(elFrequenterArea);
			if (frequenters.length > 0) {
				elFrequenters = $("<ul>").addClass("frequenters").appendTo(elFrequenterArea);
				for (i = 0, n = frequenters.length; i < n; i++) {
					frequenter = frequenters[i];
					$("<li>").text(frequenter.name).appendTo(elFrequenters).click((function(receiver) {
						return function() {
							me.addReceiver(receiver);
						};
					})(frequenter));
				}
			}
			$("<div>").text(message("notification.mail.dialog.nofrequenters"))
				.addClass("weak frequenter-message").appendTo(elFrequenterArea);
			
			//左侧容器
			elDialogLeft = $("<div>").addClass("left-part").appendTo( elDialog );
			
			// Autocomplete输入区
			elChooseArea = $("<div>").addClass("choose-area").appendTo(elDialogLeft);
			$("<span>").text(message("notification.mail.dialog.input")).appendTo(elChooseArea);
			elInput = $('<input type="text">').addClass("contact").appendTo(elChooseArea);
			this._bindAutocomplete(elInput);
			
			// 已添加用户区
			elReceiversArea = $("<div>").addClass("users-area").appendTo(elDialogLeft);
			$("<h3>").text(message("notification.mail.dialog.added-users")).appendTo(elReceiversArea);
			elReceivers = $("<ul>").addClass("added-users").appendTo(  elReceiversArea );
			
			//用户自己填写的说明
			elCustomMessage = $("<textarea>").attr( "rows", 3 ).appendTo( $("<div>").addClass( "custom-message" ).appendTo( elDialogLeft ).append ( $( "<span>" ).text( message( "notification.customMeassage") ) ) );
			
			return {
				el: elDialog,
				elChooseArea: elChooseArea,
				elInput: elInput,
				elFrequenters: elFrequenters,
				elReceiversArea: elReceiversArea,
				elReceivers: elReceivers,
				elCustomMessage: elCustomMessage
			};
		},
		
		/**
		 * 重新构建按钮的回调函数，使之传入一个当前已选中用户的邮件列表信息.
		 * @method _rebuildButtonCallbacks
		 * @param {Object} 按钮的键值对，key为按钮名称，value为按钮的回调函数
		 * @return {Object} 构建完成后的按钮键值对
		 * @private
		 */
		_rebuildButtonCallbacks: function(buttons) {
			var name, frequenters = [], results = {}, me = this;
			for (name in buttons) {
				results[name] = (function(fn) {
					return function() {
						var email, receivers = me.receivers, emails = [], customMessage = me.dialog.elCustomMessage.val();
						for (email in receivers) {
							emails.push(email);
						}
						if (fn.call(this, emails, customMessage)) {
							for (email in receivers) {
								frequenters.push(receivers[email].receiver);
							}
							me.addFrequenters(frequenters);
						}
					}
				})(buttons[name]);
			}
			return results;
		},
		
		/**
		 * 构建Auto complete效果的input.
		 * @method _bindAutocomplete
		 * @param input {HTMLElement | jQuery} 待绑定的input框
		 * @private
		 */
		_bindAutocomplete: function(input) {
			var me = this;
			$(input).autocomplete(Spark.constants.CONTEXT + "/ajax/users/emails/query", { 
				max: 30, //列表里的条目数 
				minChars: this.options.minLength, //自动完成激活之前填入的最小字符 
				scrollHeight: 300, //提示的高度，溢出显示滚动条 
				cacheLength: 0,
				dataType: "json",
				parse: function(data) {
					return $.map(data, function(row) {
						var email = row.email || "",
							value = {
								name: row.name || "",
								email: email
							};
						return { data: value, value: value, result: email };
					});
				},
				formatItem: function(row, i, max) { 
					return row.name + "," + row.email; 
				}
			})
			.result(function(event, row, formatted) {
				me.addReceiver(row);
			});
		}
		
	};
	
})(jQuery)