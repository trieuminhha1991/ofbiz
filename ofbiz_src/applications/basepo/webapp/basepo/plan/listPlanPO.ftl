<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'productPlanId', type: 'string' },
					{ name: 'productPlanTypeId', type: 'string' },
					{ name: 'customTimePeriodId', type: 'string' },
					{ name: 'productPlanName', type: 'string' },
					{ name: 'organizationPartyId', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'statusId', type: 'string' }]"/>

<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (value + 1) + '</div>';
						}
					},
					{ text: '${uiLabelMap.POProductPlanID}', datafield: 'productPlanId', width: 200, editable: false,
						cellsrenderer: function(row, colum, value) {
							return \"<span><a href='listPlanItem?productPlanId=\" + value + \"'>\" + value + \"</a></span>\";
						}
					},
					{ text: '${uiLabelMap.DmsNamePlan}', datafield: 'productPlanName',
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					},
					{ text: '${uiLabelMap.DMSOrganizationPartyId}', datafield: 'groupName', width: 300, editable: false }"/>

<#if hasOlbPermission("MODULE", "PLANPO_PLAN_NEW", "CREATE")>
	<#assign addrow = "true" />
<#else>
	<#assign addrow = "false" />
</#if>

<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="true" editmode="dbclick"
	addType="popup" alternativeAddPopup="alterpopupWindow" addrow=addrow showtoolbar="true"
	id="jqxgirdProductPlan" addrefresh="true" filterable="true" rowsheight="30"
	url="jqxGeneralServicer?sname=JQGetListProductPlanPO" clearfilteringbutton="true"
	createUrl="jqxGeneralServicer?sname=createProductPlanHeader&jqaction=C" addColumns="productPlanTypeId;customTimePeriodId;productPlanName"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateProductPlanHeader" editColumns="productPlanId;productPlanName"/>

<#include "popup/addProductPlan.ftl"/>