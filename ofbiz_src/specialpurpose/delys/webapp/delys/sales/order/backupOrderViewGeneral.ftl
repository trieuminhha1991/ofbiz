<div id="orderoverview-tab" class="tab-pane active">
	<h3 style="text-align:center;font-weight:bold">
		<#-- order name -->
	    <#if (orderHeader.orderName?has_content)>
			${orderHeader.orderName}
		<#else>
			${uiLabelMap.DAOrderFormTitle}
		</#if>
	</h3>
	
	<div class="widget-body">	
		<div class="widget-main">
			<div class="row-fluid">
				<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;">
					<div class="row margin_left_10 row-desc">
						<div class="span6">
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DAOrderName}:</label>
								<div class="controls-desc">
									<b>
										<#if orderHeader.orderName?exists && orderHeader.orderName?has_content>
											${orderHeader.orderName}
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</b>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DAOrderId}:</label>
								<div class="controls-desc">
									<b>
										<#if orderHeader.orderId?exists && orderHeader.orderId?has_content>
											${orderHeader.orderId}
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</b>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DACurrency}:</label>
								<div class="controls-desc">
									<b>
										<#if orderHeader.currencyUom?exists && orderHeader.currencyUom?has_content>
											${orderHeader.currencyUom}
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</b>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DADesiredDeliveryDate}:</label>
								<div class="controls-desc">
									<#if orderItemList?exists && orderItemList?has_content>
										<#assign desiredDeliveryDate = orderItemList[0]>
										<#if desiredDeliveryDate.estimatedDeliveryDate?has_content>
											<b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate.estimatedDeliveryDate, "dd/MM/yyyy", locale, timeZone)!}</b>
										</#if>
									<#else>
										<b>${uiLabelMap.DANotData}</b>
									</#if>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DACustomer}:</label>
								<div class="controls-desc">
									<b>
										${orderHeader.partyId?if_exists} 
										<#if displayParty?has_content || orderContactMechValueMaps?has_content>
											<#if displayParty?has_content>
								                <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
								                ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
							              	</#if>
							              	<#-- 
							              	<#if partyId?exists>
								                &nbsp;(<a href="${customerDetailLink?if_exists}${partyId}${externalKeyParam}" target="partymgr">${partyId}</a>)
							              	</#if>
							              	-->
							           	<#else>
											${uiLabelMap.DANotData}
										</#if>
									</b>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<div>
								<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
								<#list orderContactMechValueMaps as orderContactMechValueMap>
						          	<#assign contactMech = orderContactMechValueMap.contactMech>
						          	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
					              	<#-- <span>&nbsp;${contactMechPurpose.get("description",locale)}</span> -->
					              	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
						                <#assign postalAddress = orderContactMechValueMap.postalAddress>
						                <#if postalAddress?has_content>
					         	 			<div class="control-group">
												<label class="control-label-desc">${uiLabelMap.OrderDestination}:</label>
												<div class="controls-desc">
													<b>
														<#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${postalAddress.toName}<br /></#if>
											            <#if postalAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${postalAddress.attnName}<br /></#if>
											            <#if postalAddress.address1?has_content>${postalAddress.address1}<br /></#if>
											            <#if postalAddress.address2?has_content>${postalAddress.address2}<br /></#if>
											            <#if postalAddress.city?has_content>${postalAddress.city}</#if>
											            <#if postalAddress.stateProvinceGeoId?has_content>&nbsp;
													      	<#assign stateProvince = postalAddress.getRelatedOne("StateProvinceGeo", true)>
												      		${stateProvince.abbreviation?default(stateProvince.geoId)}
											            </#if>
											            <#if postalAddress.postalCode?has_content>, ${postalAddress.postalCode?if_exists}</#if>
											            <#if postalAddress.countryGeoId?has_content><br />
													      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
													      	${country.get("geoName", locale)?default(country.geoId)}
												    	</#if>
													</b>
												</div>
											</div>
						                </#if>
					                </#if>
				                </#list>
						    </div>
						    <#--
						    <#if !postalAddress.countryGeoId?has_content>
							    <#assign addr1 = postalAddress.address1?if_exists>
							    <#if addr1?has_content && (addr1.indexOf(" ") > 0)>
							      	<#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
							      	<#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
							      	<a target="_blank" href="${uiLabelMap.CommonLookupWhitepagesAddressLink}" class="buttontext">${uiLabelMap.CommonLookupWhitepages}</a>
							    </#if>
						  	</#if>
						    -->
							
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DADebt}:</label>
								<div class="controls-desc">
									<b>
										<#if displayParty?exists && displayParty?has_content>
							                <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "userLogin", userLogin))/>
							                ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</b>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DASup}:</label>
								<div class="controls-desc">
									<b>
										<#if displayParty?exists && displayParty?has_content>
											<#assign supList = delegator.findByAnd("PartyRelationship", {"partyIdTo" : displayParty.partyId, "roleTypeIdFrom" : "DELYS_SALESSUP_GT"}, null, false)>
							               	<#list supList as supItem>
							               		<#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", supItem.partyIdFrom, "userLogin", userLogin))/>
							                	${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
							               	</#list>
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</b>
								</div>
							</div>
						</div><!--.span6-->
					</div><!--.row-->
				</div><!--.form-horizontal.form-decrease-padding-->
				<div class="form-horizontal basic-custom-form" style="display: block;">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr>
								<td rowspan="2">${uiLabelMap.DASeqId}</td>
								<td colspan="2" class="center">${uiLabelMap.DAProduct}</td>
								<td rowspan="2" style="width:10px">${uiLabelMap.DAPackingPerTray}</td>
								<td colspan="3" align="center" class="center">${uiLabelMap.DAQuantity}</td>
								<td rowspan="2">${uiLabelMap.DASumTray}</td>
							  	<td rowspan="2" align="right" class="align-center" style="width:60px">${uiLabelMap.DAPriceBeforeVAT}</td>
							  	<td rowspan="2" align="right" class="align-right">${uiLabelMap.DAAdjustment}</td>
								<td rowspan="2" align="right" class="align-right">${uiLabelMap.DASubTotal} <br />${uiLabelMap.DAParenthesisBeforeVAT}</td>
								<#--<td rowspan="2" style="width:60px">${uiLabelMap.DAPriceAfterVAT}</td>
								<td colspan="2" class="color-red">${uiLabelMap.DAInvoicePrice}</td>-->
							</tr>
							<tr>
								<td>${uiLabelMap.DAProductId} - ${uiLabelMap.DAProductName}</td>
								<td style="width:15px">${uiLabelMap.DABarcode}</td>
								<td>${uiLabelMap.DAOrdered}</td>
								<td>${uiLabelMap.DAPromos}</td>
								<td>${uiLabelMap.DASum}</td>
								<#--
								<td class="color-red">${uiLabelMap.DAPrice}</td>
								<td class="color-red">${uiLabelMap.OrderSubTotal}</td>
								-->
							</tr>
						</thead>
						<tbody>
						<#assign localOrderReadHelper = Static["org.ofbiz.order.order.OrderReadHelper"]/>
						<#list orderItemList as orderItem>
	            			<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
	            			
	            			<#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
		                    <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)>
		                    <#if orderHeader.orderTypeId == "SALES_ORDER"><#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)></#if>
	            			<tr>
	            				<#assign orderItemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
		                        <#assign productId = orderItem.productId?if_exists>
								<td>${orderItem.get("orderItemSeqId")}</td>
		                        <#if productId?exists && productId == "shoppingcart.CommentLine">
					                <td colspan="10" valign="top">
					                  <b><div> &gt;&gt; ${orderItem.itemDescription}</div></b>
					                </td>
		              			<#else>
		            				<td valign="top">
					                  	<div>
					                  		<#if orderItem.supplierProductId?has_content>
		                                        ${orderItem.supplierProductId} - ${orderItem.itemDescription?if_exists}
		                                    <#elseif productId?exists>
		                                        <a href="<@ofbizUrl>product?product_id=${productId}</@ofbizUrl>">${productId}</a>
		                                         - ${orderItem.itemDescription?if_exists}
		                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
		                                            <br />
		                                            <span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}</span>
		                                        </#if>
		                                    <#elseif orderItemType?exists>
		                                        ${orderItemType.description} - ${orderItem.itemDescription?if_exists}
		                                    <#else>
		                                        ${orderItem.itemDescription?if_exists}
		                                    </#if>
					                  	</div>
		               				</td>
		                			<td><#--Barcode--></td>
		                			<td><#--QC/khay--></td>
					                <td align="right" valign="top">
					                  	<div nowrap="nowrap">${orderItem.quantity?default(0)?string.number}</div>
					                </td>
					                <td><#--km--></td>
					                <td><#--sum--></td>
					                <td><#--sum tray--></td>
					                <td align="right" valign="top"><#--unit price-->
					                  	<div nowrap="nowrap"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></div>
					                </td>
					                <td align="right" class="align-right" valign="top"><#--adjustment-->
					                  	<div nowrap="nowrap">
											<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false) isoCode=currencyUomId/>
										</div>
					                </td>
					                <td align="right" class="align-right" valign="top" nowrap="nowrap"><#--DASubTotalBeforeVAT-->
					                  	<div>
					                  		<#if orderItem.statusId != "ITEM_CANCELLED">
												<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId rounding=0/>
											<#else>
												<@ofbizCurrency amount=0.00 isoCode=currencyUomId/>
											</#if>
					                  	</div>
					                </td>
					                <#--unit price after vat <td></td>-->
		          				</#if>
							</tr>
							
							<#-- show info from workeffort -->
				            <#--
				            <#assign workOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment", null, null, false)?if_exists>
		                    <#if workOrderItemFulfillments?has_content>
		                        <#list workOrderItemFulfillments as workOrderItemFulfillment>
		                            <#assign workEffort = workOrderItemFulfillment.getRelatedOne("WorkEffort", true)>
		                            <tr>
		                                <td colspan="11">
		                                    <#if orderItem.orderItemTypeId != "RENTAL_ORDER_ITEM">
		                                        <span >${uiLabelMap.ManufacturingProductionRun}</span>
		                                        <a href="/manufacturing/control/ShowProductionRun?productionRunId=${workEffort.workEffortId}${externalKeyParam}"
		                                            class="btn btn-mini btn-primary">${workEffort.workEffortId}</a>
		                                        ${uiLabelMap.OrderCurrentStatus}
		                                        ${(delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", workEffort.getString("currentStatusId")), true).get("description",locale))?if_exists}
		                                    <#else>
		                                        ${uiLabelMap.CommonFrom}
		                                        : <#if workEffort.estimatedStartDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(workEffort.estimatedStartDate, "", locale, timeZone)!}</#if> ${uiLabelMap.CommonTo}
		                                        : <#if workEffort.estimatedCompletionDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(workEffort.estimatedCompletionDate, "", locale, timeZone)!}</#if> ${uiLabelMap.OrderNumberOfPersons}
		                                        : ${workEffort.reservPersons?default("")}
		                                    </#if>
		                                </td>
		                            </tr>
		                            <#break>
		                        </#list>
		                    </#if>
				            -->
	
							<#-- show linked order lines -->
		                   	<#--
		                   	<#assign linkedOrderItemsTo = delegator.findByAnd("OrderItemAssoc", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderItem.getString("orderId"), "orderItemSeqId", orderItem.getString("orderItemSeqId")), null, false)>
		                    <#assign linkedOrderItemsFrom = delegator.findByAnd("OrderItemAssoc", Static["org.ofbiz.base.util.UtilMisc"].toMap("toOrderId", orderItem.getString("orderId"), "toOrderItemSeqId", orderItem.getString("orderItemSeqId")), null, false)>
		                    <#if linkedOrderItemsTo?has_content>
		                        <#list linkedOrderItemsTo as linkedOrderItem>
		                            <#assign linkedOrderId = linkedOrderItem.toOrderId>
		                            <#assign linkedOrderItemSeqId = linkedOrderItem.toOrderItemSeqId>
		                            <#assign linkedOrderItemValue = linkedOrderItem.getRelatedOne("ToOrderItem", false)>
		                            <#assign linkedOrderItemValueStatus = linkedOrderItemValue.getRelatedOne("StatusItem", false)>
		                            <#assign description = linkedOrderItem.getRelatedOne("OrderItemAssocType", false).getString("description")/>
		                            <tr>
		                                <td colspan="11">
		                                    <span >${uiLabelMap.OrderLinkedToOrderItem}</span>&nbsp;(${description?if_exists})
		                                    <a href="/ordermgr/control/orderview?orderId=${linkedOrderId}"
		                                       class="btn btn-mini btn-primary">${linkedOrderId}/${linkedOrderItemSeqId}</a>&nbsp;${linkedOrderItemValueStatus.description?if_exists}
		                                </td>
		                            </tr>
		                        </#list>
		                    </#if>
		                   	-->
		                   
		                    <#--
		                    <#if linkedOrderItemsFrom?has_content>
		                        <#list linkedOrderItemsFrom as linkedOrderItem>
		                            <#assign linkedOrderId = linkedOrderItem.orderId>
		                            <#assign linkedOrderItemSeqId = linkedOrderItem.orderItemSeqId>
		                            <#assign linkedOrderItemValue = linkedOrderItem.getRelatedOne("FromOrderItem", false)>
		                            <#assign linkedOrderItemValueStatus = linkedOrderItemValue.getRelatedOne("StatusItem", false)>
		                            <#assign description = linkedOrderItem.getRelatedOne("OrderItemAssocType", false).getString("description")/>
		                            <tr>
		                                <td colspan="11">
		                                    <span >${uiLabelMap.OrderLinkedFromOrderItem}</span>&nbsp;(${description?if_exists})
		                                    <a href="/ordermgr/control/orderview?orderId=${linkedOrderId}"
		                                       class="btn btn-mini btn-primary">${linkedOrderId}/${linkedOrderItemSeqId}</a>&nbsp;${linkedOrderItemValueStatus.description?if_exists}
		                                </td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
							
							<#-- show linked requirements -->
		                    <#--
		                    <#assign linkedRequirements = orderItem.getRelated("OrderRequirementCommitment", null, null, false)?if_exists>
		                    <#if linkedRequirements?has_content>
		                        <#list linkedRequirements as linkedRequirement>
		                            <tr>
		                                <td colspan="11">
		                                    <span >${uiLabelMap.OrderLinkedToRequirement}</span>&nbsp;
		                                    <a href="<@ofbizUrl>EditRequirement?requirementId=${linkedRequirement.requirementId}</@ofbizUrl>"
		                                       class="btn btn-mini btn-primary">${linkedRequirement.requirementId}</a>&nbsp;
		                                </td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    
							<#-- show linked quote -->
		                    <#--
		                    <#assign linkedQuote = orderItem.getRelatedOne("QuoteItem", true)?if_exists>
		                    <#if linkedQuote?has_content>
		                        <tr>
		                            <td colspan="11">
		                                <span >${uiLabelMap.OrderLinkedToQuote}</span>&nbsp;
		                                <a href="<@ofbizUrl>EditQuoteItem?quoteId=${linkedQuote.quoteId}&amp;quoteItemSeqId=${linkedQuote.quoteItemSeqId}</@ofbizUrl>"
		                                   class="btn btn-mini btn-primary">${linkedQuote.quoteId}-${linkedQuote.quoteItemSeqId}</a>&nbsp;
		                            </td>
		                        </tr>
		                    </#if>
		                    -->
							
							<#-- now show adjustment details per line item -->
		                    <#assign orderItemAdjustments = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentList(orderItem, orderAdjustments)>
		                    <#if orderItemAdjustments?exists && orderItemAdjustments?has_content>
		                        <#list orderItemAdjustments as orderItemAdjustment>
		                            <#assign adjustmentType = orderItemAdjustment.getRelatedOne("OrderAdjustmentType", true)>
		                            <tr>
		                                <td align="right" colspan="10">
		                                    <span >${uiLabelMap.OrderAdjustment}</span>&nbsp;${adjustmentType.get("description",locale)}
		                                    ${orderItemAdjustment.get("description",locale)?if_exists}
		                                    <#if orderItemAdjustment.comments?has_content>
		                                        (${orderItemAdjustment.comments?default("")})
		                                    </#if>
		                                    <#if orderItemAdjustment.productPromoId?has_content>
		                                        <a class="btn btn-mini btn-primary" href="/catalog/control/EditProductPromo?productPromoId=${orderItemAdjustment.productPromoId}${externalKeyParam}"
		                                            >${orderItemAdjustment.getRelatedOne("ProductPromo", false).getString("promoName")}</a>
		                                    </#if>
		                                    <#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
		                                        <#if orderItemAdjustment.primaryGeoId?has_content>
		                                            <#assign primaryGeo = orderItemAdjustment.getRelatedOne("PrimaryGeo", true)/>
		                                            <#if primaryGeo.geoName?has_content>
		                                                <span >${uiLabelMap.OrderJurisdiction}</span>&nbsp;${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
		                                            </#if>
		                                            <#if orderItemAdjustment.secondaryGeoId?has_content>
		                                                <#assign secondaryGeo = orderItemAdjustment.getRelatedOne("SecondaryGeo", true)/>
		                                                <span >${uiLabelMap.CommonIn}</span>&nbsp;${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
		                                            </#if>
		                                        </#if>
		                                        <#if orderItemAdjustment.sourcePercentage?exists>
		                                            <span >${uiLabelMap.OrderRate}</span>&nbsp;${orderItemAdjustment.sourcePercentage?string("0.######")}
		                                        </#if>
		                                        <#if orderItemAdjustment.customerReferenceId?has_content>
		                                            <span >${uiLabelMap.OrderCustomerTaxId}</span>&nbsp;${orderItemAdjustment.customerReferenceId}
		                                        </#if>
		                                        <#if orderItemAdjustment.exemptAmount?exists>
		                                            <span >${uiLabelMap.OrderExemptAmount}</span>&nbsp;${orderItemAdjustment.exemptAmount}
		                                        </#if>
		                                    </#if>
		                                </td>
		                                <td align="right" class="align-right">
		                                    <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcItemAdjustment(orderItemAdjustment, orderItem) isoCode=currencyUomId/>
		                                </td>
		                            </tr>
		                        </#list>
		                    </#if>
							
							<#-- now show price info per line item -->
		                    <#--
		                    <#assign orderItemPriceInfos = orderReadHelper.getOrderItemPriceInfos(orderItem)>
		                    <#if orderItemPriceInfos?exists && orderItemPriceInfos?has_content>
		                        <tr>
		                            <td colspan="11">&nbsp;</td>
		                        </tr>
		                        <#list orderItemPriceInfos as orderItemPriceInfo>
		                            <tr>
		                                <td align="right" colspan="9">
		                                    <span>${uiLabelMap.ProductPriceRuleNameId}</span>&nbsp;[${orderItemPriceInfo.productPriceRuleId?if_exists}:${orderItemPriceInfo.productPriceActionSeqId?if_exists}]
		                                    ${orderItemPriceInfo.description?if_exists}
		                                </td>
		                                <td align="right">
		                                    <@ofbizCurrency amount=orderItemPriceInfo.modifyAmount isoCode=currencyUomId/>
		                                </td>
		                                <td colspan="1">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
							
							<#-- now show survey information per line item -->
		                    <#--
		                    <#assign orderItemSurveyResponses = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSurveyResponse(orderItem)>
		                    <#if orderItemSurveyResponses?exists && orderItemSurveyResponses?has_content>
		                        <#list orderItemSurveyResponses as survey>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <span >${uiLabelMap.CommonSurveys}</span>&nbsp;
		                                    <a href="/content/control/ViewSurveyResponses?surveyResponseId=${survey.surveyResponseId}&amp;surveyId=${survey.surveyId}${externalKeyParam}"
		                                       class="btn btn-mini btn-primary">${survey.surveyId}</a>
		                                </td>
		                                <td colspan="3">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    
		                    <#-- display the ship estimated/before/after dates -->
		                    <#--
		                    <#if orderItem.estimatedShipDate?exists>
		                        <tr>
		                            <td align="right" colspan="8">
		                                <span >${uiLabelMap.OrderEstimatedShipDate}</span>&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.estimatedShipDate, "", locale, timeZone)!}
		                            </td>
		                            <td colspan="3">&nbsp;</td>
		                        </tr>
		                    </#if>
		                    <#if orderItem.estimatedDeliveryDate?exists>
		                        <tr>
		                            <td align="right" colspan="8">
		                                <span >${uiLabelMap.OrderOrderQuoteEstimatedDeliveryDate}</span>&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.estimatedDeliveryDate, "", locale, timeZone)!}
		                            </td>
		                            <td colspan="3">&nbsp;</td>
		                        </tr>
		                    </#if>
		                    <#if orderItem.shipAfterDate?exists>
		                        <tr>
		                            <td align="right" colspan="8">
		                                <span >${uiLabelMap.OrderShipAfterDate}</span>&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.shipAfterDate, "", locale, timeZone)!}
		                            </td>
		                            <td colspan="3">&nbsp;</td>
		                        </tr>
		                    </#if>
		                    <#if orderItem.shipBeforeDate?exists>
		                        <tr>
		                            <td align="right" colspan="8">
		                                <span >${uiLabelMap.OrderShipBeforeDate}</span>&nbsp;${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(orderItem.shipBeforeDate, "", locale, timeZone)!}
		                            </td>
		                            <td colspan="3">&nbsp;</td>
		                        </tr>
		                    </#if>
		                    -->
		                    <#-- now show ship group info per line item -->
		                    <#--
		                    <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc", null, null, false)?if_exists>
		                    <#if orderItemShipGroupAssocs?has_content>
		                        <#list orderItemShipGroupAssocs as shipGroupAssoc>
		                            <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup", false)>
		                            <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <span >${uiLabelMap.OrderShipGroup}</span>&nbsp;[${shipGroup.shipGroupSeqId}]
		                                    ${shipGroupAddress.address1?default("${uiLabelMap.OrderNotShipped}")}
		                                </td>
		                                <td align="center">
		                                    ${shipGroupAssoc.quantity?string.number}&nbsp;
		                                </td>
		                                <td colspan="2">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    <#-- now show inventory reservation info per line item -->
		                    <#if orderItemShipGrpInvResList?exists && orderItemShipGrpInvResList?has_content>
		                        <#list orderItemShipGrpInvResList as orderItemShipGrpInvRes>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <span >${uiLabelMap.CommonInventory}</span>&nbsp;
		                                    <a class="btn btn-mini btn-primary" href="/facility/control/EditInventoryItem?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}${externalKeyParam}"
		                                       class="buttontext">${orderItemShipGrpInvRes.inventoryItemId}</a>
		                                    <span >${uiLabelMap.OrderShipGroup}</span>&nbsp;${orderItemShipGrpInvRes.shipGroupSeqId}
		                                </td>
		                                <td align="center">
		                                    ${orderItemShipGrpInvRes.quantity?string.number}&nbsp;
		                                </td>
		                                <td>
		                                    <#if (orderItemShipGrpInvRes.quantityNotAvailable?has_content && orderItemShipGrpInvRes.quantityNotAvailable > 0)>
		                                        <span style="color: red;">
		                                            [${orderItemShipGrpInvRes.quantityNotAvailable?string.number}&nbsp;${uiLabelMap.OrderBackOrdered}]
		                                        </span>
		                                        <#--<a href="<@ofbizUrl>balanceInventoryItems?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}&amp;orderId=${orderId}&amp;priorityOrderId=${orderId}&amp;priorityOrderItemSeqId=${orderItemShipGrpInvRes.orderItemSeqId}</@ofbizUrl>" class="buttontext" style="font-size: xx-small;">Raise Priority</a> -->
		                                    </#if>
		                                    &nbsp;
		                                </td>
		                                <td colspan="1">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    <#-- now show planned shipment info per line item -->
		                    <#--
		                    <#assign orderShipments = orderItem.getRelated("OrderShipment", null, null, false)?if_exists>
		                    <#if orderShipments?has_content>
		                        <#list orderShipments as orderShipment>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <span >${uiLabelMap.OrderPlannedInShipment}</span>&nbsp;<a
		                                        target="facility"
		                                        href="/facility/control/ViewShipment?shipmentId=${orderShipment.shipmentId}${externalKeyParam}"
		                                        class="buttontext">${orderShipment.shipmentId}</a>: ${orderShipment.shipmentItemSeqId}
		                                </td>
		                                <td align="center">
		                                    ${orderShipment.quantity?string.number}&nbsp;
		                                </td>
		                                <td colspan="2">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    
		                    <#-- now show item issuances (shipment) per line item -->
		                    <#--
		                    <#assign itemIssuances = itemIssuancesPerItem.get(orderItem.get("orderItemSeqId"))?if_exists>
		                    <#if itemIssuances?has_content>
		                        <#list itemIssuances as itemIssuance>
		                        <tr>
		                            <td align="right" colspan="8">
		                                <#if itemIssuance.shipmentId?has_content>
		                                    <span >${uiLabelMap.OrderIssuedToShipmentItem}</span>&nbsp;
		                                    <a target="facility"
		                                       href="/facility/control/ViewShipment?shipmentId=${itemIssuance.shipmentId}${externalKeyParam}"
		                                       class="btn btn-mini btn-primary">${itemIssuance.shipmentId}</a>: ${itemIssuance.shipmentItemSeqId?if_exists}
		                                <#else>
		                                    <span >${uiLabelMap.OrderIssuedWithoutShipment}</span>
		                                </#if>
		                            </td>
		                            <td align="center">
		                                ${itemIssuance.quantity?default(0) - itemIssuance.cancelQuantity?default(0)}&nbsp;
		                            </td>
		                            <td colspan="2">&nbsp;</td>
		                        </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    
		                    <#-- now show item issuances (inventory item) per line item -->
		                    <#--
		                    <#if itemIssuances?has_content>
		                        <#list itemIssuances as itemIssuance>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <#if itemIssuance.inventoryItemId?has_content>
		                                        <#assign inventoryItem = itemIssuance.getRelatedOne("InventoryItem", false)/>
		                                        <span >${uiLabelMap.CommonInventory}</span>
		                                        <a href="/facility/control/EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId}${externalKeyParam}"
		                                           class="btn btn-mini btn-primary">${itemIssuance.inventoryItemId}</a>
		                                        <span >${uiLabelMap.OrderShipGroup}</span>&nbsp;${itemIssuance.shipGroupSeqId?if_exists}
		                                        <#if (inventoryItem.serialNumber?has_content)>
		                                            <br />
		                                            <span >${uiLabelMap.ProductSerialNumber}</span>&nbsp;${inventoryItem.serialNumber}&nbsp;
		                                        </#if>
		                                    </#if>
		                                </td>
		                                <td align="center">
		                                    ${itemIssuance.quantity?default(0) - itemIssuance.cancelQuantity?default(0)}
		                                </td>
		                                <td colspan="2">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    <#-- now show shipment receipts per line item -->
		                    <#--
		                    <#assign shipmentReceipts = orderItem.getRelated("ShipmentReceipt", null, null, false)?if_exists>
		                    <#if shipmentReceipts?has_content>
		                        <#list shipmentReceipts as shipmentReceipt>
		                            <tr>
		                                <td align="right" colspan="8">
		                                    <#if shipmentReceipt.shipmentId?has_content>
		                                        <span >${uiLabelMap.OrderShipmentReceived}</span>&nbsp;
		                                        <a target="facility"
		                                           href="/facility/control/ViewShipment?shipmentId=${shipmentReceipt.shipmentId}${externalKeyParam}"
		                                           class="btn btn-mini btn-primary">${shipmentReceipt.shipmentId}</a>:${shipmentReceipt.shipmentItemSeqId?if_exists}
		                                    </#if>
		                                    &nbsp;<#if shipmentReceipt.datetimeReceived?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipmentReceipt.datetimeReceived, "", locale, timeZone)!}</#if>&nbsp;
		                                    <span >${uiLabelMap.CommonInventory}</span>&nbsp;
		                                    <a href="/facility/control/EditInventoryItem?inventoryItemId=${shipmentReceipt.inventoryItemId}${externalKeyParam}"
		                                       class="btn btn-mini btn-primary">${shipmentReceipt.inventoryItemId}</a>
		                                </td>
		                                <td align="center">
		                                    ${shipmentReceipt.quantityAccepted?string.number}&nbsp;/&nbsp;${shipmentReceipt.quantityRejected?default(0)?string.number}
		                                </td>
		                                <td colspan="2">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
							
						</#list>
						
						<#list orderHeaderAdjustments as orderHeaderAdjustment>
			                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
			                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
			                <#if adjustmentAmount != 0>
			                    <tr>
			                        <td align="right" class="align-right" colspan="10">
			                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments} - </#if>
			                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} - </#if>
			                            <span >${adjustmentType.get("description", locale)}</span>
			                        </td>
			                        <td align="right" class="align-right" nowrap="nowrap">
			                            <@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId rounding=0/>
			                        </td>
			                    </tr>
			                </#if>
			            </#list>
						
						<#-- subtotal -->
	          			<tr>
	            			<td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderItemsSubTotal}</b></div></td>
	            			<td align="right" class="align-right" nowrap="nowrap">
	            				<div><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=0/></div>
	        				</td>
	          			</tr>
	          			
	          			<#-- other adjustments -->
			            <tr>
			              	<td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderTotalOtherOrderAdjustments}</b></div></td>
			              	<td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId rounding=0/></div></td>
			            </tr>
	          			
	          			<#-- shipping adjustments -->
			          	<#--
			          	<tr>
				            <td align="right" colspan="10"><div><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=0/></div></td>
			          	</tr>
			          	-->
			          	
			          	<#-- tax adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderTotalSalesTax}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				              <div><@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=0/></div>
				            </td>
			          	</tr>
			          	
			          	<#-- grand total -->
			          	<tr>
				            <td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderTotalDue}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				              <div><@ofbizCurrency amount=grandTotal isoCode=currencyUomId rounding=0/></div>
				            </td>
			          	</tr>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
</div>

