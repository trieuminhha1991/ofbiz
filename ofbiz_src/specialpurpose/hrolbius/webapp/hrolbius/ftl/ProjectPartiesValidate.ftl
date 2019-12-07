<script type="text/javascript">
		var setting=jQuery("#AddWorkEffortPartyAssign").validate().settings;
		jQuery.validator.addMethod("greaterThan", 
				function(value, element, params) {
					if (value){
						return Date.parseExact(value,"dd/MM/yyyy") >= Date.parseExact($(params).val(),"dd/MM/yyyy");
					} else 
						return true;
				},'Must be greater than');
		
		$.validator.addMethod('validateToDay',function(value,element){
			var now = new Date();
			now.setHours(0,0,0,0);
			if (value){
				return Date.parseExact(value,"dd/MM/yyyy") >=now;
			} else
				return true;
		},'Greather than today');
				
			$.extend(setting,{
				rules:{	
					fromDate_i18n:
					{
						validateToDay: true
					},
					thruDate_i18n:
					{
						greaterThan: '#AddWorkEffortPartyAssign_fromDate_i18n'
					}
				}, 
				messages: {
					fromDate_i18n:{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					},
					thruDate_i18n:{
						greaterThan: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
					}
				},
				errorElement: 'div',
				errorPlacement: function(error, element) {
		    		if (element.parent() != null ){   
		    			error.appendTo(element.parent());
					}
		    	 },
			});
		 
</script>
