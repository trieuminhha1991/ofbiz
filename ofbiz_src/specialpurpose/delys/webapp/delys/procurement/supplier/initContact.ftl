<script>
	<#assign roleTypeList = delegator.findList("RoleType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["AGENT", "CUSTOMER", "SUPPLIER", "CONSUMER", "DISTRIBUTOR", "BUYER",  "VENDOR", "CONTRUCTOR", "PARTNER", "PERSON_ROLE", "ORGANIZATION_ROLE"]), null, null, null, false) />
	var roleTypeData = [<#if roleTypeList?exists><#list roleTypeList as roleType>{description:"${StringUtil.wrapString(roleType.description)?default("")}", roleTypeId:"${roleType.roleTypeId}"},</#list></#if>];	
	<#assign agreementTypeList = delegator.findList("AgreementType", null, null, null, null, false) />
	<#assign countrylList = delegator.findByAnd("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"), null, false)>
	var countryData = [<#list countrylList as item>{<#assign description = StringUtil.wrapString(item.geoName?if_exists) />geoId : '${item.geoId}',description : "${description}"},</#list>];
	<#assign provincelList = delegator.findList("GeoAssocAndGeoToDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["CITY", "PROVINCE"]), null, null, null, false)>
	var provinceData = [<#list provincelList as item><#assign description = StringUtil.wrapString(item.geoName?if_exists) />{geoId : '${item.geoId}',geoIdFrom : '${item.geoIdFrom}',description: "${description}"},</#list>];
	//Prepare District Geo
	<#assign districtlList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "DISTRICT"), null, false)>
	var districtData = [<#list districtlList as item><#assign description = StringUtil.wrapString(item.geoName?if_exists) />{ geoId: '${item.geoId}',geoIdFrom: '${item.geoIdFrom}',description : "${description}"},</#list>];
	//Prepare Ward Geo
	<#assign wardList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "WARD"), null, false)>
	var wardData = [<#list wardList as item><#assign description = StringUtil.wrapString(item.geoName?if_exists) />{geoId :  '${item.geoId}';geoIdFrom : '${item.geoIdFrom}';description : "${description}";},</#list>];
	<#assign statusList = delegator.findList("StatusItem",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PARTY_ENABLED", "PARTY_DISABLED"]), null, null, null, false) />
	var statusData = [<#if statusList?exists><#list statusList as status>{description:"${StringUtil.wrapString(status.description)?default("")}", statusId:"${status.statusId}"},</#list></#if>];
</script>

