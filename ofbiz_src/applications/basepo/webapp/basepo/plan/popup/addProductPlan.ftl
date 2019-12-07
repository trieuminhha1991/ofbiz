<script type="text/javascript" src="/poresources/js/plan/addProductPlan.js"></script>

<div id="alterpopupWindow" class="hide">
<div>${uiLabelMap.optionFilter}</div>
<div style="overflow-x: hidden;">
	
	<div class="row-fluid form-window-content-custom">
		<div class="span12">
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsYearPlan}</label></div>
				<div class="span7"><div id="txtYearPlan"></div></div>
			</div>
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsNamePlan}</label></div>
				<div class="span7"><input type="text" id="txtProductPlanName"/></div>
			</div>
		</div>
	</div>
	
	<div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>

<script>
	var listPeriod = [<#list listPeriod as period>{
		customTimePeriodId: "${period.customTimePeriodId?if_exists}",
		periodName: "${period.periodName?if_exists}",
	},</#list>];
</script>