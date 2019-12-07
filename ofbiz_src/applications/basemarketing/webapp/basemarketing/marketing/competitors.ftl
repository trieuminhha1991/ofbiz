<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "component://baseecommerce/webapp/baseecommerce/backend/content/viewImageOnGrid.ftl"/>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	
	var datafields =
		[
			{name: 'opponentEventId', type: 'string'},
			{name: 'comment', type: 'string'},
			{name: 'description', type: 'string'},
			{name: 'image', type: 'string'}
		];
	
	var columns =
		[
			{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (row + 1) + '</div>';
			    }
			},
			{text: '${uiLabelMap.BSOpponentEventId}',datafield: 'opponentEventId', width: 200 },
			{text: '${uiLabelMap.BSDescription}',datafield: 'description', minWidth: 200 },
			{text: '${uiLabelMap.FormFieldTitle_comments}',datafield: 'comment', width: 250 },
			{text: '${uiLabelMap.BSLinkImage}',datafield: 'image', width: 130, filterable: false, sortable: false,
				cellsrenderer:  function (row, column, value, a, b, data){
					return value.picturable();
				}
			}
		];
	
	Grid.initGrid({
			url: 'JQGetListEventsOfCompetitor&partyId=' + datarecord.partyId,
			width: '100%',
			showtoolbar:false,
			autoheight: true,
			showfilterrow: true,
            filterable: true,
            sortable: true,
			source: {pagesize: 5}
			}, datafields, columns, null, grid);

}"/>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					{ name: 'partyCode', type: 'string'},
					{ name: 'groupName', type: 'string'}]"/>

<#assign columnlist="{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSCompetitorId)}', datafield: 'partyCode', width: 300,hidden: true,
						validation: function (cell, value) {
							if (value) {
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									var check = DataAccess.getData({
										url: 'checkPartyCode',
										data: {partyCode: value, partyId: $('#jqxgridCompetitor').jqxGrid('getcellvalue', cell.row, 'partyId')},
										source: 'check'});
									if ('false' == check) {
										return { result: false, message: '${uiLabelMap.BSCodeAlreadyExists}' };
									} else {
										return true;
									}
								} else {
									return { result: false, message: '${uiLabelMap.ContainSpecialSymbol}' };
								}
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSCompetitorName)}', datafield: 'groupName',
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					}"/>


<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" editrefresh="true"
	showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="addRival" editable="true"
	url="jqxGeneralServicer?sname=JQGetListRivals" id="jqxgridCompetitor" editmode="dblclick"
	createUrl="jqxGeneralServicer?sname=createRival&jqaction=C" addColumns="partyCode;groupName"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyGroupIncludePartyCode" editColumns="partyId;partyCode;groupName"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203"/>

<#include "component://basecrm/webapp/basecrm/crmsetting/popup/addRival.ftl"/>