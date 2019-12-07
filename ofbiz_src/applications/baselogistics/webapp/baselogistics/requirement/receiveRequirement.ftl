<#include "script/receiveRequirementScript.ftl"/>
<div>
	<h4 class="row header smaller lighter blue" style="margin: 5px 0px 20px 0px !important;font-weight:500;line-height:20px;font-size:18px;">
		${uiLabelMap.GeneralInfo}
	</h4>
</div>
<div class="rowfluid">
	<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom content-description" style="margin:10px">
			<div class="span12">
				<div class='row-fluid'>
					<div class="span4">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.RequirementId}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementId" name="requirementId">
									${requirement.requirementId?if_exists}
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.Status}</span>
							</div>
							<div class="span7">
								<div class="span5">
									<div id="statusId" style="text-align: left;" class="green-label">${statusDesc?if_exists}</div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.RequirementType}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementTypeId" name="requirementTypeId">${requirementTypeDesc?if_exists}</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.ReasonRequirement}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="reasonEnumId" name="reasonEnumId">${reasonDesc?if_exists}</div>
					   		</div>
				   		</div>
					</div>
					<div class="span4">
						<div class='row-fluid'>
							<div class='span6'>
								<span>${uiLabelMap.CreatedBy}</span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;" id="createdByUserLogin" name="createdByUserLogin">
									${createdBy.lastName?if_exists} ${createdBy.middleName?if_exists} ${createdBy.firstName?if_exists} 
								</div>
					   		</div>
				   		</div>
				   		<div class='row-fluid'>
							<div class='span6'>
								<span style="float: right">${uiLabelMap.Department}</span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;" id="createdByDepartment" name="createdByDepartment">
									${createdByDepartment.groupName?if_exists}
								</div>
					   		</div>
				   		</div>
						<div class='row-fluid'>
							<div class='span6'>
								<span>${uiLabelMap.LogRequiredByDate}</span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;" id="requiredByDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span6'>
								<span>${uiLabelMap.LogRequirementStartDate}</span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;" id="requirementStartDate">
								</div>
					   		</div>
						</div>
					</div>
					<div class='span4'>
						<div class='row-fluid hide'>
							<div class='span5'>
								<span>${uiLabelMap.EstimatedBudget}</span>
							</div>
							<div class="span7">	
								<div id="estimatedBudget" style="text-align: left;" class="green-label"></div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.Description}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="description"> ${requirement.description?if_exists}
								</div>
					   		</div>
						</div>
						<#if requirement.statusId == "REQ_APPROVED">
							<#if requirement.requirementTypeId == "EXPORT_REQUIREMENT">
							<div class='row-fluid' style="margin-bottom: 5px !important">
								<div class='span5'>
									<span>${uiLabelMap.ExportFromFacility}</span>
								</div>
								<div class="span7">
									<div class="green-label" style="text-align: left;" id="facilityId" name="facilityId"></div>
						   		</div>
							</div>
							<div class='row-fluid' style="margin-bottom: 5px !important">
								<div class='span5'>
									<span>${uiLabelMap.Address}</span>
								</div>
								<div class="span7">
									<div class="green-label" style="text-align: left;" id="contactMechId" name="contactMechId"></div>
						   		</div>
							</div>
							<#elseif requirement.requirementTypeId == "RECEIVE_REQUIREMENT">
							<div class='row-fluid' style="margin-bottom: 5px !important">
								<div class='span5'>
									<span>${uiLabelMap.ReceiveToFacility}</span>
								</div>
								<div class="span7">
									<div class="green-label" style="text-align: left;"  id="facilityId" name="facilityId"></div>
									<div class="green-label" style="text-align: left;"  id="facilityToName" name="facilityToName"></div>
						   		</div>
							</div>
							<div class='row-fluid' style="margin-bottom: 5px !important">
								<div class='span5'>
									<span class="asterisk">${uiLabelMap.Address}</span>
								</div>
								<div class="span7">
									<div class="green-label" style="text-align: left;" id="contactMechId" name="contactMechId"></div>
						   		</div>
							</div>
							<#else>
			            		<!-- another type of requirement -->
							</#if>
						</#if>
					</div>
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	</div>
</div>
<div class="row-fluid margin-top20">
	<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
	    ${uiLabelMap.ListProduct}
	    <a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:ReceiveReqObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
	</h4>
	<div><div id="jqxgridRequirementItem"></div></div>
</div>
<div class="row-fluid wizard-actions margin-top5 bottom-action">
	<button class="btn btn-small btn-primary btn-next" id="receiveProduct" data-last="${uiLabelMap.LogFinish}">
		<i class="fa-download"></i>
		${uiLabelMap.ProductReceiveProduct}
	</button>
</div>
