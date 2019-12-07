<script type="text/javascript" language="Javascript">

<#assign listGlAccountOrganizationAndClass  = delegator.findByAnd("GlAccountOrganizationAndClass",{"organizationPartyId" : "${parameters.organizationPartyId?if_exists}"},["accountCode"],false)/>
	<#assign listCardTypeGl = delegator.findByAnd("Enumeration",null,null,false) !>
	var dataCTGL = [
		<#list listCardTypeGl as acc>
			{
				'enumId' : '${acc.enumId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.enumCode?default(''))}</span>"
			},
		</#list>	
		];
		
	var dataLCT = new Array();
	dataLCT = [
		<#list listCardType as acc>
			{
				'enumId' : '${acc.enumId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.enumId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.enumCode?default(''))}</span>"
			},
		</#list>	
		];
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {}
	uiLabelMap.CreditCardType = '${StringUtil.wrapString(uiLabelMap.BACCAccountingCreditCardType)}';
	uiLabelMap.description = '${StringUtil.wrapString(uiLabelMap.Description)}';
	uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
	uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
</script>