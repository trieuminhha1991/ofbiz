$(function(){
	LabelInvTmpObj.init();
});
var LabelInvTmpObj = (function() {
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
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				var selectedIndexs = $('#jqxgridItemAndLabel').jqxGrid('getselectedrowindexes');
				var listItems = new Array();
            	var listShipments = new Array();
            	listInventorySelected = [];
	    		for(var i = 0; i < selectedIndexs.length; i++){
	    			var parentData = $('#jqxgridItemAndLabel').jqxGrid('getrowdata', selectedIndexs[i]);
	    			var strInvLabelId = parentData.inventoryItemLabelId;
	    			var arr = null;
	    			if (strInvLabelId != null){
	    				arr = strInvLabelId.split(', ');
	    			}
	    			var quantity = parentData.quantity;
	    			var listLabelIds = [];
	    			if ($('#jqxgridItemAndLabel'+ selectedIndexs[i]).length > 0){
	    				var childIndexs = $('#jqxgridItemAndLabel'+ selectedIndexs[i]).jqxGrid('getselectedrowindexes');
		    			for(var j = 0; j < childIndexs.length; j ++){
		    				var dataChild = $('#jqxgridItemAndLabel'+ selectedIndexs[i]).jqxGrid('getrowdata', childIndexs[j])
		   					listLabelIds.push(dataChild.inventoryItemLabelId);
		    			}	
	    			}
					var olb = parentData;
   					olb['quantity'] = quantity;
   					olb['inventoryItemLabelIds'] = JSON.stringify(listLabelIds);
   					olb['listCurrentLabelIds'] = JSON.stringify(arr);
   					listInventorySelected.push(olb);
	    		}
				if (listInventorySelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		finishCreatePhysicalInventory();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	function showConfirmPage(){
		var tmpSource = $("#jqxgridInventorySelected").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listInventorySelected;
			$("#jqxgridInventorySelected").jqxGrid('source', tmpSource);
		}
	}
	
	function finishCreatePhysicalInventory(){
		var listProducts = [];
		if (listInventorySelected != undefined && listInventorySelected.length > 0){
			for (var i = 0; i < listInventorySelected.length; i ++){
				var map = {};
				var data = listInventorySelected[i];
				var expTmp = new Date(data.expireDate);
				var mnfTmp = new Date(data.datetimeManufactured);
				map['productId'] = data.productId;
				map['expireDate'] = expTmp.getTime();
				map['datetimeManufactured'] = mnfTmp.getTime();
				map['lotId'] = data.lotId;
				map['statusId'] = data.statusId;
				map['inventoryItemLabelIds'] = data.inventoryItemLabelIds;
				map['facilityId'] = data.facilityId;
				map['ownerPartyId'] = data.ownerPartyId;
		   		map['quantity'] = data.quantity;
		   		map['quantityUomId'] = data.quantityUomId;
		   		map['listCurrentLabelIds'] = data.listCurrentLabelIds;
		        listProducts.push(map);
			}
		}
		var listProducts = JSON.stringify(listProducts);
		$.ajax({
			type: 'POST',
			url: 'assignLabelInventorys',
			async: false,
			data: {
				listProducts: listProducts,
			},
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(res){
				if(res._ERROR_MESSAGE_){
					if(res._ERROR_MESSAGE_ == 'QUANTITY_NOT_ENOUGH')
						jOlbUtil.alert.error(uiLabelMap.QuantityNotEnoghForAssignLabel);
					return false;
				} else {
					getInventoryItemAndLabel();
				}
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
				$("#btnPrevWizard").removeClass("disabled");
				$("#btnNextWizard").removeClass("disabled");
			},
		});
	}
	
	function getInventoryItemAndLabel(){
		window.location.href = 'getInventoryItemAndLabel';
	}
	var initValidateForm = function(){
		
	};
	var reloadPages = function(){
		window.location.reload();
	};
	return {
		init: init,
		getInventoryItemAndLabel: getInventoryItemAndLabel,
		reloadPages: reloadPages,
	}
}());