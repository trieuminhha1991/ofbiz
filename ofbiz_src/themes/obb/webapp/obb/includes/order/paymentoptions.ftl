

  <h3>${uiLabelMap.AccountingPaymentInformation}</h3>
  <#-- initial screen show a list of options -->
  <form id="editPaymentOptions" method="post" action="<@ofbizUrl>setPaymentInformation</@ofbizUrl>" name="${parameters.formNameValue}">
     <fieldset>
       <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
         <div>
           <input type="checkbox" name="addGiftCard" value="Y" <#if addGiftCard?exists && addGiftCard == "Y">checked="checked"</#if> />
           <label for="addGiftCard">${uiLabelMap.AccountingCheckGiftCard}</label>
         </div>
       </#if>
       <#if productStorePaymentMethodTypeIdMap.EXT_OFFLINE?exists>
         <div>
           <input type="radio" id="paymentMethodTypeId_EXT_OFFLINE" name="paymentMethodTypeId" value="EXT_OFFLINE" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_OFFLINE">checked="checked"</#if> />
           <label for="paymentMethodTypeId_EXT_OFFLINE">${uiLabelMap.OrderPaymentOfflineCheckMoney}</label>
         </div>
       </#if>
       <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
         <div>
           <input type="radio" id="paymentMethodTypeId_CREDIT_CARD" name="paymentMethodTypeId" value="CREDIT_CARD" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "CREDIT_CARD">checked="checked"</#if> />
           <label for="paymentMethodTypeId_CREDIT_CARD">${uiLabelMap.AccountingVisaMastercardAmexDiscover}</label>
         </div>
       </#if>
       <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
         <div>
           <input type="radio" id="paymentMethodTypeId_EFT_ACCOUNT" name="paymentMethodTypeId" value="EFT_ACCOUNT" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EFT_ACCOUNT">checked="checked"</#if> />
           <label for="paymentMethodTypeId_EFT_ACCOUNT">${uiLabelMap.AccountingAHCElectronicCheck}</label>
         </div>
       </#if>
       <#if productStorePaymentMethodTypeIdMap.EXT_PAYPAL?exists>
         <div>
           <input type="radio" id="paymentMethodTypeId_EXT_PAYPAL" name="paymentMethodTypeId" value="EXT_PAYPAL" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_PAYPAL">checked="checked"</#if> />
           <label for="paymentMethodTypeId_EXT_PAYPAL">${uiLabelMap.AccountingPayWithPayPal}</label>
         </div>
       </#if>
       <div class="buttons" style="margin-top:10px;">
          <button type="submit" value="empty_cart" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button">
		<span><span>${uiLabelMap.CommonContinue}</span></span>
	      </button>
       </div>
     </fieldset>
  </form>
