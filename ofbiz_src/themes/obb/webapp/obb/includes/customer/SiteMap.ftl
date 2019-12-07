<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix margin-top10">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner site-map clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="page-sitemap">
						
							<#if mtype == "ct">
							
								<#assign categories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].categoriesOfWebSite(request, delegator) />
								
								<div class="page-title">
									<h1>${uiLabelMap.BSCategories}</h1>
								</div>
								<div id="sitemap_top_links">
									<ul class="links">
										<li class="first last"><a href="<@ofbizUrl>SiteMap?mtype=pd</@ofbizUrl>" title="${uiLabelMap.BEProductsSitemap}">${uiLabelMap.BEProductsSitemap}</a></li>
									</ul>
								</div>
								<div class="pager">
									<p class="amount">
										<strong>${categories?size} ${uiLabelMap.BECategory}</strong>
									</p>
								</div>
								<ul class="sitemap">
									<#list categories as category>
										<li><a href="<@ofbizUrl>productCategoryList?catId=${category.productCategoryId?if_exists}</@ofbizUrl>">${StringUtil.wrapString((category.categoryName)?if_exists)}</a></li>
									</#list>
								</ul>
								<ul class="links">
									<li class="first last"><a href="<@ofbizUrl>SiteMap?mtype=pd</@ofbizUrl>" title="${uiLabelMap.BEProductsSitemap}">${uiLabelMap.BEProductsSitemap}</a></li>
								</ul>
								<div class="pager">
									<p class="amount">
										<strong>${categories?size} ${uiLabelMap.BECategory}</strong>
									</p>
								</div>
							
							<#else>
							
							<#assign products = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].productsOfWebSite(request, delegator) />
							
							<div class="page-title">
								<h1>${uiLabelMap.BEProducts}</h1>
							</div>
							<div id="sitemap_top_links">
								<ul class="links">
									<li class="first last"><a href="<@ofbizUrl>SiteMap?mtype=ct</@ofbizUrl>" title="${uiLabelMap.BECategoriesSitemap}">${uiLabelMap.BECategoriesSitemap}</a></li>
								</ul>
							</div>
							<div class="pager">
								<p class="amount">
									<strong>${products?size} ${uiLabelMap.BEProducts}</strong>
								</p>
							</div>
							<ul class="sitemap">
								<#list products as product>
									<li><a href="<@ofbizUrl>productmaindetail?product_id=${product.productId?if_exists}</@ofbizUrl>">${StringUtil.wrapString((product.productName)?if_exists)}</a></li>
								</#list>
							</ul>
							<ul class="links">
								<li class="first last"><a href="<@ofbizUrl>SiteMap?mtype=ct</@ofbizUrl>" title="${uiLabelMap.BECategoriesSitemap}">${uiLabelMap.BECategoriesSitemap}</a></li>
							</ul>
							<div class="pager">
								<p class="amount">
									<strong>${products?size} ${uiLabelMap.BECategory}</strong>
								</p>
							</div>
							
							</#if>
						
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>