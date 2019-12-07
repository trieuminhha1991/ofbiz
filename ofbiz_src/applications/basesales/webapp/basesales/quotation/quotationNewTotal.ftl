<script type="text/javascript">
	var productPricesMap = {};
	var dataSelected = [];
	var productPricesMapCate = {};
	var dataSelectedCate = [];
</script>
<#assign updateMode = false/>
<#if productQuotation?exists>
	<#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>

<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>

<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#step1" class="active">
				<span class="step">1. ${uiLabelMap.BSEnterTheCommonInformation}</span>
			</li>
			<li data-target="#step2">
				<span class="step">2. ${uiLabelMap.BSAddItems}</span>
			</li>
			<li data-target="#step3">
				<span class="step">3. ${uiLabelMap.BSConfirmation}</span>
			</li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<#include "quotationNewInfo.ftl"/>
		</div>
		<div class="step-pane" id="step2">
			<#include "quotationNewItems.ftl"/>
		</div>
		<div class="step-pane" id="step3">
			<#include "quotationNewConfirm.ftl"/>
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
<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<#include "script/quotationNewTotalScript.ftl"/>
