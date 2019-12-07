<#assign currentCatalogId = Static["com.olbius.basesales.util.SalesUtil"].getProductCatalogDefault(delegator)!/>
<#assign inputParamEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROMO_EXT_IN_PARAM"}, null, false)!/>
<#assign condOperEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_COND"}, null, false)!/>
<#assign actionParamEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_ACTION"}, null, false)!/>
<#assign actionOperEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "PROD_PROMO_ACTION_OP"}, null, false)!/>
<#assign condParamEnumAssocs = delegator.findList("ProductPromoExtTypeEnumAssoc", Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr(), null, null, null, false)!/>
<@jqOlbCoreLib />
<script type="text/javascript">
	var ruleCount = 0;
	<#if listPromoRule?exists && listPromoRule?size &gt; 0>
		ruleCount = ${listPromoRule?size - 1};
	</#if>
	var condParamEnumAssocsData = [
	<#if condParamEnumAssocs?exists>
		<#list condParamEnumAssocs as item>
		{	productPromoTypeId: '${item.productPromoTypeId}',
			enumId: '${item.enumId}',
		},
		</#list>
	</#if>
	];
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
		</#if>
		row['description'] = description;
		condOperEnumData.push(row);
		<#if isEqual>condOperEnumDataEqual.push(row);</#if>
	</#list>

	var condOperEnumDataActive = {};
    if (condParamEnumAssocsData) {
    	$.each(condParamEnumAssocsData, function(key, value){
    		var productPromoTypeId = value.productPromoTypeId;
    		var enumId = value.enumId;
    		$.each(inputParamEnumData, function(key, value){
    			if (enumId == value.enumId) {
    				if (condOperEnumDataActive[productPromoTypeId]) {
		    			var itemArr = condOperEnumDataActive[productPromoTypeId];
		    			itemArr.push(value);
		    		} else {
		    			condOperEnumDataActive[productPromoTypeId] = [value];
		    		}
    			}
    		});
    	});
    }
	
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
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

	$(function(){
		OlbPageRules.init();
	});
	var OlbPageRules = (function(){
		var validatorEditRuleNameVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
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
						    	var productIdListCondSelected = [
						    	<#list promoCond.listProd as prod>
						    		'${prod.productId}',
						    	</#list>
						    	];
						    	config.productSelectArr = productIdListCondSelected;
						    </#if>
						    <#if promoCond.listCate?exists && promoCond.listCate?size &gt; 0>
						    	var productCatIdListCondSelected = [
						    	<#list promoCond.listCate as category>
						    		'${category.productCategoryId}',
						    	</#list>
						    	];
						    	config.categorySelectArr = productCatIdListCondSelected;
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
							var config;
							<#if promoAction.listProd?exists && promoAction.listProd?size &gt; 0>
						    	var productIdListActionSelected = [
						    	<#list promoAction.listProd as prod>
						    		'${prod.productId}',
						    	</#list>
						    	];
						    	config.productSelectArr = productIdListActionSelected;
						    </#if>
							<#if promoAction.listCate?exists && promoAction.listCate?size &gt; 0>
						    	var productCatIdListActionSelected = [
						    	<#list promoAction.listCate as category>
						    		'${category.productCategoryId}',
						    	</#list>
						    	];
						    	config.categorySelectArr = productCatIdListActionSelected;
						    </#if>
						    <#if promoAction.productPromoActionEnumId?exists>
						    	var productPromoActionEnumIdSelected = ['${promoAction.productPromoActionEnumId}'];
						    	config.promoActionSelectArr = productPromoActionEnumIdSelected;
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
			
			$("#productPromoTypeId").on("change", function(){
				var listInputParamCond = $("div[id^=inputParamEnumId_]");
				if (listInputParamCond.length > 0) {
					// check and process promotion ext type enumeration association
				    var inputParamEnumDataNew = [];
				    var productPromoTypeId = $("#productPromoTypeId").val();
				    if (OlbCore.isEmpty(productPromoTypeId)) {
				    	productPromoTypeId = "PROMO_EXHIBITION";
				    }
				    if (condOperEnumDataActive[productPromoTypeId]) {
				    	inputParamEnumDataNew = condOperEnumDataActive[productPromoTypeId]
				    }
				    
				    var lengthObj = listInputParamCond.length;
				    for (var i = 0; i < lengthObj; i++) {
				    	var obj = listInputParamCond[i];
				    	if (obj) {
				    		var objId = $(obj).attr("id");
				    		var tmpSourceOperator = $("#" + objId).jqxComboBox('source');
				    		tmpSourceOperator._source.localdata = inputParamEnumDataNew;
							$("#" + objId).jqxComboBox('clearSelection');
							$("#" + objId).jqxComboBox('source', tmpSourceOperator);
							//$("#" + objId).jqxComboBox('selectedIndex', 0);
				    	}
				    }
				}
			});
		};
		return {
			init: init
		};
	}());
</script>
<script type="text/javascript" src="/salesmtlresources/js/product/promotionExtNewRule.js"></script>