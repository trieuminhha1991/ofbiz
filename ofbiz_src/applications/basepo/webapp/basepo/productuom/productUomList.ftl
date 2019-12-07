<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasValidator=true/>
<#assign dataField = "[
			{name: 'uomId', type: 'string'},
			{name: 'uomTypeId', type: 'string'},
			{name: 'abbreviation', type: 'string'},
			{name: 'description', type: 'string'}]"/>

<#assign columnlist = "
			{text: '${uiLabelMap.BSUomId}', dataField: 'uomId', width: '20%', editable: false},
			{text: '${uiLabelMap.BSAbbreviation}', dataField: 'abbreviation', width: '20%',
				validation: function(cell, value) {
					if (value) {
						return true;
					}
					return { result : false, message : '${uiLabelMap.DmsFieldRequired}' };
				}
			},
			{text: '${uiLabelMap.BSDescription}', dataField: 'description',
				validation: function(cell, value) {
					if (value) {
						return true;
					}
					return { result : false, message : '${uiLabelMap.DmsFieldRequired}' };
				}
			}"/>

<#assign jqxGridId = "jqxGridProdPackingUom">
<#assign contextMenuItemId = "ctxmnuproduomlst">

<#assign addrow = "false" />
<#assign editable = "false" />
<#assign deleterow = "false" />
<#if hasOlbPermission("MODULE", "CONFIG_PRODPACK_NEW", "CREATE")>
	<#assign addrow = "true" />	
</#if>
<#if hasOlbPermission("MODULE", "CONFIG_PRODPACK_EDIT", "UPDATE")>
	<#assign editable = "true" />
</#if>

<@jqGrid id="${jqxGridId}" url="jqxGeneralServicer?sname=JQListProductUom" columnlist=columnlist dataField=dataField
		viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true"
		addrow=addrow deleterow=deleterow editable=editable editmode="dblclick" addType="popup" alternativeAddPopup="alterPopupProdPackUomNew"
		editrefresh= "true" 
		createUrl="jqxGeneralServicer?sname=createProductUom&jqaction=C" addColumns="uomId;abbreviation;description"
		updateUrl="jqxGeneralServicer?sname=updateProductUom&jqaction=U" editColumns="uomId;abbreviation;description" 
		mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}" />

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if addrow?exists && addrow == "true"><li id="${contextMenuItemId}_createnew"><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSCreateNew)}</li></#if>
		<#if editable?exists && editable == "true"><li id="${contextMenuItemId}_edit"><i class="fa fa-pencil-square-o"></i>${StringUtil.wrapString(uiLabelMap.BSEditSelectedRow)}</li></#if>
		
	</ul>
</div>

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
<#assign addInnerRow = true>
<#include "productUomNewPopup.ftl"/>
<#include "productUomEditPopup.ftl"/>

<script type="text/javascript">
	var uomIdEditting;
	$(function(){
		OlbPageProdPackingUomList.init();
	});
	var OlbPageProdPackingUomList = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuItemId}"));
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuItemId}").on('itemclick', function (event) {
				var args = event.args;
				var tmpId = $(args).attr('id');
				var idGrid = "#${jqxGridId}";
				var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
				var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);
				switch(tmpId) {
					case "${contextMenuItemId}_refesh": { 
						$(idGrid).jqxGrid('updatebounddata');
						break;
					};
					
					case "${contextMenuItemId}_createnew": { 
						openJqxWindow($("#alterPopupProdPackUomNew"));
						break;
					};
					case "${contextMenuItemId}_edit": { 
						$("#wn_ppu_uomId_edit").val(rowData.uomId);
						$("#wn_ppu_uomId_edit").prop('disabled', true);
						$("#wn_ppu_abbreviation_edit").val(rowData.abbreviation);
						$("#wn_ppu_description_edit").val(rowData.description);
						openJqxWindow($("#alterPopupProdPackUomEdit"));
						break;
					};
					default: break;
				}
			});
		};
		
		function openJqxWindow(jqxWindowDiv){
			var wtmp = window;
			var tmpwidth = jqxWindowDiv.jqxWindow('width');
			jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			jqxWindowDiv.jqxWindow('open');
		};
		return {
			init: init
		};
	}());
</script>