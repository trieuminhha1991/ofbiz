<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasValidator=true/>
<script type="text/javascript">
	var validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	<#if productPromo.thruDate?exists>
	var promotionThruDate = "${productPromo.thruDate}";
	</#if>
	
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSThisFieldIsRequired = "${uiLabelMap.BSThisFieldIsRequired}";
	uiLabelMap.BSThruDateMustNotBeEmpty = "${uiLabelMap.BSThruDateMustNotBeEmpty}!";
	uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate = "${uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate}?";
	uiLabelMap.BSPromoReasonCancelPromo = "${uiLabelMap.BSPromoReasonCancelPromo}:";
	uiLabelMap.BSAreYouSureYouWantToAccept = "${uiLabelMap.BSAreYouSureYouWantToAccept}";
	uiLabelMap.BSAreYouSureYouWantToCancelNotAccept = "${uiLabelMap.BSAreYouSureYouWantToCancelNotAccept}";
	uiLabelMap.validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	uiLabelMap.BSRequiredValueGreatherOrEqualToDay = "${StringUtil.wrapString(uiLabelMap.BSRequiredValueGreatherOrEqualToDay)}";
	
	var labelwgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
</script>
<script type="text/javascript" src="/salesresources/js/promotion/promotionView.js"></script>