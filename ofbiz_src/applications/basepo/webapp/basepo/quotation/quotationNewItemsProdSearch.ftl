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

<div id="jqxProductSearch" class="navbar-left"></div>

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
							<span>${uiLabelMap.BSEnterPrice}</span>
							<hr/>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSNewPrice} (T)</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_listPrice" class="div-input-important"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSNewPrice} (S)</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_listPriceVAT"></div>
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

<@jqOlbCoreLib hasCore=false hasComboBoxSearchRemote=true/>
<script type="text/javascript">
	$(function(){
		OlbQuotNewItemsProdSearch.init();
	});
	var OlbQuotNewItemsProdSearch = (function(){
		var itemProductTodo;
		var productSearchCBBS;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#popupQuotItemsProdSearchAdd"), {width: 520, height: 340, cancelButton: $("#wn_prodsearch_alterCancel")});
			
			jOlbUtil.numberInput.create($("#wn_prodsearch_listPriceVAT"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 2, allowNull: true, min: 0});
			jOlbUtil.numberInput.create($("#wn_prodsearch_listPrice"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 2, allowNull: true, min: 0});
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					$("#wn_prodsearch_listPriceVAT").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#wn_prodsearch_listPrice").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
				}
			}, 50);
		};
		var initElementComplex = function(){
			var configComboBoxSearchProduct = {
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
			    root: "productsList",
			    url: "findProductsAddToQuotPO",
			    placeHolder: " ${StringUtil.wrapString(uiLabelMap.BPOSSearchProduct)} (F1)",
		        messageItemNotFound: "${StringUtil.wrapString(uiLabelMap.BSProductProductNotFound)}",
		        displayMember: "productCode",
		        valueMember: "productCode",
		        formatDataFuncItem: function(data, searchString) {
		        	data.productToSearch = searchString;
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
					//var quantityUomId = item.salesUomId ? item.salesUomId : item.quantityUomId;
	            	var tableItem = '<div class="span6" style="margin-left: 0; width: 550px; height: 35px">'
	            	   + '<div class="span1" style="width: 100px; margin-left: 10px">' + productCode + '</div>'
	            	   + '<div class="span3" style="margin-left: 10px">' + item.productName + '</div>'
	            	   + '<div class="span1" style="margin-left: 10px">' + item.uomId + '</div>'
	            	   + '</div>';
	                return tableItem;
				},
				handlerSelectedItem: addItem,
			};
			productSearchCBBS = new OlbComboBoxSearchRemote($("#jqxProductSearch"), configComboBoxSearchProduct);
		};
		var initEvent = function(){
			<#--
			$("#wn_prodsearch_listPrice").keyup(function(e) {
				var listPrice = $("#wn_prodsearch_listPrice").jqxNumberInput('getDecimal');
				var priceVAT = listPrice;
	    		if (itemProductTodo.taxPercentage) {
			   		if (OlbCore.isNotEmpty(listPrice)) {
			   			var taxPercentage = itemProductTodo.taxPercentage;
				   		var valueCal = listPrice + listPrice * taxPercentage / 100;
				   		valueCal = Math.round(valueCal * 100) / 100
			    		priceVAT = valueCal;
			   		}
		    	}
		    	$("#wn_prodsearch_listPriceVAT").val(priceVAT);
			});
			$("#wn_prodsearch_listPriceVAT").keyup(function(e) {
				var listPriceVAT = $("#wn_prodsearch_listPriceVAT").jqxNumberInput('getDecimal');
				var priceBeforeVAT = listPriceVAT;
	    		if (itemProductTodo.taxPercentage) {
			   		if (OlbCore.isNotEmpty(listPriceVAT)) {
			   			var taxPercentage = itemProductTodo.taxPercentage;
				   		var valueCal = listPriceVAT * 100 / (100 + taxPercentage);
				   		valueCal = Math.round(valueCal * 100) / 100
				   		priceBeforeVAT = valueCal;
			   		}
		    	}
		    	$("#wn_prodsearch_listPrice").val(priceBeforeVAT);
			});
			-->
			$("#wn_prodsearch_alterSave").on("click", function(){
				addItemFromPopupToGrid();
			});
			$('#popupQuotItemsProdSearchAdd').keydown(function(e) {
				//13 enter
				var code = (e.keyCode ? e.keyCode : e.which);
				if (code == 13) {
					e.preventDefault();
					
					addItemFromPopupToGrid();
					return false;
				}
			});
			$("#popupQuotItemsProdSearchAdd").on("close", function(){
				productSearchCBBS.focusSearch();
			});
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
			
			var rowData = {
				productId: productId,
				productCode: productCode,
				productName: productName,
				quantityUomId: quantityUomId,
				uomId: item.uomId,
				quantity: quantity,
				idEAN: idEAN,
				packingUomIds: packingUomIds,
			};
			//OlbProdItemPopup.addOrIncreaseQuantity(rowData);
			getInfoProductAndAddToGrid(rowData);
		}
		function getInfoProductAndAddToGrid(rowData) {
			itemProductTodo = {};
			
			$.ajax({
				type: 'POST',
				url: "getInfoProductAddToQuotPO",
				data: {
					"productId": rowData.productId,
					"quantityUomId": rowData.uomId,
					"currencyUomId": OlbQuotationInfo.getObj().currencyUomIdCBB.getValue(),
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
			        		//OlbAddProductItems.addItemsToGridPopup([data.productInfo]);
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
		function addItemFromPopupToGrid(){
			var listPriceVAT = $("#wn_prodsearch_listPriceVAT").jqxNumberInput('getDecimal');
			var listPrice = $("#wn_prodsearch_listPrice").jqxNumberInput('getDecimal');
			var priceBeforeVAT = listPrice;
			var priceVAT = listPriceVAT;
			if (listPrice) {
				priceVAT = null;
				listPriceVAT = null;
			}
			
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
			if (listPrice) {
				priceVAT = listPrice;
	    		if (itemProductTodo.taxPercentage) {
			   		if (OlbCore.isNotEmpty(listPrice)) {
			   			var taxPercentage = itemProductTodo.taxPercentage;
				   		var valueCal = listPrice + listPrice * taxPercentage / 100;
				   		valueCal = Math.round(valueCal * 100) / 100
			    		priceVAT = valueCal;
			   		}
		    	}
			}
	    	
	    	itemProductTodo.listPrice = priceBeforeVAT;
			itemProductTodo.listPriceVAT = priceVAT;
			
			OlbAddProductItems.addItemsToGridPopup([itemProductTodo]);
			closeWindowAdd();
			productSearchCBBS.focusSearch();
		}
		function openWindowAdd(){
			if (itemProductTodo && itemProductTodo.productId != null) {
				$("#wn_prodsearch_productCode").html(itemProductTodo.productCode);
				$("#wn_prodsearch_productName").html(itemProductTodo.productName);
				$("#wn_prodsearch_quantityUomId").html(itemProductTodo.quantityUomId);
				$("#wn_prodsearch_taxPercentage").html(itemProductTodo.taxPercentage);
				$("#wn_prodsearch_currencyUomId").html(itemProductTodo.currencyUomId);
				
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
			
			$("#wn_prodsearch_listPriceVAT").val(null);
			$("#wn_prodsearch_listPrice").val(null);
			$("#wn_prodsearch_listPrice").jqxNumberInput('focus');
		}
		function closeWindowAdd(){
			$("#popupQuotItemsProdSearchAdd").jqxWindow("close");
		}
		function productToSearchFocus() {
			return productSearchCBBS.focusSearch();
		}
		return {
			init: init,
			productToSearchFocus: productToSearchFocus,
		}
	}());
</script>