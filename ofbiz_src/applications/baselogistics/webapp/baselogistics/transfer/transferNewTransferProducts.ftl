<div id="products">
<#assign dataFields = "[
				{name: 'productId', type: 'string'},
				{name: 'productCode', type: 'string'},
				{name: 'parentProductId', type: 'string'},
		   		{name: 'productName', type: 'string'},
		   		{name: 'quantityUomId', type: 'string'},
		   		{name: 'packingUomIds', type: 'string'},
		   		{name: 'weightUomId', type: 'string'},
		   		{name: 'uomId', type: 'string'},
		   		{name: 'weightUomIds', type: 'string'},
		   		{name: 'listInventoryItems', type: 'string'},
		   		{name: 'parentProductId', type: 'string'},
		   		{name: 'expiredDateChoose', type: 'string'},
		   		{name: 'expiredDate', type: 'date', other: 'Timestamp'},
		   		{name: 'unitCost', type: 'number'},
		   		{name: 'quantity', type: 'number', formatter: 'integer'},
		   		{name: 'quantityOnHandTotal', type: 'number', formatter: 'integer'},
		   		{name: 'amountOnHandTotal', type: 'number'},
		   		{name: 'description', type: 'string'},
		   		{name: 'requireAmount', type: 'string'},
			]"/>
<#assign columnlists = "
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', dataField: 'productCode', width: '15%', editable:false},
				{text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', dataField: 'productName', minwidth: '15%', editable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.QOH)}', dataField: 'quantityOnHandTotal', editable: false,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount) {
							value = data.amountOnHandTotal;
							return '<span>' + formatnumber(value, null, 2) + '</span>';
						} else {
							return '<span>' + formatnumber(value) + '</span>';
						}
					},
				},
				{text: '${StringUtil.wrapString(uiLabelMap.Unit)}', editable: false, dataField: 'uomId', width: '10%', columntype: 'dropdownlist',  filterable:false,  cellclassname: cellclassname,
					cellsrenderer: function(row, column, value){
						return '<span title=' + getUomDescription(value) + '>' + getUomDescription(value) + '</span>';
					},
				 	initeditor: function (row, cellvalue, editor) {
				 		var uomData = new Array();
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						var itemSelected = data['quantityUomId'];
						var uomIdArray = data['packingUomIds'];
						if (requireAmount && requireAmount == 'Y') {
							itemSelected = data['weightUomId'];
							uomIdArray = data['weightUomIds'];
						}
						for (var i = 0; i < uomIdArray.length; i++) {
							var uomId = uomIdArray[i];
							var row = {};
							if (uomId != undefined || uomId != '' || uomId != null) {
								row['description'] = '' + getUomDescription(uomId);
								row['uomId'] = '' + uomId;
								uomData[i] = row;
							}
						}
				 		var sourceDataUom = {
			                localdata: uomData,
			                datatype: 'array'
			            };
			            var dataAdapterUom = new $.jqx.dataAdapter(sourceDataUom);
			            editor.jqxDropDownList({ source: dataAdapterUom, displayMember: 'description', valueMember: 'uomId'});
			            editor.jqxDropDownList('selectItem', itemSelected);
			      	}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.UnitPrice)}', editable: false, dataField: 'unitCost', width: '12%', cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd',  cellclassname: cellclassname,
			 		cellsrenderer: function(row, column, value){
			 			if (value != undefined && value != null && value != ''){
			 				return '<span style=\"text-align: right;\">' + formatnumber(value, null, 2) + '</span>';
			 			} else {
			 				if (listProductSelected.length > 0){
								var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
								for (var t = 0; t < listProductSelected.length; t ++){
									var olb = listProductSelected[t]; 
									if (olb.productId == data.productId){
										if (olb.unitCost != null && olb.unitCost != undefined && olb.unitCost != ''){
											return '<span style=\"text-align: right;\">' +  formatnumber(olb.unitCost, null, 2) + '</span>';
										} else {
											var tmp = 0;
											return '<span style=\"text-align: right;\">' +  formatnumber(tmp) + '</span>';
										}
										break;
									}
								}
								return '<span style=\"text-align: right;\">' +  formatnumber(0) + '</span>';
							}
			 			}
				 	},
					validation: function (cell, value) {
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
						}
						return true;
					},
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2});
					}
			 	},
			 	{text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', dataField: 'quantity', width: '12%', cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd', cellclassname: cellclassname,  
			 		cellsrenderer: function(row, column, value){
			 			if (value != undefined && value != null && value != ''){
			 				return '<span style=\"text-align: right;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
			 			} else {
			 				if (listProductSelected.length > 0){
								var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
								for (var t = 0; t < listProductSelected.length; t ++){
									var olb = listProductSelected[t];
									if (olb.productId == data.productId){
										return '<span style=\"text-align: right;\">' +  formatnumber(olb.quantity) + '</span>';
										break;
									}
								}
								return '<span></span>';
							}
			 			}
				 	},
					validation: function (cell, value) {
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
						}
						return true;
					},
					initeditor: function (row, cellvalue, editor) {
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && requireAmount == 'Y') {
							editor.jqxNumberInput({decimalDigits: 2, digits: 9});
						} else {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
					}
			 	}
			"/>
	<@jqGrid id="jqxgridProduct" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlists dataField=dataFields 
		viewSize="10" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" width="100%" bindresize="false" 
		url="" 
	/>
</div>

<script type="text/javascript" src="/logresources/js/transfer/tranferNewTransferListProduct.js?v=2.0.5"></script>