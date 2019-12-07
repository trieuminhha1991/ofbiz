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
						
						<div class='span3 align-right'><label>${uiLabelMap.BSPrimaryUPC}:</label></div>
						<div class='span3'><span id="wn_prodsearch_idSKU" style="text-transform:uppercase"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label>${uiLabelMap.BSCurrencyUomId}:</label></div>
						<div class='span3'><span id="wn_prodsearch_currencyUomId"></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSProductPackingUomId}:</label></div>
						<div class='span3'><span id="wn_prodsearch_quantityUomId"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label>${uiLabelMap.BSTax}:</label></div>
						<div class='span3'><span id="wn_prodsearch_taxPercentage"></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSListPrice} (S):</label></div>
						<div class='span3'><span id="wn_prodsearch_unitListPriceVAT"></span></div>
					</div>
					<div class='row-fluid margin-bottom0'>
						<div class='span3 align-right'><label></label></div>
						<div class='span3'><span></span></div>
						
						<div class='span3 align-right'><label>${uiLabelMap.BSSalesPrice} (S):</label></div>
						<div class='span3'><span id="wn_prodsearch_unitPriceVAT"></span></div>
					</div>
					
					<div class="row-fluid">
						<div class="legend-container">
							<span style="background-color:inherit">${uiLabelMap.BSEnterInfo}</span>
							<hr style="width: 80%; margin: 15px 0px 10px 100px;"/>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<label>${uiLabelMap.BSNumberPrint}</label>
							</div>
							<div class='span7'>
								<div id="wn_prodsearch_numCopy"></div>
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
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSMissing = '${StringUtil.wrapString(uiLabelMap.BSMissing)}';
	uiLabelMap.BSNotPrintThisProductPrimaryUPCNotFound = '${StringUtil.wrapString(uiLabelMap.BSNotPrintThisProductPrimaryUPCNotFound)}';
	
	$(function(){
		OlbQuotNewItemsProdSearch.init();
	});
	var OlbQuotNewItemsProdSearch = (function(){
		var itemProductTodo;
		var productSearchCBBS;
		var mpopupQuotItemsProdSearchAdd = $("#popupQuotItemsProdSearchAdd");
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create(mpopupQuotItemsProdSearchAdd, {width: 520, height: 340, cancelButton: $("#wn_prodsearch_alterCancel")});
			
			jOlbUtil.numberInput.create($("#wn_prodsearch_numCopy"), {height: 30, spinButtons: true, digits: 8, decimalDigits: 0, allowNull: false, min: 1});
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					$("#wn_prodsearch_numCopy").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
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
			        {name: 'iupprm', type: 'number'},
			    ],
			    root: "productsList",
			    url: "findProductsAddToExportPrice",
			    placeHolder: " ${StringUtil.wrapString(uiLabelMap.BPOSSearchProduct)} (F1)",
		        messageItemNotFound: "${StringUtil.wrapString(uiLabelMap.BSProductProductNotFound)}",
		        displayMember: "productCode",
		        valueMember: "productCode",
		        formatDataFuncItem: function(data, searchString) {
		        	data.productToSearch = searchString;
		            data.productStoreId = OlbExportProdPrice.getObj().productStoreDDB.getValue();
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
					var classIdSku = "";
					if (item.iupprm == 1) classIdSku += " red";
	            	var tableItem = '<div class="span6" style="margin-left: 0; width: 560px; height: 35px">'
	            	   + '<div class="span1" style="width: 90px; margin-left: 10px">' + productCode + '</div>'
	            	   + '<div class="span2' + classIdSku + '" style="width: 120px; margin-left: 10px">' + item.idSKU + '</div>'
	            	   + '<div class="span2" style="width: 240px; margin-left: 10px">' + item.productName + '</div>'
	            	   + '<div class="span1" style="width: 70px; margin-left: 10px">' + item.uomId + '</div>'
	            	   + '</div>';
	                return tableItem;
				},
				handlerSelectedItem: addItem,
				beforeSearchHandler: function(){
					if (!OlbExportProdPrice.getValidator().validate()) return false;
					return true;
				},
			};
			productSearchCBBS = new OlbComboBoxSearchRemote($("#jqxProductSearch"), configComboBoxSearchProduct);
		};
		var initEvent = function(){
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
				} else if (code == 27) { // ESC
					closeWindowAdd();
				}
			});
			mpopupQuotItemsProdSearchAdd.on("close", function(){
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
			var idSKU = item.idSKU;
			
			var rowData = {
				productId: productId,
				productCode: productCode,
				productName: productName,
				quantityUomId: quantityUomId,
				uomId: item.uomId,
				quantity: quantity,
				idEAN: idEAN,
				packingUomIds: packingUomIds,
				idSKU: idSKU,
			};
			//OlbProdItemPopup.addOrIncreaseQuantity(rowData);
			getInfoProductAndAddToGrid(rowData);
		}
		function getInfoProductAndAddToGrid(rowData) {
			itemProductTodo = {};
			var productStoreId = OlbExportProdPrice.getObj().productStoreDDB.getValue();
			
			$.ajax({
				type: 'POST',
				url: "getInfoProductAddToExportPrice",
				data: {
					"idSKU": rowData.idSKU,
					"productId": rowData.productId,
					"quantityUomId": rowData.uomId,
					"productStoreId": productStoreId
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
			        		//OlbExportProdPrice.addItemsToGridPopup([data.productInfo]);
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
			if (OlbExportProdPrice.isCheckPrimaryUPC() && (OlbCore.isEmpty(itemProductTodo.idSKU) || itemProductTodo.iupprm != 1)) {
				OlbCore.alert.error(uiLabelMap.BSNotPrintThisProductPrimaryUPCNotFound, function(){
					mpopupQuotItemsProdSearchAdd.focus();
				});
				return false;
			} 
			
			var numCopy = $("#wn_prodsearch_numCopy").jqxNumberInput('getDecimal');
	    	
	    	itemProductTodo.numCopy = numCopy;
			OlbExportProdPrice.addItemsToGridPopup([itemProductTodo]);
			closeWindowAdd();
			productSearchCBBS.focusSearch();
		}
		function openWindowAdd(){
			var idSKU;
			var mwnps_productCode = $("#wn_prodsearch_productCode");
			var mwnps_productName = $("#wn_prodsearch_productName");
			var mwnps_quantityUomId = $("#wn_prodsearch_quantityUomId");
			var mwnps_taxPercentage = $("#wn_prodsearch_taxPercentage");
			var mwnps_currencyUomId = $("#wn_prodsearch_currencyUomId");
			var mwnps_idSKU = $("#wn_prodsearch_idSKU");
			var mwnps_numCopy = $("#wn_prodsearch_numCopy");
			var mwnps_unitListPriceVAT = $("#wn_prodsearch_unitListPriceVAT");
			var mwnps_unitPriceVAT = $("#wn_prodsearch_unitPriceVAT");
			
			if (itemProductTodo && itemProductTodo.productId != null) {
				mwnps_productCode.html(itemProductTodo.productCode);
				mwnps_productName.html(itemProductTodo.productName);
				mwnps_quantityUomId.html(itemProductTodo.uomId);
				mwnps_taxPercentage.html(itemProductTodo.taxPercentage);
				mwnps_currencyUomId.html(itemProductTodo.currencyUomId);
				
				idSKU = itemProductTodo.idSKU;
				
				var unitListPriceVAT = itemProductTodo.unitListPriceVAT;
		 		if (itemProductTodo && !unitListPriceVAT) unitListPriceVAT = itemProductTodo.unitPriceVAT;
   				var unitListPriceVATValue = formatcurrency(unitListPriceVAT, itemProductTodo.currencyUomId);
   				
   				var unitPriceVAT = itemProductTodo.unitPriceVAT;
   				var unitPriceVATValue = formatcurrency(unitPriceVAT, itemProductTodo.currencyUomId);
   				
   				if (unitListPriceVAT != unitPriceVAT) {
   					mwnps_unitListPriceVAT.addClass("strike-through");
   				} else {
   					mwnps_unitListPriceVAT.removeClass("strike-through");
   				}
   				
				mwnps_unitListPriceVAT.html(unitListPriceVATValue);
				mwnps_unitPriceVAT.html(unitPriceVATValue);
			} else {
				mwnps_productCode.html("");
				mwnps_productName.html("");
				mwnps_idSKU.html("");
				mwnps_quantityUomId.html("");
				mwnps_taxPercentage.html("");
				mwnps_currencyUomId.html("");
				mwnps_unitListPriceVAT.html("");
				mwnps_unitPriceVAT.html("");
			}
			
			var isShowErrorCheck = OlbExportProdPrice.isCheckPrimaryUPC() && (OlbCore.isEmpty(itemProductTodo.idSKU) || itemProductTodo.iupprm != 1);
			if (isShowErrorCheck) {
				mwnps_idSKU.text("<" + uiLabelMap.BSMissing + ">");
				mwnps_idSKU.addClass("red");
				mpopupQuotItemsProdSearchAdd.find(".form-window-container").addClass("background-lvheight");
				mwnps_numCopy.jqxNumberInput({disabled: true});
			} else {
				if (itemProductTodo.iupprm == 1) mwnps_idSKU.addClass("red");
				
				if (OlbExportProdPrice.isCheckPrimaryUPC()) {
					if (itemProductTodo.iupprm == 1) {
						mwnps_idSKU.html(idSKU);
					} else {
						mwnps_idSKU.html("");
					}
				} else {
					mwnps_idSKU.html(idSKU);
				}
				mwnps_idSKU.removeClass("red");
				mpopupQuotItemsProdSearchAdd.find(".form-window-container").removeClass("background-lvheight");
				mwnps_numCopy.jqxNumberInput({disabled: false});
			}
			
			mpopupQuotItemsProdSearchAdd.jqxWindow("open");
			
			if (isShowErrorCheck) {
				mwnps_numCopy.jqxNumberInput("val", 0);
				mpopupQuotItemsProdSearchAdd.focus();
			} else {
				mwnps_numCopy.jqxNumberInput("val", 1);
				mwnps_numCopy.jqxNumberInput('focus');
			}
		}
		function closeWindowAdd(){
			mpopupQuotItemsProdSearchAdd.jqxWindow("close");
			mpopupQuotItemsProdSearchAdd.find(".form-window-container").removeClass("background-lvheight");
			$("#wn_prodsearch_idSKU").removeClass("red");
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