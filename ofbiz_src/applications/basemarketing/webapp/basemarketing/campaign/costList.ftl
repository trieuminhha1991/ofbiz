<!--cost container for activation-->
<script>
	var currency = "VND";
	function setCurrency(key) {
		currency = key;
		var list = $("#currency li");
		for (var x = 0; x < list.length; x++) {
			var obj = $(list[x]);
			if (obj.hasClass("active")) {
				obj.removeClass("active");
			}
		}
		$("#currencyBt span").text(key);
		$("#" + key).parent("li").addClass("active");
	}
</script>
<div class="row-fluid paddingtop-10">
	<#if costTypeList?has_content>
	<div class="span3">
		<label class="header-action"> ${uiLabelMap.chooseCostType} </label>
	</div>
	<div class="span3" style="margin-left: 7px">
		<select id="costTypeList">
			<#list costTypeList as costType>
			<option value="${costType.marketingCostTypeId}">${costType.name}</option>
			</#list>
		</select>
	</div>
	<div class="span3">
		<button id="addCost" class="btn btn-primary btn-small open-sans" style="margin-left: 20px">
			<i class='fa-plus-circle'>&nbsp;</i> ${uiLabelMap.addCost}
		</button>
	</div>
	<div class="span3">
		<label class="label-currency">Choose Currency:</label>
		<div class="currency">
			<button class="btn btn-mini bigger btn-primary btn-custom-currency dropdown-toggle" data-toggle="dropdown" id="currencyBt">
				<span>VND</span>
				<i class="icon-angle-down icon-on-right"></i>
			</button>

			<ul class="dropdown-menu dropdown-yellow pull-right dropdown-caret dropdown-close" id="currency">
				<li>
					<a href="javascript:setCurrency('USD')" id="USD">USD</a>
				</li>
				<li>
					<a href="javascript:setCurrency('EUR')" id="EUR">EUR</a>
				</li>
				<li class='active'>
					<a href="javascript:setCurrency('VND')" id="VND">VND</a>
				</li>
			</ul>
		</div>
	</div>
	</#if>
</div>

<div class="cost-form" id="costContainer">
	<div class="row-al header-cost-form">
		<div class="col-al4 aligncenter">
			Chi phí
		</div>
		<div class="col-al8">
			<div class="row-al">
				<div class="col-al4 aligncenter borderleft">
					Mô tả
				</div>
				<div class="col-al2 aligncenter borderleft">
					Số lượng
				</div>
				<div class="col-al3 aligncenter borderleft">
					Đơn giá
				</div>
				<div class="col-al3 aligncenter borderleft">
					Thành tiền
				</div>
			</div>
		</div>
	</div>
	<div id="cost-form" class='form-content'>

	</div>
</div>
