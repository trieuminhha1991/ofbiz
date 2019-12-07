<#include "script/orderNewPurchaseTotalScript.ftl"/>
<div class="row-fluid winrar-background-color">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<div class="wizard-steps-steps">
			<ul class="wizard-steps wizard-steps-square">
				<li data-target="#step1" class="active">
					<#if orderId?has_content>
						<span class="step">1. ${uiLabelMap.BLEditInfo}</span>
					<#else>
						<span class="step">1. ${uiLabelMap.EnterCommonInformation}</span>
					</#if>
				</li>
				<li data-target="#step2">
					<span class="step">2. ${uiLabelMap.Confirm}</span>
				</li>
			</ul>
		</div>
		<div class="wizard-steps-actions">
			<div>
				<#if orderId?has_content>
					<span><a href="javascript:OlbTotal.reloadPages();" class="btn margin-left-2 btn-success"><i class="icon-refresh icon-only"></i></a></span>				
				<#else>
					<span><a href="<@ofbizUrl>emptyCartPO</@ofbizUrl>" class="btn margin-left-2 btn-danger"><i class="icon-trash icon-only"></i></a></span>
				</#if>
			</div>
		</div>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div id="containerMsgTotal"></div>
		<@loading id="process-loading-css" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
		<div class="step-pane active" id="step1">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span12"> 
						<#include "orderNewInfoPurchase.ftl"/>
					</div>
				</div>
				<#if orderId?has_content>
					<div class="row-fluid">
 						<div class="span5">
 							<div id="jqxProductSearch" class="span3"></div>
 							<div class="span3 align-left">
 								<button id="btnProductToAdd" class='btn btn-small btn-gray' style="margin-left: -10px;"><i class='fa fa-list'></i></button>
 							</div>
						</div>
 					</div>
				<#else>
					<div class="row-fluid hide">
 						<div class="span5">
 							<div id="jqxProductSearch" class="span3"></div>
 							<div class="span3 align-left">
 								<button id="btnProductToAdd" class='btn btn-small btn-gray margin-right5'><i class='fa fa-list'></i></button>
 							</div>
 						</div>
 					</div>
				</#if>
				<a href="#ap" class="btn btn-primary btn-rolldown"><i class="fa fa-arrow-circle-down"></i></a>
			</div>
			<a name="ap"></a>
			<div style="position:relative;" class="form-window-content-custom">	
				<#include "orderNewPurchaseProduct.ftl"/>
			</div>
			<#if orderId?has_content>
				<div id="received" class="collapsed margin-top5">
					<a href="#" data-toggle="collapse" data-action="collapse" style="font-size: 14px;"><i style="font-family: 'Open Sans';" class="icon-chevron-up" onclick="javascript:changeIconChev($(this));toggleScreenlet(this, 'collapseProduct', 'true', '${uiLabelMap.BPCollapse}', '${uiLabelMap.BPExpand}');" title="${uiLabelMap.BPExpand}">${uiLabelMap.BPListProductReceived}</i></a>
					<div id="collapseProduct" style="display: none;" class="collapseProduct margin-top5">
						<div id="jqxgridOrderItemReceived"></div>
					</div>
				</div>
			<#else>
				<div id="received" class="hide">
					<a href="#" data-toggle="collapse" data-action="collapse" style="font-size: 14px"><i class="icon-chevron-up" onclick="javascript:changeIconChev($(this));toggleScreenlet(this, 'collapseProduct', 'true', '${uiLabelMap.BPCollapse}', '${uiLabelMap.BPExpand}');" title="${uiLabelMap.BPExpand}">${uiLabelMap.BPListProductReceived}</i></a>
					<div id="collapseProduct" style="display: none;" class="collapseProduct">
						<div id="jqxgridOrderItemReceived"></div>
					</div>
				</div>
			</#if>
		</div>

		<div class="step-pane" id="step2">
			<div class="row-fluid">
				<div class="alert alert-info">
					${uiLabelMap.BSLoading}
				</div>
			</div>
		</div>
	</div><!--.step-content-->
	
	<div class="row-fluid wizard-actions bottom-action">
		<button class="btn btn-small btn-prev" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.BSPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizard" data-last="${uiLabelMap.BSFinish}" style="display:none">
			${uiLabelMap.BSNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizardTmp" style="display:inline-block">
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