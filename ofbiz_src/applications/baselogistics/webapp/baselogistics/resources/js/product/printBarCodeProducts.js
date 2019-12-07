$(function(){
	BarCodeInitObj.init();
});
var BarCodeInitObj = (function() {
	var validatorVAL;
	var init = function() {
		disableEdit = true;
		listProductSelected = [];
		glWidth = 30;
		glHeight = 20;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		loadProductToPrints();
	};
	var initInputs = function() {
		$("#pageWidth").jqxNumberInput({ width: 200, height: 25, min: 0,  spinButtons: true, inputMode: 'simple', theme: theme});
		$("#pageWidth").jqxNumberInput('val', '21');
		$("#pageHeight").jqxNumberInput({ width: 200, height: 25, min: 0,  spinButtons: true, inputMode: 'simple', theme: theme});
		$("#pageHeight").jqxNumberInput('val', '29.7');
		
		$("#includeProductId").jqxCheckBox({ width: 50, height: 25, checked: true, locked: false, theme: theme});
		$("#includeUnitPrice").jqxCheckBox({ width: 50, height: 25, checked: true, theme: theme});
		$("#includeProductName").jqxCheckBox({ width: 50, height: 25, checked: true, theme: theme});
		$("#includeCompanyName").jqxCheckBox({ width: 50, height: 25, checked: false, theme: theme});
		
		$("#page105x22").jqxCheckBox({ width: 50, height: 25, checked: false, theme: theme});
		$("#page70x22").jqxCheckBox({ width: 50, height: 25, checked: false, theme: theme});
		
		$('#pageSizeId').jqxDropDownList({selectedIndex: 1, placeHolder: uiLabelMap.PleaseSelectTitle, width: 200, dropDownHeight: 150, source: pageSizeData, theme: theme, displayMember: 'value', valueMember: 'value',});
	};
	var initElementComplex = function() {
	};
	
	var initEvents = function() {
		$("#gridProductToPrint").on("cellendedit", function (event) {
	    	var args = event.args;
	    	var rowBoundIndex = args.rowindex;
	    	var value = args.value;
	    	if (args.datafield == "quantity") {
	    		if (value > 0) {
	    			$('#gridProductToPrint').jqxGrid('selectrow', rowBoundIndex);
	    			var rowData = $('#gridProductToPrint').jqxGrid('getrowdata', rowBoundIndex);
	    			rowData.quantity = value;
	    			if (listProductSelected.length > 0) {
						for (var i = 0; i < listProductSelected.length; i ++){
							if (rowData.productId == listProductSelected[i].productId){
								listProductSelected.splice(i, 1);
								break;
							}
						}
					} 
					listProductSelected.push(rowData);
	    		} else {
	    			$('#gridProductToPrint').jqxGrid('unselectrow', rowBoundIndex);
	    		}
	    	}
		});
		
		$("#gridProductToPrint").on("rowselect", function (event) {
			var args = event.args;
			var rowData = args.row;
			var rowBoundIndex = args.rowindex;
			if (rowData.quantity > 0){
				if (listProductSelected.length > 0) {
					for (var i = 0; i < listProductSelected.length; i ++){
						if (rowData.productId == listProductSelected[i].productId){
							listProductSelected.splice(i, 1);
							break;
						}
					}
				}
				listProductSelected.push(rowData);
			} else {
				$('#gridProductToPrint').jqxGrid('begincelledit', rowBoundIndex, "quantity");
			}
		});
		
		$("#gridProductToPrint").on("rowunselect", function (event) {
			var args = event.args;
			var rowData = args.row;
			for (var i = 0; i < listProductSelected.length; i ++){
				if (rowData.productId == listProductSelected[i].productId){
					listProductSelected.splice(i, 1);
					break;
				}
			}
		});
		
		$("#page105x22").on('change', function (event) {
			Loading.show('loadingMacro');
        	setTimeout(function(){
			    var checked = event.args.checked;
			    if (checked == false){
			    	if ($("#page70x22").jqxCheckBox('checked') == false){
			    		disableEdit = true;
			    		$('#pageSizeId').jqxDropDownList({disabled: false});
				    	$('#pageWidth').jqxNumberInput({disabled: false});
				    	$('#pageHeight').jqxNumberInput({disabled: false});
				    	glWidth = 30;
				    	glHeight = 20;
				    	var size = $('#pageSizeId').jqxDropDownList('val');
				    	if (size == 'A3'){
							$("#pageWidth").jqxNumberInput('val', '29.7');
						} else if (size == 'A4'){
							$("#pageWidth").jqxNumberInput('val', '21');
						} else if (size == 'A5'){
							$("#pageWidth").jqxNumberInput('val', '14.8');
						}
			    	}
			    } else {
			    	disableEdit = false;
			    	glWidth = 35;
			    	glHeight = 22;
			    	$('#pageSizeId').jqxDropDownList({disabled: true});
			    	$('#pageWidth').jqxNumberInput({disabled: true});
			    	$('#pageHeight').jqxNumberInput({disabled: true});
			    	$('#pageWidth').jqxNumberInput('val', 10.5);
			    	$("#page70x22").jqxCheckBox({checked: false});
			    }
			    $('#gridProductToPrint').jqxGrid('updatebounddata');
			    Loading.hide('loadingMacro');
        	}, 300);
		});
		$("#page70x22").on('change', function (event) { 
			Loading.show('loadingMacro');
        	setTimeout(function(){
			    var checked = event.args.checked;
			    if (checked == false){
			    	if ($("#page105x22").jqxCheckBox('checked') == false){
			    		disableEdit = true;
			    		glWidth = 30;
				    	glHeight = 20;
				    	$('#pageSizeId').jqxDropDownList({disabled: false});
				    	$('#pageWidth').jqxNumberInput({disabled: false});
				    	$('#pageHeight').jqxNumberInput({disabled: false});
				    	var size = $('#pageSizeId').jqxDropDownList('val');
				    	if (size == 'A3'){
							$("#pageWidth").jqxNumberInput('val', '29.7');
						} else if (size == 'A4'){
							$("#pageWidth").jqxNumberInput('val', '21');
						} else if (size == 'A5'){
							$("#pageWidth").jqxNumberInput('val', '14.8');
						}
			    	}
			    } else {
			    	disableEdit = false;
			    	glWidth = 35;
			    	glHeight = 22;
			    	$('#pageSizeId').jqxDropDownList({disabled: true});
			    	$('#pageWidth').jqxNumberInput({disabled: true});
			    	$('#pageHeight').jqxNumberInput({disabled: true});
			    	$('#pageWidth').jqxNumberInput('val', 7);
			    	$("#page105x22").jqxCheckBox({checked: false});
			    }
			    $('#gridProductToPrint').jqxGrid('updatebounddata');
			    Loading.hide('loadingMacro');
        	}, 300);
		});
		$('#pageSizeId').on('change', function (event){
			var size = $('#pageSizeId').jqxDropDownList('val');
			if (size == 'A3'){
				$("#pageWidth").jqxNumberInput('val', '29.7');
				$("#pageHeight").jqxNumberInput('val', '42.0');
			} else if (size == 'A4'){
				$("#pageWidth").jqxNumberInput('val', '21');
				$("#pageHeight").jqxNumberInput('val', '29.7');
			} else if (size == 'A5'){
				$("#pageWidth").jqxNumberInput('val', '14.8');
				$("#pageHeight").jqxNumberInput('val', '21');
			} else if (size == 'A5'){
				$("#pageWidth").jqxNumberInput('val', '14.8');
				$("#pageHeight").jqxNumberInput('val', '21');
			}
		});
	};
	
	var initValidateForm = function(){
		var extendRules = [
               {input: '#pageWidth', message: uiLabelMap.ValueMustBeGreaterThanZero, action: 'valueChanged', 
					rule: function(input, commit){
						if ($(input).val() <=0){
							return false;
						}
						return true;
					}
				},
				{input: '#pageHeight', message: uiLabelMap.ValueMustBeGreaterThanZero, action: 'valueChanged', 
					rule: function(input, commit){
						if ($(input).val() <=0){
							return false;
						}
						return true;
					}
				},
          ];
   		var mapRules = [
           ];
   		validatorVAL = new OlbValidator($('#barCodeInfo'), mapRules, extendRules, {position: 'right'});
	};
	
	function loadProductToPrints(){
		var  datafields =[
         	{ name: 'productId', type: 'string' },
         	{ name: 'productCode', type: 'string' },
         	{ name: 'productName', type: 'string' },
         	{ name: 'quantity', type: 'number' },
         	{ name: 'width', type: 'number' },
         	{ name: 'height', type: 'number' },
     	];
 
        var columns = [	
			{dataField: 'productId', width: 150, editable: false, hidden: true,},
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, columntype: 'dropdownlist', pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 200, editable: false, columntype: 'dropdownlist', pinned: false,},
			{ text: uiLabelMap.Quantity, dataField: 'quantity', columntype: 'numberinput', width: 150, editable: true, filterable: false,
				cellsrenderer: function(row, column, value){
					if (!value){
						var data = $('#gridProductToPrint').jqxGrid('getrowdata', row);
						for (var i=0; i<listProductSelected.length; i++){
							if (listProductSelected[i].productId == data.productId && listProductSelected[i].quantity > 0) {
								return '<div class="focus-color align-right">' + formatnumber(listProductSelected[i].quantity) + '</div>';
							}
						}
					}
					return '<div class="focus-color align-right">' + formatnumber(value) + '</div>';
				},
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
			        editor.jqxNumberInput({ decimalDigits: 0});
			        var data = $('#gridProductToPrint').jqxGrid('getrowdata', row);
			        if (data.quantity){
			        	editor.jqxNumberInput('val', data.quantity);
			        }
			    },
			    validation: function (cell, value) {
		        	if (value < 0){
		        		return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
		        	} else {
		        		return true;
		        	}
			    },
			},
			{ text: uiLabelMap.BarcodeWidth + ' (mm)', dataField: 'width', columntype: 'numberinput', width: 150, editable: disableEdit, filterable: false,
				cellsrenderer: function(row, column, value){
					if (!value){
						if (glWidth) {
							if(disableEdit == true) {
								return '<div class="align-right focus-color" title="'+uiLabelMap.DefaultConfig+'">' + formatnumber(glWidth) + '</div>';
							} else {
								return '<div class="align-right" title="'+uiLabelMap.DefaultConfig+'">' + formatnumber(glWidth) + '</div>';
							}
						} 
					}
					return '<div class="align-right focus-color">' + formatnumber(value) + '</div>';
				},
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
			        editor.jqxNumberInput({});
			        var data = $('#gridProductToPrint').jqxGrid('getrowdata', row);
			        if (data.width){
			        	editor.jqxNumberInput('val', data.width);
			        } else if (glWidth){
			        	editor.jqxNumberInput('val', glWidth);
			        }
			    },
			    validation: function (cell, value) {
		        	if (value <= 0){
		        		return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
		        	} else {
		        		return true;
		        	}
			    },
			    cellbeginedit: function (row, datafield, columntype) {
					 if (disableEdit == false){
						 return false;
					 }
					 return true;
			    },
			},
			{ text: uiLabelMap.BarcodeHeight + ' (mm)', dataField: 'height', columntype: 'numberinput', width: 150, editable: disableEdit, filterable: false,
				cellsrenderer: function(row, column, value){
					if (!value){
						if (glHeight) {
							if(disableEdit == true) {
								return '<div class="align-right focus-color" title="'+uiLabelMap.DefaultConfig+'">' + formatnumber(glHeight) + '</div>';
							} else {
								return '<div class="align-right" title="'+uiLabelMap.DefaultConfig+'">' + formatnumber(glHeight) + '</div>';
							}
						}
					}
					return '<div class="align-right focus-color">' + formatnumber(value) + '</div>';
				},
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
			        editor.jqxNumberInput({});
			        var data = $('#gridProductToPrint').jqxGrid('getrowdata', row);
			        if (data.height){
			        	editor.jqxNumberInput('val', data.height);
			        } else if (glHeight){
			        	editor.jqxNumberInput('val', glHeight);
			        }
			    },
			    validation: function (cell, value) {
		        	if (value <= 0){
		        		return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
		        	} else {
		        		return true;
		        	}
			    },
			    cellbeginedit: function (row, datafield, columntype) {
					 if (disableEdit == false){
						 return false;
					 }
					 return true;
			    },
			},
		];
	    
	    var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	      
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        selectionmode: 'checkbox',
	        editmode: 'click',
	        url: 'JQGetListProductByIdentification',                
	        source: {pagesize: 10}
	  	};
	  	Grid.initGrid(config, datafields, columns, null, $("#gridProductToPrint"));
	}
	var getValidator = function(){
    	return validatorVAL;
    }
	return {
		init: init,
		getValidator: getValidator,
	}
}());