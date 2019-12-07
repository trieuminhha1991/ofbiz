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

<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<#if (orderHeader.externalId)?exists && (orderHeader.externalId)?has_content >
  <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
</#if>

<div id="orderHeader" class="checkout-product">
<#-- left side -->
<div class="columnLeft">
<div class="screenlet">
	<hr />
	<h2>
    <#if maySelectItems?default("N") == "Y" && returnLink?default("N") == "Y" && (orderHeader.statusId)?if_exists == "ORDER_COMPLETED" && roleTypeId?if_exists == "PLACING_CUSTOMER">
      &nbsp;<a href="<@ofbizUrl fullPath="true">makeReturn?orderId=${orderHeader.orderId}</@ofbizUrl>" class="button">${uiLabelMap.OrderRequestReturn}</a>
    </#if>
    ${uiLabelMap.OrderOrder}
	</h2>
	<table>
		<tr>
			<td>
			    <#if orderHeader?has_content>
			      <div class="divinline">${uiLabelMap.CommonNbr} </div><a href="<@ofbizUrl fullPath="true">orderstatus?orderId=${orderHeader.orderId}</@ofbizUrl>" class="button">${orderHeader.orderId}</a>
			    </#if>
		    </td>
	    </tr>
    </table>
   <hr />
   <h2>
    ${uiLabelMap.CommonInformation}
    <#if (orderHeader.orderId)?exists>
      ${externalOrder?if_exists} [ <a class="linkcolor" href="<@ofbizUrl fullPath="true">order.pdf?orderId=${(orderHeader.orderId)?if_exists}</@ofbizUrl>" class="lightbuttontext">PDF</a> ]
    </#if>
  </h2>
  <#-- placing customer information -->
  <table>
	<tr>
	    <#if localOrderReadHelper?exists && orderHeader?has_content>
	      <#assign displayParty = localOrderReadHelper.getPlacingParty()?if_exists/>
	      <#if displayParty?has_content>
	        <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
	      </#if>
	      <td>
	        <b>${uiLabelMap.PartyName}:</b>
	        ${(displayPartyNameResult.fullName)?default("[Name Not Found]")}
	      </td>
	    </#if>
	    <#-- order status information -->
	    <td>
	     <b>${uiLabelMap.CommonStatus}:</b>
	      <#if orderHeader?has_content>
	        ${localOrderReadHelper.getStatusString(locale)}
	      <#else>
	        ${uiLabelMap.OrderNotYetOrdered}
	      </#if>
	    </td>
	    <#-- ordered date -->
	    <#if orderHeader?has_content>
	      <td>
	        <b>${uiLabelMap.CommonDate}:</b>
	        ${orderHeader.orderDate.toString()}
	      </td>
	    </#if>
	    <#if distributorId?exists>
	      <td>
	        <b>${uiLabelMap.OrderDistributor}:</b>
	        ${distributorId}
	      </td>
	    </#if>
    </tr>
  </table>
</div>

