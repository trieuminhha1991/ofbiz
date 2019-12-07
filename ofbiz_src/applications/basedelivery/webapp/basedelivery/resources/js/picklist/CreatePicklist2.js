var CreatePicklist = (function () {
		var soGrid, selectedOrder = new Array(), Update = false;
		var facilitySelected = null;
		var contactMechSelected = null;
		var customerPartyIdSelected = null;
		var initElements = function () {
			
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			
			var initFacilityDrDGrid = function (dropdown, grid, width) {
				var datafields =
				[
					{ name: "facilityId", type: "string" },
					{ name: "facilityName", type: "string" }
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
					{ text: multiLang.FacilityId, datafield: "facilityId", width: 150 },
					{ text: multiLang.FacilityName, datafield: "facilityName" }
				];
				GridUtils.initDropDownButton({url: "jQListFacilities", autorowheight: false, filterable: true, showfilterrow: true,
					width: 800, source: {pagesize: 5},
					clearOnClose: false, dropdown: {width: 200 }},
					datafields, columns, null, grid, dropdown, "facilityId");
			};
			initFacilityDrDGrid($("#txtPickingFacility"), $("#jqxgridPickingFacility"), 800);
			
			var initFacilityDrDGrid = function (dropdown, grid, width) {
				var datafields =
					[
						{name: 'contactMechId', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'partyId', type: 'string'},
						{name: 'partyName', type: 'string'},
						{name: 'fullName', type: 'string'}, 
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
						{ datafield: 'contactMechId', hidden: true},
						{ text: multiLang.BSCustomerId, datafield: "partyCode", width: 100 },
						{ text: multiLang.BSCustomer, datafield: "partyName", width: 200 },
						{text: multiLang.Address, datafield: 'fullName'},
					 ];
				GridUtils.initDropDownButton({url: "JQGetShippingAddressFullCustomer2", autorowheight: false, filterable: true, showfilterrow: true,
					width: 800, source: {pagesize: 5},
					clearOnClose: false, dropdown: {width: 200 }},
					datafields, columns, null, grid, dropdown, "contactMechId", "partyId", "partyName", "fullName");
			};
			initFacilityDrDGrid($("#txtAddress"), $("#jqxgridCustomerAddress"), 800);
			
			var initSalesOrderDrDGrid = function (dropdown, grid, width) {
				var datafields =
				[
					{ name: "orderId", type: "string" },
					{ name: "customerCode", type: "string" },
					{ name: "addressFullName", type: "string" },
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
					{ text: multiLang.BSCustomer, datafield: "customerCode", width: 100 },
					{ text: multiLang.BSShippingAddress, datafield: "addressFullName", minwidth: 300, hidden: true },
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
					 	}, clearOnClose: false, dropdown: {width: 200}
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
			
			$("#jqxgridCustomerAddress").on("rowselect", function (event){
				var args = event.args;
				var rowData = args.row;				
				contactMechSelected = rowData.contactMechId;
				customerPartyIdSelected = rowData.partyId;
				if (facilitySelected && contactMechSelected && customerPartyIdSelected) {
					var adapter = soGrid.jqxGrid("source");
					if (adapter) {
						adapter.url = "jqxGeneralServicer?sname=JQGetListSalesOrderHeaderApproved2&facilityId=" + facilitySelected + "&contactMechId=" + contactMechSelected + "&partyId=" + customerPartyIdSelected;
						adapter._source.url = adapter.url;
						soGrid.jqxGrid("source", adapter);
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
			
			$("#txtPickingFacility").on("close", function (event) {
				facilitySelected = Grid.getDropDownValue($(this)).toString();
				if (facilitySelected && contactMechSelected && customerPartyIdSelected) {
					var adapter = soGrid.jqxGrid("source");
					if (adapter) {
						adapter.url = "jqxGeneralServicer?sname=JQGetListSalesOrderHeaderApproved2&facilityId=" + facilitySelected + "&contactMechId=" + contactMechSelected + "&partyId" + customerPartyIdSelected;
						adapter._source.url = adapter.url;
						soGrid.jqxGrid("source", adapter);
					}
				}
			});
//			$("#txtAddress").on("close", function (event) {
//				contactMechSelected = Grid.getDropDownValue($(this)).toString();
//				if (facilitySelected && contactMechSelected) {
//					var adapter = soGrid.jqxGrid("source");
//					if (adapter) {
//						adapter.url = "jqxGeneralServicer?sname=JQGetListSalesOrderHeaderApproved2&facilityId=" + facilitySelected + "&contactMechId=" + contactMechSelected;
//						adapter._source.url = adapter.url;
//						soGrid.jqxGrid("source", adapter);
//					}
//				}
//			});
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
					{ input: "#txtAddress", message: multiLang.fieldRequired, action: "valuechanged", 
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