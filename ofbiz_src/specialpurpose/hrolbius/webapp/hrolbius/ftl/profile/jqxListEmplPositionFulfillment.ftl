<script>
	<#assign emplPositionTypeList = delegator.findList("EmplPositionType", null, null, null, null, false) />
	var emplPosTypeData = new Array();
	<#list emplPositionTypeList as item>
		var row = {};
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${StringUtil.wrapString(item.description?if_exists)}';
		emplPosTypeData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'emplPositionId', type: 'string' },
					 { name: 'emplPositionTypeId', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 { name: 'temporaryFlag', type: 'string'},
					 { name: 'fulltimeFlag', type: 'string'},
					 { name: 'exemptFlag', type: 'string'},
					 { name: 'salaryFlag', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.HROlbiusEmplPositionId}', datafield: 'emplPositionId', width: '12%'},
					 { text: '${uiLabelMap.EmployeePositionTypeId}', datafield: 'emplPositionTypeId', width: '12%',
						cellsrenderer: function(row, col, val){
							for(var i = 0; i < emplPosTypeData.length; i++){
								if(val == emplPosTypeData[i].emplPositionTypeId){
									return '<span title=' + val + '>' + emplPosTypeData[i].description + '</span>'
								}
							}
							return '<span>' + val + '</span>'
						}
					 },
                     { text: '${uiLabelMap.HREmplTemporaryFlag}', datafield: 'temporaryFlag', width: '12%'},
                     { text: '${uiLabelMap.HREmplFulltimeFlag}', datafield: 'fulltimeFlag', width: '12%'},
                     { text: '${uiLabelMap.HREmplExemptFlag}', datafield: 'exemptFlag', width: '12%'},
                     { text: '${uiLabelMap.HREmplSalaryFlag}', datafield: 'salaryFlag', width: '12%'},
                     { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', cellsformat: 'd', filtertype:'range', width: '12%'},
                     { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'd', filtertype:'range'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplPosFulfillment&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />