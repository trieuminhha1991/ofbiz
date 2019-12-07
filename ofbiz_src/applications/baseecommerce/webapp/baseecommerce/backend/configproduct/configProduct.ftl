<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/ecommerceresources/js/backend/configproduct/configProduct.js"></script>

<style>
	label {
	    margin-top: 4px;
	}
	.product-config-image .img-product-large {
	    width: 430px;
		height: 430px;
	}
	.product-config-image .img-product-small {
		width: 248px;
		height: 248px;
	}
	.product-config-image .img-product-additional {
		width: 248px;
		height: 248px;
	}
	img {
	    max-height: 100%;
	}
</style>
<script>
	var productIdParam = "${parameters.productId?if_exists}";
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSConfigProduct)}");
</script>

<div id="container"></div>
<div id="jqxNotificationNested">
	<div id="notificationContentNested"></div>
</div>


<div class="row-fluid">
<div id="fuelux-wizard" class="row-fluid">
	<ul class="wizard-steps" style="width: 100%">
		<li data-target="#step1" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.DmsGeneralInformation}</span></li>
		<li data-target="#step2"><span class="step">2</span> <span class="title">${uiLabelMap.BSProductImages}</span></li>
	</ul>
</div>
<hr />
<div class="step-content row-fluid position-relative">
	<div class="step-pane active" id="step1">
		<#include "configProductInfo.ftl"/>
	</div>
	<div class="step-pane" id="step2">
		<#include "productImages.ftl"/>
	</div>
</div>
<div class="row-fluid wizard-actions">
	<button class="btn btn-small btn-prev" id="btnPreveiw"><i class="icon-arrow-left"></i>${uiLabelMap.BSPrev}</button>
	<button class="btn btn-small btn-success btn-next" id="btnNext">${uiLabelMap.BSNext}<i class="icon-arrow-right icon-on-right"></i></button>
</div>
</div>