<div id="ProductFormWindow">
	<div class="form-window-container">
		<div class="form-window-content">
			<div id="productNotify" style="position: absolute; right: 0">&nbsp;</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.DACustomerId}</label>
						</div>
						<div class="span7">
							<div id="ClaimPartyId"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.CurrentBrandUsing}</label>
						</div>
						<div class="span7">
							<div id="CurrentBrandUsing" class="brandUsing"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label>${uiLabelMap.CurrentProductUsing}</label>
						</div>
						<div class="span7">
							<div id="CurrentProductUsing" class="productUsing"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.PreviousBrandUsing}</label>
						</div>
						<div class="span7">
							<div id="PreviousBrandUsing" class="brandUsing"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label>${uiLabelMap.PreviousProductUsing}</label>
						</div>
						<div class="span7">
							<div id="PreviousProductUsing" class="productUsing"></div>
						</div>
					</div>
				</div>
			</div>
			<div style="width: 99%;height: 33px;">
				<button id="cancelCreateProduct" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="createProduct" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<script>
	var ProductUsing = (function(){
		var width = "95%";
		var issueForm;
		var initCurrentBrand = function(width, brand, product){
			brand.jqxDropDownList({
				theme: theme,
				source: brands,
				displayMember: "groupName",
				valueMember: "partyId",
				dropDownHeight: 250,
				width: width,
				height: "25",
				filterable: true,
				placeHolder: multiLang.filterchoosestring
			});
			product.jqxDropDownList({
				theme: theme,
				source: [],
				displayMember: "productName",
				valueMember: "productId",
				dropDownHeight: 250,
				dropDownWidth: 350,
				width: width,
				height: "25",
				filterable: true,
				renderer: function(index, label, value){
					var str = "<div><b>[" + value + "] </b>" + label + "</div>";
					return str;
				},
				placeHolder: multiLang.filterchoosestring
			});
			brand.on("change", function(event){
				if (event.args) {
					var item = event.args.item;
					var partyId = item.value;
					getProductBySupplier(partyId, product);
				}
			});
		};
		var getProductBySupplier = function(partyId, element){
			$.ajax({
				url: "getProductBySupplierClaim",
				type: "POST",
				data: {partyId : partyId},
				dataType: "json",
				success: function(data) {
					var results = data["results"];
					if (!results || !results.length){
						element.jqxDropDownList({source: []});
					} else {
						var source = {
							localdata: results,
							datatype: "array"
						};
						var dataAdapter = new $.jqx.dataAdapter(source);
						element.jqxDropDownList({source: dataAdapter});
						if(currentProductId){
							element.jqxDropDownList("val", currentProductId);
						}
					}
				}
			});
		};
		var bindEvent = function(){
			$("#createProduct").click(function() {
				if(!issueForm.jqxValidator("validate")){
					return;
				}
				var notify = $("#productNotify");
				var row = {
					partyId: $("#ClaimPartyId").val(),
					currentBrandId: $("#CurrentBrandUsing").val(),
					currentProductId: $("#CurrentProductUsing").val(),
					previousProductId: $("#PreviousProductUsing").val(),
					previousBrandId: $("#PreviousBrandUsing").val()
				};
				$.ajax({
					url: "createProductUsingHistory",
					data: row,
					type: "POST",
					success: function(res){
						if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
							notify.notify(multiLang.CreateError, { position: "left", className: "error" });
						} else {
							notify.notify(multiLang.CreateSuccessfully, { position: "left", className: "success" });
							Family.updateGridFamily();
							clearForm();
						}
					}
				});
			});
			$("#cancelCreateProduct").click(function(){
				clearForm();
			});
		};
		var clearForm = function() {
			$("#CurrentBrandUsing").jqxDropDownList("clearSelection");
			$("#PreviousBrandUsing").jqxDropDownList("clearSelection");
			$("#CurrentProductUsing").jqxDropDownList("clearSelection");
			$("#PreviousProductUsing").jqxDropDownList("clearSelection");
		};
		var initIssueFormRule = function(){
			issueForm.jqxValidator({
				position: "left",
				scroll: false,
				rules: [
					{input: "#ClaimPartyId", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					},
					{input: "#CurrentBrandUsing", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					},
					{input: "#PreviousBrandUsing", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					}
				]
			});
		};
		var init = function() {
			issueForm = $("#ProductFormWindow");
			initIssueFormRule();
			bindEvent();
			initCurrentBrand(width, $("#CurrentBrandUsing"), $("#CurrentProductUsing"));
			initCurrentBrand(width, $("#PreviousBrandUsing"), $("#PreviousProductUsing"));
		};
		return {
			init: init
		};
	})();
	$(document).ready(function(){
		ProductUsing.init();
	});
</script>