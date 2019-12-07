var OlbPromoRules = (function(){
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
			$("#form-legend" + thisSuffix).remove();
		}
		$(window).resize();
	};
	var deleteAction = function(rule_seq_id, action_seq_id) {
		var thisSuffix = "_a_" + action_seq_id + "_o_" + rule_seq_id;
		if ($('input[name="isRemoveAction' + thisSuffix + '"]') != undefined) {
			$('input[name="isRemoveAction' + thisSuffix + '"]').val("Y");
			$("#form-legend" + thisSuffix).remove();
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
		var ruleCountStr = "" + ruleCount;
		divTabHeadNew.append("<li class='active' id='recent-tab_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("'>");
		divTabHeadNew.append("<span class='close-tab' onclick='javascript:OlbPromoRules.closeTab(");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append(")'><i class='fa-times-circle open-sans open-sans-index'></i></span>");
		divTabHeadNew.append("<a data-toggle='tab' href='#");
		divTabHeadNew.append(id);
		divTabHeadNew.append("' id='recentTabItem_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("'>");
		divTabHeadNew.append("<span>");
		divTabHeadNew.append(uiLabelMap.BSRule);
		divTabHeadNew.append(" " + (ruleCount + 1));
		divTabHeadNew.append("</span>");
		/*divTabHeadNew.append("<input type='text' size='30' name='ruleNameTemp_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("' id='ruleNameTemp_o_");
		divTabHeadNew.append(ruleCountStr);
		divTabHeadNew.append("' value='");
		divTabHeadNew.append(uiLabelMap.BSRule);
		divTabHeadNew.append(" " + (ruleCount + 1));
		divTabHeadNew.append("'/>");*/
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
			//showEditorRuleName(this);
			showEditorRuleNameWindow(this);
		});
		
		/*$('#ruleNameTemp_o_' + ruleCount).on("blur", function(){
			blurEditorRuleName(this);
		});*/
	};
	var buildContentInTab = function(seq_id, div_id){
		// seq_id is sequence id of list tab. ex: 1, 2, 3, ...
		var divContent = $("#" + div_id);
		if (divContent == undefined || divContent.length < 1) return false;
		var content = new StringBuilder();
		var seq_id_str = '' + seq_id;
		var ruleNameDefaultStr = uiLabelMap.BSRule + ' ' + (seq_id + 1);
		content.append('<input type="hidden" size="30" name="ruleName_o_');
		content.append(seq_id_str);
		content.append('" id="ruleName_o_');
		content.append(seq_id_str);
		content.append('" value="');
		content.append(ruleNameDefaultStr);
		content.append('"/>');
		content.append('<input type="hidden" size="30" name="ruleText_o_');
		content.append(seq_id_str);
		content.append('" id="ruleText_o_');
		content.append(seq_id_str);
		content.append('" value="');
		content.append(ruleNameDefaultStr);
		content.append('"/>');
		content.append('<div id="ruleTextDisplay_o_');
		content.append(seq_id_str);
		content.append('" class="blue ruleTextDisplay">');
		content.append(uiLabelMap.BSDescription);
		content.append(': <span>');
		content.append(ruleNameDefaultStr);
		content.append('</span>');
		content.append('</div>');
		divContent.append(content.toString());
		
		var content = new StringBuilder();
		content.append('<div class="row-fuild">');
		content.append('<div class="span6">');
		content.append('<div id="add-new-condition-container_o_');
		content.append(seq_id_str);
		content.append('">');
		content.append('<a href="javascript:OlbPromoRules.addNewCondition(');
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
		content.append('<a href="javascript:OlbPromoRules.addNewAction(');
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
		divSpanFirstBuilder.append('&nbsp;</span><a href="javascript:OlbPromoRules.deleteCond(');
		divSpanFirstBuilder.append('' + seq_id + ', ' + cond_seq_id);
		divSpanFirstBuilder.append(');"><i class="fa-times-circle open-sans open-sans-index"></i></a></div><div class="contain"></div>');
		divSpanFirst.append(divSpanFirstBuilder.toString());
		var divSpanFirstContain = $(divSpanFirst).find(".contain");
		
		var productPromoTypeId = $("#productPromoTypeId").val();
		var isExhibited = false;
		if ("EXHIBITED" == productPromoTypeId) {
			isExhibited = true;
		}
		
		var content2 = new StringBuilder();
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSCondition + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<div class="span10"><div id="inputParamEnumId' + thisSuffix + '" name="inputParamEnumId' + thisSuffix + '"></div></div>');
		content2.append('<div class="span2"><div id="operatorEnumId' + thisSuffix + '" name="operatorEnumId' + thisSuffix + '"></div></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSProductName + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<div class="container-add-minus">');
		content2.append('<div id="productIdListCond' + thisSuffix + '" class="close-box-custom"></div>');
		content2.append('</div>');
		content2.append('<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowProductPromo(\'productIdListCond' + thisSuffix + '\');"><i class="fa fa-bars"></i></a>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSCategoryName + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<input type="hidden" name="productPromoApplEnumId' + thisSuffix + '" value="PPPA_INCLUDE"/>');
		content2.append('<input type="hidden" name="includeSubCategories' + thisSuffix + '" value="Y"/>');
		content2.append('<div class="container-add-minus">');
		content2.append('<div id="productCatIdListCond' + thisSuffix + '" class="close-box-custom"></div>');
		content2.append('</div>');
		content2.append('<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowCategoryPromo(\'productCatIdListCond' + thisSuffix + '\');"><i class="fa fa-bars"></i></a>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSParty + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<div id="ddbcpartyIdCond' + thisSuffix + '">');
		content2.append('<div id="partyGridCond' + thisSuffix + '"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSPartyGroup + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<div id="ddbcpartyGrpMemberIdCond' + thisSuffix + '">');
		content2.append('<div id="partyGrpMemberGridCond' + thisSuffix + '"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSRoleType + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<div id="ddbcroleTypeIdCond' + thisSuffix + '">');
		content2.append('<div id="roleTypeGridCond' + thisSuffix + '"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid" style="display:none">');
		content2.append('<div class="span4">');
		content2.append('<label>' + uiLabelMap.BSRecurrenceValue + '</label>');
		content2.append('</div>');
		content2.append('<div class="span8">');
		content2.append('<div class="container-add-minus">');
		content2.append('<div id="ddbcrecurrenceInfoIdCond' + thisSuffix + '" class="close-box-custom">');
		content2.append('<div id="recurrenceInfoGridCond' + thisSuffix + '"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbRecurrenceInfoNew.showWindowRecurrenceNew(\'ddbcrecurrenceInfoIdCond' + thisSuffix + '\');"><i class="fa fa-plus"></i></a>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span4"><label>' + uiLabelMap.ProductConditionValue + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<input type="hidden" size="25" id="condValue' + thisSuffix + '" name="condValue' + thisSuffix + '" class="span12"/>');
		content2.append('<div id="condValueTmp' + thisSuffix + '"></div>');
		content2.append('</div>');
		content2.append('</div>');
		content2.append('<div class="row-fluid">');
		content2.append('<div class="span4"><label>' + uiLabelMap.BSUsePriceWithTax + '</label></div>');
		content2.append('<div class="span8">');
		content2.append('<div id="usePriceWithTax' + thisSuffix + '" name="usePriceWithTax' + thisSuffix + '"></div>');
		content2.append('</div>');
		content2.append('</div>');
		
		divSpanFirstContain.append(content2.toString());
		actionScriptBuildCond(rule_seq_id, cond_seq_id, {});
		var nexCondSeqId = cond_seq_id + 1;
		$("#add-new-condition-container_o_" + rule_seq_id).html('<a href="javascript:OlbPromoRules.addNewCondition(' + rule_seq_id + ', ' + nexCondSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>' + uiLabelMap.BSAddCondition + '</a>');
		$(window).resize();
	};

	var addNewAction = function(rule_seq_id, action_seq_id){
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + action_seq_id + '_o_' + seq_id;
		$("#add-new-action-container_o_" + seq_id).before('<input type="hidden" name="isRemoveAction_a_' + action_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_a_' + action_seq_id + '_o_' + seq_id + '"></div>');
		var divSpanLast = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:last > div.form-legend:last");
		divSpanLast.append('<div class="contain-legend"><span class="content-legend text-normal">' + uiLabelMap.BSAction + ' ' + (action_seq_id + 1) + '&nbsp;<a href="javascript:OlbPromoRules.deleteAction(' + seq_id + ', ' + action_seq_id + ');"><i class="fa-times-circle open-sans open-sans-index"></i></a></span></div><div class="contain"></div>');
		var divSpanLastContain = $(divSpanLast).find(".contain");
		
		var content3 = new StringBuilder();
		content3.append('<input type="hidden" name="orderAdjustmentTypeId' + thisSuffix + '" value="PROMOTION_ADJUSTMENT" />');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.BSAction + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<div id="productPromoActionEnumId' + thisSuffix + '" name="productPromoActionEnumId' + thisSuffix + '"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.BSProductName + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<div class="container-add-minus">');
		content3.append('<div id="productIdListAction' + thisSuffix + '" class="close-box-custom"></div>');
		content3.append('</div>');
		content3.append('<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowProductPromo(\'productIdListAction' + thisSuffix + '\');"><i class="fa fa-bars"></i></a>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<input type="hidden" name="productPromoApplEnumIdAction' + thisSuffix + '" value="PPPA_INCLUDE"/>');
		content3.append('<input type="hidden" name="includeSubCategoriesAction' + thisSuffix + '" value="Y"/>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.BSCategoryName + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<div class="container-add-minus">');
		content3.append('<div id="productCatIdListAction' + thisSuffix + '"></div>');
		content3.append('</div>');
		content3.append('<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowCategoryPromo(\'productCatIdListAction' + thisSuffix + '\');"><i class="fa fa-bars"></i></a>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.ProductQuantity + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<input type="hidden" id="quantity' + thisSuffix + '" name="quantity' + thisSuffix + '" class="span12"/>');
		content3.append('<div id="quantityTmp' + thisSuffix + '"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.BSAmountOrPercent + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<input type="hidden" id="amount' + thisSuffix + '" name="amount' + thisSuffix + '" class="span12"/>');
		content3.append('<div id="amountTmp' + thisSuffix + '"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.BSOperator + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<div id="productPromoActionOperEnumId' + thisSuffix + '" name="productPromoActionOperEnumId' + thisSuffix + '"></div>');
		content3.append('</div>');
		content3.append('</div>');
		content3.append('<div class="row-fluid">');
		content3.append('<div class="span4"><label>' + uiLabelMap.BSCheckInventoryItem + '</label></div>');
		content3.append('<div class="span8">');
		content3.append('<div id="isCheckInv' + thisSuffix + '" name="isCheckInv' + thisSuffix + '"></div>');
		content3.append('</div>');
		content3.append('</div>');

		divSpanLastContain.append(content3.toString());
		actionScriptBuildAction(rule_seq_id, action_seq_id, {});
		var nexActionSeqId = action_seq_id + 1;
		$("#add-new-action-container_o_" + rule_seq_id).html('<a href="javascript:OlbPromoRules.addNewAction(' + rule_seq_id + ', ' + nexActionSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>' + uiLabelMap.BSAddAction + '</a>');
		$(window).resize();
	};
	
	var actionScriptBuildCond = function(rule_seq_id, cond_seq_id, config) {
		//var productSelectArr = config.productSelectArr ? config.productSelectArr : [];
		var productSelectMap = config.productSelectMap ? config.productSelectMap : [];
		//var categorySelectArr = config.categorySelectArr ? config.categorySelectArr : [];
		var categorySelectMap = config.categorySelectMap ? config.categorySelectMap : [];
		var inputParamSelectArr = config.inputParamSelectArr ? config.inputParamSelectArr : ['PPIP_EACH_PROD_QUANT'];
		var operatorSelectArr = config.operatorSelectArr ? config.operatorSelectArr : [];
		var partyIdSelected = typeof(config.partyIdSelected) != 'undefined' ? config.partyIdSelected : [];
		var partyGrpMemIdSelected = typeof(config.partyGrpMemIdSelected) != 'undefined' ? config.partyGrpMemIdSelected : [];
		var roleTypeIdSelected = typeof(config.roleTypeIdSelected) != 'undefined' ? config.roleTypeIdSelected : [];
		var promoCondCondValue = typeof(config.promoCondCondValue) != 'undefined' ? config.promoCondCondValue : null;
		var usePriceWithTaxSelectArr = config.usePriceWithTaxSelectArr ? config.usePriceWithTaxSelectArr : ['N'];
		
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		$("#condValueTmp" + thisSuffix).jqxNumberInput({height: 25, width: '200', spinButtons:true, decimalDigits: 0, digits: 11, max: 99999999999, min: 0, theme: OlbCore.theme});
		$("#condValueTmp" + thisSuffix).jqxNumberInput('val', promoCondCondValue);
		onChangeNumberInput($("#condValueTmp" + thisSuffix), "#condValue" + thisSuffix);
		
		// list product
		var objActiveProductIds = $("#productIdListCond" + thisSuffix);
		//initProductIdList($(objActiveProductIds), productSelectArr);
		initProductIdList($(objActiveProductIds), []);
		if (productSelectMap.length > 0){
			$(objActiveProductIds).jqxComboBox('clearSelection');
			$.each(productSelectMap, function(key, rowData){
				var itemNew = {
					"value": rowData.productId,
					"label": rowData.productCode + ' - ' + rowData.productName + ' - ' + rowData.productId
				};
				$(objActiveProductIds).jqxComboBox("addItem", itemNew);
				OlbComboBoxUtil.selectItem($(objActiveProductIds), [rowData.productId]);
			});
		}
	    
	    // list category
	    var objActiveCatIds = $("#productCatIdListCond" + thisSuffix);
	    //initProductCatIdList($("#productCatIdListCond" + thisSuffix), categorySelectArr);
	    initProductCatIdList($(objActiveCatIds), []);
	    if (categorySelectMap.length > 0){
			$(objActiveCatIds).jqxComboBox('clearSelection');
			$.each(categorySelectMap, function(key, rowData){
				var itemNew = {
					"value": rowData.productCategoryId,
					"label": rowData.categoryName + ' [' + rowData.productCategoryId + "]"
				};
				$(objActiveCatIds).jqxComboBox("addItem", itemNew);
				OlbComboBoxUtil.selectItem($(objActiveCatIds), [rowData.productCategoryId]);
			});
		}
	    
	    // list input param enum id
	    initInputParamEnum($("#inputParamEnumId" + thisSuffix), inputParamSelectArr);
	    
	    // list cond oper enum id
	    var recurrenceInfoSelected = null;
	    var localDataInter = condOperEnumData;
	    if (typeof(inputParamSelectArr) != 'undefined') {
	    	var inputParamValue = inputParamSelectArr[0];
			if ("PPIP_PARTY_ID" == inputParamValue || "PPIP_PARTY_GRP_MEM" == inputParamValue || "PPIP_ROLE_TYPE" == inputParamValue 
					|| "PPIP_PRODUCT_QUANT" == inputParamValue || "PPIP_PRODUCT_AMOUNT" == inputParamValue || "PPIP_EACH_PROD_QUANT" == inputParamValue) {
				localDataInter = condOperEnumDataEqual;
			} else if ("PPIP_RECURRENCE" == inputParamValue) {
				recurrenceInfoSelected = [promoCondCondValue];
			}
	    }
		initOperatorEnum($("#operatorEnumId" + thisSuffix), operatorSelectArr, localDataInter);
		
		// list party
		initPartyList($("#ddbcpartyIdCond" + thisSuffix), $("#partyGridCond" + thisSuffix), partyIdSelected, thisSuffix);
		initPartyList($("#ddbcpartyGrpMemberIdCond" + thisSuffix), $("#partyGrpMemberGridCond" + thisSuffix), partyGrpMemIdSelected, thisSuffix, 'Y');
		
		// list role type
		initRoleTypeList($("#ddbcroleTypeIdCond" + thisSuffix), $("#roleTypeGridCond" + thisSuffix), roleTypeIdSelected, thisSuffix);
		
		// init recurrence info
		initRecurrenceInfo($("#ddbcrecurrenceInfoIdCond" + thisSuffix), $("#recurrenceInfoGridCond" + thisSuffix), recurrenceInfoSelected, thisSuffix);
		
		initPromoActionIsCheckInv($("#usePriceWithTax" + thisSuffix), usePriceWithTaxSelectArr);
		checkAndDisplayInputValueCond($("#inputParamEnumId" + thisSuffix));
		$("#inputParamEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValueCond($(this), true);
		});
	}
	var actionScriptBuildAction = function(rule_seq_id, cond_seq_id, config){
		var productSelectMap = config.productSelectMap ? config.productSelectMap : [];
		//var productSelectArr = config.productSelectArr ? config.productSelectArr : [];
		var categorySelectMap = config.categorySelectMap ? config.categorySelectMap : [];
		//var categorySelectArr = config.categorySelectArr ? config.categorySelectArr : [];
		var promoActionSelectArr = config.promoActionSelectArr ? config.promoActionSelectArr : [];
		var promoActionOperSelectArr = config.promoActionOperSelectArr ? config.promoActionOperSelectArr : [];
		var isCheckInvSelectArr = config.isCheckInvSelectArr ? config.isCheckInvSelectArr : ['Y'];
		var promoActionQuantity = config.promoActionQuantity ? config.promoActionQuantity : null;
		var promoActionAmount = config.promoActionAmount ? config.promoActionAmount : null;
		
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + cond_seq_id + '_o_' + seq_id;
		$("#quantityTmp" + thisSuffix).jqxNumberInput({height: 25, width: '200', spinButtons:true, decimalDigits: 0, digits: 11, max: 99999999999, min: 0, theme: OlbCore.theme});
		$("#quantityTmp" + thisSuffix).jqxNumberInput('val', promoActionQuantity);
		$("#amountTmp" + thisSuffix).jqxNumberInput({height: 25, width: '200', spinButtons:true, decimalDigits: 0, digits: 11, max: 99999999999, min: 0, theme: OlbCore.theme});
		$("#amountTmp" + thisSuffix).jqxNumberInput('val', promoActionAmount);
		onChangeNumberInput($("#quantityTmp" + thisSuffix), "#quantity" + thisSuffix);
		onChangeNumberInput($("#amountTmp" + thisSuffix), "#amount" + thisSuffix);
	    
	    // list product
		var objActiveProductIds = $("#productIdListAction" + thisSuffix);
		//initProductIdList($(objActiveProductIds), productSelectArr, false);
		initProductIdList($(objActiveProductIds), [], false);
		if (productSelectMap.length > 0){
			$(objActiveProductIds).jqxComboBox('clearSelection');
			$.each(productSelectMap, function(key, rowData){
				var itemNew = {
					"value": rowData.productId,
					"label": rowData.productCode + ' - ' + rowData.productName + ' - ' + rowData.productId
				};
				$(objActiveProductIds).jqxComboBox("addItem", itemNew);
				OlbComboBoxUtil.selectItem($(objActiveProductIds), [rowData.productId]);
			});
		}
	    
	    // list category
	    var objActiveCatIds = $("#productCatIdListAction" + thisSuffix);
	    //initProductCatIdList($("#productCatIdListCond" + thisSuffix), categorySelectArr);
	    initProductCatIdList($(objActiveCatIds), []);
	    if (categorySelectMap.length > 0){
			$(objActiveCatIds).jqxComboBox('clearSelection');
			$.each(categorySelectMap, function(key, rowData){
				var itemNew = {
					"value": rowData.productCategoryId,
					"label": rowData.categoryName + ' [' + rowData.productCategoryId + "]"
				};
				$(objActiveCatIds).jqxComboBox("addItem", itemNew);
				OlbComboBoxUtil.selectItem($(objActiveCatIds), [rowData.productCategoryId]);
			});
		}
	    
	    // list input param enum id
		initPromoActionEnum($("#productPromoActionEnumId" + thisSuffix), promoActionSelectArr);
		initPromoActionOper($("#productPromoActionOperEnumId" + thisSuffix), promoActionOperSelectArr);
		initPromoActionIsCheckInv($("#isCheckInv" + thisSuffix), isCheckInvSelectArr);
		checkAndDisplayInputValue($("#productPromoActionEnumId" + thisSuffix));
		$("#productPromoActionEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValue($(this), true);
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
	var showEditorRuleNameWindow = function(thisElement){
		var idElement = $(thisElement).attr("id");
		$("#we_rn_ruleIndex").val(idElement);
		
		var arrayVar = idElement.split("_o_");
		var seq_id = arrayVar[arrayVar.length - 1];
		var ruleNameHidden = $('input[name="ruleName_o_' + seq_id + '"]');
		var ruleTextHidden = $('input[name="ruleText_o_' + seq_id + '"]');
		$("#we_rn_ruleName").val($(ruleNameHidden).val());
		$("#we_rn_ruleText").val($(ruleTextHidden).val());
		$("#windowPromoRuleNameEdit").jqxWindow("open");
		$("#we_rn_ruleName").focus();
	};
	var blurEditorRuleNameWindow = function(){
		var tabElement = $("#" + $("#we_rn_ruleIndex").val());
		if ($(tabElement) == undefined || $(tabElement).length <= 0) {
			return false;
		}
		var arrayVar = $(tabElement).attr("id").split("_o_");
		var seq_id = arrayVar[arrayVar.length - 1];
		var ruleNameHidden = $('input[name="ruleName_o_' + seq_id + '"]');
		var ruleTextHidden = $('input[name="ruleText_o_' + seq_id + '"]');
		var ruleTextDisplayHidden = $('div[id="ruleTextDisplay_o_' + seq_id + '"]');
		if (ruleNameHidden == undefined || ruleNameHidden.length <= 0) {
			return false
		}
		if (ruleTextHidden == undefined || ruleTextHidden.length <= 0) {
			return false
		}
		var ruleNameElement = $("#we_rn_ruleName");
		var ruleTextElement = $("#we_rn_ruleText");
		var ruleTextElementValue = $(ruleTextElement).val();
		$(tabElement).find("span").text($(ruleNameElement).val());
		$(ruleNameHidden).val($(ruleNameElement).val());
		$(ruleTextHidden).val(ruleTextElementValue);
		$(ruleTextDisplayHidden).find("span").text(ruleTextElementValue);
		//$(tabElement).attr('title', ruleTextElementValue);
		$("#windowPromoRuleNameEdit").jqxWindow("close");
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
	var checkAndDisplayInputValue = function(comboBox, hasReset){
		var comboBoxObj = $(comboBox).jqxComboBox('getSelectedItem');
		var idObj = $(comboBox).attr("id");
		var suffixId = idObj.substring("productPromoActionEnumId".length, idObj.length);
		if (comboBoxObj != undefined && comboBoxObj != null) {
			var value = comboBoxObj.value;
			var quantityInputId = "quantity" + suffixId;
			var amountInputId = "amount" + suffixId;
			var quantityTmpInputId = "quantityTmp" + suffixId;
			var amountTmpInputId = "amountTmp" + suffixId;
			var productInputId = "productIdListAction" + suffixId;
			var categoryInputId = "productCatIdListAction" + suffixId;
			var isCheckInvInputId = "isCheckInv" + suffixId;
			var showQuantity = false;
			var showAmount = false;
			var showProduct = false;
			if ("PROMO_PROD_DISC" == value || "PROMO_PROD_AMDISC" == value || "PROMO_PROD_PRICE" == value) {
				showQuantity = true;
				showAmount = true;
				showProduct = true;
			} else if ("PROMO_ORDER_PERCENT" == value || "PROMO_ORDER_AMOUNT" == value || "PROMO_PROD_SPPRC" == value 
							|| "PROMO_SHIP_CHARGE" == value || "PROMO_TAX_PERCENT" == value || "PROMO_TRADE_DISCOUNT" == value) {
				showQuantity = false;
				showAmount = true;
			} else if ("PROMO_GWP" == value) {
				showQuantity = true;
				showAmount = false;
				showProduct = true;
			}
			// PROMO_SERVICE
			
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
			var parentCategoryObj = $("#" + categoryInputId).closest(".row-fluid");
			var parentIsCheckInvObj = $("#" + isCheckInvInputId).closest(".row-fluid");
			if (showProduct) {
				if (parentProductObj) parentProductObj.show();
				if (parentCategoryObj) parentCategoryObj.show();
				if (parentIsCheckInvObj) parentIsCheckInvObj.show();
			} else {
				if (parentProductObj) parentProductObj.hide();
				if (parentCategoryObj) parentCategoryObj.hide();
				if (parentIsCheckInvObj) parentIsCheckInvObj.hide();
			}
			
			if (hasReset) {
				// reset value
				$("#" + quantityInputId).val("");
				$("#" + quantityTmpInputId).jqxNumberInput("val", null);
				$("#" + amountInputId).val("");
				$("#" + amountTmpInputId).jqxNumberInput("val", null);
				
				$("#" + productInputId).jqxComboBox('clearSelection');
				$("#" + categoryInputId).jqxComboBox('clearSelection');
				if (showProduct) {
					$("#" + isCheckInvInputId).jqxDropDownList('selectItem', "Y");
				} else {
					$("#" + isCheckInvInputId).jqxDropDownList('clearSelection');
				}
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
			var recurrenceInfoIdCond = "ddbcrecurrenceInfoIdCond" + suffixId;
			var usePriceWithTax = "usePriceWithTax" + suffixId;
			var showProduct = false;
			var showInput = false;
			var showParty = false;
			var showPartyGroup = false;
			var showRoleType = false;
			var showPartyType = false;
			var showRecurrenceInfo = false;
			var showInputOther = false;
			var showOptionPriceTax = false;
			var localDataInter = condOperEnumData;
			if ("PPIP_PRODUCT_TOTAL" == value) {
				showProduct = true;
				showInput = true;
				showOptionPriceTax = true;
			} else if ("PPIP_ORDER_TOTAL" == value || "PPIP_NEW_ACCT" == value
					|| "PPIP_ORST_YEAR" == value || "PPIP_ORST_LAST_YEAR" == value || "PPIP_LPMUP_AMT" == value
					|| "PPIP_LPMUP_PER" == value || "PPIP_ORDER_SHIPTOTAL" == value || "PPIP_SERVICE" == value 
					|| "PPIP_EACH_ORDER_TOTA" == value) {
				showInput = true;
				showOptionPriceTax = true;
			} else if ("PPIP_RECURRENCE" == value) {
				showRecurrenceInfo = true;
			} else if ("PPIP_PARTY_ID" == value) {
				showParty = true;
				showInput = false;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("PPIP_PARTY_GRP_MEM" == value) {
				showPartyGroup = true;
				showInput = false;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("PPIP_PARTY_CLASS" == value) {
				showPartyType = true;
				showInput = false; // deleted
			} else if ("PPIP_ROLE_TYPE" == value) {
				showRoleType = true;
				showInput = false;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("PPIP_ORST_HIST" == value) {
				showInput = true;
				showInputOther = true;
				showOptionPriceTax = true;
			} else if ("PPIP_PRODUCT_QUANT" == value || "PPIP_EACH_PROD_QUANT" == value) {
				// as case 1
				showProduct = true;
				showInput = true;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("PPIP_PRODUCT_AMOUNT" == value) {
				showProduct = true;
				showInput = true;
				showOptionPriceTax = true;
				
				localDataInter = condOperEnumDataEqual;
			} else if ("PPIP_PROD_QUANT_TOTA" == value) {
				showProduct = true;
				showInput = true;
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
			var parentRecurrenceInfoObj = $("#" + recurrenceInfoIdCond).closest(".row-fluid");
			if (parentRecurrenceInfoObj) {
				if (showRecurrenceInfo) { parentRecurrenceInfoObj.show(); } 
				else { parentRecurrenceInfoObj.hide(); }
			}
			var parentUsePriceWithTaxObj = $("#" + usePriceWithTax).closest(".row-fluid");
			if (parentUsePriceWithTaxObj) {
				if (showOptionPriceTax) { parentUsePriceWithTaxObj.show(); } 
				else { parentUsePriceWithTaxObj.hide(); }
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
				$("#" + productInputId).jqxComboBox('clearSelection');
				$("#" + categoryInputId).jqxComboBox('clearSelection');
				
				var tmpSourceOperator = $("#" + operatorInputId).jqxComboBox('source');
	    		tmpSourceOperator._source.localdata = localDataInter;
				$("#" + operatorInputId).jqxComboBox('clearSelection');
				$("#" + operatorInputId).jqxComboBox('source', tmpSourceOperator);
				$("#" + operatorInputId).jqxComboBox('selectedIndex', 0);
				
				if (showOptionPriceTax) {
					$("#" + usePriceWithTax).jqxDropDownList('selectItem', "N");
				} else {
					$("#" + usePriceWithTax).jqxDropDownList('clearSelection');
				}
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
			placeHolder: uiLabelMap.BSEnterToSearch,
			key: "productId",
    		value: "productNameSearch",
			width:'100%',
			height: 25,
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			remoteAutoComplete: true,
			remoteAutoCompleteDelay: 700,
			renderer : null,
			renderSelectedItem : null,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQGetListProductOfCompany' + otherParam, /*&pagesize=0&prodCatalogId=${currentCatalogId?if_exists} TH2: JQGetListProductSellAll*/
			search: function (searchString) {
       	 		//dataAdapter.dataBind();
	        },
	        searchKeyCode: 13, // press enter to search
	        minLength: 1,
	        contentType: 'application/x-www-form-urlencoded',
		};
		new OlbComboBox(comboBox, null, configProductIdList, selectArr);
	};
	var initProductCatIdList = function(comboBox, selectArr){
		var configProductCatIdList = {
			placeHolder: uiLabelMap.BSEnterToSearch,
			key: "productCategoryId",
    		value: "categoryName",
    		width:'100%',
			height: 25,
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			autoComplete: true,
			searchMode: 'containsignorecase',
			remoteAutoComplete: true,
			remoteAutoCompleteDelay: 700,
			useUrl: true,
			url: 'jqxGeneralServicer?sname=JQListCategoryByCatalog&showAll=Y', //&pagesize=0
			search: function (searchString) {
       	 		//dataAdapter.dataBind();
	        },
	        searchKeyCode: 13, // press enter to search
	        minLength: 1,
	        contentType: 'application/x-www-form-urlencoded',
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
			dropDownHeight: '225px',
			placeHolder: uiLabelMap.BSClickToChoose,
		};
		new OlbComboBox(comboBox, inputParamEnumData, configInputParamEnum, selectArr);
	};
	var initPromoActionEnum = function(comboBox, selectArr){
		var configPromoActionEnum = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
		};
		new OlbComboBox(comboBox, actionParamEnumData, configPromoActionEnum, selectArr);
	};
	var initPromoActionOper = function(dropDownObj, selectArr){
		var configPromoActionOper = {
			width:'200',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
			autoDropDownHeight: true,
		};
		new OlbDropDownList(dropDownObj, actionOperEnumsData, configPromoActionOper, selectArr);
	};
	var initPromoActionIsCheckInv = function(dropDownObj, selectArr){
		var configPromoActionOper = {
			width:'200',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: uiLabelMap.BSClickToChoose,
			autoDropDownHeight: true,
		};
		new OlbDropDownList(dropDownObj, actionIsEnumsData, configPromoActionOper, selectArr);
	};
	var initPartyList = function(dropdownButton, dropdownGrid, selectArr, suffixValue, isGroup){
		var otherParam = "";
		if (isGroup) otherParam = "&isGroup=Y"
		var configParty = {
			useUrl: true,
			widthButton: '100%',
			datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
			columns: [
				{text: uiLabelMap.BSPartyId, datafield: 'partyCode', width: '30%'},
				{text: uiLabelMap.BSFullName, datafield: 'fullName'}
			],
			url: 'JQGetListParties' + otherParam,
			useUtilFunc: true,
			source: {
				pagesize: 5,
			},
			key: 'partyId',
			keyCode: 'partyCode',
			description: ['fullName'],
			autoCloseDropDown: true,
			filterable: true
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
	var initRecurrenceInfo = function(dropdownButton, dropdownGrid, selectArr, suffixValue){
		var configRecurrencyInfo = {
				useUrl: true,
				root: 'results',
				widthButton: '100%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
		            {name: 'recurrenceInfoId', type: 'string'}, 
		            {name: 'frequency', type: 'string'},
		            {name: 'byHourList', type: 'string'},
		            {name: 'byDayList', type: 'string'},
				],
				columns: [
				    {text: uiLabelMap.BSRecurrencyInfoId, datafield: 'recurrenceInfoId', width: '20%'},
				    {text: uiLabelMap.BSFrequency, datafield: 'frequency', width: '20%',
				    	cellsrenderer: function(row, colum, value){
				    		for ( let i = 0; i < periodTypes.length ; i++){
				    			if( value == periodTypes[i].periodTypeId){
				    				value = periodTypes[i].description;
				    				break;
				    			}
				    				
				    		}
					        return "<span >" + value + "</span>";
						},
					},
				    {text: uiLabelMap.BSHourList, datafield: 'byHourList', width: '30%'},
				    {text: uiLabelMap.BSDayList, datafield: 'byDayList', width: '30%',
				    	cellsrenderer: function(row, colum, value){
				    		let result;
				    		value = value.split(",");
				    		for (let i = 0; i< value.length; i++){
				    			for ( let j = 0; j < dayEnums.length ; j++){
					    			if( value[i] == dayEnums[j].enumId){
					    				value[i] = dayEnums[j].description;
					    			}
					    				
					    		}
				    		}
				    		result = value[0];
				    		for (let i = 1; i< value.length; i++){
				    			result = result+ "," + value[i] ;
				    		}
				    		
					        return "<span >" + result + "</span>";
						},
				    },
	          	],
		        url: 'JQGetListRecurrenceInfo',
		        useUtilFunc: true,
		        source: {
		        	pagesize: 5,
		        },
		        key: 'recurrenceInfoId',
				displayDetail: false,
				autoCloseDropDown: true
		};
		new OlbDropDownButton($(dropdownButton), $(dropdownGrid), null, configRecurrencyInfo, []);
		
		onSelectDropDownGrid($(dropdownGrid), $(dropdownButton), 'recurrenceInfoId', suffixValue);
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
	var initValidatorEditRuleName = function(){
		var extendRules = [];
		var mapRules = [
				{input: '#we_rn_ruleName', type: 'validInputNotNull'},
				{input: '#we_rn_ruleText', type: 'validInputNotNull'},
            ];
		return new OlbValidator($('#windowPromoRuleNameEdit'), mapRules, extendRules, {position: 'bottom'});
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
		//showEditorRuleName: showEditorRuleName,
		showEditorRuleNameWindow: showEditorRuleNameWindow,
		//blurEditorRuleName: blurEditorRuleName,
		blurEditorRuleNameWindow: blurEditorRuleNameWindow,
		checkAndDisplayInputValue: checkAndDisplayInputValue,
		checkAndDisplayInputValueCond: checkAndDisplayInputValueCond,
		initValidatorEditRuleName: initValidatorEditRuleName,
	};
}()); // OlbPromoRules