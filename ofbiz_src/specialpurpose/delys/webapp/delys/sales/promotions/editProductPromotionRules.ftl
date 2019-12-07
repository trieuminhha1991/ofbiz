<#macro buildCondition ruleIndex=0 condIndex=0 promoCond="" sizeCond=0>
	<#assign promoRule_index = ruleIndex?default(0) />
	<#assign promoCond_index = condIndex?default(0) />
	<#if promoCond?has_content><input type="hidden" name="productPromoCondSeqId_c_${promoCond_index}_o_${promoRule_index}" value="${promoCond.productPromoCondSeqId}"/></#if>
	<input type="hidden" name="isRemoveCond_c_${promoCond_index}_o_${promoRule_index}" value="N"/>
	<div class="form-legend" id="form-legend_c_${promoCond_index}_o_${promoRule_index}">
		<div class="contain-legend">
			<span class="content-legend text-normal">
				${uiLabelMap.DACondition} <#if sizeCond &gt; 1>${promoCond_index + 1} </#if>&nbsp;
				<a href="javascript:deleteCond(${promoRule_index}, ${promoCond_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
			</span>
		</div>
		<div class="contain">
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DACondition}</label>
				<div class="controls">
					<div class="span10"><div id="inputParamEnumId_c_${promoCond_index}_o_${promoRule_index}" name="inputParamEnumId_c_${promoCond_index}_o_${promoRule_index}"></div></div>
					<div class="span2"><div id="operatorEnumId_c_${promoCond_index}_o_${promoRule_index}" name="operatorEnumId_c_${promoCond_index}_o_${promoRule_index}"></div></div>
				</div>
			</div>
       		<div class="control-group">
				<label class="control-label">${uiLabelMap.DAProductName}</label>
				<div class="controls">
					<div id="productIdListCond_c_${promoCond_index}_o_${promoRule_index}"></div>
				</div>
			</div>
			<div class="control-group">
        		<label class="control-label">${uiLabelMap.DelysCategoryName}</label>
				<div class="controls">
					<input type="hidden" name="productPromoApplEnumId_c_${promoCond_index}_o_${promoRule_index}" value="PPPA_INCLUDE"/>
                  	<input type="hidden" name="includeSubCategories_c_${promoCond_index}_o_${promoRule_index}" value="Y"/>
                  	<div id="productCatIdListCond_c_${promoCond_index}_o_${promoRule_index}"></div>
				</div>
       		</div>
       		<div class="control-group">			
				<label class="control-label">${uiLabelMap.ProductConditionValue}</label>
				<div class="controls">		
					<input type="text" size="25" id="condValue_c_${promoCond_index}_o_${promoRule_index}" name="condValue_c_${promoCond_index}_o_${promoRule_index}" value="<#if promoCond?has_content>${promoCond.condValue?if_exists}</#if>" class="span12"/>
				</div>
			</div>
			<#--productPromo.productPromoTypeId == "EXHIBITED"-->
			<div class="control-group exhibited-group">
				<label class="control-label">${uiLabelMap.DelysExhibitedAt}</label>
				<div class="controls">
					<input type="text" size="25" name="condExhibited_c_${promoCond_index}_o_${promoRule_index}" id="condExhibited_c_${promoCond_index}_o_${promoRule_index}" value="<#if promoCond?has_content>${promoCond.condExhibited?if_exists}</#if>" class="span12">
				</div>
			</div>
			<div class="control-group exhibited-group">
				<label class="control-label">${uiLabelMap.DANotes}</label>
				<div class="controls">
					<input type="text" size="25" name="notes_c_${promoCond_index}_o_${promoRule_index}" id="notes_c_${promoCond_index}_o_${promoRule_index}" value="<#if promoCond?has_content>${promoCond.notes?if_exists}</#if>" class="span12">
				</div>
			</div>
			<#--end productPromo.productPromoTypeId == "EXHIBITED"-->
		</div><!--.contain-->
	</div><!--.form-legend-->
</#macro>
<#macro buildAction ruleIndex=0 actionIndex=0 promoAction="" sizeAction=0>
	<#assign promoRule_index = ruleIndex?default(0) />
	<#assign promoAction_index = actionIndex?default(0) />
	<#if promoAction?has_content><input type="hidden" name="productPromoActionSeqId_a_${promoAction_index}_o_${promoRule_index}" value="${promoAction.productPromoActionSeqId}"/></#if>
	<input type="hidden" name="isRemoveAction_a_${promoAction_index}_o_${promoRule_index}" value="N"/>
	<div class="form-legend" id="form-legend_a_${promoAction_index}_o_${promoRule_index}">
		<div class="contain-legend">
			<span class="content-legend text-normal">
				${uiLabelMap.DAAction} <#if sizeAction &gt; 1>${promoAction_index + 1} </#if>&nbsp;
				<a href="javascript:deleteAction(${promoRule_index}, ${promoAction_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
			</span>
		</div>
		<div class="contain">
			<input type="hidden" name="orderAdjustmentTypeId_a_${promoAction_index}_o_${promoRule_index}" value="<#if promoAction?has_content>${promoAction.orderAdjustmentTypeId?default('PROMOTION_ADJUSTMENT')}<#else>PROMOTION_ADJUSTMENT</#if>" />
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DAAction}</label>
				<div class="controls">
					<div id="productPromoActionEnumId_a_${promoAction_index}_o_${promoRule_index}" name="productPromoActionEnumId_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DAProductName}</label>
				<div class="controls">
					<div id="productIdListAction_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>												
			</div>
			<input type="hidden" name="productPromoApplEnumIdAction_a_${promoAction_index}_o_${promoRule_index}" value="PPPA_INCLUDE"/>
			<#--
			<div class="control-group">
       			<label class="control-label">${uiLabelMap.DelysCategoryName}</label>
				<div class="controls">
	              	<input type="hidden" name="includeSubCategoriesAction_a_${promoAction_index}_o_${promoRule_index}" value="Y"/>
	              	<div id="productCatIdListAction_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>
         	</div>
			-->
         	<div class="control-group">
				<label class="control-label">${uiLabelMap.ProductQuantity}</label>
				<div class="controls">
					<input type="text" id="quantity_a_${promoAction_index}_o_${promoRule_index}" name="quantity_a_${promoAction_index}_o_${promoRule_index}" value="<#if promoAction?has_content>${promoAction.quantity?if_exists}</#if>" class="span12"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DAAmountOrPercent}</label>
				<div class="controls">
					<input type="text" id="amount_a_${promoAction_index}_o_${promoRule_index}" name="amount_a_${promoAction_index}_o_${promoRule_index}" value="<#if promoAction?has_content>${promoAction.amount?if_exists}</#if>" class="span12"/>
				</div>
			</div>
			<#--<div class="control-group">
				<label class="control-label">${uiLabelMap.ProductItemId}</label>
				<div class="controls">
					<input type="text" name="productId"/>
				</div>
			</div>-->
		</div>
	</div><!--.form-legend-->
</#macro>
<#--
<div class="row-fluid">
	<h4>${uiLabelMap.DAAbbPromotionRules}</h4>
	<div>${uiLabelMap.DAProcessing} ... </div>
</div>
-->
<style type="text/css">
	.tabbable .nav-tabs li {
		position:relative;
	}
	.tabbable .nav-tabs li .close-tab {
	    position: absolute;
	    top: 0;
	    right: 0;
	    z-index: 20;
	    //color: #d3413b;
	    display:none;
	    line-height: 14px;
	}
	.tabbable .nav-tabs li .close-tab:hover {
		cursor:pointer;
		color:#b52c26;
	}
	.tabbable .nav-tabs li:hover .close-tab {
		display:block;
	}
	.tabbable .nav-tabs li .close-tab .fa-times-circle:before {
		margin-right: 1px;
	}
	input[id^="ruleNameTemp"] {
		margin-bottom:0;
		line-height: 25px;
		font-family: 'Open Sans';
  		font-size: 13px;
  		width:80px;
  		padding:0;
  		display:none;
	}
	.nav-tabs>li>a {
		padding-top:5px;
		padding-bottom:5px;
	}
	.nav-tabs>li>a>span {
		padding: 3px 0;
		display:block
	}
	div[id^="add-new-condition-container"], div[id^="add-new-action-container"] {
		text-align:center;
	}
</style>
<#if listPromoRule?exists && listPromoRule?size &gt; 0>
	<#assign ruleCount = listPromoRule?size - 1>
<#else>
	<#assign ruleCount = 0>
</#if>
<script type="text/javascript">
	<#if listPromoRule?exists && listPromoRule?size &gt; 0>
		var ruleCount = ${listPromoRule?size - 1};
	<#else>
		var ruleCount = 0;
	</#if>
	
	<#if productList?exists>
		var productData = [
		<#list productList as productItem>
			{'productId': '${productItem.productId}',
			'description': '${StringUtil.wrapString(productItem.internalName?default(""))}'},
		</#list>
		];
	<#else>
		var productData = [];
	</#if>
	
	<#if productCategoryList?exists>
		var categoryData = [
		<#list productCategoryList as categoryItem>
			{'productCategoryId': '${categoryItem.productCategoryId}',
			'description': '${StringUtil.wrapString(categoryItem.categoryName?default(""))}'},
		</#list>
		];
	<#else>
		var categoryData = [];
	</#if>
	
	<#if inputParamEnums?exists>
		var inputParamEnumData = [
		<#list inputParamEnums as item>
			{'enumId': '${item.enumId}',
			'description': '${StringUtil.wrapString(item.get("description", locale)?default(""))}'},
		</#list>
		];
	<#else>
		var inputParamEnumData = [];
	</#if>
	
	<#if productPromoActionEnums?exists>
		var productPromoActionEnumData = [
		<#list productPromoActionEnums as item>
			{'enumId': '${item.enumId}',
			'description': '${StringUtil.wrapString(item.get("description", locale)?default(""))}'},
		</#list>
		];
	<#else>
		var productPromoActionEnumData = [];
	</#if>
	
	var condOperEnumData = new Array();
	<#list condOperEnums as item>
		var row = {};
		row['enumId'] = '${item.enumId}';
		var description = ""; <#--'${StringUtil.wrapString(item.get("description", locale)?default(""))}';-->
		<#if "PPC_EQ" == item.enumId>
			description = "=";
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
		condOperEnumData[${item_index}] = row;
	</#list>
