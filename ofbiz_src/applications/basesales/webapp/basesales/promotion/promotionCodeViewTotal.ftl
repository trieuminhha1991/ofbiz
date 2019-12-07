<#if productPromoCode?exists>
	<div class="tabbable">
		<ul class="nav nav-tabs" id="recent-tab">
			<li class="active"><a data-toggle="tab" href="#promoCodeInfo-tab">${uiLabelMap.BSInformation}</a></li>
			<#if productPromoCode.requireEmailOrParty?exists && "Y" == productPromoCode.requireEmailOrParty>
			<li><a data-toggle="tab" href="#promoCodeListEmail-tab">${uiLabelMap.BSListEmailAddress}</a></li>
			<li><a data-toggle="tab" href="#promoCodeListParty-tab">${uiLabelMap.BSListCustomerId}</a></li>
			</#if>
		</ul>
	</div>
	<div class="tab-content overflow-visible" style="padding:8px 0">
		<div id="promoCodeInfo-tab" class="tab-pane active">
			<div class="row-fluid">
				<div class="span12">
					<#include "promotionCodeViewEdit.ftl">
				</div>
			</div>
		</div>
		<#if productPromoCode.requireEmailOrParty?exists && "Y" == productPromoCode.requireEmailOrParty>
		<div id="promoCodeListEmail-tab" class="tab-pane">
			<div class="row-fluid">
				<div class="span12">
					<#include "promotionCodeViewEmail.ftl">
				</div>
			</div>
		</div>
		<div id="promoCodeListParty-tab" class="tab-pane">
			<div class="row-fluid">
				<div class="span12">
					<#include "promotionCodeViewParty.ftl">
				</div>
			</div>
		</div>
		</#if>
	</div>
<#else>
	${uiLabelMap.BSNotFoundVoucherIsValid}
</#if>