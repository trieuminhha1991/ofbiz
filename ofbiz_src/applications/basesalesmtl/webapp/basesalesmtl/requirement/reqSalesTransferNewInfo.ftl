<style type="text/css">
	.facilityIdContainer {
		display:none;
	}
</style>
<div style="position:relative">
	<form class="form-horizontal form-window-content-custom" id="initRequirementEntry" name="initRequirementEntry" method="post" action="#">
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.RequirementType}</label>
					</div>
					<div class="span7">
						<div id="requirementTypeId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.RequirementPurpose}</label>
					</div>
					<div class="span7">
						<div id="reasonEnumId"></div>
			   		</div>
				</div>
				<#--
				<div class='row-fluid hide'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSRequiredByDate}</label>
					</div>
					<div class="span7">
						<div id="requiredByDate"></div>
			   		</div>
				</div>
				-->
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSCustomer}</label>
					</div>
					<div class="span7">
						<div id="customerId">
							<div id="customerGrid"></div>
						</div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class='span5'>
						<label class="required">${uiLabelMap.BSCustomerAddress}</label>
					</div>
					<div class="span7">
						<div id="customerContactMechId">
							<div id="customerContactMechGrid"></div>
						</div>
			   		</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSRequirementStartDate}</label>
					</div>
					<div class="span7">
						<div id="requirementStartDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSDescription}</label>
					</div>
					<div class="span7">
						<textarea id="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
			   		</div>
				</div>
				<div class='row-fluid hide'>
					<div class='span5'>
						<label class="required">${uiLabelMap.FacilityFrom}</label>
					</div>
			   		<div class="span7">
						<div id="facilityIdDDB">
							<div id="facilityIdGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid hide'>
					<div class='span5'>
						<label class="required">${uiLabelMap.FacilityTo}</label>
					</div>
					<div class="span7">
						<div id="destFacilityId"></div>
			   		</div>
				</div>
			</div>
		</div><!--.row-fluid-->
	</form>
</div>
<#include "script/reqSalesTransferNewInfoScript.ftl"/>