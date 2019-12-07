<script type="text/javascript">
<#assign returnId = parameters.returnId?if_exists/>
<#assign returnHeader = delegator.findOne("ReturnHeader", {"returnId" : returnId}, false)!>
var returnId = "${parameters.returnId?if_exists}";
var currencyUomId = "VND";
var currencyUomId = "${returnHeader.currencyUomId?if_exists}";
if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";

</script>
<script type="text/javascript" src="/salesresources/js/return/returnOrderViewOpen.js?v=1.0.0"></script>