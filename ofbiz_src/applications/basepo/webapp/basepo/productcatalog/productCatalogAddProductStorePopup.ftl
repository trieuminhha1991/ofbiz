<div id="alterpopupWindowAddProductStore" style="display:none">
	<div>${uiLabelMap.BSAddSalesChannel}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class="row-fluid">
						<div class="span4">
							<label for="we_aps_prodCatalogId">${uiLabelMap.BSCatalog}</label>
						</div>
						<div class="span8">
							<input type="text" id="we_aps_prodCatalogId" class="span12" maxlength="100" value=""/>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span4">
							<label for="we_aps_productStore" class="required">${uiLabelMap.BSPSSalesChannel}</label>
						</div>
						<div class="span8">
							<div id="we_aps_productStore"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="we_aps_alterSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="we_aps_alterCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProdCatalogAddStore.init();
	});
	var OlbProdCatalogAddStore = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#we_aps_prodCatalogId"), {width: "92%", disabled: true});
			jOlbUtil.windowPopup.create($("#alterpopupWindowAddProductStore"), {width: 540, height: 240, cancelButton: $("#we_aps_alterCancel")});
		};
		var initElementComplex = function(){
			var configProductStore = {
				width: "98%",
				placeHolder: "${uiLabelMap.BSClickToChoose}",
				useUrl: true,
				url: "jqxGeneralServicer?sname=JQGetListProductStore",
				key: "productStoreId",
				value: "storeName",
				autoDropDownHeight: true,
				displayDetail: true
			}
			productStoreDDL = new OlbDropDownList($("#we_aps_productStore"), null, configProductStore, []);
		};
		var initEvent = function(){
			$("#we_aps_alterSave").click(function () {
				if (!$("#alterpopupWindowAddProductStore").jqxValidator("validate")) {
					return false;
				}
				var dataMap = {
					prodCatalogId: $("#we_aps_prodCatalogId").val(),
					productStoreId: productStoreDDL.getValue()
				};
				$.ajax({
					type: "POST",
					url: "createProductStoreCatalogAjax",
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
							$("#container").empty();
							$("#jqxNotification").jqxNotification({ template: "info"});
							$("#jqxNotification").html(errorMessage);
							$("#jqxNotification").jqxNotification("open");
							return false;
						}, function(){
							$("#container").empty();
							$("#jqxNotification").jqxNotification({ template: "info"});
							$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							$("#jqxNotification").jqxNotification("open");
							
							$("#alterpopupWindowAddProductStore").jqxWindow("close");
							var prodCatalogId = $("#we_aps_prodCatalogId").val();
							var productStoreGridObj = $("#jqxListProductStore" + prodCatalogId);
							if (productStoreGridObj.length > 0) {
								$(productStoreGridObj).jqxGrid("updatebounddata");
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
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
				{input: "#we_aps_prodCatalogId", type: "validInputNotNull"},
				{input: "#we_aps_productStore", type: "validInputNotNull"},
			];
			new OlbValidator($("#alterpopupWindowAddProductStore"), mapRules, extendRules, {position: "bottom"});
		};
		return {
			init: init
		};
	}());
</script>