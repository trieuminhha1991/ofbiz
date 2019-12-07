<div class="col-lg-2 col-sm-2 col-sm-2 hidden-xs">
	<div class="row">
		<aside class="menuleft">
			<h3>Sản phẩm HOT</h3>
			<ul>
				<#if bestsellingcat?exists>
					<#list bestsellingcat as cat>
						<li>
							<a class="giam-can" href="categorydetail?productCategoryId=${cat.productCategoryId}&VIEW_INDEX=0" title="${cat.categoryName}">
								<i class="${cat.icon}"></i>${cat.categoryName}
							</a>
						</li>
					</#list>
				</#if>
			</ul>
		</aside>
	</div>
</div>
<div class="col-lg-7 col-sm-7 col-sm-7 col-xs-12">
	<#include "slider.ftl"/>
</div>
<div class="col-lg-3 col-sm-3 col-sm-3 hidden-xs">
	<div class="row">
		<aside class="homenews">
			<figure>
				<h3>Tin tức làm đẹp</h3><b></b>
				<a href="#" title="">Xem tất cả</a>
			</figure>
			<ul>
				<li>
					<a href="#" title=""><img width="100" height="66" src="/dpc/DemoImg/0001425.jpeg" alt="" title="" /></a><a href="#" title="">Bắt giữ hơn 12 tấn thực phẩm chức năng giả tại Hà Nội</a>
					<span>1 phút trước</span>
				</li>
				<li>
					<a href="#" title=""><img width="100" height="66" src="/dpc/DemoImg/0001425.jpeg" alt="" title="" /></a><a href="#" title="">Bắt giữ hơn 12 tấn thực phẩm chức năng giả tại Hà Nội</a>
					<span>1 phút trước</span>
				</li>
				<li>
					<a href="#" title=""><img width="100" height="66" src="/dpc/DemoImg/0001425.jpeg" alt="" title="" /></a><a href="#" title="">Bắt giữ hơn 12 tấn thực phẩm chức năng giả tại Hà Nội</a>
					<span>1 phút trước</span>
				</li>
				<li>
					<a href="#" title=""><img width="100" height="66" src="/dpc/DemoImg/0001425.jpeg" alt="" title="" /></a><a href="#" title="">Bắt giữ hơn 12 tấn thực phẩm chức năng giả tại Hà Nội</a>
					<span>1 phút trước</span>
				</li>
			</ul>
		</aside>
	</div>
</div>
<div class='row'>
	<div class='col-lg-12'>
		<ul class="iinfo">
		    <li class="since"><a href="#" rel="nofollow"><i class="allicon-since"></i>Uy tín 27 năm<br>(since 1988)</a></li>
		    <li class="ck"><a href="#" rel="nofollow"><i class="allicon-ck"></i>Cam kết hàng<br>chính hãng</a></li>
		    <li class="ds"><a href="#" rel="nofollow"><i class="allicon-ds"></i>Dược sĩ tư vấn<br>kinh nghiệm trên 5 năm</a></li>
		    <li class="th"><a href="#" rel="nofollow"><i class="allicon-th"></i>Đổi trả hàng <br>trong 7 ngày</a></li>
		    <li class="gh"><a href="#" rel="nofollow"><i class="allicon-gh"></i>Giao hàng toàn quốc<br>trả tiền khi nhận hàng</a></li>
	    </ul>
	</div>
</div>
<div class="row">
	<div class="col-lg-12">
		${screens.render("component://dpc/widget/CatalogScreens.xml#category-include")}
		<div class="clearfix">&nbsp;</div>
		<div class="moreproduct">
			<#if bestsellingcat?exists>
				<#list bestsellingcat as cat>
					<a href="javascript:void(0)" title="${cat.categoryName}">
						<form action="<@ofbizUrl>main</@ofbizUrl>" method="POST">
							<input value="${cat.productCategoryId}" type="hidden" name="productCategoryId"/>
							<button><span>${cat.total?if_exists}</span> ${cat.categoryName}</button>
						</form>
					</a>
				</#list>
			</#if>
		</div>
	</div>
</div>
