<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{ name: 'partyId', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productName', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.BSCompetitor)}', dataField: 'groupName', width: '25%', editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSProduct)}', dataField: 'productName', columntype: 'textbox',
						validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					}"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true"
	alternativeAddPopup="addProductOfRival" columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListProductsOfRivals"
	createUrl="jqxGeneralServicer?sname=createRivalsProduct&jqaction=C" addColumns="partyId;productName"
	updateUrl="jqxGeneralServicer?sname=updateProduct&jqaction=U" editColumns="productId;productName"/>

<#include "popup/addProductOfRival.ftl"/>