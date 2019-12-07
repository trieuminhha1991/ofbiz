<@jqGridMinimumLib />
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script src="/basemarketingresources/js/createPlan.js"></script>
<script>
	<#if parameters.id?exists>
		<#assign plan = delegator.findOne("MarketingPlan", {"marketingPlanId": parameters.id}, false)!/>
	</#if>
	<#if parameters.parentPlanId?exists>
		<#assign parentPlan = delegator.findOne("MarketingPlan", {"marketingPlanId": parameters.parentPlanId}, false)!/>
	</#if>
	var uiLabelMap = {
		UpdateSuccessfully : "${uiLabelMap.UpdateSuccessfully}",
		CommonRequired : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
		UpdateFailure: "${StringUtil.wrapString(uiLabelMap.UpdateFailure?default(''))}",
		DataNotChange: "${StringUtil.wrapString(uiLabelMap.DataNotChange?default(''))}",
		MarketingPlanIdRequireNoSpace: "${StringUtil.wrapString(uiLabelMap.MarketingPlanIdRequireNoSpace?default(''))}",
		ThruDateLargerThanFromDate: "${StringUtil.wrapString(uiLabelMap.ThruDateLargerThanFromDate?default(''))}",
		FromDateSmallerThanThruDate: "${StringUtil.wrapString(uiLabelMap.FromDateSmallerThanThruDate?default(''))}",
		clearString: "${StringUtil.wrapString(uiLabelMap.ClearString?default(''))}",
		todayString: "${StringUtil.wrapString(uiLabelMap.Today?default(''))}",
		CreateError: "${StringUtil.wrapString(uiLabelMap.CreateError?default(''))}",
	};
	var planid = "${parameters.id?if_exists}";
	var parentPlanId = "${parameters.parentPlanId?if_exists}";
	var url = "createMarketingPlanAndItem";
	if(planid){
		url = "updateMarketingPlanAndItem";
	}
	<#assign marketingType = delegator.findList("MarketingType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "MARKETING"), null, null, null, false)/>
	var marketingType = [<#list marketingType as item>
		{<#assign description = StringUtil.wrapString(item.name?if_exists) />
			'marketingTypeId' : '${item.marketingTypeId}',
			'description' : "${description}"
		},
	</#list>];
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "MKTG_PLAN_STATUS"}, null, false) />
	var statusData = [<#list statusList as statusItem>{<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />statusId: '${statusItem.statusId}',description: "${description}"},</#list>];
</script>
<div id="createContentWrapper">
	<#include "planHeader.ftl"/>
	<div class="widget-box margin-top10">
		<div class="widget-header widget-header-blue widget-header-flat">
			<h4 class="smaller"> ${uiLabelMap.MarketAnalysis}</h4>
		</div>
		<div class="widget-body no-padding-top">
			<div class="widget-main">
				<div class="tabbable tabs-left">
					<ul class="nav nav-tabs" id="planContentTab">
						<li class="active">
							<a data-toggle="tab" href="#insightTab">
								<i class="fa fa-area-chart"></i>
								${uiLabelMap.MarketInsight}
							</a>
						</li>
						<li>
							<a data-toggle="tab" href="#objectiveTab">
								<i class="fa fa-list"></i>
								${uiLabelMap.Objective}
							</a>
						</li>
						<li>
							<a data-toggle="tab" href="#swotTab">
								<i class="fa fa-key"></i>
								${uiLabelMap.Swot}
							</a>
						</li>
						<li>
							<a data-toggle="tab" href="#pestTab">
								<i class="fa fa-bank"></i>
								${uiLabelMap.Pest}
							</a>
						</li>
						<li>
							<a data-toggle="tab" href="#comparisonTab">
								<i class="fa fa-exchange"></i>
								${uiLabelMap.Comparison}
							</a>
						</li>
						<li>
							<a data-toggle="tab" href="#customerTab">
								<i class="fa fa-users"></i>
								${uiLabelMap.CustomerGroupTarget}
							</a>
						</li>
						<li>
							<a data-toggle="tab" href="#strategyTab">
								<i class="fa fa-list-ol"></i>
								${uiLabelMap.Strategy}
							</a>
						</li>
					</ul>

					<div class="tab-content no-right-padding">
						<div id="insightTab" class="tab-pane active">
							<div class='row-fluid'>
								<div class='span12'>
									<div id="insight" data-value="${insight?if_exists.description?if_exists}" data-id="${insight?if_exists.contentId?if_exists}"></div>
								</div>
							</div>
						</div>
						<div id="objectiveTab" class="tab-pane">
							<div class='row-fluid'>
								<div class='span12'>
									<div id="objective" data-value="${objective?if_exists.description?if_exists}" data-id="${objective?if_exists.contentId?if_exists}"></div>
								</div>
							</div>
						</div>
						<div id="swotTab" class="tab-pane">
							<div class='row-fluid'>
								<div class='span12'>
									<div id="swot" data-value="${swot?if_exists.description?if_exists}" data-id="${swot?if_exists.contentId?if_exists}"></div>
								</div>
							</div>
						</div>
						<div id="pestTab" class="tab-pane">
							<div class='row-fluid'>
								<div class='span12'>
									<div id="pest" data-value="${pest?if_exists.description?if_exists}" data-id="${pest?if_exists.contentId?if_exists}"></div>
								</div>
							</div>
						</div>
						<div id="comparisonTab" class="tab-pane">
							<div class='row-fluid'>
								<div class='span12'>
									<div id="comparison" data-value="${comparison?if_exists.description?if_exists}" data-id="${comparison?if_exists.contentId?if_exists}"></div>
								</div>
							</div>
						</div>
						<div id="customerTab" class="tab-pane">
							<div class='row-fluid'>
								<div class='span12'>
									<div id="customer" data-value="${customer?if_exists.description?if_exists}" data-id="${customer?if_exists.contentId?if_exists}"></div>
								</div>
							</div>
						</div>
						<div id="strategyTab" class="tab-pane">
							<div class='row-fluid'>
								<div class='span12'>
									<#if parameters.id?exists>
										<#assign customLoadFunction="true"/>
										<#assign showtoolbar="true"/>
										<#assign showdetail="false"/>
										<#assign autoheight="false"/>
										<#include "listPlan.ftl"/>
									<#else>
										<center><b>${uiLabelMap.CreateStrategyNotify}</b></center>
									</#if>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="control-action">
	<button id="cancelPlan" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
	<button id="savePlan" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
</div>
