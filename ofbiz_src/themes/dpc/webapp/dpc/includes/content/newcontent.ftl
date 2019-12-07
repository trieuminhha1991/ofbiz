<ul class="catenews">
	<#list contentbycategory as content>
		<li class='<#if content_index == 0>hero</#if>'>
			<a href="viewcontent?pid=${content.contentId}" title="">
				<#if content.images == "">
					<img width="230" height="130" alt="" src="/dpc/DemoImg/default.jpg">
				<#else>
					<img width="230" height="130" alt="" src="${StringUtil.wrapString((content.images)?if_exists)}">
				</#if>

				<h3>${content.contentName}</h3>
				<figure style="height: 35px;">
					${StringUtil.wrapString((content.description)?if_exists)}
				</figure>
				<span>Hữu Tình</span> <span>• ${(content.ago)?if_exists}</span>
				<div>
					• <i class="allicon-comm"></i>
					<label>${(content.totalComment)?if_exists}</label>
				</div>
			</a>
		</li>
	</#list>
</ul>
