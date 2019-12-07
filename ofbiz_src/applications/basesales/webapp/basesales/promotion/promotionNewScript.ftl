<#assign isEditSpecial = true/>
<#--
<#if productPromo?exists>
	<#assign currentStatusId = productPromo.statusId!/>
	<#if ("PROMO_CANCELLED" != productPromo.statusId) && ((!(productPromo.thruDate?exists) || productPromo.thruDate?exists && productPromo.thruDate &gt; nowTimestamp))>
		<#if currentStatusId?exists && (hasOlbPermission("MODULE", "PRODPROMOTION_APPROVE", "") && currentStatusId == "PROMO_CREATED")>
			<#assign isEdit = true>
		</#if>
		<#if currentStatusId?exists && (hasOlbPermission("MODULE", "PRODPROMOTION_SPEC_APPROVE", "") && (currentStatusId == "PROMO_ACCEPTED" || currentStatusId == "PROMO_MODIFIED"))>
			<#assign isEditSpecial = true/>
		</#if>
	</#if>
</#if>
-->

<#assign currentCatalogId = Static["com.olbius.basesales.util.SalesUtil"].getProductCatalogDefault(delegator)!/>
<#assign inputParamEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_IN_PARAM"}, null, false)!/>
<#assign periodTypes = delegator.findByAnd("PeriodType", {"groupPeriodTypeId" : "BASIC_PERIOD"}, null, false)!/>
<#assign dayEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "WEEKDAYS"}, null, false)!/>
<#assign condOperEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_COND"}, null, false)!/>
<#assign actionParamEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_ACTION"}, null, false)!/>
<#assign actionOperEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_ACTION_OP"}, null, false)!/>
<script type="text/javascript">
	var periodTypes = [
	<#if periodTypes?exists>
		<#list periodTypes as item>
		{	periodTypeId: '${item.periodTypeId}',
			description: '${item.description}',
		},
		</#list>
	</#if>
	]; 
	
	var dayEnums = [
	<#if dayEnums?exists>
		<#list dayEnums as item>
		{	enumId: '${item.enumId}',
			description: '${item.description}',
		},
		</#list>
	</#if>
	];

	var ruleCount = 0;
	<#if listPromoRule?exists && listPromoRule?size &gt; 0>
		ruleCount = ${listPromoRule?size - 1};
	</#if>
	var inputParamEnumData = [
	<#if inputParamEnums?exists>
		<#list inputParamEnums as item>
		{	enumId: '${item.enumId}',
			description: '${StringUtil.wrapString(item.get("description", locale)?default(""))}'
		},
		</#list>
	</#if>
	];
	var actionParamEnumData = [
	<#if actionParamEnums?exists>
		<#list actionParamEnums as item>
		{	enumId: '${item.enumId}',
			description: '${StringUtil.wrapString(item.get("description", locale)?default(""))}'
		},
		</#list>
	</#if>
	];
	var actionOperEnumsData = [
	<#if actionOperEnums?exists>
		<#list actionOperEnums as item>
		{	enumId: '${item.enumId}',
			description: '${StringUtil.wrapString(item.get("description", locale)?default(""))}'
		},
		</#list>
	</#if>
	];
	var actionIsEnumsData = [
		{enumId: "N", description: "${StringUtil.wrapString(uiLabelMap.BSNo)}"},
		{enumId: "Y", description: "${StringUtil.wrapString(uiLabelMap.BSYes)}"}
	];
	
	var condOperEnumData = new Array();
	var condOperEnumDataEqual = new Array();
	<#list condOperEnums as item>
		<#assign isEqual = false/>
		<#assign isNotIn = false/>
		var row = {};
		row['enumId'] = '${item.enumId}';
		var description = "";
		<#if "PPC_EQ" == item.enumId>
			description = "=";
			<#assign isEqual = true/>
		<#elseif "PPC_GT" == item.enumId>
			description = ">";
		<#elseif "PPC_GTE" == item.enumId>
			description = ">=";
		<#elseif "PPC_LT" == item.enumId>
			description = "<";
		<#elseif "PPC_LTE" == item.enumId>
			description = "<=";
		<#elseif "PPC_NEQ" == item.enumId>
			description = "!=";
		<#elseif "PPC_NIN" == item.enumId>
			description = "&notin;";
			<#assign isNotIn = true/>
		</#if>
		row['description'] = description;
		<#if !isNotIn || (isEditSpecial?exists && isEditSpecial && isNotIn)>condOperEnumData.push(row);</#if>
		<#if isEqual || (isEditSpecial?exists && isEditSpecial && isNotIn)>condOperEnumDataEqual.push(row);</#if>
	</#list>
	
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BSEnterToSearch = "${StringUtil.wrapString(uiLabelMap.BSEnterToSearch)}";
	uiLabelMap.BSPartyId = "${StringUtil.wrapString(uiLabelMap.BSPartyId)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	uiLabelMap.BSRoleTypeId = "${StringUtil.wrapString(uiLabelMap.BSRoleTypeId)}";
	uiLabelMap.BSDescription = "${StringUtil.wrapString(uiLabelMap.BSDescription)}";
	uiLabelMap.BSRule = "${uiLabelMap.BSRule}";
	uiLabelMap.BSAddCondition = "${uiLabelMap.BSAddCondition}";
	uiLabelMap.BSAddAction = "${uiLabelMap.BSAddAction}";
	uiLabelMap.BSCondition = "${uiLabelMap.BSCondition}";
	uiLabelMap.BSAction = "${uiLabelMap.BSAction}";
	uiLabelMap.BSProductName = "${uiLabelMap.BSProductName}";
	uiLabelMap.BSCategoryName = "${uiLabelMap.BSCategoryName}";
	uiLabelMap.BSParty = "${uiLabelMap.BSParty}";
	uiLabelMap.BSPartyGroup = "${uiLabelMap.BSPartyGroup}";
	uiLabelMap.BSRoleType = "${uiLabelMap.BSRoleType}";
	uiLabelMap.ProductConditionValue = "${uiLabelMap.ProductConditionValue}";
	uiLabelMap.ProductQuantity = "${uiLabelMap.ProductQuantity}";
	uiLabelMap.BSAmountOrPercent = "${uiLabelMap.BSAmountOrPercent}";
	uiLabelMap.BSOperator = "${uiLabelMap.BSOperator}";
	uiLabelMap.BSUsePriceWithTax = "${uiLabelMap.BSUsePriceWithTax}";
	uiLabelMap.BSCategoryName = "${uiLabelMap.BSCategoryName}";
	uiLabelMap.BSCheckInventoryItem = "${uiLabelMap.BSCheckInventoryItem}";
	uiLabelMap.BSRecurrenceValue = "${uiLabelMap.BSRecurrenceValue}";
	uiLabelMap.BSRecurrencyInfoId = "${uiLabelMap.BSRecurrencyInfoId}";
	uiLabelMap.BSFrequency = "${uiLabelMap.BSFrequency}";
	uiLabelMap.BSHourList = "${uiLabelMap.BSHourList}";
	uiLabelMap.BSDayList = "${uiLabelMap.BSDayList}";

	$(function(){
		OlbPromoRulesScript.init();
	});
	var OlbPromoRulesScript = (function(){
		var validatorEditRuleNameVAL;
		var productGRID;
		var categoryGRID;
		var productDataMap = {};
		var categoryDataMap = {};
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initElementWindow();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#windowPromoRuleNameEdit"), {width: 380, height: 220, cancelButton: $("#we_rn_alterCancel")});
			validatorEditRuleNameVAL = OlbPromoRules.initValidatorEditRuleName();
		};
		var initElementComplex = function(){
			<#if listPromoRule?exists && listPromoRule?size &gt; 0>
				<#list listPromoRule as promoRule>
					$('#recentTabItem_o_${promoRule_index}').dblclick(function(){
						//OlbPromoRules.showEditorRuleName(this);
						OlbPromoRules.showEditorRuleNameWindow(this);
					});
					<#--
					$('#ruleNameTemp_o_${promoRule_index}').on("blur", function(){
						OlbPromoRules.blurEditorRuleName(this);
					});
					-->
					<#if promoRule.listCond?exists && promoRule.listCond?size &gt; 0>
						<#list promoRule.listCond as promoCond>
							var config = {};
							<#if promoCond.listProd?exists && promoCond.listProd?size &gt; 0>
						    	<#--
						    	var productIdListCondSelected = [
						    	<#list promoCond.listProd as prod>
						    		'${prod.productId}',
						    	</#list>
						    	];
						    	config.productSelectArr = productIdListCondSelected;
						    	-->
						    	var productListCondSelected = [
						    	<#list promoCond.listProd as prod>
						    		<#assign productTemp = delegator.findOne("Product", {"productId": prod.productId}, false)!/>
						    		<#if productTemp?exists>
						    		{	"productId": "${productTemp.productId}",
						    			"productName": "${StringUtil.wrapString(productTemp.productName)}",
						    			"productCode": "${productTemp.productCode}"
						    		},
						    		</#if>
						    	</#list>
						    	];
						    	config.productSelectMap = productListCondSelected;
						    </#if>
						    <#if promoCond.listCate?exists && promoCond.listCate?size &gt; 0>
						    	<#--
						    	var productCatIdListCondSelected = [
						    	<#list promoCond.listCate as category>
						    		'${category.productCategoryId}',
						    	</#list>
						    	];
						    	config.categorySelectArr = productCatIdListCondSelected;
						    	-->
						    	var productCatListCondSelected = [
						    	<#list promoCond.listCate as category>
						    		<#assign categoryTemp = delegator.findOne("ProductCategory", {"productCategoryId": category.productCategoryId}, false)!/>
						    		<#if categoryTemp?exists>
						    		{	"productCategoryId": "${categoryTemp.productCategoryId}",
						    			"categoryName": "${StringUtil.wrapString(categoryTemp.categoryName)}",
						    		},
						    		</#if>
						    	</#list>
						    	];
						    	config.categorySelectMap = productCatListCondSelected;
						    </#if>
							<#if promoCond.usePriceWithTax?exists>
						    	var usePriceWithTaxSelected = ['${promoCond.usePriceWithTax}'];
						    	config.usePriceWithTaxSelectArr = usePriceWithTaxSelected;
						    </#if>
						    <#if promoCond.inputParamEnumId?exists>
						    	var inputParamEnumSelected = ['${promoCond.inputParamEnumId}'];
						    	config.inputParamSelectArr = inputParamEnumSelected;
						    	<#if "PPIP_PARTY_ID" == promoCond.inputParamEnumId>
						    		config.partyIdSelected = ['${promoCond.condValue?if_exists}'];
						    	<#elseif "PPIP_PARTY_GRP_MEM" == promoCond.inputParamEnumId>
						    		config.partyGrpMemIdSelected = ['${promoCond.condValue?if_exists}'];
						    	<#elseif "PPIP_ROLE_TYPE" == promoCond.inputParamEnumId>
						    		config.roleTypeIdSelected = ['${promoCond.condValue?if_exists}'];
						    	</#if>
						    </#if>
						    <#if promoCond.condValue?exists>
						    	config.promoCondCondValue = '${promoCond.condValue}';
						    </#if>
						    <#if promoCond.operatorEnumId?exists>
						    	var operatorEnumIdSelected = ['${promoCond.operatorEnumId}'];
						    	config.operatorSelectArr = operatorEnumIdSelected;
						    </#if>
							OlbPromoRules.actionScriptBuildCond(${promoRule_index}, ${promoCond_index}, config);
						</#list>
					<#else>
						OlbPromoRules.actionScriptBuildCond(${promoRule_index}, 0, {});
					</#if>
					
					<#if promoRule.listAction?exists && promoRule.listAction?size &gt; 0>
						<#list promoRule.listAction as promoAction>
							var config = {};
							<#if promoAction.listProd?exists && promoAction.listProd?size &gt; 0>
						    	<#--var productIdListActionSelected = [
						    	<#list promoAction.listProd as prod>
						    		'${prod.productId}',
						    	</#list>
						    	];
						    	config.productSelectArr = productIdListActionSelected;
						    	-->
								var productListActionSelected = [
						    	<#list promoAction.listProd as prod>
						    		<#assign productTemp = delegator.findOne("Product", {"productId": prod.productId}, false)!/>
						    		<#if productTemp?exists>
						    		{	"productId": "${productTemp.productId}",
						    			"productName": "${StringUtil.wrapString(productTemp.productName)}",
						    			"productCode": "${productTemp.productCode}"
						    		},
						    		</#if>
						    	</#list>
						    	];
						    	config.productSelectMap = productListActionSelected;
						    </#if>
							<#if promoAction.listCate?exists && promoAction.listCate?size &gt; 0>
						    	<#--
						    	var productCatIdListActionSelected = [
						    	<#list promoAction.listCate as category>
						    		'${category.productCategoryId}',
						    	</#list>
						    	];
						    	config.categorySelectArr = productCatIdListActionSelected;
						    	-->
								var productCatListActionSelected = [
						    	<#list promoAction.listCate as category>
						    		<#assign categoryTemp = delegator.findOne("ProductCategory", {"productCategoryId": category.productCategoryId}, false)!/>
						    		<#if categoryTemp?exists>
						    		{	"productCategoryId": "${categoryTemp.productCategoryId}",
						    			"categoryName": "${StringUtil.wrapString(categoryTemp.categoryName)}",
						    		},
						    		</#if>
						    	</#list>
						    	];
						    	config.categorySelectMap = productCatListActionSelected;
						    </#if>
						    <#if promoAction.productPromoActionEnumId?exists>
						    	var productPromoActionEnumIdSelected = ['${promoAction.productPromoActionEnumId}'];
						    	config.promoActionSelectArr = productPromoActionEnumIdSelected;
						    </#if>
						    <#if promoAction.operatorEnumId?exists>
						    	var productPromoActionOperEnumIdSelected = ['${promoAction.operatorEnumId}'];
						    	config.promoActionOperSelectArr = productPromoActionOperEnumIdSelected;
						    </#if>
							<#if promoAction.isCheckInv?exists>
						    	var isCheckInventoryItemSelected = ['${promoAction.isCheckInv}'];
						    	config.isCheckInvSelectArr = isCheckInventoryItemSelected;
						    </#if>
						    <#if promoAction.quantity?exists>config.promoActionQuantity = '${promoAction.quantity}';</#if>
						    <#if promoAction.amount?exists>config.promoActionAmount = '${promoAction.amount}';</#if>
						    OlbPromoRules.actionScriptBuildAction(${promoRule_index}, ${promoAction_index}, config);
						</#list>
					<#else>
						OlbPromoRules.actionScriptBuildAction(${promoRule_index}, 0, {});
					</#if>
				</#list>
			<#else>
				$('#recentTabItem_o_0').dblclick(function(){
					//OlbPromoRules.showEditorRuleName(this);
					OlbPromoRules.showEditorRuleNameWindow(this);
				});
				<#--
				$('#ruleNameTemp_o_0').on("blur", function(){
					OlbPromoRules.blurEditorRuleName(this);
				});
				-->
				OlbPromoRules.actionScriptBuildCond(0, 0, {});
				OlbPromoRules.actionScriptBuildAction(0, 0, {});
			</#if>
			
			$("[id^='productPromoActionEnumId']").on("change", function(){
				OlbPromoRules.checkAndDisplayInputValue($(this));
			});
		};
		
		var initEvent = function(){
			$("#we_rn_alterSave").on("click", function(){
				if (!validatorEditRuleNameVAL.validate()) return false;
				
				var idElement = $("#we_rn_ruleIndex").val();
				var element = $("#" + idElement);
				if (element.length > 0) {
					OlbPromoRules.blurEditorRuleNameWindow(element);
				}
			});
			$("body").on("createRecurrenceInfoComplete", function(){
				$('[id^="recurrenceInfoGridCond"]').each(function(i, obj) {
				    $(obj).jqxGrid("updatebounddata");
				});
			});
		};
		var initElementWindow = function() {
			jOlbUtil.windowPopup.create($("#alterpopupWindowProduct"), {width: 960, height: 450, cancelButton: $("#wn_prod_alterCancel")});
			jOlbUtil.windowPopup.create($("#alterpopupWindowCategory"), {width: 960, height: 450, cancelButton: $("#wn_cate_alterCancel")});
			
			var configGridProduct = {
				datafields: [
					{name: 'productId', type: 'string'}, 
					{name: 'productCode', type: 'string'}, 
					{name: 'productName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '20%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName'},
				],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				pageable: true,
				pagesize: 10,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProductOfCompany',
				groupable: true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'checkbox',
				virtualmode: true,
			};<#--TH2: JQGetListProductSellAll-->
			productGRID = new OlbGrid($("#jqxgridProduct"), null, configGridProduct, []);
			
			var configGridCategory = {
				datafields: [
					{name: 'productCategoryId', type: 'string'}, 
					{name: 'categoryName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', dataField: 'productCategoryId', width: '20%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}', dataField: 'categoryName'},
				],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				pageable: true,
				pagesize: 10,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQListCategoryByCatalog&showAll=Y',
				groupable: true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'checkbox',
				virtualmode: true,
			};
			categoryGRID = new OlbGrid($("#jqxgridCategory"), null, configGridCategory, []);
			
			productGRID.on("bindingComplete", function(){
				productDataMap = {};
			});
			productGRID.on("rowselect", function(event){
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
					for (var i = 0; i < rowBoundIndex.length; i++) {
						processDataRowSelect1(rowBoundIndex[i]);
					}
				} else {
					processDataRowSelect1(rowBoundIndex);
				}
			});
			productGRID.on("rowunselect", function(event){
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				if (rowBoundIndex == -9999) {
					productGRID.clearSelection();
					productDataMap = {};
				} else {
					var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
					if (data) {
						if (productDataMap[data.productId] != null) {
							productDataMap[data.productId] = null;
						}
					}
				}
			});
			var processDataRowSelect1 = function(rowIndex){
				var rowData = $("#jqxgridProduct").jqxGrid("getrowdata", rowIndex);
				if (rowData) {
					var idStr = rowData.productId;
					if (OlbCore.isEmpty(productDataMap[idStr])) {
						productDataMap[idStr] = rowData;
					}
				}
			};
			
			categoryGRID.on("bindingComplete", function(){
				categoryDataMap = {};
			});
			categoryGRID.on("rowselect", function(event){
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
					for (var i = 0; i < rowBoundIndex.length; i++) {
						processDataRowSelect2(rowBoundIndex[i]);
					}
				} else {
					processDataRowSelect2(rowBoundIndex);
				}
			});
			categoryGRID.on("rowunselect", function(event){
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				if (rowBoundIndex == -9999) {
					categoryGRID.clearSelection();
					categoryDataMap = {};
				} else {
					var data = $("#jqxgridProduct").jqxGrid("getrowdata", rowBoundIndex);
					if (data) {
						if (categoryDataMap[data.productCategoryId]) {
							categoryDataMap[data.productCategoryId] = null;
						}
					}
				}
			});
			var processDataRowSelect2 = function(rowIndex){
				var rowData = $("#jqxgridCategory").jqxGrid("getrowdata", rowIndex);
				if (rowData) {
					var idStr = rowData.productCategoryId;
					if (OlbCore.isEmpty(categoryDataMap[idStr])) {
						categoryDataMap[idStr] = rowData;
					}
				}
			};
			
			$("#wn_prod_alterSave").on("click", function(){
				var objIdActive = $("#productObjIdCurrentSelected").val();
				var objActive = $("#" + objIdActive);
				if (objActive.length > 0){
					objActive.jqxComboBox('clearSelection');
					//OlbComboBoxUtil.selectItem(objActive, productDataArr);
					$.each(productDataMap, function(key, rowData){
						var itemNew = {
							"value": rowData.productId,
							"label": rowData.productCode + ' - ' + rowData.productName + ' - ' + rowData.productId
						};
						$(objActive).jqxComboBox("addItem", itemNew);
						OlbComboBoxUtil.selectItem(objActive, [rowData.productId]);
					});
				}
				$("#alterpopupWindowProduct").jqxWindow("close");
			});
			$("#wn_cate_alterSave").on("click", function(){
				var objIdActive = $("#categoryObjIdCurrentSelected").val();
				var objActive = $("#" + objIdActive);
				if (objActive.length > 0){
					objActive.jqxComboBox('clearSelection');
					//OlbComboBoxUtil.selectItem(objActive, categoryDataArr);
					$.each(categoryDataMap, function(key, rowData){
						var itemNew = {
							"value": rowData.productCategoryId,
							"label": rowData.categoryName + "[" + rowData.productCategoryId + "]"
						};
						$(objActive).jqxComboBox("addItem", itemNew);
						OlbComboBoxUtil.selectItem(objActive, [rowData.productCategoryId]);
					});
				}
				$("#alterpopupWindowCategory").jqxWindow("close");
			});
			$('#alterpopupWindowProduct').on('close', function (event) {
				productDataMap = {};
				$("#productObjIdCurrentSelected").val("");
				productGRID.clearSelection();
			});
			$('#alterpopupWindowCategory').on('close', function (event) {
				categoryDataMap = {};
				$("#categoryObjIdCurrentSelected").val("");
				categoryGRID.clearSelection();
			});
		};
		var showWindowProductPromo = function(idComboBox){
			$("#productObjIdCurrentSelected").val(idComboBox);
			$("#alterpopupWindowProduct").jqxWindow("open");
		};
		var showWindowCategoryPromo = function(idComboBox){
			$("#categoryObjIdCurrentSelected").val(idComboBox);
			$("#alterpopupWindowCategory").jqxWindow("open");
		};
		return {
			init: init,
			showWindowProductPromo: showWindowProductPromo,
			showWindowCategoryPromo: showWindowCategoryPromo,
		};
	}());
</script>
<script type="text/javascript" src="/salesresources/js/promotion/promotionNewRule.js?v=0.0.1"></script>