<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" id="olbiusPOReturn">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.VendorReturnReport)}",
			service: "returnItem",
			button: true,
			id: "olbiusPOReturn",
			olap: "olapPOReturnReport",
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
				{ text: "${StringUtil.wrapString(uiLabelMap.DAEntryDateReturn)}",
					datafield: {name: "return_date", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div class=\"text-right\">" + value + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.POReturnId)}",
					datafield: {name: "return_id", type: "string"}, width: 150,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div><a target=\"_blank\" href=\"viewGeneralReturnSupplier?returnId=" + value + "\">" + value + "</a></div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.OrderPO)}",
					datafield: {name: "order_id", type: "string"}, width: 150,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div><a target=\"_blank\" href=\"viewDetailPO?orderId=" + value + "\">" + value + "</a></div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.DAStatus)}",
					datafield: {name: "return_status", type: "string"},
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
				{ text: "${StringUtil.wrapString(uiLabelMap.POQuantityReturned)}",
					datafield: {name: "received_quantity", type: "number"}, filtertype: "number", width: 150,
					cellsrenderer: function (row, column, value) {
						return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.Unit)}",
					datafield: {name: "quantity_uom", type: "string"},
					width: 120
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.PORejectReasonReturnProduct)}",
					datafield: {name: "return_reason", type: "string"},
					width: 200
				},
				{ text: "facility_id", hidden: true, datafield: {name: "facility_id", type: "string"} },
				{ text: "${StringUtil.wrapString(uiLabelMap.DAFromFacility)}",
					datafield: {name: "facility_name", type: "string"},
					width: 200
				},
				{ text: "to_party_id", hidden: true, datafield: {name: "to_party_id", type: "string"} },
				{ text: "${StringUtil.wrapString(uiLabelMap.BPOPartyReceive)}",
					datafield: {name: "to_party_name", type: "string"}, width: 200,
					cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
						return "<div><a target=\"_blank\" href=\"viewSupplier?partyId=" + rowdata.to_party_id + "\">" + value + "</a></div>";
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
				{
					group: "dateTime",
					id: "dateTime"
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facility: popup.val("facility"),
					categories: popup.val("categories")
				}, popup.group("dateTime").val());
			},
			excel: true,
			exportFileName: "${StringUtil.wrapString(uiLabelMap.VendorReturnReport)}"
		};

		var grid = OlbiusUtil.grid(config);

	});
</script>
