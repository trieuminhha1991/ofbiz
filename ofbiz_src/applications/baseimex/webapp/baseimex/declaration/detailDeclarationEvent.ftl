<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">		
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			<#assign status = delegator.findOne("StatusItem", {"statusId" : productEvent.statusId?if_exists}, false)!>
			<#assign currentStatus = StringUtil.wrapString(status.get('description', locale))>
			
			${currentStatus?if_exists}
		</div>
		<#assign products = []/>
		<#if productEvent.statusId != 'PRODUCT_EVENT_CANCELLED'>
			<#assign productTmps = delegator.findList("ProductEventItemAndProductAvailable", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("eventId", productEvent.eventId?if_exists)), null, null, null, false) !/>
		<#else>
			<#assign productTmps = delegator.findList("ProductEventItemAndProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("eventId", eventId?if_exists)), null, null, null, false) !/>
		</#if>
		<#if productTmps?has_content>
			<#list productTmps as pr>
				<#assign products = products + [pr]/>
			</#list>
		</#if>
		
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${StringUtil.wrapString(uiLabelMap.BIEDetailExportDeclaration)?upper_case} ${StringUtil.wrapString(productEvent.eventName?if_exists)?upper_case}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom span-text-left content-description">
					<div class="row-fluid margin-top20">
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CommonId)}:</span>
								</div>
								<div class="span8 align-left">
									<#if productEvent.eventCode?has_content>
										<span>${productEvent.eventCode?if_exists}</span>
									<#else>
										<span>${productEvent.eventId?if_exists}</span>
									</#if>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIETestEventType)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#assign type = delegator.findOne("ProductEventType", {"eventTypeId" : productEvent.eventTypeId?if_exists}, false)!>
										<#if type?has_content>
											${StringUtil.wrapString(type.get('description', locale))}
										</#if>  
									</span>
								</div>
							</div>	
						</div>
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.CreatedDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										${productEvent.createdDate?if_exists?date?string('dd/MM/yyyy HH:mm:ss')}
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIEExecutedDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if productEvent.executedDate?has_content>
											${productEvent.executedDate?if_exists?date?string('dd/MM/yyyy')}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.BIECompletedDate)}:</span>
								</div>
								<div class="span8 align-left">
									<span>
										<#if productEvent.completedDate?has_content>
											${productEvent.completedDate?if_exists?date?string('dd/MM/yyyy')}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<span>${StringUtil.wrapString(uiLabelMap.Description)}:</span>
								</div>
								<div class="span8 align-left">
									<span>${StringUtil.wrapString(productEvent.description?if_exists)}</span>
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
					<td width="7%">${StringUtil.wrapString(uiLabelMap.FromDate)}</td>
				  	<td width="8%">${StringUtil.wrapString(uiLabelMap.ThruDate)}</td>
				</tr>
			</thead>
			<tbody>
				<#assign i = 0>
				<#assign total = 0>
				<#if products?has_content>
					<#list products as item>
						<#assign i = i + 1>
						<tr>
				            <td class="align-left">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productCode?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.productName?if_exists)}</td>
				            <td class="align-left">${StringUtil.wrapString(item.fromDate?if_exists?date?string('dd/MM/yyyy'))}</td>
				            <td class="align-left">${StringUtil.wrapString(item.thruDate?if_exists?date?string('dd/MM/yyyy'))}</td>
			          	</tr>
		          	</#list>
	          	</#if>
			</tbody>
		</table>
	</div><!--.form-horizontal-->
</div>
