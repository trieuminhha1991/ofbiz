<style type="text/css">
	.menu-float {
		margin-top:20px;
		padding-left:30px;
	}
	.menu-float li{
		display:inline-block;
		margin:5px;
		padding: 5px;
		width: 30%;
	}
	.menu-float li a{
		display:block;
		height: 100%;
		width: 100%;
	}
	.menu-float li img, .menu-float li label{
		display: inline-block;
	}
	.menu-float li label {
		margin-left: 10px
	}
	.menu-float li:hover {
	    background-color: rgba(111, 179, 224, 0.26);
	}
</style>
<div class="row-fluid">
	<div class="span12">
		<ul class="unstyled spaced2 menu-float">
			<li>
				<a href="<@ofbizUrl>getListProductConfigPacking</@ofbizUrl>">
					<img src="/poresources/images/product.png" width="48" height="48"/>
					<label>${uiLabelMap.listProductConfigPacking}</label>
				</a>
			</li>
			<li>
				<a href="<@ofbizUrl>ProductBrand</@ofbizUrl>">
					<img src="/poresources/images/brand.png" width="48" height="48"/>
					<label>${uiLabelMap.POProductBrand}</label>
				</a>
			</li>
			<li>
				<a href="<@ofbizUrl>getPartyCurrencyConfig</@ofbizUrl>">
					<img src="/poresources/images/currency.png" width="48" height="48"/>
					<label>${uiLabelMap.BPOCurrencyUomId}</label>
				</a>
			</li>
		</ul>
	</div>
</div>