<style>
.editable {
	cursor: text;
}
.editable.jqx-widget-olbius .editable.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .editable.jqx-grid-cell-hover-olbius {
	cursor: text;
}
.jqx-grid-pager.jqx-grid-pager-olbius {
	z-index: 0 !important;
}
</style>

<#include "script/createPurchasePlanScript.ftl"/>
<div class="row-fluid">
	<div class="span12">
		<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
			<ul class="wizard-steps wizard-steps-square">
				<li data-target="#step1" class="active">
					<span class="step">1. ${uiLabelMap.EnterCommonInformation}</span>
				</li>
				<li data-target="#step2">
					<span class="step">2. ${uiLabelMap.Confirm}</span>
				</li>
			</ul>
		</div>
		<div class="step-content row-fluid position-relative margin-top10" id="step-container">
			<div class="step-pane active" id="step1">
				<#include "showCalculatePurchaseOrder.ftl"/>
			</div>
			<div class="step-pane" id="step2">
				<#include "calculatePurchaseOrderConfirm.ftl"/>
			</div>
		</div>
		<div class="row-fluid wizard-actions" id="wizard-actions">
			<button class="btn btn-prev btn-small" id="btnPrev">
				<i class="icon-arrow-left"></i>${uiLabelMap.LogPrev}
			</button>
			<button class="btn btn-success btn-next btn-small" data-last="${uiLabelMap.LogFinish}">
				${uiLabelMap.LogNext}
				<i class="icon-arrow-right icon-on-right"></i>
			</button>
		</div>
	</div>
</div>

<div id="jqxNotification">
	<div id="notificationContent"></div>
</div>