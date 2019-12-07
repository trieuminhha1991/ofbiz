var alterData = null;
var productIds = [];
$(function() {
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		if ((info.step == 1) && (info.direction == "next")) {
			$('#container').empty();
			
	        var selectedRowIndexes = $('#jqxgridQuotationItems').jqxGrid('selectedrowindexes');
			if (selectedRowIndexes.length <= 0) {
				jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseRow);
				return false;
			}
			dataSelected = new Array();
			var count = 0;
			for(var index in selectedRowIndexes) {
				var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', selectedRowIndexes[index]);
				if (typeof(data) != 'undefined' && data != window) {
					var row = {};
					row["productId"] = data.productId;
					row["productCode"] = data.productCode;
					row["productName"] = data.productName;
					row["taxPercentage"] = data.taxPercentage;
					if (data.quantityUomId != undefined) {
						row["quantityUomId"] = data.quantityUomId;
					}
					if (data.listPrice != undefined) {
						row["listPrice"] = data.listPrice;
					} else {
						row["listPrice"] = "";
					}
					dataSelected[count] = row;
					count++;
				}
			}
			
			var sourceSuccessTwo = {
				localdata: dataSelected,
				dataType: "array",
				datafields: dataField
			};
			var dataAdapter = new $.jqx.dataAdapter(sourceSuccessTwo);
            $("#jqxgridProdSelected").jqxGrid({ source: dataAdapter });
		} else if ((info.step == 2) && (info.direction == "previous")) {
			alterData = null;
		}
	}).on('finished', function(e) {
		var form = document.createElement("form");
	    form.setAttribute("method", "POST");
	    form.setAttribute("action", "quotation.pdf");
	    form.setAttribute("target", "_blank");
	    
	    var hiddenField0 = document.createElement("input");
        hiddenField0.setAttribute("type", "hidden");
        hiddenField0.setAttribute("name", "productQuotationId");
        hiddenField0.setAttribute("value", quotationSelected.productQuotationId);
        form.appendChild(hiddenField0);
        
        var hiddenField1 = document.createElement("input");
        hiddenField1.setAttribute("type", "hidden");
        hiddenField1.setAttribute("name", "isPrint");
        hiddenField1.setAttribute("value", "true");
        form.appendChild(hiddenField1);
        
	    var prodSelectedList = new Array();
	    var prodSelectedRows = $('#jqxgridProdSelected').jqxGrid('getrows');
		for (var i = 0; i < prodSelectedRows.length; i++) {
			var itemSelected = prodSelectedRows[i];
			if (itemSelected.productId != undefined) {
				var hiddenField = document.createElement("input");
	            hiddenField.setAttribute("type", "hidden");
	            hiddenField.setAttribute("name", "productId");
	            hiddenField.setAttribute("value", itemSelected.productId);
	            form.appendChild(hiddenField);
			}
		}
	    document.body.appendChild(form);
	    form.submit();
	});
})