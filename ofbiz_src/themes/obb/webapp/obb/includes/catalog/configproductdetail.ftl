<#-- variable setup -->
<#assign productContentWrapper = productContentWrapper?if_exists>
<#assign price = priceMap?if_exists>
<#-- end variable setup -->

<#-- virtual product javascript -->
${virtualJavaScript?if_exists}
<#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists>
<#if firstLargeImage?has_content>
  <#assign productLargeImageUrl = firstLargeImage>
</#if>
<script type="text/javascript">// <![CDATA[
	jQuery.noConflict();
	jQuery(document).ready(function($){
		var optionsZoom2 = new Product.Zoom('image', 'track', 'handle', 'zoom_in', 'zoom_out', 'track_hint');
	});
// ]]></script>
<div id="messages_product_view"></div>
<div class="product-view" style="width: 1000px">
	<div class="product-essential">
		<div class="product-img-box">
			<p class="product-image product-image-zoom" style="width:370px; height:370px;">
				<#if productLargeImageUrl?string?has_content>
				<img id="image" src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" alt="${productContentWrapper.get("PRODUCT_NAME")?if_exists}" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">
			<#else>
                    <img id="image" src="/images/defaultImage.jpg" alt="${productContentWrapper.get("PRODUCT_NAME")?if_exists}" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">
                </#if>
		    </p>
		    <p class="zoom-notice" id="track_hint">Click đúp để xem ảnh lớn</p>
			<div class="zoom">
			    <img id="zoom_out" src="/obbresources/skin/frontend/default/blue/images/slider_btn_zoom_out.gif" alt="Thu nhỏ" title="Thu nhỏ" class="btn-zoom-out" />
			    <div id="track">
			        <div id="handle"></div>
			    </div>
			    <img id="zoom_in" src="/obbresources/skin/frontend/default/blue/images/slider_btn_zoom_in.gif" alt="Phóng to" title="Phóng to" class="btn-zoom-in" />
			</div>
		</div>
		<div class="product-shop">
		<div class="product-name">
	            <h1>
					<a href="<@ofbizUrl>productdetail?pid=${product.productId?if_exists}</@ofbizUrl>" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">
						${productContentWrapper.get("PRODUCT_NAME")?if_exists}
					</a>
				</h1>
	        </div>
	        <div class="ratings">
		          <div class="rating-box">
			          <#if averageRating?exists && (averageRating &gt; 0) && numRatings?exists && (numRatings &gt; 1)>
					<div class="rating" style="width:${averageRating/0.05}%"></div>
				</div>
				<p class="rating-links">
				            <a href="javascript:void(0);"><#if numRatings?number < 2>${numRatings} ${uiLabelMap.ObbReview}<#else>${numRatings} ${uiLabelMap.ObbReviews}</#if></a>
							<span class="separator">|</span>
				            <a class="link-review" href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>">${uiLabelMap.ObbAddYourReview}</a>
				        </p>
			          <#else>
						<div class="rating"></div>
					</div>
					<p class="rating-links">
				            <a href="javascript:void(0);">0 ${uiLabelMap.ObbReview}</a>
							<span class="separator">|</span>
				            <a class="link-review" href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>">${uiLabelMap.ObbProductBeTheFirstToReviewThisProduct}</a>
				        </p>
			          </#if>
	        </div>
	        <#if totalPrice?exists>
		        <div class="price-box"><span class="regular-price" id="product-price-28"><span class="price"><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></span></div>
		    <#else>
				<#if price.competitivePrice?exists && price.price?exists && price.price < price.competitivePrice>
				  <div class="price-box"><span class="regular-price" id="product-price-28"><span class="price">${uiLabelMap.ProductCompareAtPrice}: <@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span></span></div>
				</#if>
				<#if price.listPrice?exists && price.price?exists && price.price < price.listPrice>
				  <div class="price-box"><span class="regular-price" id="product-price-28"><span class="price">${uiLabelMap.ProductListPrice}: <@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span></span></div>
				</#if>
				<#if price.listPrice?exists && price.defaultPrice?exists && price.price?exists && price.price < price.defaultPrice && price.defaultPrice < price.listPrice>
				  <div class="price-box"><span class="regular-price" id="product-price-28"><span class="price">${uiLabelMap.ProductRegularPrice}: <@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed/></span></span></div>
				</#if>
				<div class="price-box"><span class="regular-price" id="product-price-28"><span class="price">
				    <#if price.isSale?exists && price.isSale>
				      ${uiLabelMap.OrderOnSale}!
				      <#assign priceStyle = "salePrice">
				    <#else>
				      <#assign priceStyle = "regularPrice">
				    </#if>
				      ${uiLabelMap.OrderYourPrice}: <#if "Y" = product.isVirtual?if_exists> from </#if><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
				</div>
				<#if price.listPrice?exists && price.price?exists && price.price < price.listPrice>
				  <#assign priceSaved = price.listPrice - price.price>
				  <#assign percentSaved = (priceSaved / price.listPrice) * 100>
				  <div class="price-box"><span class="regular-price" id="product-price-28"><span class="price">${uiLabelMap.OrderSave}: <@ofbizCurrency amount=priceSaved isoCode=price.currencyUsed/> (${percentSaved?int}%)</span></span></div>
				</#if>
	        </#if>
	        <form method="post" action="<@ofbizUrl>additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addform" style='margin: 0;'>
		        <div class="product-options" id="product-options-wrapper">
				<dl class="last">
					<#assign inStock = true>
				        <#-- Variant Selection -->
				        <#if product.isVirtual?exists && product.isVirtual?upper_case == "Y">
				          <#if variantTree?exists && 0 < variantTree.size()>
				            <#list featureSet as currentType>
					  <dt><label class="required"><em>*</em>${currentType}</label></dt>
				              <dd>
						<div class="input-box">
					                <select class="required-entry product-custom-option" name="FT${currentType}" onchange="javascript:getList(this.name, (this.selectedIndex-1), 1);">
					                  <option>${featureTypes.get(currentType)}</option>
					                </select>
				                </div>
				              </dd>
				            </#list>
				            <input type='hidden' name="product_id" value='${product.productId}' />
				            <input type='hidden' name="add_product_id" value='NULL' />
				          <#else>
				            <input type='hidden' name="product_id" value='${product.productId}' />
				            <input type='hidden' name="add_product_id" value='NULL' />
				            <div class='tabletext'><b>${uiLabelMap.ProductItemOutOfStock}.</b></div>
				            <#assign inStock = false>
				          </#if>
				        <#else>
				          <input type='hidden' name="product_id" value='${product.productId}' />
				          <input type='hidden' name="add_product_id" value='${product.productId}' />
				          <#if productNotAvailable?exists>
				            <#assign isStoreInventoryRequired = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, product)>
				            <#if isStoreInventoryRequired>
				              <#assign inStock = false>
				            </#if>
				          </#if>
				        </#if>
				</dl>
		        </div>
			<#if requestParameters.category_id?exists>
			<input type='hidden' name='category_id' value='${requestParameters.category_id}' />
			</#if>
		        <p class="availability in-stock">
				${uiLabelMap.ObbAvailability}:
				<#if inStock>
					<span>${uiLabelMap.ObbInStock}</span>
				<#else>
					<span>${uiLabelMap.ObbOutOfStock}</span>
				</#if>
			</p>
			<div class="product-options-bottom">
			        <div class="add-to-cart">
					<label for="qty">${uiLabelMap.ObbQuantity}:</label>
					<#if inStock>
				            <#if !configwrapper.isCompleted()>
				              <input type="text" name="quantity" id="qty" maxlength="12" value="0" title="Qty" class="input-text qty" disabled="disabled">
				              <a href="javascript:void(0);" class="optionsboxadd" id="optionsbox28">
							<button type="button" title="Add to Cart" class="button btn-cart"><span><span>${uiLabelMap.ObbAddToCart}</span></span></button>
							  </a>
				            <#else>
				              <input type="text" name="quantity" id="qty" maxlength="12" value="0" title="Qty" class="input-text qty">
				                <#if minimumQuantity?exists &&  minimumQuantity &gt; 0>
				                  Minimum order quantity is ${minimumQuantity}.
				               </#if>
				               <a href="javascript:void(0);" onclick="document.addform.submit();" class="optionsboxadd" id="optionsbox28">
							  <button type="button" title="Add to Cart" class="button btn-cart"><span><span>${uiLabelMap.ObbAddToCart}</span></span></button>
							   </a>
				            </#if>
					</#if>
				</div>
					<#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
						<ul class="add-to-links">
						    <li><a href="javascript:void(0);" onclick="document.addToShoppingList.submit();" class="link-wishlist">${uiLabelMap.ObbAddToWishlist}</a></li>
						    <li><span class="separator">|</span> <a href="javascript:void(0);" onclick="document.addToCompare1form.submit();" class="link-compare">${uiLabelMap.ObbAddToCompare}</a></li>
						</ul>
					<#else>
						<div style="">
							${uiLabelMap.ObbYouMust} <a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>" class="link-wishlist">${uiLabelMap.CommonBeLogged}</a>
					${uiLabelMap.ObbAddSelectedItemsToShoppingList}
				</div>
					</#if>
				</div>
	        </form>
	        <form method="post" action="<@ofbizUrl>addToCompare</@ofbizUrl>" name="addToCompare1form" style="display:none;">
			<input type="hidden" name="productId" value="548161">
			<input type="hidden" name="mainSubmitted" value="Y">
	    </form>
	    <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList/product</@ofbizUrl>" style="display:none;">
				<input type="hidden" name="productId" value="FLS_Top_271683">
		<input type="hidden" name="product_id" value="FLS_Top_271683">
		<input type="hidden" name="productStoreId" value="10030">
		<input type="hidden" name="reservStart" value="">
		<input type="hidden" name="shoppingListId" value="10115">
		<input type="hidden" size="5" name="quantity" value="1">
		<input type="hidden" name="reservStartStr" value="">
			</form>
	        <div class="short-description">
                <h2>${uiLabelMap.ObbQuickOverview}</h2>
                <div class="std"><p>${productContentWrapper.get("DESCRIPTION")?if_exists}</p><br>
				</div>
            </div>
	    </div>
    </div>

</div>