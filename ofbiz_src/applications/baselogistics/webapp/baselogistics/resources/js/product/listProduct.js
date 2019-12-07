$(function(){
	ProductObj.init();
});
var ProductObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
	
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#jqxgridProduct").on("cellendedit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "quantity") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.productId) {
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue === null || newValue === undefined || newValue === '') {
			   			$('#jqxgridProduct').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', oldValue);
			   			return false;
			   		}
			   		if (newValue > 0){
			   			var existed = false;
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					olb['quantity'] = newValue;
			   					olb['rowBoundIndex'] = rowBoundIndex;
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
					   		map['expiredDate'] = data.expiredDate;
					   		map['quantity'] = newValue;
					   		map['unitCost'] = data.unitCost;
					   		map['quantityUomId'] = data.quantityUomId;
					   		map['weightUomId'] = data.weightUomId;
					   		map['uomId'] = data.uomId;
					   		map['requireAmount'] = data.requireAmount;
		   		            map['packingUomIds'] = data.packingUomIds;
		   		            map['weightUomIds'] = data.weightUomIds;
		   		            map['description'] = data.description;
		   		            map['rowBoundIndex'] = rowBoundIndex;
			                listProductSelected.push(map);
			   			}
			   		} else {
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
			   		}
		    	}
	    	} else if (args.datafield == "uomId") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
		    	if (ValidateObj.isNotEmpty(data) && ValidateObj.isNotEmpty(data.productId) && ValidateObj.isNotEmpty(data.quantity)){
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		
			   		if (data.quantity === undefined || data.quantity === null || data.quantity === '') {
			   			$('#jqxgridProduct').jqxGrid('setcellvalue', rowBoundIndex, 'uomId', oldValue);
			   			return false;
			   		}
			   		$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == data.productId){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
			   		if (data.quantity > 0){
			   			var map = {};
			   			map['productId'] = data.productId;
				   		map['productCode'] = data.productCode;
				   		map['productName'] = data.productName;
				   		map['expiredDate'] = data.expiredDate;
				   		map['quantity'] = data.quantity;
				   		map['unitCost'] = data.unitCost;
				   		map['quantityUomId'] = newValue;
				   		map['weightUomId'] = newValue;
				   		map['uomId'] = newValue;
	   		            map['weightUomIds'] = data.weightUomIds;
	   		            map['packingUomIds'] = data.packingUomIds;
	   		            map['requireAmount'] = data.requireAmount;
	   		            map['description'] = data.description;
	   		            map['rowBoundIndex'] = rowBoundIndex;
		                listProductSelected.push(map);
			   		} 
		    	}
	    	} else if (args.datafield == "expiredDate"){
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
		    	if (ValidateObj.isNotEmpty(data) && ValidateObj.isNotEmpty(data.productId) && ValidateObj.isNotEmpty(data.quantity)){
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue != null && newValue != undefined && newValue != ''){
			   			var existed = false;
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					olb['expiredDate'] = newValue;
			   					olb['rowBoundIndex'] = rowBoundIndex;
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
					   		map['expiredDate'] = newValue;
					   		map['quantity'] = data.quantity;
					   		map['unitCost'] = data.unitCost;
					   		map['quantityUomId'] = data.quantityUomId;
					   		map['uomId'] = data.uomId;
					   		map['weightUomId'] = data.weightUomId;
		   		            map['packingUomIds'] = data.packingUomIds;
		   		            map['weightUomIds'] = data.weightUomIds;
		   		            map['description'] = data.description;
		   		            map['requireAmount'] = data.requireAmount;
		   		            map['rowBoundIndex'] = rowBoundIndex;
			                listProductSelected.push(map);
			   			}
			   		} else {
			   		}
		    	}
	    	} else if (args.datafield == "unitCost"){
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
		    	if (ValidateObj.isNotEmpty(data) && ValidateObj.isNotEmpty(data.productId) && ValidateObj.isNotEmpty(data.quantity)){
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue != null && newValue != undefined && newValue != ''){
			   			var existed = false;
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					olb['unitCost'] = newValue;
			   					olb['rowBoundIndex'] = rowBoundIndex;
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
					   		map['expiredDate'] = data.expiredDate;
					   		map['quantity'] = data.quantity;
					   		map['unitCost'] = newValue;
					   		map['quantityUomId'] = data.quantityUomId;
					   		map['uomId'] = data.uomId;
					   		map['weightUomId'] = data.weightUomId;
		   		            map['packingUomIds'] = data.packingUomIds;
		   		            map['weightUomIds'] = data.weightUomIds;
		   		            map['requireAmount'] = data.requireAmount;
		   		            map['description'] = data.description;
		   		            map['rowBoundIndex'] = rowBoundIndex;
			                listProductSelected.push(map);
			   			}
			   		} else {
			   			var existed = false;
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					olb['unitCost'] = 0;
			   					olb['rowBoundIndex'] = rowBoundIndex;
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
					   		map['expiredDate'] = data.expiredDate;
					   		map['quantity'] = data.quantity;
					   		map['unitCost'] = 0;
					   		map['quantityUomId'] = data.quantityUomId;
					   		map['uomId'] = data.uomId;
					   		map['weightUomId'] = data.weightUomId;
		   		            map['packingUomIds'] = data.packingUomIds;
		   		            map['weightUomIds'] = data.packingUomIds;
		   		            map['description'] = data.description;
		   		            map['requireAmount'] = data.description;
		   		            map['rowBoundIndex'] = rowBoundIndex;
			                listProductSelected.push(map);
			   			}
			   		}
		    	}
	    	} else if (args.datafield == "description"){
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
		    	if (ValidateObj.isNotEmpty(data) && ValidateObj.isNotEmpty(data.productId) && ValidateObj.isNotEmpty(data.quantity)){
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue != null && newValue != undefined && newValue != ''){
			   			var existed = false;
			   			$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == data.productId ){
			   					listProductSelected.splice(i,1);
			   					olb['description'] = newValue;
			   					olb['rowBoundIndex'] = rowBoundIndex;
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
					   		map['expiredDate'] = newValue;
					   		map['quantity'] = data.quantity;
					   		map['unitCost'] = data.unitCost;
					   		map['quantityUomId'] = data.quantityUomId;
					   		map['uomId'] = data.uomId;
					   		map['weightUomId'] = data.weightUomId;
		   		            map['packingUomIds'] = data.packingUomIds;
		   		            map['weightUomIds'] = data.weightUomIds;
		   		            map['description'] = data.description;
		   		            map['rowBoundIndex'] = rowBoundIndex;
			                listProductSelected.push(map);
			   			}
			   		} else {
			   		}
		    	}
	    	} 
    	});
	};
	var initValidateForm = function(){
		var extendRules = [];
   		var mapRules = [];
	};
	return {
		init: init,
	}
}());