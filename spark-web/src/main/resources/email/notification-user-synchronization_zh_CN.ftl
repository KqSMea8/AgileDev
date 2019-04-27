对不起,UIC用户数据向${systemName}（${serverUrlPrefix}）同步过程中发生了错误. 共有${infos?size}条数据同步失败，具体如下：
<div style="border: 1px solid #ddb; background: #ffe; margin: 16px 24px;">
<ul>
<#list infos as info>
		<li>${info}</li>
</#list>
</ul>
</div>
数据同步失败的原因可能是username为空等，您可以通过<a style="color: #888" href="http://uuap.baidu.com/admin/users/list">UUAP ADMIN</a>查看UIC中的原始数据
<p style="color: #999;font-size:12px;">
本邮件由<a style="color: #888" href="${systemURL}">${systemName}</a>系统自动发送。
</p>
