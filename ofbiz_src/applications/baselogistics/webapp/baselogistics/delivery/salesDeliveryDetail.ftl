<#include "script/salesDeliveryDetailScript.ftl"/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			${currentStatus?if_exists}
		</div>
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.DeliveryAndExportNote)}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom span-text-left content-description">
					<div class="row-fluid margin-top20">
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.OrderId)}:</span>
								</div>
								<div class="span8 align-left">
									<span><a href="javascript:SalesDlvDetailObj.viewDetailOrder('${delivery.orderId?if_exists}')" onclick="">${delivery.orderId?if_exists}</a></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CustomerId)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${partyTo.partyCode?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CustomerName)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${partyToFullName?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Address)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${customerAddress?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.PhoneNumber)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${phoneCustomer?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span><b>${StringUtil.wrapString(uiLabelMap.ShippingAddress)?upper_case}:</b> </span>
								</div>
								<div class="span8 align-left">
									<span>
										${StringUtil.wrapString(shippingAddress?if_exists)}
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span><b>${StringUtil.wrapString(uiLabelMap.DatetimeDelivery)?upper_case}:</b></span>
								</div>
								<div class="span8 align-left">
								<#if orderHeader?has_content>
									<#if orderHeader.shipAfterDate?exists && orderHeader.shipBeforeDate?exists>
										<span>${orderHeader.shipAfterDate?datetime?string('dd/MM/yyyy HH:mm')} ${uiLabelMap.LogTo} ${orderHeader.shipBeforeDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
									<#else>
										<span>${orderHeader.estimatedDeliveryDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
									</#if>
								</#if>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.DeliveryId)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${deliveryId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.ExportFromFacility)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if originFacility.facilityCode?has_content>
											${originFacility.facilityCode?if_exists} - ${originFacility.facilityName?if_exists}
										<#else>
											${originFacility.facilityId?if_exists} - ${originFacility.facilityName?if_exists}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Address)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${originAddress?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Saller)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${partySallerFullName?if_exists} <#if phoneSaller?has_content> - ${phoneSaller?if_exists}</#if></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CreatedDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${delivery.createDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.ExportDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span><#if delivery.actualStartDate?exists>${delivery.actualStartDate?datetime?string('dd/MM/yyyy HH:mm')}</#if></span>
								</div>
							</div>
							<#if delivery.shipmentDistributorId?has_content && hasDistributorRole == true>
								<div class="row-fluid">
									<div class="span4">
										<span><b>${StringUtil.wrapString(uiLabelMap.PurchaseShipment)}:</b> 
									</div>
									<div class="span8 align-left">	
										<span><a href="javascript:SalesDlvDetailObj.viewShipmentPurchaseDis('${delivery.shipmentDistributorId?if_exists}')" onclick="">${delivery.shipmentDistributorId?if_exists}</a></span>
									</div>
								</div>
							</#if>
							<div class="row-fluid">
								<div class="span4">
									<span><b>${StringUtil.wrapString(uiLabelMap.Notes)}:</b> 
								</div>
								<div class="span8 align-left">	
									${shippingInstructions?if_exists}</span>
								</div>
							</div>
						</div>
						<div class="span6">
						</div>
					</div>
				</div><!-- .form-horizontal -->
			</div><!--.row-fluid-->	
		</div><!--.widget-main-->
	</div><!--.widget-body-->
	<div class="form-horizontal basic-custom-form">
		<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
			<thead>
				<tr style="font-weight: bold;">
					<td width="3%">${StringUtil.wrapString(uiLabelMap.SequenceId)}</td>
					<td width="8%">${StringUtil.wrapString(uiLabelMap.LogMaHang)}</td>
					<td width="25%">${StringUtil.wrapString(uiLabelMap.LogTenHangHoa)}</td>
					<td width="8%">${StringUtil.wrapString(uiLabelMap.IsPromo)}</td>
					<td width="7%">${StringUtil.wrapString(uiLabelMap.Unit)}</td>
					<td width="5%">${StringUtil.wrapString(uiLabelMap.BLPackingForm)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.BLSalesQtySum)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.ActualDeliveryQuantitySum)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.ActualDeliveredQuantitySum)}</td>
				  	<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</td>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.DAAdjustment)}</td>
					<td width="10%">${StringUtil.wrapString(uiLabelMap.ApTotal)}</td>
					</#if>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#if listItemTotal?has_content>
					<#list listItemTotal as item>
						<#assign i = i + 1>
						<#if item.isPromo?has_content && item.isPromo == 'Y'>
						<tr class="background-promo">
						<#else>
						<tr>
						</#if>
				            <td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productCode?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productName?if_exists)}</td>
				            <td class="align-left">
				            	<#if item.isPromo?has_content && item.isPromo == 'Y'>
				            		${StringUtil.wrapString(uiLabelMap.LogYes)}
			            		<#else>
			            			${StringUtil.wrapString(uiLabelMap.LogNO)}
			            		</#if>
		            		</td>
				            <td align="center">${StringUtil.wrapString(item.unit?if_exists)}</td>
				            <td class="align-right">
				            	<#if item.selectedAmount?has_content>
		   							<#if item.selectedAmount &gt; 0>
		   								<#if item.isKg?has_content && item.isKg == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.selectedAmount?if_exists, "#,##0.00", locale)}
					   					<#else>
					   						<span></span>
					   					</#if>
			   						<#else>
			   							<span></span>
			   						</#if>
		   						<#else>
		   							<#if item.convertNumber?exists>
			   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.convertNumber?if_exists, "#,##0", locale)}
			   						<#else>
			   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(1, "#,##0", locale)}
			   						</#if>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.quantity?has_content>
		   							<#if item.quantity &gt; 0>
		   								<#if item.isKg?has_content && item.isKg == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists, "#,##0.00", locale)}
					   					<#else>
					   						<#if item.convertNumber?exists>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists/item.convertNumber?if_exists, "#,##0", locale)}
					   						<#else>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists, "#,##0", locale)}
					   						</#if>
					   					</#if>
			   						<#else>
			   							<span>0</span>
			   						</#if>
		   						<#else>
		   							<span>0</span>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.quantity?has_content>
		   							<#if item.quantity &gt; 0>
		   								<#if item.isKg?has_content && item.isKg == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists*item.selectedAmount?if_exists, "#,##0.00", locale)}
					   					<#else>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists, "#,##0", locale)}
					   					</#if>
			   						<#else>
			   							<span>0</span>
			   						</#if>
		   						<#else>
		   							<span>0</span>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.actualExportedQuantity?has_content>
				   					<#if item.actualExportedQuantity &gt; 0>
					   					<#if item.isKg?has_content && item.isKg == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists*item.selectedAmount?if_exists, "#,##0.00", locale)}
					   					<#else>
					   						<#if item.convertNumber?exists>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists/item.convertNumber?if_exists, "#,##0", locale)}
					   						<#else>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}
					   						</#if>
					   					</#if>
				   					<#else>
			   							<span>0</span>
				   					</#if>
			   					<#else>
		   							<span>0</span>
			   					</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.actualDeliveredQuantity?has_content>
				   					<#if item.actualDeliveredQuantity &gt; 0>
					   					<#if item.isKg?has_content && item.isKg == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists*item.selectedAmount?if_exists, "#,##0.00", locale)}
					   					<#else>
					   						<#if item.convertNumber?exists>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists/item.convertNumber?if_exists, "#,##0", locale)}
					   						<#else>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0", locale)}
					   						</#if>
					   					</#if>
				   					<#else>
			   							<span>0</span>
				   					</#if>
		   						<#else>
		   							<span>0</span>
			   					</#if>
				            </td>
				            <#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
                                <td class="align-right">
                                    <#if item.isPromo?has_content && item.isPromo == 'Y'>
                                        ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
                                    <#else>
                                        <#if item.unitPrice?has_content>
                                            ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
                                        <#else>
                                            ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##0.00", locale)}
                                        </#if>
                                    </#if>
                                </td>
                                <td class="align-right">
                                    <#if item.isPromo?has_content && item.isPromo == 'Y'>
                                        <#if item.actualDeliveredQuantity &gt; 0>
                                            ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber((-1)*item.unitPrice?if_exists*item.actualDeliveredQuantity?if_exists, "#,##0.00", locale)}
                                        <#else>
                                            <#if item.actualExportedQuantity &gt; 0>
                                                ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber((-1)*item.unitPrice?if_exists*item.actualExportedQuantity?if_exists, "#,##0.00", locale)}
                                            <#else>
                                                ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber((-1)*item.unitPrice?if_exists*item.quantity?if_exists, "#,##0.00", locale)}
                                            </#if>
                                        </#if>
                                    <#else>
                                        ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##0.00", locale)}
                                    </#if>
                                </td>

                                <td class="align-right">
                                    <#if item.itemTotal?has_content>
                                        ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.itemTotal?if_exists, "#,##0.00", locale)}
                                    <#else>
                                        <span>0</span>
                                    </#if>
                                </td>
                            </#if>
			          	</tr>
		          	</#list>
	          	</#if>
	          	<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
	          	<#if statusId != 'DLV_CANCELLED'>
					<#list listTaxTotals as taxTotalItem>
						<tr>
							<td class="align-right" colspan="12">
								<span><#if taxTotalItem.description?exists>${taxTotalItem.description?if_exists}</#if></span>
							</td>
							<td class="align-right">
								<#if "DLV_CANCELLED" != statusId>
									<span> 
										<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(-taxTotalItem.amount, "#,##0.00", locale)}
										<#elseif taxTotalItem.amount?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalItem.amount, "#,##0.00", locale)}
										</#if>
									</span>
					    		<#else>
					        		<span>0</span>
					    		</#if>
							</td>
						</tr>
					</#list>
                    <#list orderHeaderAdjustments as orderHeaderAdjustment>
                        <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
                        <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
                        <#if adjustmentAmount != 0>
                        <tr>
                            <td class="align-right" colspan="12">
                                <#assign adjPrinted = false>
                                <#if orderHeaderAdjustment.comments?has_content>
                                ${orderHeaderAdjustment.comments}
                                    <#assign adjPrinted = true>
                                </#if>
                                <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}
                                    <#assign adjPrinted = true>
                                </#if>
                                <#if !adjPrinted><span>${adjustmentType.get("description", locale)}</span></#if>
                            </td>
                            <td class="align-right">
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
					
					<tr>
			            <td class="align-right" colspan="12"><b>${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)?upper_case}</b></td>
			            <#if total?has_content>
			            	<td class="align-right" colspan="1"><b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(total?if_exists, "#,##0.00", locale)}</b></td>
			            <#else>
							<span>0</span>
	   					</#if>
		            <tr>
		            <#if (taxTotalByDelivery &lt; 0)>
				        <tr>
				    		<td class="align-right" colspan="12">
				    			<span><b>${StringUtil.wrapString(uiLabelMap.BSTotalSalesTax)?upper_case}</b></span>
				            </td>
				            <td class="align-right">
				                <#if "DLV_CANCELLED" != statusId>
				                    <span><b>
				                        <#assign taxAmountNegative = -taxTotalByDelivery>
				                        ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxAmountNegative, "#,##0.00", locale)}
				                    </b></span>
				        		<#else>
					        		<span><b>0</b></span>
				        		</#if>
				            </td>
				        </tr>
					<#else>
						<tr>
				    		<td class="align-right" colspan="12">
				    			<span ><b>${StringUtil.wrapString(uiLabelMap.BSTotalSalesTax)?upper_case}</b></span>
				            </td>
				            <td class="align-right">
				                <#if "DLV_CANCELLED" != statusId>
				                    <span> 
				                    	<b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalByDelivery, "#,##0.00", locale)}</b>
				                    </span>
				        		<#else>
					        		<span> <b>0</b>	
				        			</span>
				        		</#if>
				            </td>
				        </tr>
					</#if>
					<tr>
					    <td class="align-right" colspan="12">
					    	<span ><b>${StringUtil.wrapString(uiLabelMap.DATotalOrderAdjustments)?upper_case}
					    	</b></span>
					    </td>
					    <td class="align-right">
					        <#if "DLV_CANCELLED" != statusId>
						        <span>
                                    <b>
                                    <#if (otherAdjAmount &lt; 0)>
                                        <#assign otherAdjAmountNegative = -otherAdjAmount>
                                        (<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId/>)
                                    <#else>
                                        <@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
                                    </#if>
                                    </b>
					        	</span> 
							<#else>
					    		<span><b>0</b> 	
								</span>
							</#if>
					    </td>
					</tr>
					<tr>
					    <td class="align-right" colspan="12">
					    	<span ><b>${StringUtil.wrapString(uiLabelMap.TotalAmountPay)?upper_case}
					    	</b></span>
					    </td>
					    <td class="align-right">
					        <#if "DLV_CANCELLED" != statusId>
						        <span> 	
						        	<b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal, "#,##0.00", locale)}</b>
					        	</span> 
							<#else>
					    		<span><b>0</b> 	
								</span>
							</#if>
					    </td>
					</tr>
					<tr>
					    <td class="align-right" colspan="13">
					    	<span ><b>${StringUtil.wrapString(uiLabelMap.ByString)?upper_case}: 
						    	<#if "DLV_CANCELLED" != statusId>
						        	<#assign abc = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal, "#,##0.00", locale)>
							        <#if locale == "vi">
						        		<#assign stringTotal = abc.replaceAll("\\.", "")>
						        		<#if (grandTotal &lt; 0)>
						        			<#assign grandTotalByDeliver = -grandTotal>
						        		</#if>
						        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(grandTotal)>
						        		<#if (grandTotal &lt; 0)>
						        			<span ><b><i>${uiLabelMap.Negate} ${totalDlvString?if_exists}</i></b></span>
					        			<#else>
						        			<span ><b><i>${totalDlvString?if_exists}</i></b></span>
						        		</#if>
						        	<#elseif locale == "en">
						        		<#assign stringTotal = abc.replaceAll("\\,", "")>
						        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].changeToWords(stringTotal, true)>
						        		<span ><b>${totalDlvString?if_exists}</b></span>
						        	</#if>
								<#else>
						    		<span> 	
									</span>
								</#if>
					    	</b></span>
					    </td>
					</tr>
				</#if>
			</tbody>
		</table>
		<#if statusId != 'DLV_CANCELLED'>
			<table>
				<tbody>
			 		<td>
			 			<span><b>${StringUtil.wrapString(uiLabelMap.BLProductPromotion)?upper_case}:</b></span>
					</td>
					<td>
						<span>
							<#if discountAmountTotal?has_content && discountAmountTotal &gt; 0 >
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(discountAmountTotal, "#,##0.00", locale)}
							</#if>
						</span>
					</td>
					<#assign checkShipCharges = false>
					<#list orderHeaderAdjustments as x>
						<#if x.orderAdjustmentTypeId?has_content && x.orderAdjustmentTypeId== "SHIPPING_CHARGES">
							<#assign shippingChargeAmount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(x.amount.abs()?if_exists, orderHeader.currencyUom?if_exists?default('VND'), locale)>
							<#assign checkShipCharges = true>
						</#if>
					</#list>
					<#if checkShipCharges == true>
						<td>
							<span>${StringUtil.wrapString(uiLabelMap.TransportCost)?upper_case}:</span>
						</td>
						<td>
							<span font-weight="bold" text-align="left" font-size="12" margin-top="-2px">
								${shippingChargeAmount?if_exists}
							</span>
						</td>
					<#else>
						<td>
							<span>
							</span>
						</td>
						<td>
							<span>
							</span>
						</td>
					</#if>
				 </tbody>
			</table>
			<span><b>${StringUtil.wrapString(uiLabelMap.PromotionUpperCase)?upper_case}:</b></br></span>
			<#if allOrderAdjustmentsPromoDelivery?exists && allOrderAdjustmentsPromoDelivery?has_content>
				<#assign tmp = 0>
				<#list allOrderAdjustmentsPromoDelivery as objAdj>
					<#assign tmp = tmp + 1>
					<#if objAdj.amount &lt; 0>
						<#assign stringTotalNagative = - objAdj.amount>
						<span class="red"> - ${objAdj.promoName}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(stringTotalNagative, "#,##0.00", locale)}</span>
					<#else>
						<span class="red"> - ${objAdj.promoName}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(objAdj.amount, "#,##0.00", locale)}</span>
					</#if>
					<#if tmp &lt; allOrderAdjustmentsPromoDelivery?size>
						</br>
					</#if>
				</#list>
			</#if>
			<br>
			<#assign labelPm = uiLabelMap.PaymentMethod>
			<span><b>${StringUtil.wrapString(labelPm)?upper_case}: ${paymentMethodDescription?if_exists?upper_case}</b></span>
		</#if>
		</#if>
	</div><!--.form-horizontal-->
</div>
<#include "popupEditDelivery.ftl"/>
