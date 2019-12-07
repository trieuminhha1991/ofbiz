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
<#include "loyaltyNewMarcro.ftl"/>
<#if listLoyaltyRule?exists && listLoyaltyRule?size &gt; 0>
	<#assign ruleCount = listLoyaltyRule?size - 1>
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
									<#if listLoyaltyRule?exists && listLoyaltyRule?size &gt; 0>
										<#list listLoyaltyRule as loyaltyRule>
											<li class="<#if loyaltyRule_index == 0>active</#if>" id="recent-tab_o_${loyaltyRule_index}">
												<span class="close-tab" onclick="OlbLoyaltyRules.closeTab(${loyaltyRule_index})"><i class="fa-times-circle open-sans open-sans-index"></i></span>
												<a data-toggle="tab" href="#tab-rule_o_${loyaltyRule_index}" id="recentTabItem_o_${loyaltyRule_index}">
													<span>${loyaltyRule.ruleName?default(uiLabelMap.BSRule)}</span>
													<input type="text" size="30" name="ruleNameTemp_o_${loyaltyRule_index}" id="ruleNameTemp_o_${loyaltyRule_index}" value="${loyaltyRule.ruleName?default(uiLabelMap.BSRule)}"/>
												</a>
											</li>
										</#list>
									<#else>
										<li class="active" id="recent-tab_o_0">
											<span class="close-tab" onclick="OlbLoyaltyRules.closeTab(0)"><i class="fa-times-circle open-sans open-sans-index"></i></span>
											<a data-toggle="tab" href="#tab-rule_o_0" id="recentTabItem_o_0">
												<span>${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if></span>
												<input type="text" size="30" name="ruleNameTemp_o_0" id="ruleNameTemp_o_0" value="${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if>"/>
											</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div><!--.span10-->
						<div class="span2" style="height:34px; text-align:right">
							<a href="javascript:OlbLoyaltyRules.addNewRule();" data-rel="tooltip" title="${uiLabelMap.BSAddRule}" data-placement="bottom" class="button-action"><i class="fa-plus-circle open-sans open-sans-index"></i></a>
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
					<form  method="post" class="form-horizontal basic-custom-form form-window-content-custom" action="<@ofbizUrl>updateLoyaltyAdvance</@ofbizUrl>" name="editLoyaltyRules" id="editLoyaltyRules">
					<#--createLoyaltyCond createLoyaltyAction form-window-content-custom-->
						<div id="tab-content" class="tab-content overflow-visible" style="padding:15px 0">
							<#if listLoyaltyRule?exists && listLoyaltyRule?size &gt; 0>
								<#list listLoyaltyRule as loyaltyRule>
									<input type="hidden" name="loyaltyRuleId_o_${loyaltyRule_index}" value="${loyaltyRule.loyaltyRuleId}"/>
									<input type="hidden" name="isRemoveRule_o_${loyaltyRule_index}" value="N"/>
									<div id="tab-rule_o_${loyaltyRule_index}" class="tab-pane <#if loyaltyRule_index == 0>active</#if>">
										<input type="hidden" size="30" name="ruleName_o_${loyaltyRule_index}" id="ruleName_o_${loyaltyRule_index}" value="${loyaltyRule.ruleName?default(uiLabelMap.BSRule)}"/>
										<div class="row-fuild">
											<div class="span6">
												<#if loyaltyRule.listCond?exists && loyaltyRule.listCond?size &gt; 0>
													<#list loyaltyRule.listCond as loyaltyCond>
														<@buildCondition ruleIndex=loyaltyRule_index condIndex=loyaltyCond_index loyaltyCond=loyaltyCond sizeCond=loyaltyRule.listCond?size/>
													</#list>
													<div id="add-new-condition-container_o_${loyaltyRule_index}">
														<a href="javascript:OlbLoyaltyRules.addNewCondition(${loyaltyRule_index}, ${loyaltyRule.listCond?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddCondition}</a>
													</div>
												<#else>
													<@buildCondition ruleIndex=loyaltyRule_index condIndex=0/>
													<div id="add-new-condition-container_o_0">
														<a href="javascript:OlbLoyaltyRules.addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddCondition}</a>
													</div>
												</#if>
											</div><!--.span6-->
											<div class="span6">
												<#if loyaltyRule.listAction?exists && loyaltyRule.listAction?size &gt; 0>
													<#list loyaltyRule.listAction as loyaltyAction>
														<@buildAction ruleIndex=loyaltyRule_index actionIndex=loyaltyAction_index loyaltyAction=loyaltyAction sizeAction=loyaltyRule.listAction?size/>
													</#list>
													<div id="add-new-action-container_o_${loyaltyRule_index}">
														<a href="javascript:OlbLoyaltyRules.addNewAction(${loyaltyRule_index}, ${loyaltyRule.listAction?size});"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddAction}</a>
													</div>
												<#else>
													<@buildAction ruleIndex=loyaltyRule_index actionIndex=0/>
													<div id="add-new-action-container_o_0">
														<a href="javascript:OlbLoyaltyRules.addNewAction(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddAction}</a>
													</div>
												</#if>
											</div><!--.span6-->
										</div>
									</div><!--#tab-rule_o_0-->
								</#list>
							<#else>
								<div id="tab-rule_o_0" class="tab-pane active">
									<input type="hidden" size="30" name="ruleName_o_0" id="ruleName_o_0" value="${uiLabelMap.BSRule}<#if ruleCount &gt; 0>1</#if>"/>
									<div class="row-fuild">
										<div class="span6">
											<@buildCondition ruleIndex=0 condIndex=0/>
											<div id="add-new-condition-container_o_0">
												<a href="javascript:OlbLoyaltyRules.addNewCondition(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddCondition}</a>
											</div>
										</div><!--.span6-->
										<div class="span6">
											<@buildAction ruleIndex=0 actionIndex=0/>
											<div id="add-new-action-container_o_0">
												<a href="javascript:OlbLoyaltyRules.addNewAction(0, 1);"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.BSAddAction}</a>
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
<#include "loyaltyNewScript.ftl">