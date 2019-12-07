<#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
<script type="text/javascript">
<#assign transferId = parameters.transferId?if_exists/>
<#assign transfer = delegator.findOne("TransferHeader", {"transferId" : transferId}, false)!>
<#assign checkFromStk = Static["com.olbius.baselogistics.util.LogisticsPartyUtil"].checkStorekeeperOfFacility(delegator, userLogin.partyId?if_exists, transfer.originFacilityId?if_exists)/>
<#assign checkToStk = Static["com.olbius.baselogistics.util.LogisticsPartyUtil"].checkStorekeeperOfFacility(delegator, userLogin.partyId?if_exists, transfer.destFacilityId?if_exists)/>

<#assign delivered = Static["com.olbius.baselogistics.transfer.TransferReadHepler"].checkTransferDeliveredAPart(delegator, transfer.transferId?if_exists)/>

if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
uiLabelMap.AreYouSureReject = "${StringUtil.wrapString(uiLabelMap.AreYouSureReject)}";

</script>
<script type="text/javascript" src="/logresources/js/transfer/transferDetailBegin.js"></script>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
