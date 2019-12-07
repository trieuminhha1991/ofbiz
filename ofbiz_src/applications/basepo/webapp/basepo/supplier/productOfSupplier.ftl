<div id="product-tab" class="tab-pane">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<div class="row-fluid">
			<div class="span12">
			<#assign dataField="[
						{ name: 'availableFromDate', type: 'date', other: 'Timestamp' },
						{ name: 'availableThruDate', type: 'date', other: 'Timestamp' },
						{ name: 'productId', type: 'string' },
						{ name: 'partyId', type: 'string' },
						{ name: 'minimumOrderQuantity', type: 'number' },
						{ name: 'currencyUomId', type: 'string' },
						{ name: 'lastPrice', type: 'number' },
						{ name: 'quantityUomId	', type: 'string' },
						{ name: 'supplierProductId', type: 'string' },
						{ name: 'productName', type: 'string' },
						{ name: 'canDropShip', type: 'string' },
						{ name: 'productCode', type: 'string' },
						{ name: 'groupName', type: 'string' }]"/>
					
			<#assign columnlist="
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, pinned: true, cellclassname: cellclassname,
							groupable: false, draggable: false, resizable: false,
							datafield: '', columntype: 'number', width: 50,
							cellsrenderer: function (row, column, value) {
								return '<div style=margin:4px;>' + (value + 1) + '</div>';
							}
						},
						{ text: '${uiLabelMap.POProductId}', dataField: 'productCode', width: 120, pinned: true, filterable: true, cellclassname: cellclassname },
						{ text: '${uiLabelMap.BPOProductName}', datafield: 'productName', editable: false, minwidth: 250, cellclassname: cellclassname },
						{ text: '${uiLabelMap.MOQ}', dataField: 'minimumOrderQuantity', width: 90, filterable: true, cellsalign: 'right', columntype: 'numberinput', filtertype: 'number', cellclassname: cellclassname },
						{ text: '${uiLabelMap.POLastPrice}', dataField: 'lastPrice', align: 'center', filterable: true, cellsalign: 'right', columntype: 'numberinput', width: 100, filtertype: 'number', cellclassname: cellclassname,
							cellsrenderer: function(row, column, value){
								if (value){
									return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +'</span>';
								}
							}
						},
						{ text: '${uiLabelMap.BPOCurrencyUomId}', dataField: 'currencyUomId', filterable: true, width: 90, cellclassname: cellclassname },
						{ text: '${uiLabelMap.FormFieldTitle_supplierProductId}', dataField: 'supplierProductId', filterable: true, width: 180, cellclassname: cellclassname },
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_availableFromDate)}', datafield: 'availableFromDate', columntype: 'datetimeinput',width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellclassname: cellclassname },
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_availableThruDate)}', datafield: 'availableThruDate', columntype: 'datetimeinput',width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range', cellclassname: cellclassname },
						{ text: '${uiLabelMap.FormFieldTitle_canDropShip}', dataField: 'canDropShip', width: 120, filterable: true, filtertype : 'checkedlist',columntype: 'dropdownlist', cellclassname: cellclassname,
							columntype: 'dropdownlist',
							 cellsrenderer : function(row, column, value){
								if(value == null)
									return '<div style=\"margin-top: 6px; text-align:left\">' + '' + '</div>';
								if(value == 'Y')
									return '<div style=\"margin-top: 6px; text-align:left\">' + canDropShipData[0].description + '</div>';
								if(value == 'N')
									return '<div style=\"margin-top: 6px; text-align:left\">' + canDropShipData[1].description + '</div>';
								if(value != 'Y' && value != 'N')
									return '<div style=\"margin-top: 6px; text-align:left\">' + '' + '</div>';
							}, createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: canDropShipData, displayMember: 'description', valueMember: 'id' ,
									renderer: function (index, label, value) {
										if (index == 0) {
											return value;
										}
										for(var i = 0; i < canDropShipData.length; i++){
											if(value == canDropShipData[i].id){
												return canDropShipData[i].description; 
											}
										}
								} });
							}
						}"/>
						<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable=editable showtoolbar="true"
							id="jqxgridSupplierProduct" filterable="true" clearfilteringbutton="true" customLoadFunction="true"
							url="jqxGeneralServicer?sname=jqGetListSupplierProductConfig&partyId=${parameters.partyId?if_exists}" customTitleProperties="BSProduct"
							defaultSortColumn="productCode"/>
			</div>
			</div><!--.row-fluid-->
		</div>
	</div>
</div>