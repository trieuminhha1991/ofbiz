<#if horizontalBanner?has_content>
	<div class='middle-banner'>
		<a href="${StringUtil.wrapString((horizontalBanner.url)?if_exists)}">
			<img src="${StringUtil.wrapString((horizontalBanner.originalImageUrl)?if_exists)}"/>
		</a>
	</div>
</#if>