<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>
<#--
<div id="microcart">
        <#if (shoppingCartSize > 0)>
            <p id="microCartNotEmpty">
                ${uiLabelMap.ObbCartHas} <strong id="microCartQuantity">${shoppingCart.getTotalQuantity()}</strong>
                <#if shoppingCart.getTotalQuantity() == 1>${uiLabelMap.OrderItem}<#else/>${uiLabelMap.OrderItems}</#if>,
                <strong id="microCartTotal"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></strong>
            </p>
            <span id="microCartEmpty" style="display:none">${uiLabelMap.OrderShoppingCartEmpty}</span>
        <#else>
            <p>${uiLabelMap.OrderShoppingCartEmpty}</p>
        </#if>
    <ul>
      <li><a href="<@ofbizUrl>showcart</@ofbizUrl>">[${uiLabelMap.OrderViewCart}]</a></li>
      <#if (shoppingCartSize > 0)>
          <li id="quickCheckoutEnabled"><a href="<@ofbizUrl>quickcheckout</@ofbizUrl>">[${uiLabelMap.OrderCheckoutQuick}]</a></li>
          <li id="quickCheckoutDisabled" style="display:none" class="disabled">[${uiLabelMap.OrderCheckoutQuick}]</li>
          <li id="onePageCheckoutEnabled"><a href="<@ofbizUrl>onePageCheckout</@ofbizUrl>">[${uiLabelMap.ObbOnePageCheckout}]</a></li>
          <li id="onePageCheckoutDisabled" style="display:none" class="disabled">[${uiLabelMap.ObbOnePageCheckout}]</li>
          <li id="googleCheckoutEnabled"><a href="<@ofbizUrl>googleCheckout</@ofbizUrl>"><img src="https://checkout.google.com/buttons/checkout.gif?merchant_id=634321449957567&amp;w=160&amp;h=43&amp;style=white&amp;variant=text&amp;loc=en_US" alt="[${uiLabelMap.ObbCartToGoogleCheckout}]" /></a></li>
          <li id="googleCheckoutDisabled" style="display:none" class="disabled"><img src="https://checkout.google.com/buttons/checkout.gif?merchant_id=634321449957567&amp;w=160&amp;h=43&amp;style=white&amp;variant=text&amp;loc=en_US" alt="[${uiLabelMap.ObbCartToGoogleCheckout}]" /></li>
          <#if shoppingCart?has_content && (shoppingCart.getGrandTotal() > 0)>
            <li id="microCartPayPalCheckout"><a href="<@ofbizUrl>setPayPalCheckout</@ofbizUrl>"><img src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif" alt="[PayPal Express Checkout]" /></a></li>
          </#if>
      <#else>
          <li class="disabled">[${uiLabelMap.OrderCheckoutQuick}]</li>
          <li class="disabled">[${uiLabelMap.ObbOnePageCheckout}]</li>
      </#if>
    </ul>
</div>
-->

<div id="jm-mycart" class="has-toggle" style="display: block;">
	<div class="jmajmxloading">&nbsp;</div>
	<div class="btn-toggle mycart-toggle">
		<i class="fa fa-shopping-cart"></i>
		<a class="ico-shopping-cart" href="<@ofbizUrl>showcart</@ofbizUrl>">
			<span>${uiLabelMap.ObbMyCart}</span>
			 <strong>${shoppingCartSize} <#if (shoppingCartSize > 1)>${uiLabelMap.ObbItems}<#else>${uiLabelMap.ObbItem}</#if></strong>
		</a>
	</div>
	<div class="inner-toggle">
		<div class="block block-cart">
			<div class="block-content">
				<#if (shoppingCartSize == 0)>
					<p class="empty">${uiLabelMap.ObbEmptyCart}</p>
				<#else>
					<ol id="cart-sidebar" class="mini-products-list">
						<#list shoppingCart.items() as cartLine>
							<li class="item <#if cartLine_index%2==0>even<#else>odd</#if>">
								<#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
					<#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg" /></#if>
					<#if cartLine.getProductId()?exists>
									<#if cartLine.getParentProductId()?exists>
									<#assign tmpProductId=cartLine.getParentProductId()/>
						<#else>
							<#assign tmpProductId=cartLine.getProductId()/>
						</#if>
						<a href="<@ofbizUrl>productmaindetail?product_id=${tmpProductId}</@ofbizUrl>" title="${cartLine.getName()}" class="product-image">
										<img src="${smallImageUrl}" width="50" height="50" alt="${cartLine.getName()}">
									</a>
									<div class="product-details">
									<#-- FIXME Implement this feature -->
									<a class="remove red" style="display:block;" title="Loại bỏ sản phẩm khỏi giỏi hàng" onclick="CommonUtils.removeCartItem('${cartLine_index}')"><i class="fa fa-times red"></i></a>
									<p class="product-name"><a href="<@ofbizUrl>productmaindetail?product_id=${tmpProductId}</@ofbizUrl>">${StringUtil.truncateString(cartLine.getName(), 7)}</a></p>
									<span class="price"><@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency()/></span>
										x (${cartLine.getQuantity()?string.number})
									<div class="truncated">
										<div class="truncated_full_value" style="display:none;"> <#-- FIXME Implement this feature -->
										<dl class="item-options">
									<dt>Color</dt>
									                <dd>
												Light Green                                    </dd>
					                                <dt>Decor</dt>
									                <dd>
										                Painted Wood Numbers                                     </dd>
										            <dt>Gift Set</dt>
									                <dd>
										                Colorful Box                                    </dd>
										            <dt>Size</dt>
									                <dd>
										                Big                                    </dd>
										        </dl>
										</div>
									</div>
								</div>
							</#if>
							</li>
						</#list>
					</ol>
					<div class="summary">
						<p class="amount">${uiLabelMap.ObbThereAre} <a href="<@ofbizUrl>showcart</@ofbizUrl>">${shoppingCartSize} <#if (shoppingCartSize > 1)>${uiLabelMap.ObbItems}<#else>${uiLabelMap.ObbItem}</#if></a> ${uiLabelMap.ObbInYourCart}.</p>
						<p class="subtotal">
							<span class="cartlabel">${uiLabelMap.ObbTotalPrice}:</span> <span class="price"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span>
						</p>
					</div>
					<div class="actions">
						<p class="paypal-logo">
							<a data-action="checkout-form-submit" id="ec_shortcut_ed16280d9e884dcecb80b26ca7ae0007" href="#"><img src="https://fpdbs.sandbox.paypal.com/dynamicimageweb?cmd=_dynamic-image&amp;buttontype=ecshortcut&amp;locale=en_US" alt="Checkout with PayPal" title="Checkout with PayPal"></a>
					</p>
						<li>
						    <p class="paypal-logo">
							<span class="paypal-or">-OR-</span>
						    </p>
						</li>
						<button type="button" title="${uiLabelMap.ObbCheckout}" class="button btn-checkout" onclick="setLocation('<@ofbizUrl>checkoutorder</@ofbizUrl>')"><span><span>${uiLabelMap.ObbCheckout}</span></span></button>
					</div>
				</#if>
			</div>
		</div>
	</div>
</div>

<link rel="stylesheet" type="text/css" href="/obbresources/asset/perfect-scrollbar/css/perfect-scrollbar.min.css">
<script type="text/javascript" src="/obbresources/asset/perfect-scrollbar/js/perfect-scrollbar.jquery.js"></script>
<script type="text/javascript" src="/obbresources/asset/perfect-scrollbar/js/perfect-scrollbar.js"></script>
<script>
	(function($) {
		$("#cart-sidebar").perfectScrollbar();
	})(jQuery);
</script>