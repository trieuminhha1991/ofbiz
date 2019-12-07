<script type="text/javascript" id="salesDetailReportGrid">
		var sumQuantity;
		var sumExtPrice;
		var config = {
                title: ' ${uiLabelMap.ReportSalesDetailReport}',
                showstatusbar: true,
                statusbarheight: 50,
                showaggregates: true,
                sortable: true,
                service: 'salesOrder',
                columns: [
                       	{text: "${uiLabelMap.BPOSTime}", datafield:'date', cellsalign: 'left', type: 'olapdate'},
                        {text: "${uiLabelMap.BPOSOrderId}", datafield:'orderId', cellsalign: 'left', type: 'string'},
                       	{text: "${uiLabelMap.BPOSTotalQuantity}", datafield:'quantity', cellsalign: 'right', type: 'number',
                       		olapaggregatesrenderer: function(olap){
	                       	 	return function (aggregates, column, element, summaryData){
	                          		var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + 'olbius' + "' style='float: left; width: 100%; height: 100%;'>";
	                               	renderstring += '<div style="color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;">' 
	                               			+ '<b>${uiLabelMap.BPOSTotalQuantity}:</b>' + '<br>' + sumQuantity  
	                               			+ "&nbsp;" + '</div>';
	                          	  	renderstring += "</div>";
	                           		return renderstring; 
	                           	}
                       	 	}	
                       	},
                        {text: "${uiLabelMap.BPOSExtPrice}", datafield:'extPrice', cellsalign: 'right', type: 'number',}
                      ]
            };
        var configPopup = [
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date),
                 	disabled: true,
                }],
                before: 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    disabled: true,
                }],
                after: 'from_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date),
                    hide: true,
                }],
                before: 'thru_date_1'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date_1',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date),
                    hide: true,
                }],
                after: 'from_date_1'
            },
            {
		        action : 'addDropDownList',
		        params : [{
		            id : 'customTime',
		            label : '${StringUtil.wrapString(uiLabelMap.TypeTimeLabel)}',
		            data : customDate,
		            index: 4,
		        }],
                event : function(popup) {
                    popup.onEvent('customTime', 'select', function(event) {
                        var args = event.args;
                        var item = popup.item('customTime', args.index);
                        var filter = item.value;
                        popup.clear('from_date');
                        popup.clear('thru_date');
                        if(filter == 'oo') {
                            popup.show('from_date_1');
                            popup.show('thru_date_1');
                            popup.hide('from_date');
                            popup.hide('thru_date');
                        } else {
                        	popup.show('from_date');
                            popup.show('thru_date');
                        	popup.hide('from_date_1');
                            popup.hide('thru_date_1');
                        }
                        popup.resize();
                    });
                }
		    }, 
		    {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_dateType)}',
                    data : date_type_source,
                    index: 0
                }]
            },
            {
			    action : 'addDropDownList',
			    params : [{
			        id : 'productStoreId',
			        label : '${StringUtil.wrapString(uiLabelMap.BPOSProductStore)}',
			        data : productStoreData,
			        index: 0
			    }]
			},
        ];
		var sortField = '';
		var sortOption = '';
        var salesDetailGrid = OLBIUS.oLapGrid('salesDetailReportGrid', config, configPopup, 'executeGetSalesOrderReport', true);
        salesDetailGrid.funcUpdate(function (oLap) {
             oLap.update({
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'productId': '${parameters.productId?if_exists}',
                'partyId': '${parameters.partyId?if_exists}',
                'productStoreId': oLap.val('productStoreId'),
                'sortField': sortField,
                'sortOption': sortOption,
                'reportType': 'salesDetail',
                'customTime': oLap.val('customTime'),
            }, oLap.val('dateType'));
        });
        
        salesDetailGrid.getRunData(function(data){
        	sumQuantity = data.sumQuantity;
        	sumExtPrice = data.sumExtPrice;
        });
        
        salesDetailGrid.init(function () {
        	salesDetailGrid.runAjax();
        });
        
        var grid = salesDetailGrid.getGrid();
        grid.on("sort", function (event){
            var args = event.args;
            var sortInfo = event.args.sortinformation;
            sortOption = sortInfo.sortdirection.ascending ? "ASC" : "DESC";
            sortField = sortInfo.sortcolumn;
            salesDetailGrid.runAjax();
        });       
</script>