<@jqGridMinimumLib/>
<script  type="text/javascript">
	var orderId = '${parameters.orderId}';
	var deliveryId = '${parameters.deliveryId}';
	<#assign productList = delegator.findList("Product", null, null, null, null, false) />
	var productData = new Array();
	<#list productList as product>
		var row = {};
		row['productId'] = "${product.productId}";
		row['description'] = "${StringUtil.wrapString(product.get('description', locale)?if_exists)}";
		productData[${product_index}] = row;
	</#list>
	function getDescriptionByProductId(productId) {
		for ( var x in productData) {
			if (productId == productData[x].productId) {
				return productData[x].description;
			}
		}
	}
	
	<#assign uomList = delegator.findList("Uom", null, null, null, null, false) />
	var uomData = new Array();
	<#list uomList as uom>
		var row = {};
		row['uomId'] = "${uom.uomId}";
		row['description'] = "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}";
		uomData[${uom_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
</script>	
<#assign dataField="[
	{ name: 'productId', type: 'string'}, 
	{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
	{ name: 'alternativeQuantity', type: 'string'},
	{ name: 'quantityUomId', type: 'string'},
]"/>
<#assign columnlist="
	{ text: '${StringUtil.wrapString(uiLabelMap.accProductName)}', datafield: 'productId', editable:false,
		cellsrenderer: function(row, colum, value){
			var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
			var value = data.productId;
			var productId = getDescriptionByProductId(value);
			if(productId != undefined){
				return '<span>' + productId + '</span>';
			}
		    
	    },  
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ExpirationDate)}', datafield: 'actualExpireDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false, cellsalign: 'right',},
	{ text: '${StringUtil.wrapString(uiLabelMap.quantity)}', datafield: 'alternativeQuantity', editable:false,
		cellsrenderer: function(row, colum, value){
			if(value){
				return '<span style=\"text-align:right\">' + value.toLocaleString('${locale}') + '</span>';
			}
	    }, 
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', datafield: 'quantityUomId', editable:false,
		cellsrenderer: function(row, colum, value){
			var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
			var value = data.quantityUomId;
			var quantityUomId = getDescriptionByUomId(value);
			if(quantityUomId != undefined){
				return '<span style=\"text-align:right\">' + quantityUomId + '</span>';
			}
	    },  
	},
"/>

<@jqGrid filtersimplemode="true" id="jqxgridProduct" filterable="true"  dataField=dataField columnlist=columnlist  editable="true"  clearfilteringbutton="true" showtoolbar="true" editmode="click"
	url="jqxGeneralServicer?sname=JQXGetProductListInDelivery&orderId=${parameters.orderId}&deliveryId=${parameters.deliveryId?if_exists}"
/>