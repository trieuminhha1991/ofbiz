<script type='text/javascript'>
	var settings= $('#EditEmpPosTypeSalary').validate().settings;
	
	$.extend(settings,{
		rules:{
			rateAmount:{
				number:true,
				min:0
			}
		},
		messages:{
			rateAmount:{
				number:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueNumber)}',
				min:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}'
			}
		}
		
	});
</script>