<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script>
	var quotaTypeId = null;
	<#if parameters.quotaTypeId?has_content>
		quotaTypeId = '${parameters.quotaTypeId?if_exists}';
	</#if>
	var quotaTypeData = [];
	
	var listProductSelected = [];
	
	<#assign quotaTypes = delegator.findList("QuotaType", null, null, null, null, true) />
	<#if quotaTypes?has_content>
		<#list quotaTypes as item>
			var item = {
				quotaTypeId: '${item.quotaTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get('description', locale)?if_exists)}'
			}
			quotaTypeData.push(item);
		</#list>
	</#if>
	
	var getQuotaTypeDesc = function (quotaTypeId) {
		for (var i in quotaTypeData) {
			var x = quotaTypeData[i];
			if (x.quotaTypeId == quotaTypeId) {
				return x.description;
			}
		}
		return quotaTypeId;
	}
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "QUOTA_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	var getStatusDesc = function (statusId) {
		for (var i in statusData) {
			var x = statusData[i];
			if (x.statusId == statusId) {
				return x.description;
			}
		}
		return statusId;
	}
	
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
	
	var locale = '${locale}';

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";

	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.BIEListQuotaHeaders = "${StringUtil.wrapString(uiLabelMap.BIEListQuotaHeaders)}";
	uiLabelMap.BIEQuotaId = "${StringUtil.wrapString(uiLabelMap.BIEQuotaId)}";
	uiLabelMap.BIEQuotaName = "${StringUtil.wrapString(uiLabelMap.BIEQuotaName)}";
	uiLabelMap.BPSupplier = "${StringUtil.wrapString(uiLabelMap.Supplier)}";
	uiLabelMap.CreatedDate = "${StringUtil.wrapString(uiLabelMap.CreatedDate)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}";
	uiLabelMap.BIECurrentQuota = "${StringUtil.wrapString(uiLabelMap.BIECurrentQuota)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.BIECurrentQuota = "${StringUtil.wrapString(uiLabelMap.BIECurrentQuota)}";
	uiLabelMap.BIENewQuota = "${StringUtil.wrapString(uiLabelMap.BIENewQuota)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.CommonPartyId = "${StringUtil.wrapString(uiLabelMap.CommonPartyId)}";
	uiLabelMap.CommonPartyName = "${StringUtil.wrapString(uiLabelMap.CommonPartyName)}";
	uiLabelMap.FromDate = "${StringUtil.wrapString(uiLabelMap.FromDate)}";
	uiLabelMap.ThruDate = "${StringUtil.wrapString(uiLabelMap.ThruDate)}";
	uiLabelMap.BIETimeRangeNotTrue = "${StringUtil.wrapString(uiLabelMap.BIETimeRangeNotTrue)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.CreatedBy = "${StringUtil.wrapString(uiLabelMap.CreatedBy)}";
	uiLabelMap.BIENeedEnterFromThruDate = "${StringUtil.wrapString(uiLabelMap.BIENeedEnterFromThruDate)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.CommonAdd = "${StringUtil.wrapString(uiLabelMap.CommonAdd)}";
	uiLabelMap.BIENeedEnterFromThruDate = "${StringUtil.wrapString(uiLabelMap.BIENeedEnterFromThruDate)}";
</script>

<div id="popupWindowEdit" class="hide popup-bound">
	<div>${uiLabelMap.Edit} ${uiLabelMap.BIEQuota?lower_case}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10 hide">
						<div class="span3 align-right asterisk">${uiLabelMap.Supplier}</div>
						<div class="span7">	
							<div id="supplierDT">
							</div>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span3 align-right">${uiLabelMap.BIEQuotaId}</div>
						<div class="span7">	
							<input id="quotaCodeEdit"></input>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span3 align-right">${uiLabelMap.BIEQuotaName}</div>
						<div class="span7">	
							<input id="quotaNameEdit"></input>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-top10 hide">
						<div class="span3 align-right">${uiLabelMap.CurrencyUom}</div>
						<div class="span7">	
							<div id="currencyUomDT"></div>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span3 align-right">${uiLabelMap.Description}</div>
						<div class="span7"><textarea id="descriptionEdit" name="descriptionEdit" data-maxlength="250" rows="4" style="resize: vertical; margin-top:0px" class="span12"></textarea></div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="jqxGridEditProducts"></div>
			</div>
	        <div class="form-action popup-footer">
		        <button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="editSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
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

<script type="text/javascript" src="/imexresources/js/quota/editImexQuota.js"></script>