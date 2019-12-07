var CreateNewUserLogin = (function(){
	var _partyId = null;
	var init = function(){
		initJqxWindow();
		initJqxInput();
		initEvent();
		initJqxValidator();
		initBtnEvent();
	};
	var initBtnEvent = function(){
		
		$('#cancelCreateUserButton').click(function(){
			$('#CreateNewUserLoginWindow').jqxWindow('close');
		});
		$("#saveCreateUserButton").click(function(){
			$('#CreateNewUserLoginWindow').jqxValidator('validate');
			if($('#CreateNewUserLoginWindow').jqxValidator('validate')){
				var data = {
						userLoginId : $('#UserName').val(),
						currentPassword : $('#Password').val(),
						currentPasswordVerify : $('#Password').val(),
						partyId : _partyId,
				};
				bootbox.dialog(uiLabelMap.HRUpdateConfirm, [{
					"label"   : uiLabelMap.CommonNo,
		            "icon"    : 'fa fa-remove',
		            "class"   : 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {
		            	bootbox.hideAll();
		            }
				},{
					"label"   : uiLabelMap.CommonYes,
		            "icon"    : 'fa-check',
		            "class"   : 'btn btn-primary form-action-button pull-right',
		            "callback": function(){
		            	$.ajax({
		            		type : 'POST',
		            		url : 'createNewUserLogin',
		            		data : data,
		            		dataType: 'json',
		            		success : function(data){
		            			if(data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_){
		            				var compare_String = uiLabelMap.EmployeeHasHadUserLogin + ';' + uiLabelMap.HRCommonAccount + ":";
		            				if(data._ERROR_MESSAGE_.indexOf(compare_String.trim()) > -1){
		            					$('#CreateNewUserLoginWindow').jqxWindow('close');
		            					$("#updateNotification").jqxNotification('closeLast');
		                				$("#notificationText").text(data._ERROR_MESSAGE_);
		            					$("#updateNotification").jqxNotification({ template: 'error' });
		            					$("#updateNotification").jqxNotification('open');
		            				}else{
		            					bootbox.dialog(data._ERROR_MESSAGE_, [{
		            						"label"   : uiLabelMap.CommonNo,
		            			            "icon"    : 'fa fa-remove',
		            			            "class"   : 'btn  btn-danger form-action-button pull-right',
		            			            "callback": function() {
		            			            	bootbox.hideAll();
		            			            }
		            					},{
		            						"label"   : uiLabelMap.CommonYes,
		            			            "icon"    : 'fa-check',
		            			            "class"   : 'btn btn-primary form-action-button pull-right',
		            			            "callback": function() {
	            			            		var info = {
	            			            				partyId : $('#EmployeeId').val(),
	            			            		};
	            			            		$.ajax({
	            			            			type : 'POST',
	            			            			url : 'reEnableUserLogin',
	            			            			data : info,
	            			            			dataType : 'json',
	            			            			success : function(data){
	            			            				$('#CreateNewUserLoginWindow').jqxWindow('close');
	            			            				$("#updateNotification").jqxNotification('closeLast');
	            			            				$("#notificationText").text(uiLabelMap.EnabledSuccess);
	            			            				$("#updateNotification").jqxNotification({ template: 'info' });
	            			            				$("#updateNotification").jqxNotification('open');
	            			            			}
	            			            		})
		            			           	}
		            					}])
		            				}
		            			}else{
		                			$('#CreateNewUserLoginWindow').jqxWindow('close');
		            				$("#updateNotification").jqxNotification('closeLast');
		            				$("#notificationText").text(data._EVENT_MESSAGE_);
		            				$("#updateNotification").jqxNotification({ template: 'info' });
		            				$("#updateNotification").jqxNotification('open');
		            			}
		            		}
		            	})
		            }
				}])
			}else{
				return false;
			}
		})
	}
	
	var initJqxWindow = function(){
		createJqxWindow($("#CreateNewUserLoginWindow"), 400, 300);
	};
	
	var initJqxInput = function(){
		$('#EmployeeId').jqxInput({width : 210, height : 18, disabled : true});
		$('#EmployeeName').jqxInput({width : 210, height : 18, disabled : true});
		$('#OrganizationalUnit').jqxInput({width : 210, height : 18, disabled : true});
		$('#UserName').jqxInput({width : 210, height : 18});
		$('#Password').jqxInput({width : 210, height : 18});
	};
	
	var initEvent = function(){
		$('#CreateNewUserLoginWindow').on('open', function(){
			setDefaultData();
		});
		$('#CreateNewUserLoginWindow').bind('close', function(){
			$('#CreateNewUserLoginWindow').jqxValidator('hide');
			$('#EmployeeId').jqxInput('val', null);
			$('#EmployeeName').jqxInput('val', null);
			$('#UserName').jqxInput('val', null);
			$('#Password').jqxInput('val', null);
			$('#UserName').jqxInput({disabled : false});
			$('#saveCreateUserButton').attr("disabled", false);
			$('#labelPassword').text(uiLabelMap.CommonPassword);
//			$('#editPassword').css('visibility', 'hidden');
		});
	};
	
	var setDefaultData = function(data){
		var index = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var data = $('#jqxgrid').jqxGrid('getrowdata', index);
		$('#EmployeeName').jqxInput('val', data.fullName);
		$('#EmployeeId').jqxInput('val', data.partyCode);
		_partyId = data.partyId;
		$('#OrganizationalUnit').jqxInput('val', globalVar.rootPartyArr[0].partyName);
		if(data.userLoginId != ""){
			$('#UserName').jqxInput({disabled : true});
			$('#UserName').jqxInput('val', data.userLoginId);
			$('#labelPassword').text(uiLabelMap.HRChangePassword);
		}
	};
	
	var initJqxValidator = function(){
		$('#CreateNewUserLoginWindow').jqxValidator({
			rules : [
			         {
			        	 input : '#UserName', message : uiLabelMap.CommonRequired, action : 'blur', rule : 'required',
			         },
			         {
			        	 input : '#Password', message : uiLabelMap.CommonRequired, action : 'blur', rule : 'required',
			         },
			         {
			        	 input : '#UserName', message : uiLabelMap.MustntHaveSpaceChar, action : 'blur',
			        	 rule : function(){
			        		 var space = " ";
			        		 if($('#UserName').val().indexOf(space) > -1){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
	         ]
		});
	};
	
	return{
		init : init,
		setDefaultData : setDefaultData,
	}
}())
$(document).ready(function(){
	CreateNewUserLogin.init();
})