<hr />
<div class="screenlet">
  <#if paymentMethods?has_content || paymentMethodType?has_content || billingAccount?has_content>
    <#-- order payment info -->
    <h2>${uiLabelMap.AccountingPaymentInformation}</h2>
    <#-- offline payment address infomation :: change this to use Company's address -->
    <table>
	<tr>
	      <#if !paymentMethod?has_content && paymentMethodType?has_content>
	        <td>
	          <#if paymentMethodType.paymentMethodTypeId == "EXT_OFFLINE">
	            ${uiLabelMap.AccountingOfflinePayment}
	            <#if orderHeader?has_content && paymentAddress?has_content>
	              ${uiLabelMap.OrderSendPaymentTo}:
	              <#if paymentAddress.toName?has_content>${paymentAddress.toName}</#if>
	              <#if paymentAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${paymentAddress.attnName}</#if>
	              ${paymentAddress.address1}
	              <#if paymentAddress.address2?has_content>${paymentAddress.address2}</#if>
	              <#assign paymentStateGeo = (delegator.findOne("Geo", {"geoId", paymentAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
	              ${paymentAddress.city}<#if paymentStateGeo?has_content>, ${paymentStateGeo.geoName?if_exists}</#if> ${paymentAddress.postalCode?if_exists}
	              <#assign paymentCountryGeo = (delegator.findOne("Geo", {"geoId", paymentAddress.countryGeoId?if_exists}, false))?if_exists />
	              <#if paymentCountryGeo?has_content>${paymentCountryGeo.geoName?if_exists}</#if>
	              ${uiLabelMap.EcommerceBeSureToIncludeYourOrderNb}
	            </#if>
	          <#else>
	            <#assign outputted = true>
	            ${uiLabelMap.AccountingPaymentVia} ${paymentMethodType.get("description",locale)}
	          </#if>
	        </td>
	      </#if>
	      <#if paymentMethods?has_content>
	        <#list paymentMethods as paymentMethod>
	          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
	            <#assign creditCard = paymentMethod.getRelatedOne("CreditCard", false)>
	            <#assign formattedCardNumber = Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)>
	          <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId>
	            <#assign giftCard = paymentMethod.getRelatedOne("GiftCard", false)>
	          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId>
	            <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount", false)>
	          </#if>
	          <#-- credit card info -->
	          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId && creditCard?has_content>
	            <#if outputted?default(false)>
	            </#if>
	            <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress", false)?if_exists>
	            <td>
	              <table>
			<tr>
		                <td> ${uiLabelMap.AccountingCreditCard}
		                  <#if creditCard.companyNameOnCard?has_content>${creditCard.companyNameOnCard}</#if>
		                  <#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}</#if>
		                  ${creditCard.firstNameOnCard}
		                  <#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}</#if>
		                  ${creditCard.lastNameOnCard}
		                  <#if creditCard.suffixOnCard?has_content>${creditCard.suffixOnCard}</#if>
		                </td>
		                <td>${formattedCardNumber}</td>
	                </tr>
	              </table>
	            </td>
	            <#-- Gift Card info -->
	          <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId && giftCard?has_content>
	            <#if outputted?default(false)>
	            </#if>
	            <#if giftCard?has_content && giftCard.cardNumber?has_content>
	              <#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress", false)?if_exists>
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
	            <td>
	              ${uiLabelMap.AccountingGiftCard}
	              ${giftCardNumber}
	            </td>
	            <#-- EFT account info -->
	          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId && eftAccount?has_content>
	            <#if outputted?default(false)>
	            </#if>
	            <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress", false)?if_exists>
	            <td>
	              <table>
			<tr>
		                <td>
		                  ${uiLabelMap.AccountingEFTAccount}
		                  ${eftAccount.nameOnAccount?if_exists}
		                </td>
		                <td>
		                  <#if eftAccount.companyNameOnAccount?has_content>${eftAccount.companyNameOnAccount}</#if>
		                </td>
		                <td>
		                  ${uiLabelMap.AccountingBank}: ${eftAccount.bankName}, ${eftAccount.routingNumber}
		                </td>
		                <td>
		                  ${uiLabelMap.AccountingAccount} #: ${eftAccount.accountNumber}
		                </td>
	                </tr>
	              </table>
	            </td>
	          </#if>
	          <#if pmBillingAddress?has_content>
	            <td>
	              <table>
			<tr>
		                <td>
		                  <#if pmBillingAddress.toName?has_content>${uiLabelMap.CommonTo}: ${pmBillingAddress.toName}</#if>
		                </td>
		                <td>
		                  <#if pmBillingAddress.attnName?has_content>${uiLabelMap.CommonAttn}: ${pmBillingAddress.attnName}</#if>
		                </td>
		                <td>
		                  ${pmBillingAddress.address1}
		                </td>
		                <td>
		                  <#if pmBillingAddress.address2?has_content>${pmBillingAddress.address2}</#if>
		                </td>
		                <td>
		                <#assign pmBillingStateGeo = (delegator.findOne("Geo", {"geoId", pmBillingAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
		                ${pmBillingAddress.city}<#if pmBillingStateGeo?has_content>, ${ pmBillingStateGeo.geoName?if_exists}</#if> ${pmBillingAddress.postalCode?if_exists}
		                <#assign pmBillingCountryGeo = (delegator.findOne("Geo", {"geoId", pmBillingAddress.countryGeoId?if_exists}, false))?if_exists />
		                <#if pmBillingCountryGeo?has_content>${pmBillingCountryGeo.geoName?if_exists}</#if>
		                </td>
	                </tr>
	              </table>
	            </td>
	          </#if>
	          <#assign outputted = true>
	        </#list>
	      </#if>
	      <#-- billing account info -->
	      <#if billingAccount?has_content>
	        <#if outputted?default(false)>
	        </#if>
	        <#assign outputted = true>
	        <td>
	          ${uiLabelMap.AccountingBillingAccount}
	          #${billingAccount.billingAccountId?if_exists} - ${billingAccount.description?if_exists}
	        </td>
	      </#if>
	      <#if (customerPoNumberSet?has_content)>
	        <td>
	          ${uiLabelMap.OrderPurchaseOrderNumber}
	          <#list customerPoNumberSet as customerPoNumber>
	            ${customerPoNumber?if_exists}
	          </#list>
	        </td>
	      </#if>
      <tr>
    </table>
  </#if>
