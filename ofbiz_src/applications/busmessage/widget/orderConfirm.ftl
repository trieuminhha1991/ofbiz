<#if orderHeader?exists>
<div id="orderoverview-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "orderoverview-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<#--<div style="position: absolute;color:#FFF;background: rgba(0,0,0,0.3);top: -15px;right: 0;padding: 5px 20px;">
			<#if salesMethodChannelEnumId?exists && salesMethodChannelEnumId == "SALES_GT_CHANNEL">
				<#if orderHeader.statusId == "ORDER_CREATED">... ${uiLabelMap.BSOrderWaitingSupApprove}
				<#elseif orderHeader.statusId == "ORDER_SUPAPPROVED">... ${uiLabelMap.BSOrderWaitingSalesAdminApprove}
				<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">... ${uiLabelMap.BSOrderWaitingPayment}
				<#elseif orderHeader.statusId == "ORDER_NPPAPPROVED">... ${uiLabelMap.BSOrderWaitingAccountantApprove}
				<#elseif orderHeader.statusId == "ORDER_APPROVED">... ${uiLabelMap.BSOrderWaitingLogisticProcessAndShipping}
				<#elseif orderHeader.statusId == "ORDER_HOLD">... ${uiLabelMap.BSOrderHolding}
				<#elseif orderHeader.statusId == "ORDER_COMPLETED">${uiLabelMap.BSOrderCompleted}
				<#elseif orderHeader.statusId == "ORDER_CANCELLED">${uiLabelMap.BSOrderCancelled}</#if>
			<#else>
				<#if orderHeader.statusId == "ORDER_CREATED">... ${uiLabelMap.BSOrderWaitingSupApprove}
				<#elseif orderHeader.statusId == "ORDER_SUPAPPROVED">... ${uiLabelMap.BSOrderWaitingSalesAdminApprove}
				<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">... ${uiLabelMap.BSOrderWaitingAccountantApprove}
				<#elseif orderHeader.statusId == "ORDER_APPROVED">... ${uiLabelMap.BSOrderWaitingLogisticProcessAndShipping}
				<#elseif orderHeader.statusId == "ORDER_HOLD">... ${uiLabelMap.BSOrderHolding}
				<#elseif orderHeader.statusId == "ORDER_COMPLETED">${uiLabelMap.BSOrderCompleted}
				<#elseif orderHeader.statusId == "ORDER_CANCELLED">${uiLabelMap.BSOrderCancelled}</#if>
			</#if>
			</div>-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${uiLabelMap.BusOrderConfirm}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSOrderId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${ordId?if_exists}</i></span>
								</div>
							</div>
							<#--<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSOrderName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${orderHeader.orderName?if_exists}</span>
								</div>
							</div>-->
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSOrderDate}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#if orderHeader.orderDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<#if desiredDeliveryDate?exists>
								<div class="row-fluid">
									<div class="div-inline-block">
										<label>${uiLabelMap.BSDesiredDeliveryDate}:</label>
									</div>
									<div class="div-inline-block">
										<span>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</span>
									</div>
								</div>
							</#if>
							<#if shipAfterDate?exists>
								<div class="row-fluid">
									<div class="div-inline-block">
										<label>${uiLabelMap.BSShipAfterDate}:</label>
									</div>
									<div class="div-inline-block">
										<span>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</span>
									</div>
								</div>
							</#if>
							<#if shipBeforeDate?exists>
								<div class="row-fluid">
									<div class="div-inline-block">
										<label>${uiLabelMap.BSShipBeforeDate}:</label>
									</div>
									<div class="div-inline-block">
										<span>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</span>
									</div>
								</div>
							</#if>
							<div class="row-fluid" style="margin-top:5px">
								<div class="div-inline-block" style="width:65px; vertical-align: top">
									<label style="line-height: 20px;">${uiLabelMap.OrderDestination}:</label>
								</div>
								<div class="div-inline-block" style="width:calc(100% - 70px)">
									<span>
										<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
										<ul class="unstyled spaced" style="margin: 0 0 0 0">
										<#list orderContactMechValueMaps as orderContactMechValueMap>
								          	<#assign contactMech = orderContactMechValueMap.contactMech>
								          	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
							              	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
								                <#assign postalAddress = orderContactMechValueMap.postalAddress>
								                <#if postalAddress?has_content>
								                	<li style="margin-bottom:0; margin-top:0">
														<#--<#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${postalAddress.toName}<br /></#if>
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
												    	</#if>-->
												    	<#if postalAddress.toName?has_content>${postalAddress.toName}<#if postalAddress.attnName?has_content> (${postalAddress.attnName})</#if>.</#if>
											            <#if postalAddress.address1?has_content> ${postalAddress.address1}, </#if>
											            <#if postalAddress.address2?has_content> ${postalAddress.address2}, </#if>
											            <#if postalAddress.wardGeoId?has_content>
											            	<#if "_NA_" == postalAddress.wardGeoId>
												            	 ___, 
												           	<#else>
												           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : postalAddress.wardGeoId}, true)/>
												            	 ${wardGeo?default(postalAddress.wardGeoId).geoName?default(postalAddress.wardGeoId)}, 
															</#if>
														</#if>
											            <#if postalAddress.districtGeoId?has_content>
											            	<#if "_NA_" == postalAddress.districtGeoId>
												            	 ___, 
												           	<#else>
												            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : postalAddress.districtGeoId}, true)/>
												            	 ${districtGeo?default(postalAddress.districtGeoId).geoName?default(postalAddress.districtGeoId)}, 
												           	</#if>
														</#if>
											            <#if postalAddress.city?has_content> ${postalAddress.city}, </#if>
											            <#if postalAddress.countryGeoId?has_content>
													      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
													      	${country.get("geoName", locale)?default(country.geoId)}
												    	</#if>
													</li>
								                </#if>
							                </#if>
						                </#list>
						                </ul>
									</span>
								</div>
							</div>
						</div><!--.span6-->
					</div>
				</div><!-- .form-horizontal -->
				<div class="form-horizontal basic-custom-form">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr style="font-weight: bold;">
								<td>${uiLabelMap.BSSTT}</td>
								<td class="align-left">${uiLabelMap.BSProduct} - ${uiLabelMap.BSProductName}</td>
								<td style="width:20px">${uiLabelMap.BSProdPromo}</td>
								<td style="width:30px">${uiLabelMap.BSUom}</td>
								<td align="left" class="align-left">${uiLabelMap.BSQuantity}</td>
							  	<td align="left" class="align-left" style="width:60px">${uiLabelMap.BSPriceBeforeVAT}</td>
							  	<td align="left" class="align-left">${uiLabelMap.BSAdjustment}</td>
								<td align="left" class="align-left">${uiLabelMap.BSItemTotal} <br />${uiLabelMap.BSParenthesisBeforeVAT}</td>
							</tr>
						</thead>
						<tbody>
						<#list listItemLine as itemLine>
	            			<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
	            			<tr>
	            				<#assign productId = itemLine.productId?if_exists/>
	            				<#assign product = itemLine.product?if_exists/>
								<td>${itemLine_index + 1}</td>
		                        <#if productId?exists && productId == "shoppingcart.CommentLine">
					                <td colspan="7" valign="top">
					                  	<div><b> &gt;&gt; ${itemLine.itemDescription?if_exists}</b></div>
					                </td>
		              			<#else>
		            				<td valign="top">
					                  	<div>
				                  		<#if itemLine.supplierProductId?has_content>
	                                        ${itemLine.supplierProductId} - ${itemLine.itemDescription?if_exists}
	                                    <#elseif productId?exists>
	                                    	<#--<a href="<@ofbizUrl>editProduct?productId=${productId}</@ofbizUrl>">${productId}</a>-->
	                                         ${itemLine.productCode?default(productId)}  - ${itemLine.itemDescription?if_exists}
	                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
	                                            <br /><span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}</span>
	                                        </#if>
	                                    <#elseif itemLine.orderItemType?exists>
	                                        ${itemLine.orderItemType.description} - ${itemLine.itemDescription?if_exists}
	                                    <#else>
	                                        ${itemLine.itemDescription?if_exists}
	                                    </#if>
										<#if itemLine.comments?exists>
											 - <span class="text-error">(${itemLine.comments})</span>
										</#if>
					                  	</div>
		               				</td>
		               				<td align="right" class="align-center" valign="top">
		                				${itemLine.isPromo?if_exists}
					                </td>
		                			<td align="right" class="align-center" valign="top">
		                				${itemLine.quantityUomDescription?if_exists}
					                </td>
					                <td align="right" class="align-right" valign="top">
					                  	<#if itemLine.quantity?exists>${itemLine.quantity?string.number}</#if>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Unit price -->
					                  	<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Adjustment -->
										<@ofbizCurrency amount=itemLine.adjustment isoCode=currencyUomId/>
					                </td>					               
					                <td align="right" class="align-right" valign="top" nowrap="nowrap"><#-- Sub total before VAT -->
				                  		<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId/>
					                </td>
					                <#-- Unit price after VAT - new column-->
		          				</#if>
							</tr>
							<#-- show info from workeffort -->
							<#-- show linked order lines -->
							<#-- show linked requirements -->
							<#-- show linked quote -->
							<#-- now show adjustment details per line item -->
							<#-- now show price info per line item -->
							<#-- now show survey information per line item -->
		                    <#-- display the ship estimated/before/after dates -->
		                    <#-- now show ship group info per line item -->
		                    <#-- now show inventory reservation info per line item -->
		                    
		                    <#-- now show planned shipment info per line item -->
		                    <#-- now show item issuances (shipment) per line item -->
		                    <#-- now show item issuances (inventory item) per line item -->
		                    <#-- now show shipment receipts per line item -->
						</#list>
						
						<#-- display tax prices sum -->
						<#list listTaxTotal as taxTotalItem>
							<tr>
								<td align="right" class="align-right" colspan="7">
									<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
								</td>
								<td class="align-right">
									<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
										(<@ofbizCurrency amount=-taxTotalItem.amount isoCode=currencyUomId/>)
									<#elseif taxTotalItem.amount?exists>
										<@ofbizCurrency amount=taxTotalItem.amount isoCode=currencyUomId/>
									</#if>
								</td>
							</tr>
						</#list>
						
						<#list orderHeaderAdjustments as orderHeaderAdjustment>
			                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
			                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
			                <#if adjustmentAmount != 0>
			                    <tr>
			                        <td align="right" class="align-right" colspan="7">
			                        	<#assign adjPrinted = false>
			                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}<#assign adjPrinted = true></#if>
			                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}<#assign adjPrinted = true></#if>
			                            <#if !adjPrinted><span>${adjustmentType.get("description", locale)}</span></#if>
			                        </td>
			                        <td align="right" class="align-right" nowrap="nowrap">
			                        	<#if (adjustmentAmount &lt; 0)>
	                                		<#assign adjustmentAmountNegative = -adjustmentAmount>
			                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId/>)
			                            <#else>
			                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
			                            </#if>
			                        </td>
			                    </tr>
			                </#if>
			            </#list>
						
						<#-- subtotal -->
	          			<tr>
	            			<td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.BSOrderItemsSubTotal}</b></div></td>
	            			<td align="right" class="align-right" nowrap="nowrap">
	            				<#if (orderSubTotal &lt; 0)>
                            		<#assign orderSubTotalNegative = -orderSubTotal>
                            		(<@ofbizCurrency amount=orderSubTotalNegative isoCode=currencyUomId/>)
                            	<#else>
                            		<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>
                        		</#if>
	        				</td>
	          			</tr>
	          			
	          			<#-- other adjustments -->
			            <tr>
			              	<td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.BSTotalOrderAdjustments}</b></div></td>
			              	<td align="right" class="align-right" nowrap="nowrap">
			              		<#if (otherAdjAmount &lt; 0)>
                            		<#assign otherAdjAmountNegative = -otherAdjAmount>
									(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId/>)
								<#else>
									<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
								</#if>
							</td>
			            </tr>
	          			
	          			<#-- tax adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.OrderTotalSalesTax}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				            	<#if (taxAmount &lt; 0)>
                            		<#assign taxAmountNegative = -taxAmount>
				            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId/>)
				            	<#else>
				            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId/>
				            	</#if>
				            </td>
			          	</tr>
	          			
	          			<#-- shipping adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="7"><div><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></div></td>
			          	</tr>
			          	
			          	<#-- grand total -->
			          	<tr>
			          		<#assign accountOneValue = grandTotal/>
			          		<#assign accountTwoValue = currencyUomId />
				            <td align="right" class="align-right" colspan="7"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.BSTotalAmountPayment}</b></div></td><#--uiLabelMap.OrderTotalDue-->
				            <td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
				            	<b>
				            		<@ofbizCurrency amount=orderHeader.grandTotal isoCode=currencyUomId/>
				            	</b>
				            </td>
			          	</tr>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
				
				<input type="hidden" name="accountOneValue" id="accountOneValue" value="${accountOneValue?default(0)}"/>
				<input type="hidden" name="accountTwoValue" id="accountTwoValue" value="${accountTwoValue?default(VND)}"/>
			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
</div>
</#if>