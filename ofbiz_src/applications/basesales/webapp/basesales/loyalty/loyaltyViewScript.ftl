<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasValidator=true/>
<script type="text/javascript">
	var validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	<#if loyalty.thruDate?exists>
	var loyaltyThruDate = "${loyalty.thruDate}";
	</#if>
	
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSThisFieldIsRequired = "${uiLabelMap.BSThisFieldIsRequired}";
	uiLabelMap.BSThruDateMustNotBeEmpty = "${uiLabelMap.BSThruDateMustNotBeEmpty}!";
	uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate = "${uiLabelMap.BSAreYouSureYouWantToCreateUpdateThruDate}?";
	uiLabelMap.BSLoyaltyReasonCancelLoyalty = "${uiLabelMap.BSLoyaltyReasonCancelLoyalty}:";
	uiLabelMap.BSAreYouSureYouWantToAccept = "${uiLabelMap.BSAreYouSureYouWantToAccept}";
	uiLabelMap.BSAreYouSureYouWantToCancelNotAccept = "${uiLabelMap.BSAreYouSureYouWantToCancelNotAccept}";
</script>
<script type="text/javascript" src="/salesresources/js/loyalty/loyaltyView.js"></script>