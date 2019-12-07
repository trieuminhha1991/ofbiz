<div class="none jm-megamenu clearfix">
	<ul class="megamenu level0">
		<li class="mega first">
			<a href="<@ofbizUrl>main</@ofbizUrl>" class="mega first" id="menu343" title="Home"><span class="menu-title">Home</span></a>
		</li>
		<#assign catCount = 0/>
		<#if context?if_exists.cataCatList?exists>
		<#list context.cataCatList as proCat>
			<#assign catCount=catCount+1/>
			<li class="mega<#if proCat.completedTree?has_content> haschild</#if> menu_${proCat.catalog.prodCatalogId}">
				<a href="<@ofbizUrl>productCategoryList?catalogId=${proCat.catalog.prodCatalogId}</@ofbizUrl>" class="mega<#if proCat.completedTree?has_content> haschild</#if>" id="menu344" title="${proCat.catalog.catalogName?if_exists}">
					<span class="menu-title" style="background-image: url('${proCat.catalog.headerLogo?if_exists}');">${proCat.catalog.catalogName?if_exists}</span>
					<span class="mega-item-des">${proCat.catalog.description?if_exists}</span>
				</a>

				<#assign catChild = proCat.completedTree/>
				<#if catChild?is_collection>
				<#assign slideCat = Static["com.olbius.product.catalog.NewCatalogWorker"].getCatalogTopCategoryId(request,proCat.catalog.prodCatalogId)?if_exists/>
				<#assign tmpCatSL = Static["com.olbius.product.category.NewCategoryWorker"].getCategoryDetail(request,slideCat)?if_exists/>
				<div class="childcontent cols4 ">
					<div class="childcontent-inner-wrap" id="childcontent344">
						<div class="childcontent-inner clearfix" style="<#if tmpCatSL.categoryImageUrl?has_content>background-image:url('${tmpCatSL.categoryImageUrl}');background-repeat:no-repeat;background-position: right bottom;</#if>">
						<div class="root_parent_menu">${proCat.catalog.catalogName?if_exists}</div>
							<#list catChild as catcc>
								<div class="megacol column1">
									<ul class="megamenu level1">
										<li class="mega haschild">
											<div class="group">
												<div class="group-content">
													<ul class="megamenu level2">
														<li class="mega haschild">
															<div class="group">
																<div class="group-title">
																	<a href="<@ofbizUrl>productCategoryList?catId=${catcc.productCategoryId}</@ofbizUrl>" class="mega first haschild" id="menu346" title="${catcc.categoryName?if_exists}">
																		<span class="menu-title">${catcc.categoryName?if_exists}</span>
																	</a>
																</div>
																<#if catcc.child?has_content>
																	<#assign catlow = catcc.child>
																	<div class="group-content">
																		<ul class="megamenu level3">
																			<#list catlow as clcat>
																				<li class="mega">
																					<a href="<@ofbizUrl>productCategoryList?catId=${clcat.productCategoryId}</@ofbizUrl>" class="mega" id="menu347" title="${clcat.categoryName?if_exists}">
																						<span class="menu-title">${clcat.categoryName?if_exists}</span>
																					</a>
																				</li>
																			</#list>
																		</ul>
																	</div>
																</#if>
															</div>
														</li>
													</ul>
												</div>
											</div>
										</li>
									</ul>
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