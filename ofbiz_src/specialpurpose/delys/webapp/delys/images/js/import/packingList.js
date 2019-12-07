//js of listpackinglist
	function btnAddNewPL(){
		bootbox.confirm("Bạn chắc chắn muốn làm rỗng P/L đã tạo?",function(result){
			if(result){
					changeSourceGrid();
					$('#packingListNumber').jqxComboBox('clearSelection');
					$('#packingListNumber').val('');
					$('#orderNumberSupp').val('');
					$('#invoiceNumber').val('');
					$('#totalNetWeight').val('');
					$('#totalGrossWeight').val('');
					$('#packingListDate').jqxDateTimeInput('val', null);
					$('#invoiceDate').jqxDateTimeInput('val', null);
					$("#orderTypeSupp").jqxComboBox('clearSelection');
					$('#packingListId').val('');
			}
		});
	}

	//BEGIN cell end edit
	$("#jqxgridPackingListDetail").on('cellEndEdit', function (event) {
		    var args = event.args;
		    var dataField = event.args.datafield;
		    var rowBoundIndex = event.args.rowindex;
		    var value = args.value;
		    var oldvalue = args.oldvalue;
//		    var rowData = args.row;
		    var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', rowBoundIndex);
			var data = $('#jqxgridPackingListDetail').jqxGrid('getrowdatabyid', uid);
		    if(dataField == "datetimeManufactured"){
//				var dateExp = data.expireDate;
//				var productId = data.productId;
//				if (dateExp && dateExp != null && dateExp != '') {
//					if(productId && productId != null && productId != ''){
//						executeQualityPublication(data, value, productId);
//					}else{
//						bootbox.dialog("Chưa chọn sản phẩm!", [{
//							"label" : "error",
//							"class" : "btn-small btn-danger",
//							"callback": function(){
//								console.log('1313');
//								$('#jqxgridPackingListDetail').jqxGrid('begincelledit', rowBoundIndex, 'productId');
//							}
//							}]
//						);
//					}
//				}
		    }else if(dataField == "expireDate"){
				var datemanu = data.datetimeManufactured;
				var productId = data.productId;
				if(value != null && value != '' && value){
					if (datemanu && datemanu != null && datemanu != '') {
						if(productId && productId != null && productId != ''){
							executeQualityPublication(data, value, productId);
						}else{
							bootbox.dialog("Chưa chọn sản phẩm!", [{
								"label" : "error",
								"class" : "btn-small btn-danger",
								"callback": function(){
//									console.log('1313');
									$('#jqxgridPackingListDetail').jqxGrid('begincelledit', rowBoundIndex, 'productId');
								}
								}]
							);
						}
					}else{
						bootbox.dialog("Chưa chọn ngày sản xuất!", [{
							"label" : "error",
							"class" : "btn-small btn-danger",
							"callback": function(){
								$('#jqxgridPackingListDetail').jqxGrid('begincelledit', rowBoundIndex, 'datetimeManufactured');
							}
							}]
						);
					}
				}
		    }else if(dataField == "productId"){
				var datemanu = data.datetimeManufactured;
				if(value != null && value != '' && value && value != oldvalue){
					if(datemanu && datemanu != null && datemanu != '' && data.expireDate && data.expireDate !=null && data.expireDate !=''){
						executeQualityPublication(data, data.expireDate, value);
					}
				}
		    }
		});
	
	function dialogNotProduct(rowBoundIndex, productId){
		bootbox.dialog("Chua chon san pham!", [{
			"label" : "error",
			"class" : "btn-small btn-danger",
			"callback": function(){
				$('#jqxgridPackingListDetail').jqxGrid('begincelledit', rowBoundIndex, 'productId');
			}
			}]
		);
	}
	//END cell end edit

	function btnAddNewRowDetail(){
		var valueCombo = $('#orderTypeSupp').jqxComboBox('getSelectedItem');
		var orderTypeSuppId=null;
		if(valueCombo){
			orderTypeSuppId = valueCombo.value;
		}
		if(orderTypeSuppId != "ORIGINAL"){
			var datarow = {originOrderUnit: 0, packingUnit: 0, orderUnit: 0};
	        $("#jqxgridPackingListDetail").jqxGrid('addrow', null, datarow,'first');
		}else{
			var datarow = {originOrderUnit: 0, packingUnit: 0, orderUnit: 0};
	        $("#jqxgridPackingListDetail").jqxGrid('addrow', null, datarow,'first');
//			bootbox.dialog("Chỉ được thêm chi tiết P/L khi loại hóa đơn khác original order!", [{
//				"label" : "error",
//				"class" : "btn-small btn-danger",
//				"callback": function(){
//				}
//			}]
//			);
		}
		
	}
	
	function btnRemoveRowDetail(){
		var selectedrowindex = $("#jqxgridPackingListDetail").jqxGrid('getselectedrowindex');
		var rowscount = $("#jqxgridPackingListDetail").jqxGrid('getdatainformation').rowscount;
		if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
	         var id = $("#jqxgridPackingListDetail").jqxGrid('getrowid', selectedrowindex);
	         var commit = $("#jqxgridPackingListDetail").jqxGrid('deleterow', id);
	     }
	}

