<#include 'script/transferDeliveryExportTotalScript.ftl'/>
<h4 class="smaller lighter blue" style="margin: 5px 0px 10px 0px !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.BLExportBySalesDelivery} - ${uiLabelMap.DeliveryTransferId}: ${deliveryId?if_exists}
</h4>
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#step1" class="active">
				<span class="step">1. ${uiLabelMap.UpdateActualExportQuantity}</span>
			</li>
			<li data-target="#step2">
				<span class="step">2. ${uiLabelMap.Confirm}</span>
			</li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div id="containerNotify"></div>
		<div class="step-pane active" id="step1">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span12">
						<#include "transferDeliveryExportProduct.ftl"/>
					</div> 
				</div>
			</div>
		</div>

		<div class="step-pane" id="step2">
			<#include "transferDeliveryExportConfirm.ftl"/>
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