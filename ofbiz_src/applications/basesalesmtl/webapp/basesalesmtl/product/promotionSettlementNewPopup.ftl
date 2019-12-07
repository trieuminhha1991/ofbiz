<#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator)/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/>
<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)/>
<#assign listProductPromoExtType = delegator.findByAnd("ProductPromoExtType", null, null, true)!/>
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
	var promoExtTypeData = [
		{typeId: 'ORDER_PROMO', description: '${StringUtil.wrapString(uiLabelMap.BSOrderPromotion)}'}, 
	<#if listProductPromoExtType?exists>
		<#list listProductPromoExtType as typeItem>
		{	typeId: '${typeItem.productPromoTypeId}',
			description: '${StringUtil.wrapString(typeItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
</script>
<div id="alterpopupWindowSalesStatementNew" style="display:none">
	<div>${uiLabelMap.BSCreateNewProductPromoSettlement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_promoSettlementId" class="required">${uiLabelMap.BSPromoSettlementId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_promoSettlementId" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_promoSettlementName">${uiLabelMap.BSPromoSettlementName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_promoSettlementName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_productPromoId" class="required">${uiLabelMap.BSProductPromoId}</label>
						</div>
						<div class='span7'>
							<div id="wn_productPromoId">
								<div id="wn_productPromoGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_productPromoExtId" class="required">${uiLabelMap.BSProductPromoExtId}</label>
						</div>
						<div class='span7'>
							<div id="wn_productPromoExtId">
								<div id="wn_productPromoExtGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_fromDate" class="required">${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_thruDate" class="required">${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_thruDate"></div>
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

<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	var defaultDataMap = {};
	defaultDataMap.currentOrganizationPartyId = <#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'<#else>null</#if>;
	defaultDataMap.currentCurrencyUomId = <#if currentCurrencyUomId?exists>'${currentCurrencyUomId}'<#else>null</#if>;
	defaultDataMap.salesStatementTypeId = <#if parameters.tid?exists>'${parameters.tid}'<#else>null</#if>;
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.BSPromoSettlementId = "${StringUtil.wrapString(uiLabelMap.BSPromoSettlementId)}";
	uiLabelMap.BSPromoSettlementName = "${StringUtil.wrapString(uiLabelMap.BSPromoSettlementName)}";
	uiLabelMap.BSPromoSettlementTypeId = "${StringUtil.wrapString(uiLabelMap.BSPromoSettlementTypeId)}";
	uiLabelMap.BSFromDate = "${StringUtil.wrapString(uiLabelMap.BSFromDate)}";
	uiLabelMap.BSThruDate = "${StringUtil.wrapString(uiLabelMap.BSThruDate)}";
	uiLabelMap.BSProductPromoId = "${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}";
	uiLabelMap.BSPromoName = "${StringUtil.wrapString(uiLabelMap.BSPromoName)}";
</script>
<script type="text/javascript" src="/salesmtlresources/js/product/promotionSettlementNewPopup.js"></script>
