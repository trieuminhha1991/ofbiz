$(document).ready(function() {
	ObjProduct.init();
});
var ObjProduct = (function() {
	var grid = $("#jqxGridProduct");
	var init = function() { 
		initElementComplex(); 
	};
	
	var initElementComplex = function() {
		initGridProduct(grid);
	}
	
	var initGridProduct = function (grid) {
		var columns = [
		   			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
		   			    groupable: false, draggable: false, resizable: false,
		   			    datafield: '', columntype: 'number', width: 50,
		   			    cellsrenderer: function (row, column, value) {
		   			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		   			    }
		   			},
		   			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
		   			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,  },
		   			{ text: uiLabelMap.FromDate, dataField: 'fromDate' , width: '25%', editable: false, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', 
		   				cellsrenderer: function (row, column, value){
		   					if (value){
		   						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
		   					} else {
		   						return '<span class="align-right"></span>';
		   					}
		   				},
		   				
		   			},
		   			{ text: uiLabelMap.ThruDate, dataField: 'thruDate' ,  width: '25%', editable: false, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', 
		   				cellsrenderer: function (row, column, value){	
		   					if (value){
		   						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
		   					} else {
		   						return '<span class="align-right"></span>';
		   					}
		   				},
		   			},
		           ];
		
		var datafield = [
		               { name: 'productId', type: 'string'},
		               { name: 'productCode', type: 'string'},
		               { name: 'productName', type: 'string' },
		               { name: 'fromDate', type: 'date', other: 'Timestamp' },
		               { name: 'thruDate', type: 'date', other: 'Timestamp'}
		                ];
		
		var rendertoolbarProduct = function (toolbar){
			toolbar.html("");
			var id = "ProductList";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BIEProductOutOfDeclaration + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
		}; 
		
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: true,
				rendertoolbar: rendertoolbarProduct,
				selectionmode: 'singlerow',
				editmode: 'click',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: true,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'JQGetListProductOutOfDeclaration',                
				source: {pagesize: 10}
		};
		
	  	Grid.initGrid(config, datafield, columns, null, grid);
		
	}

    
	return {
		init : init,
		
	}
}());