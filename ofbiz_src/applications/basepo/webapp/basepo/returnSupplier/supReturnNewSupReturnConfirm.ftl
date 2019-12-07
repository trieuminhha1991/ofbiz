<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="span12">
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid">
							<div class="span5">
								<span >${uiLabelMap.Supplier}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="supplierIdDT" name="supplierIdDT"></div>
							</div>
						</div>
						<div class="row-fluid hide">
							<div class="span5">
								<span >${uiLabelMap.CurrencyUom}</span>
							</div>
							<div class="span7">
								<div id="currencyUomIdDT" class="green-label"></div>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span5">
								<span >${uiLabelMap.ExportFromFacility}</span>
							</div>
							<div class="span7">
								<div id="destinationFacilityIdDT" class="green-label"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="span5">
								<span >${uiLabelMap.POEntryDate}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="entryDateDT" name="entryDateDT"></div>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span5">
								<span>${uiLabelMap.Description}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="descriptionDT" name="descriptionDT"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="jqxgridProductSelected" style="width: 100%"></div>
		</div>
	</div>

    <div class="row-fluid margin-top10">
        <div class="span12">
            <div id="jqxgridProductPromoSelected" style="width: 100%"></div>
        </div>
    </div>
</div>

<#assign dataFieldConfirms = "[{ name: 'orderId', type: 'string' },
							{ name: 'productId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'itemDescription', type: 'string' },
							{ name: 'quantity', type: 'number' },
							{ name: 'unitPrice', type: 'number' },
							{ name: 'requireAmount', type: 'string' },
							{ name: 'orderItemSeqId', type: 'string' },
							{ name: 'quantityUomId', type: 'string' },
							{ name: 'weightUomId', type: 'string' },
							{ name: 'orderedQuantity', type: 'number' },
							{ name: 'returnableQuantity', type: 'number' },
							{ name: 'returnReasonId', type: 'string' }]"/>

<#assign columnlistConfirm = "{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
								groupable: false, draggable: false, resizable: false,
								datafield: '', columntype: 'number', width: 40,
									cellsrenderer: function (row, column, value) {
										return '<div style=margin:4px;>' + (value + 1) + '</div>';
									}
							},
							{ text: uiLabelMap.OrderId, datafield: 'orderId', width: 100, editable: false },
							{ text: uiLabelMap.ProductId, datafield: 'productId', width: 120, editable: false, hidden: true },
							{ text: uiLabelMap.ProductId, datafield: 'productCode', width: 120, editable: false },
							{ text: uiLabelMap.ProductName, dataField: 'itemDescription', editable: false, minwidth: 120 },
							{ text: uiLabelMap.OrderNumber, datafield: 'orderedQuantity', width: 120, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value){
									return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
								}
							},
							{ text: uiLabelMap.POReturnQuantity, datafield: 'returnableQuantity', width: 120, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput' },
							{ text : uiLabelMap.Unit, datafield : 'quantityUomId', width : 120, editable : false, filterable : false, cellsalign : 'right', columntype : 'numberinput',
								cellsrenderer : function(row, column, value) {
									var data = $('#jqxgridProductSelected').jqxGrid('getrowdata', row);
									var requireAmount = data.requireAmount;
									if (requireAmount && requireAmount == 'Y') {
										value = data.weightUomId;
									}
									if (value) {
										return '<span class=\"align-right\">' + getUomDescription(value) + '</span>';
									}
								},
							},
							{ text: uiLabelMap.Quantity, datafield: 'quantity', width: 120, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value) {
									if (value){
										return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
									}
								}, 
							},
							{ text: uiLabelMap.UnitPrice, datafield: 'unitPrice', width: 100, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value){
									if (value){
										return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
									}
								}, createeditor: function (row, cellvalue, editor) {
									editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0 });
								}
							},
							{ text: uiLabelMap.Reason, datafield: 'returnReasonId', width: 200, filterable: false, columntype: 'dropdownlist',
								cellsrenderer: function(row, column, value){
									if (value){
										for(var i = 0; i < returnReasonData.length; i++){
											if(value == returnReasonData[i].returnReasonId){
												return '<span style=\"text-align: left\">' + returnReasonData[i].description +'</span>';
											}
										}
									}
								}, createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: returnReasonData, valueMember: 'returnReasonId', displayMember: 'description' });
								}
							}"/>

<@jqGrid filtersimplemode="true" id="jqxgridProductSelected" filterable="false" dataField=dataFieldConfirms
	columnlist=columnlistConfirm editable="false" showtoolbar="false"
	url="" editmode="click" selectionmode="multiplecellsadvanced"/>

<#assign dataFieldConfirms = "[{ name: 'orderId', type: 'string' },
							{ name: 'productPromoId', type: 'string' },
							{ name: 'productPromoName', type: 'string' },
							{ name: 'orderedPromoAmount', type: 'number' },
							{ name: 'returnableAmount', type: 'number' },
							{ name: 'amount', type: 'string' },
							{ name: 'returnReasonId', type: 'string' }]"/>

<#assign columnlistConfirm = "{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
								groupable: false, draggable: false, resizable: false,
								datafield: '', columntype: 'number', width: 50,
									cellsrenderer: function (row, column, value) {
										return '<div style=margin:4px;>' + (value + 1) + '</div>';
									}
							},
							{ text: uiLabelMap.OrderId, datafield: 'orderId', width: 100, editable: false },
							{ text: uiLabelMap.ProductPromoId, datafield: 'productPromoId', width: 120, editable: false},
							{ text: uiLabelMap.ProductPromoName, datafield: 'productPromoName', minwidth: 200, editable: false},
							{ text: uiLabelMap.orderedPromoAmount, datafield: 'orderedPromoAmount', minwidth: 120, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value){
									return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
								}
							},
							{ text: uiLabelMap.returnableAmount, datafield: 'returnableAmount', width: 160, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value){
									return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
								}
							},
							{ text: uiLabelMap.returnAmount, datafield: 'amount', width: 150, editable: false, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value){
									return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
								}
							},
							{ text: uiLabelMap.Reason, datafield: 'returnReasonId', width: 150, filterable: false, columntype: 'dropdownlist',
								cellsrenderer: function(row, column, value){
									if (value){
										for(var i = 0; i < returnReasonData.length; i++){
											if(value == returnReasonData[i].returnReasonId){
												return '<span style=\"text-align: left\">' + returnReasonData[i].description +'</span>';
											}
										}
									}
								}, createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: returnReasonData, valueMember: 'returnReasonId', displayMember: 'description' });
								}
							}"/>

<@jqGrid filtersimplemode="true" id="jqxgridProductPromoSelected" filterable="false" dataField=dataFieldConfirms
columnlist=columnlistConfirm editable="false" showtoolbar="true"  customTitleProperties="listReturnProductPromoByOrder"
url="" editmode="click" selectionmode="multiplecellsadvanced"/>