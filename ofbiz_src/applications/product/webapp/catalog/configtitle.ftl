<#--<div id="page-content" class="clearfix padding-0-imp">
	<div class="page-header position-relative">
		<h1 class="margin-left-nav-1-ipt">
		<#if labelTitleProperty?has_content>
		${uiLabelMap[labelTitleProperty]}
		</#if>
		 ${uiLabelMap.CommonFor} 
		<small>
		<i class="icon-double-angle-right"></i>
		 <#if configItem.configItemName?has_content>${configItem.configItemName}
		 </#if> [${uiLabelMap.CommonId}:<#if configItemId?has_content>${configItemId}
		 </#if>] </small></h1>
	</div>
</div>-->

<i class="icon-angle-right"></i>
	<#if configItem?exists && configItem.configItemName?has_content>
		<#if configItem.configItemName?length &gt; 25 >
			<span class="display-inline-block hover-name" title="${configItem.configItemName}">${configItem.configItemName?substring(0, 25)}...</span>
		<#else>
			${configItem.configItemName}
		</#if>		
	</#if>
<#if configItemId?has_content>[${uiLabelMap.CommonId}: ${configItemId}]<#else>New Product</#if>

