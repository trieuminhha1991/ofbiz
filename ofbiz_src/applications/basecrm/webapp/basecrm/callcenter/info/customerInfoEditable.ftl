<#include "OwnerContact.ftl">

<div class="row-fluid">
	<div class="span6">
		<#include "groupInfo.ftl" />
		<#include "personalInfo.ftl"/>
		<#include "liabilityInfo.ftl">
	</div>
	<div class="span6">
		<#include "generalInfo.ftl">
	</div>
</div>

<script>
	var listGender = [{
		value : "M",
		label : "${StringUtil.wrapString(uiLabelMap.DmsMale)}"
	}, {
		value : "F",
		label : "${StringUtil.wrapString(uiLabelMap.DmsFemale)}"
	}];

	var listPhone = [{
		value : "PHONE_HOME",
		label : "${StringUtil.wrapString(uiLabelMap.Phone1)}"
	}, {
		value : "PHONE_MOBILE",
		label : "${StringUtil.wrapString(uiLabelMap.Phone2)}"
	}, {
		value : "PHONE_WORK",
		label : "${StringUtil.wrapString(uiLabelMap.Phone3)}"
	}];
</script>