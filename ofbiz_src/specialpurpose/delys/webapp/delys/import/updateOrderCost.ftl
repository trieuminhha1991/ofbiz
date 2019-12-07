<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>

<script>
	$(document).ready(function(){
    	$("#jqxNotificationNested").jqxNotification({ width: "1358px", appendContainer: "#container", opacity: 0.9, autoClose: false, template: "info" });
        
//    	$('#mainSplitter').jqxSplitter({ width: '100%', showSplitBar: false, splitBarSize: 1, resizable: false, height: 590, orientation: 'horizontal', panels: [{ size: 520 }, { size: 50 }] });
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
<#assign orderId = parameters.orderId !>
<#assign productStoreId = parameters.productStoreId !>
<#assign requirementId = parameters.requirementId !>
<#assign agreementId = parameters.agreementId !>
<#assign requiredByDate = parameters.requiredByDate !>
<#assign requirementDate = parameters.requirementDate !>
<#assign departmentId = parameters.departmentId !>
<#assign receiptId = parameters.receiptId !>
<#assign openTime = parameters.openTime !>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<div id="window" style="display:none; overflow: hidden;">
	<div id="windowHeader">
		<span>${uiLabelMap.updateCostOrder}</span>
	</div>
	<div style="overflow: hidden;" id="windowContent">
		<div>
			<div id="jqxNotificationNested">
				<div id="notificationContentNested">
				</div>
			</div>
	    	<div class="row-fluid">
	    		<div class="span12 margin-top8 ps-container ps-active-x ps-active-y" style="height: 360px; overflow: scroll; position: relative;" id="scrollPerfect" >
				    <#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
						if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
							var grid = $($(parentElement).children()[0]);
					        $(grid).attr(\"id\",\"jqxgridDetail\");
					        		if (grid != null) {
					   	             grid.jqxGrid({
					   	                 source: new Array(), width: '98%', height: 80,
					   	                 showtoolbar:false,
					   	                 showstatusbar: false,
					   			 		 showheader: true,
					   			 		 theme: \'olbius\',
					   	                 columns: [
					   					   { text: \'${uiLabelMap.invoiceItemTypeId}\', datafield: \'invoiceItemTypeId\', editable: false},
					   	                   { text: \'${uiLabelMap.childInvItemTypeId}\', datafield: \'childInvItemTypeId\', editable: false},
					   	                   { text: \'${uiLabelMap.costBase}\', datafield: \'costBase\', editable: false},
					   	                   { text: \'${uiLabelMap.costDescription}\', datafield: \'costDescription\', editable: false}
					   	                ]
					   	             });
					   	         }
					        return false;
						}
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
						         	 { name: \'invoiceItemTypeId\', type:\'string\' },
						             { name: \'childInvItemTypeId\', type: \'string\' },
						         	 { name: \'description\', type: \'string\' },
						         	 { name: \'costBase\', type: \'number\' },
						             { name: \'costDescription\', type: \'string\' },
							         { name: \'valueCost\', type: \'number\' },
						         ],
						           
						             localdata: ordersbyid,
						             updaterow: function (rowid, newdata, commit) {
						            	 commit(true);
						            	 var totalValue = 0;
						            	 var rows = grid.jqxGrid('getrows');
						            	 for(var i=0; i<rows.length; i++){
						            		 var colValue = 0;
						            		 if(typeof rows[i].valueCost != 'undefined'){
						            			 colValue = parseInt(rows[i].valueCost);
						            		 }
						            		 
						            		 totalValue += colValue;
						            	 }
							        	 $('#jqxgridCost').jqxGrid('setcellvalue', index, 'costPriceTemporary', totalValue);
						             }
						         }
						         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
						        
						         if (grid != null) {
						             grid.jqxGrid({
						                 source: nestedGridAdapter, width: '98%',
						                 autoheight: true,
						                 showtoolbar:false,
						                 showstatusbar: true,
						                 columnsheight: 30,
								 		 editable: true,
								 		 altrows: true,
								 		 editmode:\'selectedrow\',
								 		 showheader: true,
								 		 selectionmode:\'singlerow\',
								 		 showaggregates: true,
								 		 statusbarheight: 47,
								 		 rowsheight: 30,
								 		 theme: \'olbius\',
								 		 pageable: false,
						                 columns: [
											{
											    text: '', sortable: false, filterable: false, editable: false,
											    groupable: false, draggable: false, resizable: false,
											    datafield: '', columntype: 'number', width: 35,
											    cellsrenderer: function (row, column, value) {
											        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
											    }
											},
											{ text: \'${uiLabelMap.invoiceItemTypeId}\', datafield: \'invoiceItemTypeId\', editable: false, hidden: true
											},
						                   { text: \'${uiLabelMap.childInvItemTypeId}\', datafield: \'childInvItemTypeId\', editable: false, hidden: true
						                   },
						                   { text: \'${uiLabelMap.descriptionCost}\', datafield: \'description\', editable: false
						                   },
						                   { text: \'${uiLabelMap.costBase}\', datafield: \'costBase\', editable: false, width: 200, cellsalign: \'right\',
							   					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							                		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +formatcurrency(value)+ '</div>';
							                       },
						                   },
						                   { text: \'${uiLabelMap.costDescription}\', datafield: \'costDescription\', editable: false
						                   },
						                   { text: \'${uiLabelMap.valueCost}\', align:\'left\', datafield: \'valueCost\', columntype: \'numberinput\', width: 200, editable: true, cellsalign: \'left\',
						                	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						                		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +formatcurrency(value)+ '</div>';
						                       },
						                       createeditor: function (row, cellvalue, editor) {
						                           editor.jqxNumberInput({decimal: 0, spinButtonsStep:1000, inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.',digits:12 });
						                       },
						                       aggregates: ['sum'],
						                       aggregatesrenderer: 
						   					 	function (aggregates, column, element, summaryData) 
						   					 	{
						                             var renderstring = \"\";
						                              $.each(aggregates, function (key, value) {
					//	                            	 $('#jqxgridCost').jqxGrid('setcellvalue', index, 'costPriceTemporary', value);
						                                 renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.totalPrice}:<\\/b>' + '<br>' +  value.toLocaleString('VI')  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId?if_exists}\";
						                                 });                          
						                             	  renderstring += \"</div>\";
						                             	  return renderstring; 
						   					 	}
						                   }
						                ]
						             });
						         }
					 }"/>
				
						<#assign dataField="[{ name: 'costAccBaseId', type: 'string' },
											 { name: 'invoiceItemTypeId', type: 'string'},
											 { name: 'description', type: 'string'},
											 { name: 'costAccountingId', type: 'string'},
											 { name: 'costPriceTemporary', type: 'number'},
											 { name: 'rowDetail', type: 'string'},
											 ]"/>
						<#assign columnlist="					 
										{ text: '${uiLabelMap.costAccountingId}', datafield: 'costAccountingId', hidden: true, editable: false, filterable: false
										},
										{ text: '${uiLabelMap.costAccBaseId}', datafield: 'costAccBaseId', width: '150px', editable: false, filterable: false, hidden: true
										},
										{ text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'invoiceItemTypeId', editable: false, filterable: false, hidden: true
										},
										{ text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'description', editable: false, filterable: false
										},
										{ text: \'${uiLabelMap.costPriceTemporary}\', align:\'left\', datafield: \'costPriceTemporary\', columntype: \'numberinput\', width: 200, editable: false, cellsalign: \'left\',
						                	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						                		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +formatcurrency(value)+ '</div>';
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
						                                 renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.totalPrice}:<\\/b>' + '<br>' +  value.toLocaleString('VI')  + \"&nbsp;${defaultOrganizationPartyCurrencyUomId?if_exists}\";
						                                 });                          
						                             	  renderstring += \"</div>\";
						                             return renderstring; 
						   					 	}
						                   }
									 "/>
						<@jqGrid filtersimplemode="true" customLoadFunction="false" id="jqxgridCost" filterable="false" addType="" initrowdetails="true" rowsheight="30" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="true" addrow="false"
							editmode="selectedrow" editable="true" bindresize="false" altrows="true" autoheight="true"
							url="jqxGeneralServicer?sname=JQGetCostForOrder&orderId=${orderId}&departmentId=${departmentId}" height="360" showstatusbar= "true" rowdetailsheight="200" width="860"
						 />
				</div>
			</div>
			<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
			<div class="row-fluid">
		 		<div class="span12 margin-top10">
		 			<button style="float:right;" id="cancel" class="btn btn-mini btn-danger form-action-button pull-right" style="margin-top:5px;"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
		 			<button style="float:right;margin-right: 5px;" id="btnSubmit" class="btn btn-mini btn-primary form-action-button pull-right" style="margin-top:5px;"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		        </div>
		    </div>
		</div>
	</div>
</div>


<div id='wdConfirm' style="display: none;">
	<div>Header</div>
	<div>
	    <div>
	        <input type="button" id="confirm" value="Confirm" style="margin-right: 10px" />
	        <input type="button" id="notConfirm" value="Cancel" />
	    </div>
	</div>
</div>
<style type="text/css">
	.bootbox.modal {
		z-index: 18005;
	}
	.modal-backdrop {
		z-index: 18004;
	}
	.modal-footer a.btn {
		float: right;
		margin-left: 5px;
	}
</style>
<script type="text/javascript">
//$(document).ready(function(){
//	alert($('#window').jqxWindow('width'));
//});
	
	$('#window').jqxWindow({
	    showCollapseButton: false, theme:'olbius', resizable: false, zIndex: 9999,
	    isModal: true, autoOpen: false, height: 470, width: 900, maxWidth: '90%'
	});
	
//	/$('#window').on('open', function (event) {
//		initGridjqxgridCost();
//		var tmpS = $("#jqxgridCost").jqxGrid('source');
//		tmpS._source.url = "jqxGeneralServicer?sname=JQGetCostForOrder&orderId=" + ${orderId};
//		$("#jqxgridCost").jqxGrid('source', tmpS);
//	});
	$('#window').on('close', function (event) {
		$('#window').jqxWindow('destroy');
	});
	
//Submit data
	<#if departmentId == "IMPORT_ADMIN">
		$('#btnSubmit').on('click', function(){
			bootbox.confirm("${uiLabelMap.confirmCostOrder}",function(result){
				if(result){
					var dataRow = {};
					dataRow['orderId'] = '${orderId}';
					dataRow['agreementId'] = '${agreementId}';
					dataRow['productStoreId'] = '${productStoreId}';
					var rows = $('#jqxgridCost').jqxGrid('getrows');
					$.ajax({
						url: 'ajaxUpdateCostOrder',
				    	type: "POST",
				    	data: {orderCost: JSON.stringify(rows), orderId:'${orderId}' },
				    	async: false,
				    	success: function(data) {
				    		$('#jqxNotification').jqxNotification({ template: 'info'});
	                        $('#notificationContent').text('${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}');
	                        $('#jqxNotification').jqxNotification('open');
				    	},
				    	error: function(data){
				    		$('#jqxNotification').jqxNotification({ template: 'info'});
                         	$('#notificationContent').text(data.errorMessage);
                         	$('#jqxNotification').jqxNotification('open');
				    	}
						});
					dataFacility = dataRow;
					createRequirement();
					$('#window').jqxWindow('close');
				}
			});
		});
		<#elseif departmentId == "QA_QUALITY_MANAGER">
			$('#btnSubmit').on('click', function(){
				var openTime = '${StringUtil.wrapString(openTime)}';
				bootbox.confirm("${uiLabelMap.confirmCostOrder}",function(result){ 
					if(result){
						var rows = $('#jqxgridCost').jqxGrid('getrows');
						$.ajax({
							url: 'ajaxUpdateCostOrder',
					    	type: "POST",
					    	data: {orderCost: JSON.stringify(rows), orderId:'${orderId}'},
					    	async: false,
					    	success: function(data) {
					    	},
					    	error: function(data){
					    	}
							}).done(function() {
//								$.ajax({
//									url: 'createNotificationToLog',
//							    	type: "POST",
//							    	data: {partyId: '',action: 'getDetailReceipts' ,roleTypeId: 'LOG_SPECIALIST', targetLink: 'receiptId=${receiptId}' ,header: '${StringUtil.wrapString(uiLabelMap.NewReceiptIncoming)}',openTime: openTime, dateTime: '', receiptId: '${receiptId}'},
//							    	async: false,
//							    	success: function(data4) {
//							    		$('#jqxNotification').jqxNotification({ template: 'info'});
//				                        $('#notificationContent').text('${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}');
//				                        $('#jqxNotification').jqxNotification('open');
//							    	},
//							    	error: function(data4){
//							    		$('#jqxNotification').jqxNotification({ template: 'info'});
//			                         	$('#notificationContent').text(data.errorMessage);
//			                         	$('#jqxNotification').jqxNotification('open');
//							    	}
//									});
							});
						
						$('#window').jqxWindow('close');
					}
				});
			
		});
	</#if>
	
	$('#cancel').on('click', function(){
		$('#window').jqxWindow('close');
	});
	
//confirm Window
	$('#wdConfirm').jqxWindow({
	     theme: 'energyblue',
	     resizable: false,
	     zIndex: 1000,
	     modalZIndex: 19000,
	     modalBackgroundZIndex: 129,
	     autoOpen: false,
	     okButton: $('#confirm'),
	     cancelButton: $('#notConfirm'),
	     initContent: function () {
	         $('#confirm').jqxButton({
	             width: '65px',
	             theme: 'energyblue'
	         });
	         $('#confirm').on('click', function(){
	        	 var okButton = $('#wdConfirm').jqxWindow('okButton');
	         });
	         $('#notConfirm').jqxButton({
	             width: '65px',
	             theme: 'energyblue'
	         });
	         $('#confirm').focus();
	     }
	 });
	$('#scrollPerfect').perfectScrollbar({
	    wheelSpeed: 1,
	    wheelPropagation: false
	});
</script>
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