<script type="text/javascript">
		$.validator.addMethod("validateId", function (value, element) {
			return this.optional(element) || /^\S+$/i.test(value);
		}, "${uiLabelMap.NoWhiteSpaceNotify}");
		var setting=$("#AddSkillType").validate().settings;
			delete setting.rules.skillTypeId;
			$.extend(setting,{
				rules:{	
					skillTypeId:
					{
						validateId: true
					}
				}
			});
		
</script>
