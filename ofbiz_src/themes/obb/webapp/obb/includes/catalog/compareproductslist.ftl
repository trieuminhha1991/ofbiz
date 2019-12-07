<div class="block block-list block-compare">
    <div class="block-title">
        <strong><span>${uiLabelMap.ProductCompareProducts}</span></strong>
    </div>
    <div class="block-content">
	<#assign productCompareList = Static["org.ofbiz.product.product.ProductEvents"].getProductCompareList(request)/>
	<#if productCompareList?has_content>
		<ol id="compare-items">
			<#list productCompareList as product>
				${Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "PRODUCT_NAME", request)}
				<li class="item <#if product_index%2==0>odd<#else>even</#if>">
					<form method="post" action="<@ofbizUrl>removeFromCompare</@ofbizUrl>" name="removeFromCompare${product_index}form">
			              <input type="hidden" name="productId" value="${product.productId}"/>
			            </form>
					<a href="javascript:document.removeFromCompare${product_index}form.submit()" title="${uiLabelMap.CommonRemove}" class="btn-remove" onclick="return confirm('Are you sure you would like to remove this item from the compare products?');">
						<i class="fa fa-times"></i>
						</a>
						<p class="product-name"><a href="#"> UPS Ground</a></p>
				</li>
			</#list>
		</ol>
		<div class="actions">
	            <a href="<@ofbizUrl>clearCompareList</@ofbizUrl>" onclick="return confirm('Are you sure you would like to remove all products from your comparison?');">${uiLabelMap.CommonClearAll}</a>
	            <button type="button" title="Compare" class="button" onclick="javascript:popUp('<@ofbizUrl secure="${request.isSecure()?string}">compareProducts</@ofbizUrl>', 'compareProducts', '650', '750')"><span><span>${uiLabelMap.ProductCompareProducts}</span></span></button>
	        </div>
	<#else/>
		<p class="empty">${uiLabelMap.ProductNoProductsToCompare}.</p>
        </#if>
    </div>
</div>