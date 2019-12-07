var OlbLoyaltyRules = (function(){
	var closeTab = function(seq_id) {
		if ($("#recent-tab_o_" + seq_id) != undefined && $("#recent-tab_o_" + seq_id).length > 0) {
			$("#recent-tab_o_" + seq_id).remove();
		}
		if ($("#tab-rule_o_" + seq_id) != undefined && $("#tab-rule_o_" + seq_id).length > 0) {
			$("#tab-rule_o_" + seq_id).remove();
		}
		if ($('input[name="isRemoveRule_o_' + seq_id + '"]') != undefined) {
			$('input[name="isRemoveRule_o_' + seq_id + '"]').val("Y");
		}
		

		$("#recent-tab > li").removeClass("active");
		$("#tab-content > div").removeClass("active");
		
		$("#recent-tab > li:first").addClass("active");
		$("#tab-content > div:first").addClass("active");
	};
	var deleteCond = function(rule_seq_id, cond_seq_id) {
		var thisSuffix = "_c_" + cond_seq_id + "_o_" + rule_seq_id;
		if ($('input[name="isRemoveCond' + thisSuffix + '"]') != undefined) {
			$('input[name="isRemoveCond' + thisSuffix + '"]').val("Y");
			$("#form-legend" + thisSuffix).hide();
		}
		$(window).resize();
	};
	var deleteAction = function(rule_seq_id, action_seq_id) {
		var thisSuffix = "_a_" + action_seq_id + "_o_" + rule_seq_id;
		if ($('input[name="isRemoveAction' + thisSuffix + '"]') != undefined) {
			$('input[name="isRemoveAction' + thisSuffix + '"]').val("Y");
			$("#form-legend" + thisSuffix).hide();
		}
		$(window).resize();
	};
	var addNewRule = function(){
		// remove class active
		$("#recent-tab > li").removeClass("active");
		$("#tab-content > div").removeClass("active");
		
		// add new tab
		ruleCount ++;
		var id = "tab-rule_o_" + ruleCount;
		var divTabHeadNew = new StringBuilder();
		var ruleCountStr = '' + ruleCount;
		divTabHeadNew.append("<li class='active' id='recent-tab_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("'>");
		divTabHeadNew.append("<span class='close-tab' onclick='javascript:OlbLoyaltyRules.closeTab(");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append(")'><i class='fa-times-circle open-sans open-sans-index'></i></span>");
		divTabHeadNew.append("<a data-toggle='tab' href='#");
		divTabHeadNew.append(id);
		divTabHeadNew.append("' id='recentTabItem_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("'>");
		divTabHeadNew.append("<span>");
		divTabHeadNew.append(uiLabelMap.BSRule);
		divTabHeadNew.append(" ");
		divTabHeadNew.append('' + (ruleCount + 1));
		divTabHeadNew.append("</span>");
		divTabHeadNew.append("<input type='text' size='30' name='ruleNameTemp_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("' id='ruleNameTemp_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("' value='");
		divTabHeadNew.append(uiLabelMap.BSRule);
		divTabHeadNew.append(" ");
		divTabHeadNew.append('' + (ruleCount + 1));
		divTabHeadNew.append("'/>");
		divTabHeadNew.append("</a>");
		divTabHeadNew.append("</li>");

		$("#recent-tab").append(divTabHeadNew.toString());
		var divTabContentNew = new StringBuilder();
		divTabContentNew.append("<input type='hidden' name='isRemoveRule_o_");
		divTabContentNew.append(ruleCountStr);
		divTabContentNew.append("' value='N'/><div id='");
		divTabContentNew.append(id);
		divTabContentNew.append("' class='tab-pane active'></div><!--#");
		divTabContentNew.append(id);
		divTabContentNew.append("-->");
		$("#tab-content").append(divTabContentNew.toString());
		buildContentInTab(ruleCount, id);
		
		$('#recentTabItem_o_' + ruleCount).dblclick(function(){
			showEditorRuleName(this);
		});
		
		$('#ruleNameTemp_o_' + ruleCount).on("blur", function(){
			blurEditorRuleName(this);
		});
	};
	var buildContentInTab = function(seq_id, div_id){
		// seq_id is sequence id of list tab. ex: 1, 2, 3, ...
		var divContent = $("#" + div_id);
		if (divContent == undefined || divContent.length < 1) return false;
		var content = new StringBuilder();
		var seq_id_str = '' + seq_id;
		content.append('<input type="hidden" size="30" name="ruleName_o_');
		content.append(seq_id_str);
		content.append('" id="ruleName_o_');
		content.append(seq_id_str);
		content.append('" value="');
		content.append(uiLabelMap.BSRule);
		content.append(' ' + (seq_id + 1));
		content.append('"/>');
		divContent.append(content.toString());
		
		var content = new StringBuilder();
		content.append('<div class="row-fuild">');
		content.append('<div class="span6">');
		content.append('<div id="add-new-condition-container_o_');
		content.append(seq_id_str);
		content.append('">');
		content.append('<a href="javascript:OlbLoyaltyRules.addNewCondition(');
		content.append(seq_id_str);
		content.append(', 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>');
		content.append(uiLabelMap.BSAddCondition);
		content.append('</a>');
		content.append('</div>');
		content.append('</div><!--.span6-->');
		content.append('<div class="span6">');
		content.append('<div id="add-new-action-container_o_');
		content.append(seq_id_str);
		content.append('">');
		content.append('<a href="javascript:OlbLoyaltyRules.addNewAction(');
		content.append(seq_id_str);
		content.append(', 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>');
		content.append(uiLabelMap.BSAddAction);
		content.append('</a>');
		content.append('</div>');
		content.append('</div><!--.span6-->');
		content.append('</div>');
		
		divContent.append(content.toString());
		addNewCondition(seq_id, 0);
		addNewAction(seq_id, 0);
	};
	
	var addNewCondition = function(rule_seq_id, cond_seq_id){
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		$("#add-new-condition-container_o_" + rule_seq_id).before('<input type="hidden" name="isRemoveCond_c_' + cond_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_c_' + cond_seq_id + '_o_' + seq_id + '"></div>');
		var divSpanFirst = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:first > div.form-legend:last");
		var divSpanFirstBuilder = new StringBuilder();
		divSpanFirstBuilder.append('<div class="contain-legend"><span class="content-legend text-normal">');
		divSpanFirstBuilder.append(uiLabelMap.BSCondition);
		divSpanFirstBuilder.append(' ' + (cond_seq_id + 1));
		divSpanFirstBuilder.append('&nbsp;</span><a href="javascript:OlbLoyaltyRules.deleteCond(');
		divSpanFirstBuilder.append('' + seq_id + ', ' + cond_seq_id);
		divSpanFirstBuilder.append(');"><i class="fa-times-circle open-sans open-sans-index"></i></a></div><div class="contain"></div>');
		divSpanFirst.append(divSpanFirstBuilder.toString());
		var divSpanFirstContain = $(divSpanFirst).find(".contain");
		
		var content2 = new StringBuilder();
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSCondition);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div class="span10"><div id="inputParamEnumId');
		content2.append(thisSuffix);
		content2.append('" name="inputParamEnumId');
		content2.append(thisSuffix);
		content2.append('"></div></div>');
		content2.append('<div class="span2"><div id="operatorEnumId');
		content2.append(thisSuffix);
		content2.append('" name="operatorEnumId');
		content2.append(thisSuffix);
		content2.append('"></div></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSProductName);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div id="productIdListCond');
		content2.append(thisSuffix);
		content2.append('" class="close-box-custom"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSCategoryName);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<input type="hidden" name="loyaltyApplEnumId');
		content2.append(thisSuffix);
		content2.append('" value="LPPA_INCLUDE"/>');
		content2.append('<input type="hidden" name="includeSubCategories');
		content2.append(thisSuffix);
		content2.append('" value="Y"/>');
		content2.append('<div id="productCatIdListCond');
		content2.append(thisSuffix);
		content2.append('" class="close-box-custom"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSParty);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div id="ddbcpartyIdCond');
		content2.append(thisSuffix);
		content2.append('">');
		content2.append('<div id="partyGridCond');
		content2.append(thisSuffix);
		content2.append('"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSPartyGroup);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div id="ddbcpartyGrpMemberIdCond');
		content2.append(thisSuffix);
		content2.append('">');
		content2.append('<div id="partyGrpMemberGridCond');
		content2.append(thisSuffix);
		content2.append('"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSRoleType);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div id="ddbcroleTypeIdCond');
		content2.append(thisSuffix);
		content2.append('">');
		content2.append('<div id="roleTypeGridCond');
		content2.append(thisSuffix);
		content2.append('"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.ProductConditionValue);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<input type="hidden" size="25" id="condValue');
		content2.append(thisSuffix);
		content2.append('" name="condValue');
		content2.append(thisSuffix);
		content2.append('" class="span12"/>');
		content2.append('<div id="condValueTmp');
		content2.append(thisSuffix);
		content2.append('"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSUsePriceWithTax);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div id="usePriceWithTax');
		content2.append(thisSuffix);
		content2.append('" name="usePriceWithTax');
		content2.append(thisSuffix);
		content2.append('"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span5"><label>');
		content2.append(uiLabelMap.BSIsReturnOrder);
		content2.append('</label></div>');
		content2.append('<div class="span7">');
		content2.append('<div id="isReturnOrder');
		content2.append(thisSuffix);
		content2.append('" name="isReturnOrder');
		content2.append(thisSuffix);
		content2.append('"></div>');
		content2.append('</div>');
		content2.append('</div>');
		
		divSpanFirstContain.append(content2.toString());
		actionScriptBuildCond(rule_seq_id, cond_seq_id, {});
		var nexCondSeqId = cond_seq_id + 1;
		$("#add-new-condition-container_o_" + rule_seq_id).html('<a href="javascript:OlbLoyaltyRules.addNewCondition(' + rule_seq_id + ', ' + nexCondSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>' + uiLabelMap.BSAddCondition + '</a>');
		$(window).resize();
	};

	var addNewAction = function(rule_seq_id, action_seq_id){
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + action_seq_id + '_o_' + seq_id;
		$("#add-new-action-container_o_" + seq_id).before('<input type="hidden" name="isRemoveAction_a_' + action_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_a_' + action_seq_id + '_o_' + seq_id + '"></div>');
		var divSpanLast = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:last > div.form-legend:last");
		divSpanLast.append('<div class="contain-legend"><span class="content-legend text-normal">' + uiLabelMap.BSAction + ' ' + (action_seq_id + 1) + '&nbsp;<a href="javascript:OlbLoyaltyRules.deleteAction(' + seq_id + ', ' + action_seq_id + ');"><i class="fa-times-circle open-sans open-sans-index"></i></a></span></div><div class="contain"></div>');
		var divSpanLastContain = $(divSpanLast).find(".contain");
		
		var content3 = new StringBuilder();
		/*content3.append('<input type="hidden" name="orderAdjustmentTypeId');
		content3.append(thisSuffix);
		content3.append('" value="PROMOTION_ADJUSTMENT" />');*/
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSAction);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<div id="loyaltyActionEnumId');
		content3.append(thisSuffix);
		content3.append('" name="loyaltyActionEnumId');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSProductName);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<div id="productIdListAction');
		content3.append(thisSuffix);
		content3.append('" class="close-box-custom"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<input type="hidden" name="loyaltyApplEnumIdAction');
		content3.append(thisSuffix);
		content3.append('" value="LPPA_INCLUDE"/>');
		content3.append('<input type="hidden" name="includeSubCategoriesAction');
		content3.append(thisSuffix);
		content3.append('" value="Y"/>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSCategoryName);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<div id="productCatIdListAction');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSRatingType);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<div id="ratingTypeId');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSValue);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<input type="hidden" id="quantity');
		content3.append(thisSuffix);
		content3.append('" name="quantity');
		content3.append(thisSuffix);
		content3.append('" class="span12"/>');
		content3.append('<div id="quantityTmp');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSAmountOrPercent);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<input type="hidden" id="amount');
		content3.append(thisSuffix);
		content3.append('" name="amount');
		content3.append(thisSuffix);
		content3.append('" class="span12"/>');
		content3.append('<div id="amountTmp');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSOperator);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<div id="loyaltyActionOperEnumId');
		content3.append(thisSuffix);
		content3.append('" name="loyaltyActionOperEnumId');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');
		/*content3.append('<div class="row-fluid">');
		content3.append('<div class="span5"><label>');
		content3.append(uiLabelMap.BSCheckInventoryItem);
		content3.append('</label></div>');
		content3.append('<div class="span7">');
		content3.append('<div id="isCheckInv');
		content3.append(thisSuffix);
		content3.append('" name="isCheckInv');
		content3.append(thisSuffix);
		content3.append('"></div>');
		content3.append('</div>');
		content3.append('</div>');*/

		divSpanLastContain.append(content3.toString());
		actionScriptBuildAction(rule_seq_id, action_seq_id, {});
		var nexActionSeqId = action_seq_id + 1;
		$("#add-new-action-container_o_" + rule_seq_id).html('<a href="javascript:OlbLoyaltyRules.addNewAction(' + rule_seq_id + ', ' + nexActionSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>' + uiLabelMap.BSAddAction + '</a>');
		$(window).resize();
	};
	
	var actionScriptBuildCond = function(rule_seq_id, cond_seq_id, config) {
		var productSelectArr = config.productSelectArr ? config.productSelectArr : [];
		var categorySelectArr = config.categorySelectArr ? config.categorySelectArr : [];
		var inputParamSelectArr = config.inputParamSelectArr ? config.inputParamSelectArr : ['LPIP_ORDER_TOTAL'];
		var operatorSelectArr = config.operatorSelectArr ? config.operatorSelectArr : [];
		var partyIdSelected = typeof(config.partyIdSelected) != 'undefined' ? config.partyIdSelected : [];
		var partyGrpMemIdSelected = typeof(config.partyGrpMemIdSelected) != 'undefined' ? config.partyGrpMemIdSelected : [];
		var roleTypeIdSelected = typeof(config.roleTypeIdSelected) != 'undefined' ? config.roleTypeIdSelected : [];
		var loyaltyCondCondValue = typeof(config.loyaltyCondCondValue) != 'undefined' ? config.loyaltyCondCondValue : null;
		var usePriceWithTaxSelectArr = config.usePriceWithTaxSelectArr ? config.usePriceWithTaxSelectArr : ['N'];
		var isReturnOrderSelectArr = config.isReturnOrderSelectArr ? config.isReturnOrderSelectArr : ['N'];
		
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		$("#condValueTmp" + thisSuffix).jqxNumberInput({height: 25, width: '100%', spinButtons:true, decimalDigits: 0, digits: 11, max: 99999999999, min: 0, theme: OlbCore.theme});
		$("#condValueTmp" + thisSuffix).jqxNumberInput('val', loyaltyCondCondValue);
		onChangeNumberInput($("#condValueTmp" + thisSuffix), "#condValue" + thisSuffix);
		
		// list product
		initProductIdList($("#productIdListCond" + thisSuffix), productSelectArr);
	    
	    // list category
	    initProductCatIdList($("#productCatIdListCond" + thisSuffix), categorySelectArr);
	    
	    // list input param enum id
	    initInputParamEnum($("#inputParamEnumId" + thisSuffix), inputParamSelectArr);
	    
	    // list cond oper enum id
	    var localDataInter = condOperEnumData;
	    if (typeof(inputParamSelectArr) != 'undefined') {
	    	var inputParamValue = inputParamSelectArr[0];
			if ("LPIP_PARTY_ID" == inputParamValue || "LPIP_PARTY_GRP_MEM" == inputParamValue || "LPIP_ROLE_TYPE" == inputParamValue 
					|| "LPIP_PRODUCT_QUANT" == inputParamValue || "LPIP_PRODUCT_AMOUNT" == inputParamValue || "LPIP_EACH_PROD_QUANT" == inputParamValue 
					|| "LPIP_EACH_ORDER_TOTA" == inputParamValue) {
				localDataInter = condOperEnumDataEqual;
			}
	    }
		initOperatorEnum($("#operatorEnumId" + thisSuffix), operatorSelectArr, localDataInter);
		
		// list party
		initPartyList($("#ddbcpartyIdCond" + thisSuffix), $("#partyGridCond" + thisSuffix), partyIdSelected, thisSuffix);
		initPartyList($("#ddbcpartyGrpMemberIdCond" + thisSuffix), $("#partyGrpMemberGridCond" + thisSuffix), partyGrpMemIdSelected, thisSuffix, 'Y');
		
		// list role type
		initRoleTypeList($("#ddbcroleTypeIdCond" + thisSuffix), $("#roleTypeGridCond" + thisSuffix), roleTypeIdSelected, thisSuffix);
		
		initLoyaltyActionIsCheckInv($("#usePriceWithTax" + thisSuffix), usePriceWithTaxSelectArr);
		initLoyaltyActionIsCheckInv($("#isReturnOrder" + thisSuffix), isReturnOrderSelectArr);
		checkAndDisplayInputValueCond($("#inputParamEnumId" + thisSuffix));
		$("#inputParamEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValueCond($(this), true);
		});
	}
	var actionScriptBuildAction = function(rule_seq_id, cond_seq_id, config){
		var productSelectArr = config.productSelectArr ? config.productSelectArr : [];
		var categorySelectArr = config.categorySelectArr ? config.categorySelectArr : [];
		var loyaltyActionSelectArr = config.loyaltyActionSelectArr ? config.loyaltyActionSelectArr : [];
		var loyaltyActionOperSelectArr = config.loyaltyActionOperSelectArr ? config.loyaltyActionOperSelectArr : [];
		var isCheckInvSelectArr = config.isCheckInvSelectArr ? config.isCheckInvSelectArr : ['Y'];
		var ratingTypeSelectArr = config.ratingTypeSelectArr ? config.ratingTypeSelectArr : [];
		var loyaltyActionQuantity = config.loyaltyActionQuantity ? config.loyaltyActionQuantity : null;
		var loyaltyActionAmount = config.loyaltyActionAmount ? config.loyaltyActionAmount : null;
		
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + cond_seq_id + '_o_' + seq_id;
		$("#quantityTmp" + thisSuffix).jqxNumberInput({height: 25, width: '100%', spinButtons:true, decimalDigits: 0, digits: 11, max: 99999999999, theme: OlbCore.theme});
		$("#quantityTmp" + thisSuffix).jqxNumberInput('val', loyaltyActionQuantity);
		$("#amountTmp" + thisSuffix).jqxNumberInput({height: 25, width: '100%', spinButtons:true, decimalDigits: 0, digits: 11, max: 99999999999, min: 0, theme: OlbCore.theme});
		$("#amountTmp" + thisSuffix).jqxNumberInput('val', loyaltyActionAmount);
		onChangeNumberInput($("#quantityTmp" + thisSuffix), "#quantity" + thisSuffix);
		onChangeNumberInput($("#amountTmp" + thisSuffix), "#amount" + thisSuffix);
		
		// list product
	    initProductIdList($("#productIdListAction" + thisSuffix), productSelectArr, false);
	    
	    // list category
	    initProductCatIdList($("#productCatIdListAction" + thisSuffix), categorySelectArr);
	    
	    // list input param enum id
		initLoyaltyActionEnum($("#loyaltyActionEnumId" + thisSuffix), loyaltyActionSelectArr);
		initLoyaltyActionOper($("#loyaltyActionOperEnumId" + thisSuffix), loyaltyActionOperSelectArr);
		/*initLoyaltyActionIsCheckInv($("#isCheckInv" + thisSuffix), isCheckInvSelectArr);*/
		initLoyaltyActionRatingType($("#ratingTypeId" + thisSuffix), ratingTypeSelectArr);
		checkAndDisplayInputValue($("#loyaltyActionEnumId" + thisSuffix));
		$("#loyaltyActionEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValue($(this));
		});
	};
	var showEditorRuleName = function(thisElement){
		var thisE = $(thisElement);
		$(thisE).toggleClass("editing");
		if ($(thisE).hasClass("editing")) {
			$(thisE).find("input").show();
			$(thisE).find("span").hide();
			$(thisE).find("input").select();
		} else {
			$(thisE).find("input").hide();
			$(thisE).find("span").show();
		}
	};
	var blurEditorRuleName = function(thisElement){
		if ($(thisElement) == undefined || $(thisElement).length <= 0) {
			return false;
		}
		var arrayVar = $(thisElement).attr("id").split("_o_");
		var seq_id = arrayVar[arrayVar.length - 1];
		var ruleNameHidden = $('input[name="ruleName_o_' + seq_id + '"]');
		if (ruleNameHidden == undefined || ruleNameHidden.length <= 0) {
			return false
		}
		var parentThis = $(thisElement).parent();
		if ($(thisElement) != undefined && $(thisElement).length > 0) {
			if ((/^\s*$/.test($(thisElement).val()))) {
				// rollback old value
				$(thisElement).val($(ruleNameHidden).val());
				$(parentThis).find("span").text($(ruleNameHidden).val());
			}
		}
		if ($(parentThis).hasClass("editing")) {
			$(parentThis).find("input").hide();
			$(parentThis).find("span").show();
			$(parentThis).removeClass("editing");
		}
		$(parentThis).find("span").text($(thisElement).val());
		$(ruleNameHidden).val($(thisElement).val());
	};
	var checkAndDisplayInputValue = function(comboBox){
		var comboBoxObj = $(comboBox).jqxComboBox('getSelectedItem');
		var idObj = $(comboBox).attr("id");
		var suffixId = idObj.substring("loyaltyActionEnumId".length, idObj.length);
		if (comboBoxObj != undefined && comboBoxObj != null) {
			var value = comboBoxObj.value;
			var quantityInputId = "quantity" + suffixId;
			var amountInputId = "amount" + suffixId;
			var quantityTmpInputId = "quantityTmp" + suffixId;
			var amountTmpInputId = "amountTmp" + suffixId;
			var productInputId = "productIdListAction" + suffixId;
			var categoryInputId = "productCatIdListAction" + suffixId;
			var ratingTypeId = "ratingTypeId" + suffixId;
			var showQuantity = false;
			var showAmount = false;
			var showProduct = false;
			var showCategory = false;
			var showRating = false;
			if ("LOY_PROD_DISC" == value || "LOY_PROD_AMDISC" == value || "LOY_PROD_PRICE" == value) {
				showQuantity = true;
				showAmount = true;
				showProduct = true;
			} else if ("LOY_ORDER_PERCENT" == value || "LOY_ORDER_AMOUNT" == value || "LOY_PROD_SPPRC" == value 
							|| "LOY_SHIP_CHARGE" == value || "LOY_TAX_PERCENT" == value || "LOY_TRADE_DISCOUNT" == value) {
				showQuantity = false;
				showAmount = true;
			} else if ("LOY_ASWP" == value) {
				showQuantity = true;
				showAmount = false;
				showProduct = false;
				showCategory = false;
			} else if ("LOY_RATE" == value){
				showQuantity = false;
				showAmount = false;
				showProduct = false;
				showCategory = false;
				showRating = true;
			}
			
			if (showQuantity) {
				var parentQuantityObj = $("#" + quantityInputId).closest(".row-fluid");
				if (parentQuantityObj) parentQuantityObj.show();
			} else {
				var parentQuantityObj = $("#" + quantityInputId).closest(".row-fluid");
				if (parentQuantityObj) parentQuantityObj.hide();
			}
			if (showAmount) {
				var parentAmountObj = $("#" + amountInputId).closest(".row-fluid");
				if (parentAmountObj) parentAmountObj.show();
			} else {
				var parentAmountObj = $("#" + amountInputId).closest(".row-fluid");
				if (parentAmountObj) parentAmountObj.hide();
			}
			
			var parentProductObj = $("#" + productInputId).closest(".row-fluid");
			if (showProduct) {
				if (parentProductObj) parentProductObj.show();
			} else {
				if (parentProductObj) parentProductObj.hide();
			}
			
			var parentCategoryObj = $("#" + categoryInputId).closest(".row-fluid");
			if (showCategory) {
				if (parentCategoryObj) parentCategoryObj.show();
			} else {
				if (parentCategoryObj) parentCategoryObj.hide();
			}
			
			var parentRatingObj = $("#" + ratingTypeId).closest(".row-fluid");
			if (showRating) {
				if (parentRatingObj) parentRatingObj.show();
			} else {
				if (parentRatingObj) parentRatingObj.hide();
			}
		}
	};
	var checkAndDisplayInputValueCond = function(comboBox, hasReset){
		var comboBoxObj = $(comboBox).jqxComboBox('getSelectedItem');
		var idObj = $(comboBox).attr("id");
		var suffixId = idObj.substring("inputParamEnumId".length, idObj.length);
		if (comboBoxObj != undefined && comboBoxObj != null) {
			var value = comboBoxObj.value;
			var productInputId = "productIdListCond" + suffixId;
			var categoryInputId = "productCatIdListCond" + suffixId;
			var condValueInputId = "condValue" + suffixId;
			var condValueTmpInputId = "condValueTmp" + suffixId;
			var operatorInputId = "operatorEnumId" + suffixId;
			var partyIdCond = "ddbcpartyIdCond" + suffixId;
			var partyGridCond = "partyGridCond" + suffixId;
			var partyGrpMemberIdCond = "ddbcpartyGrpMemberIdCond" + suffixId;
			var partyGrpMemberGridCond = "partyGrpMemberGridCond" + suffixId;
			var roleTypeIdCond = "ddbcroleTypeIdCond" + suffixId;
			var roleTypeGridCond = "roleTypeGridCond" + suffixId;
			var showProduct = false;
			var showInput = false;
			var showParty = false;
			var showPartyGroup = false;
			var showRoleType = false;
			var showPartyType = false;
			var showInputOther = false;
			var localDataInter = condOperEnumData;
			if ("LPIP_PRODUCT_TOTAL" == value) {
				showProduct = true;
				showInput = true;
			} else if ("LPIP_ORDER_TOTAL" == value || "LPIP_NEW_ACCT" == value || "LPIP_RECURRENCE" == value
					|| "LPIP_ORST_YEAR" == value || "LPIP_ORST_LAST_YEAR" == value || "LPIP_LPMUP_AMT" == value
					|| "LPIP_LPMUP_PER" == value || "LPIP_ORDER_SHIPTOTAL" == value || "LPIP_SERVICE" == value 
					|| "LPIP_TOTAL_POINT" == value) {
				showInput = true;
			} else if ("LPIP_PARTY_ID" == value) {
				showParty = true;
				showInput = false;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("LPIP_PARTY_GRP_MEM" == value) {
				showPartyGroup = true;
				showInput = false;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("LPIP_PARTY_CLASS" == value) {
				showPartyType = true;
				showInput = false; // deleted
			} else if ("LPIP_ROLE_TYPE" == value) {
				showRoleType = true;
				showInput = false;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("LPIP_ORST_HIST" == value) {
				showInput = true;
				showInputOther = true;
			} else if ("LPIP_PRODUCT_QUANT" == value || "LPIP_PRODUCT_AMOUNT" == value || "LPIP_EACH_PROD_QUANT" == value) {
				// as case 1
				showProduct = true;
				showInput = true;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("LPIP_EACH_ORDER_TOTA" == value){
				showInput = true;
				localDataInter = condOperEnumDataEqual;
			}
			
			var parentCondInputObj = $("#" + condValueInputId).closest(".row-fluid");
			if (parentCondInputObj) {
				if (showInput) { parentCondInputObj.show(); } 
				else { parentCondInputObj.hide(); }
			}
			var parentProductObj = $("#" + productInputId).closest(".row-fluid");
			var parentCategoryObj = $("#" + categoryInputId).closest(".row-fluid");
			if (showProduct) { 
				if (parentProductObj) parentProductObj.show();
				if (parentCategoryObj) parentCategoryObj.show();
			} else {
				if (parentProductObj) parentProductObj.hide();
				if (parentCategoryObj) parentCategoryObj.hide();
			}
			
			var parentPartyObj = $("#" + partyIdCond).closest(".row-fluid");
			if (parentPartyObj) {
				if (showParty) { parentPartyObj.show(); } 
				else { parentPartyObj.hide(); }
			}
			var parentPartyGrpMemberObj = $("#" + partyGrpMemberIdCond).closest(".row-fluid");
			if (parentPartyGrpMemberObj) {
				if (showPartyGroup) { parentPartyGrpMemberObj.show(); } 
				else { parentPartyGrpMemberObj.hide(); }
			}
			var parentRoleTypeObj = $("#" + roleTypeIdCond).closest(".row-fluid");
			if (parentRoleTypeObj) {
				if (showRoleType) { parentRoleTypeObj.show(); } 
				else { parentRoleTypeObj.hide(); }
			}
			
			if (hasReset) {
				// reset value
				$("#" + condValueInputId).val("");
				$("#" + condValueTmpInputId).jqxNumberInput("val", "");
				$("#" + partyIdCond).jqxDropDownButton('setContent', "");
				$("#" + partyGridCond).jqxGrid('clearselection');
				$("#" + partyGrpMemberIdCond).jqxDropDownButton('setContent', "");
				$("#" + partyGrpMemberGridCond).jqxGrid('clearselection');
				$("#" + roleTypeIdCond).jqxDropDownButton('setContent', "");
				$("#" + roleTypeGridCond).jqxGrid('clearselection');
				
				var tmpSourceOperator = $("#" + operatorInputId).jqxComboBox('source');
	    		tmpSourceOperator._source.localdata = localDataInter;
				$("#" + operatorInputId).jqxComboBox('clearSelection');
				$("#" + operatorInputId).jqxComboBox('source', tmpSourceOperator);
				$("#" + operatorInputId).jqxComboBox('selectedIndex', 0);
			}
		}
	};
	var initOperatorEnum = function(comboBox, selectArr, localDataInter){
		var configOperatorEnum = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			dropDownWidth: 'auto',
			multiSelect: false,
			selectedIndex: 0,
			autoDropDownHeight: true,
			placeHolder: "",
		};
		new OlbComboBox(comboBox, localDataInter, configOperatorEnum, selectArr);
	};
	var initProductIdList = function(comboBox, selectArr, notVirtual){
		var otherParam = "";
		if (!notVirtual) otherParam = "&hasVirtualProd=Y"
		var configProductIdList = {
			placeHolder: uiLabelMap.BSClickToChoose,
			key: "productId",
    		value: "productNameSearch",
			width:'100%',
			height: 25,
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			renderer : null,
			renderSelectedItem : null,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductSellAll&pagesize=0' + otherParam,
		};
		new OlbComboBox(comboBox, null, configProductIdList, selectArr);
	};
	var initProductCatIdList = function(comboBox, selectArr){
		var configProductCatIdList = {
			width:'100%',
			height: 25,
			key: "productCategoryId",
    		value: "categoryName",
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			placeHolder: uiLabelMap.BSClickToChoose,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQListCategoryByCatalog&showAll=Y&pagesize=0',
		};
		new OlbComboBox(comboBox, null, configProductCatIdList, selectArr);
	};
	var initInputParamEnum = function(comboBox, selectArr){
		var configInputParamEnum = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
		};
		new OlbComboBox(comboBox, inputParamEnumData, configInputParamEnum, selectArr);
	};
	var initLoyaltyActionEnum = function(comboBox, selectArr){
		var configLoyaltyActionEnum = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
		};
		new OlbComboBox(comboBox, actionParamEnumData, configLoyaltyActionEnum, selectArr);
	};
	var initLoyaltyActionOper = function(dropDownObj, selectArr){
		var configLoyaltyActionOper = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
			autoDropDownHeight: true,
		};
		new OlbDropDownList(dropDownObj, actionOperEnumsData, configLoyaltyActionOper, selectArr);
	};
	var initLoyaltyActionIsCheckInv = function(dropDownObj, selectArr){
		var configLoyaltyActionOper = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
			autoDropDownHeight: true,
		};
		new OlbDropDownList(dropDownObj, actionIsEnumsData, configLoyaltyActionOper, selectArr);
	};
	var initLoyaltyActionRatingType = function(dropDownObj, selectArr){
		var configLoyaltyActionRatingType = {
				width:'100%',
				height: 25,
				key: "enumId",
				value: "description",
				displayDetail: false,
				selectedIndex: -1,
				dropDownWidth: 'auto',
				placeHolder: uiLabelMap.BSClickToChoose,
				autoDropDownHeight: true,
		};
		new OlbDropDownList(dropDownObj, ratingTypeData, configLoyaltyActionRatingType, selectArr);
	};
	var initPartyList = function(dropdownButton, dropdownGrid, selectArr, suffixValue, isGroup){
		var otherParam = "";
		if (isGroup) otherParam = "&isGroup=Y"
		var configParty = {
			useUrl: true,
			root: 'results',
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSPartyId, datafield: 'partyId', width: '30%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			url: 'JQGetListParties' + otherParam,
			useUtilFunc: true,
			source: {
				pagesize: 5,
			},
		};
		new OlbDropDownButton($(dropdownButton), $(dropdownGrid), null, configParty, []);
		
		onSelectDropDownGrid($(dropdownGrid), $(dropdownButton), 'partyId', suffixValue);
		if (selectArr != undefined && selectArr != null && selectArr.length > 0){
    		var item = selectArr[0];
    		onSetContentDropDownButton($(dropdownButton), item);
		}
	};
	var initRoleTypeList = function(dropdownButton, dropdownGrid, selectArr, suffixValue){
		var configRoleType = {
			useUrl: true,
			root: 'results',
			widthButton: '100%',
			showdefaultloadelement: false,
			autoshowloadelement: false,
			datafields: [{name: 'roleTypeId', type: 'string'}, {name: 'description', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSRoleTypeId, datafield: 'roleTypeId', width: '30%'},
				{text: uiLabelMap.BSDescription, datafield: 'description'}
			],
			url: 'JQGetListRoleType',
			useUtilFunc: true,
			source: {
				pagesize: 5,
			},
		};
		new OlbDropDownButton($(dropdownButton), $(dropdownGrid), null, configRoleType, []);
		
		onSelectDropDownGrid($(dropdownGrid), $(dropdownButton), 'roleTypeId', suffixValue);
		if (selectArr != undefined && selectArr != null && selectArr.length > 0){
    		var item = selectArr[0];
    		onSetContentDropDownButton($(dropdownButton), item);
		}
	};
	var onChangeNumberInput = function(thisElement, elementContainValue){
		$(thisElement).on('change, textchanged, valueChanged', function (event) {
		     var value = event.args.value;
		     if (OlbCore.isNotEmpty(value)) {
		     	$(elementContainValue).val(value);
		     }
		 });
	};
	var onSelectDropDownGrid = function(thisElement, elementSetVal, columnSetVal, suffixValue){
		$(thisElement).on('rowselect', function (event) {
	        var args = event.args;
	        var row = $(thisElement).jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div class="innerDropdownContent">' + row[columnSetVal] + '</div>';
	        $("#condValue" + suffixValue).val(row[columnSetVal]);
	        $(elementSetVal).jqxDropDownButton('setContent', dropDownContent);
	    });
	};
	var onSetContentDropDownButton = function(elementSetVal, value){
		var dropDownContent = '<div class="innerDropdownContent">' + value + '</div>';
		$(elementSetVal).jqxDropDownButton('setContent', dropDownContent);
	};
	
	return {
		closeTab: closeTab,
		deleteCond: deleteCond,
		deleteAction: deleteAction,
		addNewRule: addNewRule,
		addNewCondition: addNewCondition,
		addNewAction: addNewAction,
		actionScriptBuildCond: actionScriptBuildCond,
		actionScriptBuildAction: actionScriptBuildAction,
		showEditorRuleName: showEditorRuleName,
		blurEditorRuleName: blurEditorRuleName,
		checkAndDisplayInputValue: checkAndDisplayInputValue,
		checkAndDisplayInputValueCond: checkAndDisplayInputValueCond
	};
}()); // OlbLoyaltyRules