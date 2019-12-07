<#include "component://widget/templates/jqwLocalization.ftl" />
<style type="text/css">
#expandCollapse.jqx-fill-state-pressed{
	border-color: #aaa !important;
  	background: #efefef !important;
}
.jqx-widget-content{
	font-family: 'Roboto';
}
#salesHistoryWindow .jqx-widget-header.jqx-grid-header{
	height: 56px !important; 
}
#splitter .jqx-splitter-collapse-button-vertical.jqx-fill-state-pressed, #splitter .jqx-splitter-collapse-button-horizontal.jqx-fill-state-pressed{
	background: #0099cc !important;
}
#returnOrder, #printOrder{
	float: right;
	margin-right: 10px;
	margin-bottom: 10px;
	margin-top: 3.5px;
}
#detailSalesorder .jqx-widget-header.jqx-grid-header{
	height: 25px !important;
}
</style>
<div id="salesHistoryWindow" style="display: none;">
	<div style="background-color: #438EB9; border-color: #0077BC; color: white; font-size: 15px; font-family: 'Open-sans'">
		${uiLabelMap.BPOSSaleHistory} 
		<input type="button" value="${uiLabelMap.BPOSExpand}" id='expandCollapse' />
	</div>
	<div style="overflow: hidden;">
		<div id="splitter">
	        <div>
	            <div style="border: none;" id='salesHistory'>
	             
	            </div>
	        </div>
	        <div>
		        <div id="nestedSpliter">
		            <div id="overviewSalesorder">
		            	<div class="span12 row-fluid" style="margin-left: 10px; margin-top: 10px">
		            		<div class="span6">
		            			<div class="grey-text beauty-label" id="salesOrderId"><b> ${uiLabelMap.BPOSOrderId}:</b> </div>
		            			<div class="grey-text beauty-label" id="salesCustomerName" ><b>${uiLabelMap.BPOSCustomer}:</b> </div>
		            			<div class="grey-text beauty-label" id="salesTelecomnumber" ><b>${uiLabelMap.BPOSMobile}:</b></div>
		            			<div class="grey-text beauty-label" id="salesAddress"><b>${uiLabelMap.BPOSAddress}:</b></div>
		            			<div class="beauty-label" style="color: red" id="salesOrderReturned"></div>
		            		</div>
		            		<div class="span6">
		            			<div class="grey-text beauty-label" id="salesOrderTime" ><b>${uiLabelMap.BPOSTime}:</b></div>
		            			<div class="grey-text beauty-label" id="salesOrderTotal"><b>${uiLabelMap.BPOSHeldOrderTotal}:</b></div>
		            			<div class="grey-text beauty-label" id="salesOrderDiscount"> <b>${uiLabelMap.BPOSDiscount}:</b> </div>
		            			<#if showPricesWithVatTax == "N">
		            				<div class="grey-text beauty-label" id="salesOrderTax"> <b>${uiLabelMap.BPOSTotalSalesTax}:</b> </div>
		            			</#if>
		            			<div class="grey-text beauty-label" id="salesGrandTotal"><b>${uiLabelMap.BPOSCartTotalNotAdj}</b></div>
		            		</div>
		            	</div>
		            </div>
		            <div id="detailSalesorder">
		            	
		            </div>
		        </div>
	        </div>
	    </div>
		<div id="salesHistory-footer">
			<button type="button" id='printOrder'><i class="icon-print"></i> ${uiLabelMap.POSPrint}</button>
			<button type="button" id='returnOrder' <#if !hasOlbEntityPermission("POS_RETURN_CTRL_ENTER", "CREATE")>class="hide"</#if>><i class="icon-undo"></i> ${uiLabelMap.BPOSReturn}</button>
		</div>
    </div>
