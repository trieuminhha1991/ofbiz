<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqx'+index);
	
    var datafield =  [
  		{ name: 'orderId', type: 'string'},
		{ name: 'productId', type: 'string'},
		{ name: 'productCode', type: 'string'},
		{ name: 'productName', type: 'string'},
		{ name: 'createdBy', type: 'string'},
		{ name: 'createdByName', type: 'string'},
		{ name: 'createdByCode', type: 'string'},
		{ name: 'qtyInInventory', type: 'number'},
		{ name: 'fromDate', type: 'date', other: 'Timestamp'}
  	];
  	var columnlist = [
		{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (row + 1) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesman)}', datafield: 'createdByName', width: 200,
			cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		        return '<div style=margin:4px;>' + value + ' (' + rowdata.createdByCode + ')' + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 150 },
		{ text: '${StringUtil.wrapString(uiLabelMap.BSProduct)}', datafield: 'productName' },
		{ text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'qtyInInventory', filtertype: 'number', width: 100,
			cellsrenderer: function (row, column, value) {
		        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.InventoryDate)}', datafield: 'fromDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'orderId', width: 150}
  	];
  	var config = {
		showfilterrow: true,
		filterable: true,
		editable: false,
		width: '95%',
		height: 217,
		pagesize: 10,
		pageable: true,
		sortable: true,
		enabletooltips: true,
		autoheight: false,
		selectionmode: 'singlerow',
		url: 'JQGetListInventoryOfAgents&partyId=' + datarecord.partyId,
  		source: {pagesize: 5}
  	};
  	Grid.initGrid(config, datafield, columnlist, null, grid);
}"/>