<style>
div[id^="statusbartreeGrid"] {
	  width: 0 !important;
	 }
</style>
<script>
	$($(".breadcrumb").children()[1]).html("${uiLabelMap.Report} <span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSMRevenueByCustomerType)}");
</script>

<script type="text/javascript" id="revenueByCustomerType">
    $(function () {
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSMRevenueByCustomerType)}',
            id: 'revenueByCustomerType',
            olap: 'evaluateRevenueByCustomerType',
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
            	{ text: '${uiLabelMap.BSNo2}', 
					sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	datafield: {name: 'stt', type: 'number'}, 
			    	width: '3%',
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
			 	},  
               	{
                    text: '${StringUtil.wrapString(uiLabelMap.BSMCustomerTypeCode)}',
                    datafield: {name: 'customerType', type: 'string'},
                    width: '13%'
                }, 
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSMCustomerType)}',
                    datafield: {name: 'customerTypeName', type: 'string'},
                    width: '13%'
                }, 
               	{
                    text: '${StringUtil.wrapString(uiLabelMap.BSMProductCode)}',
                    datafield: {name: 'productCode', type: 'string'},
                    width: '12%'
                }, 
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}',
                    datafield: {name: 'productName', type: 'string'},
                    width: '27%'
                }, 
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}',
                    datafield: {name: 'salesVolume', type: 'string'}, 
                    width: '10%',
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                },
                {
                    text: '${StringUtil.wrapString(uiLabelMap.BSMUnit)}',
                    datafield: {name: 'unit', type: 'string'},
                    width: '10%'
                }, 
             	{
                    text: '${StringUtil.wrapString(uiLabelMap.BSSalesValue)}',
                    datafield: {name: 'salesValue', type: 'string'}, 
                    width: '12%',
                    cellsrenderer: function (row, column, value) {
				        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				    }
                }, 
            ],
            popup: [
            	{
                    group: 'dateTime',
                    id: 'dateTime'
               	},
            <#if products?if_exists?index_of(",", 0) != -1>
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'product',  
        	            label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
        	            grid: {
        	            	source: ${StringUtil.wrapString(products)},
        	            	id: "productId",
        	            	width: 500,
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
                <#if customerType?if_exists?index_of(",", 0) != -1>
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'customerType',  
        	            label : '${StringUtil.wrapString(uiLabelMap.BSMCustomerType2)}',
        	            grid: {
        	            	source: ${StringUtil.wrapString(customerType)},
        	            	id: "partyTypeId",
        	            	width: 500,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSMCustomerTypeCode)}", datafield: 'partyTypeId', width: 200 }, 
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSMCustomerType)}", datafield: 'description' }
	            	        ]
        	            }
                    }
                },</#if>
            ],
            apply: function (grid, popup) {
                return $.extend({
                    product: popup.val('product'),
                    customerType: popup.val('customerType'),
                }, popup.group('dateTime').val());
            },
            excel: true
        };

        var grid = OlbiusUtil.grid(config);

    });
</script>