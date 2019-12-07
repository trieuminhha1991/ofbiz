<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'groupName', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.BSCompetitorId)}', dataField: 'partyCode', width: 350,
							validation: function (cell, value) {
								if (value) {
									if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
										var check = DataAccess.getData({
											url: 'checkPartyCode',
											data: {partyCode: value, partyId: $('#jqxgrid').jqxGrid('getcellvalue', cell.row, 'partyId')},
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
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCompetitorName)}', dataField: 'groupName', columntype: 'textbox',
							validation: function (cell, value) {
								if (value) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
							}
						}"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="addRival"
	columnlist=columnlist dataField=dataField editmode="dblclick"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListRivals"
	createUrl="jqxGeneralServicer?sname=createRival&jqaction=C" addColumns="partyCode;groupName"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyGroupIncludePartyCode" editColumns="partyId;partyCode;groupName"/>

<#include "popup/addRival.ftl"/>