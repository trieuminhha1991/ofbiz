<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			<#assign status = delegator.findOne("StatusItem", {"statusId" : quotaHeader.statusId?if_exists}, false)!>
			<#assign currentStatus = StringUtil.wrapString(status.get('description', locale))!>
			
			${currentStatus?if_exists}
		</div>
		<#assign products = []/>
		<#if quotaHeader.statusId != 'QUOTA_CANCELLED'>
			<#assign productTmps = delegator.findList("QuotaItemAndProductAvailable", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("quotaId", quotaHeader.quotaId?if_exists)), null, null, null, false) !/>
		<#else>
			<#assign productTmps = delegator.findList("QuotaItemAndProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("quotaId", quotaId?if_exists)), null, null, null, false) !/>
		</#if>
		<#if productTmps?has_content>
			<#list productTmps as pr>
				<#assign products = products + [pr]/>
			</#list>
		</#if>
		
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.BIEQuota)?upper_case} ${StringUtil.wrapString(quotaHeader.quotaName?if_exists)?upper_case}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom span-text-left content-description">
					<div class="row-fluid margin-top20">
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEQuotaId)}:</span>
								</div>
								<div class="span8 align-left">
									<#if quotaHeader.quotaCode?has_content>
										<span>${quotaHeader.quotaCode?if_exists}</span>
									<#else>
										<span>${quotaHeader.quotaId?if_exists}</span>
									</#if>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEQuotaName)}:</span>
								</div>
								<div class="span8 align-left">
									<#if quotaHeader.quotaName?has_content>
										<span>${quotaHeader.quotaName?if_exists}</span>
									</#if>
								</div>
							</div>
							<#-- <div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Supplier)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#assign supplier = delegator.findOne("PartyFullNameDetail", {"partyId" : quotaHeader.supplierPartyId?if_exists}, false)!>
										<#if supplier?has_content>
											${supplier.fullName?if_exists} - ${supplier.partyCode?if_exists}
										</#if>  
									</span>
								</div>
							</div>
							-->
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CreatedDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										${quotaHeader.createdDate?if_exists?date?string('dd/MM/yyyy HH:mm:ss')}
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Description)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${StringUtil.wrapString(quotaHeader.description?if_exists)}</span>
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
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.BIEQuotaTotal)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.BIEQuotaRemain)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.FromDate)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.ThruDate)}</td>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#assign total = 0>
				<#if products?has_content>
					<#list products as item>
						<#assign uom = delegator.findOne("Uom", {"uomId": item.uomId?if_exists}, false)!/>
						<#assign i = i + 1>
						<tr>
				            <td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productCode?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productName?if_exists)}</td>
				            <td align="center"><#if uom?has_content>${StringUtil.wrapString(uom.get('description', locale))}</#if></td>
				            <td class="align-right">
	   							<#if item.quotaQuantity?exists>
	   								<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountTypeId == 'WEIGHT_MEASURE'>
		   								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quotaQuantity?if_exists, "#,##0.00", locale)}
		   							<#else>
		   								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quotaQuantity?if_exists, "#,##0", locale)}
		   							</#if>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.availableQuantity?has_content>
		   							<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountTypeId == 'WEIGHT_MEASURE'>
		   								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.availableQuantity?if_exists, "#,##0.00", locale)}
		   							<#else>
		   								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.availableQuantity?if_exists, "#,##0", locale)}
		   							</#if>
		   						<#else>
		   							<span>0</span>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.fromDate?has_content>
		   							${item.fromDate?string('dd/MM/yyyy')}
		   						<#else>
		   							<span></span>
		   						</#if>
				            </td>
				            <td class="align-right">
				            	<#if item.thruDate?has_content>
				            		${item.thruDate?string('dd/MM/yyyy')}
		   						<#else>
		   							<span></span>
		   						</#if>
				            </td>
			          	</tr>
		          	</#list>
	          	</#if>
			</tbody>
		</table>
	</div><!--.form-horizontal-->
</div>
