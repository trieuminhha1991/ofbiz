<script>
	//Prepare data for payroll parameter types
	<#assign listPayrollParaTypes = delegator.findList("PayrollEmplParameterType", null, null, null, null, false) />
	var payrollParaTypeData = new Array();
	<#list listPayrollParaTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['code'] = '${item.code}';
		row['description'] = '${description}';
		payrollParaTypeData[${item_index}] = row;
	</#list>

	//Prepare data for time period types
	<#assign listTimePeriodTypes = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["DAILY", "HOURLY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"]), null, null, null, false) />
	var timePeriodTypeData = new Array();
	<#list listTimePeriodTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['periodTypeId'] = '${item.periodTypeId}';
		row['description'] = '${description}';
		timePeriodTypeData[${item_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'code', type: 'string' },
					 { name: 'type', type: 'string' },
					 { name: 'periodTypeId', type: 'string' },
					 { name: 'value', type: 'number' },
					 { name: 'actualPercent', type: 'number'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.parameterCode}', datafield: 'code', width:'12%'},
					 { text: '${uiLabelMap.parameterType}', datafield: 'type', width:'25%', filtertype: 'checkedlist',
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < payrollParaTypeData.length; i++){
								if(value == payrollParaTypeData[i].code){
									return '<span title = ' + value + '>' + payrollParaTypeData[i].description + '</span>'
								}
							}
							return value;
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(payrollParaTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'code', valueMember: 'code',
								renderer: function(index, label, value){
									for(var i = 0; i < payrollParaTypeData.length; i++){
										if(payrollParaTypeData[i].code == value){
											return '<span>' + payrollParaTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
					 },
                     { text: '${uiLabelMap.PeriodType}', datafield: 'periodTypeId', width:'12%', filtertype: 'checkedlist',
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < timePeriodTypeData.length; i++){
								if(value == timePeriodTypeData[i].periodTypeId){
									return '<span title=' + value + '>' + timePeriodTypeData[i].description + '</span>'
								}
							}
							return value
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(timePeriodTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'periodTypeId', valueMember: 'periodTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < timePeriodTypeData.length; i++){
										if(timePeriodTypeData[i].periodTypeId == value){
											return '<span>' + timePeriodTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
                     },
                     { text: '${uiLabelMap.parameterValue}', datafield: 'value', width:'12%',
			 cellsrenderer: function(column, row, value){
				 return '<span>' + formatcurrency(value) + '</span>';
			 }
                     },
                     { text: '${uiLabelMap.actualPercent}', datafield: 'actualPercent', width:'12%'},
                     { text: '${uiLabelMap.FromDate}', datafield: 'fromDate', cellsformat: 'd', filtertype:'range', width:'12%'},
                     { text: '${uiLabelMap.ThruDate}', datafield: 'thruDate', cellsformat: 'd', filtertype:'range'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplPayrollParameter&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />