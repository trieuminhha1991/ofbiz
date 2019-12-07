<script type="text/javascript">
	//get setting validate existed of form
	var settings= $('#EditEmplLeave').validate().settings;
	delete settings.rules.fromDate;
	delete settings.rules.thruDate;
	delete settings.messages.thruDate;
	//Create method compare fromDate and ThruDate
	jQuery.validator.addMethod("greaterThan", 
		function(value, element, params) {
		        return Date.parseExact(value,"dd/MM/yyy") >= Date.parseExact($(params).val(),"dd/MM/yyy");
		},'Must be greater than');
		
	//Create method compare fromDate and To Day
	$.validator.addMethod('validateToDay',function(value,element){
		var now = new Date();
		now.setHours(0,0,0,0);
		return Date.parseExact(value,"dd/MM/yyyy")>=now;
	},'Greather than today');
	
	$.validator.addMethod("nospecialcharacter", function(value, element) {
		if(value){
			return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
		} else 
			return true;
	}, "Letters, numbers, and underscores only please");
	
	
	// add rule for field in form 
	
	$.extend(settings,{
		rules:{
			fromDate_i18n:{
				validateToDay:true				
			},
			thruDate_i18n:{
				greaterThan:'#EditEmplLeave_fromDate_i18n'
			},
			description:{
				nospecialcharacter: true
			}
		},
		messages:{
			fromDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}'
			},
			thruDate_i18n:{
				greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
			},
			description:{
				nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
			}
		}
	});
</script>