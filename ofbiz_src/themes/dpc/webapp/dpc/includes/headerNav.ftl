<script type="text/javascript" src="/./basecrm/images/js/miscUtil.js"></script>
<#assign header = Static["com.olbius.baseecommerce.backend.ContentUtils"].getHeader(delegator, locale) />
<script>
	$(document).ready(function() {
		var header = "${StringUtil.wrapString(header?if_exists)}";
		if (header) {
			Header.render(header);
		}
	});
//	headerEditor.js
	if (typeof (Header) == "undefined") {
		var Header = (function() {
			var render = function(header) {
				var data = header.toJson()['0'];
			};
			return {
				render: render
			}
		})();
	}	
</script>


<header>
	<div class="container">
		<a class="logo" href="<@ofbizUrl>main</@ofbizUrl>"><i class="icon-phuongchinh"></i></a>
		<form id="search">
			<input type="text" value=""  placeholder="Bạn tìm gì..." name="search-keyword" id="search-keyword" class="topinput" />
			<button type="submit" class="btntop">
				<i class="icon-search"></i>
			</button>
		</form>
		<a class="hotline" href="tel:18006666"><i class="icon-phone"></i>1800.6666
		<br />
		<span>Miễn phí cước gọi</span></a>
		<nav>
			<ul class="nav navbar-nav">
				<li class="dropdown">
					<a href="#" class="tpcn"><i class="allicon-tpcn"></i>TP Chức năng</a>
					<ul class="dropdown-menu">
						<li>
							<a href="#" title="">Cơ xương khớp</a>
						</li>
						<li>
							<a href="#" title="">Sinh sản - Sinh lý</a>
						</li>
						<li>
							<a href="#" title="">Thần kinh</a>
						</li>
						<li>
							<a href="#" title="">Tim mạch</a>
						</li>
						<li>
							<a href="#" title="">Tiêu hoá</a>
						</li>
						<li>
							<a href="#" title="">Ung thư</a>
						</li>
						<li>
							<a href="#" title="">Nội tiết</a>
						</li>
					</ul>
				</li>
				<li>
					<a class="my-pham" href="#"><i class="allicon-mypham"></i>Mỹ phẩm</a>
				</li>
				<li>
					<a class="tbyt" href="#"><i class="allicon-tbyt"></i>Thiết bị y tế</a>
				</li>
				<li>
					<a class="tin-tuc" href="<@ofbizUrl>newcontent</@ofbizUrl>"><i class="allicon-tintuc"></i>Tin tức</a>
				</li>
				<li>
					<a class="hoi-dap" href="<@ofbizUrl>ListFAQ</@ofbizUrl>"><i class="allicon-hoidap"></i>Hỏi đáp</a>
				</li>
			</ul>
		</nav>
		<a href="<@ofbizUrl>showcart</@ofbizUrl>"  class="s-cart">
			<i class="allicon-cart"></i><span class="cart-quantity">0</span>Giỏ hàng
		</a>
		<div role="group" class="btn-group store">
			<a aria-expanded="false" role="button" data-toggle="dropdown" class="dropdown-toggle location font-arial" href="#"><i class="allicon-store"></i> Hệ thống chuỗi<span class="caret"></span></a>
			<div role="menu" class="dropdown-menu all-store">
				<div class="topic-html-content">
					<div class="topic-html-content-body">
						<h4>Hệ thống cửa hàng</h4>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
							<h5>HÀ NỘI (7h00 - 19h00)</h5>
							<ul>
								<li>
									<p>
										169 Mai Hắc Đế, Hai Bà Trưng
									</p>
									<p>
										ĐT: <a href="tel:0473003333">04.7300.3333</a>
									</p>
								</li>
							</ul>
						</div>
						<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
							<h5>HỒ CHÍ MINH (8h00 - 20h30)</h5>
							<ul>
								<li>
									<p>
										514 3/2, Phường 14, Quận 10
									</p>
									<p>
										ĐT: <a href="tel:0873001212">08.7300.1212</a>
									</p>
								</li>
								<li>
									<p>
										162 Nguyễn Oanh, P.17, Q. Gò Vấp
									</p>
									<p>
										ĐT:&nbsp;<a href="tel:0873001212">08.7300.1212</a>
									</p>
								</li>
							</ul>
						</div>
						<p class="sp">
							<span class="sp1">Tổng đài tư vấn:&nbsp;<span class="sp2"><a href="tel:18006666">1800.6666</a></span> (miễn phí)
								<br>
							</span>
						</p>
					</div>
				</div>
			</div>
		</div>
		<a class="s-acc" href="<@ofbizUrl>login</@ofbizUrl>"><i class="allicon-acc"></i>Tài khoản</a>
	</div>
</header>