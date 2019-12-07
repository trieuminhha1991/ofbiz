<script>
	//Prepare for qualification type data
	<#assign listQualTypes = delegator.findList("PartyQualType", null, null, null, null, false) />
	var qualTypeData = new Array();
	<#list listQualTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['partyQualTypeId'] = '${item.partyQualTypeId}';
		row['description'] = '${description}';
		qualTypeData[${item_index}] = row;
	</#list>

	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["HR_DEGREE_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>

	//Prepare for status data
	<#assign listVerifyStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PARTYQUAL_VERIFY"]), null, null, null, false)>
	var verifyStatusData = new Array();
	<#list listVerifyStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		verifyStatusData[${item_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'partyQualTypeId', type: 'string' },
					 { name: 'qualificationDesc', type: 'string'},
					 { name: 'title', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'verifStatusId', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp' },
					 { name: 'thruDate', type: 'date', other: 'Timestamp' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.HRolbiusEmployeeQualID}', datafield: 'partyQualTypeId', width: 200,
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < qualTypeData.length; i++){
								if(value == qualTypeData[i].partyQualTypeId){
									return '<span title=' + value + '>' + qualTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						}
					 },
                     { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', width: 200, cellsformat: 'd', filtertype:'range'},
                     { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', width: 200, cellsformat: 'd', filtertype:'range'},
                     { text: '${uiLabelMap.qualificationDesc}', datafield: 'qualificationDesc', width: 200},
                     { text: '${uiLabelMap.title}', datafield: 'title', width: 200},
                     { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 200, filtertype:'checkedlist',
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
                     { text: '${uiLabelMap.verifStatusId}', datafield: 'verifStatusId', filtertype: 'checkedlist',
			 cellsrenderer: function(column, row, value){
							for(var i = 0; i < verifyStatusData.length; i++){
								if(value == verifyStatusData[i].statusId){
									return '<span title=' + value + '>' + verifyStatusData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(verifyStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									for(var i = 0; i < verifyStatusData.length; i++){
										if(verifyStatusData[i].statusId == value){
											return '<span>' + verifyStatusData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
                     },
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplQual&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />