<script type="text/javascript">
	var settings = $("#EditHolidayInYear").validate().settings;
	$.validator.addMethod('greatThan',function(value,element,params){
		if($(params).val()){
			return Date.parseExact(value,"dd/MM/yyyy") >= Date.parseExact($(params).val(),"dd/MM/yyyy");
		}else{
			return true;
		}
	},'great than value of params');
	$.extend(settings,{
		rules:{
			thruDate_i18n:{
				greatThan:"#EditHolidayInYear_fromDate_i18n"
			},
		},
		messages:{
			thruDate_i18n:{
				greatThan:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
			}
		}
	});
</script>