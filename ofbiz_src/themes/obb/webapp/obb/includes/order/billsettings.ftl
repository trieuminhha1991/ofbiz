

<script language="JavaScript" type="text/javascript">
function shipBillAddr() {
    <#if requestParameters.singleUsePayment?default("N") == "Y">
      <#assign singleUse = "&amp;singleUsePayment=Y">
    <#else>
      <#assign singleUse = "">
    </#if>
    if (document.billsetupform.useShipAddr.checked) {
        window.location.replace("setBilling?createNew=Y&amp;finalizeMode=payment&amp;useGc=${requestParameters.useGc?if_exists}&amp;paymentMethodType=${paymentMethodType?if_exists}&amp;useShipAddr=Y${singleUse}");
    } else {
        window.location.replace("setBilling?createNew=Y&amp;finalizeMode=payment&amp;useGc=${requestParameters.useGc?if_exists}&amp;paymentMethodType=${paymentMethodType?if_exists}${singleUse}");
    }
}
</script>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="cart">
							<div class="screenlet">
							    <div class="page-title title-buttons">
						            <#if requestParameters.singleUsePayment?default("N") != "Y">
						              <div>
						                ${screens.render(anonymoustrailScreen)}
						              </div>
						            </#if>
							        <h3>${uiLabelMap.AccountingPaymentInformation}</h3>
							    </div>
							    <div class="screenlet-body">
							        <#if (paymentMethodType?exists && !requestParameters.resetType?has_content) || finalizeMode?default("") == "payment">
							          <#-- after initial screen; show detailed screens for selected type -->
							          <#if paymentMethodType == "CC">
							            <#if creditCard?has_content && postalAddress?has_content>
							              <form method="post" action="<@ofbizUrl>changeCreditCardAndBillingAddress</@ofbizUrl>" name="billsetupform">
							                <input type="hidden" name="paymentMethodId" value="${creditCard.paymentMethodId?if_exists}" />
							                <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}" />
							            <#elseif requestParameters.useShipAddr?exists>
							              <form method="post" action="<@ofbizUrl>enterCreditCard</@ofbizUrl>" name="billsetupform">
							            <#else>
							              <form method="post" action="<@ofbizUrl>enterCreditCardAndBillingAddress</@ofbizUrl>" name="billsetupform">
							            </#if>
							          </#if>
							          <#if paymentMethodType == "EFT">
							            <#if eftAccount?has_content && postalAddress?has_content>
							              <form method="post" action="<@ofbizUrl>changeEftAccountAndBillingAddress</@ofbizUrl>" name="billsetupform">
							                <input type="hidden" name="paymentMethodId" value="${eftAccount.paymentMethodId?if_exists}" />
							                <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}" />
							            <#elseif requestParameters.useShipAddr?exists>
							              <form method="post" action="<@ofbizUrl>enterEftAccount</@ofbizUrl>" name="billsetupform">
							            <#else>
							              <form method="post" action="<@ofbizUrl>enterEftAccountAndBillingAddress</@ofbizUrl>" name="billsetupform">
							            </#if>
							          </#if>
							          <#if paymentMethodType == "GC">
							            <form method="post" action="<@ofbizUrl>finalizeOrder</@ofbizUrl>" name="billsetupform">
							          </#if>

							          <#if requestParameters.singleUsePayment?default("N") == "Y">
							            <input type="hidden" name="singleUsePayment" value="Y" />
							            <input type="hidden" name="appendPayment" value="Y" />
							          </#if>

							          <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS" />
							          <input type="hidden" name="partyId" value="${partyId}" />
							          <input type="hidden" name="paymentMethodType" value="${paymentMethodType}" />
							          <input type="hidden" name="finalizeMode" value="payment" />
							          <input type="hidden" name="createNew" value="Y" />
							          <#if requestParameters.useShipAddr?exists>
							            <input type="hidden" name="contactMechId" value="${postalFields.contactMechId}" />
							          </#if>

							          <table id="shopping-cart-table" class="data-table cart-table">
							            <#if cart.getShippingContactMechId()?exists && paymentMethodType != "GC">
							              <tr>
							                <td colspan="3" valign="center">
							                  <input type="checkbox" name="useShipAddr" id="useShipAddr" value="Y" onclick="javascript:shipBillAddr();" <#if requestParameters.useShipAddr?exists>checked="checked"</#if> />
							                  <label for="useShipAddr">${uiLabelMap.FacilityBillingAddressSameShipping}</label>
							                </td>
							              </tr>
							            </#if>

							            <#if (paymentMethodType == "CC" || paymentMethodType == "EFT")>
							              <tr>
							                <td width="26%" align="right" valign="top"><div class="tableheadtext">${uiLabelMap.PartyBillingAddress}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">&nbsp;</td>
							              </tr>
							              ${screens.render("component://obb/widget/OrderScreens.xml#genericaddress")}
							            </#if>

							            <#-- credit card fields -->
							            <#if paymentMethodType == "CC">
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
							            <#if paymentMethodType =="EFT">
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
							                  <input type="text" class="input-text" size="30" maxlength="60" name="nameOnAccount" value="${eftAccount.nameOnAccount?if_exists}" />
							                *</td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingCompanyNameOnAccount}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <input type="text" class="input-text" size="30" maxlength="60" name="companyNameOnAccount" value="${eftAccount.companyNameOnAccount?if_exists}" />
							                </td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingBankName}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <input type="text" class="input-text" size="30" maxlength="60" name="bankName" value="${eftAccount.bankName?if_exists}" />
							                *</td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingRoutingNumber}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <input type="text" class="input-text" size="10" maxlength="30" name="routingNumber" value="${eftAccount.routingNumber?if_exists}" />
							                *</td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingAccountType}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <select name="accountType" class='selectBox'>
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
							                  <input type="text" class="input-text" size="20" maxlength="40" name="accountNumber" value="${eftAccount.accountNumber?if_exists}" />
							                *</td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.CommonDescription}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <input type="text" class="input-text" size="30" maxlength="60" name="description" value="${eftAccount.description?if_exists}" />
							                </td>
							              </tr>
							            </#if>

							            <#-- gift card fields -->
							            <#if requestParameters.useGc?default("") == "GC" || paymentMethodType == "GC">
							              <#assign giftCard = requestParameters>
							              <input type="hidden" name="addGiftCard" value="Y" />
							              <#if paymentMethodType != "GC">
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
							                  <input type="text" class="input-text" size="20" maxlength="60" name="giftCardNumber" value="${giftCard.cardNumber?if_exists}" />
							                *</td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingPINNumber}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <input type="text" class="input-text" size="10" maxlength="60" name="giftCardPin" value="${giftCard.pinNumber?if_exists}" />
							                *</td>
							              </tr>
							              <tr>
							                <td width="26%" align="right" valign="middle"><div>${uiLabelMap.CommonDescription}</div></td>
							                <td width="5">&nbsp;</td>
							                <td width="74%">
							                  <input type="text" class="input-text" size="30" maxlength="60" name="description" value="${giftCard.description?if_exists}" />
							                </td>
							              </tr>
							              <#if paymentMethodType != "GC">
							                <tr>
							                  <td width="26%" align="right" valign="middle"><div>${uiLabelMap.AccountingAmountToUse}</div></td>
							                  <td width="5">&nbsp;</td>
							                  <td width="74%">
							                    <input type="text" class="input-text" size="5" maxlength="10" name="giftCardAmount" value="${giftCard.pinNumber?if_exists}" />
							                  *</td>
							                </tr>
							              </#if>
							            </#if>

							            <tr>
							              <td align="center" colspan="3">
							                <button type="submit" value="empty_cart" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button">
											<span><span>${uiLabelMap.CommonContinue}</span></span>
							                  </button>
							              </td>
							            </tr>
							          </table>
							        <#else>
							          <#-- initial screen show a list of options -->
							          <form method="post" action="<@ofbizUrl>finalizeOrder</@ofbizUrl>" name="billsetupform">
							            <input type="hidden" name="finalizeMode" value="payment" />
							            <input type="hidden" name="createNew" value="Y" />
							            <table id="shopping-cart-table" class="data-table cart-table">
							              <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
							              <tr>
							                <td width='5%' nowrap="nowrap"><input type="checkbox" name="useGc" value="GC" <#if paymentMethodType?exists && paymentMethodType == "GC">checked="checked"</#if> /></td>
							                <td width='95%' nowrap="nowrap"><div>${uiLabelMap.AccountingCheckGiftCard}</div></td>
							              </tr>
							              <tr><td colspan="2"><hr /></td></tr>
							              </#if>
							              <#if productStorePaymentMethodTypeIdMap.EXT_OFFLINE?exists>
							              <tr>
							                <td width='5%' nowrap="nowrap"><input type="radio" name="paymentMethodType" value="offline" <#if paymentMethodType?exists && paymentMethodType == "offline">checked="checked"</#if> /></td>
							                <td width='95%'nowrap="nowrap"><div>${uiLabelMap.OrderPaymentOfflineCheckMoney}</div></td>
							              </tr>
							              <tr><td colspan="2"><hr /></td></tr>
							              </#if>
							              <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
							              <tr>
							                <td width='5%' nowrap="nowrap"><input type="radio" name="paymentMethodType" value="CC" <#if paymentMethodType?exists && paymentMethodType == "CC">checked="checked"</#if> /></td>
							                <td width='95%' nowrap="nowrap"><div>${uiLabelMap.AccountingVisaMastercardAmexDiscover}</div></td>
							              </tr>
							              <tr><td colspan="2"><hr /></td></tr>
							              </#if>
							              <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
							              <tr>
							                <td width='5%' nowrap="nowrap"><input type="radio" name="paymentMethodType" value="EFT" <#if paymentMethodType?exists && paymentMethodType == "EFT">checked="checked"</#if> /></td>
							                <td width='95%' nowrap="nowrap"><div>${uiLabelMap.AccountingAHCElectronicCheck}</div></td>
							              </tr>
							              </#if>
							              <tr>
							                <td align="center" colspan="2">
							                  <button type="submit" value="empty_cart" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button">
											<span><span>${uiLabelMap.CommonContinue}</span></span>
							                  </button>
							                </td>
							              </tr>
							            </table>
							          </form>
							        </#if>
							    </div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
