<#if verticalBanner?has_content>
	<div class="banner-item">
		<a href="${StringUtil.wrapString((verticalBanner.url)?if_exists)}">
			<img src="${StringUtil.wrapString((verticalBanner.originalImageUrl)?if_exists)}"/>
		</a>
	</div>
</#if>