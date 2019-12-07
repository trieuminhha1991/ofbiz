<@jqOlbCoreLib />
<script>
	<#assign products = delegator.findList("Product", null, null, null, null, false) />
	var productData = [];
	<#list products as product>
		var row = {};
		row['productName'] = "${product.productName?if_exists}";
		row['productCode'] = "${product.productCode?if_exists}";
		row['productId'] = "${product.productId?if_exists}";
		productData.push(row);
	</#list>
</script>
<div id="imexStock"></div>
<script type="text/javascript">
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BACCLogReportImpExpStockWarehouse)}',
			button: true,
			service: "facilityInventory",
			id: 'imexStock',
			olap: 'olbiusReportImpExpStockWarehouse',  
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
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductId)}',
					datafield: {name: 'productCode', type: 'string'}, width: '11%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCProductName)}',
					datafield: {name: 'productName', type: 'string'}, width: '16%'
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}',
					datafield: {name: 'facilityId', type: 'string'}, width: '11%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.LogFacilityName)}',
					datafield: {name: 'facilityName', type: 'string'}, width: '16%'
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_rateCurrencyUomId)}',
					datafield: {name: 'quantityUomId', type: 'string'}, width: '9%', filterable: true
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.AccountingQuantity)}',
					datafield: {name: 'openingQuantity', type: 'number'}, width: '9%', filtertype: 'number', 
					columngroup: 'openingStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}',
					datafield: {name: 'openingAmount', type: 'number'}, width: '13%', filtertype: 'number', 
					columngroup: 'openingStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatcurrency(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatcurrency(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.AccountingQuantity)}',
					datafield: {name: 'importQuantity', type: 'number'}, width: '9%', filtertype: 'number', 
					columngroup: 'importStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}',
					datafield: {name: 'importAmount', type: 'number'}, width: '13%', filtertype: 'number', 
					columngroup: 'importStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatcurrency(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatcurrency(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.AccountingQuantity)}',
					datafield: {name: 'exportQuantity', type: 'number'}, width: '9%', filtertype: 'number', 
					columngroup: 'exportStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}',
					datafield: {name: 'exportAmount', type: 'number'}, width: '13%', filtertype: 'number', 
					columngroup: 'exportStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatcurrency(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatcurrency(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.AccountingQuantity)}',
					datafield: {name: 'endingQuantity', type: 'number'}, width: '9%', filtertype: 'number', 
					columngroup: 'endingStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
						}
					}
				}, {
					text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}',
					datafield: {name: 'endingAmount', type: 'number'}, width: '13%', filtertype: 'number', 
					columngroup: 'endingStock', 
					cellsrenderer: function (row, column, value) {
						if (value){
							return '<div class=\"text-right\">' + formatcurrency(value) + '</div>';
						} else {
							return '<div class=\"text-right\">' + formatcurrency(0) + '</div>';
						}
					}
				}
			],
			columngroups: [
               { text: '${uiLabelMap.BACCOpeningStock}', align: 'center', name: 'openingStock' },
               { text: '${uiLabelMap.BACCImportStock}', align: 'center', name: 'importStock' },
               { text: '${uiLabelMap.BACCExportStock}', align: 'center', name: 'exportStock' },
               { text: '${uiLabelMap.BACCEndingStock}', align: 'center', name: 'endingStock' }
            ],
			popup: [
				{
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                    	index: 0
                    }
                },
                {
	                action: 'jqxGridElement',
	                params: {
	                    id: 'facility',
	                    label: "${StringUtil.wrapString(uiLabelMap.Facility)}",
	                    grid: {
	    	            	url: 'JQGetListFacilities&organizationPartyId=' + '${userLogin.lastOrg}',
	    	            	id: 'facilityId',
	    	            	width: 550,
	    	            	sortable: true,
	    	                pagesize: 5,
	    	                columnsresize: true,
	    	                pageable: true,
	    	                altrows: true,
	    	                showfilterrow: true,
	    	                filterable: true,
	    	                datafields: [{name: 'facilityId', type: 'string'},{name: 'facilityName', type: 'string'}],
	    	                displayField: 'facilityName',
	    	                displayAdditionField: 'facilityId',
	    	                gridTitle: '${StringUtil.wrapString(uiLabelMap.Facility)}',
	    	                clearSelectionBtnText: '${StringUtil.wrapString(uiLabelMap.BACCClearSelection)}',
	    	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityId', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
	            	        ]
	    	            }
	    	    	}
                },
				{
					action: 'jqxGridMultiple',
					params: {
						id: 'product',
						label: '${StringUtil.wrapString(uiLabelMap.BACCProduct)}',
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
								{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductId)}", datafield: 'productCode', width: 150 }, 
			          			{ text: "${StringUtil.wrapString(uiLabelMap.BACCProductName)}", datafield: 'productName'}
							]
						}
					}
				}
			],
			apply: function (grid, popup) {
				return $.extend({
					facilityId: popup.val('facility'),
					product: popup.val('product')
				}, popup.group("dateTime").val());
			},
			excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}

				window.location.href = "exportReportImpExpStockWarehouseExcel?" + otherParam;
            },
			exportFileName: 'BAO_CAO_XUAT_NHAP_TON_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>