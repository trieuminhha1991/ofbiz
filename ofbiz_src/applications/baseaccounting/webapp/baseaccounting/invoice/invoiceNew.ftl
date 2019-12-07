<#include "script/invoiceNewScript.ftl"/>

<style>
	input[type=checkbox].ace-switch-6:checked+.lbl::before {
		content: "\f00c" !important;
	    text-indent: 6px !important;
	    color: #FFF !important;
	    border-color: #b7d3e5 !important;
	    background-color: #08c !important;
	}
</style>
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#step1" class="active">
		        <span class="step">1. ${uiLabelMap.BACCEnterTheCommon}</span>
		    </li>
			<li data-target="#step2">
		        <span class="step">2. ${uiLabelMap.BACCInvoiceItem}</span>
		    </li>
		    <li data-target="#step3">
		        <span class="step">3. ${uiLabelMap.BACCConfirmInvoice}</span>
		    </li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active margin-top15" id="step1">
			<#include "invoiceNewStep1.ftl"/>
		</div>
		<div class="step-pane" id="step2">
			<#include "invoiceNewStep2.ftl"/>
		</div>
		<div class="step-pane" id="step3">
			<#include "invoiceNewConfirm.ftl" />
		</div>
	</div><!--.step-content-->
    <div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.BSPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizard" data-last="${uiLabelMap.BSFinish}">
			${uiLabelMap.BSNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/invoice/invoiceNew.js?v=0.0.2"></script>