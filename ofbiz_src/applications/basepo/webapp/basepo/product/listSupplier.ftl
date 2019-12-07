<div id="jqxgridProductSuppliers"></div>

<#if !UpdateSupplier?exists>
<#assign UpdateSupplier = "true" />
<#assign showtoolbarSupplier = "true" />
<div id="contextMenu" style="display:none">
	<ul>
		<li id="update"><i class="fa-pencil-square-o open-sans"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.CommonUpdate)}</li>
	</ul>
</div>
<#include "supplierInfo.ftl"/>
</#if>
<#if !hasOlbPermission("MODULE", "PRODPRICE_EDIT", "UPDATE")>
	<#assign UpdateSupplier = "false" />
</#if>

<script>
	$(document).ready(function() {
		SupplierProduct.init();
		<#if UpdateSupplier == "true">
		AddSupplier.init();
		</#if>
	});
	var cellClassjqxSupplierProd = function (row, columnfield, value) {
		var data = $("#jqxgridProductSuppliers").jqxGrid("getrowdata", row);
		var returnValue = "";
		if (typeof(data) != "undefined") {
			var now = new Date();
			var availableFromDate = data.availableFromDate ? new Date(data.availableFromDate.time) : null;
			var availableThruDate = data.availableThruDate ? new Date(data.availableThruDate.time) : null;
			if (availableFromDate != null && availableFromDate <= now) {
				if (availableThruDate != null && (availableThruDate < now)) {
					return "background-cancel";
				}
			} else {
				if (availableThruDate == null || (availableThruDate >= now)) return "background-prepare";
				else return "background-cancel";
			}
		}
	}
	
	var SupplierProduct = (function() {
		var grid = $("#jqxgridProductSuppliers");
		var initJqxElements = function() {
		var source =
		{
			datatype: "json",
			url: "getListSuppliersOfProduct?productId=${parameters.productId?if_exists}",
			async: false,
			datafields:
			[
				{ name: "productId", type: "string" },
				{ name: "productCode", type: "string" },
				{ name: "partyId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "supplierPrefOrderId", type: "string" },
				{ name: "minimumOrderQuantity", type: "number" },
				{ name: "currencyUomId", type: "string" },
				{ name: "lastPrice", type: "number" },
				{ name: "shippingPrice", type: "number" },
				{ name: "quantityUomId", type: "string" },
				{ name: "supplierProductId", type: "string" },
				{ name: "availableFromDate", type: "date", other: "Timestamp" },
				{ name: "availableThruDate", type: "date", other: "Timestamp" },
				{ name: "canDropShip", type: "string" },
				{ name: "fullName", type: "string" },
				{ name: "comments", type: "string" }
			]
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		grid.jqxGrid({
			localization: getLocalization(),
			width: "100%",
			autoheight: true,
			theme: theme,
			source: dataAdapter,
			sortable: true,
			pagesize: 5,
			editable: false,
			columnsresize: true,
			pageable: true,
			selectionmode: "singlerow",
			<#if UpdateSupplier!="false">
			showtoolbar: true,
			rendertoolbar: rendertoolbar,
			</#if>
			columns: [
					{ text: "${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}", datafield: "", pinned: true, groupable: false, filterable: false, draggable: false, resizable: false, width: 50, cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function (row, column, value) {
							return "<div style=margin:4px;>" + (row + 1) + "</div>";
						}
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.ProductSuppliersId)}", dataField: "partyCode", width: 150, cellClassName: cellClassjqxSupplierProd },
					{ text: "${StringUtil.wrapString(uiLabelMap.ProductSupplier)}", dataField: "fullName", width: 250, columntype: "textbox", cellClassName: cellClassjqxSupplierProd },
					{ text: "${StringUtil.wrapString(uiLabelMap.BSSupplierProductId)}", dataField: "supplierProductId", width: 140, columntype: "textbox", cellClassName: cellClassjqxSupplierProd },
					{ text: "${StringUtil.wrapString(uiLabelMap.BSMinOrderQty)}", dataField: "minimumOrderQuantity", width: 150, columntype: "textbox", cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function (row, column, value) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						}	
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.BSBuyPrice)}", dataField: "lastPrice", width: 120, columntype: "textbox", cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function (row, column, value) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						}	
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.FormFieldTitle_shippingPrice)}", dataField: "shippingPrice", width: 120, columntype: "textbox", cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function (row, column, value) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						}	
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.ProductCurrencyUom)}", datafield: "currencyUomId", columntype: "dropdownlist", filtertype: "checkedlist", width: 150, cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function(row, colum, value) {
							value?value=mapCurrencyUom[value]:value;
							return "<span>" + value + "</span>";
						}
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}", datafield: "quantityUomId", columntype: "dropdownlist", filtertype: "checkedlist", width: 150, cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function(row, colum, value) {
							value?value=mapQuantityUom[value]:value;
							return "<span>" + value + "</span>";
						}
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.FormFieldTitle_canDropShip)}", dataField: "canDropShip", width: 150, columntype: "textbox", cellClassName: cellClassjqxSupplierProd },
					{ text: "${StringUtil.wrapString(uiLabelMap.POAccComments)}", dataField: "comments", width: 200, columntype: "textbox", cellClassName: cellClassjqxSupplierProd },
					{ text: "${StringUtil.wrapString(uiLabelMap.FormFieldTitle_availableFromDate)}", datafield: "availableFromDate", width: 200, cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function(row, colum, value) {
							value.time?value=jOlbUtil.dateTime.formatFullDate(new Date(value.time)):value;
							return "<span>" + value + "</span>";
						}
					},
					{ text: "${StringUtil.wrapString(uiLabelMap.FormFieldTitle_availableThruDate)}", datafield: "availableThruDate", width: 200, cellClassName: cellClassjqxSupplierProd,
						cellsrenderer: function(row, colum, value) {
							if (value.time) {
								value.time?value=jOlbUtil.dateTime.formatFullDate(new Date(value.time)):value;
							} else {
								value.time?value=jOlbUtil.dateTime.formatFullDate(new Date(value)):value;
							}
							return "<span>" + value + "</span>";
						}
					}]
			});
			function rendertoolbar(toolbar) {
				var container = $("<div style='margin: 17px 4px 0px 0px;' class='pull-right'></div>");
				var aTag = $("<a style='cursor: pointer;'><i class='fa-plus open-sans'></i>${StringUtil.wrapString(uiLabelMap.CommonAddNew)}</a>");
				var titleProperty = $("<h4 style='color: #4383b4;'>${StringUtil.wrapString(uiLabelMap.ListPartySupplier)}</h4>");
				toolbar.append(container);
				toolbar.append(titleProperty);
				<#if hasOlbPermission("MODULE", "PRODPRICE_NEW", "CREATE")>
				container.append(aTag);
				</#if>
				aTag.click(function() {
					AddSupplier.open();
				});
			}
			<#if UpdateSupplier!="false">
			$("#contextMenu").jqxMenu({ theme: theme, width: 200, autoOpenPopup: false, mode: "popup", theme: theme});
			</#if>
		};
		var handleEvents = function() {
			<#if UpdateSupplier!="false">
			grid.on("contextmenu", function () {
				return false;
			});
			grid.on("rowclick", function (event) {
				if (event.args.rightclick) {
					grid.jqxGrid("selectrow", event.args.rowindex);
					var scrollTop = $(window).scrollTop();
					var scrollLeft = $(window).scrollLeft();
					$("#contextMenu").jqxMenu("open", parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
					return false;
				}
			});
			$("body").on("click", function() {
				$("#contextMenu").jqxMenu("close");
			});
			$("#contextMenu").on("itemclick", function (event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				switch (itemId) {
				case "delete":
					if (grid) {
						var rowIndexSelected = grid.jqxGrid("getSelectedRowindex");
						var rowData = grid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							bootbox.confirm(multiLang.ConfirmDelete, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
								if (result) {
									grid.jqxGrid("deleterow", rowData.uid);
								}
							});
						}
					}
					break;
				case "update":
					if (grid) {
						var rowIndexSelected = grid.jqxGrid("getSelectedRowindex");
						var rowData = grid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							$("#jqxwindowAddSupplier").data("uid", rowData.uid.toString());
							AddSupplier.setValue(rowData);
							AddSupplier.open();
						}
					}
					break;
				default:
					break;
				}
			});
			</#if>
		};
		var getValue = function() {
			var data = grid.jqxGrid("getboundrows");
			for ( var x in data) {
				if (data[x].availableFromDate) {
					data[x].availableFromDate = data[x].availableFromDate.time?data[x].availableFromDate.time:data[x].availableFromDate;
				}
				if (data[x].availableThruDate) {
					data[x].availableThruDate = data[x].availableThruDate.time?data[x].availableThruDate.time:data[x].availableThruDate;
				}
			}
			return JSON.stringify(data);
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue
		};
	})();
</script>