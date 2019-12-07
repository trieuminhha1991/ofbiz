$(function(){
	AddLocationFacilityChildren.init();
});

var AddLocationFacilityChildren = (function(){
	var init = function(){
		initInput();
		initEvents();
		initValidateForm();
	};
	
	// KHOI TAO INPUT, POPUP
	var initInput = function(){
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow({theme: 'olbius',
			width: 500, maxWidth: 1200, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdderFacilityLocationAreaInArea"), modalOpacity: 0.7
		});
	}
	
	//VALIDATE FROM ADD LOCATION FACILITY CHILDREN
	var initValidateForm = function(){
		$('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxValidator({
	        rules: [
						{ input: '#txtLocationCodeInArea', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
						{ input: '#txtLocationCodeInArea', message: uiLabelMap.DuplicateLocationCode, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = $("#txtLocationCodeInArea").val();
								if (_.indexOf(LocationFacilityObj.arrayLocationCodeAvalible, value) != -1) {
									return false;
								}
								return true;
							}
						},
						{ input: '#txtLocationCodeInArea', message: uiLabelMap.ContainSpecialSymbol, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = $("#txtLocationCodeInArea").val();
								if (!value.containSpecialChars()) {
									return true;
								}
								return false;
							}
						}
	               ]
	    });
		
	};
	var locationIdGlobal = null;
	var warningMode = false;
	function addFacilityLocationAreaInArea(locationId, parentLocationId, typeInsert) {
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").attr("typeInsert", typeInsert);
		var path = LocationFacilityObj.getPathArea(locationId, parentLocationId, "");
		$("#tarMapArea").text(path);
		clearPopup();
		locationIdGlobal = locationId;
		$("#txtOrderNote").text("");
		var height = "280px";
		if (mapHasInventoryInLocation[locationId]) {
			$("#txtOrderNote").text(uiLabelMap.StatusLocationHasItems);
			$("#divOrderNotes").css("display", "block");
			height = "540px";
			warningMode = true;
		}else {
			$("#txtOrderNote").text("");
			$("#divOrderNotes").css("display", "none");
			warningMode = false;
		}
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow({height: height });
		var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxWindow('width');
        $("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow('open');
	}
	
	//EVENT ADD LOCATION FACILITY CHILDREN
	var initEvents = function(){
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").on('open', function (event) {
			disableScrolling();
		});
		$("#jqxwindowPopupAdderFacilityLocationAreaInArea").on('close', function (event) {
			setTimeout(function(){
	    		$("#viewProduct").attr('disabled', true);
	    		$("#addProduct").attr('disabled', true);
	    		$("#moveProductTo").attr('disabled', true);
	    		$("#btnCancelReset").attr('disabled', true);
	    	}, 500);
			enableScrolling();
			$('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxValidator('hide');
			reset();
		});
		
		$("#alterSaveAdderFacilityLocationAreaInArea").click(function () {
			if ($('#jqxwindowPopupAdderFacilityLocationAreaInArea').jqxValidator('validate')) {
				bootbox.dialog(uiLabelMap.AreYouSureCreate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){		
				    		var data = {};
							var description = $("#tarDescriptionInArea").val();
							var typeInsert = $("#jqxwindowPopupAdderFacilityLocationAreaInArea").attr("typeInsert");
							data.facilityId = facilityIdGlobal;
							data.parentLocationId = locationIdGlobal;
							data.locationCode = $("#txtLocationCodeInArea").val();
							data.description = description;
							data.locationFacilityTypeId = typeInsert;
							$("#jqxwindowPopupAdderFacilityLocationAreaInArea").jqxWindow('close');
							$("#treeGrid").jqxTreeGrid('addRow', null, data, 'last', LocationFacilityObj.rowKey);
				    	Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);	
			}
		});
	};
	
	return {
		init: init,
		addFacilityLocationAreaInArea: addFacilityLocationAreaInArea
	};
}());