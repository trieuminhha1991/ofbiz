<div id="jqxProductList" class="navbar-left"></div>

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
			        {name: 'productId'},
			        {name: 'productCode'},
			        {name: 'productName'},
			        {name: 'idSKU'},
			        {name: 'currencyUomId'}
			    ],
			    type: "POST",
			    root: "productsList",
			    contentType: 'application/x-www-form-urlencoded',
			    url: "findProductsSales"
			};
			
		   	var dataAdapter = new $.jqx.dataAdapter(sourceProduct, {
		    	downloadComplete: function (data, status, xhr) {
		     		if (data.productsList.length < 2) {
		     			$("#jqxProductList").jqxComboBox({autoOpen: false});
		     		} else {
		     			$("#jqxProductList").jqxComboBox({autoOpen: true}); 
		     		}
		     		disableKeyProduct();
		        },
		    	formatData: function (data) {
		        	if ($("#jqxProductList").jqxComboBox('searchString') != undefined) {
			            data.productToSearch = $("#jqxProductList").jqxComboBox('searchString');
			            data.productStoreId = $("#productStoreId").val();
			            return data;
		            }
		        }
			});
		   	
			$("#jqxProductList").jqxComboBox({
		   		width: 208,
		    	dropDownWidth: 680,
		        placeHolder: " ${StringUtil.wrapString(uiLabelMap.BSSearchProduct)} (F1)",
		        showArrow: false,
		        height: 30,
		        source: dataAdapter,
		        remoteAutoComplete: true,
		        selectedIndex: 0,
		        displayMember: "productName",
		        valueMember: "productCode",
		        scrollBarSize: 15,
		        autoComplete: true,
		        renderer: function (index, label, value) {
		        	var item = dataAdapter.records[index];
		            if (item != null) {
		           		var productName = item.productName;
		            	if (productName.length > 65){
		            		productName = productName.substring(0, 65);
		                	productName = productName + '...';
		            	}
		           		var productCode = item.productCode;
		            	if (productCode.length > 20){
		            		productCode = productCode.substring(0, 20);
		                	productCode = productCode + '...';
		            	}
						//var idSKU = "";
						//if (item.idSKU){
						//	idSKU = item.idSKU;
						//}
		            	var tableItem = '<div class="span12" style="width: 700px; height: 35px">'
		            	   + '<div class="span2" style="margin-left: -30px; width: 150px;">' + '[' + productCode + ']' + '</div>'
		            	   + '<div class="span6" style="width: 300px; margin-left: 10px; white-space: normal">' + item.productName + '</div>'
		            	   + '<div class="span1" style="width: 30px; margin-left: 10px">' + item.quantityUomId + '</div>';
		                return tableItem;
		             }
		               return "";
				},
		        renderSelectedItem: function(index, item) {
		        	var item = dataAdapter.records[index];
		            if (item != null) {
		           		var label = item.productName;
		                return label;
		            }
		            return "";
		        },
		        search: function (searchString) {
	       	 		dataAdapter.dataBind();
		        }
			});
		};
		var initEvent = function(){
			/*$('#jqxProductList').on('close', function (event) {
				console.log(1111);
		    	var item = $("#jqxProductList").jqxComboBox('getSelectedItem'); 
		    	if(item != undefined){
		    		item = item.originalItem;
		    		if(item){
		    			addItem(item.productId, item.productCode, item.quantityUomId, 1);
		    			//addItem(item.productId, '1', 'Y',item.termUomId);
		        		$('#jqxProductList').jqxComboBox({ disabled: false }); 
		    		}
		    	} else {
		    		flagPopup = true;
		    	}
		    });*/
		    $("#jqxProductList").on('bindingComplete', function (event) {
				var items = $("#jqxProductList").jqxComboBox('getItems');
			    $("#jqxProductList").jqxComboBox({ autoOpen: false });
		        if((items)&&(items.length > 0)){
		        	if(items.length == 1){
		        		var firstItem = items[0];
		        		if(firstItem != undefined){
		        			firstItem = firstItem.originalItem;
		        			if(firstItem){
				    			addItem(firstItem.productId, firstItem.productCode, firstItem.quantityUomId, 1);
		        				//addItem(firstItem.productId, '1', 'Y',firstItem.termUomId);
		        			}
		                } 
		        	}
		        }
		        // else {
		        //$("#jqxProductList").jqxComboBox('clearSelection');
		        //}
		        disableKeyProduct();
			});
		        
		    //$("#jqxProductList").on('open', function (event) {
		    //	flagPopup = false;
		    //});
		};
		function disableKeyProduct(){
			$("#jqxProductList").on('keydown', function (event) {
		    	if(event.keyCode === 38 || event.keyCode === 40) { //up or down
		            // focus to other element
		        	var e = $.Event('keydown');
		            e.keyCode = event.keyCode; 
		            $('body').trigger(e);
		            return false;
		        }
		    	if (event.keyCode === 13){ // enter
		    		var item = $("#jqxProductList").jqxComboBox('getSelectedItem'); 
					if(item != undefined){
			    		item = item.originalItem;
			    		if(item){
			    			addItem(item.productId, item.productCode, item.quantityUomId, 1);
			    			//addItem(item.productId, '1', 'Y', item.termUomId);
			        		$('#jqxProductList').jqxComboBox({ disabled: false }); 
			    		}
			    	}
		    	}
		        if(event.keyCode === 9){
					event.preventDefault();
					return false;
		        }
		    });
		}
		function addItem(productId, productCode, quantityUomId, quantity) {
			var rowData = {
				productId: productId,
				productCode: productCode,
				quantityUomId: quantityUomId,
				quantity: quantity,
			};
			$("#${gridProductItemsId}").jqxGrid('addRow', null, rowData, "last");
			
			//flagPopup = true;
			setTimeout(function(){
				//$("#jqxProductList").jqxComboBox('close');
				//$("#jqxProductList").jqxComboBox('focus');
				productToSearchFocus();
			}, 200);
			<#--
			flagPopup = true;
			$("#jqxProductList").jqxComboBox('close');
			$("#jqxProductList").jqxComboBox('focus');
			productToSearchFocus();
			-->
		}
		function productToSearchFocus() {
			$("#jqxProductList").jqxComboBox('clearSelection');
			$("#jqxProductList").jqxComboBox('close');
			$("#jqxProductList").jqxComboBox('focus');
			return false;
		}
		<#--function addItem(productId, qnt, updCart , uomId) {
		    var param = 'add_product_id=' + productId + "&quantity=" + qnt + "&quantityUomId=" + uomId ;
		    $.ajax({url: 'AddToCart',
		        data: param,
		        type: 'post',
		        async: false,        
		        success: function(data) {
		            getResultOfAddItem(data, updCart);
		        },
		        error: function(data) {
		            getResultOfAddItem(data, updCart);
		        }
		    });
		    flagProductSearch = 1;
		}
		function getResultOfAddItem(data, updCart) {
		    var serverError = getServerError(data);
		    if (serverError != "") {
		        productToSearchFocus();
		        bootbox.alert(serverError, function() {
					flagPopup = true;
				});
		    } else {
		    	flagPopup = true;
		        if (updCart == "Y") {
		        	selectedWebPOS = data.itemId;
		            updateCartWebPOS();
		            productToSearchFocus();
		        }
		    }
		}-->
		<#--function updateCartWebPOS(){
			var data = getCartInfo();
			sourceItem.localdata = data;
			$("#jqxProductList").jqxComboBox('close');
			$("#jqxProductList").jqxComboBox('focus');
		   	$("#showCartJqxgrid").jqxGrid('updatebounddata');
		
			var listCartItems = $("#showCartJqxgrid").jqxGrid('getrows');
			if(listCartItems && listCartItems.length >0){
				updateCartItemSelected(selectedWebPOS);
			}else{
				resetSelectCartItem();
			}
			
			//update cart header
			updateCartHeader();
			focusTime = 0;	
		}
		function updateCartHeader(){
			$.ajax({url: 'ShowCartHeaderWebPOS',
		        type: 'post',
		        async: false,
		        success: function(data) {
		            getResultOfUpdateCartHeader(data);
		        },
		        error: function(data) {
		        	getResultOfUpdateCartHeader(data);
		        }
		    });
		}
		function getCartInfo(){
			var result = null;
			$.ajax({
				url : 'GetCartInfo',
				type : 'post',
				async : false,
				success : function(data) {
					result =  getResultOfCartInfo(data);
				},
				error : function(data) {
					result = [];
				}
			});
			return result;
		}
		function getResultOfCartInfo(data){
			var serverError = getServerError(data);
		    if (serverError != "") {
		    	productToSearchFocus();
		        bootbox.alert(serverError);
		        return [];
		    } else {
		        return data.listCartItems;
		    }
		}
		function updateCartItemSelected(lineIndex) {
		    if (lineIndex == null) {
		        lineIndex = 0;
		    }
		    selectedWebPOS = lineIndex;
		    var cartItem = $('#showCartJqxgrid').jqxGrid('getrowdata', selectedWebPOS);
		    if(cartItem){
				var productName = cartItem.productName;
				var productNameTmp = productName;
				if (productName.length > 40){
					productNameTmp = productName.substring(0,40);
					productNameTmp = productNameTmp + '...';
					$("#productSelectedName").jqxTooltip({ content: '<b style="font-size: 16px">' + productName + '</b>', position: 'mouse', width: 400});
				}
				var quantity = cartItem.quantityProduct;
				var unitPrice = cartItem.price;
				var discount = cartItem.discount;
				var discountPercent = cartItem.discountPercent;
				var subTotal = cartItem.subTotal;
				var quantityUomId = cartItem.quantityUomId;
				var cartLineIdx = cartItem.cartLineIndex;
				$("#productSelectedName").html(productNameTmp);
				$("#productSelectedUnitPrice").maskMoney('mask', unitPrice);
				$("#productSelectedQuantity").val(quantity);
				$("#productSelectedQuantityTmp").val(quantity);
				if(discount){
					$("#productSelectedDiscountAmount").maskMoney('mask', discount);
				}else{
					$("#productSelectedDiscountAmount").maskMoney('mask', 0.0);
				}
				
				if(discountPercent){
					$("#productSelectedDiscountPercent").val(discountPercent);
				}else{
					$("#productSelectedDiscountPercent").val(0);
				}
				$("#itemSubTotal").val(subTotal);
				$("#uomToId").val(quantityUomId);
				$("#cartLineIdx").val(cartLineIdx);
				quantity = parseFloat(quantity);
				if (quantity < 0) {
					$("#productSelectedDiscountPercent").maskMoney('mask', 0.0);
					$("#productSelectedDiscountAmount").prop('disabled', true);
					$("#productSelectedDiscountPercent").prop('disabled', true);
				} else {
					$("#productSelectedDiscountAmount").prop('disabled', false);
					$("#productSelectedDiscountPercent").prop('disabled', false);
				}
				checkReturnIsAllItem();
			}else{
				resetSelectCartItem();
			}
		}
		function resetSelectCartItem(){
			$('#productSelectedName').jqxTooltip('destroy');
			$("#productSelectedName").text(productName);
			$("#productSelectedUnitPrice").val(0);
			$("#productSelectedQuantity").val(0);
			$("#productSelectedDiscountAmount").val(0);
			$("#productSelectedDiscountPercent").val(0);
			$("#productSelectedQuantityTmp").val(0);
			$("#itemSubTotal").val("");
			$("#uomToId").val("");
			$("#cartLineIdx").val("");
			$("#productSelectedDiscountAmount").prop('disabled', false);
			$("#productSelectedDiscountPercent").prop('disabled', false);
		}
		function getResultOfUpdateCartHeader(data){
			$("#discountWholeCart").prop('disabled', false);
			$("#discountWholeCartPercent").prop('disabled', false);
			var serverError = getServerError(data);
		    if (serverError != "") {
				productToSearchFocus();
				bootbox.alert(serverError);
				resetCartHeader();
		    } else {
				var cartHeader = data.cartHeader;
				if(cartHeader){
					var transactionId = cartHeader.transactionId;
					var totalDue = cartHeader.totalDue;
					var amountDiscount = cartHeader.amountDiscount;
					var amountPercent = cartHeader.amountPercent;
					var grandTotalCart = cartHeader.grandTotalCart;
					var totalTax = cartHeader.tax;
					var currency = cartHeader.currency;
					var totalCart = cartHeader.totalDue;
					var loyaltyPoint = cartHeader.loyaltyPoint;
					$("#totalDue").val(totalDue);
					totalDue = parseFloat(totalDue);
					if(totalDue < 0){
						$("#amountCreditCard").prop('disabled', true);
					}else{
						$("#amountCreditCard").prop('disabled', false);
					}
					$("#totalTax").html(formatcurrency(totalTax, currency));
					$("#totalTaxInput").val(totalTax);
					$("#transactionId").html(transactionId);
					$("#grandTotalCart").html(formatcurrency(grandTotalCart, currency));
					$("#grandTotalCartInput").val(grandTotalCart);
					$("#discountWholeCart").maskMoney('mask', amountDiscount);
					$("#discountWholeCartPercent").val(amountPercent);
					$("#totalCart").html(formatcurrency(totalDue, currency));
					$("#grandTotalCartHidden").val(totalDue);
					$("#loyaltyPoint").html(loyaltyPoint);
				} else {
					resetCartHeader();
				}
		    }
		}
		function getServerError(data) {
		    var serverErrorHash = [];
		    var serverError = "";
		    if (data._ERROR_MESSAGE_LIST_ != undefined) {
		        serverErrorHash = data._ERROR_MESSAGE_LIST_;
		        $.each(serverErrorHash, function(i, error) {
		          if (error != undefined) {
		              if (error.message != undefined) {
		                  serverError += error.message;
		              } else {
		                  serverError += error;
		              }
		            }
		        });
		    }
		    if (data._ERROR_MESSAGE_ != undefined) {
		        serverError = data._ERROR_MESSAGE_;
		    }
		    return serverError;
		}-->
		
		return {
			init: init
		}
	}());
</script>