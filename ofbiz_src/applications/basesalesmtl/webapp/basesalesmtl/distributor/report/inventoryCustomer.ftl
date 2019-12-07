<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/logresources/js/report/olbius.popup.extend.js"></script>
<script type="text/javascript" id="olbiusFacility">
    $(function () {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
            id: "olbiusFacility",
            service: "inventoryCustomer",
            button: true,
            url: "olapInventoryCustomerReport",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {
                    text: '${StringUtil.wrapString(uiLabelMap.TimeLabel)}',
                    datafield: {name: 'dateTime', type: 'string'},
                    width: 100, filterable: false, sortable: false,
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.BSRetailOutletId)}",
                    datafield: {name: "party_code", type: "string"},
                    width: 150
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.BSRetailOutletName)}",
                    datafield: {name: "name", type: "string"},
                    width: 150
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.BLCategoryProduct)}",
                    datafield: {name: "category_name", type: "string"},
                    width: 150
                }, {
                	text: "${StringUtil.wrapString(uiLabelMap.ProductId)}",
                	datafield: {name: "product_code", type: "string"},
                	width: 150
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.ProductName)}",
                    datafield: {name: "product_name", type: "string"},
                    width: 200
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.InventoryBeforeLabel)}",
                    datafield: {name: "inventoryF", type: "string"}, filterable: false, sortable: false,
                    width: 100,
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.ReceiveLabel)}",
                    datafield: {name: "receive", type: "string"}, filterable: false, sortable: false,
                    width: 100,
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.ExportLabel)}",
                    datafield: {name: "export", type: "string"}, filterable: false, sortable: false,
                    width: 100,
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                }, {
                    text: "${StringUtil.wrapString(uiLabelMap.InventoryAfterLabel)}",
                    datafield: {name: "inventoryL", type: "string"}, filterable: false, sortable: false,
                    width: 100,
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                }
            ],
            popup: [
                <#if categories?if_exists?index_of(",", 0) != -1>
                {
                	action: 'jqxGridMultiple',
                	params: {
                		id : 'categories',  
                		label : '${StringUtil.wrapString(uiLabelMap.BSCategory)}',
                		grid: {
                			source: ${StringUtil.wrapString(categories)},
                			id: "productCategoryId",
                			width: 550,
                			sortable: true,
                			pagesize: 5,
                			columnsresize: true,
                			pageable: true,
                			altrows: true,
                			showfilterrow: true,
                			filterable: true,
                			columns: [
                			          { text: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}", datafield: 'productCategoryId', width: 150 }, 
                			          { text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}", datafield: 'categoryName' }
                			          ]
                		}
                	}
                },</#if>
                <#if products?if_exists?index_of(",", 0) != -1>
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'product',  
        	            label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
        	            grid: {
        	            	source: ${StringUtil.wrapString(products)},
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
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductId)}", datafield: 'productCode', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}", datafield: 'productName' }
	            	        ]
        	            }
                    }
                },</#if>
                <#if Static["com.olbius.basesales.util.SalesPartyUtil"].isSalessup(delegator, userLogin.getString("partyId"))>
                {
                    action: 'jqxGridMultipleUrl',
                    params: {
                    	id : 'parties',  
        	            label : '${StringUtil.wrapString(uiLabelMap.BSMAgency)}',
        	            grid: {
        	            	url: "JQGetListAgents",
        	            	datafields:	[
								{name: 'partyId', type: 'string'},
								{name: 'partyCode', type: 'string'},
								{name: 'groupName', type: 'string'}
							],
        	            	id: "partyId",
        	            	width: 550,
        	            	sortable: true,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSRetailOutletId)}", datafield: 'partyCode', width: 150 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSRetailOutletName)}", datafield: 'groupName' }
	            	        ]
        	            }
                    }
                },</#if>
                {
                    group: "dateTime",
                    id: "dateTime"
                }
            ],
            apply: function (grid, popup) {
                return $.extend({
                product: popup.val('product'),
                categories: popup.val("categories"),
                <#if Static["com.olbius.basesales.util.SalesPartyUtil"].isSalessup(delegator, userLogin.getString("partyId"))>
                parties: popup.val("parties")?popup.val("parties"):null
                </#if>
                }, popup.group("dateTime").val());
            },
            excel: function(oLap) {
            	if (!_.isEmpty(oLap._data)) {
            		data = oLap._data;
            		var url = "exportInventoryCustomerReportExcel?" + "dateType=" + data.dateType + "&fromDate=" + new Date(data.fromDate).getTime() + "&thruDate=" + new Date(data.thruDate).getTime();
            		if (data.product) {
            			url += "&product=" + data.product;
					}
            		if (data.categories) {
            			url += "&categories=" + data.categories;
            		}
                	location.href = url;
				}
			}
        };

        var grid = OlbiusUtil.grid(config);

    });
</script>
