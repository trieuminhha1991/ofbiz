<h3 class="ret">Sản phẩm tương tự</h3>
<ul class="listproduct related">
	<#if relatedProducts?exists>
		<#list relatedProducts as productCategoryMember>
        ${setRequestAttribute("optProductId", productCategoryMember.productId)}
        ${setRequestAttribute("productCategoryMember", productCategoryMember)}
        ${setRequestAttribute("listIndex", productCategoryMember_index)}
        ${screens.render(productsummaryScreen2)}
        </#list>
	</#if>
</ul>