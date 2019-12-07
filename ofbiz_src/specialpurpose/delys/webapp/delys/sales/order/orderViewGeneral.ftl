<#if orderHeader?exists && orderHeader.salesMethodChannelEnumId?exists>
	<#assign salesMethodChannelEnumId = orderHeader.salesMethodChannelEnumId />
</#if>
<#if (isChiefAccountant || isDistributor) && salesMethodChannelEnumId?exists && salesMethodChannelEnumId == "SALES_GT_CHANNEL">
	<#assign isDisplayInvoicePrice = true>
<#else>
	<#assign isDisplayInvoicePrice = false>
</#if>
<div id="orderoverview-tab" class="tab-pane active">
	<div><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<div style="position: absolute;color:#FFF;background: rgba(0,0,0,0.3);top: 0px;right: 0;padding: 5px 20px;">
			<#if salesMethodChannelEnumId?exists && salesMethodChannelEnumId == "SALES_GT_CHANNEL">
				<#if orderHeader.statusId == "ORDER_CREATED">... ${uiLabelMap.DAOrderWaitingSupApprove}
				<#elseif orderHeader.statusId == "ORDER_SUPAPPROVED">... ${uiLabelMap.DAOrderWaitingSalesAdminApprove}
				<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">... ${uiLabelMap.DAOrderWaitingPayment}
				<#elseif orderHeader.statusId == "ORDER_NPPAPPROVED">... ${uiLabelMap.DAOrderWaitingAccountantApprove}
				<#elseif orderHeader.statusId == "ORDER_APPROVED">... ${uiLabelMap.DAOrderWaitingLogisticProcessAndShipping}
				<#elseif orderHeader.statusId == "ORDER_HOLD">... ${uiLabelMap.DAOrderHolding}
				<#elseif orderHeader.statusId == "ORDER_COMPLETED">${uiLabelMap.DAOrderCompleted}
				<#elseif orderHeader.statusId == "ORDER_CANCELLED">${uiLabelMap.DAOrderCancelled}</#if>
			<#else>
				<#if orderHeader.statusId == "ORDER_CREATED">... ${uiLabelMap.DAOrderWaitingSupApprove}
				<#elseif orderHeader.statusId == "ORDER_SUPAPPROVED">... ${uiLabelMap.DAOrderWaitingSalesAdminApprove}
				<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">... ${uiLabelMap.DAOrderWaitingAccountantApprove}
				<#elseif orderHeader.statusId == "ORDER_APPROVED">... ${uiLabelMap.DAOrderWaitingLogisticProcessAndShipping}
				<#elseif orderHeader.statusId == "ORDER_HOLD">... ${uiLabelMap.DAOrderHolding}
				<#elseif orderHeader.statusId == "ORDER_COMPLETED">${uiLabelMap.DAOrderCompleted}
				<#elseif orderHeader.statusId == "ORDER_CANCELLED">${uiLabelMap.DAOrderCancelled}</#if>
			</#if>
			</div>
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${uiLabelMap.DASalesOrderFormTitle}<#--DAOrderFormTitle-->
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
					<div class="row margin_left_10 row-desc">
						<div class="span6">
							<div class="row-fluid margin-bottom5">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DAOrderId}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b><i>${orderHeader.orderId?if_exists}</i></b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom5">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DAOrderName}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b>${orderHeader.orderName?if_exists}</b></span>
								</div>
							</div>
							<#--<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DACurrency}:</label>
								<div class="controls-desc">
									<span><b>${orderHeader.currencyUom?if_exists}</b></span>
								</div>
							</div>-->
							<div class="row-fluid margin-bottom5">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DAOrderDate}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b><#if orderHeader.orderDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if></b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom5">
								<div class="span3 align-left">
									<label class="control-label-desc">${uiLabelMap.DACustomer}:</label>
								</div>
								<div class="span9 controls-desc">
									<span><b>${displayPartyNameResult?if_exists}</b></span>
								</div>
							</div>
						</div><!-- .span6 -->
						<div class="span6">
							<div class="row-fluid margin-bottom5">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.DADesiredDeliveryDate}:</label>	
								</div>
								<div class="span8 controls-desc">
									<span><b><#if desiredDeliveryDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if></b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom5">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.OrderDestination}:</label>
								</div>
								<div class="span8 controls-desc"><b>
									<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
									<ul class="unstyled spaced" style="margin: 0 0 0 0">
									<#list orderContactMechValueMaps as orderContactMechValueMap>
							          	<#assign contactMech = orderContactMechValueMap.contactMech>
							          	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
						              	<#-- <span>&nbsp;${contactMechPurpose.get("description",locale)}</span> -->
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
										            <#if postalAddress.address1?has_content> ${postalAddress.address1}.</#if>
										            <#if postalAddress.address2?has_content> ${postalAddress.address2}.</#if>
										            <#if postalAddress.city?has_content> ${postalAddress.city}</#if>
										            <#if postalAddress.countryGeoId?has_content>, 
												      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
												      	${country.get("geoName", locale)?default(country.geoId)}
											    	</#if>
												</li>
							                </#if>
						                </#if>
					                </#list>
					                </ul></b>
								</div>
							</div>
							<div class="row-fluid margin-bottom5">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.accReceivableToApplyTotal}:</label>
								</div>
								<div class="span8 controls-desc">
									<span><b style="color:#d6412b">
										<@ofbizCurrency amount=0 isoCode=currencyUomId rounding=0/>
										<#--
										<#if totalToApply?exists && totalToApply?has_content>
						                	<@ofbizCurrency amount=totalToApply isoCode=currencyUomId rounding=0/>
								        </#if>
										-->
								    </b></span>
								</div>
							</div>
							<div class="row-fluid margin-bottom5">
								<div class="span4 align-left">
									<label class="control-label-desc">${uiLabelMap.DASUP}:</label>
								</div>
								<div class="span8 controls-desc">
									<span><b>${displaySUPsNameResult?if_exists}</b></span>
								</div>
							</div>
						</div><!-- .span6 -->
					</div><!-- .row-fluid -->
				</div><!-- .form-horizontal -->
				<div class="form-horizontal basic-custom-form" style="display: block;">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr style="font-weight: bold;">
								<td rowspan="2">${uiLabelMap.DANo}</td>
								<td colspan="3" class="center">${uiLabelMap.DAProduct}</td>
								<td rowspan="2" style="width:10px" class="align-center">${uiLabelMap.DAPackingPerTray}</td>
								<td rowspan="2" style="width:20px" class="align-center">${uiLabelMap.DAQuantityUomId}</td>
								<td rowspan="2" align="center" class="center">${uiLabelMap.DAQuantity}</td>
								<td rowspan="2" class="align-center">${uiLabelMap.DASumTray}</td>
							  	<td rowspan="2" align="center" class="align-center" style="width:60px">${uiLabelMap.DAPriceBeforeVAT}</td>
							  	<td rowspan="2" align="center" class="align-center">${uiLabelMap.DAAdjustment}</td>
								<td rowspan="2" align="center" class="align-center">${uiLabelMap.DASubTotal} <br />${uiLabelMap.DAParenthesisBeforeVAT}</td>
								<#--<td rowspan="2" style="width:60px">${uiLabelMap.DAPriceAfterVAT}</td>
								<td colspan="2" class="color-red">${uiLabelMap.DAInvoicePrice}</td>-->
								<#if isDisplayInvoicePrice>
								<td colspan="2" class="color-red align-center">${uiLabelMap.DAInvoicePrice}</td>
								</#if>
							</tr>
							<tr style="font-weight: bold;">
								<td colspan="2" class="align-center">${uiLabelMap.DAProductId} - ${uiLabelMap.DAProductName}</td>
								<#--<td>${uiLabelMap.DABarcode}</td>-->
								<td>${uiLabelMap.DAAbbExpireDate}</td>
								<#--
								<td>${uiLabelMap.DAOrdered}</td>
								<td>${uiLabelMap.DAPromos}</td>
								<td>${uiLabelMap.DASum}</td>
								-->
								<#--
								<td class="color-red">${uiLabelMap.DAPrice}</td>
								<td class="color-red">${uiLabelMap.OrderSubTotal}</td>
								-->
								<#if isDisplayInvoicePrice>
								<td class="color-red align-center">${uiLabelMap.DAPrice}</td>
								<td class="color-red align-center">${uiLabelMap.DASubTotal}</td>
								</#if>
							</tr>
						</thead>
						<tbody>
						<#--<#assign taxTotalOrderItems = 0/>
						<#assign subAmountExportOrder = 0.00/>
						<#assign subAmountExportInvoice = 0.00/>-->
						<#list listItemLine as itemLine>
	            			<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
	            			
	            			<#--
	            			<#assign orderItemContentWrapper = Static["org.ofbiz.order.order.OrderContentWrapper"].makeOrderContentWrapper(orderItem, request)>
		                    <#assign orderItemShipGrpInvResList = orderReadHelper.getOrderItemShipGrpInvResList(orderItem)>
		                    <#if orderHeader.orderTypeId == "SALES_ORDER"><#assign pickedQty = orderReadHelper.getItemPickedQuantityBd(orderItem)></#if>
	            			-->
	            			<tr>
	            				<#assign productId = itemLine.productId?if_exists/>
	            				<#assign product = itemLine.product?if_exists/>
								<td><#--${itemLine.seqId?if_exists}-->
									${itemLine_index + 1}
								</td>
		                        <#if productId?exists && productId == "shoppingcart.CommentLine">
					                <td colspan="9" valign="top">
					                  	<div><b> &gt;&gt; ${itemLine.itemDescription?if_exists}</b></div>
					                </td>
		              			<#else>
		            				<td valign="top" colspan="2">
					                  	<div>
				                  		<#if itemLine.supplierProductId?has_content>
	                                        ${orderItem.supplierProductId} - ${orderItem.itemDescription?if_exists}
	                                    <#elseif productId?exists>
	                                        <a href="<@ofbizUrl>editProduct?productId=${productId}</@ofbizUrl>">${productId}</a>  - ${itemLine.itemDescription?if_exists}
	                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
	                                            <br /><span style="color: red;">${uiLabelMap.OrderItemDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}</span>
	                                        </#if>
	                                    <#elseif itemLine.orderItemType?exists>
	                                        ${itemLine.orderItemType.description} - ${itemLine.itemDescription?if_exists}
	                                    <#else>
	                                        ${itemLine.itemDescription?if_exists}
	                                    </#if>
					                  	</div>
		               				</td>
		                			<#--<td>${itemLine.barcode?if_exists}</td>-->
		                			<td>
			                			<#if itemLine.expireDate?has_content>
			                				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(itemLine.expireDate, "dd/MM/yyyy", locale, timeZone)!}
		                				</#if>
		                			</td>
		                			<td class="align-center"><#-- Quy cach / khay -->
		                				${itemLine.packingPerTray?if_exists}
	                				</td>
		                			<td align="right" class="align-center" valign="top">
		                				${itemLine.quantityUomDescription?if_exists}
					                </td>
					                <td align="right" class="align-right" valign="top">
					                  	${itemLine.quantity?if_exists}
					                </td>
					                <td class="align-right"><#-- Tong so khay-->
						                <#if itemLine.sumTray?exists>
						                	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.sumTray, "#0.00", locale)}
			        					</#if>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Unit price -->
					                  	<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
					                </td>
					                <td align="right" class="align-right" valign="top"><#-- Adjustment -->
										<@ofbizCurrency amount=itemLine.adjustment isoCode=currencyUomId/>
					                </td>					               
					                <td align="right" class="align-right" valign="top" nowrap="nowrap"><#-- Sub total before VAT -->
				                  		<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId rounding=0/>
					                </td>
					                <#-- Unit price after VAT - new column-->
					                <#if isDisplayInvoicePrice>
									<td class="color-red align-right">
										<@ofbizCurrency amount=itemLine.invoicePrice isoCode=currencyUomId/>
									</td>
									<td class="color-red align-right">
										<@ofbizCurrency amount=itemLine.invoiceSubTotal isoCode=currencyUomId/>
									</td>
									</#if>
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
		                    <#--
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
		                                        //<a href="<@ofbizUrl>balanceInventoryItems?inventoryItemId=${orderItemShipGrpInvRes.inventoryItemId}&amp;orderId=${orderId}&amp;priorityOrderId=${orderId}&amp;priorityOrderItemSeqId=${orderItemShipGrpInvRes.orderItemSeqId}</@ofbizUrl>" class="buttontext" style="font-size: xx-small;">Raise Priority</a> 
		                                    </#if>
		                                    &nbsp;
		                                </td>
		                                <td colspan="1">&nbsp;</td>
		                            </tr>
		                        </#list>
		                    </#if>
		                    -->
		                    <#-- now show planned shipment info per line item -->
		                    <#-- now show item issuances (shipment) per line item -->
		                    <#-- now show item issuances (inventory item) per line item -->
		                    <#-- now show shipment receipts per line item -->
						</#list>
						
						<#-- display tax prices sum -->
						<#list listTaxTotal as taxTotalItem>
							<tr>
								<td align="right" class="align-right" colspan="10">
									<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
								</td>
								<td class="align-right">
									<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
										(<@ofbizCurrency amount=-taxTotalItem.amount isoCode=currencyUomId/>)
									<#elseif taxTotalItem.amount?exists>
										<@ofbizCurrency amount=taxTotalItem.amount isoCode=currencyUomId/>
									</#if>
								</td>
								<#if isDisplayInvoicePrice>
									<td></td>
	                                <td align="right" class="color-red align-right">
	                                	<#if taxTotalItem.amountForIXP?exists && taxTotalItem.amount &lt; 0>
											(<@ofbizCurrency amount=-taxTotalItem.amountForIXP isoCode=currencyUomId/>)
										<#elseif taxTotalItem.amountForIXP?exists>
											<@ofbizCurrency amount=taxTotalItem.amountForIXP isoCode=currencyUomId/>
										</#if>
	                                </td>
								</#if>
							</tr>
						</#list>
						
						<#list orderHeaderAdjustments as orderHeaderAdjustment>
			                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
			                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
			                <#if adjustmentAmount != 0>
			                    <tr>
			                        <td align="right" class="align-right" colspan="10">
			                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}</#if>
			                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}</#if>
			                            <#--<span >${adjustmentType.get("description", locale)}</span>-->
			                        </td>
			                        <td align="right" class="align-right" nowrap="nowrap">
			                        	<#if (adjustmentAmount &lt; 0)>
	                                		<#assign adjustmentAmountNegative = -adjustmentAmount>
			                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId rounding=0/>)
			                            <#else>
			                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId rounding=0/>
			                            </#if>
			                        </td>
			                        <#if isDisplayInvoicePrice>
			                        <td></td>
	                                <td align="right" class="color-red align-right">
	                                	<#if subAmountExportOrder?exists && subAmountExportOrder?has_content && (subAmountExportOrder &gt; 0)>
	                                		<#assign adjustmentAmountExportInvoice = (adjustmentAmount * (subAmountExportInvoice / subAmountExportOrder))?round>
	                                	<#else>
	                                		<#assign adjustmentAmountExportInvoice = 0/>
	                                	</#if>
	                                	<#if (adjustmentAmountExportInvoice &lt; 0)>
	                                		<#assign adjustmentAmountExportInvoiceNegative = -adjustmentAmountExportInvoice>
	                                		(<@ofbizCurrency amount=adjustmentAmountExportInvoiceNegative isoCode=currencyUomId rounding=0/>)
	                                	<#else>
	                                		<@ofbizCurrency amount=adjustmentAmountExportInvoice isoCode=currencyUomId rounding=0/>
	                                	</#if>
	                                </td>
	                                </#if>
			                    </tr>
			                </#if>
			            </#list>
						
						<#-- subtotal -->
	          			<tr>
	            			<td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.DAOrderItemsSubTotal}</b></div></td>
	            			<td align="right" class="align-right" nowrap="nowrap">
	            				<#if (orderSubTotal &lt; 0)>
                            		<#assign orderSubTotalNegative = -orderSubTotal>
                            		(<@ofbizCurrency amount=orderSubTotalNegative isoCode=currencyUomId rounding=0/>)
                            	<#else>
                            		<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=0/>
                        		</#if>
	        				</td>
	        				<#if isDisplayInvoicePrice>
	        				<td></td>
                            <td align="right" class="color-red align-right">
                            	<#if subAmountExportOrder?exists && subAmountExportOrder?has_content && (subAmountExportOrder &gt; 0)>
                            		<#assign orderSubTotalExportInvoice = (orderSubTotal * (subAmountExportInvoice / subAmountExportOrder))?round/>
                            	<#else>
                            		<#assign orderSubTotalExportInvoice = 0/>
                            	</#if>
                            	<#if (orderSubTotalExportInvoice &lt; 0)>
                            		<#assign orderSubTotalExportInvoiceNegative = -orderSubTotalExportInvoice>
                            		(<@ofbizCurrency amount=orderSubTotalExportInvoiceNegative isoCode=currencyUomId rounding=0/>)
                            	<#else>
                            		<@ofbizCurrency amount=orderSubTotalExportInvoice isoCode=currencyUomId rounding=0/>
                            	</#if>
                            </td>
                            </#if>
	          			</tr>
	          			
	          			<#-- other adjustments -->
			            <tr>
			              	<td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.DATotalOrderAdjustments}</b></div></td>
			              	<td align="right" class="align-right" nowrap="nowrap">
			              		<#if (otherAdjAmount &lt; 0)>
                            		<#assign otherAdjAmountNegative = -otherAdjAmount>
									(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId rounding=0/>)
								<#else>
									<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId rounding=0/>
								</#if>
							</td>
			              	<#if isDisplayInvoicePrice>
			              	<td></td>
                            <td align="right" class="color-red align-right">
                            	<#if subAmountExportOrder?exists && subAmountExportOrder?has_content && (subAmountExportOrder &gt; 0)>
                            		<#assign otherAdjAmountExportInvoice = (otherAdjAmount * (subAmountExportInvoice / subAmountExportOrder))?round>
                            	<#else>
                            		<#assign otherAdjAmountExportInvoice = 0>
                            	</#if>
                            	<#if (otherAdjAmountExportInvoice &lt; 0)>
                            		<#assign otherAdjAmountExportInvoiceNegative = -otherAdjAmountExportInvoice>
                            		(<@ofbizCurrency amount=otherAdjAmountExportInvoiceNegative isoCode=currencyUomId rounding=0/>)
                            	<#else>
                            		<@ofbizCurrency amount=otherAdjAmountExportInvoice isoCode=currencyUomId rounding=0/>
                            	</#if>
                            </td>
                            </#if>
			            </tr>
	          			
	          			<#-- tax adjustments -->
			          	<tr>
				            <td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderTotalSalesTax}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				            	<#if (taxAmount &lt; 0)>
                            		<#assign taxAmountNegative = -taxAmount>
				            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId rounding=0/>)
				            	<#else>
				            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=0/>
				            	</#if>
				            </td>
				            <#if isDisplayInvoicePrice>
				            <td></td>
                            <td align="right" class="color-red align-right">
                            	<#if subAmountExportOrder?exists && subAmountExportOrder?has_content && (subAmountExportOrder &gt; 0)>
                            		<#assign taxAmountExportInvoice = (taxAmount * (subAmountExportInvoice / subAmountExportOrder))?round>
                            	<#else>
                            		<#assign taxAmountExportInvoice = 0>
                            	</#if>
                            	<#if (taxAmountExportInvoice &lt; 0)>
                            		<#assign taxAmountExportInvoiceNegative = -taxAmountExportInvoice>
                            		(<@ofbizCurrency amount=taxAmountExportInvoice isoCode=currencyUomId rounding=0/>)
                            	<#else>
                            		<@ofbizCurrency amount=taxAmountExportInvoice isoCode=currencyUomId rounding=0/>
                            	</#if>
                            </td>
                            </#if>
			          	</tr>
	          			
	          			<#-- shipping adjustments -->
			          	<#if shippingAmount?exists && shippingAmount?has_content && (shippingAmount != 0)>
				          	<tr>
					            <td align="right" class="align-right" colspan="10"><div><b>${uiLabelMap.OrderTotalShippingAndHandling}</b></div></td>
					            <td align="right" class="align-right" nowrap="nowrap"><div><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=0/></div></td>
				          	</tr>
			          	</#if>
			          	
			          	<#-- grand total -->
			          	<tr>
				            <td align="right" class="align-right" colspan="10"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.DATotalAmountPayment}</b></div></td><#--uiLabelMap.OrderTotalDue-->
				            <td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
				            	<b><#if (grandTotal &lt; 0)>
                            		<#assign grandTotalNegative = -grandTotal>
				            		(<@ofbizCurrency amount=grandTotalNegative isoCode=currencyUomId rounding=0/>)
				            	<#else>
				            		<@ofbizCurrency amount=grandTotal isoCode=currencyUomId rounding=0/>
				            	</#if></b>
				            </td>
				            <#if isDisplayInvoicePrice>
				            <td></td>
                            <td align="right" class="color-red align-right">
                            	<#if subAmountExportOrder?exists && subAmountExportOrder?has_content && (subAmountExportOrder &gt; 0)>
                            		<#assign grandTotalExportInvoice = (grandTotal * (subAmountExportInvoice / subAmountExportOrder))?round>
                            	<#else>
                            		<#assign grandTotalExportInvoice = 0>
                            	</#if>
                            	<#if (grandTotalExportInvoice &lt; 0)>
                            		<#assign grandTotalExportInvoiceNegative = -grandTotalExportInvoice>
                            		(<@ofbizCurrency amount=grandTotalExportInvoiceNegative isoCode=currencyUomId rounding=0/>)
                            	<#else>
                            		<@ofbizCurrency amount=grandTotalExportInvoice isoCode=currencyUomId rounding=0/>
                            	</#if>
                            </td>
                            </#if>
			          	</tr>
			          	<#if isDisplayInvoicePrice>
				          	<tr>
								<td colspan="3" class="color-red align-right" align="right" valign="bottom" class="align-right">
									<b>${uiLabelMap.DASendToAccount}</b>
								</td>
							  	<td colspan="3" align="right" valign="bottom" class="align-right">
									<b>${uiLabelMap.DADelysAccount}</b>
								</td> 
							  	<td colspan="3" align="right" class="color-red align-right" valign="bottom">
									<b>
									<#if (grandTotalExportInvoice &lt; 0)>
	                            		<#assign grandTotalExportInvoiceNegative = -grandTotalExportInvoice>
										<@ofbizCurrency amount=grandTotalExportInvoiceNegative isoCode=currencyUomId/>
									<#else>
										<@ofbizCurrency amount=grandTotalExportInvoice isoCode=currencyUomId/>
									</#if>
									</b>
								</td>
								
							  	<td colspan="3" class="align-right"><b>${uiLabelMap.DAAccountSecond}</b></td>				  	
							  	<td colspan="3" class="color-red align-right">
							  		<b>
							  		<#if ((grandTotal - grandTotalExportInvoice) &lt; 0)>
							  			<@ofbizCurrency amount=(grandTotalExportInvoice - grandTotal) isoCode=currencyUomId/>
							  		<#else>
							  			<@ofbizCurrency amount=(grandTotal - grandTotalExportInvoice) isoCode=currencyUomId/>
							  		</#if>
							  		</b>
							  	</td>
							</tr>
						</#if>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
				<#if grandTotalExportInvoice?exists && grandTotalExportInvoice?has_content>
					<#if grandTotalExportInvoice &lt; 0>
						<#assign accountOneValue = grandTotalExportInvoiceNegative>
					<#else>
						<#assign accountOneValue = grandTotalExportInvoice>
					</#if>
					<#if ((grandTotal - grandTotalExportInvoice) &lt; 0)>
						<#assign accountTwoValue = grandTotalExportInvoice - grandTotal>
					<#else>
						<#assign accountTwoValue = grandTotal - grandTotalExportInvoice>
					</#if>
				<#else>
					<#assign accountOneValue = 0>
					<#assign accountTwoValue = 0>
				</#if>
				<input type="hidden" name="accountOneValue" id="accountOneValue" value="${accountOneValue?default(0)}"/>
				<input type="hidden" name="accountTwoValue" id="accountTwoValue" value="${accountTwoValue?default(0)}"/>
