/**
 * @descript: 卡片修改模块的javascript文件. 
 */
Spark = Spark || {};
Spark.pages = Spark.pages || {};
(function($){

	/** 国际化工具函数 */
	var i18n = Spark.util.message;
	/** 报警工具函数 */
	var Alert = Spark.widgets.Alert;
	/** 编码工具函数 */
	var encodeHTML = Spark.util.encodeHTML;

	Spark.pages.CardForm = function(options){  
			this.options = $.extend({},this.options, options);
			this._init(); 
			return this;
	};
	
	Spark.pages.CardForm.prototype= {
		options : {
				/** 空间前缀 */
				spacePrefix : null,
				/** 卡片序列. 当新建页面时为null */
				sequence : null,
				/** 卡片类型ID */
				cardTypeId : null,
				/** 卡片类型名称 */
				cardTypeName : null,
				/** 是否为根卡片 */
				isRoot : false
		} ,
		
		spacePrefix : null,
		sequence : null,
		load : null,
		attachmentUploader : null,
		notification : null,
		
		/**
		 * 点保存按钮时触发函数.
		 * @method save
		 */
		save : function() {
				// 检查是否勾选通知
				if ($("#card-notify").attr("checked")) {
					this.notification.show();
				}else {
					this._doSave();
				}
		},
		
		/**
		 * 保存卡片的实际操作函数.
		 * @method _doSave
		 */
		_doSave : function() { 
				var me = this;
				this.load.showLoading();
				var editor = tinyMCE.get("detail");
				$("<div>").addClass("card-detail rich-content").html(editor.getBody().innerHTML)
					.prependTo($("#detail").parent().parent());  // XXX 不应该使用parent方式定位元素
				editor.remove();
				document.getElementById('detail').parentNode.style.display = 'none';
				this.attachmentUploader.upload(function(ids){
						$("#card-attachment-ids") .val(ids);  
						$("#cardform").submit();
				});
		},
		
		/**
		 * 初始化函数.
		 * @method _init
		 */
		_init : function(){  
				this.spacePrefix = this.options.spacePrefix;
				this.sequence = this.options.sequence; 
				this._initBreadcrumb()._initLoad()._initTinyMCE()._initAttachmentUploader()._initNotification()._initParentTree();
		},
		
		/**
		 * 初始化TinyMCE.
		 * @method _initTinyMCE
		 */
		_initTinyMCE : function(){
				Spark.thirdparty.tinyMCE("detail");
				return this;
		},
		
		/**
		 * 初始化Loding图标.
		 * @method _initLoad
		 */
		_initLoad : function(){
				this.load = new Spark.widgets.Load();
				return this;
		},
		
		/**
		 * 初始化面包屑.
		 * @method _initBreadcrumb
		 */
		_initBreadcrumb : function(){
				if(this.options.isNew){
				 	 $("#current-card-breadcrumb").text("> " +   i18n("button.new") + this.options.cardTypeName  );
				 }else{
					 $("#current-card-breadcrumb").text("> " + this.spacePrefix + "-" + this.sequence );
				 }	
				 return this;
		},
		
		/**
		 * 初始化附件上传控件.
		 * @method _initAttachmentUploader
		 */
		_initAttachmentUploader : function(){
				this.attachmentUploader = new Spark.pages.attachment.Uploader({
					prefixCode : this.options.spacePrefix ,
					sequence : this.options.sequence,
					uploaderContainer : "newAttachmentContainer"
				});
				return this;
		},
		
		/**
		 * 初始化邮件空间.
		 * @method _initNotification
		 */
		_initNotification : function(){
				var buttons = {}, me = this;
				buttons[i18n("button.cancel")] = function() {
					$(this).dialog("close");
				};
				buttons[i18n("button.saveAndNotify")] = function(emails, customMessage) {
					// 如果需要通知
					if (emails && emails.length) {
						$("#card-notify-emails").val(emails.join(";"));
						$("#card-notify-message").val(customMessage);
					}
					me._doSave();
					$(this).dialog("close");
					return true;  // 记录常用联系人
				};
				// 显示通知窗口
				this.notification = new Spark.widgets.Notification({
					prefixCode: me.spacePrefix,
					buttons: buttons
				});
				return this;
		},
		
		/**
		 * 初始化选上级树对话框.
		 * @method _initParentTree
		 */
		_initParentTree : function(){
				var _pathIds, _rootUrl, _nodeUrlPrefix, me=this;
				//查询可以作为父级的卡片的URL
				 if(this.options.isNew){
					 	_rootUrl =   Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix + "/cards/under_root/valid_parents_for_type/" + me.options.cardTypeId;		 
					 	_nodeUrlPrefix =   Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix + "/cards/under/{sequence}/valid_parents_for_type/" + me.options.cardTypeId;		 
				 }else{
					 	_rootUrl = Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix + "/cards/under_root/valid_parents_for_card/" + me.sequence;
					 	_nodeUrlPrefix =   Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix +"/cards/under/{sequence}/valid_parents_for_card/" + me.sequence;
				 }
				 //生成按钮
				 var _buttons = [];
				 _buttons[i18n("button.cancel")] = function(){  	tree.close();   };
				 _buttons[i18n("card.parent.selectRootButton")] =function(){
						$(".parent_id_of_this_card").removeAttr("checked");
						$(".parent_id_of_this_card").val("-1") ;
						$("#parent_title_of_this_card").text(i18n("card.parent.selectMessage"));
						tree.close();
				} ,
				_buttons[i18n("button.ok")] =function(){ 
						var NODE = tree.selectedNode; //选定的节点
						if(NODE){ 
								$(".parent_id_of_this_card").attr("checked");
								$(".parent_id_of_this_card").val(NODE.attr("cardId"));
								$("#parent_title_of_this_card").text('#' + NODE.attr("sequence") + '-' + NODE.attr("title"));
								tree.close();
						}else if(me.options.isRoot){
								tree.close();
						}else{ 
								tree.showError(i18n("card.parent.selectempty"));	
						}
				};
				//生成树
				var tree = new Spark.widgets.CardTreeSelector({
					 	containerDiv:  "treeSelectorContainer",
					 	rootUrl: _rootUrl,
						nodeUrl: function(NODE){   	return  Spark.util.substitute( _nodeUrlPrefix ,  { sequence: $(NODE).attr("sequence") } ); },
						hasChildren: function(DATA){  	return DATA.childrenCount > 0;  },
						isEnable: function(DATA){  	return DATA.validForParent == true;  },
						isInPath: function(DATA){ 	 return $.inArray(DATA.id,_pathIds )!=-1;  },
						isLastSelect: function(DATA){ 	 return DATA.id == $(".parent_id_of_this_card").val(); },
						isUnabledNodeVisiable : true,
						invalidMessage:  i18n("card.parent.invalidMessage"),
						title:  i18n("label.card.tree"),
						buttons: _buttons
				 });
		  		 //点击弹出选上级的树
				$('#parent_title_of_this_card').click(function() { 
						//请求当前节点的祖先节点,用于在树上显示
						var current_parent = $(".parent_id_of_this_card").val();
						if(current_parent !="" && current_parent!="-1"){   
								 $.ajax({
										url:    Spark.constants.CONTEXT + "/ajax/spaces/" + me.spacePrefix + "/cards/" + current_parent + "/ancestorIds",
										type: "GET",
										async: false,
										success: function(data){ 	_pathIds = data;	 }
								});
						} else {
								 _pathIds = [];
						}
						tree.open();
						return false;
				 });
				 return this;
		}
	} 
})(jQuery);