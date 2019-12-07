<@jqOlbCoreLib />

<div id="olbiusOtherSalesOrderExportData"></div>
<script type="text/javascript">
    $(function() {
    	var gridObj;
        
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap[titleProperty])}",
            service: "salesOrderExportDataJob",
            button: true,
            id: "olbiusOtherSalesOrderExportData",
            olap: "olapOtherSalesOrderExportData",
            theme: OlbCore.theme,
            sortable: true,
            filterable: true,
            showfilterrow: true,
            columns: [
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderId)}",
                    datafield: {name: "order_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderItemSeqId)}",
                    datafield: {name: "order_item_seq_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderName)}",
                    datafield: {name: "order_name", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderDate)}",
                    datafield: {name: "order_date", type: "string"},
                    width: 120,
                    cellsrenderer: function(row, colum, value) {
						return '<span>' + jOlbUtil.dateTime.formatDate(value) + '</span>';
					}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}",
                    datafield: {name: "currency_uom", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelId)}",
                    datafield: {name: "product_store_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPSSalesChannel)}",
                    datafield: {name: "product_store_name", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalTaxAmount)}",
                    datafield: {name: "tax_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalDiscountAmount)}",
                    datafield: {name: "discount_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSSubTotalAmount)}",
                    datafield: {name: "sub_total_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalAmount)}",
                    datafield: {name: "total_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}",
                    datafield: {name: "product_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}",
                    datafield: {name: "product_name", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSPrimaryCategory)}",
                    datafield: {name: "primary_product_category_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSQuantity)}",
                    datafield: {name: "quantity", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalQuantity)}",
                    datafield: {name: "total_quantity", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSTotalSelectedAmount)}",
                    datafield: {name: "total_selected_amount", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSUnitPrice)}",
                    datafield: {name: "unit_price", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSUom)}",
                    datafield: {name: "quantity_uom_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSReturnId)}",
                    datafield: {name: "return_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSReturnQuantity)}",
                    datafield: {name: "return_quantity", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSReturnPrice)}",
                    datafield: {name: "return_price", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSProductAvgCost)}",
                    datafield: {name: "product_avg_cost", type: "number"},
                    width: 120, filtertype: 'number', 
                    cellsrenderer: function(row, column, value) {
						return '<div class=\"innerGridCellContent align-right\">' + formatnumber(value) + '</div>';
				 	}
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSSalesMethodChannelEnumId)}",
                    datafield: {name: "sales_method_channel_enum_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSSalesChannelEnumId)}",
                    datafield: {name: "sales_channel_enum_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderStatusId)}",
                    datafield: {name: "order_status_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSOrderItemStatusId)}",
                    datafield: {name: "order_item_status_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSCreatorId)}",
                    datafield: {name: "creator_id", type: "string"},
                    width: 120
                },
                {text: "${StringUtil.wrapString(uiLabelMap.BSSgcCustomerId)}",
                    datafield: {name: "sgc_customer_id", type: "string"},
                    width: 120
                },
            ],
            popup: [
                {
                    group: "dateTime",
                    id: "dateTime"
                },
            ],
            apply: function (grid, popup) {
            	var dateTimeData = popup.group("dateTime").val();
                return $.extend({}, dateTimeData);
            },
            excel: function(obj){
            	var isExistData = false;
				var dataRow = gridObj._grid.jqxGrid("getrows");
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
				//window.location.href = "exportReportSalesOrderExportDataExcel?" + otherParam;
				window.open("exportReportSalesOrderExportDataExcel?" + otherParam, "_blank");
				
            	//obj._grid.jqxGrid('exportdata', 'xls', obj._exportFileName, true, null, false, 'olbiusOlapExport', null, $.extend({
                //    serviceName: obj._serviceName,
                //    olapType: 'GRID',
                //    olapTitle: obj._title
                //}, obj._data));
            },
            exportFileName: 'TRICH_XUAT_SO_LIEU_BAN_HANG'
        };
        
        gridObj = OlbiusUtil.grid(config);
        
        $("#btnExportCsvSalesOrderExportData").on("click", function(){
	    	var isExistData = false;
			var dataRow = gridObj._grid.jqxGrid("getrows");
			if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
				isExistData = true;
			}
			if (!isExistData) {
				jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
				return false;
			}
			
			var otherParam = "";
			if (gridObj._data) {
				$.each(gridObj._data, function(key, value){
					otherParam += "&" + key + "=" + value;
				});
			}
			window.open("exportSalesOrderExportDataCSV?" + otherParam, "_blank");
	    });
    });
</script>

<div class="pull-right margin-top10">
	<button id="btnExportCsvSalesOrderExportData" class="btn btn-primary">Export CSV</button>
</div>
