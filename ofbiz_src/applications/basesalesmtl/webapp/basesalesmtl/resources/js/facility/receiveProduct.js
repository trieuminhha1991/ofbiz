$(document).ready(function() {
	ReceiveProducts.init();
});

if (typeof (ReceiveProducts) == "undefined") {
	var ReceiveProducts = (function($) {
		var initJqxElements = function() {
			$("#jqxwindowReceiveProducts").jqxWindow({
				theme: "olbius", width: 950, maxWidth: 1845, minHeight: 310, height: 500, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#alterCancelReceive"), modalOpacity: 0.7
			});
			
			var source = { datatype: "json",
						datafields: [{ name: "facilityId" },
						             { name: "facilityName" }],
						             url: "getFacilityByPartyId?partyId=" + ownerPartyId};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#txtLookupFacility").jqxDropDownList({ theme: 'olbius', source: dataAdapter, width: 218, height: 30, displayMember: "facilityName", valueMember: "facilityId", placeHolder: multiLang.filterchoosestring});
			
			$("#jqxNotificationNestedSlide").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$('#jqxgridReceiveProducts').on('rowselect', function (event){
				    var args = event.args;
				    var rowBoundIndex = args.rowindex;
				    var rowData = args.row;
				    var gridId = $(event.currentTarget).attr("id");
				    var tmpArray = event.args.rowindex;
					if(typeof event.args.rowindex != 'number'){
				        for(i = 0; i < tmpArray.length; i++){
				            if(checkRequiredPurchaseLabelItem(tmpArray[i]), gridId){
				                $('#jqxgridReceiveProducts').jqxGrid('clearselection');
				                break; 
				            }
				        }
				    }else{
				    	if(checkRequiredPurchaseLabelItem(event.args.rowindex, gridId)){
				            $('#jqxgridReceiveProducts').jqxGrid('unselectrow', event.args.rowindex);
				        }
				    }
			});
		};
		var setData = function(data) {
			for ( var x in data) {
				data[x].receiveQuantity = data[x].quantity;
			}
			var source =
		    {
		        localdata: data,
		        datatype: 'local',
		        datafields:
		        [
					{ name: 'productId', type: 'string'},
					{ name: 'productName', type: 'string'},
					{ name: 'quantityUomId', type: 'string'},
					{ name: 'inventoryItemTypeId', type: 'string'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'unitListPrice', type: 'number'},
					{ name: 'quantity', type: 'number'},
					{ name: 'receiveQuantity', type: 'number'},
					{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
					{ name: 'expireDate', type: 'date', other: 'Timestamp'},
		        ],
		        id: 'productId'
		    };
		    var dataAdapter = new $.jqx.dataAdapter(source);
		    $("#jqxgridReceiveProducts").jqxGrid({
		    	localization: getLocalization(),
		        width: '100%',
		        height: 350,
		        theme: 'olbius',
		        source: dataAdapter,
		        sortable: false,
		        editable: true,
		        pagesize: 5,
		 		pageable: true,
		 		selectionmode: 'checkbox',
		        altrows: true,
		        columns: [
						{text: multiLang.DmsSequenceId, datafield: '', filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: multiLang.BSProductId, datafield: 'productId', width: 170, editable: false},
						{ text: multiLang.DmsProductName, datafield: 'productName', minWidth: 200, editable: false},
						{ text: multiLang.DmsQuantityUomId, datafield: 'quantityUomId', width: 150, editable: false,
							cellsrenderer: function(row, colum, value){
							   value?value=mapQuantityUom[value]:value;
						       return '<span>' + value + '</span>';
						   	}
						},
						{ text: multiLang.BSDeliveryQuantity, datafield: 'quantity', width: 150, align: 'right', editable: false,
							cellsrenderer: function(row, colum, value){
						       return '<span style=\"text-align: right\">' + value.toLocaleString(locale) + '</span>';
						   	}
						},
						{ text: multiLang.BSReceiveQuantity, datafield: 'receiveQuantity', width: 150, align: 'right', columntype:"numberinput",
							cellsrenderer: function(row, colum, value){
						        return '<span style=\"text-align: right\">' + value.toLocaleString(locale) + '</span>';
						   	},
							createeditor: function(row, column, editor){
                	    		editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
							},
							validation: function (cell, value) {
								var quantity = $("#jqxgridReceiveProducts").jqxGrid('getcellvalue', cell.row, "quantity");
								if (value < 0 || value > quantity) {
									return { result: false, message: multiLang.DmsQuantityNotValid };
								}
								return true;
                    	    }
						},
						{ text: multiLang.UnitPrice, dataField: 'unitListPrice', width: 100, align: 'right', cellsalign: 'right', editable: false,
							cellsrenderer: function(row, colum, value){
						       return '<span style=\"text-align: right\">' + value.toLocaleString(locale) + '</span>';
						   	}
						},
						{ text: multiLang.ManufactureDate, dataField: 'datetimeManufactured', width: 150, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', editable: false,
							validation: function (cell, value) {
						 		var now = new Date();
						        if (value > now) {
						            return { result: false, message: multiLang.ManufactureDateMustBeBeforeNow};
						        }
						        var data = $('#jqxgridReceiveProducts').jqxGrid('getrowdata', cell.row);
						        if (data.expireDate){
						        	var exp = new Date(data.expireDate);
						        	if (exp < new Date(value)){
							        	return { result: false, message: multiLang.ManufactureDateMustBeBeforeExpireDate};
							        }
						        }
						        return true;
							 },
							 createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
							 },
							 initeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
							 },
						 },
						 { text: multiLang.ExpireDate, dataField: 'expireDate', width: 150,columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', editable: false,
							 initeditor: function (row, column, editor) {
							 	editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
							 },
							 validation: function (cell, value) {
						        var data = $('#jqxgridReceiveProducts').jqxGrid('getrowdata', cell.row);
						        if (data.datetimeManufactured){
						        	var mft = new Date(data.datetimeManufactured);
							        if (mft > new Date(value)){
							        	return { result: false, message: multiLang.ExpireDateMustBeBeforeManufactureDate};
							        }
						        }
						        return true;
							 },
							 createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
							 },
						 },
						 { text: multiLang.InventoryItemType, dataField: 'inventoryItemTypeId', width: 150, columntype: 'dropdownlist',
							 cellsrenderer: function (row, column, value) {
								 value?value=mapItemTypes[value]:value;
								 return '<span style=\"text-align: left\">' + value +'</span>';
							 },
							 initeditor: function (row, cellvalue, editor) {
						 		var sourceTmp = {
					                localdata: invItemTypes,
					                datatype: 'array'
					            };
					            var dataAdapterTmp = new $.jqx.dataAdapter(sourceTmp);
					            editor.jqxDropDownList({ source: dataAdapterTmp, placeHolder: multiLang.filterchoosestring, displayMember: 'description', valueMember: 'inventoryItemTypeId'});
					            editor.jqxDropDownList('selectItem', 'NON_SERIAL_INV_ITEM');
							 }
						 },
						]
		    });
		};
		var getData = function() {
			var data = []; 
			var selectedIndexs = $('#jqxgridReceiveProducts').jqxGrid('getselectedrowindexes');
			for (var i = 0; i < selectedIndexs.length; i ++){
	    		data.push($('#jqxgridReceiveProducts').jqxGrid('getrowdata', selectedIndexs[i]));
			}
			return data;
		};
		var open = function(data) {
			setData(data);
			var wtmp = window;
			var tmpwidth = $('#jqxwindowReceiveProducts').jqxWindow('width');
	        $("#jqxwindowReceiveProducts").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowReceiveProducts").jqxWindow('open');
		};
		var submit = function(listProducts) {
			DataAccess.execute({
				url: "receiveInventoryProductFromOther",
				data: {
					listProducts: listProducts,
					facilityId: $("#txtLookupFacility").jqxDropDownList("val"),
					inventoryItemTypeId: "NON_SERIAL_INV_ITEM",
					shipmentId: shipmentId,
					deliveryId: deliveryId,
					datetimeReceived: new Date().getTime()
					}
				}, ReceiveProducts.notify);
		};
		var initValidator = function() {
			$("#jqxwindowReceiveProducts").jqxValidator({
			    rules: [{ input: '#txtLookupFacility', message: multiLang.fieldRequired, action: 'change', 
				        	rule: function (input, commit) {
				        		var value = input.jqxDropDownList('val');
				        		if (value) {
				        			return true;
				        		}
				        		return false;
				        	}
				        }],
			           position: 'bottom'
			});
		};
		var notify = function(res) {
			$('#jqxNotificationNestedSlide').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'error'});
				$("#notificationContentNestedSlide").text(multiLang.updateError);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}else {
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'info'});
				$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}
			$("#listShipment").jqxGrid('updatebounddata');
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			setData: setData,
			getData: getData,
			open: open,
			submit: submit,
			notify: notify
		}
	})(jQuery);
}