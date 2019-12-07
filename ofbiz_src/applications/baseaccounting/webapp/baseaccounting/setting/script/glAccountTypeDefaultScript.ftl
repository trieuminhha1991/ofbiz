<#assign glAccountType = delegator.findByAnd("GlAccountType",null,["glAccountTypeId"],false) !>	            
<script type="text/javascript">
	var dataGATP = new Array();
		dataGATP = [
		<#list glAccountType as acc>
			{
				'glAccountTypeId' : '${acc.glAccountTypeId?if_exists}',
				'description' : "${StringUtil.wrapString(acc.get('description',locale)?default(''))}"
			},
		</#list>	
		]
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};	
	uiLabelMap.BACCglAccountTypeId = '${StringUtil.wrapString(uiLabelMap.BACCGlAccountTypeId)}';
	uiLabelMap.description = '${StringUtil.wrapString(uiLabelMap.Description)}';
	uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}'
	uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}'
</script>