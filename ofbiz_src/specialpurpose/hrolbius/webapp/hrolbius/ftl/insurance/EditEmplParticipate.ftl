<div class="row-fluid">
	<form action="<@ofbizUrl>AddEmplParticipateInsurance</@ofbizUrl>" name="EditEmplParticipate" id="EditEmplParticipate" class="form-horizontal" method="post">
		<div class="control-group no-left-margin">
			<label class="control-label asterisk" for="partyId">${uiLabelMap.EmployeeId}:</label>
			<div class="controls">	
				<input name="reportId" value="${parameters.reportId?if_exists}" type="hidden"/>
				<#assign parametersIter = ["reportId"]>
				<@htmlTemplate.lookupField formName="EditEmplParticipate" name="partyId" 
		     		id="partyId" fieldFormName="LookupEmplParicipateInsurance" targetParameterIter=parametersIter/>
		     	<span id="error"></span>	
	     	</div>	
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.InsuranceType}:</label>
			<div class="controls">
				<table style="margin-top: 18px">
				<tr>
					<#list insuranceType as type>
						<td style="padding-right: 20px">
							<label>
								<input name="insuranceTypeId" type="checkbox" value="${type.insuranceTypeId}" <#if type.isCompulsory?exists && type.isCompulsory == "Y"> checked="checked" </#if>/>
								<span class="lbl">${type.description} </span>
							</label>
						</td>
					</#list>
				</tr>
				</table>
			</div>
		</div>		
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.ParticipateFrom}:</label>
			<div class="controls">
				<@htmlTemplate.renderDateTimeField name="fromDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.ParticipateTo}:</label>
			<div class="controls">
				<@htmlTemplate.renderDateTimeField name="thruDate" value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.InsuranceParticipateType}:</label>
			<div class="controls">
				<select name="insuranceParticipateTypeId">
					<#list insuranceParticipateType as participateType>	
						<option value="${participateType.insuranceParticipateTypeId}">${participateType.description}</option>
					</#list>
				</select>
			</div>
		</div>
		
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.SocialInsuranceNbr}:</label>
			<div class="controls">
				<span id="SocialInsuranceNbr">&nbsp;</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.HealthInsuranceNbr}:</label>
			<div class="controls">
				<span id="HealthInsuranceNbr">&nbsp;</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.HRNotes}:</label>
			<div class="controls">
				<textarea rows="3" cols="10" name="comments"></textarea>
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
jQuery(function() {
	jQuery("input[name='partyId']").bind("lookupIdChange", function(event){
		var partyId = $(this).val();
		jQuery.ajax({
			url:"<@ofbizUrl>getInsuranceNbr</@ofbizUrl>",
			type: "POST",
			data: {partyId: partyId},
			dateType:'json',
			success: function(data){				
				if(data._EVENT_MESSAGE_){
					if(data.socialInsuranceNbr){
						jQuery("#SocialInsuranceNbr").html(data.socialInsuranceNbr);	
					}else{
						jQuery("#SocialInsuranceNbr").html("${uiLabelMap.NotSetting}");
					}
					if(data.healthInsuranceNbr){
						jQuery("#HealthInsuranceNbr").html(data.healthInsuranceNbr);	
					}else{
						jQuery("#HealthInsuranceNbr").html("${uiLabelMap.NotSetting}");
					}
				}else{
					jQuery("#SocialInsuranceNbr").html("${uiLabelMap.ErrorWhenRetieveData}");
					jQuery("#HealthInsuranceNbr").html("${uiLabelMap.ErrorWhenRetieveData}");
				}
			}
		});
	});	
});
jQuery('#EditEmplParticipate').validate({
	errorElement: 'span',
	errorClass: 'help-inline',
	errorLabelContainer: "#error",
	focusInvalid: false,
	rules: {
		partyId: {
			required: true,
			validateId: true
		}			
	},

	messages: {
		partyId: {
			required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",		
		},			
	},

	invalidHandler: function (event, validator) { //display error alert on form submit   
		$('.alert-error', $('.login-form')).show();
	},

	highlight: function (e) {
		$(e).closest('.control-group').removeClass('info').addClass('error');
	},

	success: function (e) {
		$(e).closest('.control-group').removeClass('error').addClass('info');
		$(e).remove();
	},

	submitHandler: function (form) {
		form.submit();
	},
	invalidHandler: function (form) {
	}
});
</script>