<#--Attach Payment Order-->
				<#if isChiefAccountant || isDistributor>
					<#if currentStatus.statusId?has_content && currentStatus.statusId == "ORDER_NPPAPPROVED">
						<span style="color:#F00; display:block; margin-bottom:20px">${uiLabelMap.DADistributorHadPaid}</span>
					</#if>
					<#--<#else>-->
						<#if salesMethodChannelEnumId?exists && salesMethodChannelEnumId == "SALES_GT_CHANNEL">
							<#assign hasPerPaymentOrder = isChiefAccountant && currentStatus.statusId == "ORDER_NPPAPPROVED"/>
						<#else>
							<#assign hasPerPaymentOrder = isChiefAccountant && currentStatus.statusId == "ORDER_SADAPPROVED"/>
						</#if>
						<#if currentStatus.statusId == "ORDER_SADAPPROVED" || hasPerPaymentOrder>
							<#if (isDistributor?exists && isDistributor) || hasPerPaymentOrder>
								<div class="row-fluid">
									<div class="span8">
										<form name="attachPaymentOrder" id="attachPaymentOrder">	
											<input type="hidden" name="orderId" id="orderId" value="${orderId}" />
											<input type="hidden" name="imageResize" id="imageResize" value="" />
											<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
											<h6 style="margin:0"><b>${uiLabelMap.DAAttachPaymentVouchers}</b></h6>
											<div class="widget-body">
												<div class="widget-main">
													<div class="span10">
														<#--<input type="file" id="uploadedFile" name="uploadedFile[]" multiple="multiple"/>-->
														<input multiple="" type="file" id="id-input-file-3"/>
														<label>
															<input type="checkbox" name="file-format" id="id-file-format" />
															<span class="lbl"> Allow only images</span>
														</label>
													</div>
												</div>
											</div>
											<div style="clear:both"></div>
										</form>
										<div class="span2">
											<button id="btn_attachPaymentOrder" class="btn btn-mini btn-primary" style="height: 28px;margin-top: 1px;">
									 			<i class="fa-floppy-o open-sans"></i>${uiLabelMap.CommonSave}
									 		</button>
										</div>
									</div>
									<div class="span4">
										<div class="widget-body" style="padding-top: 27px;">
											<div class="widget-main">
												<div class="row-fluid wizard-actions">
													<#if isDistributor?exists && isDistributor>
														<form action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" method="post" style="display:inline-block">
															<input type="hidden" name="orderId" value="${orderId}" />
															<input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
															<input type="hidden" name="setItemStatus" value="Y" />
															<input type="hidden" name="changeReason" value="" />
															<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
															<button class="btn btn-small btn-primary" type="submit"><i class="icon-remove open-sans"></i>${uiLabelMap.DACancelOrder}</button>
														</form>
														<form name="confirmPaymentForm" id="confirmPaymentForm" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" method="post" style="display:inline-block">
															<input type="hidden" name="orderId" value="${orderId}" />
															<input type="hidden" name="statusId" value="ORDER_NPPAPPROVED" />
															<input type="hidden" name="setItemStatus" value="Y" />
															<input type="hidden" name="changeReason" value="" />
															<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
															<button class="btn btn-small btn-primary" type="button" onClick="sendConfirmPayment();"><i class="icon-ok open-sans"></i>${uiLabelMap.DASendConfirm}</button>
														</form>
													</#if>
												</div>
											</div>
										</div>
									</div>
								</div>
							</#if>
						</#if>
					<#--</#if>-->
					<h6><b>${uiLabelMap.DAListPaymentVouchers}</b></h6>
					<div id="checkoutInfoLoader" style="overflow: hidden; position: absolute; width: 1120px; height: 640px; display: none;" class="jqx-rc-all jqx-rc-all-olbius">
						<div style="z-index: 99999; margin-left: -66px; left: 50%; top: 5%; margin-top: -24px; position: relative; width: 100px; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
							<div style="float: left;">
								<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
								<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
							</div>
						</div>
					</div>
					<div class="span12 no-left-margin" style="margin-bottom:20px">
						<div class="span7" id="display-payment-order">
							<#if paymentOrderList?has_content>
								<div style="overflow: auto; width: auto; height:auto; max-height: 200px;overflow-y: scroll;">
									<#list paymentOrderList as paymentOrder>
										<div class="itemdiv commentdiv">
											<div class="user">
												<a href="${paymentOrder.objectInfo?if_exists}" target="_blank" style="max-width:42px; max-height:42px">
													<img alt="${paymentOrder.dataResourceName?if_exists}" src="${paymentOrder.objectInfo?if_exists}" style="max-width:42px; max-height:42px" />
												</a>
											</div>
			
											<div class="body">
												<div class="name">
													<a href="${paymentOrder.objectInfo?if_exists}" target="_blank">${paymentOrder.dataResourceName?if_exists}</a>
												</div>
												<div class="text">
													<i class="icon-quote-left"></i>
													<#assign personAttachPaymentOrder = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", paymentOrder.createdByUserLogin, "compareDate", paymentOrder.createdDate, "userLogin", userLogin))/>
													${uiLabelMap.DAPersonCreate}: ${personAttachPaymentOrder.fullName?if_exists} [${paymentOrder.createdByUserLogin}]
													<div class="time" class="pull-right" style="display:inline-block; float:right; margin-right:50px">
														<i class="icon-time"></i>
														<span class="green">${paymentOrder.createdDate?string("yyyy-MM-dd HH:mm:ss.SSS")}</span>
													</div>
												</div>
											</div>
			
											<div class="tools">
												<#if !(currentStatus.statusId?has_content && currentStatus.statusId == "ORDER_NPPAPPROVED")>
													<input type="hidden" name="dataResourceId_btn_${paymentOrder_index}" value="${paymentOrder.dataResourceId?if_exists}"/>
													<input type="hidden" name="orderId_btn_${paymentOrder_index}" value="${orderId?if_exists}"/>
													<a href="javascript:void(0);" id="btn_${paymentOrder_index}" class="btn btn-minier btn-danger" onClick="removePaymentOrder('btn_${paymentOrder_index}')">
														<i class="icon-only icon-trash"></i>
													</a>
												</#if>
											</div>
										</div>
									</#list>
								</div><!--.comments-->
							<#else>
								${uiLabelMap.DANotFile}
							</#if>
						</div>
					</div><!--.span12-->
					<div style="clear:both"></div>
				</#if>
			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
