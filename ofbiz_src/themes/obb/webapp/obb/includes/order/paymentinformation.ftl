<#if requestParameters.paymentMethodTypeId?has_content>
   <#assign paymentMethodTypeId = "${requestParameters.paymentMethodTypeId?if_exists}">
</#if>
<script language="JavaScript" type="text/javascript">
function shipBillAddr() {
    <#if requestParameters.singleUsePayment?default("N") == "Y">
      <#assign singleUse = "&amp;singleUsePayment=Y">
    <#else>
      <#assign singleUse = "">
    </#if>
    if (document.billsetupform.useShipAddr.checked) {
        window.location.replace("setPaymentInformation?createNew=Y&amp;addGiftCard=${requestParameters.addGiftCard?if_exists}&amp;paymentMethodTypeId=${paymentMethodTypeId?if_exists}&amp;useShipAddr=Y${singleUse}");
    } else {
        window.location.replace("setPaymentInformation?createNew=Y&amp;addGiftCard=${requestParameters.addGiftCard?if_exists}&amp;paymentMethodTypeId=${paymentMethodTypeId?if_exists}${singleUse}");
    }
}
</script>

<div class="page-title title-buttons" style="margin-top:10px;">
    <h2>${uiLabelMap.AccountingPaymentInformation}</h2>
