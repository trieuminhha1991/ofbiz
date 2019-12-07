<script type="text/javascript" id="detailCustomerReportGrid">
		var config = {
                title: ' ${uiLabelMap.ReportDetailCustomerReport}',
                sortable: true,
                columns: [
						{text: "", datafield:'currency', cellsalign: 'left', type: 'string', hidden: true},
						{text: "${uiLabelMap.BPOSPartyID}", datafield:'partyId', cellsalign: 'left', type: 'string', hidden: true},
                        {text: "${uiLabelMap.BPOSCustomerName}", datafield:'partyName', cellsalign: 'left', type: 'string', width: 150,
                        	olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
           	     					if (value){
           	     						return '<div>' + value + '</div>';
           	     					} else {
           	     						if (data && data.partyId){
           	     							return '<div>' + getCustomerName(data.partyId) + '</div>';
           	     						}
           	     					}
               					}   
                       	   	},	
                        },
                        {text: "${uiLabelMap.BPOSProductID}", datafield:'productId', cellsalign: 'left', type: 'string', width: 140,
                        	olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
           	     					if (data && data.productId){
           	     						return '<div><a style = "margin-left: 10px" target="_blank" href=' + 'salesDetailReport?productId=' + data.productId + '&partyId=' + data.partyId + '>' +  data.productId + '</a>' + '</div>'
               						}   
                       	   		}
                       		}	
                        },
                        {text: "${uiLabelMap.BPOSProductName}", datafield:'productName', cellsalign: 'left', type: 'string', width: 350},
                       	{text: "${uiLabelMap.BPOSTotalQuantity}", datafield:'quantity', cellsalign: 'right', type: 'number'},
                        {text: "${uiLabelMap.BPOSExtPrice}", datafield:'extPrice', cellsalign: 'right', type: 'number',},
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
			        id : 'productStoreId',
			        label : '${StringUtil.wrapString(uiLabelMap.BPOSProductStore)}',
			        data : productStoreData,
			        index: 0
			    }]
			},
        ];

        var detailCustomerGrid = OLBIUS.oLapGrid('detailCustomerReportGrid', config, configPopup, 'executeGetSalesOrderReport', true);
        var sortField = '';
		var sortOption = '';
		detailCustomerGrid.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date_1'),
                'thruDate': oLap.val('thru_date_1'),
                'partyId': '${parameters.partyId?if_exists}',
                'productStoreId': oLap.val('productStoreId'),
                'sortField': sortField,
                'sortOption': sortOption,
                'reportType': 'detailCustomer',
                'customTime': oLap.val('customTime'),
            });
        });
        
		detailCustomerGrid.init(function () {
			detailCustomerGrid.runAjax();
        });
        
        var grid = detailCustomerGrid.getGrid();
        grid.on("sort", function (event){
            var args = event.args;
            var sortInfo = event.args.sortinformation;
            sortOption = sortInfo.sortdirection.ascending ? "ASC" : "DESC";
            sortField = sortInfo.sortcolumn;
            detailCustomerGrid.runAjax();
        }); 
</script>