//end js of listpackinglist

var resultPackingListHeader;
var contextMenu = $("#contextMenu").jqxMenu({ width: 300, height: 110, autoOpenPopup: false, mode: 'popup', theme: 'olbius' });
		$("#packingListDate").jqxDateTimeInput({width: '218px', height: '30px', theme: 'olbius'});
		$("#invoiceDate").jqxDateTimeInput({width: '218px', height: '30px', theme: 'olbius'});
		$('#packingListDate').jqxDateTimeInput('val', null);
		$('#invoiceDate').jqxDateTimeInput('val', null);
		$("#txtdepartureDate").jqxDateTimeInput({width: '220px', height: '30px', theme: 'olbius'});
		$("#txtarrivalDate").jqxDateTimeInput({width: '220px', height: '30px', theme: 'olbius'});
		$('#registerDate').jqxDateTimeInput({width: '220px', height: '30px', theme: 'olbius'});
		$('#registerDate').jqxDateTimeInput('val', null);
		$('#sampleSentDate').jqxDateTimeInput({width: '220px', height: '30px', theme: 'olbius'});
		$('#sampleSentDate').jqxDateTimeInput('val', null);
		$("#txtarrivalDate").jqxDateTimeInput('val', null);

		$('#popupWindowContainer').on('close', function(){
			changeSourceGrid();
			$('#containerNumber').val('');
			$('#indexGridDetail').val('');
			$('#sealNumber').val('');
//			$('#packingListNumber').val('');
			$('#orderNumberSupp').val('');
			$('#invoiceNumber').val('');
			$('#totalNetWeight').val('');
			$('#totalGrossWeight').val('');
			$('#packingListId').val('');
			$('#packingListDate').jqxDateTimeInput('val', null);
			$('#invoiceDate').jqxDateTimeInput('val', null);
		});
		
$('#alterSaveContainer').on('click', function(){
	if ($('#popupWindowContainer').jqxValidator('validate')) {
		var dataJson = getFormPL();
		var arr = getPackingListDetail();
		var dataRows = arr.rowsReturn;
		var check = arr.check;
		var uid = arr.uid;
		if(check){
			bootbox.confirm("Bạn chắc chắn muốn lưu và tiếp tục thêm mới P/L khác?",function(result){
				if(result){
					alterSaveContainerAndPackingList(dataJson, dataRows);
					changeSourceGrid();
//					$('#popupWindowContainer').jqxWindow('close');
//					$('#containerNumber').val('');
//					$('#indexGridDetail').val('');
//					$('#sealNumber').val('');
					$('#packingListNumber').val('');
					$('#packingListNumber').jqxComboBox('clearSelection');
					$('#orderTypeSupp').jqxComboBox('clearSelection');
					$('#orderNumberSupp').val('');
					$('#invoiceNumber').val('');
					$('#totalNetWeight').val('');
					$('#totalGrossWeight').val('');
					$('#packingListId').val('');
					$('#packingListDate').jqxDateTimeInput('val', null);
					$('#invoiceDate').jqxDateTimeInput('val', null);
//					$('#containerId').val('');
				}
			});
		}else{
			bootbox.dialog("Tồn tại sản phẩm chưa cập nhật date", [{
				"label" : "error",
				"class" : "btn-small btn-danger",
				"callback": function(){
					var index = $('#jqxgridPackingListDetail').jqxGrid('getrowboundindexbyid', uid);
					$('#jqxgridPackingListDetail').jqxGrid('selectrow', index);
					$('#jqxgridPackingListDetail').jqxGrid('selectcell', index, 'datetimeManufactured');
				}
				}]
			);
		}
	}
});

