<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/catalog.js"></script>
<style>
.text-right{
	margin-top: 4px;
}
</style>
<script>
<#if security.hasEntityPermission('ECOMMERCE', '_ADMIN', session)>
var linkCategories = 'ProductCategories';
<#else>
var linkCategories = 'ListProductCategories';
</#if>
</script>
<#assign dataField="[{ name: 'prodCatalogId', type: 'string'},
				   { name: 'catalogName', type: 'string'},
				   { name: 'useQuickAdd', type: 'string'}]"/>

<#assign columnlist="{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.DmsProdCatalogId}', datafield: 'prodCatalogId', width: 250, editable: false},
					{ text: '${uiLabelMap.DmsProdCatalogName}', datafield: 'catalogName',
						validation: function (cell, value) {
				            if (!value) {
				            	return { result: false, message: '${uiLabelMap.BPOPleaseSelectAllInfo}' };
				            }
				            return true;
				    	}
					}"/>

<#if security.hasEntityPermission("CATALOG", "_CREATE", session)>
	<#assign addrow="true"/>
	<#else>
	<#assign addrow="false"/>
</#if>
<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
	<#assign editable="true"/>
	<#else>
	<#assign editable="false"/>
</#if>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
					showtoolbar="true" addrow=addrow deleterow="false" alternativeAddPopup="alterpopupWindow" editable=editable
					url="jqxGeneralServicer?sname=JQGetListProdCatalog"
					updateUrl="jqxGeneralServicer?sname=updateProdCatalog&jqaction=U" editColumns="prodCatalogId;catalogName"
					createUrl="jqxGeneralServicer?sname=createProdCatalog&jqaction=C" addColumns="prodCatalogId;catalogName"
					contextMenuId="contextMenu" mouseRightMenu="true"/>

<#include "popup/addCatalog.ftl"/>
<#include "popup/categories.ftl"/>
<#include "popup/stores.ftl"/>

<div id='contextMenu' class='hide'>
	<ul>
		<li id='viewListCategory'><i class="fa-search"></i>&nbsp;&nbsp;${uiLabelMap.DmsViewListCategory}</li>
		<li id='viewProductStoreList'><i class="fa-search"></i>&nbsp;&nbsp;${uiLabelMap.DmsViewProductStoreList}</li>
	</ul>
</div>
