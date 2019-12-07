<script type="text/javascript">
		var setting=jQuery("#EditProject").validate().settings;
		$.validator.addMethod("nospecialcharacter", function(value, element) {
			if(value){
				return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d]+$/i.test(value);
			} else 
				return true;
		}, "${uiLabelMap.NoSpecialCharacterNotify}");	
		
		jQuery.validator.addMethod("greaterThan", 
				function(value, element, params) {
					if (value){
						return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
					} else 
						return true;
				},'Must be greater than');
				
				$.validator.addMethod('validateToDay',function(value,element){
					var now = new Date();
					now.setHours(0,0,0,0);
					if (value){
						return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") <=now;
					} else
						return true;
				},'Greather than today');
			$.extend(setting,{
				rules:{	
					workEffortName:
					{
						nospecialcharacter:true
					},
					description:
					{
						nospecialcharacter:true
					},
					actualStartDate_i18n:
					{
						validateToDay: true
					},
					actualCompletionDate_i18n:
					{
						greaterThan: '#EditProject_actualStartDate_i18n'
					},
					estimatedCompletionDate_i18n:
					{
						greaterThan: '#EditProject_estimatedStartDate_i18n'
					},
					emailAddress:
					{
						email: true
					}
				},
				messages: {
					actualStartDate_i18n:{	
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueSmallerToDay)}",
					},
					actualCompletionDate_i18n:{
						greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					},
					estimatedCompletionDate_i18n:{
						greaterThan:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
					},
					emailAddress:
					{
						email: "${StringUtil.wrapString(uiLabelMap.EmailNotify)}"
					}
				}
			});
		 
</script>
