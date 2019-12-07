<#--
<style type="text/css">
	.print-main {
	}
	.print-title {
		text-transform:uppercase;
		font-weight:700;
		font-size:18pt;
		margin-top:10px;
		margin-bottom:10px;
		text-align:center;
	}
	.print-title-date {
		text-align:center;
		font-weight:600;
		font-style:italic;
		font-size:12pt;
		margin-top:10px;
		margin-bottom:10px
	}
	.print-content {
		margin-top:10px;
		font-size:11pt;
	}
	.print-content-middle {
		margin-top:10px
	}
	.table-list {
		width:100%;
	}
	.table-list thead tr {
		border-color:black;
		background-color:#FFFF99;
		font-size:8pt;
		text-transform:uppercase
	}
	.table-list thead tr th {
		padding:2mm 1mm;
		text-align:center;
		border:1pt solid black;
		number-rows-spanned:2;
		font-weight:bold
	}
	.table-list tbody tr td {
		border:1pt solid black;
		padding:5px;
	}
	.width33{
		width: 33% !important;
	}
</style>

-->
<div id="print-order-content" class="print-main" style="margin:1cm;font-family:Arial">
	<#--<div style="display:inline-table;width:50%;"></div>
	<div style="display:inline-table;width:50%;"></div>
	<div style="display:inline-table;width:50%;">
		<div style="display:inline-rows">
			<div style="display:inline-table; width: 120px!important;">
				${uiLabelMap.DAOrderNumber}:
			</div>
			<div style="inline-table">${orderHeader.orderId}</div>
		</div>
		<div style="display:inline-rows">
			<div style="display:inline-table; width: 120px!important;">
				${uiLabelMap.DACreateOrderDate}:
			</div>
			<div style="inline-table"><#if orderHeader.orderDate?exists>
			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
			</#if></div>
		</div>
		<div style="display:inline-rows">
			<div style="display:inline-table; width: 120px!important;">
				<div>${uiLabelMap.DADesiredDeliveryDate2}:</div>
			</div>
			<div style="inline-table"><#if desiredDeliveryDate?exists>
			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
			</#if></div>
		</div>
	</div>-->
	<table style="font-size:10pt;">
		<tr>
			<td style="width:37%;padding:2mm 1mm;vertical-align:top"></td>
			<td style="width:33%;padding:2mm 1mm;vertical-align:top"></td>
			<td style="width:31%;padding:2mm 1mm;vertical-align:top">
				<div>${uiLabelMap.DAOrderNumber}: ${orderHeader.orderId}</div>
				<div>
					${uiLabelMap.DACreateOrderDate}: 
					<#if orderHeader.orderDate?exists>
						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
					</#if>
				</div>
				<div>
					${uiLabelMap.DADesiredDeliveryDate2}: 
					<#if desiredDeliveryDate?exists>
						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
					</#if>
				</div>
			</td>
		</tr>
	</table>
	<div class="print-header">
		<div class="print-title" style="text-transform:uppercase;font-weight:700;font-size:18pt;margin-top:10px;margin-bottom:10px;text-align:center;">
			${uiLabelMap.DASalesOrderFormTitle}
		</div>
		<div class="print-title-date" style="text-align:center;font-weight:600;font-style:italic;font-size:12pt;margin-top:10px;margin-bottom:10px">
			${uiLabelMap.DPMonth} <#if orderDateTime.get(2) &lt; 9>0${orderDateTime.get(2) + 1}<#else>${orderDateTime.get(2) + 1}</#if> ${uiLabelMap.DPYearLowercase} ${orderDateTime.get(1)}
		</div>
	</div>
	<div class="print-content" style="margin-top:10px;font-size:11pt;">
		<div class="print-content-top">
			<#--<div class="row-fluid">
				<div class="" style="display:inline-table;width:70%;">
					<div class="" style="display:inline-rows">
						<div style="display:inline-table; width:10%!important;">${uiLabelMap.DADistributor}:</div>
						<div style="display:inline-table">
							<b style="">${displayPartyNameResult?if_exists}</b>
						</div>
					</div>
					<div class=""  style="display:inline-rows">
						<div class=''  style="display:inline-table; width: 10%!important;">
							${uiLabelMap.DAArea}:
						</div>
						<div class=''  style="display:inline-table"><b>${areaGeoName?if_exists}</b></div>
					</div>
					<div style="display:inline-rows">
						<div style="display:inline-table; width: 10%!important;">
							${uiLabelMap.DAAddress}:
						</div>
						<div style="display:inline-table">
						<b>
							<#list listAddressCust as addressCust>
								${addressCust}
							</#list>
						</b>
						</div>
					</div>
				</div>
				<div style="display:inline-table;width:30%;">
					<div style="display:inline-rows">
						<div style="display:inline-table; width: 10%!important;">
							${uiLabelMap.DASUP}:
						</div>
						<div style="display:inline-table">${displaySUPsNameResult}</div>
					</div>
				</div>
			</div>-->
			<table style="font-size:10pt;">
				<tr>
					<td style="width:60%;padding:2mm 15mm;vertical-align:top">
						<div>${uiLabelMap.DADistributor}: <b>${displayPartyNameResult?if_exists}</b></div>
						<div>${uiLabelMap.DAArea}: <b>${areaGeoName?if_exists}</b></div>
						<div>
							${uiLabelMap.DAAddress}: 
							<b>
								<#list listAddressCust as addressCust>
									${addressCust}
								</#list>
							</b>
						</div>
					</td>
					<td style="width:40%;padding:2mm 5mm;vertical-align:top">
						${uiLabelMap.DASUP}: ${displaySUPsNameResult}
					</td>
				</tr>
			</table>
		</div>
		<div class="print-content-middle" style="margin-top:10px">
			<table class="table-list" style="border-color:black;border-style:solid;border-width:1pt;width:100%;font-size:10pt;">
				<thead>
					<tr style="border-color:black;background-color:#FFFF99;font-size:8pt;text-transform:uppercase">
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DANo}</th>
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAProductName}</th>
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAPackingPerTray}</th>
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAQuantityUomId}</th>
						<th colspan="3" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAQuantity}</th>
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DASumTray}</th>
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAPrice} ${uiLabelMap.DAParenthesisBeforeVAT}</th>
						<th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DASubTotalPrice} ${uiLabelMap.DAParenthesisBeforeVAT}</th>
						<#-- <th rowspan="2" style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAPrice} ${uiLabelMap.DAParenthesisAfterVAT}</th> -->
					</tr>
					<tr style="border-color:black;background-color:#FFFF99;font-size:8pt;text-transform:uppercase">
						<th style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAOrdered}</th>
						<th style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DAPromos}</th>
						<th style="background-color:#FFFF99;padding:2mm 1mm;text-align:center;border:1pt solid black;number-rows-spanned:2;font-weight:bold">${uiLabelMap.DASum}</th>
					</tr>
				</thead>
				<tbody>
					<#list listItemLine as itemLine>
						<#assign productId = itemLine.productId?if_exists/>
	    				<#assign product = itemLine.product?if_exists/>
						<tr>
							<td class="align-center" style="border:1pt solid black;padding:5px;">${itemLine_index + 1}</td>
							<td style="border:1pt solid black;padding:5px;">${itemLine.productName?if_exists}</td>
							<td class="align-center" style="border:1pt solid black;padding:5px;">${itemLine.packingPerTray?if_exists}</td>
							<td class="align-center" style="border:1pt solid black;padding:5px;">${itemLine.quantityUomDescription?if_exists}</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">${itemLine.quantity?if_exists}</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">&nbsp;</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">${itemLine.quantity?if_exists}</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">
								<#if itemLine.sumTray?exists>
				                	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.sumTray, "#0.00", locale)}
	        					</#if>
							</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">
								<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
							</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">
								<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId rounding=0/>
							</td>
							<#--<td class="align-right" style="border:1pt solid black;padding:5px;">
								<@ofbizCurrency amount=itemLine.unitPriceAfVAT isoCode=currencyUomId rounding=0/>
							</td>-->
						</tr>
					</#list>
					
					<#-- display tax prices sum -->
					<#list listTaxTotal as taxTotalItem>
						<tr>
							<td colspan="10" class="align-right" style="border:1pt solid black;padding:5px;">
								<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
							</td>
							<td class="align-right" style="border:1pt solid black;padding:5px;">
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
		                        <td colspan="10" class="align-right" style="border:1pt solid black;padding:5px;">
		                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} </#if>
		                            <#--${adjustmentType.get("description", locale)}-->
		                            <#if orderHeaderAdjustment.comments?has_content> ${orderHeaderAdjustment.comments}</#if>
		                        </td>
		                        <td class="align-right" style="border:1pt solid black;padding:5px;">
	                        		<#if (adjustmentAmount &lt; 0)>
	                            		<#assign adjustmentAmountNegative = -adjustmentAmount>
		                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId rounding=0/>)
		                            <#else>
		                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId rounding=0/>
		                            </#if>
		                        </td>
		                    </tr>
		                </#if>
		            </#list>
		            
		            <#-- subtotal -->
	      			<tr>
	        			<td colspan="10" class="align-right" style="border:1pt solid black;padding:5px;">
        					${uiLabelMap.DAOrderItemsSubTotal}
	        			</td>
	        			<td class="align-right" style="border:1pt solid black;padding:5px;">
        					<#if (orderSubTotal &lt; 0)>
	                    		<#assign orderSubTotalNegative = -orderSubTotal>
	                    		(<@ofbizCurrency amount=orderSubTotalNegative isoCode=currencyUomId rounding=0/>)
	                    	<#else>
	                    		<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=0/>
	                		</#if>
	    				</td>
	      			</tr>
	      			
	      			<#-- other adjustments -->
		            <tr>
		              	<td colspan="10" class="align-right" style="border:1pt solid black;padding:5px;">
	              			${uiLabelMap.DATotalOrderAdjustments}
		              	</td>
		              	<td class="align-right" style="border:1pt solid black;padding:5px;">
	              			<#if (otherAdjAmount &lt; 0)>
	                    		<#assign otherAdjAmountNegative = -otherAdjAmount>
								(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId rounding=0/>)
							<#else>
								<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId rounding=0/>
							</#if>
						</td>
		            </tr>
		            
	      			<#-- tax adjustments -->
		          	<tr>
			            <td colspan="10" class="align-right" style="border:1pt solid black;padding:5px;">
							${uiLabelMap.OrderTotalSalesTax}
						</td>
			            <td class="align-right" style="border:1pt solid black;padding:5px;">
			            	<#if (taxAmount &lt; 0)>
	                    		<#assign taxAmountNegative = -taxAmount>
			            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId rounding=0/>)
			            	<#else>
			            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=0/>
			            	</#if>
			            </td>
		          	</tr>
	      			
	      			<#-- shipping adjustments -->
	      			<#if shippingAmount?exists && shippingAmount?has_content && (shippingAmount != 0)>
	      				<tr>
			              	<td colspan="10" class="align-right" style="border:1pt solid black;padding:5px;">
		              			${uiLabelMap.OrderTotalShippingAndHandling}
			              	</td>
			              	<td class="align-right" style="border:1pt solid black;padding:5px;">
			              		${shippingAmount}
		              			<#if (shippingAmount &lt; 0)>
		                    		<#assign shippingAmountNegative = -shippingAmount>
									(<@ofbizCurrency amount=shippingAmountNegative isoCode=currencyUomId rounding=0/>)
								<#else>
									<@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=0/>
								</#if>
							</td>
			            </tr>
		          	</#if>
	      			
		          	<#-- grand total -->
		          	<tr>
			            <td class="align-right" style="font-weight:bold;text-transform:uppercase;border:1pt solid black;padding:5px;" colspan="10">
		             		${uiLabelMap.DATotalAmountPayment}
		             	</td>
			            <td class="align-right" style="font-weight:bold;border:1pt solid black;padding:5px;">
		            		<#if (grandTotal &lt; 0)>
	                    		<#assign grandTotalNegative = -grandTotal>
			            		(<@ofbizCurrency amount=grandTotalNegative isoCode=currencyUomId rounding=0/>)
			            	<#else>
			            		<@ofbizCurrency amount=grandTotal isoCode=currencyUomId rounding=0/>
			            	</#if>
			            </td>
		          	</tr>
				</tbody>
			</table>
			<div style="margin-top:10px;">${uiLabelMap.DADistributorDelegate}</div>
		</div>
	</div>
</div>
