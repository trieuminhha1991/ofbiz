<div id="alterpopupWindowCategoryItemNew" style="display:none">
	<div>${uiLabelMap.BSAddCategoryItem}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input id="wn_pcatei_categoryName" type="hidden" value=""/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSCategoryId}</label>
						</div>
						<div class='span7'>
							<div id="wn_pcatei_productCategoryId">
								<div id="wn_pcatei_productCategoryGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSAmount}</label>
						</div>
						<div class='span7'>
							<div id="wn_pcatei_amount"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_pcatei_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_pcatei_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProdCategoryAddNew.init();
	});
	var OlbProdCategoryAddNew = (function(){
		var productCategoryDDB;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initValidateForm();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.numberInput.create($("#wn_pcatei_amount"), {width: '99%', spinButtons: true, digits: 8, decimalDigits: 3});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowCategoryItemNew"), {width: 520, height: 240, cancelButton: $("#wn_pcatei_alterCancel")});
			
			setTimeout(function(){
				var locale = "${locale}";
				if(locale == "vi"){
					$("#wn_pcatei_amount").jqxNumberInput({decimalSeparator: ",", groupSeparator: "."});
				}
			}, 50);
		};
		var initEvent = function(){
			$("#wn_pcatei_alterSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				
				var uid = $("#jqxgridCategoryItem").data("uid");
				if (uid) {
					$('#jqxgridCategoryItem').jqxGrid('updaterow', uid, getValue());
				} else {
					$('#jqxgridCategoryItem').jqxGrid('addrow', null, getValue());
				}
				$("#alterpopupWindowCategoryItemNew").jqxWindow("close");
			});
			
			$('#alterpopupWindowCategoryItemNew').on('close', function (event) {
				$('#alterpopupWindowCategoryItemNew').jqxValidator('hide');
				$("#wn_pcatei_productCategoryGrid").val('');
				productCategoryDDB.clearAll();
				$("#wn_pcatei_amount").jqxNumberInput('val', null);
				$("#alterpopupWindowCategoryItemNew").data("uid", null);
			});
			
			productCategoryDDB.getGrid().rowSelectListener(function(rowData){
				if (rowData) {
					$("#wn_pcatei_categoryName").val(rowData.categoryName);
				}
			});
		};
		var initElementComplex = function(){
			var configProductCategory = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'productCategoryId', type: 'string'}, {name: 'categoryName', type: 'string'}],
				columns: [
					{text: "${StringUtil.wrapString(uiLabelMap.BSProductCategoryId)}", datafield: 'productCategoryId', width: '30%'},
					{text: "${StringUtil.wrapString(uiLabelMap.BSCategoryName)}", datafield: 'categoryName', width: '70%'},
				],
				url: 'JQListProductCategory&productCategoryTypeId=CATALOG_CATEGORY&showChildren=N',
				useUtilFunc: true,
				
				key: 'productCategoryId',
				description: ['categoryName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			productCategoryDDB = new OlbDropDownButton($("#wn_pcatei_productCategoryId"), $("#wn_pcatei_productCategoryGrid"), null, configProductCategory, []);
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#wn_pcatei_productCategoryId', type: 'validObjectNotNull', objType: 'dropDownButton'}
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowCategoryItemNew'), mapRules, extendRules, {position: 'bottom'});
		};
		var getValue = function() {
			var value = new Object();
			value.productCategoryId = productCategoryDDB.getValue();
			value.categoryName = $("#wn_pcatei_categoryName").val();
			value.amount = $("#wn_pcatei_amount").jqxNumberInput('getDecimal');
			return value;
		};
		return {
			init: init
		}
	}());
</script>