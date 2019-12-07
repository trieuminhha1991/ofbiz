var CreatePicklist = (function () {
	var btnClick = false;
		var customerDDB = null;
		var facilityDDB = null;
		var soGrid, selectedOrder = new Array(), Update = false;
		var facilitySelected = null;
		var customerSelected = null;
		var productStoreSelected = null;
		var initElements = function () {
			
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			
			var configProductStore = {
					width: 300,
					placeHolder: uiLabelMap.PleaseSelectTitle,
					useUrl: true,
					url: 'jqxGeneralServicer?sname=JQGetListProductStore',
					key: 'productStoreId',
					value: 'storeName',
					autoDropDownHeight: true
				}
			productStoreDDL = new OlbDropDownList($("#productStoreId"), null, configProductStore, []);
			
			var configFacility = {
					useUrl: true,
					root: 'results',
					widthButton: 300,
					showdefaultloadelement: false,
					autoshowloadelement: false,
					datafields: [{ name: "facilityId", type: "string" },
								 { name: "facilityCode", type: "string" },
								 { name: "facilityName", type: "string" }],
					columns: [
				          { text: multiLang.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
							 groupable: false, draggable: false, resizable: false,
							 datafield: '', columntype: 'number', width: 50,
							 cellsrenderer: function (row, column, value) {
								 return '<div style=margin:4px;>' + (value + 1) + '</div>';
							 }
						 },
						 { text: multiLang.FacilityId, datafield: "facilityCode", width: 150 },
						 { text: multiLang.FacilityName, datafield: "facilityName" }
					],
					url: 'jQListFacilities',
					useUtilFunc: true,
					
					key: 'facilityId',
					keyCode: 'facilityCode',
					description: ['facilityName'],
					autoCloseDropDown: true,
					filterable: true,
					sortable: true,
					width: 800,
				};
			facilityDDB = new OlbDropDownButton($("#txtPickingFacility"), $("#jqxgridPickingFacility"), null, configFacility, []);
			
			
			var configCustomer = {
					useUrl: true,
					root: 'results',
					widthButton: 300,
					showdefaultloadelement: false,
					autoshowloadelement: false,
					datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'partyName', type: 'string'}, 
					             {name: 'telecomId', type: 'string'}, {name: 'telecomName', type: 'string'}, {name: 'postalAddressName', type: 'string'}],
					columns: [
						{text: uiLabelMap.BSCustomerId, datafield: 'partyCode', width: '24%'},
						{text: uiLabelMap.BSFullName, datafield: 'partyName', width: '30%'},
						{text: uiLabelMap.BSPhone, datafield: 'telecomName', width: '20%'},
						{text: uiLabelMap.BSAddress, datafield: 'postalAddressName', width: '60%'},
					],
					url: '',
					useUtilFunc: true,
					
					key: 'partyId',
					keyCode: 'partyCode',
					description: ['partyName'],
					autoCloseDropDown: true,
					filterable: true,
					sortable: true,
					width: 800,
				};
			customerDDB = new OlbDropDownButton($("#customerId"), $("#customerGrid"), null, configCustomer, []);
				
			var initSalesOrderDrDGrid = function (dropdown, grid, width) {
				var datafields =
				[
					{ name: "orderId", type: "string" },
					{ name: "orderName", type: "string" },
					{ name: "orderDate", type: "date", other: "Timestamp"},
					{ name: "shipBeforeDate", type: "date", other: "Timestamp"},
					{ name: "shipAfterDate", type: "date", other: "Timestamp"}
				];
				var columns =
				[
					{ text: multiLang.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: multiLang.BSOrderId, datafield: "orderId", width: 120 },
					{ text: multiLang.ShipAfterDate, dataField: "shipAfterDate", width: 150, cellsformat: "dd/MM/yyyy", filtertype:"range",
						cellsrenderer: function (row, colum, value) {
							if (value){
								return "<span>" + (new Date(value)).formatDate('dd/MM/yyyy HH:mm') + "</span>";
							}
						}
					},
					{ text: multiLang.ShipBeforeDate, dataField: "shipBeforeDate", width: 150, cellsformat: "dd/MM/yyyy", filtertype:"range",
						cellsrenderer: function (row, colum, value) {
							if (value){
								return "<span>" + (new Date(value)).formatDate('dd/MM/yyyy HH:mm') + "</span>";
							}
						}
					},
					{ text: multiLang.BSCreateDate, dataField: "orderDate", width: 150, cellsformat: "dd/MM/yyyy HH:mm", filtertype:"range",
						cellsrenderer: function (row, colum, value) {
							return "<span>" + jOlbUtil.dateTime.formatFullDate(value) + "</span>";
						}
					},
					{ text: uiLabelMap.Description, dataField: "orderName", minwidth: 100,
					}
				];
				GridUtils.initDropDownButton({url: "", autorowheight: true, filterable: true, showfilterrow: true,
					width: 800, source: {pagesize: 5}, selectionmode: "checkbox", closeOnSelect: "N",
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								soGrid.jqxGrid("clearfilters");
								return true;
							}
					 	}, clearOnClose: false, dropdown: {width: 300}
				 	}, 
				 	datafields, 
				 	columns, null, grid, dropdown, "orderId", null);
			};
			initSalesOrderDrDGrid($("#txtPickingOrder"), soGrid, 800);
			
		};
		var handleEvents = function () {
			$("#btnUpload").click(function () {
				if ($("#stocking-form").jqxValidator("validate")) {
					var facilityId = Grid.getDropDownValue($("#txtPickingFacility")).toString();
					if (facilityId) {
						DataAccess.executeAsync({ url: "bufferIntoPickingItem",
							data: {
								orderIds: selectedOrder,
								facilityId: facilityId
							} }, function () {
								var adapter = mainGrid.jqxGrid("source");
								if (adapter) {
									adapter.url = "jqxGeneralServicer?sname=JQGetListPickingItemTempData&facilityId=" + facilityId;
									adapter._source.url = adapter.url;
									mainGrid.jqxGrid("source", adapter);
								}
						});
					}
				}
			});
			
			productStoreDDL.selectListener(function(itemData){
				var productStoreId = itemData.value;
				customerDDB.updateSource("jqxGeneralServicer?sname=JQGetCustomersByProductStore&productStoreId="+productStoreId);
				
				var adapter = soGrid.jqxGrid("source");
				if (adapter) {
					adapter.url = "";
					adapter._source.url = adapter.url;
					soGrid.jqxGrid("source", adapter);
				}
			});
			
			mainGrid.on("bindingcomplete", function (event) {
				if (!_.isEmpty(mainGrid.jqxGrid("getrows"))) {
					$("#btnCommit").removeClass("hidden");
					$("#btnDelete").removeClass("hidden");
				} else {
					if (!$("#btnCommit").hasClass("hidden")) {
						$("#btnCommit").addClass("hidden");
					}
					if (!$("#btnDelete").hasClass("hidden")) {
						$("#btnDelete").addClass("hidden");
					}
				}
			});
			$("#btnDelete").click(function () {
				DataAccess.executeAsync({ url: "deleteAllPickingItemTempData",
					data: {
						facilityId: Grid.getDropDownValue($("#txtPickingFacility")).toString()
					} }, function () {
					mainGrid.jqxGrid("updatebounddata");
				});
			});
			
			$("#btnCommit").click(function () {
				jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
					if (!btnClick){
						Loading.show('loadingMacro');
			        	setTimeout(function(){
							var data = {};
							data = {
								picklistId: picklistId,
								facilityId: Grid.getDropDownValue($("#txtPickingFacility")).toString(),
							}; 
							DataAccess.executeAsync({ 
									url: "transferToPicklistItem",
									data: data,
								}, function () {
									location.href = "Picklist";
							});
							Loading.hide('loadingMacro');
			        	}, 500);
						btnClick = true;
					}
		        }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
		        	btnClick = false;
		        });
			});

			soGrid.on("bindingcomplete", function (event) {
				if (!soGrid.find($("div[role=\"columnheader\"]")).find($("div[role=\"checkbox\"]")).hasClass("hide")) {
					soGrid.find($("div[role=\"columnheader\"]")).find($("div[role=\"checkbox\"]")).addClass("hide");
				}
				var displayrows = soGrid.jqxGrid("getdisplayrows");
				for ( var x in displayrows) {
					for ( var z in selectedOrder) {
						if (displayrows[x] && displayrows[x].orderId == selectedOrder[z]) {
							soGrid.jqxGrid("selectrow", displayrows[x].boundindex);
						}
					}
				}
			});
			
			soGrid.on("rowselect", function (event) {
				var args = event.args;
				var rowData = args.row;
				selectedOrder.push(rowData.orderId);
			});
			
			soGrid.on("rowunselect", function (event) {
				var args = event.args;
				var rowData = args.row;
				var orderId = rowData.orderId;
				$.each(selectedOrder, function(i){
	   				var olb = selectedOrder[i];
	   				if (olb == orderId){
	   					selectedOrder.splice(i,1);
	   					return false;
	   				}
	   			});
			});
			
			$("#customerId").on("close", function (event) {
//				facilitySelected = Grid.getDropDownValue($(this)).toString();
				var partyId = jOlbUtil.getAttrDataValue('customerId');
				var productStoreId = typeof($('#productStoreId').val()) != 'undefined' ? $('#productStoreId').val() : '';
				if (partyId && productStoreId) {
					var adapter = soGrid.jqxGrid("source");
					if (adapter) {
						adapter.url = "jqxGeneralServicer?sname=JQListSalesOrder&productStoreId=" + productStoreId + "&partyId=" + partyId + "&_statusId=ORDER_APPROVED";
						adapter._source.url = adapter.url;
						soGrid.jqxGrid("source", adapter);
					}
				}
			});
		};
		var initValidator = function () {
			$("#stocking-form").jqxValidator({
				rules:
				[
					{ input: "#txtPickingFacility", message: multiLang.fieldRequired, action: "valuechanged", 
						rule: function (input, commit) {
							var value = Grid.getDropDownValue(input).toString();
							if (value.trim()) {
								return true;
							}
							return false;
						}
					},
					{ input: "#customerId", message: multiLang.fieldRequired, action: "valuechanged", 
						rule: function (input, commit) {
							var value = Grid.getDropDownValue(input).toString();
							if (value.trim()) {
								return true;
							}
							return false;
						}
					},
					{ input: "#txtPickingOrder", message: multiLang.fieldRequired, action: "valuechanged", 
						rule: function (input, commit) {
							var value = Grid.getDropDownValue(input).toString();
							if (value.trim()) {
								return true;
							}
							return false;
						}
					}
				],
				position: "right",
				scroll : false
			});
		};
		var setOrderIds = function(orderIds) {
			selectedOrder = orderIds;
		};
		return {
			init: function () {
				mainGrid = $("#jqxgridPicking");
				soGrid = $("#jqxgridPickingOrder");
				initElements();
				handleEvents();
				initValidator();
			},
			setOrderIds: setOrderIds,
			Update: Update
		};
	})();