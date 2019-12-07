<script>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BACCTarget = "${uiLabelMap.BACCTarget}";
	uiLabelMap.BACCCode = "${uiLabelMap.BACCCode}";
	uiLabelMap.BACCDemonstration = "${uiLabelMap.BACCDemonstration}";
	uiLabelMap.BACCFSBalanceSheet = "${uiLabelMap.BACCFSBalanceSheet}";
	uiLabelMap.AggregateLabel = "${uiLabelMap.AggregateLabel}";
	uiLabelMap.olap_ok_grid = "${uiLabelMap.olap_ok_grid}";
	uiLabelMap.olap_cancel_grid = "${uiLabelMap.olap_cancel_grid}";
	uiLabelMap.olap_warn_grid = "${uiLabelMap.olap_warn_grid}";
	uiLabelMap.olap_lastupdated_grid = "${uiLabelMap.olap_lastupdated_grid}";
	uiLabelMap.ReportCheckNotData = "${uiLabelMap.ReportCheckNotData}";
	
	var customTimePeriodDefault; 
	<#if customTimePeriodDefault?has_content>
		customTimePeriodDefault = '${customTimePeriodDefault.customTimePeriodId?if_exists}';
	</#if>
	var organizationPartyId = '${parameters.organizationPartyId?if_exists}';
	var reportTypeId = '${parameters.reportTypeId?if_exists}';
	var flag = '${parameters.flag?if_exists}';
	var titleProperty = "${StringUtil.wrapString(uiLabelMap[titleProperty?if_exists])}&nbsp;";
	var isClosed = "${isClosed?if_exists}";
</script>

<style>
	.brand {
		font-weight: 600;
		font-size: 100%;
	}
	#statusbartreeGrid{
		border: none !important;
	}
</style>
<div class="alert alert-error" id="errorNotify" style="display: none;">
      <span>${uiLabelMap.BACCCustomPeriodHasClosedYet}</span>
</div>
<div id="treeGrid" style="border-bottom: 1px solid #CCC !important;"></div>
<script type="text/javascript" src="/accresources/js/report/financialStatementGrid.js?v=0.0.5"></script>