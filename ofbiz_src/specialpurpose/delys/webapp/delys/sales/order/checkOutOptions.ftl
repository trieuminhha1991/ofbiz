<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script type="text/javascript">
//<![CDATA[
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; checkout
        form.action="<@ofbizUrl>checkout</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateCheckoutOptions/showCart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}&preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "GC") {
        // edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=quickcheckout&partyId=${shoppingCart.getPartyId()}&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "SP") {
        // split payment
        form.action="<@ofbizUrl>updateCheckoutOptions/checkoutpayment?partyId=${shoppingCart.getPartyId()}</@ofbizUrl>";
        form.submit();
    } else if (mode == "SA") {
        // selected shipping address
        form.action="<@ofbizUrl>updateCheckoutOptions/quickCheckout</@ofbizUrl>";
        form.submit();
    } else if (mode == "SC") {
        // selected ship to party
        form.action="<@ofbizUrl>cartUpdateShipToCustomerParty</@ofbizUrl>";
        form.submit();
    }
}
//]]>
</script>

<#assign shipping = !shoppingCart.containAllWorkEffortCartItems()> <#-- contains items which need shipping? -->
<div class="widget-box transparent">
  	<div class="widget-header">
	  	<h3>
			${uiLabelMap.DAShippingAndPaymentMethod}
	    </h3>
	    <div class="widget-toolbar none-content pull-right">
	    	<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');"><i class="open-sans icon-arrow-left"></i>${uiLabelMap.OrderBacktoShoppingCart}</a>
			<a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');">${uiLabelMap.DAContinueToFinalOrderReview}&nbsp;<i class="open-sans icon-arrow-right icon-on-right"></i></a>
	    </div>
	</div>
</div>

