<div id="alterpopupNewRootCategory" style="display:none">
	<div>${uiLabelMap.AddProductRootCategory}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class="required">${uiLabelMap.DmsCategoryId}</label>
						</div>
						<div class='span8'>
							<input type="text" id="wn_prc_productCategoryId" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class="required">${uiLabelMap.DmsCategoryName}</label>
						</div>
						<div class='span8'>
							<input type="text" id="wn_prc_categoryName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label>${uiLabelMap.DmsDescription}</label>
						</div>
						<div class='span8'>
							<textarea id="wn_prc_description" rows="3" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_prc_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_prc_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#assign allCategories = delegator.findList("ProductCategory", null, null, null, null, false) />
<script>
	var listCategoryIds = [
		<#if allCategories?exists>
			<#list allCategories as item>
				"${item.productCategoryId?if_exists}".toLowerCase(),
			</#list>
		</#if>
	];
	$(function(){
		OlbProdCategoryNewRootCate.init();
	});
	var OlbProdCategoryNewRootCate = (function(){
		var init = function(){
			initElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupNewRootCategory"), {width: 460, height: 280, cancelButton: $("#wn_prc_alterCancel")});
		};
		var initEvent = function(){
			$("#wn_prc_alterSave").click(function () {
				if (!$("#alterpopupNewRootCategory").jqxValidator("validate")) {
					return false;
				}
				
				var dataMap = {
					productCategoryId: $("#wn_prc_productCategoryId").val(),
					productCategoryTypeId: "CATALOG_CATEGORY",
					categoryName: $("#wn_prc_categoryName").val(),
					longDescription: $("#wn_prc_description").val()
				};
				$.ajax({
					type: 'POST',
					url: "addRootCategory",
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	if (data && data.productCategoryId) {
						        		$("#alterpopupNewRootCategory").jqxWindow("close");
						        		cleanWindow();
						        		$("body").trigger("createRootCategoryComplete", [data.productCategoryId]);
						        	}
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
			var extendRules = [
				{input: "#wn_prc_productCategoryId", message: "${uiLabelMap.CategoryIdAlreadyExists}", action: "keyup, blur",
					rule: function (input, commit) {
						var value = input.val().toLowerCase();
						if (_.indexOf(listCategoryIds, value) === -1) {
							return true;
						}
						return false;
					}
				},
			];
			var mapRules = [
				{input: '#wn_prc_productCategoryId', type: 'validCannotSpecialCharactor'},
				{input: '#wn_prc_productCategoryId', type: 'validInputNotNull'},
				{input: '#wn_prc_categoryName', type: 'validInputNotNull'},
			];
			new OlbValidator($("#alterpopupNewRootCategory"), mapRules, extendRules, {position: "bottom"});
		};
		var cleanWindow = function(){
			$("#wn_prc_productCategoryId").val("");
			$("#wn_prc_categoryName").val("");
			$("#wn_prc_description").val("");
		};
		var open = function(){
			$("#alterpopupNewRootCategory").jqxWindow('open');
		};
		return {
			init: init,
			open: open,
		};
	}());
</script>