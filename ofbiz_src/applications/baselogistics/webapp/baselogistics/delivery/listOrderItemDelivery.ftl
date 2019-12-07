<#assign dataFieldOrderItems="[{ name: 'orderId', type: 'string' },
                 	{ name: 'orderItemSeqId', type: 'string' },
                 	{ name: 'productId', type: 'string' },
                 	{ name: 'productCode', type: 'string' },
                 	{ name: 'productName', type: 'string' },
                 	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
					{ name: 'prodCatalogId', type: 'string' },
					{ name: 'productCategoryId', type: 'string' },
                 	{ name: 'orderItemTypeId', type: 'string' },
                 	{ name: 'resQuantity', type: 'number' },
                 	{ name: 'requiredQuantity', type: 'number' },
                 	{ name: 'createdQuantity', type: 'number' },
					{ name: 'quantityUomId', type: 'string'},
                 	{ name: 'facilityId', type: 'string' },
                 	{ name: 'requiredQuantityTmp', type: 'string' },
					{ name: 'unitPrice', type: 'number'},
					{ name: 'quantityOnHandTotal', type: 'number'},
					{ name: 'availableToPromiseTotal', type: 'number'},
					{ name: 'weight', type: 'number'},
					{ name: 'baseWeightUomId', type: 'string' },
					{ name: 'baseQuantityUomId', type: 'string' },
		 		 	]"/>
<#assign columnlistOrderItems="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
						datafield: '', columntype: 'number', width: 50,
						cellsrenderer: function (row, column, value) {
					    return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},	
                      { text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 150, editable: false, pinned: true},
                      { text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, filtertype:'input', editable: false, 
                      },
					 { text: '${uiLabelMap.OrderNumber}', dataField: 'requiredQuantity', width: 150, editable: false, 
                         cellsrenderer: function(row, column, value){
                             var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
                             var description = '';
                             if (data.quantityUomId){
                            	 for(var i = 0; i < uomData.length; i++){
                                     if(uomData[i].quantityUomId == data.quantityUomId){
                                         description = uomData[i].description;
                                     }
                            	 }
                            	 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') +' ('+ description + ')</span>';
                             } else if (data.baseQuantityUomId){
                            	 for(var i = 0; i < uomData.length; i++){
                                     if(uomData[i].quantityUomId == data.baseQuantityUomId){
                                         description = uomData[i].description;
                                     }
                            	 }
                            	 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') +' ('+ description + ')</span>';
                             }
                             return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
					     }
					 },
					 { text: '${uiLabelMap.CreatedOrderNumber}', width: 150, editable: false, 
					     cellsrenderer: function(row, column, value){
					         var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
					         var tmp = data.createdQuantity;
				             return '<span style=\"text-align: right\">' + tmp.toLocaleString('${localeStr}') + '</span>';
					     }
					 },
					 { text: '${uiLabelMap.QuantityCreate}', dataField: 'requiredQuantityTmp', columntype: 'numberinput', width: 150, editable: true,
			 				cellsrenderer: function(row, column, value){
	                           return '<span style=\"text-align: right; background-color: #deedf5\">' + value.toLocaleString('${localeStr}') + '</span>';
							},
							initeditor: function(row, value, editor){
						        editor.jqxNumberInput({ decimalDigits: 0});
						        var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
						        if (value === null || value === undefined || value === ''){
						        	if (data.requiredQuantity && data.createdQuantity){
							        	editor.jqxNumberInput('val', data.requiredQuantity - data.createdQuantity);
							        }
						        }
						    },
						    validation: function (cell, value) {
						        var data = $('#jqxgrid1').jqxGrid('getrowdata', cell.row);
						        if (value > (data.requiredQuantity - data.createdQuantity)){
						            return { result: false, message: '${uiLabelMap.ExportValueLTZRequireValue}'};
						        } else{
						        	if (value <= 0){
						        		return { result: false, message: '${uiLabelMap.ExportValueMustBeGreaterThanZero}'};
						        	} else {
						        		return true;
						        	}
						        }
						    },
					 },
					 { text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: false,
                   	  cellsrenderer: function(row, column, value){
							 if (value){
								 return '<span style=\"text-align: right\" title=\"' + value + '\">' + value + '</span>'
							 } else {
								 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>'
							 }
						 },	
					 },
		 "/>
<@jqGrid customTitleProperties="DeliveryItemList" width="100%" autoheight="false" height="240" selectionmode="checkbox" id="jqxgrid1" usecurrencyfunction="true" dataField=dataFieldOrderItems columnlist=columnlistOrderItems 
        clearfilteringbutton="false" showtoolbar="true" addrow="false" filterable="false" editmode="click" editable="true" 
		url="" bindresize="false" viewSize="5" jqGridMinimumLibEnable="false" customLoadFunction="true"
/>