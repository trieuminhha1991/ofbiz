<div class="mainnav-inner" id="main-nav">
	<!-- BEGIN: NAV -->
	<div id="jm-mainnav" class="has-toggle">
		<div class="btn-toggle menu-toggle">
			<i class="fa fa-bars">&nbsp;</i>
		</div>
		<div class="inner-toggle mega-menu-top" id="jm-mainnav-inner">
			<div class="none jm-megamenu clearfix" id="jm-megamenu">
				<ul class="megamenu level0">
					<li class="mega first">
						<a href="<@ofbizUrl>main</@ofbizUrl>" class="mega first" id="menu343" title="Trang chủ"><span class="menu-title">${uiLabelMap.BEHome}</span></a>
					</li>
					<#if context?if_exists.cataCatList?exists>
					<#list context.cataCatList as proCat>
					<li class="mega<#if proCat.completedTree?has_content> haschild</#if> menu_${proCat.category.productCategoryId}">
						<a href="<@ofbizUrl>productCategoryList?catId=${proCat.category.productCategoryId}</@ofbizUrl>" class="mega<#if proCat.completedTree?has_content> haschild</#if>" id="menu344" title="${proCat.category.categoryName?if_exists}"> <span class="menu-title">${proCat.category.categoryName?if_exists}</span> <span class="mega-item-des">${proCat.category.description?if_exists}</span> </a>
						<#assign catChild = proCat.completedTree/>
						<#if catChild?is_collection>
						<#assign slideCat = Static["com.olbius.product.catalog.NewCatalogWorker"].getCatalogTopCategoryId(request,proCat.category.productCategoryId)?if_exists/>
						<#assign tmpCatSL = Static["com.olbius.product.category.NewCategoryWorker"].getCategoryDetail(request,slideCat)?if_exists/>
						<div class="childcontent">
							<div class="childcontent-inner-wrap">
								<div class="row childcontent-inner megamenu level1">
									<#list catChild as catcc>
									<div class='col-lg-6 col-md-6 group-title'>
										<a href="<@ofbizUrl>productCategoryList?catId=${catcc.productCategoryId}</@ofbizUrl>" class="mega">${catcc.categoryName}</a>
									</div>
									</#list>
								</div>
							</div>
						</div>
						</#if>
					</li>
					</#list>
					</#if>
				</ul>
			</div>
		</div>
	</div>
</div>