<div class="bf" style="z-index: 1000;">
	<ul itemprop="breadcrumb" class="breadcrumb">
		<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
			<a title="Trang chủ" href="<@ofbizUrl>main</@ofbizUrl>" itemprop="url"><i class="allicon-home"></i></a><span class="separator">›</span>
		</li>
		<li itemscope="itemscope" itemtype="http://data-vocabulary.org/Breadcrumb">
			<h1><a title="${(category.categoryName?if_exists)}" rel="category tag" href="categorydetail?productCategoryId=${(parameters.productCategoryId)?if_exists}&VIEW_INDEX=0" itemprop="url">
				<span itemprop="title">${(category.categoryName?if_exists)}</span>
			</a></h1>
		</li>
	</ul>
	<ul class="filter">
		<li>
			Tìm theo
		</li>
		<li>
			<label> Giới tính
				<span><i class='fk-arrdown fa fa-caret-down'></i></span><span  class='clearfilter' data-id="genderFilter" <#if parameters.genderFilter?exists && parameters.genderFilter!="">style='display: inline'</#if>><i class='fa fa-remove'></i></span>
				<div class="dropfilter">
					<a href="javascript:void(0)" class='gender' data-id="M">Nam</a>
					<a href="javascript:void(0)" class='gender' data-id="F">Nữ</a>
				</div>
			</label>
		</li>
		<li>
			<label> <span>Thương hiệu</span>
				<span><i class='fk-arrdown fa fa-caret-down'></i></span><span  class='clearfilter' data-id="brandFilter" <#if parameters.brandFilter?exists && parameters.brandFilter!="">style='display: inline'</#if>><i class='fa fa-remove'></i></span>
				<div class="twocolum">
					<ul>
						<#list listProductSupplier?if_exists as supplier>
							<li class='supplier filter-option' data-id="${supplier.brandName}">
								<a href="javascript:void(0)">${supplier.groupName}</a>
							</li>
						</#list>
					</ul>
				</div>
			</label>
		</li>
		<li>
			<label> Xuất xứ <span><i class='fk-arrdown fa fa-caret-down'></i></span><span  class='clearfilter' data-id="originFilter" <#if parameters.originFilter?exists && parameters.originFilter!="">style='display: inline'</#if>><i class='fa fa-remove'></i></span>
				<div class="twocolum">
					<ul>
						<#list listOrigin?if_exists as origin>
							<li class='origin filter-option' data-id="${origin.originGeoId}">
								<a href="javascript:void(0)">${origin.geoName}</a>
							</li>
						</#list>

					</ul>
				</div> </label>
		</li>
	</ul>
</div><div></div>
<div class="fastsearch">
	<span>Tìm nhanh (tuỳ chọn theo danh mục):</span>
	<a title="" href="#">Biotin</a>
	<a title="" href="#">Everyday Super</a>
	<a title="" href="#">Best Slim</a>
	<a title="" href="#">Best Group</a>

	<div class="order">
		<select name="products-orderby" id="products-orderby">
			<option value="NAS">Tên: A đến Z</option>
			<option value="NDS">Tên: Z đến A</option>
			<option value="PRAS">Giá: Thấp đến cao</option>
			<option value="PRDS">Giá: Cao đến thấp</option>
		</select>
		<form method="get" action="" id="orderForm">
			<input type="hidden" name="orb"/>
			<input type="hidden" name="VIEW_INDEX" value="${parameters.VIEW_INDEX?if_exists}"/>
			<input type="hidden" name="productCategoryId" value="${parameters.productCategoryId?if_exists}"/>
			<input type="hidden" name="genderFilter" value="${parameters.genderFilter?if_exists}"/>
			<input type="hidden" name="originFilter" value="${parameters.originFilter?if_exists}"/>
			<input type="hidden" name="brandFilter" value="${parameters.brandFilter?if_exists}"/>
		</form>
	</div>
</div>
<div class="banner">
	<#if Static["org.ofbiz.base.util.UtilValidate"].isNotEmpty(horizontalBanner)>
		<a href="#" title=""><img width="1270" height="100" src="${StringUtil.wrapString((horizontalBanner.originalImageUrl)?if_exists)}" alt=""></a>
	</#if>
</div>
<aside class="lst">
	<ul class="iinfo">
		<li class="since">
			<a href="#" rel="nofollow"><i class="allicon-since"></i>Uy tín 27 năm
			<br>
			(since 1988)</a>
		</li>
		<li class="ck">
			<a href="#" rel="nofollow"><i class="allicon-ck"></i>Cam kết hàng
			<br>
			chính hãng</a>
		</li>
		<li class="ds">
			<a href="#" rel="nofollow"><i class="allicon-ds"></i>Dược sĩ tư vấn
			<br>
			kinh nghiệm trên 5 năm</a>
		</li>
		<li class="th">
			<a href="#" rel="nofollow"><i class="allicon-th"></i>Đổi trả hàng
			<br>
			trong 7 ngày</a>
		</li>
		<li class="gh">
			<a href="#" rel="nofollow"><i class="allicon-gh"></i>Giao hàng toàn quốc
			<br>
			trả tiền khi nhận hàng</a>
		</li>
	</ul>
</aside>
<#if productCategoryMembers?has_content>
      <ul class="listproduct">
        <#list productCategoryMembers as productCategoryMember>
		<#assign main = parameters.productCategoryId/>
		<#assign feature = main + "_TOP"/>
		<#if productCategoryMember.productCategoryId == feature>
			${setRequestAttribute("isFeature", "true")}
		<#else>
			${setRequestAttribute("isFeature", "false")}
		</#if>
            ${setRequestAttribute("optProductId", productCategoryMember.productId)}
            ${setRequestAttribute("productCategoryMember", productCategoryMember)}
            ${setRequestAttribute("listIndex", productCategoryMember_index)}
            ${screens.render(productsummaryScreen2)}
        </#list>
      </ul>
<#else>
    <hr />
    <center class="top30 bottom30">${uiLabelMap.ProductNoProductsInThisCategory}</center>
</#if>
	<!-- <!-- <li class="feature">
		<a href="#" title="Breast Up - Tăng kích thước vòng ngực tự nhiên"> <img width="388" height="170" src="/dpc/DemoImg/breast-up.jpg" alt="Breast Up - Tăng kích thước vòng ngực tự nhiên"> <h3>Breast Up - Tăng kích thước vòng ngực tự nhiên</h3> <b>1.100.000 đ</b>
		<button type="button">
			Mua ngay
		</button> </a>
	</li> -->
