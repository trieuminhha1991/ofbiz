<div class="row-fluid">
	<div class="span12">
		<#if product.productTypeId == "AGGREGATED">
			<#assign dataFieldProductChildren = "[
						{name: 'productId', type: 'string'}, 
						{name: 'productCode', type: 'string'}, 
						{name: 'productName', type: 'string'}, 
						{name: 'quantityUomId', type: 'string'}, 
						{name: 'quantity', type: 'number'}, 
						{name: 'amount', type: 'number'}, 
						{name: 'sequenceNum', type: 'number'}, 
					]"/>
			<#assign columnlistProductChildren = "
						{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '14%',
							cellsrenderer: function(row, colum, value) {
								var rowData = $('#jqxgridProductChildren').jqxGrid('getrowdata', row);
								return \"<span><a href='viewProduct?productId=\" + rowData.productId + \"'>\" + value + \"</a></span>\";
							}
						}, 
						{text: '${uiLabelMap.BSProductName}', dataField: 'productName', minWidth: '14%'}, 
						{text: '${uiLabelMap.BSUnitUom}', dataField: 'quantityUomId', width: '14%'}, 
						{text: '${uiLabelMap.BSQuantity}', dataField: 'quantity', width: '14%', columntype: 'numberinput', cellsformat: 'd',
					 		cellsrenderer: function(row, column, value){
						 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
					   			returnVal += formatnumber(value) + '</div>';
				   				return returnVal;
						 	}
					 	}, 
						{text: '${uiLabelMap.BSWeight}', dataField: 'amount', width: '14%', columntype: 'numberinput', cellsformat: 'd',
					 		cellsrenderer: function(row, column, value){
						 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
					   			returnVal += formatnumber(value, '${locale}', 3) + '</div>';
				   				return returnVal;
						 	}
					 	}, 
						{text: '${uiLabelMap.BSSequenceNum}', dataField: 'sequenceNum', width: '14%', columntype: 'numberinput', cellsformat: 'd',
					 		cellsrenderer: function(row, column, value){
						 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
					   			returnVal += formatnumber(value) + '</div>';
				   				return returnVal;
						 	}
					 	}, 
					"/>
			<@jqGrid id="jqxgridProductChildren" url="jqxGeneralServicer?sname=JQGetListProdConfigItemProduct&productId=${parameters.productId?if_exists}" columnlist=columnlistProductChildren dataField=dataFieldProductChildren 
					viewSize="20" showtoolbar="true" filterable="false" filtersimplemode="true" showstatusbar="false" deleterow="false" 
					customcontrol1="" mouseRightMenu="false" contextMenuId="" bindresize="false"/>
		<#else>
			<#assign dataFieldProductChildren = "[
						{name: 'productId', type: 'string'}, 
						{name: 'productCode', type: 'string'}, 
						{name: 'productName', type: 'string'}, 
						{name: 'feature', type: 'string'}, 
						{name: 'quantityUomId', type: 'string'}, 
						{name: 'primaryCategoryId', type: 'string'}, 
					]"/>
			<#assign columnlistProductChildren = "
						{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '14%',
							cellsrenderer: function(row, colum, value) {
								var rowData = $('#jqxgridProductChildren').jqxGrid('getrowdata', row);
								return \"<span><a href='viewProduct?productId=\" + rowData.productId + \"'>\" + value + \"</a></span>\";
							}
						}, 
						{text: '${uiLabelMap.BSProductName}', dataField: 'productName', minWidth: '14%'}, 
						{text: '${uiLabelMap.BSProductFeature}', dataField: 'feature', width: '14%'}, 
						{text: '${uiLabelMap.BSUnitUom}', dataField: 'quantityUomId', width: '14%'}, 
						{text: '${uiLabelMap.BSPrimaryCategory}', dataField: 'primaryCategoryId', width: '14%'}, 
					"/>
			<@jqGrid id="jqxgridProductChildren" url="jqxGeneralServicer?sname=JQGetListProductChildren&productId=${parameters.productId?if_exists}" columnlist=columnlistProductChildren dataField=dataFieldProductChildren 
					viewSize="20" showtoolbar="true" filterable="false" filtersimplemode="true" showstatusbar="false" deleterow="false" 
					customcontrol1="" mouseRightMenu="false" contextMenuId="" bindresize="false"/>
		</#if>
	</div>
</div>