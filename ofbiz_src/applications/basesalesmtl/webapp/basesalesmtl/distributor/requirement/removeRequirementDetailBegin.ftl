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

<#assign hasContent="false"/>

<#assign deliveries = delegator.findList("Delivery", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementId", requirement.requirementId), null, null, null, false)/>
<#list deliveries as delivery>
<#assign deliveryId=delivery.deliveryId/>
	<#if delivery.contentId?has_content>
	<#assign hasContent="true"/>
	</#if>
</#list>

<#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>

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
									<#else>
										<li <#if activeTab?exists && activeTab == "deliveries-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#deliveries-tab">${uiLabelMap.DeliveryNoteCommon}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span8 action-bar" style="height:34px; text-align:right">
							<#if requirement.statusId == "REQ_APPROVED" || requirement.statusId == "REQ_COMPLETED">
								<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, false)
								|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, false)>
									<a style="font-size: 14px;" href="javascript:Uploader.open({deliveryId: '${deliveryId?if_exists}'})" data-rel="tooltip" title="${uiLabelMap.UploadFileAttachment}" data-placement="bottom" class="button-action"><i class="fa fa-upload"></i>${uiLabelMap.UploadFileAttachment}</a>
									<#if hasContent?if_exists=="true">
										<a style="font-size: 14px;" href="javascript:Viewer.open({deliveryId: '${deliveryId?if_exists}'})" data-rel="tooltip" title="${uiLabelMap.ViewFileAttachment}" data-placement="bottom" class="button-action"><i class="fa fa-search-plus"></i>${uiLabelMap.ViewFileAttachment}</a>
										<#if requirement.statusId == "REQ_APPROVED">
										<a style="font-size: 14px;" href="javascript:RequirementApprove.setRequirementStatus('${requirement.requirementId?if_exists}', 'REQ_COMPLETED')" data-rel="tooltip" title="${uiLabelMap.BSStepComplete}" data-placement="bottom" class="button-action"><i class="fa-check-circle-o"></i>${uiLabelMap.BSStepComplete}</a>
										</#if>
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