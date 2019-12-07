<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" id="olbiusPOImported">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
			service: "purchaseOrder",
			button: true,
			id: "olbiusPOImported",
			olap: "olapPOImportedReport",
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns: [
						{ text: "${uiLabelMap.SequenceId}", sortable: false, filterable: false, editable: false, pinned: true,
							groupable: false, draggable: false, resizable: false,
							datafield: "", columntype: "number", width: 50,
							cellsrenderer: function (row, column, value) {
								return "<div style=margin:4px;>" + (value + 1) + "</div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.POReceivedDate)}",
							datafield: {name: "actual_arrival_date", type: "string"}, filterable: false, width: 150,
							cellsrenderer: function (row, column, value) {
								if (value) {
									value = new Date(value).toTimeOlbius();
								}
								return "<div class=\"text-right\">" + value + "</div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.POOrderId)}",
							datafield: {name: "order_id", type: "string"}, width: 150,
							cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
								return "<div><a target=\"_blank\" href=\"viewDetailPO?orderId=" + value + "\">" + value + "</a></div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.DAStatus)}",
							datafield: {name: "order_status", type: "string"},
							width: 150
						},
						{ text: "product_id", hidden: true, datafield: {name: "product_id", type: "string"} },
						{ text: "${StringUtil.wrapString(uiLabelMap.ProductId)}",
							datafield: {name: "product_code", type: "string"}, width: 150,
							cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
								return "<div><a target=\"_blank\" href=\"viewProduct?productId=" + rowdata.product_id + "\">" + value + "</a></div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.ProductName)}",
							datafield: {name: "product_name", type: "string"},
							width: 200
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.POProductCatalogs)}",
							datafield: {name: "category_name", type: "string"},
							width: 200
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.Quantity)}",
							datafield: {name: "quantity", type: "number"}, filtertype: "number", width: 150,
							cellsrenderer: function (row, column, value) {
								return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.POActualExportedQuantity)}",
							datafield: {name: "actual_imported_quantity", type: "number"}, filtertype: "number", width: 150,
							cellsrenderer: function (row, column, value) {
								return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.PONumberDifferences)}",
							datafield: {name: "gap", type: "number"}, filtertype: "number", width: 150,
							cellsrenderer: function (row, column, value) {
								return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.BSProdPackingUom)}",
							datafield: {name: "quantity_uom", type: "string"},
							width: 120
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.POFacility)}",
							datafield: {name: "facility_name", type: "string"},
							width: 200
						},
						{ text: "party_from_id", hidden: true, datafield: {name: "party_from_id", type: "string"} },
						{ text: "${StringUtil.wrapString(uiLabelMap.POSupplier)}",
							datafield: {name: "party_from_name", type: "string"}, width: 200,
							cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
								return "<div><a target=\"_blank\" href=\"viewSupplier?partyId=" + rowdata.party_from_id + "\">" + value + "</a></div>";
							}
						}
					],
			popup: [
			<#if facilities?? && facilities?index_of(",", 0) != -1>
				{
					action: "jqxGridMultiple",
					params: {
						id: "facility",
						label: "${StringUtil.wrapString(uiLabelMap.DAFromFacility)}",
						grid: {
							source: ${StringUtil.wrapString(facilities)},
							id: "facilityId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: "facilityId", width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: "facilityName" }
							]
						}
					}
				},</#if>
				<#if categories?if_exists?index_of(",", 0) != -1>
				{
					action: "jqxGridMultiple",
					params: {
						id : "categories",  
						label : "${StringUtil.wrapString(uiLabelMap.BSCategory)}",
						grid: {
							source: ${StringUtil.wrapString(categories)},
							id: "productCategoryId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}", datafield: "productCategoryId", width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}", datafield: "categoryName" }
							]
						}
					}
				},</#if>
				<#if products?if_exists?index_of(",", 0) != -1>
				{
					action: "jqxGridMultiple",
					params: {
						id : "product",  
						label : "${StringUtil.wrapString(uiLabelMap.POProduct)}",
						grid: {
							source: ${StringUtil.wrapString(products)},
							id: "productId",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns: [
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductId)}", datafield: "productCode", width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}", datafield: "productName" }
							]
						}
					}
				},</#if>
				{
					group: "dateTime",
					id: "dateTime"
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facility: popup.val("facility"),
					product: popup.val("product"),
					categories: popup.val("categories")
				}, popup.group("dateTime").val());
			},
			excel: true,
			exportFileName: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}"
		};

		var grid = OlbiusUtil.grid(config);

	});
</script>
