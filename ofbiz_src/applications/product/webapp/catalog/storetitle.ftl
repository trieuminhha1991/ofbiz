<#-- 
<div id="page-content" class="clearfix padding-0-imp">
	<div class="page-header position-relative">
		<h1 class="margin-left-nav-1-ipt">
		${uiLabelMap[labelTitleProperty]} ${uiLabelMap.CommonFor} 
		<small>
		<i class="icon-double-angle-right"></i>
		<#if productStore.storeName?has_content> ${productStore.storeName}</#if> 
		[${uiLabelMap.CommonId}:<#if productStoreId?has_content>${productStoreId}] </#if></small></h1>
	</div>
</div>
-->

<#if productStore.storeName?has_content || productStoreId?has_content>
<i class="icon-angle-right"></i>
</#if>
<#--
<#if productStore.storeName?has_content>
	<#if productStore.storeName?has_content>
		<#if productStore.storeName?length &gt; 25 >
			 <span class="display-inline-block hover-name" title="${productStore.storeName}">${productStore.storeName?substring(0, 25)}...</span>
		<#else>
			 ${productStore.storeName}
		</#if>
	</#if>
</#if>
<#if productStoreId?has_content>
	[${uiLabelMap.CommonId}: ${productStoreId}]
<#else>
	<i class="icon-angle-right"></i> ${uiLabelMap.ProductNewProductStore}
</#if>
-->
<#if productStoreId?has_content>
	${uiLabelMap.CommonId}: <a href="<@ofbizUrl>editProductStore?productStoreId=${productStoreId}</@ofbizUrl>">${productStoreId}</a>
<#else>
	<i class="icon-angle-right"></i> ${uiLabelMap.ProductNewProductStore}
</#if>
