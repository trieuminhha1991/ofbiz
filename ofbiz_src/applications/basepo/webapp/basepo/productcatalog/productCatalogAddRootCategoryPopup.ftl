<div id="alterpopupWindowChangeRootCategory" style="display:none">
	<div>${uiLabelMap.BSChangeRootCategory}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class="row-fluid">
						<div class="span4">
							<label for="we_crc_prodCatalogId">${uiLabelMap.BSCatalog}</label>
						</div>
						<div class="span8">
							<input type="text" id="we_crc_prodCatalogId" class="span12" maxlength="100" value=""/>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span4">
							<label for="we_crc_productCategoryIdFrom">${uiLabelMap.BSFromCategory}</label>
						</div>
						<div class="span8">
							<input type="text" id="we_crc_productCategoryIdFrom" class="span12" maxlength="100" value=""/>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span4">
							<label for="we_crc_productCategoryIdTo">${uiLabelMap.BSToCategory}</label>
						</div>
						<div class="span8">
							<div class="container-add-plus">
								<div id="we_crc_productCategoryIdTo">
									<div id="we_crc_productCategoryGridTo"></div>
								</div>
								<a id="quickAddNewRootCate" tabindex="-1" href="javascript:void(0);" class="add-value"><i class="fa fa-plus"></i></a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<#--
			<span class="tooltip" style="opacity:1;margin-top:20px"><a href="javascript:void(0);" onclick="OlbProdCategoryNewRootCate.open()"><i>${uiLabelMap.BEAddNewRootCategory}</i></a></span>
			-->
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="we_crc_alterSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="we_crc_alterCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<#include "productCategoryNewRootCategoryPopup.ftl"/>

<script type="text/javascript">
	$(function(){
		OlbProdCatalogAddRootCategory.init();
	});
	
	var OlbProdCatalogAddRootCategory = (function(){
		var productCategoryIdToDDB;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#we_crc_prodCatalogId"), {width: "88%", disabled: true});
			jOlbUtil.input.create($("#we_crc_productCategoryIdFrom"), {width: "88%", disabled: true});
			jOlbUtil.windowPopup.create($("#alterpopupWindowChangeRootCategory"), {width: 540, height: 240, cancelButton: $("#we_crc_alterCancel")});
		};
		var initElementComplex = function(){
			var configProductCategoryTo = {
				widthButton: "94%",
				width: "600px",
				dropDownHorizontalAlignment: "left",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				searchId: "productCategoryId",
				datafields: [
					{name: "productCategoryId", type: "string"},
					{name: "categoryName", type: "string"},
				],
				columns: [
					{text: "${StringUtil.wrapString(uiLabelMap.BSCategoryId)}", datafield: "productCategoryId", width: "30%"},
					{text: "${StringUtil.wrapString(uiLabelMap.BSCategoryName)}", datafield: "categoryName"},
				],
				useUrl: true,
				root: "results",
				url: "JQListProductCategoryRoot",
				useUtilFunc: true,
				key: "productCategoryId", 
				description: ["categoryName"],
				//parentKeyId: "parentCategoryId",
				//gridType: "jqxTreeGrid",
				autoCloseDropDown: true,
			};
			productCategoryIdToDDB = new OlbDropDownButton($("#we_crc_productCategoryIdTo"), $("#we_crc_productCategoryGridTo"), null, configProductCategoryTo, []);
		};
		var initEvent = function(){
			$("body").on("createRootCategoryComplete", function(event, productCategoryId){
				//productCategoryIdToDDB.getGridObj().jqxTreeGrid("updateBoundData");
				productCategoryIdToDDB.getGrid().updateBoundData();
				productCategoryIdToDDB.getGrid().bindingCompleteListener(function(){
					productCategoryIdToDDB.selectItem([productCategoryId]);
				}, true);
			});
			$("#we_crc_alterSave").on("click", function(){
				jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToUpdate}", function(){
					$.ajax({
						type: "POST",
						url: "changeRootCategory",
						data: {
							"prodCatalogId": $("#we_crc_prodCatalogId").val(),
							"productCategoryIdFrom": $("#we_crc_productCategoryIdFrom").val(),
							"productCategoryIdTo": productCategoryIdToDDB.getValue(),
						},
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
								
								$("#alterpopupWindowChangeRootCategory").jqxWindow("close");
								var prodCatalogId = $("#we_crc_prodCatalogId").val();
								var categoryGridObj = $("#jqxListCategory" + prodCatalogId);
								if (categoryGridObj.length > 0) {
									$(categoryGridObj).jqxGrid("updatebounddata");
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
			});
			$("#quickAddNewRootCate").on("click", function(){
				OlbProdCategoryNewRootCate.open();
			});
		};
		return {
			init: init
		};
	}());
</script>
