<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SETTLE_RECD_STATUS"}, null, false) />
<script type="text/javascript">
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'promoSettleRecordId', type: 'string' },
               		{ name: 'fromDate', type: 'date', other: 'Timestamp'},
               		{ name: 'toDate', type: 'date', other: 'Timestamp'},
               		{ name: 'createdDate', type: 'date', other: 'Timestamp'},
               		{ name: 'createdBy', type: 'string'},
               		{ name: 'statusId', type: 'string'}
                	]"/>
<#assign columnlist="{ text: '${uiLabelMap.DAPromoSettleRecordId}', dataField: 'promoSettleRecordId', width: '180px', 
						cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgridPSR').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/viewPromoSettleRecord?promoSettleRecordId=\" + data.promoSettleRecordId + \"'>\" + data.promoSettleRecordId + \"</a></span>\";
                        }
					 },
					 { text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
					 { text: '${uiLabelMap.DAToDate}', dataField: 'toDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
					 { text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
					 { text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy'},
					 { text: '${uiLabelMap.DAStatus}', dataField: 'statusId', filtertype: 'checkedlist', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPSR').jqxGrid('getrowdata', row);
    						for(var i = 0 ; i < statusData.length; i++){
    							if (value == statusData[i].statusId){
    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
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
<@jqGrid id="jqxgridPSR" defaultSortColumn="promoSettleRecordId" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListPromoSettlementRecord"/>
		