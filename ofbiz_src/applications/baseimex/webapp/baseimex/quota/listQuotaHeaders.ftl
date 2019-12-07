<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
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
	uiLabelMap.AccountingCurrency = "${StringUtil.wrapString(uiLabelMap.AccountingCurrency)}";
</script>

<div id="list-tab" class="tab-pane<#if activeTab?exists && activeTab == "list-tab"> active</#if>">
	<div id="jqxGridQuotaHeaders"></div>
	
	<div id='jqxContextMenu' class="hide">
		<ul>
		  	<li id="addQuotaHeader"><i class="icon-plus"></i><a>${uiLabelMap.AddNew}</a></li>
		  	<li id="editQuotaHeader"><i class="icon-edit"></i><a>${uiLabelMap.Edit}</a></li>
			<li id='refreshGrid'><i class='icon-refresh'></i><a>${uiLabelMap.BSRefresh}</a></li>
		</ul>
	</div>
	
	<#include "popupAddQuota.ftl">
	<#include "popupEditQuota.ftl">
</div>
<script type="text/javascript" src="/imexresources/js/quota/listQuotaHeaders.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>