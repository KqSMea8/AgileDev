<style type="text/css"> 
	#notification-card-body {overflow: hidden; zoom: 1; font-size: 12px; font-family: Verdana;font-style: normal; font-variant: normal; margin:0; padding:0;}
	#notification-card-body .notification{ font-size: 13px; font-weight: bold; margin: 0px 8px 10px 0px; padding: 0; }
	#notification-card-body .card-info { border: 1px solid #ddb; background: #ffe; margin: 5px 8px 10px 0px; padding: 0; zoom: 1; line-height: 100%;}
	#notification-card-body .message{ color: #999;font-size:12px;  margin: 0px 8px 0px 16px; padding: 0;}
	#notification-card-body .attachment-note (margin: 10px 5px 10px 30px;)
	#card-contents{margin:0; padding:0;}
	#card-contents table {overflow: hidden; zoom: 1; font-size: 12px; font-family: Verdana;font-style: normal; font-variant: normal; margin:0; padding:0;}
	#card-contents table.card-top { width:100%;  background: #FFFFBB; font-size: 15px; color: #056 ; margin:0; padding: 5px 0px 5px 0px; zoom: 1; line-height: 100%;}
	#card-contents table.card-top .title{ font-size: 15px; font-weight:bold; overflow: hidden;  zoom: 1;}
	#card-contents table.card-detail { border-width:1px 0px; border-style:dotted; border-color: #d7d7d7; margin: 0; padding: 10px 0px 0px 0px; min-height: 32px;  width: 100%; word-break: break-all; word-wrap: break-word; }
	#card-contents table.card-properties { width: 100%; font-size: 12px; font-family: Verdana;font-style: normal; font-variant: normal; overflow: hidden; magrin:0px; padding: 10px 0px 8px 8px;}
	#card-contents table.card-properties td.label { color: #555; width: 15%; font-weight: bold; text-align: right; min-height: 32px; word-break: break-all; word-wrap: break-word; padding:7px 0px 7px 8px;  }
	#card-contents table.card-properties td.value { color: #bb0000; width: 35%; margin-left: 8px; min-height: 32px; word-break: break-all; word-wrap: break-word; padding:7px 0px 7px 8px; }
</style> 
<div id="notification-card-body">
	<div class="notification">
		${notifyMessage}
	</div>
	<div class="notification">
	${user.name} 于 ${card.lastModifiedTime?if_exists?string("yyyy-MM-dd HH:mm")} 操作了${card.space.name} 中的卡片 <a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}">${card.title}</a>，操作内容如下：
	</div>
	<div class="card-info">
		<ul>
			<#list diff.infoList as d>
			<li>${d}</li>
			</#list>
		</ul>
	</div>
	<div class="notification">
	卡片信息如下：
	</div>
	<div id="card-contents" class="card-info">
			<table class="card-top" >
				<tr><td>
				[${card.type.name}]  <span class="title">${card.title}</span> 
				</td></tr>
			</table>
			<table class="card-detail">
				<tr><td>
				${card.detail}
				</td></tr>
			</table>
			<table class="card-properties">
						<#list cardPropertyValueList as property >
							<#if property_index%2==0 >
								<tr>
							</#if>
							<td class="label">${ property.cardProperty.name }:</td>
							<td class="value">${ property.displayValue }</td>
							<#if property_index%2==1 >
								</tr>
							</#if>
						</#list>
						<tr>
							<#if card.project?exists>
							<td class="label">所属项目: </td>
							<td class="value">${ card.project.name } </td>
							</#if>
							<#if (parentSequence?exists && parentName?exists)>
							<td class="label">所属上级:</td> 
							<td class="value">#${parentSequence}-${parentName} </td>
							</#if>
						</tr>
			</table>	
	</div>
	<#if (attachments?size>0) >
	<div class="notification">
		添加附件如下：
	</div>
	<div class="card-info">
		<ul>
			<#list attachments as attachment >
			<li>
			<a href="${serverUrlPrefix}/spaces/${card.space.prefixCode}/cards/${card.sequence}/attachments/${attachment.id}/download">${attachment.originalName}</a>
			<#if (attachment.note?length>0)>
				<p class="attachment-note">${attachment.note}</p>
			</#if>
			</li>
			</#list>
		</ul>
	</div>
	</#if>
	<p class="message">
	此邮件由&nbsp;<a style="color: #888" href="mailto:${user.email}">${user.name}</a>&nbsp;通过Spark发出，如果您觉得此邮件打搅了您，请联系发送者
	</p>
</div>

