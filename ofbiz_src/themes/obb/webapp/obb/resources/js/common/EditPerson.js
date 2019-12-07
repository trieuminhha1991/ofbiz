var CustomerForm = (function($){
	var passwordlength = 6;
	var bindEvent = function(){
		$('#newuserform').submit(function(){
			if(!$(this).jqxValidator('validate')){
				return false;
			}
			var d = $('#birthDateSelect').jqxDateTimeInput("getDate");
			if(d){
				$('#birthDate').val(d.getTime());
			}
			return true;
		});
		$('#password').keyup(function(){
			var obj = $(this);
			var pass = obj.val();
			if(pass.length >= passwordlength){
				var strength = CommonUtils.checkPassStrength(pass);
				choosePasswordMessage(strength);
			}
		});
	};
	var choosePasswordMessage = function(strength){
		var pass = $('#password');
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
	var initElement = function(){
		var spd = $("#birthDateSelect");
		spd.jqxDateTimeInput({theme: 'megamall', formatString: "dd/MM/yyyy", width: 'calc(100% - 2px)', height: '35px' });
		if ($('#birthDate').val()) {
			spd.jqxDateTimeInput('setDate', new Date($('#birthDate').val().toInt()));
		} else {
			spd.jqxDateTimeInput('setDate', null);
		}
	};
	var initValidate = function(){
		if ($(window).width() < 750) {
			$('#newuserform').jqxValidator({ position: 'bottom' });
		}
		$('#newuserform').jqxValidator({
			rules: [
				{
					input : '#fullName',
					message: uiLabelMap.BERequired,
					action: 'keyup, blur',
					rule: "required"
				},{
					input : '#phone',
					message: uiLabelMap.BERequired,
					action: 'blur',
					rule: 'required'
				},{
					input : '#phone',
					message: uiLabelMap.BEPhoneEmailInvalid,
					action: 'keyup, blur',
					rule: function(input, label){
						var val = input.val();
						return CommonUtils.validatePhone(val);
					}
				},{
					input : '#email',
					message: uiLabelMap.BEEmailInvalid,
					action: 'keyup, blur',
					rule: "email"
				}
			],
			position: 'bottom'
		});
	};
	var init = function(){
		initElement();
		initValidate();
		bindEvent();
		ChangeDistrict.init($("#city"), $("#district"));
	};
	return {
		init : init
	};
})(jQuery);
jQuery(document).ready(function($){
	CustomerForm.init();
});
