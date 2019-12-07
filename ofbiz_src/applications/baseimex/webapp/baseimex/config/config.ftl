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
				<a href="<@ofbizUrl>getImportPeriodConfigs</@ofbizUrl>">
					<img src="/imexresources/css/images/custom_time_period.png" width="48" height="48"/>
					<label>${uiLabelMap.BIEImportPeriod}</label>
				</a>
			</li>
			<li>
				<a href="<@ofbizUrl>getImportPortConfigs</@ofbizUrl>">
					<img src="/imexresources/css/images/import-port.png" width="48" height="48"/>
					<label>${uiLabelMap.PortOfDischarge}</label>
				</a>
			</li>
			<li>
				<a href="<@ofbizUrl>getConfigCapacityGenerals</@ofbizUrl>">
					<img src="/imexresources/css/images/shipment-packing.png" width="48" height="48"/>
					<label>${uiLabelMap.ConfigCapacity}</label>
				</a>
			</li>
		</ul>
	</div>
</div>