<div id="olbiusFacility"></div>
<script type="text/javascript">
	$(function () {
		var config = {
			title: "${reportType}"=="DISTRIBUTOR"?'${StringUtil.wrapString(uiLabelMap.ReportFacilityDistributor)}':'${StringUtil.wrapString(uiLabelMap.ReportFacility)}',
			service: 'facilityInventory',
			button: true,
			id: 'olbiusFacility',
			olap: 'olapInventoryReport',
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns: [
				{ 
					text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.TimeLabel)}',
					datafield: {name: 'dateTime', type: 'string'},
					width: 100,
					filterable: false
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.Facility)}',
					datafield: {name: 'facility', type: 'string'}
				<#--}, {-->
				<#--text: '${StringUtil.wrapString(uiLabelMap.DistributorLabel)}',-->
				<#--datafield: {name: 'party', type: 'string'}-->
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.ProductName)}',
					datafield: {name: 'product', type: 'string'}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.olap_uom)}',
					datafield: {name: 'uom', type: 'string'},
					width: 100
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.InventoryBeforeLabel)}',
					datafield: {name: 'inventoryP', type: 'string'}, width: 100,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.ReceiveLabel)}',
					datafield: {name: 'receive', type: 'string'}, width: 100,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.ExportLabel)}',
					datafield: {name: 'export', type: 'string'}, width: 100,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.InventoryAfterLabel)}',
					datafield: {name: 'inventory', type: 'string'}, width: 100,
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
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
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityId', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
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
							columns:
							[
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductId)}", datafield: 'productCode', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductProductName)}", datafield: 'productName' }
							]
						}
					}
				},</#if>
				{
					group: 'dateTime',
					id: 'dateTime',
					params: { index: 2 }
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facility: _.isEmpty(popup.val("facility"))?${StringUtil.wrapString(originalFacilities)}:popup.val("facility"),
					group: ['facility', 'product', 'uom'],
					product: popup.val('product')
				}, popup.group('dateTime').val());
			},
			excel: true
		};

		var grid = OlbiusUtil.grid(config);

	});
</script>
