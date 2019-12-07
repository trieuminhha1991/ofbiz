<script type="text/javascript">

<#assign varianceReasonGlAccounts = delegator.findByAnd("VarianceReasonGlAccount",{"organizationPartyId" : "${parameters.organizationPartyId?if_exists}"},["glAccountId"],false) !>
<#assign glAccountOrganizationAndClasses = delegator.findByAnd("GlAccountOrganizationAndClass",{"organizationPartyId" : "${parameters.organizationPartyId?if_exists}"},["accountCode"],false) !>
<#assign listVRGC = delegator.findByAnd("VarianceReason",null,null,false) !>


	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {}
	uiLabelMap.varianceReasonId = '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_varianceReasonId)}';
	uiLabelMap.description = '${StringUtil.wrapString(uiLabelMap.Description)}';
	uiLabelMap.BACCaccountName = '${StringUtil.wrapString(uiLabelMap.BACCAccountName)}';
	uiLabelMap.BACCaccountCode = '${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}';
	
	var VRGC = new Array();
	VRGC = [
		<#list listVRGC as acc>
			{
				'varianceReasonId' : '${acc.varianceReasonId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		];
		
	var dataVR = new Array();
	dataVR = [
		<#list varianceReasons as acc>
			{
				'varianceReasonId' : '${acc.varianceReasonId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
		
	var dataGAOAC = new Array();
	dataGAOAC = [
		<#list glAccountOrganizationAndClasses as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [${acc.accountCode?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
</script>

<script type="text/javascript">
	var linkVRrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < dataVR.length; i++){
        	if(dataVR[i].varianceReasonId == data.varianceReasonId){
        		return "<span>" + dataVR[i].description + "</span>";
        	}
        }
        return data.varianceReasonId;
    }
    var linkGOACrenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        for(i=0;i < dataGAOAC.length; i++){
        	if(dataGAOAC[i].glAccountId == data.glAccountId){
        		return "<span>" + dataGAOAC[i].description + "</span>";
        	}
        }
        return data.glAccountId;
    }
</script>