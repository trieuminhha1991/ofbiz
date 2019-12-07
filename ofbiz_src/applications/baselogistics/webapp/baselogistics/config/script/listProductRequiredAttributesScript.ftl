<@jqGridMinimumLib />
<style>
	.bootbox{
		  z-index: 20001 !important;
		 }
	 .modal-backdrop{
	  z-index: 20000 !important;
	 }
</style> 
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	var yesNoData = [];
	var yes = {
		typeId: "Y",
		description: "${StringUtil.wrapString(uiLabelMap.Yes)}",
	}
	var no = {
		typeId: "N",
		description: "${StringUtil.wrapString(uiLabelMap.No)}",
	}
	yesNoData.push(yes);
	yesNoData.push(no);
	
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
	<#assign cond1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVariant", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "N")>
	<#assign cond2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVirtual", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "N")>
	<#assign andCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(cond1, cond2), Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	
	<#assign cond3 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isVariant", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "Y")>
	
	<#assign orCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(cond3, andCond), Static["org.ofbiz.entity.condition.EntityOperator"].OR)>
	<#assign cond4 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, "CATALOG_CATEGORY")>
	
	<#assign allCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(cond4, orCond), Static["org.ofbiz.entity.condition.EntityOperator"].AND)>
	
	<#assign products = delegator.findList("ProductCategoryAndProduct", allCond, null, null, null, false) />
	
	var productData = [
  		<#if products?exists>
  			<#list products as item>
  				{
  					productId: "${item.productId?if_exists}",
  					quantityUomId: "${item.quantityUomId?if_exists}",
  					productCategoryId: "${item.productCategoryId?if_exists}",
  					categoryName: "${item.categoryName?if_exists}",
  					productCode: "${item.productCode?if_exists}",
  					checkDate: "${item.checkDate?if_exists}",
  					productName: "${StringUtil.wrapString(item.get('productName', locale)?if_exists)}"
  				},
  			</#list>
  		</#if>
  	];
	
	
	<#assign facilities = Static["com.olbius.baselogistics.util.LogisticsFacilityUtil"].getFacilityWithRole(delegator, userLogin.partyId, "MANAGER") />
	var facilityData = [
  		<#if facilities?exists>
  			<#list facilities as item>
  				{
  					facilityCode: "${item.facilityCode?if_exists}",
  					facilityId: "${item.facilityId?if_exists}",
  					facilityName: "${StringUtil.wrapString(item.get('facilityName', locale)?if_exists)}"
  				},
  			</#list>
  		</#if>
  	];
  	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list quantityUoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		quantityUomData.push(row);
	</#list>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.YouNotYetChooseLabel = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseLabel)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Category = "${StringUtil.wrapString(uiLabelMap.Category)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.FacilityId = "${StringUtil.wrapString(uiLabelMap.FacilityId)}";
	uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
	uiLabelMap.Facility = "${StringUtil.wrapString(uiLabelMap.Facility)}";
	
</script>
<script type="text/javascript" src="/logresources/js/config/listProductRequiredAttributes.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasComboBox=true hasValidator=true/>
