$(document).ready(function(){
	OlbShippingTripAccm.init();
});
var OlbShippingTripAccm = (function(){

	var init = function(){
	    var validator;
		initElement();
		initComplexElement();
		initEvent();
		initJqxValidator();
	};

	var initElement = function(){
	    $("#shipperPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: listShippers, valueMember:'partyId', displayMember:'description', height: '24px', dropDownHeight: 200});
	    $('#popupCreateNewInvoice').jqxWindow({width: 1300, height: 580, maxWidth: 1500, minWidth: 950, minHeight: 400, maxHeight: 800, zIndex: 9999, resizable: true,draggable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius', cancelButton: $('#alterCancel')});
	};

	var initComplexElement = function(){

	};

	var initEvent = function(){
        $('#shipperPartyId').on('change', function (event){
        var args = event.args;
        	if (args) {
        		var index = args.index;
        		var item = args.item;
        		var value = item.value;
        		var label = item.label;
        		OlbGridUtil.updateSource($('#jqGridNewInvoice'), "jqxGeneralServicer?sname=JQGetListShippingTrip&shipperId="+value);
        		globalVar.shipperSelected = [{  "shipperId": value, "description": label}];
        		OlbShippingTripAccmInfo.init();
        	}
        });
        $('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
        	if(info.step == 1 && (info.direction == "next") ) {
        		var resultValidate = validator.validate();
                if(!resultValidate) return false;
                var rowl = document.getElementById("invoice-item-table").rows.length;
                if (rowl != null){
                	var len = rowl - 2;
                	for(var i = 0; i <len; i++){
                		document.getElementById("invoice-item-table").deleteRow(1);
                	}
                }
        		globalVar.formData = OlbShippingTripAccmInfo.getFormData();
        		globalVar.dataGridInvoiceInfo = getDataView();
        		OlbShippingTripAccmConfirm.init();
        	}
        }).on('finished', function(e) {
        	bootbox.dialog(uiLabelMap.BACCCreateInvoiceConfirm,
        		[{
        			"label" : uiLabelMap.CommonSubmit,
        			"class" : "btn-primary btn-small icon-ok open-sans",
        			"callback": function() {
        				createInvoice();
        			}
        		},
        		{
        			"label" : uiLabelMap.CommonCancel,
        			"class" : "btn-danger btn-small icon-remove open-sans",
        		}]
        		);

        });

        $('#popupCreateNewInvoice').on('close', function (event) {
            validator.hide();
        	$('#invoiceDate').jqxDateTimeInput({value : new Date()});
        	$('#dueDate').jqxDateTimeInput({value : new Date()});
        	$('#description').val('');
        	var dropData = $('#jqxGridInvoiceAccmInfo').jqxGrid('getrows');
        	for(var i = 0; i <dropData.length; i++){
        		document.getElementById("invoice-item-table").deleteRow(1);
        	}
        	resetStep();
        });
	};

	var initJqxValidator = function(){
    		var extendRules = [];

    		var mapRules = [
    		     	     	{input: '#invoiceDate', type: 'validDateCompareToday'},
    		     	    	{input: '#dueDate', type: 'validCompareTwoDate', paramId1 : "invoiceDate", paramId2 : "dueDate"}
    		             ];

    		validator = new OlbValidator($('#popupCreateNewInvoice'), mapRules, extendRules, {position: 'bottom'});
    	};


	var createPopupInvoice = function(){
	    var gridInvoice = $('#jqGridNewInvoice');
	    var selectedIndexs = gridInvoice.jqxGrid('getselectedrowindexes');
            if(selectedIndexs.length == 0){
                bootbox.dialog(uiLabelMap.DAYouNotYetChooseProduct, [{
                    "label" : uiLabelMap.OK,
                    "class" : "btn btn-primary standard-bootbox-bt",
                    "icon" : "fa fa-check",
                }]
                );
                return false;
            }
        var localdataSelected = getDataView();
        OlbGridUtil.updateSource($("#jqxGridInvoiceAccmInfo"), null, localdataSelected, function(){});
        $('#popupCreateNewInvoice').jqxWindow('open');
	}

	var getDataView = function() {
    	var grid = $('#jqGridNewInvoice');
    	var rowindexes = grid.jqxGrid('getselectedrowindexes');
    	var dataMap = [];
    	for(var i = 0; i < rowindexes.length; i++){
    		var dataSelected = grid.jqxGrid('getrowdata', rowindexes[i]);
    		dataMap.push(dataSelected);
    	}
    	return dataMap;
    };

    var resetStep = function(){
    		$('#fuelux-wizard').wizard('previous');
    	};

	return {
		init: init,
		createPopupInvoice: createPopupInvoice,
		getDataView: getDataView,
		resetStep: resetStep
	};
}());

var createInvoice = function() {
	var formData = OlbShippingTripAccmInfo.getFormData();
	var listShippingTrip = OlbShippingTripAccmInfo.getShippingTripSelected();
	var itemData = OlbShippingTripAccmConfirm.getFormData();
	var submitData = {};
	submitData['invoiceTypeId'] =  formData['invoiceTypeId'];
	submitData['description'] = formData['description'];
	submitData['dueDate'] = formData['dueDate'];
	submitData['invoiceDate'] = formData['invoiceDate'];
	submitData['organizationId'] = formData['organizationId'];
	submitData['currencyUomId'] = formData['currencyUomId'];
	submitData['customerId'] = formData['customerId'];
	submitData['listItems'] = JSON.stringify(itemData);
	if (formData['invoiceTypeId'] == 'PURCHASE_INVOICE') {
		submitData['glAccountTypeId'] = "ACCOUNTS_PAYABLE";
	}
    submitData['listShippingTrip'] = listShippingTrip;
	Loading.show('loadingMacro');
	$.ajax({
		url: 'createInvoice',
		type: "POST",
		data: submitData,
		dataType: 'json',
		success : function(data) {
			if(data.responseMessage == 'success') {
				window.location.replace('ViewAPInvoice?invoiceId=' + data.invoiceId);
			} else if(data.responseMessage == 'error') {
				Loading.hide('loadingMacro');
				bootbox.dialog(data.errorMessage,
					[{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]
					);
			}
		}
	});
}