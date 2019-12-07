<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id', 'jqxVariance' + index);

	$('#containerjqxGridAggregation').append('<div id=\"containerjqxVariance' + index + '\"></div>');

	var eventId_RDT = datarecord.eventId;
	var varianceReasons_RDT = AddVariance.reasons(datarecord);

	var datafield = [
		{ name: 'eventId', type: 'string' },
		{ name: 'eventVarianceSeqId', type: 'string' },
		{ name: 'productId', type: 'string' },
		{ name: 'varianceReasonId', type: 'string' },
		{ name: 'statusId', type: 'string' },
		{ name: 'quantity', type: 'number' },
		{ name: 'comments', type: 'string' }
	];

	var columnlist = [
		{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', datafield: 'quantity', filtertype: 'number', columntype: 'numberinput', width: 200,
			cellsrenderer: function (row, column, value, a, b, data) {
				return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
			},
			cellbeginedit: function (row, datafield, columntype, value) {
				return grid.jqxGrid('getcellvalue', row, 'statusId') == 'STOCKING_VARIANCE_CREATED';
			},
			validation: function (cell, value) {
				if (value && value > 0) {
					return true;
				}
				return { result: false, message: '${uiLabelMap.DmsQuantityNotValid}' };
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.POReasonsDifference)}', datafield: 'varianceReasonId', columntype: 'dropdownlist', width: 250,
			cellsrenderer: function (row, column, value, a, b, data) {
				value = mapVarianceReason[value]?mapVarianceReason[value]:value;
				return '<div style=margin:4px;>' + value + '</div>';
			},
			cellbeginedit: function (row, datafield, columntype, value) {
				return grid.jqxGrid('getcellvalue', row, 'statusId') == 'STOCKING_VARIANCE_CREATED';
			},
			createeditor: function (row, column, editor) {
				editor.jqxDropDownList({ source: varianceReasons_RDT, displayMember: 'text', valueMember: 'value', theme: theme, placeHolder: multiLang.filterchoosestring });
			},
			validation: function (cell, value) {
				if (value) {
					return true;
				}
				return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Notes)}', datafield: 'comments', minwidth: 250 }
	];
	var config = {
		showfilterrow: true,
		filterable: true,
		editable: editable_RDT,
		width: '95%',
		height: 250,
		pageable: true,
		sortable: true,
		virtualmode : true,
		autoheight: true,
		selectionmode: 'singlerow',
		editmode: 'dblclick',
		url: 'JQGetListStockEventVariance&eventId=' + eventId_RDT + '&productId=' + datarecord.productId,
		source: {
			pagesize: 5,
			createUrl: 'createStockEventVariance',
			addColumns: 'eventId;productId;statusId',
			updateUrl: 'updateStockEventVariance',
			editColumns: 'eventId;eventVarianceSeqId;varianceReasonId;quantity(java.math.BigDecimal);comments',
			removeUrl: 'deleteStockEventVariance',
			deleteColumn: 'eventId;eventVarianceSeqId'
		},
		showtoolbar: true,
		toolbarheight: 27,
		rendertoolbar: rendertoolbar
	};
	Grid.initGrid(config, datafield, columnlist, null, grid);
	
	var statusContent = $(\"<b></b>\");
	
	grid.on('bindingcomplete', function (event) {
		var quantity = DataAccess.getData({
			url: 'getQuantityNonVariance',
			data: { eventId: eventId_RDT, productId: datarecord.productId },
			source: 'quantity'});
		statusContent.text(quantity);
		grid.data('nonVariance', quantity);
	});

	function rendertoolbar(toolbar) {
		var container = $(\"<div style='margin: 10px 4px 0px 0px;' class='pull-right'></div>\");
		var statusTag = $(\"<a style='cursor: pointer;margin-right: 15px;'>${StringUtil.wrapString(uiLabelMap.DmsChuaBoSungLyDo)}: </a>\");
		var aTag = $(\"<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>\");
		toolbar.append(container);
		statusTag.append(statusContent);
		container.append(statusTag);
		if (editable_RDT) {
			container.append(aTag);
		}
		aTag.click(function () {
			AddVariance.add(grid, eventId_RDT, datarecord.productId);
		});
	}
}"/>