<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField = "[{ name: 'partyId', type: 'string' },
					{ name: 'partyCode', type: 'string' },
					{ name: 'partyTypeId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'groupName', type: 'string' }]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.POBrandId)}', dataField: 'partyCode', width: 250,
						validation: function (cell, value) {
							if (value) {
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									var check = DataAccess.getData({
										url: 'checkPartyCode',
										data: {partyCode: value, partyId: $('#jqxgrid').jqxGrid('getcellvalue', cell.row, 'partyId')},
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
					{ text: '${StringUtil.wrapString(uiLabelMap.POBrandName)}', dataField: 'groupName', columntype: 'textbox',
						cellbeginedit: function (row, datafield, columntype, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							if (data.partyId == 'BRGR100006') {
								return false;
							}
							return true;
						}, validation: function (cell, value) {
							if (value) {
								return true;
							}
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: 200, editable: false,
						cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
							return '<span>' + value + '</span>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapStatusItem[value];
								}
							});
						}
					}"/>
				
<#if hasOlbPermission("MODULE", "CONFIG_PRODBRAND_NEW", "CREATE")>
	<#assign addrow = "true" />
<#else>
	<#assign addrow = "false" />
</#if>
<#if hasOlbPermission("MODULE", "CONFIG_PRODBRAND_EDIT", "UPDATE")>
	<#assign editable = "true" />
<#else>
	<#assign editable = "false" />
</#if>

<@jqGrid id="jqxgrid" addrow=addrow clearfilteringbutton="true" editable=editable alternativeAddPopup="jqxwindowAddBrand"
	columnlist=columnlist dataField=dataField
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=JQGetListProductBrands"
	createUrl="jqxGeneralServicer?sname=createProductBrand&jqaction=C" addColumns="partyCode;partyTypeId;statusId;groupName"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyGroupIncludePartyCode" editColumns="partyId;partyCode;groupName"
	contextMenuId="contextMenu" mouseRightMenu="true"/>
		
<#include "addProductBrand.ftl"/>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="approveBrand"><i class="fa-check"></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}</li>
	</ul>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<script>
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: "${item.statusId?if_exists}", 
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];

$(document).ready(function() {
	$("#contextMenu").jqxMenu({ theme: theme, width: 170, autoOpenPopup: false, mode: "popup" });
	$("#contextMenu").on("shown", function () {
		var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
		var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
		if (rowData.partyId == "BRGR100006") {
			$("#contextMenu").jqxMenu("disable", "approveBrand", true);
			return;
		} else {
			$("#contextMenu").jqxMenu("disable", "approveBrand", false);
		}
		var statusId = rowData.statusId;
		if (statusId == "PARTY_DISABLED") {
			$("#approveBrand").html("<i class='fa-check'></i>&nbsp;&nbsp;${uiLabelMap.DmsActive}");
		} else {
			$("#approveBrand").html("<i class='fa-check'></i>&nbsp;&nbsp;${uiLabelMap.DmsDeactivate}");
		}
	});
	
	$("#contextMenu").on("itemclick", function (event) {
		var args = event.args;
		var itemId = $(args).attr("id");
		switch (itemId) {
			case "approveBrand":
				var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
				var statusId = rowData.statusId;
				if (statusId == "PARTY_DISABLED") {
					statusId = "PARTY_ENABLED";
				} else {
					statusId = "PARTY_DISABLED";
				}
				var result = DataAccess.execute({
					url: "setPartyStatus",
					data: {
						partyId: rowData.partyId,
						statusId: statusId}
					});
				if (result) {
					$("#jqxgrid").jqxGrid("updatebounddata");
				}
				break;
			default:
				break;
		}
	});
	
	<#if !hasOlbPermission("MODULE", "CONFIG_PRODBRAND_DELETE", "DELETE")>
		$("#contextMenu").jqxMenu({disabled: false}); 
	</#if>
});
</script>