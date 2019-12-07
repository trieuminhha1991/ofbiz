<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<h4 class="smaller lighter blue" style="margin: 5px 0px 10px 0px !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.BIECreateImportPlan}
</h4>
<script>
	var customTimePeriod = null;
	var partySelected = null;
	var salesForecastId = null;
	var currencyUom = null;
	var listProductSelected = [];
	var listPeriods = [];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	
</script>
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
		<div class="wizard-steps-steps">
			<ul class="wizard-steps wizard-steps-square">
				<li data-target="#step1" class="active" style="pointer-events: none;">
					<span class="step">1. ${uiLabelMap.EnterCommonInformation}</span>
				</li>
				<li data-target="#step2" style="pointer-events: none;">
					<span class="step">2. ${uiLabelMap.BIECalculate}</span>
				</li>
				<li data-target="#step3" style="pointer-events: none;">
					<span class="step">3. ${uiLabelMap.BIEConfirmInfo}</span>
				</li>
			</ul>
		</div>
		<div class="wizard-steps-actions">
			<div>
				<span><a href="javascript:TransferTemplateObj.reloadPages();" class="btn margin-left-2 btn-primary"><i class="icon-refresh icon-only"></i></a></span>
			</div>
		</div>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div id="containerNotify"></div>
		<div class="step-pane active" id="step1">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span12">
						<#include "createImportPlanInfo.ftl"/>
					</div>
				</div>
			</div>
		</div>
		<div class="step-pane" id="step2">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span12">
						<#include "createImportPlanProduct.ftl"/>
					</div>
				</div>
			</div>
		</div>
		<div class="step-pane" id="step3">
			<#include "createImportPlanConfirm.ftl"/>
		</div>
	</div><!--.step-content-->
	
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.LogPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizard" data-last="${uiLabelMap.LogFinish}">
			${uiLabelMap.LogNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.LogLoading}...</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/imexresources/js/util/DateUtil.js?v=1.0.0"></script>
<script type="text/javascript" src="/imexresources/js/import/plan/createImportPlanTotal.js?v=1.0.0"></script>