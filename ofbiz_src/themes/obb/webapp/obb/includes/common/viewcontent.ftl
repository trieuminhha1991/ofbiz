<article class="article">
	<h1>
		<#if content?exists>
			${StringUtil.wrapString(content.contentName)}
		</#if>
	</h1>
	<div class="timeupdate">
		<span>Lúc ${content.ago?if_exists}</span>
		<div>
			• <i class="allicon-comm"></i>
			<label>${content.totalComment?if_exists}</label>
		</div>
		<div>
			• <i class="allicon-view"></i>
			<label>5.752</label>
		</div>
	</div>
	<#if content?exists>
		${StringUtil.wrapString(content.longDescription)}
	</#if>
	<div class="likeshare">
		<div class="fb-like" data-share ="true" data-action="like" data-layout="button_count" data-href="/DetailsProduct.html"></div>
	</div>
	<div class="clearfix"></div>

</article>
