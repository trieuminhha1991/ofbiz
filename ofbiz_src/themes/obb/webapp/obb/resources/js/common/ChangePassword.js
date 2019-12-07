var ChangePassword = (function($){
	var newPasswordlength = 6;
	var bindEvent = function(){
		$('#changepasswordform').submit(function(){
			if(!$(this).jqxValidator('validate')){
				return false;
			}
			return true;
		});
		$('#newPassword').keyup(function(){
			var obj = $(this);
			var pass = obj.val();
			if(pass.length >= newPasswordlength){
				var strength = CommonUtils.checkPassStrength(pass);
				choosenewPasswordMessage(strength);
			}
		});
	};
	var choosenewPasswordMessage = function(strength){
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
	var initValidate = function(){
		if ($(window).width() < 750) {
			$('#changepasswordform').jqxValidator({ position: 'bottom' });
		}
		$('#changepasswordform').jqxValidator({
			rules: [
				{
					input : '#currentPassword',
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
						if(pass1.length >= newPasswordlength){
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
	var init = function(){
		initValidate();
		bindEvent();
	};
	return {
		init : init
	};
})(jQuery);
jQuery(document).ready(function($){
	ChangePassword.init();
});
