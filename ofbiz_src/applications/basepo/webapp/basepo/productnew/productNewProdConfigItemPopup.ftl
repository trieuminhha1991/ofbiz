<div id="alterpopupWindowProdConfigItemNew" style="display:none">
	<div>${uiLabelMap.BSAddProductConfigItem}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input id="wn_pci_productCode" type="hidden" value=""/>
			<input id="wn_pci_productName" type="hidden" value=""/>
			<input id="wn_pci_requireAmount" type="hidden" value=""/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSProduct}</label>
						</div>
						<div class='span7'>
							<div id="wn_pci_productId">
								<div id="wn_pci_productGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSQuantity}</label>
						</div>
						<div class='span7'>
							<div id="wn_pci_quantity"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSWeight}</label>
						</div>
						<div class='span7'>
							<div class="container-add-plus">
								<div id="wn_pci_amount"></div>
								&nbsp;(<span id="wn_pci_amount_uom">...</span>)
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSequenceNumber}</label>
						</div>
						<div class='span7'>
							<div id="wn_pci_sequenceNum"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_pci_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_pci_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProdConfigItemAddNew.init();
	});
	var OlbProdConfigItemAddNew = (function(){
		var productDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.numberInput.create($("#wn_pci_quantity"), {width: '99%', spinButtons: true, decimalDigits: 0, min: 1, decimal: 1, inputMode: 'simple'});
			jOlbUtil.numberInput.create($("#wn_pci_amount"), {width: '75%', spinButtons: true, decimalDigits: 3, min: 0, inputMode: 'simple', disabled: true});
			jOlbUtil.numberInput.create($("#wn_pci_sequenceNum"), {width: '99%', spinButtons: true, decimalDigits: 0, min: 0, allowNull: true, inputMode: 'simple'});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowProdConfigItemNew"), {width: 520, height: 270, cancelButton: $("#wn_pci_alterCancel")});
		};
		var initEvent = function(){
			$("#wn_pci_alterSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				
				var uid = $("#jqxgridProductConfigItem").data("uid");
				if (uid) {
					$('#jqxgridProductConfigItem').jqxGrid('updaterow', uid, getValue());
				} else {
					$('#jqxgridProductConfigItem').jqxGrid('addrow', null, getValue());
				}
				$("#alterpopupWindowProdConfigItemNew").jqxWindow("close");
			});
			
			$('#alterpopupWindowProdConfigItemNew').on('close', function (event) {
				$('#alterpopupWindowProdConfigItemNew').jqxValidator('hide');
				$("#wn_pci_productName").val('');
				$("#wn_pci_requireAmount").val('');
				$("#wn_pci_amount_uom").text('...');
				productDDB.clearAll();
				$("#wn_pci_quantity").jqxNumberInput('val', 1);
				$("#wn_pci_amount").jqxNumberInput('val', 0);
				$("#wn_pci_sequenceNum").jqxNumberInput('val', 0);
				$("#alterpopupWindowProdConfigItemNew").data("uid", null);
			});
			
			productDDB.getGrid().rowSelectListener(function(rowData){
				if (rowData) {
					$("#wn_pci_productCode").val(rowData.productCode);
					$("#wn_pci_productName").val(rowData.productName);
					$("#wn_pci_requireAmount").val(rowData.requireAmount);
					if (rowData.weightUomId) {
						$("#wn_pci_amount_uom").text(rowData.weightUomId);
					} else {
						$("#wn_pci_amount_uom").text('...');
					}
					if (rowData.requireAmount == "Y") {
						$("#wn_pci_amount").jqxNumberInput("disabled", false);
					} else {
						$("#wn_pci_amount").jqxNumberInput("disabled", true);
					}
				}
			});
		};
		var initElementComplex = function(){
			var configProduct = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'productId', type: 'string'}, 
					{name: 'productCode', type: 'string'}, 
					{name: 'productName', type: 'string'},
					{name: 'requireAmount', type: 'string'},
					{name: 'weightUomId', type: 'string'}
				],
				columns: [
					{text: "${StringUtil.wrapString(uiLabelMap.BSProductId)}", datafield: 'productCode', width: '30%'},
					{text: "${StringUtil.wrapString(uiLabelMap.BSProductName)}", datafield: 'productName', width: '40%'},
					{text: "${StringUtil.wrapString(uiLabelMap.BSRequireAmount)}", datafield: 'requireAmount', width: '15%'},
					{text: "${StringUtil.wrapString(uiLabelMap.BSUom)}", datafield: 'weightUomId', width: '15%'},
				],
				url: 'JQGetListProductOfCompany&hasVirtualProd=N&productTypeId=FINISHED_GOOD',
				useUtilFunc: true,
				
				key: 'productId',
				keyCode: 'productCode',
				description: ['productName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			productDDB = new OlbDropDownButton($("#wn_pci_productId"), $("#wn_pci_productGrid"), null, configProduct, []);
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: '#wn_pci_amount', message: "${uiLabelMap.validFieldRequire}", action: 'keyup', 
					rule: function(input, commit){
						var requireAmount = $("#wn_pci_requireAmount").val();
						if (requireAmount == "Y") {
							return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
						}
						return true;
					}
				},
				{input: '#wn_pci_amount', message: "${uiLabelMap.BSWeightMustBeGreaterThanZero}", action: 'keyup', 
					rule: function(input, commit){
						var requireAmount = $("#wn_pci_requireAmount").val();
						if (requireAmount == "Y") {
							if ($(input).val() > 0) {
								return true;
							}
							return false;
						}
						return true;
					}
				},
			];
			var mapRules = [
					{input: '#wn_pci_productId', type: 'validObjectNotNull', objType: 'dropDownButton'}
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowProdConfigItemNew'), mapRules, extendRules, {position: 'bottom'});
		};
		var getValue = function() {
			var value = new Object();
			value.productId = productDDB.getValue();
			value.productCode = $("#wn_pci_productCode").val();
			value.productName = $("#wn_pci_productName").val();
			value.quantity = $("#wn_pci_quantity").jqxNumberInput('getDecimal');
			value.amount = $("#wn_pci_amount").jqxNumberInput('getDecimal');
			var sequenceNum = $("#wn_pci_sequenceNum").jqxNumberInput('getDecimal')
			if (sequenceNum && sequenceNum > 0) value.sequenceNum = sequenceNum;
			return value;
		};
		return {
			init: init
		}
	}());
</script>

