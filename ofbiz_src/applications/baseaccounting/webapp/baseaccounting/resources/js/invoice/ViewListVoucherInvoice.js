var viewListVoucherObj = (function(){
	var _selectedRowObj = [];
	var _selectedRowDataObj = [];
	var init = function(){
		initColorbox();
		initContextMenu();
		initEvent();
	};
	var initColorbox = function(){
		var colorbox_params = {
				reposition:true,
				scalePhotos:true,
				scrolling:false,
				previous:'<i class="icon-arrow-left"></i>',
				next:'<i class="icon-arrow-right"></i>',
				close:'&times;',
				current:'{current} of {total}',
				maxWidth:'100%',
				maxHeight:'86%',
				onOpen:function(){
					document.body.style.overflow = 'hidden';
				},
				onClosed:function(){
					document.body.style.overflow = 'auto';
				},
				onComplete:function(){
					$.colorbox.resize();
				}
			};

			$('[data-rel="colorbox"]').colorbox(colorbox_params);
			$("#cboxLoadingGraphic").append("<i class='icon-spinner orange'></i>");//let's add a custom loading icon

	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 200);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			var data = {voucherIds: _selectedRowObj};
			
			//check party_from and party_to
			var partyIdFroms = [];
			var partyIdTos = [];
			for (var i = 0; i < _selectedRowDataObj.length; i++) {
				var partyIdFrom = _selectedRowDataObj[i].partyIdFrom;
				var partyIdTo = _selectedRowDataObj[i].partyIdTo;
				if (partyIdFroms.indexOf(partyIdFrom) == -1) {
					partyIdFroms.push(partyIdFrom);
				}
				if (partyIdTos.indexOf(partyIdTo) == -1) {
					partyIdTos.push(partyIdTo);
				}
			}
			
			if(action == "createAPPayment"){
				if (partyIdFroms.length > 1 || partyIdTos.length > 1) {
					bootbox.dialog(uiLabelMap.BACCNotifyCreatePaymentFromVoucher,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);	
				} else {
					data.paymentType = "AP";
					createPaymentObj.openWindow(data);//createPaymentObj is defined in createPaymentForVoucherInvoice.js
				}
			} else if(action = "createARPayment"){
				if (partyIdFroms.length > 1 || partyIdTos.length > 1) {
					bootbox.dialog(uiLabelMap.BACCNotifyCreatePaymentFromVoucher,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);	
				} else {
					data.paymentType = "AR";
					createPaymentObj.openWindow(data);//createPaymentObj is defined in createPaymentForVoucherInvoice.js
				}
			}
		});
	};
	var changeLinkImg = function(row){
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		$("#viewImgVoucher").prop("href", data.objectInfo);
		$("#viewImgVoucher").trigger('click');
	};
	var initEvent = function(){
		$("#jqxgrid").on('bindingcomplete', function(event){
			var pageInfo = getFirstAndLastRowIndexInPage();
			var selectedRowIndexes = getSelectedRowIndexes();
			for(var i = pageInfo.start; i < pageInfo.end; i++){
				if(selectedRowIndexes.indexOf(i) < 0){
					$("#jqxgrid").jqxGrid('unselectrow', i);
				}else{
					$("#jqxgrid").jqxGrid('selectrow', i);
				}
			}
		});
		$("#jqxgrid").on('rowselect', function(event){
			var args = event.args;
			var rowIndex = args.rowindex;
			//var selectedRowIndexes = getSelectedRowIndexes();
			if(typeof(rowIndex) == 'object'){//selected all checkbox is checked
				var pageInfo = getFirstAndLastRowIndexInPage();
				for(var i = pageInfo.start; i < pageInfo.end; i++){
					var rowid = $('#jqxgrid').jqxGrid('getrowid', i);
					if(_selectedRowObj.indexOf(rowid) < 0){
						addSelectedRow(rowid);
						addSelectedRowData($('#jqxgrid').jqxGrid('getrowdata', i));
					}
				}
			}else{
				var rowid = $('#jqxgrid').jqxGrid('getrowid', rowIndex);
				if(_selectedRowObj.indexOf(rowid) < 0){
					addSelectedRow(rowid);
					addSelectedRowData($('#jqxgrid').jqxGrid('getrowdata', rowIndex));
				}
			}
		});
		$("#jqxgrid").on('rowunselect', function(event){
			var args = event.args;
			var rowIndex = args.rowindex;
			if(rowIndex < -1){//selected all checkbox is unchecked 
				var pageInfo = getFirstAndLastRowIndexInPage();
				for(var i = pageInfo.start; i < pageInfo.end; i++){
					var rowid = $('#jqxgrid').jqxGrid('getrowid', i);
					var indexOfEle = _selectedRowObj.indexOf(rowid); 
					if(indexOfEle > -1){
						removeRowSelect(rowid);
						removeRowDataSelect($('#jqxgrid').jqxGrid('getrowdata', i));
					}
				}
			}else if(rowIndex > -1){
				var rowid = $('#jqxgrid').jqxGrid('getrowid', rowIndex);
				var indexOfEle = _selectedRowObj.indexOf(rowid); 
				if(indexOfEle > -1){
					removeRowSelect(rowid);
					removeRowDataSelect($('#jqxgrid').jqxGrid('getrowdata', rowIndex));
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
			var rowindex = $('#jqxgrid').jqxGrid('getrowboundindexbyid', rowid);
			if(rowindex > -1){
				arr.push(rowindex);
			}
		});
		return arr;
	};
	
	var getFirstAndLastRowIndexInPage = function(){
		var datainformation = $('#jqxgrid').jqxGrid('getdatainformation');
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
	var exportExcel = function(){
		var winName='ExportExcel';
		var winURL = 'exportListVoucherInvoiceExcel';
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		document.body.appendChild(form);
		window.open(' ', winName);
		form.target = winName;
		form.submit();                 
		document.body.removeChild(form);
	};
	
	var reset = function() {
		_selectedRowObj = [];
		_selectedRowDataObj = [];
	};
	
	return{
		init: init,
		reset: reset,
		changeLinkImg: changeLinkImg,
		exportExcel: exportExcel
	}
}());
$(document).ready(function () {
	viewListVoucherObj.init();
});