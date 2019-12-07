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
	.ruleTextDisplay {
		margin-top:-19px; 
		margin-bottom:20px; 
		font-style: italic
	}
</style>
<#include "promotionNewMarcro.ftl"/>
<#if listPromoRule?exists && listPromoRule?size &gt; 0>
	<#assign ruleCount = listPromoRule?size - 1>
<#else>
	<#assign ruleCount = 0>
</#if>
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
												<span class="close-tab" onclick="OlbPromoRules.closeTab(${promoRule_index})"><i class="fa-times-circle open-sans open-sans-index"></i></span>
												<a data-toggle="tab" href="#tab-rule_o_${promoRule_index}" id="recentTabItem_o_${promoRule_index}"><#-- title="${promoRule.ruleText?default(uiLabelMap.BSRule)}"-->
													<span>${promoRule.ruleName?default(uiLabelMap.BSRule)}</span>
													<#--<input type="text" size="30" name="ruleNameTemp_o_${promoRule_index}" id="ruleNameTemp_o_${promoRule_index}" value="${promoRule.ruleName?default(uiLabelMap.BSRule)}"/>-->
												</a>
											</li>
										</#list>
									<#else>
										<li class="active" id="recent-tab_o_0">
											<span class="close-tab" onclick="OlbPromoRules.closeTab(0)"><i class="fa-times-circle open-sans open-sans-index"></i></span>
											<a data-toggle="tab" href="#tab-rule_o_0" id="recentTabItem_o_0"><#-- title="${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if>"-->
												<span>${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if></span>
												<#--<input type="text" size="30" name="ruleNameTemp_o_0" id="ruleNameTemp_o_0" value="${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if>"/>-->
											</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div><!--.span10-->
						<div class="span2" style="height:34px; text-align:right">
							<a href="javascript:OlbPromoRules.addNewRule();" data-rel="tooltip" title="${uiLabelMap.BSAddRule}" data-placement="bottom" class="button-action"><i class="fa-plus-circle open-sans open-sans-index"></i></a>
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
					<form  method="post" class="form-horizontal basic-custom-form form-window-content-custom" action="<@ofbizUrl>updateProductPromoAdvance</@ofbizUrl>" name="editProductPromoRules" id="editProductPromoRules">
					<#--createProductPromoCond createProductPromoAction form-window-content-custom-->
						<div id="tab-content" class="tab-content overflow-visible" style="padding:15px 0">
							<#if listPromoRule?exists && listPromoRule?size &gt; 0>
								<#list listPromoRule as promoRule>
									<input type="hidden" name="productPromoRuleId_o_${promoRule_index}" value="${promoRule.productPromoRuleId}"/>
									<input type="hidden" name="isRemoveRule_o_${promoRule_index}" value="N"/>
									<div id="tab-rule_o_${promoRule_index}" class="tab-pane <#if promoRule_index == 0>active</#if>">
										<input type="hidden" size="30" name="ruleName_o_${promoRule_index}" id="ruleName_o_${promoRule_index}" value="${promoRule.ruleName?default(uiLabelMap.BSRule)}"/>
										<input type="hidden" size="30" name="ruleText_o_${promoRule_index}" id="ruleText_o_${promoRule_index}" value="${promoRule.ruleText?default(uiLabelMap.BSRule)}"/>
										<div id="ruleTextDisplay_o_${promoRule_index}" class="blue ruleTextDisplay">
											${uiLabelMap.BSDescription}: <span>${promoRule.ruleText?default(uiLabelMap.BSRule)}</span>
										</div>
										<div class="row-fuild">
											<div class="span6">
												<#if promoRule.listCond?exists && promoRule.listCond?size &gt; 0>
													<#list promoRule.listCond as promoCond>
														<@buildCondition ruleIndex=promoRule_index condIndex=promoCond_index promoCond=promoCond sizeCond=promoRule.listCond?size/>
													</#list>
													<div id="add-new-condition-container_o_${promoRule_index}">
														<a href="javascript:OlbPromoRules.addNewCondition(${promoRule_index}, ${promoRule.listCond?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddCondition}</a>
													</div>
												<#else>
													<@buildCondition ruleIndex=promoRule_index condIndex=0/>
													<div id="add-new-condition-container_o_0">
														<a href="javascript:OlbPromoRules.addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddCondition}</a>
													</div>
												</#if>
											</div><!--.span6-->
											<div class="span6">
												<#if promoRule.listAction?exists && promoRule.listAction?size &gt; 0>
													<#list promoRule.listAction as promoAction>
														<@buildAction ruleIndex=promoRule_index actionIndex=promoAction_index promoAction=promoAction sizeAction=promoRule.listAction?size/>
													</#list>
													<div id="add-new-action-container_o_${promoRule_index}">
														<a href="javascript:OlbPromoRules.addNewAction(${promoRule_index}, ${promoRule.listAction?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddAction}</a>
													</div>
												<#else>
													<@buildAction ruleIndex=promoRule_index actionIndex=0/>
													<div id="add-new-action-container_o_0">
														<a href="javascript:OlbPromoRules.addNewAction(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddAction}</a>
													</div>
												</#if>
											</div><!--.span6-->
										</div>
									</div><!--#tab-rule_o_0-->
								</#list>
							<#else>
								<div id="tab-rule_o_0" class="tab-pane active">
									<input type="hidden" size="30" name="ruleName_o_0" id="ruleName_o_0" value="${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if>"/>
									<input type="hidden" size="30" name="ruleText_o_0" id="ruleText_o_0" value="${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if>"/>
									<div id="ruleTextDisplay_o_0" class="blue ruleTextDisplay">
										${uiLabelMap.BSDescription}: <span>${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if></span>
									</div>
									<div class="row-fuild">
										<div class="span6">
											<@buildCondition ruleIndex=0 condIndex=0/>
											<div id="add-new-condition-container_o_0">
												<a href="javascript:OlbPromoRules.addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddCondition}</a>
											</div>
										</div><!--.span6-->
										<div class="span6">
											<@buildAction ruleIndex=0 actionIndex=0/>
											<div id="add-new-action-container_o_0">
												<a href="javascript:OlbPromoRules.addNewAction(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddAction}</a>
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

<div id="windowPromoRuleNameEdit" style="display:none">
	<div>${uiLabelMap.BSEditPromoRuleInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="we_rn_ruleIndex" value=""/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required" for="we_rn_ruleName">${uiLabelMap.BSRuleName}</label>
						</div>
						<div class='span9'>
							<input type="text" id="we_rn_ruleName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required" for="we_rn_ruleText">${uiLabelMap.BSRuleText}</label>
						</div>
						<div class='span9'>
							<textarea type="text" id="we_rn_ruleText" class="span12" value=""></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="we_rn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="we_rn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div id="alterpopupWindowCategory" style="display:none">
	<div>${uiLabelMap.BSListCategory}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<input type="hidden" id="categoryObjIdCurrentSelected" value=""/>
					<div id="jqxgridCategory"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_cate_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_cate_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div id="alterpopupWindowProduct" style="display:none">
	<div>${uiLabelMap.BSListProduct}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<input type="hidden" id="productObjIdCurrentSelected" value=""/>
					<div id="jqxgridProduct"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_prod_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_prod_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<#include "promotionNewScript.ftl">
<#include "promotionNewRecurrencyPopup.ftl">
