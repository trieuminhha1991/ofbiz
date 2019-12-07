<script>
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORD_RECEIPT_STATUS"}, null, false)/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
</script>
<#assign dataField="[{ name: 'orderId', type: 'string'},
					 { name: 'orderDate', type: 'date', other:'Timestamp'},
					 { name: 'receiptId', type: 'string'},
					 { name: 'invoiceId', type: 'string'},
					 { name: 'paymentId', type: 'string'},
					 { name: 'productStoreId', type: 'string'},
					 { name: 'storeName', type: 'string'},
					 { name: 'bankId', type: 'string'},
					 { name: 'bankName', type: 'string'},
					 { name: 'posTerminalStateId', type: 'string'},
					 { name: 'employeeId', type: 'string'},
					 { name: 'employeeName', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'amount', type: 'number'},
					 { name: 'amountApplied', type: 'number'},
					 { name: 'amountNotApply', type: 'number'}
				]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCOrderId}', width: 130, datafield: 'orderId', pinned: true },
					 { text: '${uiLabelMap.BPOSReceiptId}', width: 120, datafield: 'receiptId', pinned: true},
					 { text: '${uiLabelMap.BPOSWorkShiftId}', width: 100, datafield: 'posTerminalStateId', pinned: true},
					 { text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: 180, filtertype: 'checkedlist', 
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
					 { text: '${uiLabelMap.BACCBankId}', width: 120, datafield: 'bankId'},
					 { text: '${uiLabelMap.BACCBankName}', width: 300, datafield: 'bankName'},
					 { text: '${uiLabelMap.BSCreateDate}', datafield: 'orderDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype: 'range', editable: false},
					 { text: '${uiLabelMap.BSProductStoreId}', width: 120, datafield: 'productStoreId'},
					 { text: '${uiLabelMap.BSStoreName}', width: 200, datafield: 'storeName'},
					 { text: '${uiLabelMap.BPOSEmployee}', width: 100, datafield: 'employeeId'},
					 { text: '${uiLabelMap.BLEmployeeName}', width: 180, datafield: 'employeeName'},
					 { text: '${uiLabelMap.BACCInvoiceId}', width: 100, datafield: 'invoiceId'},
					 { text: '${uiLabelMap.BACCPaymentId}', width: 200, datafield: 'paymentId',
					 	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							if(value && value.length > 0){
								var paymentIdArr = value.split(', ');
								var paymentIdStr = '';
								for(var i = 0; i < paymentIdArr.length; i++){
									paymentIdStr += '<a href=\"ViewARPayment?paymentId=' + paymentIdArr[i] + '\">'  + paymentIdArr[i] + '</a>';
									if(i < paymentIdArr.length - 1){
										paymentIdStr += ', ';
									}
								}
								return '<div>' + paymentIdStr + '</div>';
							}
						}
					 },
					 { text: '${uiLabelMap.BACCAmount}', width: 180, datafield: 'amount', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	if(value){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(value) + \"</div>\";
						 	} else {
						 		return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(0) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BACCPaymentAmount}', width: 180, datafield: 'amountApplied', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	if(value){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(value) + \"</div>\";
						 	} else {
						 		return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(0) + \"</div>\";
						 	}
					 	}
					 },
					 { text: '${uiLabelMap.BACCPaymentAmountNotApply}', width: 180, datafield: 'amountNotApply', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
						 	if(value){
							 	return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(value) + \"</div>\";
						 	} else {
						 		return \"<div style='margin-left: 5px; margin-top: 5px;  text-align: right;'>\" + formatcurrency(0) + \"</div>\";
						 	}
					 	}
					 }
				"/>

<#assign customcontrol1 = "fa-file-pdf-o open-sans@${uiLabelMap.BSExportExcel}@javascript: void(0);@exportExcel()"/>
				 
<@jqGrid url="jqxGeneralServicer?sname=JQGetListOrderReceiptNote&isShowAll=Y" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" showtoolbar="true"
		 customcontrol1=customcontrol1 id="jqxgridListOrderPOS" clearfilteringbutton="true" isSaveFormData="true" formData="filterObjData"
/>

<script>
	var filterObjData = new Object();
	var exportExcel = function(){
		var dataGrid = $("#jqxgridListOrderPOS").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
	
		var winURL = "exportExcelListOrderReceiptNote";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "isShowAll");
		hiddenField0.setAttribute("value", "Y");
		form.appendChild(hiddenField0);
		
		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField1 = document.createElement("input");
				hiddenField1.setAttribute("type", "hidden");
				hiddenField1.setAttribute("name", key);
				hiddenField1.setAttribute("value", value);
				form.appendChild(hiddenField1);
			});
		}
		
		document.body.appendChild(form);
		form.submit();
	}
</script>