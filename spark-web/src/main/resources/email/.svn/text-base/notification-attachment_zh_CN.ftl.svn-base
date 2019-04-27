<style type="text/css"> 
	#notification-attachment-body {overflow: hidden; zoom: 1; font-size: 12px; font-family: Verdana;font-style: normal; font-variant: normal; margin:0; padding:0;}
	#notification-attachment-body .notification{ font-size: 13px; font-weight: bold; margin: 0px 8px 10px 0px; padding: 0; }
	#notification-attachment-body .card-info { border: 1px solid #ddb; background: #ffe; margin: 10px 5px; padding: 8px; zoom: 1; line-height: 100%;}
	#notification-attachment-body .message{ color: #999;font-size:12px;  margin: 0px 8px 0px 16px; padding: 0;}
	#notification-attachment-body .attachment-note (margin: 10px 5px 10px 30px;)
</style> 

<div id="notification-attachment-body">
	<div class="notification">
		${notifyMessage}
	</div>
	<div class="notification">
	<#if isReplace>
		${user.name} 于 ${attachment.uploadTime?if_exists?string("yyyy-MM-dd HH:mm")} 对 ${card.space.name} 中的卡片 <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}">${card.title}</a> 更新了附件：
	<#else>
		${user.name} 于 ${attachment.uploadTime?if_exists?string("yyyy-MM-dd HH:mm")} 对 ${card.space.name} 中的卡片 <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}">${card.title}</a> 追加了附件：
	</#if>
	</div>
	<div class="card-info">
	<p>
	<#if isReplace>
		将附件 ${oldAttachment.originalName}替换为  ${attachment.originalName}&nbsp;&nbsp; 
		<a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}/attachments/${attachment.id}/download">点击下载</a>
		<#if (attachment.note?length>0)>
				<p class="attachment-note">${attachment.note}</p>
		</#if>
	<#else>
		上传附件为：   ${attachment.originalName} &nbsp;&nbsp; 
		<a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}/attachments/${attachment.id}/download">点击下载</a>
		<#if (attachment.note?length>0)>
				<p class="attachment-note">${attachment.note}</p>
		</#if>
	</#if>
	</p>
	</div>
	<p  class="message">
	此邮件由<a style="color: #888" href="mailto:${user.email}">${user.name}</a>通过Spark发出，如果您觉得此邮件打搅了您，请联系发送者
	</p>
</div>