</div>
<script type="text/javascript">
	//prepare data
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) !>
	var dataUom = [<#if uomList?exists><#list uomList as uom >{
		uomId: '${uom.uomId?if_exists}',
		description: "${uom.description?if_exists}"
	},</#list></#if>];
	
	var transactionType = new Array();
	var row0 = {};
	row0['transactionType'] = 'RETURN';
	row0['description'] = '${uiLabelMap.BPOSReturnOrder}';
	transactionType[0] = row0;

	var row1 = {};
	row1['transactionType'] = 'ORDER';
	row1['description'] = '${uiLabelMap.BPOSSalesOrder}';
	transactionType[1] = row1;
	
	var row2 = {};
	row2['transactionType'] = 'ORDER_RETURN';
	row2['description'] = '${uiLabelMap.BPOSReturnReceiptsOrder}';
	transactionType[2] = row2;
	
	$(document).ready(function () {
		$("#returnOrder").on('click', function () {
			 // Display popup
			 ConfirmReturn.open();
		});
		function returnWholeOrderFunction(){
			 var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
			 var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
			 returnWholeOrder(dataRow.orderId);
		}
		var focusS = 0;
		var toggledS = $("#expandCollapse").jqxToggleButton('toggled');
		$('#splitter').on('expanded', function (event) {
			toggledS = false;
        	$("#expandCollapse")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
        });

        $('#splitter').on('collapsed', function (event) {
        	toggledS = true;
        	$("#expandCollapse")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSCollapse)}';
        });
		
		$("#expandCollapse").on('click', function () {
			if (focusS == 0){
				$('#splitter').jqxSplitter('expand');
	            $("#expandCollapse")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
	            focusS = 1;
			} 
            if (toggledS) {
            	$('#splitter').jqxSplitter('expand');
                $("#expandCollapse")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSExpand)}';
            }else {
            	$('#splitter').jqxSplitter('collapse');
            	$("#expandCollapse")[0].value = '${StringUtil.wrapString(uiLabelMap.BPOSCollapse)}';
            } 
        });
		
		initOrderItems();
		
		$('#salesHistoryWindow').on('close', function (event) { 
			flagPopup = true;
			productToSearchFocus();
			$('#salesHistory').jqxGrid('clearfilters');
		});
		
		$('#salesHistoryWindow').on('open', function (event) { 
			Loading.hide('loadingMacro');
			flagPopup = false;
			$("#salesHistory").jqxGrid('selectrow', 0);
			$("#salesHistory").jqxGrid('focus');
		});
		$('#printOrder').click(function() {

			var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
			var rowdata = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
			if (rowdata) {
				getDataForPrintOrderHistory(rowdata);
				
				$("#PrintOrder").show();
				$("#PrintOrder").css({
					"z-index" : -1,
					position: "absolute"
				});
				$("#jqxPartyList").jqxComboBox('focus');
				setTimeout(function(){
					var tmpWin = $("#PrintOrder").printArea().win;
					if(tmpWin.matchMedia){
						var printEvent = tmpWin.matchMedia('print');
					    printEvent.addListener(function(printEnd) {
					    	if (!printEnd.matches) {
						    	$("#jqxProductList").jqxComboBox('focus');
						    }
						});
					}
				}, 10);
			} else {
				bootbox.hideAll();
				bootbox.alert(BPOSNoAnyItemInCart);
			}
		});
	});
	
	function getDataForPrintOrderHistory(rowdata) {
		var param = 'orderId=' + rowdata.orderId + '&posTerminalLogId=' + rowdata.posTerminalLogId;
		var orderItems;
		$.ajax({url: 'GetOrderItems',
   	        data: param,
   	        type: 'post',
   	        async: false,
   	        success: function(data) {
   	        	orderItems = data.listOrderItems;
   	        }
   	    });
		$.ajax({url: 'GetOverViewSalesHistory',
   	        data: param,
   	        type: 'post',
   	        async: false,
   	        success: function(data) {
   	            var partyName = data.customerName?data.customerName:"";
    	    	var partyMobile = data.telecomnumber?data.telecomnumber:"";
    	    	var partyAddress = data.address?data.address:"";
    	    	var orderDate = data.orderDate;
    	    	var grandTotal = data.orderTotal;
    	    	var cartDiscountTotal = data.orderDiscount;
    	    	var totalSalesTax = data.orderSalesTax;
    	    	var totalDue = data.grandTotal;
    	    	var returnedTotal = data.returnedTotal;
    	    	var orderReturned = data.orderReturned;
    	    	var currencyUom = data.currencyUom;

    	    	var eachTr = "";
    	    	grandTotal = parseFloat(grandTotal).toLocaleString(locale);
    	    	cartDiscountTotal = cartDiscountTotal.split(",")[0];
    	    	totalSalesTax = parseFloat(totalSalesTax).toLocaleString(locale);
    	    	totalDue = parseFloat(totalDue).toLocaleString(locale);
    	    	var customerName = "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-size:" + infoFontSize + "pt\">" + BPOSCustomer + ":</span></td>" +
    	    					   "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-weight:bold;font-size:" + infoFontSize + "pt\">" + partyName + "</span></td>";
    	    	var customerAddress = "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-size:" + infoFontSize + "pt\">" + BPOSAddress + ":</span></td>" +
    	    						  "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-weight:bold;font-size:" + infoFontSize + "pt\">" + partyAddress + "</span></td>";
    	    	var customerPhone = "<td colspan=\"6\" rowspan=\"1\"><span style=\"font-size:" + infoFontSize + "pt\">" + BPOSMobile + ":</span></td>" +
    	    						"<td colspan=\"6\" rowspan=\"1\"><span style=\"font-weight:bold;font-size:" + infoFontSize + "pt\">" + partyMobile + "</span></td>";
    	    	$('#customerName').html(customerName);
    	    	$('#customerAddress').html(customerAddress);
    	    	$('#customerPhone').html(customerPhone);
    	    	for(var i = 0; i < orderItems.length; i++){
    	    		var index = i + 1;
    	            var row = orderItems[i];
    	            var productId = row.productId;
    	            var productName = row.productName;
    	            var quantityProduct = row.itemQuantity;
    	            var price = row.itemPrice.toLocaleString(locale);
    	            var discount = 0;
    	            if(row.discount){
    	            	discount = row.discount.toLocaleString(locale);
    	            }
    	            var amount = row.itemTotal.toLocaleString(locale);
    	             
    	            eachTr += "<tr>"+
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"text-align:center;display:block;padding:0 2px;font-size:" + contentFontSize + "pt\">" + index +"</span></td>"+
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"float:left;padding:0 2px;text-align:justify; font-size:" + contentFontSize + "pt\">" +productName + "</span></td>"+
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;padding:0 2px;font-size:" + contentFontSize + "pt\">" + price + "</span></td>"+
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;padding:0 2px;font-size:" + contentFontSize + "pt\">" + discount+ "</span></td>"+
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"text-align:center;display:block;padding:0 2px;font-size:" + contentFontSize + "pt\">" + quantityProduct +"</span></td>"+
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;padding:0 2px;font-size:" + contentFontSize + "pt\">"+ amount + "</span></td>"+
    	    					"</tr>";
    	        }
    	    	
    	    	eachTr += "<tr>" +
    	    				"<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSGrandTotal + "</span></td>"+
    	    				"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + grandTotal+ "</span></td>"+
    	    			"</tr>" +
    	    				"<tr>" +
    	    					"<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSDiscount+ "</span></td>" +
    	    					"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + cartDiscountTotal +"</span></td>" +
    	    				"</tr>";
    	    	if(showPricesWithVatTax){
    	    		eachTr += "<tr>" +
    	    						"<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSTotalSalesTax+ "</span></td>" +
    	    						"<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + totalSalesTax +"</span></td>" +
    	    				  "</tr>";	
    	    					
    	    	}
    	    	eachTr += "<tr>"+ 
    	    				 "<td style=\"border:1px solid #CCC;\" colspan=\"5\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + BPOSTotalPay+ "</span></td>" +
    	    				 "<td style=\"border:1px solid #CCC;\"><span style=\"float:right;font-weight:bold;padding:0 2px;font-size:" + contentFontSize + "pt\">" + totalDue + "</span></td>"+
    	    			  "</tr>";
    	    	$('#bodyPrint').html(eachTr);

   	        }
   	    });
	}
	
	var source;
	function getFieldType(fName){
    	for (i=0;i < source.datafields.length;i++) {
        	if(source.datafields[i]['name'] == fName){
            	if(!(typeof source.datafields[i]['other'] === 'undefined' || source.datafields[i]['other'] =="")){
                	return  source.datafields[i]['other'];
                }else{
                    return  source.datafields[i]['type'];
                }
            }
        }
	}
	var flagSalesHistoryWindow = 0;
	function updateSalesHistory(){
		flagSalesHistoryWindow = 1; 
		Loading.show('loadingMacro');
		var cusId = $('#partyIdTmp').val();
		$('#salesHistoryWindow').jqxWindow('open');
		var urlStr = 'jqxGeneralServicer?sname=JQSalesOrderHistory&hasrequest=Y&createdBy=' + '${userLogin.userLoginId}' + "&checkUserLogin=Y" + "&customerId=" + cusId; 
		source =
		{
			datafields:
		      [
		           { name: 'orderId', type: 'string' },
		           { name: 'returnId', type: 'string' },
		           { name: 'orderDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'},
		           { name: 'returnDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'},
		           { name: 'grandTotal', type: 'number' },
		           { name: 'returnGrandTotal', type: 'number' },
		           { name: 'transactionType', type: 'string' },
		           { name: 'partyId', type: 'string' },
		           { name: 'firstName', type: 'string' },
		           { name: 'middleName', type: 'string' },
		           { name: 'statusIdPTL', type: 'string' },
		           { name: 'posTerminalLogId', type: 'string' },
		           { name: 'lastName', type: 'string' }
		      ],
			cache: false,
		    root: 'results',
		    datatype: "json",
		    updaterow: function (rowid, rowdata) {
		         
		    },
		    beforeprocessing: function (data) {
		   		source.totalrecords = data.TotalRows;
		    },
		    pager: function (pagenum, pagesize, oldpagenum) {
		         
		    },
		    filter: function () {
	            // update the grid and send a request to the server.
	            $("#salesHistory").jqxGrid('updatebounddata');
	        },
	        sort: function () {
	            // update the grid and send a request to the server.
	            $("#salesHistory").jqxGrid('updatebounddata');
	        },
		    sortcolumn: '',
			sortdirection: '',
		    type: 'POST',
		    data:{
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
			},
			pagesize:20,
		    contentType: 'application/x-www-form-urlencoded',
		    url: urlStr
		};
	
		var dataAdapter = new $.jqx.dataAdapter(source,
		{	
			formatData: function (data) {
		    	if (data.filterscount) {
		        	var filterListFields = "";
		            var tmpFieldName = "";
		            for (var i = 0; i < data.filterscount; i++) {
		                var filterValue = data["filtervalue" + i];
		                var filterCondition = data["filtercondition" + i];
		                var filterDataField = data["filterdatafield" + i];
		                var filterOperator = data["filteroperator" + i];
		                if(getFieldType(filterDataField)=='number'){
		                    filterListFields += "|OLBIUS|" + filterDataField + "(BigDecimal)";
		                }else if(getFieldType(filterDataField)=='date'){
		                    filterListFields += "|OLBIUS|" + filterDataField + "(Date)";
		                }else if(getFieldType(filterDataField)=='Timestamp'){
		                    filterListFields += "|OLBIUS|" + filterDataField + "(Timestamp)[HH:mm:ss dd/MM/yyyy]";
		                }
		                else{
		                    filterListFields += "|OLBIUS|" + filterDataField;
		                }
		                if(getFieldType(filterDataField)=='Timestamp'){
		                    if(tmpFieldName != filterDataField){
		                        filterListFields += "|SUIBLO|" + filterValue + " 00:00:00";
		                    }else{
		                        filterListFields += "|SUIBLO|" + filterValue + " 11:59:59";
		                    }
		                }else{
		                    filterListFields += "|SUIBLO|" + filterValue;
		                }
		                filterListFields += "|SUIBLO|" + filterCondition;
		                filterListFields += "|SUIBLO|" + filterOperator;
		                tmpFieldName = filterDataField;
		            }
		            data.filterListFields = filterListFields;
				}
	            data.$skip = data.pagenum * data.pagesize;
	            data.$top = data.pagesize;
	            data.$inlinecount = "allpages";
	            return data;
			},
		});
		var shcoloring = function (row, column, value, data) {
			var data = $('#salesHistory').jqxGrid('getrowdata', row);
			if(data.statusIdPTL=="POSTX_RETURNED"){
				return "shredcell";
			}else if(data.statusIdPTL="POSTX_SOLD" && data.returnId != null){
				return "shorangecell";
			}
		}
		$("#salesHistory").jqxGrid({
		    width: '100%',
		    height: 550,
		    source: dataAdapter,
		    filterable: true,
		    showfilterrow : true,
		    sortable: true,
		    virtualmode: true,
		    rendergridrows: function () {
		    	return dataAdapter.records;
		    },
		 	autoheight: false,
		    columnsresize: true,
		    pagesize: 20,
		    pageable: true,
		    localization: getLocalization(),
		    columns: [
	                 { text: '${StringUtil.wrapString(uiLabelMap.BPOSOrderId)}', datafield: 'orderId', width: 150, cellClassName: shcoloring,
	                 	cellsrenderer: function (row, column, value) {
		                 	if(value){
		                 		return "<div style='margin-left: 5px;margin-top: 5px;'>" + value + "</div>";
		                 	}else{
		                 		var data = $('#salesHistory').jqxGrid('getrowdata', row);
		                 		if(data.returnId){
		                 			return "<div style='margin-left: 5px;margin-top: 5px;'>" + data.returnId + "</div>";
		                 		}
		                 	}
	                 }},
	                 { text: '${StringUtil.wrapString(uiLabelMap.BPOSTransactionOrder)}',filtertype: 'range', cellsformat: 'HH:mm:ss dd/MM/yyyy', datafield: 'orderDate', width: 180 , cellClassName: shcoloring,
	                 	cellsrenderer: function (row, column, value) {
                 			var data = $('#salesHistory').jqxGrid('getrowdata', row);
                 			if(data.returnDate){
                 				return "<div style='margin-left: 5px;margin-top: 5px;'>" + (new Date(data.returnDate)).formatDate('HH:mm:ss dd/MM/yyyy') + "</div>";
                 			}else if(value){
	                 			return "<div style='margin-left: 5px;margin-top: 5px;'>" + (new Date(value)).formatDate('HH:mm:ss dd/MM/yyyy') + "</div>";
	                 		}
	                 }},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSGrandTotal)}', datafield: 'grandTotal', cellClassName: shcoloring,cellsalign: 'right', width: 150, filtertype: 'number',
	                	 cellsrenderer: function (row, column, value) {
	                	 	var data = $('#salesHistory').jqxGrid('getrowdata', row);
	                		 if(value){
	                			 return "<div style='margin-left: 5px; margin-top: 5px;  text-align: right; margin-right: 5px;'>" + formatcurrency(value, data.currencyUom) + "</div>";
	                		 }else{
	                		 	if(data.returnGrandTotal){
	                		 		return "<div style='margin-left: 5px; margin-top: 5px;  text-align: right; margin-right: 5px;'>" + formatcurrency(data.returnGrandTotal, data.currencyUom) + "</div>";
	                		 	}
	                		 }
	                	 }
	                 },
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSOrderType)}', datafield: 'transactionType', width: 220, cellClassName: shcoloring, filtertype: 'checkedlist',
	                	 cellsrenderer: function (row, column, value){
	                		 var data = $('#salesHistory').jqxGrid('getrowdata', row);
	                		 if (data.statusIdPTL=="POSTX_RETURNED"){
	                			 data.transactionType = "RETURN";
	                		 }else if (data.returnId != null){
	                		 	data.transactionType = "ORDER_RETURN";
	                		 }else if ((data.transactionType == "SALES_ORDER")&&(data.returnId == null)){
	                			 data.transactionType = "ORDER";
	                		 }else if (data.orderTypeId == "PURCHASE_ORDER"){
	                			 data.transactionType = "RETURN_ORDER";
	                		 }
	                		 if(data){
	                			for(i = 0 ; i < transactionType.length ; i++){
	                				if(data.transactionType == transactionType[i].transactionType){
	                					return "<div style='margin-left: 5px; margin-top: 5px; text-align: left;'>" + transactionType[i].description + "</div>";
	                				}
	                			}
	                		 }
	                	 },
	                	 createfilterwidget: function (column, columnElement, widget) {
						  		var sourceOt =
							    {
							        localdata: transactionType,
							        datatype: "array"
							    };
				   				var filterBoxAdapterOt = new $.jqx.dataAdapter(sourceOt,
				                {
				                    autoBind: true
				                });
				                var uniqueRecordsOt = filterBoxAdapterOt.records;
				   				uniqueRecordsOt.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				
	        					widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecordsOt, displayMember: 'description', valueMember: 'transactionType', autoDropDownHeight:false, dropDownHeight:200,
	        						renderer: function (index, label, value) {
	                    				for(i = 0; i < transactionType.length; i++){
											if(transactionType[i].transactionType==value){
												return '<span>' + transactionType[i].description + '</span>'
											}
	                    				}
	                    			return value;
	        					}});
	    					}
	              	 },
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSCustomerId)}', datafield: 'partyId', width: 180, cellClassName: shcoloring},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSLastName)}', datafield: 'lastName', width: 180, cellClassName: shcoloring},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSMiddleName)}', datafield: 'middleName', width: 180, cellClassName: shcoloring},
	              	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSFirstName)}', datafield: 'firstName', width: 180, cellClassName: shcoloring},
			         ],
			handlekeyboardnavigation: function (event) {
		    	var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		        if (event.ctrlKey && key == 13 && POSPermission.has("POS_RETURN_CTRL_ENTER", "CREATE")) {
	               	var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
	       			var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
	       			if (dataRow.orderTypeId == "RETURN_ORDER"){
	       				//$('#salesHistoryWindow').jqxWindow('close');
	       				bootbox.alert("${StringUtil.wrapString(uiLabelMap.BPOSOrderIsReturned)}", function() {
	       					//$('#salesHistoryWindow').jqxWindow('open');
	       				});
	       			} else {
	       				// Display popup
			 			ConfirmReturn.open();
	       				//returnWholeOrder(dataRow.orderId);
	       			}
	               	event.preventDefault();
	       			return false;
		   		}
			},
		    ready: function(){   
		    	$("#salesHistory").jqxGrid('selectrow', 0);
		    },
		});
	      	
		$("#salesHistory").on("bindingcomplete", function (event){
			$("#salesHistory").on("filter", function (event){
				var getRows = $("#salesHistory").jqxGrid("getrows");
				if(getRows[0]){
					var firstRow = getRows[0];
					var orderId = firstRow.orderId;
					var posTerminalLogId = firstRow.posTerminalLogId;
					if(orderId){
						updateOverViewSalesHistory(orderId, posTerminalLogId, false);
						updateOrderItemsSalesHistory(orderId, posTerminalLogId, false);
					}else{
						updateOverViewSalesHistory(firstRow.returnId, posTerminalLogId, true);
						updateOrderItemsSalesHistory(orderId, posTerminalLogId, true);
					}
				}else{
					resetOverviewSalesHistory();
					var orderItemsData = [{}];
					updateSoruceOrderItems(orderItemsData);
				}
			});
   	
			$("#salesHistory").on("sort", function (event){
				var getRows = $("#salesHistory").jqxGrid("getrows");
				if(getRows[0]){
					var firstRow = getRows[0];
					var orderId = firstRow.orderId;
					var posTerminalLogId = firstRow.posTerminalLogId;
					if(orderId){
						updateOverViewSalesHistory(orderId, posTerminalLogId, false);
						updateOrderItemsSalesHistory(orderId, posTerminalLogId, false);
					}else{
						updateOverViewSalesHistory(firstRow.returnId, posTerminalLogId, true);
						updateOrderItemsSalesHistory(orderId, posTerminalLogId, true);
					}
					
				}else{
					resetOverviewSalesHistory();
					var orderItemsData = [{}];
					updateSoruceOrderItems(orderItemsData);
				}
		 	});

	 		var data = $("#salesHistory").jqxGrid('getrows');
			if (data.length > 0){
				 $("#salesHistory").jqxGrid('selectrow', 0);
				 $("#salesHistory").jqxGrid('focus');
			} else {
				 $("#salesHistoryWindow").jqxWindow('focus');
			}
		});

	  	$('#salesHistory').on('rowselect', function (event) {
		 	var args = event.args;
			var row = event.args.row;
			if(row && row.returnId){
				updateOverViewSalesHistory(row.returnId, row.posTerminalLogId, true);
				updateOrderItemsSalesHistory(row.returnId, row.posTerminalLogId, true);
			} else if (row && row.orderId){
				updateOverViewSalesHistory(row.orderId, row.posTerminalLogId, false);
				updateOrderItemsSalesHistory(row.orderId, row.posTerminalLogId, false);
			}
	  	});
	      
	  	$('#salesHistory').on('pagechanged', function (event) {
			var args = event.args;
			var pagenum = args.pagenum;
			var pagesize = args.pagesize;
			$("#salesHistory").on("bindingcomplete", function (event) {
				$("#salesHistory").jqxGrid('selectrow', pagenum*pagesize);
			});
	  	});
	}
	
	function returnWholeOrder(orderId){
		var param = "orderId=" + orderId + "&facilityId=${facilityId?if_exists}";
		bootbox.confirm("${uiLabelMap.BPOSAreYouSureReturnThisOrder}", function(result) {
			if(result){
				Loading.show('loadingMacro');
		    	setTimeout(function(){
		    		$.ajax({url: 'returnWholeOrder',
		                data: param,
		                type: 'post',
		                async: false,
		                success: function(data) {
		                	ConfirmReturn.data = data;
		                    getResultOfReturnWholeOrder(data);
		                },
		                error: function(data) {
		                    getResultOfReturnWholeOrder(data);
		                }
		            });
		    		Loading.hide('loadingMacro');
		    	}, 500);
			} else {
				$('#salesHistoryWindow').jqxWindow('focus');
			}
		});
	}
	
	function getResultOfReturnWholeOrder(data){
		var serverError = getServerError(data);
   	    if (serverError != "") {
   	    	//$('#salesHistoryWindow').jqxWindow('close');
   	    	bootbox.alert(serverError, function() {
   	    		//$('#salesHistoryWindow').jqxWindow('open');
   	    		$('#salesHistoryWindow').jqxWindow('focus');
   	    	});
   	    } else {
   	    	bootbox.alert("${uiLabelMap.BPOSReturnOrderSuccess}", function() {
   	    		$('#salesHistoryWindow').jqxWindow('focus');
   	    	});
   	    	$('#returnOrder').jqxButton({
   	    	    disabled: true
   	    	});
   	    	$("#salesOrderReturned").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSOrderIsReturned)}</b>");
   	     	$('#salesHistory').jqxGrid('updatebounddata');
   	    }
	}
	
	//process select order
    function updateOverViewSalesHistory(orderId, posTerminalLogId, isReturn){
		$('#salesOrderId').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSOrderId)}: </b>" + orderId);
		var param = 'posTerminalLogId=' + posTerminalLogId;
		if(isReturn){
			param += '&returnId=' + orderId;
		}else{
   			param += '&orderId=' + orderId;
   		}
   	    $.ajax({url: 'GetOverViewSalesHistory',
   	        data: param,
   	        type: 'post',
   	        async: false,
   	        success: function(data) {
   	            getResultOfupdateOverViewSalesHistory(data);
   	        },
   	        error: function(data) {
   	        	getResultOfupdateOverViewSalesHistory(data);
   	        }
   	    });
   	}
	
   	function getResultOfupdateOverViewSalesHistory(data){
   		var serverError = getServerError(data);
   	    if (serverError != "") {
   	    	bootbox.hideAll();
   	    	bootbox.alert(serverError, function() {
   	    	});
   	    } else {
   	    	var customerName = data.customerName;
   	    	var telecomnumber = data.telecomnumber;
   	    	var address = data.address;
   	    	var orderDate = data.orderDate;
   	    	var returnDate = data.returnDate;
   	    	var orderTotal = data.orderTotal;
   	    	var returnTotal = data.returnTotal;
   	    	var orderDiscount = data.orderDiscount;
   	    	var returnDiscount = data.returnDiscount;
   	    	var orderSalesTax = data.orderSalesTax;
   	    	var grandTotal = data.grandTotal;
   	    	var returnedTotal = data.returnedTotal;
   	    	var orderReturned = data.orderReturned;
   	    	var currencyUom = data.currencyUom;
   	    	if(customerName){
   	    		$("#salesCustomerName").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}: </b>" + customerName);
   	    	}else{
   	    		$("#salesCustomerName").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}: </b>");
   	    	}
   	    	if(telecomnumber){
   	    		$("#salesTelecomnumber").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSMobile)}: </b>" + telecomnumber);
   	    	}else{
   	    		$("#salesTelecomnumber").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSMobile)}: </b>");
   	    	}
   	    	if(address){
   	    		$("#salesAddress").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSAddress)}: </b>" + address);
   	    	}else{
   	    		$("#salesAddress").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSAddress)}: </b>");
   	    	}
   	    	if(orderDate){
   	    		$("#salesOrderTime").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTime)}: </b>" + orderDate);
   	    	}else if(returnDate){
   	    		$("#salesOrderTime").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTime)}: </b>" + returnDate);
   	    	}else{
   	    		$("#salesOrderTime").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTime)}: </b>");
   	    	}
   	    	if(orderTotal){
   	    		$("#salesOrderTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHeldOrderTotal)}: </b>" + formatcurrency(orderTotal, currencyUom));
   	    	}else if(returnTotal){
   	    		$("#salesOrderTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHeldOrderTotal)}: </b>" + formatcurrency(returnTotal, currencyUom));
   	    	}else{
   	    		$("#salesOrderTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHeldOrderTotal)}: </b>");
   	    	}
   	    	if(orderDiscount){
   	    		$("#salesOrderDiscount").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}: </b>" + formatcurrency(orderDiscount, currencyUom));
   	    	}else if(returnDiscount){
   	    		$("#salesOrderDiscount").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}: </b>" + formatcurrency(returnDiscount, currencyUom));
   	    	}else{
   	    		$("#salesOrderDiscount").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}: </b>");
   	    	}
   	    	<#if showPricesWithVatTax == "N">
	   	    	if(orderSalesTax){
	   	    		$("#salesOrderTax").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTotalSalesTax)}: </b>" + formatcurrency(orderSalesTax, currencyUom));
	   	    	} else {
	   	    		$("#salesOrderTax").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTotalSalesTax)}: </b>");
	   	    	}
	   	    </#if>
   	    	if(grandTotal){
   	    		$("#salesGrandTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCartTotalNotAdj)}: </b> " + formatcurrency(grandTotal, currencyUom));
   	    	}else if(returnTotal){
   	    		$("#salesGrandTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCartTotalNotAdj)}: </b> " + formatcurrency(returnTotal, currencyUom));
   	    	}else{
   	    		$("#salesGrandTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCartTotalNotAdj)}: </b> ");
   	    	}
   	    	if(orderReturned){
   	    		$("#salesOrderReturned").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSOrderIsReturned)} </b>");
   	    		$('#returnOrder').jqxButton({disabled: true });
   	    	}else{
   	    		$("#salesOrderReturned").html("");
   	    		$('#returnOrder').jqxButton({disabled: false });
   	    	}
   	    }
   	}
   	
   	function resetOverviewSalesHistory(){
   		$('#salesOrderId').html("<b>${StringUtil.wrapString(uiLabelMap.BPOSOrderId)}: </b>");
   		$("#salesCustomerName").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCustomer)}: </b>");
   		$("#salesTelecomnumber").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSMobile)}: </b>");
   		$("#salesAddress").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSAddress)}: </b>");
   		$("#salesOrderTime").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSTime)}: </b>");
   		$("#salesOrderTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSHeldOrderTotal)}: </b>");
   		$("#salesOrderDiscount").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}: </b>");
   		$("#salesGrandTotal").html("<b>${StringUtil.wrapString(uiLabelMap.BPOSCartTotalNotAdj)}: </b> ");
   		$("#salesOrderReturned").html("");
   		$('#returnOrder').jqxButton({disabled: false });
   	}
   	
   	function updateOrderItemsSalesHistory(orderId, posTerminalLogId, isReturn){
   		var param = "posTerminalLogId=" + posTerminalLogId;
   		if(isReturn){
   			param += "&returnId=" + orderId;
   		}else{
   			param += "&orderId=" + orderId;
   		}
   		$.ajax({url: 'GetOrderItems',
   	        data: param,
   	        type: 'post',
   	        async: false,
   	        success: function(data) {
   	            getResultOfUpdateOrderItemsSalesHistory(data);
   	        },
   	        error: function(data) {
   	        	getResultOfUpdateOrderItemsSalesHistory(data);
   	        }
   	    });
   	}
   	
   	var sourceOrderItems = 
   		{
	   	   localdata: [],
	   	   dataType: "array",
           datafields: [
              { name: 'productId', type: 'string' },
              { name: 'productCode', type: 'string' },
              { name: 'internalName', type: 'string' },
              { name: 'productName', type: 'string' },
              { name: 'itemQuantity', type: 'number' },
              { name: 'currencyUom', type: 'string' },
              { name: 'quantityUomId', type: 'string' },
              { name: 'itemPrice', type: 'number' },
              { name: 'itemAdjustment', type: 'number' },
              { name: 'itemTotal', type: 'number' }
           ]
		};

   	function initOrderItems(){
   		var dataAdapterItems = new $.jqx.dataAdapter(sourceOrderItems);
   		$("#detailSalesorder").jqxGrid({
             width: '100%',
             height: '100%',
             source: dataAdapterItems,
             filterable: false,
             pageable: false,
             sortable: false,
             columnsresize: true,
             localization: getLocalization(),
             columns: [
                 { text: '${StringUtil.wrapString(uiLabelMap.BPOSId)}', datafield: 'productCode', width: 130 },
                 { text: '${StringUtil.wrapString(uiLabelMap.BPOSProductName)}', datafield: 'productName'},
                 { text: '${StringUtil.wrapString(uiLabelMap.BPOSQuantity)}', datafield: 'itemQuantity', width: 80,cellsalign: 'right'},
             	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSUom)}', datafield: 'quantityUomId', width: 60, 
                	 cellsrenderer: function (row, column, value){
                		 var data = $('#detailSalesorder').jqxGrid('getrowdata', row);
                		 if(data){
                			for(i =0 ; i < dataUom.length ; i++){
                				if(data.quantityUomId == dataUom[i].uomId){
                					return "<div style='margin-left: 5px; margin-top: 5px; text-align: left;'>" + dataUom[i].description + "</div>";
                				}
                			}
                		 }
                	 }
				 },
               	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSUnitPrice)}', datafield: 'itemPrice', width: 100,cellsalign: 'right',
                	 cellsrenderer: function (row, column, value) {
                		 var data = $('#detailSalesorder').jqxGrid('getrowdata', row);
                		 if(data){
                			 return "<div style='margin-left: 5px; margin-top: 5px;text-align: right; margin-right: 5px;'>" + formatcurrency(data.itemPrice, data.currencyUom) + "</div>";
                		 }
                	 }
				 },
               	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSDiscount)}', datafield: 'itemAdjustment', width: 100,cellsalign: 'right',
                	 cellsrenderer: function (row, column, value) {
                		 var data = $('#detailSalesorder').jqxGrid('getrowdata', row);
                		 if(data){
                			 return "<div style='margin-left: 5px; margin-top: 5px;text-align: right; margin-right: 5px;'>" + formatcurrency(data.itemAdjustment, data.currencyUom) + "</div>";
                		 }
                	 }
				 },
               	 { text: '${StringUtil.wrapString(uiLabelMap.BPOSTotalPrice)}', datafield: 'itemTotal', width: 150,cellsalign: 'right',
                	 cellsrenderer: function (row, column, value) {
                		 var data = $('#detailSalesorder').jqxGrid('getrowdata', row);
                		 if(data){
                			 return "<div style='margin-left: 5px; margin-top: 5px; text-align: right; margin-right: 5px;'>" + formatcurrency(data.itemTotal, data.currencyUom) + "</div>";
                		 }
                	 }
				 }
             ]
         });
   	}
   	
   	function getResultOfUpdateOrderItemsSalesHistory(data){
   		var serverError = getServerError(data);
   	    if (serverError != "") {
   	        bootbox.alert(serverError);
   	    } else {
   	    	updateSoruceOrderItems(data);
   		}
   	}
   	
   	function updateSoruceOrderItems(data){
   		sourceOrderItems.localdata = data.listOrderItems;
	    var dataAdapterItems = new $.jqx.dataAdapter(sourceOrderItems);
	    $("#detailSalesorder").jqxGrid({source: dataAdapterItems});
	    $("#detailSalesorder").jqxGrid('updatebounddata');
   	}
	
   	$(document).ready(function(){
   		$('body').keydown(function(e) {
   		    var code = (e.keyCode ? e.keyCode : e.which);
   		    //72 la ma cua H
   		    if(e.ctrlKey && code == 72 && POSPermission.has("POS_ORDER_CTRL_H", "VIEW")){
	    		if (flagPopup){
	    			if(flagSalesHistoryWindow==0){
	    				updateSalesHistory();
	    			}else{
	    				Loading.show('loadingMacro');
	    				var tmpS = $("#salesHistory").jqxGrid('source');
	    				if($('#partyIdTmp').val() != null && $('#partyIdTmp').val() != ""){
	    					tmpS._source.url= 'jqxGeneralServicer?sname=JQSalesOrderHistory&hasrequest=Y&createdBy=' + '${userLogin.userLoginId}' + "&checkUserLogin=Y" + "&customerId=" + $('#partyIdTmp').val();
	    				}else{
	    					tmpS._source.url= 'jqxGeneralServicer?sname=JQSalesOrderHistory&hasrequest=Y&createdBy=' + '${userLogin.userLoginId}' + "&checkUserLogin=Y";
	    				}
	    				$("#salesHistory").jqxGrid('source', tmpS); 
	    				$('#salesHistoryWindow').jqxWindow('open');
	    			}
	    		}
   		    	e.preventDefault();
   		    	return false;
   		    }
   		});
   	});
</script>
<script type="text/javascript">
	$.jqx.theme = 'basic';
	theme = $.jqx.theme;
	
	$("#salesHistoryWindow").jqxWindow({
		width: "95%", maxWidth: "95%", minHeight: "95%", resizable: false, draggable: false, isModal: true, autoOpen: false, modalOpacity: 0.7, theme: theme,
		position: { x: "2%", y: "5%" }
	});
	$("#splitter").jqxSplitter({ width: "100%", height: "95%", panels: [{size: "31.7%", min: "31.7%", collapsible: false}, { size: "60%" }] });
	$("#nestedSpliter").jqxSplitter({ width: "100%",height: "100%",  orientation: 'horizontal', panels: [{ size: 150 }] });
	$("#returnOrder").jqxButton({width: 100, template: 'primary'});
	$("#printOrder").jqxButton({width: 100, template: 'primary'});
	$("#expandCollapse").jqxToggleButton({toggled: true});
</script>
<style>
	.shredcell{
		background-color: #CF6182;
	}
	.shorangecell{
		background-color: #F7982A;
	}
</style>