<#assign salesMonth = "[{'text': '${StringUtil.wrapString(uiLabelMap.BSJanuary)}', 'value': '1'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSFebruary)}', 'value': '2'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSMarch)}', 'value': '3'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSApril)}', 'value': '4'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSMay)}', 'value': '5'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSJune)}', 'value': '6'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSJuly)}', 'value': '7'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSAugust)}', 'value': '8'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSSeptember)}', 'value': '9'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSOctober)}', 'value': '10'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSNovember)}', 'value': '11'}," +
		"{'text': '${StringUtil.wrapString(uiLabelMap.BSDecember)}', 'value': '12'}]" />

<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" id="SalesEffectiveness">
    $(function () {
    	var originalProducts = ${StringUtil.wrapString(products)};
    	var mapUomId = ${StringUtil.wrapString(mapUomId)};
    	var originalProductIds =  _.map(originalProducts, function(obj, key){
    		return obj.productId;
        });
    	var columns = [
		{ 
			text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false,
			pinned: true, groupable: false, draggable: false, resizable: false,
			datafield: 'stt', columntype: 'number', width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (value + 1) + '</div>';
			}
		}, {
			text: "SALES_IN"=="${forecastType?if_exists}"?"${StringUtil.wrapString(uiLabelMap.DADistributorId)}":"${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}",
			datafield: {name: "se_code", type: "string"},
			pinned: true, groupable: false, draggable: false, resizable: false,
			width: 150
		}, {
			text: "SALES_IN"=="${forecastType?if_exists}"?"${StringUtil.wrapString(uiLabelMap.DADistributorName)}":"${StringUtil.wrapString(uiLabelMap.EmployeeName)}",
			datafield: {name: "se_name", type: "string"},
			pinned: true, groupable: false, draggable: false, resizable: false,
			width: 200
		}];
    	var columngroups = new Array();
    	for ( var x in originalProducts) {
    		columns.push({
    			text: "${StringUtil.wrapString(uiLabelMap.BSActual)}", columngroup: originalProducts[x].productId,
    			datafield: {name: originalProducts[x].productId + "_a", type: "number"}, width: 120,
    			cellsrenderer: function (row, column, value) {
    				value = value==0?value="-":value;
			        return '<div class=\"text-right\" title=\"' + (typeof (value) == 'number'?mapUomId[originalProducts[x].productId]:'') + '\">' + value.toLocaleString(locale) + '</div>';
			    }, filterable: false
    		});
    		columns.push({
    			text: "${StringUtil.wrapString(uiLabelMap.BSTarget)}", columngroup: originalProducts[x].productId,
    			datafield: {name: originalProducts[x].productId + "_e", type: "number"}, width: 120,
    			cellsrenderer: function (row, column, value) {
    				value = value==0?value="-":value;
    				return '<div class=\"text-right\" title=\"' + (typeof(value)=='number'?mapUomId[originalProducts[x].productId]:'') + '\">' + value.toLocaleString(locale) + '</div>';
    			}, filterable: false
    		});
    		columns.push({
    			text: "${StringUtil.wrapString(uiLabelMap.FormFieldTitle_percentage)}", columngroup: originalProducts[x].productId,
    			datafield: {name: originalProducts[x].productId + "_p", type: "number"}, width: 120,
    			cellsrenderer: function (row, column, value) {
    				var cellClass = "";
    				value = value==0?value="-":value;
    				if (typeof (value) == 'number') {
    					if (value < 100) {
    						cellClass = "background-important-nd";
						}
    					value = value.toLocaleString(locale) + ' %';
    				}
    				return '<div class=\"text-right ' + cellClass + '\" title=\"' + (typeof(value)=='number'?mapUomId[originalProducts[x].productId]:'') + '\">' + value + '</div>';
    			}, filterable: false
    		});
    		var full_title = "";
			if (OlbiusConfig.report.show.productName) {
				full_title += originalProducts[x].text ? originalProducts[x].text : "";
			}
			if (OlbiusConfig.report.show.productCode) {
				if (full_title != "") {
					full_title += "</b><br><b>";
				}
				full_title += originalProducts[x].productCode;
			}
    		columngroups.push({text: full_title, align: 'center', name: originalProducts[x].productId});
		}
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
            service: "salesOrder",
            button: true,
            id: "SalesEffectiveness",
            url: "olapSalesEffectivenessReport",
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: columns,
            columngroups: columngroups,
            popup: [
				{
				    action: 'jqxDropDownList',
				    params: {
				        id: 'YEAR',
				        label: '${StringUtil.wrapString(uiLabelMap.BSYear)}',
				        source: ${StringUtil.wrapString(salesYear)},
				        selectedIndex: _.find(_.map(${StringUtil.wrapString(salesYear)}, function(obj, key){
						    	if (obj.value == OlbiusConfig.report.time.current.year) {
								return key;
							}
					    }), function(key){ return key })
				    }
				},
				{
					action: 'jqxDropDownList',
					params: {
						id: 'MONTH',
						label: '${StringUtil.wrapString(uiLabelMap.BSMonth)}',
						source: ${StringUtil.wrapString(salesMonth)},
						selectedIndex: _.find(_.map(${StringUtil.wrapString(salesMonth)}, function(obj, key){
							if (obj.value == (OlbiusConfig.report.time.current.month + 1)) {
								return key;
							}
						}), function(key){ return key })
					}
				},
                <#if products?if_exists?index_of(",", 0) != -1>
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'product',  
        	            label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
        	            grid: {
        	            	source: originalProducts,
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
            ],
            apply: function (grid, popup) {
            	var products = popup.val('product');
            	if (_.isEmpty(products)) {
            		products = originalProductIds;
        		}
                return {
            		product: products,
            		forecastType: "${forecastType?if_exists}",
            		YEAR: popup.val('YEAR'),
            		MONTH: popup.val('MONTH'),
            		ORG: "${ORG?if_exists}",
            		BOO_DIS: "${BOO_DIS?if_exists}",
            		DIS_ID: "${DIS_ID?if_exists}"
                };
            },
            excel: function(oLap) {
            	if (!_.isEmpty(oLap._data)) {
            		data = oLap._data;
            		var url = "exportWarehouseReportExcel?" + "dateType=" + data.dateType + "&fromDate=" + new Date(data.fromDate).getTime() + "&thruDate=" + new Date(data.thruDate).getTime();
            		if (data.product) {
            			url += "&product=" + data.product;
            		}
                	location.href = url;
				}
			}
        };
        var grid = OlbiusUtil.grid(config);
    });
</script>
