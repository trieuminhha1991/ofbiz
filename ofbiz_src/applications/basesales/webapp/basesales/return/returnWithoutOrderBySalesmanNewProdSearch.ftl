<style type="text/css">
	#popupQuotItemsProdSearchAdd .form-window-content-custom label {
		font-size: 14px;
	}
	#popupQuotItemsProdSearchAdd .form-window-content-custom span {
		color: #468847;
		font-weight: bold;
		font-size: 14px;
	}
	#popupQuotItemsProdSearchAdd .form-window-content-custom .legend-container {
		text-align: left
	}
	#popupQuotItemsProdSearchAdd .form-window-content-custom .legend-container span {
		font-weight:normal; 
		color: #222222;
		font-style: italic
	}
</style>

<div id="popupQuotItemsProdSearchAdd" style="display:none">
	<div>${uiLabelMap.BSEnterPrice}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid margin-bottom0'>
						<div class='span3'><label>${uiLabelMap.BSProductName}:</label></div>
						<div class='span9'><span id="wn_prodsearch_productName"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label>${uiLabelMap.BSProductId}:</label></div>
						<div class='span3'><span id="wn_prodsearch_productCode"></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSProductPackingUomId}:</label></div>
						<div class='span3'><span id="wn_prodsearch_quantityUomId"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label>${uiLabelMap.BSCurrencyUomId}:</label></div>
						<div class='span3'><span id="wn_prodsearch_currencyUomId"></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSCurrentPrice} (T):</label></div>
						<div class='span3'><span id="wn_prodsearch_unitPriceBef"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label>${uiLabelMap.BSTax}:</label></div>
						<div class='span3'><span id="wn_prodsearch_taxPercentage"></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSCurrentPrice} (S):</label></div>
						<div class='span3'><span id="wn_prodsearch_unitPriceVAT"></span></div>
					</div>
					
					<div class="row-fluid">
						<div class="legend-container">
							<span>${uiLabelMap.BSEnterReturnQuantityAndReturnPrice}</span>
							<hr/>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSReturnPrice} (${uiLabelMap.BSAfterVAT})</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_listPriceVAT"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BSReturnPrice} (${uiLabelMap.BSBeforeVAT})</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_listPrice"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label class="required">${uiLabelMap.BSReturnQty}</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_returnQuantity" style="display:inline-block"></div> <span id="wn_prodsearch_returnQuantityStr" style="vertical-align:top">()</span>
					   		</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_prodsearch_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSAdd}</button>
				<button id="wn_prodsearch_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		setTimeout(
			function(){OlbReturnNewItemsProdSearchBySalesman.init();},
			700
		)
		
	});
	var OlbReturnNewItemsProdSearchBySalesman = (function(){
		var itemProductTodo;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#popupQuotItemsProdSearchAdd"), {width: 550, height: 420, cancelButton: $("#wn_prodsearch_alterCancel")});
			
			jOlbUtil.numberInput.create($("#wn_prodsearch_returnQuantity"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 0, allowNull: true, min: 0, inputMode: 'simple'});
			jOlbUtil.numberInput.create($("#wn_prodsearch_listPriceVAT"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 2, allowNull: true, min: 0});
			jOlbUtil.numberInput.create($("#wn_prodsearch_listPrice"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 2, allowNull: true, min: 0});
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					//$("#wn_prodsearch_returnQuantity").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#wn_prodsearch_listPriceVAT").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#wn_prodsearch_listPrice").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
				}
			}, 50);
		};
		var initElementComplex = function(){
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
			        {name: 'uomId', type: 'string'},
			        {name: 'packingUomIds', type: 'array'},
			        {name: 'amountPrice', type: 'number'},
			        {name: 'amountWeight', type: 'number'},
			    ],
			    type: "POST",
			    root: "productsList",
			    contentType: 'application/x-www-form-urlencoded',
			    url: "findProductsAddToReturn"
			};
			
		   	var dataAdapter = new $.jqx.dataAdapter(sourceProduct, {
		    	downloadComplete: function (data, status, xhr) {
		    		$("#loader_page_common").hide();
		    		
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
			            return data;
		            }
		        },
		        beforeSend: function(xhr, settings) {
					$("#loader_page_common").show();
				},
			});
			$("#jqxProductSearch").keypress(function(e) {
			    if (e.which == 13) {
			    	dataAdapter.dataBind();
			    }
			});
		   	
			$("#jqxProductSearch").jqxComboBox({
		   		width: 290,
		    	dropDownWidth: 700,
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
						//var quantityUomId = item.salesUomId ? item.salesUomId : item.quantityUomId;
		            	var tableItem = '<div class="span6" style="margin-left: 0; width: 700px; height: 35px">'
		            	   + '<div class="span1" style="width: 100px; margin-left: 10px">' + productCode + '</div>'
		            	   + '<div class="span3" style="margin-left: 10px; width: 500px;">' + item.productName + '</div>'
		            	   + '<div class="span1" style="margin-left: 10px">' + getUomDesc(item.uomId) + '</div>'
		            	   + '</div>';
		                return tableItem;
		             }
		             return "";
				},
		        renderSelectedItem: function(index, item) {
		        	var item = dataAdapter.records[index];
		            if (item != null) {
		           		var label = item.productCode	;
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
			
			$("#wn_prodsearch_alterSave").on("click", function(){
				if (!validatorVAL.validate()) {
					return false;
				}
				addItemFromPopupToGrid();
			});
			$('#popupQuotItemsProdSearchAdd').keydown(function(e) {
				//13 enter
				var code = (e.keyCode ? e.keyCode : e.which);
				if (code == 13) {
					e.preventDefault();
					
					if (!validatorVAL.validate()) {
						return false;
					}
					addItemFromPopupToGrid();
					return false;
				}
			});
			$("#popupQuotItemsProdSearchAdd").on('open', function(e){
				setTimeout(
					function(){
						$("#jqxProductSearch").jqxComboBox('clearSelection');
						$("#jqxProductSearch").jqxComboBox('close');
					}, 300);
			});
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: '#wn_prodsearch_listPrice, #wn_prodsearch_listPrice', message: validFieldRequire,
					rule: function(input, commit){
						var listPriceVAT = $("#wn_prodsearch_listPrice").jqxNumberInput('getDecimal');
						var listPrice = $("#wn_prodsearch_listPrice").jqxNumberInput('getDecimal');
						if (listPriceVAT || listPrice) {
							return true;
						}
						return false;
					}
				},
				{input: '#wn_prodsearch_returnQuantity', message: validFieldRequire,
					rule: function(input, commit){
						var returnQuantity = $("#wn_prodsearch_returnQuantity").jqxNumberInput('getDecimal');
						if (returnQuantity) {
							return true;
						}
						return false;
					}
				},
			];
			var mapRules = [];
			validatorVAL = new OlbValidator($('#popupQuotItemsProdSearchAdd'), mapRules, extendRules, {position: 'bottom', scroll: true});
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
			var returnQuantity = 1;
			var productName = item.productName;
			var idEAN = item.idEAN;
			var amountPrice = item.amountPrice;
			var amountWeight = item.amountWeight;
			/*setTimeout(function(){
				productToSearchFocus();
			}, 300);
			*/
			
			var rowData = {
				productId: productId,
				productCode: productCode,
				productName: productName,
				quantityUomId: quantityUomId,
				uomId: item.uomId,
				returnQuantity: returnQuantity,
				idEAN: idEAN,
			};
			//OlbProdItemPopup.addOrIncreaseQuantity(rowData);
			getInfoProductAndAddToGrid(rowData);
		}
		function getInfoProductAndAddToGrid(rowData) {
			itemProductTodo = {};
			
			$.ajax({
				type: 'POST',
				url: "getInfoProductAddToQuot",
				data: {
					"productId": rowData.productId,
					"quantityUomId": rowData.uomId
				},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						$("#btnPrevWizard").removeClass("disabled");
						$("#btnNextWizard").removeClass("disabled");
						
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	return false;
					}, function(){
						/*
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
						*/
			        	if (data.productInfo != undefined && data.productInfo != null) {
			        		// add item to grid
			        		//OlbReturnAddProductItems.addItemsToGridPopup([data.productInfo]);
			        		itemProductTodo = data.productInfo;
			        		openWindowAdd();
			        	}
					});
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		}
		function productToSearchFocus() {
			$("#jqxProductSearch").jqxComboBox('clearSelection');
			$("#jqxProductSearch").jqxComboBox('close');
			$("#jqxProductSearch").jqxComboBox('focus');
			return false;
		}
		function addItemFromPopupToGrid(){
			var returnQuantity = $("#wn_prodsearch_returnQuantity").jqxNumberInput('getDecimal');
			var listPriceVAT = $("#wn_prodsearch_listPriceVAT").jqxNumberInput('getDecimal');
			var listPrice = $("#wn_prodsearch_listPrice").jqxNumberInput('getDecimal');
			var priceBeforeVAT = listPrice;
			var priceVAT = listPriceVAT;
			
			if (listPriceVAT) {
				priceBeforeVAT = listPriceVAT;
	    		if (itemProductTodo.taxPercentage) {
			   		if (OlbCore.isNotEmpty(listPriceVAT)) {
			   			var taxPercentage = itemProductTodo.taxPercentage;
				   		var valueCal = listPriceVAT * 100 / (100 + taxPercentage);
				   		valueCal = Math.round(valueCal * 100) / 100
				   		priceBeforeVAT = valueCal;
			   		}
		    	}
			}
			
	    	itemProductTodo.returnPrice = priceBeforeVAT;
	    	itemProductTodo.returnQuantity = returnQuantity;
			
			OlbReturnAddProductItemsBySalesman.addItemsToGridPopup([itemProductTodo]);
			closeWindowAdd();
			productToSearchFocus();
		}
		function openWindowAdd(){
			if (itemProductTodo && itemProductTodo.productId != null) {
				$("#wn_prodsearch_productCode").html(itemProductTodo.productCode);
				$("#wn_prodsearch_productName").html(itemProductTodo.productName);
				$("#wn_prodsearch_quantityUomId").html(getUomDesc(itemProductTodo.quantityUomId));
				$("#wn_prodsearch_taxPercentage").html(itemProductTodo.taxPercentage);
				$("#wn_prodsearch_currencyUomId").html(itemProductTodo.currencyUomId);
				$("#wn_prodsearch_returnQuantityStr").html("(" + getUomDesc(itemProductTodo.quantityUomId) + ")");
				
				var unitPriceBef = itemProductTodo.unitPriceBef;
		 		if (itemProductTodo && !unitPriceBef) unitPriceBef = itemProductTodo.unitPrice;
   				var unitPriceBefValue = formatcurrency(unitPriceBef, currencyUomId);
   				
   				var unitPriceVAT = itemProductTodo.unitPriceVAT;
		 		if (itemProductTodo && !unitPriceVAT) unitPriceVAT = itemProductTodo.unitPrice;
   				var unitPriceVATValue = formatcurrency(Math.round(unitPriceVAT + (unitPriceVAT * itemProductTodo.taxPercentage)/100), itemProductTodo.currencyUomId);
   				
				$("#wn_prodsearch_unitPriceBef").html(unitPriceBefValue);
				$("#wn_prodsearch_unitPriceVAT").html(unitPriceVATValue);
			} else {
				$("#wn_prodsearch_productCode").html("");
				$("#wn_prodsearch_productName").html("");
				$("#wn_prodsearch_quantityUomId").html("");
				$("#wn_prodsearch_taxPercentage").html("");
				$("#wn_prodsearch_currencyUomId").html("");
				$("#wn_prodsearch_unitPriceBef").html("");
				$("#wn_prodsearch_unitPriceVAT").html("");
			}
			
			$("#popupQuotItemsProdSearchAdd").jqxWindow("open");
			
			$("#wn_prodsearch_returnQuantity").val(null);
			$("#wn_prodsearch_listPriceVAT").val(null);
			$("#wn_prodsearch_listPrice").val(null);
			$("#wn_prodsearch_listPrice").jqxNumberInput('focus');
		}
		function closeWindowAdd(){
			$("#popupQuotItemsProdSearchAdd").jqxWindow("close");
		}
		return {
			init: init,
			productToSearchFocus: productToSearchFocus,
		}
	}());
</script>