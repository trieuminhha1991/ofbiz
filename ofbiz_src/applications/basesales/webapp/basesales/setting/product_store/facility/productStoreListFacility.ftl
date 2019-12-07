<script type="text/javascript">
	var cellClassProdStoreFacility = function (row, columnfield, value) {
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
			if (data.thruDate != null && data.thruDate < now) {
				return "background-cancel";
			} else if (data.fromDate >= now) {
				return "background-prepare";
			}
 		}
    }
</script>

<#assign dataField = "[
			{name: 'productStoreId', type: 'string'},
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
			{name: 'sequenceNum', type: 'number'},
			{name: 'fromDate', type: 'date', other: 'Timestamp'},
			{name: 'thruDate', type: 'date', other: 'Timestamp'}
		]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.FacilityId)}', dataField: 'facilityCode', width: 250, editable: false, cellClassName: cellClassProdStoreFacility},
			{text: '${StringUtil.wrapString(uiLabelMap.FacilityName)}', dataField: 'facilityName', editable: false, cellClassName: cellClassProdStoreFacility},
			{text: '${StringUtil.wrapString(uiLabelMap.ProductSequenceNum)}', dataField: 'sequenceNum', cellsalign: 'right', filtertype: 'number', columntype: 'numberinput', width: 100, cellClassName: cellClassProdStoreFacility,
				validation: function (cell, value) {
					if (value >= 0) {
						return true;
					}
					return { result: false, message: '${uiLabelMap.DmsQuantityNotValid}' };
				},
				cellbeginedit: function (row, datafield, columntype, value) {
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					if (data != null && data.thruDate != null && data.thruDate != undefined) {
						
						var thruDate = new Date(data.thruDate).getTime();
						var nowDate = new Date('${nowTimestamp}').getTime();
						if (thruDate < nowDate) {
							return false;
						}
					}
					return true;
	            }
			},
			{ text: '${uiLabelMap.DmsFromDate}', datafield: 'fromDate', width: 200, filtertype: 'range', cellsformat:'dd/MM/yyyy - HH:mm:ss', editable: false, cellClassName: cellClassProdStoreFacility},
			{ text: '${uiLabelMap.DmsThruDate}', datafield: 'thruDate', width: 200, filtertype: 'range', cellsformat:'dd/MM/yyyy - HH:mm:ss', editable: false, cellClassName: cellClassProdStoreFacility},
			{text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', dataField: 'status', width: 100, editable: false, sortable: false, filterable: false, cellClassName: cellClassProdStoreFacility,
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					if (data != null && data.thruDate != null && data.thruDate != undefined) {
						var thruDate = new Date(data.thruDate).getTime();
						var nowDate = new Date('${nowTimestamp}').getTime();
						if (thruDate < nowDate) {
							return '<span title=\"${uiLabelMap.BSExpired}\">${uiLabelMap.BSExpired}</span>';
						}
					}
					return '<span></span>';
				}
			}
		"/>

<#assign contextMenuItemId = "ctxmnupsfaclst">
<#assign permitCreate = false>
<#assign permitUpdate = false>
<#assign permitDelete = false>
<#if hasOlbPermission("MODULE", "SALES_STOREFACILITY_NEW", "")><#assign permitCreate = true></#if>
<#if hasOlbPermission("MODULE", "SALES_STOREFACILITY_EDIT", "")><#assign permitUpdate = true></#if>
<#if hasOlbPermission("MODULE", "SALES_STOREFACILITY_DELETE", "")><#assign permitDelete = true></#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" alternativeAddPopup="alterpopupWindowNewFacility"
		columnlist=columnlist dataField=dataField 
		showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListProductStoreFacility&productStoreId=${productStoreId?if_exists}"
		addrow="${permitCreate?string}" createUrl="jqxGeneralServicer?sname=createProductStoreFacilityOlb&jqaction=C" addColumns="productStoreId;facilityId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);sequenceNum(java.lang.Long)"
		editable="${permitUpdate?string}" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateProductStoreFacility" editColumns="productStoreId;facilityId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);sequenceNum(java.lang.Long)"
		deleterow="${permitDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteProductStoreFacilityOlb" deleteColumn="productStoreId;facilityId;fromDate(java.sql.Timestamp)" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}"/>

<#include "productStoreNewFacility.ftl"/>

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if permitDelete><li id="${contextMenuItemId}_delete"><i class="fa-trash-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li></#if>
	</ul>
</div>

<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbProductStoreListFacility.init();
	});
	
	var OlbProductStoreListFacility = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemId}"));
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuItemId}").on('itemclick', function (event) {
				var args = event.args;
				var tmpId = $(args).attr('id');
				var idGrid = "#jqxgrid";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		    		case "${contextMenuItemId}_delete": { 
	    				$("#deleterowbuttonjqxgrid").click();
						break;
					};
		    		case "${contextMenuItemId}_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		default: break;
		    	}
		    });
		    <#if permitDelete>
		    $("#contextMenu_${contextMenuItemId}").on('shown', function () {
				var rowIndexSelected = $("#jqxgrid").jqxGrid('getSelectedRowindex');
				var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowIndexSelected);
				if (rowData != null && rowData.thruDate != null && rowData.thruDate != undefined) {
					var thruDate = new Date(rowData.thruDate).getTime();
					var nowDate = new Date('${nowTimestamp}').getTime();
					if (thruDate < nowDate) {
						$("#contextMenu_${contextMenuItemId}").jqxMenu('disable', '${contextMenuItemId}_delete', true);
					} else {
						$("#contextMenu_${contextMenuItemId}").jqxMenu('disable', '${contextMenuItemId}_delete', false);
					}
				} else {
					$("#contextMenu_${contextMenuItemId}").jqxMenu('disable', '${contextMenuItemId}_delete', false);
				}
			});
			</#if>
		};
		return {
			init: init
		};
	}());
</script>