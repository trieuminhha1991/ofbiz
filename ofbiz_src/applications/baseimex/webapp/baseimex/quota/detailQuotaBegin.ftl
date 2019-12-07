<script>
	<#assign quotaId = parameters.quotaId?if_exists/>
	<#assign quotaHeader = delegator.findOne("QuotaHeader", {"quotaId" : quotaId}, false)!>
	
	var quotaHeader = {};
	quotaHeader.quotaId = "${StringUtil.wrapString(quotaHeader.quotaId?if_exists)}";
	quotaHeader.quotaCode = "${StringUtil.wrapString(quotaHeader.quotaCode?if_exists)}";
	quotaHeader.quotaName = "${StringUtil.wrapString(quotaHeader.quotaName?if_exists)}";
	quotaHeader.description = "${StringUtil.wrapString(quotaHeader.description?if_exists)}";
	quotaHeader.currencyUomId = "${StringUtil.wrapString(quotaHeader.currencyUomId?if_exists)}";
	quotaHeader.partyCode = "${StringUtil.wrapString(quotaHeader.partyCode?if_exists)}";
	quotaHeader.fullName = "${StringUtil.wrapString(quotaHeader.fullName?if_exists)}";
	quotaHeader.supplierPartyId = "${StringUtil.wrapString(quotaHeader.supplierPartyId?if_exists)}";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CreateSuccessfully = "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}";
</script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/imexresources/js/quota/detailQuotaBegin.js?v=1.0.0"></script>
<#include "popupEditQuota.ftl">
<#include "popupAddQuota.ftl">
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if quotaHeader?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if quotaHeader.statusId != "QUOTA_CANCELLED">	
								<a style="cursor: pointer;" href="javascript:QuotaDetailBegin.exportQuotaHeader('${quotaHeader.quotaId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Excel}" data-placement="bottom" class="button-action"><i class="fas fa-file-excel-o"></i></a>
							</#if>
							<#if quotaHeader.statusId == "QUOTA_CREATED">
								<#if hasOlbPermission("MODULE", "IMEX_QUOTA", "ADMIN")>
					    			<a style="cursor: pointer;" href="javascript:QuotaDetailBegin.approveQuotaHeader('${parameters.quotaId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
								</#if>
								<#if hasOlbPermission("MODULE", "IMEX_QUOTA", "ADMIN") || quotaHeader.createdByUserLogin == userLogin.userLoginId>
									<a style="cursor: pointer;" href="javascript:QuotaDetailBegin.editQuotaHeader('${quotaHeader.quotaId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
									<a style="cursor: pointer;" href="javascript:QuotaDetailBegin.cancelQuotaHeader('${quotaHeader.quotaId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
				    		</#if>
				    		<a style="cursor: pointer;" href="javascript:QuotaDetailBegin.createQuotaHeader()" data-rel="tooltip" title="${uiLabelMap.AddNew}" data-placement="bottom" class="button-action"><i class="fas fa-plus"></i></a>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">