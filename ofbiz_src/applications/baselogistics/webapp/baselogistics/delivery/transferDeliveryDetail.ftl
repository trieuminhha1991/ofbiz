<script>
	<#assign currentStatusId = delivery.statusId?if_exists>
	<#assign currentStatus = "">
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
		<#if item.statusId == currentStatusId>
			<#assign currentStatus = descStatus>;
		</#if>
	</#list>
	
	function viewDetailTransfer(transferId){
		window.location.replace("viewDetailTransfer?transferId="+transferId);
	}
	if (uiLabelMap == undefined) var uiLabelMap = {};
</script>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			${currentStatus?if_exists}
		</div>
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.DeliveryTransferNote)?upper_case}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom span-text-left content-description">
					<div class="row-fluid margin-top20">
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.TransferId)}:</span>
								</div>
								<div class="span8 align-left">
									<span><a href="javascript:viewDetailTransfer('${delivery.transferId?if_exists}')" onclick="">${delivery.transferId?if_exists}</a></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.FacilityFrom)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if originFacility?has_content>
											<#if originFacility.facilityCode?has_content>
												${originFacility.facilityCode?if_exists} - ${originFacility.facilityName?if_exists}
											<#else>
												${originFacility.facilityId?if_exists} - ${originFacility.facilityName?if_exists}
											</#if>
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
									<span>${StringUtil.wrapString(uiLabelMap.FacilityTo)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if destFacility?has_content>
											<#if destFacility.facilityCode?has_content>
												${destFacility.facilityCode?if_exists} - ${destFacility.facilityName?if_exists}
											<#else>
												${destFacility.facilityId?if_exists} - ${destFacility.facilityName?if_exists}
											</#if>
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
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.DeliveryTransferId)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${deliveryId?if_exists}</span>
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
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.ReceiveDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span><#if delivery.actualArrivalDate?exists>${delivery.actualArrivalDate?datetime?string('dd/MM/yyyy HH:mm')}</#if></span>
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
					<td width="7%">${StringUtil.wrapString(uiLabelMap.Unit)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.Quantity)}</td>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.ActualDeliveryQuantitySum)}</td>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.ActualDeliveredQuantitySum)}</td>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.UnitPrice)}
				  	<#if statusId != "DLV_EXPORTED" && statusId != "DLV_DELIVERED" && statusId !="DLV_CANCELLED"> 
				  		</br>(${StringUtil.wrapString(uiLabelMap.BLCurrentCostOfGood)?lower_case})
				  	<#else>
				  		(${StringUtil.wrapString(uiLabelMap.BLCostOfGood)?lower_case})
				  	</#if>
				  	</td>
					<td width="12%">${StringUtil.wrapString(uiLabelMap.ApTotal)}
					<#if statusId != "DLV_EXPORTED" && statusId != "DLV_DELIVERED" && statusId !="DLV_CANCELLED">
						(${StringUtil.wrapString(uiLabelMap.BLProvisionalCalculating)?lower_case})
				  	</#if>
					</td>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#if listItems?has_content>
					<#list listItems as item>
						<#assign i = i + 1>
						<tr>
				            <td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productCode?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productName?if_exists)}</td>
				            <td align="center">${StringUtil.wrapString(item.unit?if_exists)}</td>
				            <td class="align-right">
				            	<#if item.quantity?has_content>
		   							<#if item.quantity &gt; 0>
		   								<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.amount?if_exists, "#,##0.00", locale)}
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
					   					<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedAmount?if_exists, "#,##0.00", locale)}
					   					<#else>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}
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
					   					<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredAmount?if_exists, "#,##0.00", locale)}
					   					<#else>
				   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0", locale)}
					   					</#if>
				   					<#else>
			   							<span>0</span>
				   					</#if>
		   						<#else>
		   							<span>0</span>
			   					</#if>
				            </td>
				            <td class="align-right">
				            	<#if statusId != 'DLV_CANCELLED'>
					            	<#if item.unitCost?has_content>
					            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitCost?if_exists, "#,##0.00", locale)}
					            	<#else>
			   							<span>0</span>
				   					</#if>
				            	<#else>
		   							<span>0</span>
			   					</#if>
		   					</td>
				            <td class="align-right">
				            	<#if statusId != 'DLV_CANCELLED'>
					            	<#if item.itemTotal?has_content>
					            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.itemTotal?if_exists, "#,##0.00", locale)}
					            	<#else>
			   							<span>0</span>
				   					</#if>
				            	<#else>
		   							<span>0</span>
			   					</#if>
			            	</td>
			          	</tr>
		          	</#list>
	          	</#if>
	          	<#if statusId != 'DLV_CANCELLED'>
		          	<tr>
		          	<#if statusId == 'DLV_EXPORTED' || statusId == 'DLV_DELIVERED'>
			            <td class="align-right" colspan="8"><b>${StringUtil.wrapString(uiLabelMap.BLGrandTotal)}</b></td>
		            <#else>
		            	<td class="align-right" colspan="8"><b>${StringUtil.wrapString(uiLabelMap.BLGrandTotal)} (${StringUtil.wrapString(uiLabelMap.BLProvisionalCalculating)?lower_case})</b></td>
		            </#if>
			            <#if grandTotal?has_content>
			            	<td class="align-right" colspan="1"><b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal?if_exists, "#,##0.00", locale)}</b></td>
			            <#else>
							<td><span>0</span></td>
	   					</#if>
		            <tr>
				</#if>
			</tbody>
		</table>
	</div><!--.form-horizontal-->
</div>
<#include "popupEditDelivery.ftl"/>