<div class="widget-box olbius-extra">
	<div class="widget-box transparent no-bottom-border">
    <div class="widget-body">
    <div class="widget-body-inner"> 
	<div class="widget-main">
		<div class="row-fluid">
			<form method="post" name="checkoutInfoForm" class="form-horizontal basic-custom-form form-decrease-padding" style="margin:0;">
			  	<input type="hidden" name="checkoutpage" value="quick"/>
			  	<input type="hidden" name="BACK_PAGE" value="quickcheckout"/>
				
				<div class="span4 no-left-margin">
					<div class="span12 label label-large label-info arrowed-in arrowed-right label-origin">
						<#if shipping == true>
							1)&nbsp;${uiLabelMap.DAWhereShallWeShipIt}?
		                <#else>
							1)&nbsp;${uiLabelMap.OrderInformationAboutYou}
		                </#if>
					</div>
					
					<div>
						<a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');"> ${uiLabelMap.DAAddNewAddress}</a>
						<div class="control-group">
							<label for="shipToCustomerPartyId" style="float:left; margin-top:5px">${uiLabelMap.OrderShipToParty}:</label>
							<select name="shipToCustomerPartyId" id="shipToCustomerPartyId" style="width:auto; float:left; margin-left:10px" onchange="javascript:submitForm(document.checkoutInfoForm, 'SC', null);">
	                          	<#list cartParties as cartParty>
		                          	<option value="${cartParty}">${cartParty}</option>
	                          	</#list>
	                      	</select>
						</div>
						
						<#if shippingContactMechList?has_content>
							<#if shoppingCart.getShippingContactMechId()?exists && shoppingCart.getShippingContactMechId()?has_content>
								<#assign shippingContactMechSelected = true>
							</#if>
							<#list shippingContactMechList as shippingContactMech>
								<#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
								<div class="control-group-radio">
									<label class="control-label-radio">
										<input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}" onclick="javascript:submitForm(document.checkoutInfoForm, 'SA', null);"
											<#if !noCheck0?exists && shippingContactMechSelected?exists>
												<#if shoppingCart.getShippingContactMechId()?default("") == shippingAddress.contactMechId> checked="checked"</#if>
											<#else>
												<#assign noCheck0 = "true">
												 checked="checked"
											</#if>/>
										<span class="lbl"></span>
										<div class="inner-radio" style="margin-left:10px;float:left">
			                             	<#if shippingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${shippingAddress.toName}<br /></#if>
			                             	<#if shippingAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b>&nbsp;${shippingAddress.attnName}<br /></#if>
			                             	<#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
			                             	<#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
			                             	<#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
			                             	<#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
			                             	<#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
			                             	<#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if>
			                           </div>
									</label>
								</div>
		                       <#if shippingContactMech_has_next>
		                         	<hr />
		                       </#if>
                     		</#list>
                   		</#if>
						<#-- Party Tax Info -->
		                <#-- commented out by default because the TaxAuthority drop-down is just too wide...
		                <hr />
		                <div>&nbsp;${uiLabelMap.PartyTaxIdentification}</div>
		                ${screens.render("component://order/widget/ordermgr/OrderEntryOrderScreens.xml#customertaxinfo")}
		                -->
					</div>
					<#--
					<div class="screenlet-title-bar">
		                <div class="row-fluid" style="width:95%;">
							<div class="span12 label label-large label-info arrowed-right"><b>
								
							</b></div>
						</div>
		            </div>
		            <div class="control-group" id="quickAddFormQuatity" style="margin-bottom:0 !important">
						<label class="control-label" for="quantity" style="text-align:left">${uiLabelMap.OrderQuantity}:</label>
						<div class="controls">
							<div class="span12">
								<input type="text" size="6" name="quantity" id="quantity" value=""/>
								<span class="help-inline tooltipob">
									<button class="btn btn-primary btn-mini open-sans" type="submit" style="margin-bottom:3px">
										<i class="icon-ok"></i>${uiLabelMap.DAAddToOrder}
									</button>
								</span>
							</div>
						</div>
					</div>
					-->
				</div><!--.span4 001-->
				<div class="span4">
					<div class="span12 label label-large label-info arrowed-in arrowed-right label-origin">
						<#if shipping == true>
		                    <div class="h3">2)&nbsp;${uiLabelMap.DAHowShallWeShipIt}</div>
		                <#else>
		                    <div class="h3">2)&nbsp;${uiLabelMap.OrderOptions}</div>
		                </#if>
					</div>
					
					<#if shipping == true>
						<#if chosenShippingMethod?exists>
							<#assign shippingMethodChecked = chosenShippingMethod>
						</#if>
						<#assign notCheck = false />
						<#list carrierShipmentMethodList as carrierShipmentMethod>
							<#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
							<div class="control-group-radio">
								<label class="control-label-radio">
									<input type="radio" name="shipping_method" value="${shippingMethod?if_exists}"  
										<#if shippingMethodChecked?exists && (shippingMethod == shippingMethodChecked)>
											checked="checked"
										<#elseif shippingMethod == "NO_SHIPPING@_NA_">
											checked="checked"
											<#assign notCheck = true>
										<#elseif !notCheck>
											<#if shippingMethod == "N@A">checked="checked"</#if>
										</#if>/>
									<span class="lbl"></span>
								
									<div class="inner-radio">
										<#if shoppingCart.getShippingContactMechId()?exists>
				                            <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierShipmentMethod)?default(-1)>
			                          	</#if>
			                          	<#if carrierShipmentMethod.partyId != "_NA_">${carrierShipmentMethod.partyId?if_exists}&nbsp;</#if>
			                          	${carrierShipmentMethod.description?if_exists}
			                          	<#if shippingEst?has_content> - 
			                          		<#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=shoppingCart.getCurrency()/>
		                          			<#else>${uiLabelMap.OrderCalculatedOffline}
		                          			</#if>
		                          		</#if>
									</div>
								</label>
							</div>
						</#list>
						
						<#if !carrierShipmentMethodList?exists || carrierShipmentMethodList?size == 0>
	                  		<div class="control-group-radio">
	                  			<label class="control-label-radio">
									<input type="radio" name="shipping_method" value="Default" checked="checked"/><span class="lbl"></span>
									<div class="inner-radio">
										${uiLabelMap.OrderUseDefault}.
									</div>
								</label>
							</div>
                 	 	</#if>
                 	 	<div class="control-group-radio">
                 	 		<label class="control-label-radio">
                 	 			<h4 class="green smaller lighter" style="margin-top:20px">${uiLabelMap.DAShipAllAtOnce}?</h4>
                 	 		</label>
                 	 		<div class="controls-radio"></div>
                 	 	</div>
                 		<div class="control-group-radio">
							<label class="control-label-radio">
								<input type="radio" <#if shoppingCart.getMaySplit()?default("N") == "N">checked="checked"</#if> name="may_split" value="false"/><span class="lbl"></span>
								<div class="inner-radio">
									${uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.
								</div>
							</label>
                  		</div>
                  		<div class="control-group-radio">
							<label class="control-label-radio">
								<input <#if shoppingCart.getMaySplit()?default("N") == "Y">checked="checked"</#if> type="radio" name="may_split" value="true"/><span class="lbl"></span>
								<div class="inner-radio">
									${uiLabelMap.DAPleaseShipItemsBecomeAvailable}.
								</div>
							</label>
                  		</div>
                  		<hr />
                 	<#else/>
	                    <input type="hidden" name="shipping_method" value="NO_SHIPPING@_NA_"/>
	                    <input type="hidden" name="may_split" value="false"/>
	                    <input type="hidden" name="is_gift" value="false"/>
                 	</#if>
                 	
                 	<div class="control-group-radio">
             	 		<label class="control-label-radio">
             	 			<h4 class="green smaller lighter" style="margin-top:20px; margin-top:0">${uiLabelMap.DASpecialInstructions}</h4>
             	 		</label>
             	 		<div class="controls-radio"></div>
             	 	</div>
                 	<textarea cols="30" rows="2" wrap="hard" name="shipping_instructions" style="width:96%">${shoppingCart.getShippingInstructions()?if_exists}</textarea>
                    
                   	<input type="hidden" name="is_gift" value="false"/>
                    <#--
                    <#if shipping == true>
                  		<#if productStore.showCheckoutGiftOptions?if_exists != "N" && giftEnable?if_exists != "N">
                 			<hr />
                 			<div>
		                        <span class="h2"><b>${uiLabelMap.OrderIsThisGift}</b></span>
		                        <input type="radio" <#if shoppingCart.getIsGift()?default("Y") == "Y">checked="checked"</#if> name="is_gift" value="true"><span>${uiLabelMap.CommonYes}</span>
		                        <input type="radio" <#if shoppingCart.getIsGift()?default("N") == "N">checked="checked"</#if> name="is_gift" value="false"><span>${uiLabelMap.CommonNo}</span>
	                      	</div>
                 			
                 			<h2>${uiLabelMap.OrderGiftMessage}</h2>
                 			<textarea cols="30" rows="3" wrap="hard" name="gift_message">${shoppingCart.getGiftMessage()?if_exists}</textarea>
                		<#else/>
              				<input type="hidden" name="is_gift" value="false"/>
              			</#if>
             		</#if>
                    -->
                 	
             		<hr />
             		<div class="control-group-radio">
             	 		<label>
             	 			<h4 class="green smaller lighter" style="margin-top:20px;margin-top:0">${uiLabelMap.PartyEmailAddresses}</h4>
             	 		</label>
             	 		<div class="controls-radio"></div>
             	 	</div>
             	 	
             		
          			<#if emailList?exists && emailList?has_content && emailList?size &gt; 0>
          				<div>${uiLabelMap.OrderEmailSentToFollowingAddresses}:</div>
	                  	<div>
	                  		<b>
	                      	<#list emailList as email>
	                       	 	${email.infoString?if_exists}<#if email_has_next>,</#if>
	                      	</#list>
	                      	</b>
              			</div>
                  	<#else>
                  		<div><b>${uiLabelMap.DANotYetHaveEmailContact}</b></div>
                  	</#if>
                  	
              		<#--
              		<div>${uiLabelMap.OrderUpdateEmailAddress} 
                  		<a class="btn btn-primary btn-mini" href="
                  				<#if customerDetailLink?exists>${customerDetailLink}${shoppingCart.getPartyId()}" target="partymgr"
                    			<#else><@ofbizUrl>viewprofile?DONE_PAGE=quickcheckout</@ofbizUrl>"
                    			</#if> class="btn btn-primary btn-mini">${uiLabelMap.PartyProfile}</a>.
    				</div>-->
                  	<br />
                  	<div>${uiLabelMap.OrderCommaSeperatedEmailAddresses}:</div>
                  	<input type="text" size="30" style="width:96%" name="order_additional_emails" value="${shoppingCart.getOrderAdditionalEmails()?if_exists}"/>
                    
				</div><!--.span4 002-->
				<div class="span4">
					<#-- Payment Method Selection -->
					<div class="span12 label label-large label-info arrowed-in arrowed-right label-origin">
						3)&nbsp;${uiLabelMap.OrderHowShallYouPay}
					</div>
					<#--
					<span>${uiLabelMap.CommonAdd}:</span>
                  	<#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
	                    <a href="javascript:submitForm(document.checkoutInfoForm, 'NC', '');" class="btn btn-primary btn-mini open-sans">${uiLabelMap.AccountingCreditCard}</a>
                  	</#if>
                  	<#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
	                    <a href="javascript:submitForm(document.checkoutInfoForm, 'NE', '');" class="btn btn-primary btn-mini open-sans">${uiLabelMap.AccountingEFTAccount}</a>
                  	</#if>
					
					<hr />
					<a href="javascript:submitForm(document.checkoutInfoForm, 'SP', '');" class="btn btn-primary btn-mini open-sans">${uiLabelMap.AccountingSplitPayment}</a>
					-->
					<div style="clear:clear-both"></div>
                    <hr />
                    <#if !checkOutPaymentId?exists || !checkOutPaymentId?has_content>
                    	<#assign checkOutPaymentId = "EXT_OFFLINE" />
                    </#if>
                    <#if productStorePaymentMethodTypeIdMap.EXT_OFFLINE?exists>
                    	<div class="control-group-radio">
	             	 		<label class="control-label-radio">
	             	 			<input type="radio" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == checkOutPaymentId>checked="checked"</#if>/><span class="lbl"></span>
	             	 			<div class="inner-radio"><span>${uiLabelMap.OrderMoneyOrder}</span></div>
	             	 		</label>
	             	 	</div>
                  	</#if>
                    
                    <#if productStorePaymentMethodTypeIdMap.EXT_COD?exists>
                    	<div class="control-group-radio">
	             	 		<label class="control-label-radio">
	             	 			<input type="radio" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == checkOutPaymentId>checked="checked"</#if>/><span class="lbl"></span>
								<div class="inner-radio"><span>${uiLabelMap.OrderCOD}</span></div>
							</label>
	             	 	</div>
                  	</#if>
					
					<#if productStorePaymentMethodTypeIdMap.EXT_WORLDPAY?exists>
                    	<div class="control-group-radio">
	             	 		<label class="control-label-radio">
	             	 			<input type="radio" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == checkOutPaymentId>checked="checked"</#if>/><span class="lbl"></span>
								<div class="inner-radio"><span>${uiLabelMap.AccountingPayWithWorldPay}</span></div>
							</label>
	             	 	</div>
                  	</#if>
                  	<#if productStorePaymentMethodTypeIdMap.EXT_PAYPAL?exists>
                    	<div class="control-group-radio">
	             	 		<label class="control-label-radio">
	             	 			<input type="radio" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == checkOutPaymentId>checked="checked"</#if>/><span class="lbl"></span>
								<div class="inner-radio"><span>${uiLabelMap.AccountingPayWithPayPal}</span></div>
							</label>
	             	 	</div>
                  	</#if>
                  	<hr />
                  	<#-- financial accounts -->
                  	<#list finAccounts as finAccount>
                  		<div class="control-group-radio">
	             	 		<label class="control-label-radio">
	             	 			<input type="radio" name="checkOutPaymentId" value="FIN_ACCOUNT|${finAccount.finAccountId}" <#if "FIN_ACCOUNT" == checkOutPaymentId>checked="checked"</#if>/><span class="lbl"></span>
								<div class="inner-radio"><span>${uiLabelMap.AccountingFinAccount} #${finAccount.finAccountId}</span></div>
							</label>
	             	 	</div>
                  	</#list>
                  	
                  	<#if !paymentMethodList?has_content>
	                    <#if (!finAccounts?has_content)>
                          	<div><b>${uiLabelMap.AccountingNoPaymentMethods}</b></div>
	                    </#if>
                  	<#else>
                  		<#list paymentMethodList as paymentMethod>
                    		<#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                     			<#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
                      				<#assign creditCard = paymentMethod.getRelatedOne("CreditCard", false)>
		                        	<label>
										<input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if shoppingCart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if>/>
										<span class="lbl"></span>
									</label>
                          			
                          			<span>CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</span>
                          			<a href="javascript:submitForm(document.checkoutInfoForm, 'EC', '${paymentMethod.paymentMethodId}');" class="btn btn-primary btn-mini open-sans icon-ok">${uiLabelMap.CommonUpdate}</a>
                          			<#if paymentMethod.description?has_content><br /><span>(${paymentMethod.description})</span></#if>
                          			&nbsp;${uiLabelMap.OrderCardSecurityCode}&nbsp;<input type="text" size="5" maxlength="10" name="securityCode_${paymentMethod.paymentMethodId}" value=""/>
                     			</#if>
                			<#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                     			<#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
                      				<#assign eftAccount = paymentMethod.getRelatedOne("EftAccount", false)>
		                        	<label>
										<input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if shoppingCart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if>/><span class="lbl"></span>
									</label>
                          			
                          			<span>${uiLabelMap.AccountingEFTAccount}:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</span>
                          			<a href="javascript:submitForm(document.checkoutInfoForm, 'EE', '${paymentMethod.paymentMethodId}');" class="btn btn-primary btn-mini open-sans icon-ok">${uiLabelMap.CommonUpdate}</a>
                          			<#if paymentMethod.description?has_content><br /><span>(${paymentMethod.description})</span></#if>
                 				</#if>
                    		<#elseif paymentMethod.paymentMethodTypeId == "GIFT_CARD">
                     			<#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
                      				<#assign giftCard = paymentMethod.getRelatedOne("GiftCard", false)>
                      				<#if giftCard?has_content && giftCard.cardNumber?has_content>
				                        <#assign giftCardNumber = "">
				                        <#assign pcardNumber = giftCard.cardNumber>
                        				<#if pcardNumber?has_content>
                          					<#assign psize = pcardNumber?length - 4>
                          					<#if 0 < psize>
					                            <#list 0 .. psize-1 as foo>
					                              	<#assign giftCardNumber = giftCardNumber + "*">
					                            </#list>
                            					<#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3]>
                          					<#else>
                            					<#assign giftCardNumber = pcardNumber>
                          					</#if>
                        				</#if>
                      				</#if>
									<label>
										<input type="radio" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if shoppingCart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if>/><span class="lbl"></span>
									</label>
									
									<span>${uiLabelMap.AccountingGift}:&nbsp;${giftCardNumber}</span>
		                          	<a href="javascript:submitForm(document.checkoutInfoForm, 'EG', '${paymentMethod.paymentMethodId}');" class="btn btn-primary btn-mini open-sans icon-ok">[${uiLabelMap.CommonUpdate}]</a>
		                          	<#if paymentMethod.description?has_content><br /><span>(${paymentMethod.description})</span></#if>
                     			</#if>
                    		</#if>
                  		</#list>
					</#if>
					
					<#-- special billing account functionality to allow use w/ a payment method -->
	                <#if productStorePaymentMethodTypeIdMap.EXT_BILLACT?exists>
	                  	<#if billingAccountList?has_content>
	                    	<hr />
	                        <select name="billingAccountId">
	                          <option value=""></option>
	                            <#list billingAccountList as billingAccount>
	                              <#assign availableAmount = billingAccount.accountBalance?double>
	                              <#assign accountLimit = billingAccount.accountLimit?double>
	                              <option value="${billingAccount.billingAccountId}" <#if billingAccount.billingAccountId == selectedBillingAccountId?default("")>selected="selected"</#if>>${billingAccount.description?default("")} [${billingAccount.billingAccountId}] Available: <@ofbizCurrency amount=availableAmount isoCode=billingAccount.accountCurrencyUomId/> Limit: <@ofbizCurrency amount=accountLimit isoCode=billingAccount.accountCurrencyUomId/></option>
	                            </#list>
	                        </select>
	                        <span>${uiLabelMap.FormFieldTitle_billingAccountId}</span>
	                        <input type="text" size="5" name="billingAccountAmount" value=""/>
	                        ${uiLabelMap.OrderBillUpTo}
	                  	</#if>
                	</#if>
	                <#-- end of special billing account functionality -->
					
					<#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
                  		<hr />
                        <label>
							<input type="checkbox" name="addGiftCard" value="Y"/><span class="lbl"></span>
						</label>
                      	<span>${uiLabelMap.AccountingUseGiftCardNotOnFile}</span>
                      	<div>${uiLabelMap.AccountingNumber}</div>
                      	<input type="text" size="15" name="giftCardNumber" value="${(requestParameters.giftCardNumber)?if_exists}" onFocus="document.checkoutInfoForm.addGiftCard.checked=true;"/>
                  		<#if shoppingCart.isPinRequiredForGC(delegator)>
                      		<div>${uiLabelMap.AccountingPIN}</div>
                      		<input type="text" size="10" name="giftCardPin" value="${(requestParameters.giftCardPin)?if_exists}" onFocus="document.checkoutInfoForm.addGiftCard.checked=true;"/>
                  		</#if>
                      	<div>${uiLabelMap.AccountingAmount}</div>
                      	<input type="text" size="6" name="giftCardAmount" value="${(requestParameters.giftCardAmount)?if_exists}" onFocus="document.checkoutInfoForm.addGiftCard.checked=true;"/>
            		</#if>
					
					<#-- End Payment Method Selection -->
				</div><!--.span4 003-->
			</form>
		</div>
	</div><!--.widget-main-->
	</div>
	</div>
	</div>
</div>
<table width="100%">
  	<tr valign="top">
	    <td>
	      &nbsp;<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="btn btn-primary btn-small"><i class="open-sans icon-arrow-left"></i>${uiLabelMap.OrderBacktoShoppingCart}</a>
	    </td>
	    <td align="right">
	      <a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" class="btn btn-primary btn-small">${uiLabelMap.DAContinueToFinalOrderReview}&nbsp;<i class="open-sans icon-arrow-right icon-on-right"></i></a>
	    </td>
  	</tr>
</table>

