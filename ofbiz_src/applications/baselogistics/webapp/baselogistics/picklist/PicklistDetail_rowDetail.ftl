<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id', 'jqxGridBin' + index);
	
	var datafield = [
		{ name: 'locationCode', type: 'string'},
		{ name: 'quantity', type: 'number'}
	];
	var columnlist = [
		{ text: '${StringUtil.wrapString(uiLabelMap.BSLocationCode)}', datafield: 'locationCode', width: 500 },
		{ text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity', width: 530 }
	];
	var config = {
		showfilterrow: true,
		filterable: true,
		editable: false,
		width: '95%',
		height: 250,
		pageable: true,
		sortable: true,
		virtualmode : true,
		selectionmode: 'singlerow',
		url: 'JQGetPicklistItemLocation&picklistBinId=' + datarecord.picklistBinId + '&orderId=' + datarecord.orderId + 
			'&orderItemSeqId=' + datarecord.orderItemSeqId + '&shipGroupSeqId=' + datarecord.shipGroupSeqId,
		source: {
			pagesize: 5
		}
	};
	Grid.initGrid(config, datafield, columnlist, null, grid);
	
	grid.on('contextmenu', function () {
		return false;
	});
	grid.on('rowclick', function (event) {
		if (event.args.rightclick) {
			grid.jqxGrid('selectrow', event.args.rowindex);
			var scrollTop = $(window).scrollTop();
			var scrollLeft = $(window).scrollLeft();
			contextMenuChild.data('grid', grid);
			contextMenuChild.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
			return false;
		}
	});
}"/>