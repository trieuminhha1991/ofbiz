<#if productData?exists>
	<#assign productUrl = "productmaindetail?product_id=" + productData.productId/>
	<#assign smallImageUrl = productData.get("smallImageUrl")?if_exists>
	<#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
	<#assign sale = false />
    <#if price.isSale?exists && price.isSale>
	<#assign sale = true />
	</#if>
	<#if !sale && price.listPrice?exists && price.price?exists && (price.price?double lt price.listPrice?double)>
		<#assign sale = true />
	</#if>
	<#if request.getAttribute("leftSL")?has_content && request.getAttribute("leftSL")?string=="N">
		<li class="jm-slider-li-vertical">
			<div class="inner">
				<div class="product-image">
					<a title="${productData.get("productName")?if_exists}" href="<@ofbizUrl>${productUrl}</@ofbizUrl>">
						<img class="image-product<#if device=="mobile"> lazy" src="/obbresources/images/preload.gif" data-original=<#else>" src=</#if>"<#if smallImageUrl?has_content>${smallImageUrl}<#else>${smallImageUrl}</#if>" alt="${productData.get("productName")?if_exists?trim}">
					</a>
					<#if sale>
						<span class="sales-label icon-label">
							sales
						</span>
					</#if>
					<button class="form-button btn-cart" onclick="setLocation('${productUrl}')">
						<span>Add to Cart</span>
					</button>
					<div class="add-to-links">
						<a qv="${productUrl}" href="javascript:void(0);" class="link-wishlist">
							Wishlist
						</a>
						<a qv="${productUrl}" href="javascript:void(0);" class="link-compare">Compare</a>
					</div>
				</div>
				<div class="product-detail">
					<h5 class="product-name">
						<a title="${productData.get("productName")?if_exists}" href="<@ofbizUrl>${productUrl}</@ofbizUrl>">
							<span>
								<script type="text/javascript">
									var name = limittext("${StringUtil.wrapString(productData.get("productName")?if_exists)}", 4);
									document.write(name);
								</script>
							</span>
						</a>
					</h5>
					<div class="ratings row">
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-6 no-padding-left">
						<div class=''>
							<div class='rating-container'>
						            <div class="rating-box">
								<#if averageRating?exists && (averageRating &gt; 0)>
								<div class="rating" style="width:${(averageRating*2)?string?replace(",", ".")}%"></div>
								<#else>
								<div class="rating" style="width:0%"></div>
								</#if>
									</div>
								</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-6 rating-wrapper">
						<p class="rating-links">
							<#if numRatings?has_content>
							<#if numRatings gt 1>
								<a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReviews}</a>
									<#else>
								<a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReview}</a>
									</#if>
						<#else>
								<a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">0&nbsp;${uiLabelMap.ObbReview}</a>
						</#if>
						</p>
					</div>
					</div>
					<div class="price-box">
						<#if price.listPrice?has_content>
							<p class="old-price">
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
	<#else>
		 <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
		 <#assign sale = false />
	    <#if price.isSale?exists && price.isSale>
		<#assign sale = true />
		</#if>
		<li class="jm-slider-li-horizontal">
			<div class="item-slider">
				<div class="product-image">
					<a title="${productData.get("productName")?if_exists}" href="<@ofbizUrl>${productUrl}</@ofbizUrl>">
						<img class="<#if device=="mobile"> lazy" src="/obbresources/images/preload.gif" data-original=<#else>" src=</#if>"<#if smallImageUrl?has_content>${smallImageUrl}<#else>${smallImageUrl}</#if>" alt="${productData.get("productName")?if_exists}">
					</a>
					<br class="clear">
					<button class="button btn-cart" onclick="setLocation('${productUrl}')">
						<span>Đặt hàng</span>
					</button>
					<div class="add-to-links">
						<a qv="${productUrl}" href="javascript:void(0);" class="link-wishlist">
							Thích</a>
						<a qv="${productUrl}" href="javascript:void(0);" class="link-compare">So sánh</a>
					</div>
				</div>
				<div class="product-information">
					<h5 class="product-name">
						<a class="product-title" title="${productData.get("productName")?if_exists}" href="<@ofbizUrl>${productUrl}</@ofbizUrl>">
							<span>
								<script type="text/javascript">
									var name = limittext('${StringUtil.wrapString(productData.get("productName")?if_exists)}', 4);
									document.write(name);
								</script>
							</span>
						</a>
					</h5>
					<div class="ratings row">
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-6 no-padding-left">
						<div class=''>
							<div class='rating-container'>
						            <div class="rating-box">
								<#if averageRating?exists && (averageRating &gt; 0)>
								<div class="rating" style="width:${(averageRating*2)?string?replace(",", ".")}%"></div>
								<#else>
								<div class="rating" style="width:0%"></div>
								</#if>
									</div>
								</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-6 rating-wrapper">
						<div class="rating-links">
							<#if numRatings?has_content>
							<#if numRatings gt 1>
							<a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReviews}</a>
									<#else>
							<a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">${numRatings} ${uiLabelMap.ObbReview}</a>
									</#if>
						<#else>
							<a href="<@ofbizUrl>${productUrl}&rev=Y</@ofbizUrl>">0&nbsp;${uiLabelMap.ObbReview}</a>
						</#if>
						</div>
					</div>
					</div>
					<div class="price-box">
		                <#if price.listPrice?has_content>
		                	<p class="old-price">
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
</#if>