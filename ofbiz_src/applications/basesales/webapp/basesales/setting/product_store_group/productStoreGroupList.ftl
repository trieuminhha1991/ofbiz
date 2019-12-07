
<#--
<#assign listProductStoreGroupType = delegator.findByAnd("ProductStoreGroupType", null, null, false)!/>
<script type="text/javascript">
	var productStoreGroupTypeList = [
	<#if listProductStoreGroupType?exists>
	    <#list listProductStoreGroupType as item>
	    {   id: "${item.productStoreGroupTypeId}",
	    	description: "${StringUtil.wrapString(item.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
</script>
-->

<#assign dataField = "[
				{name: 'productStoreGroupId', type: 'string'}, 
				{name: 'productStoreGroupName', type: 'string'},
				{name: 'description', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSPSProductStoreGroupId)}', dataField: 'productStoreGroupId', width: 160, editable: false,
					cellsrenderer: function(row, colum, value) {
				    	return \"<span><a href='viewProductStoreGroup?productStoreGroupId=\" + value + \"'>\" + value + \"</a></span>\";
				    }
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSPSProductStoreGroupName)}', dataField: 'productStoreGroupName', width: 260},
				{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', minwidth: 260},
			"/>

<#assign tmpCreate = false/>
<#assign tmpUpdate = false/>
<#if hasOlbPermission("MODULE", "SALES_PRODUCTSTOREGRP_NEW", "")><#assign tmpCreate = true/></#if>
<#if hasOlbPermission("MODULE", "SALES_PRODUCTSTOREGRP_EDIT", "")><#assign tmpUpdate = true/></#if>

<#assign contextMenuItemId = "ctxmnustoregrplst"/>
<@jqGrid id="jqxgridStoreGroup" clearfilteringbutton="true" editable="false" alternativeAddPopup="popupProductStoreGroupNew" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" addrefresh="true" 
		url="jqxGeneralServicer?sname=JQGetListProductStoreGroup" mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" 
		addrow="${tmpCreate?string}" createUrl="jqxGeneralServicer?sname=createProductStoreGroupOlb&jqaction=C" addColumns="productStoreGroupId;productStoreGroupName;description"
	/>

<div id="contextMenu_${contextMenuItemId}" style="display:none">
	<ul>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpUpdate><li id="${contextMenuItemId}_edit"><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li></#if>
	</ul>
</div>
<#include "productStoreGroupNewPopup.ftl" />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbProducStoreList.init();
	});
	var OlbProducStoreList = (function(){
		var contextMenuItemId = "${contextMenuItemId}";
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create("#contextMenu_" + contextMenuItemId);
		};
		var initEvent = function(){
			$("#contextMenu_" + contextMenuItemId).on('itemclick', function (event) {
				var args = event.args;
				// var tmpKey = $.trim($(args).text());
				var tmpId = $(args).attr('id');
				var idGrid = "#jqxgridStoreGroup";
				
		        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
		        var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
		        
		        switch(tmpId) {
		    		case contextMenuItemId + "_refesh": { 
		    			$(idGrid).jqxGrid('updatebounddata');
		    			break;
		    		};
		    		<#if tmpUpdate>
		    		case contextMenuItemId + "_edit": { 
		    			if (rowData) {
							//OlbProductStoreEdit.openWindow(rowData);
						}
						break;
		    		};
		    		</#if>
		    		default: break;
		    	}
			});
		};
		return {
			init: init
		};
	}());
</script>
