<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<script type="text/javascript">
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	<#if promotionPO?exists>
		var urlCreateUpdatePromotion = "updatePromotionPOAdvance";
		uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToUpdate}";
	<#else>
		var urlCreateUpdatePromotion = "createPromotionPOAdvance";
		uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	</#if>
	uiLabelMap.BSThisIsSpecialPromotionSetLimitEqualOne = "${StringUtil.wrapString(uiLabelMap.BSThisIsSpecialPromotionSetLimitEqualOne)}";
</script>
<script type="text/javascript" src="/poresources/js/promotion/promotionNewTotal.js?v=0.0.1"></script>
