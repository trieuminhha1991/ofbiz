<div id="jqxProductSearch" class="navbar-left"></div>

<#include "productItemsPopup.ftl">

<@jqOlbCoreLib hasCore=false hasComboBoxSearchRemote=true/>
<script type="text/javascript">
	$(function(){
		OlbProdItemSearch.init();
	});
	var OlbProdItemSearch = (function(){
		var productSearchCBBS;

		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			var configComboBoxSearchProduct = {
				datafields: [
			        {name: 'productId', type: 'string'},
			        {name: 'productCode', type: 'string'},
			        {name: 'productName', type: 'string'},
			        {name: 'idSKU', type: 'string'},
			        {name: 'idEAN', type: 'string'},
			        {name: 'quantityUomId', type: 'string'},
			        {name: 'salesUomId', type: 'string'},
			        {name: 'packingUomIds', type: 'array'},
			        {name: 'requireAmount', type: 'string'},
			        {name: 'amountPrice', type: 'number'},
			        {name: 'amountWeight', type: 'number'},
			    ],
			    root: "productsList",
			    url: "findProductsSales",
			    placeHolder: " ${StringUtil.wrapString(uiLabelMap.BPOSSearchProduct)} (F1)",
		        messageItemNotFound: "${StringUtil.wrapString(uiLabelMap.BSProductProductNotFound)}",
		        displayMember: "productCode",
		        valueMember: "productCode",
		        formatDataFuncItem: function(data, searchString) {
		        	data.productToSearch = searchString;
		            data.productStoreId = $("#productStoreId").val();
		            return data;
		        },
		        rendererFuncItem: function (item) {
		        	var productName = item.productName;
	            	if (productName && productName.length > 65){
	            		productName = productName.substring(0, 65);
	                	productName = productName + '...';
	            	}
	           		var productCode = item.productCode;
	            	if (productCode && productCode.length > 20){
	            		productCode = productCode.substring(0, 20);
	                	productCode = productCode + '...';
	            	}
					//var idSKU = "";
					//if (item.idSKU){
					//	idSKU = item.idSKU;
					//}
					var quantityUomId = item.salesUomId ? item.salesUomId : item.quantityUomId;
	            	var tableItem = '<div class="span12" style="width: 700px; height: 35px">'
	            	   + '<div class="span2" style="margin-left: -30px; width: 150px;">' + '[' + productCode + ']' + '</div>'
	            	   + '<div class="span6" style="width: 300px; margin-left: 10px; white-space: normal">' + item.productName + '</div>'
	            	   + '<div class="span1" style="width: 30px; margin-left: 10px">' + quantityUomId + '</div>';
	                return tableItem;
				},
				handlerSelectedItem: addItem,
			};
			productSearchCBBS = new OlbComboBoxSearchRemote($("#jqxProductSearch"), configComboBoxSearchProduct);
		};
		var initEvent = function(){

		};
		function addItem(item) {
			var productId = item.productId;
			var productCode = item.productCode;
			var quantityUomId = item.salesUomId ? item.salesUomId : item.quantityUomId;
			var quantity = 1;
			var productName = item.productName;
			var idEAN = item.idEAN;
			var packingUomIds = item.packingUomIds;
			var amountPrice = item.amountPrice;
			var amountWeight = item.amountWeight;
			var requireAmount = item.requireAmount;
			if (requireAmount == "Y") {
				if (idEAN == null || idEAN.length != 13) {
					OlbCore.alert.error("${uiLabelMap.BSPriceOfProductNotFound} " + productCode, function(){
						$("#jqxProductSearch").jqxComboBox('focus');
					});
					return false;
				}
			}

			var rowData = {
				productId: productId,
				productCode: productCode,
				productName: productName,
				quantityUomId: quantityUomId,
				quantity: quantity,
				idEAN: idEAN,
				packingUomIds: packingUomIds,
				requireAmount: requireAmount,
			};
			OlbProdItemPopup.addOrIncreaseQuantity(rowData);
		}

		return {
			init: init
		}
	}());
</script>