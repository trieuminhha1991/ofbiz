<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">

	var supplierData = [<#list listSupplierParty as sup>{
		partyId: "${sup.partyId?if_exists}",
		description: "${sup.groupName?if_exists}",
	},</#list>];

	var listReturnReasons = [<#list listReturnReasons as returnReason>{
		returnReasonId: "${returnReason.returnReasonId?if_exists}",
		description: "${StringUtil.wrapString(returnReason.get("description",locale)?if_exists)}"
	},</#list>];

if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.BSClickToChoose = "${uiLabelMap.BSClickToChoose}";
uiLabelMap.BSValueIsNotEmptyK = "${uiLabelMap.BSValueIsNotEmptyK}";
uiLabelMap.BPOAreYouSureYouWantCreate = "${uiLabelMap.BPOAreYouSureYouWantCreate}";
uiLabelMap.BPOPleaseSelectSupplier = "${uiLabelMap.BPOPleaseSelectSupplier}";
uiLabelMap.BPOPleaseSelectProduct = "${uiLabelMap.BPOPleaseSelectProduct}";
uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}";
uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
uiLabelMap.BPOPleaseSelectProductQuantity = "${uiLabelMap.BPOPleaseSelectProductQuantity}";
uiLabelMap.BPORestrictMOQ = "${uiLabelMap.BPORestrictMOQ}";
uiLabelMap.BPOSequenceId = "${uiLabelMap.BPOSequenceId}";
uiLabelMap.BPOContactMechId = "${uiLabelMap.BPOContactMechId}";
uiLabelMap.BPOReceiveName = "${uiLabelMap.BPOReceiveName}";
uiLabelMap.BPOOtherInfo = "${uiLabelMap.BPOOtherInfo}";
uiLabelMap.BPOAddress1 = "${uiLabelMap.BPOAddress1}";
uiLabelMap.BPOCity = "${uiLabelMap.BPOCity}";
uiLabelMap.wgok = "${uiLabelMap.wgok}";
uiLabelMap.wgcancel = "${uiLabelMap.wgcancel}";
uiLabelMap.wgupdatesuccess = "${uiLabelMap.wgupdatesuccess}";
uiLabelMap.validRequiredValueGreatherOrEqualToDay = "${uiLabelMap.validRequiredValueGreatherOrEqualToDay}";
uiLabelMap.POOrderId = "${uiLabelMap.POOrderId}";
uiLabelMap.POOrderDate = "${uiLabelMap.POOrderDate}";
uiLabelMap.BPOProductCanNotReturn = "${uiLabelMap.BPOProductCanNotReturn}";

</script>