<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasCore=true hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		uomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	function getUomDesc(uomId) {
		for (var i = 0; i < weightUomData.length; i ++) {
			if (weightUomData[i].uomId == uomId) {
				return weightUomData[i].description;
			}
		}
		for (var i = 0; i < uomData.length; i ++) {
			if (uomData[i].uomId == uomId) {
				return uomData[i].description;
			}
		}
		return uomId;
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonCode = "${StringUtil.wrapString(uiLabelMap.CommonId)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.Name = "${StringUtil.wrapString(uiLabelMap.CommonName)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.BIETestEventType = "${StringUtil.wrapString(uiLabelMap.BIETestEventType)}";
	uiLabelMap.BIEAgreementId = "${StringUtil.wrapString(uiLabelMap.BIEAgreementId)}";
	uiLabelMap.BIEPackingListId = "${StringUtil.wrapString(uiLabelMap.BIEPackingListId)}";
	uiLabelMap.CreatedDate = "${StringUtil.wrapString(uiLabelMap.CreatedDate)}";
	uiLabelMap.BIEExecutedDate = "${StringUtil.wrapString(uiLabelMap.BIEExecutedDate)}";
	uiLabelMap.BIECompletedDate = "${StringUtil.wrapString(uiLabelMap.BIECompletedDate)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.CreatedBy = "${StringUtil.wrapString(uiLabelMap.CreatedBy)}";
	uiLabelMap.BIEListTestAndQuarantine = "${StringUtil.wrapString(uiLabelMap.BIEListTestAndQuarantine)}";
	uiLabelMap.BIECommonCannotEdit = "${StringUtil.wrapString(uiLabelMap.BIECommonCannotEdit)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.BLQuantityUse = "${StringUtil.wrapString(uiLabelMap.BLQuantityUse)}";
	uiLabelMap.BLQuantityRegistered = "${StringUtil.wrapString(uiLabelMap.BLQuantityRegistered)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}";
	uiLabelMap.CommonAdd = "${StringUtil.wrapString(uiLabelMap.CommonAdd)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.BLAddProducts = "${StringUtil.wrapString(uiLabelMap.BLAddProducts)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.FromDate = "${StringUtil.wrapString(uiLabelMap.FromDate)}";
	uiLabelMap.ThruDate = "${StringUtil.wrapString(uiLabelMap.ThruDate)}";
	uiLabelMap.FromDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeAfterNow)}";
	uiLabelMap.FromDateMustBeBeforeThruDate = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeBeforeThruDate)}";
	uiLabelMap.ThruDateMustBeAfterNow = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterNow)}";
	uiLabelMap.MissingFromDate = "${StringUtil.wrapString(uiLabelMap.MissingFromDate)}";
	uiLabelMap.MissingThruDate = "${StringUtil.wrapString(uiLabelMap.MissingThruDate)}";
	uiLabelMap.UpdateSuccess = "${StringUtil.wrapString(uiLabelMap.UpdateSuccess)}";
	uiLabelMap.WrongFormat = "${StringUtil.wrapString(uiLabelMap.WrongFormat)}";
</script>

<div id="popupEdit" class="hide popup-bound">
	<div>${uiLabelMap.Edit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">

				<div class="span6">
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<span class="">${uiLabelMap.BIEDeclarationEventCode}</span>
						</div>
						<div class="span7">
							<input id="eventCode"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class="">${uiLabelMap.BIEDeclarationEventName}</span>
						</div>
						<div class="span7">	
							<input id="eventName"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class=" asterisk">${uiLabelMap.BIEExecutedDate}</span>
						</div>
						<div class="span7">
							<div id="executedDate"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5 align-right'>
							<span class="">${uiLabelMap.BIECompletedDate}</span>
						</div>
						<div class="span7">
							<div id="completedDate"></div>
				   		</div>
					</div>
				</div>
				
				<div class="span6">
					<div class='row-fluid'>
						<div class="span3 align-right"><span class="">${uiLabelMap.Description}</span></div>
						<div class="span7"><textarea id="description" name="description" data-maxlength="250" rows="8" style="resize: vertical; margin-top:0px" class="span12"></textarea></div>
					</div>
				</div>
				
			</div>
			<div class="row-fluid">
				<div id="jqxGridProducts"></div>
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
		        <button id="addProductCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="addProductSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/imexresources/js/declaration/editDeclarationEvent.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>