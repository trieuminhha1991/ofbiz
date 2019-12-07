$(function(){
	SalesFunctionObj.init();
});
var SalesFunctionObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInputs = function(){
		$('#document').ready(function(){
			Loading.setIndex('999999');
		});
		$("#returnOrderPopupWindow").jqxWindow({
		    maxWidth: 1000, minWidth: 200, width: 600, modalZIndex: 10000, zIndex:10000, minHeight: 200, height: 340, maxHeight: 1000, resizable: false, cancelButton: $("#returnCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme: 'olbius'           
		});

		$("#reasonPopupWindow").jqxWindow({
		    maxWidth: 900, minWidth: 400, width: 600, modalZIndex: 10000, zIndex:10000, minHeight: 200, height: 240, maxHeight: 500, resizable: false, cancelButton: $("#holdCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme: 'olbius'           
		});

		$("#selectSupplierWindow").jqxWindow({
		    maxWidth: 800, minWidth: 400, width: 600, modalZIndex: 10000, zIndex:10000, minHeight: 100, height: 320, maxHeight: 500, resizable: false, cancelButton: $("#chooseCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme: 'olbius'           
		});

		$("#addReasonWindow").jqxWindow({
			maxWidth: 900, minWidth: 400, width: 600, minHeight: 250, height: 250, maxHeight: 250, resizable: true,  isModal: true, modalZIndex: 100000, autoOpen: false, cancelButton: $("#addReasonCancel"), modalOpacity: 0.7, theme: 'olbius'           
		});
		
		$('#enumId').jqxDropDownList({ width: 300, dropDownHeight: 200, selectedIndex: 0, source: listReasonData, theme: 'olbius', displayMember: 'description', valueMember: 'enumId', placeHolder: uiLabelMap.PleaseSelectTitle,});
		$("#enumCode").jqxInput({width: 300, placeHolder: uiLabelMap.Enter1to60characters});
		$('#orderReturnReasonId').jqxDropDownList({ width: 300, selectedIndex: 0, source: returnReasonData, theme: theme, displayMember: 'description', valueMember: 'returnReasonId',});
		$('#orderFacilityId').jqxDropDownList({ width: 300, selectedIndex: 0, source: orderFacToReturnData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
		$('#orderInventoryStatusId').jqxDropDownList({ width: 300, selectedIndex: 0, source: invStatusData, theme: theme, displayMember: 'description', valueMember: 'statusId',});
		$('#orderReturnDate').jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#deliveredReturnDate').jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss'});

		$('#startShipDateFromVendor').jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#requiredDeliveryDate').jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: true});
		$('#requiredDeliveryDate').jqxDateTimeInput('val',requireDeliveryDate);

		var now = new Date();
		$('#startShipDateFromVendor').jqxDateTimeInput('val', now);

		initValidateReturn($("#returnOrderPopupWindow"));
		
	};
	
	var initElementComplex = function(){
		
	};
	
	var initEvents = function(){
		$("#reasonPopupWindow").on('open', function(event){
			if (!$('#enumId').val()){
				$('#holdSave').attr('disabled', true);
			}
		});

		$("#addReasonWindow").on('close', function(event){
			$('#enumCode').val("");
			$('#reasonDescription').val('');
			$('#addReasonWindow').jqxValidator('hide')
		});

		$("#returnSave").on("click", function (event){
			if (!$('#returnOrderPopupWindow').jqxValidator('validate')){
				return false;
			}
			
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
			[{"label": uiLabelMap.Cancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
					setTimeout(function(){
						var datetimeReceivedReturn = $('#orderReturnDate').jqxDateTimeInput('getDate');
						var deliveredReturnDate = $('#deliveredReturnDate').jqxDateTimeInput('getDate');
						var statusTemp = $("#orderInventoryStatusId").val();
						if (statusTemp == "Good"){
							statusTemp = null;
						}
						jQuery.ajax({
							url: "returnSalesOrder",
							type: "POST",
							async: false,
							data: {
								orderId: orderId,
								returnReasonId: $("#orderReturnReasonId").val(), 
								inventoryStatusId: statusTemp,
								facilityId: $("#orderFacilityId").val(),
								datetimeReceived: datetimeReceivedReturn.getTime(),
								datetimeDelivered: deliveredReturnDate.getTime(),
								},
							success: function(res) {
								$("#returnOrderPopupWindow").jqxWindow('close');
								window.location.href = "viewOrder?orderId="+orderId+"&activeTab=deliveries-tab";
							}
						});
					Loading.hide('loadingMacro');
					}, 500);
			    }
			}]);
		});
		
		$('#selectSupplierWindow').jqxValidator({
			rules:[
				{
					input: '#startShipDateFromVendor', 
				    message: uiLabelMap.MustBeBeforeRequiredDate, 
				    action: 'blur',
				    position: 'topcenter',
				    rule: function (input) {
				    	var startShipDateFromVendor = $('#startShipDateFromVendor').jqxDateTimeInput('getDate');
					   	if ((typeof(requireDeliveryDate) != 'undefined' && requireDeliveryDate != null && !(/^\s*$/.test(requireDeliveryDate))) && (typeof(startShipDateFromVendor) != 'undefined' && startShipDateFromVendor != null && !(/^\s*$/.test(startShipDateFromVendor)))) {
				 		    if (requireDeliveryDate < startShipDateFromVendor) {
				 		    	return false;
				 		    }
					   	}
					   	return true;
				    }
				},
				{
					input: '#supplierId', 
			        message: uiLabelMap.FieldRequired, 
			        action: 'blur',
			        position: 'topcenter',
			        rule: function (input) {	
			     	   	var tmp = $('#supplierId').val();
			            return tmp ? true : false;
			        }
				},
				{
					input: '#supplierFacilityId', 
			        message: uiLabelMap.FieldRequired, 
			        action: 'blur',
			        position: 'topcenter',
			        rule: function (input) {	
			     	   	var tmp = $('#supplierFacilityId').val();
			            return tmp ? true : false;
			        }
				},
				{
					input: '#supplierFacilityCTMId', 
			        message: uiLabelMap.FieldRequired, 
			        action: 'blur',
			        position: 'topcenter',
			        rule: function (input) {	
			     	   	var tmp = $('#supplierFacilityId').val();
			            return tmp ? true : false;
			        }
				},
		]});
		
		$("#holdSave").on("click", function (event){
			if (!$("#enumId").val()){
				bootbox.dialog(uiLabelMap.PleaseChooseAReason + ". " + uiLabelMap.GoToConfigToAddReason, [{
		            "label" : uiLabelMap.OK,
		            "class" : "btn btn-primary standard-bootbox-bt",
		            "icon" : "fa fa-check",
		            }]
		        );
				return false;
			} else {
				if (!$("#statusDescription").val()){
					bootbox.dialog(PleaseEnterDescription, [{
			            "label" : uiLabelMap.OK,
			            "class" : "btn btn-primary standard-bootbox-bt",
			            "icon" : "fa fa-check",
			            }]
			        );
					return false;
				} else {
					bootbox.dialog(uiLabelMap.AreYouSureChange, 
					[{"label": uiLabelMap.Cancel, 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					    "callback": function() {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.OK,
					    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					    "callback": function() {
					    	Loading.show('loadingMacro');
					    	setTimeout(function(){		
					    		jQuery.ajax({
									url: "changeOrderHold",
									type: "POST",
									async: false,
									data: {
										orderId: orderId,
										statusId: "ORDER_HOLD", 
										changeReason : $("#enumId").val(),
										noteInfo: $("textarea#statusDescription").val(),
										},
									success: function(res) {
										window.location.href = "viewOrder?orderId="+orderId+"&activeTab=orderoverview-tab";
										$("#notifyId").jqxNotification("open");
										$('#reasonPopupWindow').jqxWindow('close');
										if ($("#jqxgrid")){
											$("#addrowbuttonjqxgrid").hide();
											$("#holdOrderId").hide();
											$("#approveOrderId").show();
											$("#customcontrol3").hide();
										}
						       	  	}
								});
				    		Loading.hide('loadingMacro');
					    	}, 500);
					    }
					}]);
				}
			}
		});
		
		$("#addReasonSave").on("click", function(){
			if (!$('#addReasonWindow').jqxValidator('validate')){
				return false;
			}
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
			[{"label": uiLabelMap.Cancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": uiLabelMap.OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
					setTimeout(function(){
						 $.ajax({
							type: "POST",
							url: "updateOrderHoldingReason",
							data: {
								enumCode: $("#enumCode").val(),
						 		description: $("#reasonDescription").val(),
						 		enumTypeId: $("#enumTypeId").val(),
							},
							async: false,
							success: function(res) {
								var enumId = res['enumId'];
								var row = {};
								row['enumId'] = enumId;
								row['description'] = $("#enumCode").val() + " " + $("#reasonDescription").val();
								listReasonData.push(row);
								$('#enumId').jqxDropDownList({source: listReasonData});
								$("#addReasonWindow").jqxWindow('close');
							}
						 });
					 Loading.hide('loadingMacro');
					}, 500);
			    }
			}]);
		});

		$("#chooseSave").on("click", function(){
			if (!$('#selectSupplierWindow').jqxValidator('validate')){
				return false;
			}
			bootbox.dialog(AreYouSureCreate, 
			[{"label": Cancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			    "callback": function() {bootbox.hideAll();}
			}, 
			{"label": OK,
			    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			    "callback": function() {
			    	Loading.show('loadingMacro');
			    	setTimeout(function(){		
			    		$.ajax({
							type: "POST",
							url: "updateSupplierPartyIdForOrder",
							data: {
						 		supplierPartyId: jOlbUtil.getAttrDataValue("supplierId"),
						 		facilityId: jOlbUtil.getAttrDataValue("supplierFacilityId"),
						 		contactMechId: jOlbUtil.getAttrDataValue("supplierFacilityCTMId"),
						 		orderId: orderId,
						 		startShipDateFromVendor: $('#startShipDateFromVendor').jqxDateTimeInput('getDate').getTime(),
							},
							async: false,
							success: function(res) {
								window.location.href = "viewOrder?orderId="+orderId+"&activeTab=deliveries-tab";
								$("#selectSupplierWindow").jqxWindow('close');
							}
						 });
			    	Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);	
		});
	};
	
	var initValidateForm = function(){
		$("#returnOrderPopupWindow").jqxValidator({
			rules:[
			{
				input: '#orderFacilityId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderFacilityId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#orderReturnReasonId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderReturnReasonId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#orderInventoryStatusId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderInventoryStatusId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#orderReturnDate', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderReturnDate').val();
	                return tmp ? true : false;
	            }
			},
			],
		});
		
		$("#addReasonWindow").jqxValidator({
			rules:[{
						input: '#enumCode', 
			            message: uiLabelMap.FieldRequired, 
			            action: 'blur', 
			            rule: function (input) {	
			     		   	if (!$("#enumCode").val()) {
			     		    	return false;
			     		   	}
			     		   	return true;
			            }
					},
					{
						input: '#enumCode', 
			            message: uiLabelMap.FieldRequired, 
			            action: 'blur', 
			            rule: function (input) {
			            	var code = $("#enumCode").val();
			     		   	if (code.length > 60 || code.length < 1) {
			     		    	return false;
			     		   	}
			     		   	return true;
			            }
					},
			],
		});
	};
	
	var showEditPendingPopup = function showEditPendingPopup(){
		$("#reasonPopupWindow").jqxWindow('open');
	}

	var showChooseProviderPopup = function showChooseProviderPopup(){
		$("#selectSupplierWindow").jqxWindow('open');
	}

	var showReturnOrderPopup = function showReturnOrderPopup(orderId){
		$("#returnOrderPopupWindow").jqxWindow('open');
		$("#orderId").text(orderId);
	}

	var approveHoldOrder = function approveHoldOrder(orderId){
		bootbox.dialog(uiLabelMap.AreYouSureApprove, 
		[{"label": uiLabelMap.Cancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){		
		    		jQuery.ajax({
						url: "approveHeldOrder",
						type: "POST",
						async: false,
						data: {
							orderId: orderId,
							statusId: "ORDER_APPROVED", 
						},
						success: function(res) {
							window.location.href = "viewOrder?orderId="+orderId+"&activeTab=orderoverview-tab";
							$("#notifyId").jqxNotification("open");
							$('#reasonPopupWindow').jqxWindow('close');
							if ($("#jqxgrid")){
								$("#addrowbuttonjqxgrid").hide();
								$("#holdOrderId").hide();
								$("#approveOrderId").show();
								$("#customcontrol3").hide();
							}
			       	  	}
					});
	    		Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);
	}
	
	var addReason = function addReason(){
		if (hasPermission == true){
			$("#addReasonWindow").jqxWindow('open');
		} else {
			bootbox.dialog(uiLabelMap.YouHavenotCreatePermission, [{
	            "label" : uiLabelMap.OK,
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
			return false;
		}
	}
	
	function initValidateReturn(element){
		element.jqxValidator({
			rules:[
			{
				input: '#orderFacilityId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderFacilityId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#orderReturnReasonId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderReturnReasonId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#orderInventoryStatusId', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderInventoryStatusId').jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			},
			{
				input: '#orderReturnDate', 
	            message: uiLabelMap.FieldRequired, 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#orderReturnDate').val();
	                return tmp ? true : false;
	            }
			},
			],
		});
	}
	
	return {
		init: init,
		addReason: addReason,
		approveHoldOrder: approveHoldOrder,
		showReturnOrderPopup: showReturnOrderPopup,
		showChooseProviderPopup: showChooseProviderPopup,
		showEditPendingPopup: showEditPendingPopup,
	};
}());