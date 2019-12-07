<div id="tab_content_product_comments" class="tab-pane fb-comments-title">${uiLabelMap.BSCommentsAboutProduct}:</div>
<div class="fb-comments"
	data-href="${prefixUrl}<@ofbizUrl>${currentUrl?if_exists}</@ofbizUrl>"
	data-numposts="<#if (facebookSettings.fbCommentNumber)?exists>${facebookSettings.fbCommentNumber}<#else>10</#if>"
	data-colorscheme="<#if (facebookSettings.fbTheme)?exists>${facebookSettings.fbTheme}<#else>light</#if>"
	data-width="<#if (facebookSettings.fbCommentWidth)?exists>${(facebookSettings.fbCommentWidth)}<#else>100%</#if>">
</div>
