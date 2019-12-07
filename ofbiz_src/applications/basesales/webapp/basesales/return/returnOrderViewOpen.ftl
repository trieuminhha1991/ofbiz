<#assign checkDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, returnHeader.destinationFacilityId)/>
<#include "script/returnOrderViewOpenScript.ftl"/>
<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasComboBox=true/>
<div id="contentNotificationAddSuccess">
<div id="jqxNotificationAddSuccess" >
    <div id="notificationAddSuccess">
    </div>
</div>
<#if checkDistributor>
	<#include 'receiveFacilityDistributors.ftl'/>
</#if>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "returnorderoverview-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#returnorderoverview-tab">${uiLabelMap.BSOverview}</a>
									</li>
									<#--
									<li<#if activeTab?exists && activeTab == "orderinfo-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
									</li>
									-->
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#--list buttons action to order-->
                            <a href="javascript:CustomerDetailOpenObj.exportPDFReturn('${returnId}')" data-rel="tooltip" title="${uiLabelMap.PDF}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
				    		<#if checkDistributor>
                                <#if returnHeader.statusId == "RETURN_ACCEPTED">
				    			    <a href="javascript:void(0);" onclick="receiveFacilityDistributor(); return false;"  target="_blank" data-rel="tooltip" title="${uiLabelMap.StockIn}" data-placement="bottom" class="button-action"><i class="fa-download"></i></a>
                                </#if>
							</#if>
							<#if returnHeader.statusId == "RETURN_REQUESTED">
                                <#if hasOlbPermission("MODULE", "DIS_RETURNORDER_UPDATE", "UPDATE")>
                                    <a href="javascript:CustomerDetailOpenObj.approveReturnDistributor('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
                                <#elseif hasOlbPermission("MODULE", "RETURN_ORDER", "ACTION_APPROVE")>
                                    <a href="javascript:CustomerDetailOpenObj.approveReturn('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
                                </#if>
                                <#if hasOlbPermission("MODULE", "DIS_RETURNORDER_UPDATE", "UPDATE")>
                                    <#if returnHeader.createdBy == userLogin.userLoginId>
                                        <a href="javascript:CustomerDetailOpenObj.cancelReturnDistributor('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="fa fa-times-circle-o"></i></a>
                                    </#if>
                                <#else>
                                    <#if hasOlbPermission("MODULE", "RETURN_ORDER", "ACTION_CANCEL")>
                                        <a href="javascript:CustomerDetailOpenObj.cancelReturn('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="fa fa-times-circle-o"></i></a>
                                    </#if>
                                </#if>
                            <#elseif returnHeader.statusId == "RETURN_ACCEPTED">
                            	<#if hasOlbPermission("MODULE", "RETURN_ORDER", "UPDATE") && hasOlbPermission("MODULE", "LOGISTICS", "VIEW")>
                                    <a href="javascript:CustomerDetailOpenObj.prepareReceiveCustomerReturn('${returnHeader.returnId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ReceiveProduct}" data-placement="bottom" class="button-action"><i class="fa fa-download"></i></a>
                                </#if>
				    		</#if>
						</div>
					</div>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
