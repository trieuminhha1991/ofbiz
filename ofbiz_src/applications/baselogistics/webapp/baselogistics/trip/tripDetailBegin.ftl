<#include "script/tripDetailBeginScript.ftl"/>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if trip?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
									</#if>
                                    <li<#if activeTab?exists && activeTab == "items-tab"> class="active"</#if>>
                                        <a data-toggle="tab" href="#items-tab">${uiLabelMap.BSOrderItem}</a>
                                    </li>
									<#if trip?has_content && trip.statusId == "TRIP_EXPORTED" >
									<li<#if activeTab?exists && activeTab == "route-tab"> class="active"</#if>>
                                        <a data-toggle="tab" href="#route-tab">${uiLabelMap.BLRoute}</a>
                                    </li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						
						<div class="span2" style="height:34px; text-align:right">
							<#if trip.statusId == "TRIP_CREATED">
								<#if security.hasPermission("DELIVERY_ADMIN", session)>
									<a style="cursor: pointer;" href="javascript:TripDetailBeginObj.quickApproveAndExportShippingTrip('${trip.shippingTripId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ApproveAndExportShippingTrip}" data-placement="bottom" class="button-action"><i class="fa-fighter-jet"></i></a>
								</#if>
									<a style="cursor: pointer;" href="javascript:TripDetailBeginObj.editShippingTrip('${trip.shippingTripId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Edit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
									<a style="cursor: pointer;" href="javascript:TripDetailBeginObj.cancelShippingTrip('${trip.shippingTripId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
							</#if>
							<#--<#if trip?has_content && trip.statusId == "TRIP_CONFIRMED">
								<a href="javascript:TripDetailBeginObj.prepareUpdateExportedDeliveryInTrip('${trip.shippingTripId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ExportProduct}" data-placement="bottom" class="button-action button-size"><i class="fa fa-upload"></i></a>
							</#if>-->
							<#if trip?has_content && trip.statusId != "TRIP_CANCELLED">
								<a href="javascript:TripDetailBeginObj.exportPDFDeliveryDocument('${trip.shippingTripId}')" data-rel="tooltip" title="${uiLabelMap.DeliveryDoc}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
								<#--<a href="javascript:TripDetailBeginObj.exportPDFStockOutDocument('${trip.shippingTripId}')" data-rel="tooltip" title="${uiLabelMap.DeliveryNote}" data-placement="bottom" class="button-action"><i class="fa fa-file-text-o"></i></a>-->
							</#if>
							<#if trip.statusId == "TRIP_EXPORTED">
									<a style="cursor: pointer;" href="javascript:TripDetailBeginObj.updateShippingTripStatus('${trip.shippingTripId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CompleteShippingTrip}" data-placement="bottom" class="button-action"><i class="fa fa-check"></i></a>
							</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">