<#include "/obb/webapp/obb/includes/common/messageError.ftl"/>
<div class="my-account">
	<#if person?exists>
		<div class="dashboard account-create">
		    <div class="page-title no-margin-bottom">
		        <h1 class='account-title'>${uiLabelMap.ObbMyDash}</h1>
		    </div>
	    <div class="account-form">
			<div class="welcome-msg">
				<p class="hello"><strong>${uiLabelMap.BEHello}, ${party.partyFullName?if_exists}! </strong></p>
				<p>${uiLabelMap.ObbIntroDash}</p>
				</div>
		        <div class="box-account box-info">
			        <div class="box-head">
			            <h2>${uiLabelMap.ObbAccountInfo}</h2>
			            <a href="<@ofbizUrl>changepassword</@ofbizUrl>">${uiLabelMap.ObbChangePassword}</a>
			        </div>
		            <div class="row">
					    <div class="col-lg-7 col-md-7">
					        <div class="box">
					            <div class="box-title">
					                <h3>${uiLabelMap.ObbContactInformation}</h3>
					                <a href="<@ofbizUrl>editperson</@ofbizUrl>">${uiLabelMap.CommonEdit}</a>
					            </div>
					            <div class="box-content">
					            
					            <#if partyContactMechValueMaps?has_content>
									<#list partyContactMechValueMaps as partyContactMechValueMap>
											<#assign contactMech = partyContactMechValueMap.contactMech?if_exists />
											<#assign contactMechType = partyContactMechValueMap.contactMechType?if_exists />
											<#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists />
										<#if contactMech.contactMechTypeId?if_exists = "EMAIL_ADDRESS">
											<#assign infoString = contactMech.infoString?if_exists />
										</#if>
										<#if contactMech.contactMechTypeId?if_exists = "TELECOM_NUMBER">
											<#assign contactNumber = telecomNumber.contactNumber?if_exists />
										</#if>
										<#assign postalAddress = partyContactMechValueMap.postalAddress?if_exists />
										<#if contactMechType?has_content && postalAddress?has_content && contactMechType.contactMechTypeId == "POSTAL_ADDRESS">
											<#assign partyContactMechPurposes = partyContactMechValueMap.partyContactMechPurposes/>
											<#if partyContactMechPurposes?has_content && (partyContactMechPurposes.size() > 0)>
												<#list partyContactMechValueMap.partyContactMechPurposes?if_exists as partyContactMechPurpose>
													<#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true) />
													<#if contactMechPurposeType?exists>
														<#if contactMechPurposeType.contactMechPurposeTypeId == "SHIPPING_LOCATION">
															<#assign shippingAddress = postalAddress/>
															<#if shippingAddress.districtGeoId?exists>
															<#assign shippingDistrict = delegator.findOne("Geo", {"geoId" : shippingAddress.districtGeoId}, false) />
															<#if shippingDistrict?has_content>
																<#assign shippingDistrict = shippingDistrict.geoName/>
															</#if>
															</#if>
															<#if shippingAddress.stateProvinceGeoId?exists>
															<#assign shippingProvince = delegator.findOne("Geo", {"geoId" : shippingAddress.stateProvinceGeoId}, false) />
															<#if shippingProvince?has_content>
															<#assign shippingProvince = shippingProvince.geoName/>
															</#if>
															</#if>
															<#if shippingAddress.countryGeoId?exists>
															<#assign shippingCountry = delegator.findOne("Geo", {"geoId" : shippingAddress.countryGeoId}, false) />
															<#if shippingCountry?has_content>
																<#assign shippingCountry = shippingCountry.geoName/>
															</#if>
															</#if>
														<#elseif contactMechPurposeType.contactMechPurposeTypeId == "BILLING_LOCATION">
															<#assign billingAddress = postalAddress/>
															<#if billingAddress.districtGeoId?exists>
															<#assign billingDistrict = delegator.findOne("Geo", {"geoId" : billingAddress.districtGeoId}, false) />
															<#if billingDistrict?has_content>
																<#assign billingDistrict = billingDistrict.geoName/>
															</#if>
															</#if>
															<#if billingAddress.stateProvinceGeoId?exists>
															<#assign billingProvince = delegator.findOne("Geo", {"geoId" : billingAddress.stateProvinceGeoId}, false) />
															<#if billingProvince?has_content>
																<#assign billingProvince = billingProvince.geoName/>
															</#if>
															</#if>
															<#if billingAddress.countryGeoId?exists>
															<#assign billingCountry = delegator.findOne("Geo", {"geoId" : billingAddress.countryGeoId}, false) />
															<#if billingCountry?has_content>
																<#assign billingCountry = billingCountry.geoName/>
															</#if>
															</#if>
														</#if>
													</#if>
												</#list>
											</#if>
										</#if>
									</#list>
								</#if>
					            
						            <table class="table table-bordered customer-info table-responsive">
						                <tbody>
						                    <tr>
						                        <td>${uiLabelMap.BEPartyFullName}</td>
						                        <td>${party.partyFullName?if_exists}</td>
						                    </tr>
						                    <tr>
						                        <td>${uiLabelMap.BEEmail}</td>
						                        <td>${infoString?if_exists}</td>
						                    </tr>
						                    <tr>
							                    <td>${uiLabelMap.BEPhone}</td>
							                    <td>${contactNumber?if_exists}</td>
						                    </tr>
						                </tbody>
						            </table>
					            </div>
					        </div>
					    </div>
					<div class="col-lg-5 col-md-5">
					        <div class="box">
					            <div class="box-title">
					                <h3>${uiLabelMap.ObbLoyaltyPoints}</h3>
					            </div>
					            <div class="box-content">
										<#if monthsToInclude?exists && totalSubRemainingAmount?exists && totalOrders?exists>
											<p>${uiLabelMap.BEYouHaveBuy} ${totalSubRemainingAmount} ${uiLabelMap.BEFrom} ${totalOrders} ${uiLabelMap.ObbOrderInLast} ${monthsToInclude} ${uiLabelMap.ObbMonths}</p>
					                </#if>
					                <#if loyaltyPoint?exists>
					                	<p><b>${uiLabelMap.BECustomerPoint}: </b>${loyaltyPoint}</p>
					                </#if>
					            </div>
					        </div>
			</div>
				</div>
				<div class="col2-set">
					    <div class="box">
					        <div class="box-title">
					            <h3>${uiLabelMap.ObbAddressBook}</h3>
					        </div>
					        <div class="box-content">
					            <div class="col-1">
					                <h4>${uiLabelMap.ObbDefaultBillingAddress}</h4>
					                <address>
						                <#if billingAddress?has_content>
							                    <#if billingAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${billingAddress.attnName}<br /></#if>
							                    ${billingAddress.address1?if_exists}<br />
							                    <#if billingDistrict?exists>${billingDistrict}</#if>
							                    <#if billingProvince?exists> - ${billingProvince}</#if>
							                    <#if billingCountry?exists> - ${billingCountry}</#if>
							                    <#if (!billingAddress.countryGeoId?has_content || billingAddress.countryGeoId?if_exists = "USA")>
							                      <#assign addr1 = billingAddress.address1?if_exists />
							                      <#if (addr1.indexOf(" ") > 0)>
							                        <#assign addressNum = addr1.substring(0, addr1.indexOf(" ")) />
							                        <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1) />
							                      </#if>
						                    </#if><br/>
						                <#else>
						                    ${uiLabelMap.ObbNotBillingAddress}<br>
						                </#if>
					                </address>
					            </div>
						<div class="col-2">
					                <h4>${uiLabelMap.ObbDefaultShippingAddress}</h4>
					                <address>
					                    <#if shippingAddress?has_content>
							                    <#if shippingAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${shippingAddress.attnName}<br /></#if>
							                    ${shippingAddress.address1?if_exists}<br />
							                    <#if shippingDistrict?exists>${shippingDistrict}</#if>
							                    <#if shippingProvince?exists> - ${shippingProvince}</#if>
							                    <#if shippingCountry?exists> - ${shippingCountry}</#if>
							                    <#if (!shippingAddress.countryGeoId?has_content || shippingAddress.countryGeoId?if_exists = "USA")>
							                      <#assign addr1 = shippingAddress.address1?if_exists />
							                      <#if (addr1.indexOf(" ") > 0)>
							                        <#assign addressNum = addr1.substring(0, addr1.indexOf(" ")) />
							                        <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1) />
							                      </#if>
						                    </#if><br/>
						                <#else>
						                    ${uiLabelMap.ObbNotShippingAddress}<br>
						                </#if>
					                </address>
					            </div>
					        </div>
					    </div>
					</div>
			</div>
		</div>
		</#if>
	</div>
</div>
