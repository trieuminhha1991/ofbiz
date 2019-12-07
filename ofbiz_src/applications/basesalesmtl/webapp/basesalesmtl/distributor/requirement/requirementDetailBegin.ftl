<style>
	.action-bar a {
		margin: 5px;
	}
</style>
<div id="jqxNotification">
<div id="notificationContent"></div>
</div>
<script src="/crmresources/js/generalUtils.js"></script>

<#assign distributorId = (requirement.getRelatedOne("Facility", false)?if_exists).get("ownerPartyId") />
<#assign urlGetListFacility = "JQGetListFacilityByAdmin&partyId=" + distributorId />
	
<div id="jqxwindowDestroyedOption" style="display:none;">
	<div>${uiLabelMap.DestroyedInWarehouseOfDistributor}</div>
	<div>
		<div class="row-fluid margin-top10 margin-bottom10">
			<div class="span5">
				<label class="text-right asterisk">${uiLabelMap.ChooseDepositWarehouse}</label>
			</div>
			<div class="span7">
				<div id="divDepositWarehouse">
					<div style="border-color: transparent;" id="jqxgridDepositWarehouse" tabindex="0"></div>
				</div>
			</div>
		</div>
		<input type="hidden" id="distributorId" value="${distributorId?if_exists}" />
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="cancelDestroyedOption" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveDestroyedOption" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/salesmtlresources/js/distributor/requirement/RequirementApprove.js"></script>
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
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_RSM_GT", userLogin, false)
								|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_RSM_MT", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_RSMAPPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_RSMAPPROVED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_CSM_GT", userLogin, false)
								|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_CSM_MT", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CSMAPPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_CSMAPPROVED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_MANAGER", userLogin, false)>
									<#if requirement.requirementTypeId?if_exists == "RETURN_REQ">
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_SMAPPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<#else>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_APPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									</#if>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_SMAPPROVED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "HRMADMIN", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_APPROVED')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.Approve}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQRETURN_CANCELLED')" data-rel="tooltip" title="${uiLabelMap.Cancel}" data-placement="bottom" class="button-action"><i class="fa-ban"></i>${uiLabelMap.Cancel}</a>
								</#if>
							<#elseif requirement.statusId == "REQRETURN_APPROVED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "HRMADMIN", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.processRequirement('Destroyed')" data-rel="tooltip" title="${uiLabelMap.DestroyedInWarehouseOfDistributor}" data-placement="bottom" class="button-action"><i class="fa fa-trash-o"></i>${uiLabelMap.DestroyedInWarehouseOfDistributor}</a>
									<a style="font-size: 14px;" href="javascript:RequirementApprove.processRequirement('Transfer')" data-rel="tooltip" title="${uiLabelMap.TransferToTheCompanyWarehouse}" data-placement="bottom" class="button-action"><i class="fa fa-reply"></i>${uiLabelMap.TransferToTheCompanyWarehouse}</a>
								</#if>
							</#if>
						</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">