$('#saveAndContinueContainer').on('click', function(){
	var dataJson = getFormPL();
	var arr = getPackingListDetail();
	var dataRows = arr.rowsReturn;
	var check = arr.check;
	var uid = arr.uid;
	if(check){
		bootbox.confirm("Bạn chắc chắn muốn lưu và tiếp tục thêm mới container khác?",function(result){
			if(result){
				alterSaveContainerAndPackingList(dataJson, dataRows);
				$('#containerId').val('');
				$('#sealNumber').val('');
				$('#containerNumber').val('');
				var item = $("#orderPurchaseId").jqxComboBox('getSelectedItem');
				$("#orderPurchaseId").jqxComboBox('removeItem', item.value);
				$("#orderPurchaseId").jqxComboBox('clearSelection');
				changeSourceGrid();
				$('#packingListNumber').jqxComboBox('clear');
				$('#orderTypeSupp').jqxComboBox('clearSelection');
				$('#orderNumberSupp').val('');
				$('#invoiceNumber').val('');
				$('#totalNetWeight').val('');
				$('#totalGrossWeight').val('');
				$('#packingListDate').jqxDateTimeInput('val', null);
				$('#invoiceDate').jqxDateTimeInput('val', null);
				$("#orderTypeSupp").jqxComboBox('clearSelection');
				$('#packingListId').val('');
			}
		});
	}else{
		bootbox.dialog("Tồn tại sản phẩm chưa cập nhật date", [{
			"label" : "error",
			"class" : "btn-small btn-danger",
			"callback": function(){
				var index = $('#jqxgridPackingListDetail').jqxGrid('getrowboundindexbyid', uid);
				$('#jqxgridPackingListDetail').jqxGrid('selectrow', index);
				$('#jqxgridPackingListDetail').jqxGrid('selectcell', index, 'datetimeManufactured');
			}
			}]
		);
	}
	
});

$("#orderTypeSupp").jqxComboBox({
	displayMember: 'externalOrderTypeName',
	valueMember: 'externalOrderTypeId',
	autoDropDownHeight: true,
	width: '218px',
	height: '30px',
	searchMode: 'containsignorecase',
	autoOpen: true,
	autoComplete: true
});

$("#orderPurchaseId").jqxComboBox({
	displayMember: 'attrValue',
	valueMember: 'agreementId',
	width: '218px',
	height: '30px',
	searchMode: 'containsignorecase',
	autoOpen: false,
	autoComplete: true
});

//$('#orderPurchaseId').on('select', function (event){
//	$("#orderTypeSupp").jqxComboBox('clearSelection');         
//});

