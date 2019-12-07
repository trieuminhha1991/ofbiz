<#-- upgrade from ../create/accountInfo.ftl -->
<div class="step-pane" id="accountInfo">
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="span6">
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.UserLoginID}</label></div>
				<div class="span7"><input type="text" id="wn_acc_userLoginId" tabindex="23" disabled/></div>
			</div>
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.CurrentPassword}</label></div>
				<div class="span7"><input type="password" id="wn_acc_password" tabindex="24"/></div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid margin-top10">
				<div class="span5"><label></label></div>
				<div class="span7"></div>
			</div>
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.CurrentPasswordVerify}</label></div>
				<div class="span7"><input type="password" id="wn_acc_passwordVerify" tabindex="25"/></div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbDistributorNewAccount.init();
	});
	var OlbDistributorNewAccount = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_acc_userLoginId"), {width: "98%", disabled: true});
			jOlbUtil.input.create($("#wn_acc_password"), {width: "98%"});
			jOlbUtil.input.create($("#wn_acc_passwordVerify"), {width: "98%"});
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: "#wn_acc_password", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
				{input : '#wn_acc_password', message: multiLang.BEPasswordShort, action: 'change',
					rule: function(input, label){
						if (UpdateMode) {
							return true;
						}
						if(input.val().length > 4){
							return true;
						}
						return false;
					}
				},
				{input: "#wn_acc_passwordVerify", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
				{input: "#wn_acc_passwordVerify", message: multiLang.BEPasswordNotMatch, action: "change",
					rule: function (input, commit) {
						if (input.val() == $("#wn_acc_password").val()) {
							return true;
						}
						return false;
					}
				}
			];
			var mapRules = [];
			
			validatorVAL = new OlbValidator($("#accountInfo"), mapRules, extendRules, {position: "bottom", scroll: true});
		};
		return {
			init: init,
		};
	}());
</script>