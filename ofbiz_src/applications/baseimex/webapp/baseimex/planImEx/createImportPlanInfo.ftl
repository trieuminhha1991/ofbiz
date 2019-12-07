<script>
	var listPeriod =
	[
		<#list listPeriod as period>
		{
			customTimePeriodId: "${period.customTimePeriodId?if_exists}",
			periodName: "${period.periodName?if_exists}",
		},
		</#list>
	];
	
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
	uiLabelMap.Detail = "${StringUtil.wrapString(uiLabelMap.Detail)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.SupplierId = "${StringUtil.wrapString(uiLabelMap.SupplierId)}";
	uiLabelMap.SupplierName = "${StringUtil.wrapString(uiLabelMap.SupplierName)}";
	uiLabelMap.Year = "${StringUtil.wrapString(uiLabelMap.Year)}";
	uiLabelMap.FromDate = "${StringUtil.wrapString(uiLabelMap.FromDate)}";
	uiLabelMap.ThruDate = "${StringUtil.wrapString(uiLabelMap.ThruDate)}";
	
</script>
<div id="AddPlan" class="margin-top10 boder-all-profile">
	<div class="row-fluid">
		<div class="span12">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div>${uiLabelMap.POProductPlanID}</div>
						</div>
						<div class="span7">	
							<input id="productPlanCode"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.DmsNamePlan}</div>
						</div>
						<div class="span7">	
							<input id="productPlanName"></input>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div>${uiLabelMap.Description}</div>
						</div>
						<div class="span7">
							<textarea id="description" name="description" data-maxlength="250" rows="3" style="resize: vertical; margin-top:0px" class="span12"></textarea>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.Supplier}</div>
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
							<div class="asterisk">${uiLabelMap.CurrencyUom}</div>
						</div>
						<div class="span7">	
							<div id="currencyUomId" class="green-label">
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.CommercialPeriod}</div>
						</div>
						<div class="span7">	
							<div id="customTimePeriod" class="green-label">
								<div id="jqxGridTimePeriod">
					            </div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="/imexresources/js/import/plan/createProductPlan.js?v=1.0.0"></script>