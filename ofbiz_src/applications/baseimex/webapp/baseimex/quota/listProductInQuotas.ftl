<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
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
	uiLabelMap.BIEListProductOutOfQuota = "${StringUtil.wrapString(uiLabelMap.BIEListProductOutOfQuota)}";
	uiLabelMap.BIEQuotaRemain = "${StringUtil.wrapString(uiLabelMap.BIEQuotaRemain)}";
	uiLabelMap.BIEQuotaTotal = "${StringUtil.wrapString(uiLabelMap.BIEQuotaTotal)}";
	uiLabelMap.BIEListProductInQuota = "${StringUtil.wrapString(uiLabelMap.BIEListProductInQuota)}";
</script>

<div id="list-tab" class="tab-pane<#if activeTab?exists && activeTab == "list-tab"> active</#if>">
	<div id="jqxGridProducts"></div>
	
	<div id='jqxContextMenu' class="hide">
		<ul>
			<li id='refreshGrid'><i class='icon-refresh'></i><a>${uiLabelMap.BSRefresh}</a></li>
		</ul>
	</div>
</div>
<script type="text/javascript" src="/imexresources/js/quota/listProductInQuota.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>