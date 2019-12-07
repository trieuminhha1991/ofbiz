<script>
	function viewOrderDetail(orderId){
		window.location.href = 'viewOrder?orderId=' + orderId;
	}
</script>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			<#assign descStatus = StringUtil.wrapString(status.get('description', locale))>
			${descStatus?if_exists}
		</div>
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.PurchaseShipment)?upper_case}
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
									<span><a href="javascript:viewOrderDetail('${shipment.primaryOrderId?if_exists}')" onclick="">${shipment.primaryOrderId?if_exists}</a></span>
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
									<#elseif orderHeader.estimatedDeliveryDate?has_content>
										<span>${orderHeader.estimatedDeliveryDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
									</#if>
								</#if>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.ShipmentId)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${shipmentId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CreatedDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${shipment.createdDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
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
					<td width="8%">${StringUtil.wrapString(uiLabelMap.ProductId)}</td>
					<td width="25%">${StringUtil.wrapString(uiLabelMap.ProductName)}</td>
					<td width="8%">${StringUtil.wrapString(uiLabelMap.IsPromo)}</td>
					<td width="7%">${StringUtil.wrapString(uiLabelMap.Unit)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.Quantity)}</td>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</td>
					<td width="10%">${StringUtil.wrapString(uiLabelMap.ApTotal)}</td>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#assign totalValue = 0>
				<#if listItems?has_content>
					<#list listItems as item>
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
				            <td align="center">
				            	<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId='WEIGHT_MEASURE'>
				            		<#assign unit = delegator.findOne("Uom", {"uomId" : item.weightUomId?if_exists}, false)!/>
					            	<#assign descUnit = StringUtil.wrapString(unit.get('description', locale))>
					            	${StringUtil.wrapString(descUnit?if_exists)}
				            	<#else>
					            	<#assign unit = delegator.findOne("Uom", {"uomId" : item.quantityUomId?if_exists}, false)!/>
					            	<#assign descUnit = StringUtil.wrapString(unit.get('description', locale))>
					            	${StringUtil.wrapString(descUnit?if_exists)}
				            	</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.quantity?has_content>
		   							<#if item.quantity &gt; 0>
		   								<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId='WEIGHT_MEASURE'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.weight?if_exists, "#,##0.000", locale)}
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
				            	<#if item.unitPrice?has_content>
				            		<#if item.isPromo?has_content && item.isPromo == 'Y'>
				            			<strike>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}</strike>
				            		<#else>
				            			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
				            		</#if>
				            	<#else>
		   							<span>0</span>
			   					</#if>
		   					</td>
				            <td class="align-right">
				            	<#if item.unitPrice?has_content && item.quantity?has_content>
				            		<#if item.isPromo?has_content && item.isPromo == 'Y'>
			            				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##0.00", locale)}
			            			<#else>
			            				<#assign itemValue = 0>
					            		<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId='WEIGHT_MEASURE'>
					            			<#assign itemValue = item.unitPrice * item.weight>
					            		<#else>
					            			<#assign itemValue = item.unitPrice * item.quantity>
					            		</#if>
					            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemValue, "#,##0.00", locale)}
					            		<#assign totalValue = totalValue + itemValue>
				            		</#if>
				            	<#else>
		   							<span>0</span>
			   					</#if>
			            	</td>
			          	</tr>
		          	</#list>
		          	<tr>
		          		<td class="align-right" colspan="7">
		          			<b>${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)?upper_case}</b>
		          		</td>
		          		<td class="align-right">
		          			<span> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalValue, "#,##0.00", locale)} </span>
		          		</td>
		          	</tr>
		          	<tr>
		          		<td class="align-right" colspan="8">
		          			<span ><b>${StringUtil.wrapString(uiLabelMap.ByString)?upper_case}: 
		          			<#assign abc = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalValue, "#,##0.00", locale)>
		          			<#if locale == "vi">
				        		<#assign stringTotal = abc.replaceAll("\\.", "")>
				        		<#if (totalValue &lt; 0)>
				        			<#assign grandTotalByDeliver = -totalValue>
				        		</#if>
				        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(totalValue)>
				        		<#if (totalValue &lt; 0)>
				        			<span ><b><i>${uiLabelMap.Negate} ${totalDlvString?if_exists}</i></b></span>
			        			<#else>
				        			<span ><b><i>${totalDlvString?if_exists}</i></b></span>
				        		</#if>
				        	<#elseif locale == "en">
				        		<#assign stringTotal = abc.replaceAll("\\,", "")>
				        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].changeToWords(totalValue, true)>
				        		<span ><b>${totalDlvString?if_exists}</b></span>
				        	</#if>
		          		</td>
		          	</tr>
	          	</#if>
			</tbody>
		</table>
	</div><!--.form-horizontal-->
</div>