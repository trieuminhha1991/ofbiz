<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
    <#assign idGridJQ = "jqxEditSO"/>
    <#assign addType = "popup"/>
    <#assign alternativeAddPopup="alterpopupWindow"/>

    function renderJqxTitle(){
    <#if titleProperty?has_content && (!customTitleProperties?exists || customTitleProperties == "")>
        <@renderJqxTitle titlePropertyTmp=titleProperty id=idGridJQ/>
        return jqxheader;
    <#elseif customTitleProperties?exists && customTitleProperties != "">
        <@renderJqxTitle titlePropertyTmp=customTitleProperties id=idGridJQ/>
        return jqxheader;
    </#if>
        return "";
    }
    var dataCreateAddRowButton = null;
    var addType = "${addType}";
    <#if addType != "popup">
        <#if addinitvalue !="">
        dataCreateAddRowButton = {${primaryColumn}: '${addinitvalue}'}
        <#else>
        dataCreateAddRowButton = ${primaryColumn}
        </#if>
    </#if>

    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BDTripId = "${StringUtil.wrapString(uiLabelMap.BDTripId)}";
    uiLabelMap.BDScLogId = "${StringUtil.wrapString(uiLabelMap.BDScLogId)}";
    uiLabelMap.BDContractorId = "${StringUtil.wrapString(uiLabelMap.BDContractorId)}";
    uiLabelMap.BDVehicleId = "${StringUtil.wrapString(uiLabelMap.BDVehicleId)}";
    uiLabelMap.BDDriverId = "${StringUtil.wrapString(uiLabelMap.BDDriverId)}";
    uiLabelMap.BDStatusId= "${StringUtil.wrapString(uiLabelMap.BDStatusId)}";
    uiLabelMap.BDDescription = "${StringUtil.wrapString(uiLabelMap.BDDescription)}";
    uiLabelMap.BDTotalWeight = "${StringUtil.wrapString(uiLabelMap.BDTotalWeight)}!";
    uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
    uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
    uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen = "${StringUtil.wrapString(uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen)}";
    uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem)}";
    uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";

    uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}!";
    uiLabelMap.BSDeleteOrderItem = "${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}";

    var idGridJQ = "${idGridJQ}";
    var alternativeAddPopup = <#if alternativeAddPopup?exists>"${alternativeAddPopup}"<#else>""</#if>

    jOlbUtil.setUiLabelMap("wgupdatesuccess", uiLabelMap.wgupdatesuccess);
</script>

<script type="text/javascript">
    <#assign vehicles = delegator.findByAnd("VehicleV2", null, null, false)/>
    var vehicleData = [
    <#if vehicles?exists>
        <#list vehicles as vehicle>
            {
                vehicleId: '${vehicle.vehicleId}',
                licensePlate: '${StringUtil.wrapString(vehicle.get("licensePlate", locale))}'
            },
        </#list>
    </#if>];
</script>


<script type="text/javascript">
    <#assign tripStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "TRIP_STATUS"}, null, false)/>
    var tripStatusData = [
    <#if tripStatuses?exists>
        <#list tripStatuses as statusItem>
            {
                statusId: '${statusItem.statusId}',
                description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
            },
        </#list>
    </#if>];
</script>

<script type="text/javascript" src="/deliresources/js/trip/editTrip.js"></script>