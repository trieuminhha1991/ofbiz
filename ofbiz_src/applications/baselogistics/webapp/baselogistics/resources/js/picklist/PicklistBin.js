$(function(){
	OlbPicklistBinList.init();
});
var OlbPicklistBinList = (function(){
	var _selectedRowObj = [];
	var _selectedData = [];
	
	var initElement = (function(){
		$("#contextMenuPicklistBin").jqxMenu({ theme: theme, width: 220, autoOpenPopup: false, mode: "popup" });
	});
	
	var initEvent = function(){
		$("#contextMenuPicklistBin").on('itemclick', function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			var rowIndexSelected = $("#jqxgridPicklistBin").jqxGrid("getSelectedRowindex");
			
			switch (itemId) {
				case "binViewDetail":
					if (_selectedData.length == 1) {
						location.href = "PicklistDetail?picklistBinId=" + _selectedData[0].picklistBinId;
					}
					break;
				case "binApprove":
					if (_selectedData.length == 1 && (!_selectedData[0].partyPickId || !_selectedData[0].partyCheckId)){
						jOlbUtil.alert.error(uiLabelMap.BLYouNotUpdateEmployeeYet);
					} else {
						approveBin(_selectedData);
					}
					break;
				case "binCancel":
					cancelBin(_selectedData);
					break;
				case "mnuCreateDelivery":
					if (_selectedData.length == 1 && _selectedData[0].picklistBinId){
						createDelivery(_selectedData[0].picklistBinId);
					}
					break;
				case "binEmployee":
					if (_selectedData.length == 1) {
						ChangeEmployee.open($("#jqxgridPicklistBin"), _selectedData[0].picklistBinId, $("#jqxgridPicklistBin").jqxGrid("getcellvalue", rowIndexSelected, "partyPickId"), $("#jqxgridPicklistBin").jqxGrid("getcellvalue", rowIndexSelected, "partyCheckId"));
					}
					break;
				default:
					break;
			}
		});
		
		$("#contextMenuPicklistBin").on("shown", function () {
				var rowindex = $("#jqxgridPicklistBin").jqxGrid('getSelectedRowindex');
				var dataRecord = $("#jqxgridPicklistBin").jqxGrid('getRowData', rowindex);
				var binStatusId = dataRecord.binStatusId;
				if (binStatusId === "PICKBIN_INPUT" || binStatusId === "PICKBIN_APPROVED" || binStatusId === "PICKBIN_PICKED" || binStatusId === "PICKBIN_CHECKED") {
					$("#contextMenuPicklistBin").jqxMenu("disable", "binCancel", false);
				} else {
					$("#contextMenuPicklistBin").jqxMenu("disable", "binCancel", true);
				}
				if (binStatusId === "PICKBIN_INPUT" || binStatusId === "PICKBIN_PICKED" || binStatusId === "PICKBIN_CHECKED") {
					$("#contextMenuPicklistBin").jqxMenu("disable", "binApprove", false);
				} else {
					$("#contextMenuPicklistBin").jqxMenu("disable", "binApprove", true);
				}
				if (binStatusId === "PICKBIN_INPUT" || binStatusId === "PICKBIN_APPROVED" || binStatusId === "PICKBIN_PICKED" || binStatusId === "PICKBIN_CHECKED") {
					$("#contextMenuPicklistBin").jqxMenu("disable", "binEmployee", false);
				} else {
					$("#contextMenuPicklistBin").jqxMenu("disable", "binEmployee", true);
				}
				if (binStatusId === "PICKBIN_APPROVED") {
					$("#contextMenuPicklistBin").jqxMenu("disable", "mnuCreateDelivery", false);
				} else {
					$("#contextMenuPicklistBin").jqxMenu("disable", "mnuCreateDelivery", true);
				}
				$("#contextMenuPicklistBin").jqxMenu("disable", "binViewDetail", false);
		});
		
		$("#jqxgridPicklistBin").on('bindingcomplete', function(event) {
			var pageInfo = getFirstAndLastRowIndexInPage();
			var selectedRowIndexes = getSelectedRowIndexes();
			for (var i = pageInfo.start; i < pageInfo.end; i++) {
				if (selectedRowIndexes.indexOf(i) < 0) {
					$("#jqxgridPicklistBin").jqxGrid('unselectrow', i);
				} else {
					$("#jqxgridPicklistBin").jqxGrid('selectrow', i);
				}
			}
		});
		
		$("#jqxgridPicklistBin").on('rowselect', function(event) {
			var args = event.args;
			var rowIndex = args.rowindex;
			if (typeof (rowIndex) == 'object') {// selected all checkbox is checked
				var pageInfo = getFirstAndLastRowIndexInPage();
				for (var i = pageInfo.start; i < pageInfo.end; i++) {
					var rowid = $('#jqxgridPicklistBin').jqxGrid('getrowid', i);
					var data = $('#jqxgridPicklistBin').jqxGrid('getrowdata', i);
					if (_selectedRowObj.indexOf(rowid) < 0) {
						addSelectedRow(rowid);
						_selectedData.push(data);
					}
				}
			} else {
				var rowid = $('#jqxgridPicklistBin').jqxGrid('getrowid', rowIndex);
				var data = $('#jqxgridPicklistBin').jqxGrid('getrowdata', rowIndex);
				if (_selectedRowObj.indexOf(rowid) < 0) {
					addSelectedRow(rowid);
					_selectedData.push(data);
				}
			}
		});
		
		$("#jqxgridPicklistBin").on('rowunselect', function(event) {
			var args = event.args;
			var rowIndex = args.rowindex;
			if (rowIndex < -1) {// selected all checkbox is unchecked
				var pageInfo = getFirstAndLastRowIndexInPage();
				for (var i = pageInfo.start; i < pageInfo.end; i++) {
					var rowid = $('#jqxgridPicklistBin').jqxGrid('getrowid', i);
					var data = $('#jqxgridPicklistBin').jqxGrid('getrowdata', i);
					var indexOfEle = _selectedRowObj.indexOf(rowid);
					if (indexOfEle > -1) {
						removeRowSelect(rowid);
						_selectedData.splice(_selectedData.indexOf(data), 1);
					}
				}
			} else if (rowIndex > -1) {
				var rowid = $('#jqxgridPicklistBin').jqxGrid('getrowid', rowIndex);
				var data = $('#jqxgridPicklistBin').jqxGrid('getrowdata', rowIndex);
				var indexOfEle = _selectedRowObj.indexOf(rowid);
				if (indexOfEle > -1) {
					removeRowSelect(rowid);
					_selectedData.splice(_selectedData.indexOf(data), 1);
				}
			}
		});
	};
	
	var addSelectedRow = function(rowid) {
		_selectedRowObj.push(rowid);
	};

	var removeRowSelect = function(rowid) {
		var eleIndex = _selectedRowObj.indexOf(rowid);
		if (eleIndex > -1) {
			_selectedRowObj.splice(eleIndex, 1);
		}
	};

	var getSelectedRowIndexes = function() {
		var arr = [];
		_selectedRowObj.forEach(function(rowid) {
			var rowindex = $('#jqxgridPicklistBin').jqxGrid('getrowboundindexbyid', rowid);
			if (rowindex > -1) {
				arr.push(rowindex);
			}
		});
		return arr;
	};

	var getFirstAndLastRowIndexInPage = function() {
		var datainformation = $('#jqxgridPicklistBin').jqxGrid('getdatainformation');
		var paginginformation = datainformation.paginginformation;
		var rowscount = datainformation.rowscount;
		var pagenum = paginginformation.pagenum;
		var pagesize = paginginformation.pagesize;
		var start = pagenum * pagesize;
		var end = start + pagesize;
		if (end > rowscount) {
			end = rowscount;
		}
		return {
			start : start,
			end : end
		};
	};
	
	var approveBin = function (data) {
		var listPicklistBin = [];
		for(var i = 0; i < data.length; i++){
			var item = data[i];
			var map = {};
			map['picklistBinId'] = item.picklistBinId;
			map['binStatusId'] = item.binStatusId;
			map['partyPickId'] = item.partyPickId;
			map['partyCheckId'] = item.partyCheckId;
			listPicklistBin.push(map);
		}
		var listPicklistBinStr = JSON.stringify(listPicklistBin);
		
		bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
			"callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
			"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
			"callback": function() {
				Loading.show("loadingMacro");
				setTimeout(function(){
					$.ajax({ 
						url: "approveListPicklistBinStatus",
						data: {
							listPicklistBin: listPicklistBinStr
						},
						type: "POST",
						async: false,
						success: function (res){
							if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
								jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
								return false;
							}
							_selectedRowObj = [];
							_selectedData = [];
							$("#jqxgridPicklistBin").jqxGrid("clearSelection");
							$("#jqxgridPicklistBin").jqxGrid("updatebounddata");
						}
					});
					Loading.hide("loadingMacro");
				}, 500);
			}
		}]);
	}
	
	var cancelBin = function (data) {
		var listPicklistBin = [];
		for(var i = 0; i < data.length; i++){
			var item = data[i];
			var map = {};
			map['picklistBinId'] = item.picklistBinId;
			map['binStatusId'] = item.binStatusId;
			listPicklistBin.push(map);
		}
		var listPicklistBinStr = JSON.stringify(listPicklistBin);
		
		bootbox.dialog(uiLabelMap.AreYouSureCancel, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
			"callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
			"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
			"callback": function() {
				Loading.show("loadingMacro");
				setTimeout(function(){
					$.ajax({ 
						url: "deleteListPicklistBinStatus",
						data: {
							listPicklistBin: listPicklistBinStr
						},
						type: "POST",
						async: false,
						success: function (res){
							if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
								jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
								return false;
							}
							_selectedRowObj = [];
							_selectedData = [];
							$("#jqxgridPicklistBin").jqxGrid("clearSelection");
							$("#jqxgridPicklistBin").jqxGrid("updatebounddata");
						}
					});
					Loading.hide("loadingMacro");
				}, 500);
			}
		}]);
	}
	
	var init = (function(){
		initElement();
		initEvent();
	});
	
	var createDelivery = function(picklistBinId){
		jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
			Loading.show('loadingMacro');
			setTimeout(function(){
				$.ajax({
					type: 'POST',
					url: 'createDeliveryFromPicklistBin',
					async: false,
					data: {
						picklistBinId: picklistBinId,
					},
					success: function(res){
						if(res._ERROR_MESSAGE_ || !res.deliveryId){
							if(res._ERROR_MESSAGE_){
								jOlbUtil.alert.error(uiLabelMap.UpdateError+ ": "+res._ERROR_MESSAGE_);
								return false;
							}
						} else {
							var deliveryId = res.deliveryId;
							window.location.href = 'deliverySalesDeliveryDetail?deliveryId='+deliveryId;
						}
					},
				});
				Loading.hide('loadingMacro');
			}, 500);
		});
	}
	
	return {
		init: init,
	}
}());