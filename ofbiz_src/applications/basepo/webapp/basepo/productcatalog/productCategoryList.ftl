<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollview.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/crmresources/js/ResizePageContent.js"></script>

<#--<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/listCategories.js"></script>-->

<style>
	.text-right {
		margin-top: 4px;
	}
</style>

<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<div id="jqxListCategory"></div>

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

<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
<#assign mainCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listMainCategories(delegator, userLogin, parameters.prodCatalogId, true) />
<#assign rootCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listRootCategories(delegator, userLogin, parameters.prodCatalogId, true) />
<#else>
<#assign mainCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listMainCategories(delegator, userLogin, parameters.prodCatalogId, false) />
<#assign rootCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listRootCategories(delegator, userLogin, parameters.prodCatalogId, false) />
</#if>

<#assign tmpUpdate = false/>
<#assign tmpCreate = false/>
<#assign customcontrol1 = ""/>
<#if hasOlbPermission("MODULE", "CATEGORY_EDIT", "")>
	<#assign tmpUpdate = true/>
</#if>
<#if hasOlbPermission("MODULE", "CATEGORY_NEW", "")>
	<#assign tmpCreate = true/>
	<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript:OlbProdCategoryNewPopup.openWindow();"/>
</#if>

<#assign contextMenuSsdvItemId = "ctxmnulpcate">
<div id='contextMenu_${contextMenuSsdvItemId}' style="display:none;">
	<ul>
		<#if tmpCreate><li id='${contextMenuSsdvItemId}_newcategory'><i class="fa fa-plus"></i>&nbsp;&nbsp;${uiLabelMap.BSAddNewCategory}</li></#if>
		<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
		<li id='configCategory'><i class="fa-file-image-o"></i>&nbsp;&nbsp;${uiLabelMap.BSConfigCategory}</li>
		</#if>
		<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
		<li id='moveToCategory'><i class="icon-share-alt"></i>&nbsp;&nbsp;${uiLabelMap.DmsMoveToCategory}
			<ul id="moveTarget" style="width: 220px;">
				<#if mainCategories?exists>
					<#list mainCategories as item>
						<li id="${item.productCategoryId}">${item.categoryName?if_exists}</li>
					</#list>
				</#if>
			</ul>
		</li>
		</#if>
	</ul>
</div>

<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<#include "productCategoryNewPopup.ftl">

