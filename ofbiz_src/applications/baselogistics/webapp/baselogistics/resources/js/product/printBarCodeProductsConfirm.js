$(function(){
	if (typeof(dataSelected) == "undefined") var dataSelected = [];
	BarCodeConfirm.init();
});
var BarCodeConfirm = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initGridProductSelected();
	};
	
	var initInputs = function() {
	};
	
	var initElementComplex = function() {
	};
	
	var initEvents = function() {
	};
	
	var initGridProductSelected = function(){
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
  					return '<div class=" align-right">' + formatnumber(value) + '</div>';
  				},
  			},
  			{ text: uiLabelMap.BarcodeWidth + ' (mm)', dataField: 'width', columntype: 'numberinput', width: 150, editable: false, filterable: false,
  				cellsrenderer: function(row, column, value){
  					return '<div class="align-right ">' + formatnumber(value) + '</div>';
  				},
  			},
  			{ text: uiLabelMap.BarcodeHeight + ' (mm)', dataField: 'height', columntype: 'numberinput', width: 150, editable: disableEdit, filterable: false,
  				cellsrenderer: function(row, column, value){
  					return '<div class="align-right ">' + formatnumber(value) + '</div>';
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
  	        editable: false,
  	        rowsheight: 26,
  	        rowdetails: false,
  	        useUrl: false,
  	        url: '',                
  	        source: {pagesize: 10}
  	  	};
  	  	Grid.initGrid(config, datafields, columns, null, $("#jqxgridProductBarCodeConfirm"));
	};
	
	return {
		init: init
	};
}());