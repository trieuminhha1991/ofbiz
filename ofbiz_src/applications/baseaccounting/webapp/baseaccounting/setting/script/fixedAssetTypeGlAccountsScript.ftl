<#assign listFixedAsset  = delegator.findByAnd("FixedAsset",{"partyId" : "${userLogin.lastOrg?if_exists}"},["fixedAssetId"],false) !>
<#assign listGlAccount  = delegator.findByAnd("GlAccountOrganizationAndClass",{"organizationPartyId" : "${userLogin.lastOrg?if_exists}"},null,false) !>
<#assign listFixedAssetType  = delegator.findByAnd("FixedAssetType",null,["description"],false) />	
<script type="text/javascript">
var dataLFLAT = new Array();
dataLFLAT = [
		<#list listFixedAssetType as acc>
			{
				'fixedAssetTypeId' : '${acc.fixedAssetTypeId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.fixedAssetTypeId?if_exists} ] " + " - " +  "${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
var assetTypeRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLFLAT.length; i++){
    	if(dataLFLAT[i].fixedAssetTypeId == data.fixedAssetTypeId){
    		return "<span>" + dataLFLAT[i].description + "</span>";
    	}
    }
    return data.fixedAssetTypeId;
}

var dataLFA = new Array();
dataLFA = [
		<#list listFixedAsset as acc>
			{
				'fixedAssetId' : '${acc.fixedAssetId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${acc.fixedAssetId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.fixedAssetName?default(''))}</span>"
			},
		</#list>	
		]
var fixedAssetRenderer = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLFA.length; i++){
    	if(dataLFA[i].fixedAssetId == data.fixedAssetId){
    		return "<span>" + dataLFA[i].description + "</span>";
    	}
    }
    return "<span>" + value + "</span>";
}

var dataLLGLA = new Array();
dataLLGLA = [
	<#list listGlAccount as acc>{
			'glAccountId' : '${acc.glAccountId?if_exists}',
			'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
		},
	</#list>	
]
var listLossGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLLGLA.length; i++){
    	if(dataLLGLA[i].glAccountId == data.lossGlAccountId){
    		return "<span>" + dataLLGLA[i].description + "</span>";
    	}
    }
    return "<span>" + value + "</span>";
}


var dataLPGLA = new Array();
dataLPGLA = [
		<#list listGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'>[ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listProfitGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLPGLA.length; i++){
    	if(dataLPGLA[i].glAccountId == data.profitGlAccountId){
    		return "<span>" + dataLPGLA[i].description + "</span>";
    	}
    }
    return "<span>" + value + "</span>";
}

var dataLAGLA = new Array();
dataLAGLA = [
		<#list listGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listAssetGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLAGLA.length; i++){
    	if(dataLAGLA[i].glAccountId == data.assetGlAccountId){
    		return "<span>" + dataLAGLA[i].description + "</span>";
    	}
    }
    return "<span>" + value + "</span>";
}

var dataLDGLA = new Array();
dataLDGLA = [
		<#list listGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
		
var listDeptGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLDGLA.length; i++){
    	if(dataLDGLA[i].glAccountId == data.depGlAccountId){
    		return "<span>" + dataLDGLA[i].description + "</span>";
    	}
    }
    return "<span>" + value + "</span>";
}


var dataLADGLA = new Array();
dataLADGLA = [
		<#list listGlAccount as acc>
			{
				'glAccountId' : '${acc.glAccountId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${acc.glAccountId?if_exists} ]" + " - " +  "${StringUtil.wrapString(acc.accountName?default(''))}</span>"
			},
		</#list>	
		]
var listAccDeptGlAccountRender = function (row, column, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    for(i=0;i < dataLADGLA.length; i++){
    	if(dataLADGLA[i].glAccountId == data.accDepGlAccountId){
    		return "<span>" + dataLADGLA[i].description + "</span>";
    	}
    }
    return "<span>" + value + "</span>";
}

</script>