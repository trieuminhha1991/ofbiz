<script>
	$(document).ready(function(){
    	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "info" });
	});
	
	function formatcurrency(num, uom){
	      decimalseparator = ",";
	          thousandsseparator = ".";
	          currencysymbol = "đ";
	          if(typeof(uom) == "undefined" || uom == null){
	           uom = "${currencyUomId?if_exists}";
	          }
	      if(uom == "USD"){
	       currencysymbol = "$";
	       decimalseparator = ".";
	           thousandsseparator = ",";
	      }else if(uom == "EUR"){
	       currencysymbol = "€";
	       decimalseparator = ".";
	           thousandsseparator = ",";
	      }
	         var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
	         if(str.indexOf(".") > 0) {
	             parts = str.split(".");
	             str = parts[0];
	         }
	         str = str.split("").reverse();
	         for(var j = 0, len = str.length; j < len; j++) {
	             if(str[j] != ",") {
	                 output.push(str[j]);
	                 if(i%3 == 0 && j < (len - 1)) {
	                     output.push(thousandsseparator);
	                 }
	                 i++;
	             }
	         }
	         formatted = output.reverse().join("");
	         console(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
	         return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
	}
</script>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#assign party = parameters.party !>
<#assign heightDetail = 275 />
<#assign heightRow = 297 />
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	 	var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
        orders = ordersDataAdapter.records;
		 var nestedGrids = new Array();
         var id = datarecord.uid.toString();
         var grid = $($(parentElement).children()[0]);
         $(grid).attr(\"id\",\"jqxgridDetail\");
         nestedGrids[index] = grid;
         var ordersbyid = [];
         for (var m = 0; m < orders.length; m++) {
            
                 ordersbyid.push(orders[m]);
         }
         var orderssource = { datafields: [
         	 { name: \'billId\', type:\'string\' },
             { name: \'invoiceItemTypeId\', type: \'string\' },
             { name: \'description\', type: \'string\' },
             { name: \'costAccBaseId\', type: \'number\' },
             { name: \'costBillAccountingId\', type: \'string\' },
             { name: \'costPriceTemporary\', type: \'number\' },
             { name: \'costPriceActual\', type: \'number\' },
            	 
         ],
         localdata: ordersbyid,
         updaterow: function (rowid, newdata, commit) {
        	// alert(newdata.datetimeManufactured);
        	 commit(true);
        	 var billId = newdata.billId;
        	 var invoiceItemTypeId = newdata.invoiceItemTypeId;
        	 var costAccBaseId = newdata.costAccBaseId;
        	 var costBillAccountingId = newdata.costBillAccountingId;
        	 var costPriceTemporary = newdata.costPriceTemporary;
        	 var costPriceActual = newdata.costPriceActual;
        	 $.ajax({
                 type: \"POST\",                        
                 url: 'updateCostBillAcc',
                 data: {billId: billId, invoiceItemTypeId: invoiceItemTypeId, costAccBaseId: costAccBaseId, costBillAccountingId: costBillAccountingId, costPriceTemporary: costPriceTemporary, costPriceActual: costPriceActual},
                 success: function (data, status, xhr) {
                     // update command is executed.
                     if(data.responseMessage == \"error\"){
                     	commit(false);
                     	$(\"#jqxNotificationNested\").jqxNotification({ template: 'error'});
                     	$(\"#notificationContentNested\").text(data.errorMessage);
                     	$(\"#jqxNotificationNested\").jqxNotification(\"open\");
//                 				grid.jqxGrid('setcellvaluebyid', rowid, 'costBillAccountingId', 'error');
                 }else{
//                         	commit(true);
//                         	grid.jqxGrid('updatebounddata');
                     	$(\"#container\").empty();
                     	$(\"#jqxNotificationNested\").jqxNotification({ template: 'info'});
                     	$(\"#notificationContentNested\").text(\"${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}\");
                     	$(\"#jqxNotificationNested\").jqxNotification(\"open\");
             			grid.jqxGrid('setcellvaluebyid', rowid, 'costBillAccountingId', data.costBillAccountingId);
//                         	commit(false);
                 }
                 },
                 error: function () {
                     commit(false);
                 }
             });
             }
         }
         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
         if (grid != null) {
        	 var a ;
             grid.jqxGrid({
                 source: nestedGridAdapter, width: '98%', height: ${heightDetail},
                 showtoolbar:false,
                 showstatusbar: true,
                 columnsheight: 30,
		 		 editable: true,
		 		 editmode:\'selectedrow\',
		 		 showheader: true,
		 		 selectionmode:\'singlerow\',
		 		 showaggregates: true,
		 		 statusbarheight: 40,
		 		 rowsheight: 30,
		 		 statusbarheight: 47,
		 		 theme: \'olbius\',
		 		 pageable: false,
                 columns: [
					{
					    text: '', sortable: false, filterable: false, editable: false,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 35,
					    cellsrenderer: function (row, column, value) {
					        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
					    },
					    cellclassname: function (row, column, value, data) {
						    var mod = row % 2;
						    if(mod == 0){
						    	return 'green1';
						    }
						}
					},
					{ text: \'${uiLabelMap.billId}\', datafield: \'billId\', editable: false, hidden: true,
						   cellclassname: function (row, column, value, data) {
							    var mod = row % 2;
							    if(mod == 0){
							    	return 'green1';
							    }
							}
					},
					{ text: \'${uiLabelMap.invoiceItemTypeId}\', datafield: \'invoiceItemTypeId\', editable: false, hidden: true,
						   cellclassname: function (row, column, value, data) {
							    var mod = row % 2;
							    if(mod == 0){
							    	return 'green1';
							    }
							}
					},
                   { text: \'${uiLabelMap.invoiceItemTypeId}\', datafield: \'description\', editable: false,
                	   cellclassname: function (row, column, value, data) {
	   					    var mod = row % 2;
	   					    if(mod == 0){
	   					    	return 'green1';
	   					    }
	   					}
                   },
                   { text: \'${uiLabelMap.costBillAccountingId}\', datafield: \'costBillAccountingId\', editable: false, hidden: true,
                	   cellclassname: function (row, column, value, data) {
	   					    var mod = row % 2;
	   					    if(mod == 0){
	   					    	return 'green1';
	   					    }
	   					}
                   },
                   { text: \'${uiLabelMap.costAccBaseId}\', datafield: \'costAccBaseId\', editable: false, hidden: true,
                	   cellclassname: function (row, column, value, data) {
	   					    var mod = row % 2;
	   					    if(mod == 0){
	   					    	return 'green1';
	   					    }
	   					}
                   },
                   { text: \'${uiLabelMap.costPriceTemporary}\', align:\'left\', datafield: \'costPriceTemporary\', columntype: \'numberinput\', width: \'200px\', editable: true, cellsalign: \'left\',
                	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                		   return '<div style=\"text-align: right; margin-right: 10px;margin-top: 5px;\">' +formatnumber(value)+ '</div>';
                       },
                       createeditor: function (row, cellvalue, editor) {
                           editor.jqxNumberInput({ spinButtonsStep:1000, inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', digits: 12});
                       },
                       aggregates: ['sum'],
                       aggregatesrenderer: 
   					 	function (aggregates, column, element, summaryData) 
   					 	{
                             var renderstring = \"\";
                              $.each(aggregates, function (key, value) {
                                 renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.totalPrice}:<\\/b>' + '<br>' +  value.toLocaleString('VI')  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId?if_exists}\" + '</div>';
                                 });                          
                             	  renderstring += \"</div>\";
                             return renderstring; 
   					 	},
   					 	
	   					 cellclassname: function (row, column, value, data) {
	   					    var mod = row % 2;
	   					    if(mod == 0){
	   					    	return 'green1';
	   					    }
	   					 }
                   },
                   { text: \'${uiLabelMap.costPriceActual}\', align:\'left\', datafield: \'costPriceActual\', columntype: \'numberinput\', width: 200, editable: true, cellsalign: \'left\',
                	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +formatnumber(value)+ '</div>';
                       },
                       createeditor: function (row, cellvalue, editor) {
                           editor.jqxNumberInput({ spinButtonsStep:1000, inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.',digits:12 });
                       },
                       aggregates: ['sum'],
                       aggregatesrenderer: 
   					 	function (aggregates, column, element, summaryData) 
   					 	{
                             var renderstring = \"\";
                              $.each(aggregates, function (key, value) {
                                 renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.totalPrice}:<\\/b>' + '<br>' +  value.toLocaleString('VI')  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId?if_exists}\" + '</div>';
                                 });
                             	  renderstring += \"</div>\";
                             return renderstring;
   					 	},
   					 	cellclassname: function (row, column, value, data) {
	   					    var mod = row % 2;
	   					    if(mod == 0){
	   					    	return 'green1';
	   					    }
	   					}
                   }
                ]
             });
         }
 }"/>

<#assign dataField="[{ name: 'billId', type: 'string' },
					 { name: 'billNumber', type: 'string'},
					 { name: 'departureDate', type: 'string'},
					 { name: 'arrivalDate', type: 'string'},
					 { name: 'rowDetail', type: 'string'}]"/>
<#assign columnlist="{text: '${uiLabelMap.accSTT}', sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false, datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
					    }
					},
					{ text: '${uiLabelMap.billId}', datafield: 'billId', hidden: true, width: '150px', editable: false, filterable: false},
					{ text: '${uiLabelMap.billNumber}', datafield: 'billNumber', editable: false, filterable: true},
					{ text: '${uiLabelMap.departureDate}', datafield: 'departureDate', editable: false, columntype: 'datetimeinput', width: '150px', cellsformat: 'dd/MM/yyyy', filterable: false},
					{ text: '${uiLabelMap.arrivalDate}', datafield: 'arrivalDate', editable: false,  columntype: 'datetimeinput', width: '150px', cellsformat: 'dd/MM/yyyy', filterable: false}"/>

<@jqGrid filtersimplemode="true" addType="" initrowdetails = "true" rowsheight="30" dataField=dataField
	initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow" editable="true" columnlist=columnlist
	clearfilteringbutton="false" showtoolbar="true" addrow="false"
 	url="jqxGeneralServicer?sname=JQGetBillForAcc&party=${departmentId?if_exists}" rowdetailsheight = "${heightRow}"
 />

<style>     
    .green1 {
        color: #black;
        background-color: #F0FFFF;
    }
    .yellow1 {
        color: black\9;
        background-color: yellow\9;
    }
    .red1 {
        color: black\9;
        background-color: #e83636\9;
    }
    .green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: #F0FFFF;
    }
    .yellow1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: yellow;
    }
    .red1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
        color: black;
        background-color: #e83636;
    }
    #pagerjqxgridDetail{
    	display: none;
    }
</style>