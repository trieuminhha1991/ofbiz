var addNewEmplGeneralInfo = (function(){
	var _imageAvatar = null;
	var init = function(){
		initJqxInput();
		initJqxDateTimeInput();
		initJqxDropDownList();
		initJqxValidator();
		initEmplAvatar();
		initPopupAddNew();
	};
	var initJqxInput = function(){		
		$('#employeeId').jqxInput({width : '95%',height : '20px'});
		$('#lastName').jqxInput({width : '95%',height : '20px'});
		$('#middleName').jqxInput({width : '95%',height : '20px'});
		$('#firstName').jqxInput({width : '95%',height : '20px'});
		$('#nativeLandInput' + globalVar.defaultSuffix).jqxInput({width : '95%',height : '20px'});
		$("#editIdNumber" + globalVar.defaultSuffix).jqxInput({width : '95%', height : '20px'});
	};
	
	var initJqxDateTimeInput = function(){
		$("#birthDate" + globalVar.defaultSuffix).jqxDateTimeInput({width: '97%', height: '25px',});
		$("#idIssueDateTime" + globalVar.defaultSuffix).jqxDateTimeInput({width: '97%', height: '25px',});
		$("#idIssueDateTime" + globalVar.defaultSuffix).val(null);
		$("#birthDate" + globalVar.defaultSuffix).val(null);
	};
	
	var initJqxDropDownList = function(){		
		createJqxDropDownList(globalVar.genderList, $("#gender" + globalVar.defaultSuffix), 'genderId', 'description', 25, '97%');
		$("#gender" + globalVar.defaultSuffix).jqxDropDownList({selectedIndex: 0});
		createJqxDropDownList(globalVar.geoArr, $("#idIssuePlaceDropDown" + globalVar.defaultSuffix), "geoId", "geoName", 25, "97%");
		$("#idIssuePlaceDropDown" + globalVar.defaultSuffix).jqxDropDownList({dropDownHeight: 180});
		createJqxDropDownListBinding($("#ethnicOriginDropdown" + globalVar.defaultSuffix), [{name: 'ethnicOriginId'}, {name: 'description'}],
				'getEthnicOriginList', "listReturn", "ethnicOriginId", "description", "83%", 25);
		createJqxDropDownListBinding($("#religionDropdown" + globalVar.defaultSuffix), [{name: 'religionId'}, {name: 'description'}],
				'getReligionList', "listReturn", "religionId", "description", "83%", 25);
		createJqxDropDownListBinding($("#nationalityDropdown" + globalVar.defaultSuffix), [{name: 'nationalityId'}, {name: 'description'}],
				'getNationalityList', "listReturn", "nationalityId", "description", "83%", 25);
		
		createJqxDropDownList(globalVar.maritalStatusList, $("#maritalStatusDropdown" + globalVar.defaultSuffix), "maritalStatusId", "description", 25, "97%");
		
	};
	
	var getData = function(){
		var data = {
				partyCode: $('#employeeId').val(),
				lastName: $('#lastName').val(),
				middleName: $('#middleName').val(),
				firstName: $('#firstName').val(),
				nativeLand: $('#nativeLandInput' + globalVar.defaultSuffix).val(),
				idNumber: $("#editIdNumber" + globalVar.defaultSuffix).val(),
				gender: $("#gender" + globalVar.defaultSuffix).val(),
				idIssuePlace: $("#idIssuePlaceDropDown" + globalVar.defaultSuffix).val(),
				ethnicOrigin: $("#ethnicOriginDropdown" + globalVar.defaultSuffix).val(),
				religion: $("#religionDropdown" + globalVar.defaultSuffix).val(),
				nationality: $("#nationalityDropdown" + globalVar.defaultSuffix).val(),
				maritalStatusId: $("#maritalStatusDropdown" + globalVar.defaultSuffix).val()
		};
		if($("#birthDate" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date')){
			data.birthDate = $("#birthDate" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date').getTime();
		}
		if($("#idIssueDateTime" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date')){
			data.idIssueDate = $("#idIssueDateTime" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date').getTime();
		}
		var statusId = $("#statusId" + globalVar.defaultSuffix).val();
		if(statusId){
			data.workingStatusId = statusId;
			if(statusId != 'EMPL_WORKING'){
				data.terminationReasonId = $("#reasonResign" + globalVar.defaultSuffix).val();
				data.dateTermination = $("#resignDate" + globalVar.defaultSuffix).jqxDateTimeInput('val', 'date').getTime();
			}
		}
		return data;
	};
	
	var resetData = function(){
		$("#employeeId").val("");
		$("#firstName").val("");
		$("#middleName").val("");
		$("#lastName").val("");
		$("#nativeLandInput" + globalVar.defaultSuffix).val("");
		$("#gender" + globalVar.defaultSuffix).jqxDropDownList({selectedIndex: 0});
		$("#editIdNumber" + globalVar.defaultSuffix).val("");
		$("#idIssueDateTime" + globalVar.defaultSuffix).val(null);
		$("#birthDate" + globalVar.defaultSuffix).val(null);
		$("#idIssuePlaceDropDown" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#ethnicOriginDropdown" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#religionDropdown" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#nationalityDropdown" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#maritalStatusDropdown" + globalVar.defaultSuffix).jqxDropDownList('clearSelection');
		$("#avatar" + globalVar.defaultSuffix).attr('src', '/aceadmin/assets/avatars/no-avatar.png');
		_imageAvatar = null;
	};
	
	var hideValidate = function(){
		$("#" + globalVar.profileInfoDiv).jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
			$("#" + globalVar.profileInfoDiv).jqxValidator({
				scroll: false,
				rules: [
				        {
				        	input: '#employeeId',
				        	message: uiLabelMap.FieldRequired,
				        	action: 'keyup, blur',
				        	rule : function(input, commit){
				        		var val = input.val();
				        		if(!val){
				        			return false;
				        		}
				        		return true;
				        	}
				        },
                        {
                            input: '#employeeId',
                            message: uiLabelMap.HRCharacterIsNotValid,
                            action: 'keyup, blur',
                            rule : function(input, commit){
                                return checkRegex(input.val(),uiLabelMap.HRCheckId);
                            }
                        },
				        {
				        	input: '#firstName',
				        	message: uiLabelMap.FieldRequired,
				        	action: 'keyup, blur',
				        	rule : function(input, commit){
				        		var val = input.val();
				        		if(!val){
				        			return false;
				        		}
				        		return true;
				        	}
				        },
                        {
                            input: '#firstName',
                            message: uiLabelMap.HRCharacterIsNotValid,
                            action: 'keyup, blur',
                            rule : function(input, commit){
                                return checkRegex(input.val(),uiLabelMap.HRCheckName);
                            }
                        },
				        {
				        	input: '#lastName',
				        	message: uiLabelMap.FieldRequired,
				        	action: 'keyup, blur',
				        	rule : function(input, commit){
				        		var val = input.val();
				        		if(!val){
				        			return false;
				        		}
				        		return true;
				        	}
				        },
                        {
                            input: '#lastName',
                            message: uiLabelMap.HRCharacterIsNotValid,
                            action: 'keyup, blur',
                            rule : function(input, commit){
                                return checkRegex(input.val(),uiLabelMap.HRCheckName);
                            }
                        },
                        {
                            input: '#middleName',
                            message: uiLabelMap.HRCharacterIsNotValid,
                            action: 'keyup, blur',
                            rule : function(input, commit){
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRegex(input.val(),uiLabelMap.HRCheckFullName);
                            }
                        },
				        {
				        	input : "#birthDate" + globalVar.defaultSuffix,
				        	message : uiLabelMap.LTCurrentDateRequired,
				        	action : 'change, blur',
				        	rule : function(input, commit){
				        		var now = new Date();
				        		if(input.jqxDateTimeInput('getDate') > now){
									return false;
								}
								return true;
				        	}
				        },
				        {
				        	input : "#birthDate" + globalVar.defaultSuffix,
				        	message : uiLabelMap.BirthDateBefIdentifyCardDay,
				        	action : 'blur',
				        	rule : function(input, commit){
				        		if($(input).jqxDateTimeInput('getDate')){
				        			if($('#idIssueDateTime' + globalVar.defaultSuffix).val() && input.jqxDateTimeInput('getDate') > $('#idIssueDateTime' + globalVar.defaultSuffix).jqxDateTimeInput('getDate')){
										return false;
									}
				        		}
								return true;
				        	}
				        },
				        {
				        	input : "#birthDate" + globalVar.defaultSuffix,
				        	message : uiLabelMap.FieldRequired,
				        	action : 'blur',
				        	rule : function(input,commit){
				        		if($('#idIssueDateTime' + globalVar.defaultSuffix).val() && !input.val()){
									return false;
								}
								return true;
				        	}
				        },
                        {
                            input: '#nativeLandInput' + globalVar.defaultSuffix,
                            message: uiLabelMap.HRCharacterIsNotValid,
                            action: 'keyup, blur',
                            rule : function(input, commit){
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRegex(input.val(),uiLabelMap.HRCheckAddress);
                            }
                        },
				        {
				        	input : "#editIdNumber" + globalVar.defaultSuffix,
                            message: uiLabelMap.HRIdCardIsNotValid,
                            action: 'keyup, blur',
                            rule : function(input, commit){
                                if(OlbCore.isEmpty(input.val())){
                                    return true;
                                }
                                return checkRegex(input.val(),uiLabelMap.HRCheckIdCard);
                            }
				        },
                        {
                            input : "#idIssueDateTime" + globalVar.defaultSuffix,
                            message : uiLabelMap.LTCurrentDateRequired,
                            action : 'change, blur',
                            rule : function(input, commit){
                                var now = new Date();
                                if(input.jqxDateTimeInput('getDate') > now){
                                    return false;
                                }
                                return true;
                            }
                        },
				        {
				        	input : "#idIssueDateTime" + globalVar.defaultSuffix,
				        	message : uiLabelMap.IdentifyDayGreaterBirthDate,
				        	action : 'blur',
				        	rule : function(input,commit){
				        		if($(input).jqxDateTimeInput('getDate')){
				        			if($("#birthDate" + globalVar.defaultSuffix).jqxDateTimeInput('getDate') && input.jqxDateTimeInput('getDate') < $("#birthDate" + globalVar.defaultSuffix).jqxDateTimeInput('getDate')){
					        			return false;
					        		}
				        		}
				        		return true;
				        	}
				        }
				]
			});
	};
	var initEmplAvatar = function(){
		$("#avatar" + globalVar.defaultSuffix).on('click', function(){
			var modal = 
			'<div class="modal hide fade">\
				<div class="modal-header">\
					<button type="button" class="close" data-dismiss="modal">&times;</button>\
					<h4 class="blue">'+ uiLabelMap.ChangeAvatar +'</h4>\
				</div>\
				\
				<form class="no-margin" action="" id="upLoadImageForm"  method="post" enctype="multipart/form-data">\
				<div class="modal-body">\
					<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />\
					<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />\
					<div class="space-4"></div>\
					<div style="width:75%;margin-left:12%;"><input type="file" name="uploadedFile" id="uploadedFile" /></div>\
				</div>\
				\
				<div class="modal-footer" style="text-align:center">\
					<button type="submit" class="btn btn-small btn-primary" id="submitImage"><i class="icon-ok"></i>' + uiLabelMap.CommonSubmit + '</button>\
					<button type="button" class="btn btn-small btn-danger" data-dismiss="modal"><i class="icon-remove"></i> ' + uiLabelMap.CommonClose + '</button>\
				</div>\
				</form>\
			</div>';
			
			
			var modal = $(modal);
			modal.modal("show").on("hidden", function(){
				modal.remove();
			});

			var working = false;

			var form = modal.find('form:eq(0)');
			var file = form.find('input[type=file]').eq(0);
			file.ace_file_input({
				style:'well',
				btn_choose:uiLabelMap.ClickToChooseNewAvatar,
				btn_change:null,
				no_icon:'icon-picture',
				thumbnail: 'fit',
				allowExt:  ['jpg', 'jpeg', 'png', 'gif', 'tif', 'tiff', 'bmp'],
				allowMime: ['image/jpg', 'image/jpeg', 'image/png', 'image/gif', 'image/tif', 'image/tiff', 'image/bmp'],
				before_remove: function() {
					//don't remove/reset files while being uploaded
					return !working;
				},
				before_change: function(files, dropped) {
					var file = files[0];
					if(typeof file === "string") {
						//file is just a file name here (in browsers that don't support FileReader API)
						if(! (/\.(jpe?g|png|gif)$/i).test(file) ) return false;
					}
					else {//file is a File object
						var type = $.trim(file.type);
						if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif)$/i).test(type) )
								|| ( type.length == 0 && ! (/\.(jpe?g|png|gif)$/i).test(file.name) )//for android default browser!
							) return false;

						if( file.size > 2048000 ) {//~2Mb
							return false;
						}
					}

					return true;
				}
			});
			form.on('submit', function(){
				if(!file.data('ace_input_files')) return false;
				_imageAvatar = $('#uploadedFile')[0].files[0];
				file.ace_file_input('disable');
				form.find('button').attr('disabled', 'disabled');
				form.find('.modal-body').append("<div class='center'><i class='icon-spinner icon-spin bigger-150 orange'></i></div>");
				
				var deferred = new $.Deferred;
				working = true;
				deferred.done(function() {
					form.find('button').removeAttr('disabled');
					form.find('input[type=file]').ace_file_input('enable');
					form.find('.modal-body > :last-child').remove();
					
					modal.modal("hide");
	
					var thumb = file.next().find('img').data('thumb');
					if(thumb) $('#avatar' + globalVar.defaultSuffix).get(0).src = thumb;
	
					working = false;
				});
				deferred.resolve();
				
				return false;
			});
		});
	};
	var getImageAvatar = function(){
		return _imageAvatar;
	};
	var validate = function(){
		return $("#" + globalVar.profileInfoDiv).jqxValidator('validate');
	};
	var getPartyCode = function(){
		return $('#employeeId').val();
	};
	var initPopupAddNew = function(){
		var configEleArr = [];
		configEleArr.push({type: 'jqxWindow', id: 'addOriginWindow' + globalVar.defaultSuffix, 
			config: {width: 350, height: 140}, 
			openWindowBtn: 'addOriginBtn' + globalVar.defaultSuffix, 
			closeWindowBtn: 'cancelOrigin' + globalVar.defaultSuffix,
			saveWindowBtn: 'saveOrigin' + globalVar.defaultSuffix,
			warningMessage: uiLabelMap.AddNewRowConfirm,
			ajaxConfig: {
				url: 'createEthnicOrigin', 
				loadingAjax: 'loadingOrigin' + globalVar.defaultSuffix, 
				spinnerAjax: 'spinnerOrigin' + globalVar.defaultSuffix,
				getDataFunc: function(){ 
					return{description: $('#ethnicOrigin' + globalVar.defaultSuffix).val()}
					
				},
				successFunction: function(){
					updateJqxDropDownListBinding($("#ethnicOriginDropdown" + globalVar.defaultSuffix), "getEthnicOriginList")
				}
			},
			validator: [
                {input: '#ethnicOrigin' + globalVar.defaultSuffix, message: uiLabelMap.FieldRequired, action: 'keyup, blur',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false;
                        }
                        return true;
                    }
                },
                {
                    input: '#ethnicOrigin' + globalVar.defaultSuffix,
                    message: uiLabelMap.HRCharacterIsNotValid,
                    action: 'keyup, blur',
                    rule : function(input, commit){
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRegex(input.val(),uiLabelMap.HRCheckFullName);
                    }
                },
			]
		});
		configEleArr.push({type: 'jqxWindow', id: 'addReligionWindow' + globalVar.defaultSuffix, 
			config: {width: 350, height: 140},  
			openWindowBtn: 'addReligionBtn' + globalVar.defaultSuffix,
			closeWindowBtn: 'cancelReligion' + globalVar.defaultSuffix,
			saveWindowBtn: 'saveReligion' + globalVar.defaultSuffix,
			warningMessage: uiLabelMap.AddNewRowConfirm,
			ajaxConfig: {
				url: 'createReligion', 
				loadingAjax: 'loadingReligion' + globalVar.defaultSuffix, 
				spinnerAjax: 'spinnerReligion' + globalVar.defaultSuffix,
				getDataFunc: function(){ 
					return{description: $('#religion' + globalVar.defaultSuffix).val()}
				},
				successFunction: function(){
					updateJqxDropDownListBinding($("#religionDropdown" + globalVar.defaultSuffix), "getReligionList")
				}
			},
			validator: [
                {
                    input: '#religion' + globalVar.defaultSuffix, message: uiLabelMap.FieldRequired, action: 'keyup, blur',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false;
                        }
                        return true;
                    },
                },
                {
                    input: '#religion' + globalVar.defaultSuffix, message: uiLabelMap.HRCharacterIsNotValid, action: 'keyup, blur',
                    rule : function(input, commit){
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRegex(input.val(),uiLabelMap.HRCheckFullName);
                    }
                }
			]
		});
		configEleArr.push({type: 'jqxWindow', id: 'addNationalityWindow' + globalVar.defaultSuffix, 
			config: {width: 350, height: 140},  
			openWindowBtn: 'addNationalityBtn' + globalVar.defaultSuffix, 
			closeWindowBtn: 'cancelNationality' + globalVar.defaultSuffix,
			saveWindowBtn: 'saveNationality' + globalVar.defaultSuffix,
			warningMessage: uiLabelMap.AddNewRowConfirm,
			ajaxConfig: {
				url: 'createNationality', 
				loadingAjax: 'loadingNationality' + globalVar.defaultSuffix, 
				spinnerAjax: 'spinnerNationality' + globalVar.defaultSuffix,
				getDataFunc: function(){ 
					return {description: $('#nationality' + globalVar.defaultSuffix).val()}
				},
				successFunction: function(){
					updateJqxDropDownListBinding($("#nationalityDropdown" + globalVar.defaultSuffix), "getNationalityList")
				}
			},
			validator: [
                {input: '#nationality' + globalVar.defaultSuffix, message: uiLabelMap.FieldRequired, action: 'keyup, blur',
                    rule: function (input, commit) {
                        if(!input.val()){
                            return false;
                        }
                        return true;
                    }
                },
                {
                    input: '#nationality' + globalVar.defaultSuffix, message: uiLabelMap.HRCharacterIsNotValid, action: 'keyup, blur',
                    rule : function(input, commit){
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRegex(input.val(),uiLabelMap.HRCheckFullName);
                    }
                }
			]
		});
		
		configEleArr.push({type: 'jqxInput', id: 'nationality' + globalVar.defaultSuffix, config: {width: '96%', height: 20}});
		configEleArr.push({type: 'jqxInput', id: 'religion' + globalVar.defaultSuffix, config: {width: '96%', height: 20}});
		configEleArr.push({type: 'jqxInput', id: 'ethnicOrigin' + globalVar.defaultSuffix, config: {width: '96%', height: 20}});

		popupAddNewObj.init(configEleArr);
	};
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData,
		getPartyCode: getPartyCode,
		getImageAvatar: getImageAvatar
	}
}());


