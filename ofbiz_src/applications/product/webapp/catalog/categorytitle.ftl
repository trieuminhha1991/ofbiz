
<#--
<div id="page-content" class="clearfix padding-0-imp">
	<div class="page-header position-relative">
		<h1 class="margin-left-nav-1-ipt">
			${uiLabelMap[labelTitleProperty]} ${uiLabelMap.CommonFor} 
			<small>
				<i class="icon-double-angle-right"></i>
				<#if productCategory.categoryName?has_content> ${productCategory.categoryName}</#if>
	 			[${uiLabelMap.CommonId}:${productCategoryId}]
	 		</small>
		 </h1>
	</div>
</div>
-->

<#if productCategory.categoryName?has_content || (productCategoryId?exists && productCategoryId?has_content)>
	<i class="icon-angle-right"></i>
</#if>
<#if productCategory.categoryName?has_content>
	
	<#if productCategory.categoryName?length &gt; 5 >
		<span class="display-inline-block hover-name" title="${productCategory.categoryName}">${productCategory.categoryName?substring(0, 5)}...</span>
	<#else>
		${productCategory.categoryName}
	</#if>
</#if>
<#if productCategoryId?exists && productCategoryId?has_content>
	[${uiLabelMap.CommonId}:${productCategoryId}]
<#else>
	${uiLabelMap.ProductNewCategory}
</#if>
