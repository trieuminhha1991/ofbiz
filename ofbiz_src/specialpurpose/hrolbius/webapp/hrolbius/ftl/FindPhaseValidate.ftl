<script type="text/javascript">
		var setting=jQuery("#AddPhase").validate().settings;
		$.validator.addMethod("nospecialcharacter", function(value, element) {
			if(value){
				return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d]+$/i.test(value);
			} else 
				return true;
		}, "Letters, numbers, and underscores only please");
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
					description:
					{
						nospecialcharacter: true
					},
				}, 
				messages: {
					sequenceNum:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					workEffortName:{
						nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
					},
					description:{
						nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
					}
				}
			});
			
			/* var sett= $('#ListPhases_o_0').validate().settings;
			$.extend(sett,{
				ignore: "",
				rules: {
					actualHours: {
						required: true,
					},
				},

				messages: {
					actualHours: {
						required: "Please provide a valid email.",
					},
				},
				
			}); */
			
</script>