<@jqTreeGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script>
	var mainCategories = [
		<#if mainCategories?exists>
			<#list mainCategories as item>
			{	productCategoryId: "${item.productCategoryId?if_exists}",
				categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
			},
			</#list>
		</#if>
	];
	var rootCategories = [
		<#if rootCategories?exists>
			<#list rootCategories as item>"${item.productCategoryId?if_exists}",</#list>
		</#if>
	];
	
	<#--
	<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
		var urlCategories = "loadCategoriesOfWebSite?isEc=true&prodCatalogId=" + "${parameters.prodCatalogId?if_exists}";
	<#else>
		var urlCategories = "loadCategoriesOfWebSite?isEc=false&prodCatalogId=" + "${parameters.prodCatalogId?if_exists}&getAll=Y";
	</#if>
	-->
	$(function(){
		OlbProdCategoryList.init();
	});

	var OlbProdCategoryList = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuSsdvItemId}"));
		};
		var initElementComplex = function(){
			var configCategory = {
				width: '100%',
				filterable: false,
				showfilterrow: false,
				key: 'productCategoryId',
				parentKeyId: 'primaryParentCategoryId',
				localization: getLocalization(),
				datafields: [
					{name: "productCategoryId", type: "string" },
					{name: "productCategoryTypeId", type: "string" },
					{name: "primaryParentCategoryId", type: "string" },
					{name: "prodCatalogId", type: "string" },
					{name: "categoryName", type: "string" },
					{name: "longDescription", type: "string" },
					{name: "sequenceNum", type: "number" },
					{name: "fromDate", type: "date", other: "Timestamp"}
				],
				columns: [
					{text: "${uiLabelMap.DmsCategoryId}", dataField: "productCategoryId", width: 150, editable: false,
						cellsrenderer: function(row, colum, value) {
					    	return "<span><a href='viewCategory?productCategoryId=" + value + "'>" + value + "</a></span>";
					    }
					},
					{text: "${uiLabelMap.BSOfCatalog}", dataField: "prodCatalogId", width: 150, editable: false},
					{text: "${uiLabelMap.DmsCategoryName}", dataField: "categoryName", width: 250},
					{text: "${uiLabelMap.DmsDescription}", dataField: "longDescription"},
					{text: "${uiLabelMap.BSSequenceNumber}", datafield: 'sequenceNum', width: 100}
				],
				useUrl: true,
				root: 'categories',
				//url: urlCategories,
				url: "getTreeProductCategories",
				data: {
					prodCatalogId: "${parameters.prodCatalogId?if_exists}"
				},
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap[titleProperty])}",
					<#if tmpCreate>customcontrol1: "${StringUtil.wrapString(customcontrol1)}",</#if>
					expendButton: true,
				},
                contextMenu: "contextMenu_${contextMenuSsdvItemId}",
            	showtoolbar: true,
            	pageable: true,
            	editable: <#if tmpUpdate>true<#else>false</#if>,
            	updateRow: function (rowID, rowData, commit) {
					if (typeof (rowData.fromDate) == 'object') {
						rowData.fromDate = rowData.fromDate.time;
					}
					$.ajax({
						type: 'POST',
						url: "updateCategoryAndRollupAjax",
						data: rowData,
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, "default", function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	if ($("#jqxListCategory").length > 0) {
							        		$("#jqxListCategory").jqxTreeGrid("updateBoundData");
							        	}
									}
							);
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
	            },
			};
			new OlbTreeGrid($("#jqxListCategory"), null, configCategory, []);
		};
		var initEvent = function(){
			 $("#contextMenu_${contextMenuSsdvItemId}").on("shown", function () {
				var rowIndexEditing = $("#jqxListCategory").jqxTreeGrid("getSelection");
				var parentProductCategoryId = rowIndexEditing[0].parentProductCategoryId;
				
				$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "configCategory", true);
				if (parentProductCategoryId) {
					if (_.indexOf(rootCategories, parentProductCategoryId) === -1) {
						//$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "moveToCategory", false);
					} else {
						$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "moveToCategory", false);
					}
				} else {
					$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "moveToCategory", true);
				}
				for (var x in mainCategories) {
					$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", mainCategories[x].productCategoryId, false);
				}
				$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", parentProductCategoryId, true);
		    });
		    $("#contextMenu_${contextMenuSsdvItemId}").on("itemclick", function (event) {
		        var args = event.args;
		        var itemId = $(args).attr("id");
		        
		        var idTreeGrid = "#jqxListCategory";
		        var rowData;
		        var selection = $(idTreeGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	
		        switch (itemId) {
		        case "configCategory":
		        	if (rowData) {
		        		window.location.href = "ConfigCategory?productCategoryId=" + rowData.productCategoryId;
		        	}
					break;
				case "${contextMenuSsdvItemId}_newcategory":
					if (rowData) {
						var productCategoryIdToDDB = OlbProdCategoryNewPopup.getObj().productCategoryIdToDDB;
						if (productCategoryIdToDDB) {
							productCategoryIdToDDB.selectItem([rowData.productCategoryId], null, {"defaultValue": rowData.productCategoryId, "defaultLabel": rowData.categoryName});
							OlbProdCategoryNewPopup.openWindow();
						}
					}
					break;
				default:
					if (itemId && rowData) {
						moveCategory(rowData.productCategoryId, itemId);
					}
					break;
				}
		    });
		};
		var moveCategory = function(productCategoryId, primaryParentCategoryId) {
			var dataMap = {
				productCategoryId: productCategoryId,
				primaryParentCategoryId: primaryParentCategoryId,
				productCategoryTypeId: "CATALOG_CATEGORY",
			}
			$.ajax({
				type: 'POST',
				url: "moveProductCategoryAjax",
				data: dataMap,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	if ($("#jqxListCategory").length > 0) {
					        		$("#jqxListCategory").jqxTreeGrid("updateBoundData");
					        	}
							}
					);
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		}
		return {
			init: init
		};
	}());
</script>