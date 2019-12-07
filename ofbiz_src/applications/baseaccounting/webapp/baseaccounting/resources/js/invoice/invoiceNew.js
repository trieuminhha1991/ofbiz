var _data = [];
var createInvoice = function() {
	var itemData = _data.length > 0 ? _data: $("#newInvoiceItemGrid").jqxGrid('getrows');
	var formData = OLBCommonInfo.prototype.getFormData();
	var submitData = {};
	submitData['invoiceTypeId'] =  formData['invoiceTypeId'];
	submitData['description'] = formData['description'];
	submitData['dueDate'] = formData['dueDate'];
	submitData['invoiceDate'] = formData['invoiceDate'];
	submitData['organizationId'] = formData['organizationId'];
	submitData['currencyUomId'] = formData['currencyUomId'];
	submitData['customerId'] = formData['customerId'];
    submitData['listItems'] = JSON.stringify(itemData);
	if (formData['invoiceTypeId'] === 'PURCHASE_INVOICE' || formData['invoiceTypeId'] === 'SALES_INVOICE' || formData['invoiceTypeId'] === 'COMMISSION_INVOICE') {
		submitData['glAccountTypeId'] = formData['glAccountTypeId'];
	}
	if(formData['invoiceTypeId'] === 'IMPORT_INVOICE') {
        submitData['conversionFactor'] = formData['conversionFactor'];
    }
	var invoiceTaxInfo = invoicePartyInfo.getSubmitData();
	if (invoiceTaxInfo.hasOwnProperty("partyName")) {
		submitData.invoiceTaxInfo = JSON.stringify(invoiceTaxInfo);
	}
	//Send Ajax Request
	Loading.show('loadingMacro');
	$.ajax({
		url: 'createInvoice',
		type: "POST",
		data: submitData,
		dataType: 'json',
		success : function(data) {
			if(data.responseMessage == 'success') {
				if (globalVar.businessType == 'AP') {
					window.location.replace('ViewAPInvoice?invoiceId=' + data.invoiceId);
				} else {
					window.location.replace('ViewARInvoice?invoiceId=' + data.invoiceId);
				}
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
};

$(document).on('ready', function() {
	$.jqx.theme = 'olbius';
	OLBCommonInfo.prototype.init();
	OLBNewInvItem.prototype.init();
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
		if(info.step == 1 && (info.direction == "next")) {
			var result = OLBCommonInfo.prototype.validateForm();
			if (!result) {
				return false;
			}
			addNewIITObj.prepareData();//addNewIITObj is defined in invoiceNewStep2.js
			
		} else if(info.step == 2 && (info.direction == "next")) {
			var itemData = OLBNewInvItem.prototype.getData();
			if (itemData.length == 0) {
 				bootbox.dialog(uiLabelMap.BACCNeedEnterInvItems,
 						[{
 							"label" : uiLabelMap.CommonClose,
 							"class" : "btn-danger btn-small icon-remove open-sans",
 						}]		
 				);
 				return false;
 			}
			var formData = OLBCommonInfo.prototype.getFormData();
			var submitData = {};
 			submitData['invoiceTypeId'] = formData['invoiceTypeId'];
 			submitData['partyIdFrom'] = formData['organizationId'];
 			submitData['partyId'] = formData['customerId'];
 			submitData['listItems'] = JSON.stringify(itemData);
 			var addtaxFlag = $('input[name=switch-field-1]').is(":checked");
 			//Send Ajax Request
 			if(addtaxFlag){
 				$.ajax({
 					url: 'addTaxOlbius',
 					type: "POST",
 					data: submitData,
 					dataType: 'json',
 					async: false,
 					success: function(data) {
 						if (!data._ERROR_MESSAGE_ && !data.errorMessage) {
 							_data = data.listItems;
 							rendererTableItem(data);
 							addtax = true;
 						} else {
 							var errorMsg = data._ERROR_MESSAGE_ ? data._ERROR_MESSAGE_ : (data.errorMessage ? data.errorMessage : '');
 							_data = [];
 							bootbox.dialog(errorMsg,
 									[{
 										"label" : uiLabelMap.CommonClose,
 										"class" : "btn-danger btn-small icon-remove open-sans",
 									}]		
 							);
 							addtax = false;
 						}
 					}
 				});
 				if (!addtax) {
 					return false;
 				}
 			} else {
 				rendererTableItem(undefined);
 				_data = OLBNewInvItem.prototype.getData();
 			}
			
			function rendererTableItem(data){
				if (data === undefined) {
					itemData = OLBNewInvItem.prototype.getData();
				} else if (data.hasOwnProperty('listItems')) {
					itemData = data['listItems'];
				} else return;
				$('#invoiceTypeLabel').text(formData['invoiceTypeIdLabel']);
				$('#organizationLabel').text(formData['organizationIdLabel']);
				$('#customerLabel').text(formData['customerIdLabel']);
				var invoiceTaxInfo = invoicePartyInfo.getSubmitData();//invoicePartyInfo is defined in invoiceNewPartyInfo.js
				if ($("#invoiceTaxInfoTr").length > 0) {
					$("#invoiceTaxInfoTr").remove();
				}
				if (invoiceTaxInfo.hasOwnProperty("partyName")) {
					var invoiceTaxInfoTr = $("<tr id='invoiceTaxInfoTr'></tr>");
					var invoiceTaxInfoColumn1 = $("<td align='right' valign='top' width='25%'><b>" + uiLabelMap.AdditionalInformation + "</b></td>");
					var invoiceTaxInfoColumn2 = $("<td valign='top' width='70%'></td>");
					if(globalVar.businessType == "AP") {
						invoiceTaxInfoColumn2.append("<div> -" + uiLabelMap.SellerName + ": " + invoiceTaxInfo.partyName + "</div>");
					} else {
						invoiceTaxInfoColumn2.append("<div> - " + uiLabelMap.BuyerName + ": " + invoiceTaxInfo.partyName + "</div>");
					}
					invoiceTaxInfoColumn2.append("<div> - " + uiLabelMap.BACCTaxCode + ": " + invoiceTaxInfo.taxCode + "</div>");
					var address = invoiceTaxInfo.address + ", " + invoiceTaxInfo.stateGeoName + ", " + invoiceTaxInfo.countryGeoName;
					if (invoiceTaxInfo.hasOwnProperty("phoneNbr")) {
						address += " (" + uiLabelMap.BSAbbPhone + ": " + invoiceTaxInfo.phoneNbr + ")";
					}
					invoiceTaxInfoColumn2.append("<div> - " + uiLabelMap.CommonAddress1 + ": " + address + "</div>");
					invoiceTaxInfoTr.append(invoiceTaxInfoColumn1);
					invoiceTaxInfoTr.append(invoiceTaxInfoColumn2);
					$("#generalInvTable").append(invoiceTaxInfoTr);
				} 
				var total = 0;
				$('.invoice-item').remove();
				var currencyUomId;
				for (var i = itemData.length - 1; i >= 0; i--) {
				    currencyUomId = itemData[i].currencyUomId;
					if(OLBNewInvItem.prototype.attr.invoiceType.val() == 'PAYROL_INVOICE'){
						itemData[i].quantity = 1;
					}
					var _productName = itemData[i].productName;
					var tr = $('<tr class="invoice-item">'
 								+ '<td align="right" valign="top" class="align-center">' 
			                  	+ '<div nowrap="nowrap">' + itemData[i].invoiceItemSeqId + '</div>' 
					            + '</td>'
		 						+ '<td align="" valign="top" class="">' 
			                  	+ '<div nowrap="nowrap">' + itemData[i].invoiceItemTypeDesc + '</div>' 
					            + '</td>' 
		 						+ '<td align="" valign="top" class="">' 
					            + '<div nowrap="nowrap">' + (_productName != undefined ? _productName : '')   + '</div>' 
					            + '</td>' 
					            + '<td align="right" valign="top" class="align-right">'  
					            + '<div nowrap="nowrap">' + itemData[i].quantity + '</div>'  
					            + '</td>'  
					            + '<td align="right" valign="top" class="align-right">'
			                  	+ '<div nowrap="nowrap">' + formatcurrency(itemData[i].amount, itemData[i].currencyUomId) + '</div>'
				                + '</td>'
				                + '<td align="right" valign="top" nowrap="nowrap" class="align-right">'
			                  	+ '<div nowrap="nowrap">' + (itemData[i].description != undefined ? itemData[i].description : "") + '</div>'
				                + '</td>'
				                + '<td align="right" valign="top" nowrap="nowrap" class="align-right">'
			                  	+ '<div nowrap="nowrap">' + formatcurrency(accutils.multi(itemData[i].quantity, itemData[i].amount), itemData[i].currencyUomId) + '</div>'
				                + '</td>'
		 						+ '</tr>');
					$('#invoice-item-table tbody').prepend(tr);
					total += accutils.multi(itemData[i].quantity, itemData[i].amount);
				}
				$('#invoice-total').text(formatcurrency(total, currencyUomId));
			}
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
	}).on('stepclick', function(e){
		//return false;//prevent clicking on steps
	});
});