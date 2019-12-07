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

    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
</script>
<script type="text/javascript"
        src="/deliresources/js/distributorgroup/newDistributorGroupInfo.js"></script>
