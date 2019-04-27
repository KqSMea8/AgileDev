/**
 * @descript: 附件功能模块的javascript文件. 
 */
Spark = Spark || {};
Spark.pages = Spark.pages || {};
Spark.pages.attachment = Spark.pages.attachment || {};
(function($){

	/** 国际化工具函数 */
	var i18n = Spark.util.message;
	/** 报警工具函数 */
	var Alert = Spark.widgets.Alert;
	/** 编码工具函数 */
	var encodeHTML = Spark.util.encodeHTML;
	/** 比较时间工具函数 */
	var difDate = Spark.util.formatDateInterval;
	/** 时间格式化工具函数 */
	var formatDate = Spark.util.formatDate;
	/** 当前用户 */
	var current_user = Spark.constants.CURRENT_USER;

	/**
	 * @author shixiaolei 
	 * @descript: 附件上传的javascript类, 在卡片编辑页面使用. 
	 */
	Spark.pages.attachment.Uploader = function(options){  
			this.options = $.extend({},this.options, options);
			this._init();
			return this;
	};
	
	Spark.pages.attachment.Uploader.prototype = {
		options : {
				/** @descript: 空间前缀 */
				prefixCode : null,
				/** @descript: 卡片序列 */
				sequence : null,
				/** @descript: 附件上传空间所在DIV  ID*/
				uploaderContainer : "newAttachmentContainer" ,
				/** @descript: 附件上传空间所在DIV ID*/
				uploadInput : "attachment_file" ,
				/** @descript: 增加附件上传控件的按钮ID */
				addButton : "add_uploader_button" 
		},
		
		/** 当前附件上传控件数计数器 */
		uploaderCount : 1,
		
		/**
		 * 上传全部附件.
		 * @method upload
		 */
		upload : function(callback){ 
				var me = this,  ids=[], uploaders = $("#" + this.options.uploaderContainer).find(".attachment_uploader"), count=uploaders.length;
				uploaders.each(function(i){ 
						var fileElementId = $(this).find(".attachment_file").attr("id");
						var noteElementId = $(this).find(".attachment_note").attr("id");;
						if($("#" + fileElementId).val()){
								me._uploadOne(fileElementId, noteElementId, 	
									function(id){ids.push(id);   count--;}, 	
									function(id){count--;}
								);
						}else{
							count--;
						}
				}); 
				var check = setInterval(function(){
						if(!count){ 
							callback.call(this, ids.join(","));
							clearInterval(check);
						}
				}, 100)
		},
		
		/**
		 * 上传单个附件.
		 * @method _uploadOne
		 */
		_uploadOne : function(fileElementId, noteElementId, successCallback, failedCallback){ 
				var url =  Spark.constants.CONTEXT + "/spaces/" + this.options.prefixCode  + "/attachments/upload";
				Spark.pages.attachment.upload(url, fileElementId, noteElementId, successCallback, failedCallback);
		},
		
		/**
		 * 初始化函数.
		 * @method _init
		 */
		_init : function(){
			var me = this;
			$("#" + this.options.addButton).click(function(){
				$("#" + me.options.addButton).text(i18n("card.attachment.addUploader"));
				me._appendUploader();
			});
		},
		
		/**
		 * 生成单个附件上传控件(文件选择input + 备注input).
		 * @method _appendUploader
		 */
		_appendUploader : function(){
				var me = this, count = this.uploaderCount++;
				var div = $("<div>").addClass("attachment_uploader").insertBefore($("#" + this.options.addButton));
				var fileInputSpan =  $("<span>").addClass("area").appendTo(div);
				var fileInput =  $("<input type='file' id='attachment_file_" + count + "' hidefocus>").attr("size", 35).attr("name", "attachment_file_" + count ).addClass("attachment_file").focus().appendTo(fileInputSpan);
				var fileTextInput =  $("<input type='text' id='attachment_file_text_" + count + "' hidefocus readOnly>").attr("size", 35).addClass("attachment_file_text").appendTo(fileInputSpan);
				var fileButton =  $("<button>").text(i18n("button.browse")).addClass("attachment_file_img_button").appendTo(fileInputSpan);
				$("<button>").text(i18n("button.cancel")).click(function(){ 	
					div.empty().remove();	
					if(!$("#" + me.options.uploaderContainer).find(".attachment_uploader").length){
						$("#" + me.options.addButton).text(i18n("card.attachment.add"));
					}
				}).appendTo(fileInputSpan);
				fileInput.change(function(){
					var str =fileInput.val(); 
			 		fileTextInput.val(str.substr(str.lastIndexOf('\\')+1));	
				});
				var opspan = $("<span>").addClass("operations area").appendTo(div),
				noteButton = $("<span>").addClass("operation").appendTo(opspan);
				$("<span>").addClass("change_attachment").appendTo(noteButton);
				$("<a>").text(i18n("card.attachment.addNote")).attr("href", "javascript: void(0);").addClass("icon-link").click(function(){ noteInputSpan.toggle();  noteInput.focus();	}).appendTo(noteButton);
				var noteInputSpan =  $("<div>").addClass("clear-both").hide().appendTo(div);
				var noteInput =  $("<textarea>")
					.attr("id","attachment_note_" + count )
					.attr("name", "attachment_note_" + count )
					.addClass("attachment_note").appendTo(noteInputSpan);
				
		}
	}
	

	/**
	 * @author shixiaolei 
	 * @descript: 附件列表功能的javascript类, 包含附件列表、及在其上的下载、删除和重新上传等. 在卡片只读页面的附件TAB使用.
	 */
	Spark.pages.attachment.Manager = function(options){  
			this.options = $.extend({},this.options, options);
			this._init(); 
			return this;
	};
	
	Spark.pages.attachment.Manager.prototype = {
		options : {
				/** @descript: 空间前缀 */
				prefixCode : null,
				/** @descript: 卡片序列 */
				sequence : null,
				/** @descript: 上传附件Input的ID */
				reuploadAttachmentElemId : "attachmentReuploadFile",
				/** @descript: 附件备注的Input ID */
				reuploadAttachmentNoteId : "attachmentReuploadNote",
				/** @descript: 保存被替换的附件ID的Input ID */
				oldAttachmentElemId: "old_attachment_id",
				/** @descript: 附件列表DIV的ID */
				attachmentContainerId : "attachments_list",
				/** @descript: 上传附件弹出框DIV */
				uploaderContainer : "uploaderContainer",
				/** @descript: 当前用户是否具有卡片的写权限,它意味着是否有附件的上传、重新上传和删除权限 */
				hasPermision: false
		},
		
		/**
		 * 初始化附件列表页面.
		 * @method _init
		 */
		_init : function(){
				this._reloadAttachments();
				this._initReploladDialog();
		},
		
		/**
		 * 加载附件信息列表.
		 * @method _reloadAttachments
		 */
		_reloadAttachments : function(){
				var me = this;
				$("#" + this.options.attachmentContainerId).empty();
				$.ajax({
						url :  Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/cards/" + this.options.sequence + "/attachments/" ,
						type : "GET",
						success : function(data){
								$.each(data, function(i,d){
									me._renderAttach(d);
								});
						}
				});
		},
		
		/**
		 * 显示一条附件信息.
		 * @method _renderAttach
		 * @param {Object} data 附件信息
		 */
		_renderAttach : function(data){
				var me = this, fragments = [];
				var  _div = $("<div id='attachment_" + data.id + "'></div>").addClass("card-attachment").appendTo($("#" + this.options.attachmentContainerId));
				fragments.push("<span class='card-attachment-user date'>");
				fragments.push(data.uploadUserName);
				fragments.push("</span>");
				fragments.push("<span class='card-attachment-time date'>");
				fragments.push("<span title='" + formatDate(data.uploadTime, "longdatetime") + "'>" + difDate(data.uploadTime) + "</span>&nbsp;");
				fragments.push(i18n("card.upload"));
				fragments.push("</span>");
				fragments.push("<span class='opertions-label'>"); 
				if(this.options.hasPermision || data.uploadUserId == current_user.id  ){ 
						fragments.push("<span class='operation'><span class='change_attachment'></span><a class='icon-link' style='color:#2779AA;' href='javascript:void(0);' id='attachment_replace_" + data.id + "'> " + i18n("card.attachment.reupload") + "</a></span>");
						fragments.push("<span class='operation'><span class='delete_attachment'></span><a class='icon-link' style='color:#2779AA;' href='javascript:void(0);' id='attachment_delete_" + data.id + "'> " + i18n("button.delete") + "</a></span>");
				}
				fragments.push("</span>");
				fragments.push("<span class='card-attachment-name'>");
				if(data.originalName.length > 23){
						var sub_name = data.originalName.substr(0, 23) + "...";
						fragments.push("<a href='" + Spark.constants.CONTEXT + "/spaces/" + this.options.prefixCode + "/cards/" + this.options.sequence +"/attachments/" + data.id + "/download' title='" + data.originalName + "'>" );
						fragments.push(sub_name);
						fragments.push("</a>");
				}else{
						fragments.push("<a href='" + Spark.constants.CONTEXT + "/spaces/" + this.options.prefixCode + "/cards/" + this.options.sequence +"/attachments/" + data.id + "/download'>" );
		 				fragments.push(data.originalName);
		 				fragments.push("</a>");
				}
				fragments.push("</span>");
				var note = encodeHTML(data.note).replace(/\'/g, "\\\"");
				if(note.length > 10){
						var sub_note = data.note.substr(0, 10) + "..." ;   
						fragments.push("<span class='card-attachment-note' title='" + note + "'>");
						fragments.push(sub_note);
						fragments.push("</span>");
				}else{
						fragments.push("<span class='card-attachment-note'>");
						fragments.push(note);
						fragments.push("</span>");
				}
				_div.html(fragments.join(""));
				$("#attachment_delete_" + data.id).click(function(){
						me._remove(data.id);
				});
				$("#attachment_replace_" + data.id).click(function(){
						$("#" + me.options.oldAttachmentElemId).val(data.id);
						me._openReUploadDialog(data.id);
				});
		},
		
		
		/**
		 * 当删除附件按钮被点击时触发此方法.
		 * @method onSaveClick
		 * @param {number} id 被删除的附件的ID
		 */
		_remove : function(id){
				var me = this, _buttonsConfirm = {};
				_buttonsConfirm[i18n("button.cancel")]=function(){
						$(this).dialog("close");
				};
				_buttonsConfirm[i18n("button.ok")]=function(){
						$(this).dialog("close");
						$.ajax({
								url : Spark.constants.CONTEXT + "/ajax/spaces/" + me.options.prefixCode + "/cards/" + me.options.sequence + "/attachments/" + id,
								type : "DELETE",
								success : function(data){ 	$('#attachment_' + id).remove(); }
						});
				};
				$("#attachment-dialog-confirm-content").html(i18n("card.attachment.deleteconfirm"));
				$(function(){
						$("#attachment-dialog-confirm").dialog({
								autoOpen: false,
								resizable: false,
								modal: true,
								buttons: _buttonsConfirm
						});
				});
				$("#attachment-dialog-confirm").dialog("open");
				return false;
		},
		
		/**
		 * 初始化上传对话框.
		 * @method _initReploladDialog
		 */
		_initReploladDialog : function(){
				var me = this, _buttonsUpload = {};
				_buttonsUpload[i18n("button.cancel")]=function(){ 	$(this).dialog("close"); };
				_buttonsUpload[i18n("card.attachment.submitAndSave")]=function(){ 	me._reUploadAndNotify(); };
				_buttonsUpload[i18n("card.attachment.submit")]=function(){ 	me._reUpload(); };
				$("#" + me.options.uploaderContainer).dialog({
						height: 300,
						width: 450,
						modal: true, 
						bgiframe :true,
						autoOpen: false,
						buttons: _buttonsUpload,
						title: i18n("card.attachment.upload")
				});
		},
		
		/**
		 * 打开上传对话框..
		 * @method _openReUploadDialog
		 */
		_openReUploadDialog : function(oldId){
				$("#" + this.options.reuploadAttachmentElemId).val("");
				$("#" + this.options.reuploadAttachmentNoteId).val("");
				$("#" + this.options.oldAttachmentElemId).val(oldId);
				$("#" + this.options.uploaderContainer).dialog('open');
		}, 
		
		/**
		 * 重新上传一个附件.
		 * @param {Function} callback 上传成功后的回调函数
		 */
		_reUpload: function(callback){
				var me = this, replacedId = $("#" + this.options.oldAttachmentElemId).val();
				var url = Spark.constants.CONTEXT + "/spaces/" + this.options.prefixCode + "/cards/" + this.options.sequence + "/attachments/" + replacedId + "/replace";
				this._doUpload(url, this.options.reuploadAttachmentElemId, this.options.reuploadAttachmentNoteId, function(id){
						$("#" + me.options.uploaderContainer).dialog('close');
						me._reloadAttachments();
						if(callback){callback.call(this, id);}
				});
		},
		
		/**
		 * 重新上传一个附件, 并发邮件通知.
		 * @param {Number} id 要替换的附件ID
		 */
		_reUploadAndNotify: function(id){
				var me = this, buttons = {};
				buttons[i18n("button.cancel")] = function() {
						$(this).dialog("close");
				};
				buttons[i18n("button.submit")] = function(emails, customMessage) {
						me._reUpload(function(id) { 	 
							if (!emails || !emails.length) {return ;}
							$.ajax({
								url: Spark.constants.CONTEXT + "/ajax/spaces/" + me.options.prefixCode + "/cards/" + me.options.sequence + "/attachments/" + id + "/notifications",
								type: "POST",
								data: { "emails" : emails, "customMessage" : customMessage },
								dataType: "json"
							});
						});	
						$(this).dialog("close");
				};
				new Spark.widgets.Notification({
						prefixCode: me.options.prefixCode,
						buttons: buttons
				}).show();
		},
		
		/**
		 * 上传.
		 * @method _doUpload
		 */
		_doUpload: function(url, fileElementId, noteElementId, callback){ 
				Spark.pages.attachment.upload(url, fileElementId, noteElementId, callback);
		}
	}
	
	/**
	  * 上传一个附件. 为Spark.pages.attachment.Upload和Spark.pages.attachment.Manager共用的工具函数.
	  * @method upload
	  * @param {String} url 上传URL
	  * @param {String} fileElementId 文件选择Input的ID
	  * @param {String} noteElementId 备注Input的ID
	  * @param {Function} successCallback 成功后的回调
	  * @param {Function} failCallback 失败时的回调
      */
	Spark.pages.attachment.upload = function(url, fileElementId, noteElementId, successCallback, failCallback){ 
			var me = this, filename = $("#" + fileElementId).val(); 
			if(!filename){ 
			 		Alert.alert(i18n("card.attachment.noAttachment"));  return false;
			}
			if($("#" + noteElementId).val().length > 500){ 
			 		Alert.alert(i18n("card.attachment.noteTooLong"));  	return false;
			}
			$.ajaxFileUpload({
					url: url + "?attachment=" + fileElementId + "&note=" + encodeURIComponent($("#" + noteElementId).val()) ,
					secureuri:false,
					fileElementId: fileElementId,
					dataType: "json",
					success: function (data, status){
							if(successCallback){ successCallback.call(this,data.msg); }
					},
					error: function (x, s, e){ 
							Alert.alert(i18n("card.attachment.failedToUpload") + ": " + filename );
							if(failCallback){ 	failCallback.call(x, s, e); }
					}
			})
	}	
	
})(jQuery);