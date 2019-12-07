<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField = "[{ name: 'recurrenceRuleId', type: 'string' },
					{ name: 'frequency', type: 'string' },
					{ name: 'countNumber', type: 'number', other: 'Long' },
					{ name: 'intervalNumber', type: 'number', other: 'Long' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.recurrenceRuleId)}', dataField: 'recurrenceRuleId', width: 350, editable: false },
					{ text: '${StringUtil.wrapString(uiLabelMap.intervalNumber)}', datafield: 'intervalNumber', columntype: 'numberinput', filtertype: 'number', minwidth: 200,
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}, validation: function (cell, value) {
							if (value > 0) {
								return true;
							}
							return { result: false, message: multiLang.DmsQuantityNotValid };
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.frequency)}', datafield: 'frequency', width: 400, editable: false }"/>

<#assign editable = "false"/>
<#if hasOlbPermission("MODULE", "SYS_SYNCHRONIZATION_SCHEDULE", "UPDATE")>
	<#assign editable = "true"/>
</#if>
		
<@jqGrid id="jqxgridScheduleSynchronization" addrow="false" clearfilteringbutton="true" editable=editable alternativeAddPopup="addUserGroup"
	columnlist=columnlist dataField=dataField customTitleProperties="ADScheduleSynchronization"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListRecurrenceRules"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRecurrenceRule" editColumns="recurrenceRuleId;intervalNumber(java.lang.Long)"/>