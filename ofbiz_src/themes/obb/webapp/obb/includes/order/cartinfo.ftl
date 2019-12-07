<div class='widget-wrapper'>
	<div class='widget-header'>
		<i class='fa fa-opencart marker cart-icon'>&nbsp;</i>
		<h3 class='cart-header'>${uiLabelMap.BEOrderInformation}</h3>
	</div>
	<div class='widget-body'>
		<#assign fixedAssetExist = shoppingCart.containAnyWorkEffortCartItems() /> <#-- change display format when rental items exist in the shoppingcart -->
		<form action="<@ofbizUrl>modifycart</@ofbizUrl>" method="post" name="cartform">
			<input type="hidden" name="removeSelected" value="false" />
			<div class='scroll-mobile'>
				<table class="table table-bordered">
					<thead>
						<tr class="row1">
							<th class="title">${uiLabelMap.BEProductName}</th>
							<th width="15%" class="title" align="center">${uiLabelMap.BEPrice}</th>
							<th width="15%" class="title" align="center">${uiLabelMap.BEQuantity}</th>
							<th width="15%" class="title" align="center">${uiLabelMap.BESubTotal}</th>
							<th width="10%" class="title" align="center" style='position: relative'>
								<span class='reject-title' id="reject">${uiLabelMap.BERemove}</span>
								<input class='reject' type="checkbox" name="selectAll" value="0" onclick="javascript:Cart.toggleAll(this);" />
							</th>
						</tr>
					</thead>
					<tbody>
						<#assign itemsFromList = false />
						<#assign isoCode = shoppingCart.getCurrency()/>
						<#assign promoItems = false />
							<#list orderItems as cartLine>
							<#assign cartLineIndex = shoppingCart.getItemIndex(cartLine) />
							<#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures() />
							<#assign productId = cartLine.getProductId() />
							<#assign subTotal = cartLine.getDisplayItemSubTotal()/>
							<#if !cartLine.getIsPromo()>
								<#assign id="cartItemDisplayRow_${cartLineIndex}"/>
								<#assign itemName=cartLine.getName()?if_exists/>
								<#assign displayPrice=cartLine.getDisplayPrice()/>
								<#assign quantity=cartLine.getQuantity()?string.number/>
								<#assign isPromo=cartLine.getIsPromo()/>
								<#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
								<#include "cartItem.ftl"/>
							</#if>
						</#list>
						<#list Static["com.olbius.baseecommerce.cart.CartEvents"].getPromoItem(shoppingCart.items()) as cartLine>
							<#assign id=""/>
							<#assign itemName=cartLine.get("productName")?if_exists/>
							<#assign productId=cartLine.get("productId")?if_exists/>
							<#assign displayPrice=cartLine.get("amount")?if_exists/>
							<#assign quantity=cartLine.get("quantity")?if_exists/>
							<#assign isPromo=true/>
							<#assign smallImageUrl=Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.get("product"), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
							<#include "cartItem.ftl"/>
						</#list>
						<tr>
							<td colspan="5" valign="middle">
								<div class='row cart-total'>
									<div class="col-lg-5 col-md-6 col-sm-6">
										<div class='loyalty-point'>
											<#if userLogin?exists>
												<div class='row price-item'>
													<div class='col-lg-7 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
														<div class='pull-right'><b>${uiLabelMap.BEOrderPoint}: </b></div></div>
													<div class='col-lg-5 col-md-6 col-xs-6 col-sm-6 point'>
														${totalPointOrder?if_exists}
													</div>
												</div>
												<div class='row price-item'>
													<div class='col-lg-7 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
														<div class='pull-right'><b>${uiLabelMap.BECustomerPoint}: </b></div></div>
													<div class='col-lg-5 col-md-6 col-xs-6 col-sm-6 point'>
														${totalPointCustomer?if_exists}
													</div>
												</div>
											<#else>
												<p><a href="<@ofbizUrl>login</@ofbizUrl>" title="${uiLabelMap.BELoginToGetPoint}">${uiLabelMap.BELoginToGetPoint}</a></p>
											</#if>
										</div>
									</div>
									<div class="col-lg-7 col-md-6 col-sm-6">
										<#if promoCode?exists>
											<!-- <div class='row price-item'>
												<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
													<div class='pull-right'><b>${uiLabelMap.BEPromoCodeApply}: </b></div></div>
												<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 price'>
													<b>${promoCode}</b>
												</div>
											</div> -->
										</#if>
										<div class='row price-item'>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
												<div class='pull-right'><b>${uiLabelMap.BEProductPrice}: </b></div></div>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 price'>
												<@ofbizCurrency amount=shoppingCart.getSubTotal() isoCode=currencyUomId/>
											</div>
										</div>
										<#list listItemAdjustment?if_exists as itemAdjustment>
										<div class='row price-item'>
									        <div class='col-lg-6 col-md-6 col-xs-6 col-sm-6'>
											${StringUtil.wrapString(itemAdjustment.description?if_exists)}</div>
									        <div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 price'>
											<#if itemAdjustment.value?exists && itemAdjustment.value &lt; 0>
												<div>(<@ofbizCurrency amount=-itemAdjustment.value isoCode=currencyUomId/>)</div>
												<#else>
													<@ofbizCurrency amount=itemAdjustment.value isoCode=currencyUomId/>
												</#if>
										</div>
										</div>
										</#list>
										<#if curPage?exists>
										<div class='row price-item'>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
												<div class='pull-right'><b>${uiLabelMap.BEAbbValueAddedTax}:</b></div></div>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 price'>
												<@ofbizCurrency amount=shoppingCart.getTotalSalesTax() isoCode=currencyUomId/>
											</div>
										</div>
										</#if>
										<div class='row price-item'>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
												<div class='pull-right'><b>${uiLabelMap.BEShippingFee}:</b></div>
											</div>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 price'>
												<@ofbizCurrency amount=shoppingCart.getTotalShipping() isoCode=currencyUomId/>
											</div>
										</div>
										<div class='row price-item'>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 no-padding-right'>
												<div class='pull-right'><b>${uiLabelMap.BECartSubTotal}:</b></div></div>
											<div class='col-lg-6 col-md-6 col-xs-6 col-sm-6 price'>
												<@ofbizCurrency amount=shoppingCart.getGrandTotal() isoCode=currencyUomId/>
											</div>
										</div>
									</div>
									<button class="updatecart btn-primary" type="button" id="updatecart">
										<i class='fa fa-refresh marginright-5'>&nbsp;</i>${uiLabelMap.BEUpdateCart}
									</button>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</form>
		<div class='row'>
			<div class='col-lg-6 col-md-6 col-sm-6 promotion'>
				<form action="<@ofbizUrl>addpromocode</@ofbizUrl>" method="post" class="clearfix">
					<p class="text-align-left">
						<b>${uiLabelMap.BEApplyPromotionCode}</b>
					</p>
					<!-- <div class='row'>
						<div class='col-lg-12 '> -->
					<div class='pull-left'>
						<input type="text" id="promotion-code" class="topinput full-width-input" class='no-space' name="productPromoCodeId">
					</div>
					<div class='pull-left'>
						<button type="submit" class="btntop btn-primary">
							${uiLabelMap.BEApply}
						</button>
					</div>

						<!-- </div> -->
					<!-- </div> -->
				</form>
			</div>
			<div class='col-lg-6 col-md-6 col-sm-6 text-align-right cart-info'>
				<!-- <p>
					<b>${uiLabelMap.BEProductPrice}: </b><span class="price-cart"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span>
				</p>
				<p>
					<b>${uiLabelMap.BEShippingFee}:</b><span class="price-cart"><@ofbizCurrency amount=orderShippingTotal isoCode=shoppingCart.getCurrency()/></span>
				</p>
				<p>
					<b>${uiLabelMap.BECartSubTotal}:</b><span class="price-cart"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span>
				</p> -->
				<p class='hidden-xs'>&nbsp;</p>
				<button type="button" class="btn-primary" onclick="window.location.href='<@ofbizUrl>main</@ofbizUrl>'">
					<i class='fa fa-chevron-left'>&nbsp;</i> ${uiLabelMap.BEContinueShopping}
				</button>
			</div>
		</div>
	</div>
</div>
