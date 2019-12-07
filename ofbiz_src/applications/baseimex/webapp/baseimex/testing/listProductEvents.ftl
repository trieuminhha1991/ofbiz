<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script>
	var productEventTypeData = [];
	var parentEventTypeId = '${parameters.parentEventTypeId?if_exists}';
	<#if parameters.parentEventTypeId?has_content>
		<#assign qualityTestTypes = delegator.findList("ProductEventType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", parameters.parentEventTypeId?if_exists), null, null, null, true) />
	<#else>
		<#assign qualityTestTypes = delegator.findList("ProductEventType", null, null, null, null, true) />
	</#if>
	
	<#if qualityTestTypes?has_content>
		<#list qualityTestTypes as item>
			var item = {
				eventTypeId: '${item.eventTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get('description', locale)?if_exists)}'
			}
			productEventTypeData.push(item);
		</#list>
	</#if>
	
	var getTypeDesc = function (eventTypeId) {
		for (var i in productEventTypeData) {
			var x = productEventTypeData[i];
			if (x.eventTypeId == eventTypeId) {
				return x.description;
			}
		}
		return eventTypeId;
	}
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PRODUCT_EVENT_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${descStatus?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	var getStatusDesc = function (statusId) {
		for (var i in statusData) {
			var x = statusData[i];
			if (x.statusId == statusId) {
				return x.description;
			}
		}
		return statusId;
	}
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.AddNew = "${StringUtil.wrapString(uiLabelMap.AddNew)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonCode = "${StringUtil.wrapString(uiLabelMap.CommonId)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.Name = "${StringUtil.wrapString(uiLabelMap.CommonName)}";
	uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
	uiLabelMap.BIETestEventType = "${StringUtil.wrapString(uiLabelMap.BIETestEventType)}";
	uiLabelMap.BIEAgreementId = "${StringUtil.wrapString(uiLabelMap.BIEAgreementId)}";
	uiLabelMap.BIEPackingListId = "${StringUtil.wrapString(uiLabelMap.BIEPackingListId)}";
	uiLabelMap.CreatedDate = "${StringUtil.wrapString(uiLabelMap.CreatedDate)}";
	uiLabelMap.BIEExecutedDate = "${StringUtil.wrapString(uiLabelMap.BIEExecutedDate)}";
	uiLabelMap.BIECompletedDate = "${StringUtil.wrapString(uiLabelMap.BIECompletedDate)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.CreatedBy = "${StringUtil.wrapString(uiLabelMap.CreatedBy)}";
	uiLabelMap.BIEListTestAndQuarantine = "${StringUtil.wrapString(uiLabelMap.BIEListTestAndQuarantine)}";
	uiLabelMap.BIECommonCannotEdit = "${StringUtil.wrapString(uiLabelMap.BIECommonCannotEdit)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Unit = "${StringUtil.wrapString(uiLabelMap.Unit)}";
	uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
	uiLabelMap.BLQuantityUse = "${StringUtil.wrapString(uiLabelMap.BLQuantityUse)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.ValueMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreaterThanZero)}";
	uiLabelMap.CommonAdd = "${StringUtil.wrapString(uiLabelMap.CommonAdd)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.BLAddProducts = "${StringUtil.wrapString(uiLabelMap.BLAddProducts)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.UpdateSuccess = "${StringUtil.wrapString(uiLabelMap.UpdateSuccess)}";
	uiLabelMap.WrongFormat = "${StringUtil.wrapString(uiLabelMap.WrongFormat)}";
</script>
<div id="jqxGridProductEvent"></div>

<#include "popupEditEvent.ftl">	
<div id='contextMenu' class="hide">
	<ul>
    	<li><i class="fa fa-plus"></i>${uiLabelMap.AddNew}</li>
    	<li><i class="fa fa-edit"></i>${uiLabelMap.Edit}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="jqxNotification">
    <div id="notificationContent"></div>
</div>
<script type="text/javascript" src="/imexresources/js/product/listProductEvents.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>