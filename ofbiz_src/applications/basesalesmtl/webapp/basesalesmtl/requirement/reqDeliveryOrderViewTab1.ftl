<#assign gridProductItemsId = "jqxgridOrderItem">
<#assign dataField = "[
				{ name: 'orderDate', type: 'date', other: 'Timestamp'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderItemSeqId', type: 'string'},
				{ name: 'quantity', type: 'string'},
				{ name: 'estimatedDeliveryDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
				{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
				{ name: 'customerId', type: 'string'},
				{ name: 'customerCode', type: 'string'},
				{ name: 'customerFullName', type: 'string'},
				{ name: 'productStoreId', type: 'string'},
				{ name: 'grandTotal', type: 'string'},
				{ name: 'priority', type: 'string'},
				{ name: 'createdBy', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'totalWeight', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', dataField: 'orderId', pinned: true, width: '13%',
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewOrder?orderId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSDesiredDeliveryDate)}', dataField: 'fullDeliveryDate', width: '25%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
						if (typeof(data) != 'undefined') {
							var returnStr = \"<span>\";
							if (data.estimatedDeliveryDate != null) {
								returnStr += jOlbUtil.dateTime.formatFullDate(data.estimatedDeliveryDate)
								if (data.shipAfterDate != null || data.shipBeforeDate != null) {
									returnStr += ' (';
									returnStr += jOlbUtil.dateTime.formatFullDate(data.shipAfterDate) + ' - ' + jOlbUtil.dateTime.formatFullDate(data.shipBeforeDate);
									returnStr += ')';
								}
							} else {
								returnStr += jOlbUtil.dateTime.formatFullDate(data.shipAfterDate) + ' - ' + jOlbUtil.dateTime.formatFullDate(data.shipBeforeDate);
							}
							returnStr += \"</span>\";
							return returnStr;
						}
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', width: '8%', cellsalign: 'right', cellsformat: 'd'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSWeight)} (kg)', dataField: 'totalWeight', width: '12%', cellsalign: 'right', cellsformat: 'd'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'customerCode', width: '12%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', dataField: 'customerFullName'},
			"/>
<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
		viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="singleRow" width="100%" bindresize="true" groupable="false" 
		url="jqxGeneralServicer?sname=JQGetListOrderReqDelivery&requirementId=${requirement.requirementId}" 
	/>