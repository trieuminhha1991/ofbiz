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
						<div class="row-fluid">
							<div class="span5">
								<span >${uiLabelMap.CurrencyUom}</span>
							</div>
							<div class="span7">
								<div id="currencyUomIdDT" class="green-label"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<#--<div class="row-fluid">
							<div class="span5">
								<span >${uiLabelMap.POEntryDate}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="entryDateDT" name="entryDateDT"></div>
							</div>
						</div>-->
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
</div>

<#assign dataFieldConfirms = "[{name: 'productId', type: 'string'},
	         			{name: 'productCode', type: 'string'},
	               		{name: 'internalName', type: 'string'},
	               		{name: 'productName', type: 'string'},
	               		{name: 'quantityUomId', type: 'string'},
	               		{name: 'returnQuantity', type: 'string'},
	               		{name: 'description', type: 'string'},
	               		{name: 'returnReasonId', type: 'string' },
	               		{name: 'returnPrice', type: 'number', formatter: 'float'},]"/>

<#assign columnlistConfirm = "{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
								groupable: false, draggable: false, resizable: false,
								datafield: '', columntype: 'number', width: 40,
									cellsrenderer: function (row, column, value) {
										return '<div style=margin:4px;>' + (value + 1) + '</div>';
									}
							},
							{ text: uiLabelMap.ProductId, datafield: 'productId', width: 150, editable: false, hidden: true },
							{ text: uiLabelMap.ProductId, datafield: 'productCode', width: 130, editable: false },
							{ text: uiLabelMap.ProductName, dataField: 'productName', editable: false, minwidth: 120 },
							
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
							{ text: uiLabelMap.Quantity, datafield: 'returnQuantity', width: 120, filterable: false, cellsalign: 'right', columntype: 'numberinput',
								cellsrenderer: function(row, column, value) {
									if (value){
										return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
									}
								}, 
							},
							{text: uiLabelMap.BSReturnPrice, dataField: 'returnPrice', width: 140, cellsalign: 'right',
								editable: true, filterable:false, sortable:false, 
							 	cellsrenderer: function(row, column, value){
							 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
										returnVal += formatcurrency(value, currencyUomId, true) + '</div>';
										return returnVal;
							 	},
							 	validation: function (cell, value) {
									if (value < 0) {
										return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
									}
									return true;
								}
							},
							{text: uiLabelMap.Description, dataField: 'description', editable: true, filterable: false,},
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
							}"
							/>

<@jqGrid filtersimplemode="true" id="jqxgridProductSelected" filterable="false" dataField=dataFieldConfirms
	columnlist=columnlistConfirm editable="false" showtoolbar="false"
	url="" editmode="click" selectionmode="multiplecellsadvanced"/>