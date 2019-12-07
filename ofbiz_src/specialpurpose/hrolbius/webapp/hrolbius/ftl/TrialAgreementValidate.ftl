<script type="text/javascript">
jQuery(document).ready( function() {
	var setting=jQuery("#AddTrialAgreement").validate().settings;
	jQuery.validator.addMethod("greaterThan", 
	function(value, element, params) {
		if (value){
			return Date.parseExact(value,"dd/MM/yyyy") >= Date.parseExact($(params).val(),"dd/MM/yyyy");
		} else 
			return true;
	},'Must be greater than');
	
	$.validator.addMethod('validateToDay',function(value,element){
		var now = new Date();
		now.setHours(0,0,0,0);
		if (value){
			return Date.parseExact(value,"dd/MM/yyyy") <=now;
		} else
			return true;
	},'Greather than today');
	
	$.extend(setting,{
		rules:{	
			fromDate_i18n:{
				greaterThan:'#AddTrialAgreement_agreementDate_i18n'
			},
			thruDate_i18n:{
				greaterThan:'#AddTrialAgreement_fromDate_i18n'
			},
			agreementDate_i18n:{
				validateToDay:true
			},
			trialSalaryRate:{
				min: 1
			},
			salary:{
				min: 1
			}
		}, 
		messages: {
			fromDate_i18n:{	
				greaterThan: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherAgreementDate)}"
			},
			thruDate_i18n:{
				greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
			},
			agreementDate_i18n:{
				validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueSmallerToDay)}"
			},
			trialSalaryRate:{
				min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
			},
			salary:{
				min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
			}
		}
	});	
})
</script>
