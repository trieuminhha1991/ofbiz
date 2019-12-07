<script src="/crmresources/js/crmsetting/productsInSubject.js"></script>

<div id="jqxwindowViewListProducts" class="hide">
	<div>${uiLabelMap.BSListProduct}</div>
	<div style="overflow: hidden;">
	<div class="form-window-content" style="height: 350px;">
			<div class="pull-right margin-bottom10">
				<a href="javascript:void(0)" onclick="AddProduct.open()"><i class="icon-plus open-sans"></i>${uiLabelMap.accAddNewRow}</a>
				&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" onclick="Products._delete()" class="red"><i class="icon-trash open-sans"></i>${uiLabelMap.DmsDelete}</a>
			</div>
			<div id="jqxgridViewListProducts"></div>
		</div>
		<div class="form-action">
			<button id="cancelViewListProducts" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationProducts">
	<div id="notificationContentProducts">
	</div>
</div>

<#include "addProductToSubject.ftl"/>