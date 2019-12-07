<#--
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/catalog.js"></script>
<style>
	.text-right {margin-top: 4px}
</style>
<script>
	<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
		var linkCategories = "ProductCategories";
	<#else>
		var linkCategories = "ListProductCategories";
	</#if>
</script>
-->
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
				var tabsDiv = $($(parentElement).children()[0]);
				if (tabsDiv != null) {
					var loadingStr = '<div id=\"info_loader_' + index + '\" class=\"jqx-rc-all jqx-rc-all-olbius loader-page-common-custom\">';
					loadingStr += '<div class=\"jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius\">';
					loadingStr += '<div><div class=\"jqx-grid-load\"></div><span>${uiLabelMap.BSLoading}...</span></div></div></div>';
					var notescontainer = $(loadingStr);
					$(tabsDiv).append(notescontainer);
					
					var prodCatalogId = datarecord.prodCatalogId;
					
					var loadPage = function (url, tabClass, data, index) {
						$.ajax({
							type: 'POST',
							url: url,
							data: data,
							beforeSend: function () {
								$(\"#info_loader_\" + index).show();
							}, 
							success: function(data){
								var tabActive = tabsDiv.find('.' + tabClass);
								var container2 = $('<div style=\"margin: 5px;\">' + data + '</div>');
								container2.appendTo($(tabActive));
							},
							complete: function() {
								$(\"#info_loader_\" + index).hide();
							}
						});
					}
					loadPage('getDetailCatalogAjax', 'contentTab1', {'prodCatalogId' : prodCatalogId}, index);
				}
			 }"/>
<#assign rowdetailstemplateAdvance = "<div class='contentTab1'></div>"/>

<#assign dataField="[{ name: 'prodCatalogId', type: 'string' },
					{ name: 'catalogName', type: 'string' },
					{ name: 'useQuickAdd', type: 'string' }]"/>

<#assign columnlist="
				{ text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (row + 1) + '</div>';
					}
				},
				{ text: '${uiLabelMap.DmsProdCatalogId}', datafield: 'prodCatalogId', width: 250, editable: false},
				{ text: '${uiLabelMap.DmsProdCatalogName}', datafield: 'catalogName',
					validation: function (cell, value) {
						if (!value) {
							return {result: false, message: '${uiLabelMap.BPOPleaseSelectAllInfo}'};
						}
						return true;
					}
				}"/>

<#if hasOlbPermission("MODULE", "CATALOGS_NEW", "")>
	<#assign addrow="true"/>
<#else>
	<#assign addrow="false"/>
</#if>
<#if hasOlbPermission("MODULE", "CATALOGS_EDIT", "")>
	<#assign editable="true"/>
<#else>
	<#assign editable="false"/>
</#if>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow=addrow deleterow="false" alternativeAddPopup="alterpopupWindow" editable=editable
		url="jqxGeneralServicer?sname=JQGetListProdCatalog"
		updateUrl="jqxGeneralServicer?sname=updateProdCatalog&jqaction=U" editColumns="prodCatalogId;catalogName"
		createUrl="jqxGeneralServicer?sname=createProdCatalog&jqaction=C" addColumns="prodCatalogId;catalogName"
		contextMenuId="" mouseRightMenu="false" 
		initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailstemplateAdvance=rowdetailstemplateAdvance rowdetailsheight="270"/>

<#include "popup/addCatalog.ftl"/>
<#--
<#include "popup/categories.ftl"/>
<#include "popup/stores.ftl"/>
<div id="contextMenu" class="hide">
	<ul>
		<li id="viewListCategory"><i class="fa-search"></i>&nbsp;&nbsp;${uiLabelMap.DmsViewListCategory}</li>
		<li id="viewProductStoreList"><i class="fa-search"></i>&nbsp;&nbsp;${uiLabelMap.DmsViewProductStoreList}</li>
	</ul>
</div>
-->

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

<#include "productCatalogAddRootCategoryPopup.ftl"/>
<#include "productCatalogAddProductStorePopup.ftl"/>
<@jqTreeGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	var changeRootCategory = function(prodCatalogId){
		if (OlbCore.isNotEmpty(prodCatalogId)) {
			$("#we_crc_prodCatalogId").jqxInput("val", prodCatalogId);
			var isRunned = false;
			var categoryGridObj = $("#jqxListCategory" + prodCatalogId);
			if (categoryGridObj.length > 0) {
				var data = $(categoryGridObj).jqxGrid("getboundrows");
				if (data) {
					for (var i = 0; i < data.length; i++) {
						var dataItem = data[i];
						if (dataItem != window) {
							if (dataItem.prodCatalogCategoryTypeId == "PCCT_BROWSE_ROOT") {
								$("#we_crc_productCategoryIdFrom").jqxInput("val", dataItem.productCategoryId);
								isRunned = true;
								break;
							}
						}
					}
				}
			}
			if (!isRunned) $("#we_crc_productCategoryIdFrom").jqxInput("val", "");
			$("#alterpopupWindowChangeRootCategory").jqxWindow("open");
		}
	};
	var addProductStore = function(prodCatalogId){
		if (OlbCore.isNotEmpty(prodCatalogId)) {
			$("#we_aps_prodCatalogId").jqxInput("val", prodCatalogId);
			$("#alterpopupWindowAddProductStore").jqxWindow("open");
		}
	};
</script>