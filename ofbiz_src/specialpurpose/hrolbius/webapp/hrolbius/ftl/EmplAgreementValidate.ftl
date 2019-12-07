<script type="text/javascript">
jQuery(document).ready( function() {
	var setting=jQuery("#AddEmplAgreement").validate().settings;
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
	
	$.validator.addMethod("nospecialcharacter", function(value, element) {
		if(value){
			return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
		} else 
			return true;
	}, "Letters, numbers, and underscores only please");
	
	$.validator.addMethod("validateId", function(value, element) {
		if(value){
			return this.optional(element) || /^\w+$/i.test(value);
		} else 
			return true;
	}, "Letters, numbers, and underscores only please");
	
	$.extend(setting,{
		rules:{	
			fromDate_i18n:{
				greaterThan:'#AddEmplAgreement_agreementDate_i18n'
			},
			thruDate_i18n:{
				greaterThan:'#AddEmplAgreement_fromDate_i18n'
			},
			agreementDate_i18n:{
				validateToDay:true
			},
			description:{
				nospecialcharacter: true
			},
			textData:{
				nospecialcharacter: true
			},
			agreementId:{
				validateId: true
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
			description:{
				nospecialcharacter: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			},
			textData:{
				nospecialcharacter: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			},
			agreementId:{
				validateId: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
			}
		}
	});
});
		 
</script>
