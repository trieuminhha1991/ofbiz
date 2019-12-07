$(document).ready(function(){
	generalInfoObj.init();
});

var generalInfoObj = (function(){
		var init = function(){
			initJqxDropdownlist();
			initJqxDateTimeInput();
			initJqxInut();
			initJqxNumberInput();	
			initBtnEvent();
			initJqxValidator();
			create_spinner($("#spinnerUpdateAvatar"));
		};
		var initJqxDropdownlist = function(){
			createJqxDropDownList(genderList, $("#editGenderDropdownlist"), "genderId", "description", 23, "100%");
			createJqxDropDownList(maritalStatusList, $("#editMaritalStatusDropdownlist"), "maritalStatusId", "description", 23, "100%");
			createJqxDropDownList(ethnicOriginList, $("#editEthnicOriginDropdownlist"), "ethnicOriginId", "description", 23, "100%");
			createJqxDropDownList(religionTypes, $("#editReligionDropdownlist"), "religionId", "description", 23, "100%");
			createJqxDropDownList(nationalityTypes, $("#editNationalityDropdownlist"), "nationalityId", "description", 23, "100%");
			createJqxDropDownList(geoArr, $("#editIdIssuePlaceDropDownList"), "geoId", "geoName", 23, "100%");
		};
		
		var initJqxDateTimeInput = function(){
			$("#editBirthDateTimeInput").jqxDateTimeInput({ width: '100%', height: 23, theme: 'olbius', value : null});
			$("#editIdIssueDateTimeInput").jqxDateTimeInput({ width: '100%', height: 23, theme: 'olbius', value : null});
		};
		
		var initJqxNumberInput = function(){
			$("#editIdNumberInput").jqxInput({width: '96.5%', height: 23, theme: 'olbius'});
			$("#editNbrChildrenNumberInput").jqxNumberInput({ width: '100%', height: 23,  spinButtons: false, decimalDigits: 0, digits: 3, inputMode: 'simple', theme: 'olbius'});
		}
		
		var initJqxInut = function(){
			$("#editNativeLandInput").jqxInput({width: '96.5%', height: 23, theme: 'olbius'});
			$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		};
		
		var initBtnEvent = function(){
				$('#personal-image').on('click', function(){
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
							<button type="button" class="btn btn-small btn-primary" data-dismiss="modal" id="submitImage" onclick="uploadImage();"><i class="icon-ok"></i>' + uiLabelMap.CommonSubmit + '</button>\
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
						thumbnail:'large',
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
				});		
		};
		var initJqxValidator = function(){
			$('#personal-info-1').jqxValidator({
				rules : [
				         {
				        	input : '#editNbrChildrenNumberInput', 
				        	message : uiLabelMap.ValueMustBeGreateThanZero,
				        	action: 'blur',
				        	rule : function(input,commit){
				        		if(input.val() < 0){
				        			return false;
				        		}
				        		return true;
				        	}
				         },
				         {
				        	 input : '#editIdIssueDateTimeInput',
				        	 message : uiLabelMap.IdentifyDayGreaterBirthDate,
				        	 action : 'blur',
				        	 rule : function(input,commit){
				        		 if($('#editBirthDateTimeInput').val() != "" && $(input).val() != ""){
				        			 if($('#editBirthDateTimeInput').jqxDateTimeInput('val', 'date').getTime() 
				        					 && input.jqxDateTimeInput('val', 'date').getTime() < $('#editBirthDateTimeInput').jqxDateTimeInput('val', 'date').getTime()){
					        			 return false;
					        		 }
					        		 return true;
				        		 }else{
				        			 return true;
				        		 }
				        	 }
				         },
				         {
				        	 input : '#editBirthDateTimeInput',
				        	 message : uiLabelMap.BirthDateBefIdentifyCardDay,
				        	 action : 'blur',
				        	 rule : function(input,commit){
				        		 if($('#editIdIssueDateTimeInput').jqxDateTimeInput('getDate') && $(input).val() != ""){
				        			 if($('#editIdIssueDateTimeInput').jqxDateTimeInput('val', 'date').getTime() && 
					        				 input.jqxDateTimeInput('val', 'date').getTime() > $('#editIdIssueDateTimeInput').jqxDateTimeInput('val', 'date').getTime()){
					        			 return false;
					        		 }
				        		 }
				        		 return true;
				        	 }
				         },
				         {
				        	 input : '#editIdNumberInput',
				        	 message : uiLabelMap.ValueMustBeGreateThanZero,
				        	 action : 'blur',
				        	 rule : function(input,commit){
				        		 var value = parseInt(input.val());
				        		 if(value < 0 ){
				        			 return false;
				        		 }
				        		 return true;
				        	 }
				         }
		         ]
			});
		};
		return {
			init: init
		};
}());

