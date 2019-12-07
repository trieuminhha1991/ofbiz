<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	var billId = null;
	<#if parameters.billId?has_content>
		billId = '${parameters.billId?if_exists}';
		<#assign bill = delegator.findOne("BillOfLading", Static["org.ofbiz.base.util.UtilMisc"].toMap("billId", parameters.billId?if_exists), false)!/>
		<#if bill?has_content>
			if (billSelected == undefined) { 
				var billSelected = null;
			}
			if (billId) {
				billSelected = {"billId": billId, "billNumber": "${bill.billNumber?if_exists}"};
			}
		</#if>
	</#if>
	<#assign orgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
	<#assign partyAcctgPreference = delegator.findOne("PartyAcctgPreference", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orgId), false)!/>
	<#assign baseCurrencyUomId = partyAcctgPreference.baseCurrencyUomId?if_exists>
    var baseCurrencyUomId = "${baseCurrencyUomId?if_exists}";
    var currencyUomId = "${currencyUomId?if_exists}";
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.packingListNumber = "${StringUtil.wrapString(uiLabelMap.packingListNumber)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.NotChosenDateManu = "${StringUtil.wrapString(uiLabelMap.NotChosenDateManu)}";
	uiLabelMap.ExistsProductNotDate = "${StringUtil.wrapString(uiLabelMap.ExistsProductNotDate)}";
	uiLabelMap.SaveAndConPL = "${StringUtil.wrapString(uiLabelMap.SaveAndConPL)}";
	uiLabelMap.LoadFailPO = "${StringUtil.wrapString(uiLabelMap.LoadFailPO)}";
    uiLabelMap.ClearPL = "${StringUtil.wrapString(uiLabelMap.ClearPL)}";
    uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";

	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	
	
	uiLabelMap.BIEPackingListId = "${StringUtil.wrapString(uiLabelMap.BIEPackingListId)}";
	uiLabelMap.BIEAgreementId = "${StringUtil.wrapString(uiLabelMap.BIEAgreementId)}";
	uiLabelMap.OrderPO = "${StringUtil.wrapString(uiLabelMap.OrderPO)}";
	uiLabelMap.BIEVendorInvoiceNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorInvoiceNum)}";
	uiLabelMap.BIEVendorOrderNum = "${StringUtil.wrapString(uiLabelMap.BIEVendorOrderNum)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.BIENetWeight = "${StringUtil.wrapString(uiLabelMap.BIENetWeight)}";
	uiLabelMap.BIEGrossWeight = "${StringUtil.wrapString(uiLabelMap.BIEGrossWeight)}";
	uiLabelMap.BIEPackingListDate = "${StringUtil.wrapString(uiLabelMap.BIEPackingListDate)}";
	uiLabelMap.BIEInvoiceDate = "${StringUtil.wrapString(uiLabelMap.BIEInvoiceDate)}";
	uiLabelMap.BIEPackingList = "${StringUtil.wrapString(uiLabelMap.BIEPackingList)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.globalTradeItemNumber = "${StringUtil.wrapString(uiLabelMap.globalTradeItemNumber)}";
	uiLabelMap.batchNumber = "${StringUtil.wrapString(uiLabelMap.batchNumber)}";
	uiLabelMap.packingUnits = "${StringUtil.wrapString(uiLabelMap.packingUnits)}";
	uiLabelMap.packingUomId = "${StringUtil.wrapString(uiLabelMap.packingUomId)}";
	uiLabelMap.orderUnits = "${StringUtil.wrapString(uiLabelMap.orderUnits)}";
	uiLabelMap.orderUomId = "${StringUtil.wrapString(uiLabelMap.orderUomId)}";
	uiLabelMap.originOrderUnit = "${StringUtil.wrapString(uiLabelMap.originOrderUnit)}";
	uiLabelMap.dateOfManufacture = "${StringUtil.wrapString(uiLabelMap.dateOfManufacture)}";
	uiLabelMap.ProductExpireDate = "${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}";
	uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.AddDetailPL = "${StringUtil.wrapString(uiLabelMap.AddDetailPL)}";
	uiLabelMap.AddPL = "${StringUtil.wrapString(uiLabelMap.AddPL)}";
	uiLabelMap.AddProduct = "${StringUtil.wrapString(uiLabelMap.BLAddProducts)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.BIEContainerId = "${StringUtil.wrapString(uiLabelMap.BIEContainerId)}";
	uiLabelMap.BIEBillId = "${StringUtil.wrapString(uiLabelMap.BIEBillId)}";
	uiLabelMap.BIEBillNumber = "${StringUtil.wrapString(uiLabelMap.BIEBillNumber)}";
	uiLabelMap.BIEContainer = "${StringUtil.wrapString(uiLabelMap.BIEContainer)}";
	uiLabelMap.BIEContainerNumber = "${StringUtil.wrapString(uiLabelMap.BIEContainerNumber)}";
	uiLabelMap.BIESealNumber = "${StringUtil.wrapString(uiLabelMap.BIESealNumber)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.BIEContainerType = "${StringUtil.wrapString(uiLabelMap.BIEContainerType)}";
	uiLabelMap.BIEDepartureDate = "${StringUtil.wrapString(uiLabelMap.BIEDepartureDate)}";
	uiLabelMap.BIEArrivalDate = "${StringUtil.wrapString(uiLabelMap.BIEArrivalDate)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.testedDocument = "${StringUtil.wrapString(uiLabelMap.testedDocument)}";
	uiLabelMap.quarantineDocument = "${StringUtil.wrapString(uiLabelMap.quarantineDocument)}";
	
	uiLabelMap.BillOfLadingBudget = "${StringUtil.wrapString(uiLabelMap.BillOfLadingBudget)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.costPriceActual = "${StringUtil.wrapString(uiLabelMap.costPriceActual)}";
	uiLabelMap.costPriceTemporary = "${StringUtil.wrapString(uiLabelMap.costPriceTemporary)}";
	uiLabelMap.BACCInvoiceItemTypeId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemTypeId)}";
	uiLabelMap.Total = "${StringUtil.wrapString(uiLabelMap.Total)}";
	
