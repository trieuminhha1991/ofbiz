<#if productMini?exists>
	 <#--<#if backendPath?default("Y") == "Y">
        <#assign productUrl><@ofbizCatalogUrl productId=productMini.productId productCategoryId=productCategoryId/></#assign>
    <#else>
        <#assign productUrl><@ofbizCatalogAltUrl productId=productMini.productId productCategoryId=productCategoryId/></#assign>
    </#if> -->
	<#assign productUrl = "productmaindetail?product_id=" + productMini.productId/>
	 <#assign smallImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists>
	 <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
	 <#assign sale = false />
    <#if price.isSale?exists && price.isSale>
	<#assign sale = true />
	</#if>
	<#if !sale && price.listPrice?exists && price.price?exists && (price.price?double lt price.listPrice?double)>
		<#assign sale = true />
	</#if>
	<li class="item <#if request.getAttribute("listIndex")%2==0>odd<#else>even</#if>">
		<div class="inner">
			<a title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}" href="${productUrl}" class="product-image">
				<img style="width:222px;height:222px;" src="<#if smallImageUrl?has_content>${smallImageUrl}<#else>${smallImageUrl}</#if>" alt="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">
			</a>
			<div class="product-shop">
				<div class="f-fix">
					<h2 class="product-name"><a href="${productUrl}" title="${productContentWrapper.get("PRODUCT_NAME")?if_exists}">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a></h2>
					<div class="ratings">
					
						<div class="col-lg-1 col-md-1 no-padding">
							
							<div class="rows">
								<div class="col-lg-12 col-md-12  no-padding-left">
									<div class="rating-box">
							            <#if averageRating?exists && (averageRating &gt; 0)>
						            		<div class="rating" style="width:${(averageRating*2)?string?replace(",", ".")}%"></div>
						            	<#else>
						            		<div class="rating" style="width:0%"></div>
						            	</#if>
					            	</div>
				            	</div>
			            	</div>
			            	<div class="rows">
								<div class="col-lg-12 col-md-12  no-padding-left">
									<#if numRatings?has_content>
					            		<#if numRatings gt 1>
					            			<p class="review-count">${numRatings} ${uiLabelMap.ObbReviews}</p>
										<#else>
					            			<p class="review-count">${numRatings} ${uiLabelMap.ObbReview}</p>
										</#if>
				            		<#else>
										<p class="review-count">0&nbsp;${uiLabelMap.ObbReview}</p>
				            		</#if>
								</div>
							</div>
						</div>
						
						<p class="rating-links col-lg-11 col-md-11">
							<a onclick="openRate(this, '${productMini.productId}', '${categoryId?if_exists}')" class="btn-review">${uiLabelMap.BEReview}</a>
						</p>
					</div>
					<div class="price-box">
	                    <p class="old-price">
			                <span class="price-label">Regular Price:</span>
			                <span class="price" id="old-price-${productMini.productId?if_exists}">
			                    <@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
			            </p>
	                    <p class="special-price">
		                    <span class="price-label">Special Price</span>
		                    <span class="price" id="product-price-${productMini.productId?if_exists}">
		                    <@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span>
		                </p>
			        </div>
			        <div class="short-description" style="width:501px;">
				        <#if productMini.longDescription?has_content>
							${StringUtil.wrapString(productMini.longDescription?if_exists)}
							<#else>
							${uiLabelMap.BENoDescription}
						</#if>
					</div>
					<div class="btn-cart"></div>
					<#if sale>
						<span class="sales-label icon-label">
							sales
						</span>
					</#if>
					<ul class="add-to-links">
						<li>
							<button type="button" title="Add to Cart" class="button btn-cart" onclick="setLocation('${productUrl}')">
								<span>Đặt hàng</span>
							</button>
						</li>
						<li class="hide"><a qv="${productUrl}" href="javascript:void(0);" class="link-wishlist">Thích</a></li>
						<li class="hide"><span class="separator"></span><a qv="${productUrl}" href="javascript:void(0);" class="link-compare">So sánh</a></li>
					</ul>
				</div>
			</div>
		</div>
	</li>
</#if>