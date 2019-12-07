<#if relatedProducts?has_content>
<div class="navinews">
	<h1 class='navi-header'>
		<a href="javascript:void(0)" title="">
			${uiLabelMap.BERelatedProducts}
		</a>
	</h1>
	<div class='navi-content'>
	<ul class="listproduct related">
		<#if relatedProducts?exists>
			<#list relatedProducts as productCategoryMember>
		        ${setRequestAttribute("optProductId", productCategoryMember.productId)}
		        ${setRequestAttribute("productCategoryMember", productCategoryMember)}
		        ${setRequestAttribute("listIndex", productCategoryMember_index)}
		        ${screens.render(productsummaryScreen)}
	        </#list>
		</#if>
	</ul>
	</div>
</div>
</#if>