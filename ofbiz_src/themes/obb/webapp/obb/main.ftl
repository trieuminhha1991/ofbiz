<script type="text/javascript" src="/obbresources/skin/frontend/default/jm_megamall/joomlart/jmproductsslider/js/jcarousellite_1.0.1_custom.js"></script>

<#-- FIXME slide left in odd, right in even -->
<#if context?if_exists.cataCatList?exists>
<#list context.cataCatList as proCat>
<!-- //TOP SPOTLIGHT 3 -->
<div id="jm-tops3" class="jm-position wrap clearfix">
	<div class="main col1-set">
		<div class="inner">
			<#assign banner = Static["com.olbius.baseecommerce.backend.ContentUtils"].getBannerByCategoryId(delegator, webSiteId, proCat.category.productCategoryId) />
			<#if banner?has_content && (banner.url)?has_content>
				<div class="obb-banner obb-banner-${proCat_index+1}">
					<a href="<@ofbizUrl>${StringUtil.wrapString((banner.url)?if_exists)}</@ofbizUrl>">
						<img src="${(banner.originalImageUrl)?if_exists}" alt="Obb">
					</a>
				</div>
			</#if>
		</div>
	</div>
</div>
<!-- //TOP SPOTLIGHT 3 -->
<!-- //TOP SPOTLIGHT 4 -->

