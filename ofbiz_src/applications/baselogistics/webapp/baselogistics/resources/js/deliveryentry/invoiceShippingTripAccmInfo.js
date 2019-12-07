$(document).ready(function(){
	OlbShippingTripAccmInfo.init();
});
var OlbShippingTripAccmInfo = (function(){
	var grid = $('#jqxGridInvoiceAccmInfo');
	var invoiceAccmInfoDDL;

	var init = function(){
		initElement();
		initComplexElement();
		initEvent();
	};
	var initElement = function(){
	    $("#invoiceDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px' , width: '96%', theme: THEME});
	    $('#invoiceDate ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
        $("#dueDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px' , width: '96%', theme: THEME});
        $("#invoiceTypeId").jqxDropDownList({disabled: true, width: '98%', height: 25, theme: 'olbius', dropDownHorizontalAlignment: 'right', source: globalVar.invoiceTypeData, valueMember: "invoiceTypeId", displayMember: "description", selectedIndex: 0});
        $("#shipperIdInfo").jqxDropDownList({disabled: true, width: '98%', height: 25, theme: 'olbius', dropDownHorizontalAlignment: 'right', source: globalVar.shipperSelected, valueMember: "shipperId", displayMember: "description", selectedIndex: 0});
        $("#partyIdInfo").jqxDropDownList({disabled: true, width: '98%', height: 25, theme: 'olbius', dropDownHorizontalAlignment: 'right', source: globalVar.partyInfo, valueMember: "partyId", displayMember: "partyName", selectedIndex: 0});

	};

	var datafieldsSelected = [	{ name: 'shippingTripId', type: 'string' },
                            	{ name: 'startDateTime', type: 'date', other: 'Timestamp'},
                            	{ name: 'finishedDateTime', type: 'date', other: 'Timestamp'},
                            	{ name: 'tripCost', type: 'number'},
                            	{ name: 'costCustomerPaid', type: 'number'},
                            	{ name: 'statusId', type: 'string'}];
    var columnsSelected = [	{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                          		groupable: false, draggable: false, resizable: false,
                          		datafield: '', columntype: 'number', width: 50,
                          		cellsrenderer: function (row, column, value) {
                          			return '<span style=margin:4px;>' + (value + 1) + '</span>';
                          		}
                          	},
                          	{ text: uiLabelMap.DeliveryEntryCode, datafield: 'shippingTripId', width: 300, editable: false, pinned: true,
                          	cellsrenderer: function(row, column, value){
                          		return '<span><a href=\"shippingTripDetail?shippingTripId=' + value +'\">' + value + '</a></span>';
                          	}
                          	},
                          	{ text: uiLabelMap.BLDstartDateTime, datafield: 'startDateTime', cellsalign: 'right', width: 200, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range'},
                          	{ text: uiLabelMap.BLDfinishedDateTime, datafield: 'finishedDateTime', cellsalign: 'right', width: 200, cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range'},
                          	{ text: uiLabelMap.Status, datafield: 'statusId', width: 200, editable: false, columntype: 'dropdownlist',
                          	cellsrenderer: function(row, column, value){
                          		for(var i = 0; i < statusDataDE.length; i++){
                          			if(statusDataDE[i].statusId == value){
                          				return '<span title=' + value + '>' + statusDataDE[i].description + '</span>';
                          			}
                          		}
                          		return value;
                          	}
                          	},
                          	{ text: uiLabelMap.BLDTripCost, datafield: 'tripCost', width: 200, editable: false, },
                          	{ text: uiLabelMap.BLDCostCustomerPaid, datafield: 'costCustomerPaid', width: 250, editable: false, }];

	var initComplexElement = function(){
	     var configInvoiceAccmInfo = {
         	showdefaultloadelement: false,
         	autoshowloadelement: false,
         	dropDownHorizontalAlignment: 'right',
         	datafields: datafieldsSelected,
         	columns: columnsSelected,
         	useUrl: false,
         	clearfilteringbutton: false,
         	editable: true,
         	alternativeAddPopup: 'popupCreateNewInvoice',
         	pageable: true,
         	pagesize: 15,
         	showtoolbar: false,
         	editmode: 'click',
         	selectionmode: 'multiplecellsadvanced',
         	width: '100%',
         	bindresize: true,
         	groupable: false,
         	showtoolbar: true,
         	showdefaultloadelement: true,
         	autoshowloadelement: true,
         	virtualmode: false
         };
         invoiceAccmInfoDDL = new OlbGrid($("#jqxGridInvoiceAccmInfo"), null, configInvoiceAccmInfo, []);

	};
	var initEvent = function(){
	    $('#invoiceDate').on('change', function (event)
        {
            var jsDate = event.args.date;
            $('#dueDate ').jqxDateTimeInput('setMinDate', formatMinDate(jsDate));

        });

	};

    function formatMinDate (value) {
    	if (value) {
    		var dateStr = "";
    		dateStr += addZero(value.getFullYear()) + '/';
    		dateStr += addZero(value.getMonth()+1) + '/';
    		dateStr += addZero(value.getDate()) + ' ';
    		return dateStr;
    	} else {
    		return "";
    	}
    };

    var getFormData = function(){
        var data = {};
        data['invoiceTypeIdLabel'] = $('#invoiceTypeId').jqxDropDownList('getSelectedItem').label;
        data['organizationIdLabel'] = $('#shipperIdInfo').jqxDropDownList('getSelectedItem').label;
        data['customerIdLabel'] = $('#partyIdInfo').jqxDropDownList('getSelectedItem').label;
        data['invoiceTypeId'] =  $('#invoiceTypeId').val();
        data['description'] = $('#description').val();
        data['currencyUomId'] = "VND";
        data['dueDate'] = ($('#dueDate').jqxDateTimeInput('getDate')).getTime();
        data['invoiceDate'] = ($('#invoiceDate').jqxDateTimeInput('getDate')).getTime();
        data['organizationId'] = $('#shipperIdInfo').val();
        data['customerId'] = $('#partyIdInfo').val();
        data['quantity'] = "1";
        if (invoiceTypeId == 'PURCHASE_INVOICE') {
        	data['glAccountTypeId'] = $('#glAccountTypeId').attr('data-value');
        }
        return data;
    }

    var getShippingTripSelected = function(){
            var data = [];
            for (var i of globalVar.dataGridInvoiceInfo){data.push(i.shippingTripId);};
            return data;
    }

	return {
		init: init,
		getFormData: getFormData,
		getShippingTripSelected: getShippingTripSelected
	};
}());