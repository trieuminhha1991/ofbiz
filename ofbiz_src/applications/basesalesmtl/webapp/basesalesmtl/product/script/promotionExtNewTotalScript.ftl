<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<script type="text/javascript">
	<#if productPromo?exists>
		var urlCreateUpdatePromotion = "updatePromotionExtAdvance";
	<#else>
		var urlCreateUpdatePromotion = "createPromotionExtAdvance";
	</#if>
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
</script>
<script type="text/javascript" src="/salesmtlresources/js/product/promotionExtNewTotal.js"></script>