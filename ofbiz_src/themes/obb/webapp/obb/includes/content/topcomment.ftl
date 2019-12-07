<aside class="colproduct">

	<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(verticalBanner)>
		<a target="_blank" href="#"><img width="210" height="635" alt="" src="${StringUtil.wrapString((verticalBanner.originalImageUrl)?if_exists)}" style="cursor:pointer"></a>
	</#if>
	<h4>Thảo luận nhiều</h4>
	<div class="discuss">
		<#list posttopcomment as content>
			<a href="<@ofbizUrl>viewcontent?pid=${content.contentId}</@ofbizUrl>" title="">
				<h3>${StringUtil.wrapString((content.contentName)?if_exists)} <label><i class="allicon-comm"></i><span>${(content.totalComment)?if_exists}</span></label></h3>
			</a>
		</#list>
	</div>
</aside>