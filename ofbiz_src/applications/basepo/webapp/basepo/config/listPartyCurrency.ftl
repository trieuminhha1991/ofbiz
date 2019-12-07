<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script>
	
	var listPartySelected = {};
	var listCurrencySelected = [];
	
	var currencyUomData = [];
	<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, true) />
	
	<#if listCurrencyUom?has_content>
		<#list listCurrencyUom as item>
			var item = {
				currencyUomId: '${item.uomId?if_exists}',
				description: '${item.uomId?if_exists}' + ' - ' + '${item.description?if_exists}'
			}
			currencyUomData.push(item);
		</#list>
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CommonPartyId = "${StringUtil.wrapString(uiLabelMap.BPCommonPartyId)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.CommonPartyName = "${StringUtil.wrapString(uiLabelMap.BPCommonPartyName)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BPThruDateNow = "${StringUtil.wrapString(uiLabelMap.BPThruDateNow)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.MustAfterEffectiveDate = "${StringUtil.wrapString(uiLabelMap.MustAfterEffectiveDate)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.PartyCurrency = "${StringUtil.wrapString(uiLabelMap.BPPartyCurrency)}";
	uiLabelMap.BPFromDate = "${StringUtil.wrapString(uiLabelMap.BPFromDate)}";
	uiLabelMap.BPThruDate = "${StringUtil.wrapString(uiLabelMap.BPThruDate)}";
	uiLabelMap.CurrencyUomId = "${StringUtil.wrapString(uiLabelMap.CurrencyUomId)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";

	
</script>
<div id="jqxGridPartyCurrency"></div>
<div id="AddPartyCurrency" class="hide popup-bound">
	<div>${uiLabelMap.AddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top20">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.CommonParty}</div>
					</div>
					<div class="span7">	
						<div id="party" class="green-label">
							<div id="jqxGridListParty">
				            </div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.CurrencyUomId}</div>
					</div>
					<div class="span7">	
						<div id="currencyUomId" class="green-label">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk">${uiLabelMap.BPFromDate}</div>
					</div>
					<div class="span7">	
						<div id="fromDate">
			            </div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.BPThruDate}</div>
					</div>
					<div class="span7">	
						<div id="thruDate">
			            </div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="addCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="addSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id='contextMenu' class="hide">
	<ul>
    	<li><i class="fa fa-plus"></i>${uiLabelMap.AddNew}</li>
    	<li><i class="fa red fa-times"></i>${StringUtil.wrapString(uiLabelMap.BPThruDateNow)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script src="/poresources/js/config/listPartyCurrency.js"></script>