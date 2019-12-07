<script type="text/javascript">
	<#--
		<#assign prodCatalogCategoryTypeList = delegator.findByAnd("ProdCatalogCategoryType", null, null, false)!/>
		var prodCatalogCategoryTypeData = [
		<#if prodCatalogCategoryTypeList?exists>
			<#list prodCatalogCategoryTypeList as item>
			{	id: "${item.prodCatalogCategoryTypeId}",
				description: "${StringUtil.wrapString(item.get("description", locale))}"
			},
			</#list>
		</#if>
		];
	-->
</script>

<style type="text/css">
	#statusbarjqxListCategory {
		width: 0 !important;
	}
	#pagerjqxListCategory .jqx-button-olbius, #pagerjqxListCategory .jqx-dropdownlist-state-normal-olbius {
		margin-bottom: 5px;
	}
</style>

<#assign mainCategories = Static["com.olbius.basesales.product.ProductWorker"].getAllCategoryTreeMapNoPa(delegator, productCategory.productCategoryId)!/>

<div id="jqxListCategory"></div>

<#assign contextMenuSsdvItemId = "ctxmnulpcate">
<div id='contextMenu_${contextMenuSsdvItemId}' style="display:none">
	<ul>
	    <li id="${contextMenuSsdvItemId}_viewproducts"><i class="fa fa-bars"></i>${StringUtil.wrapString(uiLabelMap.BSViewProduct)}</li>
	    <li id="${contextMenuSsdvItemId}_viewcategory"><i class="fa icon-share-alt"></i>${StringUtil.wrapString(uiLabelMap.BSViewCategory)}</li>
	    <li id="${contextMenuSsdvItemId}_backToParentCategory"><i class="fa fa-reply"></i>${StringUtil.wrapString(uiLabelMap.BSBackToParentCategory)}</li>
	    <li id="${contextMenuSsdvItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
	    <li id="${contextMenuSsdvItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>
		<li id="${contextMenuSsdvItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li id='${contextMenuSsdvItemId}_moveToCategory'><i class="fa icon-share-alt"></i>${uiLabelMap.DmsMoveToCategory}
			<ul id="moveTarget" style="width: 220px;">
				<#if mainCategories?exists>
					<#list mainCategories as item>
						<li id="${item.productCategoryId}">${item.categoryName?if_exists}</li>
					</#list>
				</#if>
			</ul>
		</li>
	</ul>
</div>

<#include "productCategoryNewPopup.ftl">

<#assign addType = "popup"/>
<#assign alternativeAddPopup="alterpopupWindow"/>
<#assign titleProperty="BSListCategory"/>

<@jqTreeGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>

<#assign tmpUpdate = false/>
<#assign tmpCreate = false/>
<#assign customcontrol1 = ""/>
<#if hasOlbPermission("MODULE", "CATEGORY_EDIT", "")>
	<#assign tmpUpdate = true/>
</#if>
<#if hasOlbPermission("MODULE", "CATEGORY_NEW", "")>
	<#assign tmpCreate = true/>
	<#assign customcontrol1 = "icon-plus open-sans@@javascript:OlbProductCategoryList.openWindowNewCategoryChild();"/>
