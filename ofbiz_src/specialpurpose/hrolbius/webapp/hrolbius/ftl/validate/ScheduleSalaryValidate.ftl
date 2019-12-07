<script type='text/javascript'>
	var settings= $('#ScheduleSalary').validate().settings;
	delete settings.rules.expireTime_i18n;
	delete settings.messages.expireTime_i18n;
	$.validator.addMethod('greatThan',function(value, element, params){
		return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
	},'Great than fromdate');
	$.extend(settings,{
		rules:{
			expireTime_i18n:{
				greatThan:'#ScheduleSalary_startTime_i18n'
			}
		},
		messages:{
			expireTime_i18n:{
				greatThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
			}
		}
	});
</script>