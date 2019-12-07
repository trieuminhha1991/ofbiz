<@jqGridMinimumLib />
<script type="text/javascript">
    $.jqx.theme = 'olbius';
    theme = $.jqx.theme;
    var shippingTripId = "${parameters.shippingTripId?if_exists}";
    <#assign shippingTripId = parameters.shippingTripId?if_exists/>
    <#assign shippingTrip = delegator.findOne("ShippingTrip", {"shippingTripId" : shippingTripId }, false)/>
    var isHasOptimalRoute = "";
    var routeTripId = "";
    <#if shippingTrip.isHasOptimalRoute?has_content>
        isHasOptimalRoute = '${shippingTrip.isHasOptimalRoute}';
        routeTripId = '${shippingTrip.isHasOptimalRoute}';
    </#if>
	<#assign company = Static['com.olbius.basehr.util.MultiOrganizationUtil'].getCurrentOrganization(delegator, userLogin.get('userLoginId'))! />;
    <#assign localeStr = "VI" />
    <#if locale = "en">
        <#assign localeStr = "EN" />
    </#if>
    <#assign statusDEes = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "TRIP_STATUS"), null, null, null, false)/>
    <#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
    var statusDataDE = new Array();
    var statusAllStatusDE = new Array();
    <#list statusDEes as item>
		  var row = {};
      <#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
      <#if item.statusId != 'TRIP_CANCELLED'>
			   statusDataDE.push(row);
      </#if>
		  row['statusId'] = '${item.statusId}';
		  row['description'] = '${descStatus?if_exists}';
		  statusAllStatusDE.push(row);
    </#list>

    <#assign statusPack = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "SHIP_PACK_STATUS"), null, null, null, false)/>
    var statusAllStatusPack = new Array();
    <#list statusPack as item>
		  var row = {};
      <#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
		  row['statusId'] = '${item.statusId}';
		  row['description'] = '${descStatus?if_exists}';
		  statusAllStatusPack.push(row);
    </#list>
    <#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
    var quantityUomData = new Array();
    <#list quantityUoms as item>
		  var row = {};
		  row['uomId'] = "${item.uomId}";
		  row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		  quantityUomData.push(row);
    </#list>

    <#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
      var weightUomData = new Array();
    <#list weightUoms as item>
		  var row = {};
		  row['uomId'] = "${item.uomId}";
		  row['description'] = "${StringUtil.wrapString(item.get('abbreviation', locale)?if_exists)}";
		  weightUomData.push(row);
    </#list>

    function getUomDescription(uomId) {
        for (x in quantityUomData) {
            if (quantityUomData[x].uomId == uomId) {
                return quantityUomData[x].description;
            }
        }
        for (x in weightUomData) {
            if (weightUomData[x].uomId == uomId) {
                return weightUomData[x].description;
            }
        }
    }

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
    uiLabelMap.RequireDeliveryDate = "${StringUtil.wrapString(uiLabelMap.RequireDeliveryDate)}";
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
    uiLabelMap.BSCustomerId = "${uiLabelMap.BSCustomerId}";
    uiLabelMap.BSCustomerName = "${uiLabelMap.BSCustomerName}";
    uiLabelMap.PhoneNumber = "${uiLabelMap.PhoneNumber}";
    uiLabelMap.BSSequenceIdCustomer = "${uiLabelMap.BSSequenceIdCustomer}";
    uiLabelMap.FacilityName = "${StringUtil.wrapString(uiLabelMap.FacilityName)}";
    uiLabelMap.BLMissedLatLong = "${StringUtil.wrapString(uiLabelMap.BLMissedLatLong)}";
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
<script type="text/javascript" src="/logresources/js/trip/tripDetailPack.js"></script>
<script type="text/javascript" src="/logresources/js/trip/tripDetailRoute.js"></script>