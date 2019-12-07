<#include "script/salesDeliveryDetailBeginScript.ftl"/>
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
							<#if delivery?has_content && (delivery.statusId == "DLV_EXPORTED" || delivery.statusId == "DLV_DELIVERED") && hasOlbPermission('MODULE', 'DISTRIBUTOR', 'ADMIN') && !delivery.shipmentDistributorId?has_content>
				    			<a href="javascript:SalesDlvDetailOpenObj.prepareReceivePurchaseDistributor('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.ReceiveProduct}" data-placement="bottom" class="button-action"><i class="fa fa-download"></i></a>
				    		</#if>
							<#if delivery?has_content && delivery.statusId == "DLV_CREATED" && hasOlbPermission('MODULE', 'LOGISTICS', 'ADMIN')>
			    				<a href="javascript:SalesDlvDetailOpenObj.approveDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.Approved}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
				    		</#if>
							<#if delivery?has_content && delivery.statusId == "DLV_APPROVED" && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
								<a href="javascript:SalesDlvDetailOpenObj.prepareUpdateExportedSalesDelivery('${delivery.deliveryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ExportProduct}" data-placement="bottom" class="button-action button-size"><i class="fa fa-upload"></i></a>
							</#if>
				    		<#if delivery?has_content && delivery.statusId == "DLV_EXPORTED" && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
								<a href="javascript:SalesDlvDetailOpenObj.prepareUpdateDeliveredSalesDelivery('${delivery.deliveryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.UpdateActualDeliveredQuantity}" data-placement="bottom" class="button-action button-size"><i class="fa fa-exchange"></i></a>
							</#if>
							<#if delivery?has_content && delivery.statusId != "DLV_CANCELLED">
								<a href="javascript:SalesDlvDetailOpenObj.exportPDFDeliveryDocument('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.DeliveryDoc}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
								<a href="javascript:SalesDlvDetailOpenObj.exportPDFStockOutDocument('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.DeliveryNote}" data-placement="bottom" class="button-action"><i class="fa fa-file-text-o"></i></a>
							</#if>
							<#if delivery?has_content && (delivery.statusId == "DLV_CREATED" || delivery.statusId == "DLV_APPROVED") && hasOlbPermission('MODULE', 'LOG_DELIVERY', 'ADMIN')>
			    				<a href="javascript:dlvEditObj.editDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" class="button-action"><i class="fa fa-edit"></i></a>
			    				<a href="javascript:SalesDlvDetailOpenObj.cancelDelivery('${deliveryId}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="fa fa-trash red"></i></a>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
