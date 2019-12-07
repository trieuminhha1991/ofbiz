<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<#--  <div class="title-status" id="statusTitle">
			${currentStatus?if_exists}
		</div>
		-->
		<#assign products = []/>
		<#assign productTmps = delegator.findList("PackingListDetailAndOrderAndProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("packingListId", packingListId?if_exists)), null, null, null, false) !/>
		<#if productTmps?has_content>
			<#list productTmps as pr>
				<#assign products = products + [pr]/>
			</#list>
		</#if>
		<#assign containerId = packing.containerId?if_exists>
		<#if containerId?has_content>
			<#assign container = delegator.findOne("Container", {"containerId" : containerId?if_exists}, false)/>
			<#assign billId = container.billId?if_exists>
			<#if container?has_content>
				<#assign bill = delegator.findOne("BillOfLading", {"billId" : billId?if_exists}, false)/>
				<#assign shipComp = delegator.findOne("PartyFullNameDetail", {"partyId": bill.partyIdFrom?if_exists}, false)!/>
			</#if>
		</#if>
		<#assign packing = delegator.findOne("PackingListHeader", {"packingListId" : packingListId?if_exists}, false)!/>
		<#assign facility = delegator.findOne("Facility", {"facilityId" : packing.destFacilityId?if_exists}, false)!/>
			
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.BIEPackingList)?upper_case}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom span-text-left content-description">
					<div class="row-fluid margin-top20">
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEBillOfLading)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${bill.billNumber?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEContainer)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${container.containerNumber?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEShippingParty)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if shipComp?has_content>
											${shipComp.fullName?if_exists}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.ReceiveToFacility)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if facility?has_content>
											${facility.facilityName?if_exists}
										</#if>
									</span>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if bill?has_content>
											${bill.departureDate?if_exists?date?string('dd/MM/yyyy')}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if bill?has_content>
											${bill.arrivalDate?if_exists?date?string('dd/MM/yyyy')}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEGrossWeight)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if packing.grossWeightTotal?has_content>
											${packing.grossWeightTotal?if_exists?string(",##0.00")} (Kg)
										</#if>
									</span>
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
					<td width="8%">${StringUtil.wrapString(uiLabelMap.OrderId)}</td>
					<td width="8%">${StringUtil.wrapString(uiLabelMap.LogMaHang)}</td>
					<td width="25%">${StringUtil.wrapString(uiLabelMap.LogTenHangHoa)}</td>
					<td width="7%">${StringUtil.wrapString(uiLabelMap.Unit)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.Quantity)}</td>
				  	<td width="10%">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</td>
					<td width="10%">${StringUtil.wrapString(uiLabelMap.ApTotal)}</td>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#assign total = 0>
				<#if products?has_content>
					<#list products as item>
						<#assign uom = delegator.findOne("Uom", {"uomId": item.quantityUomId?if_exists}, false)!/>
						<#assign i = i + 1>
						<tr>
				            <td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.orderId?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productCode?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productName?if_exists)}</td>
				            <td align="center"><#if uom?has_content>${StringUtil.wrapString(uom.get('description', locale))}</#if></td>
				            <td class="align-right">
	   							<#if item.orderUnit?exists>
		   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.orderUnit?if_exists, "#,##0", locale)}
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.unitPrice?has_content>
		   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
		   						<#else>
		   							<span>0</span>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.orderUnit?has_content && item.unitPrice?has_content>
				            		<#assign itemTotal = item.orderUnit*item.unitPrice>
				            		<#assign total = total + itemTotal>
				            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemTotal, "#,##0.00", locale)}
				            	<#else>
		   							<span>0</span>
			   					</#if>
			            	</td>
			          	</tr>
		          	</#list>
	          	</#if>
				<tr>
		            <td class="align-right" colspan="7"><b>${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)?upper_case}</b></td>
		            <#if total?has_content>
		            	<td class="align-right" colspan="1"><b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(total?if_exists, "#,##0.00", locale)}</b></td>
		            <#else>
						<td class="align-right"> <b>0</b> </td>
   					</#if>
				</tr>
			</tbody>
		</table>
	</div><!--.form-horizontal-->
</div>
