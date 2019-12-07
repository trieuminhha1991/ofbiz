<#assign hasPermNewRootCategory = false/>
<#if !productCategory?exists>
	<#assign hasPermNewRootCategory = true/>
</#if>

<div id="alterpopupWindowNewCategory" style="display:none">
	<div>${uiLabelMap.BSAddNewProductCategory}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class="required">${uiLabelMap.DmsCategoryId}</label>
						</div>
						<div class='span8'>
							<input type="text" id="wn_pc_productCategoryId" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class="required">${uiLabelMap.DmsCategoryName}</label>
						</div>
						<div class='span8'>
							<input type="text" id="wn_pc_categoryName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class="required">${uiLabelMap.BSParentCategory}</label>
						</div>
						<div class='span8'>
							<div id="wn_pc_primaryParentCategoryId">
								<div id="wn_pc_productCategoryGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label>${uiLabelMap.DmsDescription}</label>
						</div>
						<div class='span8'>
							<textarea id="wn_pc_description" rows="5" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
			<#if hasPermNewRootCategory>
				<span class="tooltip" style="opacity:1;margin-top:20px"><a href="javascript:void(0);" onclick="OlbProdCategoryNewRootCate.open()"><i>${uiLabelMap.BEAddNewRootCategory}</i></a></span>
			</#if>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_pc_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_pc_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#if hasPermNewRootCategory>
<#include "productCategoryNewRootCategoryPopup.ftl"/>
</#if>

<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<script type="text/javascript">
	$(function(){
		OlbProdCategoryNewPopup.init();
	});
	var OlbProdCategoryNewPopup = (function(){
		var productCategoryIdToDDB;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowNewCategory"), {width: 560, height: 360, cancelButton: $("#wn_pc_alterCancel")});
			jOlbUtil.input.create($("#wn_pc_productCategoryId"), {width: '93%'});
			jOlbUtil.input.create($("#wn_pc_categoryName"), {width: '93%'});
		};
		var initElementComplex = function(){
			var configProductCategoryTo = {
				widthButton: '99%',
				width: '500px',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				searchId: 'productCategoryId',
				datafields: [
					{name: 'productCategoryId', type: 'string'},
					{name: 'primaryParentCategoryId', type: 'string'},
					{name: 'categoryName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'productCategoryId', width: '40%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}', datafield: 'categoryName'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListProductCategory&pagesize=0&productCategoryTypeId=CATALOG_CATEGORY',
				useUtilFunc: false,
				key: 'productCategoryId',
				description: ['categoryName'],
				parentKeyId: 'primaryParentCategoryId',
				gridType: 'jqxTreeGrid',
				autoCloseDropDown: true,
			};
			productCategoryIdToDDB = new OlbDropDownButton($("#wn_pc_primaryParentCategoryId"), $("#wn_pc_productCategoryGrid"), null, configProductCategoryTo, []);
		};
		var initEvent = function(){
			$("body").on("createRootCategoryComplete", function(event){
				productCategoryIdToDDB.getGridObj().jqxTreeGrid('updateBoundData');
			});
			$("#wn_pc_alterSave").on("click", function(){
				if (!$("#alterpopupWindowNewCategory").jqxValidator("validate")) {
					return false;
				}
				var dataMap = {
					productCategoryId: $("#wn_pc_productCategoryId").val(),
					productCategoryTypeId: "CATALOG_CATEGORY",
					categoryName: $("#wn_pc_categoryName").val(),
					primaryParentCategoryId: productCategoryIdToDDB.getValue(),
					longDescription: $("#wn_pc_description").val()
				};
				<#if security.hasEntityPermission("ECOMMERCE", "_CREATE", session)>
					var urlCreate = "createProductCategoryWithWebSite";
					<#else>
					var urlCreate = "createProductCategoryAjax";
				</#if>
				$.ajax({
					type: 'POST',
					url: urlCreate,
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'error'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	$("#alterpopupWindowNewCategory").jqxWindow("close");
						        	resetWindow();
						        	if ($("#jqxListCategory").length > 0) {
						        		$("#jqxListCategory").jqxTreeGrid("updateBoundData");
						        	}
						        	//$("body").trigger("createRootCategoryComplete");
								}
						);
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
			<#--
			var extendRules = [
				{ input: "#wn_pc_productCategoryId", message: "${uiLabelMap.CategoryIdAlreadyExists}", action: "change",
					rule: function (input, commit) {
						var value = input.val();
						if (value) {
							var check = DataAccess.getData({
								url: "checkProductCategoryId",
								data: {productCategoryId: value},
								source: "check"});
							if ("false" == check) {
								 return false;
							}
						}
						return true;
					}
				},
			];
			-->
			var mapRules = [
				{input: '#wn_pc_productCategoryId', type: 'validCannotSpecialCharactor'},
				{input: '#wn_pc_productCategoryId', type: 'validInputNotNull'},
				{input: '#wn_pc_categoryName', type: 'validInputNotNull'},
				{input: '#wn_pc_primaryParentCategoryId', type: 'validObjectNotNull', objType: 'dropDownButton'},
			];
			new OlbValidator($("#alterpopupWindowNewCategory"), mapRules, null, {position: "bottom"});
		};
		var openWindow = function(){
			$("#alterpopupWindowNewCategory").jqxWindow("open");
		};
		var resetWindow = function(){
			$("#wn_pc_productCategoryId").val("");
			$("#wn_pc_categoryName").val("");
			$("#wn_pc_description").val("");
			productCategoryIdToDDB.clearAll();
		};
		var getObj = function(){
			return {
				productCategoryIdToDDB: productCategoryIdToDDB
			}
		};
		return {
			init: init,
			openWindow: openWindow,
			getObj: getObj,
		};
	}());
</script>
