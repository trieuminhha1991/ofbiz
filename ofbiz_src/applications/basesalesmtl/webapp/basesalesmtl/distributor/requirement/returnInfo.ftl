${setContextField("gridProductItemsId", "jqxgridSO")}
<#assign dataField = "[
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
					{name: 'parentProductId', type: 'string'},
					{name: 'parentProductCode', type: 'string'},
			   		{name: 'productName', type: 'string'},
			   		{name: 'quantityUomId', type: 'string'},
			   		{name: 'packingUomIds', type: 'string'},
			   		{name: 'isVirtual', type: 'string'},
			   		{name: 'isVariant', type: 'string'},
			   		{name: 'parentProductId', type: 'string'},
			   		{name: 'features', type: 'string'},
			   		{name: 'colorCode', type: 'string'},
			   		{name: 'returnReason', type: 'string'},
			   		{name: 'quantity', type: 'number', formatter: 'integer'},
			   		{name: 'returnPrice', type: 'number', formatter: 'float'},
			   		{name: 'quantityReturnPromo', type: 'number', formatter: 'integer'},
			   		{name: 'productAvailable', type: 'string'}]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 150, editable:false, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', editable: false, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'quantityUomId', width: 150, columntype: 'dropdownlist', cellClassName: cellClass${gridProductItemsId},
					cellsrenderer: function(row, column, value){
						var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				 		var resultVal = value;
				 		if (data != undefined && data != null) {
				 			var productId = data.productId;
				 			var idStr = data.productId + '@' + data.quantityUomId;
				 			var itemMapValue = productOrderMap[idStr];
							if (typeof(itemMapValue) != 'undefined' && OlbCore.isNotEmpty(itemMapValue.quantityUomId)) {
				   				data.productUomId = itemMapValue.quantityUomId;
				   				resultVal = itemMapValue.quantityUomId;
				   			}
			   			}
			   			for (var i = 0 ; i < uomData.length; i++){
							if (resultVal == uomData[i].uomId){
								returnVal += uomData[i].description + '</div>';
		   						return returnVal;
							}
						}
			   			returnVal += value + '</div>';
		   				return returnVal;
					},
				 	initeditor: function (row, cellvalue, editor) {
				 		var packingUomData = new Array();
						var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);

						var itemSelected = data['quantityUomId'];
						var packingUomIdArray = data['packingUomIds'];
						for (var i = 0; i < packingUomIdArray.length; i++) {
							var packingUomIdItem = packingUomIdArray[i];
							var row = {};
							if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
								row['description'] = '' + packingUomIdItem.uomId;
							} else {
								row['description'] = '' + packingUomIdItem.description;
							}
							row['uomId'] = '' + packingUomIdItem.uomId;
							packingUomData[i] = row;
						}
				 		var sourceDataPacking = {
			                localdata: packingUomData,
			                datatype: 'array'
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
			            editor.jqxDropDownList('selectItem', itemSelected);
			      	}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSReturnPrice)}', datafield: 'returnPrice', sortable: false, width: 130, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput',
						cellClassName: cellClass${gridProductItemsId},
						cellsrenderer: function(row, column, value){
							var rowData = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
								var item = mapPriceEdit[rowData.productId];
								if (item) {
									value = item;
								}
								if (value) {
									return '<span style=\"text-align: right\" title=\"'+formatcurrency(value)+'\">' + formatcurrency(value) +'</span>';
								}
						},
						initeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 3 });
						},
						cellendedit: ReceiveReturn.cellendedit
				},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', width: 150, cellsalign: 'right', filterable:false, sortable: false,
			 		cellClassName: cellClass${gridProductItemsId}, columntype: 'numberinput', cellsformat: 'd',
			 		cellsrenderer: function(row, column, value){
				 		var rowData = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
								var item = mapProductSelected[rowData.productId];
								if (item) {
									value = item;
								} 
								if (value) {
									return '<span style=\"text-align: right\">' + value +'</span>';
								} 
								return value;
				 	},
					validation: function (cell, value) {
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
						}
						return true;
					},
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({decimalDigits: 0, digits: 9});
					},
					cellendedit: ReceiveReturn.cellendedit
			 	},
			 	{text: '${StringUtil.wrapString(uiLabelMap.LogRejectReasonReturnProduct)}', dataField: 'returnReason', columntype: 'dropdownlist', width: 200, editable: true, filterable:false, sortable: false, cellClassName: cellClass${gridProductItemsId},
			 		cellsrenderer: function(row, column, value){
			 			var rowData = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
						var item = mapReasonEdit[rowData.productId];
							if (item) {
								value = item;
							}
						if (value){
						value?value=mapReturnReason[value]:value;
						return '<span title=' + value +'>' + value + '</span>';}
					},
			 		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDropDownList({source: returnReasons, valueMember: 'returnReasonId', displayMember:'description', placeHolder: multiLang.filterchoosestring, autoDropDownHeight: false });
					},
					cellendedit: ReceiveReturn.cellendedit
			 	}"/>

<#if !viewSize?exists><#assign viewSize="15"/></#if>
<#if !otherParamUrl?exists><#assign otherParamUrl = "productStoreId=${defaultProductStoreId?if_exists}&hasrequest=Y${prodCatalogIdStr?if_exists}"/></#if>

<@jqGrid id=gridProductItemsId clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
viewSize=viewSize showtoolbar="true" editmode="click" bindresize="false"
url="jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&${otherParamUrl}" selectionmode="checkbox" />

