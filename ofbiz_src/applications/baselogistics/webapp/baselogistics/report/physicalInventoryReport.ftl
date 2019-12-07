<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>

<script>

<#assign productCategorys = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, Static["org.ofbiz.base.util.UtilMisc"].toList("RECYCLE_CATEGORY", "CATALOG_CATEGORY")), null, null, null, false)>
var categoryData = [
	<#if productCategorys?exists>
		<#list productCategorys as item>
			{
				categoryId: "${item.productCategoryId?if_exists}",
				categoryName: "${StringUtil.wrapString(item.get("categoryName", locale)?if_exists)}"
			},
		</#list>
	</#if>
];

</script>

<script type="text/javascript" id="physicalInventory">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
			service: "facilityInventory",
			button: true,
			id: "physicalInventory",
			url: "olapPhysicalInventoryReport",
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns:
			[
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.Facility)}",
					datafield: {name: "facility_name", type: "string"},
					width: 200
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductId)}",
					datafield: {name: "product_code", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductName)}",
					datafield: {name: "product_name", type: "string"},
					width: 200
				},{
					text: "${StringUtil.wrapString(uiLabelMap.Quantity)}",
					datafield: {name: "quantity_on_hand_total", type: "string"}, width: 100,
					cellsrenderer: function (row, column, value) {
						return "<div class=\"text-right\">" + formatnumber(value) + "</div>";
					}
				}, 
				 {
					text: "${StringUtil.wrapString(uiLabelMap.Unit)}",
					datafield: {name: "quantity_uom_id", type: "string"},
					width: 100
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.Reason)}",
					datafield: {name: "variance_reason_id", type: "string"},
					width: 200
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.PhysicalInventoryDate)}",
					datafield: {name: "inventory_date", type: "string"}, filterable: true, width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellsalign: 'right',
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div style=\"text-align: right\">" + value + "</div>";
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}",
					datafield: {name: "manufactured_date", type: "string"}, filterable: true, width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellsalign: 'right',
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div style=\"text-align: right\">" + value + "</div>";
					}
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ExpireDate)}",
					datafield: {name: "expire_date", type: "string"}, filterable: true, width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy HH:mm:ss', cellsalign: 'right',
					cellsrenderer: function (row, column, value) {
						if (value) {
							value = new Date(value).toTimeOlbius();
						}
						return "<div style=\"text-align: right\">" + value + "</div>";
					}
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.ProductCategory)}",
					datafield: {name: "category_id", type: "string"},
					width: 200
				},
			],
			popup: [
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'facility',
						label: '${StringUtil.wrapString(uiLabelMap.Facility)}',
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
				},
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'reason',  
						label : '${StringUtil.wrapString(uiLabelMap.Reason)}',
						grid: {
							source: ${StringUtil.wrapString(reasons)},
							id: "varianceReasonId",
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
							 	{ text: "${StringUtil.wrapString(uiLabelMap.ReasonId)}", datafield: 'varianceReasonId', width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.Description)}", datafield: 'description', minwidth: 150 },
							]
						}
					}
				},
				{
					action: 'jqxGridMultiple',
					params: {
						id : 'category',  
						label : '${StringUtil.wrapString(uiLabelMap.ProductCategory)}',
						grid: {
							source: categoryData,
							id: "categoryId",
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
								{ text: "${StringUtil.wrapString(uiLabelMap.CategoryId)}", datafield: 'categoryId', width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.CategoryName)}", datafield: 'categoryName' }
							]
						}
					}
				},
				{
					group: "dateTime",
					id: "dateTime"
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facility: popup.val("facility"),
					reason: popup.val("reason"),
					category: popup.val("category"),
				}, popup.group("dateTime").val());
			},
			excel: true,
            exportFileName: '[LOGISTICS]_PHYSICAL_INVENTORY_REPORT_' + (new Date()).formatDate("ddMMyyyy")
		};

		var grid = OlbiusUtil.grid(config);

	});
</script>
