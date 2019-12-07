<script>
	//Prepare for skill type data
	<#assign statusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "WOT_REGISTER"), null, null, null, false) />
	var statusData = new Array();
	<#list statusList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'statusId', type: 'string' },
					 { name: 'dateRegistration', type: 'date', other: 'Timestamp' },
					 { name: 'overTimeFromDate', type: 'date' },
					 { name: 'overTimeThruDate', type: 'date' },
					 { name: 'reasonRegister', type: 'string' },
					 { name: 'reasonApproval', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.EmplProposalCheck}', datafield: 'statusId', width: 200, filtertype:'checkedlist',
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								for(var i = 0; i < statusData.length; i++){
									if(statusData[i].statusId == value){
										return '<span>' + statusData[i].description + '</span>';
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
						}
					 },
                     { text: '${uiLabelMap.HRWorkOvertimeDateRegis}', datafield: 'dateRegistration', width: 200, cellsformat: 'd', filtertype: 'range'},
                     { text: '${uiLabelMap.HREmplOverTimeFromDate}', datafield: 'overTimeFromDate', width: 200, cellsformat: 'hh:mm:ss tt'},
                     { text: '${uiLabelMap.HREmplOverTimeThruDate}', datafield: 'overTimeThruDate', width: 200, cellsformat: 'hh:mm:ss tt'},
                     { text: '${uiLabelMap.HREmplReasonRegisOvertime}', datafield: 'reasonRegister', width: 200},
                     { text: '${uiLabelMap.HREmplReasonAcceptReject}', datafield: 'reasonApproval'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplOvertimeHistory&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />