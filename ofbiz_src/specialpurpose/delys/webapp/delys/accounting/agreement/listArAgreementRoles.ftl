<#if parameters.agreementId?exists>
<script>
	<#assign roleTypeList = delegator.findList("RoleType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["AGENT", "CUSTOMER", "SUPPLIER", "CONSUMER", "DISTRIBUTOR", "BUYER",  "VENDOR", "CONTRUCTOR", "PARTNER", "PERSON_ROLE", "ORGANIZATION_ROLE"]), null, null, null, false) />
	var rtData = new Array();
	<#list roleTypeList as item >
		var row = {};
		row['roleTypeId'] = '${item.roleTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		rtData[${item_index}] = row;
	</#list>
	var roleTypeUnique = [
		<#list roleTypeList as item>
		'${item.roleTypeId?if_exists}',
		</#list>
	];
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'roleTypeId', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accAgreementPartyId}', width: '50%', datafield: 'partyId'},
					 { text: '${uiLabelMap.roleTypeId}', datafield: 'roleTypeId'}"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="accTransaction" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementRoles&agreementId=${parameters.agreementId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementRole&jqaction=C&agreementId=${parameters.agreementId}"
		 removeUrl="jqxGeneralServicer?sname=deleteAgreementRole&jqaction=D&agreementId=${parameters.agreementId}"
		 addColumns="agreementId[${parameters.agreementId}];partyId;roleTypeId"
		 deleteColumn="agreementId[${parameters.agreementId}];partyId;roleTypeId"
		 />
 <#include 'popupAgreementRoles.ftl'/>
</#if>