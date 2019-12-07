var invoiceDeliveryNoteObj = (function(){
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#shipmentItemBillingGrid");
		var datafield = [{name: 'shipmentId', type: 'string'},
		                 {name: 'shipmentItemSeqId', type: 'string'},
		                 {name: 'invoiceId', type: 'string'},
		                 {name: 'invoiceItemSeqId', type: 'string'},
		                 {name: 'productId', type: 'string'},
		                 {name: 'productCode', type: 'string'},
		                 {name: 'productName', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'subTotal', type: 'number'},
		                 ];
		
		var columns = [{text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '14%'},
		               {text: uiLabelMap.BACCProductName, datafield: 'productName', width: '26%'},
		               {text: uiLabelMap.LogExportQuantity, datafield: 'quantity', columntype: 'numberinput', filtertype: 'number', width: '16%', 
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + value + '</span>';	
								}
								return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BSUnitPrice, datafield: 'amount', columntype: 'numberinput', filtertype: 'number', width: '22%', 
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span style="text-align: right">' + formatcurrency(value) + '</span>';	
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BACCTotal, datafield: 'subTotal', columntype: 'numberinput', filtertype: 'number', width: '22%', 
		            	   cellsrenderer: function(row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span style="text-align: right">' + formatcurrency(value) + '</span>';	
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   }
		               },
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "shipmentItemBillingGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BSListProduct + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source: {
					pagesize: 5,
				}
		};
	    Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editDeliveryNoteWindow"), 800, 480);
	};
	var initEvent = function(){
		$("#editDeliveryNoteBtn").click(function(e){
			accutils.openJqxWindow($("#editDeliveryNoteWindow"));
		});
		$("#editDeliveryNoteWindow").on('open', function(e){
			updateGridUrl($("#shipmentItemBillingGrid"), 'jqxGeneralServicer?sname=JQGetShipmentItemBillingByInvoice&invoiceId=' + globalVar.invoiceId);
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getDeliveryByInvoiceId',
				data: {invoiceId: globalVar.invoiceId},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "error"){
						bootbox.dialog(response.errorMessage,
							  [
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);	
						return;
					}
					$("#deliveryIdView").html(response.deliveryId);
					$("#orderIdView").html(response.orderId);
					$("#statusIdView").html(response.statusDesc);
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		
		$("#closeEditDeliveryNote").click(function(e){
			$("#editDeliveryNoteWindow").jqxWindow('close');
		});
		
		$("#cancelDeliveryNote").click(function(e){
			bootbox.dialog(uiLabelMap.BACCCancelDeliveryNoteWarning,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							cancelDeliveryNote();	
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
	};
	
	var cancelDeliveryNote = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'cancelDeliveryNoteByInvoice',
			data: {invoiceId: globalVar.invoiceId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
						  [
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);	
					return;
				}
				bootbox.dialog(response.successMessage,
					  [
						{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								Loading.show('loadingMacro');
								location.reload();
							}
						}]		
				);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var updateGridUrl = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	invoiceDeliveryNoteObj.init();
});