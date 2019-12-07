<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{ name: 'checkInId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'latitude', type: 'number', other: 'Double' },
					{ name: 'longitude', type: 'number', other: 'Double' },
					{ name: 'customerId', type: 'string' },
					{ name: 'customerCode', type: 'string' },
					{ name: 'customerName', type: 'string' },
					{ name: 'salesmanId', type: 'string' },
					{ name: 'salesmanCode', type: 'string' },
					{ name: 'salesmanName', type: 'string' },
					{ name: 'customerLatitude', type: 'number', other: 'Double' },
					{ name: 'customerLongitude', type: 'number', other: 'Double' },
					{ name: 'distance', type: 'number', other: 'Double' },
					{ name: 'checkInDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'checkOutDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' },
					{ name: 'checkInOk', type: 'bool' }]"/>

<#assign columnlist = "{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
							cellsrenderer: function (row, column, value) {
								return '<div style=margin:4px;>' + (row + 1) + '</div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'customerCode', width: 120 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', dataField: 'customerName', minWidth: 220 },
						{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', dataField: 'salesmanCode', width: 120 },
						{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', dataField: 'salesmanName', minWidth: 220 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCheckInTime)}', datafield: 'checkInDate', width: 150, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy' },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCheckOutTime)}', datafield: 'checkOutDate', width: 150, filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy' },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSDistance)}', datafield: 'distance', filtertype: 'number', width: 200,
							cellsrenderer: function(row, column, value, a, b, data) {
								return '<div class=\"text-right\">' + value.toLocaleString(locale) + ' m</div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSStandards)}', datafield: 'checkInOk', width: 150, columntype: 'checkbox',
							cellbeginedit: function (row, datafield, columntype, value) {
								if (jqxgrid1.jqxGrid('getcellvalue', row, 'applicationId') == datarecord.applicationId) {
									return true;
								} else {
									return false;
								}
							}
						}"/>

<@jqGrid id="jqxgridCheckInHistory" addrow="false" clearfilteringbutton="true" editable="false"
	columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true"
	url="jqxGeneralServicer?sname=JQGetListCheckInHistory"/>