<script src="/crmresources/js/crmsetting/addProductOfRival.js"></script>

<div id="addProductOfRival" style="display:none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSCompetitor}</label></div>
			<div class="span8"><div id="PartyRivals" tabindex="5"></div></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span4"><label class="text-right asterisk">${uiLabelMap.BSProductName}</label></div>
			<div class="span8"><input type="text" id="ProductName" tabindex="6" /></div>
		</div>
		<div class="row-fluid margin-top10">
			<div class="span12">
				<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<#assign partyRivals = delegator.findByAnd("PartyRivals", null, null, false)>
<script type="text/javascript">
var partyRivals = [<#list partyRivals as partyRival>{
	partyId : "${partyRival.partyId}",
	groupName: "${StringUtil.wrapString(partyRival.groupName)}",
},</#list>];
</script>