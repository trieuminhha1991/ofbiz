function getServerError(data) {
    var serverErrorHash = [];
    var serverError = "";
   
    
    if (data._ERROR_MESSAGE_LIST_ != undefined) {
     
        serverErrorHash = data._ERROR_MESSAGE_LIST_;
        jQuery.each(serverErrorHash, function(i, error) {
          if (error != undefined) {
              if (error.message != undefined) {
                  serverError += error.message;
              } else {
                  serverError += error;
              }
            }
        });
    }
    if (data._ERROR_MESSAGE_ != undefined) {
     
        serverError = data._ERROR_MESSAGE_;
    }
    
    return serverError;
}
function getResultOfCreateProduct(data){
	
	$('#alterpopupAddProduct').jqxWindow('close');
	var serverError = getServerError(data);

	if(serverError != ""){
		$("#showError").html(serverError);
		$('#popUpShowError').jqxWindow('open');
		
	}else{
		var productId = data.productId;
		var productName = data.internalName;
		var description = data.description;
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ productId +'</div>';
		$('#jqxgridListProduct').jqxDropDownButton('setContent', dropDownContent);
		$("#addProductName").val(productName);
		$("#addProductDescription").val(description);
	
		
	}
}
function formatcurrency(num, uom){
	decimalseparator = ",";
 	thousandsseparator = ".";
 	currencysymbol = "đ";
 	if(typeof(uom) == "undefined" || uom == null){
 		uom = "${currencyUomId?if_exists}";
 	}
	if(uom == "USD"){
		currencysymbol = "$";
		decimalseparator = ".";
 		thousandsseparator = ",";
	}else if(uom == "EUR"){
		currencysymbol = "€";
		decimalseparator = ".";
 		thousandsseparator = ",";
	}
    var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
    if(str.indexOf(".") > 0) {
        parts = str.split(".");
        str = parts[0];
    }
    str = str.split("").reverse();
    for(var j = 0, len = str.length; j < len; j++) {
        if(str[j] != ",") {
            output.push(str[j]);
            if(i%3 == 0 && j < (len - 1)) {
                output.push(thousandsseparator);
            }
            i++;
        }
    }
    formatted = output.reverse().join("");
    return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
};
