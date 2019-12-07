<#include "script/purchaseDeliveryDetailScript.ftl"/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			${currentStatus?if_exists}
		</div>
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.BLPurchaseReceiptDelivery)?upper_case}
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
									<span><a href="javascript:viewDetailOrder('${delivery.orderId?if_exists}')" onclick="">${delivery.orderId?if_exists}</a></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Supplier)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if supplierCode?has_content>
											${supplierCode?if_exists}
										<#else>
											${supplierId?if_exists}
										</#if>
										-
										${supplierName?if_exists}
									 </span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BPOCurrencyUomId)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${currencyUomId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Address)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${supplierAddress?if_exists}</span>
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
									</#if>
								</#if>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.LOGReceiveNoteId)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${deliveryId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.ReceiveToFacility)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if destFacility.facilityCode?has_content>
											${destFacility.facilityCode?if_exists} - ${destFacility.facilityName?if_exists} 
										<#else>
											${destFacility.facilityId?if_exists} - ${destFacility.facilityName?if_exists}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Address)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${destAddress?if_exists}</span>
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
									<span>${StringUtil.wrapString(uiLabelMap.ReceiveDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span><#if delivery.actualArrivalDate?exists>${delivery.actualArrivalDate?datetime?string('dd/MM/yyyy HH:mm')}</#if></span>
								</div>
							</div>
                            <div class="row-fluid">
                                <div class="span4">
                                    <span>${StringUtil.wrapString(uiLabelMap.BACCConversionFactor)}:</span>
                                </div>
                                <div class="span8 align-left">
                                    <span><@ofbizCurrency amount=delivery.conversionFactor?default(0.00) isoCode='VND'/></span>
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
					<td width="7%">${StringUtil.wrapString(uiLabelMap.BSPurchaseUomId)}</td>
					<td width="5%">${StringUtil.wrapString(uiLabelMap.BLPackingForm)}</td>
				  	<td width="7%">${StringUtil.wrapString(uiLabelMap.BLPurchaseQtySum)}</td>
				  	<td width="7%">${StringUtil.wrapString(uiLabelMap.BLQuantityEATotal)}</td>
				  	<td width="7%">${StringUtil.wrapString(uiLabelMap.ActualDeliveredQuantitySum)}</td>
				  	<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
					  	<td width="10%" class="align-center">${uiLabelMap.UnitPrice} </br> ${uiLabelMap.DAParenthesisBeforeVAT}</td>
						<td width="15%" class="align-center">${uiLabelMap.DAItemTotal} </br> ${uiLabelMap.DAParenthesisBeforeVAT}</td>
					</#if>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#if listItem?has_content>
					<#list listItem as item>
						<#assign i = i + 1>
						<#if item.isPromo?has_content && item.isPromo == 'Y'>
						<tr class="background-promo">
						<#else>
						<tr>
						</#if>
				            <td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productCode?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productName?if_exists)}<#if item.isPromo == "Y"> <i class="fa-gift"></i> </#if></td>
				            <td class="align-left">
				            	<#if item.isPromo?has_content && item.isPromo == 'Y'>
				            		${StringUtil.wrapString(uiLabelMap.LogYes)}
			            		<#else>
			            			${StringUtil.wrapString(uiLabelMap.LogNO)}
			            		</#if>
		            		</td>
		            		<td align="center">${StringUtil.wrapString(item.unit?if_exists)}</td>
		            		<td class="align-right">
   								<#if item.isKg?has_content && item.isKg == 'Y'>
		   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(1, "#,##0.00", locale)}
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
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists, "#,##0.00", locale)}
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
				            	<#if item.actualDeliveredQuantity?has_content>
				   					<#if item.actualDeliveredQuantity &gt; 0>
					   					<#if item.isKg?has_content && item.isKg == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0.000", locale)}
					   					<#else>
					   						<#-- <#if item.convertNumber?exists>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists/item.convertNumber?if_exists, "#,##0", locale)}
					   						<#else>
					   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0", locale)}
					   						</#if>
					   						-->
					   						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0", locale)}
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
					            	<#if item.unitPrice?has_content>
					            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
					            	<#else>
			   							<span>0</span>
				   					</#if>
			   					</td>
					            <td class="align-right">
					            	<#if item.total?has_content>
					            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.total?if_exists, "#,##0.00", locale)}
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
		          		<#assign grandTotalByDelivery = itemPriceTotal + taxTotalByDelivery>
			          	<tr>
				            <td class="align-right" colspan="10"><b>${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}</b></td>
				            <#if itemPriceTotal?has_content>
				            	<td class="align-right" colspan="1"><b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemPriceTotal?if_exists, "#,##0.00", locale)}</b></td>
				            <#else>
								<span>0</span>
		   					</#if>
			            <tr>
			            <#if allOrderAdjustmentsPromoDelivery?has_content>
							<#list allOrderAdjustmentsPromoDelivery as objAdj>
								<#assign grandTotalByDelivery = grandTotalByDelivery + objAdj.amount?if_exists>
								<tr>
									<td class="align-right" colspan="10">
										<span><b>${StringUtil.wrapString(objAdj.promoName)}</b></span>
									</td>
									<td class="align-right">
										<span><b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(objAdj.amount, "#,##0.00", locale)}</b></span>
									</td>
								</tr>
							</#list>	
						</#if>
						<#list listTaxTotals as taxTotalItem>
							<tr>
								<td class="align-right" colspan="10">
									<span><b><#if taxTotalItem.description?exists>${taxTotalItem.description?if_exists}</#if></b></span>
								</td>
								<td class="align-right">
									<#if "DLV_CANCELLED" != statusId>
										<span><b> 
											<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
												${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(-taxTotalItem.amount, "#,##0.00", locale)}
											<#elseif taxTotalItem.amount?exists>
												${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalItem.amount, "#,##0.00", locale)}
											</#if>
										</b></span>
						    		<#else>
						        		<span><b>0</b></span>
						    		</#if>
								</td>
							</tr>
						</#list>
						<#if taxDiscountTotal?has_content && taxDiscountTotal != 0>
							<#assign grandTotalByDelivery = grandTotalByDelivery + taxDiscountTotal>
							<tr>
								<td align="right" class="align-right" colspan="10">
									<span><b> ${StringUtil.wrapString(uiLabelMap.BPTaxDiscountTotal)} </b>
								</td>
								<td class="align-right">
									<span><b> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxDiscountTotal, "#,##0.00", locale)} </b>
								</td>
							</tr>
						</#if>
							<tr>
							    <td class="align-right" colspan="10">
							    	<span ><b>${StringUtil.wrapString(uiLabelMap.TotalAmountPay)?upper_case}: 
								    	<#if "DLV_CANCELLED" != statusId>
								        	<#assign abc = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalByDelivery, "#,##0.00", locale)>
									        <#if locale == "vi">
								        		<#assign stringTotal = abc.replaceAll("\\.", "")>
								        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(grandTotalByDelivery, currencyUomId, delegator)>
								        		<span ><b><i>${totalDlvString?if_exists}</i></b></span>
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
							    <td class="align-right">
							        <#if "DLV_CANCELLED" != statusId>
								        <span> 	
								        	<b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalByDelivery, "#,##0.00", locale)} (${currencyUomId?if_exists})</b>
							        	</span> 
									<#else>
							    		<span><b>0</b> 	
										</span>
									</#if>
							    </td>
							</tr>
						</#if>
					</#if>
			</tbody>
		</table>
	</div><!--.form-horizontal-->
</div>
<#include "popupEditDelivery.ftl"/>