$('#orderTypeSupp').on('select', function (event){
		    var args = event.args;
		    if (args) {
			    var index = args.index;
			    var item = args.item;
			    var label = item.label;
			    var value = item.value;
			    var packingListId = $('#packingListId').val();
			    if(value == "ORIGINAL"){
			    	if(packingListId == null || packingListId == ''){
			    		loadOrderPurchase();
			    	}
			    }else{
			    	if(packingListId == null || packingListId == ''){
			    		changeSourceGrid();
			    	}else{
//			    		$('#jqxgridPackingListDetail').jqxGrid('refreshdata');
//			    		bootbox.dialog("Bạn muốn thêm mới packing list hay thay đổi loại hóa đơn cho packing list này?", [{
//			    			"label" : "Thêm mới",
//			    			"class" : "btn-small btn-primary",
//			    			"callback": function(){
//			    				changeSourceGrid();
//			    				$('#packingListNumber').jqxComboBox('clearSelection');
//			    				$('#orderNumberSupp').val('');
//			    				$('#invoiceNumber').val('');
//			    				$('#totalNetWeight').val('');
//			    				$('#totalGrossWeight').val('');
//			    				$('#packingListDate').jqxDateTimeInput('val', null);
//			    				$('#invoiceDate').jqxDateTimeInput('val', null);
////			    				$("#orderTypeSupp").jqxComboBox('clearSelection');
//			    				$('#packingListId').val('');
//			    			}
//			    			},
//			    			{
//								"label" : "Thay đổi",
//								"class" : "btn-small btn-success",
//								"callback": function() {
//									//Example.show("uh oh, look out!");
//								}
//								}
//			    			]
//			    		);
			    	}
			    }
		    }
});

$('#packingListNumber').jqxComboBox({
	source: [{}],
	displayMember: 'packingListNumber',
	valueMember: 'packingListId',
	width: '218px',
	height: '30px',
	searchMode: 'none',
	autoOpen: false,
	autoComplete: true
	
});

$('#packingListNumber').on('select', function (event){
    var args = event.args;
    if (args) {
	    var index = args.index;
	    var item = args.item;
	    var value = item.value;
	    $.ajax({
	 	     url: "doSomethingWhenSelectPLNumber",
	 	     type: "POST",
	 	     data: {packingListId: value},
	 	     async: false,
	 	     success: function(res) {
	 	    	resultPackingListHeader = res.resultPackingListHeader;
	 	    	if (resultPackingListHeader) {
	 	    		$('#packingListId').val(resultPackingListHeader.packingListId);
	 	    		$('#packingListDate').jqxDateTimeInput('setDate', resultPackingListHeader.packingListDate);
		 	     	$('#orderNumberSupp').val(resultPackingListHeader.externalOrderNumber);
		 	     	$('#orderTypeSupp').jqxComboBox('selectItem', resultPackingListHeader.externalOrderTypeId);
		 	     	$('#invoiceNumber').val(resultPackingListHeader.externalInvoiceNumber);
		 	     	$('#invoiceDate').jqxDateTimeInput('setDate', resultPackingListHeader.externalInvoiceDate);
		 	     	$('#totalNetWeight').val(resultPackingListHeader.netWeightTotal);
		 	     	$('#totalGrossWeight').val(resultPackingListHeader.grossWeightTotal);
		 	     	loadPackingListDetail(resultPackingListHeader.packingListId);
				}
	 	     }
	 	 });
    }          
});

function loadPackingListDetail(packingListId){
	var tmpS = $("#jqxgridPackingListDetail").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=jqxGetPackingListDetail&packingListId=" +packingListId;
	$("#jqxgridPackingListDetail").jqxGrid('source', tmpS);
}

function loadOrderPurchase(){
			var valueCombo = $('#orderPurchaseId').jqxComboBox('getSelectedItem');
			if(valueCombo){
				var agreementId = valueCombo.value;
				var agreementName = valueCombo.label;
//				containerId = $('#containerId').val();
//				if(containerId != null && containerId != ''){
//					$.ajax({
//				 	     url: "checkOriginOrder",
//				 	     type: "POST",
//				 	     data: {agreementId: agreementId, containerId: containerId},
//				 	     async: false,
//				 	     success: function(res) {
//				 	    	
//				 	     }
//				 	 });
//				}
				var tmpS = $("#jqxgridPackingListDetail").jqxGrid('source');
				tmpS._source.url = "jqxGeneralServicer?sname=jqxGetListOrderItemsAjax&agreementId=" +agreementId;
				$("#jqxgridPackingListDetail").jqxGrid('source', tmpS);
			}else{
				bootbox.dialog("Tải không thành công, chưa chọn hóa đơn mua!", [{
					"label" : "error",
					"class" : "btn-small btn-danger",
					"callback": function(){
						$('#orderTypeSupp').jqxComboBox('clearSelection');
						$("#orderPurchaseId").jqxComboBox('focus');
					}
					}]
				);
			}
}


