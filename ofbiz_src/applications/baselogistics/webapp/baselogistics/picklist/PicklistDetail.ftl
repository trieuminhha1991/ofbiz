<script>
	var checkAcc = false;
	<#if hasOlbPermission('MODULE', 'ACC_PICKING_LIST', 'UPDATE')>
		checkAcc = true;
	</#if>
</script>									
<#assign dataField = "[{ name: 'picklistId', type: 'string' },
					{ name: 'picklistBinId', type: 'string' },
					{ name: 'orderId', type: 'string' },
					{ name: 'orderItemSeqId', type: 'string' },
					{ name: 'facilityId', type: 'string' },
					{ name: 'facilityCode', type: 'string' },
					{ name: 'itemStatusId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'requireAmount', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'shipGroupSeqId', type: 'string' },
					]"/>

<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoDotSoan)}', dataField: 'picklistId', width: 100, editable: false, cellClassName: cellClass },
						{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoPhieuSoan)}', dataField: 'picklistBinId', width: 110, editable: false, cellClassName: cellClass },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', dataField: 'orderId', width: 120, editable: false, cellClassName: cellClass },
						{ text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', dataField: 'facilityCode', width: 120, editable: false, cellClassName: cellClass },
						{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'itemStatusId', filtertype: 'checkedlist', width: 120, editable: false, cellClassName: cellClass,
							cellsrenderer: function(row, colum, value) {
								value = value?value=mapStatusItem[value]:value;
								return '<div style=margin:4px;>' + value + '</div>';
							},
							createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' });
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 120, editable: false, cellClassName: cellClass },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minWidth: 200, editable: false, cellClassName: cellClass },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', columntype: 'numberinput', filtertype: 'number', width: 120, filterable: false, cellClassName: cellClass,
							cellsrenderer: function(row, column, value, a, b, data) {
								return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
							},
							cellbeginedit: function (row, datafield, columntype, value) {
								var _itemStatusId = mainGrid.jqxGrid('getcellvalue', row, 'itemStatusId');
								if (_itemStatusId == 'PICKITEM_PENDING' || _itemStatusId == 'PICKITEM_APPROVED') {
									if (_itemStatusId == 'PICKITEM_APPROVED') { 
										if (checkAcc == true) {
											return true;
										} else {
											return false;
										}
									}
									return true;
								} else {
									return false;
								}
							}, 
							initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
						        editor.jqxNumberInput({ decimalDigits: 0});
						        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						        var requireAmount = data.requireAmount;
							    var reqAmount = false; 
							    if (requireAmount && requireAmount == 'Y') reqAmount = true; 
	                            if (reqAmount) {
	                        		editor.jqxNumberInput({decimalDigits: 2, disabled: false});
	                        	} else {
	                        		editor.jqxNumberInput({decimalDigits: 0, disabled: false});
	                        	}
	                        	if (cellvalue){
	                        		editor.jqxNumberInput('val', cellvalue);
                        		}
						    },
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', dataField: 'quantityUomId', filtertype: 'checkedlist', width: 120, editable: false, cellClassName: cellClass,
							cellsrenderer: function(row, colum, value) {
								value = value?value=mapUom[value]:value;
								return '<div style=margin:4px;>' + value + '</div>';
							},
							createfilterwidget: function (column, htmlElement, editor) {
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listUom, displayMember: 'description', valueMember: 'uomId' });
							}
						}
						"/>

<#include "PicklistDetail_rowDetail.ftl"/>
<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="true" alternativeAddPopup="addRival"
	columnlist=columnlist dataField=dataField contextMenuId="contextmenu" mouseRightMenu="true"
	showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" 
	initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail
	url="jqxGeneralServicer?sname=JQGetListPicklistItem&picklistBinId=${(parameters.picklistBinId)?if_exists}"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePicklistItemSum" editColumns="picklistId;picklistBinId;orderId;orderItemSeqId;facilityId;productId;quantity(java.math.BigDecimal);itemStatusId"/>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PICKITEM_STATUS"), null, null, null, true) />
<#assign listUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, true) />
<script>
	const mainGrid = $("#jqxgrid");
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: "${item.statusId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
		"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var listUom = [<#if listUom?exists><#list listUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapUom = {<#if listUom?exists><#list listUom as item>
	"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	$(document).ready(function () {
		$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.DmsPicklistDetail)}");
	});

	var cellClass = function (row, columnfield, value) {
		var data = mainGrid.jqxGrid("getrowdata", row);
		var _itemStatusId = mainGrid.jqxGrid('getcellvalue', row, 'itemStatusId');
		if (typeof(data) != "undefined") {
			if ("PICKITEM_CANCELLED" == _itemStatusId) {
				return "background-cancel";
			} else if ("PICKITEM_PENDING" == _itemStatusId) {
				return "background-important-nd";
			} else if ("PICKITEM_APPROVED" == _itemStatusId) {
				return "background-prepare";
			}
		}
	}
</script>