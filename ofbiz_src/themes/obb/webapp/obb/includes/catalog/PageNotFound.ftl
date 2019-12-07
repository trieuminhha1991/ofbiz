<script>
	$(document).ready(function() {
		$(".breadcrumbs").append("<li class='breadcrumbs_end'><i class='fa fa-caret-right'></i><a>&nbsp;&nbsp;&nbsp;404</a></li>");
		$("#tab_content_product_comments").addClass("hide");
		$(".fb-comments").addClass("hide");
		$(".product-related").addClass("hide");
		
		$("#left-nav").parent().removeClass("col-lg-9");
		$("#left-nav").parent().removeClass("col-md-9");
		
		$("#left-nav").parent().addClass("col-lg-11");
		$("#left-nav").parent().addClass("col-md-11");
	});
	function focusToSearch() {
		$("#searchInput").focus();
	}
</script>



<div class="row page-404">
	<div class="col-lg-5 col-md-5 col-sm-5 col-xs-5">
		<img class="product-primary-img" src="/obbresources/images/404-cart.png"/>
	</div>
	<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7">
		
		<div class="content">
		    <div class="message">
		        <h2>
		            Chúng tôi không tìm thấy<br>
		            trang bạn yêu cầu
		        </h2>
		        <div class="subtitle-404">Trang này không tồn tại hoặc sản phẩm đã bị xóa khỏi website.</div>
		    </div>
		
		    <div>
		        <a href="<@ofbizUrl>main</@ofbizUrl>">
		            <div class="icon">
		            	<i class="fa fa-home"></i>
		            </div>
		            <div class="title-404">
		                Về trang chủ
		            </div>
		        </a>
		        <a href="javascript:void(0)" class="hide-mobile hide-tab" onclick="focusToSearch()">
		            <div class="icon">
		            	<i class="fa fa-search"></i>
		            </div>
		            <div class="title-404">
		                Tìm kiếm
		            </div>
		        </a>
		        <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
				  <a href="<@ofbizUrl>contactus</@ofbizUrl>">
				<#else>
				  <a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">
				</#if>
		            <div class="icon">
		            	<i class="fa fa-phone"></i>
		            </div>
		            <div class="title-404">
		                Liên hệ
		            </div>
		        </a>
		    </div>
		</div>
	
	</div>
</div>