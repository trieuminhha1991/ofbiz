$(function(){
	BarCodeTemplateObj.init();
});
var BarCodeTemplateObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				var resultValidate = !BarCodeInitObj.getValidator().validate();
				if(resultValidate) return false;
				if (listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
					return false;
				}
	    		for (var i = 0; i < listProductSelected.length; i ++){
	    			var data = listProductSelected[i];
	    			if ($("#page105x22").jqxCheckBox('checked') == true || $("#page70x22").jqxCheckBox('checked') == true){
	    				data.height = glHeight;
	    				data.width = glWidth;
	    			} else {
	    				if (!data.height){
		    				if (glHeight){
		    					data.height = glHeight;
		    				}
		    			}
		    			if (!data.width){
		    				if (glWidth){
		    					data.width = glWidth;
		    				}
		    			}
	    			}
	    		}
	    		showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSurePrint, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		finishPrintBarCodeProducts();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
		
	function showConfirmPage(){
		var txt = "";
		if ($("#includeProductId").jqxCheckBox('checked') == true){
			txt = txt + uiLabelMap.ProductId + '; ';
		}
		if ($("#includeProductName").jqxCheckBox('checked') == true){
			txt = txt + uiLabelMap.ProductName + '; ';
		}
		if ($("#includeUnitPrice").jqxCheckBox('checked') == true){
			txt = txt + uiLabelMap.UnitPrice + '; ';
		}
		if ($("#includeCompanyName").jqxCheckBox('checked') == true){
			txt = txt + uiLabelMap.CompanyName + '; ';
		}
		$("#displayInfo").text(txt);
		
		var sz = "";
		sz = sz + uiLabelMap.Width + ': ' + $('#pageWidth').jqxNumberInput('val') + ' (cm) - ';
		sz = sz + uiLabelMap.Height + ': ' + $('#pageHeight').jqxNumberInput('val') + ' (cm)';
		$("#pageSize").text(sz);
		
		var tmpSource = $("#jqxgridProductBarCodeConfirm").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listProductSelected;
			$("#jqxgridProductBarCodeConfirm").jqxGrid('source', tmpSource);
		}
	}
	
	function finishPrintBarCodeProducts(){
		var listProducts = [];
		if (listProductSelected != undefined && listProductSelected.length > 0){
			var includeProductId = "N";
			var includeProductName = "N";
			var includeUnitPrice = "N";
			var includeCompanyName = "N";
			if ($("#includeProductId").jqxCheckBox('checked') == true){
				includeProductId = "Y";
			}
			if ($("#includeProductName").jqxCheckBox('checked') == true){
				includeProductName = "Y";
			}
			if ($("#includeUnitPrice").jqxCheckBox('checked') == true){
				includeUnitPrice = "Y";
			}
			if ($("#includeCompanyName").jqxCheckBox('checked') == true){
				includeCompanyName = "Y";
			}
        	var listProductTmps = [];
        	var tmp = null;
        	for (var i = 0; i < listProductSelected.length; i ++){
        		var data = listProductSelected[i];
        		if (data.quantity != 2 && tmp == null){
        			tmp = {};
        			tmp["productId"] = data.productId;
        			tmp["quantity"] = data.quantity;
        			tmp["height"] = data.height;
        			tmp["width"] = data.width;
        		} else {
        			var row = {};
            		row["productId"] = data.productId;
            		row["quantity"] = data.quantity;
            		row["height"] = data.height;
            		row["width"] = data.width;
            		listProductTmps.push(row);
        		}
        	}
        	if (tmp != null){
        		listProductTmps.push(tmp);
        	}
        	var listProductStrs = JSON.stringify(listProductTmps);
			
			var form = document.createElement("form");
 	        form.setAttribute("method", "post");
 	        form.setAttribute("action", "printBarCodeProducts");

 	        form.setAttribute("target", "view");

 	        var hiddenFieldProductList = document.createElement("input");
 	        hiddenFieldProductList.setAttribute("type", "hidden");
 	        hiddenFieldProductList.setAttribute("name", "productIds");
 	        hiddenFieldProductList.setAttribute("id", "productIds");
 	        hiddenFieldProductList.setAttribute("value", listProductStrs);
 	        form.appendChild(hiddenFieldProductList);
 	        
 	        var hiddenFieldHeight = document.createElement("input");
 	        hiddenFieldHeight.setAttribute("type", "hidden");
 	        hiddenFieldHeight.setAttribute("name", "pdfHeight");
 	        hiddenFieldHeight.setAttribute("id", "pdfHeight");
 	        hiddenFieldHeight.setAttribute("value", $("#pageHeight").jqxNumberInput('val'));
 	        form.appendChild(hiddenFieldHeight);
 	        
 	        var hiddenFieldWidth = document.createElement("input");
 	        hiddenFieldWidth.setAttribute("type", "hidden");
 	        hiddenFieldWidth.setAttribute("name", "pdfWidth");
 	        hiddenFieldWidth.setAttribute("id", "pdfWidth");
 	        hiddenFieldWidth.setAttribute("value", $("#pageWidth").jqxNumberInput('val'));
 	        form.appendChild(hiddenFieldWidth);
 	        
 	        var hiddenFieldWidth = document.createElement("input");
 	        hiddenFieldWidth.setAttribute("type", "hidden");
 	        hiddenFieldWidth.setAttribute("name", "includeProductId");
 	        hiddenFieldWidth.setAttribute("id", "includeProductId");
 	        hiddenFieldWidth.setAttribute("value", includeProductId);
 	        form.appendChild(hiddenFieldWidth);
 	        
 	        var hiddenFieldWidth = document.createElement("input");
 	        hiddenFieldWidth.setAttribute("type", "hidden");
 	        hiddenFieldWidth.setAttribute("name", "includeProductName");
 	        hiddenFieldWidth.setAttribute("id", "includeProductName");
 	        hiddenFieldWidth.setAttribute("value", includeProductName);
 	        form.appendChild(hiddenFieldWidth);
 	        
 	        var hiddenFieldWidth = document.createElement("input");
 	        hiddenFieldWidth.setAttribute("type", "hidden");
 	        hiddenFieldWidth.setAttribute("name", "includeUnitPrice");
 	        hiddenFieldWidth.setAttribute("id", "includeUnitPrice");
 	        hiddenFieldWidth.setAttribute("value", includeUnitPrice);
 	        form.appendChild(hiddenFieldWidth);
 	        
 	        var hiddenFieldWidth = document.createElement("input");
 	        hiddenFieldWidth.setAttribute("type", "hidden");
 	        hiddenFieldWidth.setAttribute("name", "includeCompanyName");
 	        hiddenFieldWidth.setAttribute("id", "includeCompanyName");
 	        hiddenFieldWidth.setAttribute("value", includeCompanyName);
 	        form.appendChild(hiddenFieldWidth);
 	        
 	        document.body.appendChild(form);
 	       
 	        window.open('', 'view');
 	        form.submit();
		}
	}
	
	var initValidateForm = function(){
		
	};
	var reloadPages = function(){
		window.location.reload();
	};
	
	return {
		init: init,
		reloadPages: reloadPages,
	}
}());