var popupAddNewObj = (function(){
	var init = function(configEleArr){
		configEleArr.forEach(function(configEle){
			var type = configEle.type;
			var config = configEle.config;
			var id = configEle.id;
			if(type == 'jqxWindow'){
				createJqxWindow($("#" + id), config.width, config.height, config.initContent);
				var openWindowBtn = configEle.openWindowBtn;
				var closeWindowBtn = configEle.closeWindowBtn;
				var saveWindowBtn = configEle.saveWindowBtn;
				var validator = configEle.validator;
				var ajaxConfig = configEle.ajaxConfig;
				$("#" + id).on('close', function(event){
					Grid.clearForm($(this));
				});
				if(openWindowBtn){
					$("#" + openWindowBtn).click(function(event){
						openJqxWindow($("#" + id))
					});
				}
				if(closeWindowBtn){
					$("#" + closeWindowBtn).click(function(event){
						$("#" + id).jqxWindow('close');
					});
				}
				if(validator){
					$("#" + id).jqxValidator({
						rules: validator
					});
				}
				if(ajaxConfig && ajaxConfig.spinnerAjax){
					create_spinner($("#" + ajaxConfig.spinnerAjax));
				}
				if(saveWindowBtn){
					$("#" + saveWindowBtn).click(function(event){
						if(validator){
							var valid = $("#" + id).jqxValidator('validate');
							if(!valid){
								return;
							}
						}
						if(configEle.warningMessage){
							bootbox.dialog(configEle.warningMessage,
								[{
									"label" : uiLabelMap.CommonSubmit,
					    		    "class" : "btn-primary btn-small icon-ok open-sans",
					    		    "callback": function() {
					    		    	sendRequest(ajaxConfig.url, ajaxConfig.getDataFunc(), ajaxConfig.loadingAjax, saveWindowBtn, closeWindowBtn, id, ajaxConfig.successFunction);	
					    		    }	
								},
								{
					    		    "label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",
					    		}]		
							);
						}else{
							sendRequest(ajaxConfig.url, ajaxConfig.getDataFunc(), ajaxConfig.loadingAjax, saveWindowBtn, closeWindowBtn, id, ajaxConfig.successFunction);
						}
					});
				}
			}else if(type == 'jqxInput'){
				$("#" + id).jqxInput(config);
			}
		});
	};
	var sendRequest = function(url, data, loadingId, saveBtn, closeBtn, windowId, successFunction){
		$("#" + loadingId).show();
		$("#" + saveBtn).attr("disabled", 'disabled');
		$("#" + closeBtn).attr("disabled", 'disabled');
		$.ajax({
			url: url,
			data: data,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					if(typeof(successFunction) == "function"){
						successFunction();
					}
					$("#" + windowId).jqxWindow('close');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#" + loadingId).hide();
				$("#" + saveBtn).removeAttr("disabled");
				$("#" + closeBtn).removeAttr("disabled");
			}
		});
	};
	return{
		init: init
	}
}());
