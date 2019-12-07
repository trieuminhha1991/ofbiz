<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'costAccMapDepId', type: 'string'},
					{ name: 'invoiceItemTypeId', type: 'string'},
					{ name: 'invoiceItemTypeName', type: 'string'},
					{ name: 'applicationBaseId', type: 'string'},
					{ name: 'organizationPartyId', type: 'string'},
					{ name: 'organizationPartyName', type: 'string'},
					{ name: 'departmentId', type: 'string'},
					{ name: 'departmentName', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'}]"/>

<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BACCOrganizationParty)}', datafield: 'organizationPartyName', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.Department)}', datafield: 'departmentName', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ConfigId)}', datafield: 'costAccMapDepId', width: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.invoiceItemTypeId)}', datafield: 'invoiceItemTypeName', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.applicationBaseId)}', datafield: 'applicationBaseId', width: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy' }"/>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListCostAccMapDepartment" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteCostAccMapDepartment" deleteColumn="costAccMapDepId"
	createUrl="jqxGeneralServicer?sname=createCostAccMapDepartment&jqaction=C" addColumns="costAccMapDepId;invoiceItemTypeId;applicationBaseId;organizationPartyId;departmentId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	addrow="true" deleterow="true"/>

<#include "component://baseaccounting/webapp/baseaccounting/setting/addDepartmentalCost.ftl"/>