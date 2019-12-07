<div class="row-fluid">
	<form action="<@ofbizUrl>SuspendEmplParticipateInsurance</@ofbizUrl>" name="EditSuspendEmplParticipate" id="EditSuspendEmplParticipate" class="form-horizontal" method="post">
		<div class="control-group no-left-margin">
			<label class="control-label asterisk" for="partyId">${uiLabelMap.EmployeeId}:</label>
			<div class="controls">	
				<input name="reportId" value="${parameters.reportId?if_exists}" type="hidden"/>
				<#assign parametersIter = ["reportId"]>
				<@htmlTemplate.lookupField formName="EditSuspendEmplParticipate" name="partyId" 
		     		id="partyId" fieldFormName="LookupSuspendEmplParicipateInsurance" targetParameterIter=parametersIter/>
	     	</div>	
		</div>
		
		<div class="control-group no-left-margin">
			<label class="control-label asterisk" for="">${uiLabelMap.CommonFromDate}:</label>
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
			<label class="control-label" for="">${uiLabelMap.InsuranceType}:</label>
			<div class="controls">
				<div id="insuranceTypeIdDiv">
					&nbsp;
				</div>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.InsuranceParticipateType}:</label>
			<div class="controls">
				<select name="insuranceParticipateTypeId" id="EditSuspendEmplParticipate_insuranceParticipateTypeId">
					<#list insuranceParticipateType as participateType>	
						<option value="${participateType.insuranceParticipateTypeId}">${participateType.description}</option>
					</#list>
				</select>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.SuspendParticipateInsuranceReason}:</label>
			<div class="controls">
				<select name="suspendReasonId" id="EditSuspendEmplParticipate_suspendReasonId">
				</select>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label" for="">${uiLabelMap.HRNotes}:</label>
			<div class="controls">
				<textarea rows="3" cols="10" name="comments"></textarea>
			</div>
		</div>
		<div class="control-group no-left-margin ">
			<label>
				&nbsp;
			</label>
			<div class="controls">
				<button type="submit" class="btn btn-small btn-primary icon-ok">
					${uiLabelMap.CommonSubmit}
				</button>
			</div>	
		</div>
	</form>
</div>
<style>
	#insuranceTypeIdDiv label{
		margin-top: 0px !important;
	}
</style>
<script type="text/javascript">
jQuery(function() {
	jQuery("input[name='partyId']").bind("lookupIdChange", function(event){
		var partyId = $(this).val();
		jQuery.ajax({
			url:"<@ofbizUrl>getInsuranceTypePartyParticipate</@ofbizUrl>",
			type: "POST",
			data: {partyId: partyId},
			dateType:'json',
			success: function(data){
				jQuery("#insuranceTypeIdDiv").empty();
				if(data._EVENT_MESSAGE_){
					var listInsuranceParicipate = data.listInsuranceParicipate;
					for(var i in listInsuranceParicipate){
						var label = jQuery("<label></label>");
						label.append("<input name='insuranceTypeId' type='checkbox' value='" + listInsuranceParicipate[i].insuranceTypeId+ "' checked='checked'/>")
						label.append("<span class='lbl'>" + listInsuranceParicipate[i].insuranceTypeId +"</span>");
						jQuery("#insuranceTypeIdDiv").append(label);
					}
				}else{
					
				}
			}
		});
	});
});
	
	$('#EditSuspendEmplParticipate').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		rules:{
			partyId:{
				required:true
			},
			fromDate_i18n:{
				required:true
			}
			
		},
		
		messages:{
			partyId:{
				required:"${uiLabelMap.CommonRequired}"
			},
			fromDate_i18n:{
				required:"${uiLabelMap.CommonRequired}"
			}
		}
	});
</script>