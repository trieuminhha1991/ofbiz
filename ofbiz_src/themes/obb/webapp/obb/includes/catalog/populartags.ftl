<#if tagCloudList?has_content>
	<div class="block block-tags">
	    <div class="block-title">
	        <strong><span>${uiLabelMap.ObbTags}</span></strong>
	    </div>
	    <div class="block-content">
	        <ul class="tags-list">
			<#list tagCloudList as tagCloud>
				<li><a href="<@ofbizUrl>tagsearch?SEARCH_STRING=${tagCloud.tag}&amp;keywordTypeId=KWT_TAG&amp;statusId=KW_APPROVED&amp;hoz=Y</@ofbizUrl>" style="font-size:${tagCloud.fontSize}pt;">${tagCloud.tag}</a></li>
		        </#list>
	        </ul>
	    </div>
	</div>
</#if>
