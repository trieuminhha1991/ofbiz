<script type="text/javascript">
<#assign itlength = listFixedAssetType.size()/>
<#if listFixedAssetType?size gt 0>
    <#assign lfat="var lfat = ['" + StringUtil.wrapString(listFixedAssetType.get(0).fixedAssetTypeId?if_exists) + "'"/>
	<#assign lfatValue="var lfatValue = [\"" + StringUtil.wrapString(listFixedAssetType.get(0).fixedAssetTypeId?if_exists) + " - " + StringUtil.wrapString(listFixedAssetType.get(0).description?if_exists) +"\""/>
	<#if listFixedAssetType?size gt 1>
		<#list 1..(itlength - 1) as i>
			<#assign lfat=lfat + ",'" + StringUtil.wrapString(listFixedAssetType.get(i).fixedAssetTypeId?if_exists) + "'"/>
			<#assign lfatValue=lfatValue + ",\"" + StringUtil.wrapString(listFixedAssetType.get(i).fixedAssetTypeId?if_exists) + " - " + StringUtil.wrapString(listFixedAssetType.get(i).description?if_exists) + "\""/>
		</#list>
	</#if>
	<#assign lfat=lfat + "];"/>
	<#assign lfatValue=lfatValue + "];"/>
<#else>
	<#assign lfat="var lfat = [];"/>
	<#assign lfatValue="var lfatValue = [];"/>
</#if>
${lfat}
${lfatValue}	

var dataLFLAT = new Array();
var row = {};
row["glAccountId"] = '';
row["description"] = '';
dataLFLAT[0] = row;
for (var i = 0; i < ${itlength}; i++) {
    var row = {};
    row["fixedAssetTypeId"] = lfat[i];
    row["description"] = lfatValue[i];
    dataLFLAT[i] = row;
}
var assetTypeRenderer = function (row, column, value) {
	var data = $('#jqxgridGfatgla').jqxGrid('getrowdata', row);
    for(i=0;i < lfat.length; i++){
    	if(lfat[i] == data.fixedAssetTypeId){
    		return "<span>" + lfatValue[i] + "</span>";
    	}
    }
    return "";
}

var listLossGlAccountGlobalRender = function (row, column, value) {
	var data = $('#jqxgridGfatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLLGLA.length; i++){
    	if(dataLLGLA[i].glAccountId == data.lossGlAccountId){
    		return "<span>" + dataLLGLA[i].description + "</span>";
    	}
    }
    return "";
}


var listProfitGlAccountGlobalRender = function (row, column, value) {
	var data = $('#jqxgridGfatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLPGLA.length; i++){
    	if(dataLPGLA[i].glAccountId == data.profitGlAccountId){
    		return "<span>" + dataLPGLA[i].description + "</span>";
    	}
    }
    return "";
}

var listAssetGlAccountGlobalRender = function (row, column, value) {
	var data = $('#jqxgridGfatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLAGLA.length; i++){
    	if(dataLAGLA[i].glAccountId == data.assetGlAccountId){
    		return "<span>" + dataLAGLA[i].description + "</span>";
    	}
    }
    return "";
}


var listDeptGlAccountGlobalRender = function (row, column, value) {
	var data = $('#jqxgridGfatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLDGLA.length; i++){
    	if(dataLDGLA[i].glAccountId == data.depGlAccountId){
    		return "<span>" + dataLDGLA[i].description + "</span>";
    	}
    }
    return "";
}

var listAccDeptGlAccountGlobalRender = function (row, column, value) {
	var data = $('#jqxgridGfatgla').jqxGrid('getrowdata', row);
    for(i=0;i < dataLADGLA.length; i++){
    	if(dataLADGLA[i].glAccountId == data.accDepGlAccountId){
    		return "<span>" + dataLADGLA[i].description + "</span>";
    	}
    }
    return "";
}

</script>

<#assign dataField="[{ name: 'fixedAssetTypeId', type: 'string'},
					 { name: 'fixedAssetId', type: 'string'},
					 { name: 'assetGlAccountId', type: 'string'},
					 { name: 'accDepGlAccountId', type: 'string'},
					 { name: 'depGlAccountId', type: 'string'},
					 { name: 'profitGlAccountId', type: 'string'},
					 { name: 'lossGlAccountId', type: 'string'},
					]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.fixedAssetTypeId)}', datafield: 'fixedAssetTypeId', cellsrenderer:assetTypeRenderer},
					 { text: '${StringUtil.wrapString(uiLabelMap.assetGlAccountId)}', datafield: 'assetGlAccountId', cellsrenderer:listAssetGlAccountGlobalRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.accDepGlAccountId)}', datafield: 'accDepGlAccountId', cellsrenderer:listAccDeptGlAccountGlobalRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.depGlAccountId)}', datafield: 'depGlAccountId', cellsrenderer:listDeptGlAccountGlobalRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.profitGlAccountId)}', datafield: 'profitGlAccountId', cellsrenderer:listProfitGlAccountGlobalRender},
					 { text: '${StringUtil.wrapString(uiLabelMap.lossGlAccountId)}', datafield: 'lossGlAccountId', cellsrenderer:listLossGlAccountGlobalRender}
					"/>
	
<@jqGrid  filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 addrow="false" deleterow="false" id="jqxgridGfatgla" customTitleProperties="${StringUtil.wrapString(uiLabelMap.PageTitleFixedAssetGlobalMappings)}"
		 url="jqxGeneralServicer?sname=listGlobalFixedAssetTypeyGLAccountJqx&fixedAssetTypeId=${fixedAssetTypeId}"
		 />