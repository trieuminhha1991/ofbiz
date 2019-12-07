<#assign janrainEnabled = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("ecommerce.properties", "janrain.enabled")>
<#assign appName = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("ecommerce.properties", "janrain.appName")>

<div class='container'>
	<h1>Chào bạn, xin đăng nhập!</h1>
	<div class="wrap-login-form">
		<div class="row">
			<div class="col-lg-5 col-md-5 col-sm-6 col-xs-12">
				<div class="login">
					<h2>Chào mừng bạn, khách hàng của chúng tôi!</h2>
					<div class="form-fields">
						<form action="<@ofbizUrl>login</@ofbizUrl>" method="post" name="loginform">
							<div class="message-error">

							</div>
							<div class="form-group font-arial">
								<label class="col-sm-3 control-label" for="Email">Địa chỉ email:</label>
								<div class="col-sm-9">
									<#if autoUserLogin?has_content>
									<p>
										(${uiLabelMap.CommonNot} ${autoUserLogin.userLoginId}? <a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)
									</p>
									</#if>
									<input autofocus="autofocus" class="form-control" type="text" id="userName" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>">
								</div>
							</div>
							<div class="form-group font-arial">
								<label class="col-sm-3 control-label" for="password">${uiLabelMap.CommonPassword}:</label>
								<div class="col-sm-9">
									<input class="form-control" id="password" name="PASSWORD" type="password">
									<span class="field-validation-valid" data-valmsg-for="Password" data-valmsg-replace="true"></span>
								</div>
							</div>
							<div class="col-sm-3 form-group">
								<input data-val="true" data-val-required="'Remember Me' must not be empty." id="RememberMe" name="RememberMe" type="checkbox" value="true">
								<input name="RememberMe" type="hidden" value="false">
								<label for="RememberMe">Lưu?</label>
							</div>
							<div class="col-sm-4 form-group">
								${uiLabelMap.CommonForgotYourPassword}
							</div>
							<div class="col-sm-5 form-group">
								<input class="btn btn-login" type="submit" value="${uiLabelMap.CommonLogin}"/>
							</div>
						</form>
					</div>
				</div>
			</div>
			<div class="col-lg-7 col-md-7 col-sm-6 col-xs-12">
				<div class="register">
					<div class="new-wrapper register-block">
						<div class="title">
							<h2>Khách hàng mới</h2>
						</div>
						<div class="text">
							Đăng ký 1 tài khoản mới giúp bạn mua hàng nhanh hơn, cập nhập thông tin đơn hàng mới nhất, và theo dõi các đơn hàng đã đặt.
						</div>
						<div class="buttons">
							<form method="post" action="<@ofbizUrl>newcustomer</@ofbizUrl>">
								<label for="newcustomer_submit">${uiLabelMap.CommonMayCreateNewAccountHere}:</p>
									<input type="submit" class="button" id="newcustomer_submit" value="${uiLabelMap.CommonMayCreate}"/>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


<script language="JavaScript" type="text/javascript">
	<#if autoUserLogin?has_content>document.loginform.PASSWORD.focus();</#if>
	<#if !autoUserLogin?has_content>document.loginform.USERNAME.focus();</#if>
</script>
