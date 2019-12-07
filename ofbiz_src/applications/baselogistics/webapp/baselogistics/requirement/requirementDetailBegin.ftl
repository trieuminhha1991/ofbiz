<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#include 'script/requirementDetailBeginScript.ftl'/>
<#if requirement.requirementTypeId == "CHANGEDATE_REQUIREMENT">
	<#include 'reqRequirementFacility.ftl'>
</#if>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span8">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if requirement?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
										</li>
									</#if>
									<#if requirement?has_content && requirement.requirementTypeId?has_content && requirement.requirementTypeId == "TRANSFER_REQUIREMENT" && hasOlbPermission("MODULE", "LOG_TRANSFER", "CREATE")>
										<li <#if activeTab?exists && activeTab == "transfers-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#transfers-tab">${uiLabelMap.Transfer}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span4" style="height:34px; text-align:right">
							<#if (requirement.statusId == 'REQ_APPROVED' || requirement.statusId == 'REQ_COMPLETED' || requirement.statusId == 'REQ_EXPORTED' || requirement.statusId == 'REQ_RECEIVED') && requirement.requirementTypeId != 'TRANSFER_REQUIREMENT'>
								<a href="javascript:ReqDetailObj.exportPDF('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.PDF}" data-placement="bottom" class="button-action"><i class="fa fa-file-pdf-o"></i></a>
							</#if>
							<#if requirement.statusId == "REQ_CREATED">
								<#if requirement.createdByUserLogin == userLogin.userLoginId || hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN")>
									<#if requirement.createdByUserLogin == userLogin.userLoginId>
										<a href="javascript:ReqDetailObj.sendRequirementNotify('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.SendRequest}" data-placement="bottom" class="button-action"><i class="fa-paper-plane-o"></i></a>
										<a href="javascript:ReqDetailObj.editRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Edit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
									</#if>
									<a href="javascript:ReqDetailObj.cancelRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" class="button-action"><i class="icon-trash red"></i></a>
								</#if>
								<#assign hasAnotherPermission = false>
								<#if requirement.requirementTypeId == "BORROW_REQUIREMENT" && requirement.facilityId?exists>
									<#assign isOwnerFacility = delegator.findByAnd("Facility", {"ownerPartyId": userLogin.partyId, "facilityId": requirement.facilityId}, null, false)/>
									<#if isOwnerFacility?has_content>
										<#assign hasAnotherPermission = true>
									</#if>
								</#if>
								<#if hasAnotherPermission>
									<a href="javascript:ReqDetailObj.changeRequirementStatus('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
								</#if>
				            </#if>
							<#if requirement.statusId == "REQ_PROPOSED">
								<#if hasOlbPermission("MODULE", "ACC_REQUIREMENT", "ADMIN")>
									<a href="javascript:ReqDetailObj.changeRequirementStatus('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Approve}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i></a>
								</#if>
								<#if hasOlbPermission("MODULE", "ACC_REQUIREMENT", "ADMIN")>
									<a href="javascript:ReqDetailObj.editRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.Edit}" data-placement="bottom" class="button-action"><i class="icon-edit"></i></a>
									<a href="javascript:ReqDetailObj.prepareRejectRequirement()" data-rel="tooltip" title="${uiLabelMap.Recject}" data-placement="bottom" class="button-action"><i class="fa fa-times red"></i></a>
								</#if>
				            </#if>
				            <#if requirement.statusId == "REQ_APPROVED" || requirement.statusId == "REQ_RECEIVED" || requirement.statusId == "REQ_EXPORTED">
					            	<#if requirement.requirementTypeId == "EXPORT_REQUIREMENT" || requirement.requirementTypeId == "PAY_REQUIREMENT">
										<#if (hasOlbPermission("MODULE", "LOG_REQUIREMENT", "UPDATE") || hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN") || hasOlbPermission("MODULE", "LOGISTICS", "ADMIN"))>
											<a href="javascript:ReqDetailObj.prepareShipmentFromRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ExportProduct}" data-placement="bottom" class="button-action"><i class="fa fa-upload"></i></a>
					            		</#if>
									<#elseif requirement.requirementTypeId == "RECEIVE_REQUIREMENT" || requirement.requirementTypeId == "BORROW_REQUIREMENT">
										<#if !requirement.facilityId?has_content && hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN") && userLogin.userLoginId == requirement.createdByUserLogin>
					            			<a href="javascript:ReqDetailObj.selectFacilityToAssign('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.AssignFacility}" data-placement="bottom" class="button-action"><i class="fa fa-external-link"></i></a>
										</#if>
										<#if (hasOlbPermission("MODULE", "LOG_REQUIREMENT", "UPDATE") || hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN") || hasOlbPermission("MODULE", "LOGISTICS", "ADMIN"))>
											<#if doneBefore?has_content && doneBefore == true>
												<a href="javascript:ReqDetailObj.prepareShipmentFromRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ReceiveProduct}" data-placement="bottom" class="button-action"><i class="fa fa-download"></i></a>
											<#else>
												<i title="${uiLabelMap.HasRequirementNeedDoneBefore}" class="fa fa-info-circle button-action" style="cursor: pointer;"></i>
											</#if>
										</#if>
									<#elseif requirement.requirementTypeId == "TRANSFER_REQUIREMENT" && hasOlbPermission("MODULE", "LOG_TRANSFER", "ADMIN")>
					            		<#--
					            			<a href="javascript:ReqDetailObj.quickCreateTransferFromRequirement('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ExportProduct}" data-placement="bottom" class="button-action"><i class="fa fa-fast-forward "></i></a>
					            		-->
									<#elseif requirement.requirementTypeId == "CHANGE_PURPOSE_USING" && hasOlbPermission("MODULE", "LOG_INVENTORY", "UPDATE")>
										<a href="javascript:ReqDetailObj.prepareInventoryToChange('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.ExportProduct}" data-placement="bottom" class="button-action"><i class="fa fa-upload"></i></a>
									<#-- <#elseif requirement.requirementTypeId == "BORROW_REQUIREMENT">
										<a href="javascript:ReqDetailObj.quickReceiveInventory('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.MoveProduct}" data-placement="bottom" class="button-action"><i class="fa fa-exchange"></i></a>
									-->
									<#elseif requirement.requirementTypeId == "COMBINE_PRODUCT">
										<#if requirement.facilityId?has_content && hasOlbPermission("MODULE", "LOG_FACILITY", "ADMIN")>
					            			<a href="javascript:ReqDetailObj.prepareCombineProduct('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.BLExecute}" data-placement="bottom" class="button-action"><i class="fa fa-retweet"></i></a>
										</#if>
									<#elseif requirement.requirementTypeId == "CHANGEDATE_REQUIREMENT">
										<#if (hasOlbPermission("MODULE", "LOG_REQUIREMENT", "UPDATE") || hasOlbPermission("MODULE", "LOG_REQUIREMENT", "ADMIN") || hasOlbPermission("MODULE", "LOGISTICS", "ADMIN"))>
											<#if shipmentTypeEnums?has_content>
												<#list shipmentTypeEnums as shipmentType >
													<#if shipmentType.parentTypeId == "OUTGOING_SHIPMENT" && (requirement.statusId == "REQ_APPROVED" || requirement.statusId == "REQ_RECEIVED")>
														<a href="javascript:ReqDetailObj.checkFacility('${requirement.requirementId?if_exists}', 'OUTGOING_SHIPMENT', '${requirement.facilityId?if_exists}')" data-rel="tooltip" title="${shipmentType.description?if_exists}" data-placement="bottom" class="button-action"><i class="fa fa-upload"></i></a>
								            		<#elseif shipmentType.parentTypeId == "INCOMING_SHIPMENT"  && (requirement.statusId == "REQ_APPROVED" || requirement.statusId == "REQ_EXPORTED")>
								            			<a href="javascript:ReqDetailObj.checkFacility('${requirement.requirementId?if_exists}', 'INCOMING_SHIPMENT', '${requirement.facilityId?if_exists}')" data-rel="tooltip" title="${shipmentType.description?if_exists}" data-placement="bottom" class="button-action"><i class="fa fa-download"></i></a>
								            		</#if>
							            		</#list>
						            		</#if>
					            		</#if>
									<#else>
										<#-- another type of requirement -->
									</#if>
								<#else>
									<#-- <#if !requirement.facilityId?has_content>
					            			<a href="javascript:ReqDetailObj.selectFacilityToAssign('${requirement.requirementId?if_exists}')" data-rel="tooltip" title="${uiLabelMap.AssignFacility}" data-placement="bottom" class="button-action"><i class="fa-external-link"></i></a>
									<#else>
										<i title="${uiLabelMap.NotHasPermissionWithFacilityRequirement}" class="fa fa-info-circle button-action" style="cursor: pointer;"></i>
									</#if>
									-->
								</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">