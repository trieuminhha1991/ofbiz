<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var viewDetailOrder = function (orderId){
		window.open("viewDetailPO?orderId="+ orderId, "_blank");
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
</script>
<script type="text/javascript" src="/logresources/js/delivery/purchaseDeliveryDetailBegin.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js?v=1.1.1"></script>

<@jqGridMinimumLib />
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "general-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
									</li>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if delivery?has_content && (delivery.statusId == "DLV_APPROVED" || delivery.statusId == "DLV_EXPORTED") && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
								<a href="javascript:PurchaseDlvDetailOpenObj.prepareReceivePurchaseDelivery('${delivery.deliveryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.UpdateActualDeliveredQuantity}" data-placement="bottom" class="button-action button-size"><i class="fa fa-download"></i></a>
							</#if>
							<#if delivery?has_content && delivery.statusId == "DLV_CREATED" && hasOlbPermission('MODULE', 'LOGISTICS', 'ADMIN')>
				    			<a href="javascript:PurchaseDlvDetailOpenObj.approveDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.Approved}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
				    		</#if>
				    		<#if delivery?has_content && (delivery.statusId == "DLV_CREATED" || delivery.statusId == "DLV_APPROVED") && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
			    				<a href="javascript:dlvEditObj.editDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" class="button-action"><i class="fa fa-edit"></i></a>
				    		</#if>
							<#if delivery?has_content && delivery.statusId != "DLV_CANCELLED">
								<#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW")>
									<a href="javascript:PurchaseDlvDetailOpenObj.exportPDFReceiptDocument('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.PurchaseDeliveryHasPrice}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
								<#else>
									<a href="javascript:PurchaseDlvDetailOpenObj.exportPDFStockInDocument('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.PurchaseDelivery}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
								</#if>
							</#if>
							<#if delivery?has_content && (delivery.statusId == "DLV_CREATED" || delivery.statusId == "DLV_APPROVED")>
			    				<a href="javascript:PurchaseDlvDetailOpenObj.cancelDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="fa fa-trash red"></i></a>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
