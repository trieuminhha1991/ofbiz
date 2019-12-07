<#include 'script/reqEditRequirementTotalScript.ftl'/>

<h4 class="smaller lighter blue" style="margin: 5px 0px 10px 0px !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.BLEditInfo}
</h4>
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#step1" class="active">
					<span class="step">1. ${uiLabelMap.BLEditInfo}</span>
			</li>
			<li data-target="#step2">
				<span class="step">2. ${uiLabelMap.ConfirmRequirement}</span>
			</li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div id="containerNotify"></div>
		<div class="step-pane active" id="step1">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span12">
						<#include 'reqEditRequirementInfo.ftl'/>
					</div> 
				</div>
			</div>
			<div style="position:relative" class="form-window-content-custom">
				<div id="jqxGridProduct"></div>
			</div>
		</div>
		

<div id="addProductPopup" style="z-index: 100000000 !important" class="hide popup-bound">
 	<div>${uiLabelMap.BLAddProducts}</div>
 	<div class='form-window-container'>
 		<div class='form-window-content'>
 	        <div class="row-fluid">
 	    		<div class="span12">
 	    			<div id="jqxgridProductAdd"></div>
				</div>
 			</div>
 		</div>
 		<div class="form-action popup-footer">
 	        <button id="addProductCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
 	        <button id="addProductSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonAdd}</button>
 		</div>
 	</div>
 </div>
		
		<div class="step-pane" id="step2">
			<#include "reqEditRequirementConfirm.ftl"/>
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
<script type="text/javascript" src="/logresources/js/requirement/reqEditRequirementProduct.js?v=1.1.1"></script>