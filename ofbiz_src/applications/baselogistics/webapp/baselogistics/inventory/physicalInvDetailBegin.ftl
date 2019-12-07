<#include "script/physcialInvDetailBeginScript.ftl"/>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if physicalInventory?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
										<li <#if activeTab?exists && activeTab == "variance-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#variance-tab">${uiLabelMap.VarianceTotal}</a>
										</li>
										<#--
										<li <#if activeTab?exists && activeTab == "deliveries-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#deliveries-tab">${uiLabelMap.DeliveryTransferNote}</a>
										</li>
										-->
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<#--
						<div class="span2" style="height:34px; text-align:right">
				    		<a style="font-size: 14px;" href="javascript:PhysicalInvDetailBeginObj.functionTmp('${parameters.physicalInventoryId?if_exists}')" data-rel="tooltip" title="" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
						</div>
						--> 
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
