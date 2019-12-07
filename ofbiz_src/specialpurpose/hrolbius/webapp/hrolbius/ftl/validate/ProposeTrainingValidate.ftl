<script type='text/javascript'>
	var settings= $('#CreateTrainingProposal').validate().settings;
	$.validator.addMethod('validateToday',function(value,element){
		var now = new Date();
		now.setHours(0,0,0,0);
		return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>=now;
	},'required great than today');
	
	$.validator.addMethod('greatThan',function(value,element,params){
		return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss")
	},'Great than params');
	$.extend(settings,{
		rules:{
			fromDate_i18n:{
				validateToday:true
			},
			thruDate_i18n:{
				greatThan:'#CreateTrainingProposal_fromDate_i18n'
			}
		},
		messages:{
			fromDate_i18n:{
				validateToday:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}'
			},
			thruDate_i18n:{
				greatThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
			}
		}
	});
</script>