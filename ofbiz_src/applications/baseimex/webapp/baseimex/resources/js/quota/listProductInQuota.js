$(document).ready(function() {
	ObjQuotas.init();
});
var ObjQuotas = (function() {
	var gridProduct = $("#jqxGridProducts");
	var validatorVAL = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidate();
	};
	
	var initInput = function() { 
	}
	
	var initElementComplex = function() {
		initGridProduct(gridProduct);
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
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 130, },
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, },
			{ text: uiLabelMap.Unit, dataField: 'uomId', minwidth: 100, filterable: false,
				cellsrenderer: function(row, column, value) {
					return '<span>' + getUomDesc(value) +'</span>';
			    }
			},
			{ text: uiLabelMap.BIEQuotaTotal, datafield: 'quotaQuantity', sortable: false,  width: 150, editable: true, filterable: true, filtertype: 'number', sortable: false,
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
			{ text: uiLabelMap.BIEQuotaRemain, datafield: 'availableQuantity', sortable: false,  width: 150, editable: true, filterable: true, filtertype: 'number', sortable: false,
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
        ];
		
		var datafield = [
			{ name: 'productId', type: 'string'},
			{ name: 'productCode', type: 'string'},
			{ name: 'productName', type: 'string' },
			{ name: 'uomId', type: 'string' },
			{ name: 'quotaQuantity', type: 'number' },
			{ name: 'availableQuantity', type: 'number' },
		 	]
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "Container";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BIEListProductInQuota + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
//	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.AddNew + "@javascript:void(0)@ObjQuotas.openPopupAdd()";
//	        Grid.createCustomControlButton(grid, container, customcontrol1);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		editmode: 'rowclick',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: "jqGetProductInQuota",                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	
	var initEvents = function() {
		
		$("#jqxContextMenu").on('itemclick', function (event) {
			var liId = event.args.id;
			var data = grid.jqxGrid('getRowData', grid.jqxGrid('selectedrowindexes'));
			
			if (liId == "refreshGrid"){
				grid.jqxGrid('updatebounddata');
			}
		});
		
	}
	
	var initValidate = function() {
		var extendRules = [
		];
   		var mapRules = [
        ];
	}
	
	var showDetailQuota = function(quotaId) {
		location.href = "getDetailQuotaHeaders?quotaId=" + quotaId;
	}
	
	return {
		init : init,
		showDetailQuota: showDetailQuota,
	}
}());