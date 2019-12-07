<script type="text/javascript">
	
	var settings= $('#EditTrainingClass').validate().settings;
	
	
	$.validator.addMethod('validateToday',function(value,element){
		if(value){
			var today= new Date();
			today.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>=today;
		}else{
			return true;
		}
		
	},'Great than today');
	
	$.validator.addMethod('greatThan',function(value,element,params){
		if($(params).val()){
			return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
		}else{
			var today= new Date();
			today.setHours(0,0,0,0)
			return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>=today;
		}
		
	},'great than begin');
	
	$.extend(settings,{
		rules:{
			fromDate_i18n:{
				validateToday:true
			},
			
			thruDate_i18n:{
				greatThan:'#EditTrainingClass_fromDate_i18n'
			}
		},
		messages:{
			fromDate_i18n:{
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			},
			thruDate_i18n:{
				greatThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDateOrToday)}'
			}
		}
	
	});
	
</script>