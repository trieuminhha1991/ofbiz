$(function(){
	PhysicalInvTemplateObj.init();
});
var PhysicalInvTemplateObj = (function() {
	var init = function() {
		listInvToUpdates = [];
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
				var resultValidate = !PhysicalInvInfoObj.getValidator().validate();
				if(resultValidate) return false;
				
	    		listProductUpdates = [];
	    		showListInventoryToUpdate(listInvToUpdates);
			} else if(info.step == 2 && (info.direction == "next")) {
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
		
		$("#jqxgridInventoryItemUpdate").on('cellendedit', function (event) {
		    // event arguments.
		    var args = event.args;
		    // column data field.
		    var dataField = event.args.datafield;
		    // row's bound index.
		    var rowBoundIndex = event.args.rowindex;
		    // cell value
		    var value = args.value;
		    if (value == "") {
		    	value = null;
		    }
		    // cell old value.
		    var oldvalue = args.oldvalue;
		    // row's data.
		    var rowData = args.row;
		    
		    var id = rowData.uid;
		    var check = true;
		    if (oldvalue != value){
		    	if (rowData.quantity > 0){
		    		if (dataField == "lotId"){
			    		if (rowData.initExpireDate === rowData.expireDate && rowData.initDatetimeManufactured === rowData.datetimeManufactured && rowData.initLotId === value && rowData.initStatusId === rowData.statusId){
				    		check = false;
				    	}
			    	} else if (dataField == "expireDate"){
			    		if (rowData.initExpireDate === value && rowData.initDatetimeManufactured === rowData.datetimeManufactured && rowData.initLotId === rowData.lotId && rowData.initStatusId === rowData.statusId){
				    		check = false;
				    	}
			    	} else if (dataField == "datetimeManufactured"){
			    		if (rowData.initExpireDate === rowData.expireDate && rowData.initDatetimeManufactured === value && rowData.initLotId === value && rowData.initStatusId === rowData.statusId){
				    		check = false;
				    	}
			    	} else if (dataField == "statusId"){
			    		if (rowData.initExpireDate === rowData.expireDate && rowData.initDatetimeManufactured === rowData.datetimeManufactured && rowData.initLotId === value && rowData.initStatusId === value){
				    		check = false;
				    	}
			    	} else if (dataField == "quantity"){
		    			if (rowData.initExpireDate === rowData.expireDate && rowData.initDatetimeManufactured === rowData.datetimeManufactured && rowData.initLotId === rowData.lotId && rowData.initStatusId === rowData.statusId){
				    		check = false;
				    	} else if (value == 0 || value === null || value === undefined || value === ''){
				    		check = false;
				    	}
		    		} else {
		    			check = false;
		    		}
		    	} else {
		    		if (dataField == "quantity" && value > 0 && value != null){
		    			if (rowData.initExpireDate === rowData.expireDate && rowData.initDatetimeManufactured === rowData.datetimeManufactured && rowData.initLotId === rowData.lotId && rowData.initStatusId === rowData.statusId){
				    		check = false;
				    	}
		    		} else {
		    			check = false;
		    		}
		    	}
		    } else {
		    	check = false;
		    }
		    if (check){
		    	for(var i = 0; i < listProductUpdates.length; i++) {
		    	    var objTmp = listProductUpdates[i];
		    	    if(objTmp.uid == id) {
			    		listProductUpdates.splice(i, 1);
		    	    	break;
		    	    }
		    	}
		    	var obj = jQuery.extend({}, rowData);
		    	if (dataField == "lotId"){
		    		obj['lotId'] = value;
	    		} else if (dataField == "expireDate"){
	    			obj['expireDate'] = value;
	    		} else if (dataField == "datetimeManufactured"){
	    			obj['datetimeManufactured'] = value;
	    		} else if (dataField == "statusId"){
	    			obj['statusId'] = value;
	    		} if (dataField == "quantity"){
	    			obj['quantity'] = value;
	    		}
	    		obj['oldExpireDate'] = rowData.initExpireDate;
	    		obj['oldDatetimeManufactured'] = rowData.initDatetimeManufactured;
	    		obj['oldLotId'] = rowData.initLotId;
	    		obj['oldStatusId'] = rowData.initStatusId;
	    		listProductUpdates.push(obj);
		    	$('#jqxgridInventoryItemUpdate').jqxGrid('selectrow', rowBoundIndex);
		    } else {
		    	for(var i = 0; i < listProductUpdates.length; i++) {
		    	    var obj = listProductUpdates[i];
		    	    if(obj.uid == id) {
		    	    	listProductUpdates.splice(i, 1);
		    	    }
		    	}
		    	$('#jqxgridInventoryItemUpdate').jqxGrid('unselectrow', rowBoundIndex);
		    }
		});
	};
	
	function showListInventoryToUpdate(data){
		var tmpSource = $("#jqxgridInventoryItemUpdate").jqxGrid('source');
		var tmpSource = $("#jqxgridInventoryItemUpdate").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			listProductInitUpdates = [];
			var facilityId = $("#facilityId").jqxDropDownList('val');
			tmpSource._source.url = "jqxGeneralServicer?sname=jqGetInventoryItemPhysicalDetail&facilityId=" + facilityId;
		    $("#jqxgridInventoryItemUpdate").jqxGrid('source', tmpSource);
		}
	}
		
	function showConfirmPage(){
		for (var i = 0; i < facilityData.length; i ++){
			if (facilityData[i].facilityId == $('#facilityId').val()){
				$('#facilityIdDT').text(facilityData[i].description);
			}
		}
		
		if ($("#physicalInventoryDate") != undefined && $("#physicalInventoryDate").val() != '' && $("#physicalInventoryDate") != null) {
			$("#physicalInventoryDateDT").text(DatetimeUtilObj.formatFullDate($("#physicalInventoryDate").jqxDateTimeInput('getDate')));
		}
		
		if ($("#generalComments") != undefined && $("#generalComments").val() != '' && $("#generalComments") != null) {
			$("#generalCommentsDT").text($("#generalComments").val());
		}
		
		$("#partyIdDT").text('_NA_');
		var party = $('#partyId').jqxDropDownList('val');
		for(var i = 0; i < partyData.length; i ++){
			if (partyData[i].partyId == party){
				$("#partyIdDT").text(partyData[i].description);
				break;
			}
		}
		
		var tmpSource = $("#jqxgridInventorySelected").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listInventorySelected;
			$("#jqxgridInventorySelected").jqxGrid('source', tmpSource);
		}
		
		var tmpSource = $("#jqxgridInvGroupByProduct").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listProductSelected;
			$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpSource);
		}
		
		var tmpSource = $("#jqxgridProductToUpdate").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listProductUpdates;
			$("#jqxgridProductToUpdate").jqxGrid('source', tmpSource);
		}
	}
	
	function finishCreatePhysicalInventory(){
		var listProducts = [];
		if (listInventorySelected != undefined && listInventorySelected.length > 0){
			for (var i = 0; i < listInventorySelected.length; i ++){
				var map = {};
				var data = listInventorySelected[i];
				var exp = null;
				var mnf = null;
				if (data.expireDate){
					var expTmp = new Date(data.expireDate);
					exp = expTmp.getTime();
				}
				if (data.datetimeManufactured){
					var mnfTmp = new Date(data.datetimeManufactured);
					mnf = mnfTmp.getTime();
				}
				
				map['productId'] = data.productId;
				map['expireDate'] = exp;
				map['datetimeManufactured'] = mnf;
				map['lotId'] = data.lotId;
				map['statusId'] = data.statusId;
				map['facilityId'] = data.facilityId;
				map['ownerPartyId'] = data.ownerPartyId;
		   		map['quantityOnHandVar'] = data.quantityOnHandVar;
		   		map['varianceReasonId'] = data.varianceReasonId;
		   		map['quantityUomId'] = data.quantityUomId;
		   		map['comments'] = data.comments;
		        listProducts.push(map);
			}
		}
		var physicalInventoryDate = null;
		if ($("#physicalInventoryDate").val()){
			physicalInventoryDate = $("#physicalInventoryDate").jqxDateTimeInput('getDate').getTime();
		}
		var listProductInventoryCounts = createPhysicalInventoryCount();
		var listProductUpdates = updateInventoryItem();
		var physicalInventoryId = null;
		if (listProducts.length > 0) {
			listProducts = JSON.stringify(listProducts);
		} else {
			listProducts = null;
		}
		$.ajax({
			type: 'POST',
			url: 'createPhysicalInventoryAll',
			async: false,
			data: {
				facilityId: $("#facilityId").val(),
				partyId: $("#partyId").val(),
				generalComments: $("#generalComments").val().split('\n').join(' '),
				physicalInventoryDate: physicalInventoryDate,
				listProductVariances: listProducts,
				listProductCounts: listProductInventoryCounts,
				listProductUpdates: listProductUpdates,
			},
			beforeSend: function(){
				$("#btnPrevWizard").addClass("disabled");
				$("#btnNextWizard").addClass("disabled");
				$("#loader_page_common").show();
			},
			success: function(res){
				if(res._ERROR_MESSAGE_){
					if(res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_.indexOf("QUANTITY_NOT_ENOUGH_TO_UPDATE") > 0){
						jOlbUtil.alert.error(uiLabelMap.QuantityNotEnoghForUpdate);
						return false;
					}
					if(res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_.indexOf("QUANTITY_NOT_ENOUGH") > 0){
						jOlbUtil.alert.error(uiLabelMap.QuantityNotEnoghForLost);
						return false;
					}
				} else {
					var physicalInventoryId = res.physicalInventoryId;
					viewPhysicalInventoryDetail(physicalInventoryId);
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
	
	function updateInventoryItem (physicalInventoryId){
		var listProducts = [];
		if (listProductUpdates != undefined && listProductUpdates.length > 0){
			for (var i = 0; i < listProductUpdates.length; i ++){
				var map = {};
				var data = listProductUpdates[i];
				var exp = null;
				var mnf = null;
				if (data.expireDate){
					var expTmp = new Date(data.expireDate);
					exp = expTmp.getTime();
				}
				if (data.datetimeManufactured){
					var mnfTmp = new Date(data.datetimeManufactured);
					mnf = mnfTmp.getTime();
				}
				
				map['productId'] = data.productId;
				map['expireDate'] = exp;
				map['datetimeManufactured'] = mnf;
				map['lotId'] = data.lotId;
				if ('good' === data.statusId){
					map['statusId'] = null;
				} else {
					map['statusId'] = data.statusId;
				}
				map['facilityId'] = data.facilityId;
				map['requireAmount'] = data.requireAmount;
				map['ownerPartyId'] = data.ownerPartyId;
		   		map['quantityOnHandTotal'] = data.quantityOnHandTotal;
		   		map['quantity'] = data.quantity;
		   		
		   		var oldExp = null;
				var oldMnf = null;
				if (data.oldExpireDate){
					var expTmp2 = new Date(data.oldExpireDate);
					oldExp = expTmp2.getTime();
				}
				if (data.oldDatetimeManufactured){
					var mnfTmp2 = new Date(data.oldDatetimeManufactured);
					oldMnf = mnfTmp2.getTime();
				}
				
		   		map['oldExpireDate'] = oldExp;
				map['oldDatetimeManufactured'] = oldMnf;
				map['oldLotId'] = data.oldLotId;
		   		
				if ('good' === data.oldStatusId){
					map['oldStatusId'] = null;
				} else {
					map['oldStatusId'] = data.oldStatusId;
				}
				
		        listProducts.push(map);
			}
		}
		if (listProducts.length > 0){
			listProducts = JSON.stringify(listProducts);
		} else {
			listProducts = null;
		}
		return listProducts;
	}
	
	function createPhysicalInventoryCount (physicalInventoryId){
		var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			for (var i = 0; i < listProductSelected.length; i ++){
				var map = {};
				var data = listProductSelected[i];
				var exp = null;
				var mnf = null;
				if (data.expireDate){
					var expTmp = new Date(data.expireDate);
					exp = expTmp.getTime();
				}
				if (data.datetimeManufactured){
					mnfTmp = new Date(data.datetimeManufactured);
					mnf = mnfTmp.getTime();
				}
				
				map['productId'] = data.productId;
				map['expireDate'] = exp;
				map['datetimeManufactured'] = mnf;
				map['lotId'] = data.lotId;
				map['statusId'] = data.statusId;
				map['facilityId'] = data.facilityId;
				map['ownerPartyId'] = data.ownerPartyId;
		   		map['quantityOnHandTotal'] = data.quantityOnHandTotal;
		   		map['quantityUomId'] = data.quantityUomId;
		   		map['comments'] = data.comments;
		        listProducts.push(map);
			}
		}
		if (listProducts.length > 0){
			listProducts = JSON.stringify(listProducts);
		} else {
			listProducts = null;
		}
		return listProducts;
	}
	
	function viewPhysicalInventoryDetail(physicalInventoryId){
		window.location.href = 'viewDetailPhysicalInventory?physicalInventoryId=' + physicalInventoryId;
	}
	var initValidateForm = function(){
		
	};
	var reloadPages = function(){
		window.location.reload();
	};
	
	return {
		init: init,
		viewPhysicalInventoryDetail: viewPhysicalInventoryDetail,
		reloadPages: reloadPages,
	}
}());