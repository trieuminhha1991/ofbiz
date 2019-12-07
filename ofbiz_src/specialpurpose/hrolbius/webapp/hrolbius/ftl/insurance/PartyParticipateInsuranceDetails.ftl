<div class="row-fluid">
	<form class="form-horizontal">
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.EmployeeName}
			</label>
			<div class="controls">
				<span>${partyName?if_exists} [${parameters.partyId?if_exists}]</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.SocialInsuranceNbr}:</label>
			<div class="controls">
				${socialInsuranceNbr?if_exists}
				&nbsp;
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.HealthInsuranceNbr}:</label>
			<div class="controls">
				${healthInsuranceNbr?if_exists}
				&nbsp;
			</div>
		</div>
		
		<#list insuranceTypeList as tempInsuranceType>	
			<div class="control-group no-left-margin">
				<#assign insuranceType = delegator.findOne("InsuranceType", Static["org.ofbiz.base.util.UtilMisc"].toMap("insuranceTypeId", tempInsuranceType.insuranceTypeId), false)> 
				<label class="control-label" for="">${uiLabelMap.InsuranceType}:</label>
				<div class="controls">
					<span>${insuranceType.description}</span>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<#assign participateStatusList = delegator.findByAnd("PartyParticipateInsuranceAndStatus", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", parameters.partyId, "insuranceTypeId", tempInsuranceType.insuranceTypeId), null, false)>
				<label class="control-label" for="">${uiLabelMap.CommonStatus}:</label>
				<div class="controls">
					<span>
						<#if participateStatusList?has_content>
							<#assign participateStatus = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(participateStatusList)>
							<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", participateStatus.statusId), false)>
							${statusItem.description?if_exists}
						<#else>
							${uiLabelMap.NotParticipating}	
						</#if>	
					</span>
				</div>
			</div>
		</#list>
	</form>
</div>