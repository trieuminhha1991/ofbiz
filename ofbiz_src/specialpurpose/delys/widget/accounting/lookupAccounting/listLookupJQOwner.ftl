 <#assign dataField = "[
 		{name : 'partyId',type : 'string'},
 		{name : 'groupName',type : 'string'}
 ]"/>       
 <#assign columnlist = "
 		{text  : '${uiLabelMap.PartyPartyId}',datafield : 'partyId',width : '50%',cellsrenderer : function(row,datafield,column){
 			var data = $(\"#jqxGridOwner\").jqxGrid('getrowdata',row);
    		return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.partyId + \"&#39;)'>\" + data.partyId + \"</a></span>\";
 		}},
 		{text  : '${uiLabelMap.PartyGroupName}',datafield : 'groupName',width : '50%'}
 " />
 <@jqGrid id="jqxGridOwner" width="900" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" addrefresh="true" clearfilteringbutton="true" showtoolbar="true"
 	url="jqxGeneralServicer?sname=JQgetListOwner"
   />
