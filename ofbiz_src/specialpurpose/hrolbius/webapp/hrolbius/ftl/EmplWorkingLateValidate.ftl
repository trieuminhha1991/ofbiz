<script type="text/javascript">
		var setting=jQuery("#EditEmplWorkingLate").validate().settings;
			delete setting.rules.dateWorkingLate_i18n;
			delete setting.rules.delayTime;
			
			$.validator.addMethod('validateDateWorkingLate',function(value,element){
				var now = new Date();
				return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") <= now;
			},'Greather than today');
			
			$.validator.addMethod("nospecialcharacter", function(value, element) {
				if(value){
					return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d]+$/i.test(value);
				} else 
					return true;
			}, "Letters, numbers, and underscores only please");
			
			$.extend(setting,{
				rules:{	
					dateWorkingLate_i18n:{
						validateDateWorkingLate:true
					},
					delayTime:
					{
						min:1
					},
					reason:
					{
						nospecialcharacter: true
					}
				}, 
				messages: {
					delayTime:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					dateWorkingLate_i18n:{
						validateDateWorkingLate:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueSmallerToDay)}"
					},
					reason:
					{
						nospecialcharacter:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredNotSpecialCharacter)}"
					}
				}
			});
		 
</script>
