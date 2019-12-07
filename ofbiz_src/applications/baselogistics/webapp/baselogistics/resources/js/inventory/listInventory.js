$(function(){
	InventoryObj.init();
});
var InventoryObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
	};
	
	var initElementComplex = function (){
		$("#locationPopupWindow").jqxWindow({
			maxWidth: 1000, minWidth: 600, width: 500, modalZIndex: 10000, zIndex:10000, minHeight: 100, height: 470, maxHeight: 800, resizable: false, cancelButton: $("#locationCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		$("#upcPopupWindow").jqxWindow({
			maxWidth: 1000, minWidth: 200, width: 300, modalZIndex: 10000, zIndex:10000, minHeight: 100, height: 300, maxHeight: 800, resizable: true, cancelButton: $("#locationCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		initGridLocation($("#jqxgridLocation"));
	};
	
	var initInputs = function(){
		$("#menuInvDetail").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#menuProductDetail").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};

	var initEvents = function(){
		$("#jqxgridInvGroupByProduct").on("celldoubleclick", function (event)
		{
		    // event arguments.
		    var args = event.args;
		    // row's bound index.
		    var rowBoundIndex = args.rowindex;
		    // row's visible index.
		    var rowVisibleIndex = args.visibleindex;
		    // right click.
		    var rightClick = args.rightclick; 
		    // original event.
		    var ev = args.originalEvent;
		    // column index.
		    var columnIndex = args.columnindex;
		    // column data field.
		    var dataField = args.datafield;
		    // cell value
		    var value = args.value;
		    if (dataField == "idSKU"){
		    	var data = $('#jqxgridInvGroupByProduct').jqxGrid('getrowdata', rowBoundIndex);
		    	if (data.listUPCs){
		    		var listUPCs = data.listUPCs;
		    		var primaryUPC = data.primaryUPC;
		    		var text = "<div style='font-weight: bold'> SKU: " + data.productCode + "</div>";
		    		for (var x in listUPCs){
		    			if (listUPCs[x].idValue != '' && listUPCs[x].idValue != null && listUPCs[x].idValue != 'null' && listUPCs[x].idValue != undefined){
		    				if (listUPCs[x].idValue == primaryUPC){
		    					text = text + "<div style='color: red'>" + listUPCs[x].idValue + " - " + uiLabelMap.BLPrimaryUPC + "</div>"; 
			    			} else {
			    				text = text + "<div>" + listUPCs[x].idValue + "</div>"; 
			    			}
		    			}
		    		}
		    		$("#listUPC").html(text);
		    		$("#upcPopupWindow").jqxWindow('open');
		    	}
		    }
		});                       

		$("#menuInvDetail").on('itemclick', function (event) {
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridItemDetail').jqxGrid('updatebounddata');
			}
		});
		$("#menuProductDetail").on('itemclick', function (event) {
			var args = event.args;
			var id = args.firstChild.id;
			var rowindex = $("#jqxgridInvGroupByProduct").jqxGrid('getselectedrowindex');
			var data = $('#jqxgridInvGroupByProduct').jqxGrid('getRowData', rowindex);
			$("#productIdLocation").text(data.productCode);
			$("#productNameLocation").text(": " + data.productName);
			if(id == "refreshProduct"){
				$('#jqxgridInvGroupByProduct').jqxGrid('updatebounddata');
			} else if (id == "viewLocationProduct"){
				var productId = data.productId;
				viewDetailLocation(productId);
			}
		});
		
	};
	
	var getLocationDataFields = function(){
		var datafield = [{ name: 'locationCode', type: 'string' },
						{ name: 'locationId', type: 'string' },
						{ name: 'quantity', type: 'number' },
						{ name: 'quantityUomId', type: 'string' },
						{ name: 'weightUomId', type: 'string' },
						{ name: 'requireAmount', type: 'string' },
						];
		return datafield;
	};
	
	var getLocationColumnLists = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.BLLocationCode, dataField: 'locationCode', width: 200, editable: false, pinned: true,},
			{ text: uiLabelMap.Quantity, dataField: 'quantity', minwidth: 150, editable:false,},
			{ text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 150, editable:false,
				cellsrenderer: function (row, column, value){
					var data = grid.jqxGrid('getrowdata', row);
					var requireAmount = data.requireAmount;
					if (requireAmount && requireAmount == 'Y') {
						return '<span class="align-right">' + getDescriptionByUomId(data.weightUomId) +'</span>';
					} else {
						return '<span class="align-right">' + getDescriptionByUomId(value) +'</span>';
					}	
				},
			},
        ];
		return columns;
	};
	
	var initGridLocation = function(grid){	
		var datafield = getLocationDataFields();
		var columns = getLocationColumnLists(grid);
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: '',                
				source: {pagesize: 10}
		};
		
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var viewDetailLocation = function (productId){
		var tmpS = $("#jqxgridLocation").jqxGrid("source");
		tmpS._source.url = "jqxGeneralServicer?sname=jqGetLocationByProduct&productId=" + productId;
		$("#jqxgridLocation").jqxGrid("source", tmpS);
		$("#locationPopupWindow").jqxWindow('open');
	};
	
	return {
		init: init,
	}
}());