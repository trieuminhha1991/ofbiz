<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)/>
<script type="text/javascript">
    <#assign priorityList = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_PRIORITY"}, ["sequenceId"], false)/>
    var priorityData = [
    <#if priorityList?exists>
        <#list priorityList as priority>
            {
                enumId: "${priority.enumId}",
                description: "${StringUtil.wrapString(priority.description?default(""))}",
            },
        </#list>
    </#if>];

    <#assign productGroupList = delegator.findByAnd("ProductStoreGroup", {"productStoreGroupTypeId" : "SHIPPING"}, null, false)/>
    var allGroup = [];
    var productGroupData = [
    <#if productGroupList?exists>
        <#list productGroupList as item>
            {
                productStoreGroupId: "${item.productStoreGroupId}",
                productStoreGroupName: "${StringUtil.wrapString(item.productStoreGroupName?default(""))}",
            },
        </#list>
    </#if>];
    allGroup = productGroupData.slice();
    allGroup.push(
            {
                productStoreGroupId: "ALL",
                productStoreGroupName: "${StringUtil.wrapString(uiLabelMap.BDAll)}"
            },
            {
                productStoreGroupId: "EMPTY",
                productStoreGroupName: "${StringUtil.wrapString(uiLabelMap.BDEmpty)}"
            }
    );
//    console.log('allGroup', allGroup);


    <#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "DELIVERY_STATUS"}, null, false)/>
    var orderStatusData = [
    <#if orderStatuses?exists>
        <#list orderStatuses as statusItem>
            {
                statusId: '${statusItem.statusId}',
                description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
            },
        </#list>
    </#if>];

    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
    var requirementSelected = {};
    <#if requirement?exists>
    requirementSelected.requirementId = "${requirement.requirementId?if_exists}";
        <#if requirement.requiredByDate?exists>
        requirementSelected.requiredByDate = "${requirement.requiredByDate}";
        </#if>
        <#if requirement.requirementStartDate?exists>
        requirementSelected.requirementStartDate = "${requirement.requirementStartDate}";
        </#if>
    </#if>

    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BDDeliveryId = "${StringUtil.wrapString(uiLabelMap.BDDeliveryId)}";
    uiLabelMap.BDAll= "${StringUtil.wrapString(uiLabelMap.BDAll)}";
    uiLabelMap.BDEmpty = "${StringUtil.wrapString(uiLabelMap.BDEmpty)}";
    uiLabelMap.BDPartyIdFrom = "${StringUtil.wrapString(uiLabelMap.BDPartyIdFrom)}";
    uiLabelMap.BDPartyIdTo = "${StringUtil.wrapString(uiLabelMap.BDPartyIdTo)}";
    uiLabelMap.BDDesContactMechId = "${StringUtil.wrapString(uiLabelMap.BDDesContactMechId)}";
    uiLabelMap.BDTotalWeight = "${StringUtil.wrapString(uiLabelMap.BDTotalWeight)}";
    uiLabelMap.BDDeliveryDate = "${StringUtil.wrapString(uiLabelMap.BDDeliveryDate)}";
    uiLabelMap.BDCreateDate = "${StringUtil.wrapString(uiLabelMap.BDCreateDate)}";
    uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
    uiLabelMap.BDProductStoreGroupId = "${StringUtil.wrapString(uiLabelMap.BDProductStoreGroupId)}";
    uiLabelMap.BDProductStoreGroupName = "${StringUtil.wrapString(uiLabelMap.BDProductStoreGroupName)}";
    uiLabelMap.BSClickToChoose = '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}';

</script>
<script type="text/javascript"
        src="/deliresources/js/trip/newTripInfo.js"></script>
