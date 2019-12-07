<#include "script/deliveryEntryDetailBeginScript.ftl"/>
<#include "deliveryEntryConfirmInfo.ftl"/>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if deliveryentry?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
										<#-- <li <#if activeTab?exists && activeTab == "deliveries-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#deliveries-tab">${uiLabelMap.DeliveryTransferNote}</a>
										</li> -->
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#-- <#if deliveryentry.statusId == "DELIVERY_ENTRY_" && security.hasPermission("_ADMIN", session)>
				    			<a id="approveTransferId" href="javascript:DEDetailBeginObj.someFunction('${parameters.xId?if_exists}')" data-rel="tooltip" 
					    			title="${uiLabelMap.Approve}" data-placement="left" class="button-action">
					    			<i class="fa fa-check-circle-o" style="font-size: 20px;"></i>
					    		</a>
				    		</#if>
				    		-->
				    		<#-- <#if deliveryentry.statusId == "DELI_ENTRY_CREATED" && security.hasPermission("DELIVERY_ADMIN", session)>
				    			<a id="approveDlvEntry" href="javascript:DEDetailBeginObj.approveDlvEntry('${parameters.deliveryEntryId?if_exists}')" data-rel="tooltip" 
					    			title="${uiLabelMap.BLSchedule}" data-placement="left" class="button-action">
					    			<i class="fa fa-check-circle-o" style="font-size: 20px;"></i>
					    		</a>
				    		</#if>
				    		-->
				    		<#-- <#if deliveryentry.statusId == "DELI_ENTRY_SCHEDULED" && security.hasPermission("DELIVERY_ADMIN", session)>
				    			<a id="approveDlvEntry" href="javascript:DEDetailBeginObj.approveDlvEntry('${parameters.deliveryEntryId?if_exists}')" data-rel="tooltip" 
					    			title="${uiLabelMap.Approve}" data-placement="left" class="button-action">
					    			<i class="fa fa-check-circle-o" style="font-size: 20px;"></i>
					    		</a>
				    		</#if>
				    		-->
				    		<#if (deliveryentry.statusId == "DELI_ENTRY_CREATED" || deliveryentry.statusId == "DELI_ENTRY_SCHEDULED" || deliveryentry.statusId == "DELI_ENTRY_SHIPPED") && security.hasPermission("DELIVERY_ADMIN", session)>
				    			<a id="approveDlvEntry" href="javascript:DEDetailBeginObj.completeDlvEntry('${parameters.deliveryEntryId?if_exists}')" data-rel="tooltip" 
					    			title="${uiLabelMap.BLConfirmCompletedTransport}" data-placement="left" class="button-action">
					    			<i class="fa fa-check-circle-o"></i>
					    		</a>
				    		</#if>
				    		<#if security.hasPermission("DELIVERY_ADMIN", session) && deliveryentry.statusId == "DELI_ENTRY_CREATED">
				    			<a class="button-action" href="javascript:DEDetailBeginObj.cancelDeliveryEntry('${deliveryentry.deliveryEntryId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom">
				    			<i class="icon-trash red"></i></a>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
