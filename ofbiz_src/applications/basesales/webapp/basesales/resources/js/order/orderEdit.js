$(function(){
	OlbEditSO.init();
});
var OlbEditSO = (function(){
	var init = function(){
		initElement();
		initElementAdvance();
		initEvent();
	};
	var initElement = function(){
		jOlbUtil.notification.create("#containerEditSO", "#jqxNotificationEditSO");
        jOlbUtil.contextMenu.create($("#contextMenu"));
        jOlbUtil.windowPopup.create($("#windowEditContactMech"), {maxWidth: 1200, width: 1200, height: 600, showCloseButton: false});
	};
	var initElementAdvance = function(){
		var datafields = [
			{ name: 'orderId', type: 'string' },
			{ name: 'orderItemSeqId', type: 'string' },
			{ name: 'shipGroupSeqId', type: 'string' },
			{ name: 'productId', type: 'string' },
			{ name: 'productCode', type: 'string' },
       		{ name: 'productName', type: 'string' },
       		{ name: 'quantityUomId', type: 'string'},
       		{ name: 'packingUomIds', type: 'array'},
       		{ name: 'productPackingUomId', type: 'string'},
       		{ name: 'quantity', type: 'number', formatter: 'integer'},
       		{ name: 'packingUomId', type: 'string'}, 
       		{ name: 'isPromo', type: 'string'},
       		{ name: 'unitPrice', type: 'number', formatter: 'float'}
       	];
       	var columns = [
       		{ text: uiLabelMap.BSShipGroupSeqId, dataField: 'shipGroupSeqId', width: '16%', editable:false, cellclassname: cellclass},
	 		{ text: uiLabelMap.BSProductId, dataField: 'productCode', width: '16%', editable:false, cellclassname: cellclass},
		 	{ text: uiLabelMap.BSProductName, dataField: 'productName', editable:false, cellclassname: cellclass},
		 	{ text: uiLabelMap.BSUom, dataField: 'quantityUomId', width: '10%', columntype: 'dropdownlist', cellclassname: cellclass, 
         		cellsrenderer: function(row, column, value){
					for (var i = 0 ; i < uomData.length; i++){
						if (value == uomData[i].uomId){
							var content = '<div class=\"innerGridCellContent\" title=\"' + uomData[i].description + '\">' + uomData[i].description + '</div>';
							return content;
						}
					}
					return '<div class=\"innerGridCellContent\" title=\"' + value + '\">' + value + '</div>';
				},
				initeditor: function (row, cellvalue, editor) {
			 		var packingUomData = new Array();
					var data = $('#' + idGridJQ).jqxGrid('getrowdata', row);
					
					var itemSelected = data['quantityUomId'];
					var packingUomIdArray = data['packingUomIds'];
					
					for (var i = 0; i < packingUomIdArray.length; i++) {
						var packingUomIdItem = packingUomIdArray[i];
						var row = {};
						if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
							row['description'] = '' + packingUomIdItem.uomId;
						} else {
							row['description'] = '' + packingUomIdItem.description;
						}
						row['uomId'] = '' + packingUomIdItem.uomId;
						packingUomData[i] = row;
					}
			 		var sourceDataPacking = {
		                localdata: packingUomData,
		                datatype: 'array'
		            };
		            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
		            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
		            editor.jqxDropDownList('selectItem', itemSelected);
		      	}
         	},
         	{ text: uiLabelMap.BSUnitPrice, dataField: 'unitPrice', width: '12%', editable: false,
         		cellsalign: 'right', cellsformat: 'c', 
         		cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatcurrency(value, currencyUomId);
	   				returnVal += '</div>';
	   				return returnVal;
			 	}
         	},
       		{ text: uiLabelMap.BSQuantity, dataField: 'quantity', cellsalign: 'right', filterable:false, editable: true, sortable:false, 
       			cellclassname: cellclass, columntype: 'numberinput',
				validation: function (cell, value) {
					if (value <= 0) {
						return {result: false, message: uiLabelMap.BSQuantityMustBeGreaterThanZero};
					}
					return true;
				},
				createeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({decimalDigits: 0, digits: 9});
				}
		 	}
       	];
		var configProductList = {
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: datafields,
			columns: columns,
			useUrl: false,
			clearfilteringbutton: false,
			editable: true,
			alternativeAddPopup: 'alterpopupWindow',
			pageable: true,
			pagesize: 15,
			showtoolbar: false,
			editmode: 'click',
			selectionmode: 'multiplecellsadvanced',
			width: '100%',
			bindresize: true,
			groupable: false,
			localization: getLocalization(),
			showtoolbar: true,
			showdefaultloadelement: true,
			autoshowloadelement: true,
			virtualmode: false,
			
			rendertoolbar: function (toolbar) {
				toolbar.html("");
            	var grid = $('#' + idGridJQ);
                var me = this;
                var jqxheader = renderJqxTitle();
                toolbar.append(jqxheader);
                var container = $('#toolbarButtonContainer' + idGridJQ);
                var maincontainer = $("#toolbarcontainer" + idGridJQ);
                Grid.createAddRowButton(
                	grid, container, uiLabelMap.accAddNewRow, {
                		type: addType,
                		container: $("#" + alternativeAddPopup),
                		data: dataCreateAddRowButton,
                	}
                );
            },
            contextMenu: "contextMenu",
		};
		new OlbGrid($("#" + idGridJQ), localData, configProductList, []);
	};
	function simulateKeyPress(character) {
	  	jQuery.event.trigger({ type : 'keypress', which : 50});
	}
	var initEvent = function(){
		$('#alterCancelEdit').on('click', function(){
			window.open('viewOrder?orderId=' + orderId, '_self');
		});
		$('#we_alterSave').on('click', function(){
			$.ajax({
				type: 'POST',
				url: 'processEditSalesOrderSaveToOrder',
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	return false;
					}, function(data){
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	if (data != undefined && data != null && data.orderId != undefined) {
			        		window.location.href = 'viewOrder?orderId=' + data.orderId;
			        		return true;
			        	} else {
			        		location.reload();
			        	}
					});
					return true;
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		});
		$('#alterSaveEdit').on('click', function(){
			var rowIndex = $('#' + idGridJQ).jqxGrid('getselectedrowindex');
			if (OlbCore.isNotEmpty(rowIndex)) {
				$("#" + idGridJQ).jqxGrid('endcelledit', rowIndex, "quantity", true, true);
			}
			var dataMap = {
				orderId: orderId,
			};
			var data = $("#" + idGridJQ).jqxGrid("getrows");
			if (typeof(data) == 'undefined') {
				alert("Error check data");
			}
			
			var listProd = [];
			for (var i = 0; i < data.length; i++) {
				var dataItem = data[i];
				if (OlbCore.isNotEmpty(dataItem.quantity) && OlbCore.isNotEmpty(dataItem.productId)) {
					if (parseInt(dataItem.quantity) > 0) {
						var prodItem = {
							shipGroupSeqId: dataItem.shipGroupSeqId,
							orderItemSeqId: dataItem.orderItemSeqId,
							unitPrice: dataItem.unitPrice,
							productId: dataItem.productId,
							quantityUomId: dataItem.quantityUomId,
							quantity: dataItem.quantity
						};
						listProd.push(prodItem);
					}
				}
			}
			if (listProd.length > 0) {
				dataMap.listProd = JSON.stringify(listProd);
				
				$.ajax({
					type: 'POST',
					url: 'processEditSalesOrderLoadToCart',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", function(){
							$('#container').empty();
		    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
		    	        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
		    	        	$("#jqxNotification").jqxNotification("open");
		    	        	window.location.href = 'viewOrder?orderId=' + orderId;
		    	        	return true;
						}, function(data){
							$("#windowEditContactMech").jqxWindow("open");
							$("#windowEditContactMechContainer").html(data);
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			} else {
				jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
				return false;
			}
		});
		
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#" + idGridJQ).jqxGrid('getselectedrowindex');
	        var tmpKey = $.trim($(args).text());
	        if (tmpKey == uiLabelMap.BSRefresh) {
	        	$("#" + idGridJQ).jqxGrid('updatebounddata');
	        } else if (tmpKey == uiLabelMap.BSDeleteOrderItem) {
	        	var messageConfirm = "";
	        	var dataRows = $("#" + idGridJQ).jqxGrid('getrows');
	        	if (dataRows != undefined && dataRows.length == 1) {
	        		messageConfirm += "<b>" + uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen + "</b><br/>";
	        	}
	        	messageConfirm += uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem;
	        	
	        	jOlbUtil.confirm.dialog(messageConfirm, function() {
					var data = $("#" + idGridJQ).jqxGrid("getrowdata", rowindex);
					if (data != undefined && data != null) {
						var orderId = data.orderId;
						$.ajax({
							type: 'POST',
							url: 'cancelOrderItemSales',
							data: {
								orderId: orderId,
								orderItemSeqId: data.orderItemSeqId,
								shipGroupSeqId: data.shipGroupSeqId,
							},
							beforeSend: function(){
								$("#loader_page_common").show();
							},
							success: function(data){
								var result = jOlbUtil.processResultDataAjax(data, "default", function(){
									$('#container').empty();
				    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
				    	        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
				    	        	$("#jqxNotification").jqxNotification("open");
				    	        	window.location.href = 'viewOrder?orderId=' + orderId;
				    	        	return true;
								}, function(data){
									$("#windowEditContactMech").jqxWindow("open");
									$("#windowEditContactMechContainer").html(data);
								});
								return result;
							},
							error: function(data){
								alert("Send request is error");
							},
							complete: function(data){
								$("#loader_page_common").hide();
							},
						});
					}
	            });
	        }
		});
	};
	return {
		init: init
	};
}());