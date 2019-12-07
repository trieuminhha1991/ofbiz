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
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutshippingaddress</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutoptions&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    }
}

function toggleBillingAccount(box) {
    var amountName = box.value + "_amount";
    box.checked = true;
    box.form.elements[amountName].disabled = false;

    for (var i = 0; i < box.form.elements[box.name].length; i++) {
        if (!box.form.elements[box.name][i].checked) {
            box.form.elements[box.form.elements[box.name][i].value + "_amount"].disabled = true;
        }
    }
}

//]]>
</script>
<#assign cart = shoppingCart?if_exists/>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title">
					<h1>${uiLabelMap.OrderWhereShallWeShipIt}?</h1>
					  </div>
					  <div>
						<form method="post" name="checkoutInfoForm" style="margin:0;">
						    <input type="hidden" name="checkoutpage" value="shippingaddress"/>
						    <div class="screenlet" style="height: 100%;">
						        <div class="screenlet-body" style="height: 100%;">
								<div>
							            <table width="100%" border="0" cellpadding="1" cellspacing="0">
							              <tr>
							                <td colspan="2">
							                  <#-- <a href="<@ofbizUrl>splitship</@ofbizUrl>" class="buttontext">${uiLabelMap.OrderSplitShipment}</a>
											  <span style="padding-left:5px;padding-right:5px;">|</span>-->
							                  <a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');" class="buttontext">${uiLabelMap.PartyAddNewAddress}</a>
							                  <#if (cart.getShipGroupSize() > 1)>
							                    <div style="color: red;">${uiLabelMap.OrderNOTEMultipleShipmentsExist}</div>
							                  </#if>
							                </td>
							              </tr>
							               <#if shippingContactMechList?has_content>
							                 <#list shippingContactMechList as shippingContactMech>
							                   <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
							                   <#assign checkThisAddress = (shippingContactMech_index == 0 && !cart.getShippingContactMechId()?has_content) || (cart.getShippingContactMechId()?default("") == shippingAddress.contactMechId)/>
							                   <tr><td colspan="2">&nbsp;</td></tr>
							                   <tr>
							                     <td valign="top" width="1%" nowrap="nowrap">
							                       <input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}"<#if checkThisAddress> checked="checked"</#if> />
							                     </td>
							                     <td valign="top" width="99%" nowrap="nowrap">
							                       <div>
							                         <#if shippingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${shippingAddress.toName}<br /></#if>
							                         <#if shippingAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b>&nbsp;${shippingAddress.attnName}<br /></#if>
							                         <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
							                         <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
							                         <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
							                         <#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
							                         <#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
							                         <#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if>
							                         <a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');" class="buttontext">${uiLabelMap.CommonUpdate}</a>
							                       </div>
							                     </td>
							                   </tr>
							                 </#list>
							               </#if>
							              </table>
						              </div>
						              <#-- <div class="page-title" style="margin-top:30px;">
								<h1>&nbsp;${uiLabelMap.AccountingAgreementInformation}</h1>
							  </div>
						               <table>
						                 <#if agreements?exists>
						                   <#if agreements.size()!=1>
						                     <tr>
						                       <td>&nbsp;</td>
						                       <td align='left' valign='top' nowrap="nowrap">
						                         <div class='tableheadtext'>
						                           ${uiLabelMap.OrderSelectAgreement}
						                         </div>
						                       </td>
						                       <td>&nbsp;</td>
						                       <td valign='middle'>
						                         <div class='tabletext' valign='top'>
						                           <select name="agreementId">
						                             <#list agreements as agreement>
						                               <option value='${agreement.agreementId?if_exists}'>${agreement.agreementId} - ${agreement.description?if_exists}</option>
						                             </#list>
						                           </select>
						                         </div>
						                       </td>
						                     </tr>
						                   <#else>
						                     <#list agreements as agreement>
						                        <input type="radio" name="agreementId" value="${agreement.agreementId?if_exists}"<#if checkThisAddress> checked="checked"</#if> />${agreement.description?if_exists} will be used for this order.
						                     </#list>
						                   </#if>
						                 </#if>
						               </table>
						             <br />

						            <div>&nbsp;${uiLabelMap.PartyTaxIdentification}</div>
						            ${screens.render("component://obb/widget/OrderScreens.xml#customertaxinfo")} --><#-- Party Tax Info -->
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
