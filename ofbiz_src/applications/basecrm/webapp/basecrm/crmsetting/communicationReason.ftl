<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

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
						{ text: '${StringUtil.wrapString(uiLabelMap.KReasonClaimId)}', dataField: 'enumCode', width: 100, editable: false }, 
						{ text: '${StringUtil.wrapString(uiLabelMap.KDescription)}', dataField: 'description', columntype: 'textbox', minwidth: 350,
							validation: function (cell, value) {
								if (value) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.enumTypeId)}', dataField: 'enumTypeId', columntype: 'dropdownlist', minwidth: 200, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								for(var i = 0 ; i < reasonTypeList.length; i++){
									if (value == reasonTypeList[i].enumTypeId){
										return '<span title = ' + reasonTypeList[i].description +'>' + reasonTypeList[i].description + ' - [' + reasonTypeList[i].enumTypeId + ']' + '</span>';
									}
								}
								return '<span title=' + value +'>' + value + '</span>';
							}
						}"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="addCommunicationReason" columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListCommunicationReason" autoheight="true" filterable="true" deleterow="false"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteEnumeration" deleteColumn="enumId"
	createUrl="jqxGeneralServicer?sname=createEnumeration&jqaction=C" addColumns="enumCode;sequenceId;enumTypeId;description"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEnumeration" editColumns="enumId;enumCode;sequenceId;enumTypeId;description"/>
<#include "popup/addCommunicationReason.ftl"/>
<script>
	var reasonContacted = [<#if reasonContacted?exists><#list reasonContacted as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var reasonUnContacted = [<#if reasonUnContacted?exists><#list reasonUnContacted as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var reasonTypeList = _.union(reasonContacted, reasonUnContacted);
</script>