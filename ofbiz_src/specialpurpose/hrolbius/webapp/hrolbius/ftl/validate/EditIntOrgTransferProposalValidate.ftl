<script type="text/javascript">
$( document ).ready(function() {
	var settings= $('#CreateJobTransferProposal').validate().settings;
	$.validator.addMethod('validateToday',function(value,element){
		if(value){
			var today= new Date();
			today.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy")>=today;
		}else{
			return true;
		}
		
	},'Great than today');
	
	$.validator.addMethod('greatThan',function(value,element,params){
		if($(params).val()){
			return Date.parseExact(value,"dd/MM/yyyy")>= Date.parseExact($(params).val(),"dd/MM/yyyy");
		}else{
			var today= new Date();
			today.setHours(0,0,0,0)
			return Date.parseExact(value,"dd/MM/yyyy")>=today;
		}
		
	},'great than begin');
	delete settings.rules.dateLeave_i18n;
	delete settings.messages.dateLeave_i18n;
	delete settings.rules.dateMoveTo_i18n;
	delete settings.messages.dateMoveTo_i18n;
	$.extend(settings,{
		rules:{
			dateLeave_i18n:{
				validateToday:true
			},
			dateMoveTo_i18n:{
				greatThan:'#CreateJobTransferProposal_dateLeave_i18n'
			}
		},
		messages:{
			dateLeave_i18n:{
				validateToday:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
			},
			dateMoveTo_i18n:{
				greatThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDateOrToday)}'
			}
		}
		
	});
});
	
</script>