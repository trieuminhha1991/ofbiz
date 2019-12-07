<div id="popupAddCost" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="HistoryForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.FormTitle_marketingCostTypeId}</label>
						<div class="span7 margin-bottom10">
							<div id="marketingCostTypeIdAdd">
							</div>
						</div>
					</div>

					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.CommonPlace}</label>
						<div class="span7 margin-bottom10">
							<textarea id="descriptionAdd" row="3" class="no-resize"></textarea>
						</div>
					</div>

					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.unitPrice}</label>
						<div class="span7 margin-bottom10">
							<div id="unitPriceAdd"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.DACurrencyUomId}</label>
						<div class="span7 margin-bottom10">
							<div id="currencyUomIdAdd"></div>
						</div>
					</div>

					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.quantity}</label>
						<div class="span7 margin-bottom10">
							<div id="quantityCostAdd"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.uomId}</label>
						<div class="span7 margin-bottom10">
							<div id="quantityUomIdAdd"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="cancelCost" class='btn btn-danger form-action-button pull-right'>
							<i class='fa fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="saveCostAndContinue" class='btn btn-primary form-action-button pull-right'>
							<i class='fa fa-plus'> </i> ${uiLabelMap.SaveAndContinue}
						</button>
						<button type="button" id="saveCost" class='btn btn-primary form-action-button pull-right'>
							<i class='fa fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	var MKCost = ( function() {
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			var form = $('#popupAddCost');
			var width = 200;
			var grid = $("#MarketingCost");
			var initElement = function() {
				$("#marketingCostTypeIdAdd").jqxDropDownList({
					source : marketingCostTypeData,
					width : width,
					dropDownHeight: 200,
					filterable: true,
					displayMember : "name",
					valueMember : "marketingCostTypeId",
					placeHolder : "${StringUtil.wrapString(uiLabelMap.ChooseCostType?default(''))}"
				});
				$("#quantityUomIdAdd").jqxDropDownList({
					source : quantityUomData,
					width : width,
					dropDownHeight: 200,
					filterable: true,
					displayMember : "description",
					valueMember : "uomId",
					placeHolder : "${StringUtil.wrapString(uiLabelMap.ChooseUom?default(''))}"
				});
				$("#currencyUomIdAdd").jqxDropDownList({
					source : currencyUomData,
					width : width,
					dropDownHeight: 200,
					filterable: true,
					displayMember : "description",
					valueMember : "uomId",
					placeHolder : "${StringUtil.wrapString(uiLabelMap.ChooseCurrencyUom?default(''))}"
				});
				$("#currencyUomIdAdd").val('VND');
				$("#quantityCostAdd").jqxNumberInput({
					width : width,
					max : 999999999999999999,
					digits : 18,
					decimalDigits : 0,
					spinButtons : false,
					min : 0
				});
				$("#unitPriceAdd").jqxNumberInput({
					width : width,
					max : 999999999999999999,
					digits : 18,
					decimalDigits : 2,
					spinButtons : false,
					min : 0
				});
			};
			var initWindow = function() {
				form.jqxWindow({
					width : 500,
					height : 370,
					resizable : true,
					isModal : true,
					autoOpen : false,
					cancelButton : $("#cancelCost"),
					modalOpacity : 0.7
				});
				form.on("close", function() {
					clearForm();
				});
			};
			var initRules = function() {
				form.jqxValidator({
					rules : [{
						input : '#marketingCostTypeIdAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'blur',
						rule : function(input, commit){
							var val = input.jqxDropDownList('getSelectedIndex');
							return val != -1;
						}
					},{
						input : '#unitPriceAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'blur',
						rule : function(input, commit){
							var val = input.jqxNumberInput('val');
							if(val){
								return true;
							}
							return false;
						}
					},{
						input : '#quantityCostAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'blur',
						rule : function(input, commit){
							var val = input.jqxNumberInput('val');
							if(val){
								return true;
							}
							return false;
						}
					},{
						input : '#currencyUomIdAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'blur',
						rule : function(input, commit){
							var val = input.jqxDropDownList('getSelectedIndex');
							return val != -1;
						}
					},{
						input : '#quantityUomIdAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'blur',
						rule : function(input, commit){
							var val = input.jqxDropDownList('getSelectedIndex');
							return val != -1;
						}
					}]
				});
			};
			var bindEvent = function() {
				$("#saveCost").click(function() {
					if (!save()) {
						return;
					}
					form.jqxWindow('close');
				});
				$("#saveCostAndContinue").click(function() {
					save();
					clearForm();
				});

				form.on('close', function() {

				});
			};
			var save = function() {
				if (!form.jqxValidator('validate')) {
					return false;
				}
				var index = $('#marketingCostTypeIdAdd').jqxDropDownList('getSelectedItem');
				var ct = index ? index.value : "";
				index = $('#currencyUomIdAdd').jqxDropDownList('getSelectedItem');
				var uom = index ? index.value : "";
				index = $('#quantityUomIdAdd').jqxDropDownList('getSelectedItem');
				var qu = index ? index.value : "";

				var row = {
					marketingCostTypeId : ct,
					description : $('#descriptionAdd').val(),
					unitPrice : $('#unitPriceAdd').jqxNumberInput('val'),
					currencyUomId : uom,
					quantity : $('#quantityCostAdd').jqxNumberInput('val'),
					quantityUomId : qu,
				};

				grid.jqxGrid('addRow', null, row, "last");
				grid.jqxGrid('clearSelection');
				// grid.jqxGrid('selectRow', 0);
				return true;
			};
			var quickAddCost = function(){
				grid.jqxGrid('addRow', null, {}, "last");
				grid.jqxGrid('clearSelection');
			};
			var clearForm = function() {
				Grid.clearForm(form);
			};

			return {
				init : function() {
					initElement();
					initWindow();
					bindEvent();
					initRules();
				},
				quickAddCost: quickAddCost
			};
		}());

	$(document).ready(function() {
		MKCost.init();
	});
</script>