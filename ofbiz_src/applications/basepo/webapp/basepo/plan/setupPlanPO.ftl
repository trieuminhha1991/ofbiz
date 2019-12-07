a<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/poresources/js/plan/setupPlanPO.js"></script>
<style>
	.expired {
		background-color: #eeeeee !important;
		cursor: not-allowed;
	}
	.expired.jqx-widget-olbius .expired.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .expired.jqx-grid-cell-hover-olbius {
		background-color: #eeeeee !important;
		cursor: not-allowed;
	}
	.editable {
		cursor: text;
	}
	.editable.jqx-widget-olbius .editable.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .editable.jqx-grid-cell-hover-olbius {
		cursor: text;
	}
	.jqx-scrollbar-olbius {
		top: 205px !important;
	}
</style>

<#include "setupPlanPO_rowDetail.ftl"/>

<#assign dataField = "[{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'description', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', dataField: 'productCode', width: 300 },
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductName)}', dataField: 'productName' }"/>

<@jqGrid id="jqxgridSetupPlanPO" addrow="false" clearfilteringbutton="true" editable="false"
	customcontrol1="icon-filter open-sans@${uiLabelMap.PODisplayOptions}@javascript: void(0);@PlanFilter.open()"
	columnlist=columnlist dataField=dataField pagesizeoptions="['2', '4', '6']" viewSize="2"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="240"
	url="jqxGeneralServicer?sname=JQGetListProductNonVirtual"/>

<#assign showViewOn="Y" />
<#include "popup/setupPlanPO_Filter.ftl"/>
<script>
	var mapTimePeriod = ${StringUtil.wrapString(mapTimePeriod)};
	const productPlanId = "${(parameters.productPlanId)?if_exists}";
</script>