$("#jqxgrid").on('contextmenu', function () {
    return false;
});
$("#CreateInvoiceTotal").on("click", function() {
		var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
		var billId = rowData.billId;
		window.location.href = "CreateInvoiceTotal?billId=" + billId;
});
 $("#CreateListAttachments").on("click", function() {
	 var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
	 var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
	 var billId = rowData.billId;
	 window.location.href = "CreateListAttachments?billId=" + billId;
 });
 $("#SentTwoNotifice").on("click", function() {
	 var rowIndexSelected = $('#jqxgrid').jqxGrid('getSelectedRowindex');
	 var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowIndexSelected);
	 var billNumber = rowData.billNumber;
	 var header = "Co van don sap ve: " + billNumber;
	 var openTime = null;
	 sentNotify(header, openTime);
	 header = "10 ngay nua van don " + billNumber + " se ve.";
	 openTime = new Date().getTime() + 10*86400000;
	 sentNotify(header, openTime);
 });
 $("#Advances").on("click", function() {
	 window.location.href = "billOfLadingCost?party=IMPORT_ADMIN";
 });
 

 function alterSaveContainerAndPackingList(dataJson, dataRows){
 	$.ajax({
 	     url: "createContainerAndPackingList",
 	     type: "POST",
 	     data: {packingList: JSON.stringify(dataJson), packingListDetail: JSON.stringify(dataRows)},
 	     async: false,
 	     success: function(res) {
 	    	 if(dataJson.containerId == null || dataJson.containerId == '' || !dataJson.containerId){
 	    		addNewRowMainJqxDetail(dataJson, res.resultContainer, res.containerId);
 	    		console.log(res.resultContainer.listPackingListHeader);
 	    		$('#packingListNumber').jqxComboBox({source: res.resultContainer.listPackingListHeader, searchMode: 'containsignorecase'});
 	    		$('#containerId').val(res.containerId);
 	    		$('#indexGridDetail').val(0);
 	    	 }else{
 	    		updateRowMainJqxDetail(dataJson, res.resultContainer);
 	    		console.log(res.resultContainer.listPackingListHeader);
 	    		$('#packingListNumber').jqxComboBox({source: res.resultContainer.listPackingListHeader, searchMode: 'containsignorecase'});
// 	    		console.log(res.resultContainer.listPackingListHeader);
// 	    		console.log(res.resultContainer);
 	    	 }
 	     }
 	 });
 }

 function getPackingListDetail(){
	 var rows = $('#jqxgridPackingListDetail').jqxGrid('getboundrows');
	 var rowsReturn = [];
	 var check = true;
	 var uid=0;
	 for(var i=0; i<rows.length; i++){
		 if(rows[i].productId != "" && rows[i].productId != null){
			 rowsReturn.push(rows[i]);
		 }
	 }
	 for(var i=0; i<rowsReturn.length; i++){
		 if(rowsReturn[i].expireDate != null && rowsReturn[i].expireDate != "" && rowsReturn[i].datetimeManufactured != null && rowsReturn[i].datetimeManufactured != ""){
		 }else{
			 check = false;
			 uid = rowsReturn[i].uid;
			 break;
		 }
	 }
	 if(check){
		 for(var i=0; i<rowsReturn.length; i++){
			 if(rowsReturn[i].expireDate instanceof Date && rowsReturn[i].datetimeManufactured instanceof Date){
				 rowsReturn[i].expireDate = rowsReturn[i].expireDate.getTime();
				 rowsReturn[i].datetimeManufactured = rowsReturn[i].datetimeManufactured.getTime();
			 }
		 }
	 }
	 var returnArr = {rowsReturn: rowsReturn, check: check, uid: uid};
	 return returnArr;
 }
 
 function getFormPL(){
	 		var billId = $('#billId').val();
 			var containerId = $('#containerId').val();
 			var containerNumber = $('#containerNumber').val();
 			var sealNumber = $('#sealNumber').val();
 			var purchaseOrderId = "";
 			var labelPurchaseId = "";
 			var valueComboOrder = $('#orderPurchaseId').jqxComboBox('getSelectedItem');
 			if(valueComboOrder){
 				purchaseOrderId = valueComboOrder.value;
 				labelPurchaseId = valueComboOrder.label;
 			}
 			var packingListId = $('#packingListId').val();
 			
 			var packingListNumber = $('#packingListNumber').val();
 			var valueComboPL = $('#packingListNumber').jqxComboBox('getSelectedItem');
 			if(valueComboPL){
 				packingListNumber = valueComboPL.label;
 			}
 			var orderNumberSupp = $('#orderNumberSupp').val();
 			var invoiceNumber = $('#invoiceNumber').val();
 			var totalNetWeight = $('#totalNetWeight').val();
 			var totalGrossWeight = $('#totalGrossWeight').val();
 			var packingListDate = $('#packingListDate').jqxDateTimeInput('getDate').getTime();
// 			var packingListDateLong = packingListDate.getTime();
 			var invoiceDate = $('#invoiceDate').jqxDateTimeInput('getDate').getTime();
// 			var invoiceDateLong = invoiceDate.getTime();
 			var orderTypeSuppId = "";
 			var valueCombo = $('#orderTypeSupp').jqxComboBox('getSelectedItem');
 			if(valueCombo){
 				orderTypeSuppId = valueCombo.value;
 			}
 			var gridDetailId = $('#gridDetailId').val();
 			var dataJson = {
 					packingListId: packingListId, purchaseOrderId: purchaseOrderId, sealNumber: sealNumber, containerNumber: containerNumber, containerId: containerId,
 					packingListNumber: packingListNumber, orderNumberSupp: orderNumberSupp, invoiceNumber: invoiceNumber,
 					totalNetWeight: totalNetWeight, totalGrossWeight: totalGrossWeight, packingListDate: packingListDate,
 					invoiceDate: invoiceDate, orderTypeSuppId: orderTypeSuppId, billId : billId, gridDetailId: gridDetailId, agreementName: labelPurchaseId
 			};
 			return dataJson;
 		}
 
 function changeSourceGrid(){
		var tmpS = $("#jqxgridPackingListDetail").jqxGrid('source');
		tmpS._source.url = "";
		$("#jqxgridPackingListDetail").jqxGrid('source', tmpS);
		$('#jqxgridPackingListDetail').jqxGrid('clear');
 }
 function sentNotify(header, openTime) {
	 var jsonObject = {partyId: "ImportSpecialist",
				header: header,
				openTime: openTime,
				action: "receiveAgreement"};
	$.ajax({
	     url: "createNotification",
	     type: "POST",
	     data: jsonObject,
	     async: false,
	     success: function(res) {
	     	
	     }
	 }).done(function() {
	 	
	});
}
//add new row for jqxDetailGrid
 function addNewRowMainJqxDetail(dataJson, resultContainer, containerId){
 	var newData = {agreementId: dataJson.purchaseOrderId, containerId: containerId, containerNumber: dataJson.containerNumber, sealNumber: dataJson.sealNumber, externalOrderNumber: resultContainer.externalOrderNumber,
 			agreementName: dataJson.agreementName, netWeightTotal: parseFloat(resultContainer.netWeightTotal), grossWeightTotal: parseFloat(resultContainer.grossWeightTotal), packingUnitTotal : parseFloat(resultContainer.packingUnitTotal)
 	};
 	$("#"+dataJson.gridDetailId).jqxGrid('addrow', null, newData,'first');
 };
 // end add new row for jqxDetailGrid
 //start update row jqxDetailGrid
 function updateRowMainJqxDetail(dataJson, resultContainer){
 	var index = $('#indexGridDetail').val();
 	var grid = $('#gridDetailId').val();
 	var data = $('#'+grid).jqxGrid('getrowdata', index);
 	$("#"+grid).jqxGrid('setcellvalue', index, "containerNumber", dataJson.containerNumber);
 	$("#"+grid).jqxGrid('setcellvalue', index, "externalOrderNumber", resultContainer.externalOrderNumber);
 	$("#"+grid).jqxGrid('setcellvalue', index, "sealNumber", dataJson.sealNumber);
 	$("#"+grid).jqxGrid('setcellvalue', index, "grossWeightTotal", parseFloat(resultContainer.grossWeightTotal));
 	$("#"+grid).jqxGrid('setcellvalue', index, "netWeightTotal", parseFloat(resultContainer.netWeightTotal));
 	$("#"+grid).jqxGrid('setcellvalue', index, "packingUnitTotal", parseFloat(resultContainer.packingUnitTotal));
 };

 //BEGIN function Menu viewDetailCont
 	$('#viewDetailCont').on('click', function(){
 		$('#saveAndContinueContainer').css('display', 'none');
 		$('#customcontrol1jqxgridPackingListDetail').css('display', 'none');
 		 AddAgreementToRow(indexParentGrid,'jqxgridDetail'+indexParentGrid);
 		 var dataRow = $('#jqxgridDetail'+indexParentGrid).jqxGrid('getrowdata', indexChildGrid);
 		 $('#indexGridDetail').val(indexChildGrid);
 		 $('#containerId').val(dataRow.containerId);
 		 $('#containerNumber').val(dataRow.containerNumber);
 		 $('#sealNumber').val(dataRow.sealNumber);
// 		 $('#orderPurchaseId').jqxComboBox('selectItem',dataRow.agreementId);
//// 		 $('#containerNumber').attr('disabled', 'disabled');
//// 		 $('#sealNumber').attr('disabled', 'disabled');
// 		 $('#orderPurchaseId').jqxComboBox({ disabled: true });
 		 jQuery.ajax({
 	        url: 'getPackingListByContainer',
 	        type: 'POST',
 	        async: false,
 	        data: {containerId: dataRow.containerId},
 	        dataType: 'json',
 	        success: function(res){
 	        	var listPackingList = res.listPackingList;
 	        	if(listPackingList.length > 0){
 		        	$('#packingListNumber').jqxComboBox({source: listPackingList, searchMode: 'containsignorecase'});
 		        	$('#packingListNumber').jqxComboBox('selectIndex', 0 );
 	        	}
 	        	var listAgreement = res.listAgreement;
 	        	if(listAgreement.length > 0){
// 	        		console.log(listAgreement);
 	        		$('#orderPurchaseId').jqxComboBox({source: listAgreement, searchMode: 'containsignorecase'});
 	        		$('#orderPurchaseId').jqxComboBox('selectIndex',0);
// 	        		$('#orderPurchaseId').jqxComboBox({ disabled: true });
 	        	}
 	        }
 		 });
 		 
 	 });
 //ENd function Menu viewDetailCont
 //End update row jqxDetailGrid
$('#saveDoc').on('click', function(){
	var documentCustomsId = $('#documentCustomsId').val();
	var containerId = $('#containerCustomsId').val();
	var documentCustomsTypeId = $('#documentCustomsTypeId').val();
	var registerNumber = $('#registerNumber').val();
	var registerDate = $('#registerDate').jqxDateTimeInput('getDate').getTime();
	var sampleSendDate = $('#sampleSentDate').jqxDateTimeInput('getDate').getTime();
	jQuery.ajax({
	        url: 'updateDocumentCustomsAjax',
	        type: 'POST',
	        async: false,
	        data: {containerId: containerId, documentCustomsId: documentCustomsId, documentCustomsTypeId: documentCustomsTypeId, registerNumber: registerNumber, registerDate: registerDate, sampleSendDate: sampleSendDate},
	        dataType: 'json',
	        success: function(res){
	        	$('#popupDocQA').jqxWindow('close');
	        }
		 });
});

 	
 	
