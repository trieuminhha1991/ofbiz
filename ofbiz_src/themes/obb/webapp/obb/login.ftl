<#assign janrainEnabled = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("ecommerce.properties", "janrain.enabled")>
<#assign appName = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("ecommerce.properties", "janrain.appName")>
<div class="account-login">
	<div class="page-title">
		<h1>${uiLabelMap.BELoginOrCreateNew}</h1>
	</div>
	<div class="col2-set">
		<div class="col-1 new-users">
			<div class="content">
				<h2><i class='fa fa-user-md marginright-10'></i>${uiLabelMap.BENewCustomer}</h2>
				<p class='margin-top-15'>
					${uiLabelMap.BENewCustomerProfit}
				</p>
				<div class='row margin-top-15'>
					<div class='col-lg-6'>
					</div>
					<div class='col-lg-6'>
						<button type="button" title="Tạo mới tài khoản" class="button full-width-button pull-right" onclick="window.location='<@ofbizUrl>newcustomer</@ofbizUrl>';">
							<i class="fa fa-user-plus"></i>&nbsp;${uiLabelMap.BECreateAccount}
						</button>
					</div>
					<!-- <div class='col-lg-6' id="fbLogin">
						<button class="fb-login-bt full-width-button">
							<i class="fa fa-facebook-square"></i>&nbsp;${uiLabelMap.BELogin} Facebook
						</button>
						<script type="text/javascript" src="/obbresources/js/facebook/fbLoginSync.js"></script>
					</div> -->
				</div>
			</div>
		</div>
		<div class="col-2 registered-users">
		<#include "/obb/webapp/obb/includes/common/messageError.ftl"/>
			<div class="content">
				<div id="login-form" class='login-form'>
					<h2><i class='fa fa-lock marginright-10'></i>${uiLabelMap.BELogin}</h2>
					<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform" id="loginform" class="login">
						<input type="hidden" name="isSubmit" value="Y" />
						<div class="row margin-top-15">
							<div class='col-lg-3 col-md-3'>
								<label for="username" class="required">${uiLabelMap.BEUsername}</label>
							</div>
							<div class='col-lg-9 col-md-9'>
								<input type="text" name="USERNAME" value="" id="username" class="full-width-input" placeholder="${uiLabelMap.BEUsername}">
							</div>
						</div>
						<div class="row margin-top-15">
							<div class='col-lg-3 col-md-3'>
								<label for="email" class="required">${uiLabelMap.BEPassword}</label>
							</div>
							<div class='col-lg-9 col-md-9'>
								<input type="password" name="PASSWORD" class="full-width-input" id="pass" placeholder="${uiLabelMap.BEPassword}">
							</div>
						</div>
						<div class='row margin-top-15'>
							<div class='col-lg-3 col-md-3'>
							
							</div>
							<div class='col-lg-9 col-md-9'>
								<div class='row'>
									<div class='col-lg-6'>
										<p class="required">
											<a href="javascript:forgotPassword()" id="forgot-password">
												<i class='fa fa-key'>&nbsp;</i>&nbsp;${uiLabelMap.BEForgotPassword}?
											</a>
										</p>
									</div>
									<div class='col-lg-6'>
										<button type="submit" class="button pull-right" title="Đăng nhập" name="send" id="send2">
											${uiLabelMap.BELogin}
										</button>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div id="forgot-password-form" style="display: none;">
				<form method="post" name="forgotpassword" id="forgotpassword" action="<@ofbizUrl>forgotpassword</@ofbizUrl>" class="horizontal">
					<div style="border-top:1px solid #ebebeb;">
						<div class="content">
							<h2><i class='fa fa-refresh marginright-10'></i>&nbsp;${uiLabelMap.BEForgotPassword}</h2>
							<div class="row margin-top-15">
								<div class='col-lg-2 col-md-2'>
									<label for="username2" class='pull-right'>${uiLabelMap.BEUsername}</label>
								</div>
								<div class='col-lg-5 col-md-5'>
									<input type="hidden" name="EMAIL_PASSWORD" value="Y" />
									<input type="text" name="USERNAME"
										value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"
										id="username2" class="full-width-input" title="${uiLabelMap.BEEmail}">
								</div>
								<div class='col-lg-5 col-md-5'>
									<button type="submit" class="button" title="${uiLabelMap.BSPasswordRetrieval}" name="EMAIL_PASSWORD" class='full-width-button'>
										${uiLabelMap.BSPasswordRetrieval}
									</button>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script language="JavaScript" type="text/javascript">
	<#if autoUserLogin?has_content>document.loginform.PASSWORD.focus();</#if>
	<#if !autoUserLogin?has_content>document.loginform.USERNAME.focus();</#if>
	function forgotPassword() {
		$("#username2").val($("#username").val());
		$("#login-form").slideUp(400);
		$("#forgot-password-form").slideDown(400);
	}
</script>
