<#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/>
<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)/>
<script type="text/javascript">
	var currencyUomData = [
	<#if currencyUom?exists>
		<#list currencyUom as uomItem>
		{	uomId : "${uomItem.uomId}",
			descriptionSearch : "${StringUtil.wrapString(uomItem.get("description", locale))} [${uomItem.abbreviation}]",
		},
		</#list>
	</#if>
	];
</script>
<div id="alterpopupWindowSalesStatementNew" style="display:none">
	<div>${uiLabelMap.BSCreateNewSalesStatement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_salesStatementTypeId" class="required">${uiLabelMap.BSSalesStatementType}</label>
						</div>
						<div class='span7'>
							<div id="wn_salesStatementTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_salesStatementId">${uiLabelMap.BSSalesStatementId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_salesStatementId" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_salesStatementName">${uiLabelMap.BSSalesStatementName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_salesStatementName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_organizationPartyId" class="required">${uiLabelMap.BSOrganizationId}</label>
						</div>
						<div class='span7'>
							<div id="wn_organizationPartyId">
								<div id="wn_organizationPartyGrid"></div>
							</div>
				   		</div>
					</div>
					<#--
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_internalPartyId" class="required">${uiLabelMap.BSInternalPartyId}</label>
						</div>
						<div class='span7'>
							<div id="wn_internalPartyId">
								<div id="wn_internalPartyGrid"></div>
							</div>
				   		</div>
					</div>
					-->
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_customTimePeriodId" class="required">${uiLabelMap.BSSalesCustomTimePeriodId}</label>
						</div>
						<div class='span7'>
							<div id="wn_customTimePeriodId">
								<div id="wn_customTimePeriodGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_currencyUomId">${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class='span7'>
							<div id="wn_currencyUomId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_salesForecastId">${uiLabelMap.BSSalesForecastId}</label>
						</div>
						<div class='span7'>
							<div id="wn_salesForecastId">
								<div id="wn_salesForecastGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	var defaultDataMap = {};
	defaultDataMap.currentOrganizationPartyId = <#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'<#else>null</#if>;
	defaultDataMap.currentCurrencyUomId = <#if currentCurrencyUomId?exists>'${currentCurrencyUomId}'<#else>null</#if>;
	defaultDataMap.salesStatementTypeId = <#if parameters.tid?exists>'${parameters.tid}'<#else>null</#if>;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.BSSalesStatementId = "${StringUtil.wrapString(uiLabelMap.BSSalesStatementId)}";
	uiLabelMap.BSOrganizationId = "${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}";
	uiLabelMap.BSInternalPartyId = "${StringUtil.wrapString(uiLabelMap.BSInternalPartyId)}";
	uiLabelMap.BSSalesCustomTimePeriodId = "${StringUtil.wrapString(uiLabelMap.BSSalesCustomTimePeriodId)}";
	uiLabelMap.BSSalesPeriodName = "${StringUtil.wrapString(uiLabelMap.BSSalesPeriodName)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	uiLabelMap.BSCurrencyUomId = "${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}";
	uiLabelMap.BSParentPeriodId = "${StringUtil.wrapString(uiLabelMap.BSParentPeriodId)}";
	uiLabelMap.BSPeriodName = "${StringUtil.wrapString(uiLabelMap.BSPeriodName)}";
	uiLabelMap.BSFromDate = "${StringUtil.wrapString(uiLabelMap.BSFromDate)}";
	uiLabelMap.BSThruDate = "${StringUtil.wrapString(uiLabelMap.BSThruDate)}";
	uiLabelMap.BSSalesForecastId = "${StringUtil.wrapString(uiLabelMap.BSSalesForecastId)}";
	uiLabelMap.BSCustomTimePeriodId = "${StringUtil.wrapString(uiLabelMap.BSCustomTimePeriodId)}";
</script>
<script type="text/javascript" src="/salesmtlresources/js/sales_statement/salesStatementNewPopup.js"></script>
