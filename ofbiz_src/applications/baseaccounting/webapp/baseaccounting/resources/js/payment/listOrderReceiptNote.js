var OrderReceiptNote = (function(){
	var _selectedRowObj = [];
	var _selectedRowDataObj = [];
	var init = function(){
		initContextMenu();
		initEvent();
	};
	
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 200);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgridListOrderReceiptNote").jqxGrid('getselectedrowindex');
			var action = $(args).attr("action");
			var data = {orderIds: _selectedRowObj};
			if(action = "createARPayment"){
				var bankIds = [];
				var bankName = "";
				for (var i = 0; i < _selectedRowDataObj.length; i++) {
					var bankId = _selectedRowDataObj[i].bankId;
					if (bankIds.indexOf(bankId) == -1) {
						bankIds.push(bankId);
						bankName = _selectedRowDataObj[i].bankName;
					}
				}
				if (bankIds.length > 1) {
					bootbox.dialog(uiLabelMap.BACCNotifyTakeMoneyFromBank,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);	
				} else {
					data.bankId = bankIds[0];
					data.bankName = bankName;
					createARPaymentObj.openWindow(data);//createARPaymentObj is defined in createPaymentForBank.js
				}
			}
		});
	};
	
	var initEvent = function(){
		$("#jqxgridListOrderReceiptNote").on('bindingcomplete', function(event){
			var pageInfo = getFirstAndLastRowIndexInPage();
			var selectedRowIndexes = getSelectedRowIndexes();
			for(var i = pageInfo.start; i < pageInfo.end; i++){
				if(selectedRowIndexes.indexOf(i) < 0){
					$("#jqxgridListOrderReceiptNote").jqxGrid('unselectrow', i);
				}else{
					$("#jqxgridListOrderReceiptNote").jqxGrid('selectrow', i);
				}
			}
		});
		$("#jqxgridListOrderReceiptNote").on('rowselect', function(event){
			var args = event.args;
			var rowIndex = args.rowindex;
			if(typeof(rowIndex) == 'object'){ //selected all checkbox is checked
				var pageInfo = getFirstAndLastRowIndexInPage();
				for(var i = pageInfo.start; i < pageInfo.end; i++){
					var rowid = $('#jqxgridListOrderReceiptNote').jqxGrid('getrowid', i);
					if(_selectedRowObj.indexOf(rowid) < 0){
						addSelectedRow(rowid);
						addSelectedRowData($('#jqxgridListOrderReceiptNote').jqxGrid('getrowdata', i));
					}
				}
			}else{
				var rowid = $('#jqxgridListOrderReceiptNote').jqxGrid('getrowid', rowIndex);
				if(_selectedRowObj.indexOf(rowid) < 0){
					addSelectedRow(rowid);
					addSelectedRowData($('#jqxgridListOrderReceiptNote').jqxGrid('getrowdata', rowIndex));
				}
			}
		});
		$("#jqxgridListOrderReceiptNote").on('rowunselect', function(event){
			var args = event.args;
			var rowIndex = args.rowindex;
			if(rowIndex < -1){//selected all checkbox is unchecked 
				var pageInfo = getFirstAndLastRowIndexInPage();
				for(var i = pageInfo.start; i < pageInfo.end; i++){
					var rowid = $('#jqxgridListOrderReceiptNote').jqxGrid('getrowid', i);
					var indexOfEle = _selectedRowObj.indexOf(rowid); 
					if(indexOfEle > -1){
						removeRowSelect(rowid);
						removeRowDataSelect($('#jqxgridListOrderReceiptNote').jqxGrid('getrowdata', i));
					}
				}
			}else if(rowIndex > -1){
				var rowid = $('#jqxgridListOrderReceiptNote').jqxGrid('getrowid', rowIndex);
				var indexOfEle = _selectedRowObj.indexOf(rowid); 
				if(indexOfEle > -1){
					removeRowSelect(rowid);
					removeRowDataSelect($('#jqxgridListOrderReceiptNote').jqxGrid('getrowdata', rowIndex));
				}
			}
		});
	};
	
	var addSelectedRow = function(rowid){
		_selectedRowObj.push(rowid);
	};
	
	var addSelectedRowData = function(data){
		_selectedRowDataObj.push(data);
	};
	
	var removeRowSelect = function(rowid){
		var eleIndex = _selectedRowObj.indexOf(rowid);
		if(eleIndex > -1){
			_selectedRowObj.splice(eleIndex, 1);
		}
	};
	
	var removeRowDataSelect = function(data){
		var eleIndex = _selectedRowDataObj.indexOf(data);
		if(eleIndex > -1){
			_selectedRowDataObj.splice(eleIndex, 1);
		}
	};
	
	var getSelectedRowIndexes = function(){
		var arr = [];
		_selectedRowObj.forEach(function(rowid){
			var rowindex = $('#jqxgridListOrderReceiptNote').jqxGrid('getrowboundindexbyid', rowid);
			if(rowindex > -1){
				arr.push(rowindex);
			}
		});
		return arr;
	};
	
	var getFirstAndLastRowIndexInPage = function(){
		var datainformation = $('#jqxgridListOrderReceiptNote').jqxGrid('getdatainformation');
		var paginginformation = datainformation.paginginformation;
		var rowscount = datainformation.rowscount;
		var pagenum = paginginformation.pagenum;
		var pagesize = paginginformation.pagesize;
		var start = pagenum * pagesize;
		var end = start + pagesize;
		if(end > rowscount){
			end = rowscount;
		}
		return {start: start, end: end};
	};
	
	var reset = function() {
		_selectedRowObj = [];
		_selectedRowDataObj = [];
	};
	
	return {
		init: init,
		reset: reset
	}
}());	

$(document).ready(function(){	
	OrderReceiptNote.init();	
});