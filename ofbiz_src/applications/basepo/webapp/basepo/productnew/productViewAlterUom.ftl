<#assign listQuantityUom = delegator.findByAnd("Uom", {"uomTypeId", "PRODUCT_PACKING"}, null, false)!/>
<script type="text/javascript">
	var quantityUomData = [
		<#if listQuantityUom?has_content>
			<#list listQuantityUom as item>
			{	uomId: "${item.uomId}",
				abbreviation: "${item.abbreviation?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
			</#list>
		</#if>
	];
</script>
<div id="alteruom-tab" class="tab-pane<#if activeTab?exists && activeTab == "alteruom-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<#assign dataField = "[
						{name: 'productId', type: 'string'}, 
						{name: 'uomFromId', type: 'string'}, 
						{name: 'uomToId', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
						{name: 'quantityConvert', type: 'number'}, 
						{name: 'barcode', type: 'string'},
						{name: 'price', type: 'number'},
						{name: 'taxAmount', type: 'number'},
						{name: 'priceTotal', type: 'number'},
					]"/>
			<#assign columnlist = "
						{text: '${uiLabelMap.BSUomFromId}', dataField: 'uomFromId', width: 140, 
							cellsrenderer: function(row, column, value){
						 		var returnVal = '<div class=\"innerGridCellContent\">';
					   			for (var i = 0 ; i < quantityUomData.length; i++){
									if (value == quantityUomData[i].uomId){
										returnVal += quantityUomData[i].description + '</div>';
				   						return returnVal;
									}
								}
					   			returnVal += value + '</div>';
				   				return returnVal;
							},
						}, 
						{text: '${uiLabelMap.BSUomToId}', dataField: 'uomToId', width: 140, 
							cellsrenderer: function(row, column, value){
						 		var returnVal = '<div class=\"innerGridCellContent\">';
					   			for (var i = 0 ; i < quantityUomData.length; i++){
									if (value == quantityUomData[i].uomId){
										returnVal += quantityUomData[i].description + '</div>';
				   						return returnVal;
									}
								}
					   			returnVal += value + '</div>';
				   				return returnVal;
							},
						}, 
						{text: '${uiLabelMap.BSQuantityConvert}', dataField: 'quantityConvert', width: 140, cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd',
							cellsrenderer: function(row, column, value) {
						 		var str = '<div class=\"innerGridCellContent align-right\">';
						 		if (typeof(value) != 'undefined') {
							 		str += formatnumber(value);
						 		} else {
									str += value;
								}
								str += '</div>';
								return str;
						 	},
						},
						{text: '${uiLabelMap.BSBarcode}', dataField: 'barcode', minwidth: 220, columntype: 'input', 
							cellsrenderer: function(row, column, value) {
						 		var str = '<div class=\"innerGridCellContent align-right\">';
								str += value;
								str += '</div>';
								return str;
						 	}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${uiLabelMap.BSBeforeVAT}', datafield: 'price', width: 140, columntype: 'numberinput', cellsformat: 'd',
							cellsrenderer: function (row, column, value) {
						        if (OlbCore.isNotEmpty(value)) {
									return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
								} else {
									return '<div class=\"text-right\"></div>';
								}
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${StringUtil.wrapString(uiLabelMap.BSTax)}', datafield: 'taxAmount', width: 140, columntype: 'numberinput', cellsformat: 'd',
							cellsrenderer: function (row, column, value) {
								if (OlbCore.isNotEmpty(value)) {
									return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
								} else {
									return '<div class=\"text-right\"></div>';
								}
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${uiLabelMap.BSAfterVAT}', datafield: 'priceTotal', width: 160, columntype: 'numberinput', cellsformat: 'd',
							cellsrenderer: function (row, column, value) {
								var resultValue;
								var data = $('#jqxGridAlterUom').jqxGrid('getrowdata', row);
								if (data) {
									resultValue = data.price;
									if (data.taxAmount) resultValue += data.taxAmount;
								}
								if (OlbCore.isNotEmpty(resultValue)) {
									return '<div class=\"text-right\">' + resultValue.toLocaleString(locale) + '</div>';
								} else {
									return '<div class=\"text-right\"></div>';
								}
						    }
						},
					"/>
					<#--
					{text: '${uiLabelMap.BSUnitPrice}', dataField: 'price', minwidth: 180, cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd',
						cellsrenderer: function(row, column, value) {
					 		var str = '<div class=\"innerGridCellContent align-right\">';
					 		if (typeof(value) != 'undefined') {
						 		str += formatnumber(value, '${locale}', 3);
					 		} else {
								str += value;
							}
							str += '</div>';
							return str;
					 	}
					},
					-->
			<@jqGrid id="jqxGridAlterUom" url="jqxGeneralServicer?sname=JQListProductAlterUom&productId=${product?if_exists.productId?if_exists}" columnlist=columnlist dataField=dataField 
					viewSize="15" showtoolbar="false" filterable="false" filtersimplemode="true" showstatusbar="false" 
					deleterow="false" removeUrl="" deleteColumn="" 
					customcontrol1="" mouseRightMenu="false" contextMenuId="" bindresize="false"/>
		</div>
	</div>
</div>