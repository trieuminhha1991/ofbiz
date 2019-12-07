<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#if hasOlbEntityPermission("ORG_MGR", "CREATE")>
	<#assign hasCreatePermission = true/>
<#else>
	<#assign hasCreatePermission = false/>
</#if>
<#if hasCreatePermission>
	<#assign roleTypeList = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "DEPARTMENT"), null, false)>
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	<#if !rootOrgList?exists>
		<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
	</#if>
	var globalObject = (function(){
			<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
			var updateSourceJqxDropdownList = function (dropdownlistEle, data, url, selectItem){
	        	$.ajax({
	        		url: url,
	        		data: data,
	        		type: 'POST',
	        		success: function(response){
	        			var listGeo = response.listReturn;
	        			if(listGeo && listGeo.length > -1){
	        				updateSourceDropdownlist(dropdownlistEle, listGeo);        				
	        				if(selectItem != 'undefinded'){
	        					dropdownlistEle.jqxDropDownList('selectItem', selectItem);
	        				}
	        			}
	        		}
	        	});
	        };
	        return {
	        	<#if countryGeoIdDefault?exists>
	        	countryGeoIdDefault: "${countryGeoIdDefault}", 
	        	</#if>
	        	createJqxTreeDropDownBtn: createJqxTreeDropDownBtn,
	        	updateSourceJqxDropdownList: updateSourceJqxDropdownList,
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
	        }
	}());
	
	var roleTypeData = [
		<#list roleTypeList as roleType>
			{roleTypeId: "${roleType.roleTypeId}", description: "${StringUtil.wrapString(roleType.description)}"}
			<#if roleType_has_next>
			,
			</#if>
		</#list>
	]; 
	 
	<#assign countrylList = delegator.findByAnd("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"), null, false)>
	var countryData = [
			<#list countrylList as item>
			{
				geoId: '${item.geoId?if_exists}',
				geoName: "${StringUtil.wrapString(item.geoName?if_exists)}"
			},
			</#list>
	];
</#if>	
	var uiLabelMap = {
			OrgUnitName: '${uiLabelMap.OrgUnitName}',
			OrgUnitId: '${uiLabelMap.OrgUnitId}',
			NumEmployees: '${uiLabelMap.NumEmployees}',
			CommonAddress: '${uiLabelMap.CommonAddress}',
			ClickToEdit: '${uiLabelMap.ClickToEdit}',
			CommonEdit: '${uiLabelMap.CommonEdit}',
			UpdateAddress: '${uiLabelMap.UpdateAddress}',
			DAAddNewAddress: '${uiLabelMap.DAAddNewAddress}',
			OrganizationUnit: '${uiLabelMap.OrganizationUnit}',
			HRCommonNotSetting: '${uiLabelMap.HRCommonNotSetting}',
			CommonRequired: '${uiLabelMap.CommonRequired}',
			CommonChooseFile: '${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}',
			AddNewRowConfirm: '${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm)}',
			CommonSubmit: '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}',
			CommonCancel: '${StringUtil.wrapString(uiLabelMap.CommonCancel)}',
			MustntHaveSpaceChar : '${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}',
			PartyIdCannotIsChildOfItSelf : '${StringUtil.wrapString(uiLabelMap.PartyIdCannotIsChildOfItSelf)}',
			OnlyContainInvalidChar : "${StringUtil.wrapString(uiLabelMap.OnlyContainInvalidChar)}",
			InvalidChar : "${StringUtil.wrapString(uiLabelMap.InvalidChar)}",
            EmployeeId : "${StringUtil.wrapString(uiLabelMap.EmployeeId)}",
            EmployeeName : "${StringUtil.wrapString(uiLabelMap.EmployeeName)}",
            HrCommonPosition : "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}",
            CommonDepartment : "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}",
	};
</script>