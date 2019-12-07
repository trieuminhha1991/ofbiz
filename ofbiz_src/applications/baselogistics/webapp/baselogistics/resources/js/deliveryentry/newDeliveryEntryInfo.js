$(function(){
	DlvEntryInfoObj.init();
});
var DlvEntryInfoObj = (function() {
	var validatorVAL;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		//Create fromDate
		$("#fromDateAdd").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString : 'dd/MM/yyyy HH:mm'});
		$("#fromDateAdd").jqxDateTimeInput('clear');
		
		//Create thruDate
		$("#thruDateAdd").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString : 'dd/MM/yyyy HH:mm'});
		$("#thruDateAdd").jqxDateTimeInput('clear');
		
		//Create facilityId
		$("#facilityIdAdd").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, selectedIndex: 0, theme: theme, source: faciData, valueMember:'facilityId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		//Create deliverer
		$("#delivererPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: delivererPartyData, valueMember:'partyId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		// Create vehicle type
		$("#fixedAssetId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: vehicleData, valueMember:'fixedAssetId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		// Create shipment type
//		$("#shipmentTypeId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: shipmentTypeData, valueMember:'shipmentTypeId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		$("#shipmentTypeId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: shipmentTypeDataTmp, valueMember:'shipmentTypeId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		// Create status 
		$("#statusDEId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: statusDataDE, valueMember:'statusId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		//Create driver
		$("#driverPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: driverPartyData, valueMember:'partyId', displayMember:'description', height: '24px', dropDownHeight: 200});
		
		// Create contactmech
		var contactMechData = [];
		$("#contactMechId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: contactMechData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "contactMechId"});
		
		// Create estimatedShipCost
		var carrierPartyData = [];
		$("#carrierPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: carrierPartyData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "partyId"});
		
		update({
			shipmentMethodTypeId: 'GROUND_HOME',
			}, 'getPartyCarrierByShipmentMethodAndStore' , 'listParties', 'partyId', 'fullName', 'carrierPartyId');
		
		carrierPartyData = $("#carrierPartyId").jqxDropDownList('getItems');
		
		if (departmentData.length > 0){
			for (var j = 0; j < carrierPartyData.length; j ++){
				var data = carrierPartyData[j].originalItem;
				for (var i = 0; i < departmentData.length; i ++){
					if (departmentData[i].partyId == data.partyId){
						$("#carrierPartyId").jqxDropDownList('val', departmentData[i].partyId);
						break;
					}
				}
			}
		}
		
		$("#shipCost").jqxNumberInput({ width: 295, height: 25, spinButtons: true });
        
		$("#description").jqxInput({ width: 293});
		
		checkInternalOrg();
//		$("#weightUomIdAdd").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, selectedIndex: 0, theme: theme, source: weightUomData, valueMember:'weightUomId', displayMember:'abbreviation', height: '24px', dropDownHeight: 200});
//		// Set default to Kilogram
//		for(i = 0; i < weightUomData.length; i++){
//		    if(weightUomData[i].uomId == 'WT_kg'){
//		        $("#weightUomIdAdd").jqxDropDownList('selectItem', weightUomData[i].uomId);
//		        break;
//		    }
//		}
//		
//		$("#totalWeight").text(0);
		if ($("#facilityIdAdd").val() != null && $("#facilityIdAdd").val() != undefined && $("#facilityIdAdd").val() != ""){
			update({
				facilityId: $("#facilityIdAdd").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
		}
		
		var tempFrom;
		var tempThru;
		
		if ($("#facilityIdAdd").val()){
			update({
				facilityId: $("#facilityIdAdd").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
		}
		if (curFacilityId == "" || curFacilityId == null){
	 		curFacilityId = $("#facilityIdAdd").val();
	 	}
	 	if (curFromDate == "" || curFromDate == null){
	 		tempFrom = $("#fromDateAdd").jqxDateTimeInput('getDate');
	 	}
	 	if (curThruDate == "" || curThruDate == null){
	 		tempThru = $("#thruDateAdd").jqxDateTimeInput('getDate');
	 	}
	 	
	 	var shipmentStatusId = $("#statusSMId").val();
	 	var shipmentTypeId = $("#shipmentTypeId").val();
	 	
	 	
	 	var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
	 	var curFacilityId = $("#facilityIdAdd").jqxDropDownList('val');
	 	var shipmentTypeId = $("#shipmentTypeId").jqxDropDownList('val');
	 	if (tmpS && curFacilityId && shipmentTypeId){
	 		tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&shipmentTypeId="+shipmentTypeId;
		 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
	 	}
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
//		$("#fromDateAdd").on("change", function(event){
//			if ($("#thruDateAdd").jqxDateTimeInput('getDate') != null && $("#thruDateAdd").jqxDateTimeInput('getDate') != undefined){
//		 		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
//			 	var curFacilityId = $("#facilityIdAdd").jqxDropDownList('val');
//			 	var shipmentTypeId = $("#shipmentTypeId").jqxDropDownList('val');
//				var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate').getTime();
//				var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate').getTime();
//			 	tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&fromDate="+fromDateTmp+"&thruDate="+thruDateTmp+"&shipmentTypeId="+shipmentTypeId+"&statusId="+curShipmentStatusId;
//			 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
//		 	}
//		});
//		
//		$("#thruDateAdd").on("change", function(event){
//			if ($("#thruDateAdd").jqxDateTimeInput('getDate') != null && $("#thruDateAdd").jqxDateTimeInput('getDate') != undefined){
//		 		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
//			 	var curFacilityId = $("#facilityIdAdd").jqxDropDownList('val');
//			 	var shipmentTypeId = $("#shipmentTypeId").jqxDropDownList('val');
//				var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate').getTime();
//				var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate').getTime();
//			 	tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&fromDate="+fromDateTmp+"&thruDate="+thruDateTmp+"&shipmentTypeId="+shipmentTypeId+"&statusId="+curShipmentStatusId;
//			 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
//		 	}
//		});
//		
//		$("#statusDEId").on('change', function(event){
//			changeShipmentStatus($("#statusDEId").jqxDropDownList('val'));
//			if ($("#fromDateAdd").jqxDateTimeInput('getDate') != null && $("#fromDateAdd").jqxDateTimeInput('getDate') != undefined && $("#thruDateAdd").jqxDateTimeInput('getDate') != null && $("#thruDateAdd").jqxDateTimeInput('getDate') != undefined){
//				var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
//			 	var curFacilityId = $("#facilityIdAdd").jqxDropDownList('val');
//			 	var shipmentTypeId = $("#shipmentTypeId").jqxDropDownList('val');
//			 	var tempFrom = $("#fromDateAdd").jqxDateTimeInput('getDate');
//			 	tempFrom = tempFrom.setDate(tempFrom.getDate() - 3);
//			 	var tempThru = $("#thruDateAdd").jqxDateTimeInput('getDate');
//			 	tempThru = tempThru.setDate(tempThru.getDate() + 3);
//			 	tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&fromDate="+tempFrom+"&thruDate="+tempThru+"&shipmentTypeId="+shipmentTypeId+"&statusId="+curShipmentStatusId;
//			 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
//			}
//		});
		
		$("#shipmentTypeId").on('change', function(event){
//			if ($("#fromDateAdd").jqxDateTimeInput('getDate') != null && $("#fromDateAdd").jqxDateTimeInput('getDate') != undefined && $("#thruDateAdd").jqxDateTimeInput('getDate') != null && $("#thruDateAdd").jqxDateTimeInput('getDate') != undefined){
				var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
			 	var curFacilityId = $("#facilityIdAdd").jqxDropDownList('val');
			 	var shipmentTypeId = $("#shipmentTypeId").jqxDropDownList('val');
//			 	var tempFrom = $("#fromDateAdd").jqxDateTimeInput('getDate');
//			 	tempFrom = tempFrom.setDate(tempFrom.getDate() - 3);
//			 	var tempThru = $("#thruDateAdd").jqxDateTimeInput('getDate');
//			 	tempThru = tempThru.setDate(tempThru.getDate() + 3);
			 	if (tmpS && curFacilityId && shipmentTypeId){
			 		tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&shipmentTypeId="+shipmentTypeId;
				 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
			 	}
//			}
		});
		
		$("#carrierPartyId").on('change', function(event){
			checkInternalOrg();
		});
		
//		$("#fixedAssetId").on('change', function(event){
//			var listParties = [];
//			$.ajax({
//				url: "getPartyAssignedFixedAsset",
//				type: "POST",
//				data: {
//					fixedAssetId: $("#fixedAssetId").val(),
//					roleTypeId: "LOG_DRIVER",
//				},
//				dataType: "json",
//				async: false,
//				success: function(data) {
//					listParties = data.listParties;
//				}
//			}).done(function(data) {
//			});
//			if (listParties.length > 0){
//				$("#driverPartyId").jqxDropDownList('val', listParties[0].partyId);
//			}
//		});
		
//		$("#driverPartyId").on('change', function(event){
//			var listFixedAsset = [];
//			$.ajax({
//				url: "getFixedAssetByParty",
//				type: "POST",
//				data: {
//					partyId: $("#driverPartyId").val(),
//					roleTypeId: "LOG_DRIVER",
//				},
//				dataType: "json",
//				async: false,
//				success: function(data) {
//					listFixedAsset = data.listFixedAsset;
//				}
//			}).done(function(data) {
//			});
//			if (listFixedAsset.length > 0){
//				$("#fixedAssetId").jqxDropDownList('val', listFixedAsset[0].fixedAssetId);
//			}
//		});
		
		$("#facilityIdAdd").on('change', function(event){
			if ($("#fromDateAdd").jqxDateTimeInput('getDate') != null && $("#fromDateAdd").jqxDateTimeInput('getDate') != undefined && $("#thruDateAdd").jqxDateTimeInput('getDate') != null && $("#thruDateAdd").jqxDateTimeInput('getDate') != undefined){
				var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
			 	var curFacilityId = $("#facilityIdAdd").jqxDropDownList('val');
			 	var shipmentTypeId = $("#shipmentTypeId").jqxDropDownList('val');
			 	var tempFrom = $("#fromDateAdd").jqxDateTimeInput('getDate');
			 	tempFrom = tempFrom.setDate(tempFrom.getDate() - 3);
			 	var tempThru = $("#thruDateAdd").jqxDateTimeInput('getDate');
			 	tempThru = tempThru.setDate(tempThru.getDate() + 3);
			 	tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&fromDate="+tempFrom+"&thruDate="+tempThru+"&shipmentTypeId="+shipmentTypeId+"&statusId="+curShipmentStatusId;
			 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
			}
		});
		
		$("#thruDateAdd").on('change', function(event){
			var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
		 	var curFacilityId = $("#facilityIdAdd").val();
		 	var shipmentTypeId = $("#shipmentTypeId").val();
		 	var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate');
			fromDateTmp = fromDateTmp.setDate(fromDateTmp.getDate() - 3);
			var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate');
			thruDateTmp = thruDateTmp.setDate(thruDateTmp.getDate() + 3);
			
		 	tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId="+curDeliveryEntryId+"&facilityId="+curFacilityId+"&fromDate="+fromDateTmp+"&thruDate="+thruDateTmp+"&shipmentTypeId="+shipmentTypeId+"&statusId="+curShipmentStatusId;
		 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
		});
		
		$("#facilityIdAdd").on('change', function(event){
			update({
				facilityId: $("#facilityIdAdd").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
		});
		 
//		$("#weightUomIdAdd").on('change', function(event){
//			updateTotalWeight();
//		});
		// Update total weight
		$("#jqxgridfilterGrid").on("rowselect", function(event){
			updateTotalWeight();
		});
		$("#jqxgridfilterGrid").on("rowunselect", function(event){
			updateTotalWeight();
		});
		
		//add row when the user clicks the 'Save' button.
	    $("#addSaveButton").click(function () {
	    	var row;
	    	var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
	    	if(selectedIndexs.length == 0 || selectedIndexs == undefined){
	    		bootbox.dialog(uiLabelMap.DAYouNotYetChooseProduct, [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                }]
	            );
	            return false;
	    	} else{
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	var listShipmentItems = new Array();
		            	var listShipments = new Array();
			    		for(var i = 0; i < selectedIndexs.length; i++){
			    			var childIndexs = $('#jqxgridDetail'+ selectedIndexs[i]).jqxGrid('getselectedrowindexes');
			    			for(var j = 0; j < childIndexs.length; j ++){
			    				var data = $('#jqxgridDetail'+ selectedIndexs[i]).jqxGrid('getrowdata', childIndexs[j]);
			    				var map = {};
				    			map['shipmentId'] = data.shipmentId;
				    			map['shipmentItemSeqId'] = data.shipmentItemSeqId;
				    			map['quantity'] = data.quantityCreate;
				    			map['quantityUomId'] = data.quantityUomId;
				    			listShipmentItems.push(map);
			    			}
			    		}
			    		listShipmentItems = JSON.stringify(listShipmentItems);
			            row = { 
			            		weightUomId:$('#defaultWeightUomId').val(),
			            		weight:totalWeight,
			            		facilityId:$('#facilityIdAdd').val(),
			            		contactMechId:$('#contactMechId').val(),
			            		delivererPartyId:$('#delivererPartyId').val(),
			            		driverPartyId:$('#driverPartyId').val(),
			            		fixedAssetId:$('#fixedAssetId').val(),
			            		statusId:$('#statusDEId').val(),
			            		fromDate: new Date($('#fromDateAdd').jqxDateTimeInput('getDate')),
			            		thruDate: new Date($('#thruDateAdd').jqxDateTimeInput('getDate')),
			            		listShipmentItems:listShipmentItems
			            	  };
			    	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		            }
				}]);
	    	}
	    });
	};
	
	var totalWeight = 0;
	// get Total weight
	function updateTotalWeight(){
		totalWeight = 0;
		var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
		var uomIdTo = $("#defaultWeightUomId").val();
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgridfilterGrid').jqxGrid('getrowdata', selectedIndexs[i]);
			if (data){
				var uomId = data.defaultWeightUomId;
				if (uomId == uomIdTo){
					totalWeight = totalWeight + data.totalWeight;
				} else {
					for (var j=0; j<uomConvertData.length; j++){
						if ((uomConvertData[j].uomId == uomId && uomConvertData[j].uomIdTo == uomIdTo)){
							totalWeight = totalWeight + (uomConvertData[j].conversionFactor)*(data.totalWeight);
							break;
						}
						if ((uomConvertData[j].uomId == uomIdTo && uomConvertData[j].uomIdTo == uomId)){
							totalWeight = totalWeight + (data.totalWeight)/(uomConvertData[j].conversionFactor);
							break;
						}
					}
				}
			}
		}
		$("#totalWeight").text(totalWeight.toLocaleString(localeStr));
	}
	
	function deleteDeliveryEntry(deliveryEntryId){
		$.ajax({
			url: "deleteDeliveryEntry",
			type: "POST",
			data: {deliveryEntryId: deliveryEntryId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$("#jqxgrid").jqxGrid('updatebounddata');
			$('#jqxgrid').jqxGrid('clearselection');
		});
	}
	
	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#thruDateAdd', 
			    message: uiLabelMap.EndDateMustBeAfterStartDate, 
			    action: 'keyup, blur', 
			    position: 'topcenter',
			    rule: function (input) {	
			    	var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate');
			    	var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate');
				   	if ((typeof(fromDateTmp) != 'undefined' && fromDateTmp != null && !(/^\s*$/.test(fromDateTmp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
			 		    if (fromDateTmp > thruDateTmp) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
//			{
//				input: '#thruDateAdd', 
//			    message: uiLabelMap.CannotBeforeNow, 
//			    action: 'keyup, blur', 
//			    position: 'topcenter',
//			    rule: function (input) {	
//			    	var temp = new Date();
//			    	var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate');
//				   	if ((typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
//			 		    if (thruDateTmp < temp && curShipmentStatusId != 'SHIPMENT_DELIVERED') {
//			 		    	return false;
//			 		    }
//				   	}
//				   	return true;
//			    }
//			},
//			{
//				input: '#thruDateAdd', 
//			    message: uiLabelMap.CannotAfterNow, 
//			    action: 'keyup, blur', 
//			    position: 'topcenter',
//			    rule: function (input) {	
//			    	var temp = new Date();
//			    	var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate');
//				   	if ((typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
//			 		    if (thruDateTmp > temp && curShipmentStatusId == 'SHIPMENT_DELIVERED') {
//			 		    	return false;
//			 		    }
//				   	}
//				   	return true;
//			    }
//			},
//			{
//				input: '#fromDateAdd', 
//			    message: uiLabelMap.StartDateMustBeAfterNow, 
//			    action: 'keyup, blur', 
//			    position: 'topcenter',
//			    rule: function (input) {	
//			    	var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate');
//			    	var temp = new Date();
//				   	if ((typeof(fromDateTmp) != 'undefined' && fromDateTmp != null && !(/^\s*$/.test(fromDateTmp))) && (typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp)))) {
//			 		    if (fromDateTmp < temp && curShipmentStatusId == 'SHIPMENT_INPUT') {
//			 		    	return false;
//			 		    }
//				   	}
//				   	return true;
//			    }
//			},
//			{
//				input: '#fromDateAdd', 
//			    message: uiLabelMap.CannotAfterNow, 
//			    action: 'keyup, blur', 
//			    position: 'topcenter',
//			    rule: function (input) {	
//			    	var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate');
//			    	var temp = new Date();
//				   	if ((typeof(fromDateTmp) != 'undefined' && fromDateTmp != null && !(/^\s*$/.test(fromDateTmp))) && (typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp)))) {
//			 		    if (fromDateTmp > temp && curShipmentStatusId != 'SHIPMENT_INPUT' && curShipmentStatusId != 'SHIPMENT_SCHEDULED') {
//			 		    	return false;
//			 		    }
//				   	}
//				   	return true;
//			    }
//			},
//			{
//				input: '#fromDateAdd', 
//			    message: uiLabelMap.StartDateMustBeAfterEndDate, 
//			    action: 'keyup, blur', 
//			    position: 'topcenter',
//			    rule: function (input) {	
//			    	var fromDateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate');
//			    	var thruDateTmp = $('#thruDateAdd').jqxDateTimeInput('getDate');
//				   	if ((typeof(fromDateTmp) != 'undefined' && fromDateTmp != null && !(/^\s*$/.test(fromDateTmp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
//			 		    if (fromDateTmp > thruDateTmp) {
//			 		    	return false;
//			 		    }
//				   	}
//				   	return true;
//			    }
//			},
          ];
   		var mapRules = [
				{input: '#facilityIdAdd', type: 'validInputNotNull'},
   				{input: '#contactMechId', type: 'validInputNotNull'},
   				{input: '#carrierPartyId', type: 'validInputNotNull'},
   				{input: '#shipmentTypeId', type: 'validInputNotNull'},
   				{input: '#fromDateAdd', type: 'validInputNotNull'},
   				{input: '#thruDateAdd', type: 'validInputNotNull'},
//   				{input: '#delivererPartyId', type: 'validInputNotNull'},
   				
               ];
   		validatorVAL = new OlbValidator($('#initDeliveryEntry'), mapRules, extendRules, {position: 'right'});
	};
	function changeShipmentStatus(deStatusId){
		switch(deStatusId) {
	    case 'DELI_ENTRY_CREATED':
	    	curShipmentStatusId = 'SHIPMENT_INPUT';
	        break;
	    case 'DELI_ENTRY_SCHEDULED':
	    	curShipmentStatusId = 'SHIPMENT_INPUT';
	        break;
	    case 'DELI_ENTRY_SHIPPING':
	    	curShipmentStatusId = 'SHIPMENT_SHIPPED';
	    	break;
	    case 'DELI_ENTRY_DELIVERED':
	    	curShipmentStatusId = 'SHIPMENT_DELIVERED';
	    	break;
	    case 'DELI_ENTRY_COMPLETED':
    		curShipmentStatusId = 'SHIPMENT_DELIVERED';
    		break;
	    case 'DELI_ENTRY_CANCELLED':
	    	curShipmentStatusId = 'SHIPMENT_CANCELLED';
	    	break;
	    default:
	    	break;
		}
	}
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
	
	function checkInternalOrg(){
		var isChild = false;
		$.ajax({
			url: "checkInternalParty",
			type: "POST",
			data: {
				partyId: $("#carrierPartyId").val(),
				ancestorId: company,
			},
			dataType: "json",
			async: false,
			success: function(data) {
				isChild = data.isChild;
			}
		}).done(function(data) {
			if (isChild == false){
				$("#driverPartyId").jqxDropDownList({disabled: true});
				$("#delivererPartyId").jqxDropDownList({disabled: true});
				$("#fixedAssetId").jqxDropDownList({disabled: true});
			} else {
				$("#driverPartyId").jqxDropDownList({disabled: false});
				$("#delivererPartyId").jqxDropDownList({disabled: false});
				$("#fixedAssetId").jqxDropDownList({disabled: false});
			}
		});
	}
	
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    var getValidator = function(){
    	return validatorVAL;
    }
	return {
		init: init,
		getShipmentUrl: getShipmentUrl,
		getValidator: getValidator,
	}
}());