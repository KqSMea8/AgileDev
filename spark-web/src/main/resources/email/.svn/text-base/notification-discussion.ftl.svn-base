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
	${user.name} commented on${card.title} in <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}">${card.space.name}</a> at   ${discussion.lastModifyTime?if_exists?string("yyyy-MM-dd HH:mm")}, the details：
	</div>
	<div class="card-info">
	<p>
	${discussion.content}
	</p>
	</div>
	<p  class="message">
	This email is sent by &nbsp;;<a style="color: #888" href="mailto:${user.email}">${user.name}</a>&nbsp; through &nbsp;Spark&nbsp;, contact sender if it troubles you.	</p>
	<p></p>
</div>
