<#-- TODO deleted -->
<script src="/salesmtlresources/js/common/additionalContact.js"></script>

<div id="jqxgridContact"></div>

<script>
	multiLang = _.extend(multiLang, {
		FullName: "${StringUtil.wrapString(uiLabelMap.FullName)}",
		PhoneNumber: "${StringUtil.wrapString(uiLabelMap.PhoneNumber)}",
		CommonAddNew: "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}",
		AdditionalContact: "${StringUtil.wrapString(uiLabelMap.AdditionalContact)}",
		});
	<#if updatableAdditionalContact?if_exists == "Y">
	var updatable = true;
	<#else>
	var updatable = false;
	</#if>
</script>