</script>

<div id="cost-tab" class="tab-pane<#if activeTab?exists && activeTab == "cost-tab"> active</#if>">
<div id="jqxGridBillCosts"></div>
<div id="popupWindowAddNewCost" class="hide popup-bound">
	<div>${uiLabelMap.AddNew} ${uiLabelMap.BIECost}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="row-fluid margin-top10">
					<div class="span4 align-right asterisk">${uiLabelMap.BIEBillOfLading}</div>
					<div class="span8">	
						<div id="billOfLading">
							<div id="jqxGridBOLCost"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4 align-right asterisk">${uiLabelMap.BACCInvoiceItemTypeId}</div>
					<div class="span8">	
						<div id="costAccBase">
							<div id="jqxGridCostAccBase"></div>
						</div>
					</div>
				</div>
                <div class="row-fluid margin-top10 hide" id="exchangedRateDiv">
                    <div class="span4 align-right asterisk">${uiLabelMap.BACCExchangedRateForTax}</div>
                    <div class="span8"><div type='text' id="exchangedRate"></div></div>
                </div>
				<div class="row-fluid margin-top10">
					<div class="span4 align-right">${uiLabelMap.costPriceTemporary}</div>
					<div class="span8"><div type='text' id="costPriceTemporary"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4 align-right">${uiLabelMap.costPriceActual}</div>
					<div class="span8"><div type='text' id="costPriceActual"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span4 align-right">${uiLabelMap.Unit}</div>
					<div class="span8"><div id="currencyUomId" class="green-label">${baseCurrencyUomId?if_exists}</div></div>
				</div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="alterCancelCost" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="alterSaveCost" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id='jqxCostMenu' style="display: none;">
	<ul>
		<li id="refreshCost"><i class="icon-refresh"></i><a> ${uiLabelMap.BSRefresh}</a></li>
	</ul>
</div>
</div>
<script type="text/javascript" src="/imexresources/js/import/listBillOfLadingCost.js?v=0.0.1"></script>