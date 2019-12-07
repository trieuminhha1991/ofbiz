<#assign stores = Static["com.olbius.baseecommerce.backend.ContentUtils"].getStores(delegator, locale) />

<aside class="col-lg-4">
	<div class="row">
		<div class="find-all-store">
			<h4><i class="allicon-point"></i>Hệ thống cửa hàng</h4>
			${StringUtil.wrapString(stores?if_exists)}
		</div>
	</div>
</aside>

