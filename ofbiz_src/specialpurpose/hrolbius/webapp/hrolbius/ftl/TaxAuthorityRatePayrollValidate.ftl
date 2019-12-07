<script type="text/javascript">
		var setting=jQuery("#AddTaxAuthorityRatePayroll").validate().settings;
			jQuery.validator.addMethod("greaterThanValue", 
				function(value, element, params) {
					if (value){
						return value >= $(params).val();
					} else
						return true;
				},'Must be greater than');
		
			jQuery.validator.addMethod("greaterThan", 
			function(value, element, params) {
				if (value){
					return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
				} else
					return true;
			},'Must be greater than');
			
			$.validator.addMethod('validateToDay',function(value,element){
				var now = new Date();
				now.setHours(0,0,0,0);
				if (value){
					return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >=now;
				} else
					return true;
			},'Greather than today');
			
			$.validator.addMethod("noWhiteSpaceValidate", function(value, element) {
				if (value){
					return this.optional(element) || /^\S+$/i.test(value);
				} else 
					return true;
			}, "${uiLabelMap.NoWhiteSpaceValidate}");
			
			$.extend(setting,{
				rules:{	
					fromValue:{
						min: 0
					},
					thruValue:{
						min: 0,
						greaterThanValue: '#AddTaxAuthorityRatePayroll_fromValue'
					},
					taxPercentage:{
						min: 0,
						max: 99,
						noWhiteSpaceValidate: true
					},
					fromDate_i18n:{
						validateToDay:true
					},
					thruDate_i18n:{
						greaterThan:'#AddTaxAuthorityRatePayroll_fromDate_i18n'
					},
				}, 
				messages: {
					fromValue:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					thruValue:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
						greaterThanValue:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherValue)}'
					},
					taxPercentage:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
						max:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue1)}",
					},
					fromDate_i18n:{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					},
					thruDate_i18n:{
						greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					}
				}
			});
		 
</script>
