<#include "script/listProductScript.ftl"/>
<script>
var productGridCellclass= function (row, column, value, data) {
        if (column == 'expiredDate' || column == 'expiredDateChoose' || column == 'description' || column == 'quantityOnHandTotal' || column == 'quantity' ) {
            return 'background-prepare';
        }
    }
</script>

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
				{text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', dataField: 'productName', minwidth: '15%', editable: false},">
				<#if displayQOH == "Y">
				<#assign columnlists = columnlists +"
				{text: '${StringUtil.wrapString(uiLabelMap.QOH)}', dataField: 'expiredDateChoose', width: '14%', cellsalign: 'right', columntype: 'dropdownlist', filterable:false, sortable: false, cellclassname: productGridCellclass,
			 		cellsrenderer: function(row, column, value){
		 				var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
		 				if (data.requireAmount == 'Y') {
		 					value = data.amountOnHandTotal;
		 				} else {
		 					value = data.quantityOnHandTotal;
		 				}
		 				return '<span style=\"text-align: right;\" title=\"'+uiLabelMap.ChooseExpireDate+'\">' +  formatnumber(value) + ' <i class=\"fa fa-sort-desc\"></i></span>';
				 	},
				 	initeditor: function (row, cellvalue, editor) {
				 		var expireDateData = new Array();
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						var listInventoryItems = data['listInventoryItems'];
						var listExpiredDate = [];
						var listExpiredDateTmp = [];
						for (var i = 0; i < listInventoryItems.length; i++) {
							var check = false;
							for (var j = 0; j < listExpiredDateTmp.length; j++) {
								if (listInventoryItems[i].expireDate) {
									if (listExpiredDateTmp[j] == listInventoryItems[i].expireDate){
										check = true;
										break;
									}
								}
							}
							if (!check){
								listExpiredDateTmp.push(listInventoryItems[i].expireDate);
							}
						}
						if (listExpiredDateTmp.length > 0){
							for (var j = 0; j < listExpiredDateTmp.length; j++) {
								var qoh = 0; 
								for (var i = 0; i < listInventoryItems.length; i++) {
									if (listExpiredDateTmp[j] == listInventoryItems[i].expireDate){
										if (requireAmount && requireAmount == 'Y') {
											qoh = qoh + listInventoryItems[i].amountOnHandTotal;
										} else {
											qoh = qoh + listInventoryItems[i].quantityOnHandTotal;
										}
									}
								}
								var h = {};
								if (listExpiredDateTmp[j]) {
									h = {
										expireDate: listExpiredDateTmp[j],
										qoh: qoh,
									}
								} else {
									h = {
										expireDate: '_NA_',
										qoh: qoh,
									}
								}
								listExpiredDate.push(h);
							}
						}
						for (var i = 0; i < listExpiredDate.length; i++) {
							var expireDate = listExpiredDate[i].expireDate;
							var qoh = listExpiredDate[i].qoh;
							var obj = {};
							if (expireDate && expireDate != '_NA_'){
								obj['description'] = DatetimeUtilObj.getFormattedDate(new Date(expireDate)) + ' - QOH: ' + formatnumber(qoh);
								obj['expireDate'] = expireDate;
							} else {
								obj['description'] = '${uiLabelMap.ProductMissExpiredDate}' + ' - QOH: ' + formatnumber(qoh);
								obj['expireDate'] = '_NA_';
							}
							expireDateData[i] = obj;
						}
				 		var sourceDataExpireDate = {
			                localdata: expireDateData,
			                datatype: 'array'
			            };
			            var dataAdapterExpireDate = new $.jqx.dataAdapter(sourceDataExpireDate);
			            editor.off('change');
			            editor.jqxDropDownList({autoDropDownHeight: true, selectedIndex: -1, placeHolder: uiLabelMap.ChooseExpireDate, source: dataAdapterExpireDate, displayMember: 'description', valueMember: 'expireDate',
			            });
			            var id = data.uid;
			            editor.on('change', function (event){
							var args = event.args;
				     	    if (args) {
			     	    		var item = args.item;
				     		    if (item){
				     		    	var qoh = 0;
				     		    	for (var i = 0; i < listExpiredDate.length; i ++) {
				     		    		if (listExpiredDate[i].expireDate == item.originalItem.expireDate) {
				     		    			qoh = listExpiredDate[i].qoh;
				     		    			break;
				     		    		}
				     		    	}
				     		    	$('#jqxgridProduct').jqxGrid('setcellvaluebyid', id, 'quantity', qoh);
			     		    		$('#jqxgridProduct').jqxGrid('begincelledit', row, 'quantity');
				     		    	var key = 'row' + data.productId;
				     		    	if (item.originalItem.expireDate && item.originalItem.expireDate != '_NA_'){
				     		    		$('#jqxgridProduct').jqxGrid('setcellvaluebyid', id, 'expiredDate', item.originalItem.expireDate);
					     		    	var tmp = DatetimeUtilObj.getFormattedDate(new Date(item.originalItem.expireDate)) + ' - ' + DatetimeUtilObj.getFormattedDate(new Date(item.originalItem.expireDate));
										glEditorId[''+key] = tmp;
									} else {
										delete glEditorId[''+key];
				     		    		$('#jqxgridProduct').jqxGrid('setcellvaluebyid', id, 'expiredDate', null);
									}
				     		    }
				     	    }
				        });
			      	}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.QOH)}', dataField: 'quantityOnHandTotal', cellclassname: productGridCellclass
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
				"/>
				<#else>
					<#assign columnlists = columnlists + "
						{text: '${StringUtil.wrapString(uiLabelMap.QOH)}', dataField: 'quantityOnHandTotal', hidden: true},
					">
				</#if>
				<#assign columnlists = columnlists +"
				{text: '${StringUtil.wrapString(uiLabelMap.Unit)}', dataField: 'uomId', width: '10%', columntype: 'dropdownlist',  filterable:false,
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
				{ text: '${uiLabelMap.ExpireDate}', hidden: true, dataField: 'expiredDate', columntype: 'custom', cellsformat: 'dd/MM/yyyy', width: 200, editable: true, sortable: false, filterable: false, cellclassname: productGridCellclass,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if (glEditorId != null){
							var key = 'row' + data.productId;
							if (glEditorId[''+key]){
								return '<span style=\"text-align: right;\">' +glEditorId[''+key] + '</span>';
							}
							return '<span></span>';
						} 
						return '<span></span>';
					},
					cellendedit: function (row, b, c ,o,n){
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var key = 'row' + data.productId;
						glEditorId[''+key] = $('#'+ glEditorId[''+key]).val();
					},
				 	initeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({clearString: '${uiLabelMap.Clear}', width: 200, height: '25px', formatString: 'dd/MM/yyyy', selectionMode: 'range' });
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var key = 'row' + data.productId;
						if (editor[0].id){
							glEditorId[''+key] = editor[0].id;
						}
				 	},
				},">
				<#if displayCost == "Y">
				<#assign columnlists = columnlists + " 
				{text: '${StringUtil.wrapString(uiLabelMap.UnitPrice)}', dataField: 'unitCost', width: '12%', cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd',
			 		cellsrenderer: function(row, column, value){
			 			if (value != undefined && value != null && value != ''){
			 				return '<span style=\"text-align: right;\" title=' + formatnumber() + '>' + formatnumber(value) + '</span>';
			 			} else {
			 				if (listProductSelected.length > 0){
								var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
								for (var t = 0; t < listProductSelected.length; t ++){
									var olb = listProductSelected[t];
									if (olb.productId == data.productId){
										if (olb.unitCost != null && olb.unitCost != undefined && olb.unitCost != ''){
											return '<span style=\"text-align: right;\">' +  formatnumber(olb.unitCost) + '</span>';
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
						editor.jqxNumberInput({decimalDigits: 0, digits: 9});
					}
			 	},">
			 	<#else>
					<#assign columnlists = columnlists + "
						{text: '${StringUtil.wrapString(uiLabelMap.UnitPrice)}', dataField: 'unitCost', hidden: true},
					">
			 	</#if>
				<#if displayDescription?exists && displayDescription = "Y">
					<#assign columnlists = columnlists + "{text: '${StringUtil.wrapString(uiLabelMap.Description)}', dataField: 'description', width: '14%', filterable:false, sortable:false, cellclassname: productGridCellclass},"/>
				</#if>
			 	<#assign columnlists = columnlists + "
			 	{text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', dataField: 'quantity', width: '12%', cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd', cellclassname: productGridCellclass,
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
<#if fixUrl?has_content>
	<@jqGrid id="jqxgridProduct" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlists dataField=dataFields 
		viewSize="10" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" width="100%" bindresize="false" 
		url="" 
	/>
<#else>
	<@jqGrid id="jqxgridProduct" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlists dataField=dataFields 
		viewSize="10" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" width="100%" bindresize="false" 
		url="jqxGeneralServicer?sname=JQGetListProductByOrganiztion&orgPartyId=${company}" 
	/>
</#if>
</div>