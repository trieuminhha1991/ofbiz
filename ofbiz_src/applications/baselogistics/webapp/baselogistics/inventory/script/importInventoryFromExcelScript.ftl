<style type="text/css">
	.bootbox{
	    z-index: 990009 !important;
	}
	.modal-backdrop{
	    z-index: 890009 !important;
	}
	.loading-container{
		z-index: 999999 !important;
	}
</style>
<script>
$.jqx.theme = 'olbius';
theme = $.jqx.theme;

<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>

<#assign acctgs = delegator.findList("PartyAcctgPreference", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", company)), null, null, null, false)>

var currencyUomId = 'VND';
<#if acctgs?has_content>
	currencyUomId = "${acctgs.get(0).baseCurrencyUomId?if_exists}";
</#if>

<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", company)), null, null, null, false)>
var facilityData = [];
<#list facilities as item>
	var row = {};
	<#assign descFac = StringUtil.wrapString(item.facilityName?if_exists)/>
	row['facilityId'] = "${item.facilityId?if_exists}";
	row['description'] = "${descFac?if_exists}";
	facilityData.push(row);
</#list>

<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
var quantityUomData = [];
<#list uoms as item>
	var row = {};
	<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
	row['quantityUomId'] = "${item.uomId?if_exists}";
	row['description'] = "${descPackingUom?if_exists}";
	quantityUomData.push(row);
</#list>

if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.NoFile = "${StringUtil.wrapString(uiLabelMap.NoFile)}";
uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";
uiLabelMap.CommonChange = "${StringUtil.wrapString(uiLabelMap.CommonChange)}";
uiLabelMap.YouNotYetChooseFile = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseFile)}";
uiLabelMap.YouNeedUploadFileWithType = "${StringUtil.wrapString(uiLabelMap.YouNeedUploadFileWithType)}";
uiLabelMap.or = "${StringUtil.wrapString(uiLabelMap.or)}";
uiLabelMap.uploadSuccessfully = "${StringUtil.wrapString(uiLabelMap.uploadSuccessfully)}";
uiLabelMap.RecordsInsertedSuccessfully = "${StringUtil.wrapString(uiLabelMap.RecordsInsertedSuccessfully)}";
uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
uiLabelMap.Batch = "${StringUtil.wrapString(uiLabelMap.Batch)}";
uiLabelMap.DatetimeManufactured = "${StringUtil.wrapString(uiLabelMap.DatetimeManufactured)}";
uiLabelMap.ExpireDate = "${StringUtil.wrapString(uiLabelMap.ExpireDate)}";
uiLabelMap.Facility = "${StringUtil.wrapString(uiLabelMap.Facility)}";
uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";

uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.FormatWrong = "${StringUtil.wrapString(uiLabelMap.FormatWrong)}";
uiLabelMap.NotExisted = "${StringUtil.wrapString(uiLabelMap.NotExisted)}";
uiLabelMap.DataNotExisted = "${StringUtil.wrapString(uiLabelMap.DataNotExisted)}";
uiLabelMap.LogReceiveProuductSuccess = "${StringUtil.wrapString(uiLabelMap.LogReceiveProuductSuccess)}";

</script>
<script type="text/javascript" src="/logresources/js/inventory/importInventoryFromExcel.js"></script>
