<div id="popupAddProduct" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="HistoryForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.DAProductId}</label>
						<div class="span7 margin-bottom10">
							<div id="productIdAdd">
								<div id="jqxGridProduct"></div>
							</div>
						</div>
					</div>

					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.ProductType}</label>
						<div class="span7 margin-bottom10">
							<div id="productTypeIdAdd"></div>
						</div>
					</div>

					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.quantity}</label>
						<div class="span7 margin-bottom10">
							<div id="quantityProductAdd"></div>
						</div>
					</div>

					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.uomId}</label>
						<div class="span7 margin-bottom10">
							<div id="uomIdAdd"></div>
						</div>
					</div>
					<div class="row-fluid no-left-margin hide">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.CommonPlace}</label>
						<div class="span7 margin-bottom10">
							<div id="marketingPlaceAdd"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="cancelProduct" class='btn btn-danger form-action-button pull-right'>
							<i class='fa fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="saveProductAndContinue" class='btn btn-primary form-action-button pull-right'>
							<i class='fa fa-plus'> </i> ${uiLabelMap.SaveAndContinue}
						</button>
						<button type="button" id="saveProduct" class='btn btn-primary form-action-button pull-right'>
							<i class='fa fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">

	var MKProduct = ( function() {
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			var form = $('#popupAddProduct');
			var width = 200;
			var grid = $("#MarketingProductGrid");
			var initProductSelect = function() {
				var datafields = [{
					name : 'productId',
					type : 'string'
				}, {
					name : 'productName',
					type : 'string'
				}, {
					name : 'productTypeId',
					type : 'string'
				}];
				var columns = [{
					text : '${uiLabelMap.FormFieldTitle_productId}',
					datafield : 'productId',
					width : 150
				}, {
					text : '${uiLabelMap.ProductInternalName}',
					datafield : 'productName'
				}, {
					text : '${uiLabelMap.ProductProductType}',
					datafield : 'productTypeId'
				}];
				Grid.initDropDownButton({
					url : "JQGetListProductsPrices",
					autorowheight : true,
					filterable : true,
					source : {
						cache : true,
						pagesize : 5
					}
				}, datafields, columns, null, $("#jqxGridProduct"), $('#productIdAdd'), "productId");
			};
			var initElement = function() {
				initProductSelect();
				$("#marketingPlaceAdd").jqxComboBox({
					placeHolder : "",
					source : marketingPlace,
					width : width,
					checkboxes: true,
					displayMember : "groupName",
					valueMember : "marketingPlaceId",
					autoDropDownHeight : true
				});
				$("#productTypeIdAdd").jqxDropDownList({
					source : productType,
					width : width,
					autoDropDownHeight: true,
					displayMember : "description",
					valueMember : "productTypeId",
					placeHolder : "${StringUtil.wrapString(uiLabelMap.ChooseProductType?default(''))}"
				});
				$("#uomIdAdd").jqxDropDownList({
					source : quantityUomData,
					width : width,
					dropDownHeight: 200,
					filterable: true,
					displayMember : "description",
					valueMember : "uomId",
					placeHolder : "${StringUtil.wrapString(uiLabelMap.ChooseUom?default(''))}"
				});
				$("#uomIdAdd").val("OTH_ea");
				$("#quantityProductAdd").jqxNumberInput({
					width : width,
					max : 999999999999999999,
					digits : 18,
					decimalDigits : 0,
					spinButtons : false,
					min : 0
				});
			};
			var initWindow = function() {
				form.jqxWindow({
					width : 450,
					height : 250,
					resizable : true,
					isModal : true,
					autoOpen : false,
					cancelButton : $("#cancelProduct"),
					modalOpacity : 0.7
				});
				form.on("close", function() {
					clearForm();
				});
			};
			var initRules = function() {
				form.jqxValidator({
					rules : [{
						input : '#productIdAdd',
						message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						action : 'blur',
						rule : function(input, commit) {
							var value = input.val();
							if (!value)
								return false;
							return true;
						}
					}
					// ,{
						// input : '#productTypeIdAdd',
						// message : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
						// action : 'blur',
						// rule : 'required'
					// }
					]
				});
			};
			var bindEvent = function() {
				$("#saveProduct").click(function() {
					if (!save()) {
						return;
					}
					form.jqxWindow('close');
				});
				$("#saveProductAndContinue").click(function() {
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
				var index = $('#uomIdAdd').jqxDropDownList('getSelectedItem');
				var uom = index ? index.value : "";
				index = $('#productTypeIdAdd').jqxDropDownList('getSelectedItem');
				var pt = index ? index.value : "";
				index = $('#marketingPlaceAdd').jqxDropDownList('getSelectedItem');
				var mp = index ? index.value : "";
				var row = {
					productId : $('#productIdAdd').val(),
					marketingPlaceId : mp,
					productTypeId : pt,
					uomId : uom,
					quantity : $('#quantityProductAdd').jqxNumberInput('val'),
				};
				grid.jqxGrid('addRow', null, row, "last");
				grid.jqxGrid('clearSelection');
				return true;
			};
			var quickAddProduct = function(){
				grid.jqxGrid('addRow', null, {}, "last");
				grid.jqxGrid('clearSelection');
			};
			var showPopupPO = function(){
				var obj = $('#popupAddPO');
				if(obj.length){
					obj.jqxWindow('open');
				}
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
				quickAddProduct : quickAddProduct,
				showPopupPO: showPopupPO
			};
		}());

	$(document).ready(function() {
		MKProduct.init();
	});
</script>