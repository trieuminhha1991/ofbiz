$(function(){
	StockInObj.init();
});
var StockInObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInputs = function(){
		var productData = [];
	};
	
	var initElementComplex = function(){
		
	};
	
	var initValidateForm = function(){
		
	};
	
	var initEvents = function(){
	};
	
	function addNewRow(){
		var firstRow = $('#jqxgridOrderInven').jqxGrid('getrowdata', 0);
		var selectedIndexs = $('#jqxgridOrderInven').jqxGrid('getselectedrowindexes');
		if (firstRow.productCode){
			$('#jqxgridOrderInven').jqxGrid('clearselection');
			var datarow = generaterow();
	        $("#jqxgridOrderInven").jqxGrid('addrow', null, datarow, "first");
	        $("#jqxgridOrderInven").jqxGrid('unselectrow', 0);
	        for (var i = 0; i < selectedIndexs.length; i ++){
				$("#jqxgridOrderInven").jqxGrid('selectrow', selectedIndexs[i] + 1);
			}
	        $("#jqxgridOrderInven").jqxGrid('begincelledit', 0, "productCode");
		} else {
			$("#jqxgridOrderInven").jqxGrid('begincelledit', 0, "productCode");
		}
	}
	
	function generaterow(productCode){
		var row = {};
		if (productCode){
			var listSames = [];
			for(var i = 0; i < productData.length; i++){
				var item = productData[i];
				if (item.productCode == productCode){
					listSames.push(item);
				}
			}
			if (listSames.length > 0){
				var dlvItem = listSames[0];
				row["productId"] = dlvItem.productId;
				row["productCode"] = dlvItem.productCode;
				row["productName"] = dlvItem.productName;
				row["datetimeManufactured"] =  new Date(dlvItem.datetimeManufactured);
				row["expireDate"] = new Date(dlvItem.expireDate);
				row["deliveryId"] = dlvItem.deliveryId;
				row["quantityOrderItem"] = dlvItem.quantityOrderItem;
				row["actualExportedQuantity"] = dlvItem.actualExportedQuantity;
				row["quantityOnHandDiff"] = dlvItem.quantityOnHandDiff;
				row["quantityRecieve"] = 0;
				row["fromOrderId"] = dlvItem.fromOrderId;
				row["fromOrderItemSeqId"] = dlvItem.fromOrderItemSeqId;
				row["lotId"] = dlvItem.lotId;
			}
		} else {
			row["productId"] = "";
			row["productCode"] = "";
			row["productName"] = "";
			row["datetimeManufactured"] = "";
			row["expireDate"] = "";
			row["deliveryId"] = "";
			row["quantityOrderItem"] = "";
			row["actualExportedQuantity"] = "";
			row["actualExportedQuantity"] = "";
			row["quantityOnHandDiff"] = "";
			row["quantityRecieve"] = "";
			row["quantityUomId"] = "";
			row["fromOrderId"] = "";
			row["fromOrderItemSeqId"] = "";
			row["lotId"] = "";
		}
		return row;
	}
	
	var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridOrderInven").jqxGrid('getrowid', 0);
        $("#jqxgridOrderInven").jqxGrid('updaterow', id, datarow);
	}
	
	return {
		init: init,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
	}
}());