</script>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab">
									<#if listPromoRule?exists && listPromoRule?size &gt; 0>
										<#list listPromoRule as promoRule>
											<li class="<#if promoRule_index == 0>active</#if>" id="recent-tab_o_${promoRule_index}">
												<span class="close-tab" onclick="closeTab(${promoRule_index})"><i class="fa-times-circle open-sans open-sans-index"></i></span>
												<a data-toggle="tab" href="#tab-rule_o_${promoRule_index}" id="recentTabItem_o_${promoRule_index}">
													<span>${promoRule.ruleName?default(uiLabelMap.DARule)}</span>
													<input type="text" size="30" name="ruleNameTemp_o_${promoRule_index}" id="ruleNameTemp_o_${promoRule_index}" value="${promoRule.ruleName?default(uiLabelMap.DARule)}"/>
												</a>
											</li>
										</#list>
									<#else>
										<li class="active" id="recent-tab_o_0">
											<span class="close-tab" onclick="closeTab(0)"><i class="fa-times-circle open-sans open-sans-index"></i></span>
											<a data-toggle="tab" href="#tab-rule_o_0" id="recentTabItem_o_0">
												<span>${uiLabelMap.DARule}<#if ruleCount &gt; 0>1</#if></span>
												<input type="text" size="30" name="ruleNameTemp_o_0" id="ruleNameTemp_o_0" value="${uiLabelMap.DARule}<#if ruleCount &gt; 0>1</#if>"/>
											</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div><!--.span10-->
						<div class="span2" style="height:34px; text-align:right">
							<a href="javascript:addNewRule();" data-rel="tooltip" title="${uiLabelMap.DAAddRule}" data-placement="bottom" class="button-action"><i class="fa-plus-circle open-sans open-sans-index"></i></a>
						</div><!--.span2-->
					</div><!--.row-fluid-->
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div><!--.widget-header-->

			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<form  method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>updateProductPromoAdvance</@ofbizUrl>" name="editProductPromoRuleCondAction" id="editProductPromoRuleCondAction">
					<#--createProductPromoCond createProductPromoAction-->
						<div id="tab-content" class="tab-content overflow-visible" style="padding:8px 0">
							<#if listPromoRule?exists && listPromoRule?size &gt; 0>
								<#list listPromoRule as promoRule>
									<input type="hidden" name="productPromoRuleId_o_${promoRule_index}" value="${promoRule.productPromoRuleId}"/>
									<input type="hidden" name="isRemoveRule_o_${promoRule_index}" value="N"/>
									<div id="tab-rule_o_${promoRule_index}" class="tab-pane <#if promoRule_index == 0>active</#if>">
										<input type="hidden" size="30" name="ruleName_o_${promoRule_index}" id="ruleName_o_${promoRule_index}" value="${promoRule.ruleName?default(uiLabelMap.DARule)}"/>
										<div class="row-fuild">
											<div class="span6">
												<#if promoRule.listCond?exists && promoRule.listCond?size &gt; 0>
													<#list promoRule.listCond as promoCond>
														<@buildCondition ruleIndex=promoRule_index condIndex=promoCond_index promoCond=promoCond sizeCond=promoRule.listCond?size/>
													</#list>
													<div id="add-new-condition-container_o_${promoRule_index}">
														<a href="javascript:addNewCondition(${promoRule_index}, ${promoRule.listCond?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>
													</div>
												<#else>
													<@buildCondition ruleIndex=promoRule_index condIndex=0/>
													<div id="add-new-condition-container_o_0">
														<a href="javascript:addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>
													</div>
												</#if>
											</div><!--.span6-->
											<div class="span6">
												<#if promoRule.listAction?exists && promoRule.listAction?size &gt; 0>
													<#list promoRule.listAction as promoAction>
														<@buildAction ruleIndex=promoRule_index actionIndex=promoAction_index promoAction=promoAction sizeAction=promoRule.listAction?size/>
													</#list>
													<div id="add-new-action-container_o_${promoRule_index}">
														<a href="javascript:addNewAction(${promoRule_index}, ${promoRule.listAction?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>
													</div>
												<#else>
													<@buildAction ruleIndex=promoRule_index actionIndex=0/>
													<div id="add-new-action-container_o_0">
														<a href="javascript:addNewAction(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>
													</div>
												</#if>
											</div><!--.span6-->
										</div>
									</div><!--#tab-rule_o_0-->
								</#list>
							<#else>
								<div id="tab-rule_o_0" class="tab-pane active">
									<input type="hidden" size="30" name="ruleName_o_0" id="ruleName_o_0" value="${uiLabelMap.DARule}<#if ruleCount &gt; 0>1</#if>"/>
									<div class="row-fuild">
										<div class="span6">
											<@buildCondition ruleIndex=0 condIndex=0/>
											<div id="add-new-condition-container_o_0">
												<a href="javascript:addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>
											</div>
										</div><!--.span6-->
										<div class="span6">
											<@buildAction ruleIndex=0 actionIndex=0/>
											<div id="add-new-action-container_o_0">
												<a href="javascript:addNewAction(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>
											</div>
										</div><!--.span6-->
									</div>
								</div><!--#tab-rule_o_0-->
							</#if>
						</div><!--.tab-content-->
					</form>
				</div><!--.widget-main-->
			</div><!--.widget-body-->
		</div><!--.widget-box-->
	</div>
