<#include 'script/requirementDetailScript.ftl'/>
<div id="notifyCreateSuccessful" style="display: none;">
	<div>
		${uiLabelMap.CreateSuccessfully}. 
	</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;"></div>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">					
	<div style="position:relative">
		<div class="row-fluid">
			<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
				<div class='row-fluid'>
					<div class="span4">
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.RequirementId}</b></span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementId" name="requirementId">
									${requirement.requirementId?if_exists}
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.Status}</b></span>
							</div>
							<div class="span7">
								<div id="reqStatusId" style="text-align: left;" class="green-label"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.RequirementType}</b></span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementTypeId" name="requirementTypeId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.ReasonRequirement}</b></span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="reasonEnumId" name="reasonEnumId"></div>
					   		</div>
				   		</div>
					</div>
					<div class='span4'>
						<#if requirement.facilityId?has_content>
							<div class='row-fluid'>
								<div class='span6'>
									<span style="float: right"><b>
									<#--  <#if requirement.requirementTypeId == "TRANSFER_REQUIREMENT">-->
										${uiLabelMap.FacilityFrom}
									<#-- <#else>
										${uiLabelMap.Facility}
									</#if> -->
									</b></span>
								</div>
								<div class="span6">
									<div class="green-label" style="text-align: left;" id="facilityIdDT" name="facilityIdDT"></div>
						   		</div>
							</div>
						</#if>
						<#if requirement.destFacilityId?has_content <#-- && requirement.requirementTypeId == "TRANSFER_REQUIREMENT"  -->>
							<div class='row-fluid'>
								<div class='span6'>
									<span style="float: right"><b>${uiLabelMap.FacilityTo}</b></span>
								</div>
								<div class="span6">
									<div class="green-label" style="text-align: left;" id="destFacilityIdDT" name="destFacilityIdDT"></div>
						   		</div>
							</div>
						</#if>
				   		<div class='row-fluid'>
							<div class='span6'>
								<span style="float: right"><b>${uiLabelMap.LogRequiredByDate}</b></span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;" id="requiredByDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span6'>
								<span style="float: right"><b>${uiLabelMap.LogRequirementStartDate}</b></span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;" id="requirementStartDate">
								</div>
					   		</div>
						</div>
						<#if requirement.requirementTypeId == "CHANGEDATE_REQUIREMENT">
							<div class='row-fluid'>
								<div class='span6'>
									<span style="float: right"><b>${uiLabelMap.BSCustomerId}</b></span>
								</div>
								<div class="span6">
									<#assign requirementRoleCustomer = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("RequirementRole", {"requirementId" : requirement.requirementId, "roleTypeId": "CUSTOMER"}, null, false))!/>
									<#if requirementRoleCustomer?exists><#assign customerParty = delegator.findOne("PartyFullNameDetailSimple", {"partyId" : requirementRoleCustomer.partyId}, false)!/></#if>
									<div class="green-label" style="text-align: left;"><#if customerParty?has_content>${customerParty.fullName?if_exists} (${customerParty.partyCode?if_exists})</#if></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span6'>
									<span style="float: right"><b>${uiLabelMap.BSCustomerAddress}</b></span>
								</div>
								<div class="span6">
									<#assign customerAddress = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : requirement.destContactMechId?if_exists}, false)!/>
									<div class="green-label" style="text-align: left;"><#if customerAddress?has_content>${customerAddress.fullName?if_exists}</#if></div>
						   		</div>
							</div>
						</#if>
					</div>
					<div class="span4">
						<div class='row-fluid hide'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.EstimatedBudget}</b></span>
							</div>
							<div class="span7">	
								<div id="estimatedBudget" style="text-align: left;" class="green-label"></div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.CreatedBy}</b></span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="createdByUserLogin" name="createdByUserLogin">
									[${createdBy.partyCode?if_exists}] ${createdBy.lastName?if_exists} ${createdBy.middleName?if_exists} ${createdBy.firstName?if_exists} 
								</div>
					   		</div>
				   		</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span style="float: right"><b>${uiLabelMap.Description}</b></span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="description" name="description">
									${requirement.description?if_exists}
								</div>
					   		</div>
						</div>
						<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right"><b>${uiLabelMap.RemainingSubTotal}</b></span>
								</div>
								<div class="span7">
									<div class="green-label" style="text-align: left;" id="grandTotal" name="grandTotal">
										<#if requirement.grandTotal?has_content>
											${requirement.grandTotal?string(",##0.00")}
										</#if>  
									</div>
						   		</div>
							</div>
						</#if>
					</div>
				<div class='row-fluid' style="margin-bottom: -10px !important">
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<h4 class="row margin-left10 smaller lighter blue grid-title">
			    ${uiLabelMap.ListProduct}
			</h4>
			<div id = "listRequirementItem"></div>
		</div>
	</div>
	<div class="row-fluid hide" id="noteContent">
		<div class="span12">
			<h4 class="row header smaller lighter blue grid-title">
			    ${uiLabelMap.Notes}
			</h4>
			<div id="listNote"></div>
		</div>
	</div>
</div>

<div id="noteRequirement" class="hide popup-bound">
	<div>${uiLabelMap.ReasonRejected}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div class="span3" style="text-align: right">
					<div class="asterisk">${uiLabelMap.Notes}</div>
				</div>
				<div class="span8">	
					<textarea id="note" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="noteCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="noteSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
</div>