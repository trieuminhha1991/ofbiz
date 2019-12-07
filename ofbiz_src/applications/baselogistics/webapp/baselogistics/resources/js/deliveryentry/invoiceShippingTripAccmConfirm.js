$(document).ready(function(){
	OlbShippingTripAccmConfirm.init();
});
var OlbShippingTripAccmConfirm = (function(){

	var init = function(){
		initElement();
		initComplexElement();
		initEvent();
	};
	var initElement = function(){
        $('#invoiceTypeLabel').text(globalVar.formData['invoiceTypeIdLabel']);
        $('#organizationLabel').text(globalVar.formData['organizationIdLabel']);
        $('#customerLabel').text(globalVar.formData['customerIdLabel']);
	};

	var initComplexElement = function(){
	    var total = 0;
        for (var i = globalVar.dataGridInvoiceInfo.length - 1; i >= 0; i--) {
        	var tr = $('<tr class="invoice-item">'
        		+ '<td align="right" valign="top" class="align-center">'
        		+ '<div nowrap="nowrap">' + globalVar.dataGridInvoiceInfo[i].shippingTripId + '</div>'
        		+ '</td>'
        		+ '<td align="" valign="top" class="">'
        		+ '<div nowrap="nowrap">' + formatFullDate(globalVar.dataGridInvoiceInfo[i].startDateTime) + '</div>'
        		+ '</td>'
        		+ '<td align="" valign="top" class="">'
        		+ '<div nowrap="nowrap">' + formatFullDate(globalVar.dataGridInvoiceInfo[i].finishedDateTime) + '</div>'
        		+ '</td>'
        		+ '<td align="right" valign="top" class="align-right">'
        		+ '<div nowrap="nowrap">'+ globalVar.formData.quantity  +'</div>'
        		+ '</td>'
        		+ '<td align="right" valign="top" class="align-right">'
        		+ '<div nowrap="nowrap">' + formatcurrency(globalVar.dataGridInvoiceInfo[i].tripCost, globalVar.formData.currencyUomId) + '</div>'
        		+ '</td>'
        		+ '<td align="right" valign="top" nowrap="nowrap" class="align-right">'
        		+ '<div nowrap="nowrap">'+ globalVar.formData.currencyUomId + '</div>'
        		+ '</td>'
        		+ '<td align="right" valign="top" nowrap="nowrap" class="align-right">'
        		+ '<div nowrap="nowrap">'+formatcurrency(globalVar.dataGridInvoiceInfo[i].tripCost, globalVar.formData.currencyUomId)+'</div>'
        		+ '</td>'
        		+ '</tr>');
        	$('#invoice-item-table tbody').prepend(tr);
        	total += globalVar.dataGridInvoiceInfo[i].tripCost;
        }
        $('#invoice-total').text(formatcurrency(total, globalVar.formData.currencyUomId));

	};
	var initEvent = function(){

	};

    var getFormData = function(){
    	var data = {};
    	var amount = 0;
        for (var i of globalVar.dataGridInvoiceInfo){ amount += i.tripCost;};
        data.amount = amount;
        data.uid = "0";
        data.currencyUomId = globalVar.formData.currencyUomId;
        data.quantity = globalVar.formData.quantity;
        data.description = globalVar.formData.description;
        data.invoiceItemSeqId = null;
        data.invoiceItemTypeId = "PITM_OUTPURSER_COST_SHIP";
        data.productId = "shipping_service";
    	return data;
    }

	return {
		init: init,
		getFormData: getFormData
	};
}());