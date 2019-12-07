<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
<script type="text/javascript">
var globalHistoryDetailObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

var globalHistoryDetailVar = {
		rootPartyArr: [
   			<#if rootOrgList?has_content>
   				<#list rootOrgList as rootOrgId>
   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
   				{
   					partyId: "${rootOrgId}",
   					partyName: "${rootOrg.groupName}"
   				},
   				</#list>
   			</#if>
   		],
};

var uiLabelMapDetail = {
		HRIncome: '${StringUtil.wrapString(uiLabelMap.HRIncome)}',
		HRCommonAmount: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}',
		OrganizationPaid: '${StringUtil.wrapString(uiLabelMap.OrganizationPaid)}',
		HRDeduction: '${StringUtil.wrapString(uiLabelMap.HRDeduction)}',
		OrgPaidType: '${StringUtil.wrapString(uiLabelMap.OrgPaidType)}',
		PayrollItemType: '${StringUtil.wrapString(uiLabelMap.PayrollItemType)}',
		AccountingInvoiceItemType: '${StringUtil.wrapString(uiLabelMap.AccountingInvoiceItemType)}',
		FieldRequired: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}'
};
</script>
 