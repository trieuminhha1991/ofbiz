<script src="/crmresources/js/crmsetting/addRival.js"></script>

<div id="addRival" style="display:none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSCompetitorId}</label></div>
			<div class="span8"><input type="text" id="PartyCode" tabindex="5" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSCompetitorName}</label></div>
			<div class="span8"><input type="text" id="GroupName" tabindex="6" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<script>
var reasonType = [<#if reasonEnums?exists><#list reasonEnums as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
</script>