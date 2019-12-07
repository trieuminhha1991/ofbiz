<#assign dataField1="[{ name: 'orderId', type: 'string' },
                 	{ name: 'orderItemSeqId', type: 'string' },
                 	{ name: 'productId', type: 'string' },
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
					{ name: 'comments', type: 'string' },
					{ name: 'baseWeightUomId', type: 'string' },
					{ name: 'baseQuantityUomId', type: 'string' },
		 		 	]"/>
<#-- { text: '${uiLabelMap.prodCatalogId}', dataField: 'prodCatalogId', width: 150, editable: false},
{ text: '${uiLabelMap.OrderItemType}', dataField: 'orderItemTypeId', width: 150, editable: false,
    cellsrenderer: function(row, column, value){
        for(var i = 0; i < orderItemTypeData.length; i++){
            if(orderItemTypeData[i].orderItemTypeId == value){
                return '<span title=' + value + '>' + orderItemTypeData[i].description + '</span>'
            }
        }
        return value;
    }
 },{ text: '${uiLabelMap.facility}', dataField: 'facilityId', width: 150, editable: true,
     cellsrenderer: function(row, column, value){
         for(var i = 0; i < facilityData.length; i++){
             if(facilityData[i].facilityId == value){
                 return '<span title=' + value + '>' + facilityData[i].description + '</span>'
             }
         }
    }
 },{ text: '${uiLabelMap.unitPrice} ${uiLabelMap.beforeVAT}', dataField: 'unitPrice', width: 150, editable: false,
     cellsrenderer: function(row, column, value){
         var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
         return '<span>' + formatcurrency(value, data.currencyUomId) + '<span>';
     }
 },-->
<#assign columnlist1="{ text: '${uiLabelMap.STT}', width: 35, editable: false, pinned: true, cellsrenderer: function(row, column, value){
                            return '<span>' + (row + 1) + '</span>';
                      }},
                      { text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 150, editable: false, pinned: true},
                      { text: '${uiLabelMap.DAProductName}', dataField: 'productName', width: 300, filtertype:'input', editable: false, 
                      },
					 { text: '${uiLabelMap.RequiredOrderNumber}', dataField: 'requiredQuantity', width: 130, editable: false, 
                         cellsrenderer: function(row, column, value){
                             var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
                             var description = value;
                             for(var i = 0; i < uomData.length; i++){
                                  if(uomData[i].quantityUomId == data.quantityUomId){
                                      description = uomData[i].description;
                                  }
                              }
                             return '<span style=\"text-align: right\" title=' + value + '>' + value +' ('+ description + ')</span>';
					     }
					 },
					 { text: '${uiLabelMap.CreatedOrderNumber}', width: 125, editable: false, 
					     cellsrenderer: function(row, column, value){
					         var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
//					         var description = '';
//					         for(var i = 0; i < uomData.length; i++){
//					             if(uomData[i].quantityUomId == data.quantityUomId){
//					                 description = uomData[i].description;
//					             }
//					         }
//					         if(description != ''){
//					             return '<span style=\"text-align: right\">' + data.createdQuantity + '&nbsp;('  + description + ')</span>';
//					         }else{
					             return '<span style=\"text-align: right\">' + data.createdQuantity + '</span>';
//					         }
					     }
					 },
					 { text: '${uiLabelMap.quantity}', dataField: 'requiredQuantityTmp', columntype: 'numberinput', width: 140, editable: true,
		 				cellsrenderer: function(row, column, value){
		 					updateTotalWeight();
		 				   var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
//                           var description = '';
//                           for(var i = 0; i < uomData.length; i++){
//                               if(uomData[i].quantityUomId == data.quantityUomId){
//                                   description = uomData[i].description;
//                               }
//                           }
                           var requiredQuantityTmp = data.requiredQuantityTmp;
                           if (data.requiredQuantity && data.createdQuantity){
                        	   requiredQuantityTmp = data.requiredQuantity - data.createdQuantity;
                           }
//                           if(description != ''){
//                               return '<span style=\"text-align: right\">' + requiredQuantityTmp + '&nbsp;('  + description + ')</span>';
//                           }else{
                               return '<span style=\"text-align: right\">' + value + '</span>';
//                           }
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					        editor.jqxNumberInput({ decimalDigits: 0});
					        var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
					        if (data.requiredQuantity && data.createdQuantity){
					        	editor.jqxNumberInput('val', data.requiredQuantity - data.createdQuantity);
					        }
					    },validation: function (cell, value) {
					        var data = $('#jqxgrid1').jqxGrid('getrowdata', cell.row);
					        if(value > (data.requiredQuantity - data.createdQuantity)){
					            return { result: false, message: '${uiLabelMap.ExportValueLTZRequireValue}'};
					        }else{
					            return true;
					        }
					    },
					 },
					 { text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expireDate', width: 160, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: false,
					 },
					 { text: '${uiLabelMap.SummaryATP}', dataField: 'availableToPromiseTotal', minwidth: 150, editable: false, cellsalign: 'right', align: 'center',
			 				cellsrenderer: function(row, column, value){
			 					var baseUomDes;
			 					var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
			 					for (var i=0; i < uomData.length; i++){
									 if (data.baseQuantityUomId == uomData[i].quantityUomId){
										 baseUomDes = uomData[i].description;
									 }
			 					}
			 					var localeQuantity = parseInt(value);
								return '<span style=\"text-align: right\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + ' ('+baseUomDes+')</span>'
							}
			 			 },
					 { text: '${uiLabelMap.SummaryQOH}', dataField: 'quantityOnHandTotal', minwidth: 150, editable: false, cellsalign: 'right', align: 'center',
		 				cellsrenderer: function(row, column, value){
		 					var baseUomDes;
		 					var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
		 					for (var i=0; i < uomData.length; i++){
								 if (data.baseQuantityUomId == uomData[i].quantityUomId){
									 baseUomDes = uomData[i].description;
								 }
		 					}
		 					var localeQuantity = parseInt(value);
							return '<span style=\"text-align: right\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + ' ('+baseUomDes+')</span>'
						}
		 			 },
					 { text: '${uiLabelMap.comments}', dataField: 'comments', width: 300, editable: false}
		 "/>
<@jqGrid customTitleProperties="DeliveryItemList" width="890" autoheight="false" height="260" selectionmode="checkbox" id="jqxgrid1" usecurrencyfunction="true" dataField=dataField1 columnlist=columnlist1 
        clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="false" editmode="dblclick" editable="true" 
		url="" bindresize="false" viewSize="5" jqGridMinimumLibEnable="false" customLoadFunction="true"
		rowselectfunction="rowselectfunction2(event);" rowunselectfunction="rowsunelectfunction2(event);"
		/>