</div>
<div style="display:none">
	${screens.render("component://delys/widget/sales/OrderScreens.xml#OrderPrintHtml")}
</div>
<script type="text/javascript" src="/delys/images/js/printarea/jquery.printarea.js"></script>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	function sendConfirmPayment() {
		var isSend = false;
		<#if paymentOrderList?exists && paymentOrderList?has_content && paymentOrderList?size &gt; 0>
			isSend = true;
		</#if>
		if (!isSend) {
			bootbox.dialog("${uiLabelMap.DAError}: ${uiLabelMap.DAYouNotYetAttachPaymentOrder}!", [{
				"label" : "OK",
				"class" : "btn-small btn-primary",
				}]
			);
		} else {
			document.confirmPaymentForm.submit();
		}
	}
	function removePaymentOrder(id) {
		var orderId = $('[name="orderId_' + id + '"]').val();
		var dataResourceId = $('[name="dataResourceId_' + id + '"]').val();
		var data = "orderId=" + orderId + "&dataResourceId=" + dataResourceId + "&userLoginId=${userLogin.userLoginId?if_exists}";
		$.ajax({
            type: "POST",                        
            url: "removePaymentOrderAjax",
            data: data,
            beforeSend: function () {
				$("#checkoutInfoLoader").show();
			}, 
            success: function (data) {
            	if (data.thisRequestUri == "json") {
            		var errorMessage = "";
			        if (data._ERROR_MESSAGE_LIST_ != null) {
			        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
			        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
			        	}
			        }
			        if (data._ERROR_MESSAGE_ != null) {
			        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
			        }
			        if (errorMessage != "") {
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        } else {
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			        	$("#jqxNotification").jqxNotification("open");
			        }
            	} else {
        			$("#display-payment-order").empty();
        			$("#display-payment-order").html(data);
            	}
            },
            error: function () {
                //commit(false);
            },
            complete: function() {
		        $("#checkoutInfoLoader").hide();
		    }
        });
	}
	<#--function getFileSizeandName(input){
		var tmp = input.files;
		console.log(tmp);
		if(!input.files.length){
			return false;
		}else{
			for(var i=0;i<input.files.length;i++){
				var file= input.files[i];
				$("#_uploadedFile_fileName").val(file.name);
				$("#_uploadedFile_contentType").val(file.type);
			}
		}
	}-->
