<script type="text/javascript">
		var setting=jQuery("#AddEmplAgreementTerm").validate().settings;
			delete setting.rules.EditJobRequest_resourceNumber;
			jQuery.validator.addMethod("greaterThan", 
			function(value, element, params) {
				if(value){
					return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
				} else
					return true;
			},'Must be greater than');
			
			jQuery.validator.addMethod("greaterThanNumber", 
					function(value, element, params) {
						if(value){
							return value >= $(params).val();
						} else
							return true;
					},'Must be greater than');
			
			$.validator.addMethod('validateToDay',function(value,element){
				var now = new Date();
				now.setHours(0,0,0,0);
				if(value){
					return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >=now;
				} else
					return true;
			},'Greather than today');
			
			$.extend(setting,{
				rules:{	
					fromDate_i18n:{
						validateToDay:true
					},
					thruDate_i18n:{
						greaterThan:'#AddEmplAgreementTerm_fromDate_i18n'
					},
					textValue:{
						min: 1,
						greaterThanNumber: '#AddEmplAgreementTerm_termValue'
					}
					termValue:{
						min: 1
					}
				}, 
				messages: {
					fromDate_i18n:{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					},
					thruDate_i18n:{
						greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					},
					textValue:{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
						greaterThanNumber: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherTermValue)}'
					},
					termValue:{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					}
				}
			});
		 
</script>
