<div class="row-fluid">
	<#if partyInsuranceSalary?exists>
		<#assign url = "updatePartyInsuranceSalary">
	<#else>
		<#assign url = "createPartyInsuranceSalary">	
	</#if>
	<form action="<@ofbizUrl>${url}</@ofbizUrl>" name="EditPartyInsuranceSalary" id="EditPartyInsuranceSalary" class="form-horizontal" method="post">
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.EmployeeName}
			</label>
			<div class="controls">
				<span>${partyName?if_exists} [${parameters.partyId?if_exists}]</span>
				<input type="hidden" name="partyId" value="${parameters.partyId}">
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">
				${uiLabelMap.ApplyFromDate}
			</label>
			<div class="controls">
				<span>
					<#if partyInsuranceSalary?exists>
						${partyInsuranceSalary.fromDate?date}
						<input type="hidden" name="fromDate" value="${partyInsuranceSalary.fromDate}">
					<#else>
						<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" className="" 
							alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" 
							size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" 
							localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" 
							ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</#if>	
				</span>	
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.ApplyThruDate}
			</label>
			<div class="controls">
				<span>					 
					<#if partyInsuranceSalary?exists>
						<#assign thruDate = partyInsuranceSalary.thruDate?if_exists>
					</#if>
					<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" className="" 
						alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${thruDate?if_exists}" 
						size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" 
						localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" 
						ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
						
				</span>	
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">
				${uiLabelMap.InsuranceSalary}
			</label>
			<div class="controls">
				<span>
					<input type="text" name="salaryInsurance" id="salaryInsurance" value="<#if partyInsuranceSalary?exists>${partyInsuranceSalary.salaryInsurance?if_exists}</#if>">
				</span>
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
<script type="text/javascript">
$(function() {
	jQuery("#salaryInsurance").maskMoney({allowZero: false, thousands: ".", precision: "0"});
});
</script>