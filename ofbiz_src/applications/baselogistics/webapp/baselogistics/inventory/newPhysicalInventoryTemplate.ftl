<#include 'script/newPhysicalInvTemplateScript.ftl'/>
<h4 class="smaller lighter blue" style="margin: 5px 0px 10px 0px !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.PhysicalInventory}
</h4>
<div class="row-fluid winrar-background-color">
	<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
		<div class="wizard-steps-steps">
			<ul class="wizard-steps wizard-steps-square">
				<li data-target="#step1" class="active">
					<span class="step">1. ${uiLabelMap.CountAndVarianceRecord}</span>
				</li>
				<li data-target="#step2">
					<span class="step">2. ${uiLabelMap.UpdateInventoryItemInformation}</span>
				</li>
				<li data-target="#step3">
					<span class="step">3. ${uiLabelMap.Confirm}</span>
				</li>
			</ul>
		</div>
		<div class="wizard-steps-actions">
			<div>
				<span><a href="javascript:PhysicalInvTemplateObj.reloadPages();" class="btn margin-left-2 btn-success"><i class="icon-refresh icon-only"></i></a></span>
			</div>
		</div>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div id="containerNotify"></div>
		<div class="step-pane active" id="step1">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span12">
						${screens.render("component://baselogistics/widget/InventoryScreens.xml#NewPhysicalInventoryInfo")}
					</div>
				</div>
			</div>
			<div style="position:relative" class="">
				<div id="jqxgridItemPhysical"></div>
				<#include "listInventoryPhysical.ftl"/>
			</div>
		</div>
		<div class="step-pane" id="step2">
			<div id="jqxgridInventoryItemUpdate"></div>
			<#include "listInventoryItemUpdate.ftl"/>
		</div>
		<div class="step-pane" id="step3">
			<#include "newPhysicalInventoryConfirm.ftl"/>
		</div>
	</div><!--.step-content-->
	
	<div class="row-fluid wizard-actions bottom-action">
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