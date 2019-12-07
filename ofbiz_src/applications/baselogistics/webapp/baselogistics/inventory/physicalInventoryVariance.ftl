<div id="variance-tab" class="tab-pane<#if activeTab?exists && activeTab == "variance-tab"> active</#if>">
<div class="row-fluid ">
	<div class="span12">
		<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
		    ${uiLabelMap.InventoryVarianceInPhysicalInventory}
		</h4>
		<div id="listInventoryItemVariance" style="width: 100%"></div>
	</div>
</div>
	<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, pinned:true, editable: false, groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<span>' + (value + 1) + '</span>';
		    }
		},
		{ text: '${uiLabelMap.ProductProductId}', dataField: 'productCode', width: 130, pinned:true,
		 },
		 { text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200,
		 },
		 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 100,
			 cellsrenderer: function (row, column, value){
			 	var data = $('#listInventoryItemVariance').jqxGrid('getrowdata', row);
			 	if (data.requireAmount && data.requireAmount == 'Y') {
			 		value = data.weightUomId;
			 	}
				return '<span title=' + getUomDescription(value) + '>' + getUomDescription(value) + '</span>'
			 }
		 },
		 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date',
			 cellsrenderer: function(row, column, value){
				if (!value){
					return '<span style=\"text-align: right\" title=\"${uiLabelMap.NotRequiredExpiredDate}\"></span>';
				} 
			 },
		 },
		 { text: '${uiLabelMap.Quantity}', dataField: 'quantityOnHandVar', width: 130,
			 cellsrenderer: function(row, column, value){
			 	var data = $('#listInventoryItemVariance').jqxGrid('getrowdata', row);
			 	if (data.requireAmount && data.requireAmount == 'Y') {
			 		value = data.amountOnHandVar;
			 	}
				if (value){
					return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
				} else {
					return '<span style=\"text-align: right\">'+0+'</span>';
				}
			},
		 },
		 { text: '${uiLabelMap.Reason}', dataField: 'description', width: 200,
			 cellsrenderer: function(row, column, value){
			},
		 },
		 { text: '${uiLabelMap.Description}', dataField: 'comments', width: 150,
			 cellsrenderer: function(row, column, value){
			},
		 },
	 "/>
		<#assign dataField="[{ name: 'productId', type: 'string'},
		{ name: 'productName', type: 'string'},
		{ name: 'productCode', type: 'string'},
		{ name: 'quantityUomId', type: 'string'},
		{ name: 'weightUomId', type: 'string'},
		{ name: 'requireAmount', type: 'string'},
		{ name: 'inventoryItemId', type: 'string'},
		{ name: 'lotId', type: 'string'},
		{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
		{ name: 'expireDate', type: 'date', other: 'Timestamp'},
		{ name: 'availableToPromiseVar', type: 'number'},
		{ name: 'quantityOnHandVar', type: 'number'},
		{ name: 'amountOnHandVar', type: 'number'},
		{ name: 'physicalInventoryId', type: 'string'},
		{ name: 'varianceReasonId', type: 'string'},
		{ name: 'comments', type: 'string'},
		{ name: 'description', type: 'string'},
		]"/>
<@jqGrid filtersimplemode="true" id="listInventoryItemVariance" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="false"
url="jqxGeneralServicer?sname=jqGetInventoryItemVarianceDetail&physicalInventoryId=${parameters.physicalInventoryId?if_exists}" editmode='click' selectionmode='multiplecellsadvanced'
/>
</div>