<script type="text/javascript">
		var setting=jQuery("#EditPerfReviewItemType").validate().settings;
			jQuery.validator.addMethod("greaterThan", 
			function(value, element, params) {
			        return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
			},'Must be greater than');
			
			$.validator.addMethod('validateToDay',function(value,element){
				var now = new Date();
				now.setHours(0,0,0,0);
				return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >=now;
			},'Greather than today');
			
			$.extend(setting,{
				rules:{	
					weight:{
						min: 0,
						max: 99
					},
					fromDate_i18n:{
						validateToDay:true
					},
					thruDate_i18n:{
						greaterThan:'#EditPerfReviewItemType_fromDate_i18n'
					}
				}, 
				messages: {
					weight:{
						min: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
						max: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue1)}"
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
