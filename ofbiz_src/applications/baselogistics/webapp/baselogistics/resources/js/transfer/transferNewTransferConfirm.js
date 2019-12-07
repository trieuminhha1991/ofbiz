$(function(){
	if (typeof(dataSelected) == "undefined") var dataSelected = [];
	OlbTransferConfirm.init();
});
var OlbTransferConfirm = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
	};
	var initInputs = function() {
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#jqxgridProductSelected").on("cellendedit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "quantity") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProductSelected").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.productId) {
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (isNaN(newValue)) {
			   			$('#jqxgridProductSelected').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', oldValue);
			   			return false;
			   		}
			   		if (newValue > 0){
			   			var existed = false;
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					olb['quantity'] = newValue;
			   					listProductSelected.push(olb);
			   					existed = true;
			   					return false;
			   				}
			   			});
			   			if (existed == false){
			   				var map = {};
					   		map['productId'] = data.productId;
					   		map['productCode'] = data.productCode;
					   		map['productName'] = data.productName;
					   		map['expireDate'] = data.expireDate;
					   		map['quantity'] = newValue;
					   		map['quantityUomId'] = data.quantityUomId;
					   		map['quantityOnHandTotal'] = data.quantityOnHandTotal;
				   		    map['availableToPromiseTotal'] = data.availableToPromiseTotal;
		   		            map['packingUomIds'] = data.packingUomIds;
			                listProductSelected.push(map);
			   			}
			   		} else {
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.lotId == data.lotId && olb.facilityId == data.facilityId && olb.expireDate.getTime() == data.expireDate.getTime() && olb.datetimeManufactured.getTime() == data.datetimeManufactured.getTime() && olb.datetimeReceived.getTime() == data.datetimeReceived.getTime() && olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
			   		}
		    	}
	    	} else if (args.datafield == "quantityUomId") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProductSelected").jqxGrid("getrowdata", rowBoundIndex);
		    	if (ValidateObj.isNotEmpty(data) && ValidateObj.isNotEmpty(data.productId) && ValidateObj.isNotEmpty(data.quantity)){
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		
			   		if (isNaN(quantity)) {
			   			$('#jqxgridProductSelected').jqxGrid('setcellvalue', rowBoundIndex, 'quantityUomId', oldValue);
			   			return false;
			   		}
			   		if (data.quantity > 0){
			   			var map = {};
			   			map['productId'] = data.productId;
				   		map['productCode'] = data.productCode;
				   		map['productName'] = data.productName;
				   		map['expireDate'] = data.expireDate;
				   		map['quantity'] = newValue;
				   		map['quantityUomId'] = data.quantityUomId;
				   		map['quantityOnHandTotal'] = data.quantityOnHandTotal;
			   		    map['availableToPromiseTotal'] = data.availableToPromiseTotal;
	   		            map['packingUomIds'] = data.packingUomIds;
		                listProductSelected.push(map);
			   		} 
		    	}
	    	}
    	});
	};
	return {
		init: init
	};
}());