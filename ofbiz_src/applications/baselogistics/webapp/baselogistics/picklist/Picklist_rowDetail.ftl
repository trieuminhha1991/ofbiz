<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id', 'jqxGridBin' + index);
	
	var datafield = [
		{ name: 'picklistId', type: 'string'},
		{ name: 'picklistBinId', type: 'string'},
		{ name: 'binStatusId', type: 'string'},
		{ name: 'primaryOrderId', type: 'string'},
		{ name: 'partyPickId', type: 'string'},
		{ name: 'partyPickCode', type: 'string'},
		{ name: 'partyPickName', type: 'string'},
		{ name: 'partyCheckId', type: 'string'},
		{ name: 'partyCheckCode', type: 'string'},
		{ name: 'partyCheckName', type: 'string'},
	];
	var columnlist = [
		{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoPhieuSoan)}', datafield: 'picklistBinId', minwidth: 150,
			cellsrenderer: function(row, colum, value) {
				return \"<span><a href='PicklistDetail?picklistBinId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'primaryOrderId', width: 170,
			cellsrenderer: function(row, colum, value) {
				return \"<span><a href='viewOrder?orderId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'binStatusId', width: 170, filtertype: 'checkedlist',
			cellsrenderer: function(row, column, value){
				value = value?mapPickbinStatus[value]:value;
				return '<span title=' + value +'>' + value + '</span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({ source: pickbinStatusData, displayMember: 'description', valueMember: 'statusId' });
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsNhanVienSoan)}', datafield: 'partyPickName', width: 250 },
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsNhanVienKiem)}', datafield: 'partyCheckName', width: 250 }
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
		url: 'JQGetListPicklistBin&primaryPicklistId=' + datarecord.picklistId,
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