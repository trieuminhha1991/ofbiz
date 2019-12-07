<style>
	.action-bar a {
		margin: 5px;
	}
</style>
<div id="jqxNotification">
<div id="notificationContent"></div>
</div>

<#assign distributorId = (requirement.getRelatedOne("Facility", false)?if_exists).get("ownerPartyId") />
<#assign urlGetListFacility = "JQGetListFacilityByAdmin&partyId=" + distributorId />


<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/salesmtlresources/js/distributor/requirement/TransferRequirementApprove.js"></script>
<#include 'component://baselogistics/webapp/baselogistics/requirement/script/requirementDetailBeginScript.ftl'/>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span4">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if requirement?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
									</#if>
									<#if requirement?has_content && requirement.requirementTypeId?has_content && requirement.requirementTypeId == "TRANSFER_REQUIREMENT" && security.hasPermission("TRANSFER_VIEW", userLogin)>
										<li <#if activeTab?exists && activeTab == "transfers-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#transfers-tab">${uiLabelMap.Transfer}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span8 action-bar" style="height:34px; text-align:right">
						<#if hasOlbPermission("MODULE", "REQSALES_PRODUCTDIS_APPROVE", "")>
							<#if requirement.statusId == "REQRETURN_CREATED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, false)
								|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_ASMAPPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_ASMAPPROVED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_MANAGER", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_SMAPPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_SMAPPROVED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "HRMADMIN", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_APPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_APPROVED">
								
							</#if>
						</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">