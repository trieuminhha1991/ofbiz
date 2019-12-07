<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{name: 'termTypeId', type: 'string'},
					{name: 'description', type: 'string'}]"/>

<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.termTypeId)}', dataField: 'termTypeId', width: 350, editable: false,
						cellsrenderer: function(row, colum, value){
					        var link = 'AgreementTermDetail?termTypeId=' + value;
					        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
						}	
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description',
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					}"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="true" alternativeAddPopup="addCommunicationResult"
	columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListAgreementTerm"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateTermType" editColumns="termTypeId;description"/>