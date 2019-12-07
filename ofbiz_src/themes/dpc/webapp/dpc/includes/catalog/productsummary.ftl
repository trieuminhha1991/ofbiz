<#if product?exists>
    <#-- variable setup -->
    <#if backendPath?default("N") == "Y">
        <#assign productUrl><@ofbizCatalogUrl productId=product.productId productCategoryId=categoryId/></#assign>
    <#else>
        <#assign productUrl><@ofbizCatalogAltUrl productId=product.productId productCategoryId=categoryId/></#assign>
    </#if>

    <#if requestAttributes.productCategoryMember?exists>
        <#assign prodCatMem = requestAttributes.productCategoryMember>
    </#if>
    <#assign mediumImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists>
    <#if !mediumImageUrl?string?has_content><#assign mediumImageUrl = "/images/defaultImage.jpg"></#if>
    <#-- end variable setup -->
    <#assign productInfoLinkId = "productInfoLink">
    <#assign productInfoLinkId = productInfoLinkId + product.productId/>
    <#assign productDetailId = "productDetailId"/>
    <#assign productDetailId = productDetailId + product.productId/>
    <li>
	<form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform" style="margin: 0;">
		<input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}"/>
			  <input type="hidden" name="quantity" value="${prodCatMem.quantity?if_exists}"/>
			  <input type="hidden" name="clearSearch" value="N"/>
			  <input type="hidden" name="mainSubmitted" value="Y"/>
		    <a href="${productUrl}" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">
				<img width="388" height="170" src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${mediumImageUrl}</@ofbizContentUrl>"
					alt="${productContentWrapper.get("PRODUCT_NAME")?if_exists}" />
		        <h3>${productContentWrapper.get("PRODUCT_NAME")?if_exists}</h3>
		        <div class="main-product-price">
					<#if totalPrice?exists>
		              <div>${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice'><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
		            <#else>
		            <#if price.competitivePrice?exists && price.price?exists && price.price?double < price.competitivePrice?double>
		              ${uiLabelMap.ProductCompareAtPrice}: <span class='basePrice'><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
		            </#if>
		            <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
		              <span class="price-old"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span>
		            </#if>
		            <b>
		              <#if price.isSale?exists && price.isSale>
		                <#assign priceStyle = "salePrice">
		              <#else>
		                <#assign priceStyle = "regularPrice">
		              </#if>

		              <#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
		                <#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><span class="${priceStyle}"><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
		              </#if>
		            </b>
		            <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
		              <#assign priceSaved = price.listPrice?double - price.price?double>
		              <#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
		                <span>(${percentSaved?int}%)</span>
		            </#if>
		            </#if>
		            <#-- show price details ("showPriceDetails" field can be set in the screen definition) -->
		            <#if (showPriceDetails?exists && showPriceDetails?default("N") == "Y")>
		                <#if price.orderItemPriceInfos?exists>
		                    <#list price.orderItemPriceInfos as orderItemPriceInfo>
		                        <div>${orderItemPriceInfo.description?if_exists}</div>
		                    </#list>
		                </#if>
		            </#if>
				</div>
		    </a>
		    <button type="button" onclick="javascript:document.the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform.submit()">Mua ngay</button>
	    </form>
    </li>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br />
</#if>