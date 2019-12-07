	//BEGIN cell end edit
	$("#jqxgridPackingListDetail").on('cellEndEdit', function (event) {
	    var args = event.args;
	    var dataField = event.args.datafield;
	    var rowBoundIndex = event.args.rowindex;
	    var value = args.value;
	    var oldvalue = args.oldvalue;
	    var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', rowBoundIndex);
		var data = $('#jqxgridPackingListDetail').jqxGrid('getrowdatabyid', uid);
	    if(dataField == "datetimeManufactured"){
	    }else if(dataField == "expireDate"){
			var datemanu = data.datetimeManufactured;
			var productId = data.productId;
			if(value != null && value != '' && value){
				if (datemanu && datemanu != null && datemanu != '') {
					if(productId && productId != null && productId != ''){
						executeQualityPublication(data, value, productId);
					}else{
						bootbox.dialog(DAYouNotYetChooseProduct, [{
							"label" : "error",
							"class" : "btn-small btn-danger",
							"callback": function(){
								$('#jqxgridPackingListDetail').jqxGrid('begincelledit', rowBoundIndex, 'productId');
							}
							}]
						);
					}
				}else{
					bootbox.dialog(NotChosenDateManu, [{
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
	
var contextMenu = $("#contextMenu").jqxMenu({ width: 300, autoOpenPopup: false, mode: 'popup', theme: theme });

function loadPackingListDetail(packingListId){
	var tmpS = $("#jqxgridPackingListDetail").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=jqxGetPackingListDetail&packingListId=" +packingListId;
	$("#jqxgridPackingListDetail").jqxGrid('source', tmpS);
}

$("#contextMenu").on('itemclick', function (event) {
	var liId = event.args.id;
	var data = $("#jqxgrid").jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
	var billId = data.billId;
	$('#billId').val(billId);
	if (liId == "AddContainer"){
		jQuery.ajax({
	        url: "getExternalOrderType",
	        type: "POST",
	        async: false,
	        data: {},
	        dataType: 'json',
	        success: function(res){
	        	$("#orderPurchaseId").jqxComboBox({
	        		source: res.listAgreementNotBill
	        	});
	        	$("#orderTypeSupp").jqxComboBox({
	    			source: res.listOrderType
	        	});
	        }
	    });
		ContainerManager.openPopupAdd();
	}
});		

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
