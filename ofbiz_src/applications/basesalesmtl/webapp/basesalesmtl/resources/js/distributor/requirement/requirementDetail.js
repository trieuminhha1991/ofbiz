$(function(){
	ReqDetailObj.init();
});
var ReqDetailObj = (function(){
	var reqDetailVLD;
	var gridReqItemObj;
	var noteValidate;
	var btnClick = false;
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		/*
		if (noteData.length <= 0 && $("#noteContent").length > 0) {
			$("#noteContent").hide();
		} else {
			if ($("#noteContent").length > 0){
				$("#noteContent").show();
				$('#listNote').html("");
				for (var i = 0; i < noteData.length; i ++){
					$('#listNote').append("["+DatetimeUtilObj.formatFullDate(noteData[i].noteDateTime) +"] - " + noteData[i].noteInfo + "</br>");
				}
			}
		}
		if ($("#note").length > 0){
			$("#note").jqxInput({ width: 300, height: 100});
		}
		if ($("#noteRequirement").length > 0){
			$("#noteRequirement").jqxWindow({
				maxWidth: 800, minWidth: 300, width: 500, height: 230, minHeight: 100, maxHeight: 800, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#noteCancel"), modalOpacity: 0.7, theme:theme
			});
		}
		*/

		if (requirement.requirementTypeId == "TRANSFER_REQUIREMENT"){
			var originContactData = [];
			var destContactData = [];
			$('#transferTypeId').jqxDropDownList({disabled: true, placeHolder: uiLabelMap.PleaseSelectTitle, source: transferTypeData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'transferTypeId'});
			$('#transferShipmentMethodTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: shipmentMethodData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'shipmentMethodTypeId'});
			var partyTmpData = [];
			$('#carrierPartyId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: partyTmpData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'partyId'});
			$('#needsReservesInventory').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: yesNoData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'value'});
			$('#maySplit').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: yesNoData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'value'});
			$('#transferOriginContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: originContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
			$('#transferDestContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
			$("#shipBeforeDate").jqxDateTimeInput({width: '103%', formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
			$("#shipAfterDate").jqxDateTimeInput({width: '103%', formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
			$("#shipBeforeDate").jqxDateTimeInput('clear');
			$("#shipAfterDate").jqxDateTimeInput('clear');
			$("#transferDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
			$("#transferDate").jqxDateTimeInput('val', requirement.requirementStartDate);
			
			$("#priority").jqxNumberInput({ width:200, height: 25, min: 0,  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
			$("#priority").val(0);
			
			if ($("#transferShipmentMethodTypeId").length > 0){
				$("#transferShipmentMethodTypeId").val("GROUND_HOME");
				update({
					shipmentMethodTypeId: $("#transferShipmentMethodTypeId").val(),
					}, 'getPartyCarrierByShipmentMethodAndStore' , 'listParties', 'partyId', 'fullName', 'carrierPartyId');
			}
		}
	};
	var initElementComplex = function (){
		initGridRequirementItem($("#listRequirementItem"));
		initGridRequirementItemPopup($("#listRequirementItemPopup"));
		getDetailRequirement(requirementId);
	};
	var initEvents = function (){
		$("#createTransferWindow").on('open', function(event){
			var listReqItemTmps = [];
			$.ajax({
				type: 'POST',
				url: 'getRequirementItemToTransfer',
				async: false,
				data: {
					requirementId: requirementId,
					getAllStatus: false,
				},
				success: function(data){
					listReqItemTmps = data.listRequirementItems;
				},
			});
			loadRequirementItemPopup(listReqItemTmps);
		});
		
		$("#selectFacilityWindow").on('open', function(event){
			selectFacilityClick = 0;
			update({
				facilityId: $("#requiredFacilityId").val(),
				contactMechPurposeTypeId: purposeContactMechId,
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'requiredContactMechId');
		});
		
		$("#requiredFacilityId").on('change', function(event){
			update({
				facilityId: $("#requiredFacilityId").val(),
				contactMechPurposeTypeId: purposeContactMechId,
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'requiredContactMechId');
		});
		
		$('#selectFacilityTransferSave').click(function(){
			var resultValidate = !reqDetailVLD.validate();
			if(resultValidate) return false;
			var selectedIndexs = $('#listRequirementItemPopup').jqxGrid('getselectedrowindexes');
			if(selectedIndexs.length == 0){
			    jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			    return false;
			} 
			var listProducts = [];
    		for(var i = 0; i < selectedIndexs.length; i++){
    			var data = $('#listRequirementItemPopup').jqxGrid('getrowdata', selectedIndexs[i]);
    			var map = {};
    			map['productId'] = data.productId;
    			map['quantity'] = data.quantityCreate;
    			map['quantityUomId'] = data.quantityUomId; 
    			map['weightUomId'] = data.weightUomId; 
    			var exp = data.expireDate;
    			if (exp){
    				map['expiredDate'] = exp.getTime();
    			}
    			map['requirementId'] = data.requirementId; 
    			map['reqItemSeqId'] = data.reqItemSeqId; 
    			listProducts.push(map);
    		}
			var shipBeforeDateTmp = null;
			var shipAfterDateTmp = null;
			var transferDateTmp =null;
			if ($("#shipBeforeDate").val()){
				shipBeforeDateTmp = $("#shipBeforeDate").jqxDateTimeInput('getDate').getTime();
			}
			if ($("#shipAfterDate").val()){
				shipAfterDateTmp = $("#shipAfterDate").jqxDateTimeInput('getDate').getTime();
			}
			if ($("#transferDate").val()){
				transferDateTmp = $("#transferDate").jqxDateTimeInput('getDate').getTime();
			}
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll(); btnClick = false;}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	if (!btnClick){
			    		Loading.show('loadingMacro');
		            	setTimeout(function(){
		            		listProducts = JSON.stringify(listProducts);
		            		var inputData = {
	            				originFacilityId: $("#transferOriginFacilityId").val(),
	            				requirementId: requirementId,
	            				destFacilityId: $("#transferDestFacilityId").val(),
	            				originContactMechId: $("#transferOriginContactMechId").val(),
	            				destContactMechId: $("#transferDestContactMechId").val(),
	            				transferTypeId: $("#transferTypeId").val(),
	            				shipmentMethodTypeId: $("#transferShipmentMethodTypeId").val(),
	            				carrierPartyId: $("#carrierPartyId").val(),
	            				shipBeforeDate: shipBeforeDateTmp,
	            				shipAfterDate: shipAfterDateTmp,
	            				transferDate: transferDateTmp,
	            				needsReservesInventory: $("#needsReservesInventory").val(),
	            				maySplit: $("#maySplit").val(),
	            				priority: $("#priority").val(),
	            				statusId: "TRANSFER_APPROVED",
	            				itemStatusId: "TRANS_ITEM_APPROVED",
	            				listProducts: listProducts,
	            			}
		            		$.ajax({
		            			type: 'POST',
		            			url: 'createTransfer',
		            			async: false,
		            			data: inputData,
		            			success: function(data){
		            				$("#jqxgridTransfer").jqxGrid('updatebounddata');
		            				$("#createTransferWindow").jqxWindow('close');
		            			},
		            		});
						Loading.hide('loadingMacro');
		            	}, 400);
			    		btnClick = true;
			    	}
			    }
			}]);
		});
		
		$('#noteSave').click(function(){
			var resultValidate = !noteValidate.validate();
			if(resultValidate) return false;
			var note = $("#note").val().split('\n').join(' ');
			rejectRequirement(requirementId, note);
		});
		
		$('#noteRequirement').on('close', function(){
			noteValidate.hide();
		});
	};
	var initValidateForm = function (){
		if ($('#noteRequirement').length > 0){
			var mapNoteRules = [
                {input: '#note', type: 'validInputNotNull'},
    		];
    		noteValidate = new OlbValidator($('#noteRequirement'), mapNoteRules, null, {position: 'right'});
		}
		
		if ($('#createTransferWindow').length > 0){
			var extendTransferRules = [
				{input: '#transferDate', message: uiLabelMap.PleaseChooseTransferDateOrShipBeforeAndAfter, action: 'valueChanged', 
					rule: function(input, commit){
						var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
						var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
						var transferDate = $('#transferDate').jqxDateTimeInput('getDate');
						if (!transferDate){
							if ((!shipAfterDate && !shipBeforeDate)) {
						   		return false;
						   	} else {
						   		return true;
						   	}
						}
						return true;
					}
				},
				{input: '#shipAfterDate, #shipBeforeDate', message: uiLabelMap.CannotBeforeNow, action: 'valueChanged', 
					rule: function(input, commit){
						var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
						var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
		     		   	var nowDate = new Date();
					   	if ((typeof(shipAfterDate) != 'undefined' && shipAfterDate != null && !(/^\s*$/.test(shipAfterDate)))) {
				 		    if (shipAfterDate < nowDate) {
				 		    	return false;
				 		    } 
					 		if ((typeof(shipBeforeDate) != 'undefined' && shipBeforeDate != null && !(/^\s*$/.test(shipBeforeDate)))) {
					 		if (shipBeforeDate < nowDate) {
					 		    	return false;
					 		    } 
					 		}
				 		    return true;
					   	}
					   	return true;
					}
				},
				{input: '#shipAfterDate', message: uiLabelMap.CanNotAftershipBeforeDate, action: 'valueChanged', 
					rule: function(input, commit){
						var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
						var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
					   	if ((typeof(shipBeforeDate) != 'undefined' && shipBeforeDate != null && !(/^\s*$/.test(shipBeforeDate))) && (typeof(shipAfterDate) != 'undefined' && shipAfterDate != null && !(/^\s*$/.test(shipAfterDate)))) {
				 		    if (shipAfterDate > shipBeforeDate) {
				 		    	return false;
				 		    }
				 		    return true;
					   	}
					   	return true;
					}
				},
				{input: '#shipBeforeDate', message: uiLabelMap.CanNotBeforeshipAfterDate, action: 'valueChanged', 
					rule: function(input, commit){
						var shipAfterDate = $('#shipAfterDate').jqxDateTimeInput('getDate');
						var shipBeforeDate = $('#shipBeforeDate').jqxDateTimeInput('getDate');
					   	if ((typeof(shipBeforeDate) != 'undefined' && shipBeforeDate != null && !(/^\s*$/.test(shipBeforeDate))) && (typeof(shipAfterDate) != 'undefined' && shipAfterDate != null && !(/^\s*$/.test(shipAfterDate)))) {
				 		    if (shipAfterDate > shipBeforeDate) {
				 		    	return false;
				 		    }
				 		    return true;
					   	}
					   	return true;
					}
				},
			];
			var mapTransferRules = [
	            {input: '#transferOriginFacilityId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#transferDestFacilityId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#transferOriginContactMechId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#transferDestContactMechId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#carrierPartyId',type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#transferShipmentMethodTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
				{input: '#transferTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
			];
			reqDetailVLD = new OlbValidator($('#createTransferWindow'), mapTransferRules, extendTransferRules, {position: 'topcenter'});
		}
	};
	var getDetailRequirement = function(requirementId){
		var budgetEstimated = requirement.estimatedBudget;
		if (requirement.facilityId != null && requirement.facilityId != undefined && requirement.facilityId != ''){
			if (originFacility.facilityCode){
				$("#facilityIdDT").text("[" + originFacility.facilityCode + "] " + originFacility.facilityName);
			} else {
				$("#facilityIdDT").text("[" + originFacility.facilityId + "] " + originFacility.facilityName);
			}
		} else {
			$("#facilityIdDT").hide();
			if ($("#facilityId").length > 0){
				$("#facilityId").show();
			}
		}
		if (requirement.destFacilityId && destFacility){
			if (destFacility.facilityCode){
				$("#destFacilityIdDT").text("[" + destFacility.facilityCode + "] " + destFacility.facilityName);
			} else {
				$("#destFacilityIdDT").text("[" + destFacility.facilityId + "] " + destFacility.facilityName);
			}
		}
		$("#requirementStartDate").text(DatetimeUtilObj.formatToMinutes(new Date(requirement.requirementStartDate)));
		$("#requiredByDate").text(DatetimeUtilObj.formatFullDate(new Date(requirement.requiredByDate)));
		
		for (var i = 0; i < reqStatusData.length; i ++){
			if (reqStatusData[i].statusId == requirement.statusId){
				$("#reqStatusId").text(reqStatusData[i].description);
			}
		}
		if (requirement.reasonEnumId != null && requirement.reasonEnumId != undefined && requirement.reasonEnumId != ''){
			for (var i=0; i< reasonEnumData.length; i ++){
				if (reasonEnumData[i].enumId == requirement.reasonEnumId){
					$("#reasonEnumId").text(reasonEnumData[i].description);
				}
			}
		} else {
			$('#reasonEnumId').text('');
		}
		if (requirement.requirementTypeId != null && requirement.requirementTypeId != undefined && requirement.requirementTypeId != ''){
			for (var i=0; i< requirementTypeData.length; i ++){
				if (requirementTypeData[i].requirementTypeId == requirement.requirementTypeId){
					$("#requirementTypeId").text(requirementTypeData[i].description);
				}
			}
		} else {
			$('#requirementTypeId').text('');
		}
		if (requirement.description != null && requirement.description != undefined && requirement.description != ''){
			$('#description').text(requirement.description);
		}
		
		if ($("#listRequirementItem").length > 0){
			var listReqItemTmps = [];
			$.ajax({
				type: 'POST',
				url: 'getRequirementItems',
				async: false,
				data: {
					requirementId: requirementId,
				},
				success: function(data){
					listReqItemTmps = data.listRequirementItems;
				},
			});
			for (var i in  listReqItemTmps){
				var data = listReqItemTmps[i];
				
				if (listReqItemTmps[i]['expireDate'] != null && listReqItemTmps[i]['expireDate'] != undefined){
					listReqItemTmps[i]['expireDate'] = listReqItemTmps[i]['expireDate'].time;
				}
				if (listReqItemTmps[i]['datetimeManufactured'] != null && listReqItemTmps[i]['datetimeManufactured'] != undefined){
					listReqItemTmps[i]['datetimeManufactured'] = listReqItemTmps[i]['datetimeManufactured'].time;
				}
				if (listReqItemTmps[i]['fromExpiredDate'] != null && listReqItemTmps[i]['fromExpiredDate'] != undefined){
					listReqItemTmps[i]['fromExpiredDate'] = listReqItemTmps[i]['fromExpiredDate'].time;
				}
				if (listReqItemTmps[i]['toExpiredDate'] != null && listReqItemTmps[i]['toExpiredDate'] != undefined){
					listReqItemTmps[i]['toExpiredDate'] = listReqItemTmps[i]['toExpiredDate'].time;
				}
			}
			loadRequirementItem(listReqItemTmps);
		}
	};
	var changeRequirementStatus = function changeRequirementStatus(requirementId){
		bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
            		Loading.show('loadingMacro');
                	setTimeout(function(){
    					$.ajax({
    						type: 'POST',
    						url: 'changeRequirementStatus',
    						async: false,
    						data: {
    							requirementId: requirementId,
    							statusId: "REQ_APPROVED",
    						},
    						success: function(data){
    							location.reload();
    						},
    					});
    					Loading.hide('loadingMacro');
                	}, 300);
            		btnClick = true;
            	}
            }
		}]);
	}
	
	var prepareRejectRequirement = function (){
		$("#noteRequirement").jqxWindow("open");
	};
	
	var rejectRequirement = function rejectRequirement(requirementId, note){
		bootbox.dialog(uiLabelMap.AreYouSureReject, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
            		Loading.show('loadingMacro');
                	setTimeout(function(){
    					$.ajax({
    						type: 'POST',
    						url: 'changeRequirementStatus',
    						async: false,
    						data: {
    							requirementId: requirementId,
    							noteInfo: note,
    							statusId: "REQ_REJECTED",
    						},
    						success: function(data){
    							location.reload();
    						},
    					});
    					Loading.hide('loadingMacro');
                	}, 300);
                	btnClick = true;
            	}
            }
		}]);
	};
	
	var quickShipRequirement = function quickShipRequirement(requirementId){
		bootbox.dialog(uiLabelMap.AreYouSureExport, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
            		Loading.show('loadingMacro');
                	setTimeout(function(){
    					$.ajax({
    						type: 'POST',
    						url: 'changeRequirementStatus',
    						async: false,
    						data: {
    							requirementId: requirementId,
    							facilityId: $("#facilityId").val(),
    							statusId: "REQ_COMPLETED",
    						},
    						success: function(data){
    							location.reload();
    						},
    					});
    					Loading.hide('loadingMacro');
                	}, 500);
                	btnClick = true;
            	}
            }
		}]);
	}
	
	var prepareInventoryToChange = function prepareInventoryToChange(requirementId){
		window.location.replace("prepareInventoryToChangePurpose?requirementId="+requirementId);
	} 
	
	var prepareShipmentFromRequirement = function prepareShipmentFromRequirement(requirementId){
		var facilityId = $("#facilityId").val();
		if (requirement.requirementTypeId == "EXPORT_REQUIREMENT"){
			window.location.replace("prepareExportProductFromRequirement?requirementId="+requirementId);
		} else if (requirement.requirementTypeId == "RECEIVE_REQUIREMENT"){
			window.location.replace("prepareReceiveProductFromRequirement?requirementId="+requirementId);
		} else {
			// another type
		}
	}
	
	var sendRequirementNotify = function sendRequirementNotify(requirementId){
		bootbox.dialog(uiLabelMap.AreYouSureSend, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
            		Loading.show('loadingMacro');
                	setTimeout(function(){
						$.ajax({
							type: 'POST',
							url: 'changeRequirementStatus',
							async: false,
							data: {
								requirementId: requirementId,
								statusId: "REQ_PROPOSED",
							},
							success: function(data){
							},
						}).done(function(data) {
							location.reload();
						});
    					Loading.hide('loadingMacro');
                	}, 300);
            	}
            	btnClick = true;
            }
		}]);
	};
	
	var quickReceiveInventory = function quickReceiveInventory (requirementId){
		bootbox.dialog(uiLabelMap.AreYouSureExchange, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
            		Loading.show('loadingMacro');
                	setTimeout(function(){
    					$.ajax({
    						type: 'POST',
    						url: 'quickCompleteRequirementReceive',
    						async: false,
    						data: {
    							requirementId: requirementId,
    						},
    					}).done(function(data) {
    						location.reload();
    					});
    					Loading.hide('loadingMacro');
                	}, 500);
            		btnClick = true;
            	}
            }
		}]);
	};
	
	var sendUpRequirementNotify = function sendUpRequirementNotify(requirementId){
		bootbox.dialog(uiLabelMap.AreYouSureConfirm, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
            		Loading.show('loadingMacro');
                	setTimeout(function(){
    					$.ajax({
    						type: 'POST',
    						url: 'logisticsSendRequirement',
    						async: false,
    						data: {
    							requirementId: requirementId,
    							roleTypeId: specialistRole,
    						},
    						success: function(data){
    							$.ajax({
    								type: 'POST',
    								url: 'changeRequirementStatus',
    								async: false,
    								data: {
    									requirementId: requirementId,
    									statusId: "REQ_CONFIRMED",
    								},
    								success: function(data){
    								},
    							}).done(function(data) {
    								location.reload();
    							});
    						},
    					}).done(function(data) {
    					});
    					Loading.hide('loadingMacro');
                	}, 300);
            		btnClick = true;
            	}
            }
		}]);
	};

	var selectFacilityToAssign = function selectFacilityToAssign(requirementId){
		$("#selectFacilityWindow").jqxWindow("open");
	}
	
	var updateFacilityToRequirement = function updateFacilityToRequirement(requirementId, facilityId, contactMechId){
		jQuery.ajax({
	        url: "updateFacilityToRequirement",
	        type: "POST",
	        async: false,
	        data: {
	        	requirementId: requirementId,
	        	facilityId: $("#requiredFacilityId").val(),
	        	contactMechId: $("#requiredContactMechId").val(),
	        },
	        success: function(res) {
	        	$("#notifyCreateSuccessful").jqxNotification("open");
	        },
	    }).done(function() {
	    	if (requirement.statusId == "REQ_CREATED" || requirement.statusId == "REQ_PROPOSED"){
	    		$.ajax({
					type: 'POST',
					url: 'changeRequirementStatus',
					async: false,
					data: {
						requirementId: requirementId,
						statusId: "REQ_APPROVED",
					},
					success: function(data){
						location.reload();
					},
				});
	    	}
	    	window.location.replace("viewRequirementDetail?requirementId="+requirementId);
	    });
		$("#selectFacilityWindow").jqxWindow("close");
	}
	
	var prepareCreateTransferFromRequirement = function prepareCreateTransferFromRequirement(requirementId){
		window.location.replace("prepareCreateTransferFromRequirement?requirementId="+requirementId);
	}
	
	var createTransferFromRequirement = function createTransferFromRequirement(){
		$("#createTransferWindow").jqxWindow("open");
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
    
    var dataField = [
                	{ name: 'productId', type: 'string'},
                	{ name: 'productCode', type: 'string'},
                	{ name: 'productName', type: 'string' },
                	{ name: 'requirementId', type: 'string'},
                 	{ name: 'reqItemSeqId', type: 'string'},
                	{ name: 'internalName', type: 'string' },
                	{ name: 'expireDate', type: 'date', other: 'Timestamp'},
                	{ name: 'expireDateRequired', type: 'string'},
                	{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp'},
                	{ name: 'toExpiredDate', type: 'date', other: 'Timestamp'},
                	{ name: 'statusId', type: 'string' },
                	{ name: 'quantityUomId', type: 'string' },
                	{ name: 'currencyUomId', type: 'string' },
                	{ name: 'weightUomId', type: 'string' },
                	{ name: 'baseWeightUomId', type: 'string' },
                	{ name: 'quantity', type: 'number' },
                	{ name: 'weight', type: 'number' },
                	{ name: 'quantityAccepted', type: 'number' },
                	{ name: 'issuedQuantity', type: 'number' },
                	{ name: 'issuedWeight', type: 'number' },
                	{ name: 'actualExecutedQuantity', type: 'number' },
                	{ name: 'actualExecutedWeight', type: 'number' },
                	{ name: 'unitCost', type: 'number' },
                	{ name: 'description', type: 'string' },
                	{ name: 'requireAmount', type: 'string' },
                	{ name: 'lotId', type: 'string'},
                 	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
                 	{ name: 'rowDetail', type: 'string'},
                	];
    var columnList = [
					{
					    text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: '10%', pinned: true, editable: false,},
					{ text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: '20%', editable: false,},
					{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: '10%', editable: false, filterable: true, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') {
								var weightUomId = data.weightUomId;
								return '<span class="align-right" >' + getUomDescription(weightUomId) + '</span>';
							} else {
								return '<span class="align-right" >' + getUomDescription(value) + '</span>';
							}
							return false;
						},
						createfilterwidget: function (column, columnElement, widget) {
							widget.jqxDropDownList({ source: uomData, displayMember: 'description', valueMember: 'uomId' });
						}
					},
					{ text: uiLabelMap.QtyRequired, datafield: 'quantity', cellsalign: 'right', align: 'left', width: '10%', editable: false, filterable: true, filtertype: 'number',
						cellsrenderer: function(row, column, value){
							var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') {
								return '<span class="align-right">' +  formatnumber(data.weight) + '</span>';
							} else {
								return '<span class="align-right">' +  formatnumber(value) + '</span>';
							}
						},
					},
					{ text: uiLabelMap.ActualExecutedQuantity, datafield: 'issuedQuantity', cellsalign: 'right', align: 'left', width: '12%', editable: false, filterable: true, filtertype: 'number',
						cellsrenderer: function(row, column, value){
							if (value) {
								var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
								var requireAmount = data.requireAmount;
								if (requireAmount && requireAmount == 'Y') {
									value = data.issuedWeight;
								} else {
									if (!value){
										if (data.quantityAccepted){
											value = data.quantityAccepted;
										}
										return '<span class="align-right"></span>';
									}
								}
								return '<span class="align-right">' + formatnumber(value) + '</span>';
							} else {
								var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
								var requireAmount = data.requireAmount;
								if (requireAmount && requireAmount == 'Y') {
									value = data.actualExecutedWeight;
								} else {
									value = data.actualExecutedQuantity;
								}
								return '<span class="align-right">' + formatnumber(value) + '</span>';
							}
						},
					},
					{ text: uiLabelMap.ExecutedExpireDate, dataField: 'expireDate', align: 'left', width: '12%', editable: false, cellsalign: 'right', hidden: true,
						cellsrenderer: function(row, column, value){
							if (value){
								return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(value) + '</span>';
							} else {
								return '<span class="align-right"></span>';
							}
						}
					},
					{ text: uiLabelMap.ManufactureDate, dataField: 'datetimeManufactured', align: 'left', width: '12%', editable: false, cellsalign: 'right', hidden: true,
						cellsrenderer: function(row, column, value){
							if (value){
								return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(value) + '</span>';
							} else {
								return '<span class="align-right"></span>';
							}
						}
					},
					{ text: uiLabelMap.UnitPrice, datafield: 'unitCost', align: 'left', width: '10%', editable: false, filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span class="align-right">' + formatcurrency(value) +'</span>';
							} else {
								value = 0;
								return '<span class="align-right">' + formatcurrency(value) +'</span>';
							}
					    }, 
					},
					{ hidden: true, text: uiLabelMap.EXPRequired, dataField: 'expireDateRequired', align: 'left', width: '20%', editable: false, cellsalign: 'right',
						cellsrenderer: function(row, column, value){
							var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
							if (data.fromExpiredDate){
								var str = value;
								if (data.fromExpiredDate) {
									str = DatetimeUtilObj.getFormattedDate(new Date(data.fromExpiredDate));
								} 
								if (data.toExpiredDate) {
									str = str + ' - ' + DatetimeUtilObj.getFormattedDate(new Date(data.toExpiredDate));
								}
								return '<span class="align-right">'+str+'</span>';
							} else {
								return '<span class="align-right"></span>';
							}
						}
					},
					{ hidden: true, text: uiLabelMap.Batch, datafield: 'lotId', align: 'left', width: '10%', editable: false,
						cellsrenderer: function(row, colum, value){
					    }, 
					},
					{ text: uiLabelMap.Description, datafield: 'description', align: 'left', width: '20%', editable: false,},
                  ];
    
    var dataFieldPopup = [
                 	{ name: 'productId', type: 'string'},
                 	{ name: 'productCode', type: 'string'},
                 	{ name: 'requirementId', type: 'string'},
                 	{ name: 'reqItemSeqId', type: 'string'},
                 	{ name: 'productName', type: 'string' },
                 	{ name: 'internalName', type: 'string' },
                 	{ name: 'requireAmount', type: 'string' },
                 	{ name: 'expireDateRequired', type: 'string'},
                 	{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp'},
                 	{ name: 'toExpiredDate', type: 'date', other: 'Timestamp'},
                 	{ name: 'statusId', type: 'string' },
                 	{ name: 'quantityUomId', type: 'string' },
                 	{ name: 'weightUomId', type: 'string' },
                 	{ name: 'currencyUomId', type: 'string' },
                 	{ name: 'quantity', type: 'number' },
                 	{ name: 'weight', type: 'number' },
                 	{ name: 'actualExecutedQuantity', type: 'number' },
                 	{ name: 'unitCost', type: 'number' },
                 	{ name: 'quantityCreate', type: 'number' },
                 	{ name: 'quantityCreated', type: 'number' },
                 	{ name: 'description', type: 'string' },
                 	];
     var columnListPopup = [
 					{
 					    text: uiLabelMap.SequenceId, sortable: false, filterable: true, editable: false, pinned: true,
 					    groupable: false, draggable: false, resizable: false,
 					    datafield: '', columntype: 'number', width: 50,
 					    cellsrenderer: function (row, column, value) {
 					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
 					    }
 					},
 					{ text: uiLabelMap.ProductId, datafield: 'productCode', align: 'left', width: 120, pinned: true, editable: false,},
 					{ text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 200, editable: false,},
 					{ text: uiLabelMap.QuantityUomId, datafield: 'quantityUomId', align: 'left', width: 120, editable: false, filterable: true,
 						cellsrenderer: function(row, column, value){
 							if (value){
 								var data = $('#listRequirementItemPopup').jqxGrid('getrowdata', row);
 								var requireAmount = data.requireAmount;
 								if (requireAmount && requireAmount == 'Y') value = data.weightUomId;
				            	return '<span class="align-right" title='+getUomDescription(value)+'>' + getUomDescription(value) + '</span>';
 							} else {
 								return '<span class="align-right"></span>';
 							}
 							return false;
 						},
 					},
 					{ text: uiLabelMap.RequiredNumberSum, datafield: 'quantity', cellsalign: 'right', align: 'left', width: 130, editable: false, filterable: true,
 						cellsrenderer: function(row, column, value){
 							var data = $('#listRequirementItemPopup').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') value = data.weight;
 							if (value){
 								return '<span class="align-right">' +  formatnumber(value) + '</span>';
 							} else {
 								return '<span></span>';
 							}
 						},
 					},
 					{ text: uiLabelMap.CreatedNumberSum, datafield: 'quantityCreated', cellsalign: 'right', align: 'left', width: 130, editable: false, filterable: true,
 						cellsrenderer: function(row, column, value){
 							if (value){
 								return '<span class="align-right">' +  formatnumber(value) + '</span>';
 							} else {
 								return '<span class="align-right">0</span>';
 							}
 						},
 					},
 					{ text: uiLabelMap.QuantityDelivered, datafield: 'actualExecutedQuantity', cellsalign: 'right', align: 'left', width: 130, editable: false, filterable: true,
 						cellsrenderer: function(row, column, value){
 							var data = $('#listRequirementItemPopup').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') value = data.actualExecutedWeight;
 							if (value){
 								return '<span class="align-right">' +  formatnumber(value) + '</span>';
 							} else {
 								return '<span class="align-right">0</span>';
 							}
 						},
 					},
 					{ text: uiLabelMap.QuantityCreateSum, datafield: 'quantityCreate', cellsalign: 'right', columntype: 'numberinput', align: 'left', width: 130, editable: true, filterable: true,
 						cellsrenderer: function(row, column, value){
 							return '<span style=\"text-align: right\" class=\"focus-color\">' + formatnumber(value) + '</span>';
 						},
 						initeditor: function(row, value, editor){
 							var data = $('#listRequirementItemPopup').jqxGrid('getrowdata', row);
 							editor.jqxNumberInput({ decimalDigits: 0});
 							if (null === value || value === undefined){
 								editor.jqxNumberInput('val', data.quantityCreate);
 							} else {
 								editor.jqxNumberInput('val', value);
 							}
 					    },
 					    validation: function (cell, value) {
 					        var data = $('#listRequirementItemPopup').jqxGrid('getrowdata', cell.row);
 					        var qty = data.quantity;
 					        var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') qty = data.weight;
							
 					        if (value > (qty - data.quantityCreated)){
 					        	var tmp = qty - data.quantityCreated;
 					            return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ': ' + formatnumber(value) + ' > ' + formatnumber(tmp)};
 					        } else{
 					        	if (value <= 0){
 					        		return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
 					        	} else {
 					        		return true;
 					        	}
 					        }
 					    },
 					},
 					{ text: uiLabelMap.UnitPrice, datafield: 'unitCost', align: 'left', width: 130, editable: false,
 						cellsrenderer: function(row, colum, value){
 							if(value){
 								return '<span class="align-right">' + formatcurrency(value) +'</span>';
 							} else {
 								value = 0;
 								return '<span class="align-right">' + formatcurrency(value)+'</span>';
 							}
 					    }, 
 					},
 					{ text: uiLabelMap.EXPRequired, dataField: 'expireDateRequired', align: 'left', width: 200, editable: false, cellsalign: 'right',
						cellsrenderer: function(row, column, value){
							var data = $('#listRequirementItem').jqxGrid('getrowdata', row);
							if (data.fromExpiredDate){
								var str = value;
								if (data.fromExpiredDate) {
									str = DatetimeUtilObj.getFormattedDate(new Date(data.fromExpiredDate));
								} 
								if (data.toExpiredDate) {
									str = str + ' - ' + DatetimeUtilObj.getFormattedDate(new Date(data.toExpiredDate));
								}
								return '<span class="align-right">'+str+'</span>';
							} else {
								return '<span class="align-right"></span>';
							}
						}
					},
 					{ text: uiLabelMap.Description, datafield: 'description', align: 'left', width: 200, editable: false,},
                   ];
     
     var initGridRequirementItem = function (grid){
    	 var configGrid = {
    			 width: '100%', 
    			 virtualmode: true,
    			 showtoolbar: false,
    			 selectionmode: 'singlerow',
    			 pageable: true,
    			 sortable: true,
    			 filterable: true,	        
	        	editable: false,
	        	rowsheight: 26,
	        	rowdetails: showDetail,
	        	rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 222, rowdetailshidden: true },
	        	initrowdetails: initrowdetails,
	        	useUrl: false,
	        	source: {pagesize: 10}
		};
		Grid.initGrid(configGrid, dataField, columnList, null, grid);
     }
     
     var initGridRequirementItemPopup = function (grid){
    	 var configGrid = {
    			 datafields: dataFieldPopup,
    			 columns: columnListPopup,
    			 width: '100%',
    			 height: 'auto',
    			 sortable: true,
    			 editable: true,
    			 filterable: true,
    			 pageable: true,
    			 showfilterrow: true,
    			 useUtilFunc: false,
    			 useUrl: true,
    			 url: '',
    			 groupable: false,
    			 showgroupsheader: false,
    			 showaggregates: false,
    			 showstatusbar: false,
    			 virtualmode:true,
    			 showdefaultloadelement:true,
    			 autoshowloadelement:true,
    			 showtoolbar:false,
    			 columnsresize: true,
    			 isSaveFormData: true,
    			 formData: "filterObjData",
    			 selectionmode: "checkbox",
    			 bindresize: true,
    			 pagesize: 10,
    	 };
    	 gridReqItemObj = new OlbGrid(grid, null, configGrid, []);
     }
     
     var initrowdetails = function (index, parentElement, gridElement, datarecord) {
 		var grid = $($(parentElement).children()[0]);
 		$(grid).attr('id','listRequirementItem'+index);
 		var listChilds = [];
 		var items = [];
 		if (datarecord.rowDetail){
 			items = datarecord.rowDetail;
 		}
 		for (var m = 0; m < items.length; m ++){
 			var child = items[m];
 			var rowDetail = {};
 			rowDetail['productId'] = child.productId;
 			rowDetail['productCode'] = child.productId;
 			rowDetail['productName'] = child.productId;
 			rowDetail['expireDate'] = child.expireDate;
 			rowDetail['datetimeManufactured'] = child.datetimeManufactured;
 			rowDetail['lotId'] = child.lotId;
 			rowDetail['quantityExecuted'] = child.quantityExecuted;
 			rowDetail['weightExecuted'] = child.weightExecuted;
 			
 			listChilds.push(rowDetail);
 		}
 		var sourceGridDetail =
 	    {
 	        localdata: listChilds,
 	        datatype: 'local',
 	        datafields:
 		        [
 		        { name: 'productId', type: 'string' },
 		        { name: 'productCode', type: 'string' },
 				{ name: 'productName', type: 'string' },
 				{ name: 'lotId', type: 'string' },
 				{ name: 'quantityExecuted', type: 'number' },
 				{ name: 'weightExecuted', type: 'number' },
 				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
 			 	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
 				]
 	    };
 	    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
 	    grid.jqxGrid({
 	        width: '98%',
 	        height: 200,
 	        theme: 'olbius',
 	        localization: getLocalization(),
 	        source: dataAdapterGridDetail,
 	        sortable: true,
 	        pagesize: 5,
 	 		pageable: true,
 	 		editable: true,
 	 		columnsresize: true,
 	        selectionmode: 'singlerow',
 	        columns: [
 						{ text: uiLabelMap.ExpireDate, dataField: 'expireDate', width: '25%', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',},
 						{ text: uiLabelMap.ProductManufactureDate, dataField: 'datetimeManufactured', width: '25%', columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
 							cellsrenderer: function (row, column, value) {
 						    }
 						},
 						{ text: uiLabelMap.Batch, dataField: 'lotId', width: '25%', filtertype:'input', editable: false,},
 						{ text: uiLabelMap.Quantity, datafield: 'quantityExecuted', cellsalign: 'right', align: 'left', minwidth: '20%', editable: false, filterable: true, filtertype: 'number',
 							cellsrenderer: function(row, column, value){
 								var data = grid.jqxGrid('getrowdata', row);
 								var requireAmount = data.requireAmount;
 								if (requireAmount && requireAmount == 'Y') {
 									return '<span class="align-right">' +  formatnumber(data.weightExecuted) + '</span>';
 								} else {
 									return '<span class="align-right">' +  formatnumber(value) + '</span>';
 								}
 							},
 						},
 					]
 	        });
 	}
     
    function loadRequirementItem(valueDataSoure){
    	var tmpS = $("#listRequirementItem").jqxGrid('source');
		tmpS._source.localdata = valueDataSoure;
		$("#listRequirementItem").jqxGrid('updatebounddata');
	}
	         
    function loadRequirementItemPopup(valueDataSoure){
    	var tmpS = $("#listRequirementItemPopup").jqxGrid('source');
		tmpS._source.localdata = valueDataSoure;
		$("#listRequirementItemPopup").jqxGrid('updatebounddata');
	}
    
    function viewTransferDetail(transferId){
		window.location.href = 'viewDetailTransfer?transferId=' + transferId;
	}
    
    var prepareCombineProduct = function (){
    	window.location.replace("prepareCombineProduct?requirementId="+requirementId);
    };
    
    var cancelRequirement = function (requirementId){
    	bootbox.dialog(uiLabelMap.AreYouSureCancel, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll(); btnClick = false;}
	        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	if (!btnClick){
	            		Loading.show('loadingMacro');
		            	setTimeout(function(){
							$.ajax({
								type: 'POST',
								url: 'changeRequirementStatus',
								async: false,
								data: {
									requirementId: requirementId,
									statusId: 'REQ_CANCELLED'
								},
								success: function(data){
									location.reload();
								},
							}).done(function(data) {
							});
							Loading.hide('loadingMacro');
		            	}, 300);
	            		btnClick = true;
	            	}
	            }
			}]);
    };
    
    var exportPDF = function (requirementId){
    	window.open("printRequirement.pdf?requirementId="+requirementId, "_blank");
    };
    
	return {
		init: init,
		changeRequirementStatus: changeRequirementStatus,
		quickShipRequirement: quickShipRequirement,
		sendRequirementNotify: sendRequirementNotify,
		selectFacilityToAssign: selectFacilityToAssign,
		prepareShipmentFromRequirement: prepareShipmentFromRequirement,
		prepareCreateTransferFromRequirement: prepareCreateTransferFromRequirement,
		createTransferFromRequirement: createTransferFromRequirement,
		loadRequirementItem: loadRequirementItem,
		sendUpRequirementNotify: sendUpRequirementNotify, 
		prepareInventoryToChange:prepareInventoryToChange,
		quickReceiveInventory: quickReceiveInventory,
		prepareCombineProduct: prepareCombineProduct,
		cancelRequirement: cancelRequirement,
		rejectRequirement: rejectRequirement,
		prepareRejectRequirement: prepareRejectRequirement,
		exportPDF: exportPDF,
	};
}());