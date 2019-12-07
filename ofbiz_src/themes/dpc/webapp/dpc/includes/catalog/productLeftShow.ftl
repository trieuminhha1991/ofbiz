<#if product?exists>
	<#-- variable setup -->
    <#if backendPath?default("N") == "Y">
        <#assign productUrl><@ofbizCatalogUrl productId=product.productId productCategoryId=productCategoryId/></#assign>
    <#else>
        <#assign productUrl><@ofbizCatalogAltUrl productId=product.productId productCategoryId=productCategoryId/></#assign>
    </#if>

    <#if requestAttributes.productCategoryMember?exists>
        <#assign prodCatMem = requestAttributes.productCategoryMember>
    </#if>
    <#assign smallImageUrl = productContentWrapper.get("SMALL_IMAGE_URL")?if_exists>
    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
    <#-- end variable setup -->
    <#assign productInfoLinkId = "productInfoLink">
    <#assign productInfoLinkId = productInfoLinkId + product.productId/>
    <#assign productDetailId = "productDetailId"/>
    <#assign productDetailId = productDetailId + product.productId/>
		<li>
		<div>
			<div class="image">
				<a href="${productUrl}">
					<img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="${product.productName?if_exists}">
				</a>
			</div>
            <div class="name">
		<a href="${productUrl}">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a>
		</div>
		<#if averageRating?exists && (averageRating?double > 0) && numRatings?exists && (numRatings?long > 1)>
		        <div class="rating">
		          <#if (averageRating?double < 0.5)>
		            <img src="/bigshop/images/stars-0.png" alt="${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})">
		          <#elseif (averageRating?double >= 0.5 && averageRating?double < 1.5)>
				<img src="/bigshop/images/stars-1.png" alt="${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})">
		          <#elseif (averageRating?double >= 1.5 && averageRating?double < 2.5)>
		            <img src="/bigshop/images/stars-2.png" alt="${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})">
		          <#elseif (averageRating?double >= 2.5 && averageRating?double < 3.5)>
		            <img src="/bigshop/images/stars-3.png" alt="${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})">
		          <#elseif (averageRating?double >= 3.5 && averageRating?double < 4.5)>
		            <img src="/bigshop/images/stars-4.png" alt="${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})">
		          <#else>
		            <img src="/bigshop/images/stars-5.png" alt="${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})">
		          </#if>
		        </div><br />
	        </#if>
            <div class="price">

		<#if price.isSale?exists && price.isSale>
			<span class="price-old"><@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span>
                  <#else>
                  </#if>
                  <#--<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                  <#assign priceSaved = price.listPrice?double - price.price?double>
	                  <#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
	                    <span>(${percentSaved?int}%)</span>
	                </#if> -->
		<#if totalPrice?exists>
                  <@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/>
                <#else>

	                <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                </#if>
		            <#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
		                <#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>
		            </#if>
                </#if>
		</div>
            <div class="cart"><input type="button" value="Add to Cart" onclick="addToCart('42');" class="button"></div>
	</div>
	</li>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br />
</#if>
