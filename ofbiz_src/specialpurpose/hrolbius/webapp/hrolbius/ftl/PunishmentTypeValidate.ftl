<script type="text/javascript">
		$.validator.addMethod("validateId", function (value, element) {
			return this.optional(element) || /^\S+$/i.test(value);
		}, "${uiLabelMap.NoWhiteSpaceNotify}");
		$.validator.addMethod("validateNumber", function (value, element) {
			return (this.optional(element) || /^\S+$/i.test(value))&&(value == parseInt(value));
		}, "${uiLabelMap.ErrorNumberRemindToWarning}");
		var setting=jQuery("#${formId}").validate().settings;
			delete setting.rules.punishmentTypeId;
			$.extend(setting,{
				errorElement: "div",
				errorClass: "style-block",
				rules:{	
					punishmentTypeId:
					{
						validateId: true
					},
					numberRemindToWarning:
					{
						validateNumber: true,
						min: 0
					}
				}, 
				messages: {
					numberRemindToWarning:
					{
						min: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					}
				}
			});
			
			 $('#listPunishmentType').validate({ // initialize the plugin
			        // other options
			 });

		    $("[name^=numberRemindToWarning_o").each(function () {
		        $(this).rules("add", {
		            required: true,
		            validateNumber: true,
		        });
		    });
		    
</script>
