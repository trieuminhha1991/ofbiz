<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" id="olbiusFacility">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
			service: "facilityInventory",
			button: true,
			id: "olbiusFacility",
			url: "olapWarehouseReport",
			sortable: true,
//			filterable: true,
//			showfilterrow: true,
			columns: [
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.Facility)}",
					datafield: {name: "facility_name", type: "string"},
					width: 150
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.ProductId)}",
					datafield: {name: "product_code", type: "string"},
					width: 150
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.ProductName)}",
					datafield: {name: "product_name", type: "string"},
					width: 200
				},
				{ text: "${inventoryType}"=="EXPORT"?"${StringUtil.wrapString(uiLabelMap.ActualExportedDate)}":"${StringUtil.wrapString(uiLabelMap.ReceivedDate)}",
					datafield: {name: "inventory_date", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div class=\"text-right\">" + value + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.Quantity)}",
					datafield: {name: "quantity_on_hand_total", type: "string"}, width: 120,
					cellsrenderer: function (row, column, value) {
						return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.Unit)}",
					datafield: {name: "quantity_uom_id", type: "string"},
					width: 80
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}",
					datafield: {name: "manufactured_date", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div class=\"text-right\">" + value + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.ExpireDate)}",
					datafield: {name: "expire_date", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div class=\"text-right\">" + value + "</div>";
					}
				},
				{ text: "${StringUtil.wrapString(uiLabelMap.BLCategoryProduct)}",
					datafield: {name: "category_name", type: "string"},
					width: 200
				}
			],
			popup: [
			<#if facilities?? && facilities?index_of(",", 0) != -1>
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'facility',
						label: '${StringUtil.wrapString(uiLabelMap.FacilityLabel)}',
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
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityId', width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
							]
						}
					}
				},</#if>
				<#if categories?if_exists?index_of(",", 0) != -1>
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'categories',  
						label : '${StringUtil.wrapString(uiLabelMap.BSCategory)}',
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
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}", datafield: 'productCategoryId', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}", datafield: 'categoryName' }
								]
						}
					}
				},</#if>
				<#if products?if_exists?index_of(",", 0) != -1>
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'product',  
						label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
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
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductId)}", datafield: 'productCode', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}", datafield: 'productName' }
							]
						}
					}
				},</#if>
				{
					group: "dateTime",
					id: "dateTime",
					params: { index: 2 }
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facility: popup.val("facility"),
					product: popup.val('product'),
					categories: popup.val("categories"),
					inventoryType: "${inventoryType}"
				}, popup.group("dateTime").val());
			},
			excel: function(oLap) {
				if (!_.isEmpty(oLap._data)) {
					data = oLap._data;
					var url = "exportWarehouseReportExcel?" + "dateType=" + data.dateType + "&fromDate=" + new Date(data.fromDate).getTime() + "&thruDate=" + new Date(data.thruDate).getTime();
					if (data.facility) {
						url += "&facility=" + data.facility;
					}
					if (data.categories) {
						url += "&categories=" + data.categories;
					}
					if (data.product) {
						url += "&product=" + data.product;
					}
					url += "&inventoryType=" + data.inventoryType;
					location.href = url;
				}
			}
		};

		var grid = OlbiusUtil.grid(config);

	});
</script>
