<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<script type="text/javascript">
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [<#list uoms as item>{
		uomId : '${item.uomId}',
		description : '${StringUtil.wrapString(item.description)}',
	},</#list>];
    <#assign listSupplierParty = delegator.findList("ListPartySupplierByRole", null, null, null, null, false) !>
	var supplierData = [<#list listSupplierParty as sup>{
		partyId: "${sup.partyId?if_exists}",
		description: "${sup.groupName?if_exists}",
	},</#list>];
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${uiLabelMap.BSClickToChoose}";
	uiLabelMap.BSValueIsNotEmptyK = "${uiLabelMap.BSValueIsNotEmptyK}";
	uiLabelMap.BPOAreYouSureYouWantCreate = "${uiLabelMap.BPOAreYouSureYouWantCreate}";
	uiLabelMap.BPOPleaseSelectSupplier = "${uiLabelMap.BPOPleaseSelectSupplier}";
	uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}";
	uiLabelMap.BPOSequenceId = "${uiLabelMap.BPOSequenceId}";
	uiLabelMap.wgok = "${uiLabelMap.wgok}";
	uiLabelMap.wgcancel = "${uiLabelMap.wgcancel}";
	uiLabelMap.wgupdatesuccess = "${uiLabelMap.wgupdatesuccess}";
	uiLabelMap.BSProductId = "${uiLabelMap.BSProductId}";
	uiLabelMap.BSProductName = "${uiLabelMap.BSProductName}";
	uiLabelMap.DAQuantityMustBeGreaterThanZero = "${uiLabelMap.DAQuantityMustBeGreaterThanZero}";
    uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";

</script>