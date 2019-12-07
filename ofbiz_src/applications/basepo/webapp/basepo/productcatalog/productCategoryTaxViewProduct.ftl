
<div id="jqxgridProducts"></div>

<@jqGridMinimumLib />
<@jqOlbCoreLib hasGrid=true/>
<script type="text/javascript">
	$(function(){
		OlbProdCategoryViewProduct.init();
	});
	
	var OlbProdCategoryViewProduct = (function(){
		var productCategoryMemberGRID;
		
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configCategory = {
				width: "100%",
				autoshowloadelement: true,
				showdefaultloadelement: true,
				datafields: [
					{name: "productId", type: "string"},
					{name: "productCode", type: "string"},
					{name: "mainCategoryId", type: "string"},
					{name: "primaryProductCategoryId", type: "string"},
					{name: "productName", type: "string"},
					{name: "productCategoryId", type: "string"},
					{name: "fromDate", type: "date", other: "Timestamp"},
					{name: "thruDate", type: "date", other: "Timestamp"},
					{name: "sequenceNum", type: "number"},
				],
				columns: [
	                {text: "${uiLabelMap.DmsProductId}", datafield: "productCode", width: 150},
					{text: "${uiLabelMap.BSProductName}", datafield: "productName", minwidth: 250},
					{text: "${uiLabelMap.DmsSequenceId}", datafield: "sequenceNum", width: 150, columntype: "numberinput",
						validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: uiLabelMap.DmsQuantityNotValid };
							}
							return true;
						}
					},
				],
				useUrl: true,
				useUtilFunc: false,
				url: 'jqxGeneralServicer?sname=JQGetListProductInCategoryInclude&productCategoryId=${productCategoryId}',
            	showfilterrow: true,
		        filterable: true,
		        editable: false,
            	sortable: true,
            	pageable: true,
		        enabletooltips: true,
		        showtoolbar: true,
				rendertoolbarconfig: {
					titleProperty: "${uiLabelMap[titleProperty]}: ${productCategory.categoryName} [${productCategory.productCategoryId}]",
				},
			};
			productCategoryMemberGRID = new OlbGrid($("#jqxgridProducts"), null, configCategory, []);
		};
		return {
			init: init,
		};
	}());
</script>