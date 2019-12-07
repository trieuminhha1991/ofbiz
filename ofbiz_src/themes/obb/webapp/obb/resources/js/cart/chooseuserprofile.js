var Profile = (function($){
	var guid = CommonUtils.getWindowGUID();
	var getEmail = function(){
		var val = $('input[name="contact"]').val();
		return val;
	};
	var getPassword = function(){
		var val = $('#password').val();
		return val;
	};
	var renderMessage = function(obj, msg){
		obj.jqxTooltip({ content: msg, position: 'right', autoHide: true, trigger: "none", closeOnClick: true, theme:'validation', autoHideDelay : 5000 });
		obj.jqxTooltip('open');
	};
	var prepareSubmitInfo = function(){
		$('#NoLoginForm').submit(function(){
			var email = getEmail();
			if(!CommonUtils.validateEmail(email) && !CommonUtils.validatePhone(email)){
				renderMessage($('input[name="contact"]'), uiLabelMap.BEPhoneEmailInvalid);
				return false;
			}
			if(CommonUtils.validateEmail(email)){
				LocalStorage.setItem(partyId, "userEmail", email);
				$('#NoLoginForm input[name="email"]').val(email);
			}else if(CommonUtils.validatePhone(email)){
				LocalStorage.setItem(partyId, "userPhone", email);
				$('#NoLoginForm input[name="phone"]').val(email);
			}
			return true;
		});
	};
	var prepareLoginForm = function(){
		$('#LoginForm').submit(function(){
			var email = getEmail();
			if(!CommonUtils.validateEmail(email)){
				return false;
			}
			var password = getPassword();
			$('#LoginForm input[name="USERNAME"]').val(email);
			$('#LoginForm input[name="PASSWORD"]').val(password);
			return true;
		});
	};
	var showRegisterButton = function(){
		$('#NoLoginForm').show();
		$('#LoginForm').hide();
	};
	var showLoginButton = function(){
		$('#LoginForm').show();
		$('#NoLoginForm').hide();
	};
	var disablePassword = function(){
		var obj = $('input[name="password"]');
		obj.addClass('disabled');
		obj.attr('disabled', 'disabled');
	};
	var enabledPassword = function(){
		$('input[name="password"]').removeClass('disabled');
		obj.removeClass('disabled');
		obj.removeAttr('disabled');
	};
	var bindEvent = function(){
		$('.customer-chosen').change(function(){
			var val = $(this).val();
			if(val == "EXIST"){
				showLoginButton();
			}else if(val == "NEW"){
				showRegisterButton();
			}
		});
		$('.customer label').click(function(){
			var inp = $(this).siblings();
			inp.click();
			var sib = inp.prop('checked', 'checked');
		});
		$('input[name="contact"]').change(function(){
			var x = $(this).val();
		});
		prepareSubmitInfo();
	};
	var setContactValue = function(){
		var email = LocalStorage.getItem(partyId, "userEmail");
		var phone = LocalStorage.getItem(partyId, "userPhone");
		if(CommonUtils.validateEmail(email)){
			$('input[name="contact"]').val(email);
		}else if(CommonUtils.validatePhone(phone)){
			$('input[name="contact"]').val(phone);
		}
	};
	var init = function(){
		bindEvent();
		setContactValue();
	};
	return {
		init : init
	};
})(jQuery);
jQuery(document).ready(function($){
	Profile.init();
});
