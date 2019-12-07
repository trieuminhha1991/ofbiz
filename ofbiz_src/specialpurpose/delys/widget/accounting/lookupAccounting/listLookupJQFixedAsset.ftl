 <#assign dataField = "[
 		{name : 'fixedAssetId',type : 'string'},
 		{name : 'fixedAssetName',type : 'string'}
 ]"/>       
 <#assign columnlist = "
 		{text  : '${uiLabelMap.PartyPartyId}',datafield : 'fixedAssetId',width : '50%',cellsrenderer : function(row,datafield,column){
 			var data = $(\"#jqxGridFixedAssets\").jqxGrid('getrowdata',row);
    		return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.fixedAssetId + \"&#39;)'>\" + data.fixedAssetId + \"</a></span>\";
 		}},
 		{text  : '${uiLabelMap.PartyGroupName}',datafield : 'fixedAssetName',width : '50%'}
 " />
 <@jqGrid id="jqxGridFixedAssets" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" addrefresh="true" clearfilteringbutton="true" showtoolbar="true"
 	url="jqxGeneralServicer?sname=JQgetListParentFixedAssets"
   />
