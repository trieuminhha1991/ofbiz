<@jqGridMinimumLib/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(monthStart, timeZone, locale)/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript">
var globalVar = {};
var emplPosTypeArr = [
	<#if emplPosType?has_content>	                      
		<#list emplPosType as posType>
			{
				emplPositionTypeId: "${posType.emplPositionTypeId}",
				description: "${StringUtil.wrapString(posType.description)}" 		
			},
		</#list>                     
	</#if>
];
var periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: "${periodType.periodTypeId}",
				description: "${StringUtil.wrapString(periodType.description?if_exists)}"		
			},
		</#list>
	</#if>
];

var roleTypeGroupArr = [
	<#if roleTypeGroupList?has_content>
		<#list roleTypeGroupList as roleTypeGroup>
			{
				roleTypeGroupId: "${roleTypeGroup.roleTypeGroupId}",
				description: "${StringUtil.wrapString(roleTypeGroup.description)}"
			}
		</#list>
	</#if>
];

var uomArray = [
	<#if uomList?has_content>
		<#list uomList as uom>
			{
				uomId: "${uom.uomId}",
				description: "${uom.description?if_exists}" 	
			},
		</#list>
	</#if>
];

var globalVar = {
		<#if defaultPeriodTypeId?exists>
		defaultPeriodTypeId: "${defaultPeriodTypeId}", 	
		</#if>
		nowTimestamp: ${nowTimestamp.getTime()},
		monthStart: ${monthStart.getTime()},
		monthEnd: ${monthEnd.getTime()}
};
var uiLabelMap = {};
uiLabelMap.AddRowDataConfirm = "${StringUtil.wrapString(uiLabelMap.AddRowDataConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.EmplPositionTypeRateGeoApplUpdate = "${StringUtil.wrapString(uiLabelMap.EmplPositionTypeRateGeoApplUpdate)}";
uiLabelMap.AmountValueGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.AmountValueGreaterThanZero)}";
uiLabelMap.CommonRequired = "${StringUtil.wrapString(uiLabelMap.CommonRequired)}";
uiLabelMap.NotChooseOrChooseInvalid = "${StringUtil.wrapString(uiLabelMap.NotChooseOrChooseInvalid)}";
uiLabelMap.ChooseInvalid = "${StringUtil.wrapString(uiLabelMap.ChooseInvalid)}";
uiLabelMap.AddRowDataConfirm = "${StringUtil.wrapString(uiLabelMap.AddRowDataConfirm)}?";

<#if setupByGeo>
	globalVar.setupByGeo = true;
	var prepareDataTreeGeoObject = (function(){
		var getDataIncludeGeo = function(){
			var dataIncludeGeo = [
	       		<#if geoRegionList?has_content>
	       			<#list geoRegionList as geo>
	       				{
	       					"id": "${geo.geoId}_include",
	       					"parentid": "-1",
	       					"text": "${StringUtil.wrapString(geo.geoName)}",
	       					"value": "${geo.geoId}"
	       				},
	       				{
	       					"id": "${geo.geoId}_inclChild",
	       					"parentid": "${geo.geoId}_include",
	       					"text": "Loading...",
	       					"value": "getGeoAssoc"
	       				}
	       				<#if geo_has_next>
	       					,
	       				</#if>
	       			</#list>
	       		</#if>
	       	];
			return dataIncludeGeo;
		};
		
		var getDataExcludeGeo = function(){
			var dataExcludeGeo = [
           		<#if geoRegionList?has_content>
           			<#list geoRegionList as geo>
           				{
           					"id": "${geo.geoId}_exclude",
           					"parentid": "-1",
           					"text": "${StringUtil.wrapString(geo.geoName)}",
           					"value": "${geo.geoId}"
           				},
           				{
           					"id": "${geo.geoId}_exclChild",
           					"parentid": "${geo.geoId}_exclude",
           					"text": "Loading...",
           					"value": "getGeoAssoc"
           				}
           				<#if geo_has_next>
           					,
           				</#if>
           			</#list>
           		</#if>
           	];
			return dataExcludeGeo;
		};
       	
		var getDataIncludeGeoEdit = function(){
			var dataIncludeGeoEdit = [
           		<#if geoRegionList?has_content>
           			<#list geoRegionList as geo>
           				{
           					"id": "${geo.geoId}_includeEdit",
           					"parentid": "-1",
           					"text": "${StringUtil.wrapString(geo.geoName)}",
           					"value": "${geo.geoId}"
           				},
           				{
           					"id": "${geo.geoId}_inclChildEdit",
           					"parentid": "${geo.geoId}_includeEdit",
           					"text": "Loading...",
           					"value": "getGeoAssoc"
           				}
           				<#if geo_has_next>
           					,
           				</#if>
           			</#list>
           		</#if>
           	];
			return dataIncludeGeoEdit;
		};
       	
       	var getDataExcludeGeoEdit = function(){
       		var dataExcludeGeoEdit = [
          		<#if geoRegionList?has_content>
          			<#list geoRegionList as geo>
          				{
          					"id": "${geo.geoId}_excludeEdit",
          					"parentid": "-1",
          					"text": "${StringUtil.wrapString(geo.geoName)}",
          					"value": "${geo.geoId}"
          				},
          				{
          					"id": "${geo.geoId}_exclChild_edit",
          					"parentid": "${geo.geoId}_excludeEdit",
          					"text": "Loading...",
          					"value": "getGeoAssoc"
          				}
          				<#if geo_has_next>
          					,
          				</#if>
          			</#list>
          		</#if>
          	];
       		return dataExcludeGeoEdit;
       	};
       	return{
       		getDataIncludeGeo: getDataIncludeGeo,
       		getDataExcludeGeo: getDataExcludeGeo,
       		getDataIncludeGeoEdit: getDataIncludeGeoEdit,
       		getDataExcludeGeoEdit: getDataExcludeGeoEdit
       	}
	}());
</#if>
globalVar.useRoleTypeGroup = ${useRoleTypeGroup};
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
globalVar.rootPartyArr = [
	<#if rootOrgList?has_content>
		<#list rootOrgList as rootOrgId>
			<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
			{
				partyId: "${rootOrgId}",
				partyName: "${rootOrg.groupName}"
			},
		</#list>
	</#if>
];
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

function enableAlterSave(){
	 $("#alterSave").removeAttr("disabled");
}

function refreshRowDetail(){
	if(rowUpdateIndex > -1 ){
		var id = $('#jqxgrid').jqxGrid('getrowid', rowUpdateIndex);
		 if($("#jqxgridDetail_" + id).length){
			 $("#jqxgridDetail_" + id).jqxGrid('updatebounddata');	 
		 }	
	}
}
</script>