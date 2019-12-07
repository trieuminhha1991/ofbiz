<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>
	<!--Mini Cart Start-->
	<div id="cart">
	  <#if (shoppingCartSize > 0)>
      <div class="heading">
        <h4><img width="32" height="32" alt="small-cart-icon" src="/bigshop/images/cart-bg.png"></h4>
        <a><span id="cart-total">${shoppingCart.getTotalQuantity()} <#if shoppingCart.getTotalQuantity() == 1>${uiLabelMap.BigshopOrderItems}<#else/>${uiLabelMap.BigshopOrderItems}</#if>, <@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></span></a>
      </div>
      <div class="content">
        <div class="mini-cart-info">
          <table>
		<tbody>
            <#list shoppingCart.items() as cartLine>
				<tr>
					<td class="image">
                    <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
                    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg" /></#if>
                    <#if smallImageUrl?string?has_content>
                        <img width="43" height="43" src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" class="imageborder" />
                    </#if>
                    </td>
                    <td class="name">
	                  <#if cartLine.getProductId()?exists>
	                      <#if cartLine.getParentProductId()?exists>
	                          <a href="<@ofbizCatalogAltUrl productId=cartLine.getParentProductId()/>" class="invarseColor">${cartLine.getName()}</a>
	                      <#else>
	                          <a href="<@ofbizCatalogAltUrl productId=cartLine.getProductId()/>" class="invarseColor">${cartLine.getName()}</a>
	                      </#if>
	                  <#else>
	                    <strong>${cartLine.getItemTypeDescription()?if_exists}</strong>
	                  </#if>
	                  <#if cartLine.getIsPromo()><br/><strong>${uiLabelMap.BigshopPromo}<strong></#if>
	                </td>
	                <td class="quantity">
				x${cartLine.getQuantity()?string.number}
	                </td>
	                <td class="total"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency()/></td>
            </#list>
			</tbody>
          </table>
        </div>
        <div class="mini-cart-total">
          <table>
            <tbody>
              <tr>
                <td class="right"><b>${uiLabelMap.EcommerceCartTotal}:</b></td>
                <td class="right"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/></td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="checkout"><a href="<@ofbizUrl>view/showcart</@ofbizUrl>" class="button">${uiLabelMap.OrderViewCart}</a> &nbsp; <a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckout}</a></div>
      </div>
      <#else>
      <div class="heading">
	<h4><img width="32" height="32" alt="small-cart-icon" src="/bigshop/images/cart-bg.png"></h4>
	<a><span id="cart-total">${uiLabelMap.BigshopOrderShoppingCartEmpty}</span></a>
      </div>
      <div class="content">
      </div>
      </#if>
    </div>
    <!--Mini Cart End-->
