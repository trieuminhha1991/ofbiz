<#include "script/listPhysicalInventoryScript.ftl"/>
<#assign dataField="[
				{ name: 'physicalInventoryId', type: 'string'},
				{ name: 'partyId', type: 'string' },
				{ name: 'fullName', type: 'string' },
				{ name: 'facilityId', type: 'string' },
				{ name: 'facilityName', type: 'string' },
				{ name: 'generalComments', type: 'string' },
                { name: 'physicalInventoryDate', type: 'date', other: 'Timestamp'},
				]"/>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.PhysicalInventoryId}', datafield: 'physicalInventoryId', align: 'left', width: 150, pinned: true,
					cellsrenderer: function(row, column, value){
						 return '<span><a href=\"javascript:PhysicInvObj.detailPhysicalInventory(&#39;' + value + '&#39;)\"> ' + value  + '</a></span>'
					}
				},
				{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 200, pinned: true},
				{ text: '${StringUtil.wrapString(uiLabelMap.PhysicalInventoryDate)}', dataField: 'physicalInventoryDate', align: 'left', width: 200, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellsalign: 'right',
					cellsrenderer: function(row, colum, value){
						if(value === null || value === undefined || value === ''){
							return '<span style=\"text-align: right;\">_NA_</span>';
						}
					}
				},
				{ text: '${uiLabelMap.PartyExecuted}', datafield: 'fullName', align: 'left', width: 200,
					
				},
				{ text: '${uiLabelMap.Description}', datafield: 'generalComments', align: 'left', minwidth: 150},
			"/>
<#if hasOlbPermission("MODULE", "LOG_PHYSICAL_INVENTORY_NEW", "CREATE")>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=jqGetPhysicalInventoryDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}" customTitleProperties="ListPhysicalInventory" id="jqxgridPhysicalInventory"
		customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript:PhysicInvObj.prepareCreatePhysicalInventory();" mouseRightMenu="true" contextMenuId="PhysicalInvMenu"
	/>
<#else>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true" mouseRightMenu="true" contextMenuId="PhysicalInvMenu"
		url="jqxGeneralServicer?sname=jqGetPhysicalInventoryDetail&facilityId=${parameters.facilityId?if_exists}&ownerPartyId=${company}" customTitleProperties="ListPhysicalInventory" id="jqxgridPhysicalInventory"
	/>			
</#if>
<div id='PhysicalInvMenu' style="display:none;">
<ul>
    <li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
</ul>
</div>
