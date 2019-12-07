<div class="usage_tips">
	<h2>${topRightTitle}</h2>
	<#list topRightContent as content>
		<a href="#" title="">
			<#if content.images == "">
				<img width="140" height="85" alt="" src="/dpc/DemoImg/default.jpg">
			<#else>
				<img width="140" height="85" alt="" src="${content.images}">
			</#if>
			<h3>${content.contentName}</h3>
			<div>
				<i class="allicon-comm"></i>
				<label>${content.totalComment?if_exists}</label>
			</div>
		</a>
	</#list>
</div>