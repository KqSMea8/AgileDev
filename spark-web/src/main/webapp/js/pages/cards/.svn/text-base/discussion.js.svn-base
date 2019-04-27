/**
 * @descript: 讨论功能模块的javascript文件, 包含页面内容的生成. 
 */
Spark = Spark || {};

/** @descript: 包的命名空间. */
Spark.pages = Spark.pages || {};

(function ($) {
	
	/** 别名 */
	var i18n = Spark.util.message;
	var Alert = Spark.widgets.Alert;
	
	/** 属性相关的 */
	var Discussion = Discussion || {};
	/** 本条回复的id. */
	Discussion.ID = 'discussionRowId';
	/** 如果是回复的话 这个引用上级回复id. */
	Discussion.REPLY_ID = 'discussionReplyId';
	/** 子回复列表id. */
	Discussion.LIST_ID = 'discussionReplyListId';
	/** 回复编辑框id. */
	Discussion.EDIT_ID = 'discussionTextArea';
	/** 回复内容编辑框id. */
	Discussion.REPLY_EDIT_ID = 'discussionReplyTextArea';
	/** 对卡片回复的id. */
	Discussion.REPLY_CARD_ID = 'discussionReplyCard';
	/** 小图标操作区id. */
	Discussion.ACTION_ICON_SPACE_ID = 'discussionOperationId';
	/** 回复按钮id. */
	Discussion.REPLY_BTN_ID = 'discussionReplyBtn';
	/** 编辑器类型常量. TODO 此处的常量应该改为字符串，就不再需要其他ID了 */
	EDITOR_TYPE = {
		REPLY: 1,
		EDIT: 2,
		NEW: 3
	};
	
	/**
	 * @author sunjiaxiang 
	 * @descript: 讨论功能模块的javascript文件, 包含页面内容的生成. 
	 */
	Spark.pages.Discussion = function (elem, path, prefix, cardId) {
		this.operatingAreas = {};
		this.box = elem;
		this.appPath = path;
		this.prefix = prefix;
		this.cardId = cardId;
		this.userId = Spark.constants.CURRENT_USER.id;
		this._init();
	};
	
	Spark.pages.Discussion.prototype = {
		/** @descript: js脚本在页面上生成的位置 */
		box: null,
		
		/** @descript: 加载js文件的路径 */
		appPath: null,
		
		/** @descript: 取数据用的参数. */
		prefix: null,
		
		/** @descript: 卡片信息 */
		cardId: null,
		
		/** @descript: 当前用户id */
		userId: null,
		
		operatingAreas: null,
			
		/** @descript: 初始化讨论数据. */
		_init: function () {
			var me = this;
			$.ajax({
				url: this.appPath + '/ajax/spaces/' + this.prefix + '/cards/' + this.cardId + '/discussions?d=' + new Date().getTime(),
				type : "GET",
				success: function(data){
					if (typeof(data) != 'string')
						me._initResponse(data);
				},
				error: function(x, status, code){
	//				Spark.util.handleAjaxError(x, function(code, msg, text){
	//					$("#tabs-1").html(text);
	//				});
				}
			});
		},
		
		/** @descript: 初始化讨论数据加载. */
		_initResponse: function (res) {
			this.replyRoot = document.createElement('ul');
			$('#tabtitle-1').html(i18n('card.show.discussion') + '(' + res.length + ')');
			for (var i = 0, _data, n = res.length; i < n; i ++) {
				_data = res[i];
				if (_data.id) {
					var rowLi = document.createElement('li');
					rowLi.appendChild(this._createRow({
						replyId: _data.id, 
						status: _data.status, 
						contentMsg: _data.content, 
						userId: _data.userId, 
						userName: _data.userName, 
						lastModifyTime: _data.lastModifyTime, 
						replyList: _data.replyList
					}));
					this.replyRoot.appendChild(rowLi);
				}
			}
			this.cardReplyTitleRow = this._createCardReplyRow();
			this.cardReplyRow = this._createCardReplyRow();
			if (!this.replyRoot.childNodes.length) {
				this.cardReplyTitleRow.style.display = 'none';
			}
			
			this.box.appendChild(this.cardReplyTitleRow);
			this.box.appendChild(this.replyRoot);
			this.box.appendChild(this.cardReplyRow);
		},
		
		/** @descript: 创建对卡片回复的连接. */
		_createCardReplyRow: function () {
			var me = this;
			var space = document.createElement('div');
			var link = document.createElement('a');
			link.innerHTML = i18n('discussion.card.reply.message');
			link.setAttribute('href', 'javascript:void(0)');
			link.className = 'join-button';
			link.onclick = function () {
				if (me.cardReplyRow.style.display != 'none') {
					var editSpace = document.createElement('li');
					editSpace.appendChild(me._createRow({replyId: me.cardId, userId: me.userId, type: 3}));
					me.replyRoot.appendChild(editSpace);
					me.cardReplyRow.style.display = 'none';
				}
			};
			space.appendChild(link);
			return space;
		},
		
		/** 
		 * @descript: 生成数据行. 
		 * @param: replyId 本条回复的ID.
		 * @param: status 本条回复的状态(是否被删除).
		 * @param: contentMsg 回复内容.
		 * @param: userName 回复用户名.
		 * @param: userId 回复用户ID.
		 * @param: lastModifyTime 回复时间.
		 * @param: type 本行类型 0=数据显示, 1=进行回复, 2=进行编辑, 3=发表新回复(对卡片).
		 * @param: replyList 子回复.
		 */
		_createRow: function (parames) {
			var me = this;
			var replyId = parames.replyId, 
				status = parames.status || 0,
				contentMsg = parames.contentMsg || null,
				userId = parames.userId || null,
				userName = parames.userName || null,
				lastModifyTime = parames.lastModifyTime || null,
				type = parames.type || 0,
				replyList = parames.replyList || null;
			
			var rowSpace = document.createElement('div'),
				contentSpace = document.createElement('div'),
				title = document.createElement('div'),
				center = document.createElement('div');
			
			contentSpace.appendChild(title);
			contentSpace.appendChild(center);
			rowSpace.className = 'discussion';
			rowSpace.appendChild(contentSpace);
			
			// 设置css样式
			// rowSpace.className = 'discussion';
			contentSpace.className = 'discussion-radius';
			title.className = 'title-row';
			
			// 设置属性
			if (type == 1 || type == 3)
				title.setAttribute('id', Discussion.REPLY_ID + Discussion.ACTION_ICON_SPACE_ID + replyId);
			else
				title.setAttribute('id', Discussion.ACTION_ICON_SPACE_ID + replyId);
			
			// 回复用户名装填
			var nameLab = document.createElement('span');
			nameLab.className = 'name-space';
			title.appendChild(nameLab);
			nameLab.innerHTML = (userId == this.userId ? i18n('discussion.user.my') : userName);
				
			// 时间装填
			if (lastModifyTime) {
				nameLab.innerHTML += ' ' + Spark.util.formatDateInterval(lastModifyTime);
			}
			
			nameLab.innerHTML += ' ' + i18n('discussion.user.say');
			
			// 回复内容装填
			var content = document.createElement('div');
			content.setAttribute('replyId', replyId);
			content.setAttribute('userId', userId);
			center.appendChild(content);
			
			if (!type) {
				content.setAttribute('id', Discussion.ID + replyId);
				var msgSpan = document.createElement('div');
				msgSpan.className = 'context rich-content';
				content.appendChild(msgSpan);
			
				// 回复的删除检测
				if (status) {
					msgSpan.innerHTML = '<p>' + i18n('discussion.message.delete') + '</p>';
				}
				else {
					msgSpan.innerHTML = contentMsg;
					// 添加小图标
					this._setTitleActionBtn(title, replyId, userId, msgSpan);
				}
			}
			// 无回复内容
			else {
				content.setAttribute('id', Discussion.REPLY_ID + replyId);
				var editSpace = this._editSpace(replyId, type);
				var delBtn = document.createElement('div');
				delBtn.setAttribute('replyId', replyId);
				delBtn.className = 'delete' + ($.browser.msie ? ' title-offset-action' : '');
				delBtn.setAttribute('title', i18n('button.delete'));
				title.appendChild(delBtn);
				delBtn.onclick = function () {
					// 是否关闭的提示.
					// var isDel = confirm(i18n('discussion.confirm.clear'));
					// if (isDel) {
						var replyId = type == 2 ? Discussion.REPLY_EDIT_ID : type == 3 ? Discussion.REPLY_CARD_ID : Discussion.EDIT_ID;
						replyId += this.getAttribute('replyId');
						editor.remove(replyId);
						var curNode = this.parentNode.parentNode.parentNode.parentNode;
						curNode.parentNode.removeChild(curNode);
						me.cardReplyRow.style.display = 'block';
					// }
				}
				content.appendChild(editSpace);
			}
			
			// 递归绘制子节点
			if (replyList && replyList.length) {
				var rowList = document.createElement('ul');
				rowList.className = 'node';
				rowList.setAttribute('id', Discussion.LIST_ID + replyId);
				for (var i = 0, n = replyList.length; i < n; i ++) {
					var replyListLi = document.createElement('li'), replyLi = replyList[i];
					replyListLi.appendChild(this._createRow({
						replyId: replyLi.id, 
						status: replyLi.status, 
						contentMsg: replyLi.content, 
						userId: replyLi.userId, 
						userName: replyLi.userName, 
						lastModifyTime: replyLi.lastModifyTime, 
						replyList: replyLi.replyList
					}));
					rowList.appendChild(replyListLi);
				}
				rowSpace.appendChild(rowList);
			}
			return rowSpace;
		},
			
		/** 
		 * @descript: 装填控制列的按钮. 
		 * @param addElem {Element} 添加的元素位置.
		 * @param replyId {string} 回复id.
		 * @param userId {string} 用户id.
		 * @param msgSpan {string} 内容区域.
		 * */
		_setTitleActionBtn: function (addElem, replyId, userId, msgSpan) {
			var me = this;
			if (userId == this.userId) {
				// 删除
				var delBtn = document.createElement('div');
				delBtn.setAttribute('replyId', replyId);
				delBtn.className = 'delete' + ($.browser.msie ? ' title-offset-action' : '');
				delBtn.setAttribute('title', i18n('button.delete'));
				delBtn.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;';
				addElem.appendChild(delBtn);
				delBtn.onclick = function () {
					var isDel = confirm(i18n('discussion.confirm.delete'));
					if (isDel) {
						var replyId = this.getAttribute('replyId');
						var space = findById(replyId);
						space.firstChild.innerHTML = i18n('discussion.message.delete');
						space.firstChild.ondblclick = null;
						space.style.display = 'block';
						if (space.nextSibling) {
							editor.remove(Discussion.REPLY_EDIT_ID + replyId);
							space.parentNode.removeChild(space.nextSibling);
						}
						space = document.getElementById(Discussion.ACTION_ICON_SPACE_ID + replyId);
						var icons = space.getElementsByTagName('div');
						for (var i = 0; i < icons.length; ) {
							icons[i].onclick = null;
							space.removeChild(icons[i]);
						}
						me.doDelete(this.getAttribute('replyId'));
					}
				}
				// 编辑
				var editBtn = document.createElement('div');
				editBtn.setAttribute('replyId', replyId);
				editBtn.className = 'edit' + ($.browser.msie ? ' title-offset-action' : '');
				editBtn.setAttribute('title', i18n('button.edit'));
				editBtn.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;';
				addElem.appendChild(editBtn);
				editBtn.onclick = function () {
					var curRow = findById(editBtn.getAttribute('replyId'));
					if (curRow.style.display == 'none') {
						curRow.style.display == 'block';
						if (curRow.childNodes.length > 1)
							curRow.removeChild (curRow.lastChild);
					}
					else {
						curRow.style.display = 'none';
						me.eventEdit(this, curRow.firstChild.innerHTML);
					}
				}
			}
			// 回复
			var replyBtn = document.createElement('div');
			replyBtn.setAttribute('replyId', replyId);
			replyBtn.setAttribute('id', Discussion.REPLY_BTN_ID + replyId);
			replyBtn.className = 'reply' + ($.browser.msie ? ' title-offset-action' : '');
			replyBtn.setAttribute('title', i18n('discussion.user.reply'));
			replyBtn.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;';
			addElem.appendChild(replyBtn);
			replyBtn.onclick = function () {
				me.eventReply(this);
			}
			// 回复
			msgSpan.ondblclick = function (evt) {
				var curReplyId = this.parentNode.getAttribute('replyId');
				var replyBtn = document.getElementById(Discussion.REPLY_BTN_ID + curReplyId);
				me.eventReply(replyBtn);
				
				if (evt) {
					evt.stopPropagation && evt.stopPropagation();
					evt.preventDefault && evt.preventDefault()
				}
				else {
					event.cancelBubble = true;
					event.returnValue = false;
				}
			};
		},
		
		/** 
		 * @descript: 回复的点击事件.
		 * @param evtElem {Element} 事件元素.
		 */
		eventReply: function (evtElem) {
			var replyId = evtElem.getAttribute('replyId');
			if (!document.getElementById(Discussion.REPLY_ID + replyId)) {
				var newRow = this._createRow({replyId: replyId, userId: this.userId, type: 1});
				var space = document.getElementById(Discussion.LIST_ID + replyId);
				
				if (space) {
					var replyListLi = document.createElement('li');
					replyListLi.appendChild(newRow);
					space.appendChild(replyListLi);
				}
				else {
					var rowList = document.createElement('ul');
					rowList.className = 'node';
					rowList.setAttribute('id', Discussion.LIST_ID + replyId);
					
					var replyListLi = document.createElement('li');
					replyListLi.appendChild(newRow);
					rowList.appendChild(replyListLi);
					findById(replyId).parentNode.parentNode.parentNode.parentNode.appendChild(rowList);
				}
			}
		},
		
		/** 
		 * @descript: 编辑的点击事件. 
		 * @param evtElem {Element} 事件元素.
		 * @param msg {string} 编辑后的内容.
		 */
		eventEdit: function (evtElem, msg) {
			var replyId = evtElem.getAttribute('replyId');
			var editSpace = this._editSpace(replyId, EDITOR_TYPE.EDIT, msg);
			var space = findById(replyId).parentNode;
			space.appendChild(editSpace);
		},
		
		/** 
		 * @descript: 删除回复. 
		 * @param id {string} 删除的回复id.
		 */
		doDelete: function (id) {
			$.ajax({
				url: this.appPath + '/ajax/spaces/' + this.prefix + '/cards/' + this.cardId + '/discussions/' + id,
				type : "DELETE",
				success: function(data){},
				error: function(x, status, code){
					Alert.responseStatusAlert(x.status, status, function () {
						location.reload();
					});
				}
			});
		},
		
		/** 
		 * @descript: 回复. 
		 * @param id {string} 回复id.
		 * @param msg {string} 回复内容.
		 * @param callback {Function} 可选参数，当保存成功后的回调函数
		 */
		doReply: function (id, msg, callback) {
			var me = this;
			$.ajax({
				url: me.appPath + '/ajax/spaces/' + me.prefix + '/cards/' + me.cardId + '/discussions/' + id + '/replies',
				type : "POST",
				data : {msg: msg},
				success: function(data){
					var newRowTitle = document.getElementById(Discussion.REPLY_ID + Discussion.ACTION_ICON_SPACE_ID + data.replyId);
					newRowTitle.setAttribute('id', Discussion.ACTION_ICON_SPACE_ID + data.id);
					var newRow = document.getElementById(Discussion.REPLY_ID + data.replyId);
					newRow.setAttribute('id', Discussion.ID + data.id);
					newRow.setAttribute('replyId', data.id);
					for (var i = 1; i < newRowTitle.childNodes.length;)
						newRowTitle.removeChild(newRowTitle.childNodes[i]);
					me._setTitleActionBtn(newRowTitle, data.id, me.userId, newRow.firstChild);
					
					// 触发回调函数
					if (callback && (callback instanceof Function)) {
						callback.call(me, data.id);
					}
				},
				error: function(x, status, code){
					Alert.responseStatusAlert(x.status, status, function () {
						location.reload();
					});
				}
			});
			
			// 显示回复内容
			$("<div>").addClass("context rich-content").html(msg).appendTo($("#" + Discussion.REPLY_ID + id));
		},
		
		/** 
		 * @descript: 修改. 
		 * @param id {string} 修改的回复id.
		 * @param msg {string} 修改的回复内容.
		 * @param callback {Function} 可选参数，当保存成功后的回调函数
		 */
		doEdit: function (id, msg, callback) {
			var me = this;
			$.ajax({
				url: me.appPath + '/ajax/spaces/' + me.prefix + '/cards/' + me.cardId + '/discussions/' + id,
				type : "POST",  
				data : {msg: msg},
				success: function(data){
					var space = findById(data.id);
					space.firstChild.innerHTML = data.content;
					space.style.display = 'block';
					
					// 触发回调函数
					if (callback && (callback instanceof Function)) {
						callback.call(me, id);
					}
				},
				error: function(x, status, code){
					Alert.responseStatusAlert(x.status, status, function () {
						location.reload();
					});
				}
			});
			
			// 显示原有内容
			$(findById(id)).find(":first-child").html(msg).show();
		},
		
		/** 
		 * @descript: 对卡片进行回复. 
		 * @param msg {string} 回复内容.
		 * @param callback {Function} 可选参数，当保存成功后的回调函数
		 */
		doCardReply: function (id, msg, callback) {
			var me = this;
			$.ajax({
				url: me.appPath + '/ajax/spaces/' + me.prefix + '/cards/' + me.cardId + '/discussions',
				type : "POST",
				data : {msg: msg},
				success: function(data){
					var newRowTitle = document.getElementById(Discussion.REPLY_ID + Discussion.ACTION_ICON_SPACE_ID + me.cardId);
					newRowTitle.setAttribute('id', Discussion.ACTION_ICON_SPACE_ID + data.id);
					var newRow = document.getElementById(Discussion.REPLY_ID + me.cardId);
					newRow.setAttribute('id', Discussion.ID + data.id);
					newRow.setAttribute('replyId', data.id);
					
					for (var i = 1; i < newRowTitle.childNodes.length;)
						newRowTitle.removeChild(newRowTitle.childNodes[i]);
					
					me._setTitleActionBtn(newRowTitle, data.id, me.userId, newRow.firstChild);
					if (me.cardReplyTitleRow.style.display == 'none')
						me.cardReplyTitleRow.style.display = 'block';
					
					// 触发回调函数
					if (callback && (callback instanceof Function)) {
						callback.call(me, data.id);
					}
				},
				error: function(x, status, code){
					Alert.responseStatusAlert(x.status, status, function () {
						location.reload();
					});
				}
			});
			
			// 显示回复内容
			$("<div>").addClass("context rich-content").html(msg).appendTo($("#" + Discussion.REPLY_ID + id));
		},
		
		/**
		 * 通知.
		 * @method doNotify
		 * @param id {int} 评论ID
		 * @param emails {Array} 收件人数组
		 * @param customMessage {String} 评论
		 */
		doNotify: function(id, emails, customMessage) { 
			if (!emails || !emails.length) {
				return;
			}
			$.ajax({
				url: Spark.constants.CONTEXT + "/ajax/spaces/" + this.prefix + "/cards/" + this.cardId + "/discussions/" + id + "/notifications",
				type: "POST",
				data: { "emails" : emails, "customMessage" : customMessage },
				dataType: "json",
				success: function(data) {
					// Do nothing
				}
			});
		},
		
		/**
		 * 当保存按钮被点击时触发此方法.
		 * @method onSaveClick
		 * @param {EDITOR_TYPE Enumeration} type 编辑器类型
		 * @param {string} msg 待保存的内容
		 * @param {Function} callback 可选参数，当保存成功后的回调函数
		 * @private
		 */
		_save: function(type, msg, callback) {
			var operatingArea = this.operatingAreas[type],
				discussionId = operatingArea.data("discussionId"),
				editorId = operatingArea.data("editorId");
			
			switch (type) {
				case 1: this.doReply(discussionId, msg, callback); break;
				case 2: this.doEdit(discussionId, msg, callback); break;
				default: this.doCardReply(discussionId, msg, callback); break;
			}
			
			// 移除编辑内容
			operatingArea.remove();
			editor.remove(editorId);
			this.cardReplyRow.style.display = 'block';
		},
		
		/** 
		 * @descript: 获取编辑器. 
		 * @param id {string} 元素id.
		 * @param type {number} 编辑器类型1=回帖, 2=编辑, 3=新帖.
		 * @param msg {string} 编辑器的默认填充内容.
		 */
		_editSpace: function (id, type, msg) {
			var editSpace,
				saveButton = this._generateSaveButton(type),
				saveAndNotifyButton = this._generateSaveAndNotifyButton(type),
				cancelButton = this._generateCancelButton(type),
				me = this;
				editorId = this._generateEditorId(id, type);
			
			editSpace = $('<div>').addClass("edit-area").data("discussionId", id).data("editorId", editorId);
			
			$("<textarea>").width("100%").height(48).val(msg || "").attr("id", editorId)
				.appendTo(editSpace);
			
			$("<div>").addClass("action-area").append(saveButton).append(saveAndNotifyButton).append(cancelButton)
				.appendTo(editSpace);
			
			setTimeout(function () {
				me.tinyConf(editorId);
			}, 10);
			
			// 将生成的编辑区域按类型存储起来
			this.operatingAreas[type] = editSpace;
			
			return editSpace[0];
		},
		
		/**
		 * 生成保存并通知的按钮.
		 * @method _generateSaveAndNotifyButton
		 * @param {EDITOR_TYPE Enumeration} 编辑器类型
		 * @return {HTMLElement} 生成的保存并通知按钮元素
		 * @private
		 */
		_generateSaveAndNotifyButton: function(type) { 
			var me = this;
			return $("<a>").text(i18n("button.saveAndNotify")).addClass("discussion-notify").click(function() {
				var operatingArea = me.operatingAreas[type],
					editorId = operatingArea.data("editorId"),
					msg = editor.getContent(editorId);
				
				// 校验内容是否为空
				if (!msg.replace(/<br.*>|<p[^>]*>|<\/p>/gi, '')) {
					Alert.alert(i18n('discussion.confirm.submit'));
					return;
				}
				
				// 生成通知所需的按钮们
				var buttons = {};
				buttons[i18n("button.cancel")] = function() {
					$(this).dialog("close");
				};
				buttons[i18n("button.saveAndNotify")] = function(emails, customMessage) { 
					me._save(type, msg, function(id) {
						me.doNotify(id, emails, customMessage);
					});
					$(this).dialog("close");
					return true;  // 记录常用联系人
				};
				
				// 显示通知窗口
				var notification = new Spark.widgets.Notification({
					prefixCode: me.prefix,
					buttons: buttons
				});
				notification.show();
			});
		},
		
		/**
		 * 生成提交按钮.
		 * @method _generateSaveButton
		 * @param {EDITOR_TYPE Enumeration} 编辑器类型
		 * @return {HTMLElement} 生成的提交按钮元素
		 * @private
		 */
		_generateSaveButton: function(type) {
			var me = this;
			return $("<a>").text(i18n("button.save")).addClass("discussion-save").click(function() {
				var operatingArea = me.operatingAreas[type],
					discussionId = operatingArea.data("discussionId"),
					editorId = operatingArea.data("editorId"),
					msg = editor.getContent(editorId);
				
				// 校验内容是否为空
				if (!msg.replace(/<br.*>|<p[^>]*>|<\/p>/gi, '')) {
					Alert.alert(i18n('discussion.confirm.submit'));
					return;
				}
				
				me._save(type, msg);
			});
		},
		
		/**
		 * 生成取消按钮.
		 * @method _generateCancelButton
		 * @param {EDITOR_TYPE Enumeration} 编辑器类型
		 * @return {HTMLElement} 生成的取消按钮元素
		 * @private
		 */
		_generateCancelButton: function(type) {
			var me = this;
			return $("<a>").text(i18n('button.cancel')).addClass("discussion-back").click(function () {
				var operatingArea = me.operatingAreas[type],
					discussionId = operatingArea.data("discussionId"),
					editorId = operatingArea.data("editorId");
				
				// 移除TinyMCE
				editor.remove(editorId);
				
				// 如果非新建则直接展现原有容器
				if (type != EDITOR_TYPE.NEW) {
					findById(discussionId).style.display = 'block';
				}
				me.cardReplyRow.style.display = 'block';
				
				// 区别对待编辑和非编辑
				if (type != EDITOR_TYPE.EDIT) {
					operatingArea.parents("li").first().remove();
				} else {
					operatingArea.remove();
				}
			})
		},
		
		/**
		 * 生成编辑器ID.
		 * @param {int} id 评论ID
		 * @param {EDITOR_TYPE Enumeration} type 编辑器类型 
		 */
		_generateEditorId: function(id, type) {
			return (type === EDITOR_TYPE.EDIT ? Discussion.REPLY_EDIT_ID : 
					(type === EDITOR_TYPE.NEW) ? Discussion.REPLY_CARD_ID : Discussion.EDIT_ID) 
					+ id;
		},
		
		/** 
		 * @descript: tinyMCE转换. 
		 * @param elementId {string} 转换的元素id.
		 */
		tinyConf: function (elementId) {
			tinyMCE.init({
				mode: "exact",
				theme: "advanced",
				elements: elementId,
				theme_advanced_buttons1: 'bold,italic,underline,strikethrough,forecolor,backcolor,bullist,numlist,outdent,inden,fontsizeselect,code',
				theme_advanced_buttons2: '',
				theme_advanced_buttons3: '',
				theme_advanced_buttons4: '',
				theme_advanced_toolbar_location : "top",
				theme_advanced_toolbar_align : "left",
				auto_focus: elementId,
				language: Spark.constants.LOCALE.substring(0, 2) || 'zh'
			});
		}
		
	};

	/** 
	 * @descript: 通过replyId查找记录是否存在.
	 * @method findById
	 * @param id {string} 查找的元素id.
	 * @return {HTMLElement} 查找到的元素
	 * @static
	 */
	var findById = function (id) {
		return document.getElementById(Discussion.ID + id);
	};
	
	/**
	 * 私有编辑器，封装TinyMCE动作，简化代码.
	 */
	var editor = {
		get: function(id) {
			return tinyMCE.get(id);
		},
		getContent: function(id) {
			return tinyMCE.get(id).getBody().innerHTML;
		},
		remove: function(id) {
			tinyMCE.remove(tinyMCE.get(id));
		}
	}
	
})(jQuery);
