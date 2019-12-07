var emplInfoAccountObj = (function(){
	var _data = {};
	var init = function(){
		initWindow();
		initEvent();
	};
	var initWindow = function(){
		createJqxWindow($("#infoAccountEmployee"), 820, 350);
	};
	var initEvent = function(){
		$("#btnenabledStatusLock").on('click',function(){
			editDateTimeDisObj.openWindow();
		});
		$("#btnenabledStatusUnLock").on('click',function(){
			bootbox.dialog(uiLabelMap.HRChangeInfoEmplAccount,
					[{
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		 		changeInfoEmplAccount();   	
		    		    }	
					},
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]		
			);
		});
		
		$("#infoAccountEmployee").on('open', function(){
			initOpen();
		});
		$("#infoAccountEmployee").on('close', function(){
			Grid.clearForm($(this));
			_data = {};
		});
		$("#cancelChangeEnableStatus").click(function(event){
			$("#infoAccountEmployee").jqxWindow('close');
		});
	};
	var openWindow = function(data){
		_data = data;
		openJqxWindow($("#infoAccountEmployee"));
	};
	var initOpen = function(){
		Loading.show('loadingMacro');
		$("#viewUserLoginId").html(_data.partyCode);
		$.ajax({
			url: 'getInfoAccountEmpl',
			data: {partyId: _data.partyId},
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				var results = response.results;
				setData(results);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var getData = function(){
		var data = {};
		data.userLoginId = $("#viewUserLoginId").val();
		data.disabledDateTime = new Date().getTime();
		return data;
	};
	var changeInfoEmplAccount = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'changingInfoEmplAccount',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				Grid.renderMessage('jqxgrid', response.results.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				updateViewEmlInf(response.results);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var updateViewEmlInf = function(data){
		if(OlbCore.isNotEmpty(data.enabled)){
			if(data.enabled == "N"){
				$("#enabledStatusAct").html(uiLabelMap.HRLocked);
				$("#enabledStatusAct").css("color","red");
				$("#btnenabledStatusUnLock").html("<i class='fa fa-unlock'></i>"+uiLabelMap.HRUnLock);
				$("#datetimeContainer").show();
				$("#btnenabledStatusLock").hide();
				$("#btnenabledStatusUnLock").show();
			}else{
				$("#enabledStatusAct").html(uiLabelMap.HRUnLocked);
				$("#enabledStatusAct").css("color","green");
				$("#btnenabledStatusLock").html("<i class='fa fa-lock'></i>"+uiLabelMap.HRLock);
				$("#btnenabledStatusUnLock").hide();
				$("#datetimeContainer").hide();
				$("#btnenabledStatusLock").show();
			}
		}else{
			$("#enabledStatusAct").html("");
			$("#enabledStatusAct").css("color","green");
			$("#btnenabledStatusLock").html("<i class='fa fa-lock'></i>"+uiLabelMap.HRLock);
			$("#datetimeContainer").show();
			$("#btnenabledStatusUnLock").hide();
			$("#btnenabledStatusLock").show();
		}
		$("#btDisabledDateTime").html(new Date(parseInt(data.disabledDateTime)).formatDate("dd-MM-yyyy HH:mm:ss"));
	};
	var setData = function(data){
		$("#viewUserLoginId").val(data.userLoginId);
		if(OlbCore.isNotEmpty(data.hasLoggedOut)){
			if(data.hasLoggedOut == "N"){
				$("#viewHasLoggedOut").html(uiLabelMap.HRLoggedIn);
			}else{
				$("#viewHasLoggedOut").html(uiLabelMap.HRLoggedOut);
			}		
		}else{
			$("#viewHasLoggedOut").html("");
		}
		if(OlbCore.isNotEmpty(data.requirePasswordChange)){
			if(data.requirePasswordChange == "N"){
				$("#viewRequirePwdChange").html(uiLabelMap.HRNo);
			}else{
				$("#viewRequirePwdChange").html(uiLabelMap.HRYes);
			}		
		}else{
			$("#viewRequirePwdChange").html("");
		}
		if(OlbCore.isNotEmpty(data.enabled)){
			if(data.enabled == "N"){
				$("#enabledStatusAct").html(uiLabelMap.HRLocked);
				$("#enabledStatusAct").css("color","red");
				$("#btnenabledStatusUnLock").html("<i class='fa fa-unlock'></i>"+uiLabelMap.HRUnLock);
				$("#datetimeContainer").show();
				$("#btnenabledStatusLock").hide();
				$("#btnenabledStatusUnLock").show();
			}else{
				$("#enabledStatusAct").html(uiLabelMap.HRUnLocked);
				$("#enabledStatusAct").css("color","green");
				$("#btnenabledStatusLock").html("<i class='fa fa-lock'></i>"+uiLabelMap.HRLock);
				$("#btnenabledStatusUnLock").hide();
				$("#datetimeContainer").hide();
				$("#btnenabledStatusLock").show();
			}
		}else{
			$("#enabledStatusAct").html("");
			$("#enabledStatusAct").css("color","green");
			$("#btnenabledStatusLock").html("<i class='fa fa-lock'></i>"+uiLabelMap.HRLock);
			$("#datetimeContainer").show();
			$("#btnenabledStatusUnLock").hide();
			$("#btnenabledStatusLock").show();
		}
		$("#viewLastLocale").html(data.lastLocale);
		$("#viewSuccessiveFailedLogins").html(data.successiveFailedLogins);
		$("#viewPartyCode").html(data.partyCode);
		$("#btDisabledDateTime").html(new Date(parseInt(data.disabledDateTime)).formatDate("dd-MM-yyyy HH:mm:ss"));
	};
	return{
		init: init,
		updateViewEmlInf:updateViewEmlInf,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	emplInfoAccountObj.init();
});
