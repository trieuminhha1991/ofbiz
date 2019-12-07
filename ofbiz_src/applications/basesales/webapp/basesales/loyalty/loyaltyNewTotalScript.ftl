<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<script type="text/javascript">
	<#if loyalty?exists>
		var urlCreateUpdateLoyalty = "updateLoyaltyAdvance";
		var flag = true;
	<#else>
		var urlCreateUpdateLoyalty = "createLoyaltyAdvance";
		var flag = false;
	</#if>
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.BSAreYouSureYouWantToUpdate = "${uiLabelMap.BSAreYouSureYouWantToUpdate}";
</script>
<script type="text/javascript" src="/salesresources/js/loyalty/loyaltyNewTotal.js"></script>