<script>

</script>
<#assign dataField1="[{ name: 'transferId', type: 'string' },
					{ name: 'transferItemSeqId', type: 'string' },
					{ name: 'transferItemTypeId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantity', type: 'string' },
					{ name: 'quantityToDelivery', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'expireDate', type: 'date', other: 'Timestamp' },
					{ name: 'quantityOnHandTotal', type: 'number'},
					{ name: 'availableToPromiseTotal', type: 'number'},
					{ name: 'originFacilityId', type: 'string' },
					{ name: 'destFacilityId', type: 'string' },
					{ name: 'baseQuantityUomId', type: 'string' },
					{ name: 'weight', type: 'number' },
					{ name: 'baseWeightUomId', type: 'string' },
					{ name: 'convertNumber', type: 'number' },
		 		 	]"/>
<#assign columnlist1="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
				        groupable: false, draggable: false, resizable: false,
				        datafield: '', columntype: 'number', width: 50,
				        cellsrenderer: function (row, column, value) {
				            return '<span style=margin:4px;>' + (value + 1) + '</span>';
				        }
				    },
					{ text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 200, filtertype:'input', editable: false, pinned: true},
					 { text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 150, filtertype:'input', editable: false, 
					 },
					 { text: '${uiLabelMap.QuantityNeedToTransfer}', dataField: 'quantity', width: 200, editable: false, cellsalign: 'right'
					 },
					 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 120, editable: false,
						 cellsrenderer: function (row, column, value){
							 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
							 for (var i=0; i < packingUomData.length; i++){
								 if (value == packingUomData[i].uomId){
									 return '<span>'+packingUomData[i].description+'</span>';
								 }
							 }
						 }
					 },
//					 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', filtertype: 'date', cellsalign: 'right',},
					 { text: '${uiLabelMap.QuantityToTransfer}', dataField: 'quantityToDelivery', width: 200, align: 'center', cellsalign: 'right', columntype: 'numberinput', editable: true,
						 validation: function (cell, value) {
	                    	 var data = $('#jqxgridProduct').jqxGrid('getrowdata', cell.row);
	                    	 if (value <= 0) {
	                             return { result: false, message: '${StringUtil.wrapString(uiLabelMap.QuantityMustBeGreateThanZero)}'};
	                         }
	                         if (value > data.quantity){
	                        	 return { result: false, message: '${StringUtil.wrapString(uiLabelMap.QuantityCantNotGreateThanQuantityNeedTransfer)}'};
	                         }
	                         return true;
	                     },
	                     createeditor: function (row, cellvalue, editor) {
	                         editor.jqxNumberInput({ decimalDigits: 0, digits: 10});
	                     },
	                     initeditor: function(row, value, editor){
	                    	 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
	                    	 if (data.quantity){
	                          	editor.jqxNumberInput('val', data.quantity);
	                          }
	                     },
	                     cellsrenderer: function (row, column, value){
//	                    	 updateTotalWeight();
	                    	 if (value){
	                    		 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
	                    	 } else {
	                    		 	var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
                    		 		var id = data.uid;
                    		 		var requiredQty = data.quantity;
                    		 		$('#jqxgridProduct').jqxGrid('setcellvaluebyid', id, 'quantityToDelivery', requiredQty);
                    		 		return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + requiredQty.toLocaleString('${localeStr}') + '>' + requiredQty.toLocaleString('${localeStr}') + '</span>';
	                    	 }
	                     }
					 },
//					 { text: '${uiLabelMap.QOH}', dataField: 'quantityOnHandTotal', minwidth: 150, editable: false, align: 'center', 
//	 						cellsrenderer: function(row, column, value){
//		 					var baseUomDes;
//		 					var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//		 					for (var i=0; i < packingUomData.length; i++){
//								 if (data.baseQuantityUomId == packingUomData[i].uomId){
//									 baseUomDes = packingUomData[i].description;
//								 }
//		 					}
//		 					var localeQuantity = parseInt(value);
//							return '<span style=\"text-align: right\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + ' ('+baseUomDes+')</span>'
//	 						}
//		 			 },
//					 { text: '${uiLabelMap.ATP}', dataField: 'availableToPromiseTotal', minwidth: 150, editable: false, align: 'center',
//		 				cellsrenderer: function(row, column, value){
//		 					var baseUomDes;
//		 					var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//		 					for (var i=0; i < packingUomData.length; i++){
//								 if (data.baseQuantityUomId == packingUomData[i].uomId){
//									 baseUomDes = packingUomData[i].description;
//								 }
//		 					}
//		 					var localeQuantity = parseInt(value);
//							return '<span style=\"text-align: right\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + ' ('+baseUomDes+')</span>'
//						}
//		 			 },
//					 { text: '${uiLabelMap.TransferItemType}', dataField: 'transferItemTypeId', minwidth: 150, editable: false,
//			 				cellsrenderer: function(row, column, value){
//								for(var i = 0; i < transferItemTypeData.length; i++){
//									if(transferItemTypeData[i].transferItemTypeId == value){
//										return '<span title=' + value + '>' + transferItemTypeData[i].description + '</span>'
//									}
//								}
//								return value;
//							}
//			 			 },
					 "/>
<@jqGrid filtersimplemode="true" width="100%" selectionmode="checkbox" id="jqxgridProduct" usecurrencyfunction="true" addType="popup" dataField=dataField1
         columnlist=columnlist1 clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="false" editmode="click" editable="true" rowselectfunction=""
		 url="jqxGeneralServicer?sname=getListTransferItemDelivery&transferId=${parameters.transferId}" bindresize="false" pagesizeoptions="['5', '10', '15']" viewSize="5"	 
		 customTitleProperties="DeliveryItemList" autoheight="false" height="240" virtualmode="true"
/>
