<script type="text/javascript">
		var setting=jQuery("#AddSalaryStep").validate().settings;
		$.validator.addMethod("validateId", function (value, element) {
			return this.optional(element) || /^\S+$/i.test(value);
		}, "${uiLabelMap.NoWhiteSpaceNotify}");
			$.extend(setting,{
				rules:{	
					amount:
					{
						required: true,
						min:0,
						validateId: true
					}
				}, 
				messages: {
					amount:
					{	
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					}
				}
			});
		 
</script>
