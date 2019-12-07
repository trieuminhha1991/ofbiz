<#if !orderNewSearchProductSplit?exists><#assign orderNewSearchProductSplit = false/></#if>
<style type="text/css">
	.container-add-plus, .wizard-actions {
		display:none;
	}
</style>
<#if !emptyCartUrl?exists>
	<#assign emptyCartUrl = "emptyCart"/>
</#if>
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<div class="wizard-steps-steps">
			<ul class="wizard-steps wizard-steps-square">
				<li data-target="#step1" class="active">
					<span class="step">1. ${uiLabelMap.BSEnterTheCommonInformation}</span>
				</li>
				<li data-target="#step2">
					<span class="step">2. ${uiLabelMap.BSConfirmOrder}</span>
				</li>
			</ul>
		</div>
		<div class="wizard-steps-actions">
			<div>
				<span><a href="<@ofbizUrl>${emptyCartUrl}</@ofbizUrl>" class="btn margin-left-2 btn-danger"><i class="icon-trash icon-only"></i></a></span>
			</div>
		</div>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<div id="containerMsgTotal"></div>
		<@loading id="process-loading-css" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/><#--rgba(245, 245, 245, 0)-->
		<div class="step-pane active" id="step1">
			<div style="position:relative">
				<div class="row-fluid">
					<div class="span6">
						<#--${screens.render("component://basesales/widget/OrderScreens.xml#NewSalesOrderInfo")}-->
						<#include "orderNewInfo.ftl"/>
					</div>
					<div class="span6">
						${screens.render("component://basesales/widget/OrderScreens.xml#NewSalesOrderCheckoutOption")}
					</div>
				</div>
				<a href="#ap" class="btn btn-primary btn-rolldown"><i class="fa fa-arrow-circle-down"></i></a>
			</div>
			<a name="ap"></a>
			<div style="position:relative" class="form-window-content-custom">
				${setContextField("gridProductItemsId", "jqxgridSO")}
				${setContextField("displayQuantityReturnPromo", "true")}
				<#if orderNewSearchProductSplit>${setContextField("orderNewSearchProductSplit", orderNewSearchProductSplit)}</#if>
				${screens.render("component://basesales/widget/ProductScreens.xml#ProductItemsPopup")}
			</div>
		</div>

		<div class="step-pane" id="step2">
			<div class="row-fluid">
				<div class="alert alert-info">
					${uiLabelMap.BSLoading}
				</div>
			</div>
		</div>
	</div><!--.step-content-->
	
	<div class="row-fluid wizard-actions">
		<#if enableCheckInventory?exists && enableCheckInventory>
		<button class="btn btn-small btn-primary" id="btnCheckProduct">
			<i class="icon-ok"></i>
			${uiLabelMap.BSCheckInventoryItem}
		</button>
		</#if>
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
<#include "script/orderNewTotalScript.ftl"/>
