tinyMCEPopup.requireLangPack();

var UploadDialog = {
	init : function() {
		var f = document.forms[0];
	},

	insert : function() {
		var fileName = $("#fileToUpload").val();
		if (null == fileName || fileName == ""){
			//未填写文件的异常分支
			alert($("#no_file").val());
			return;
		}
		if (fileName.indexOf(".")>-1){
			var extName = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length);
			var lowerExtName = extName.toLowerCase();
			if (lowerExtName == "jpeg" || lowerExtName == "jpg" || lowerExtName == "png" || lowerExtName == "gif" || lowerExtName == "bmp"){
				//正常分支
				ajaxFileUpload();
				return;
			}
		}
		//填写文件扩展名不对的异常分支
		alert($("#available_file_desc").val());
	}
};

tinyMCEPopup.onInit.add(UploadDialog.init, UploadDialog);
