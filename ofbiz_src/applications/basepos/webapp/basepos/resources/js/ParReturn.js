$(document).ready(function() {
});
var ParReturn = (function(){
	function parReturn(){};
	parReturn.prototype.initialized = false;
	parReturn.prototype.inputId = "";
	parReturn.prototype.windowId = "";
	parReturn.prototype.data = null;
	parReturn.prototype.focusInputCtlId = null;
	parReturn.prototype.altOn = false;
	parReturn.prototype.jqxGridId = null;
	parReturn.prototype.jqxAdjGridId = null;
	parReturn.prototype.grandTotal = null;
	parReturn.prototype.jqxReturnItemGridId = null;
	parReturn.prototype.gridReturnData = [];
	parReturn.prototype.gridAdjustmentData = [];
	parReturn.prototype.gridReturnAdjustmentData = [];
	parReturn.prototype.gridReturnProductData = [];
	parReturn.prototype = {
		initialize : function(){
			if(!this.initialized){
				this.grandTotal = 0;
				this.gridReturnData = [];
				this.gridAdjustmentData = [];
				this.focusInputCtlId = 'btnOkParReturn';
				this.jqxGridId = 'jqxParReturn';
				this.jqxAdjGridId = 'jqxParAdjustmentReturn';
				this.jqxReturnItemGridId = 'jqxParReturnItem';
				this.windowId = "jqxwindowParReturn";
				this.window().jqxWindow({
					width: "95%", maxWidth: "95%", minHeight: "95%", resizable: true,draggable: true, isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
				});
				this.window().on('open', function (event) {
					flagPopup = false;
				});
				this.window().on('close', function (event) { 
					flagPopup = true;
					ConfirmReturn.focusInput();
				});
				this.cashWindow().jqxWindow({theme: 'olbius', modalZIndex: 10000, zIndex:10000, width: 500, height: 200, resizable: false, draggable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, showCloseButton: true,});
				this.cashWindow().on('open', function (event) {
					flagPopup = false;
					$("#parReturnCashAmount").val((0).toFixed(2));
					$("#parReturnCashBackAmount").val((0).toFixed(2));
					$("#parReturnCashAmount").maskMoney({ precision:2,thousands: '.', decimal: ',', allowZero: true });
					$("#parReturnCashBackAmount").maskMoney({ precision:2,thousands: '.', decimal: ',', allowZero: true });
					$("#parReturnCashAmount").focus();
				});
				$("#parReturnCashAmount").keydown(function (e) {
					var key = 'which' in e ? e.which : e.keyCode;
				    //if the letter is not digit then display error and don't type anything
					if(key == 13){
						var amountCash = $("#parReturnCashAmount").maskMoney('unmasked')[0];
						if(amountCash == 0){
							$("#parReturnCashAmount").val((ParReturn.grandTotal).toFixed(2));
							$("#parReturnCashAmount").focus();
						}
					}
					else if (key != 8 && key != 0 && (key < 48 || key > 57) && key != 16 && key != 46 && key != 9) {
				   	 	return false;
				    } else {
				    	var amountCash = $("#parReturnCashAmount").maskMoney('unmasked')[0];
				    	var totalDue = ParReturn.grandTotal;
				    	amountCash = parseFloat(amountCash);
				    	totalDue = parseFloat(totalDue);
				    	var paybackCash = amountCash - totalDue;
				    	paybackCash = parseFloat(paybackCash);
				    	paybackCash = Math.round(paybackCash*100)/100;
				    	if(paybackCash >0){
				    		$("#parReturnCashBackAmount").maskMoney('mask', paybackCash);
				    	}else{
				    		$("#parReturnCashBackAmount").maskMoney('mask', 0.0);
				    	}
				    }
				});
				this.cashWindow().on('close', function (event) { 
					flagPopup = true;
				});
				$('#parReturnbtn').on('click', function(){
					ParReturn.printFlag = false;
					ParReturn.displayConfirm();
				});
				$('#parReturnPrintbtn').on('click', function(){
					ParReturn.printFlag = true;
					ParReturn.displayConfirm();
				});
				$('#btnOkParReturn').on('click', function(){
					var rowindexes = ParReturn.jqxgrid().jqxGrid('getselectedrowindexes');
					if(rowindexes == null || rowindexes.length == 0){
						ParReturn.displayError(GlobalMessagesJS.POSReturnItemEmpty, GlobalMessagesJS.POSErrorPopupTitle);
						return;
					}
					ParReturn.openCash();
				});
				$('#btnCancelParReturn').on('click', function(){
					ParReturn.close();
				});
				$('#jqxParReturnGrandTotal').html(formatcurrency(0));
				var datafields = [
	        			{ name: 'orderId', type: 'string'},
	        			{ name: 'orderItemSeqId', type: 'string'},
	        			{ name: 'quantityUomId', type: 'string'},
	        			{ name: 'productCode', type: 'string'},
	        			{ name: 'productId', type: 'string'},
	        			{ name: 'itemDescription', type: 'string'},
	        			{ name: 'alternativeQuantity', type: 'number' },
	        			{ name: 'alternativeUnitPrice', type: 'number' },
	        			{ name: 'returnableQuantity', type: 'number' },
	        			{ name: 'returnQuantity', type: 'number' },
	        			{ name: 'grandTotal', type: 'number' },
	        			{ name: 'tax', type: 'number' },
	        			{ name: 'itemGrandTotal', type: 'number' }
	           	];
	  			var columns = [
								{
								    text: '#', sortable: false, filterable: false, editable: false,
								    groupable: false, draggable: false, resizable: false,
								    datafield: '', columntype: 'number', width: 20,
								    cellsrenderer: function (row, column, value) {
								        return "<div style='margin:4px;'>" + (value + 1) + "</div>";
								    }
								},
	  			          		{ text: GlobalMessagesJS.POSproductCode, dataField: 'productCode',editable : false, width: '14%'},
	  			          		{ text: GlobalMessagesJS.POSitemDescription, dataField: 'itemDescription',editable : false, minWidth: '16%'},
	  			          		{ text: GlobalMessagesJS.POSalternativeQuantity, dataField: 'alternativeQuantity',editable : false, width: 20,
	  			          			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  			          				return '<div class=\"innerReturnGridRight\">' + value + '</div>';
	  		          			}},
	  		          			{ text: GlobalMessagesJS.POSreturnableQuantity, dataField: 'returnableQuantity',editable : false, width: 100,
	  		          				cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  		          					return '<div class=\"innerReturnGridRight\">' + value + '</div>';
	  	          				}},
	  	          				{ text: GlobalMessagesJS.POSreturnQuantity, dataField: 'returnQuantity', width: 60,editable : true,
	  	          					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  	          						if(value){
	  	          							return '<div class=\"innerReturnGridRight\">' + value + '</div>';
	  	          						}else{
	  	          							var data = $('#jqxParReturn').jqxGrid('getrowdata', row);
	  	          							return '<div class=\"innerReturnGridRight\">' + data.returnableQuantity + '</div>';
	  	          						}
	  	          					},
		  	          				cellbeginedit: function (row, datafield, columntype, oldvalue, newvalue) {
		  	          					$('#jqxParReturn').jqxGrid('unselectrow', row);
		  	          				},
		  	          				cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
		  	          					$('#jqxParReturn').jqxGrid('selectrow', row);
		  	          					var data = $('#jqxParReturn').jqxGrid('getrowdata', row);
		  	          					if(oldvalue == null){
		  	          						oldvalue = data.returnableQuantity;
		  	          					}
		  	          					var newTax = (newvalue*data.tax)/oldvalue;
		  	          					$("#jqxParReturn").jqxGrid('setcellvalue', row, "tax", newTax);
		  	          					ParReturn.updateReceiveAbleAmount(row, newvalue, newTax);
		  	          				}, 
		  	          				validation: function (cell, value) {
			  	          				if (value <= 0 || !value) {
		  									return { result: false, message: GlobalMessagesJS.POSWrongReturnQuantity };
		  								}
		  	          					var data = $('#jqxParReturn').jqxGrid('getrowdata', cell.row);
		  								if (value > data.returnableQuantity) {
		  									return { result: false, message: GlobalMessagesJS.POSReturnGreater };
		  								}
		  								return true;
	  							}},
	  			          		{ text: GlobalMessagesJS.POSquantityUomId, dataField: 'quantityUomId',editable : false, width: '5%',
	  	          					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  	          						for(i = 0; i < pusData.length;i++){
	  	          							if(pusData[i].quantityUomId == value){
	  	          								return '<div class=\"innerReturnGridRight\">' + pusData[i].description + '</div>';
	  	          							}
	  	          						}
  			          			}},
	  			          		{ text: GlobalMessagesJS.POSalternativeUnitPrice, dataField: 'alternativeUnitPrice',editable : false, width: '12%',
	  			          			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  			          				return '<div class=\"innerReturnGridRight\">' + formatcurrency(value) + '</div>';
	  		          			}},
	  			          		{ text: GlobalMessagesJS.POSTax, dataField: 'tax', width: '12%',editable : false,
	  			          			cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  			          				if(value != null){
		  			          				var data = $('#jqxParReturn').jqxGrid('getrowdata', row);
		  			          				return '<div class=\"innerReturnGridRight\">' + formatcurrency(data.tax) + '</div>';
	  			          				}else{
		  			          				return '<div class=\"innerReturnGridRight\">' + formatcurrency(0) + '</div>';
	  			          				}
	  		          			}},
	  		          			{ text: GlobalMessagesJS.POSamount, dataField: 'grandTotal', width: '12%',editable : false,
	  		          				cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  		          					var data = $('#jqxParReturn').jqxGrid('getrowdata', row);
	  		          					var grandTotal = 0;
	  		          					if(data.returnQuantity){
	  		          						grandTotal = data.returnQuantity*data.alternativeUnitPrice;
	  		          					}else{
	  		          						grandTotal = data.returnableQuantity*data.alternativeUnitPrice;
	  		          					}
	  		          					if(data.tax != null){
	  		          						grandTotal += data.tax;
	  		          					}
	  		          					return '<div class=\"innerReturnGridRight\">' + formatcurrency(grandTotal) + '</div>';
	  		          				}}
	  			          	];
	  			var configReturnItemList = {
	  					showdefaultloadelement: false,
	  					autoshowloadelement: false,
	  					dropDownHorizontalAlignment: 'right',
	  					datafields: datafields,
	  					columns: columns,
	  					useUrl: false,
	  					clearfilteringbutton: false,
	  					editable: true,
	  					pageable: false,
	  					scrollable: true,
	  					columnsresize: true,
	  					showtoolbar: true,
	  					editmode: 'click',
	  					selectionmode: 'multiplecellsadvanced',
	  					width: '99%',
	  					height: 300,
	  					bindresize: true,
	  					groupable: false,
	  					localization: getLocalization(),
	  					selectionmode: 'checkbox',
	  					selectall: false,
	  					virtualmode: false,
	  					rendertoolbarconfig: {
	  						titleProperty: GlobalMessagesJS.POSReturnItemList
  						}
	  			};
	  			var datafieldsAdj = [
	  			        			{ name: 'orderAdjustmentId', type: 'string'},
	  			        			{ name: 'description', type: 'string'},
	  			        			{ name: 'amount', type: 'number'}
	  			           	];
	  			var columnsAdj = [
	  			               {
	  		                      text: '#', sortable: false, filterable: false, editable: false,
	  		                      groupable: false, draggable: false, resizable: false,
	  		                      datafield: '', columntype: 'number', width: 20,
	  		                      cellsrenderer: function (row, column, value) {
	  		                          return "<div style='margin:4px;'>" + (value + 1) + "</div>";
	  		                      }
	  		                   },
	  			               { text: GlobalMessagesJS.POSPromoName, dataField: 'description', minWidth: '40%'},
	  			               { text: GlobalMessagesJS.POSamount, dataField: 'amount', width: '20%',
	  			            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	  			            		   return '<div class=\"innerReturnGridRight\">' + formatcurrency(Math.abs(value)) + '</div>';
	  		            	   }}
	  	            		   ];
	  			var configReturnItemListAdj = {
	  					showdefaultloadelement: false,
	  					autoshowloadelement: false,
	  					dropDownHorizontalAlignment: 'right',
	  					datafields: datafieldsAdj,
	  					columns: columnsAdj,
	  					useUrl: false,
	  					clearfilteringbutton: false,
	  					editable: false,
	  					pageable: false,
	  					scrollable: true,
	  					showtoolbar: true,
	  					columnsresize: true,
	  					editmode: 'click',
	  					selectionmode: 'click',
	  					width: '99%',
	  					height: 300,
	  					bindresize: true,
	  					groupable: false,
	  					localization: getLocalization(),
	  					selectall: false,
	  					virtualmode: false,
	  					rendertoolbarconfig: {
	  						titleProperty: GlobalMessagesJS.POSPromotionItemList
  						}
	  			};
	  			var datafieldsReturnItem = [
	  			                     { name: 'quantityUomId', type: 'string'},
	  			                     { name: 'orderAdjustmentId', type: 'string'},
	  			                     { name: 'productCode', type: 'string'},
	  			                     { name: 'productName', type: 'string'},
	  			                     { name: 'quantity', type: 'number' }
	  			                     ];
	  			var columnsReturnItem = [
								  {
								      text: '#', sortable: false, filterable: false, editable: false,
								      groupable: false, draggable: false, resizable: false,
								      datafield: '', columntype: 'number', width: 20,
								      cellsrenderer: function (row, column, value) {
								          return "<div style='margin:4px;'>" + (value + 1) + "</div>";
								      }
								  },
	  			                  { text: GlobalMessagesJS.POSproductCode, dataField: 'productCode', width: '25%'},
	  			                  { text: GlobalMessagesJS.POSitemDescription, dataField: 'productName', minWidth: '20%'},
  			                	  { text: GlobalMessagesJS.POSalternativeQuantity, dataField: 'quantity', width: '4%',
  			                		  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
  			                			  return '<div class=\"innerReturnGridRight\">' + value + '</div>';
		                		  }},
		                		  { text: GlobalMessagesJS.POSquantityUomId, dataField: 'quantityUomId', width: '9%',
		                			  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		                				  for(i = 0; i < pusData.length;i++){
		                					  if(pusData[i].quantityUomId == value){
		                						  return '<div class=\"innerReturnGridRight\">' + pusData[i].description + '</div>';
		                					  }
		                				  }
		                			  }}
		                		  ];
	  			var configReturnItemListReturnItem = {
  					showdefaultloadelement: false,
  					autoshowloadelement: false,
  					dropDownHorizontalAlignment: 'right',
  					datafields: datafieldsReturnItem,
  					columns: columnsReturnItem,
  					useUrl: false,
  					clearfilteringbutton: false,
  					editable: false,
  					pageable: false,
  					scrollable: true,
  					showtoolbar: true,
  					columnsresize: true,
  					editmode: 'click',
  					selectionmode: 'click',
  					width: '99%',
  					height: 300,
  					bindresize: true,
  					groupable: false,
  					localization: getLocalization(),
  					selectall: false,
  					virtualmode: false,
  					rendertoolbarconfig: {
  						titleProperty: GlobalMessagesJS.POSPromotionProductItemList 
  					}
	  			};
	  			new OlbGrid(ParReturn.jqxgrid(), ParReturn.gridReturnData, configReturnItemList, []);
				new OlbGrid(ParReturn.jqxgridAdj(), ParReturn.gridReturnAdjustmentData, configReturnItemListAdj, []);
				new OlbGrid(ParReturn.jqxgridReturnItem(), ParReturn.gridReturnProductData, configReturnItemListReturnItem, []);
				this.jqxgrid().on('rowselect', function (event){
					ParReturn.updateReceiveAbleAmount();
				});
				this.jqxgrid().on('rowunselect', function (event){
					ParReturn.updateReceiveAbleAmount();
				});
				
				this.initialized = true;
			}
		},
		updateReceiveAbleAmount : function(rowEditIndex, value, newTax){
			var rowindex = ParReturn.jqxgrid().jqxGrid('getselectedrowindexes');
			var tmpAmount = 0;
			for(i = 0; i < rowindex.length; i++){
				if(rowEditIndex == rowindex[i]){
					continue;
				}
				var tmpdata = ParReturn.jqxgrid().jqxGrid('getrowdata', rowindex[i]);
				if(tmpdata.returnQuantity){
					tmpAmount += tmpdata.alternativeUnitPrice * tmpdata.returnQuantity;
				}else{
					tmpAmount += tmpdata.alternativeUnitPrice * tmpdata.returnableQuantity;
				}
				if(tmpdata.tax){
					tmpAmount += tmpdata.tax;
				}
			}
			if(rowEditIndex != null){
				var data = ParReturn.jqxgrid().jqxGrid('getrowdata', rowEditIndex);
				tmpAmount += data.alternativeUnitPrice * value + newTax;
				rowindex[rowindex.length] = rowEditIndex;
			}
			// Get adjustment
			var listOrderItem = [];
			var j = 0;
			for(i = 0; i < rowindex.length; i++){
				var tmpdata = ParReturn.jqxgrid().jqxGrid('getrowdata', rowindex[i]);
				var tmpObject = {
					orderId : '' + tmpdata.orderId,
					orderItemSeqId : '' + tmpdata.orderItemSeqId
				}
				listOrderItem[j++] = tmpObject;
			}
			var param = new Object();
			param.orderItems = JSON.stringify(listOrderItem);
			$.ajax({url: 'getOrderAdjustment',
                data: param,
                type: 'post',
                async: false,
                success: function(data) {
        			ParReturn.gridReturnAdjustmentData = data.listOrderAdjustmentsPromo;
            		ParReturn.gridReturnProductData = data.listOrderItemsPromo;
            		if(!ParReturn.gridReturnAdjustmentData){
            			ParReturn.gridReturnAdjustmentData = [];
            		}
            		if(!ParReturn.gridReturnProductData){
            			ParReturn.gridReturnProductData = [];
            		}
                	OlbGridUtil.updateSource(ParReturn.jqxgridAdj(), null, ParReturn.gridReturnAdjustmentData);
                	OlbGridUtil.updateSource(ParReturn.jqxgridReturnItem(), null, ParReturn.gridReturnProductData);
                	for(i = 0; i < ParReturn.gridReturnAdjustmentData.length;i++){
                		tmpAmount += Math.abs(ParReturn.gridReturnAdjustmentData[i].amount);
                	}
                	ParReturn.grandTotal = tmpAmount;
                	$('#jqxParReturnGrandTotal').html(formatcurrency(tmpAmount));
                },
                error: function(data) {
                	ParReturn.displayError(data, GlobalMessagesJS.POSErrorPopupTitle);
                }
            });
		},
		jqxgrid : function(){
			return $('#' + this.jqxGridId);
		},
		jqxgridAdj : function(){
			return $('#' + this.jqxAdjGridId);
		},
		jqxgridReturnItem : function(){
			return $('#' + this.jqxReturnItemGridId);
		},
		focusInput : function(){
			if(this.focusInputCtlId == null){
				this.input().focus();
			}else{
				$('#' + this.focusInputCtlId).focus();
			}
		},
		open : function(product){
			if(!this.initialized){
				this.initialize();
			}
			this.window().jqxWindow('open'); 
			Loading.show('loadingMacro');
			// Get returnAbleItems
			var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
			var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
			var param = new Object();
			param.orderId = dataRow.orderId;
			ParReturn.gridAdjustmentData = [];
			ParReturn.gridReturnData = [];
			$.ajax({url: 'getParReturnAbleItems',
				data: param,
				type: 'POST',
				async: true,        
				success: function(data) {
					j = 0;
					k = 0;
					for(i = 0; i < data.returnItems.length; i++){
						if(data.returnItems[i].orderAdjustmentId || data.returnItems[i].isPromo == 'Y'){
							// Adjustment data
							if(data.returnItems[i].orderAdjustmentTypeId == 'SALES_TAX'){
								continue;
							}
							if(data.returnItems[i].amount > -1){
								ParReturn.gridAdjustmentData[j++] = data.returnItems[i];
							}
							if(data.returnItems[i].productId != null){
								ParReturn.gridAdjustmentData[j++] = data.returnItems[i];
								for(t = 0; t < data.returnItemMaps.length; t++){
									if(data.returnItemMaps[t].productCode && data.returnItemMaps[t].productId == ParReturn.gridAdjustmentData[j - 1].productId){
										ParReturn.gridAdjustmentData[j - 1].productCode = data.returnItemMaps[t].productCode;
									}
								}
							}
							ParReturn.gridAdjustmentData[j - 1].returnQuantity = ParReturn.gridAdjustmentData[j - 1].returnableQuantity;
						}else{
							// Order item data
							ParReturn.gridReturnData[k++] = data.returnItems[i];
							for(t = 0; t < data.returnItemMaps.length; t++){
								if(data.returnItemMaps[t].orderAdjustmentTypeId && data.returnItemMaps[t].orderItemSeqId == data.returnItems[i].orderItemSeqId){
									ParReturn.gridReturnData[k - 1].tax = data.returnItemMaps[t].returnablePrice;
								}
								if(data.returnItemMaps[t].orderItemSeqId == data.returnItems[i].orderItemSeqId && data.returnItemMaps[t].orderAdjustmentTypeId != 'SALES_TAX'){
									ParReturn.gridReturnData[k - 1].returnableQuantity = data.returnItemMaps[t].returnableQuantity;
								}
								if(data.returnItemMaps[t].productCode && data.returnItemMaps[t].productId == ParReturn.gridReturnData[k - 1].productId){
									ParReturn.gridReturnData[k - 1].productCode = data.returnItemMaps[t].productCode;
								}
							}
						}
					}
					// Reduce tax
					if(data.returnAdjustmentMaps && data.returnAdjustmentMaps.length > 0){
						for(i = 0; i < ParReturn.gridReturnData.length; i++){
							if(!ParReturn.gridReturnData[i].tax){
								ParReturn.gridReturnData[i].tax = 0;
							}
							for( x = 0; x < data.returnAdjustmentMaps.length;x++){
								if(data.returnAdjustmentMaps[x].orderItemSeqId == ParReturn.gridReturnData[i].orderItemSeqId){
									ParReturn.gridReturnData[i].tax = ParReturn.gridReturnData[i].tax - data.returnAdjustmentMaps[x].amount;
								}
							}
						}
					}
					OlbGridUtil.updateSource(ParReturn.jqxgrid(), null, ParReturn.gridReturnData);
					//OlbGridUtil.updateSource(ParReturn.jqxgridAdj(), null, ParReturn.gridReturnAdjustmentData);
					Loading.hide('loadingMacro');
				},
				error: function(data) {
					Loading.hide('loadingMacro');
					ParReturn.displayError(data, GlobalMessagesJS.POSErrorPopupTitle);
				}
			});
			this.focusInput();
		},	
		close : function(){
			if(!this.initialized){
				this.initialize();
			}
			this.window().jqxWindow('close'); 
		},
		fillData : function(data){
		},
		displayError : function(message, title){
			bootbox.dialog({
			  message: message,
			  title: title,
			  buttons: {
			    success: {
			      label: GlobalMessagesJS.BPOSOK,
			      className: "btn-small btn-primary",
			      callback: function() {
			    	  ParReturn.focusInput();
			      }
			    }
			  },
			  onEscape: function() {
				  ParReturn.focusInput();
			  }
			});
		},
		displayConfirm : function(message, title){
			var tmpTotalAmount = $('#parReturnCashAmount').maskMoney('unmasked')[0];
			var tmpReturnAmount = $('#parReturnCashBackAmount').maskMoney('unmasked')[0];
			tmpTotalAmount = parseFloat(tmpTotalAmount);
			tmpReturnAmount = parseFloat(tmpReturnAmount);
			if(Math.round((tmpTotalAmount - tmpReturnAmount)*100)/100 != ParReturn.grandTotal){
				this.displayError(GlobalMessagesJS.POSReturnAmountNotEnough, GlobalMessagesJS.POSErrorPopupTitle);
				return;
			}
			bootbox.confirm({
				message : GlobalMessagesJS.BPOSAreYouSureReturnThisOrder,
				buttons: {
					cancel: {
						label: GlobalMessagesJS.BPOSCancel,
						className: "btn-small btn-danger pull-right",
						callback: function() {
							ParReturn.focusInput();
						}
					},
					confirm: {
				      label: GlobalMessagesJS.BPOSOK,
				      className: "btn-small btn-primary pull-right margin-right-3px",
				      callback: function() {
				    	  ParReturn.focusInput();
				      }
				    }
				},
				callback: function(result){
					if(result){
						var rowindex = ParReturn.jqxgrid().jqxGrid('getselectedrowindexes');
						var returnList = [];
						j = 0;
						for(i = 0; i < rowindex.length; i++){
							var tmpdata = ParReturn.jqxgrid().jqxGrid('getrowdata', rowindex[i]);
							var returnQuantity = 0;
							if(tmpdata.returnQuantity){
								returnQuantity = tmpdata.returnQuantity;
							}else{
								returnQuantity = tmpdata.returnableQuantity;
							}
							var tmpObject = {
								productId : '' + tmpdata.productId,
								quantity : '' + returnQuantity,
								alternativeUnitPrice : '' + tmpdata.alternativeUnitPrice,
								orderItemSeqId : '' + tmpdata.orderItemSeqId
							}
							returnList[j++] = tmpObject;
						}
						var tmpdata = ParReturn.jqxgrid().jqxGrid('getrowdata', rowindex[0]);
						var param = new Object();
						param.returnItems = JSON.stringify(returnList);
						// returnAdjustment
						var returnAdjustment = [];
						j = 0;
						for(i = 0; i < ParReturn.gridReturnAdjustmentData.length;i++){
							var tmpObject = new Object();
							tmpObject.orderAdjustmentId = ParReturn.gridReturnAdjustmentData[i].orderAdjustmentId;
							returnAdjustment[j++] = tmpObject; 
						}
						for(i = 0; i < ParReturn.gridReturnProductData.length;i++){
							var tmpObject = new Object();
							tmpObject.orderAdjustmentId = ParReturn.gridReturnProductData[i].orderAdjustmentId;
							returnAdjustment[j++] = tmpObject;
						}
						param.returnAdjustment = JSON.stringify(returnAdjustment);
						param.orderId = tmpdata.orderId;
						param.grandTotal = ParReturn.grandTotal;
			    		$.ajax({url: 'returnPartialOrder',
			                data: param,
			                type: 'post',
			                async: false,
				    		beforeSend: function(){
				    			Loading.show('loadingMacro');
							},
			                success: function(data) {
			                	if(data.result){
				                	ParReturn.close();
				                	ConfirmReturn.close();
				                	$('#salesHistoryWindow').jqxWindow('close');
				                	Loading.hide('loadingMacro');
				                	// Print return order
				                	var returnId = data.result;
				                	ParReturn.prepareData(returnId);
				                	//ParReturn.print();
			                	}
			                },
			                error: function(data) {
			                	Loading.hide('loadingMacro');
			                	ParReturn.displayError(data, GlobalMessagesJS.POSErrorPopupTitle);
			                	ParReturn.close();
			                }
			            });
			    		Loading.hide('loadingMacro');
					}
				}
			});
		},
		prepareData : function(returnId){
			
		},
		print : function(){
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
		},
		clear : function(){
		},
		window : function(){
			return $('#' + this.windowId);
		},
		input : function(){
			return $('#' + this.inputId);
		}
	}
	return new parReturn();
})();
var ConfirmReturn = (function(){
	function confirmReturn(){};
	confirmReturn.prototype.initialized = false;
	confirmReturn.prototype.inputId = "";
	confirmReturn.prototype.printFlag = null;
	confirmReturn.prototype.wholeReturnWindowId = "";
	confirmReturn.prototype.windowId = "";
	confirmReturn.prototype.data = null;
	confirmReturn.prototype.focusInputCtlId = null;
	confirmReturn.prototype.altOn = false;
	confirmReturn.prototype = {
		initialize : function(){
			if(!this.initialized){
				this.focusInputCtlId = 'btncfOkParReturn';
				this.printFlag = false;
				this.windowId = "jqxwindowConfirmReturn";
				this.wholeReturnWindowId = "wholeReturnWindowPayCash";
				this.window().jqxWindow({
					width: '350', height:'150', resizable: true,draggable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
				});
				this.window().on('open', function (event) {
					flagPopup = false;
				});
				this.window().on('close', function (event) { 
					flagPopup = true;
					$('#returnOrder').focus();
				});
				$('#btncfOkParReturn').on('click', function () {
					 ConfirmReturn.openCash();
				});
				$('#btncfParReturn').on('click', function () {
					ParReturn.open();
				});
				$('#btncfCancelParReturn').on('click', function () {
					ConfirmReturn.close();
				});
				$('#wholeReturnbtn').on('click', function () {
					ConfirmReturn.printFlag = false;
					ConfirmReturn.returnWholeOrder();
				});
				$('#wholeReturnPrintbtn').on('click', function () {
					ConfirmReturn.printFlag = true;
					ConfirmReturn.returnWholeOrder();
				});
				this.wholeWindow().jqxWindow({theme: 'olbius', modalZIndex: 10000, zIndex:10000, width: 500, height: 250, resizable: false, draggable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, showCloseButton: true,});
				this.wholeWindow().on('open', function (event) {
					flagPopup = false;
					$("#wholeReturnCashAmount").val((0).toFixed(2));
					$("#wholeReturnCashBackAmount").val((0).toFixed(2));
					$("#wholeReturnCashAmount").maskMoney({ precision:2,thousands: '.', decimal: ',', allowZero: true });
					$("#wholeReturnCashBackAmount").maskMoney({ precision:2,thousands: '.', decimal: ',', allowZero: true });
					$("#wholeReturnCashAmount").focus();
					var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
					var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
					$("#grandTotalLbl").html(formatcurrency(dataRow.grandTotal));
				});
				$("#wholeReturnCashAmount").keydown(function (e) {
					var key = 'which' in e ? e.which : e.keyCode;
				    //if the letter is not digit then display error and don't type anything
					if(key == 13){
						var amountCash = $("#wholeReturnCashAmount").maskMoney('unmasked')[0];
						if(amountCash == 0){
							var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
							var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
							$("#wholeReturnCashAmount").val((dataRow.grandTotal).toFixed(2));
							$("#wholeReturnCashAmount").focus();
						}
					}
					else if (key != 0 && (key < 48 || key > 57) && key != 16 && key != 46 && key != 9 && key != 8) {
				   	 	return false;
				    } else if(key == 8){
				    	var amountCash = $("#wholeReturnCashAmount").maskMoney('unmasked')[0];
				    	amountCash = parseFloat(amountCash);
				    	if(amountCash > 0){
				    		amountCash = parseFloat(parseFloat(amountCash/10));
				    		var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
							var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
					    	var totalDue = dataRow.grandTotal;
					    	var paybackCash = Math.round((amountCash - totalDue)*100)/100;
					    	if(paybackCash > 0){
					    		$("#wholeReturnCashBackAmount").maskMoney('mask', paybackCash);
					    	}else{
					    		$("#wholeReturnCashBackAmount").maskMoney('mask', 0.0);
					    	}
				    	}
				    } 
					else {
				    	var amountCash = $("#wholeReturnCashAmount").maskMoney('unmasked')[0];
				    	amountCash = parseFloat(parseFloat(amountCash*10) + String.fromCharCode(key)/100 + 0);
				    	var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
						var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
				    	var totalDue = dataRow.grandTotal;
				    	totalDue = parseFloat(totalDue);
				    	var paybackCash = amountCash - totalDue;
				    	paybackCash = parseFloat(paybackCash);
				    	paybackCash = Math.round(paybackCash*100)/100;
				    	if(paybackCash > 0){
				    		$("#wholeReturnCashBackAmount").maskMoney('mask', paybackCash);
				    	}else{
				    		$("#wholeReturnCashBackAmount").maskMoney('mask', 0.0);
				    	}
				    }
				});
				this.wholeWindow().on('close', function (event) { 
					flagPopup = true;
				});
				this.initialized = true;
			}
		},
		returnWholeOrder : function(){
			// Validate cash amount
			var selectedrowindex = $('#salesHistory').jqxGrid('selectedrowindex'); 
			var dataRow = $('#salesHistory').jqxGrid('getrowdata', selectedrowindex);
			var tmpTotalAmount = $('#wholeReturnCashAmount').maskMoney('unmasked')[0];
			var tmpReturnAmount = $('#wholeReturnCashBackAmount').maskMoney('unmasked')[0];
			tmpTotalAmount = parseFloat(tmpTotalAmount);
			tmpReturnAmount = parseFloat(tmpReturnAmount);
			if(Math.round((tmpTotalAmount - tmpReturnAmount)*100)/100 != dataRow.grandTotal){
				this.displayError(GlobalMessagesJS.POSReturnAmountNotEnough, GlobalMessagesJS.POSErrorPopupTitle);
				return;
			}
			returnWholeOrder(dataRow.orderId);
			ConfirmReturn.close();
		},
		focusInput : function(){
			if(this.focusInputCtlId == null){
				this.input().focus();
			}else{
				$('#' + this.focusInputCtlId).focus();
			}
		},
		print : function(){
			
		},
		open : function(product){
			if(!this.initialized){
				this.initialize();
			}
			this.window().jqxWindow('open'); 
			this.focusInput();
		},	
		openCash : function(){
			this.wholeWindow().jqxWindow('open'); 
			this.focusInput();
		},
		close : function(){
			if(!this.initialized){
				this.initialize();
			}
			this.window().jqxWindow('close'); 
			this.wholeWindow().jqxWindow('close'); 
		},
		fillData : function(data){
		},
		displayError : function(message, title){
			bootbox.dialog({
				message: message,
				title: title,
				buttons: {
					success: {
						label: GlobalMessagesJS.BPOSOK,
						className: "btn-small btn-primary",
						callback: function() {
							ParReturn.focusInput();
						}
					}
				},
				onEscape: function() {
					ParReturn.focusInput();
				}
			});
		},
		clear : function(){
		},
		window : function(){
			return $('#' + this.windowId);
		},
		wholeWindow : function(){
			return $('#' + this.wholeReturnWindowId);
		},
		input : function(){
			return $('#' + this.inputId);
		}
	}
	return new confirmReturn();
})();