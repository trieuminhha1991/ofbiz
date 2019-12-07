<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}!";
	uiLabelMap.BSYouNotYetChooseRow = "${uiLabelMap.BSYouNotYetChooseRow}!";
	uiLabelMap.BSExistProductHaveNotPriceIs = "${uiLabelMap.BSExistProductHaveNotPriceIs}";
	uiLabelMap.wgcreatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
	uiLabelMap.BSSelectAll = "${StringUtil.wrapString(uiLabelMap.BSSelectAll)}";
	
	var updateMode = <#if updateMode>true<#else>false</#if>;
	<#if updateMode && !copyMode>
		uiLabelMap.BSAreYouSureYouWantToCreate = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToUpdate)}?";
	<#else>
		uiLabelMap.BSAreYouSureYouWantToCreate = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreate)}?";
	</#if>

	var columnlistProductItems = [${StringUtil.wrapString(columnlist?default(""))}];
	var columngrouplistProductItems = ${StringUtil.wrapString(columngrouplist?default("[]"))};
	var datafieldCategoryItemConfirm = ${StringUtil.wrapString(dataFieldCategoryItem?default("[]"))};
	var columnlistCategoryItemConfirm = [${StringUtil.wrapString(columnlistCategoryItem?default(""))}];
</script>
<script type="text/javascript" src="/salesresources/js/quotation/quotationNewTotal.js"></script>
