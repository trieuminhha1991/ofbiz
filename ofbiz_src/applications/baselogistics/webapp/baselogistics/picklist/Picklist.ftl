<script type="text/javascript">
<#assign picklistStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PICKLIST_STATUS"}, null, false)/>
var picklistStatusData = [
<#if picklistStatuses?exists><#list picklistStatuses as statusItem>{
	statusId: "${statusItem.statusId}", description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
	},</#list></#if>];
var mapPicklistStatus = {
	<#if picklistStatuses?exists><#list picklistStatuses as statusItem>
		"${statusItem.statusId}": "${StringUtil.wrapString(statusItem.get("description", locale))}",
	</#list></#if>
}
<#assign pickbinStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "PICKBIN_STATUS"}, null, false)/>
var pickbinStatusData = [
<#if pickbinStatuses?exists><#list pickbinStatuses as statusItem>{
	statusId: "${statusItem.statusId}", description: "${StringUtil.wrapString(statusItem.get("description", locale))}"
	},</#list></#if>];
var mapPickbinStatus = {
	<#if pickbinStatuses?exists><#list pickbinStatuses as statusItem>
		"${statusItem.statusId}": "${StringUtil.wrapString(statusItem.get("description", locale))}",
	</#list></#if>
}

if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BLYouNotUpdateEmployeeYet = "${StringUtil.wrapString(uiLabelMap.BLYouNotUpdateEmployeeYet)}";
	uiLabelMap.AreYouSureApprove = "${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";
	uiLabelMap.AreYouSureCancel = "${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}";
	uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
	uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
	uiLabelMap.fieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
	uiLabelMap.updateError = "${StringUtil.wrapString(uiLabelMap.updateError)}";
	uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.updateSuccess)}";
	uiLabelMap.BLSearchProductInPicklistBin = "${StringUtil.wrapString(uiLabelMap.BLSearchProductInPicklistBin)}";
	uiLabelMap.BSProductId = "${StringUtil.wrapString(uiLabelMap.BSProductId)}";
	uiLabelMap.BSProductName = "${StringUtil.wrapString(uiLabelMap.BSProductName)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.DmsDownloadPicklist = "${StringUtil.wrapString(uiLabelMap.DmsDownloadPicklist)}";
	
	uiLabelMap.AreYouSureSave = '${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}';
	uiLabelMap.CommonCancel = '${StringUtil.wrapString(uiLabelMap.CommonCancel)}';
	uiLabelMap.OK = '${StringUtil.wrapString(uiLabelMap.OK)}';

