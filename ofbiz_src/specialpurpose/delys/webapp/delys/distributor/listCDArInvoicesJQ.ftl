<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'total', type: 'number'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'partyNameResultFrom', type: 'string'},
					 { name: 'partyNameResultTo', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:150, datafield: 'invoiceId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/accApinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
                        }},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', width:150, datafield: 'invoiceTypeId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	for(i=0; i < vaIT.length;i++){
                        		if(value==vaIT[i]){
                        			return \"<span>\" + vaITValue[i] + \"</span>\";
                        		}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        }},
					 { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:150, datafield: 'invoiceDate', cellsrenderer:
                     	 function(row, colum, value)
                        {
                            var jsonDateRE = /^\\/Date\\((-?\\d+)(\\+|-)?(\\d+)?\\)\\/$/;
                            var arr = jsonDateRE.exec(\"\\/\" + value + \"\\/\");
                            if (arr) {
                                // 0 - complete results; 1 - ticks; 2 - sign; 3 - minutes
                                var result = new Date(parseInt(arr[1]));
                                if (arr[2]) {
                                    var mins = parseInt(arr[3]);
                                    if (arr[2] === \"-\") {
                                        mins = -mins;
                                    }
                                    var current = result.getUTCMinutes();
                                    result.setUTCMinutes(current - mins);
                                }
                                if (!isNaN(result.valueOf())) {
                                    var date = $.jqx.dataFormat.formatdate(result, 'dd-MM-yyyy',getLocalization);
                                    return '<span style=\"float: left; margin: 4px;\">' + date + '</span>';
                                }
                            }
                            return \"\";
                     }},
					 { text: '${uiLabelMap.CommonStatus}', width:150, datafield: 'statusId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	for(i=0; i < vaSI.length;i++){
                        		if(value==vaSI[i]){
                        			return \"<span>\" + vaSIValue[i] + \"</span>\";
                        		}
                        	}
                        	return value;
                        }},
					 { text: '${uiLabelMap.description}', width:150, datafield: 'description'},
					 { text: '${uiLabelMap.accSuppliers}', width:150, datafield: 'partyIdFrom', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultTo + '[' + data.partyId + ']' + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_total}', width:200, datafield: 'total', aggregates: ['sum'],
					 	aggregatesrenderer: 
					 	function (aggregates, column, element, summaryData) 
					 	{
                          var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
                           $.each(aggregates, function (key, value) {
                              renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.accPayableToApplyTotal}:<\\/b>' + '<br>' +  value  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId}\" + '</div>';
                              });                          
                          	  renderstring += \"</div>\";
                          return renderstring; 
                          } ,  
					 	cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}', width:180, datafield: 'amountToApply',  aggregates: ['sum'],
					 	aggregatesrenderer: 
					 	function (aggregates, column, element, summaryData) 
					 	{
                          var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
                           $.each(aggregates, function (key, value) {
                              renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.accReceivableToApplyTotal}:<\\/b>' + '<br>' +  value  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId}\" + '</div>';
                              });                          
                          	  renderstring += \"</div>\";
                          return renderstring; 
                          } , 
					 cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
					 	}}"
					 />		

<@jqGrid url="jqxGeneralServicer?sname=JQGetListCDARInvoiceByUserLoginDis" dataField=dataField columnlist=columnlist sortdirection="desc" defaultSortColumn="invoiceDate" showstatusbar="true" 
	 otherParams="total:S-getInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getInvoiceNotApplied(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 />