<div style="position:relative">
	<form class="form-horizontal form-window-content-custom" id="initRequirementEntry" name="initRequirementEntry" method="post" action="#">
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSRequiredByDate}</label>
					</div>
					<div class="span7">
						<div id="requiredByDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSRequirementStartDate}</label>
					</div>
					<div class="span7">
						<div id="requirementStartDate"></div>
			   		</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span3'>
						<label>${uiLabelMap.BSDescription}</label>
					</div>
					<div class="span9">
						<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
			   		</div>
				</div>
			</div>
		</div><!--.row-fluid-->
	</form>
</div>
<#assign gridProductItemsId = "jqxgridOrder">
<div style="position:relative" class="form-window-content-custom">
	<#assign dataField = "[
					{ name: 'orderDate', type: 'date', other: 'Timestamp'},
					{ name: 'orderId', type: 'string'},
					{ name: 'orderName', type: 'string'},
					{ name: 'fullDeliveryDate', type: 'date', other: 'Timestamp'},
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
					{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', dataField: 'orderId', pinned: true, width: '13%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCreatedBy)}', dataField: 'createdBy', width: '12%'},
					{ text: '${uiLabelMap.BSCreateDate}', dataField: 'orderDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
						cellsrenderer: function(row, colum, value) {
							return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
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
					{text: '${StringUtil.wrapString(uiLabelMap.BSTotalWeight)}', dataField: 'totalWeight', width: '8%', cellsalign: 'right', cellsformat: 'd'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'customerCode', width: '12%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}', dataField: 'productStoreId', width: '14%', filtertype: 'checkedlist', 
						cellsrenderer: function(row, column, value){
							if (productStoreData.length > 0) {
								for(var i = 0 ; i < productStoreData.length; i++){
	    							if (value == productStoreData[i].productStoreId){
	    								return '<span title =\"' + productStoreData[i].storeName +'\">' + productStoreData[i].storeName + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
					 		if (productStoreData.length > 0) {
					 			var filterDataAdapter = new $.jqx.dataAdapter(productStoreData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'productStoreId', valueMember: 'productStoreId',
									renderer: function(index, label, value){
										if (productStoreData.length > 0) {
											for(var i = 0; i < productStoreData.length; i++){
												if(productStoreData[i].productStoreId == value){
													return '<span>' + productStoreData[i].storeName + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
					 		}
			   			}
		   			},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonAmount)}', dataField: 'grandTotal', width: '12%', cellsalign: 'right', cellsformat: 'c', 
					 	cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
					 		if (typeof(data) != 'undefined') {
						 		str += formatcurrency(value, data.currencyUom);
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	}
					},
					{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '8%', filtertype: 'checkedlist', 
						cellsrenderer: function(row, column, value){
							if (orderStatusData.length > 0) {
								for(var i = 0 ; i < orderStatusData.length; i++){
	    							if (value == orderStatusData[i].statusId){
	    								return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
					 		if (orderStatusData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(orderStatusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										if (orderStatusData.length > 0) {
											for(var i = 0; i < orderStatusData.length; i++){
												if(orderStatusData[i].statusId == value){
													return '<span>' + orderStatusData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
							}
			   			}
					}, 
					{text: '${StringUtil.wrapString(uiLabelMap.BSPriority)}', dataField: 'priority', width: '10%', filtertype: 'checkedlist', 
						cellsrenderer: function(row, column, value){
							if (priorityData.length > 0) {
								for(var i = 0 ; i < priorityData.length; i++){
	    							if (value == priorityData[i].enumId){
	    								return '<span title =\"' + priorityData[i].description +'\">' + priorityData[i].description + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
					 		if (priorityData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(priorityData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
									renderer: function(index, label, value){
										if (priorityData.length > 0) {
											for(var i = 0; i < priorityData.length; i++){
												if(priorityData[i].enumId == value){
													return '<span>' + priorityData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
							}
			   			}
			   		},
				"/>
	<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
			viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="checkbox" width="100%" bindresize="true" groupable="false" 
			url="jqxGeneralServicer?sname=JQGetListOrderNeedDelivery" 
		/>
</div>

<#include "script/reqDeliveryOrderNewInfoScript.ftl"/>

<#--
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAOrderId}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAOrderSeqId}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAInventoryItemId}</th>
<th align="center" class="align-center" colspan="3">${uiLabelMap.DAProduct}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAUnit}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAQuantity}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAPackingPerTray}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASumTray}</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASumWeightPerPacking} (${uiLabelMap.DAUomKg})</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASumWeight} (${uiLabelMap.DAUomKg})</th>
<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASum} (${uiLabelMap.DAUomKg})</th>
-->