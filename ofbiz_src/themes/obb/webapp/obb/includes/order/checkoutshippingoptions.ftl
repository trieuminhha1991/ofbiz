<script language="javascript" type="text/javascript">
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
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutshippingaddress&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }
}

//]]>
</script>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title">
					<h1>2)&nbsp;${uiLabelMap.OrderHowShallWeShipIt}?</h1>
					  </div>
					  <div>
						<form method="post" name="checkoutInfoForm" style="margin:0;">
					    <input type="hidden" name="checkoutpage" value="shippingoptions"/>
					    <div class="screenlet" style="height: 100%;">
					        <div class="screenlet-body" style="height: 100%;">
					            <table width="100%" cellpadding="1" border="0" cellpadding="0" cellspacing="0">
					              <#list carrierShipmentMethodList as carrierShipmentMethod>
					                <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
					                <tr>
					                  <td width="1%" valign="top" >
					                    <input type="radio" id="tmp_${carrierShipmentMethod_index}" name="shipping_method" value="${shippingMethod}" <#if shippingMethod == StringUtil.wrapString(chosenShippingMethod!"N@A")>checked="checked"</#if> />
					                  </td>
					                  <td valign="top">
					                    <label for="tmp_${carrierShipmentMethod_index}">
					                      <#if shoppingCart.getShippingContactMechId()?exists>
					                        <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierShipmentMethod)?default(-1)>
					                      </#if>
					                      <#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>${carrierShipmentMethod.description?if_exists}
					                      <#if shippingEst?has_content> - <#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency()/><#else>${uiLabelMap.OrderCalculatedOffline}</#if></#if>
					                    </label>
					                  </td>
					                </tr>
					              </#list>
					              <#if !carrierShipmentMethodList?exists || carrierShipmentMethodList?size == 0>
					                <tr>
					                  <td width="1%" valign="top">
					                    <input type="radio" name="shipping_method" value="Default" checked="checked" />
					                  </td>
					                  <td valign="top">
					                    <div>${uiLabelMap.OrderUseDefault}.</div>
					                  </td>
					                </tr>
					              </#if>
					              <#-- <tr><td colspan="2"><hr color="#ebebeb" style="margin-top:5px;margin-bottom:20px;"/></td></tr>
					              <tr>
					                <td colspan="2">
					                  <h2>${uiLabelMap.OrderShipAllAtOnce}?</h2>
					                </td>
					              </tr>
					              <tr>
					                <td valign="top">
					                  <input type="radio" <#if "Y" != shoppingCart.getMaySplit()?default("N")>checked="checked"</#if> name="may_split" id="may_split" value="false" />
					                </td>
					                <td valign="top">
					                  <label for="may_split">${uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.</label>
					                </td>
					              </tr>
					              <tr>
					                <td valign="top">
					                  <input <#if "Y" == shoppingCart.getMaySplit()?default("N")>checked="checked"</#if> type="radio" name="may_split" id="may_split" value="true" />
					                </td>
					                <td valign="top">
					                  <label for="may_split">${uiLabelMap.OrderPleaseShipItemsBecomeAvailable}.</label>
					                </td>
					              </tr> -->
					              <tr><td colspan="2"><hr color="#ebebeb" style="margin-top:20px;margin-bottom:20px;"/></td></tr>
					              <tr>
					                <td colspan="2">
					                  <h2>${uiLabelMap.OrderSpecialInstructions}</h2>
					                </td>
					              </tr>
					              <tr>
					                <td colspan="2">
					                  <textarea class="textAreaBox" cols="30" rows="3" wrap="hard" name="shipping_instructions">${shoppingCart.getShippingInstructions()?if_exists}</textarea>
					                </td>
					              </tr>
					              <tr><td colspan="2"><hr color="#ebebeb" style="margin-top:20px;margin-bottom:20px;"/></td></tr>
					              <#-- <tr>
					                <td colspan="2">
					                  <h2>${uiLabelMap.OrderPoNumber}</h2>&nbsp;
					                  <#if shoppingCart.getPoNumber()?exists && shoppingCart.getPoNumber() != "(none)">
					                    <#assign currentPoNumber = shoppingCart.getPoNumber()>
					                  </#if>
					                  <input type="text" class="input-text" name="correspondingPoId" size="15" value="${currentPoNumber?if_exists}"/>
					                </td>
					              </tr>
					              <#if productStore.showCheckoutGiftOptions?if_exists != "N">
					              <tr><td colspan="2"><hr color="#ebebeb" style="margin-top:5px;margin-bottom:20px;"/></td></tr>
					              <tr>
					                <td colspan="2">
					                  <div>
					                    <h2>${uiLabelMap.OrderIsThisGift}</h2>
					                    <input type="radio" <#if "Y" == shoppingCart.getIsGift()?default("N")>checked="checked"</#if> name="is_gift" value="true" /><span><label for="is_gift">${uiLabelMap.CommonYes}</label></span>
					                    <input type="radio" <#if "Y" != shoppingCart.getIsGift()?default("N")>checked="checked"</#if> name="is_gift" value="false" /><span><label for="is_gift">${uiLabelMap.CommonNo}</label></span>
					                  </div>
					                </td>
					              </tr>
					              <tr><td colspan="2"><hr color="#ebebeb" style="margin-top:20px;margin-bottom:20px;"/></td></tr>
					              <tr>
					                <td colspan="2">
					                  <h2>${uiLabelMap.OrderGiftMessage}</h2>
					                </td>
					              </tr>
					              <tr>
					                <td colspan="2">
					                  <textarea class="textAreaBox" cols="30" rows="3" wrap="hard" name="gift_message">${shoppingCart.getGiftMessage()?if_exists}</textarea>
					                </td>
					              </tr>
					              <#else/>
					              <input type="hidden" name="is_gift" value="false"/>
					              </#if>
					              <tr><td colspan="2"><hr color="#ebebeb" style="margin-top:20px;margin-bottom:20px;"/></td></tr> -->
					              <tr>
					                <td colspan="2">
					                  <h2>${uiLabelMap.PartyEmailAddresses}</h2>
					                </td>
					              </tr>
					              <tr>
					                <td colspan="2">
					                  <div>${uiLabelMap.OrderEmailSentToFollowingAddresses}:</div>
					                  <div>
					                    <b>
					                      <#list emailList as email>
					                        ${email.infoString?if_exists}<#if email_has_next>,</#if>
					                      </#list>
					                    </b>
					                  </div>
					                  <div>${uiLabelMap.OrderUpdateEmailAddress} <a href="<@ofbizUrl>dashboard?DONE_PAGE=checkoutoptions</@ofbizUrl>" class="buttontext">${uiLabelMap.PartyProfile}</a>.</div>
					                  <br />
					                  <div>${uiLabelMap.OrderCommaSeperatedEmailAddresses}:</div>
					                  <input type="text" class="input-text" size="30" name="order_additional_emails" value="${shoppingCart.getOrderAdditionalEmails()?if_exists}"/>
					                </td>
					              </tr>
					            </table>
					        </div>
					    </div>
					</form>
					<table width="100%" style="margin-top:20px;">
					  <tr valign="top">
					    <td>
					      <button type="button" name="update_cart_action" value="empty_cart" onclick="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" title="${uiLabelMap.OrderBacktoShoppingCart}" class="button btn-empty" id="empty_cart_button">
						  <span><span>${uiLabelMap.OrderBacktoShoppingCart}</span></span>
					  </button>
					    </td>
					    <td align="right" style="text-align:right;">
						<button type="button" name="update_cart_action" value="empty_cart" onclick="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" title="${uiLabelMap.CommonNext}" class="button btn-empty" id="empty_cart_button">
							<span><span>${uiLabelMap.CommonNext}</span></span>
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
