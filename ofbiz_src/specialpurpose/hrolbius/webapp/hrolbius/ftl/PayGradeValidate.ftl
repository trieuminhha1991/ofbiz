<script type="text/javascript">
		$.validator.addMethod("validateId", function (value, element) {
			return this.optional(element) || /^\S+$/i.test(value);
		}, "${uiLabelMap.NoWhiteSpaceNotify}");
		var setting=$("#EditPayGrade").validate().settings;
			delete setting.rules.payGradeId;
			delete setting.rules.payGradeName;
			$.extend(setting,{
				rules:{	
					payGradeId:
					{
						validateId: true
					}
				}
					
			});
		
</script>
