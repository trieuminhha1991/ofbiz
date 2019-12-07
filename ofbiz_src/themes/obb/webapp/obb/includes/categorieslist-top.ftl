<div class="jm-megamenu clearfix" id="megamenu-top">
	<ul class="megamenu level0" id="maincategory">
		<#assign catCount = 0/>
		<#if cataCatList?exists>
		<#list cataCatList as proCat>
		<#assign catCount=catCount+1/>
		<li class="mega mega-top<#if proCat.completedTree?has_content> haschild</#if> menu_${proCat.category.productCategoryId}"
		data-submenu-id="submenu-${proCat.category.productCategoryId}">
			<a href="<@ofbizUrl>productCategoryList?catId=${proCat.category.productCategoryId}</@ofbizUrl>" class="mega mega-top-link<#if proCat.completedTree?has_content> haschild</#if>" id="menu344" title="${proCat.category.categoryName?if_exists}"> <span class="menu-title"><div class="row"><div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"><i class="icon ${(proCat.category.icon)?if_exists}"></i></div><div class="col-lg-10 col-md-10 col-sm-10 col-xs-10 no-left-padding">${proCat.category.categoryName?if_exists}</div></div></span></a>
			<#assign catChild = proCat.completedTree/>
			<#if catChild?is_collection>
			<#assign topProduct = proCat.topProduct/>
			<div class="childcontent cols4 " id="submenu-${proCat.category.productCategoryId}">
				<div class="childcontent-inner-wrap" id="childcontent344">
					<div class="childcontent-inner clearfix">
						<!-- <div class="root_parent_menu">${proCat.category.categoryName?if_exists}</div> -->
						<div class='row'>
							<div class='col-lg-3 col-md-3 no-paddingright column1'>
								<#list catChild as catcc>
								
								<#if catcc_index gt 8>
								<#break>
								</#if>
								
								<ul class="megamenu level1">
									<li class="mega mega-level1 haschild">
										<div class="group">
											<div class="group-content">
												<div class="group-title">
													<a href="<@ofbizUrl>productCategoryList?catId=${catcc.productCategoryId}</@ofbizUrl>" class="mega first haschild" id="menu346" title="${catcc.categoryName?if_exists}"> <span class="menu-title">${catcc.categoryName?if_exists}</span> </a>
												</div>
												<#if catcc.child?has_content>
												<#assign catlow = catcc.child>
												<div class="group-content">
													<ul class="megamenu level2">
														<#list catlow as clcat>
														<li class="mega">
															<a href="<@ofbizUrl>productCategoryList?catId=${clcat.productCategoryId}</@ofbizUrl>" class="mega" id="menu347" title="${clcat.categoryName?if_exists}"> <span class="menu-title">${clcat.categoryName?if_exists}</span> </a>
														</li>
														</#list>
													</ul>
												</div>
												</#if>
											</div>
										</div>
									</li>
								</ul>
								</#list>
							</div>
							<div class='col-lg-9 col-md-9 no-paddinghorizon'>
								<div class='category-big-image'>
									<#if (proCat.category.categoryImageUrl)?exists>
										<a href="<#if (proCat.category.url)?exists><@ofbizUrl>${StringUtil.wrapString((proCat.category.url)?if_exists)}</@ofbizUrl></#if>">
											<img src="${StringUtil.wrapString((proCat.category.categoryImageUrl)?if_exists)}" alt="${StringUtil.wrapString((proCat.category.categoryName)?if_exists)}"/>
										</a>
									</#if>
								</div>
								<div class="category-top-product">
									<form id="MenuAddToCart" method="post" action="<@ofbizUrl>additem</@ofbizUrl>">
										<input type="hidden" name="quantityUomId" />
										<input type="hidden" name="quantity" value="1" />
										<input type="hidden" name="add_product_id" />
									</form>
									<#if topProduct?exists>
									<#list topProduct as product>
									<div class="product-top">
										<div class='product-top-content'>
											<a href="<@ofbizUrl>productCategoryList?catId=${product.productCategoryId}</@ofbizUrl>"
												title="${proCat.category.categoryName?if_exists}">
											<p>
												${(product.productName)?if_exists}
											</p> </a>
											<div class='product-top-info'>
												<button class="button buy-now" data-product="${product.productId}" data-uom="${product.quantityUomId}"><i class='fa fa-cart-plus marginright-10'></i>&nbsp;${uiLabelMap.BEBuyNow}</button>
												<#if (product.discount)?exists>
												<div class='sales'>
													<p class='sales-label'>
														${uiLabelMap.BESalesUpTo}: <span class='sales-price'>23%</span>
													</p>
												</div>
												</#if>
												<#if (product.promoCode)?exists>
												<p>
													Nhập mã: <span class='promos-code'>${product.promoCode}</span>
												</p>
												</#if>
												<#if (product.promoValue)?exists>
												<p>
													Tặng thêm: <span class='price-sales'>${product.promoValue}</span>
												</p>
												</#if>
											</div>
										</div>
										<div class='product-top-img'>
											<img src="${(product.smallImageUrl)?if_exists}"/>
										</div>
										<!-- <div class='product-on-sale'>
										&nbsp;
										</div> -->
									</div>
									</#list>
									</#if>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</#if>
		</li>
		</#list>
		</#if>
	</ul>
</div>
