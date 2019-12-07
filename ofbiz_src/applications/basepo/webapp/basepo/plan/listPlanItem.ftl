<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/poresources/js/plan/listPlanItem.js"></script>

<div id="jqxgridProductPlan"></div>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="mnuCreateOrder"><i class="fa fa-hand-o-right"></i>${uiLabelMap.BPOGoToCreateOrder}</li>
	</ul>
</div>

<#assign showViewOn="N" />
<#include "popup/setupPlanPO_Filter.ftl"/>

<script>
	const productPlanId = "${(parameters.productPlanId)?if_exists}";
	multiLang = _.extend(multiLang, {
		BEDisplayOptions: "${StringUtil.wrapString(uiLabelMap.PODisplayOptions)}",
		DmsCancelFilter: "${StringUtil.wrapString(uiLabelMap.DmsCancelFilter)}",
	});
</script>