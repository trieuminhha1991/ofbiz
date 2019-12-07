<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "User_rowDetail.ftl"/>
<#include "popup/OverridePermission.ftl"/>
<#include "component://administration/webapp/administration/common/AdministrationConfig.ftl"/>
<#include "popup/addUserGroup.ftl"/>
<#include "popup/UserInGroup.ftl"/>

<#assign dataField = "[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'partyTypeId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'description', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.ADUserGroupId)}', dataField: 'partyCode', width: 250,
						validation: function (cell, value) {
							if (value) {
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									var check = DataAccess.getData({
										url: 'checkPartyCode',
										data: {partyCode: value, partyId: mainGrid.jqxGrid('getcellvalue', cell.row, 'partyId')},
										source: 'check'});
									if ('false' == check) {
										return { result: false, message: '${uiLabelMap.BSCodeAlreadyExists}' };
									} else {
										return true;
									}
								} else {
									return { result: false, message: '${uiLabelMap.ContainSpecialSymbol}' };
								}
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description' }"/>

<@jqGrid id="jqxgridUserInModule" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="addUserGroup"
	columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"
	url="jqxGeneralServicer?sname=JQGetListUserGroupSecurity" contextMenuId="contextMenu" mouseRightMenu="true"
	createUrl="jqxGeneralServicer?sname=createPartyGroupIncludePartyCode&jqaction=C" addColumns="partyCode;description;groupName;partyTypeId;statusId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyGroupIncludePartyCode" editColumns="partyId;partyCode;description"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="viewListUser"><i class="fa fa-user"></i>&nbsp;${uiLabelMap.ADListUser}</li>
	</ul>
</div>
<script src="/crmresources/js/generalUtils.js"></script>
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
	var mainGrid;
	$(document).ready(function() {
		mainGrid = $("#jqxgridUserInModule");
		OverridePermission.init();
		UserInGroup.init();
		
		var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 200, autoOpenPopup: false, mode: "popup"});
		contextmenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "viewListUser":
				var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
				UserInGroup.open(mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "partyId"));
				break;
			default:
				break;
			}
		});
	});
</script>