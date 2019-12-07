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

<#assign localeStr = "VI" />
var localeStr = "VI";
<#if locale = "en">
	<#assign localeStr = "EN" />
	localeStr = "EN";
</#if>

<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
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
uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
uiLabelMap.SequenceId = "${uiLabelMap.SequenceId}";
uiLabelMap.ProductId = "${uiLabelMap.ProductId}";
uiLabelMap.ProductName = "${uiLabelMap.ProductName}";
uiLabelMap.Unit = "${uiLabelMap.Unit}";
uiLabelMap.OpeningInventoryQuantity = "${uiLabelMap.OpeningInventoryQuantity}";
uiLabelMap.EndingInventoryQuantity = "${uiLabelMap.EndingInventoryQuantity}";
uiLabelMap.PleaseSelectTitle = "${uiLabelMap.PleaseSelectTitle}";

</script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/logresources/js/inventory/inventoryForecast.js"></script>
