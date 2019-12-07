<#assign isEdit = false/>
<#if productPromo?exists>
	<#assign currentStatusId = productPromo.statusId!/>
	<#if ("PROMO_CANCELLED" != productPromo.statusId) && ((!(productPromo.thruDate?exists) || productPromo.thruDate?exists && productPromo.thruDate &gt; nowTimestamp))>
		<#if currentStatusId?exists && (security.hasEntityPermission("PROMOTION_PO", "_APPROVE", session) && currentStatusId == "PROMO_CREATED")>
			<#assign isEdit = true>
		</#if>
	</#if>
</#if>
<#if !currentStatusId?exists || isEdit >
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#step1" class="active">
				<span class="step">1. ${uiLabelMap.BSEnterTheCommonInformation}</span>
			</li>
			<li data-target="#step2">
				<span class="step">2. ${uiLabelMap.BSListPromotionRules}</span>
			</li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container" style="padding-top:15px">
		<div id="containerMsgTotal">
			<div id="container" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotification" style="margin-bottom:5px">
		        <div id="notificationContent">
		        </div>
		    </div>
		</div>
		
		<div class="step-pane active" id="step1">
			${screens.render("component://basepo/widget/PromotionScreens.xml#NewPromotionInfo")}
		</div>

		<div class="step-pane" id="step2">
			${screens.render("component://basepo/widget/PromotionScreens.xml#NewPromotionRules")}
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
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<#include 'script/promotionNewTotalScript.ftl'>
<#else>
	<div class="alert alert-info">
		${uiLabelMap.BSYouHavenotUpdatePermission}
	</div>
</#if>