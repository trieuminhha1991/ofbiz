$(function() {  
	EventProduct.init();
});

var EventProduct = (function() {
	var grid = $("#jqxGridProduct");  
	var product = null;
	var validatorVAL = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();
	};
	
	var initInput = function() { 
		
		$("#eventCode").jqxInput({width: 300, height: 24, theme: theme}); 
		$("#eventName").jqxInput({width: 300, height: 24, theme: theme}); 
		$("#description").jqxInput({width: 300, theme: theme}); 
		
		$('#productEventType').jqxDropDownList({ source: productEventTypeData, selectedIndex: 0, width: 300,theme: theme, valueMember: 'eventTypeId', displayMember: 'description', placeHolder : uiLabelMap.PleaseSelectTitle})
		
		$("#executedDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#completedDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#executedDate").jqxDateTimeInput('clear');
		$("#completedDate").jqxDateTimeInput('clear');
		
	}
	
	var initValidateForm = function(){
		var extendRules = [
		       			{
		       				input: '#eventCode', 
		       			    message: uiLabelMap.WrongFormat + "0-9, a-z, A-Z, _, -", 
		       			    action: 'blur', 
		       			    position: 'right',
		       			    rule: function (input) {
		       				    if (input.length > 0 ){
		       				    	var patt = /[^0-9a-zA-Z\_\-]/gm;
			       			    	var result = input.val().match(patt);
		       				    	if (result) return false
		       				    	else return true;
		       				    }
		       				    return true;
		       			    }
		       			},
		       		];
		
   		var mapRules = [
   				{input: '#executedDate', type: 'validInputNotNull'},
   				{input: '#productEventType', type: 'validObjectNotNull', objType: 'dropDownList'},
               ];
   		validatorVAL = new OlbValidator($('#infoForm'), mapRules, extendRules, {position: 'right'});
	};
	
	 var getValidator = function(){
    	return validatorVAL;
    };
    
	var initElementComplex = function() {
		initGridProduct(grid);
	}
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, cellclassname: productGridCellclass,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true, cellclassname: productGridCellclass,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false, cellclassname: productGridCellclass, },
			{ text: uiLabelMap.FromDate, dataField: 'fromDate' , width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.fromDate;
			   					return false;
			   				}
			   			});
				    }
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = grid.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		var now = new Date();
			 		if (value) {
				        if (value < now) {
				            return { result: false, message: uiLabelMap.FromDateMustBeAfterNow};
				        }
				        var data = grid.jqxGrid('getrowdata', cell.row);
				        if (data.thruDate){
				        	var exp = new Date(data.thruDate);
				        	if (exp < new Date(value)){
					        	return { result: false, message: uiLabelMap.FromDateMustBeBeforeThruDate};
					        }
				        }
			        } 
			        return true;
				 },
			},
			{ text: uiLabelMap.ThruDate, dataField: 'thruDate' ,  width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.thruDate;
			   					return false;
			   				}
			   			});
				    }
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = grid.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		var now = new Date();
			 		if (value) {
				        if (value < now) {
				            return { result: false, message: uiLabelMap.ThruDateMustBeAfterNow};
				        }
				        var data = grid.jqxGrid('getrowdata', cell.row);
				        if (data.fromDate){
				        	var exp = new Date(data.fromDate);
				        	if (exp > new Date(value)){
					        	return { result: false, message: uiLabelMap.FromDateMustBeBeforeThruDate};
					        }
				        }
			        } 
			        return true;
				 },
			},
        ];
		return columns; 
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createQuantity', type: 'number' },
				{ name: 'requireAmount', type: 'String'},
				{ name: 'fromDate', type: 'date', other: 'Timestamp' },
				{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				
				]
		return datafield;
	};
	
	var initGridProduct = function(grid){
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				editmode: 'click',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: true,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'JQGetPOListProducts',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, getDataField(), getColumns(grid), null, grid);
	};
	
	var initEvents = function() {
		grid.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			
			if (rowData){
				if (dataField == 'fromDate'){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
					if (value != null ){
						var item = $.extend({}, rowData);
						item.fromDate = value;
						listProductSelected.push(item);
					} 
				}
				if (dataField == 'thruDate'){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
					if (value != null ){
						var item = $.extend({}, rowData);
						item.thruDate = value;
						listProductSelected.push(item);
					} 
				} 
			}
		});	
	 
	}
	
	var productGridCellclass = function (row, column, value, data) {
		var data = grid.jqxGrid('getrowdata',row);
    	if (data.fromDate !=null || data.thruDate != null) {
    		return 'background-prepare';
    	}
	}
	
	function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	
	 function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
	 
	 
	return {
		init : init,
		getValidator: getValidator, 
	}
}());