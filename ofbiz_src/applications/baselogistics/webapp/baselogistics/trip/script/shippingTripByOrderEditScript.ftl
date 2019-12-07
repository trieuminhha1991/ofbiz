<@jqGridMinimumLib/>
<@jqOlbCoreLib hasCore=true hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">
    <#assign shippingTripId = parameters.shippingTripId?if_exists/>
    var shippingTripId = "${shippingTripId}";
    <#assign shippingTrip = delegator.findOne("ShippingTrip", {"shippingTripId" : shippingTripId}, false)/>
    <#assign statusPack = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "SHIP_PACK_STATUS"), null, null, null, false)/>
    var statusAllStatusOrder = new Array();
    <#list statusPack as item>
		  var row = {};
      <#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
		  row['statusId'] = '${item.statusId}';
		  row['description'] = '${descStatus?if_exists}';
		  statusAllStatusOrder.push(row);
    </#list>
    <#assign listCluster = delegator.findList("DeliveryCluster", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("executorId", shippingTrip.shipperId), null, null, null, false)/>
    var listClusterByShipper = new Array();
    <#list listCluster as item>
            var row = '${item.deliveryClusterId}';
		    listClusterByShipper.push(row);
    </#list>
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BLDeliveryClusterId="${StringUtil.wrapString(uiLabelMap.BLDeliveryClusterId)}";
    uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
    uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
    uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
    uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";
    uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
    uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
    uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
    uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
    uiLabelMap.AreYouSureReject = "${StringUtil.wrapString(uiLabelMap.AreYouSureReject)}";
    uiLabelMap.EXPRequired = "${StringUtil.wrapString(uiLabelMap.EXPRequired)}";
    uiLabelMap.QuantityDelivered = "${StringUtil.wrapString(uiLabelMap.QuantityDelivered)}";
    uiLabelMap.QuantityShipping = "${StringUtil.wrapString(uiLabelMap.QuantityShipping)}";
    uiLabelMap.QuantityScheduled = "${StringUtil.wrapString(uiLabelMap.QuantityScheduled)}";
    uiLabelMap.QuantityRemain = "${StringUtil.wrapString(uiLabelMap.QuantityRemain)}";
    uiLabelMap.NotRequiredExpiredDate = "${StringUtil.wrapString(uiLabelMap.NotRequiredExpiredDate)}";
    uiLabelMap.BPSearchProductToAdd = "${StringUtil.wrapString(uiLabelMap.BPSearchProductToAdd)}";
    uiLabelMap.BPProductNotFound = "${StringUtil.wrapString(uiLabelMap.BPProductNotFound)}";
    uiLabelMap.DAYouNotYetChooseOrder = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseOrder)}";
    uiLabelMap.QOH = "${StringUtil.wrapString(uiLabelMap.QOH)}";
    uiLabelMap.DeliveryId = '${uiLabelMap.DeliveryId}';
    uiLabelMap.Quantity = "${StringUtil.wrapString(uiLabelMap.Quantity)}";
    uiLabelMap.DeliveryItemSeqId = '${StringUtil.wrapString(uiLabelMap.DeliveryItemSeqId)}';
    uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
    uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';
    uiLabelMap.BSAddress = '${StringUtil.wrapString(uiLabelMap.BSAddress)}';
    uiLabelMap.BSAverageCost = "${StringUtil.wrapString(uiLabelMap.BSAverageCost)}";
    uiLabelMap.BPOTotal = "${StringUtil.wrapString(uiLabelMap.BPOTotal)}";
    uiLabelMap.OrderItemsSubTotal = "${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)}";
    uiLabelMap.BLAddInsideClusterOrder = "${StringUtil.wrapString(uiLabelMap.BLAddInsideClusterOrder)}";
    uiLabelMap.BLAddOutsideClusterOrder = "${StringUtil.wrapString(uiLabelMap.BLAddOutsideClusterOrder)}";
    uiLabelMap.BLDeleteOrder = "${StringUtil.wrapString(uiLabelMap.BLDeleteOrder)}";
</script>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
	</#if>
	var localeStr = '${localeStr}';
	<#assign company = Static['com.olbius.basehr.util.MultiOrganizationUtil'].getCurrentOrganization(delegator, userLogin.get('userLoginId'))! />;

	
	if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.Hasnotbeenapproved = "${StringUtil.wrapString(uiLabelMap.Hasnotbeenapproved)}";
    uiLabelMap.Hasbeenapproved = "${StringUtil.wrapString(uiLabelMap.Hasbeenapproved)}";
    uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
    uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
    uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
    uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
    uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
    uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
    uiLabelMap.OrderId = "${StringUtil.wrapString(uiLabelMap.OrderId)}";
    uiLabelMap.DeliveryId = "${StringUtil.wrapString(uiLabelMap.DeliveryId)}";
    uiLabelMap.PackCode = "${StringUtil.wrapString(uiLabelMap.PackCode)}";
    uiLabelMap.Address = "${StringUtil.wrapString(uiLabelMap.Address)}";
    uiLabelMap.Status = "${StringUtil.wrapString(uiLabelMap.Status)}";
    uiLabelMap.BLProductStoreId = "${StringUtil.wrapString(uiLabelMap.BLProductStoreId)}";
    uiLabelMap.ShipAfterDate = "${StringUtil.wrapString(uiLabelMap.ShipAfterDate)}";
    uiLabelMap.ShipBeforeDate = "${StringUtil.wrapString(uiLabelMap.ShipBeforeDate)}";
    uiLabelMap.CustomerId = "${StringUtil.wrapString(uiLabelMap.CustomerId)}";
    uiLabelMap.CannotBeforeNow = "${uiLabelMap.CannotBeforeNow}";
    uiLabelMap.AreYouSureApproveAndExport  = "${StringUtil.wrapString(uiLabelMap.AreYouSureApproveAndExport)}";
    uiLabelMap.CanNotAfterShipBeforeDate = "${uiLabelMap.CanNotAfterShipBeforeDate}";
    uiLabelMap.CanNotBeforeShipAfterDate = "${uiLabelMap.CanNotBeforeShipAfterDate}";
    uiLabelMap.AreYouSureApprove = "${uiLabelMap.AreYouSureApprove}";
    uiLabelMap.EndDateMustBeAfterStartDate = '${StringUtil.wrapString(uiLabelMap.EndDateMustBeAfterStartDate)}';
	uiLabelMap.StartDateMustBeAfterNow = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterNow)}';
	uiLabelMap.StartDateMustBeAfterEndDate = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterEndDate)}';
    
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js?v=1.0.0"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.validator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/logresources/js/pack/packSearchProduct.js?v=1.1.1"></script>
<script type="text/javascript" src="/logresources/js/trip/shippingTripByOrderEditInfo.js"></script>
<script type="text/javascript" src="/logresources/js/trip/shippingTripByOrderEdit.js"></script>
<script type="text/javascript" src="/logresources/js/trip/shippingTripByOrderEditTotal.js"></script>
