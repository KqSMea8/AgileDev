<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>{#upload_dlg.title}</title>
	<script type="text/javascript" src="../../tiny_mce_popup.js"></script>
	<script type="text/javascript" src="../../../../../jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="../../../../../jqueryfileupload/ajaxfileupload.js"></script>
	<script type="text/javascript" src="js/dialog.js"></script>
	<script type="text/javascript">
		function ajaxFileUpload(){
			$("#loading")
			.ajaxStart(function(){
				$(this).show();
			})
			.ajaxComplete(function(){
				$(this).hide();
			});
			$.ajaxFileUpload({
				url:'<%= request.getContextPath() %>/uploadfile/uploadimage',
				//url:'upload.jsp',
				secureuri:false,
				fileElementId:'fileToUpload',
				dataType: 'json',
				success: function (data, status){
					if(typeof(data.error) != 'undefined'){
						if(data.error != ''){
							alert(data.error);
						}else{
							alert(data.msg);
						}
						return;
					}
					var insertValue = data.msg;
					insertValue = "<img src='<%= request.getContextPath() %>/upload/" + insertValue + "'>";
					tinyMCEPopup.editor.execCommand('mceInsertContent', false, insertValue);
					tinyMCEPopup.close();
				},
				error: function (data, status, e){
					alert(e);
				}
			})
			return false;
		}
	</script>
</head>
<body>

<form onsubmit="UploadDialog.insert();return false;" action="#">
	<p>{#upload_dlg.available_file_desc}</p>
	<p><input id="fileToUpload" type="file" name="fileToUpload" value=""/></p>
	<input id="available_file_desc" type="hidden" value="{#upload_dlg.available_file_desc}"/>
	<input id="no_file" type="hidden" value="{#upload_dlg.no_file}"/>
	<div class="mceActionPanel">
		<input type="button" id="insert" name="insert" value="{#insert}" onclick="UploadDialog.insert();" />
		<input type="button" id="cancel" name="cancel" value="{#cancel}" onclick="tinyMCEPopup.close();" />
	</div>
</form>

</body>
</html>
