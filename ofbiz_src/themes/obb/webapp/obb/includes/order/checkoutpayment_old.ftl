<!-- TODO : Need formatting -->
<script type="text/javascript">
//<![CDATA[
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; checkout
        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateCheckoutOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "GC") {
        // edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }else if(mode = "EG")
    //edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
}
//]]>
$(document).ready(function(){
var issuerId = "";
    if ($('#checkOutPaymentId_IDEAL').attr('checked') == true) {
        $('#issuers').show();
        issuerId = $('#issuer').val();
        $('#issuerId').val(issuerId);
    } else {
        $('#issuers').hide();
        $('#issuerId').val('');
    }
    $('input:radio').click(function(){
        if ($(this).val() == "EXT_IDEAL") {
            $('#issuers').show();
            issuerId = $('#issuer').val();
            $('#issuerId').val(issuerId);
        } else {
            $('#issuers').hide();
            $('#issuerId').val('');
        }
    });
    $('#issuer').change(function(){
        issuerId = $(this).val();
        $('#issuerId').val(issuerId);
    });
});
</script>


<#assign cart = shoppingCart?if_exists />
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title">
					<h1>3)${uiLabelMap.OrderHowShallYouPay}?</h1>
					  </div>
					  <div>
						<form method="post" id="checkoutInfoForm" action="">
						  <fieldset>
						    <input type="hidden" name="checkoutpage" value="payment" />
						    <input type="hidden" name="BACK_PAGE" value="checkoutoptions" />
						    <input type="hidden" name="issuerId" id="issuerId" value="" />
						    <div class="screenlet">
						        <div class="screenlet-body inline">
						            <#-- Payment Method Selection -->
						            <div>
						              <#--  <label>${uiLabelMap.CommonAdd}:</label>
						                <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
						                  <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'NC', '');" class="button">${uiLabelMap.AccountingCreditCard}</a>
						                </#if>
						                <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
						                  <span style="margin-left:5px;margin-right:5px">|</span><a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'NE', '');" class="button">${uiLabelMap.AccountingEFTAccount}</a>
						                </#if> -->
						              <#if productStorePaymentMethodTypeIdMap.EXT_OFFLINE?exists>
						              </div>
						              <div>
						                  <input type="radio" id="checkOutPaymentId_OFFLINE" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == checkOutPaymentId>checked="checked"</#if> />
						                  <label for="checkOutPaymentId_OFFLINE">${uiLabelMap.OrderMoneyOrder}</label>
						              </div>
						              </#if>
						              <#if productStorePaymentMethodTypeIdMap.EXT_COD?exists>
						              <div>
						                  <input type="radio" id="checkOutPaymentId_COD" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == checkOutPaymentId>checked="checked"</#if> />
						                  <label for="checkOutPaymentId_COD">${uiLabelMap.OrderCOD}</label>
						              </div>
						              </#if>
						              <#if productStorePaymentMethodTypeIdMap.EXT_WORLDPAY?exists>
						              <div>
						                  <input type="radio" id="checkOutPaymentId_WORLDPAY" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == checkOutPaymentId>checked="checked"</#if> />
						                  <label for="checkOutPaymentId_WORLDPAY">${uiLabelMap.AccountingPayWithWorldPay}</label>
						              </div>
						              </#if>
						              <#if productStorePaymentMethodTypeIdMap.EXT_PAYPAL?exists>
						              <div>
						                  <input type="radio" id="checkOutPaymentId_PAYPAL" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == checkOutPaymentId>checked="checked"</#if> />
						                  <label for="checkOutPaymentId_PAYPAL">${uiLabelMap.AccountingPayWithPayPal}</label>
						              </div>
						              </#if>
						              <#if productStorePaymentMethodTypeIdMap.EXT_IDEAL?exists>
						              <div>
						                  <input type="radio" id="checkOutPaymentId_IDEAL" name="checkOutPaymentId" value="EXT_IDEAL" <#if "EXT_IDEAL" == checkOutPaymentId>checked="checked"</#if> />
						                  <label for="checkOutPaymentId_IDEAL">${uiLabelMap.AccountingPayWithiDEAL}</label>
						              </div>

						              <div id="issuers">
						              <div><label >${uiLabelMap.AccountingBank}</label></div>
						                <select name="issuer" id="issuer">
						                <#if issuerList?has_content>
						                    <#list issuerList as issuer>
						                        <option value="${issuer.getIssuerID()}" >${issuer.getIssuerName()}</option>
						                    </#list>
						                </#if>
						              </select>
						              </div>
						              </#if>
					              <#--
						              <#if !paymentMethodList?has_content>
						              <br/>
						              <div>
						                  <strong>${uiLabelMap.AccountingNoPaymentMethods}.</strong>
						              </div>
						            <#else>
						              <#list paymentMethodList as paymentMethod>
						                <#if paymentMethod.paymentMethodTypeId == "GIFT_CARD">
						                 <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
						                  <#assign giftCard = paymentMethod.getRelatedOne("GiftCard", false) />
						                  <#if giftCard?has_content && giftCard.cardNumber?has_content>
						                    <#assign giftCardNumber = "" />
						                    <#assign pcardNumber = giftCard.cardNumber />
						                    <#if pcardNumber?has_content>
						                      <#assign psize = pcardNumber?length - 4 />
						                      <#if 0 &lt; psize>
						                        <#list 0 .. psize-1 as foo>
						                          <#assign giftCardNumber = giftCardNumber + "*" />
						                        </#list>
						                        <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3] />
						                      <#else>
						                        <#assign giftCardNumber = pcardNumber />
						                      </#if>
						                    </#if>
						                  </#if>
						                  <div>
						                      <input type="checkbox" id="checkOutPayment_${paymentMethod.paymentMethodId}" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if cart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if> />
						                      <label for="checkOutPayment_${paymentMethod.paymentMethodId}">${uiLabelMap.AccountingGift}:${giftCardNumber}</label>
						                        <#if paymentMethod.description?has_content>(${paymentMethod.description})</#if>
						                        <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'EG', '${paymentMethod.paymentMethodId}');" class="button">${uiLabelMap.CommonUpdate}</a>
						                        <strong>${uiLabelMap.OrderBillUpTo}:</strong> <input type="text" size="5" class="input-text" name="amount_${paymentMethod.paymentMethodId}" value="<#if (cart.getPaymentAmount(paymentMethod.paymentMethodId)?default(0) > 0)>${cart.getPaymentAmount(paymentMethod.paymentMethodId)?string("##0.00")}</#if>" />
						                  </div>
						                 </#if>
						                <#elseif paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
						                 <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
						                  <#assign creditCard = paymentMethod.getRelatedOne("CreditCard", false) />
						                  <div>
						                      <input type="checkbox" id="checkOutPayment_${paymentMethod.paymentMethodId}" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if cart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if> />
						                      <label for="checkOutPayment_${paymentMethod.paymentMethodId}">CC:${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</label>
						                        <#if paymentMethod.description?has_content>(${paymentMethod.description})</#if>
						                        <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'EC', '${paymentMethod.paymentMethodId}');" class="button">${uiLabelMap.CommonUpdate}</a>
						                        <label for="amount_${paymentMethod.paymentMethodId}"><strong>${uiLabelMap.OrderBillUpTo}:</strong></label><input type="text" size="5" class="input-text" id="amount_${paymentMethod.paymentMethodId}" name="amount_${paymentMethod.paymentMethodId}" value="<#if (cart.getPaymentAmount(paymentMethod.paymentMethodId)?default(0) > 0)>${cart.getPaymentAmount(paymentMethod.paymentMethodId)?string("##0.00")}</#if>" />
						                  </div>
						                 </#if>
						                <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
						                 <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
						                  <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount", false) />
						                  <div>
						                      <input type="radio" id="checkOutPayment_${paymentMethod.paymentMethodId}" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked="checked"</#if> />
						                      <label for="checkOutPayment_${paymentMethod.paymentMethodId}">${uiLabelMap.AccountingEFTAccount}:${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</label>
						                        <#if paymentMethod.description?has_content><p>(${paymentMethod.description})</p></#if>
						                      <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'EE', '${paymentMethod.paymentMethodId}');" class="button">${uiLabelMap.CommonUpdate}</a>
						                  </div>
						                 </#if>
						                </#if>
						              </#list>
						            </#if>

						            <#if productStorePaymentMethodTypeIdMap.EXT_BILLACT?exists>
						              <#if billingAccountList?has_content>
						                <div>
						                    <select name="billingAccountId" id="billingAccountId">
						                      <option value=""></option>
						                        <#list billingAccountList as billingAccount>
						                          <#assign availableAmount = billingAccount.accountBalance>
						                          <#assign accountLimit = billingAccount.accountLimit>
						                          <option value="${billingAccount.billingAccountId}" <#if billingAccount.billingAccountId == selectedBillingAccountId?default("")>selected="selected"</#if>>${billingAccount.description?default("")} [${billingAccount.billingAccountId}] ${uiLabelMap.ObbAvailable} <@ofbizCurrency amount=availableAmount isoCode=billingAccount.accountCurrencyUomId/> ${uiLabelMap.ObbLimit} <@ofbizCurrency amount=accountLimit isoCode=billingAccount.accountCurrencyUomId/></option>
						                        </#list>
						                    </select>
						                    <label for="billingAccountId">${uiLabelMap.FormFieldTitle_billingAccountId}</label>
						                </div>
						                <div>
						                    <input type="text" size="5" id="billingAccountAmount" name="billingAccountAmount" value="" />
						                    <label for="billingAccountAmount">${uiLabelMap.OrderBillUpTo}</label>
						                </div>
						              </#if>
						            </#if>

						            <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
						              <div>
						                  <input type="checkbox" id="addGiftCard" name="addGiftCard" value="Y" />
						                  <input type="hidden" name="singleUseGiftCard" value="Y" />
						                  <label for="addGiftCard">${uiLabelMap.AccountingUseGiftCardNotOnFile}</label>
						              </div>
						              <div>
						                  <label for="giftCardNumber">${uiLabelMap.AccountingNumber}</label>
						                  <input type="text" size="15" class="input-text" id="giftCardNumber" name="giftCardNumber" value="${(requestParameters.giftCardNumber)?if_exists}" onfocus="document.getElementById('addGiftCard').checked=true;" />
						              </div>
						              <#if cart.isPinRequiredForGC(delegator)>

						              <div>
						                  <label for="giftCardPin">${uiLabelMap.AccountingPIN}</label>
						                  <input type="text" size="10" class="input-text" id="giftCardPin" name="giftCardPin" value="${(requestParameters.giftCardPin)?if_exists}" onfocus="document.getElementById('addGiftCard').checked=true;" />
						              </div>
						              </#if>
						              <div>
						                  <label for="giftCardAmount">${uiLabelMap.AccountingAmount}</label>
						                  <input type="text" size="6" class="input-text" id="giftCardAmount" name="giftCardAmount" value="${(requestParameters.giftCardAmount)?if_exists}" onfocus="document.getElementById('addGiftCard').checked=true;" />
						              </div>
						            </#if>

						              <div style="margin-top:10px;">
						                    <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists><a href="<@ofbizUrl>setBilling?paymentMethodType=CC&amp;singleUsePayment=Y</@ofbizUrl>" class="button">${uiLabelMap.AccountingSingleUseCreditCard}</a></#if>
						                    <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists><span style="margin-left:5px;margin-right:5px">|</span><a href="<@ofbizUrl>setBilling?paymentMethodType=GC&amp;singleUsePayment=Y</@ofbizUrl>" class="button">${uiLabelMap.AccountingSingleUseGiftCard}</a></#if>
						                    <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists><span style="margin-left:5px;margin-right:5px">|</span><a href="<@ofbizUrl>setBilling?paymentMethodType=EFT&amp;singleUsePayment=Y</@ofbizUrl>" class="button">${uiLabelMap.AccountingSingleUseEFTAccount}</a></#if>
						              </div>
						               -->
						            <#-- End Payment Method Selection -->
						        </div>
						    </div>
						  </fieldset>
						</form>
						<style type="text/css">
					form input[type=text]{
						width:150px;
						margin-top:5px;
					}
			            </style>
			            <table width="100%" style="margin-top:20px;">
						  <tr valign="top">
						    <td>
						      <button type="button" name="update_cart_action" value="empty_cart" onclick="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'CS', '');" title="${uiLabelMap.OrderBacktoShoppingCart}" class="button btn-empty" id="empty_cart_button">
							  <span><span>${uiLabelMap.OrderBacktoShoppingCart}</span></span>
						  </button>
						    </td>
						    <td align="right" style="text-align:right;">
							<button type="button" name="update_cart_action" value="empty_cart" onclick="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'DN', '');" title="${uiLabelMap.OrderContinueToFinalOrderReview}" class="button btn-empty" id="empty_cart_button">
								<span><span>${uiLabelMap.OrderContinueToFinalOrderReview}</span></span>
							</button>
						    </td>
						  </tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div>