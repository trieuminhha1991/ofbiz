<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>

	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descPackingUom?if_exists}';
		quantityUomData[${item_index}] = row;
	</#list>
	
	<#assign wuoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list wuoms as item>
		var row = {};
		<#assign descWUom = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${descWUom?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	
	function getUomDesc(uomId) {
	 	for (x in quantityUomData) {
	 		if (quantityUomData[x].uomId == uomId) {
	 			return quantityUomData[x].description;
	 		}
	 	}
	 	for (x in weightUomData) {
	 		if (weightUomData[x].uomId == uomId) {
	 			return weightUomData[x].description;
	 		}
	 	}
 	}
	var customTimePeriodId = '${parameters.customTimePeriodId?if_exists}';
	var productPlanId = '${parameters.productPlanId?if_exists}';
	<#if parameters.agreementId?has_content>
		<#assign listProducts = delegator.findList("AgreementProductAppl", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("agreementId", parameters.agreementId), null, null, null, false)>
		<#assign agreementAtt = delegator.findOne("AgreementAttribute", {"agreementId" : agreementId, "attrName": "AGREEMENT_NAME"}, false)!>
	<#else>
		<#assign listProducts = delegator.findList("AgreementProductAppl", null, null, null, null, true) />
	</#if>
	var listProducts = "${listProducts?if_exists}";
	var agreement = {};
	agreement.fromDate = "${agreement.fromDate?if_exists}";
	agreement.thruDate = "${agreement.thruDate?if_exists}";
	agreement.agreementDate = "${agreement.agreementDate?if_exists}";
	agreement.agreementId = "${StringUtil.wrapString(agreement.agreementId?if_exists)}";
	agreement.agreementCode = "${StringUtil.wrapString(agreement.agreementCode?if_exists)}";
	agreement.partyIdTo = "${StringUtil.wrapString(agreement.partyIdTo?if_exists)}";
	var agreementAtt = {};
	agreementAtt.attrValue = "${StringUtil.wrapString(agreementAtt.attrValue)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.Product= "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.PlanQuantity = "${StringUtil.wrapString(uiLabelMap.PlanQuantity)}";
	uiLabelMap.orderedQuantity = "${StringUtil.wrapString(uiLabelMap.orderedQuantity)}";
	uiLabelMap.OrderQuantityEdit = "${StringUtil.wrapString(uiLabelMap.OrderQuantityEdit)}";
	uiLabelMap.unitPrice = "${StringUtil.wrapString(uiLabelMap.unitPrice)}";
	uiLabelMap.DAItemTotal = "${StringUtil.wrapString(uiLabelMap.DAItemTotal)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.UpdateError = "${StringUtil.wrapString(uiLabelMap.UpdateError)}";
	uiLabelMap.MOQ = "${StringUtil.wrapString(uiLabelMap.MOQ)}";
	uiLabelMap.DAQuantityMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.DAQuantityMustBeGreaterThanZero)}";
	uiLabelMap.DmsRestrictQuantityPO = "${StringUtil.wrapString(uiLabelMap.DmsRestrictQuantityPO)}";
	uiLabelMap.PortOfDischarge = "${StringUtil.wrapString(uiLabelMap.PortOfDischarge)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.BIEPortCode = "${StringUtil.wrapString(uiLabelMap.BIEPortCode)}";
	uiLabelMap.BIEPortName = "${StringUtil.wrapString(uiLabelMap.BIEPortName)}";
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.BIEQuota = "${StringUtil.wrapString(uiLabelMap.BIEQuota)}";
	uiLabelMap.DAOrderId = "${StringUtil.wrapString(uiLabelMap.DAOrderId)}";
	uiLabelMap.DACreateDate = "${StringUtil.wrapString(uiLabelMap.DACreateDate)}";
	uiLabelMap.DAShipAfterDate = "${StringUtil.wrapString(uiLabelMap.DAShipAfterDate)}";
	uiLabelMap.DAShipBeforeDate = "${StringUtil.wrapString(uiLabelMap.DAShipBeforeDate)}";
	uiLabelMap.BIEOrderCreateAgreementFull = "${StringUtil.wrapString(uiLabelMap.BIEOrderCreateAgreementFull)}";
	uiLabelMap.CommonAdd = "${StringUtil.wrapString(uiLabelMap.CommonAdd)}";
	uiLabelMap.AreYouSureUpdate= "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.UpdateSuccess= "${StringUtil.wrapString(uiLabelMap.UpdateSuccess)}";
	uiLabelMap.ThruDateMustGreaterThanFromDate= "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate)}";
	uiLabelMap.AgreementDateMustBeforeFromDate= "${StringUtil.wrapString(uiLabelMap.AgreementDateMustBeforeFromDate)}";
	uiLabelMap.WrongFormat= "${StringUtil.wrapString(uiLabelMap.WrongFormat)}";
	
	
</script>

<div id="popupEdit" class="hide popup-bound">
	<div>${uiLabelMap.Edit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<span class=" asterisk">${uiLabelMap.BIEAgreementNumber}</span>
						</div>
						<div class="span7">
							<input id="agreementCode"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class=" asterisk">${uiLabelMap.AgreementName}</span>
						</div>
						<div class="span7">	
							<input id="agreementName"></input>
				   		</div>
					</div>
				</div>
				
				<div class="span6">
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class=" asterisk">${uiLabelMap.AgreementDate}</span>
						</div>
						<div class="span7">
							<div id="agreementDate"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class="asterisk">${uiLabelMap.AvailableFromDate}</span>
						</div>
						<div class="span7">
							<div id="fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class="asterisk">${uiLabelMap.AvailableThruDate}</span>
						</div>
						<div class="span7">
							<div id="thruDate"></div>
				   		</div>
					</div>
				</div>	
			</div>
			<div class="row-fluid">
				<div id="listProduct" class="margin-top15"></div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="popupAddProduct" class="hide popup-bound">
	<div>${uiLabelMap.BLAddProducts}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="jqxGridProductAdds"></div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="alterCancelAdd" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="addProductSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/imexresources/js/import/agreement/editAgreement.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>