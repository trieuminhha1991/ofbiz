<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<@jqOlbCoreLib hasGrid=true hasValidator=true/>
<script type="text/javascript">
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.BSYouNotYetChooseRecord = "${uiLabelMap.BSYouNotYetChooseRecord}?";
	uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
	uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}!";
	uiLabelMap.BSExistProductHaveNotPriceIs = "${uiLabelMap.BSExistProductHaveNotPriceIs}";

	var urlCreateUpdateQuotation = <#if requirement?exists>"updateReqDeliveryOrderAjax"<#else>"createReqDeliveryOrderAjax"</#if>;

	var dataFieldProductItems = ${StringUtil.wrapString(dataFieldProductItems?default("[]"))};
	var columnlistProductItems = [${StringUtil.wrapString(columnlistProductItems?default("[]"))}];
	var columnlistProductItemsConfirm = [${StringUtil.wrapString(columnlistProductItemsConfirm?default("[]"))}];
	var columngrouplistProductItems = ${StringUtil.wrapString(columngrouplistProductItems?default("[]"))};
</script>
<script type="text/javascript" src="/salesmtlresources/js/requirement/reqDeliveryOrderNewTotal.js"></script>
