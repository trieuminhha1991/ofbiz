<script type="text/javascript" src="/obbresources/js/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/obbresources/js/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/obbresources/js/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/obbresources/js/jqwidgets/globalization/globalize.js"></script>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>

<div class="account-create">
	<h1 class='account-title'><i class='fa fa-user'></i>&nbsp;&nbsp;${uiLabelMap.CommonPasswordChange}</h1>
	<div class="account-form">
		<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform" id="loginform" class='createUserForm'>
			<input type="hidden" name="requirePasswordChange" value="Y"/>
			<input type="hidden" name="isSubmit" value="Y" />
			<input type="hidden" name="USERNAME" value="${username}"/>
			<div class='row no-margin-bottom'>
				<div class="row">
					<div class='col-lg-6 col-md-6'>
						<div class="row">
							<div class="col-lg-12">
								<div class='row'>
									<div class="col-lg-4 col-md-5">
										<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEUsername}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
									</div>
									<div class="col-lg-8 col-md-7">
										<label class='title'>${username}</label>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='col-lg-6 col-md-6'>
					
					</div>
				</div>
				
				<div class="row">
					<div class='col-lg-6 col-md-6'>
						<div class="row">
							<div class="col-lg-12">
								<div class='row'>
									<div class="col-lg-4 col-md-5">
										<label class='title pull-right mobile-pull-left'>${uiLabelMap.CommonCurrentPassword}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
									</div>
									<div class="col-lg-8 col-md-7">
										<div class="input-container">
											<input class="full-width-input" type="password" name="PASSWORD" id="PASSWORD" value=""/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='col-lg-6 col-md-6'>
					
					</div>
				</div>
				
				<div class="row">
					<div class='col-lg-6 col-md-6'>
						<div class="row">
							<div class="col-lg-12">
								<div class='row'>
									<div class="col-lg-4 col-md-5">
										<label class='title pull-right mobile-pull-left'>${uiLabelMap.CommonNewPassword}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
									</div>
									<div class="col-lg-8 col-md-7">
										<div class="input-container">
											<input class="full-width-input" type="password" name="newPassword" id="newPassword" value=""/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='col-lg-6 col-md-6'>
						<div class="row">
							<div class="col-lg-12">
								<div class='row'>
									<div class="col-lg-4 col-md-5">
										<label class='title pull-right mobile-pull-left'>${uiLabelMap.CommonNewPasswordVerify}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
									</div>
									<div class="col-lg-8 col-md-7">
										<div class="input-container">
											<input class="full-width-input" type="password" name="newPasswordVerify" id="newPasswordVerify" value=""/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class='pull-right clearfix'>
					<button class='btn-submit'>
						<i class='fa fa-check'>&nbsp;</i>&nbsp;${uiLabelMap.BESubmit}
					</button>
				</div>
			</div>
		</form>
	</div>
</div>


<script language="JavaScript" type="text/javascript">
	$(document).ready(function() {
		document.loginform.PASSWORD.focus();
		ChangePass.init();
	});
	var ChangePass = (function($) {
		var passwordlength = 6;
		var bindEvent = function(){
			$('#loginform').submit(function(){
				if(!$(this).jqxValidator('validate')){
					return false;
				}
				return true;
			});
			$('#newPassword').keyup(function(){
				var obj = $(this);
				var pass = obj.val();
				if(pass.length >= passwordlength){
					var strength = CommonUtils.checkPassStrength(pass);
					choosePasswordMessage(strength);
				}
			});
		};
		var choosePasswordMessage = function(strength){
			var pass = $('#newPassword');
			switch(strength){
				case "strong":
					CommonUtils.renderMessage(pass, uiLabelMap.BEPasswordStrong, 'passstrong');
					break;
				case "weak":
					CommonUtils.renderMessage(pass, uiLabelMap.BEPasswordWeak, 'passweek');
					break;
				case "good":
					CommonUtils.renderMessage(pass, uiLabelMap.BEPasswordWell, 'passstrong');
					break;
				case "":
					CommonUtils.renderMessage(pass, uiLabelMap.BEPasswordTooWeak, 'passtooweak');
					break;
			}
		};
		var initValidator = function() {
			if ($(window).width() < 750) {
				$('#loginform').jqxValidator({ position: 'bottom' });
			}
			$('#loginform').jqxValidator({
				rules: [
					{
						input : '#PASSWORD',
						message: uiLabelMap.BERequired,
						action: 'keyup, blur',
						rule: "required"
					},{
						input : '#newPassword',
						message: uiLabelMap.BERequired,
						action: 'keyup, blur',
						rule: "required"
					},{
						input : '#newPassword',
						message: uiLabelMap.BEPasswordLength,
						action: 'keyup, blur',
						rule: function(input, label){
							var pass1 = input.val();
							if(pass1.length >= passwordlength){
								return true;
							}
							return false;
						}
					},{
						input : '#newPasswordVerify',
						message: uiLabelMap.BERequired,
						action: 'keyup, blur',
						rule: "required"
					},{
						input : '#newPasswordVerify',
						message: uiLabelMap.BEPasswordNotMatch,
						action: 'keyup, blur',
						rule: function(input, label){
							var pass1 = $("#newPassword").val();
							var pass2 = input.val();
							if(pass1 == pass2){
								return true;
							}
							return false;
						}
					}
				]
			});
		};
		return {
			init: function() {
				bindEvent();
				initValidator();
			}
		};
	})(jQuery);
</script>
