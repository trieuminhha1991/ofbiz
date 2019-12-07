<script>
	<#assign roleTypeList = delegator.findList("RoleType",  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["AGENT", "CUSTOMER", "SUPPLIER", "CONSUMER", "DISTRIBUTOR", "BUYER",  "VENDOR", "CONTRUCTOR", "PARTNER", "PERSON_ROLE", "ORGANIZATION_ROLE"]), null, null, null, false) />
	var roleTypeData = [<#if roleTypeList?exists><#list roleTypeList as roleType><#assign description = StringUtil.wrapString(roleType.get("description", locale)) /> {description:"${description}", roleTypeId:"${roleType.roleTypeId}"},</#list></#if>];
	var listRoleCondition = [<#if roleTypeList?exists><#list roleTypeList as roleType>"${roleType.roleTypeId}",</#list></#if>];
	<#assign agreementTypeList = delegator.findList("AgreementType", null, null, null, null, false) />
	var agreementTypeData = [<#if agreementTypeList?exists><#list agreementTypeList as agreementType>{<#assign description = StringUtil.wrapString(agreementType.get("description", locale)) />description:"${description}", agreementTypeId:"${agreementType.agreementTypeId}"},</#list></#if>];
	<#assign listStatusItem = delegator.findByAnd("StatusItem", {"statusTypeId" : "AGREEMENT_STATUS"}, null, false) />
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
</script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<style type="text/css">
	.background-red {
		background-color: #ddd !important;
	}
</style>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'partyIdFrom', type: 'string' },
					{ name: 'partyIdTo', type: 'string' },
					{ name: 'fullNameF', type: 'string' },
					{ name: 'fullNameT', type: 'string' },
					{ name: 'groupNameF', type: 'string' },
					{ name: 'groupNameT', type: 'string' },
					{ name: 'roleTypeIdFrom', type: 'string' },
					{ name: 'roleTypeIdTo', type: 'string' },
					{ name: 'agreementTypeId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'agreementDate', type: 'date', other:'Timestamp' },
					{ name: 'fromDate', type: 'date', other:'Timestamp' },
					{ name: 'thruDate', type: 'date', other:'Timestamp' },
					{ name: 'description', type: 'string' },
					{ name: 'textData', type: 'string' }]"/>

<#assign columnlist = "{ text: '${uiLabelMap.DAAgreementId}', width:150, datafield: 'agreementId',
							cellsrenderer: function (row, column, value) {
								return \"<span><a href='editAgreement?agreementId=\" + value + \"'>\" + value + \"</a></span>\";
							}
						},
						{ text: '${uiLabelMap.POSupplier}', width:300, datafield: 'groupNameF', width:200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: 150, editable: true,
							cellsrenderer: function(row, colum, value) {
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
							}
						},
						{ text: '${uiLabelMap.DAAgreementDate}', width:150, datafield: 'agreementDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
						{ text: '${uiLabelMap.DAFromDate}', width:150, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
						{ text: '${uiLabelMap.DAThruDate}', width:150, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
						{ text: '${uiLabelMap.DADescription}', width:150, datafield: 'description' },
						{ text: '${uiLabelMap.textValue}', width:150, datafield: 'textData' }"/>

<#assign addrow = "false" />
<#if hasOlbEntityPermission("ENTITY_PURCHASE_AGREEMENT", "CREATE")>
	<#assign addrow = "true" />
</#if>
						
<@jqGrid url="jqxGeneralServicer?sname=JQGetListAgreementPurchase" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" autorowheight="false"
	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" addrow=addrow addType="popup" deleterow="false" defaultSortColumn="-agreementDate"
	createUrl="jqxGeneralServicer?sname=createAgreement&jqaction=C" addColumns="partyIdFrom;partyIdTo;roleTypeIdFrom;roleTypeIdTo;agreementTypeId;agreementDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description;statusId[AGREEMENT_CREATED]"
	contextMenuId="contextMenu" mouseRightMenu="true"/>

${screens.render("component://basepo/widget/AgreementScreens.xml#AddAgreement")}
<#include "component://basesalesmtl/webapp/basesalesmtl/common/uploadFileScan.ftl"/>
 <div id="contextMenu" style="display:none;">
	<ul>
		<#if hasOlbEntityPermission("ENTITY_PURCHASE_AGREEMENT", "APPROVE")>
			<li id="approveAgreement"><i class="fa fa-check"></i>${uiLabelMap.DmsApprove}</li>
		</#if>
		<#if hasOlbEntityPermission("ENTITY_PURCHASE_AGREEMENT", "APPROVE")>
			<li id="cancelAgreement"><i class="fa fa-ban"></i>${uiLabelMap.CommonCancel}</li>
		</#if>
		<#if hasOlbEntityPermission("ENTITY_PURCHASE_AGREEMENT", "CREATE")>
			<li id="uploadFileScan"><i class="fa fa-cloud-upload"></i>${uiLabelMap.UploadFileScan}</li>
		</#if>
		<li id="viewFileScan"><i class="fa fa-file-image-o"></i>${uiLabelMap.ViewFileScan}</li>
		<li id="refresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script>
$(document).ready(function() {
	var mainGrid = $("#jqxgrid");
	$("#contextMenu").jqxMenu({ theme: theme, width: 170, autoOpenPopup: false, mode: "popup"});
	$("#contextMenu").on("shown", function () {
		var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
		var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
		var statusId = rowData.statusId;
		if (statusId == "AGREEMENT_CREATED" || statusId == "AGREEMENT_MODIFIED") {
			$("#contextMenu").jqxMenu("disable", "approveAgreement", false);
		} else {
			$("#contextMenu").jqxMenu("disable", "approveAgreement", true);
		}
		if (statusId == "AGREEMENT_CANCELLED") {
			$("#contextMenu").jqxMenu("disable", "cancelAgreement", true);
		} else {
			$("#contextMenu").jqxMenu("disable", "cancelAgreement", false);
		}
	});
	$("#contextMenu").on("itemclick", function (event) {
		var args = event.args;
		var itemId = $(args).attr("id");
		switch (itemId) {
		case "uploadFileScan":
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
			Uploader.open(rowData.agreementId);
			break;
		case "viewFileScan":
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
			Viewer.open(rowData.agreementId);
			break;
		case "approveAgreement":
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
			var agreementId = rowData.agreementId;
			var result = DataAccess.execute({
					url: "approveAgreement",
					data: {agreementId: agreementId}
					});
			if (result) {
				mainGrid.jqxGrid("setcellvaluebyid", rowData.uid, "statusId", "AGREEMENT_APPROVED");
				mainGrid.jqxGrid("refreshdata");
			}
			break;
		case "cancelAgreement":
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
			var agreementId = rowData.agreementId;
			var result = DataAccess.execute({
				url: "cancelAgreement",
				data: {agreementId: agreementId}
			});
			if (result) {
				mainGrid.jqxGrid("setcellvaluebyid", rowData.uid, "statusId", "AGREEMENT_CANCELLED");
				mainGrid.jqxGrid("refreshdata");
			}
			break;
		case "refresh":
			mainGrid.jqxGrid("updatebounddata");
			break;
		default:
			break;
		}
	});
});
</script>