</script>
<script type="text/javascript">
	$(function() {
		<#--$('#id-input-file-1 , #uploadedFile').ace_file_input({
			no_file:'${StringUtil.wrapString(uiLabelMap.DANoFile)} ...',
			btn_choose:'${StringUtil.wrapString(uiLabelMap.DAChoose)}',
			btn_change:'${StringUtil.wrapString(uiLabelMap.DAChange)}',
			droppable:false,
			onchange: null,
			thumbnail:false,
			//| true | large
			//whitelist:'gif|png|jpg|jpeg'
			//blacklist:'exe|php'
			//onchange:''
			//
		});-->
		$('#id-input-file-3').ace_file_input({
			style:'well',
			btn_choose:'Drop files here or click to choose',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			thumbnail:'small'
			//,icon_remove:null//set null, to hide remove/reset button
				/**,before_change:function(files, dropped) {
				//Check an example below
				//or examples/file-upload.html
				return true;
			},*/
			/**,before_remove : function() {
				return true;
			}*/
			,
			preview_error : function(filename, error_code) {
				//name of the file that failed
				//error_code values
				//1 = 'FILE_LOAD_FAILED',
				//2 = 'IMAGE_LOAD_FAILED',
				//3 = 'THUMBNAIL_FAILED'
				//alert(error_code);
			}
		
		}).on('change', function(){
//			console.log($(this).data('ace_input_files'));
//			console.log($(this).data('ace_input_method'));
		});
		
		//dynamically change allowed formats by changing before_change callback function
		$('#id-file-format').removeAttr('checked').on('change', function() {
			var before_change
			var btn_choose
			var no_icon
			if(this.checked) {
				btn_choose = "Drop images here or click to choose";
				no_icon = "icon-picture";
				before_change = function(files, dropped) {
					var allowed_files = [];
					for(var i = 0 ; i < files.length; i++) {
						var file = files[i];
						if(typeof file === "string") {
							//IE8 and browsers that don't support File Object
							if(! (/\.(jpe?g|png|gif|bmp)$/i).test(file) ) return false;
						}
						else {
							var type = $.trim(file.type);
							if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif|bmp)$/i).test(type) )
									|| ( type.length == 0 && ! (/\.(jpe?g|png|gif|bmp)$/i).test(file.name) )//for android's default browser which gives an empty string for file.type
								) continue;//not an image so don't keep this file
						}
						allowed_files.push(file);
					}
					if(allowed_files.length == 0) return false;
					return allowed_files;
				}
			}
			else {
				btn_choose = "Drop files here or click to choose";
				no_icon = "icon-cloud-upload";
				before_change = function(files, dropped) {
					return files;
				}
			}
			var file_input = $('#id-input-file-3');
			file_input.ace_file_input('update_settings', {'before_change':before_change, 'btn_choose': btn_choose, 'no_icon':no_icon})
			file_input.ace_file_input('reset_input');
		});
		$("#btn_attachPaymentOrder").click(function(){
			$("#btn_attachPaymentOrder").attr("disabled", true).addClass( 'ui-state-disabled' );
			for(var i=0;i<$("#id-input-file-3")[0].files.length;i++){
				var uploadedFile = $("#id-input-file-3")[0].files[i];
				var _uploadedFile_fileName= $("#id-input-file-3")[0].files[i].name;
				var _uploadedFile_contentType = $("#id-input-file-3")[0].files[i].type;
				var  form_data= new FormData();
				form_data.append("uploadedFile",uploadedFile);
				form_data.append("_uploadedFile_fileName",_uploadedFile_fileName);
	    		form_data.append("_uploadedFile_contentType",_uploadedFile_contentType);
	    		form_data.append("imageResize",null);
	    		form_data.append("ntfId",null);
	    		form_data.append("orderId", "${parameters.orderId}");
				var request = $.ajax({
					type : 'POST',
					data : form_data,
					url : 'attachPaymentOrder',
					datatype: 'json',
					async : false,
					processData: false,
		    		contentType : false,
					success : function(){
					}
				});request.done(function(){
					if(i==($("#id-input-file-3")[0].files.length-1)){
						window.location.reload();
					}
				})
			}
		})
		<#--$('#attachPaymentOrder').on('submit', function(e){
			var file = $('#uploadedFile')[0].files[0];
			if (file == undefined || file == null) {
				bootbox.dialog("${uiLabelMap.DAYouNeedChooseAFileBeforeSave}!", [{
					"label" : "OK",
					"class" : "btn-small btn-primary",
					}]
				);
				return false;
			}else{
				$("#_uploadedFile_fileName").val(file.name);
				$("#_uploadedFile_contentType").val(file.type);
				$("#btn_attachPaymentOrder").attr("disabled", true).addClass( 'ui-state-disabled' );
				$("#attachPaymentOrder").submit();
			}
		});-->
		
		$('#print-order-btn').on('click', function() {
			if ($("#print-order-content").length != undefined && $("#print-order-content").length > 0) {
				$("#print-order-content").printArea({
					mode:"iframe",  //printable window is either iframe or browser popup: "iframe","popup"
					popHt: 500,   // popup window height
					popWd: 400,  // popup window width
					popX: 500,   // popup window screen X position
					popY: 600,  //popup window screen Y position
					popTitle: 'Print order', // popup window title element
					popClose: true,  // popup window close after printing: false,true
					strict: undefined // strict or looseTransitional html 4.01 document standard or undefined to not include at all only for popup option: undefined,true,false
				});
			}
		});
	})
</script>