<div id="jm-tops4" class="jm-position wrap clearfix">
	<div class="main col1-set">
		<div class="inner">
			<div class="block block-cate">
				<h2><a href="<@ofbizUrl>productCategoryList?catId=${proCat.category.productCategoryId}</@ofbizUrl>"><i class="fa fa-play-circle-o block-mobile"></i>&nbsp;${proCat.category.categoryName?if_exists}</a></h2>
				<ul class="cat-child">
				<#if proCat.completedTree?is_collection>
					<#list proCat.completedTree as catcc>
					<#if catcc_index gt 5>
					<#break>
					</#if>
					<li>
						<a href="<@ofbizUrl>productCategoryList?catId=${catcc.productCategoryId?if_exists}</@ofbizUrl>">${catcc.categoryName?if_exists}</a>
					</li>
					</#list>
					</#if>
				</ul>
				<div class="btn-group block-mobile block-tab">
				<#if proCat.completedTree?is_collection>
					<button class="dropdown-toggle btn-cat-child-nav" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<i class="fa fa-bars cat-child-nav"></i>
					</button>
					<ul class="dropdown-menu dropdown-menu-right">
					
					<#list proCat.completedTree as catcc>
					<li>
						<a href="<@ofbizUrl>productCategoryList?catId=${catcc.productCategoryId?if_exists}</@ofbizUrl>">${catcc.categoryName?if_exists}</a>
					</li>
					</#list>
					</ul>
				</#if>
				</div>
				
			</div>
			<div class="col2-set list-categories<#if proCat_index%2==0> list-cate-colr</#if>">
				<div class="col-1">
					<!-- promotion -->
					<#if proCat_index%2==0>
					<div class="block jm-products-slider-listing jm-products-slider-vertical carousel-vertical" data-id="jmmainwrap-jm-content-${proCat_index}" id="jmmainwrap-jm-contain-${proCat_index}">
						<#assign slideCat = proCat.category.productCategoryId?upper_case + "_PROMOS"/>
						<#assign tmpCatSL = Static["com.olbius.product.category.NewCategoryWorker"].getCategoryDetail(request,slideCat)?if_exists/>
						<div class="block-title">
							<strong>
								<span><a href="<@ofbizUrl>productCategoryList?catId=${slideCat?if_exists}</@ofbizUrl>">${uiLabelMap.ObbOnSale}</a></span>
								<#if tmpCatSL.description?has_content><span>${tmpCatSL.description?if_exists}</span> <#else></#if>
							</strong>
						</div>
						<div class="jm-prev">
							<span><i class="fa fa-caret-up"></i></span>
						</div>
						<div class="jm-next">
							<span> <i class="fa fa-caret-down"></i> </span>
						</div>
						<div class="jm-products-slider-content clearfix block-content jm-products-slider-content${proCat_index}_slide" id="jmmainwrap-jm-content-${proCat_index}">
							<div id="jm-contain-${proCat_index}" class="jm-slider" style="">
								<ul class="products-grid jm-slider-ul even" style="">
									${setRequestAttribute("productCategoryId", slideCat)}
									${setRequestAttribute("leftSL", "N")}
									${screens.render("component://obb/widget/CatalogScreens.xml#productDetailSlide")}
								</ul>
							</div>
						</div>
					</div>
					<#else>
					<!-- best selling -->
					<div class="block jm-products-slider-listing jm-products-slider-horizon carousel" data-id="jmmainwrap-jm-content-${proCat_index}" id="jmmainwrap-jm-contain-${proCat_index}">
						<#assign slideCat = proCat.category.productCategoryId?upper_case + "_BSL"/>
						<#assign tmpCatSL = Static["com.olbius.product.category.NewCategoryWorker"].getCategoryDetail(request,slideCat)?if_exists/>
						<div class="block-title">
							<strong>
								<span>
									<a href="<@ofbizUrl>productCategoryList?catId=${slideCat?if_exists}</@ofbizUrl>">${uiLabelMap.ObbBestSelling}</a>
								</span>
								<#if tmpCatSL.description?has_content><span> ${tmpCatSL.description?if_exists}</span></#if></strong>
							<div class="jm-prev">
								<span><i class="fa fa-caret-left"></i></span>
							</div>
							<div class="jm-next">
								<span> <i class="fa fa-caret-right"></i> </span>
							</div>
						</div>
						<div class="jm-products-slider-content clearfix block-content jm-products-slider-content${proCat_index}_slide" id="jmmainwrap-jm-content-${proCat_index}">
							<div id="jm-contain-${proCat_index}" class="jm-slider">
								<ul class="products-grid jm-slider-ul even">
									${setRequestAttribute("productCategoryId", slideCat)}
									${setRequestAttribute("leftSL", "Y")}
									${screens.render("component://obb/widget/CatalogScreens.xml#productDetailSlide")}
								</ul>
							</div>
						</div>
					</div>
					</#if>
				</div>
				<!-- get newest products -->
				<#assign newCatId = proCat.category.productCategoryId?upper_case + "_NEW"/>
				<div class="col-2">
					<div class="jm-product-list latest clearfix">
						<div class="page-title category-title">
							<#assign tmpCat = Static["com.olbius.product.category.NewCategoryWorker"].getCategoryDetail(request,newCatId)?if_exists/>
							<h3>${uiLabelMap.ObbNewProducts}<#if tmpCat.description?exists><span>${tmpCat.description?if_exists}</span></#if></h3>
							<#if newCatId?has_content>
							<a href="<@ofbizUrl>productCategoryList?catId=${newCatId?if_exists}</@ofbizUrl>">${uiLabelMap.ObbViewAll}</a>
							</#if>
						</div>
						<div class="listing-type-grid category-products">
							<ul class="products-grid last odd" id="productsgrid_${newCatId}">
								${setRequestAttribute("productCategoryId", newCatId)}
								${setRequestAttribute("categoryId", proCat.category.productCategoryId)}
								${screens.render("component://obb/widget/CatalogScreens.xml#productDetailMain")}
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- //TOP SPOTLIGHT 4 -->
</#list>
</#if>
<script type="text/javascript">
	jQuery.noConflict();
	var initSlider = function(obj, options){
		var id = obj.attr('id');
		obj.jCarouselLite(options);
	};
	jQuery(document).ready(function($) {
		var windowWidth = $(window).width()
		if(windowWidth > 980){
			var car = jQuery('.carousel');
			for(var x = 0; x < car.length; x++){
				(function(x){
					var obj = $(car[x]);
					var parId = obj.attr('id');
					var id = obj.attr('data-id');
					var next = "#" + parId + " .jm-next";
					var prev = "#" + parId+ " .jm-prev";
					initSlider($('#' + id), {
						vertical: false,
						auto : true,
						speed : 2000,
						visible : 1,
						btnNext : next,
						btnPrev : prev
					});
				})(x);
			}
			var carVer = jQuery('.carousel-vertical');
			for(var x = 0; x < carVer.length; x++){
				(function(x){
					var obj = $(carVer[x]);
					var parId = obj.attr('id');
					var id = obj.attr('data-id');
					var next = "#" + parId + " .jm-next";
					var prev = "#" + parId+ " .jm-prev";
					initSlider($('#' + id), {
						vertical: true,
						auto : true,
						speed : 4000,
						visible : 3,
						btnNext : next,
						btnPrev : prev
					});
				})(x);
			}
		}
	});

</script>