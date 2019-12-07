<#if shoppingCart?has_content && shoppingCart.size() &gt; 0>
  <h3>${uiLabelMap.ObbStep} 1: ${uiLabelMap.PageTitleShoppingCart}</h3>
  <div id="cartSummaryPanel" style="display: none;">
    <a href="javascript:void(0);" id="openCartPanel" class="button">${uiLabelMap.ObbClickHereToEdit}</a>
    <table id="cartSummaryPanel_cartItems" class="data-table cart-table" summary="This table displays the list of item added into Shopping Cart.">
      <thead>
        <tr>
          <th id="orderItem">${uiLabelMap.OrderItem}</th>
          <th id="description">${uiLabelMap.CommonDescription}</th>
          <th id="unitPrice">${uiLabelMap.ObbUnitPrice}</th>
          <th id="quantity">${uiLabelMap.OrderQuantity}</th>
          <th id="adjustment">${uiLabelMap.ObbAdjustments}</th>
          <th id="itemTotal">${uiLabelMap.ObbItemTotal}</th>
        </tr>
      </thead>
      <tfoot>
        <tr id="completedCartSubtotalRow">
          <th id="subTotal" scope="row" colspan="5">${uiLabelMap.CommonSubtotal}</th>
          <td headers="subTotal" id="completedCartSubTotal"><@ofbizCurrency amount=shoppingCart.getSubTotal() isoCode=shoppingCart.getCurrency() /></td>
        </tr>
        <#assign orderAdjustmentsTotal = 0 />
        <#list shoppingCart.getAdjustments() as cartAdjustment>
          <#assign orderAdjustmentsTotal = orderAdjustmentsTotal + Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) />
        </#list>
        <tr id="completedCartDiscountRow">
          <th id="productDiscount" scope="row" colspan="5">${uiLabelMap.ProductDiscount}</th>
          <td headers="productDiscount" id="completedCartDiscount"><input type="hidden" value="${orderAdjustmentsTotal}" id="initializedCompletedCartDiscount" /><@ofbizCurrency amount=orderAdjustmentsTotal isoCode=shoppingCart.getCurrency() /></td>
        </tr>
        <tr>
          <th id="shippingAndHandling" scope="row" colspan="5">${uiLabelMap.OrderShippingAndHandling}</th>
          <td headers="shippingAndHandling" id="completedCartTotalShipping"><@ofbizCurrency amount=shoppingCart.getTotalShipping() isoCode=shoppingCart.getCurrency() /></td>
        </tr>
        <tr>
          <th id="salesTax" scope="row" colspan="5">${uiLabelMap.OrderSalesTax}</th>
          <td headers="salesTax" id="completedCartTotalSalesTax"><@ofbizCurrency amount=shoppingCart.getTotalSalesTax() isoCode=shoppingCart.getCurrency() /></td>
        </tr>
        <tr>
          <th id="grandTotal" scope="row" colspan="5">${uiLabelMap.OrderGrandTotal}</th>
          <td headers="grandTotal" id="completedCartDisplayGrandTotal"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency() /></td>
        </tr>
      </tfoot>
      <tbody>
        <#list shoppingCart.items() as cartLine>
          <#if cartLine.getProductId()?exists>
            <#if cartLine.getParentProductId()?exists>
              <#assign parentProductId = cartLine.getParentProductId() />
            <#else>
              <#assign parentProductId = cartLine.getProductId() />
            </#if>
            <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
            <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "" /></#if>
          </#if>
          <tr id="cartItemDisplayRow_${cartLine_index}">
            <td headers="orderItem"><img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt = "Product Image" /></td>
            <td headers="description">${cartLine.getName()?if_exists}</td>
            <td headers="unitPrice">${cartLine.getDisplayPrice()}</td>
            <td headers="quantity"><span id="completedCartItemQty_${cartLine_index}">${cartLine.getQuantity()?string.number}</span></td>
            <td headers="adjustment"><span id="completedCartItemAdjustment_${cartLine_index}"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=shoppingCart.getCurrency() /></span></td>
            <td headers="itemTotal" align="right"><span id="completedCartItemSubTotal_${cartLine_index}"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency() /></span></td>
          </tr>
        </#list>
      </tbody>
    </table>
  </div>
  <div id="editCartPanel">
    <form id="cartForm" method="post" action="<@ofbizUrl></@ofbizUrl>">
      <fieldset>
        <input type="hidden" name="removeSelected" value="false" />
        <div id="cartFormServerError" class="errorMessage"></div>
        <table id="editCartPanel_cartItems" class="data-table cart-table">
          <thead>
            <tr>
              <th id="editOrderItem">${uiLabelMap.OrderItem}</th>
              <th id="editDescription">${uiLabelMap.CommonDescription}</th>
              <th id="editUnitPrice">${uiLabelMap.ObbUnitPrice}</th>
              <th id="editQuantity">${uiLabelMap.OrderQuantity}</th>
              <th id="editAdjustment">${uiLabelMap.ObbAdjustments}</th>
              <th id="editItemTotal">${uiLabelMap.ObbItemTotal}</th>
              <th id="removeItem">${uiLabelMap.FormFieldTitle_removeButton}</th>
            </tr>
          </thead>
          <tfoot>
            <tr>
              <th scope="row" colspan="5"></th>
              <th scope="row">${uiLabelMap.CommonSubtotal}</th>
              <td id="cartSubTotal" style="border:none;"><@ofbizCurrency amount=shoppingCart.getSubTotal() isoCode=shoppingCart.getCurrency() /></td>
            </tr>
            <tr>
              <th scope="row" colspan="5"></th>
              <th scope="row">${uiLabelMap.ProductDiscount}</th>
              <td id="cartDiscountValue" style="border:none;">
                <#assign orderAdjustmentsTotal = 0  />
                <#list shoppingCart.getAdjustments() as cartAdjustment>
                  <#assign orderAdjustmentsTotal = orderAdjustmentsTotal + Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) />
                </#list>
                <@ofbizCurrency amount=orderAdjustmentsTotal isoCode=shoppingCart.getCurrency() />
              </td>
            </tr>
            <tr>
              <th scope="row" colspan="5"></th>
              <th scope="row">${uiLabelMap.OrderShippingAndHandling}</th>
              <td id="cartTotalShipping" style="border:none;"><@ofbizCurrency amount=shoppingCart.getTotalShipping() isoCode=shoppingCart.getCurrency() /></td>
            </tr>
            <tr>
              <th scope="row" colspan="5"></th>
              <th scope="row">${uiLabelMap.OrderSalesTax}</th>
              <td id="cartTotalSalesTax" style="border:none;"><@ofbizCurrency amount=shoppingCart.getTotalSalesTax() isoCode=shoppingCart.getCurrency() /></td>
            </tr>
            <tr>
              <th scope="row" colspan="5"></th>
              <th scope="row">${uiLabelMap.OrderGrandTotal}</th>
              <td id="cartDisplayGrandTotal" style="border:none;"><@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency() /></td>
            </tr>
          </tfoot>
          <tbody id="updateBody">
            <#list shoppingCart.items() as cartLine>
              <tr id="cartItemRow_${cartLine_index}">
                <td headers="editOrderItem">
                  <#if cartLine.getProductId()?exists>
                    <#if cartLine.getParentProductId()?exists>
                      <#assign parentProductId = cartLine.getParentProductId() />
                    <#else>
                      <#assign parentProductId = cartLine.getProductId() />
                    </#if>
                    <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
                    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "" /></#if>
                    <#if smallImageUrl?string?has_content>
                      <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image" />
                    </#if>
                  </#if>
                </td>
                <td headers="editDescription">${cartLine.getName()?if_exists}</td>
                <td headers="editUnitPrice" id="itemUnitPrice_${cartLine_index}"><@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency() /></td>
                <td headers="editQuantity">
                  <#if cartLine.getIsPromo()>
                    ${cartLine.getQuantity()?string.number}
                  <#else>
                    <input type="hidden" name="cartLineProductId" id="cartLineProductId_${cartLine_index}" value="${cartLine.getProductId()}" />
                    <input type="text" name="update${cartLine_index}" id="qty_${cartLine_index}" value="${cartLine.getQuantity()?string.number}" class="input-text required-entry validate-number" />
                    <span id="advice-required-qty_${cartLine_index}" style="display:none;" class="errorMessage"> (${uiLabelMap.CommonRequired})</span>
                    <span id="advice-validate-number-qty_${cartLine_index}" style="display:none;" class="errorMessage"> (${uiLabelMap.CommonPleaseEnterValidNumberInThisField}) </span>
                  </#if>
                </td>
                <#if !cartLine.getIsPromo()>
                  <td headers="editAdjustment" id="addPromoCode_${cartLine_index}"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=shoppingCart.getCurrency() /></td>
                <#else>
                  <td headers="editAdjustment"><@ofbizCurrency amount=cartLine.getOtherAdjustments() isoCode=shoppingCart.getCurrency() /></td>
                </#if>
                <td headers="editItemTotal" id="displayItem_${cartLine_index}"><@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency() /></td>
                <#if !cartLine.getIsPromo()>
                  <td><a id="removeItemLink_${cartLine_index}" href="javascript:void(0);"><img id="remove_${cartLine_index}" src="<@ofbizContentUrl>/obbresources/images/removeProduct.png</@ofbizContentUrl>" alt="Remove Item Image" /></a></td>
                </#if>
              </tr>
            </#list>
          </tbody>
        </table>
      </fieldset>
      <fieldset id="productPromoCodeFields">
        <div style="margin-top:10px;margin-bottom:10px;">
          <label for="productPromoCode">${uiLabelMap.ObbEnterPromoCode}</label>
          <input id="productPromoCode" name="productPromoCode" class="input-text" type="text" value="" />
        </div>
      </fieldset>
      <fieldset style="margin-top:10px;margin-bottom:10px;">
        <a href="javascript:void(0);" class="button" id="updateShoppingCart" >${uiLabelMap.ObbContinueToStep} 2</a>
        <a style="display: none" class="button" href="javascript:void(0);" id="processingShipping">${uiLabelMap.ObbPleaseWait}....</a>
      </fieldset>
    </form>
    <script type="text/javascript">
	    //<![CDATA[
			var dataFormTmp = new VarienForm('cartForm', true);
	    //]]>
	</script>
  </div>
</#if>