</div>
</div>
<#-- right side -->
<hr />
<div class="screenlet columnRight">
  <#if orderItemShipGroups?has_content>
    <h2>${uiLabelMap.OrderShippingInformation}</h2>
    <#-- shipping address -->
    <#assign groupIdx = 0>
    <#list orderItemShipGroups as shipGroup>
      <#if orderHeader?has_content>
        <#assign shippingAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
        <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
      <#else>
        <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
        <#assign groupNumber = groupIdx + 1>
      </#if>
      <table>
	<tr>
		<#if shippingAddress?has_content>
	          <td>
	            <table>
			<thead>
				<tr>
					<td>${uiLabelMap.OrderDestination} </td>
					<td>${uiLabelMap.CommonTo}</td>
					<td>${uiLabelMap.PartyAddressLine1}</td>
					<td>${uiLabelMap.PartyAddressLine2}</td>
					<td>${uiLabelMap.CommonCity}, ${uiLabelMap.CommonState}, ${uiLabelMap.PartyZipCode}</td>
					<td>${uiLabelMap.CommonCountry}</td>
				</tr>
			</thead>
			<tbody>
				<tr>
			              <td>
			                [${groupNumber}] <#if shippingAddress.toName?has_content>- ${shippingAddress.toName}</#if>
			              </td>
			              <td>
			                <#if shippingAddress.attnName?has_content> ${shippingAddress.attnName}</#if>
			              </td>
			              <td>
			                ${shippingAddress.address1}
			              </td>
			              <td>
			                <#if shippingAddress.address2?has_content>${shippingAddress.address2}</#if>
			              </td>
			              <td>
			                <#assign shippingStateGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
			                ${shippingAddress.city}<#if shippingStateGeo?has_content>, ${shippingStateGeo.geoName?if_exists}</#if>, ${shippingAddress.postalCode?if_exists}
			              </td>
			              <td>
			                <#assign shippingCountryGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.countryGeoId?if_exists}, false))?if_exists />
			                <#if shippingCountryGeo?has_content>${shippingCountryGeo.geoName?if_exists}</#if>
			              </td>
		              </tr>
	              </tbody>
	            </table>
	          </td>
	        </#if>
	</tr>
	<tr>
	        <td>
	          <table>
			<tr>
		            <td>
		              <b>${uiLabelMap.OrderMethod}:</b>
		              <#if orderHeader?has_content>
		                <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType", false)?if_exists>
		                <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
		              <#else>
		                <#assign shipmentMethodType = cart.getShipmentMethodType(groupIdx)?if_exists>
		                <#assign carrierPartyId = cart.getCarrierPartyId(groupIdx)?if_exists>
		              </#if>
		              <#if carrierPartyId?exists && carrierPartyId != "_NA_">${carrierPartyId?if_exists}</#if>
		              ${(shipmentMethodType.description)?default("N/A")}
		            </td>
		            <td>
		              <#if shippingAccount?exists>${uiLabelMap.AccountingUseAccount}: ${shippingAccount}</#if>
		            </td>
	            </tr>
	          </table>
	        </td>
        </tr>
        <tr>
	          <#-- splitting preference -->
	          <#if orderHeader?has_content>
	            <#assign maySplit = shipGroup.maySplit?default("N")>
	          <#else>
	            <#assign maySplit = cart.getMaySplit(groupIdx)?default("N")>
	          </#if>
	          <td>
	            <b>${uiLabelMap.OrderSplittingPreference}:</b>
	            <#if maySplit?default("N") == "N">${uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.</#if>
	            <#if maySplit?default("N") == "Y">${uiLabelMap.OrderPleaseShipItemsBecomeAvailable}.</#if>
	          </td>
          </tr>
          <tr>
	          <#-- shipping instructions -->
	          <#if orderHeader?has_content>
	            <#assign shippingInstructions = shipGroup.shippingInstructions?if_exists>
	          <#else>
	            <#assign shippingInstructions =  cart.getShippingInstructions(groupIdx)?if_exists>
	          </#if>
	          <#if shippingInstructions?has_content>
	            <td>
	              ${uiLabelMap.OrderInstructions}
	              ${shippingInstructions}
	            </td>
	          </#if>
          </tr>
          <tr>
	          <#-- gift settings -->
	          <#if orderHeader?has_content>
	            <#assign isGift = shipGroup.isGift?default("N")>
	            <#assign giftMessage = shipGroup.giftMessage?if_exists>
	          <#else>
	            <#assign isGift = cart.getIsGift(groupIdx)?default("N")>
	            <#assign giftMessage = cart.getGiftMessage(groupIdx)?if_exists>
	          </#if>
	          <#if productStore.showCheckoutGiftOptions?if_exists != "N">
	          <td>
	            <b>${uiLabelMap.OrderGift}?</b>
	            <#if isGift?default("N") == "N">${uiLabelMap.OrderThisIsNotGift}.</#if>
	            <#if isGift?default("N") == "Y">${uiLabelMap.OrderThisIsGift}.</#if>
	          </td>
	          <#if giftMessage?has_content>
	            <td>
	              ${uiLabelMap.OrderGiftMessage}
	              ${giftMessage}
	            </td>
	          </#if>
	        </#if>
	        <#if shipGroup_has_next>
	        </#if>
        </tr>
        <tr>
		 <#-- tracking number -->
	        <#if trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
	          <td>
	            ${uiLabelMap.OrderTrackingNumber}
	            <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
	            <#if shipGroup.trackingNumber?has_content>
	              ${shipGroup.trackingNumber}
	            </#if>
	            <#if orderShipmentInfoSummaryList?has_content>
	              <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
	                <#if (orderShipmentInfoSummaryList?size > 1)>${orderShipmentInfoSummary.shipmentPackageSeqId}: </#if>
	                Code: ${orderShipmentInfoSummary.trackingCode?default("[Not Yet Known]")}
	                <#if orderShipmentInfoSummary.boxNumber?has_content>${uiLabelMap.OrderBoxNumber}${orderShipmentInfoSummary.boxNumber}</#if>
	                <#if orderShipmentInfoSummary.carrierPartyId?has_content>(${uiLabelMap.ProductCarrier}: ${orderShipmentInfoSummary.carrierPartyId})</#if>
	              </#list>
	            </#if>
	          </td>
	          </#if>
        </tr>
      </table>
      <#assign groupIdx = groupIdx + 1>
    </#list><#-- end list of orderItemShipGroups -->
  </#if>
</div>

<div class="clearBoth"></div>
</div>
