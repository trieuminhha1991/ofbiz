<div id="jqxProductSearch" class="navbar-left"></div>

<#include "productItemsPopup.ftl">

<script type="text/javascript">
	$(function(){
		OlbProdItemSearch.init();
	});
	var OlbProdItemSearch = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			var sourceProduct = {
			    datatype: "json",
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
			    type: "POST",
			    root: "productsList",
			    contentType: 'application/x-www-form-urlencoded',
			    url: "findProductsSales"
			};
			
		   	var dataAdapter = new $.jqx.dataAdapter(sourceProduct, {
		    	downloadComplete: function (data, status, xhr) {
		     		if (OlbCore.isNotEmpty(data.productsList) && data.productsList.length < 2) {
		     			$("#jqxProductSearch").jqxComboBox({autoOpen: false});
		     		} else {
		     			$("#jqxProductSearch").jqxComboBox({autoOpen: true}); 
		     		}
		     		disableKeyProduct();
		        },
		    	formatData: function (data) {
		        	if ($("#jqxProductSearch").jqxComboBox('searchString') != undefined) {
			            data.productToSearch = $("#jqxProductSearch").jqxComboBox('searchString');
			            data.productStoreId = $("#productStoreId").val();
			            return data;
		            }
		        }
			});
			$("#jqxProductSearch").keypress(function(e) {
			    if (e.which == 13) {
			    	dataAdapter.dataBind();
			    }
			});
		   	
			$("#jqxProductSearch").jqxComboBox({
		   		width: 208,
		    	dropDownWidth: 680,
		        placeHolder: " ${StringUtil.wrapString(uiLabelMap.BPOSSearchProduct)} (F1)",
		        showArrow: false,
		        height: 30,
		        source: dataAdapter,
		        remoteAutoComplete: true,
		        selectedIndex: 0,
		        displayMember: "productCode",
		        valueMember: "productCode",
		        scrollBarSize: 15,
		        autoComplete: true,
		        renderer: function (index, label, value) {
		        	var item = dataAdapter.records[index];
		            if (item != null) {
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
		             }
		               return "";
				},
		        renderSelectedItem: function(index, item) {
		        	var item = dataAdapter.records[index];
		            if (item != null) {
		           		var label = item.productCode;
		                return label;
		            }
		            return "";
		        },
		        search: function (searchString) {
	       	 		//dataAdapter.dataBind();
		        }
			});
		};
		var initEvent = function(){
		    $('body').keydown(function(e) {
				//112 F1
				$(window).keydown(function(e){
					var code = (e.keyCode ? e.keyCode : e.which);
					if(code == 112){e.preventDefault();}
				});
				var code = (e.keyCode ? e.keyCode : e.which);
				if (code == 112) {
					//if (flagPopup){
					productToSearchFocus();
					//}
					e.preventDefault();
					return false;
				}
			});
		    $("#jqxProductSearch").on('bindingComplete', function (event) {
				var items = $("#jqxProductSearch").jqxComboBox('getItems');
			    $("#jqxProductSearch").jqxComboBox({ autoOpen: false });
		        if (items && items.length == 1){
	        		var firstItem = items[0];
	        		if (firstItem) {
	        			firstItem = firstItem.originalItem;
	        			if (firstItem) {
			    			addItem(firstItem);
	        			}
	                }
		        }
		        disableKeyProduct();
			});
		};
		function disableKeyProduct(){
			$("#jqxProductSearch").on('keydown', function (event) {
		    	if(event.keyCode === 38 || event.keyCode === 40) { //up or down
		            // focus to other element
		        	var e = $.Event('keydown');
		            e.keyCode = event.keyCode; 
		            $('body').trigger(e);
		            return false;
		        }
		    	if (event.keyCode === 13){ // enter
		    		var item = $("#jqxProductSearch").jqxComboBox('getSelectedItem'); 
					if(item != undefined){
			    		item = item.originalItem;
			    		if(item){
			    			addItem(item);
			        		$('#jqxProductSearch').jqxComboBox({ disabled: false }); 
			    		}
			    	}
		    	}
		        if(event.keyCode === 9){
					event.preventDefault();
					return false;
		        }
		    });
		}
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
					productToSearchFocus();
					OlbCore.alert.error("${uiLabelMap.BSPriceOfProductNotFound} " + productCode, function(){
						$("#jqxProductSearch").jqxComboBox('focus');
					});
					return false;
				}
			}
			setTimeout(function(){
				productToSearchFocus();
			}, 300);
			
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
		function productToSearchFocus() {
			$("#jqxProductSearch").jqxComboBox('clearSelection');
			$("#jqxProductSearch").jqxComboBox('close');
			$("#jqxProductSearch").jqxComboBox('focus');
			return false;
		}
		
		return {
			init: init
		}
	}());
</script>