<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "User_rowDetail.ftl"/>
<#include "popup/OverridePermission.ftl"/>
<#include "component://administration/webapp/administration/common/AdministrationConfig.ftl"/>

<#assign dataField = "[{ name: 'userLoginId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'partyName', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.userLoginId)}', dataField: 'userLoginId', width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', dataField: 'partyCode', width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName' }"/>

<@jqGrid id="jqxgridUserInModule" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="jqxwindowAddUser"
	columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"
	url="jqxGeneralServicer?sname=JQGetListUserLogin"/>

<script>
	multiLang = _.extend(multiLang, {
		ADPermissionDefault: "${StringUtil.wrapString(uiLabelMap.ADPermissionDefault)}",
		ADApplication: "${StringUtil.wrapString(uiLabelMap.ADApplication)}",
		ADApplicationId: "${StringUtil.wrapString(uiLabelMap.ADApplicationId)}",
		ADApplicationName: "${StringUtil.wrapString(uiLabelMap.ADApplicationName)}",
		userLoginId: "${StringUtil.wrapString(uiLabelMap.userLoginId)}",
		EmployeeName: "${StringUtil.wrapString(uiLabelMap.EmployeeName)}",
		CommonEmployee: "${StringUtil.wrapString(uiLabelMap.CommonEmployee)}",
		ADOverridePermission: "${StringUtil.wrapString(uiLabelMap.ADOverridePermission)}",
		ADModule: "${StringUtil.wrapString(uiLabelMap.ADModule)}",
		ADAction: "${StringUtil.wrapString(uiLabelMap.ADAction)}",
		ADActionId: "${StringUtil.wrapString(uiLabelMap.ADActionId)}",
		ADActionName: "${StringUtil.wrapString(uiLabelMap.ADActionName)}",
		});
	$(document).ready(function() {
		OverridePermission.init();
	});
</script>