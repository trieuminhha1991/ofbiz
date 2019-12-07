<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/crmresources/js/generalUtils.js"></script>

<#assign dataField = "[{ name: 'enumId', type: 'string' },
					{ name: 'enumCode', type: 'string' },
					{ name: 'sequenceId', type: 'string' },
					{ name: 'enumTypeId', type: 'string' },
					{ name: 'description', type: 'string' }]"/>

<#assign columnlist = "{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.KCommSubjectId)}', dataField: 'enumCode', columntype: 'textbox', width: 200,
						validation: function (cell, value) {
							if (value.containSpecialChars() || hasWhiteSpace(value)) {
								return { result: false, message: '${uiLabelMap.ContainSpecialSymbol}' };
							}
							if (value) {
								var enumId = $('#jqxgrid').jqxGrid('getcellvalue', cell.row, 'enumId');
								var check = DataAccess.getData({
									url: 'checkEnumerationCode',
									data: {enumCode: value, enumId: enumId},
									source: 'check'});
								if ('false' == check) {
									return { result: false, message: '${uiLabelMap.BSCodeAlreadyExists}' };
								}
							} else {
								return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
							}
							return true;
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', columntype: 'textbox',
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.KSequenceId)}', dataField: 'sequenceId', width: 200, cellsalign: 'right', columntype: 'numberinput',
						validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: '${uiLabelMap.DmsQuantityNotValid}' };
							}
							return true;
						}
					}"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="addCommunicationSubject" columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListCommunicationSubject" addrefresh="true" deleterow="true"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteEnumeration" deleteColumn="enumId"
	createUrl="jqxGeneralServicer?sname=createEnumeration&jqaction=C" addColumns="description;enumCode;enumTypeId;sequenceId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEnumeration" editColumns="enumId;description;enumTypeId;enumCode;sequenceId"
	contextMenuId="contextMenu" mouseRightMenu="true"/>

<#include "popup/addCommunicationSubject.ftl"/>
<#include "popup/productsInSubject.ftl"/>

<div id="contextMenu" class="hide">
	<ul>
		<li id="viewProducts"><i class="fa-list"></i>&nbsp;&nbsp;${uiLabelMap.BSListProduct}</li>
	</ul>
</div>

<script>
	var mainGrid;
	$(document).ready(function() {
		var contextMenu = $("#contextMenu");
		mainGrid = $("#jqxgrid");
		contextMenu.jqxMenu({ theme: theme, width: 220, autoOpenPopup: false, mode: "popup"});
		mainGrid.on("contextmenu", function () {
		    return false;
		});
		contextMenu.on("itemclick", function (event) {
	        var args = event.args;
	        var itemId = $(args).attr("id");
	        switch (itemId) {
			case "viewProducts":
				var rowIndex = mainGrid.jqxGrid("getSelectedRowindex");
				var enumId = mainGrid.jqxGrid("getcellvalue", rowIndex, "enumId");
				Products.open(enumId);
				break;
			default:
				break;
			}
	    });
		Products.init();
	});
</script>