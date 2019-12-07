<script type="text/javascript">
		var setting=jQuery("#AddTaskSkill").validate().settings;
			
			$.extend(setting,{
				rules:{	
					estimatedNumPeople:
					{
						min:1
					},
					estimatedDuration:{
						min:1
					},
					estimatedCost:{
						min:1
					}
				}, 
				messages: {
					estimatedNumPeople:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					estimatedDuration:{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					estimatedCost:{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					}
				}
			});
		 
</script>
