	            
<#assign listGlAccountOrganizationAndClass  = delegator.findByAnd("GlAccountOrganizationAndClass",{"organizationPartyId" : "${parameters.organizationPartyId?if_exists}"},["accountCode"],false)/>
<#assign listPM = delegator.findByAnd("PaymentMethodType",null,null,false)/>
<script type="text/javascript" language="Javascript">
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
	uiLabelMap.paymentMethodTypeId = '${StringUtil.wrapString(uiLabelMap.BACCPaymentMethodTypeId)}';
	uiLabelMap.description = '${StringUtil.wrapString(uiLabelMap.description)}';
	uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
	uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
	var listPM = [
		<#list listPM as pm>
		{
			paymentMethodTypeId : '${pm.paymentMethodTypeId?if_exists}' ,
			description : "<span class='custom-style-word'>${StringUtil.wrapString(pm.get("description",locale)?if_exists)}</span>"
		},
		</#list>
	];	
	
	var dataLPMT = new Array();
	dataLPMT = [
		<#list listPaymentMethodType as acc>
			{
				'paymentMethodTypeId' : '${acc.paymentMethodTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
	var dataGAOAC = new Array();
	dataGAOAC = [
		<#list listGlAccountOrganizationAndClass as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.accountName?default(''))}" + "[" + '${acc.glAccountId?if_exists}' + "]</span>"
			},
		</#list>	
		]
		
    var listGlAccountOrganizationAndClassRender = function (row, column, value) {
    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(typeof(data) != 'undefined' && data.accountCode != null){
    		return "<span class='custom-style-word'>" + data.accountName + '[' + data.accountCode + ']' + "</span>";
    	}
        return "";
    }

	var listDefaultGlAccountRender = function (row, column, value) {
    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    	if(typeof(data) != 'undefined' && data.accountCodeDef != null){
    		return "<span class='custom-style-word'>" + data.accountNameDef + '[' + data.accountCodeDef + ']' + "</span>";
		}
        return "";
    }
</script>