</div>
<script type="text/javascript">
	function closeTab(seq_id) {
		if ($("#recent-tab_o_" + seq_id) != undefined && $("#recent-tab_o_" + seq_id).length > 0) {
			$("#recent-tab_o_" + seq_id).remove();
		}
		if ($("#tab-rule_o_" + seq_id) != undefined && $("#tab-rule_o_" + seq_id).length > 0) {
			$("#tab-rule_o_" + seq_id).remove();
		}
		if ($('input[name="isRemoveRule_o_' + seq_id + '"]') != undefined) {
			$('input[name="isRemoveRule_o_' + seq_id + '"]').val("Y");
		}
	}
	
	function deleteCond(rule_seq_id, cond_seq_id) {
		var thisSuffix = "_c_" + cond_seq_id + "_o_" + rule_seq_id;
		if ($('input[name="isRemoveCond' + thisSuffix + '"]') != undefined) {
			$('input[name="isRemoveCond' + thisSuffix + '"]').val("Y");
			$("#form-legend" + thisSuffix).hide();
		}
	}
	
	function deleteAction(rule_seq_id, action_seq_id) {
		var thisSuffix = "_a_" + action_seq_id + "_o_" + rule_seq_id;
		if ($('input[name="isRemoveAction' + thisSuffix + '"]') != undefined) {
			$('input[name="isRemoveAction' + thisSuffix + '"]').val("Y");
			$("#form-legend" + thisSuffix).hide();
		}
	}
	
	function addNewRule() {
		// remove class active
		$("#recent-tab > li").removeClass("active");
		$("#tab-content > div").removeClass("active");
		/*
		if (ruleCount == 0) {
			if ($("#recent-tab > li:first > a") != undefined && $("#recent-tab > li:first > a").length > 0) {
				var content1 = "<span>${StringUtil.wrapString(uiLabelMap.DARule)} " + (ruleCount + 1) + "</span>";
				content1 += "<input type='text' size='30' name='ruleNameTemp_o_" + ruleCount + "' id='ruleNameTemp_o_" + ruleCount + "' value='${uiLabelMap.DARule} " + (ruleCount + 1) + "'/>";;
				$("#recent-tab > li:first > a").html(content1);
				blurEditorRuleName($("#recent-tab > li:first > a"));
			}
			if ($("#ruleName_o_" + ruleCount) != undefined && $("#ruleName_o_" + ruleCount).length > 0) {
				$("#ruleName_o_" + ruleCount).val("${StringUtil.wrapString(uiLabelMap.DARule)} " + (ruleCount + 1));
			}
		}
		*/
		
		// add new tab
		ruleCount ++;
		var id = "tab-rule_o_" + ruleCount;
		var divTabHeadNew = "<li class='active' id='recent-tab_o_" + ruleCount + "'>";
		divTabHeadNew += "<span class='close-tab' onclick='closeTab(" + ruleCount + ")'><i class='fa-times-circle open-sans open-sans-index'></i></span>";
		divTabHeadNew += "<a data-toggle='tab' href='#" + id + "' id='recentTabItem_o_" + ruleCount + "'>";
		divTabHeadNew += "<span>${uiLabelMap.DARule} " + (ruleCount + 1) + "</span>";
		divTabHeadNew += "<input type='text' size='30' name='ruleNameTemp_o_" + ruleCount + "' id='ruleNameTemp_o_" + ruleCount + "' value='${uiLabelMap.DARule} " + (ruleCount + 1) + "'/>";
		divTabHeadNew += "</a>"
		divTabHeadNew += "</li>";
		$("#recent-tab").append(divTabHeadNew);
		var divTabContentNew = "<input type='hidden' name='isRemoveRule_o_" + ruleCount + "' value='N'/><div id='" + id + "' class='tab-pane active'></div><!--#" + id + "-->";
		$("#tab-content").append(divTabContentNew);
		//var divTabContentNewVar = $("#" + id);
		buildContentInTab(ruleCount, id);
		//divTabContentNewVar.append(content);
		
		$('#recentTabItem_o_' + ruleCount).dblclick(function(){
			showEditorRuleName(this);
		});
		
		$('#ruleNameTemp_o_' + ruleCount).on("blur", function(){
			blurEditorRuleName(this);
		});
	}
	
	function buildContentInTab(seq_id, div_id) {
		// seq_id is sequence id of list tab. ex: 1, 2, 3, ...
		var divContent = $("#" + div_id);
		if (divContent == undefined || divContent.length < 1) return false;
		var content = '<input type="hidden" size="30" name="ruleName_o_' + seq_id + '" id="ruleName_o_' + seq_id + '" value="${uiLabelMap.DARule} ' + (seq_id + 1) + '"/>';
		divContent.append(content);
		
		var content = '<div class="row-fuild">';
		content += '<div class="span6">';
		content += '<div id="add-new-condition-container_o_' + seq_id + '">';
		content += '<a href="javascript:addNewCondition(' + seq_id + ', 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>';
		content += '</div>';
		content += '</div><!--.span6-->';
		content += '<div class="span6">';
		content += '<div id="add-new-action-container_o_' + seq_id + '">';
		content += '<a href="javascript:addNewAction(' + seq_id + ', 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>';
		content += '</div>';
		content += '</div><!--.span6-->';
		content += '</div>';
		divContent.append(content);
		addNewCondition(seq_id, 0);
		addNewAction(seq_id, 0);
	}
	
	function addNewCondition(rule_seq_id, cond_seq_id) {
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		$("#add-new-condition-container_o_" + rule_seq_id).before('<input type="hidden" name="isRemoveCond_c_' + cond_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_c_' + cond_seq_id + '_o_' + seq_id + '"></div>');
		var divSpanFirst = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:first > div.form-legend:last");
		divSpanFirst.append('<div class="contain-legend"><span class="content-legend text-normal">${uiLabelMap.DACondition} ' + (cond_seq_id + 1) + '&nbsp;</span><a href="javascript:deleteCond(' + seq_id + ', ' + cond_seq_id + ');"><i class="fa-times-circle open-sans open-sans-index"></i></a></div><div class="contain"></div>');
		var divSpanFirstContain = $(divSpanFirst).find(".contain");
		
		var productPromoTypeId = $("#productPromoTypeId").val();
		var isExhibited = false;
		if ("EXHIBITED" == productPromoTypeId) {
			isExhibited = true;
		}
		
		var content2 = '<div class="control-group">';
		content2 += '<label class="control-label">${uiLabelMap.DACondition}</label>';
		content2 += '<div class="controls">';
		content2 += '<div class="span10"><div id="inputParamEnumId' + thisSuffix + '" name="inputParamEnumId' + thisSuffix + '"></div></div>';
		content2 += '<div class="span2"><div id="operatorEnumId' + thisSuffix + '" name="operatorEnumId' + thisSuffix + '"></div></div>';
		content2 += '</div>';
		content2 += '</div>';
   		content2 += '<div class="control-group">';
		content2 += '<label class="control-label">${uiLabelMap.DAProductName}</label>';
		content2 += '<div class="controls">';
		content2 += '<div id="productIdListCond' + thisSuffix + '"></div>';
		content2 += '</div>';
		content2 += '</div>';
		content2 += '<div class="control-group">';
    	content2 += '<label class="control-label">${uiLabelMap.DelysCategoryName}</label>';
		content2 += '<div class="controls">';
		content2 += '<input type="hidden" name="productPromoApplEnumId' + thisSuffix + '" value="PPPA_INCLUDE"/>';
        content2 += '<input type="hidden" name="includeSubCategories' + thisSuffix + '" value="Y"/>';
        content2 += '<div id="productCatIdListCond' + thisSuffix + '"></div>';
		content2 += '</div>';
		content2 += '</div>';
		content2 += '<div class="control-group">';		
		content2 += '<label class="control-label">${uiLabelMap.ProductConditionValue}</label>';
		content2 += '<div class="controls">';
		content2 += '<input type="text" size="25" id="condValue' + thisSuffix + '" name="condValue' + thisSuffix + '" class="span12"/>';
		content2 += '</div>';
   		content2 += '</div>';
   		if (isExhibited) {
			content2 += '<div class="control-group exhibited-group" style="display:block">';
		} else {
			content2 += '<div class="control-group exhibited-group">';
		}
		content2 += '<label class="control-label">${uiLabelMap.DelysExhibitedAt}</label>';
		content2 += '<div class="controls">';
		content2 += '<input type="text" size="25" name="condExhibited' + thisSuffix + '" id="condExhibited' + thisSuffix + '" class="span12">';
		content2 += '</div>';
		content2 += '</div>';
		if (isExhibited) {
			content2 += '<div class="control-group exhibited-group" style="display:block">';
		} else {
			content2 += '<div class="control-group exhibited-group">';
		}
		content2 += '<label class="control-label">${uiLabelMap.DANotes}</label>';
		content2 += '<div class="controls">';
		content2 += '<input type="text" size="25" name="notes' + thisSuffix + '" id="notes' + thisSuffix + '" class="span12">';
		content2 += '</div>';
		content2 += '</div>';
		
		divSpanFirstContain.append(content2);
		actionScriptBuildCond(rule_seq_id, cond_seq_id, {});
		var nexCondSeqId = cond_seq_id + 1;
		$("#add-new-condition-container_o_" + rule_seq_id).html('<a href="javascript:addNewCondition(' + rule_seq_id + ', ' + nexCondSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>');
	}
	
	function addNewAction(rule_seq_id, action_seq_id) {
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + action_seq_id + '_o_' + seq_id;
		$("#add-new-action-container_o_" + seq_id).before('<input type="hidden" name="isRemoveAction_a_' + action_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_a_' + action_seq_id + '_o_' + seq_id + '"></div>');
		var divSpanLast = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:last > div.form-legend:last");
		divSpanLast.append('<div class="contain-legend"><span class="content-legend text-normal">${uiLabelMap.DAAction} ' + (action_seq_id + 1) + '&nbsp;<a href="javascript:deleteAction(' + seq_id + ', ' + action_seq_id + ');"><i class="fa-times-circle open-sans open-sans-index"></i></a></span></div><div class="contain"></div>');
		var divSpanLastContain = $(divSpanLast).find(".contain");
		
		var content3 = '<input type="hidden" name="orderAdjustmentTypeId' + thisSuffix + '" value="PROMOTION_ADJUSTMENT" />';
		content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.DAAction}</label>';
		content3 += '<div class="controls">';
		content3 += '<div id="productPromoActionEnumId' + thisSuffix + '" name="productPromoActionEnumId' + thisSuffix + '"></div>';
		content3 += '</div>';
		content3 += '</div>';
		content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.DAProductName}</label>';
		content3 += '<div class="controls">';
		content3 += '<div id="productIdListAction' + thisSuffix + '"></div>';
		content3 += '</div>';
		content3 += '</div>';
		<#--
		content3 += '<div class="control-group">';
   		content3 += '<label class="control-label">${uiLabelMap.DelysCategoryName}</label>';
		content3 += '<div class="controls">';
		-->
		content3 += '<input type="hidden" name="productPromoApplEnumIdAction' + thisSuffix + '" value="PPPA_INCLUDE"/>';
        content3 += '<input type="hidden" name="includeSubCategoriesAction' + thisSuffix + '" value="Y"/>';
      	<#-- content3 += '<div id="productCatIdListAction' + thisSuffix + '"></div>';
		content3 += '</div>';
     	content3 += '</div>';-->
     	content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.ProductQuantity}</label>';
		content3 += '<div class="controls">';
		content3 += '<input type="text" id="quantity' + thisSuffix + '" name="quantity' + thisSuffix + '" class="span12"/>';
		content3 += '</div>';
		content3 += '</div>';
		content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.DAAmountOrPercent}</label>';
		content3 += '<div class="controls">';
		content3 += '<input type="text" id="amount' + thisSuffix + '" name="amount' + thisSuffix + '" class="span12"/>';
		content3 += '</div>';
		content3 += '</div>';
		
		divSpanLastContain.append(content3);
		actionScriptBuildAction(rule_seq_id, action_seq_id, {});
		var nexActionSeqId = action_seq_id + 1;
		$("#add-new-action-container_o_" + rule_seq_id).html('<a href="javascript:addNewAction(' + rule_seq_id + ', ' + nexActionSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>');
	}
	
	function actionScriptBuildCond(rule_seq_id, cond_seq_id, config) {
		var productSelectArr = config.productSelectArr ? config.productSelectArr : [];
		var categorySelectArr = config.categorySelectArr ? config.categorySelectArr : [];
		var inputParamSelectArr = config.inputParamSelectArr ? config.inputParamSelectArr : ['PPIP_PRODUCT_QUANT'];
		var operatorSelectArr = config.operatorSelectArr ? config.operatorSelectArr : [];
		
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		$("#condValue" + thisSuffix).jqxInput({height:25});
		$("#condExhibited" + thisSuffix).jqxInput({height:25});
		$("#notes" + thisSuffix).jqxInput({height:25});
		
		// list product
		initProductIdList($("#productIdListCond" + thisSuffix), productSelectArr);
	    
	    // list category
	    initProductCatIdList($("#productCatIdListCond" + thisSuffix), categorySelectArr);
	    
	    // list input param enum id
	    initInputParamEnum($("#inputParamEnumId" + thisSuffix), inputParamSelectArr);
	    
	    // list cond oper enum id
		initOperatorEnum($("#operatorEnumId" + thisSuffix), operatorSelectArr);
		
		checkAndDisplayInputValueCond($("#inputParamEnumId" + thisSuffix));
		$("#inputParamEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValueCond($(this));
		});
	}
	
	function actionScriptBuildAction(rule_seq_id, cond_seq_id, config) {
		var productSelectArr = config.productSelectArr ? config.productSelectArr : [];
		<#--var categorySelectArr = config.categorySelectArr ? config.categorySelectArr : [];-->
		var promoActionSelectArr = config.promoActionSelectArr ? config.promoActionSelectArr : [];
		
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + cond_seq_id + '_o_' + seq_id;
		$("#quantity" + thisSuffix).jqxInput({height:25});
		$("#amount" + thisSuffix).jqxInput({height:25});
		
		// list product
	    initProductIdList($("#productIdListAction" + thisSuffix), productSelectArr);
	    
	    // list category
	    <#--initProductCatIdList($("#productCatIdListAction" + thisSuffix), categorySelectArr);-->
	    
	    // list input param enum id
		initPromoActionEnum($("#productPromoActionEnumId" + thisSuffix), promoActionSelectArr);
		checkAndDisplayInputValue($("#productPromoActionEnumId" + thisSuffix));
		$("#productPromoActionEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValue($(this));
		});
	}
	
	function showEditorRuleName(thisElement) {
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
	}
	
	function blurEditorRuleName(thisElement) {
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
	}
