<#assign listGlAccountOrganizationAndClass = delegator.findByAnd("GlAccountOrganizationAndClass",{"organizationPartyId" : "${parameters.orgazanitonPartyId?if_exists}"},["accountCode"],false) !>
<#assign finAccountTypes = delegator.findByAnd("FinAccountType",null,["finAccountTypeId"],false) !>
<script>
if(!uiLabelMap) var uiLabelMap = {};
uiLabelMap.finAccountTypeId = '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_finAccountTypeId)}';
uiLabelMap.FinAccountTypeGlAccount = '${StringUtil.wrapString(uiLabelMap.AccountingFinAccountTypeGlAccount)}';
uiLabelMap.BACCaccountName ='${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
uiLabelMap.BACCaccountCode ='${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
var dataGLOAC = new Array();
dataGLOAC = [
	<#list listGlAccountOrganizationAndClass as acc>
		{
			'glAccountId' : '${acc.glAccountId?if_exists}',
			'description' : "${StringUtil.wrapString(acc.accountName?default(''))}[${StringUtil.wrapString(acc.accountCode?default(''))}]"
		},
	</#list>	
	]
	
var listlistGlAccountOrganizationAndClassRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataGLOAC.length; i++){
    	if(dataGLOAC[i].glAccountId == data.glAccountId){
    		return "<span>" + dataGLOAC[i].description + "</span>";
    	}
    }
    return "";
}

var dataFLAT = new Array();
	dataFLAT = [
	<#list finAccountTypes as acc>
		{
			'finAccountTypeId' : '${acc.finAccountTypeId?if_exists}',
			'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
		},
	</#list>	
	]


</script>