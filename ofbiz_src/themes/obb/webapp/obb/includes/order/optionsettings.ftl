
  <h3>${uiLabelMap.OrderShippingInformation}</h3>
  <form id="shipOptionsAndShippingInstructions" method="post" action="<@ofbizUrl>processShipOptions</@ofbizUrl>" name="${parameters.formNameValue}">
    <fieldset><legend>${uiLabelMap.OrderShippingInformation}</legend>
      <input type="hidden" name="finalizeMode" value="options"/>
      <ul>
      <#list carrierShipmentMethodList as carrierShipmentMethod>
        <li>
          <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
          <input type="radio" id="shipping_method_${shippingMethod}" name="shipping_method" value="${shippingMethod}" <#if shippingMethod == chosenShippingMethod?default("N@A")>checked="checked"</#if>/>
          <label for="shipping_method_${shippingMethod}">
            <#if shoppingCart.getShippingContactMechId()?exists>
              <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierShipmentMethod)?default(-1)>
            </#if>
            <#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}
              <#if shippingEst?has_content> - <#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency()/><#else>${uiLabelMap.OrderCalculatedOffline}</#if>
             </#if>
          </label>
        </li>
      </#list>
      <#if !carrierShipmentMethodList?exists || carrierShipmentMethodList?size == 0>
        <div>
          <input type="radio" name="shipping_method" value="Default" checked="checked"/>
          <label for="shipping_method">${uiLabelMap.OrderUseDefault}.</label>
        </div>
      </#if>
    </fieldset>
    <fieldset><legend>${uiLabelMap.OrderShipAllAtOnce}?</legend>
        <div>
          <input type="radio" id="maySplit_N" <#if shoppingCart.getMaySplit()?default("N") == "N">checked="checked"</#if> name="may_split" value="false"/>
          <label for="maySplit_N">${uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.</label>
        </div>
        <div>
          <input type="radio" id="maySplit_Y" <#if shoppingCart.getMaySplit()?default("N") == "Y">checked="checked"</#if> name="may_split" value="true"/>
          <label for="maySplit_Y">${uiLabelMap.OrderPleaseShipItemsBecomeAvailable}.</label>
        </div>
    </fieldset>
    <fieldset>
        <div>
          <label for="shipping_instructions">${uiLabelMap.OrderSpecialInstructions}</label>
          <textarea cols="30" rows="3" name="shipping_instructions">${shoppingCart.getShippingInstructions()?if_exists}</textarea>
        </div>
        <div>
          <label for="correspondingPoId">${uiLabelMap.OrderPoNumber}</label>
          <input type="text" class="input-text" name="correspondingPoId" value="${shoppingCart.getPoNumber()?if_exists}"/>
        </div>
    </fieldset>
    <#if productStore.showCheckoutGiftOptions?if_exists != "N">
        <fieldset><legend>${uiLabelMap.OrderIsThisGift}</legend>
          <div>
            <input type="radio" id="is_gift_Y" <#if shoppingCart.getIsGift()?default("Y") == "Y">checked="checked"</#if> name="is_gift" value="true"/>
            <label for="is_gift_Y">${uiLabelMap.CommonYes}</label>
          </div>
          <div>
            <input type="radio" id="is_gift_N" <#if shoppingCart.getIsGift()?default("N") == "N">checked="checked"</#if> name="is_gift" value="false"/>
            <label far="is_gift_N">${uiLabelMap.CommonNo}</label>
          </div>
          <div>
            <label for="gift_message">${uiLabelMap.OrderGiftMessage}</label>
            <textarea class="textAreaBox" name="gift_message">${shoppingCart.getGiftMessage()?if_exists}</textarea>
          </div>
        </fieldset>
    </#if>
    <div class="buttons">
      <button type="submit" value="empty_cart" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button">
	<span><span>${uiLabelMap.CommonContinue}</span></span>
      </button>
    </div>
  </form>
