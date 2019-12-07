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

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
	<#if parameters.hideFields?has_content>
		<form name='lookupandhidefields${requestParameters.hideFields?default("Y")}' method="post" action="<@ofbizUrl>searchOrders</@ofbizUrl>">
		 	<!-- <#if parameters.hideFields?default("N")=='Y'>
		    	<input type="hidden" name="hideFields" value="N"/>
		  	<#else>
		    	<input type='hidden' name='hideFields' value='Y'/>
		  	</#if>-->
		  	<input type="hidden" name="viewSize" value="${viewSize}"/>
		  	<input type="hidden" name="viewIndex" value="${viewIndex}"/>
		  	<input type='hidden' name='correspondingPoId' value='${requestParameters.correspondingPoId?if_exists}'/>
		  	<input type='hidden' name='internalCode' value='${requestParameters.internalCode?if_exists}'/>
		  	<input type='hidden' name='productId' value='${requestParameters.productId?if_exists}'/>
		  	<input type='hidden' name='goodIdentificationTypeId' value='${requestParameters.goodIdentificationTypeId?if_exists}'/>
		  	<input type='hidden' name='goodIdentificationIdValue' value='${requestParameters.goodIdentificationIdValue?if_exists}'/>
		  	<input type='hidden' name='inventoryItemId' value='${requestParameters.inventoryItemId?if_exists}'/>
		  	<input type='hidden' name='serialNumber' value='${requestParameters.serialNumber?if_exists}'/>
		  	<input type='hidden' name='softIdentifier' value='${requestParameters.softIdentifier?if_exists}'/>
		  	<input type='hidden' name='partyId' value='${requestParameters.partyId?if_exists}'/>
		  	<input type='hidden' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'/>
		  	<input type='hidden' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'/>
		  	<input type='hidden' name='createdBy' value='${requestParameters.createdBy?if_exists}'/>
		  	<input type='hidden' name='minDate' value='${requestParameters.minDate?if_exists}'/>
		  	<input type='hidden' name='maxDate' value='${requestParameters.maxDate?if_exists}'/>
		  	<input type='hidden' name='roleTypeId' value="${requestParameters.roleTypeId?if_exists}"/>
		  	<input type='hidden' name='orderTypeId' value='${requestParameters.orderTypeId?if_exists}'/>
		  	<input type='hidden' name='salesChannelEnumId' value='${requestParameters.salesChannelEnumId?if_exists}'/>
		  	<input type='hidden' name='productStoreId' value='${requestParameters.productStoreId?if_exists}'/>
		  	<input type='hidden' name='orderWebSiteId' value='${requestParameters.orderWebSiteId?if_exists}'/>
		  	<input type='hidden' name='orderStatusId' value='${requestParameters.orderStatusId?if_exists}'/>
		  	<input type='hidden' name='hasBackOrders' value='${requestParameters.hasBackOrders?if_exists}'/>
		  	<input type='hidden' name='filterInventoryProblems' value='${requestParameters.filterInventoryProblems?if_exists}'/>
		  	<input type='hidden' name='filterPartiallyReceivedPOs' value='${requestParameters.filterPartiallyReceivedPOs?if_exists}'/>
		  	<input type='hidden' name='filterPOsOpenPastTheirETA' value='${requestParameters.filterPOsOpenPastTheirETA?if_exists}'/>
		  	<input type='hidden' name='filterPOsWithRejectedItems' value='${requestParameters.filterPOsWithRejectedItems?if_exists}'/>
		  	<input type='hidden' name='countryGeoId' value='${requestParameters.countryGeoId?if_exists}'/>
		  	<input type='hidden' name='includeCountry' value='${requestParameters.includeCountry?if_exists}'/>
		  	<input type='hidden' name='isViewed' value='${requestParameters.isViewed?if_exists}'/>
		  	<input type='hidden' name='shipmentMethod' value='${requestParameters.shipmentMethod?if_exists}'/>
		  	<input type='hidden' name='gatewayAvsResult' value='${requestParameters.gatewayAvsResult?if_exists}'/>
		  	<input type='hidden' name='gatewayScoreResult' value='${requestParameters.gatewayScoreResult?if_exists}'/>
		</form>
	</#if>
	<form class="form-horizontal basic-custom-form form-horizontal-mini" method="POST" name="lookuporder" id="lookuporder" action="<@ofbizUrl>searchOrders</@ofbizUrl>" onsubmit="javascript:lookupOrders();">
		<input type="hidden" name="lookupFlag" value="Y"/>
		<!--<input type="hidden" name="hideFields" value="Y"/>-->
		<input type="hidden" name="viewSize" value="${viewSize}"/>
		<input type="hidden" name="viewIndex" value="${viewIndex}"/>
		
		<div id="findOrders" class="widget-box">
  			<div class="widget-box transparent no-bottom-border">
				<#if parameters.hideFields?default("N") != "Y">
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="orderId">${uiLabelMap.DAOrderId}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="orderId" id="orderId" value="${parameters.orderId?if_exists}">
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="externalId">${uiLabelMap.OrderExternalId}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="externalId" id="externalId" value="${parameters.externalId?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="correspondingPoId">${uiLabelMap.OrderCustomerPo}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="correspondingPoId" id="correspondingPoId" value="${parameters.correspondingPoId?if_exists}">
								</div>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label" for="internalCode">${uiLabelMap.OrderInternalCode}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="internalCode" id="internalCode" value="${parameters.internalCode?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="productId">${uiLabelMap.DAProductId}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="productId" id="productId" value="${parameters.productId?if_exists}">
								</div>
							</div>
						</div>
						<#if goodIdentificationTypes?has_content>
							<#assign hasSKU = false />
							<#list goodIdentificationTypes as goodIdentificationType>
                              	<#if goodIdentificationType.goodIdentificationTypeId == "SKU">
                              		<#assign hasSKU = true />
                              	</#if>
                          	</#list>
                          	<#if hasSKU>
                          		<input type="hidden" name="goodIdentificationTypeId" id="goodIdentificationTypeId" value="SKU" />
                          		<div class="control-group">
									<label class="control-label" for="goodIdentificationIdValue">${uiLabelMap.DAProductBarcode}:</label>
									<div class="controls">
										<div class="span12">
											<input type='text' name='goodIdentificationIdValue' id="goodIdentificationIdValue" value='${requestParameters.goodIdentificationIdValue?if_exists}'/>
										</div>
									</div>
								</div>
                          	</#if>
							<#--
							<div class="control-group" style="border:solid 1px #555555; border-bottom:0">
								<label class="control-label" for="goodIdentificationTypeId">${uiLabelMap.ProductGoodIdentificationType}:</label>
								<div class="controls">
									<div class="span12">
										<select name='goodIdentificationTypeId' id="goodIdentificationTypeId">
			                          	<#if currentGoodIdentificationType?has_content>
			                              	<option value="${currentGoodIdentificationType.goodIdentificationTypeId}">${currentGoodIdentificationType.get("description", locale)}</option>
			                              	<option value="${currentGoodIdentificationType.goodIdentificationTypeId}">---</option>
			                          	</#if>
			                          	<option value="">${uiLabelMap.ProductAnyGoodIdentification}</option>
			                          	<#list goodIdentificationTypes as goodIdentificationType>
			                              	<option value="${goodIdentificationType.goodIdentificationTypeId}">${goodIdentificationType.get("description", locale)}</option>
			                          	</#list>
			                      	</select>
									</div>
								</div>
							</div>
							-->
						</#if>
						<div class="control-group">
							<label class="control-label" for="inventoryItemId">${uiLabelMap.DAInventoryItemId}:</label>
							<div class="controls">
								<div class="span12">
									<input type='text' name='inventoryItemId' id="inventoryItemId" value='${requestParameters.inventoryItemId?if_exists}'/>
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="serialNumber">${uiLabelMap.ProductSerialNumber}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="serialNumber" id="serialNumber" value="${parameters.serialNumber?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="softIdentifier">${uiLabelMap.ProductSoftIdentifier}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="softIdentifier" id="softIdentifier" value="${parameters.softIdentifier?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="softIdentifier">${uiLabelMap.PartyRoleType}:</label>
							<div class="controls">
								<div class="span12">
									<select name='roleTypeId' id='roleTypeId' multiple="multiple">
					                    <#if currentRole?has_content>
					                    	<option value="${currentRole.roleTypeId}">${currentRole.get("description", locale)}</option>
					                    </#if>
					                    <option value="">${uiLabelMap.CommonAnyRoleType}</option>
					                    <#list roleTypes as roleType>
					                      	<option value="${roleType.roleTypeId}">${roleType.get("description", locale)}</option>
					                    </#list>
				                  	</select>
								</div>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label" for="partyId">${uiLabelMap.DACustomerId}:</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="lookuporder" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="userLoginId">${uiLabelMap.DAUserLoginTKId}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="userLoginId" id="userLoginId" value="${parameters.userLoginId?if_exists}">
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="billingAccountId">${uiLabelMap.AccountingBillingAccount}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="billingAccountId" id="billingAccountId" value="${parameters.billingAccountId?if_exists}">
								</div>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label" for="createdBy">${uiLabelMap.CommonCreatedBy}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="createdBy" id="createdBy" value="${parameters.createdBy?if_exists}">
								</div>
							</div>
						</div>
					</div><!--.span6-->
					
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="userLoginId">${uiLabelMap.DAOrderType}:</label>
							<div class="controls">
								<div class="span12">
									<select name='orderTypeId'>
					                    <#if currentType?has_content>
					                    	<option value="${currentType.orderTypeId}">${currentType.get("description", locale)}</option>
					                    	<option value="${currentType.orderTypeId}">---</option>
					                    </#if>
					                    <option value="">${uiLabelMap.OrderAnyOrderType}</option>
					                    <#list orderTypes as orderType>
					                      	<option value="${orderType.orderTypeId}">${orderType.get("description", locale)}</option>
					                    </#list>
				                  	</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="orderIdz">${uiLabelMap.AccountingPaymentStatus}:</label>
							<div class="controls">
								<div class="span12">
									<select name="paymentStatusId">
				                        <option value="">${uiLabelMap.CommonAll}</option>
				                        <#list paymentStatusList as paymentStatus>
				                            <option value="${paymentStatus.statusId}">${paymentStatus.get("description", locale)}</option>
				                        </#list>
				                    </select>
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="salesChannelEnumId">${uiLabelMap.OrderSalesChannel}:</label>
							<div class="controls">
								<div class="span12">
									<select name='salesChannelEnumId' id="salesChannelEnumId">
					                    <#if currentSalesChannel?has_content>
					                    	<option value="${currentSalesChannel.enumId}">${currentSalesChannel.get("description", locale)}</option>
					                    	<option value="${currentSalesChannel.enumId}">---</option>
					                    </#if>
					                    <option value="">${uiLabelMap.CommonAnySalesChannel}</option>
					                    <#list salesChannels as channel>
					                      	<option value="${channel.enumId}">${channel.get("description", locale)}</option>
					                    </#list>
				                  	</select>
								</div>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label" for="productStoreId">${uiLabelMap.DAProductStore}:</label>
							<div class="controls">
								<div class="span12">
									<select name='productStoreId' id="productStoreId">
					                    <#if currentProductStore?has_content>
					                    	<option value="${currentProductStore.productStoreId}">${currentProductStore.storeName?if_exists}</option>
					                    	<option value="${currentProductStore.productStoreId}">---</option>
					                    </#if>
					                    <option value="">${uiLabelMap.CommonAnyStore}</option>
					                    <#list productStores as store>
					                      	<option value="${store.productStoreId}">${store.storeName?if_exists}</option>
					                    </#list>
			                  		</select>
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="orderWebSiteId">${uiLabelMap.ProductWebSite}:</label>
							<div class="controls">
								<div class="span12">
									<select name='orderWebSiteId' id="orderWebSiteId">
					                    <#if currentWebSite?has_content>
					                    	<option value="${currentWebSite.webSiteId}">${currentWebSite.siteName}</option>
					                    	<option value="${currentWebSite.webSiteId}">---</option>
					                    </#if>
					                    <option value="">${uiLabelMap.CommonAnyWebSite}</option>
					                    <#list webSites as webSite>
					                      	<option value="${webSite.webSiteId}">${webSite.siteName?if_exists}</option>
					                    </#list>
				                  	</select>
								</div>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label" for="orderStatusId">${uiLabelMap.CommonStatus}:</label>
							<div class="controls">
								<div class="span12">
									<select name='orderStatusId' id="orderStatusId">
					                    <#if currentStatus?has_content>
					                    	<option value="${currentStatus.statusId}">${currentStatus.get("description", locale)}</option>
					                    	<option value="${currentStatus.statusId}">---</option>
					                    </#if>
					                    <option value="">${uiLabelMap.OrderAnyOrderStatus}</option>
					                    <#list orderStatuses as orderStatus>
					                      	<option value="${orderStatus.statusId}">${orderStatus.get("description", locale)}</option>
					                    </#list>
				                  	</select>
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="orderIdz">${uiLabelMap.OrderContainsBackOrders}:</label>
							<div class="controls">
								<div class="span12">
									<select name='hasBackOrders'>
					                    <#if requestParameters.hasBackOrders?has_content>
					                    	<option value="Y">${uiLabelMap.OrderBackOrders}</option>
					                    	<option value="Y">---</option>
					                    </#if>
				                    	<option value="">${uiLabelMap.CommonShowAll}</option>
					                    <option value="Y">${uiLabelMap.CommonOnly}</option>
				                  	</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="orderIdz">${uiLabelMap.DAShippingMethod}:</label>
							<div class="controls">
								<div class="span12">
									<select name="shipmentMethod">
					                    <#if currentCarrierShipmentMethod?has_content>
					                      	<#assign currentShipmentMethodType = currentCarrierShipmentMethod.getRelatedOne("ShipmentMethodType", false)>
					                      	<option value="${currentCarrierShipmentMethod.partyId}@${currentCarrierShipmentMethod.shipmentMethodTypeId}">${currentCarrierShipmentMethod.partyId?if_exists} ${currentShipmentMethodType.description?if_exists}</option>
					                      	<option value="${currentCarrierShipmentMethod.partyId}@${currentCarrierShipmentMethod.shipmentMethodTypeId}">---</option>
					                    </#if>
					                    <option value="">${uiLabelMap.OrderSelectShippingMethod}</option>
					                    <#list carrierShipmentMethods as carrierShipmentMethod>
					                      	<#assign shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType", false)>
					                      	<option value="${carrierShipmentMethod.partyId}@${carrierShipmentMethod.shipmentMethodTypeId}">${carrierShipmentMethod.partyId?if_exists} ${shipmentMethodType.description?if_exists}</option>
					                    </#list>
				                  	</select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="orderIdz">${uiLabelMap.OrderViewed}:</label>
							<div class="controls">
								<div class="span12">
									<select name="isViewed">
					                    <#if requestParameters.isViewed?has_content>
					                      	<#assign isViewed = requestParameters.isViewed>
					                      	<option value="${isViewed}"><#if "Y" == isViewed>${uiLabelMap.CommonYes}<#elseif "N" == isViewed>${uiLabelMap.CommonNo}</#if></option>
					                    </#if>
					                    <option value=""></option>
					                    <option value="Y">${uiLabelMap.CommonYes}</option>
					                    <option value="N">${uiLabelMap.CommonNo}</option>
				                  	</select>
								</div>
							</div>
						</div>
						-->
						
						<div class="control-group">
							<label class="control-label" for="gatewayAvsResult">${uiLabelMap.OrderAddressVerification}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="gatewayAvsResult" id="gatewayAvsResult" value="${parameters.gatewayAvsResult?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="gatewayScoreResult">${uiLabelMap.OrderScore}:</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="gatewayScoreResult" id="gatewayScoreResult" value="${parameters.gatewayScoreResult?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="orderIdz">${uiLabelMap.CommonDateFilter}:</label>
							<div class="controls">
								<div>
									<@htmlTemplate.renderDateTimeField name="minDate" event="" action="" value="${requestParameters.minDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
									<span class="help-inline tooltipob">${uiLabelMap.DAFromDate}</span>
								</div>
								<div>
									<@htmlTemplate.renderDateTimeField name="maxDate" event="" action="" value="${requestParameters.maxDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="maxDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
									<span class="help-inline tooltipob">${uiLabelMap.DAThroughDate}</span>
								</div>
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="orderIdz">${uiLabelMap.OrderShipToCountry}:</label>
							<div class="controls">
								<div class="span12">
									<select name="countryGeoId">
					                    <#if requestParameters.countryGeoId?has_content>
					                        <#assign countryGeoId = requestParameters.countryGeoId>
					                        <#assign geo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", countryGeoId), true)>
					                        <option value="${countryGeoId}">${geo.geoName?if_exists}</option>
					                        <option value="${countryGeoId}">---</option>
					                    <#else>
					                        <option value="">---</option>
					                    </#if>
				                    	${screens.render("component://common/widget/CommonScreens.xml#countries")}
				                  	</select>
				                  	<select name="includeCountry">
					                    <#if requestParameters.includeCountry?has_content>
					                       <#assign includeCountry = requestParameters.includeCountry>
					                       <option value="${includeCountry}"><#if "Y" == includeCountry>${uiLabelMap.OrderOnlyInclude}<#elseif "N" == includeCountry>${uiLabelMap.OrderDoNotInclude}</#if></option>
					                       <option value="${includeCountry}">---</option>
					                    </#if>
					                    <option value="Y">${uiLabelMap.OrderOnlyInclude}</option>
					                    <option value="N">${uiLabelMap.OrderDoNotInclude}</option>
				                  	</select>
								</div>
							</div>
						</div>
						-->
					</div><!--.span6-->
				</div><!--.row-fluid-->
				<div class="row-fluid">
					<div class="span8">
						<div class="control-group">
							<label class="control-label">${uiLabelMap.DAFilter}: </label>
							<div class="controls">
								<div class="span12">
									<label style="margin-top:0 !important; color:#000">
										<input type="checkbox" name="filterInventoryProblems" value="Y" <#if requestParameters.filterInventoryProblems?default("N") == "Y">checked="checked"</#if> />
			                            <span class="lbl">&nbsp${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterInventoryProblems}</span>
			                    	</label>
			    					<label style="margin-top:0 !important; color:#000">
										<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y" <#if requestParameters.filterPartiallyReceivedPOs?default("N") == "Y">checked="checked"</#if> />
			                            <span class="lbl">&nbsp${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterPOs} ${uiLabelMap.OrderFilterPartiallyReceivedPOs}</span>
			                    	</label>
			                    	<label style="margin-top:0 !important; color:#000">
										<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y" <#if requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y">checked="checked"</#if> />
			                            <span class="lbl">&nbsp${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterPOs} ${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</span>
			                    	</label>
			    					<label style="margin-top:0 !important; color:#000">
										<input type="checkbox" name="filterPOsWithRejectedItems" value="Y" <#if requestParameters.filterPOsWithRejectedItems?default("N") == "Y">checked="checked"</#if> />
			                            <span class="lbl">&nbsp${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterPOs} ${uiLabelMap.OrderFilterPOsWithRejectedItems}</span>
			                    	</label>
								</div>
							</div><!--.controls-->
						</div>
					</div>
					<div class="span4">
						<input type="hidden" name="showAll" value="Y"/>
        				<button type="submit" class="btn btn-small btn-primary open-sans" name="submitButton"><i class="icon-search"></i>${uiLabelMap.CommonFind}</button>
					</div>
    			</div><!--.row-fluid-->
				</#if>
			</div><!--.widget-box-->
		</div><!--#findOrders-->

		<input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onclick="javascript:lookupOrders(true);"/>
	</form>
	<#if requestParameters.hideFields?default("N") != "Y">
		<script language="JavaScript" type="text/javascript">
			<!--//
			document.lookuporder.orderId.focus();
			//-->
		</script>
	</#if>
<#else>
  	<div class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</div>
</#if>