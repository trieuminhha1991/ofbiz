<div id="detailItems">
	<#assign dataFieldVehicle="[
					{ name: 'fullName', type: 'string'},
					{ name: 'roleTypeId', type: 'string' },
					{ name: 'roleTypeDescription', type: 'string' },
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				]"/>
	<#assign columnlistVehicle="  
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.PartyOrg}', datafield: 'fullName', align: 'left', width: 250, pinned: true},
					{ text: '${uiLabelMap.BLRoles}', datafield: 'roleTypeDescription', align: 'left',
						cellsrenderer: function(row, colum, value) {
						}
					},
					{ text: '${uiLabelMap.BLEffectiveDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 150, cellsalign: 'right',
	                },
	                { text: '${uiLabelMap.BLExpiryDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: 150,	 cellsalign: 'right',              
	                },
				"/> 
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=JQGetFacilityPartyRole&facilityId=${parameters.facilityId?if_exists}" customTitleProperties="BLFacilityParty" id="jqxgridFacilityPartyRole"
	/> 
</div>
