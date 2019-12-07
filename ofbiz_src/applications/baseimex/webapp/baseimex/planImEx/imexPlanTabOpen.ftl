<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/imexresources/js/import/plan/importPlanDetailBegin.js?v=1.0.0"></script>

<script>
	
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

<#assign productPlanId = parameters.productPlanId />
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "imexPlanItemView-tab"> class="active"</#if>>
										<a href="listImExPlanItem?productPlanId=${productPlanId}">${uiLabelMap.DmsPlanItem}</a>
									</li>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if planHeader.statusId == "IMPORT_PLAN_CREATED">
								<#if hasOlbPermission("MODULE", "IMEX_PLANING", "ADMIN")>
					    			<a style="cursor: pointer;" href="javascript:PlanDetailBeginObj.approvePlan('${productPlanId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
					    			<a style="cursor: pointer;" href="javascript:PlanDetailBeginObj.editPlan('${productPlanId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.BIEUpdatePlan}" data-placement="bottom" class="button-action"><i class="fa fa-refresh"></i></a>
					    			<a style="cursor: pointer;" href="javascript:PlanDetailBeginObj.cancelPlan('${productPlanId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Delete}" data-placement="bottom" class="button-action"><i class="fa fa-trash red"></i></a>
								</#if>
				    		</#if>
							<#if planHeader.statusId == "IMPORT_PLAN_APPROVED">
								<#if hasOlbPermission("MODULE", "IMEX_PLANING", "ADMIN")>
									<a style="cursor: pointer;" href="javascript:PlanDetailBeginObj.editPlan('${productPlanId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.BIEUpdatePlan}" data-placement="bottom" class="button-action"><i class="fa fa-refresh"></i></a>
					    			<a style="cursor: pointer;" href="javascript:PlanDetailBeginObj.cancelPlan('${productPlanId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Delete}" data-placement="bottom" class="button-action"><i class="fa fa-trash red"></i></a>
				    			</#if>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
					