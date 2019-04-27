Sorry,Error occurs while synchronizing user infomations from UIC, the following ${infos?size}messages has not successfully stored in ${systemName}(${serverUrlPrefix}).
<div style="border: 1px solid #ddb; background: #ffe; margin: 16px 24px;">
<ul>
<#list infos as info>
	<li>${info}</li>
</#list>
</ul>
</div>
You can trace the original data in UIC through<a style="color: #888" href="http://uuap.baidu.com/admin/users/list">UUAP ADMIN</a>
<p style="color: #999;font-size:12px;">
This email is sent by <a style="color: #888" href="${systemURL}">${systemName}</a> System automatically.
</p>
