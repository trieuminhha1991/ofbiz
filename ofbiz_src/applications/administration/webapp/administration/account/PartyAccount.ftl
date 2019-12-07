<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{ name: 'userLoginId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'partyName', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.userLoginId)}', dataField: 'userLoginId', width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', dataField: 'partyCode', width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName' }"/>

<@jqGrid id="jqxgridPartyAccount" addrow="false" clearfilteringbutton="true" editable="false"
	columnlist=columnlist dataField=dataField contextMenuId="contextMenu" mouseRightMenu="true"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListUserLogin"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="resetPassword"><i class="fa fa-eraser"></i>&nbsp;${uiLabelMap.SettingResetPassword}</li>
	</ul>
</div>
<script>
var mainGrid;
$(document).ready(function() {
	mainGrid = $("#jqxgridPartyAccount");
	var contextmenu = $("#contextMenu").jqxMenu({ theme: theme, width: 180, autoOpenPopup: false, mode: "popup"});
	contextmenu.on("itemclick", function (event) {
		var args = event.args;
		var itemId = $(args).attr("id");
		switch (itemId) {
		case "resetPassword":
			bootbox.confirm("${StringUtil.wrapString(uiLabelMap.BSAreYouSureResetPasswordOfThisAccount)}", "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}", function(result) {
				if (result) {
					var rowIndexSelected = mainGrid.jqxGrid('getSelectedRowindex');
					DataAccess.executeAsync({
						url: "resetPassword",
						data: { userLoginId: mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "userLoginId")}
						}, notify);
				}
			});
			break;
		default:
			break;
		}
	});
	var notify = function(res) {
		if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
			Grid.renderMessage(mainGrid.attr("id"), multiLang.updateError, {
				autoClose : true,
				template : "error",
				appendContainer : "#container" + mainGrid.attr("id"),
				opacity : 0.9
			});
		} else {
			Grid.renderMessage(mainGrid.attr("id"), multiLang.updateSuccess, {
				autoClose : true,
				template : "info",
				appendContainer : "#container" + mainGrid.attr("id"),
				opacity : 0.9
			});
			mainGrid.jqxGrid("updatebounddata");
		}
	};
});
</script>