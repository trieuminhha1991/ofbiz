<#if productData?exists>
	<#assign productUrl = "productmaindetail?product_id=" + productData.productId/>
	<#assign smallImageUrl = productData.smallImageUrl?if_exists>
	<#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
	<li class="item item-row-last" style="<#if request.getAttribute("maxwidth")?has_content>width:${request.getAttribute("maxwidth")?if_exists}<#else></#if>">
		<div class="inner">
			<div class="product-image">
				<a title="${productData.productName?if_exists}" href="<@ofbizUrl>${productUrl}</@ofbizUrl>">
					<img class="lazy" src="/obbresources/images/preload.gif" data-original="<#if smallImageUrl?has_content>${smallImageUrl}<#else>${smallImageUrl}</#if>" alt="${productData.productName?if_exists}">
				</a>
				<#if (price.isSale?exists && price.isSale) || (price.listPrice?exists && price.price?exists && (price.price?double lt price.listPrice?double))>
					<span class="sales-label icon-label">
						sales
					</span>
				</#if>
				<button class="form-button btn-cart" onclick="setLocation('<@ofbizUrl>${productUrl}</@ofbizUrl>')">
					<span>Add to Cart</span>
				</button>
				<ul class="add-to-links">
					<li><a qv="${productUrl}" href="javascript:void(0);" class="link-wishlist">Add to wishlist</a></li>
					<li><span class="separator">|</span><a qv="${productUrl}" href="javascript:void(0);" class="link-compare">Compare</a></li>
				</ul>
			</div>
			<div class="product-information">
				<h5 class="product-name">
					<a title="${productData.productName?if_exists}" href="<@ofbizUrl>${productUrl}</@ofbizUrl>">
						<script type="text/javascript">
							var text = limittext("${StringUtil.wrapString(productData.productName?if_exists)}", 5);
							document.write(text);
						</script>
					</a>
				</h5>
			    <div class="ratings row">
				<div class="col-lg-4 col-md-6 col-sm-12 col-xs-6 no-padding-left">
			    		<div class=''>
				    		<div class='rating-container'>
				            <div class="rating-box">
								<div class="rating" style="width:${(averageRating*2)?string?replace(",", ".")}%"></div>
							</div>
							</div>
			    		</div>
			    	</div>
				<div class="col-lg-7 col-md-6 col-sm-12 col-xs-6 rating-wrapper">
				    	<p class="rating-links">
				        <#if numRatings gt 1>
					        <a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReviews}</a>
						<#else>
					        <a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReview}</a>
						</#if>
			            <span class="separator">|</span>
			            <a class="link-review" href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${productData.productId}</@ofbizUrl>">${uiLabelMap.ObbAddYourReview}</a>
			         </p>
			    	</div>
				</div>
				<div class="price-box">
	                <#if price.listPrice?has_content>
	                	<p class="old-price <#if price.price gte price.listPrice>hide</#if>">
			                <span class="price-label">Regular Price:</span>
			                <span class="price" id="old-price-${productData.productId?if_exists}">
		                    <@ofbizCurrency amount=price.listPrice isoCode=price.currencyUsed/></span>
			            </p>
	                    <p class="special-price">
		                    <span class="price-label">Special Price</span>
		                    <span class="price" id="product-price-${productData.productId?if_exists}">
		                    <@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
		                </p>
	                <#else>
                		<span class="regular-price">
                            <span class="price"><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
                        </span>
	                </#if>
		        </div>
			</div>
		</div>
	</li>
</#if>