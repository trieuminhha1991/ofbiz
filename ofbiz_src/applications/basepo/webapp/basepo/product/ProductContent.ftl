<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/poresources/js/product/ProductContent.js"></script>

<style>
	.text-header {
		color: black !important;
	}
	.form-window-content-custom label {
		margin-top: -4px;
	}
	.boder-all-profile .label {
		font-size: 14px;
		text-shadow: none;
		background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
		line-height: 14px !important;
		margin-top: -20px;
	}
	.product-config-image .img-product-large {
		width: 430px;
		height: 430px;
		text-align: center;
	}
	.product-config-image .img-product-small {
		width: 248px;
		height: 248px;
		text-align: center;
	}
	.product-config-image .img-product-additional {
		width: 248px;
		height: 248px;
		text-align: center;
	}
	img {
		max-height: 100%;
	}
</style>


<div class="tabbable product-config-image">
	<ul class="nav nav-tabs">
		<li class="active"><a data-toggle="tab" href="#info"><i class="green fa-info-circle bigger-110"></i> ${uiLabelMap.DAInformation}</a></li>
		<li><a data-toggle="tab" href="#image"><i class="green fa-file-image-o bigger-110"></i> ${uiLabelMap.BSProductImages}</a></li>
	</ul>
	<div class="tab-content">
		<div id="image" class="tab-pane row-fluid">
			
			<div class="row-fluid margin-top10">
				<div class="span1"></div>
				<div class="span5">
					<div class="img-product-large">
						<img id="largeImage" src="/poresources/logo/product_demo.png"/>
					</div>
				</div>
				<div class="span1"></div>
				<div class="span3">
					<div class="img-product-small">
						<img id="smallImage" src="/poresources/logo/product_demo.png"/>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span1"></div>
				<div class="span5 border-top"><label style="text-align: center">${uiLabelMap.BSLargeImage}</label></div>
				<div class="span1"></div>
				<div class="span3 border-top"><label style="text-align: center">${uiLabelMap.BSSmallImage}</label></div>
			</div>
		
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BSAdditionalImages}</span>
				<div class="row-fluid">
					<div class="span3">
						<div class="img-product-additional">
							<img id="additional1" src="/poresources/logo/product_demo.png"/>
						</div>
					</div>
					<div class="span3">
						<div class="img-product-additional">
							<img id="additional2" src="/poresources/logo/product_demo.png"/>
						</div>
					</div>
					<div class="span3">
						<div class="img-product-additional">
							<img id="additional3" src="/poresources/logo/product_demo.png"/>
						</div>
					</div>
					<div class="span3">
						<div class="img-product-additional">
							<img id="additional4" src="/poresources/logo/product_demo.png"/>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span3 border-top center">1</div>
					<div class="span3 border-top center">2</div>
					<div class="span3 border-top center">3</div>
					<div class="span3 border-top center">4</div>
				</div>
			</div>
		
		</div>
		<div id="info" class="tab-pane  in active row-fluid">
				
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECEffects}</span>
				<div id="txtEffects"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECComposition}</span>
				<div id="txtComposition"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECShelfLife}</span>
				<div id="txtShelfLife"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECUsers}</span>
				<div id="txtUsers"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECInstructions}</span>
				<div id="txtInstructions"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECLicense}</span>
				<div id="txtLicense"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECPacking}</span>
				<div id="txtPacking"></div>
			</div>
			
			<div class="span12 no-left-margin boder-all-profile margin-bottom10">
				<span class="text-header">${uiLabelMap.BECContraindications}</span>
				<div id="txtContraindications"></div>
			</div>
		
		</div>
	</div>
</div>


<script>
	var productIdParam = "${parameters.productId?if_exists}";
</script>

