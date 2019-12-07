<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/catalog/categories.js"></script>

<div id="jqxwindowViewListCategories" class='hide'>
	<div>${uiLabelMap.DmsViewListCategory}</div>
	<div style="overflow: hidden;">
	<div class='form-window-content' style="height: 350px;">
			<#if security.hasEntityPermission("CATALOG", "_UPDATE", session)>
			<div class="pull-right margin-bottom10">
				<a href='javascript:void(0)' onclick='AddCategory.open()'><i class='icon-plus open-sans'></i>${uiLabelMap.accAddNewRow}</a>
				&nbsp;&nbsp;&nbsp;<a href='javascript:void(0)' onclick='Categories._delete()' class='red'><i class='icon-trash open-sans'></i>${uiLabelMap.DmsDelete}</a>
			</div>
			</#if>
			<div id="jqxgridViewListCategories"></div>
		</div>
		<div class="form-action">
			<button id="cancelViewListCategories" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationCategories">
	<div id="notificationContentCategories">
	</div>
</div>

<#include "addCategoryToCatalog.ftl"/>