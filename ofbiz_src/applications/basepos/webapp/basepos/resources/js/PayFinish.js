function getDataForPrint(){
	if (locale == 'vi_VN'){
		locale = 'vi';
	} else {
		locale = 'en';
	}
	var rows = $('#showCartJqxgrid').jqxGrid('getrows');
	var eachTr = "";
	var grandTotal = $('#grandTotalCartInput').val();
	grandTotal = parseFloat(grandTotal).toLocaleString(locale); 
	var cartDiscountTotal = $('#discountWholeCart').val();
	cartDiscountTotal = cartDiscountTotal.split(",")[0];
	var totalSalesTax = $('#totalTaxInput').val();
	totalSalesTax = parseFloat(totalSalesTax).toLocaleString(locale); 
	var totalDue = $('#totalDue').val();
	totalDue = parseFloat(totalDue).toLocaleString(locale); 
	var amountCash = $('#amountCash').val();
	amountCash = amountCash.split(",")[0];
	var paybackCash = $('#paybackCash').val();
	paybackCash = paybackCash.split(",")[0];
	var partyName = $('#partyName').text();
	var partyAddress = $('#partyAddress').text();
	var partyMobile = $('#partyMobile').text();
	var customerName = "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-size:" + infoFontSize + "pt\">" + BPOSCustomer + ":</span></td>" +
					   "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-weight:bold;font-size:" + infoFontSize + "pt\">" + partyName + "</span></td>";
	var customerAddress = "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-size:" + infoFontSize + "pt\">" + BPOSAddress + ":</span></td>" +
						  "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-weight:bold;font-size:" + infoFontSize + "pt\">" + partyAddress + "</span></td>";
	var customerPhone = "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-size:" + infoFontSize + "pt\">" + BPOSMobile + ":</span></td>" +
						"<td colspan=\"6\" rowspan=\"1\"><span style=\"font-weight:bold;font-size:" + infoFontSize + "pt\">" + partyMobile + "</span></td>";
	$('#customerName').html(customerName);
	$('#customerAddress').html(customerAddress);
	$('#customerPhone').html(customerPhone);
	for(var i = 0; i < rows.length; i++){
		var index = i + 1;
        var row = rows[i];
        var productId = row.productId;
        var productName = row.productName;
        var quantityProduct = row.quantityProduct;
        var price = row.price.toLocaleString(locale);
        var discount = 0;
        if(row.discount){
        	discount = row.discount.toLocaleString(locale);
        }
        var amount = row.amount.toLocaleString(locale);
         
        eachTr += "<tr>"+
						"<td style=\"border:1px solid #CCC;\"><span style=\"text-align:center;display:block;padding:0 2px;font-size:" + contentFontSize + "pt\">" + index +"</span></td>"+
						"<td style=\"border:1px solid #CCC;\"><span style=\"float:left;padding:0 2px;text-align:justify; font-size:" + contentFontSize + "pt\">" +productName + "</span></td>"+
						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;padding:0 2px;font-size:" + contentFontSize + "pt\">" + price + "</span></td>"+
						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;padding:0 2px;font-size:" + contentFontSize + "pt\">" + discount+ "</span></td>"+
						"<td style=\"border:1px solid #CCC;\"><span style=\"text-align:center;display:block;padding:0 2px;font-size:" + contentFontSize + "pt\">" + quantityProduct +"</span></td>"+
						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;padding:0 2px;font-size:" + contentFontSize + "pt\">"+ amount + "</span></td>"+
					"</tr>";
    }
	
	eachTr += "<tr>" +
				"<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSGrandTotal + "</span></td>"+
				"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + grandTotal+ "</span></td>"+
			"</tr>" +
				"<tr>" +
					"<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSDiscount+ "</span></td>" +
					"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + cartDiscountTotal +"</span></td>" +
				"</tr>";
	if(showPricesWithVatTax){
		eachTr += "<tr>" +
						"<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSTotalSalesTax+ "</span></td>" +
						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + totalSalesTax +"</span></td>" +
				  "</tr>";	
					
	}
	eachTr += "<tr>"+ 
				 "<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSTotalPay+ "</span></td>" +
				 "<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + totalDue+ "</span></td>"+
			  "</tr>";
	$('#bodyPrint').html(eachTr);		
}

function printWebPOSAgain(){
	$("#PrintOrder").printArea();
	return false;
}

function printWebPOS(){
	getDataForPrint();
	$("#PrintOrder").printArea();
	return false;
}