<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{ name: 'enumTypeId', type: 'string' },
					{ name: 'parentTypeId', type: 'string' },
					{ name: 'description', type: 'string' }]"/>

<#assign columnlist = "{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.KReasonTypeId)}', dataField: 'enumTypeId', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', dataField: 'parentTypeId', width: 200, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', columntype: 'textbox',
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					}"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="addCommunicationResult"
	columnlist=columnlist dataField=dataField deleterow="false" editmode="dblclick"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListCommunicationResult"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteEnumerationType" deleteColumn="enumTypeId"
	createUrl="jqxGeneralServicer?sname=createEnumerationType&jqaction=C" addColumns="enumTypeId;parentTypeId;description"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEnumerationType" editColumns="enumTypeId;description"/>

<#include "popup/addCommunicationResult.ftl"/>