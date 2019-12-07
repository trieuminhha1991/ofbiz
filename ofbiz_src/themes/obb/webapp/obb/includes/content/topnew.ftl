<ul class="latestnews">
	<#if hotContents?exists>
		<#list hotContents as content>
		<li>
			<a href="<@ofbizUrl>viewcontent?pid=${(content.contentId)?if_exists}</@ofbizUrl>" title="${StringUtil.wrapString((content.contentName)?if_exists)}">
				<img width="240" height="135" alt="" src="${StringUtil.wrapString((content.originalImageUrl)?if_exists)}">
				<h2 title="${StringUtil.wrapString((content.contentName)?if_exists)}">
					${StringUtil.wrapString((content.contentName)?if_exists)}
				</h2>
				<span>${StringUtil.wrapString((content.author)?if_exists)}</span>
				<div>
					• <i class="allicon-comm"></i>
					<label>${(content.numberOfComments)?if_exists}</label>
				</div>
			</a>
		</li>
		</#list>
	</#if>
</ul>
