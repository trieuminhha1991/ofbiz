<script type="text/javascript">
		var setting=jQuery("#AddKPI").validate().settings;
			jQuery.validator.addMethod("greaterThan", 
			function(value, element, params) {
			        return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
			},'Must be greater than');
			
			$.validator.addMethod('validateToDay',function(value,element){
				var now = new Date();
				now.setHours(0,0,0,0);
				return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >=now;
			},'Greather than today');
			
			$.validator.addMethod("nospecialcharacter", function(value, element) {
				if(value){
					return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
				} else 
					return true;
			}, "Letters, numbers, and underscores only please");
			
			$.extend(setting,{
				rules:{	
					description:{
						nospecialcharacter: true
					},
					fromDate_i18n:{
						validateToDay:true
					},
					thruDate_i18n:{
						greaterThan:'#AddKPI_fromDate_i18n'
					}
				}, 
				messages: {
					fromDate_i18n:{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					},
					thruDate_i18n:{
						greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					},
					description:{
						nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
					}
				}
			});
		 
</script>