</#if>
<script type="text/javascript">
	var mainCategories = [
		<#if mainCategories?exists>
			<#list mainCategories as item>
			{	productCategoryId: "${item.productCategoryId?if_exists}",
				categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
			},
			</#list>
		</#if>
	];
	
	var OlbProductCategoryList = (function(){
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
				parentKeyId: 'parentCategoryId',
				localization: getLocalization(),
				datafields: [
					{name: 'productCategoryId', type: 'string'},
					{name: 'productCategoryTypeId', type: 'string'},
					{name: 'primaryParentCategoryId', type: 'string'},
					{name: 'parentCategoryId', type: 'string'},
					{name: 'description', type: 'string'},
					{name: 'categoryName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}', datafield: 'categoryName', 
						cellsRenderer: function (row, column, value, rowData) {
							if (rowData) {
								return "<span>" + value + " (" + rowData.productCategoryId + ")</span>";
							}
					    }
					},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListProductCategory&pagesize=0&rootCategoryId=${productCategoryId}&productCategoryTypeId=CATALOG_CATEGORY', 
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap.BSAbbListCategory)}",
					<#if tmpCreate>customcontrol1: "${StringUtil.wrapString(customcontrol1)}",</#if>
					expendButton: true,
				},
                contextMenu: "contextMenu_${contextMenuSsdvItemId}",
            	showtoolbar: true,
            	showHeader: false,
            	pageable: true,
            	pagesize: 12,
            	altRows: false,
            	height: 408,
            	pageSizeOptions: [5, 10, 12],
			};
			new OlbTreeGrid($("#jqxListCategory"), null, configCategory, []);
		};
		var initEvent = function(){
			$("#contextMenu_${contextMenuSsdvItemId}").on("shown", function () {
				var idGrid = "#jqxListCategory";
				var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	if (rowData) id = rowData.uid;
	        	
	        	if (rowData && OlbCore.isNotEmpty(rowData.primaryParentCategoryId)) {
	        		$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "${contextMenuSsdvItemId}_backToParentCategory", false);
	        	} else {
	        		$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "${contextMenuSsdvItemId}_backToParentCategory", true);
	        	}
	        	
	        	if (rowData && rowData.productCategoryId == "${productCategory.productCategoryId}") {
	        		$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "${contextMenuSsdvItemId}_viewcategory", true);
	        	} else {
	        		$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "${contextMenuSsdvItemId}_viewcategory", false);
	        	}
	        	
	        	if (!rowData || OlbCore.isEmpty(rowData.parentCategoryId) || rowData.productCategoryTypeId == "RECYCLE_CATEGORY") {
	        		$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "${contextMenuSsdvItemId}_moveToCategory", true);
	        	} else {
	        		$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", "${contextMenuSsdvItemId}_moveToCategory", false);
	        	}
	        	for (var x in mainCategories) {
					$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", mainCategories[x].productCategoryId, false);
				}
				$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", rowData.parentCategoryId, true);
				$("#contextMenu_${contextMenuSsdvItemId}").jqxMenu("disable", rowData.productCategoryId, true);
		    });
	        $("#contextMenu_${contextMenuSsdvItemId}").on('itemclick', function (event) {
	            var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var idGrid = "#jqxListCategory";
		        var rowData;
		        var id;
	        	var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	if (rowData) id = rowData.uid;
	        	switch(tmpId) {
	        		case "${contextMenuSsdvItemId}_refesh": { 
	        			$(idGrid).jqxTreeGrid('updateBoundData');
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_expand": { 
	        			if(id) $(idGrid).jqxTreeGrid('expandRow', id);
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_collapse": { 
	        			if(id) $(idGrid).jqxTreeGrid('collapseRow', id);
	        			break;
        			};
	        		case "${contextMenuSsdvItemId}_viewproducts": { 
	        			var gridGRID = OlbProdCategoryViewProduct.getObj().productCategoryMemberGRID;
	        			if (gridGRID) {
	        				$("#toolbarcontainerjqxgridProducts").find("h4").html("&quot;" + rowData.categoryName + "&quot;&nbsp;&nbsp;>&nbsp;&nbsp;" + "${uiLabelMap.BSProduct}");
	        				var productCategoryObj = $("#wn_prod_productCategoryId");
	        				if (productCategoryObj.length > 0) {
	        					$(productCategoryObj).val(rowData.productCategoryId);
	        				}
	        				//gridGRID.updateSource("getProductByProductCategoryIdIncludeChild?productCategoryId=" + rowData.productCategoryId);
	        				gridGRID.updateSource("jqxGeneralServicer?sname=JQGetListProductInCategoryInclude&productCategoryId=" + rowData.productCategoryId);
	        			}
	        			break;
        			};
	        		case "${contextMenuSsdvItemId}_viewcategory": { 
	        			if (rowData) {
	        				window.location.href = "viewCategory?productCategoryId=" + rowData.productCategoryId;
	        			}
	        			break;
        			};
	        		case "${contextMenuSsdvItemId}_backToParentCategory": { 
	        			if (rowData && OlbCore.isNotEmpty(rowData.primaryParentCategoryId)) {
	        				window.location.href = "viewCategory?productCategoryId=" + rowData.primaryParentCategoryId;
	        			}
	        			break;
        			};
	        		default: 
	        			if (tmpId && rowData) {
							moveCategory(rowData.productCategoryId, tmpId);
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
		var openWindowNewCategoryChild = function(){
			var productCategoryIdToDDB = OlbProdCategoryNewPopup.getObj().productCategoryIdToDDB;
			if (productCategoryIdToDDB) {
				var newUrl = "jqxGeneralServicer?sname=JQListProductCategory&pagesize=0&productCategoryTypeId=CATALOG_CATEGORY&rootCategoryId=${productCategory.productCategoryId}";
				productCategoryIdToDDB.updateSource(newUrl, null, function(){
					var idTreeGrid = "#jqxListCategory";
			        var rowData;
			        var selection = $(idTreeGrid).jqxTreeGrid('getSelection');
		        	if (selection.length > 0) rowData = selection[0];
		        	if (rowData) {
		        		productCategoryIdToDDB.selectItem([rowData.productCategoryId], null, {"defaultValue": rowData.productCategoryId, "defaultLabel": rowData.categoryName});
		        	}
				});
				OlbProdCategoryNewPopup.openWindow();
			}
		};
		return {
			init: init,
			openWindowNewCategoryChild: openWindowNewCategoryChild,
		}
	}());
	$(function(){
		OlbProductCategoryList.init();
	});
</script>