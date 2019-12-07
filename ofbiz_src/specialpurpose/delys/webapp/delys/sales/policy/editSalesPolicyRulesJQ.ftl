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
	.form-horizontal .control-group.before-form-legend {
		padding: 0 9px 15px;
		margin-bottom:10px;
	}
</style>
<#if listSalesPolicyRule?exists && listSalesPolicyRule?size &gt; 0>
	<#assign ruleCount = listSalesPolicyRule?size - 1>
<#else>
	<#assign ruleCount = 0>
</#if>
<script type="text/javascript">
	<#if listSalesPolicyRule?exists && listSalesPolicyRule?size &gt; 0>
		var ruleCount = ${listSalesPolicyRule?size - 1};
	<#else>
		var ruleCount = 0;
	</#if>
	
	<#if satementTypeList?exists>
		var satementTypeData = [
			<#list satementTypeList as statementTypeItem>
			{	salesTypeId: '${statementTypeItem.salesTypeId}',
				description: '${StringUtil.wrapString(statementTypeItem.get("description", locale))}',
			},
			</#list>
		];
	<#else>
		var satementTypeData = [];
	</#if>
	
	<#if productList?exists>
		var productData = [
			<#list productList as productItem>
			{	productId: '${productItem.productId}',
				description: '${StringUtil.wrapString(productItem.internalName?default(""))}',
			},
			</#list>
		];
	<#else>
		var productData = [];
	</#if>
	
	<#if productCategoryList?exists>
		var categoryData = [
			<#list productCategoryList as categoryItem>
			{	productCategoryId: '${categoryItem.productCategoryId}',
				description: '${StringUtil.wrapString(categoryItem.categoryName?default(""))}',
			},
			</#list>
		];
	<#else>
		var categoryData = [];
	</#if>
	
	<#if inputParamEnums?exists>
		var inputParamEnumData = [
			<#list inputParamEnums as item>
			{	enumId: '${item.enumId}',
				description: '${StringUtil.wrapString(item.get("description", locale)?default(""))}',
			},
			</#list>
		];
	<#else>
		var inputParamEnumData = [];
	</#if>
	
	<#if salesPolicyActionEnums?exists>
		var salesPolicyActionEnumData = [
			<#list salesPolicyActionEnums as item>
			{	enumId: '${item.enumId}',
				description: '${StringUtil.wrapString(item.get("description", locale)?default(""))}',
			},
			</#list>
		];
	<#else>
		var salesPolicyActionEnumData = [];
	</#if>
	
	<#if condOperEnums?exists><#--'${StringUtil.wrapString(item.get("description", locale)?default(""))}';-->
		var condOperEnumData = [
			<#list condOperEnums as item>
			{	enumId: '${item.enumId}',
				description: <#if "SPC_EQ" == item.enumId>"="<#elseif "SPC_GT" == item.enumId>">"<#elseif "SPC_GTE" == item.enumId>">="<#elseif "SPC_LT" == item.enumId>"<"<#elseif "SPC_LTE" == item.enumId>"<="<#elseif "SPC_NEQ" == item.enumId>"!="</#if>
			},
			</#list>
		];
	<#else>
		var condOperEnumData = [];
	</#if>
	
	<#assign listPaymentParty = Static["com.olbius.util.SalesPartyUtil"].getPaymentSalesPolicyPartyInProperties(delegator)/>
	<#if listPaymentParty?exists>
		var paymentPartyData = [
			<#list listPaymentParty as item>
			{	paymentParty: '${item.paymentParty?default("")}',
				description: '${StringUtil.wrapString(uiLabelMap.get(item.description))}',
			},
			</#list>
		];
	<#else>
		var paymentPartyData = [];
	</#if>
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
									<#if listSalesPolicyRule?exists && listSalesPolicyRule?size &gt; 0>
										<#list listSalesPolicyRule as promoRule>
											<li class="<#if promoRule_index == 0>active</#if>" id="recent-tab_o_${promoRule_index}">
												<span class="close-tab" onclick="closeTab(${promoRule_index})"><i class="fa-times-circle open-sans open-sans-index"></i></span>
												<a data-toggle="tab" href="#tab-rule_o_${promoRule_index}" id="recentTabItem_o_${promoRule_index}">
													<span>${promoRule.ruleName?default(uiLabelMap.DARule)}</span>
													<input type="text" size="30" maxlength="100" name="ruleNameTemp_o_${promoRule_index}" id="ruleNameTemp_o_${promoRule_index}" value="${promoRule.ruleName?default(uiLabelMap.DARule)}"/>
												</a>
											</li>
										</#list>
									<#else>
										<li class="active" id="recent-tab_o_0">
											<span class="close-tab" onclick="closeTab(0)"><i class="fa-times-circle open-sans open-sans-index"></i></span>
											<a data-toggle="tab" href="#tab-rule_o_0" id="recentTabItem_o_0">
												<span>${uiLabelMap.DARule}<#if ruleCount &gt; 0>1</#if></span>
												<input type="text" size="30" maxlength="100" name="ruleNameTemp_o_0" id="ruleNameTemp_o_0" value="${uiLabelMap.DARule}<#if ruleCount &gt; 0>1</#if>"/>
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
					<form  method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>updateSalesPolicyAdvance</@ofbizUrl>" name="editSalesPolicyRuleCondAction" id="editSalesPolicyRuleCondAction">
					<#--createSalesPolicyCond createSalesPolicyAction-->
						<div id="tab-content" class="tab-content overflow-visible" style="padding:0">
							<#if listSalesPolicyRule?exists && listSalesPolicyRule?size &gt; 0>
								<#list listSalesPolicyRule as promoRule>
									<input type="hidden" name="salesPolicyRuleId_o_${promoRule_index}" value="${promoRule.salesPolicyRuleId}"/>
									<input type="hidden" name="isRemoveRule_o_${promoRule_index}" value="N"/>
									<div id="tab-rule_o_${promoRule_index}" class="tab-pane <#if promoRule_index == 0>active</#if>">
										<input type="hidden" size="30" name="ruleName_o_${promoRule_index}" id="ruleName_o_${promoRule_index}" value="${promoRule.ruleName?default(uiLabelMap.DARule)}"/>
										<div class="row-fuild">
											<div class="span6">
												<div class="control-group before-form-legend">
													<label class="control-label required" for="paymentParty_o_${promoRule_index}">${uiLabelMap.DAPaymentParty}</label>
													<div class="controls">
														<div id="paymentParty_o_${promoRule_index}"></div>
													</div>
												</div>
												<#if promoRule.listCond?exists && promoRule.listCond?size &gt; 0>
													<#list promoRule.listCond as promoCond>
														<input type="hidden" name="salesPolicyCondSeqId_c_${promoCond_index}_o_${promoRule_index}" value="${promoCond.salesPolicyCondSeqId}"/>
														<input type="hidden" name="isRemoveCond_c_${promoCond_index}_o_${promoRule_index}" value="N"/>
														<div class="form-legend" id="form-legend_c_${promoCond_index}_o_${promoRule_index}">
															<div class="contain-legend">
																<span class="content-legend text-normal">
																	${uiLabelMap.DACondition} <#if promoRule.listCond?size &gt; 1>${promoCond_index + 1} </#if>&nbsp;
																	<a href="javascript:deleteCond(${promoRule_index}, ${promoCond_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
																</span>
															</div>
															<div class="contain">
																<div class="control-group">
																	<label class="control-label">${uiLabelMap.DAProductName}</label>
																	<div class="controls">
																		<div id="productIdListCond_c_${promoCond_index}_o_${promoRule_index}"></div>
																	</div>
																</div>
																<div class="control-group">
												            		<label class="control-label">${uiLabelMap.DelysCategoryName}</label>
																	<div class="controls">
																		<input type="hidden" name="salesPolicyApplEnumId_c_${promoCond_index}_o_${promoRule_index}" value="PPPA_INCLUDE"/>
													                  	<input type="hidden" name="includeSubCategories_c_${promoCond_index}_o_${promoRule_index}" value="Y"/>
													                  	<div id="productCatIdListCond_c_${promoCond_index}_o_${promoRule_index}"></div>
																	</div>
												           		</div>
												           		<div class="control-group">
																	<label class="control-label">${uiLabelMap.DACondition}</label>
																	<div class="controls">
																		<div class="span10"><div id="inputParamEnumId_c_${promoCond_index}_o_${promoRule_index}" name="inputParamEnumId_c_${promoCond_index}_o_${promoRule_index}"></div></div>
																		<div class="span2"><div id="operatorEnumId_c_${promoCond_index}_o_${promoRule_index}" name="operatorEnumId_c_${promoCond_index}_o_${promoRule_index}"></div></div>
																	</div>
																</div>
												           		<div class="control-group">			
																	<label class="control-label">${uiLabelMap.ProductConditionValue}</label>
																	<div class="controls">		
																		<input type="text" size="25" id="condValue_c_${promoCond_index}_o_${promoRule_index}" name="condValue_c_${promoCond_index}_o_${promoRule_index}" value="${promoCond.condValue?if_exists}" class="span12"/>
																	</div>
																</div>
															</div><!--.contain-->
														</div><!--.form-legend-->
													</#list>
												</#if>
												<div id="add-new-condition-container_o_${promoRule_index}">
													<a href="javascript:addNewCondition(${promoRule_index}, ${promoRule.listCond?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>
												</div>
											</div><!--.span6-->
											<div class="span6">
												<div class="control-group before-form-legend">
													<label class="control-label required" for="salesStatementTypeId_o_${promoRule_index}">${uiLabelMap.DAStatementType}</label>
													<div class="controls">
														<div id="salesStatementTypeId_o_${promoRule_index}"></div>
													</div>
												</div>
												<#if promoRule.listAction?exists && promoRule.listAction?size &gt; 0>
													<#list promoRule.listAction as promoAction>
														<input type="hidden" name="salesPolicyActionSeqId_a_${promoAction_index}_o_${promoRule_index}" value="${promoAction.salesPolicyActionSeqId}"/>
														<input type="hidden" name="isRemoveAction_a_${promoAction_index}_o_${promoRule_index}" value="N"/>
														<div class="form-legend" id="form-legend_a_${promoAction_index}_o_${promoRule_index}">
															<div class="contain-legend">
																<span class="content-legend text-normal">
																	${uiLabelMap.DAAction} <#if promoRule.listAction?size &gt; 1>${promoAction_index + 1} </#if>&nbsp;
																	<a href="javascript:deleteAction(${promoRule_index}, ${promoAction_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
																</span>
															</div>
															<div class="contain">
																<#--<input type="hidden" name="orderAdjustmentTypeId_a_${promoAction_index}_o_${promoRule_index}" value="${promoAction.orderAdjustmentTypeId?default('PROMOTION_ADJUSTMENT')}" />-->
																<div class="control-group">
																	<label class="control-label">${uiLabelMap.DAProductName}</label>
																	<div class="controls">
																		<div id="productIdListAction_a_${promoAction_index}_o_${promoRule_index}"></div>
																	</div>												
																</div>
																<div class="control-group">
												           			<label class="control-label">${uiLabelMap.DelysCategoryName}</label>
																	<div class="controls">
																		<input type="hidden" name="salesPolicyApplEnumIdAction_a_${promoAction_index}_o_${promoRule_index}" value="PPPA_INCLUDE"/>
														              	<input type="hidden" name="includeSubCategoriesAction_a_${promoAction_index}_o_${promoRule_index}" value="Y"/>
														              	<div id="productCatIdListAction_a_${promoAction_index}_o_${promoRule_index}"></div>
																	</div>
												             	</div>
												             	<div class="control-group">
																	<label class="control-label">${uiLabelMap.DAAction}</label>
																	<div class="controls">
																		<div id="salesPolicyActionEnumId_a_${promoAction_index}_o_${promoRule_index}" name="salesPolicyActionEnumId_a_${promoAction_index}_o_${promoRule_index}"></div>
																	</div>
																</div>
																<div class="control-group">
																	<label class="control-label">${uiLabelMap.ProductQuantity}</label>
																	<div class="controls">
																		<input type="text" id="quantity_a_${promoAction_index}_o_${promoRule_index}" name="quantity_a_${promoAction_index}_o_${promoRule_index}" value="${promoAction.quantity?if_exists}" class="span12"/>
																	</div>
																</div>
																<div class="control-group">
																	<label class="control-label">${uiLabelMap.ProductAmount}</label>
																	<div class="controls">
																		<input type="text" id="amount_a_${promoAction_index}_o_${promoRule_index}" name="amount_a_${promoAction_index}_o_${promoRule_index}" value="${promoAction.amount?if_exists}" class="span12"/>
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
													</#list>
												</#if>
												<div id="add-new-action-container_o_${promoRule_index}">
													<a href="javascript:addNewAction(${promoRule_index}, ${promoRule.listAction?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>
												</div>
											</div><!--.span6-->
										</div>
									</div><!--#tab-rule_o_0-->
								</#list>
							<#else>
								<div id="tab-rule_o_0" class="tab-pane active">
									<input type="hidden" size="30" name="ruleName_o_0" id="ruleName_o_0" value="${uiLabelMap.DARule}<#if ruleCount &gt; 0>1</#if>"/>
									<div class="row-fuild">
										<div class="span6">
											<div class="control-group before-form-legend">
												<label class="control-label required" for="paymentParty_o_0">${uiLabelMap.DAPaymentParty}</label>
												<div class="controls">
													<div id="paymentParty_o_0"></div>
													<#--<div id="paymentParty_o_0">
														<div id="jqxPartyGrid"></div>
													</div>-->
												</div>
											</div>
											<input type="hidden" name="isRemoveCond_c_0_o_0" value="N"/>
											<div class="form-legend" id="form-legend_c_0_o_0">
												<div class="contain-legend">
													<span class="content-legend text-normal">
														${uiLabelMap.DACondition} &nbsp;
														<a href="javascript:deleteCond(0, 0);"><i class="fa-times-circle open-sans open-sans-index"></i></a>
													</span>
												</div>
												<div class="contain">
													<div class="control-group">
														<label class="control-label">${uiLabelMap.DAProductName}</label>
														<div class="controls">
															<div id="productIdListCond_c_0_o_0"></div>
														</div>
													</div>
													<div class="control-group">
									            		<label class="control-label">${uiLabelMap.DelysCategoryName}</label>
														<div class="controls">
															<input type="hidden" name="salesPolicyApplEnumId_c_0_o_0" value="PPPA_INCLUDE"/>
										                  	<input type="hidden" name="includeSubCategories_c_0_o_0" value="Y"/>
										                  	<div id="productCatIdListCond_c_0_o_0"></div>
														</div>
									           		</div>
									           		<div class="control-group">
														<label class="control-label">${uiLabelMap.DACondition}</label>
														<div class="controls">
															<div class="span10"><div id="inputParamEnumId_c_0_o_0" name="inputParamEnumId_c_0_o_0"></div></div>
															<div class="span2"><div id="operatorEnumId_c_0_o_0" name="operatorEnumId_c_0_o_0"></div></div>
														</div>
													</div>
									           		<div class="control-group">			
														<label class="control-label">${uiLabelMap.ProductConditionValue}</label>
														<div class="controls">		
															<input type="text" size="25" id="condValue_c_0_o_0" name="condValue_c_0_o_0" class="span12"/>
														</div>
													</div>
												</div><!--.contain-->
											</div>
											<div id="add-new-condition-container_o_0">
												<a href="javascript:addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>
											</div>
										</div><!--.span6-->
										<div class="span6">
											<div class="control-group before-form-legend">
												<label class="control-label required" for="salesStatementTypeId_o_0">${uiLabelMap.DAStatementType}</label>
												<div class="controls">
													<div id="salesStatementTypeId_o_0"></div>
												</div>
											</div>
											<input type="hidden" name="isRemoveAction_a_0_o_0" value="N"/>
											<div class="form-legend" id="form-legend_a_0_o_0">
												<div class="contain-legend">
													<span class="content-legend text-normal">
														${uiLabelMap.DAAction} &nbsp;
														<a href="javascript:deleteAction(0, 0);"><i class="fa-times-circle open-sans open-sans-index"></i></a>
													</span>
												</div>
												<div class="contain">
													<#--<input type="hidden" name="orderAdjustmentTypeId_a_0_o_0" value="PROMOTION_ADJUSTMENT" />-->
													<div class="control-group">
														<label class="control-label">${uiLabelMap.DAProductName}</label>
														<div class="controls">
															<div id="productIdListAction_a_0_o_0"></div>
														</div>
													</div>
													<div class="control-group">
									           			<label class="control-label">${uiLabelMap.DelysCategoryName}</label>
														<div class="controls">
															<input type="hidden" name="salesPolicyApplEnumIdAction_a_0_o_0" value="PPPA_INCLUDE"/>
											              	<input type="hidden" name="includeSubCategoriesAction_a_0_o_0" value="Y"/>
											              	<div id="productCatIdListAction_a_0_o_0"></div>
														</div>
									             	</div>
									             	<div class="control-group">
														<label class="control-label">${uiLabelMap.DAAction}</label>
														<div class="controls">
															<div id="salesPolicyActionEnumId_a_0_o_0" name="salesPolicyActionEnumId_a_0_o_0"></div>
														</div>
													</div>
													<div class="control-group">
														<label class="control-label">${uiLabelMap.ProductQuantity}</label>
														<div class="controls">
															<input type="text" id="quantity_a_0_o_0" name="quantity_a_0_o_0" class="span12"/>
														</div>
													</div>
													<div class="control-group">
														<label class="control-label">${uiLabelMap.ProductAmount}</label>
														<div class="controls">
															<input type="text" id="amount_a_0_o_0" name="amount_a_0_o_0" class="span12"/>
														</div>
													</div>
													<#--<div class="control-group">
														<label class="control-label">${uiLabelMap.ProductItemId}</label>
														<div class="controls">
															<input type="text" name="productId"/>
														</div>
													</div>-->
												</div>
											</div>
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
	
	function addNewCondition(rule_seq_id, cond_seq_id) {
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		var contentBefore = '<input type="hidden" name="isRemoveCond_c_' + cond_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_c_' + cond_seq_id + '_o_' + seq_id + '"></div>';
		$("#add-new-condition-container_o_" + rule_seq_id).before(contentBefore);
		var divSpanFirst = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:first > div.form-legend:last");
		divSpanFirst.append('<div class="contain-legend"><span class="content-legend text-normal">${uiLabelMap.DACondition} ' + (cond_seq_id + 1) + '&nbsp;</span><a href="javascript:deleteCond(' + seq_id + ', ' + cond_seq_id + ');"><i class="fa-times-circle open-sans open-sans-index"></i></a></div><div class="contain"></div>');
		var divSpanFirstContain = $(divSpanFirst).find(".contain");
		
		var content2 = '<div class="control-group">';
		content2 += '<label class="control-label">${uiLabelMap.DAProductName}</label>';
		content2 += '<div class="controls">';
		content2 += '<div id="productIdListCond' + thisSuffix + '"></div>';
		content2 += '</div>';
		content2 += '</div>';
		content2 += '<div class="control-group">';
    	content2 += '<label class="control-label">${uiLabelMap.DelysCategoryName}</label>';
		content2 += '<div class="controls">';
		content2 += '<input type="hidden" name="salesPolicyApplEnumId' + thisSuffix + '" value="PPPA_INCLUDE"/>';
        content2 += '<input type="hidden" name="includeSubCategories' + thisSuffix + '" value="Y"/>';
        content2 += '<div id="productCatIdListCond' + thisSuffix + '"></div>';
		content2 += '</div>';
   		content2 += '</div>';
   		content2 += '<div class="control-group">';
		content2 += '<label class="control-label">${uiLabelMap.DACondition}</label>';
		content2 += '<div class="controls">';
		content2 += '<div class="span10"><div id="inputParamEnumId' + thisSuffix + '" name="inputParamEnumId' + thisSuffix + '"></div></div>';
		content2 += '<div class="span2"><div id="operatorEnumId' + thisSuffix + '" name="operatorEnumId' + thisSuffix + '"></div></div>';
		content2 += '</div>';
		content2 += '</div>';
   		content2 += '<div class="control-group">';		
		content2 += '<label class="control-label">${uiLabelMap.ProductConditionValue}</label>';
		content2 += '<div class="controls">';
		content2 += '<input type="text" size="25" id="condValue' + thisSuffix + '" name="condValue' + thisSuffix + '" class="span12"/>';
		content2 += '</div>';
		content2 += '</div>';
		
		divSpanFirstContain.append(content2);
		initPaymentPartyComboBox($("#paymentParty_o_" + rule_seq_id), []);
		actionScriptBuildCond(rule_seq_id, cond_seq_id);
		var nexCondSeqId = cond_seq_id + 1;
		$("#add-new-condition-container_o_" + rule_seq_id).html('<a href="javascript:addNewCondition(' + rule_seq_id + ', ' + nexCondSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddCondition}</a>');
	}
	
	function addNewAction(rule_seq_id, action_seq_id) {
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + action_seq_id + '_o_' + seq_id;
		var contentBefore = '<input type="hidden" name="isRemoveAction_a_' + action_seq_id + '_o_' + seq_id + '" value="N"/><div class="form-legend" id="form-legend_a_' + action_seq_id + '_o_' + seq_id + '"></div>';
		$("#add-new-action-container_o_" + seq_id).before(contentBefore);
		var divSpanLast = $("#tab-rule_o_" + rule_seq_id + " > div.row-fuild > div.span6:last > div.form-legend:last");
		divSpanLast.append('<div class="contain-legend"><span class="content-legend text-normal">${uiLabelMap.DAAction} ' + (action_seq_id + 1) + '&nbsp;<a href="javascript:deleteAction(' + seq_id + ', ' + action_seq_id + ');"><i class="fa-times-circle open-sans open-sans-index"></i></a></span></div><div class="contain"></div>');
		var divSpanLastContain = $(divSpanLast).find(".contain");
		
		var content3 = '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.DAProductName}</label>';
		content3 += '<div class="controls">';
		content3 += '<div id="productIdListAction' + thisSuffix + '"></div>';
		content3 += '</div>';
		content3 += '</div>';
		content3 += '<div class="control-group">';
   		content3 += '<label class="control-label">${uiLabelMap.DelysCategoryName}</label>';
		content3 += '<div class="controls">';
		content3 += '<input type="hidden" name="salesPolicyApplEnumIdAction' + thisSuffix + '" value="PPPA_INCLUDE"/>';
        content3 += '<input type="hidden" name="includeSubCategoriesAction' + thisSuffix + '" value="Y"/>';
        content3 += '<div id="productCatIdListAction' + thisSuffix + '"></div>';
		content3 += '</div>';
     	content3 += '</div>';
     	content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.DAAction}</label>';
		content3 += '<div class="controls">';
		content3 += '<div id="salesPolicyActionEnumId' + thisSuffix + '" name="salesPolicyActionEnumId' + thisSuffix + '"></div>';
		content3 += '</div>';
		content3 += '</div>';
		content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.ProductQuantity}</label>';
		content3 += '<div class="controls">';
		content3 += '<input type="text" id="quantity' + thisSuffix + '" name="quantity' + thisSuffix + '" class="span12"/>';
		content3 += '</div>';
		content3 += '</div>';
		content3 += '<div class="control-group">';
		content3 += '<label class="control-label">${uiLabelMap.ProductAmount}</label>';
		content3 += '<div class="controls">';
		content3 += '<input type="text" id="amount' + thisSuffix + '" name="amount' + thisSuffix + '" class="span12"/>';
		content3 += '</div>';
		content3 += '</div>';
		
		divSpanLastContain.append(content3);
		initSalesStatementTypeId($("#salesStatementTypeId_o_" + rule_seq_id), []);
		actionScriptBuildAction(rule_seq_id, action_seq_id);
		var nexActionSeqId = action_seq_id + 1;
		$("#add-new-action-container_o_" + rule_seq_id).html('<a href="javascript:addNewAction(' + rule_seq_id + ', ' + nexActionSeqId + ');"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.DAAddAction}</a>');
	}
	
	function actionScriptBuildCond(rule_seq_id, cond_seq_id) {
		var seq_id = rule_seq_id;
		var thisSuffix = '_c_' + cond_seq_id + '_o_' + seq_id;
		$("#condValue" + thisSuffix).jqxInput({height:25});
		
		// list product
        initProductIdList($("#productIdListCond" + thisSuffix), []);
	    
	    // list category
	    initProductCatIdList($("#productCatIdListCond" + thisSuffix), []);
	    
	    // list input param enum id
	    initInputParamEnumId($("#inputParamEnumId" + thisSuffix), ['SPIP_PC_TURN_OVER']);
	    
	    // list cond oper enum id
	    initOperatorEnumId($("#operatorEnumId" + thisSuffix), []);
	}
	
	function actionScriptBuildAction(rule_seq_id, cond_seq_id) {
		var seq_id = rule_seq_id;
		var thisSuffix = '_a_' + cond_seq_id + '_o_' + seq_id;
		$("#quantity" + thisSuffix).jqxInput({height:25});
		$("#amount" + thisSuffix).jqxInput({height:25});
		
		// list product --- Product 2
	    initProductIdList($("#productIdListAction" + thisSuffix), []);
	    
	    // list category --- Product 2
	    initProductCatIdList($("#productCatIdListAction" + thisSuffix), []);
	    
	    // list input param enum id --- Action 2 : salesPolicyActionEnumId
        initSalesPolicyActionEnumId($("#salesPolicyActionEnumId" + thisSuffix), []);
        checkAndDisplayInputValue($("#salesPolicyActionEnumId" + thisSuffix));
		$("#salesPolicyActionEnumId" + thisSuffix).on("change", function(){
			checkAndDisplayInputValue($(this));
		});
	}
	
	function addNewRule() {
		// remove class active
		$("#recent-tab > li").removeClass("active");
		$("#tab-content > div").removeClass("active");
		
		// add new tab
		ruleCount ++;
		var id = "tab-rule_o_" + ruleCount;
		var divTabHeadNew = "<li class='active' id='recent-tab_o_" + ruleCount + "'>";
		divTabHeadNew += "<span class='close-tab' onclick='closeTab(" + ruleCount + ")'><i class='fa-times-circle open-sans open-sans-index'></i></span>";
		divTabHeadNew += "<a data-toggle='tab' href='#" + id + "' id='recentTabItem_o_" + ruleCount + "'>";
		divTabHeadNew += "<span>${uiLabelMap.DARule} " + (ruleCount + 1) + "</span>";
		divTabHeadNew += "<input type='text' size='30' maxlength='100' name='ruleNameTemp_o_" + ruleCount + "' id='ruleNameTemp_o_" + ruleCount + "' value='${uiLabelMap.DARule} " + (ruleCount + 1) + "'/>";
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
		
		var contentBefore = '<div class="control-group before-form-legend">';
		contentBefore += '<label class="control-label required" for="paymentParty_o_' + seq_id + '">${uiLabelMap.DAPaymentParty}</label>';
		contentBefore += '<div class="controls">';
		contentBefore += '<div id="paymentParty_o_' + seq_id + '"></div>';
		contentBefore += '</div>';
		contentBefore += '</div>';
		$("#add-new-condition-container_o_" + seq_id).before(contentBefore);
		
		var contentBefore2 = '<div class="control-group before-form-legend">';
		contentBefore2 += '<label class="control-label required" for="salesStatementTypeId_o_' + seq_id + '">${uiLabelMap.DAStatementType}</label>';
		contentBefore2 += '<div class="controls">';
		contentBefore2 += '<div id="salesStatementTypeId_o_' + seq_id + '"></div>';
		contentBefore2 += '</div>';
		contentBefore2 += '</div>';
		$("#add-new-action-container_o_" + seq_id).before(contentBefore2);
		
		addNewCondition(seq_id, 0);
		addNewAction(seq_id, 0);
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
		if ($(thisElement) == undefined || $(thisElement).length <= 0 || $(thisElement).length > 100) {
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
// ================================================== READY ===========================================************===================================
	$(function(){
		<#if listSalesPolicyRule?exists && listSalesPolicyRule?size &gt; 0>
			<#list listSalesPolicyRule as promoRule>
				$('#recentTabItem_o_${promoRule_index}').dblclick(function(){
					showEditorRuleName(this);
				});
				
				$('#ruleNameTemp_o_${promoRule_index}').on("blur", function(){
					blurEditorRuleName(this);
				});
				
				<#if promoRule.salesStatementTypeId?exists>
					var salesStatementTypeIdSelected = ['${promoRule.salesStatementTypeId}'];
				<#else>
					var salesStatementTypeIdSelected = [];
				</#if>
				initSalesStatementTypeId($("#salesStatementTypeId_o_${promoRule_index}"), salesStatementTypeIdSelected);
				
				<#if promoRule.paymentParty?exists>
					var paymentPartySelected = ['${promoRule.paymentParty}'];
				<#else>
					var paymentPartySelected = [];
				</#if>
				initPaymentPartyComboBox($("#paymentParty_o_${promoRule_index}"), paymentPartySelected);
				
				<#if promoRule.listCond?exists && promoRule.listCond?size &gt; 0>
					<#list promoRule.listCond as promoCond>
						var thisSuffix = "_c_${promoCond_index}_o_${promoRule_index}";
						$("#condValue" + thisSuffix).jqxInput({height:25});
						
						// list product
						<#if promoCond.listProd?exists && promoCond.listProd?size &gt; 0>
					    	var productIdListCondSelected = [
					    	<#list promoCond.listProd as prod>
					    		'${prod.productId}',
					    	</#list>
					    	];
					    <#else>
					    	var productIdListCondSelected = [];
					    </#if>
						initProductIdList($("#productIdListCond" + thisSuffix), productIdListCondSelected);
					    
					    // list category
					    <#if promoCond.listCate?exists && promoCond.listCate?size &gt; 0>
					    	var productCatIdListCondSelected = [
					    	<#list promoCond.listCate as category>
					    		'${category.productCategoryId}',
					    	</#list>
					    	];
					    <#else>
					    	var productCatIdListCondSelected = [];
					    </#if>
					    initProductCatIdList($("#productCatIdListCond" + thisSuffix), productCatIdListCondSelected);
					    
					    // list input param enum id
					    <#if promoCond.inputParamEnumId?exists>
					    	var inputParamEnumIdSelected = ['${promoCond.inputParamEnumId}'];
					    <#else>
					    	var inputParamEnumIdSelected = [];
					    </#if>
						initInputParamEnumId($("#inputParamEnumId" + thisSuffix), inputParamEnumIdSelected);
					    
					    // list cond oper enum id
					    <#if promoCond.operatorEnumId?exists>
					    	var operatorEnumIdSelected = ['${promoCond.operatorEnumId}'];
					    <#else>
					    	var operatorEnumIdSelected = [];
					    </#if>
						initOperatorEnumId($("#operatorEnumId" + thisSuffix), operatorEnumIdSelected);
					</#list>
				</#if>
				
				<#if promoRule.listAction?exists && promoRule.listAction?size &gt; 0>
					<#list promoRule.listAction as promoAction>
						$("#quantity_a_${promoAction_index}_o_${promoRule_index}").jqxInput({height:25});
						$("#amount_a_${promoAction_index}_o_${promoRule_index}").jqxInput({height:25});
						
						// ------------------------ Product 2 ----------------------
						<#if promoAction.listProd?exists && promoAction.listProd?size &gt; 0>
					    	var productCatIdListActionSelected = [
					    	<#list promoAction.listProd as prod>
					    		'${prod.productId}',
					    	</#list>
					    	];
					    <#else>
					    	var productCatIdListActionSelected = [];
					    </#if>
						initProductIdList($("#productIdListAction_a_${promoAction_index}_o_${promoRule_index}"), productCatIdListActionSelected);
					    <#if promoAction.listProd?exists && promoAction.listProd?size &gt; 0>
					    	<#list promoAction.listProd as prod>
					    		$("#productIdListAction_a_${promoAction_index}_o_${promoRule_index}").jqxComboBox('selectItem',"${prod.productId}");
					    	</#list>
					    </#if>
					    
					    // Product 2 
					    <#if promoAction.listCate?exists && promoAction.listCate?size &gt; 0>
					    	var productCatIdListActionSelected = [
					    	<#list promoAction.listCate as category>
					    		'${category.productCategoryId}',
					    	</#list>
					    	];
					    <#else>
					    	var productCatIdListActionSelected = [];
					    </#if>
				 		initProductCatIdList($("#productCatIdListAction_a_${promoAction_index}_o_${promoRule_index}"), productCatIdListActionSelected);
					    
					    // Action 2 : salesPolicyActionEnumId
					    <#if promoAction.salesPolicyActionEnumId?exists>
					    	var salesPolicyActionEnumIdSelected = ['${promoAction.salesPolicyActionEnumId}'];
					    <#else>
					    	var salesPolicyActionEnumIdSelected = [];
					    </#if>
						initSalesPolicyActionEnumId($("#salesPolicyActionEnumId_a_${promoAction_index}_o_${promoRule_index}"), salesPolicyActionEnumIdSelected);
					</#list>
				</#if>
			</#list>
		<#else>
			$('#recentTabItem_o_0').dblclick(function(){
				showEditorRuleName(this);
			});
			
			$('#ruleNameTemp_o_0').on("blur", function(){
				blurEditorRuleName(this);
			});
			
			initSalesStatementTypeId($("#salesStatementTypeId_o_0"), []);
			initPaymentPartyComboBox($("#paymentParty_o_0"), []);
			
			actionScriptBuildCond(0, 0);
			actionScriptBuildAction(0, 0);
		</#if>
		
		$("[id^='salesPolicyActionEnumId']").on("change", function(){
			checkAndDisplayInputValue($(this));
		});
	});
	
	function checkAndDisplayInputValue(comboBox) {
		var comboBoxObj = $(comboBox).jqxComboBox('getSelectedItem');
		var idObj = $(comboBox).attr("id");
		var sufixId = idObj.substring("salesPolicyActionEnumId".length, idObj.length);
		if (comboBoxObj != undefined && comboBoxObj != null) {
			var value = comboBoxObj.value;
			var quantityInputId = "quantity" + sufixId;
			var amountInputId = "amount" + sufixId;
			var showQuantity = false;
			var showAmount = false;
			if ("POLICY_AWD" == value) {
				showQuantity = false;
				showAmount = true;
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
		}
	}
	
	function initInputParamEnumId(comboBox, selectArr){
		var sourceInputEnum = {
			localdata: inputParamEnumData,
	        datatype: "array",
	        datafields: [
	            { name: 'enumId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterInputEnum = new $.jqx.dataAdapter(sourceInputEnum, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $(comboBox).jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $(comboBox).jqxComboBox({source: dataAdapterInputEnum, multiSelect: false, width:'100%', height: 25,
	    	selectedIndex: 0,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "enumId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label;
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterInputEnum.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterInputEnum.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
	
	function initOperatorEnumId(comboBox, selectArr){
		var sourceCondOper = {
			localdata: condOperEnumData,
	        datatype: "array",
	        datafields: [
	            { name: 'enumId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterCondOper = new $.jqx.dataAdapter(sourceCondOper, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $(comboBox).jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $(comboBox).jqxComboBox({source: dataAdapterCondOper, multiSelect: false, width:'100%', height: 25,
	    	selectedIndex: 0,
	    	dropDownWidth: 'auto', 
	    	autoDropDownHeight: true, 
	    	placeHolder: "", 
	    	displayMember: "description", 
	    	valueMember: "enumId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label;
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterCondOper.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterCondOper.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
	
	function initProductIdList(comboBox, selectArr){
		var sourceProduct2 = {
			localdata: productData,
	        datatype: "array",
	        datafields: [
	            { name: 'productId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterProduct2 = new $.jqx.dataAdapter(sourceProduct2, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $(comboBox).jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $(comboBox).jqxComboBox({source: dataAdapterProduct2, multiSelect: true, width: '100%', height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "productId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterProduct2.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterProduct2.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
	
	function initProductCatIdList(comboBox, selectArr){
		var sourceCategory2 = {
			localdata: categoryData,
	        datatype: "array",
	        datafields: [
	            { name: 'productCategoryId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterCategory2 = new $.jqx.dataAdapter(sourceCategory2, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#productCatIdListAction").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $(comboBox).jqxComboBox({source: dataAdapterCategory2, multiSelect: true, width: '100%', height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "productCategoryId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterCategory2.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterCategory2.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
	
	function initSalesPolicyActionEnumId(comboBox, selectArr){
		var sourcePromoActionEnum = {
			localdata: salesPolicyActionEnumData,
	        datatype: "array",
	        datafields: [
	            { name: 'enumId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterPromoActionEnum = new $.jqx.dataAdapter(sourcePromoActionEnum, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#salesPolicyActionEnumId").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $(comboBox).jqxComboBox({source: dataAdapterPromoActionEnum, multiSelect: false, width:'100%', height: 25,
	    	selectedIndex: 0,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "enumId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label;
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterPromoActionEnum.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterPromoActionEnum.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
	
	function initPaymentPartyComboBox(comboBox, selectArr) {
		var sourcePaymentParty = {
			localdata: paymentPartyData,
	        datatype: "array",
	        datafields: [
	            { name: 'paymentParty' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterPaymentParty = new $.jqx.dataAdapter(sourcePaymentParty, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = comboBox.jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    comboBox.jqxComboBox({source: dataAdapterPaymentParty, 
	    	multiSelect: false, 
	    	width: 200, 
	    	height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "paymentParty", 
	    	autoDropDownHeight: true, 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterPaymentParty.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterPaymentParty.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
	
	function initSalesStatementTypeId(comboBox, selectArr){
		var sourceStatementType = {
			localdata: satementTypeData,
	        datatype: "array",
	        datafields: [
	            { name: 'salesTypeId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterStatementType = new $.jqx.dataAdapter(sourceStatementType, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = comboBox.jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    comboBox.jqxComboBox({source: dataAdapterStatementType, 
	    	multiSelect: false, 
	    	width: 200, 
	    	height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "salesTypeId", 
	    	autoDropDownHeight: true, 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterStatementType.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterStatementType.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
</script>
