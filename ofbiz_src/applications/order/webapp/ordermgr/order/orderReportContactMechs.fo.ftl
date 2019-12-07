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
<#escape x as x?xml>

<#if orderHeader.getString("orderTypeId") == "PURCHASE_ORDER">
    <#if supplierGeneralContactMechValueMap?exists>
        <#assign contactMech = supplierGeneralContactMechValueMap.contactMech>
        <fo:block font-weight="bold" font-family="Arial">${uiLabelMap.OrderPurchasedFrom}:</fo:block>
        <#assign postalAddress = supplierGeneralContactMechValueMap.postalAddress>
        <#if postalAddress?has_content>
            <fo:block text-indent="0.2in" font-family="Arial">
                <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName}</fo:block></#if>
                <#if postalAddress.attnName?has_content><fo:block>${postalAddress.attnName?if_exists}</fo:block></#if>
                <fo:block>${postalAddress.address1?if_exists}</fo:block>
                <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                <fo:block>
                    <#assign wardGeo = (delegator.findOne("Geo", {"geoId", postalAddress.wardGeoId?if_exists}, false))?if_exists />
                    <#if wardGeo?has_content>${wardGeo.geoName?if_exists}, </#if>
	                <#assign districtGeo = (delegator.findOne("Geo", {"geoId", postalAddress.districtGeoId?if_exists}, false))?if_exists />
	                <#if districtGeo?has_content>${districtGeo.geoName?if_exists}, </#if>
	                <#assign stateGeo = (delegator.findOne("Geo", {"geoId", postalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
	                <#if stateGeo?has_content>${stateGeo.geoName?if_exists}, </#if>
                    <#assign countryGeo = (delegator.findOne("Geo", {"geoId", postalAddress.countryGeoId?if_exists}, false))?if_exists />
                    <#if countryGeo?has_content>${countryGeo.geoName?if_exists}.</#if>
                </fo:block>
            </fo:block>                
        </#if>
    <#else>
        <#-- here we just display the name of the vendor, since there is no address -->
        <#assign vendorParty = orderReadHelper.getBillFromParty()>
        <fo:block font-family="Arial">
            <fo:inline font-weight="bold">${uiLabelMap.OrderPurchasedFrom}:</fo:inline> ${Static['org.ofbiz.party.party.PartyHelper'].getPartyName(vendorParty)}
        </fo:block>
    </#if>
</#if>

<#-- list all postal addresses of the order.  there should be just a billing and a shipping here. -->
<fo:table border="1px solid #ccc">
	<fo:table-column column-width="45%"/>
	<fo:table-column column-width="55%"/>
	<fo:table-body>
		<#list orderContactMechValueMaps as orderContactMechValueMap>
		    <#assign contactMech = orderContactMechValueMap.contactMech>
		    <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
		    <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
		        <#assign postalAddress = orderContactMechValueMap.postalAddress>
		        <fo:table-row border-bottom="1px solid #ccc">
		        <fo:table-cell><fo:block font-weight="bold" margin="6px" font-family="Arial">${contactMechPurpose.get("description",locale)}:</fo:block></fo:table-cell>
		        <fo:table-cell><fo:block text-indent="0.2in" margin-top="6px" font-family="Arial">
		            <#if postalAddress?has_content>
		                <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName?if_exists}</fo:block></#if>
		                <#if postalAddress.attnName?has_content><fo:block>${postalAddress.attnName?if_exists}</fo:block></#if>
		                <fo:block>${postalAddress.address1?if_exists}</fo:block>
		                <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
		                <fo:block>
			                <#assign wardGeo = (delegator.findOne("Geo", {"geoId", postalAddress.wardGeoId?if_exists}, false))?if_exists />
			                <#if wardGeo?has_content>${wardGeo.geoName?if_exists}, </#if>
			                <#assign districtGeo = (delegator.findOne("Geo", {"geoId", postalAddress.districtGeoId?if_exists}, false))?if_exists />
			                <#if districtGeo?has_content>${districtGeo.geoName?if_exists}, </#if>
		                    <#assign stateGeo = (delegator.findOne("Geo", {"geoId", postalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
		                    <#if stateGeo?has_content>${stateGeo.geoName?if_exists}, </#if>
		                    <#assign countryGeo = (delegator.findOne("Geo", {"geoId", postalAddress.countryGeoId?if_exists}, false))?if_exists />
		                    <#if countryGeo?has_content>${countryGeo.geoName?if_exists}.</#if>
		                </fo:block>
		            </#if>
		        </fo:block></fo:table-cell>
		        </fo:table-row>
		    </#if>
		</#list>
		
		<#if orderPaymentPreferences?has_content>
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin="6px" font-weight="bold" font-family="Arial">${uiLabelMap.AccountingPaymentInformation}:</fo:block></fo:table-cell>
			<fo:table-cell>
				<#list orderPaymentPreferences as orderPaymentPreference>
			        <fo:block margin-top="6px" text-indent="0.2in" font-family="Arial">
			            <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType", false)?if_exists>
			            <#if (orderPaymentPreference?? && (orderPaymentPreference.getString("paymentMethodTypeId") == "CREDIT_CARD") && (orderPaymentPreference.getString("paymentMethodId")?has_content))>
			                <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod", false).getRelatedOne("CreditCard", false)>
			                ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
			            <#else>
			                ${paymentMethodType.get("description",locale)?if_exists}
			            </#if>
			        </fo:block>
			    </#list>
			</fo:table-cell>
		</fo:table-row>
		</#if>
		<#if orderHeader.getString("orderTypeId") == "SALES_ORDER" && shipGroups?has_content>
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin="6px" font-weight="bold" font-family="Arial">${uiLabelMap.OrderShipmentInformation}:</fo:block></fo:table-cell>
			<fo:table-cell>
				<#list shipGroups as shipGroup>
			        <fo:block margin-top="6px" text-indent="0.2in" font-family="Arial">
			            <#if shipGroups.size() gt 1>${shipGroup.shipGroupSeqId} - </#if>
			            <#if (shipGroup.shipmentMethodTypeId)?exists>
			                ${(shipGroup.getRelatedOne("ShipmentMethodType", false).get("description", locale))?default(shipGroup.shipmentMethodTypeId)}
			            </#if>
			            <#if (shipGroup.shipAfterDate)?exists || (shipGroup.shipByDate)?exists>
			                <#if (shipGroup.shipAfterDate)?exists> - ${uiLabelMap.OrderShipAfterDate}: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipGroup.shipAfterDate)}</#if><#if (shipGroup.shipByDate)?exists> - ${uiLabelMap.OrderShipBeforeDate}: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipGroup.shipByDate)}</#if>
			            </#if>
			        </fo:block>
			    </#list>
			</fo:table-cell>
		</fo:table-row>
		</#if>
		<#if orderTerms?has_content && orderTerms.size() gt 0>
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin="6px" font-weight="bold" font-family="Arial">${uiLabelMap.OrderOrderTerms}:</fo:block></fo:table-cell>
			<fo:table-cell>
				<#list orderTerms as orderTerm>
			        <fo:block margin-top="6px" text-indent="0.2in" font-family="Arial">
			            ${orderTerm.getRelatedOne("TermType", false).get("description",locale)} ${orderTerm.termValue?default("")} ${orderTerm.termDays?default("")} ${orderTerm.textValue?default("")}
			        </fo:block>
			    </#list>
			</fo:table-cell>
		</fo:table-row>
		</#if>
	</fo:table-body>
	</fo:table>
	<fo:block space-after="0.2in"/>
</#escape>
