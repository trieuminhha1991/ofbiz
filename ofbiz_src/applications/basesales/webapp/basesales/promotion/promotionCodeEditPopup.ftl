<#if productPromo?exists>
<div id="alterpopupWindowEditPromoCode" style="display:none">
	<div>${uiLabelMap.ProductPromotionAddSetOfPromotionCodes}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="windowEditPromoCodeContainer"></div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="we_alterSavePromoCode" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSave}</button>
				<button id="we_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
	uiLabelMap.BSVoucherCodeIsEmpty = "${StringUtil.wrapString(uiLabelMap.BSVoucherCodeIsEmpty)}";
</script>
<script type="text/javascript" src="/salesresources/js/promotion/promotionCodeEditPopup.js"></script>
</#if>