<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
	{
		localdata: datarecord.rowDetail,
		datatype: 'local',
		datafields:
		[
			{ name: 'productId', type: 'string' },
			{ name: 'productCode', type: 'string' },
			{ name: 'primaryProductCategoryId', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'brandName', type: 'string' },
			{ name: 'productWeight', type: 'number' },
			{ name: 'weightUomId', type: 'string' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'longDescription', type: 'string' },
			{ name: 'isVirtual', type: 'string' },
			{ name: 'taxCatalogs', type: 'string' }
		]
    };
	var columns = [
					{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 150,
						cellsrenderer: function(row, colum, value){
							var productId = grid.jqxGrid('getcellvalue', row, 'productId');
							var link = 'viewProduct?productId=' + productId;
							return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', datafield: 'productName', minWidth: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'longDescription', width: 250 }];
	for ( var x in productFeatureTypes) {
		sourceGridDetail.datafields.push({ name: productFeatureTypes[x], type: 'string'});
		columns.push({ text: mapProductFeatureType[productFeatureTypes[x]], datafield: productFeatureTypes[x], width: 200 });
	}
	var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	
	grid.jqxGrid({
		localization: getLocalization(),
		width: '98%',
		height: '92%',
		theme: theme,
		source: dataAdapterGridDetail,
		sortable: true,
		pagesize: 5,
		columnsresize: true,
		enabletooltips: true,
		pageable: true,
		selectionmode: 'singlerow',
		columns: columns
	});
	grid.on('rowclick', function (event) {
		if (event.args.rightclick) {
			grid.jqxGrid('selectrow', event.args.rowindex);
			var scrollTop = $(window).scrollTop();
			var scrollLeft = $(window).scrollLeft();
			$('#contextMenu').jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
			gridSelecting = grid;
			$('#jqxgrid').jqxGrid('clearselection');
			return false;
		}
	});
}"/>