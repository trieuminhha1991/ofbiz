<script>
	var stateData = new Array();
	var row = {};
	row['state'] = 'open';
	row['description'] = '${StringUtil.wrapString(uiLabelMap.CommonOpen)}';
	stateData[0] = row;
	var row = {};
	row['state'] = 'close';
	row['description'] = '${StringUtil.wrapString(uiLabelMap.CommonClose)}';
	stateData[1] = row;
</script>
<#assign dataField="[{ name: 'ntfId', type: 'string' },
					 { name: 'ntfGroupId', type: 'string' },
					 { name: 'header', type: 'string' },
					 { name: 'state', type: 'string' },
					 { name: 'dateTime', type: 'date', other:'Timestamp'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.parameterCode}', datafield: 'ntfId', width: '20%'},
                     { text: '${uiLabelMap.NotificationHeader}', datafield: 'header', width: '40%'},
                     { text: '${uiLabelMap.NotificationDateTime}', datafield: 'dateTime', width: '20%', cellsformat: 'd', filtertype:'range'},
                     { text: '${uiLabelMap.NotificationState}', datafield: 'state', filtertype:'checkedlist',
			 cellsrenderer: function(column, row, value){
				 if(value == 'open'){
					 return '<span>${uiLabelMap.CommonOpen}</span>';
				 }else{
					 return '<span>${uiLabelMap.CommonClose}</span>'
				 }
			 },
			createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(stateData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'state', valueMember: 'state',
								renderer: function(index, label, value){
									for(var i = 0; i < stateData.length; i++){
										if(stateData[i].state == value){
											return '<span>' + stateData[i].description + '</span>';
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
		 url="jqxGeneralServicer?sname=JQGetListEmplNotification&partyId=${parameters.partyId?if_exists}" dataField=dataField columnlist=columnlist
		 />