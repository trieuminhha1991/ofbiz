<#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
<script type="text/javascript">
<#assign physicalInventoryId = parameters.physicalInventoryId?if_exists/>
<#assign physicalInventory = delegator.findOne("PhysicalInventory", {"physicalInventoryId" : physicalInventoryId?if_exists}, false)!>

if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
</script>
<script type="text/javascript" src="/logresources/js/inventory/physicalInvDetailBegin.js"></script>