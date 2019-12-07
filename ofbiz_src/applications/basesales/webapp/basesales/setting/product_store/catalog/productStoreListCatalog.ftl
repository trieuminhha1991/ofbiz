<script type="text/javascript">
	var cellClassProdStoreCatalog = function (row, columnfield, value) {
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
				{name: 'prodCatalogId', type: 'string'}, 
				{name: 'catalogName', type: 'string'}, 
				{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
				{name: 'thruDate', type: 'date', other: 'Timestamp'},
				{name: 'sequenceNum', type: 'number'}
			]"/>
<#if hasOlbPermission("MODULE", "CATALOGS_VIEW", "")>
	<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSProdCatalogId)}', dataField: 'prodCatalogId', width: 160, editable: false, cellClassName: cellClassProdStoreCatalog,
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='listCatalog'>\" + value + \"</a></span>\";
					}
				}, "/>
<#else>
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSProdCatalogId)}', dataField: 'prodCatalogId', width: 160, editable: false, cellClassName: cellClassProdStoreCatalog},"/>
</#if>
<#assign columnlist = columnlist + "
				{text: '${StringUtil.wrapString(uiLabelMap.BSCatalogName)}', dataField: 'catalogName', minWidth: 100, editable: false, cellClassName: cellClassProdStoreCatalog}, 
				{text: '${StringUtil.wrapString(uiLabelMap.ProductSequenceNum)}', dataField: 'sequenceNum', width: 100, cellsalign: 'right', filtertype: 'number', columntype: 'numberinput', cellClassName: cellClassProdStoreCatalog,
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
				{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', dataField: 'fromDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false, cellClassName: cellClassProdStoreCatalog,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', dataField: 'thruDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: false, cellClassName: cellClassProdStoreCatalog,
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', dataField: 'status', width: 140, editable: false, cellClassName: cellClassProdStoreCatalog,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						if (data != null && data.thruDate != null && data.thruDate != undefined) {
							var thruDate = new Date(data.thruDate);
							var nowDate = new Date('${nowTimestamp}');
							if (thruDate < nowDate) {
								return '<span title=\"${uiLabelMap.BSExpired}\">${uiLabelMap.BSExpired}</span>';
							}
						}
						return '<span></span>';
					}, 
				}, 
			"/>

<#assign contextMenuItemId = "ctxmnupscatalst">
<#assign permitCreate = false>
<#assign permitUpdate = false>
<#assign permitDelete = false>
<#if hasOlbPermission("MODULE", "SALES_STORECATALOG_NEW", "")><#assign permitCreate = true></#if>
<#if hasOlbPermission("MODULE", "SALES_STORECATALOG_EDIT", "")><#assign permitUpdate = true></#if>
<#if hasOlbPermission("MODULE", "SALES_STORECATALOG_DELETE", "")><#assign permitDelete = true></#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindowNewProdCatalog" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addType="popup" addrefresh="true" jqGridMinimumLibEnable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductStoreCatalog&productStoreId=${productStore.productStoreId?if_exists}" 
		addrow="${permitCreate?string}" createUrl="jqxGeneralServicer?jqaction=C&sname=createProductStoreCatalogOlb" addColumns="productStoreId;prodCatalogId;sequenceNum(java.lang.Long);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" 
		editable="${permitUpdate?string}" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateProductStoreCatalog" editColumns="productStoreId;prodCatalogId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);sequenceNum(java.lang.Long)"
		deleterow="${permitDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteProductStoreCatalogOlb" deleteColumn="productStoreId;prodCatalogId;fromDate(java.sql.Timestamp)" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" 
	/>

<div id="contextMenu_${contextMenuItemId}" style="display:none">
	<ul>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <li id="${contextMenuItemId}_delete"><i class="fa-trash-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
	</ul>
</div>

<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	var contextMenuItemId = "${contextMenuItemId}";
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}';
</script>
<script type="text/javascript" src="/salesresources/js/setting/catalog/productStoreListCatalog.js"></script>

<#include "productStoreNewCatalog.ftl">
<#--<#include "productStoreUpdateCatalog.ftl">-->
