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

<#assign emplLeaveReasonType = delegator.findList('EmplLeaveReasonType', null, null, null, null, false)/>

var listLeaveReasonType = [
       <#if emplLeaveReasonType?exists>
       		<#list emplLeaveReasonType as elrt>
       			{
       				emplLeaveReasonTypeId : "${elrt.emplLeaveReasonTypeId?if_exists}",
       				description : "${elrt.description?if_exists}"
       			},
       		</#list>
       </#if>
]
</script>