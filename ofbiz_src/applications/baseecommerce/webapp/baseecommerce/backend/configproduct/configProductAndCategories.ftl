<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/configProductAndCategories.js"></script>

<div class="row-fluid">
    <div class="pull-right" id="txtProductId"></div>
</div>

<#assign dataField="[{name: 'productId', type: 'string'},
					{name: 'productCategoryId', type: 'string'},
					{name: 'categoryName', type: 'string'},
					{name: 'isBestSell', type: 'bool'},
					{name: 'isPromos', type: 'bool'},
					{name: 'isNew', type: 'bool'},
					{name: 'bestSellFromDate', type: 'number'},
					{name: 'promosFromDate', type: 'number'},
					{name: 'newFromDate', type: 'number'}]"/>

<#assign columnlist="{text: '${uiLabelMap.DmsCategoryId}', dataField: 'productCategoryId', width: 250, editable: false},
					{text: '${uiLabelMap.DmsCategoryName}', dataField: 'categoryName', minWidth: 250, editable: false},
					{text: '${uiLabelMap.BSInTheListSelling}', dataField: 'isBestSell', columntype: 'checkbox', align: 'center', width: 200, filterable: false, sortable: false},
					{text: '${uiLabelMap.BSIsPromosProduct}', dataField: 'isPromos', columntype: 'checkbox', align: 'center', width: 200, filterable: false, sortable: false},
					{text: '${uiLabelMap.BSIsNewProduct}', dataField: 'isNew', columntype: 'checkbox', align: 'center', width: 200, filterable: false, sortable: false},
					"/>

<@jqGrid id="productAndCategory" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="" editable="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=configCategory"
	editColumns="productId;productCategoryId;isBestSell;isPromos;isNew;bestSellFromDate(java.lang.Long);promosFromDate(java.lang.Long);newFromDate(java.lang.Long)"/>

<#assign listProduct = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listProducts(delegator, userLogin)>
<script>
	var listProduct = [<#if listProduct?exists><#list listProduct as item>{
		productId: "${item.productId?if_exists}",
		productName: "<b>[${item.productId?if_exists}] </b>" + "${StringUtil.wrapString(item.productName?if_exists)}"
	},</#list></#if>];
</script>