</script>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#include "Picklist_rowDetail.ftl"/>
<#assign dataField = "[{ name: 'picklistId', type: 'string' },
					{ name: 'facilityId', type: 'string' },
					{ name: 'facilityCode', type: 'string' },
					{ name: 'facilityName', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'picklistDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoDotSoan)}', dataField: 'picklistId', width: 250 },
						{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', dataField: 'facilityCode', width: 250 },
						{ text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', dataField: 'facilityName', minwidth: 250 },
						{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'statusId', width: 250, filtertype: 'checkedlist',
							cellsrenderer: function(row, column, value){
								value = value?mapPicklistStatus[value]:value;
								return '<span title=' + value +'>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								widget.jqxDropDownList({ source: picklistStatusData, displayMember: 'description', valueMember: 'statusId' });
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsNgaySoan)}', datafield: 'picklistDate', width: 250, cellsformat: 'dd/MM/yyyy HH:mm', filtertype: 'range' }
						"/>

<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="addRival"
	columnlist=columnlist dataField=dataField contextMenuId="contextmenu" mouseRightMenu="true"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="270"
	url="jqxGeneralServicer?sname=JQGetListPicklist"/>

<#include "changeEmployee.ftl"/>

<div id="contextmenu" style="display:none;">
	<ul>
		<li id="mnuDownload"><i class="fa fa-download"></i>${uiLabelMap.DmsDownloadPicklist}</li>
		<li id="mnuRefresh"><i class="fa fa-refresh"></i>${uiLabelMap.BSRefresh}</li>
		<#if hasOlbPermission("MODULE", "LOG_PICKLIST", "UPDATE")>
		<#-- <li id="mnuApproved"><i class="fa fa-check"></i>${uiLabelMap.BACCApprove}</li> -->
		<#-- <li id="mnuEdit"><i class="fa fa-pencil-square-o"></i>${uiLabelMap.CommonEdit}</li> -->
		<#-- <li id="mnuCancel"><i class="fa fa-trash red"></i>${uiLabelMap.CommonCancel}</li> -->
		</#if>
	</ul>
</div>
<div id="contextMenuChild" style="display:none;">
	<ul>
		<li id="binViewDetail"><i class="fa fa-folder-open-o"></i>${uiLabelMap.BSViewDetail}</li>
		<#if hasOlbPermission("MODULE", "LOG_PICKLIST", "UPDATE")>
		
		<li id="binEmployee"><i class="fa fa-user"></i>${uiLabelMap.Employee}
		</li>
		
		<li id="binApprove"><i class="fa fa-check"></i>${StringUtil.wrapString(uiLabelMap.BACCApprove)}</li>
		<li id="binCancel"><i class="fa fa-trash red"></i>${uiLabelMap.CommonCancel}</li>
		</#if>
	</ul>
</div>
<script>
var mainGrid, contextMenuChild;
$(document).ready(function() {
	mainGrid = $("#jqxgrid");
	var contextmenu = $("#contextmenu").jqxMenu({ theme: theme, width: 220, autoOpenPopup: false, mode: "popup" });
	contextmenu.on("itemclick", function (event) {
		var args = event.args;
		var itemId = $(args).attr("id");
		var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
		var picklistId = mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "picklistId");
		switch (itemId) {
		case "mnuDownload":
			location.href = "exportPicklistExcelExcel?picklistId=" + picklistId;
			break;
		case "mnuEdit":
			location.href = "CreatePicklist?picklistId=" + picklistId
			break;
		case "mnuCancel":
			jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}", function() {
				DataAccess.executeAsync({ url: "cancelPicklist",
					data: {
						picklistId: picklistId
					} }, function () {
					mainGrid.jqxGrid("updatebounddata");
				});
			});
			break;
		case "mnuApproved":
			jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BLDmsAreYouSureApprove)}", function() {
				DataAccess.executeAsync({ url: "approvePicklist",
					data: {
						picklistId: picklistId
					} }, function () {
					mainGrid.jqxGrid("updatebounddata");
				});
			});
			break;
		case "mnuRefresh":
			mainGrid.jqxGrid("updatebounddata");
			break;
		default:
			break;
		}
	});

	contextmenu.on("shown", function () {
		var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
		var statusId = mainGrid.jqxGrid("getcellvalue", rowIndexSelected, "statusId");
		contextmenu.jqxMenu("disable", "mnuEdit", true);
		contextmenu.jqxMenu("disable", "mnuCancel", true);
		contextmenu.jqxMenu("disable", "mnuApproved", true);
		
		if (statusId === "PICKLIST_INPUT" || statusId === "PICKLIST_PICKED" || statusId === "PICKLIST_CHECKED") {
			contextmenu.jqxMenu("disable", "mnuEdit", false);
			contextmenu.jqxMenu("disable", "mnuCancel", false);
			contextmenu.jqxMenu("disable", "mnuApproved", false);
		} 
		
		if (statusId === "PICKLIST_APPROVED") {
			contextmenu.jqxMenu("disable", "mnuCancel", false);
		}
		
		if (statusId === "PICKLIST_CANCELLED") {
			contextmenu.jqxMenu("disable", "mnuDownload", true);
		} else {
			contextmenu.jqxMenu("disable", "mnuDownload", false);
		}
	});
	
	var createDelivery = function(picklistId){
		jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}", function() {
			Loading.show("loadingMacro");
			setTimeout(function(){
				$.ajax({
					type: "POST",
					url: "createDeliveryFromPicklist",
					async: false,
					data: {
						picklistId: picklistId,
					},
					success: function(res){
						if(res._ERROR_MESSAGE_){
							if(res._ERROR_MESSAGE_){
								jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.UpdateError)}");
								return false;
							}
						} else {
							window.location.href = "getListSalesDeliveries";
						}
					},
				});
				Loading.hide("loadingMacro");
			}, 500);
		});
	}
	
	contextMenuChild = $("#contextMenuChild").jqxMenu({ theme: theme, width: 220, autoOpenPopup: false, mode: "popup" });
	contextMenuChild.on("itemclick", function (event) {
		var args = event.args;
		var itemId = $(args).attr("id");
		var rowIndexSelected = contextMenuChild.data("grid").jqxGrid("getSelectedRowindex");
		var picklistBinId = contextMenuChild.data("grid").jqxGrid("getcellvalue", rowIndexSelected, "picklistBinId");
		var partyPickId = contextMenuChild.data("grid").jqxGrid("getcellvalue", rowIndexSelected, "partyPickId");
		var partyCheckId = contextMenuChild.data("grid").jqxGrid("getcellvalue", rowIndexSelected, "partyCheckId");
		switch (itemId) {
		case "binViewDetail":
			location.href = "PicklistDetail?picklistBinId=" + picklistBinId;
			break;
		case "binApprove":
			if (!partyPickId || !partyCheckId){
				jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BLYouNotUpdateEmployeeYet)}");
			} else {
				approveBin(picklistBinId);
			}
			break;
		case "binCancel":
			cancelBin(picklistBinId);
			break;
		case "binEmployee":
			ChangeEmployee.open(contextMenuChild.data("grid"), picklistBinId, contextMenuChild.data("grid").jqxGrid("getcellvalue", rowIndexSelected, "partyPickId"), contextMenuChild.data("grid").jqxGrid("getcellvalue", rowIndexSelected, "partyCheckId"));
			break;
		default:
			break;
		}
	});

	contextMenuChild.on("shown", function () {
		var rowIndexSelected = contextMenuChild.data("grid").jqxGrid("getSelectedRowindex");
		var binStatusId = contextMenuChild.data("grid").jqxGrid("getcellvalue", rowIndexSelected, "binStatusId");
		if (binStatusId === "PICKBIN_INPUT" || binStatusId === "PICKBIN_PICKED" || binStatusId === "PICKBIN_CHECKED") {
			contextMenuChild.jqxMenu("disable", "binCancel", false);
			contextMenuChild.jqxMenu("disable", "binApprove", false);
		} else {
			contextMenuChild.jqxMenu("disable", "binCancel", true);
			contextMenuChild.jqxMenu("disable", "binApprove", true);
		}
		if (binStatusId === "PICKBIN_INPUT" || binStatusId === "PICKBIN_APPROVED" || binStatusId === "PICKBIN_PICKED" || binStatusId === "PICKBIN_CHECKED") {
			contextMenuChild.jqxMenu("disable", "binEmployee", false);
		} else {
			contextMenuChild.jqxMenu("disable", "binEmployee", true);
		}
	});
	
	var approveBin = function (picklistBinId) {
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureApprove)}", 
		[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
			"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
			"callback": function() {bootbox.hideAll();}
		}, 
		{"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
			"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
			"callback": function() {
				Loading.show("loadingMacro");
				setTimeout(function(){
					$.ajax({ 
						url: "changePicklistBinStatus",
						data: {
							picklistBinId: picklistBinId,
							statusId: "PICKBIN_APPROVED",
						},
						type: "POST",
						async: false,
						success: function (res){
							if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
								jOlbUtil.alert.error("${uiLabelMap.HasErrorWhenProcess}");
								return false;
							}
							contextMenuChild.data("grid").jqxGrid("updatebounddata"); 
							mainGrid.jqxGrid("updatebounddata");
						}
					});
					Loading.hide("loadingMacro");
				}, 500);
			}
		}]);
	}
	var cancelBin = function (picklistBinId) {
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureCancel)}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function() {bootbox.hideAll();}
				}, 
				{"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function() {
						Loading.show("loadingMacro");
						setTimeout(function(){
							$.ajax({ 
								url: "changePicklistBinStatus",
								data: {
									picklistBinId: picklistBinId,
									statusId: "PICKBIN_CANCELLED",
								},
								type: "POST",
								async: false,
								success: function (res){
									if (res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_) {
										jOlbUtil.alert.error("${uiLabelMap.HasErrorWhenProcess}");
										return false;
									}
									contextMenuChild.data("grid").jqxGrid("updatebounddata");
								}
							});
							Loading.hide("loadingMacro");
						}, 500);
					}
				}]);
	}
});
</script>