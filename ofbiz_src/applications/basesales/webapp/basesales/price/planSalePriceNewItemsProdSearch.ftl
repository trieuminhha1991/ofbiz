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
						
						<div class='span3 align-right'><label>${uiLabelMap.BSDefaultPrice} (S):</label></div>
						<div class='span3'><span id="wn_prodsearch_defaultPriceCurrent"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label>${uiLabelMap.BSTax}:</label></div>
						<div class='span3'><span id="wn_prodsearch_taxPercentage"></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSListPrice} (S):</label></div>
						<div class='span3'><span id="wn_prodsearch_listPriceCurrent"></span></div>
					</div>
					
					<div class="row-fluid">
						<div class="legend-container">
							<span>${uiLabelMap.BSEnterPrice}</span>
							<hr/>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSDefaultPrice} (${uiLabelMap.BSNewPrice} - S)</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_defaultPriceNew" class="div-input-important"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSListPrice} (${uiLabelMap.BSNewPrice} - S)</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_listPriceNew" class="div-input-important"></div>
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
			
			jOlbUtil.numberInput.create($("#wn_prodsearch_listPriceNew"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 2, allowNull: true, min: 0});
			jOlbUtil.numberInput.create($("#wn_prodsearch_defaultPriceNew"), {height: 30, spinButtons: false, digits: 8, decimalDigits: 2, allowNull: true, min: 0});
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					$("#wn_prodsearch_listPriceNew").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
					$("#wn_prodsearch_defaultPriceNew").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
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
			        /*
			        {name: "productId", type: "string"},
					{name: "productCode", type: "string"},
					{name: "idSKU", type: "string"},
					{name: "primaryProductCategoryId", type: "string"},
					{name: "productName", type: "string"},
					{name: "quantityUomId", type: "string"}
					var columns = [
						{text: multiLang.BSProductId, datafield: "productCode", width: 120},
						{text: multiLang.ProductProductName, datafield: "productName", minwidth: 200},
						{text: multiLang.BSUPC, datafield: "idSKU", width: 200},
						{text: multiLang.BSPrimaryProductCategory, datafield: "primaryProductCategoryId", width: 120},
						{text: multiLang.DmsQuantityUomId, datafield: "quantityUomId", filtertype: "checkedlist", width: 100,
							cellsrenderer: function(row, colum, value) {
								value?value=mapQuantityUom[value]:value;
								return "<span>" + value + "</span>";
							},
							createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listQuantityUom, displayMember: "description", valueMember: "uomId" });
							}
						}
					];
			         */
			    ],
			    root: "productsList",
			    url: "findProducts",
			    placeHolder: uiLabelMap.BPOSSearchProduct,
		        messageItemNotFound: uiLabelMap.BSProductProductNotFound,
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
			$("#wn_prodsearch_defaultPriceNew").keyup(function(e) {
				var listPrice = $("#wn_prodsearch_defaultPriceNew").jqxNumberInput('getDecimal');
				var priceVAT = listPrice;
	    		if (itemProductTodo.taxPercentage) {
			   		if (OlbCore.isNotEmpty(listPrice)) {
			   			var taxPercentage = itemProductTodo.taxPercentage;
				   		var valueCal = listPrice + listPrice * taxPercentage / 100;
				   		valueCal = Math.round(valueCal * 100) / 100
			    		priceVAT = valueCal;
			   		}
		    	}
		    	$("#wn_prodsearch_listPriceNew").val(priceVAT);
			});
			$("#wn_prodsearch_listPriceNew").keyup(function(e) {
				var listPriceVAT = $("#wn_prodsearch_listPriceNew").jqxNumberInput('getDecimal');
				var priceBeforeVAT = listPriceVAT;
	    		if (itemProductTodo.taxPercentage) {
			   		if (OlbCore.isNotEmpty(listPriceVAT)) {
			   			var taxPercentage = itemProductTodo.taxPercentage;
				   		var valueCal = listPriceVAT * 100 / (100 + taxPercentage);
				   		valueCal = Math.round(valueCal * 100) / 100
				   		priceBeforeVAT = valueCal;
			   		}
		    	}
		    	$("#wn_prodsearch_defaultPriceNew").val(priceBeforeVAT);
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
				url: "getInfoProductAddToPlanSalePrice",
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
			var listPriceNew = $("#wn_prodsearch_listPriceNew").jqxNumberInput('getDecimal');
			var defaultPriceNew = $("#wn_prodsearch_defaultPriceNew").jqxNumberInput('getDecimal');
	    	itemProductTodo.defaultPriceNew = defaultPriceNew;
			itemProductTodo.listPriceNew = listPriceNew;
			
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
				
				var defaultPriceVAT = itemProductTodo.defaultPriceVAT;
				var listPriceVAT = itemProductTodo.listPriceVAT;
   				var defaultPriceVATValue = formatcurrency(defaultPriceVAT, itemProductTodo.currencyUomId);
   				var listPriceVATValue = formatcurrency(listPriceVAT, itemProductTodo.currencyUomId);
   				
				$("#wn_prodsearch_defaultPriceCurrent").html(defaultPriceVATValue);
				$("#wn_prodsearch_listPriceCurrent").html(listPriceVATValue);
				$("#wn_prodsearch_defaultPriceNew").jqxNumberInput('setDecimal', defaultPriceVAT);
				$("#wn_prodsearch_listPriceNew").jqxNumberInput('setDecimal', listPriceVAT);
			} else {
				$("#wn_prodsearch_productCode").html("");
				$("#wn_prodsearch_productName").html("");
				$("#wn_prodsearch_quantityUomId").html("");
				$("#wn_prodsearch_taxPercentage").html("");
				$("#wn_prodsearch_currencyUomId").html("");
				$("#wn_prodsearch_defaultPriceCurrent").html("");
				$("#wn_prodsearch_listPriceCurrent").html("");
				$("#wn_prodsearch_defaultPriceNew").val(null);
				$("#wn_prodsearch_listPriceNew").val(null);
			}
			
			$("#popupQuotItemsProdSearchAdd").jqxWindow("open");
			
			$("#wn_prodsearch_defaultPriceNew").jqxNumberInput('focus');
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