// ================================================== READY ==============================================================================
	
	$(function(){
		<#if listPromoRule?exists && listPromoRule?size &gt; 0>
			<#list listPromoRule as promoRule>
				$('#recentTabItem_o_${promoRule_index}').dblclick(function(){
					showEditorRuleName(this);
				});
				
				$('#ruleNameTemp_o_${promoRule_index}').on("blur", function(){
					blurEditorRuleName(this);
				});
				
				<#if promoRule.listCond?exists && promoRule.listCond?size &gt; 0>
					<#list promoRule.listCond as promoCond>
						var config = {};
						// ------------------------ List product ---------------------------------------------
						<#if promoCond.listProd?exists && promoCond.listProd?size &gt; 0>
					    	var productIdListCondSelected = [
					    	<#list promoCond.listProd as prod>
					    		'${prod.productId}',
					    	</#list>
					    	];
					    	config.productSelectArr = productIdListCondSelected;
					    </#if>
					    // ------------------------ List category --------------------------------------------
					    <#if promoCond.listCate?exists && promoCond.listCate?size &gt; 0>
					    	var productCatIdListCondSelected = [
					    	<#list promoCond.listCate as category>
					    		'${category.productCategoryId}',
					    	</#list>
					    	];
					    	config.categorySelectArr = productCatIdListCondSelected;
					    </#if>
					    // ------------------------ List input param enum id ---------------------------------
					    <#if promoCond.inputParamEnumId?exists>
					    	var inputParamEnumSelected = ['${promoCond.inputParamEnumId}'];
					    	config.inputParamSelectArr = inputParamEnumSelected;
					    </#if>
					    // ------------------------ List cond oper enum id -----------------------------------
					    <#if promoCond.operatorEnumId?exists>
					    	var operatorEnumIdSelected = ['${promoCond.operatorEnumId}'];
					    	config.operatorSelectArr = operatorEnumIdSelected;
					    </#if>
					    
						actionScriptBuildCond(${promoRule_index}, ${promoCond_index}, config);
					</#list>
				<#else>
					actionScriptBuildCond(${promoRule_index}, 0, {});
				</#if>
				
				<#if promoRule.listAction?exists && promoRule.listAction?size &gt; 0>
					<#list promoRule.listAction as promoAction>
						var config;
						// ------------------------ Product 2 ------------------------------------------------
						<#if promoAction.listProd?exists && promoAction.listProd?size &gt; 0>
					    	var productIdListActionSelected = [
					    	<#list promoAction.listProd as prod>
					    		'${prod.productId}',
					    	</#list>
					    	];
					    	config.productSelectArr = productIdListActionSelected;
					    </#if>
						// ------------------------ Product 2 ------------------------------------------------
					    <#--<#if promoAction.listCate?exists && promoAction.listCate?size &gt; 0>
					    	var productCatIdListActionSelected = [
					    	<#list promoAction.listCate as category>
					    		'${category.productCategoryId}',
					    	</#list>
					    	];
					    	config.categorySelectArr = productCatIdListActionSelected;
					    -->
					    // ------------------------ Action 2 : productPromoActionEnumId ----------------------
					    <#if promoAction.productPromoActionEnumId?exists>
					    	var productPromoActionEnumIdSelected = ['${promoAction.productPromoActionEnumId}'];
					    	config.promoActionSelectArr = productPromoActionEnumIdSelected;
					    </#if>
					    
					    actionScriptBuildAction(${promoRule_index}, ${promoAction_index}, config);
					</#list>
				<#else>
					actionScriptBuildAction(${promoRule_index}, 0, {});
				</#if>
			</#list>
		<#else>
			$('#recentTabItem_o_0').dblclick(function(){
				showEditorRuleName(this);
			});
			
			$('#ruleNameTemp_o_0').on("blur", function(){
				blurEditorRuleName(this);
			});
			
			actionScriptBuildCond(0, 0, {});
			actionScriptBuildAction(0, 0, {});
		</#if>
		
		$("[id^='productPromoActionEnumId']").on("change", function(){
			checkAndDisplayInputValue($(this));
		});
	});
	
	function checkAndDisplayInputValue(comboBox) {
		var comboBoxObj = $(comboBox).jqxComboBox('getSelectedItem');
		var idObj = $(comboBox).attr("id");
		var sufixId = idObj.substring("productPromoActionEnumId".length, idObj.length);
		if (comboBoxObj != undefined && comboBoxObj != null) {
			var value = comboBoxObj.value;
			var quantityInputId = "quantity" + sufixId;
			var amountInputId = "amount" + sufixId;
			var productInputId = "productIdListAction" + sufixId;
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
				var parentQuantityObj = $("#" + quantityInputId).closest(".control-group");
				if (parentQuantityObj) parentQuantityObj.show();
			} else {
				var parentQuantityObj = $("#" + quantityInputId).closest(".control-group");
				if (parentQuantityObj) parentQuantityObj.hide();
			}
			if (showAmount) {
				var parentAmountObj = $("#" + amountInputId).closest(".control-group");
				if (parentAmountObj) parentAmountObj.show();
			} else {
				var parentAmountObj = $("#" + amountInputId).closest(".control-group");
				if (parentAmountObj) parentAmountObj.hide();
			}
			
			var parentProductObj = $("#" + productInputId).closest(".control-group");
			if (showProduct) {
				if (parentProductObj) parentProductObj.show();
			} else {
				if (parentProductObj) parentProductObj.hide();
			}
		}
	}
	
	function checkAndDisplayInputValueCond(comboBox) {
		var comboBoxObj = $(comboBox).jqxComboBox('getSelectedItem');
		var idObj = $(comboBox).attr("id");
		var sufixId = idObj.substring("inputParamEnumId".length, idObj.length);
		if (comboBoxObj != undefined && comboBoxObj != null) {
			var value = comboBoxObj.value;
			var productInputId = "productIdListCond" + sufixId;
			var categoryInputId = "productCatIdListCond" + sufixId;
			var condValueInputId = "condValue" + sufixId;
			var showProduct = false;
			var showInput = false;
			var showParty = false;
			var showPartyGroup = false;
			var showRoleType = false;
			var showPartyType = false;
			var showInputOther = false;
			if ("PPIP_PRODUCT_QUANT" == value || "PPIP_PRODUCT_TOTAL" == value || "PPIP_PRODUCT_AMOUNT" == value 
					|| "PPIP_PRODUCT_QUANT" == value) {
				showProduct = true;
				showInput = true;
			} else if ("PPIP_ORDER_TOTAL" == value || "PPIP_NEW_ACCT" == value || "PPIP_RECURRENCE" == value
					|| "PPIP_ORST_YEAR" == value || "PPIP_ORST_LAST_YEAR" == value || "PPIP_LPMUP_AMT" == value
					|| "PPIP_LPMUP_PER" == value || "PPIP_ORDER_SHIPTOTAL" == value || "PPIP_SERVICE" == value) {
				showInput = true;
			} else if ("PPIP_PARTY_ID" == value) {
				showParty = true;
				showInput = true; // todo
			} else if ("PPIP_PARTY_GRP_MEM" == value) {
				showPartyGroup = true;
				showInput = true; // todo
			} else if ("PPIP_PARTY_CLASS" == value) {
				showPartyType = true;
				showInput = true; // todo
			} else if ("PPIP_ROLE_TYPE" == value) {
				showRoleType = true;
				showInput = true; // todo
			} else if ("PPIP_ORST_HIST" == value) {
				showInput = true;
				showInputOther = true;
			}
			
			var parentCondInputObj = $("#" + condValueInputId).closest(".control-group");
			if (parentCondInputObj) {
				if (showInput) { parentCondInputObj.show(); } 
				else { parentCondInputObj.hide(); }
			}
			var parentProductObj = $("#" + productInputId).closest(".control-group");
			var parentCategoryObj = $("#" + categoryInputId).closest(".control-group");
			if (showProduct) { 
				if (parentProductObj) parentProductObj.show();
				if (parentCategoryObj) parentCategoryObj.show();
			} else {
				if (parentProductObj) parentProductObj.hide();
				if (parentCategoryObj) parentCategoryObj.hide();
			}
		}
	}
	
	function initOperatorEnum(comboBox, selectArr){
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
		jSalesCommon.initComboBox(comboBox, condOperEnumData, configOperatorEnum, selectArr);
	}
	
	function initProductIdList(comboBox, selectArr){
		var configProductIdList = {
			width:'100%',
			height: 25,
			key: "productId",
    		value: "description",
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
		};
		jSalesCommon.initComboBox(comboBox, productData, configProductIdList, selectArr);
	}
	
	function initProductCatIdList(comboBox, selectArr){
		var configProductCatIdList = {
			width:'100%',
			height: 25,
			key: "productCategoryId",
    		value: "description",
    		displayDetail: true,
			dropDownWidth: 'auto',
			multiSelect: true,
			placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
		};
		jSalesCommon.initComboBox(comboBox, categoryData, configProductCatIdList, selectArr);
	}
	
	function initInputParamEnum(comboBox, selectArr){
		var configInputParamEnum = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
		};
		jSalesCommon.initComboBox(comboBox, inputParamEnumData, configInputParamEnum, selectArr);
	}
	
	function initPromoActionEnum(comboBox, selectArr){
		var configPromoActionEnum = {
			width:'100%',
			height: 25,
			key: "enumId",
    		value: "description",
    		displayDetail: false,
			selectedIndex: 0,
			dropDownWidth: 'auto',
			placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
		};
		jSalesCommon.initComboBox(comboBox, productPromoActionEnumData, configPromoActionEnum, selectArr);
	}
</script>