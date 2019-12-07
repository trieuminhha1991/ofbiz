<#assign dataField = "[
				{name: 'partyId', type: 'string'}, 
				{name: 'partyCode', type: 'string'},
				{name: 'groupName', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSGroupId)}', dataField: 'partyCode', width: '20%', editable: false, 
					cellsrenderer: function(row, colum, value) {
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						return \"<span><a href='listPartyMember?groupId=\" + data.partyId + \"'>\" + value + \"</a></span>\";
					}
				}, 
				{text: '${StringUtil.wrapString(uiLabelMap.BSGroupName)}', dataField: 'groupName'},
			"/>

<#assign tmpCreate = false/>
<#assign tmpUpdate = false/>
<#if hasOlbPermission("MODULE", "SALES_CUSGROUPST_NEW", "")><#assign tmpCreate = true/></#if>
<#if hasOlbPermission("MODULE", "SALES_CUSGROUPST_EDIT", "")><#assign tmpUpdate = true/></#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindowCustomerGroupCreateNew" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQListCustomerGroup" mouseRightMenu="true" contextMenuId="contextMenu" 
		addrow="${tmpCreate?string}" createUrl="jqxGeneralServicer?sname=createCustomerGroup&jqaction=C" addColumns="partyCode;groupName"/>

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<div id="contextMenu" style="display:none">
	<ul>
		<li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
		<li><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpUpdate><li><i class="fa fa-plus"></i>${StringUtil.wrapString(uiLabelMap.BSAddMember)}</li></#if>
	</ul>
</div>

<#include "customerGroupNewPopup.ftl"/>
<#include "customerGroupAddItemPopup.ftl"/>

<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbCustomerGroupList.init();
	});
	var OlbCustomerGroupList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu"));
		};
		var initEvent = function(){
			$("#contextMenu").on('itemclick', function (event) {
				var args = event.args;
		        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   	var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	    	   	
		        var tmpKey = $.trim($(args).text());
		        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}") {
		    	   	if (rowData) {
						var partyId = rowData.partyId;
						var url = 'listPartyMember?groupId=' + partyId;
						var win = window.open(url, '_blank');
						win.focus();
					}
		        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}") {
		        	if (rowData) {
						var partyId = rowData.partyId;
						var url = 'listPartyMember?groupId=' + partyId;
						var win = window.open(url, '_self');
						win.focus();
					}
		        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
		        	$("#jqxgrid").jqxGrid('updatebounddata');
		        }<#if tmpUpdate> else if (tmpKey == '${StringUtil.wrapString(uiLabelMap.BSAddMember)}') {
		    	   	if (rowData) {
		    	   		$("#wn_additem_groupId").val(rowData.partyId);
		    	   		$("#wn_additem_groupCode").val(rowData.partyCode);
		    	   		$("#alterpopupWindowCustomerGroupAddItem").jqxWindow("open");
		    	   	}
		    	}</#if>
			});
		};
		return {
			init: init
		};
	}());
</script>