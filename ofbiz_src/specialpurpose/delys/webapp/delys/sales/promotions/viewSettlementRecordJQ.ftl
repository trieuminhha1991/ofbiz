<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SETTLE_ITEM_STATUS"}, null, false) />
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
<#assign dataField="[{ name: 'orderId', type: 'string' },
       		{ name: 'orderItemSeqId', type: 'string'}, 
       		{ name: 'statusId', type: 'string'},
       		{ name: 'reason', type: 'string'}
        	]"/>
<#assign columnlist="{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: '180px'},
					 { text: '${uiLabelMap.DAOrderItemSeqId}', dataField: 'orderItemSeqId', width: '180px'},
					 { text: '${uiLabelMap.DAStatus}', dataField: 'statusId', width: '200px', filtertype: 'checkedlist', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
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
			   		 }, 
              		 { text: '${uiLabelMap.DAReason}', dataField: 'reason'},
              		"/>
<#-- Promotion by order settlement record -->
<@jqGrid id="jqxgridPOSR" defaultSortColumn="orderId; orderItemSeqId" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="20" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListPromoSettleItem&promoSettleRecordId=${parameters.promoSettleRecordId?if_exists}"/>