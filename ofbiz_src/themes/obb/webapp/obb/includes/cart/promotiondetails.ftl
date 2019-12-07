<div class="block">
    <h2>${uiLabelMap.OrderPromotionDetails}:</h2>
    <div class="screenlet-body">
        <div>${StringUtil.wrapString(productPromo.promoText?if_exists)}</div>
        <div>${uiLabelMap.ObbGeneratedDescription}  ${promoAutoDescription?if_exists}</div>
    </div>
</div>
