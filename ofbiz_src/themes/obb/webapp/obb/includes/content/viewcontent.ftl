<div class='content-body'>
<#if content?exists>
<article class="article">
	<h1>
		${StringUtil.wrapString(content.contentName?if_exists)}
	</h1>
	<div class="timeupdate">
		<#if (content.ago)?exists><p><i class="fa fa-calendar">&nbsp;</i>${content.ago}</p></#if>
	</div>
	<#if content?exists>
		${StringUtil.wrapString((content.longDescription)?if_exists)}
	</#if>
	<div class="clearfix"></div>
</article>
<#else>
${uiLabelMap.BEContentNotFound}
</#if>
</div>