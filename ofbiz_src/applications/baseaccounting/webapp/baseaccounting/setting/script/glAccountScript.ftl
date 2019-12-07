<#-- init list data  --> 
<#assign listGlAccountType = delegator.findByAnd("GlAccountType",null,["description"],false) !>
<#assign listGlAccountClass = delegator.findByAnd("GlAccountClass",null,["description"],false) !>
<#assign listGlResourceType = delegator.findByAnd("GlResourceType",null,["description"],false) !>
<#assign listGlXbrlClass = delegator.findByAnd("GlXbrlClass",null,["description"],false) !>
<#assign listTaxFormId = delegator.findByAnd("Enumeration",{"enumTypeId" : "TAX_FORMS"},["enumId"],false) !>
<#assign partyAccountingPreference = dispatcher.runSync("getPartyAccountingPreferences",{"userLogin" : userLogin,"organizationPartyId" : "${parameters.organizationPartyId?if_exists}"}) !>
<#if partyAccountingPreference?exists && partyAccountingPreference?has_content>
	<#assign aggregatedPartyAcctgPreference = partyAccountingPreference.partyAccountingPreference !>
</#if>

<script type="text/javascript" language="Javascript">
    var organizationPartyId = '${parameters.organizationPartyId?if_exists}';
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
	uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
	uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
	uiLabelMap.glAccountId = '${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}';
	uiLabelMap.accountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
	uiLabelMap.accountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
	uiLabelMap.description = '${StringUtil.wrapString(uiLabelMap.Description)}';
    uiLabelMap.BACCConfirmDelete = '${StringUtil.wrapString(uiLabelMap.BACCConfirmDelete)}';
    uiLabelMap.BACCCancel = '${StringUtil.wrapString(uiLabelMap.BACCCancel)}';
    uiLabelMap.BACCOK = '${StringUtil.wrapString(uiLabelMap.BACCOK)}';
    uiLabelMap.CannotDeleteRow = '${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}';
    uiLabelMap.wgdeletesuccess = '${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}';
	var dataGLAT = new Array();
	dataGLAT = [
		<#list listGlAccountType as type>
			{
				'glAccountTypeId' : '${StringUtil.wrapString(type.glAccountTypeId?if_exists)}',
				'description' : "<span >[ ${StringUtil.wrapString(type.glAccountTypeId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	var dataGLAC = new Array();
	dataGLAC = [
		<#list listGlAccountClass as type>
			{
				'glAccountClassId' : '${StringUtil.wrapString(type.glAccountClassId?if_exists)}',
				'description' : "<span >[ ${StringUtil.wrapString(type.glAccountClassId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	
	var dataGRT = new Array();
	dataGRT = [
		<#list listGlResourceType as type>
			{
				'glResourceTypeId' : '${StringUtil.wrapString(type.glResourceTypeId?if_exists)}',
				'description' : "<span >[ ${StringUtil.wrapString(type.glResourceTypeId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	var dataGC = new Array();
	dataGC = [
		<#list listGlXbrlClass as type>
			{
				'glXbrlClassId' : '${StringUtil.wrapString(type.glXbrlClassId?if_exists)}',
				'description' : "<span>[ ${StringUtil.wrapString(type.glXbrlClassId?if_exists)} ]" + ":" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}</span>"
			},
		</#list>
	]
	
	var dataTFAI = new Array();
	dataTFAI = [
		<#list listTaxFormId as type>
			{
				'glTaxFormId' : '${StringUtil.wrapString(type.enumId?if_exists)}',
				'description' : "[ ${StringUtil.wrapString(type.enumId?if_exists)} ]" + "-" + "${StringUtil.wrapString(type.get('description',locale)?if_exists)}"
			},
		</#list>
	]
</script>			  