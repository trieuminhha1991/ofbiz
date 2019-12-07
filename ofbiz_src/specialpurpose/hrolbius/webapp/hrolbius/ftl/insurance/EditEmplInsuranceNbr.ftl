<div class="row-fluid">
	<form action="<@ofbizUrl>AddEmplInsuranceNbr</@ofbizUrl>" name="EditEmplInsuranceNbr" id="EditEmplInsuranceNbr" class="form-horizontal" method="post">
		<div class="control-group no-left-margin">
			<input type="hidden" name="orgId" value="${orgId}">
			<label class="control-label">${uiLabelMap.EmployeeId}</label>
			<div class="controls">
				<#assign parametersIter = ["orgId"]>
				<@htmlTemplate.lookupField formName="EditEmplInsuranceNbr" name="partyId" 
		     		id="partyId" fieldFormName="LookupEmplInOrg" targetParameterIter=parametersIter/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.InsuranceType}:</label>
			<div class="controls">
				<select name="insuranceTypeId">
					<#list insuranceTypeList as type>
						<option value="${type.insuranceTypeId}">${type.description}</option>
					</#list>
				</select>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.InsuranceNbr}:</label>
			<div class="controls">
				<input type="text" name="insuranceNumber">
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.CommonFromDate}:</label>
			<div class="controls">
				<@htmlTemplate.renderDateTimeField name="fromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.CommonThruDate}:</label>
			<div class="controls">
				<@htmlTemplate.renderDateTimeField name="thruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">&nbsp;</label>
			<div class="controls">
				<button class="btn btn-small btn-primary icon-ok">
					${uiLabelMap.CommonSubmit}
				</button>
			</div>
		</div>
	</form>
</div>