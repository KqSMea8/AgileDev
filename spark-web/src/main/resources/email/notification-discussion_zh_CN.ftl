<style type="text/css"> 
	#notification-discussion-body {overflow: hidden; zoom: 1; font-size: 12px; font-family: Verdana;font-style: normal; font-variant: normal; margin:0; padding:0;}
	#notification-discussion-body .notification{ font-size: 13px; font-weight: bold; margin: 0px 8px 10px 0px; padding: 0; }
	#notification-discussion-body .card-info { border: 1px solid #ddb; background: #ffe; margin: 10px 5px; padding: 8px; zoom: 1; line-height: 100%;}
	#notification-discussion-body .message{ color: #999;font-size:12px;  margin: 0px 8px 0px 16px; padding: 0;}
</style> 

<div id="notification-discussion-body">
	<div class="notification">
		${notifyMessage}
	</div>
	<div class="notification">
	${user.name} 于 ${discussion.lastModifyTime?if_exists?string("yyyy-MM-dd HH:mm")} 评论了${card.space.name}中的卡片 <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}">${card.title}</a>，评论内容如下：
	</div>
	<div class="card-info">
	<p>
	${discussion.content}
	</p>
	</div>
	<p  class="message">
	此邮件由<a style="color: #888" href="mailto:${user.email}">${user.name}</a>通过Spark发出，如果您觉得此邮件打搅了您，请联系发送者
	</p>
</div>