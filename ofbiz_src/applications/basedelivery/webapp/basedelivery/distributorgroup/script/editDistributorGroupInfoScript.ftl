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
    <#if productStoreGroupId?exists>
        <#list listDistributor as data>
        var idStr = '${data.partyCode}';
        if (typeof(productPricesMap[idStr]) != "undefined") {
            var itemValue = productPricesMap[idStr];
            itemValue.selected = true;
            productPricesMap[idStr] = itemValue;
        } else {
            var itemValue = {};
            itemValue.partyCode = '${data.partyCode?if_exists}';
            itemValue.groupName = '${data.groupName?if_exists}';
            itemValue.contactNumber = '${data.contactNumber?if_exists}';
            itemValue.address1 = '${data.address1?if_exists}';
            itemValue.emailAddress = '${data.emailAddress?if_exists}';
            itemValue.statusId = '${data.statusId?if_exists}';
            itemValue.selected = true;
            productPricesMap[idStr] = itemValue;
        }
        </#list>
    </#if>

    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
</script>
<script type="text/javascript"
        src="/deliresources/js/distributorgroup/editDistributorGroupInfo.js"></script>
