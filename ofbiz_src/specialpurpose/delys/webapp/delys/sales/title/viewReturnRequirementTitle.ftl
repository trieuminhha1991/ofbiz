<#if requirement?exists && requirement?has_content>
	<i class="icon-angle-right"></i> ${uiLabelMap.DAId}: <a href="<@ofbizUrl>viewReturnProductReq?requirementId=${requirement.requirementId}</@ofbizUrl>">${requirement.requirementId}</a>
</#if>