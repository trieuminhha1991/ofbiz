$(document).ready(function() {
	ProductPlan.init();
});
if (typeof (ProductPlan) == "undefined") {
	var ProductPlan = (function() {
		var mainGrid;
		var initJqxElements = function() {
			$("#contextMenu").jqxMenu({
				theme : theme,
				width : 220,
				autoOpenPopup : false,
				mode : "popup"
			});
		};
		var handleEvents = function() {
			mainGrid.on("contextmenu", function() {
				return false;
			});
			mainGrid.on("cellclick", function(event) {
				if (event.args.rightclick) {
					var args = event.args;
					var dataField = args.datafield;
					if (dataField == "productCode"
							|| dataField == "productName") {
						return true;
					}
					mainGrid.jqxGrid("selectcell", args.rowindex, dataField);
					var scrollTop = $(window).scrollTop();
					var scrollLeft = $(window).scrollLeft();
					$("#contextMenu").jqxMenu(
							"open",
							parseInt(event.args.originalEvent.clientX) + 5
									+ scrollLeft,
							parseInt(event.args.originalEvent.clientY) + 5
									+ scrollTop);
					return false;
				}
			});
			$("#contextMenu").on(
					"itemclick",
					function(event) {
						var args = event.args;
						var itemId = $(args).attr("id");
						switch (itemId) {
						case "mnuCreateOrder":
							var dataCell = mainGrid.jqxGrid("getselectedcell");
							if (dataCell) {
								window.open(
										"newPurchaseOrder?customTimePeriodId="
												+ dataCell.datafield
												+ "&productPlanId="
												+ productPlanId, "_blank");
							}
							break;
						default:
							break;
						}
					});
		};
		var load = function() {
			$.ajax({
				url : "loadProductPlanItem",
				type : "POST",
				dataType : "json",
				data : _.extend({
					productPlanId : productPlanId
				}, PlanFilter.getValue())
			}).done(function(res) {
				ProductPlan.render(res.compressedPackage);
			});
		};
		var render = function(data) {
			if (!_.isEmpty(data)) {
				var columns = data.columns;
				for ( var x in columns) {
					columns[x].cellsrenderer = function(row, column, value) {
						if (column == "productCode" || column == "productName") {
							return "<div>" + value + "</div>";
						} else {
							return "<div class='text-right'>"
									+ value.toLocaleString(locale) + "</div>";
						}
					};
				}
				var source = {
					localdata : data.localdata,
					datatype : "json",
					datafields : data.datafields
				};
				var dataAdapter = new $.jqx.dataAdapter(source);

				mainGrid
						.jqxGrid({
							theme : theme,
							localization : getLocalization(),
							width : "98%",
							source : dataAdapter,
							pageable : true,
							autoheight : true,
							sortable : true,
							enabletooltips : true,
							editable : false,
							selectionmode : "singlecell",
							showfilterrow : true,
							filterable : true,
							columns : columns,
							handlekeyboardnavigation : function(event) {
								var key = event.charCode ? event.charCode
										: event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									mainGrid.jqxGrid("clearfilters");
									return true;
								}
							},
							showtoolbar : true,
							rendertoolbar : function(toolbar) {
								var h4r = $("<h4>Danh sách kỳ thương mại</h4>");
								var container = $("<div style='margin: 12px 4px 0px 0px;' class='pull-right'></div>");
								var btnFilter = $("<a id='filterPlan' style='cursor: pointer; margin-right: 15px; font-size: 15px;' class='icon-filter open-sans'>"
										+ multiLang.BEDisplayOptions + "</a>");
								var btnCancelFilter = $("<a id='cancelFilterPlan' style='cursor: pointer; font-size: 15px;'><i class='icon-filter open-sans'><span style='color: red; right: 3px; position: relative;'>x</span></i>"
										+ multiLang.DmsCancelFilter + "</a>");
								toolbar.append(container);
								toolbar.append(h4r);
								container.append(btnFilter);
								container.append(btnCancelFilter);
								btnFilter.click(function() {
									PlanFilter.open();
								});
								btnCancelFilter.click(function() {
									mainGrid.jqxGrid("clearfilters");
								});
							}
						});
			}
		};
		return {
			init : function() {
				mainGrid = $("#jqxgridProductPlan");
				initJqxElements();
				handleEvents();
				PlanFilter.init();
				load();
			},
			render : render,
			load : load
		}
	})();
}
if (typeof (FilterAdapter) == "undefined") {
	var FilterAdapter = (function() {
		var apply = function() {
			ProductPlan.load();
		};
		return {
			apply : apply
		}
	})();
}