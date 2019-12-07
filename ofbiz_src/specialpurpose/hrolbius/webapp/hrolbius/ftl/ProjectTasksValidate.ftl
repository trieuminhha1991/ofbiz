<script type="text/javascript">
		var setting=jQuery("#EditTask").validate().settings;
		$.validator.addMethod("nospecialcharacter", function(value, element) {
			if(value){
				return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d]+$/i.test(value);
			} else 
				return true;
		}, "Letters, numbers, and underscores only please");
		
		jQuery.validator.addMethod("greaterThan", 
				function(value, element, params) {
					if (value){
						return Date.parseExact(value,"dd/MM/yyyy") >= Date.parseExact($(params).val(),"dd/MM/yyyy");
					} else 
						return true;
				},'Must be greater than');
				
			$.extend(setting,{
				rules:{	
					sequenceNum:
					{
						min:1,
					},
					workEffortName:
					{
						nospecialcharacter: true
					},
					estimatedCompletionDate_i18n:{
						greaterThan: '#EditTask_estimatedStartDate_i18n'
					}
				}, 
				messages: {
					sequenceNum:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					workEffortName:{
						nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
					},
					estimatedCompletionDate_i18n:{
						greaterThan: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					}
				},
				errorElement: 'div',
			});
		 
</script>
