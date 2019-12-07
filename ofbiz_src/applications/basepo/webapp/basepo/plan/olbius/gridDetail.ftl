<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	GridDetail.initRowDetail(index, parentElement, gridElement, datarecord);
}"/>
<#assign dataField = "[{ name: 'productId', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'grandTotal', type: 'number' },
					{ name: 'comments', type: 'string' },
					{ name: 'currencyUomId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'minimumOrderQuantity', type: 'number' },
					{ name: 'lastPrice', type: 'number' }
					]"/>

<#assign columnlist = "
					{ text: '${uiLabelMap.SettingProductID}', datafield: 'productId', pinned: true, editable: false, width: 150 },
					{ text: '${uiLabelMap.SettingProductName}', datafield: 'productName', pinned: true, editable: false, width: 250 },
					{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', editable: false, filterable: false, width: 120,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							return '<div style=\"margin:4px;text-align: right;\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.UnitPrice}', datafield: 'lastPrice', columntype: 'numberinput', filterable: false, width: 150,
						cellsrenderer: function (row, column, value) {
							var data = mainGrid.jqxGrid('getrowdata', row);
							if (data && data.lastPrice) {
								return '<div style=\"margin:4px;text-align: right;\">' + commonObject.formatcurrency(data.lastPrice, data.currencyUomId) + '</div>';
							} else {
								return '<div style=\"margin:4px;text-align: right;\">' + commonObject.formatcurrency(0, data.currencyUomId) + '</div>';
							}
						}, initeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({inputMode: 'simple', groupSeparator: locale.toLocaleLowerCase()=='vi'?',':'.', min:0, decimalDigits: 3 });
						}, validation : function (cell, value) {
							if ( value < 0) {
								return { result:false, message: '${uiLabelMap.SettingQuantityIsMustGreaterZero}'};
							}
							return true;
						}, cellclassname : function (row, column, value, data) {
							return 'editable';
						}, cellbeginedit: function (row, datafield, columntype, value) {
							var gridrowdata = mainGrid.jqxGrid('getrowdata', row);
							if (gridrowdata.quantity > 0) {
								return true;
							}
							return false;
						}
					},
					{ text: '${uiLabelMap.BACCTotal}', datafield: 'grandTotal', editable: false, filterable: false, width: 150,
						cellsrenderer: function (row, column, value) {
							var data = mainGrid.jqxGrid('getrowdata', row);
							if (data && data.grandTotal) {
								return '<div style=\"margin:4px;text-align: right;\">' + commonObject.formatcurrency(data.grandTotal, data.currencyUomId) + '</div>';
							} else {
								return '<div style=\"margin:4px;text-align: right;\">' + commonObject.formatcurrency(0, data.currencyUomId) + '</div>';
							}
						}
					},
					{ text: '${uiLabelMap.SettingNotes}', datafield: 'comments', filterable: false, minWidth: 200,
						cellclassname : function (row, column, value, data) {
							return 'editable';
						}
					}
					"/>

<@jqGrid filtersimplemode="true" id="jqxGridProductList" showlist="true" dataField=dataField filterable="true"
	columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" editable="true" editmode="click" sortable="false"
	selectionmode="checkbox"
	initrowdetails="true" filtersimplemode="true" initrowdetailsDetail= initrowdetailsDetail rowdetailsheight="300"
	url="jqxGeneralServicer?sname=jqGetListSupplierProducts"/>