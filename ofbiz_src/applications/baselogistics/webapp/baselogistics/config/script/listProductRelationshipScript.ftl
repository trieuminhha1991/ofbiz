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
	<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" /> 
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	
	<#assign inventoryItemLabelTypes = delegator.findList("InventoryItemLabelType", null, null, null, null, false) />
	
	<#if inventoryItemLabelTypes?has_content>
		var invLabelTypeData = [
			<#list inventoryItemLabelTypes as item>
			{	inventoryItemLabelTypeId: '${item.inventoryItemLabelTypeId}',
				description: '${StringUtil.wrapString(item.description?default(""))}',
			},
			</#list>
		];
	<#else>
		var invLabelTypeData = [];
	</#if>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.AreYouSureDetele = "${StringUtil.wrapString(uiLabelMap.AreYouSureDetele)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
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
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
</script>
<script type="text/javascript" src="/logresources/js/config/listProductRelationship.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<@jqOlbCoreLib hasComboBox=true hasValidator=true/>
