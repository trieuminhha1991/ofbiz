<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>

<script>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	<#assign facilityParty = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "MANAGER", "partyId", userLogin.partyId)), null, null, null, false)>
	<#assign facilityParty = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(facilityParty)>
	<#assign listFacilityIds = []>
	<#if facilityParty?has_content>
		<#list facilityParty as facPr>
			<#assign listFacilityIds = listFacilityIds + [facPr.facilityId?if_exists]>
		</#list>
	</#if>
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listFacilityIds))), null, null, null, false)>
	var facilityData = [];
	<#list facilities as item>
		var row = {};
		<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['ownerPartyId']= "${item.ownerPartyId?if_exists}";
		row['description'] = "${descFac?if_exists?replace('\n', ' ')}";
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		facilityData.push(row);
	</#list>
	
	<#assign products = delegator.findList("Product", null, null, null, null, false)>
	var productData = [];
	<#list products as product>
		var row = {};
		row['productName'] = "${product.productName?if_exists}";
		row['productCode'] = "${product.productCode?if_exists}";
		row['productId'] = "${product.productId?if_exists}";
		productData.push(row);
	</#list>
	
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

<script type="text/javascript" id="cancelInventory">
	$(function () {
		var config = {
			title: "${StringUtil.wrapString(uiLabelMap.get(titleProperty))}",
			service: "facilityInventory",
            button: true,
            id: "cancelInventory",
            olap: "olapExportCancelInventory",
            sortable: true,
            filterable: true,
            showfilterrow: true,
			columns:
			[
				{
				    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.Facility)}",
					datafield: {name: "facility_name", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductId)}",
					datafield: {name: "product_code", type: "string"},
					width: 150
				}, {
					text: "${StringUtil.wrapString(uiLabelMap.ProductName)}",
					datafield: {name: "product_name", type: "string"},
					width: 200
				}, {
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
					text: "${StringUtil.wrapString(uiLabelMap.ExportDate)}",
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
					text: "${StringUtil.wrapString(uiLabelMap.Batch)}",
					datafield: {name: "lot_id", type: "string"},
					width: 100
				},
				{
					text: "${StringUtil.wrapString(uiLabelMap.Category)}",
					datafield: {name: "category_id", type: "string"},
					width: 200
				}
			],
			popup: [
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'facility',
						label: '${StringUtil.wrapString(uiLabelMap.Facility)}',
						grid: {
							source: facilityData,
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
						id : 'product',  
						label : '${StringUtil.wrapString(uiLabelMap.Product)}',
						grid: {
							source: productData,
							id: "productId",
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
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductId)}", datafield: 'productCode', width: 150 },
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductName)}", datafield: 'productName' }
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
					product: popup.val('product'),
					category: popup.val('category'),
				}, popup.group("dateTime").val());
			},
			excel: true,
            exportFileName: '[LOGISTICS]_EXPORT_CANCEL_REPORT_' + (new Date()).formatDate("ddMMyyyy")
        
		};

		var grid = OlbiusUtil.grid(config);

	});
</script>
