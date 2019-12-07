$(document).ready(function() {
	SetupSupporter.init();
});
if (typeof (SetupSupporter) == "undefined") {
	var SetupSupporter = (function() {
		var mainGrid;
		var initJqxElements = function() {

		};
		var handleEvents = function() {
			mainGrid.on("bindingcomplete", function(event) {
				_.each($(this).jqxGrid("getdisplayrows"), showrowdetails);
			});
		};
		var showrowdetails = function(v) {
			if (v) {
				mainGrid.jqxGrid("showrowdetails", v.boundindex);
			}
		};
		var load = function(grid, productId) {
			$.ajax({
				url : "loadIndicesOfProduct",
				type : "POST",
				dataType : "json",
				data : _.extend({
					productPlanId : productPlanId,
					productId : productId
				}, PlanFilter.getValue())
			}).done(function(res) {
				SetupSupporter.render(grid, productId, res.compressedPackage);
			});
		};
		var render = function(grid, productId, data) {
			if (!_.isEmpty(data)) {
				var columns = data.columns;
				for ( var x in columns) {
					columns[x].cellclassname = function(row, column, value,
							data) {
						if (mapTimePeriod[column]) {
							if (new Date(mapTimePeriod[column]).getTime() < new Date()
									.getTime()) {
								return "expired";
							} else {
								var gridrowdata = grid.jqxGrid("getrowdata",
										row);
								if (gridrowdata.id == "PurchaseForecast"
										|| gridrowdata.id == "InventoryDesired") {
									return "editable";
								}
							}
						}
						return "";
					};
					columns[x].cellbeginedit = function(row, datafield,
							columntype, value) {
						var gridrowdata = grid.jqxGrid("getrowdata", row);
						if (gridrowdata.id == "PurchaseForecast"
								|| gridrowdata.id == "InventoryDesired") {
							if (mapTimePeriod[datafield]) {
								if (new Date(mapTimePeriod[datafield])
										.getTime() > new Date().getTime()) {
									return true;
								}
							}
						}
						return false;
					};
					columns[x].cellendedit = function(row, datafield,
							columntype, oldvalue, newvalue) {
						var rowid = grid.jqxGrid("getrowid", row);
						var salesForecast = grid.jqxGrid("getcellvaluebyid",
								"SalesForecast", datafield);
						var openingInventoryQuantity = grid.jqxGrid(
								"getcellvaluebyid", "OpeningInventoryQuantity",
								datafield);
						if (rowid == "PurchaseForecast") {
							grid.jqxGrid("setcellvaluebyid",
									"InventoryDesired", datafield, newvalue
											- salesForecast
											+ openingInventoryQuantity);
						} else if (rowid == "InventoryDesired") {
							grid.jqxGrid("setcellvaluebyid",
									"PurchaseForecast", datafield,
									salesForecast + newvalue
											- openingInventoryQuantity);
						}
					};
					columns[x].cellsrenderer = function(row, column, value) {
						if (column == "indices") {
							return "<div>" + value + "</div>";
						} else {
							return "<div class='text-right'>"
									+ value.toLocaleString(locale) + "</div>";
						}
					};
					columns[x].validation = function(cell, value) {
						var rowid = grid.jqxGrid("getrowid", cell.row);
						if (rowid == "PurchaseForecast") {
							if (value < 0) {
								return {
									result : false,
									message : multiLang.DmsQuantityNotValid
								};
							}
						} else if (rowid == "InventoryDesired") {
							var salesForecast = grid.jqxGrid("getcellvaluebyid", "SalesForecast", cell.datafield);
							var openingInventoryQuantity = grid.jqxGrid("getcellvaluebyid", "OpeningInventoryQuantity", cell.datafield);
							if (salesForecast + value - openingInventoryQuantity < 0) {
								return {
									result : false,
									message : multiLang.DmsQuantityNotValid
								};
							}
						}
						return true;
					};
				}

				var source = {
					localdata : data.localdata,
					datatype : "json",
					datafields : data.datafields,
					id : "id",
					updaterow : function(rowid, newdata, commit) {
						if (rowid == "PurchaseForecast") {
							commit(DataAccess.execute({
								url : "createOrStoreProductPlanItem",
								data : {
									productPlanId : productPlanId,
									productId : productId,
									purchaseForecast : JSON.stringify(newdata)
								}
							}, SetupSupporter.notify));
						}
						commit(true);
					}
				};
				var dataAdapter = new $.jqx.dataAdapter(source);

				grid.jqxGrid({
					theme : theme,
					localization : getLocalization(),
					width : "98%",
					source : dataAdapter,
					pageable : false,
					autoheight : true,
					sortable : false,
					altrows : true,
					enabletooltips : true,
					editable : true,
					selectionmode : "singlerow",
					columns : columns
				});

				$("#pager" + grid.attr("id")).addClass("hide");
			}
		};
		var notify = function(res) {
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				Grid.renderMessage(mainGrid.attr("id"), multiLang.updateError,
						{
							autoClose : true,
							template : "error",
							appendContainer : "#container"
									+ mainGrid.attr("id"),
							opacity : 0.9
						});
			} else {
				Grid.renderMessage(mainGrid.attr("id"),
						multiLang.updateSuccess, {
							autoClose : true,
							template : "info",
							appendContainer : "#container"
									+ mainGrid.attr("id"),
							opacity : 0.9
						});
			}
		};
		return {
			init : function() {
				mainGrid = $("#jqxgridSetupPlanPO");
				PlanFilter.init();
				initJqxElements();
				handleEvents();
			},
			load : load,
			render : render,
			notify : notify
		};
	})();
}
if (typeof (FilterAdapter) == "undefined") {
	var FilterAdapter = (function() {
		var apply = function() {
			var mainGrid = $("#jqxgridSetupPlanPO");
			var adapter = mainGrid.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListProductNonVirtual&displayOption="
						+ JSON.stringify(PlanFilter.getValue());
				adapter._source.url = adapter.url;
				mainGrid.jqxGrid("source", adapter);
			}
		};
		return {
			apply : apply
		}
	})();
}