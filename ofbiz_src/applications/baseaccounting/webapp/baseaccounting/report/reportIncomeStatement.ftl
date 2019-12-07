<div id="incomeStatementReport"></div>
<@jqOlbCoreLib hasCore=true />
<script>
	<#assign products = delegator.findList("Product", null, null, null, null, false)>
	var productData = [];
	<#list products as product>
		var row = {};
		row['productName'] = "${product.productName?if_exists}";
		row['productCode'] = "${product.productCode?if_exists}";
		row['productId'] = "${product.productId?if_exists}";
		productData.push(row);
	</#list>

	<#assign categories = Static["com.olbius.acc.utils.accounts.AccountUtils"].getProductCategoryByType(delegator, "CATALOG_CATEGORY", false)>
	var categoryData = [
	    <#if categories?exists>
            <#list categories as item>
                {
                    <#assign description = StringUtil.wrapString(item.categoryName?if_exists)?replace("'", "\"") />
                    categoryId : '${item.productCategoryId}',
                    categoryName : '${description}',
                },
            </#list>
        </#if>
	]
	
	<#assign productStores = delegator.findList("ProductStore", null, null, null, null, false)>
	var productStoreData = [
		<#if productStores?exists>
			<#list productStores as item>
				{
					productStoreId: "${item.productStoreId?if_exists}",
					storeName: "${StringUtil.wrapString(item.get("storeName", locale)?if_exists)}"
				},
			</#list>
		</#if>
	];
	
	var customerData = [
		<#if customers?exists>
			<#list customers as item>
				{
					partyId: "${item.partyId?if_exists}",
					partyCode: "${item.partyCode?if_exists}",
					fullName: "${item.fullName?if_exists}"
				},
			</#list>
		</#if>
	];
</script>
<script type="text/javascript">
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCIncomeStatement)}',
			button: true,
			service: "acctgTransTotal",
			id: 'incomeStatementReport',
			olap: 'olbiusReportIncomeStatement',
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns: [
				{
					text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductId)}',
					datafield: {name: 'productCode', type: 'string'},
					width: '11%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductName)}',
					datafield: {name: 'productName', type: 'string'}, width: '22%'
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}',
					datafield: {name: 'categoryName', type: 'string'}, width: '15%'
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}',
					datafield: {name: 'partyCode', type: 'string'},
					width: '11%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerName)}',
					datafield: {name: 'fullName', type: 'string'},
					width: '20%', filterable: true
				},
				{
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductStoreId)}',
					datafield: {name: 'productStoreId', type: 'string'},
					width: '11%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreName)}',
					datafield: {name: 'productStoreName', type: 'string'},
					width: '20%', filterable: true
				},
				{
					text: '${StringUtil.wrapString(uiLabelMap.BACCTransactionTime)}',
					datafield: {name: 'transTime', type: 'date'}, width: '13%', cellsformat: 'dd/MM/yyyy',
					columntype: 'datetimeinput', filtertype: 'range', filterable: false
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleIncome)}',
					datafield: {name: 'saleIncome', type: 'number'},
					width: '14%', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleDiscount)}',
					datafield: {name: 'saleDiscount', type: 'number'},
					width: '14%', columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCPromotion)}',
					datafield: {name: 'promotion', type: 'number'},
					width: '14%', columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCSaleReturn)}',
					datafield: {name: 'saleReturn', type: 'number'},
					width: '14%', columngroup: 'deductions', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCNetRevenue)}',
					datafield: {name: 'netRevenue', type: 'number'},
					width: '17%', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCCOGS)}',
					datafield: {name: 'cogs', type: 'number'},
					width: '17%', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCGrossProfit)}',
					datafield: {name: 'grossProfit', type: 'number'},
					width: '17%', filtertype: 'number', columntype: 'numberinput',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						return '<span class=align-right>' + formatcurrency(value) + '</span>';
    				}
				}
			],
			columngroups: [
                   { text: '${uiLabelMap.BACCDeductions}', align: 'center', name: 'deductions' },
                   { text: '${uiLabelMap.BACCSaleIncome}', align: 'center', name: 'saleIncome' },
            ],
			popup: [
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'product',
						label : '${StringUtil.wrapString(uiLabelMap.BACCProduct)}',
						grid: {
							source: productData,
							id: "productId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductId)}", datafield: 'productCode', width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductName)}", datafield: 'productName' }
							]
						}
					}
				},
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'categories',
						label : '${StringUtil.wrapString(uiLabelMap.BACCCategory)}',
						grid: {
							source: categoryData,
							id: "categoryId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCCategoryId)}", datafield: 'categoryId', width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCCategoryName)}", datafield: 'categoryName' }
								]
						}
					}
				},
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'party',
						label: '${StringUtil.wrapString(uiLabelMap.BACCCustomer)}',
						grid: {
							source: customerData,
							id: "partyId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}", datafield: 'partyCode', width: 150 },
			          			{ text: "${StringUtil.wrapString(uiLabelMap.BACCCustomer)}", datafield: 'fullName'}
							]
						}
					}
				},
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'productStore',
						label: '${StringUtil.wrapString(uiLabelMap.BACCProductStoreDemension)}',
						grid: {
							source: productStoreData,
							id: "productStoreId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductStoreId)}", datafield: 'productStoreId', width: 150 },
			          			{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductStoreDemension)}", datafield: 'storeName'}
							]
						}
					}
				},
				{
					group: "dateTime",
					id: "dateTime",
					params: { index: 0 }
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					party: popup.val("party"),
					product: popup.val('product'),
					category: popup.val('category'),
					productStore: popup.val('productStore'),
					reportType: 'general'
				}, popup.group("dateTime").val());
			},
			excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}

				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}
				window.location.href = "exportIncomeStatementExcel?" + otherParam;
            },
			exportFileName: 'BAO_CAO_DOANH_THU_TONG_HOP_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>