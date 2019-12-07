<script type="text/javascript" src="/poresources/js/plan/setupPlanPO_Filter.js"></script>

<div id="jqxwindowPlanFilter" class="hide">
<div>${uiLabelMap.optionFilter}</div>
<div style="overflow-x: hidden;">
	
	<div class="row-fluid form-window-content-custom">
		<div class="span6">
			<#if showViewOn?if_exists == "Y">
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right">${uiLabelMap.POViewAs}</label></div>
					<div class="span7"><div id="txtViewOn"></div></div>
				</div>
				<#else>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right">${uiLabelMap.CommonPeriod}</label></div>
					<div class="span7"><div id="txtPeriod"></div></div>
				</div>
			</#if>
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right">${uiLabelMap.PrevWM}</label></div>
				<div class="span7"><div id="txtPrevious"></div></div>
			</div>
		</div>
		
		<div class="span6">
			<#if showViewOn?if_exists == "Y">
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="text-right">${uiLabelMap.CommonPeriod}</label></div>
					<div class="span7"><div id="txtPeriod"></div></div>
				</div>
			<#else>
				<div class="row-fluid margin-top10">
					<div class="span5"></div>
					<div class="span7"><div id="txtViewOn" class="hide"></div></div>
				</div>
			</#if>
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right">${uiLabelMap.NextWM}</label></div>
				<div class="span7"><div id="txtNext"></div></div>
			</div>
		</div>
	</div>
	
	<div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelPlanFilter" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
				<button id="savePlanFilter" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSubmit}</button>
			</div>
		</div>
	</div>
</div>
</div>

<script>
	var displayOption = [
		{ text: "${StringUtil.wrapString(uiLabelMap.OnlyProductHasSalesForecast)}", value: "HasSalesForecast" },
		{ text: "${StringUtil.wrapString(uiLabelMap.BSAllObject)}", value: "All" }
	];
	var period = [
		{ text: "${StringUtil.wrapString(uiLabelMap.Week)}", value: "COMMERCIAL_WEEK" },
		{ text: "${StringUtil.wrapString(uiLabelMap.Month)}", value: "COMMERCIAL_MONTH" }
	];
</script>