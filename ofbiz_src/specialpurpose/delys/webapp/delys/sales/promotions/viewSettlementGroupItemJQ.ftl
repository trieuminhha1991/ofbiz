<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SETTLE_GRPITM_STATUS"}, null, false) />
<#assign promoTypeList = delegator.findByAnd("ProductPromoType", null, null, false)/>
<script type="text/javascript">
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
	
	var promoTypeData = new Array();
	<#list promoTypeList as promoTypeItem>
		<#assign description = StringUtil.wrapString(promoTypeItem.get("description", locale))/>
		var row = {};
		row['typeId'] = '${promoTypeItem.productPromoTypeId}';
		row['description'] = "${description}";
		promoTypeData[${promoTypeItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'promoSettleGroupItemId', type: 'string' },
       				{ name: 'promoSettleGroupItemParentId', type: 'string'}, 
       				{ name: 'promoSettleGroupId', type: 'string'},
       				{ name: 'promoSettleGroupType', type: 'string'},
       				{ name: 'productId', type: 'string'},
       				{ name: 'quantityRequired', type: 'string'},
       				{ name: 'amountRequired', type: 'string'},
       				{ name: 'quantityAccepted', type: 'string'},
       				{ name: 'amountAccepted', type: 'string'},
       				{ name: 'statusId', type: 'string'}
        			]"/>
<#assign columnlist="{ text: '${uiLabelMap.DAPromoSettleGroupItemId}', dataField: 'promoSettleGroupItemId', width: '180px'},
					 { text: '${uiLabelMap.DAPromoSettleGroupItemParentId}', dataField: 'promoSettleGroupItemParentId', width: '180px'},
					 { text: '${uiLabelMap.DAPromoSettleGroupType}', dataField: 'promoSettleGroupType', width: '180px', filtertype: 'checkedlist', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
    						for(var i = 0 ; i < promoTypeData.length; i++){
    							if (value == promoTypeData[i].typeId){
    								return '<span title = ' + promoTypeData[i].description +'>' + promoTypeData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(promoTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
								renderer: function(index, label, value){
									for(var i = 0; i < promoTypeData.length; i++){
										if(promoTypeData[i].typeId == value){
											return '<span>' + promoTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					 },
					 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px'},
					 { text: '${uiLabelMap.DAQuantityRequired}', dataField: 'quantityRequired', width: '180px'},
					 { text: '${uiLabelMap.DAAmountRequired}', dataField: 'amountRequired', width: '180px'},
					 { text: '${uiLabelMap.DAQuantityAccepted}', dataField: 'quantityAccepted', width: '180px'},
					 { text: '${uiLabelMap.DAAmountAccepted}', dataField: 'amountAccepted', width: '180px'},
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
			   		 }
              		"/>
<#-- Promotion by order settlement record -->
<@jqGrid id="jqxgridPOSR" defaultSortColumn="promoSettleGroupItemId;promoSettleGroupItemParentId" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="20" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListPromoSettleGroupItem&promoSettleGroupId=${parameters.promoSettleGroupId?if_exists}"/>
