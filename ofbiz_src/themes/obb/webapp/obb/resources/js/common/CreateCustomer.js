var CustomerForm = (function($){
	var passwordlength = 6;
	var showAge = function(){

	};
	var setAge = function(age){
		var d = new Date();
		var year = d.getYear();
		d.setYear(year - age);
	};
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
		spd.jqxDateTimeInput({theme: 'megamall', formatString: "dd/MM/yyyy", width: 'calc(100% - 2px)', height: '33px' });
		spd.jqxDateTimeInput('setDate', null);
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
					input : '#username',
					message: uiLabelMap.BERequired,
					action: 'keyup, blur',
					rule: "required"
				},
				{ input: "#username", message: uiLabelMap.BSUserLoginIdAlreadyExists, action: "change",
					rule: function (input, commit) {
						var userLoginId = input.val();
						if (userLoginId) {
							var check = DataAccess.getData({
								url: "checkUserLoginId",
								data: {userLoginId: userLoginId},
								source: "check"});
							if ("false" == check) {
								 return false;
							}
						}
						return true;
					}
				},{
					input : '#password',
					message: uiLabelMap.BERequired,
					action: 'keyup, blur',
					rule: "required"
				},{
					input : '#password',
					message: uiLabelMap.BEPasswordEqualsUserName,
					action: 'change',
					rule: function(input, label){
						if(input.val() != $("#username").val()){
							return true;
						}
						return false;
					}
				},{
					input : '#password',
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
					input : '#RepeatPassword',
					message: uiLabelMap.BERequired,
					action: 'keyup, blur',
					rule: "required"
				},{
					input : '#RepeatPassword',
					message: uiLabelMap.BEPasswordNotMatch,
					action: 'keyup, blur',
					rule: function(input, label){
						var pass1 = $("#password").val();
						var pass2 = input.val();
						if(pass1 == pass2){
							return true;
						}
						return false;
					}
				},{
					input : '#phone',
					message: uiLabelMap.BERequired,
					action: 'keyup, blur',
					rule: 'required'
				},{
					input : '#phone',
					message: uiLabelMap.BEPhoneInvalid,
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
			]
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