function openEditPersonInfo(showDivEle, hideDivEle, inputDiv, valueProp, fieldType){
	showDivEle.show();
	hideDivEle.hide();
	if(valueProp){		
		if(fieldType && fieldType == 'datetimeinput' && personInfo[valueProp] != 'undefined'){
			inputDiv.val(new Date(parseInt(personInfo[valueProp])));	
		}else{
			inputDiv.val(personInfo[valueProp]);
		}
	}
	
}

function cancelEdit(showDivEle, hideDivEle){
	showDivEle.show();
	hideDivEle.hide();
}

function updatePersonInfo(showDivEle, hideDivEle, fieldUpdate, fieldInputUpdateValue, personProperty, fieldType){
	$('#personal-info-1').jqxValidator('validate');
	if($('#personal-info-1').jqxValidator('validate')){
		var data = {};
		data.partyId = globalVar.userLogin_partyId;
		var updateValue;
		if(fieldType && fieldType == 'datetimeinput'){
			if(fieldInputUpdateValue.val()){
				updateValue = fieldInputUpdateValue.jqxDateTimeInput('val', 'date');
				if(!updateValue){
					cancelEdit(showDivEle, hideDivEle);
					return;
				}
				updateValue = fieldInputUpdateValue.jqxDateTimeInput('val', 'date').getTime();
			}
		}else{
			updateValue = fieldInputUpdateValue.val();
			if(!updateValue || updateValue.length == 0){
				cancelEdit(showDivEle, hideDivEle);
				return;
			}
		}
		var description = updateValue;
		if('dropdownlist' == fieldType){
			var selectItem = fieldInputUpdateValue.jqxDropDownList('getSelectedItem');
			if(selectItem){
				description = selectItem.label;
			}
		}else if('datetimeinput' == fieldType){
			var date = fieldInputUpdateValue.jqxDateTimeInput('val', 'date');
			description = date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear();
		}
		data[fieldUpdate] = updateValue;
		
		jQuery.ajax({
			url: "updateEmplProfile",
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					personInfo[personProperty] = updateValue;				
					$(showDivEle.find("span")[0]).text(description);
					openNotify(uiLabelMap.UpdateSuccessfully, "success");
				}else if(response.responseMessage == "error"){
					if (response.errorMessage != null && response.errorMessage != undefined && response.errorMessage != ''){
						openNotify(response.errorMessage, "error");
					} else {
						openNotify(uiLabelMap.UpdateError, "error");
					}
				}
			},
			complete: function( jqXHR, textStatus){
				showDivEle.show();
				hideDivEle.hide();
			}
		});
	}else{
		return false;
	}
	
}

function uploadImage(){
	var form = jQuery("#upLoadImageForm");
	var file = form.find('input[type=file]').eq(0);		
	if(!file.data('ace_input_files')) return false;
	
	var fileUpload = $('#uploadedFile')[0].files[0];
	jQuery("#_uploadedFile_fileName").val(fileUpload.name);
	jQuery("#_uploadedFile_contentType").val(fileUpload.type);
	var formData = new FormData(jQuery('#upLoadImageForm')[0]);
	$("#loadingUpdateAvatar").show();
	jQuery.ajax({
        url: 'updatePersonalImage',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(response, textStatus, jqXHR){	        	
        	if(response._EVENT_MESSAGE_){					
				var urlImg = response.contentUrl;
				//console.log(urlImg);
				if(urlImg){
					jQuery("#personal-image").attr("src", urlImg);
				}else{
					bootbox.dialog(uiLabelMap.ErrorWhenUpdate,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			}else{
				bootbox.dialog(response._ERROR_MESSAGE_,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            //console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        },
        complete: function(jqXHR, textStatus){
        	$("#loadingUpdateAvatar").hide();
        }
    });
}

function openNotify(message, type){
	$("#jqxNotification").jqxNotification({ template: type});
	$("#notificationContent").text(message);
  	$("#jqxNotification").jqxNotification("open");
}

function fixSelectAll(dataList) {
	var sourceST = {
	        localdata: dataList,
	        datatype: "array"
    };
	var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
    var uniqueRecords2 = filterBoxAdapter2.records;
	uniqueRecords2.splice(0, 0, uiLabelMap.filterselectallstring);
	return uniqueRecords2;
}

function showEditDiv(divEle){
	divEle.show();
}

function hideEditDiv(divEle){
	divEle.hide();
}
