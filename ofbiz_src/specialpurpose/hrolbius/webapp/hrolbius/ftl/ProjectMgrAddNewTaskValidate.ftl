<script type="text/javascript">
		var setting=jQuery("#EditTask").validate().settings;
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
				if(value){
					return Date.parseExact(value,"dd/MM/yyyy") >=now;
				} else 
					return true;
			},'Greather than today');
			
			$.validator.addMethod("nospecialcharacter", function(value, element) {
				if(value){
					return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
				} else 
					return true;
			}, "Letters, numbers, and underscores only please");
			
			$.extend(setting,{
				rules:{
					workEffortName:{
						nospecialcharacter: true
					},
					description:{
						nospecialcharacter: true
					},
					sequenceNum:{
						min:1,
					},
					estimatedStartDate_i18n:{
						validateToDay:true
					},
					estimatedCompletionDate_i18n:{
						greaterThan:'#EditTask_estimatedStartDate_i18n'
					},
					estimatedHours:
					{
						min:1
					}
				}, 
				messages: {
					workEffortName:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					description:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					},
					sequenceNum:{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					estimatedHours:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					estimatedStartDate_i18n:{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					},
					estimatedCompletionDate_i18n:{
						greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					}
				}
			});
		 
</script>
