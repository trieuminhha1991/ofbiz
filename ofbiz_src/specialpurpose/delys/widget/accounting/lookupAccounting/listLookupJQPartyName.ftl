 <#assign dataField = "[
 		{name : 'partyId',type : 'string'},
 		{name : 'partyTypeId',type : 'string'},
 		{name : 'firstName',type : 'string'},
 		{name : 'lastName',type : 'string'},
 		{name : 'groupName',type : 'string'}
 ]"/>       
 <#assign columnlist = "
 		{text  : '${uiLabelMap.PartyPartyId}',datafield : 'partyId',width : '15%',cellsrenderer : function(row,datafield,column){
 			var data = $(\"#jqxgridPartyName\").jqxGrid('getrowdata',row);
    		return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.partyId + \"&#39;)'>\" + data.partyId + \"</a></span>\";
 		}},
 		{text  : '${uiLabelMap.PartyTypeId}',datafield : 'partyTypeId',width : '15%'},
 		{text  : '${uiLabelMap.PartyFirstName}',datafield : 'firstName',width : '15%'},
 		{text  : '${uiLabelMap.PartyLastName}',datafield : 'lastName',width : '15%'},
 		{text  : '${uiLabelMap.PartyGroupName}',datafield : 'groupName'}
 " />
 <@jqGrid id="jqxgridPartyName" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" addrefresh="true" clearfilteringbutton="true" showtoolbar="true"
 	url="jqxGeneralServicer?sname=JQgetListPartyName"
   />
   <script>
   		$('#jqxgridPartyName').jqxGrid('width','850');
   </script>
