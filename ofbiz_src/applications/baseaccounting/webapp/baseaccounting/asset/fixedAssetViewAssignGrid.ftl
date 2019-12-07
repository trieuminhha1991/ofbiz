<#assign dataField="[
		{name: 'partyId', type: 'string'},
		{ name: 'roleTypeId', type: 'string' },
		{ name: 'fixedAssetId', type: 'string' },
		{ name: 'fromDate', type: 'date', other:'Timestamp' },
		{ name: 'thruDate', type: 'date', other:'Timestamp' },
		{ name: 'allocatedDate', type: 'date', other:'Timestamp' },
	  	{ name: 'statusId', type: 'string'},
	  	{ name: 'comments', type: 'string'}
]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCPartyId}',filterable : false, datafield: 'partyId', width: 100 },
      { text: '${uiLabelMap.BACCRoleTypeId}', datafield: 'roleTypeId', width: 150, 
       	   cellsrenderer: function(row, colum, value){
       		   var data = $('#jqxgridAssignedParties').jqxGrid('getrowdata',row);
       		   for(var i = 0 ; i < roleTypeData.length ; i++ ){
   					if(roleTypeData[i].roleTypeId == data.roleTypeId){
   						return '<span>' + roleTypeData[i].description  + '</span>';
   						}
  					}
				return '<span>' + data.roleTypeId  + '</span>';
           }
      },
      { text: '${uiLabelMap.BACCFromDate}', dataField: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy'},
      { text: '${uiLabelMap.BACCThruDate}', dataField: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy'},
      { text: '${uiLabelMap.BACCAllocatedDate}', dataField: 'allocatedDate', width: 150, cellsformat: 'dd/MM/yyyy'},
      { text: '${uiLabelMap.BACCStatusId}', dataField: 'statusId', width: 150,
    	  cellsrenderer: function(row, colum, value){
      		   var data = $('#jqxgridAssignedParties').jqxGrid('getrowdata',row);
      		   for(var i = 0 ; i < statusData.length ; i++ )
  					if(statusData[i].statusId == data.statusId){
  						return '<span>' + statusData[i].description  + '</span>';
					}
      		   if(data.status == null)
      			   return '<span></span>';
				return '<span>' + data.statusId + '</span>';
          }  
      },
      { text: '${uiLabelMap.BACCComment}', dataField: 'comments'}
  "/>

<#assign fixedAsset = delegator.findOne("FixedAsset", {"fixedAssetId" : parameters.fixedAssetId?if_exists}, true)>
<#if fixedAsset.statusId == 'FA_USING'>
	<@jqGrid id="jqxgridAssignedParties" customTitleProperties="BACCListAssignedParties" filtersimplemode="true" filterable="false" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewAssignedParty.openWindow()" editable="false" addType="popup" showtoolbar="true"
		 url="jqxGeneralServicer?sname=JqxGetListAssignedParties&fixedAssetId=${parameters.fixedAssetId}" dataField=dataField columnlist=columnlist
	/>
<#else>
	<@jqGrid id="jqxgridAssignedParties" customTitleProperties="BACCListAssignedParties" filtersimplemode="true" filterable="false" editable="false" addType="popup" showtoolbar="true"
		 url="jqxGeneralServicer?sname=JqxGetListAssignedParties&fixedAssetId=${parameters.fixedAssetId}" dataField=dataField columnlist=columnlist
	/>
</#if>
