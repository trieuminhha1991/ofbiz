<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script>
	<#assign eventId = parameters.eventId?if_exists/>
	<#assign productEvent = delegator.findOne("ProductEvent", {"eventId" : eventId}, false)!>
	var eventTypeId = '${productEvent.eventTypeId?if_exists}';
	var productEvent = {};
	productEvent.eventId = '${StringUtil.wrapString(productEvent.eventId?if_exists)}';
	productEvent.eventCode = '${StringUtil.wrapString(productEvent.eventCode?if_exists)}';
	productEvent.eventName = '${StringUtil.wrapString(productEvent.eventName?if_exists)}';
	productEvent.description = '${StringUtil.wrapString(productEvent.description?if_exists)}';
	productEvent.executedDate = '${productEvent.executedDate?if_exists}';
	productEvent.completedDate = '${productEvent.completedDate?if_exists}';
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureSend = "${StringUtil.wrapString(uiLabelMap.AreYouSureSend)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CreateSuccessfully = "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.WrongFormat = "${StringUtil.wrapString(uiLabelMap.WrongFormat)}";
</script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/imexresources/js/declaration/detailDeclarationEventBegin.js?v=1.0.0"></script>	
<div id="jqxNotification">
    <div id="notificationContent"></div>
</div>
<#include "popupEditEvent.ftl">
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if productEvent?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if productEvent.statusId != "PRODUCT_EVENT_CANCELLED">	
								<!-- <a style="cursor: pointer;" href="javascript:EventDetailBegin.exportProductEvent('${productEvent.eventId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Excel}" data-placement="bottom" class="button-action"><i class="fas fa-file-excel-o"></i></a> -->
							</#if>
							<#if productEvent.statusId == "PRODUCT_EVENT_CREATED">
								<#if hasOlbPermission("MODULE", "IMEX_DECLARATION", "ADMIN")>
					    			<a style="cursor: pointer;" href="javascript:EventDetailBegin.approveProductEvent('${parameters.eventId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
								</#if>
								<#if hasOlbPermission("MODULE", "IMEX_DECLARATION", "ADMIN") || productEvent.createdByUserLogin == userLogin.userLoginId>
									<a style="cursor: pointer;" href="javascript:EventDetailBegin.editProductEvent(productEvent)" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
									<a style="cursor: pointer;" href="javascript:EventDetailBegin.cancelProductEvent('${productEvent.eventId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
				    		</#if>
				    		<#if productEvent.statusId == "PRODUCT_EVENT_APPROVED">	
							</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">