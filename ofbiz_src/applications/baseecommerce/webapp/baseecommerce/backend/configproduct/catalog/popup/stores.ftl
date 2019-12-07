<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/stores.js"></script>

<div id="jqxwindowStores" class='hide'>
	<div>${uiLabelMap.DmsProductStoreList}</div>
	<div style="overflow: hidden;">
	<div class='form-window-content' style="height: 350px;">
			<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
			<div class="pull-right margin-bottom10">
				<a href='javascript:void(0)' onclick='AddStore.open()'><i class='icon-plus open-sans'></i>${uiLabelMap.accAddNewRow}</a>
				&nbsp;&nbsp;&nbsp;<a href='javascript:void(0)' onclick='Stores._delete()' class='red'><i class='icon-trash open-sans'></i>${uiLabelMap.DmsDelete}</a>
			</div>
			</#if>
			<div id="jqxgridStores"></div>
		</div>
		<div class="form-action">
			<button id="cancelStores" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationStores">
	<div id="notificationContentStores"></div>
</div>

<#include "addStoreToCatalog.ftl"/>

<#assign listProductStore = delegator.findList("ProductStore", null, null, null, null, false) />

<script>
var mapProductStoreCurrency = {<#if listProductStore?exists><#list listProductStore as item>
	'${item.productStoreId?if_exists}': '${StringUtil.wrapString(item.defaultCurrencyUomId?if_exists)}',
</#list></#if>};
</script>