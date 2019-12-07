<script type="text/javascript">
		var setting=jQuery("#createTimeEntry").validate().settings;
			
			$.validator.addMethod('validateToDay',function(value,element){
				var now = new Date();
				now.setHours(0,0,0,0);
				if(value){
					return Date.parseExact(value,"dd/MM/yyyy") >=now;
				} else 
					return true;
			},'Greather than today');
			
			$.validator.addMethod("nospecialcharacter", function(value, element) {
				if(value){
					return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
				} else 
					return true;
			}, "Letters, numbers, and underscores only please");
			
			$.extend(setting,{
				rules:{	
					fromDate_i18n:{
						validateToDay:true
					},
					hours:
					{
						min:1
					},
					comments:{
						nospecialcharacter: true
					}
				}, 
				messages: {
					hours:
					{
						min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}"
					},
					fromDate_i18n:{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					},
					comments:{
						nospecialcharacter: "${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}"
					}
				},
				errorElement: 'div',
		    	errorClass: "invalid",
		    	errorPlacement: function(error, element) {
					element.addClass("border-error");
		    		if (element.parent() != null ){   
						element.parent().find("button").addClass("button-border");     			
		    			error.appendTo(element.parent());
					}
		    	  },
			});
		 
</script>
