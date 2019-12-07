<#include "script/transferDetailBeginScript.ftl"/>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if transfer?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
										<#if transfer.statusId != "TRANSFER_CANCELLED">
											<li <#if activeTab?exists && activeTab == "deliveries-tab"> class="active"</#if>>
												<a data-toggle="tab" href="#deliveries-tab">${uiLabelMap.DeliveryTransferNote}</a>
											</li>
										</#if>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if transfer.statusId == "TRANSFER_CREATED">
								<#if hasOlbPermission("MODULE", "ACC_TRANSFER", "ADMIN")>
					    			<a style="cursor: pointer;" href="javascript:TransferDetailBeginObj.approveTransfer('${parameters.transferId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
					    			<a style="cursor: pointer;" href="javascript:TransferDetailBeginObj.prepareRejectTransfer()" data-rel="tooltip" title="${uiLabelMap.Reject}" data-placement="bottom" class="button-action"><i class="fa fa-times"></i></a>
					    		</#if>
					    		<#if hasOlbPermission("MODULE", "LOG_TRANSFER", "ADMIN") || transfer.createdByUserLogin == userLogin.userLoginId>
					    			<a style="cursor: pointer;" href="javascript:TransferDetailBeginObj.editTransfer()" data-rel="tooltip" title="${uiLabelMap.Edit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
					    		</#if>
					    		<#if transfer.createdByUserLogin == userLogin.userLoginId>
									<a style="cursor: pointer;" href="javascript:TransferDetailBeginObj.cancelTransfer('${transfer.transferId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
				    		</#if>
				    		<#if delivered?has_content && delivered == true>
				    			<a style="cursor: pointer;" href="javascript:TransferDetailBeginObj.completeTransfer('${parameters.transferId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.BLCompleteWithDeliveredQuantity}" data-placement="bottom" class="button-action"><i class="fas fa-power-off"></i></a>
				    		</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">