<script type="text/javascript">
<#assign personFactory = delegator.findList('PersonFactoryFact', null, null, null, null, false) />
<#assign emplList = Static['com.olbius.basehr.util.PartyUtil'].getEmplInOrgAtPeriod(delegator, userLogin.userLoginId) />

var listPersonFactory = [
     <#if emplList?exists>
	     <#list emplList as pF>
			{
				partyId : "${pF.partyId?if_exists}",
				fullName : "${pF.fullName?if_exists}"
			},
	     </#list>  
     </#if>
 ];

var monthSort = [
	{text : '${StringUtil.wrapString(uiLabelMap.CommonJanuary)}', value : 'jan'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonFebruary)}', value : 'feb'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonMarch)}', value : 'mar'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonApril)}', value : 'apr'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonMay)}', value : 'may'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonJune)}', value : 'jun'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonJuly)}', value : 'jul'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonAugust)}', value : 'aug'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonSepember)}', value : 'sep'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonOctobor)}', value : 'oct'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonNovember)}', value : 'nov'},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonDecember)}', value : 'dec'},
];
<#if !rootOrgList?exists>
	<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {
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
</script>