</div>
<div class="block">
      <#-- after initial screen; show detailed screens for selected type -->
      <#if paymentMethodTypeId?if_exists == "CREDIT_CARD">
        <#if creditCard?has_content && postalAddress?has_content && !requestParameters.useShipAddr?exists>
          <form method="post" action="<@ofbizUrl>changeCreditCardAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
            <input type="hidden" name="paymentMethodId" value="${creditCard.paymentMethodId?if_exists}"/>
            <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}"/>
        <#elseif requestParameters.useShipAddr?exists>
          <form method="post" action="<@ofbizUrl>enterCreditCard</@ofbizUrl>" name="${parameters.formNameValue}">
        <#else>
          <form method="post" action="<@ofbizUrl>enterCreditCardAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
        </#if>
      <#elseif paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
        <#if eftAccount?has_content && postalAddress?has_content>
          <form method="post" action="<@ofbizUrl>changeEftAccountAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
            <input type="hidden" name="paymentMethodId" value="${eftAccount.paymentMethodId?if_exists}"/>
            <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}"/>
        <#elseif requestParameters.useShipAddr?exists>
          <form method="post" action="<@ofbizUrl>enterEftAccount</@ofbizUrl>" name="${parameters.formNameValue}">
        <#else>
          <form method="post" action="<@ofbizUrl>enterEftAccountAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
        </#if>
      <#elseif paymentMethodTypeId?if_exists == "GIFT_CARD"> <#--Don't know much how this is handled -->
        <form method="post" action="<@ofbizUrl>enterGiftCard</@ofbizUrl>" name="${parameters.formNameValue}">
      <#elseif paymentMethodTypeId?if_exists == "EXT_OFFLINE">
        <form method="post" action="<@ofbizUrl>processPaymentSettings</@ofbizUrl>" name="${parameters.formNameValue}">
      <#else>
        <div>${uiLabelMap.AccountingPaymentMethodTypeNotHandled} ${paymentMethodTypeId?if_exists}</div>
      </#if>

      <#if requestParameters.singleUsePayment?default("N") == "Y">
        <input type="hidden" name="singleUsePayment" value="Y"/>
        <input type="hidden" name="appendPayment" value="Y"/>
      </#if>
      <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS"/>
      <input type="hidden" name="partyId" value="${partyId}"/>
      <input type="hidden" name="paymentMethodTypeId" value="${paymentMethodTypeId?if_exists}"/>
      <input type="hidden" name="createNew" value="Y"/>
      <#if requestParameters.useShipAddr?exists>
        <input type="hidden" name="contactMechId" value="${parameters.contactMechId?if_exists}"/>
      </#if>

      <table id="shopping-cart-table" class="data-table cart-table">
        <#if cart.getShippingContactMechId()?exists && paymentMethodTypeId?if_exists != "GIFT_CARD">
          <tr>
            <td width="26%" align="right" valign="top" colspan="3">
              <input type="checkbox" name="useShipAddr" id="useShipAddr" value="Y" onclick="javascript:shipBillAddr();" <#if useShipAddr?exists>checked="checked"</#if>/>
              <label for="useShipAddr">${uiLabelMap.FacilityBillingAddressSameShipping}</label>
            </td>
          </tr>
        </#if>

        <#if (paymentMethodTypeId?if_exists == "CREDIT_CARD" || paymentMethodTypeId?if_exists == "EFT_ACCOUNT")>
          <tr>
            <td width="26%" align="right" valign="top"><div class="tableheadtext">${uiLabelMap.PartyBillingAddress}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">&nbsp;</td>
          </tr>
          ${screens.render("component://obb/widget/OrderScreens.xml#genericaddress")}
        </#if>

        <#-- credit card fields -->
        <#if paymentMethodTypeId?if_exists == "CREDIT_CARD">
          <#if !creditCard?has_content>
            <#assign creditCard = requestParameters>
          </#if>
          <tr>
            <td width="26%" align="right" valign="top"><div class="tableheadtext">${uiLabelMap.AccountingCreditCardInformation}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">&nbsp;</td>
          </tr>
          ${screens.render("component://obb/widget/CommonScreens.xml#creditCardFields")}
        </#if>

        <#-- eft fields -->
        <#if paymentMethodTypeId?if_exists =="EFT_ACCOUNT">
          <#if !eftAccount?has_content>
            <#assign eftAccount = requestParameters>
          </#if>
          <tr>
            <td width="26%" align="right" valign="top"><div class="tableheadtext">${uiLabelMap.AccountingEFTAccountInformation}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">&nbsp;</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingNameOnAccount}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="30" maxlength="60" name="nameOnAccount" value="${eftAccount.nameOnAccount?if_exists}"/>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingCompanyNameOnAccount}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="30" maxlength="60" name="companyNameOnAccount" value="${eftAccount.companyNameOnAccount?if_exists}"/>
            </td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingBankName}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="30" maxlength="60" name="bankName" value="${eftAccount.bankName?if_exists}"/>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingRoutingNumber}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="10" maxlength="30" name="routingNumber" value="${eftAccount.routingNumber?if_exists}"/>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingAccountType}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <select name="accountType" class="selectBox">
                <option>${eftAccount.accountType?if_exists}</option>
                <option></option>
                <option>Checking</option>
                <option>Savings</option>
              </select>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingAccountNumber}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="20" maxlength="40" name="accountNumber" value="${eftAccount.accountNumber?if_exists}"/>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.CommonDescription}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="30" maxlength="60" name="description" value="${eftAccount.description?if_exists}"/>
            </td>
          </tr>
        </#if>

        <#-- gift card fields -->
        <#if requestParameters.addGiftCard?default("") == "Y" || paymentMethodTypeId?if_exists == "GIFT_CARD">
          <input type="hidden" name="addGiftCard" value="Y"/>
          <#assign giftCard = giftCard?if_exists>
          <#if paymentMethodTypeId?if_exists != "GIFT_CARD">
            <tr>
              <td colspan="3"><hr /></td>
            </tr>
          </#if>
          <tr>
            <td width="26%" align="right" valign="top"><div class="tableheadtext">${uiLabelMap.AccountingGiftCardInformation}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">&nbsp;</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingGiftCardNumber}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="20" maxlength="60" name="giftCardNumber" value="${giftCard.cardNumber?if_exists}"/>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingPINNumber}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="10" maxlength="60" name="giftCardPin" value="${giftCard.pinNumber?if_exists}"/>
            *</td>
          </tr>
          <tr>
            <td width="26%" align="right" valign="middle"><div>${uiLabelMap.CommonDescription}</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="input-text" size="30" maxlength="60" name="description" value="${giftCard.description?if_exists}"/>
            </td>
          </tr>
          <#if paymentMethodTypeId?if_exists != "GIFT_CARD">
            <tr>
              <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingAmountToUse}</div></td>
              <td width="5">&nbsp;</td>
              <td width="74%">
                <input type="text" class="input-text" size="5" maxlength="10" name="giftCardAmount" value="${giftCard.pinNumber?if_exists}"/>
              *</td>
            </tr>
          </#if>
        </#if>

        <tr>
          <td align="center" colspan="3">
            <button type="submit" title="${uiLabelMap.CommonContinue}" class="button btn-continue">
		<span><span>${uiLabelMap.CommonContinue}</span></span>
		</button>
          </td>
        </tr>
      </table>
    </form>
</div>
