<script>
	<#assign agreementId = parameters.agreementId?if_exists/>
	<#assign agreement = delegator.findOne("Agreement", {"agreementId" : agreementId}, false)!>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CreateSuccessfully = "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}";
	uiLabelMap.AreYouSureConfirm = "${StringUtil.wrapString(uiLabelMap.AreYouSureConfirm)}";
</script>
<script type="text/javascript" src="/imexresources/js/import/agreement/agreementDetailBegin.js?v=1.0.0"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<#include "popup/popupEditAgreement.ftl">	
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if agreement?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
										<li <#if activeTab?exists && activeTab == "order-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#order-tab">${uiLabelMap.PurchaseOrder}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if agreement.statusId != "AGREEMENT_CANCELLED">	
								<a style="cursor: pointer;" href="javascript:AgrDetailBeginObj.printAgreement('${agreement.agreementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Print}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
							</#if>
							<#if agreement.statusId == "AGREEMENT_CREATED">
								<#if hasOlbPermission("MODULE", "ACC_AGREEMENT", "ADMIN")>
					    			<a style="cursor: pointer;" href="javascript:AgrDetailBeginObj.approveAgreement('${parameters.agreementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
								</#if>
								<#if hasOlbPermission("MODULE", "IMEX_AGREEMENT", "ADMIN")>
									<a style="cursor: pointer;" href="javascript:AgrDetailBeginObj.editAgreement('${agreement.agreementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Edit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
					    			<a style="cursor: pointer;" href="javascript:AgrDetailBeginObj.cancelAgreement('${agreement.agreementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
				    		</#if>
							<#if agreement.statusId == "AGREEMENT_APPROVED" && (!agreement.hasOrdered?has_content || agreement.hasOrdered == 'N')>
								<#if hasOlbPermission("MODULE", "IMEX_AGREEMENT", "ADMIN")>
									<a style="cursor: pointer;" href="javascript:AgrDetailBeginObj.supplierConfirmAgremeent('${agreement.agreementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.BIEConfirmAgreement}" data-placement="bottom" class="button-action"><i class="fa-thumbs-up"></i></a>
									<a style="cursor: pointer;" href="javascript:AgrDetailBeginObj.cancelAgreement('${agreement.agreementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">