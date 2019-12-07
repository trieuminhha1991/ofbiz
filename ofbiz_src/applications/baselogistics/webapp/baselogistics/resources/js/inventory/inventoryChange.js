$(function(){
	InvChangeObj.init();
});
var InvChangeObj = (function() {
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
		$("#jqxgridInventory").on("cellendedit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "quantity") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridInventory").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.productId) {
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (isNaN(newValue)) {
			   			$('#jqxgridInventory').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', oldValue);
			   			return false;
			   		}
			   		if (newValue > 0){
			   			var existed = false;
			   			$.each(listInvChanged, function(i){
			   				var olb = listInvChanged[i];
			   				if (olb.lotId == data.lotId && olb.facilityId == data.facilityId && olb.expireDate.getTime() == data.expireDate.getTime() && olb.datetimeManufactured.getTime() == data.datetimeManufactured.getTime() && olb.datetimeReceived.getTime() == data.datetimeReceived.getTime() && olb.productId == data.productId ){
			   					listInvChanged.splice(i,1);
			   					olb['quantity'] = newValue;
			   					listInvChanged.push(olb);
			   					existed = true;
			   					return false;
			   				}
			   			});
			   			if (existed == false){
			   				if (data.quantityOnHandTotal >= newValue){
				   				var map = {};
						   		map['listInventoryItemIds'] = data.listInventoryItemIds;
						   		map['productId'] = data.productId;
						   		map['productCode'] = data.productCode;
						   		map['productName'] = data.productName;
						   		map['expireDate'] = data.expireDate;
						   		map['datetimeManufactured'] = data.datetimeManufactured;
						   		map['datetimeReceived'] = data.datetimeReceived;
						   		map['quantity'] = newValue;
						   		map['quantityUomId'] = data.quantityUomId;
						   		map['uomId'] = data.uomId;
						   		map['quantityOnHandTotal'] = data.quantityOnHandTotal;
					   		    map['availableToPromiseTotal'] = data.availableToPromiseTotal;
				   		        map['facilityId'] = data.facilityId;
			   		            map['facilityName'] = data.facilityName;
					   		    map['internalName'] = data.internalName;
						   		map['statusId'] = data.statusId;
					   		    map['statusDesc'] = data.statusDesc;
				   		        map['lotId'] = data.lotId;
			   		            map['listQuantityUoms'] = data.listQuantityUoms;
				                listInvChanged.push(map);
			   				}
			   			}
			   		} else {
			   			$.each(listInvChanged, function(i){
			   				var olb = listInvChanged[i];
			   				if (olb.lotId == data.lotId && olb.facilityId == data.facilityId && olb.expireDate.getTime() == data.expireDate.getTime() && olb.datetimeManufactured.getTime() == data.datetimeManufactured.getTime() && olb.datetimeReceived.getTime() == data.datetimeReceived.getTime() && olb.productId == data.productId ){
			   					listInvChanged.splice(i,1);
			   					return false;
			   				}
			   			});
			   		}
		    	}
	    	} else if (args.datafield == "quantityUomId") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridInventory").jqxGrid("getrowdata", rowBoundIndex);
		    	if (OlbCore.isNotEmpty(data) && OlbCore.isNotEmpty(data.productId) && OlbCore.isNotEmpty(data.quantity)) {
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		
			   		if (isNaN(quantity)) {
			   			$('#jqxgridInventory').jqxGrid('setcellvalue', rowBoundIndex, 'quantityUomId', oldValue);
			   			return false;
			   		}
			   		if (data.quantity > 0){
			   			var map = {};
				   		map['listInventoryItemIds'] = data.listInventoryItemIds;
				   		map['productId'] = data.productId;
				   		map['productCode'] = data.productCode;
				   		map['productName'] = data.productName;
				   		map['expireDate'] = data.expireDate;
				   		map['datetimeManufactured'] = data.datetimeManufactured;
				   		map['datetimeReceived'] = data.datetimeReceived;
				   		map['quantity'] = data.quantity;
				   		map['quantityUomId'] = data.quantityUomId;
				   		map['quantityOnHandTotal'] = data.quantityOnHandTotal;
			   		    map['availableToPromiseTotal'] = data.availableToPromiseTotal;
		   		        map['facilityId'] = data.facilityId;
	   		            map['facilityName'] = data.facilityName;
			   		    map['internalName'] = data.internalName;
				   		map['statusId'] = data.statusId;
			   		    map['statusDesc'] = data.statusDesc;
		   		        map['lotId'] = data.lotId;
	   		            map['listQuantityUoms'] = data.listQuantityUoms;
				   		map['uomId'] = newValue;
				   		listInvChanged.push(map);
			   		} 
		    	}
	    	}
    	});
	};
	var initValidateForm = function(){
		var extendRules = [
              ];
   		var mapRules = [
               ];
	};
	return {
		init: init,
	}
}());