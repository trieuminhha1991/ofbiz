<script>
	//Prepare for party data
	<#assign listParties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData = new Array();
	<#list listParties as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.groupName?if_exists) + StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists)>
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyData[${item_index}] = row;
	</#list>

	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PAYROLL_APP_STTS", "PAYROLL_INIT_STTS", "PAYROLL_PAID_STTS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'code', type: 'string' },
					 { name: 'value', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.HRPayrollCode}', datafield: 'code', width: 200},
                     { text: '${uiLabelMap.CommonAmount}', datafield: 'value',
						 cellsrenderer: function(column, row, value){
							return '<span>' + formatcurrency(value, '${defaultCurrencyUomId}') + '</span>';
						}
                     },
                     { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', cellsformat: 'd', filtertype: 'range'},
                     { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'd', filtertype: 'range'},
                     { text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', filtertype:'checkedlist',
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
			}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplPayrollHistory&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />