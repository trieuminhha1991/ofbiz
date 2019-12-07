<#--
<div id="page-content" class="clearfix padding-0-imp">
	<div class="page-header position-relative">
		<h1>
			<#if labelTitleProperty?has_content>${uiLabelMap[labelTitleProperty]}</#if> 
			${uiLabelMap.CommonFor} <small><i class="icon-double-angle-right"></i>
			<#if product?has_content>
				<#if product.internalName?has_content> ${product.internalName}</#if>
			</#if> 
			[${uiLabelMap.CommonId}:<#if productId?has_content> ${productId}</#if>] </small>
		</h1>
	</div>
</div>
<#if labelTitleProperty?has_content>${uiLabelMap[labelTitleProperty]}</#if>
-->
		
<#if product?has_content || productId?has_content>
	<i class="icon-angle-right"></i>
</#if>
<#if product?has_content>
	<#if product.internalName?has_content>
		<#if product.internalName?length &gt; 25 >
			 <span class="display-inline-block hover-name" title="${product.internalName}">${product.internalName?substring(0, 25)}...</span>
		<#else>
			 ${product.internalName}
		</#if>
	</#if>
</#if>
<#if productId?has_content>
	[${uiLabelMap.CommonId}: ${productId}]
<#else>
	<i class="icon-angle-right"></i> ${uiLabelMap.ProductNewProduct}
</#if>
