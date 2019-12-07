<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollview.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/crmresources/js/ResizePageContent.js"></script>

<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/listCategories.js"></script>

<style>
	.text-right {
		margin-top: 4px;
	}
</style>

<div id="container"></div>
<div id="jqxNotificationNested">
	<div id="notificationContentNested"></div>
</div>
<div id="treeGrid"></div>

<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
<#assign mainCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listMainCategories(delegator, userLogin, parameters.prodCatalogId, true) />
<#assign rootCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listRootCategories(delegator, userLogin, parameters.prodCatalogId, true) />
<#else>
<#assign mainCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listMainCategories(delegator, userLogin, parameters.prodCatalogId, false) />
<#assign rootCategories = Static["com.olbius.baseecommerce.backend.ConfigProductServices"].listRootCategories(delegator, userLogin, parameters.prodCatalogId, false) />
</#if>

<div id='contextMenu' style="display:none;">
	<ul>
		<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
		<li id='configCategory'><i class="fa-file-image-o"></i>&nbsp;&nbsp;${uiLabelMap.BSConfigCategory}</li>
		</#if>
		<li id='viewListProduct'><i class="fa-search"></i>&nbsp;&nbsp;${uiLabelMap.DmsViewListProduct}</li>
		<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
		<li id='moveToCategory'><i class="icon-share-alt"></i>&nbsp;&nbsp;${uiLabelMap.DmsMoveToCategory}
			<ul id="moveTarget" style="width: 220px;">
				<#if mainCategories?exists>
					<#list mainCategories as item>
						<li id="${item.productCategoryId}">${item.categoryName}</li>
					</#list>
				</#if>
			</ul>
		</li>
		</#if>
	</ul>
</div>

<#include "popup/addCategory.ftl"/>
<#include "popup/viewListProducts.ftl"/>

<script>
	<#if security.hasEntityPermission("CATALOG", "_CREATE", session)>
		$(document).ready(function() {
			$(".widget-header").append("<div style='margin-top: -9px;font-size: 14px;'><a href='javascript:void(0)'" +
											"onclick='AddCatagory.open()'><i class='icon-plus open-sans'>" +
											"</i>" + "${uiLabelMap.DmsCreateNew}" + "</a></div>");
		});
	</#if>
	var editable = false;
	<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
		editable = true;
	</#if>
	var mainCategories = [<#if mainCategories?exists><#list mainCategories as item>{
		productCategoryId: "${item.productCategoryId?if_exists}",
		categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
	},</#list></#if>];
	var rootCategories = [<#if rootCategories?exists><#list rootCategories as item>{
		productCategoryId: "${item.productCategoryId?if_exists}",
		categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
	},</#list></#if>];
	var rootCategories = [<#if rootCategories?exists><#list rootCategories as item>
		"${item.productCategoryId?if_exists}",
	</#list></#if>];
	<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session)>
		var urlCategories = "loadCategoriesOfWebSite?isEc=true&prodCatalogId=" + "${parameters.prodCatalogId?if_exists}";
		<#else>
		var urlCategories = "loadCategoriesOfWebSite?isEc=false&prodCatalogId=" + "${parameters.prodCatalogId?if_exists}";
	</#if>
</script>