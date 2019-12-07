<#assign receiptId = parameters.receiptId !>
<#assign dataField="[{ name: 'receiptItemSeqId', type: 'string' },
					 { name: 'productId', type: 'string'},
					 { name: 'receiptId', type: 'string'},
					 { name: 'orderedQuantity', type: 'string'},
					 { name: 'fromOrderId', type: 'string'},
					 { name: 'expireDate', type: 'date'},
					 { name: 'quantityUomId', type: 'string'},
					 { name: 'unitPrice', type: 'string'},
					 { name: 'subTotal', type: 'string'},
					 { name: 'actualQuantity', type: 'string'},
					 { name: 'quantityRejected', type: 'string'},
					 { name: 'rejectionId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'testQuantity', type: 'string'},
					 { name: 'sampleQuantity', type: 'string'},
					 { name: 'inspectQuantity', type: 'string'},
					 { name: 'lackQuantity', type: 'string'},
					 { name: 'comment', type: 'string'},
					 { name: 'lotId', type: 'string'}
					 ]"/>
<#assign columnlist="					 
						{
						    text: 'stt', sortable: false, filterable: false, editable: false,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 45,
						    cellsrenderer: function (row, column, value) {
						        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
						    },
						    cellclassname: function (row, column, value, data) {
							    var mod = row % 2;
							    if(mod == 0){
							    	return 'green1';
							    }
							}
						},
						{ text: 'receiptId', datafield: 'receiptId', editable: false, hidden: true},
						{ text: '${uiLabelMap.OrderOrderId}', datafield: 'fromOrderId', editable: false, hidden: false, width: '100px'},
						{ text: 'receiptItemSeqId', datafield: 'receiptItemSeqId', editable: false, hidden: true},
						{ text: '${uiLabelMap.ProductName}', datafield: 'productId', editable: false},
						{ text: '${uiLabelMap.ProductExpireDate}', datafield: 'expireDate', columntype: 'datetimeinput',width: '100px', editable: false,cellsformat: 'dd/MM/yyyy'},
						{ text: 'quantityUomId', datafield: 'quantityUomId', editable: false, hidden: true},
						{ text: '${uiLabelMap.OrderQuantity}', datafield: 'orderedQuantity', editable: false, width: '100px',cellsalign: 'right'},
						{ text: 'unitPrice', datafield: 'unitPrice', editable: false, width: '100px', hidden: true},
						{ text: 'subTotal', datafield: 'subTotal', editable: false, width: '100px', hidden: true},
						{ text: 'actualQuantity', datafield: 'actualQuantity', width: '80px', editable: false, hidden: true},
						{ text: '${uiLabelMap.QuantityReject}', datafield: 'quantityRejected', width: '80px', editable: false, hidden: true},
						{ text: 'rejectionId', datafield: 'rejectionId', width: '80px', editable: false, hidden: true},
						{ text: '${uiLabelMap.ProductTestQuantity}', datafield: 'testQuantity', width: '120px', editable: true, columntype: 'numberinput', cellsalign: 'right',
							createeditor: function (row, cellvalue, editor) {
		                          editor.jqxNumberInput({ decimalDigits: 0, digits: 9 });
		                      }	
						},
						{ text: '${uiLabelMap.ProductSampleQuantity}', datafield: 'sampleQuantity', width: '125px', editable: true, columntype: 'numberinput', cellsalign: 'right',
							createeditor: function (row, cellvalue, editor) {
		                          editor.jqxNumberInput({ decimalDigits: 0, digits: 9 });
		                      }
						},
						{ text: '${uiLabelMap.ProductInspectQuantity}', datafield: 'inspectQuantity', width: '130px', editable: true, columntype: 'numberinput', cellsalign: 'right',
							createeditor: function (row, cellvalue, editor) {
		                          editor.jqxNumberInput({ decimalDigits: 0, digits: 9 });
		                      }
						},
						{ text: '${uiLabelMap.ProductLackQuantity}', datafield: 'lackQuantity', width: '150px', editable: true, columntype: 'numberinput', cellsalign: 'right'},
						{ text: '${uiLabelMap.comment}', datafield: 'comment', width: '80px', editable: true},
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField editmode="selectedrow" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="true" addrow="false" deleterow="true"
		 	url="jqxGeneralServicer?receiptId=${receiptId}&sname=JQGetListProductByReceipt" showtoolbar="false"
		 		updateUrl="jqxGeneralServicer?sname=updateReceiptItemAjax&jqaction=U" editColumns="receiptId;receiptItemSeqId;actualQuantity;quantityRejected;rejectionId;statusId[RECEIPT_QA_ACCEPTED];testQuantity;sampleQuantity;inspectQuantity;lackQuantity;comment;"
		 />

<div id="showPopup"></div>
<div>
	<button id="updateCost" class="btn btn-primary btn-small open-sans icon-ok">${uiLabelMap.Finish}</button>
</div>
<script>
	$('#updateCost').on('click', function(){
		var data = $('#jqxgrid').jqxGrid('getrowdata', 0);
		orderId = data.fromOrderId;
		var openTime = $('#DetailReceipt_receiptDate').val();
//		alert(data.fromOrderId);
		$.ajax({
				url: 'showOrderCost?orderId='+orderId+'&organizationPartyId=QA_QUALITY_MANAGER&receiptId=${receiptId}&openTime='+openTime,
		    	type: "POST",
		    	data: {},
		    	async: false,
		    	success: function(data2) {
		    		$("#showPopup").html(data2);
		    		$('#window').jqxWindow('open');
		    	},
		    	error: function(data2){
		    	}
				});
	});
</script>
