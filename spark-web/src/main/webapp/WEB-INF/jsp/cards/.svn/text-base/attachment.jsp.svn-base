<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/pages/cards/attachment.js?1109"></script>
<div id="attachments_div">
	<div id="attachments_list"></div>
</div>
<div id="uploaderContainer">
		<div class="attach_input">
		<fmt:message key="card.attachment"></fmt:message> : <input type="file" id="attachmentReuploadFile" name="attachmentReuploadFile" size="40" />
	</div>
	<div class="attach_input">
		<fmt:message key="card.attachment.note"></fmt:message> : <textarea id="attachmentReuploadNote" name="attachmentReuploadNote"></textarea>
		<input type="hidden" id="old_attachment_id" name="old_attachment_id">
		<div class="weak right">
		<fmt:message key="card.attachment.validation"></fmt:message>
		</div>
	</div>
</div>
<div style='display:none;' id='attachment-dialog-confirm' title='<fmt:message key="card.attachment"></fmt:message>'>
	<span class='ui-icon ui-icon-alert' style='float: left; margin: 0 7px 20px 0;'></span>
	<div id='attachment-dialog-confirm-content'></div>
</div>
