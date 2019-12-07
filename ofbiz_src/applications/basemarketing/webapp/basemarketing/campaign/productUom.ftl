<!--cost container for activation-->
<div class="row-fluid paddingtop-10">
	<#if products?has_content>
	<div class="span3">
		<label class="header-action"> ${uiLabelMap.chooseProductSampling} </label>
	</div>
	<div class="span3" style="margin-left: 7px">
		<select id="productSampling">
			<#if products?exists>
			<#list products as product>
			<option value="${product.productId}">${product.productName}</option>
			</#list>
			</#if>
		</select>
	</div>
	<div class="span5">
		<button id="addProduct" class="btn btn-primary btn-small open-sans" style="margin-left: 20px">
			${uiLabelMap.addProduct}
		</button>
	</div>
	</#if>
</div>

<div class="cost-form" id="productContainer">
	<div class="row-al header-cost-form">
		<div class="col-al3 aligncenter">
			Tên sản phẩm
		</div>
		<div class="col-al3 borderleft aligncenter">
			Địa điểm
		</div>
		<div class="col-al6">
			<div class="row-al">
				<div class="col-al6 aligncenter borderleft">
					Sampling
				</div>
				<div class="col-al6 aligncenter borderleft">
					Số lượng bán
				</div>
			</div>
		</div>
	</div>
	<div id="product-form" class='form-content'>
		<!-- <div class='row-al paddingtop-10 product-row' data-id='" + id + "'>
			<div class='col-al3 aligncenter' id='title" + id + "'>
				<div class='title-inner'>
					abc
				</div>
			</div>
			<div class='col-al9'>
				<div class='row-al'>
					<div class='col-al12'></div>
				</div>
				<div class='row-al paddingtop-10'>
					<div class='col-al12'>
						<button class='add-new-row' id='address-product" + id + "'>
							+
						</button>
					</div>
				</div>
			</div>
		</div> -->
		<!--
		<div class='row-al'>
			<div class='col-al4 paddingtop-5'>
				<select class='fullwidth'></select>
			</div>
			<div class='col-al4'>
				<div class='row-al' id='productSampling'>
					<div class='col-al6'>
						<input type="number"  class='fullwidth'/>
					</div>
					<div class='col-al6 paddingtop-5'>
						<select class='fullwidth'></select>
					</div>
				</div>
				<button class='add-new-row' id='productSamplingAction'>+</button>
			</div>
		</div> -->
	</div>
</div>
