<script type="text/javascript" id="returnReportGrid">
		var sumQuantity;
		var sumExtPrice;
		var config = {
                title: ' ${uiLabelMap.ReportReturnReport}',
                showstatusbar: true,
                statusbarheight: 50,
                showaggregates: true,
                sortable: true,
                service: 'returnOrder',
                columns: [
						{text: "", datafield:'currency', cellsalign: 'left', type: 'string', hidden: true},
                        {text: "${uiLabelMap.BPOSProductID}", datafield:'productId', cellsalign: 'left', type: 'string', width: 150},
                        {text: "${uiLabelMap.BPOSProductName}", datafield:'productName', cellsalign: 'left', type: 'string'},
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
                        {text: "${uiLabelMap.BPOSTotalPrice}", datafield:'total', cellsalign: 'right', type: 'number',
                       		olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
                           		 	var currencyId = 'VND';
                        		 	if (data && data.currency){
                        		 		currencyId = data.currency;
                        		 	}
           	     					if (data && data.total){
           	     						return '<div style="text-align: right;">' + formatcurrency(data.total, currencyId) + '</div>';
           	     					} else {
           	     						return '<div style="text-align: right;">' + formatcurrency(0, currencyId) + '</div>';
           	     					}
               					}   
                       	   	},
                       	 	olapaggregatesrenderer: function(olap){
	                       	 	return function (aggregates, column, element, summaryData){
	                   	 			var data = olap.jqxGrid('getrows');
	                       	 		var currencyId = 'VND';
						 			if (data.length > 0){
						 				currencyId = data[0].currency;
						 			}
	                          		var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + 'olbius' + "' style='float: left; width: 100%; height: 100%;'>";
	                               	renderstring += '<div style="color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;">' 
	                               			+ '<b>${uiLabelMap.BPOSTotalPrice}:</b>' + '<br>' + formatcurrency(sumExtPrice, currencyId)  
	                               			+ "&nbsp;" + '</div>';
	                          	  	renderstring += "</div>";
	                           		return renderstring; 
	                           	}
                    	 	}
                        },
                      ]
            };
		var configPopup = [
		   				{
		   				    action : 'addDropDownList',
		   				    params : [{
		   				        id : 'facilityId',
		   				        label : '${StringUtil.wrapString(uiLabelMap.BPOSFacility)}',
		   				        data : facilityData,
		   				        index: 0
		   				    }]
		   				},
		   				{
		   				    action : 'addDropDownList',
		   				    params : [{
		   				        id : 'partyId',
		   				        label : '${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}',
		   				        data : customerData,
		   				        index: 0
		   				    }]
		   				},
		       		   	{
		                       action : 'addDateTimeInput',
		                       params : [{
		                           id : 'from_date',
		                           label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
		                           value: OLBIUS.dateToString(past_date)
		                       }],
		                       before: 'thru_date'
		                   },
		                   {
		                       action : 'addDateTimeInput',
		                       params : [{
		                           id : 'thru_date',
		                           label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
		                           value: OLBIUS.dateToString(cur_date)
		                       }],
		                       after: 'from_date'
		                   }
		           ];
		
		var returnGrid = OLBIUS.oLapGrid('returnReportGrid', config, configPopup, 'executeGetReturnReport', true);
        var sortField = '';
		var sortOption = '';
		returnGrid.funcUpdate(function (oLap) {
            oLap.update({
            	'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'partyId': oLap.val('partyId'),
                'facilityId': oLap.val('facilityId'),
                'sortField': sortField,
                'sortOption': sortOption,
            });
        });
        
		returnGrid.getRunData(function(data){
        	sumQuantity = data.sumQuantity;
        	sumExtPrice = data.sumExtPrice;
        });
        
		returnGrid.init(function () {
			returnGrid.runAjax();
        });
        
        var grid = returnGrid.getGrid();
        grid.on("sort", function (event){
            var args = event.args;
            var sortInfo = event.args.sortinformation;
            sortOption = sortInfo.sortdirection.ascending ? "ASC" : "DESC";
            sortField = sortInfo.sortcolumn;
            returnGrid.runAjax();
        }); 
        
</script>