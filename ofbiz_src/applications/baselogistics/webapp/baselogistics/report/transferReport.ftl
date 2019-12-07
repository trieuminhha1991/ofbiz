<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" id="olbiusFacility">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
			service: "transferItem",
			id: "olbiusFacility",
			button: true,
			url: "olapTransferReport",
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns: [
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.TransferId)}",
					datafield: {name: "transfer_id", type: "string"},
					width: 120
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.Status)}",
					datafield: {name: "status_transfer_id", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function(row, colum, value){
						value?value=mapTransferStatusItem[value]:value;
						return '<span>' + value + '</span>';
					}
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.FacilityFrom)}",
					datafield: {name: "origin_facility", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.FacilityTo)}",
					datafield: {name: "dest_facility", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.TransferType)}",
					datafield: {name: "transfer_type_id", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function(row, colum, value){
						value?value=mapTransferType[value]:value;
						return '<span>' + value + '</span>';
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductCode)}",
					datafield: {name: "product_code", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductName)}",
					datafield: {name: "product_name", type: "string"},
					width: 200
				}, 
				{
					text: "${StringUtil.wrapString(uiLabelMap.RequiredNumber)}",
					datafield: {name: "quantity", type: "string"}, width: 150,
					cellsrenderer: function (row, column, value) {
						return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ActualExportedQuantity)}",
					datafield: {name: "actual_exported_quantity", type: "string"}, width: 150,
					cellsrenderer: function (row, column, value) {
						return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ActualDeliveredQuantity)}",
					datafield: {name: "actual_delivered_quantity", type: "string"}, width: 150,
					cellsrenderer: function (row, column, value) {
						return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.Unit)}",
					datafield: {name: "quantity_uom", type: "string"},
					width: 80
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}",
					datafield: {name: "category_name", type: "string"},
					width: 200
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.Batch)}",
					datafield: {name: "lot_id", type: "string"},
					width: 100
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}",
					datafield: {name: "datetime_manufactured", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div>" + value + "</div>";
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ExpireDate)}",
					datafield: {name: "expire_date", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div>" + value + "</div>";
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.DeliveryTransferId)}",
					datafield: {name: "delivery_id", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.StatusDelivery)}",
					datafield: {name: "delivery_status_id", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function(row, colum, value){
						value?value=mapDeliveryStatusItem[value]:value;
						return '<span>' + value + '</span>';
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.TransferDate)}",
					datafield: {name: "transfer_date", type: "string"}, filterable: false, width: 150,
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div>" + value + "</div>";
					}
				}
			],
			popup: [
			<#if facilities?? && facilities?index_of(",", 0) != -1>
				{
					action: "jqxGridMultiple",
					params: {
						id: "origin_facility",
						label: "${StringUtil.wrapString(uiLabelMap.FacilityFrom)}",
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
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityId', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
							]
						}
					}
				}, {
					action: "jqxGridMultiple",
					params: {
						id: "dest_facility",
						label: "${StringUtil.wrapString(uiLabelMap.FacilityTo)}",
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
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityId', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
							]
						}
					}
				},</#if>
				{
					action: "jqxGridMultiple",
					params: {
						id: "status_transfer_id",
						label: "${StringUtil.wrapString(uiLabelMap.BSStatus)}",
						grid: {
							source: ${StringUtil.wrapString(transferStatusItems)},
							id: "value",
							width: 550,
							sortable: true,
							pagesize: 5,
							columnsresize: true,
							pageable: true,
							altrows: true,
							showfilterrow: true,
							filterable: true,
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}", datafield: 'value', width: 200 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}", datafield: 'text' }
							]
						}
					}
				},
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
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}", datafield: 'productCategoryId', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}", datafield: 'categoryName' }
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
					origin_facility: popup.val("origin_facility"),
					dest_facility: popup.val("dest_facility"),
					status_transfer_id: popup.val("status_transfer_id"),
					categories: popup.val("categories"),
				}, popup.group("dateTime").val());
			},
			excel: function(oLap) {
				if (!_.isEmpty(oLap._data)) {
					data = oLap._data;
					var url = "exportTransferOlapExcel?" + "dateType=" + data.dateType + "&fromDate=" + new Date(data.fromDate).getTime() + "&thruDate=" + new Date(data.thruDate).getTime();
					if (data.origin_facility) {
						url += "&origin_facility=" + data.origin_facility;
					}
					if (data.dest_facility) {
						url += "&dest_facility=" + data.dest_facility;
					}
					if (data.status_transfer_id) {
						url += "&status_transfer_id=" + data.status_transfer_id;
					}
					if (data.categories) {
						url += "&categories=" + data.categories;
					}
					if (data.product) {
						url += "&product=" + data.product;
					}
					location.href = url;
				}
			}
		};

		var grid = OlbiusUtil.grid(config);

	});
	var transferTypes = ${StringUtil.wrapString(transferTypes)};
	var mapTransferType = ${StringUtil.wrapString(mapTransferType)};
	var transferStatusItems = ${StringUtil.wrapString(transferStatusItems)};
	var mapTransferStatusItem = ${StringUtil.wrapString(mapTransferStatusItem)};
	var deliveryStatusItems = ${StringUtil.wrapString(deliveryStatusItems)};
	var mapDeliveryStatusItem = ${StringUtil.wrapString(mapDeliveryStatusItem)};
</script>