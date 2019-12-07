<#assign currentCatalogId = Static["com.olbius.basesales.util.SalesUtil"].getProductCatalogDefault(delegator)!/>
<#assign inputParamEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "LOYALTY_IN_PARAM"}, null, false)!/>
<#assign condOperEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "LOYALTY_COND"}, null, false)!/>
<#assign actionParamEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "LOYALTY_ACTION"}, null, false)!/>
<#assign actionOperEnums = delegator.findByAnd("Enumeration", {"enumTypeId" : "LOYALTY_ACTION_OP"}, null, false)!/>
<#assign partyClassificationGroup = delegator.findByAnd("PartyClassificationGroup", {"partyClassificationTypeId" : "LOYALTY_POINT_CLASS"}, null, false)!/>
<script type="text/javascript">
	var ruleCount = 0;
	<#if listLoyaltyRule?exists && listLoyaltyRule?size &gt; 0>
		ruleCount = ${listLoyaltyRule?size - 1};
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
	var ratingTypeData = [
   	<#if partyClassificationGroup?exists>
   		<#list partyClassificationGroup as item>
   		{	enumId: '${item.partyClassificationGroupId}',
   			description: '[${item.partyClassificationGroupId}] ${StringUtil.wrapString(item.description)}'
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
		<#if "LPC_EQ" == item.enumId>
			description = "=";
			<#assign isEqual = true/>
		<#elseif "LPC_GT" == item.enumId>
			description = ">";
		<#elseif "LPC_GTE" == item.enumId>
			description = ">=";
		<#elseif "LPC_LT" == item.enumId>
			description = "<";
		<#elseif "LPC_LTE" == item.enumId>
			description = "<=";
		<#elseif "LPC_NEQ" == item.enumId>
			description = "!=";
		</#if>
		row['description'] = description;
		condOperEnumData.push(row);
		<#if isEqual>condOperEnumDataEqual.push(row);</#if>
	</#list>
	
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
	uiLabelMap.BSRatingType = "${uiLabelMap.BSRatingType}";
	uiLabelMap.BSParty = "${uiLabelMap.BSParty}";
	uiLabelMap.BSPartyGroup = "${uiLabelMap.BSPartyGroup}";
	uiLabelMap.BSRoleType = "${uiLabelMap.BSRoleType}";
	uiLabelMap.ProductConditionValue = "${uiLabelMap.ProductConditionValue}";
	uiLabelMap.ProductQuantity = "${uiLabelMap.ProductQuantity}";
	uiLabelMap.BSValue = "${uiLabelMap.BSValue}";
	uiLabelMap.BSAmountOrPercent = "${uiLabelMap.BSAmountOrPercent}";
	uiLabelMap.BSOperator = "${uiLabelMap.BSOperator}";
	uiLabelMap.BSUsePriceWithTax = "${uiLabelMap.BSUsePriceWithTax}";
	uiLabelMap.BSIsReturnOrder = "${uiLabelMap.BSIsReturnOrder}";
	uiLabelMap.BSCategoryName = "${uiLabelMap.BSCategoryName}";
	uiLabelMap.BSCheckInventoryItem = "${uiLabelMap.BSCheckInventoryItem}";

	$(function(){
		OlbPageRules.init();
	});
	var OlbPageRules = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			<#if listLoyaltyRule?exists && listLoyaltyRule?size &gt; 0>
				<#list listLoyaltyRule as loyaltyRule>
					$('#recentTabItem_o_${loyaltyRule_index}').dblclick(function(){
						OlbLoyaltyRules.showEditorRuleName(this);
					});
					$('#ruleNameTemp_o_${loyaltyRule_index}').on("blur", function(){
						OlbLoyaltyRules.blurEditorRuleName(this);
					});
					<#if loyaltyRule.listCond?exists && loyaltyRule.listCond?size &gt; 0>
						<#list loyaltyRule.listCond as loyaltyCond>
							var config = {};
							<#if loyaltyCond.listProd?exists && loyaltyCond.listProd?size &gt; 0>
						    	var productIdListCondSelected = [
						    	<#list loyaltyCond.listProd as prod>
						    		'${prod.productId}',
						    	</#list>
						    	];
						    	config.productSelectArr = productIdListCondSelected;
						    </#if>
						    <#if loyaltyCond.listCate?exists && loyaltyCond.listCate?size &gt; 0>
						    	var productCatIdListCondSelected = [
						    	<#list loyaltyCond.listCate as category>
						    		'${category.productCategoryId}',
						    	</#list>
						    	];
						    	config.categorySelectArr = productCatIdListCondSelected;
						    </#if>
							<#if loyaltyCond.usePriceWithTax?exists>
						    	var usePriceWithTaxSelected = ['${loyaltyCond.usePriceWithTax}'];
						    	config.usePriceWithTaxSelectArr = usePriceWithTaxSelected;
						    </#if>
						    <#if loyaltyCond.isReturnOrder?exists>
						    	var isReturnOrderSelected = ['${loyaltyCond.isReturnOrder}'];
						    	config.isReturnOrderSelectArr = isReturnOrderSelected;
					    	</#if>
						    <#if loyaltyCond.inputParamEnumId?exists>
						    	var inputParamEnumSelected = ['${loyaltyCond.inputParamEnumId}'];
						    	config.inputParamSelectArr = inputParamEnumSelected;
						    	<#if "LPIP_PARTY_ID" == loyaltyCond.inputParamEnumId>
						    		config.partyIdSelected = ['${loyaltyCond.condValue?if_exists}'];
						    	<#elseif "LPIP_PARTY_GRP_MEM" == loyaltyCond.inputParamEnumId>
						    		config.partyGrpMemIdSelected = ['${loyaltyCond.condValue?if_exists}'];
						    	<#elseif "LPIP_ROLE_TYPE" == loyaltyCond.inputParamEnumId>
						    		config.roleTypeIdSelected = ['${loyaltyCond.condValue?if_exists}'];
						    	</#if>
						    </#if>
						    <#if loyaltyCond.condValue?exists>
						    	config.loyaltyCondCondValue = '${loyaltyCond.condValue}';
						    </#if>
						    <#if loyaltyCond.operatorEnumId?exists>
						    	var operatorEnumIdSelected = ['${loyaltyCond.operatorEnumId}'];
						    	config.operatorSelectArr = operatorEnumIdSelected;
						    </#if>
							OlbLoyaltyRules.actionScriptBuildCond(${loyaltyRule_index}, ${loyaltyCond_index}, config);
						</#list>
					<#else>
						OlbLoyaltyRules.actionScriptBuildCond(${loyaltyRule_index}, 0, {});
					</#if>
					
					<#if loyaltyRule.listAction?exists && loyaltyRule.listAction?size &gt; 0>
						<#list loyaltyRule.listAction as loyaltyAction>
							var config;
							<#if loyaltyAction.listProd?exists && loyaltyAction.listProd?size &gt; 0>
						    	var productIdListActionSelected = [
						    	<#list loyaltyAction.listProd as prod>
						    		'${prod.productId}',
						    	</#list>
						    	];
						    	config.productSelectArr = productIdListActionSelected;
						    </#if>
							<#if loyaltyAction.listCate?exists && loyaltyAction.listCate?size &gt; 0>
						    	var productCatIdListActionSelected = [
						    	<#list loyaltyAction.listCate as category>
						    		'${category.productCategoryId}',
						    	</#list>
						    	];
						    	config.categorySelectArr = productCatIdListActionSelected;
						    </#if>
						    <#if loyaltyAction.loyaltyActionEnumId?exists>
						    	var loyaltyActionEnumIdSelected = ['${loyaltyAction.loyaltyActionEnumId}'];
						    	config.loyaltyActionSelectArr = loyaltyActionEnumIdSelected;
						    </#if>
						    <#if loyaltyAction.operatorEnumId?exists>
						    	var loyaltyActionOperEnumIdSelected = ['${loyaltyAction.operatorEnumId}'];
						    	config.loyaltyActionOperSelectArr = loyaltyActionOperEnumIdSelected;
						    </#if>
							<#if loyaltyAction.isCheckInv?exists>
						    	var isCheckInventoryItemSelected = ['${loyaltyAction.isCheckInv}'];
						    	config.isCheckInvSelectArr = isCheckInventoryItemSelected;
						    </#if> 
						    <#if loyaltyAction.actionValue?exists>
					    		var ratingTypeItemSelected = ['${loyaltyAction.actionValue}'];
					    		config.ratingTypeSelectArr = ratingTypeItemSelected;
					    	</#if>
						    <#if loyaltyAction.quantity?exists>config.loyaltyActionQuantity = '${loyaltyAction.quantity}';</#if>
						    <#if loyaltyAction.amount?exists>config.loyaltyActionAmount = '${loyaltyAction.amount}';</#if>
						    OlbLoyaltyRules.actionScriptBuildAction(${loyaltyRule_index}, ${loyaltyAction_index}, config);
						</#list>
					<#else>
						OlbLoyaltyRules.actionScriptBuildAction(${loyaltyRule_index}, 0, {});
					</#if>
				</#list>
			<#else>
				$('#recentTabItem_o_0').dblclick(function(){
					OlbLoyaltyRules.showEditorRuleName(this);
				});
				$('#ruleNameTemp_o_0').on("blur", function(){
					OlbLoyaltyRules.blurEditorRuleName(this);
				});
				OlbLoyaltyRules.actionScriptBuildCond(0, 0, {});
				OlbLoyaltyRules.actionScriptBuildAction(0, 0, {});
			</#if>
			
			$("[id^='loyaltyActionEnumId']").on("change", function(){
				OlbLoyaltyRules.checkAndDisplayInputValue($(this));
			});
		};
		
		return {
			init: init,
		};
	}());
</script>
<script type="text/javascript" src="/salesresources/js/loyalty/loyaltyNewRule.js"></script>