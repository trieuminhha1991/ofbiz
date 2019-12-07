var ProductSearch = (function() {
	var init = function() {
		initInput();
		initSearch();
		initEvents();
	};
	
	var initInput = function (){
		$("#productSearch").jqxDropDownButton({width: 300}); 
		$('#productSearch').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+searchDescription+'</div>');
	};
	
	var initSearch = function() {
		initGridProductSearch($('#jqxgridListProduct'));
	};
	
	var getProductDataFields = function(){
		var datafield = [{ name: 'productId', type: 'string' },
						{ name: 'productCode', type: 'string' },
						{ name: 'productName', type: 'string' },
						];
		return datafield;
	};
	
	var getProductColumnLists = function(){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable: false, pinned: true,
				cellsrenderer: function(row, column, value){
				 }, 
			},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 200, editable:false,},
        ];
		return columns;
	};
	
	var initGridProductSearch = function(grid){	
		var datafield = getProductDataFields();
		var columns = getProductColumnLists();
		var config = {
				width: 450, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'checkbox',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'JQGetListProductByOrganiztion&hasVirtualProd=N',                
				source: {pagesize: 10}
		};
		
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initEvents = function() {
		$("#jqxgridListProduct").on('bindingcomplete', function (event) {
			if (listProductSelected != undefined) {
				if (listProductSelected.length > 0){
					var rows = $("#jqxgridListProduct").jqxGrid('getrows');
					for (var i in rows) {
						var row = $('#jqxgridListProduct').jqxGrid('getrowboundindexbyid', rows[i].uid);
						var check = false;
						for (var x in listProductSelected){
							if (rows[i].productId == listProductSelected[x].productId){
								$('#jqxgridListProduct').jqxGrid('selectrow', row);
								check = true;
								break;
							}
						}
						if (check == false){
							$('#jqxgridListProduct').jqxGrid('unselectrow', row);
						}
					}
				}
			}
		});
		$("#jqxgridListProduct").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var check = false;
	        for (var i in listProductSelected){
	        	if (listProductSelected[i].productId == rowData.productId){
	        		check = true;
	        		break;
	        	}
	        }
	        if (!check){
	        	var tmp = {productId: rowData.productId};
	        	listProductSelected.push(tmp);
	        }
	        var rows = $("#jqxgridListProduct").jqxGrid('selectedrowindexes');
	        var description = null; 
	        if (rows.length <= 1) {
	        	description = rowData.productName + ' ['+rowData.productCode+']';
	        } else {
	        	description = rowData.productName + ' ['+rowData.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#productSearch').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#jqxgridListProduct").on('rowunselect', function (event) {
			var args = event.args;
			if (args.row){
				var rowData = args.row;
				for (var i in listProductSelected){
		        	if (listProductSelected[i].productId == rowData.productId){
		        		listProductSelected.splice(i, 1);
		        	}
		        }
				var rows = $("#jqxgridListProduct").jqxGrid('selectedrowindexes');
		        var description = null; 
		        if (rows.length == 1) {
		        	var data = $("#jqxgridListProduct").jqxGrid('getrowdata', rows[0]);
		        	description = data.productName + ' ['+data.productCode+']';
		        } else if (rows.length > 1){
		        	var data = $("#jqxgridListProduct").jqxGrid('getrowdata', rows[0]);
		        	description = data.productName + ' ['+data.productCode+'], ... (' + rows.length + ' ' + uiLabelMap.Product +')';
		        } else {
		        	if (searchDescription){
		        		description = searchDescription;
		        	} else {
		        		description = 'Search by products';
		        	}
		        }
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
		        $('#productSearch').jqxDropDownButton('setContent', dropDownContent);
			}
		});
		
		$("#productSearch").on('close', function (event) {
			if (url != undefined && url != null && gridResult.length > 0) {
				if (listProductSelected.length > 0){
					var json = JSON.stringify(listProductSelected);
					var tmpS = $(gridResult).jqxGrid("source");
					tmpS._source.url = url +"&products=" + json;
					$(gridResult).jqxGrid("source", tmpS);
				} else {
					var tmpS = $(gridResult).jqxGrid("source");
					tmpS._source.url = url;
					$(gridResult).jqxGrid("source", tmpS);
				}
			}
		});
	};
	return {
		init : init,
		initSearch: initSearch,
		initEvents: initEvents,
	}
}());