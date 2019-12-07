<script type="text/javascript">
		$.validator.addMethod("validateSpace", function (value, element) {
			return this.optional(element) || /^\S+$/i.test(value);
		}, "${uiLabelMap.NoWhiteSpaceNotify}");
		var setting=jQuery("#AddPartySkills").validate().settings;
			$.extend(setting,{
				rules:{	
					rating:
					{
						min:0,
						max:99,
						validateSpace: true
					},
					skillLevel:
					{
						min:0,
						validateSpace: true
					},
					yearsExperience:
					{
						min:0,
					}	
				}, 
				messages: {
					rating:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
						max:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue1)}"
					},
					skillLevel:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
					},
					yearsExperience:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
					},
				}
			});
		 
</script>
