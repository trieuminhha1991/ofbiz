<#if returnHeader?exists && returnHeader?has_content>
	<i class="icon-angle-right"></i> ${uiLabelMap.DAId}: <a href="<@ofbizUrl>viewReturnOrderGeneral?returnId=${returnHeader.returnId}</@ofbizUrl>">${returnHeader.returnId}</a>
</#if>