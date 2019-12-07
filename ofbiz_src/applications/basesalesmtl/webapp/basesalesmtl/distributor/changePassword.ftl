<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<style>
	.row-fluid {
	    min-height: 40px;
	}
</style>

<div class="row-fluid" id="formChangePassword">
	<div class="span6">
		<div class='row-fluid margin-bottom10'>
			<div class='span5 text-algin-right'>
				<label class="asterisk">${uiLabelMap.CommonCurrentPassword}</label>
			</div>
			<div class="span7">
				<input type="password" id="currentPassword"/>
			</div>
		</div>
		<div class='row-fluid margin-bottom10'>
			<div class='span5 text-algin-right'>
				<label class="asterisk">${uiLabelMap.CommonNewPassword}</label>
			</div>
			<div class="span7">
				<input type="password" id="newPassword"/>
			</div>
		</div>
	</div>
	<div class="span5">
		<div class='row-fluid margin-bottom10'></div>
		<div class='row-fluid margin-bottom10'>
			<div class='span5 text-algin-right'>
				<label class="asterisk">${uiLabelMap.CommonNewPasswordVerify}</label>
			</div>
			<div class="span7">
				<input type="password" id="verifyNewPassword"/>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div class="span12">
		<button type="button" class="btn btn-primary form-action-button pull-right" id="saveChangePw"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested"></div>
</div>

<script>
	$(document).ready(function() {
		ChangePassword.init();
	});
    var ChangePassword = (function() {
    	var initJqxElements = function() {
    		$("#jqxNotificationNested").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
    	var handleEvents = function() {
    		$("#saveChangePw").click(function(event){
    			if($("#formChangePassword").jqxValidator('validate')){
    				DataAccess.execute({
						url: "updateEmplPassword",
						data: {
							currentPassword: $("#currentPassword").val(),
							newPassword: $("#newPassword").val(),
							newPasswordVerify: $("#verifyNewPassword").val()}
						}, notify);
    			}
    		});
		};
		var initValidator = function() {
			$('#formChangePassword').jqxValidator({
				rules : [{input : '#currentPassword',message : multiLang.fieldRequired , action : 'blur', rule : 'required'},
				         {input : '#newPassword',message : multiLang.fieldRequired, action : 'blur', rule : 'required'},
				         { input: "#verifyNewPassword", message: multiLang.fieldRequired, action: 'keyup, blur', rule: 'required' },
	                     { input: "#verifyNewPassword", message: multiLang.BEPasswordNotMatch, action: 'change', 
	                         rule: function (input, commit) {
	                             var firstPassword = $("#newPassword").val();
	                             var secondPassword = $("#verifyNewPassword").val();
	                             return firstPassword == secondPassword;
	                         }
	                     }]
			});
		};
		var notify = function(res) {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if(res["errorMessageList"] || res["errorMessage"]){
				var errormes = "";
				res["errorMessage"]?errormes=res["errorMessage"]:errormes=res["errorMessageList"];
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
		      	$("#notificationContentNested").text(errormes);
		      	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
		      	$("#notificationContentNested").text(multiLang.updateSuccess);
		      	$("#jqxNotificationNested").jqxNotification("open");
		      	setTimeout(function() {
		      		location.href = "logout";
				}, 1000);
			}
		};
    	return {
    		init: function() {
    			initJqxElements();
    			handleEvents();
    			initValidator();
			}
    	};
	})();
</script>