<#include "script/reqEditRequirementInfoScript.ftl"/>
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="editRequirement" name="editRequirement">
	<div class="row-fluid">
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span >${uiLabelMap.RequirementType}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="requirementTypeId" style="text-align: left;"  name="requirementTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span >${uiLabelMap.RequirementPurpose}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="reasonEnumId" style="text-align: left;" name="reasonEnumId"></div>
				   		</div>
			   		</div>
			   		<#if requirement.facilityId?exists>
		   			<div class='row-fluid'>
						<div class='span5'>
							<span >${uiLabelMap.Facility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="originFacility" style="text-align: left;" name="originFacility"></div>
				   		</div>
					</div>
		   			<div class='row-fluid'>
						<div class='span5'>
							<span>${uiLabelMap.FacilityTo}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="destFacility" style="text-align: left;" name="destFacility"></div>
				   		</div>
					</div>
					</#if>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.LogRequirementStartDate}</span>
						</div>
						<div class="span7">
							<div id="requirementStartDate">
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span>${uiLabelMap.Description}</span>
						</div>
						<div class="span7">
							<textarea id="description" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px; float: left;" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>