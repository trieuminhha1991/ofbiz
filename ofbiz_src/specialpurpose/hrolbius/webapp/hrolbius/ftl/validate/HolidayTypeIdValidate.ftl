<script type="text/javascript">
		$.validator.addMethod("validateId", function (value, element) {
			if(value){
				return this.optional(element) || /^\S+$/i.test(value);
			}else{
				return true;
			}
		}, "${uiLabelMap.NoWhiteSpaceNotify}");
		var setting=$("#EditTypeHolidays").validate().settings;
			$.extend(setting,{
				rules:{	
					holidayTypeId:
					{
						validateId: true
					}
				}
			});
		
</script>
