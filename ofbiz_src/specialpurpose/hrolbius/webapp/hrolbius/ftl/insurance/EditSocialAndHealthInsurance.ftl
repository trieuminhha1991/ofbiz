<div class="row-fluid">
	<#if !socialInsurance?exists>
		<#assign socialInsurance=delegator.makeValue("PartyInsurance")>
	</#if>
	<#if !healthInsurance?exists>
		<#assign healthInsurance=delegator.makeValue("PartyInsurance")>
	</#if>
	<div class="span12">
		<div class="row-fuild">
			<form action="<@ofbizUrl>updateSocialAndHealthInsurance</@ofbizUrl>" name="SocialAndHealthInsurance" id="SocialAndHealthInsurance" class="form-horizontal" method="post">
				<input type="hidden" value="${person.partyId}" name="partyId">	
				<div class="control-group no-left-margin">
					<label class="control-label ">${uiLabelMap.EmployeeName}</label>
					<div class="controls">
						${person.lastName?if_exists} ${person.middleName?if_exists} ${person.firstName?if_exists} [${person.partyId}]
					</div> 
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label ">${uiLabelMap.SocialInsuranceNbr}</label>
					<div class="controls">
						<input type="text" name="socialInsuranceNbr" value="${socialInsurance.insuranceNumber?if_exists}">
					</div>				
				</div>										
				<div class="control-group no-left-margin">
					<label class="control-label" for="">${uiLabelMap.ParticipateFrom}</label>
					<#if socialInsurance.fromDate?exists>
						<#assign tempsocialFromDate = socialInsurance.fromDate?string["yyyy-MM-dd"]>
					</#if>
					<div class="controls">
						<@htmlTemplate.renderDateTimeField name="socialInsuranceFromDate" value="${tempsocialFromDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="">${uiLabelMap.CommonThruDate}</label>
					<div class="controls">
						<#if socialInsurance.thruDate?exists>
							<#assign tempsocialThruDate = socialInsurance.fromDate?string["yyyy-MM-dd"]>
						</#if>
						<@htmlTemplate.renderDateTimeField name="socialInsuranceThruDate" value="${tempsocialThruDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>								
					</div>	
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label ">${uiLabelMap.HealthInsuranceNbr}</label>
					<div class="controls">
						<input type="text" name="healthInsuranceNbr" value="${healthInsurance.insuranceNumber?if_exists}">
					</div>	
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label" for="">${uiLabelMap.CommonFromDate}</label>
					<div class="controls">
						<#if healthInsurance.fromDate?exists>
							<#assign temphealthFromDate = healthInsurance.fromDate?string["yyyy-MM-dd"]>
						</#if>
						<@htmlTemplate.renderDateTimeField name="healthInsuranceFromDate" value="${temphealthFromDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd" size="25" maxlength="30" id="healthFromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</div>
				</div>	
				<div class="control-group no-left-margin">
					<label class="control-label" for="">${uiLabelMap.DateExpire}</label>
					<div class="controls">
						<#if healthInsurance.thruDate?exists>
							<#assign temphealthThruDate = healthInsurance.thruDate?string["yyyy-MM-dd"]>
						</#if>
						<@htmlTemplate.renderDateTimeField name="healthInsuranceThruDate" value="${temphealthThruDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd" size="25" maxlength="30" id="healthThruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.MedicalPlace}</label>
					<div class="controls">
						<@htmlTemplate.lookupField formName="SocialAndHealthInsurance" name="partyHealthCareId" 
				     				id="partyHealthCareId" fieldFormName="LookupHealthCareProviderOrg" className="partyHealthCareId"/>
				     	<a href="#${dataToggleModalId}" class="icon-plus-sign open-sans" data-toggle="modal" role="button" id="${createNewLinkId}">${uiLabelMap.CommonAddNew}</a>			
					</div>
				</div>			
				<div class="control-group no-left-margin">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button class="btn btn-small btn-primary icon-ok">
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>					
			</form>
		</div>
	</div>
</div>