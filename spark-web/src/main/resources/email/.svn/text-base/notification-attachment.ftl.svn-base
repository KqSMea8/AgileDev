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
		${user.name}  updates an attachment in Card  <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}">${card.title}</a> of Space ${card.space.name} at  ${attachment.uploadTime?if_exists?string("yyyy-MM-dd HH:mm")}： 
	<#else>
		${user.name}  upload an attachment in Card  <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}">${card.title}</a> of Space ${card.space.name} at  ${attachment.uploadTime?if_exists?string("yyyy-MM-dd HH:mm")}：
	</#if>
	</div>
	<div class="card-info">
	<p>
	<#if isReplace>
		Change attachment from ${oldAttachment.originalName} to ${attachment.originalName}  &nbsp;&nbsp; 
		<a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}/attachments/${attachment.id}/download">Click here to download</a>
		<#if (attachment.note?length>0)>
				<p class="attachment-note">${attachment.note}</p>
		</#if>
	<#else>
		Upload attachment  ${attachment.originalName} &nbsp;&nbsp; 
		<a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}/attachments/${attachment.id}/download">Click here to download</a>
		<#if (attachment.note?length>0)>
				<p class="attachment-note">${attachment.note}</p>
		</#if>
	</#if>
	</p>
	</div>
	<p  class="message">
	This email is sent by &nbsp;<a style="color: #888" href="mailto:${user.email}">${user.name}</a>&nbsp; through Spark&nbsp;, contact sender if it troubles you.	</p>
	<p></p>
</div>