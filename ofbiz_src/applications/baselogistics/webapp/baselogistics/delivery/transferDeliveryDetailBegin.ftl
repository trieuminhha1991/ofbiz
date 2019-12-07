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
<script type="text/javascript" src="/logresources/js/delivery/transferDeliveryDetailBegin.js?v=1.1.1"></script>
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
							<#if delivery?has_content && (delivery.statusId == "DLV_APPROVED") && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
								<#if shipmentMethodTypeId?has_content && "NO_SHIPPING" != shipmentMethodTypeId>
									<a href="javascript:TransferDlvDetailOpenObj.prepareExportTransferDelivery('${delivery.deliveryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.UpdateActualDeliveredQuantity}" data-placement="bottom" class="button-action button-size"><i class="fa fa-upload"></i></a>
								<#else>
									<a href="javascript:TransferDlvDetailOpenObj.prepareExportTransferDelivery('${delivery.deliveryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.UpdateActualExportQuantity}" data-placement="bottom" class="button-action button-size"><i class="fa fa-exchange"></i></a>
								</#if>
							</#if>
							<#if delivery?has_content && (delivery.statusId == "DLV_EXPORTED") && shipmentMethodTypeId?has_content && "NO_SHIPPING" != shipmentMethodTypeId  && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
								<a href="javascript:TransferDlvDetailOpenObj.prepareReceiveTransferDelivery('${delivery.deliveryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.UpdateActualDeliveredQuantity}" data-placement="bottom" class="button-action button-size"><i class="fa fa-download"></i></a>
							</#if>
							<#if delivery?has_content && delivery.statusId == "DLV_CREATED"  && hasOlbPermission('MODULE', 'LOGISTICS', 'ADMIN')>
				    			<a href="javascript:TransferDlvDetailOpenObj.approveDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.Approved}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
				    		</#if>
				    		<#if delivery?has_content && (delivery.statusId == "DLV_CREATED" || delivery.statusId == "DLV_APPROVED") && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
			    				<a href="javascript:dlvEditObj.editDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" class="button-action"><i class="fa fa-edit"></i></a>
				    		</#if>
							<#if delivery?has_content && delivery.statusId != "DLV_CANCELLED" && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
								<a href="javascript:TransferDlvDetailOpenObj.exportPDFTransferDocument('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.DeliveryTransferNote}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
							</#if>
							<#if delivery?has_content && (delivery.statusId == "DLV_CREATED" || delivery.statusId == "DLV_APPROVED") && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
			    				<a href="javascript:TransferDlvDetailOpenObj.cancelDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="fa fa-trash red"></i></a>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
