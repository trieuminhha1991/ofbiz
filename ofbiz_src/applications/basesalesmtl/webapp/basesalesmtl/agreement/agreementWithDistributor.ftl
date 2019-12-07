<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'agreementId', type: 'string'},
					{ name: 'agreementCode', type: 'string'},
					{ name: 'partyIdFrom', type: 'string'},
					{ name: 'partyCodeFrom', type: 'string'},
					{ name: 'partyIdTo', type: 'string'},
					{ name: 'partyFromFullName', type: 'string'},
					{ name: 'agreementTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'}]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', pinned: true, groupable: false, filterable: false, draggable: false, resizable: false, width: 50,
						cellsrenderer: function (row, column, value) {
							return '<div style=margin:4px;>' + (row + 1) + '</div>';
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsAgreementId)}', datafield: 'agreementCode', width: 120,
						cellsrenderer: function (row, column, value) {
							var data = $('#listAgreementDis').jqxGrid('getrowdata', row);
							return '<div style=margin:4px;><a href=AgreementDetail?sub=AgreementWithDistributor&agreementId=' + data.agreementId + '>' + value + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DADistributorId)}', datafield: 'partyCodeFrom', width: 150},
					{ text: '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield: 'partyFromFullName', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: 150, editable: true,
						cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: 200, filtertype: 'range', cellsformat: 'dd/MM/yyyy'}"/>

<@jqGrid id="listAgreementDis" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
	url="jqxGeneralServicer?sname=JQGetListAgreementDistributor" editable="false"
	contextMenuId="contextMenu" mouseRightMenu="true"/>

<div id='contextMenu' style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_APPROVE", "")>
			<li id='approveAgreement'><i class="fa-check"></i>&nbsp;&nbsp;${uiLabelMap.DmsApprove}</li>
		</#if>
		<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_NEW", "")>
			<li id='uploadFileScan'><i class="fa-cloud-upload"></i>&nbsp;&nbsp;${uiLabelMap.UploadFileScan}</li>
		</#if>
		<li id='viewFileScan'><i class="fa-file-image-o"></i>&nbsp;&nbsp;${uiLabelMap.ViewFileScan}</li>
		<li id='createOrder'><i class="fa-cart-plus"></i>&nbsp;&nbsp;${uiLabelMap.DACreateOrder}</li>
	</ul>
</div>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/uploadFileScan.ftl"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />
<script>
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};

	$(document).ready(function() {
		var mainGrid = $("#listAgreementDis");
		$("#contextMenu").jqxMenu({ theme: theme, width: 170, autoOpenPopup: false, mode: "popup"});
		$("#contextMenu").on("shown", function () {
			var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
			var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
			var statusId = rowData.statusId;
			if (statusId == "AGREEMENT_CREATED" || statusId == "AGREEMENT_MODIFY") {
				$("#contextMenu").jqxMenu("disable", "approveAgreement", false);
			}else {
				$("#contextMenu").jqxMenu("disable", "approveAgreement", true);
			}
		});
		$("#contextMenu").on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "createOrder":
				var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				window.open("newSalesOrder?partyId=" + rowData.partyIdFrom + "&agreementId=" + rowData.agreementId, "_blank");
				break;
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
			default:
				break;
			}
		});
	});
</script>