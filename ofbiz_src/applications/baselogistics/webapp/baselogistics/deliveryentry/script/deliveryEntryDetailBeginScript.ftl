<#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
<script type="text/javascript">
var fromDate = null;
var thruDate = null;
var shipCost = null;
var deliveryEntryId = null;
<#if deliveryentry?has_content>
	fromDate = new Date('${deliveryentry.fromDate?if_exists}');
	thruDate = new Date('${deliveryentry.thruDate?if_exists}');
	shipCost = '${deliveryentry.shipCost?if_exists}';
	deliveryEntryId = '${deliveryentry.deliveryEntryId?if_exists}';
 	<#if locale == 'vi'>
		if (typeof shipCost == 'string') {
			shipCost = shipCost.replace(',', '.');
		}
	</#if>
	shipCost = parseFloat(shipCost);
</#if>
	
if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";

</script>
<script type="text/javascript" src="/logresources/js/deliveryentry/deliveryEntryDetailBegin.js"></script>