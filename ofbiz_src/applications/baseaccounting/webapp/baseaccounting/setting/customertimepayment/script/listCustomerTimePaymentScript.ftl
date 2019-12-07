<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};

globalVar.enumPartyTypeArr = [
<#if enumPartyTypeList?exists>
    <#list enumPartyTypeList as enumPartyType>
        {
            enumId: "${enumPartyType.enumId}",
            description: '${StringUtil.wrapString(enumPartyType.description)}'
        },
    </#list>
</#if>
];
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EmailRequired = "${StringUtil.wrapString(uiLabelMap.EmailRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateCustomerConfirm = "${StringUtil.wrapString(uiLabelMap.CreateCustomerConfirm)}";
uiLabelMap.ExpireCustomerRelationshipConfirm = "${StringUtil.wrapString(uiLabelMap.ExpireCustomerRelationshipConfirm)}";
uiLabelMap.BACCOrganizationId = "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}";
uiLabelMap.BACCFullName = "${StringUtil.wrapString(uiLabelMap.BACCFullName)}";
uiLabelMap.CreateSuccessfully = "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}";
uiLabelMap.editCustomerTimePayment = "${StringUtil.wrapString(uiLabelMap.Edit)}";
uiLabelMap.BACCCreateNew = "${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}";
</script>