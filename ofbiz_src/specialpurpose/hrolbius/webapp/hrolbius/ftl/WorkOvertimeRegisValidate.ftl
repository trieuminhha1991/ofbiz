<script type="text/javascript">
		$.validator.addMethod('validateToDay',function(value,element){
			var now = new Date();
			now.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy")>=now;
		},'Greather than today');
		var setting=jQuery("#EditWorkOvertimeRegis").validate().settings;
			delete setting.rules.dateRegistration;
			$.extend(setting,{
				rules:{	
					dateRegistration_i18n:
					{
						validateToDay:true
					}
				}, 
				messages: {
					dateRegistration_i18n:
					{
						validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
					}
				}
			});
		 $('#EditWorkOvertimeRegis_reasonRegister').val('');
</script>
