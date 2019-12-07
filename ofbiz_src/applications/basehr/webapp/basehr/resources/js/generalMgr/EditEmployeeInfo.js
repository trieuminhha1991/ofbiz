var editEmplGeneralInfo = (function(){
	var _imageAvatar = null;
	var _partyId;
	var init = function(){
		initJqxInput();
		initJqxDateTimeInput();
		initJqxDropDownList();
		initJqxValidator();
		initEmplAvatar();
	};
	
	var initJqxInput = function(){		
		$('#employeeId' + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
		$('#lastName' + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
		$('#middleName' + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
		$('#firstName' + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
		$('#nativeLandInput' + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
		$("#editIdNumber" + globalVar.editEmplWindow).jqxInput({ width: '95%', height: 19});
		$("#birthPlace" + globalVar.editEmplWindow).jqxInput({ width: '95%', height: 19});
		/*$('#emplPositionClassType' + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px', disabled: true});*/
	};
	
	var initJqxDateTimeInput = function(){
		$("#birthDate" + globalVar.editEmplWindow).jqxDateTimeInput({width: '97%', height: '25px'});
		$("#birthDate" + globalVar.editEmplWindow).val(null);
		$("#idIssueDateTime" + globalVar.editEmplWindow).jqxDateTimeInput({width: '97%', height: '25px'});
		$("#idIssueDateTime" + globalVar.editEmplWindow).val(null);
	};
	
	var initJqxDropDownList = function(){		
		createJqxDropDownList(globalVar.genderList, $("#gender" + globalVar.editEmplWindow), 'genderId', 'description', 25, '97%');
		createJqxDropDownList(globalVar.geoArr, $("#idIssuePlaceDropDown" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
		$("#idIssuePlaceDropDown" + globalVar.editEmplWindow).jqxDropDownList({dropDownHeight: 180});
		createJqxDropDownList(globalVar.ethnicOriginList, $("#ethnicOriginDropdown" + globalVar.editEmplWindow), "ethnicOriginId", "description", 25, "97%");
		createJqxDropDownList(globalVar.religionTypes, $("#religionDropdown" + globalVar.editEmplWindow), "religionId", "description", 25, "97%");
		createJqxDropDownList(globalVar.nationalityTypes, $("#nationalityDropdown" + globalVar.editEmplWindow), "nationalityId", "description", 25, "97%");
		createJqxDropDownList(globalVar.maritalStatusList, $("#maritalStatusDropdown" + globalVar.editEmplWindow), "maritalStatusId", "description", 25, "97%");
	};
	
	var resetData = function(){
		Grid.clearForm($("#generalInfo" + globalVar.editEmplWindow));
		_imageAvatar = null;
		$("#avatar" + globalVar.editEmplWindow).attr('src', '/aceadmin/assets/avatars/no-avatar.png');
	};
	
	var getData = function(){
		var data = {
				partyId: _partyId,
				partyCode: $('#employeeId' + globalVar.editEmplWindow).val(),
				lastName: $('#lastName' + globalVar.editEmplWindow).val(),
				middleName: $('#middleName' + globalVar.editEmplWindow).val(),
				firstName: $('#firstName' + globalVar.editEmplWindow).val(),
				nativeLand: $('#nativeLandInput' + globalVar.editEmplWindow).val(),
				idNumber: $("#editIdNumber" + globalVar.editEmplWindow).val(),
				gender: $("#gender" + globalVar.editEmplWindow).val(),
				idIssuePlace: $("#idIssuePlaceDropDown" + globalVar.editEmplWindow).val(),
				ethnicOrigin: $("#ethnicOriginDropdown" + globalVar.editEmplWindow).val(),
				religion: $("#religionDropdown" + globalVar.editEmplWindow).val(),
				nationality: $("#nationalityDropdown" + globalVar.editEmplWindow).val(),
				maritalStatusId: $("#maritalStatusDropdown" + globalVar.editEmplWindow).val(),
				birthPlace: $("#birthPlace" + globalVar.editEmplWindow).val(),
		};
		if($("#birthDate" + globalVar.editEmplWindow).jqxDateTimeInput('val', 'date')){
			data.birthDate = $("#birthDate" + globalVar.editEmplWindow).jqxDateTimeInput('val', 'date').getTime();
		}
		if($("#idIssueDateTime" + globalVar.editEmplWindow).jqxDateTimeInput('val', 'date')){
			data.idIssueDate = $("#idIssueDateTime" + globalVar.editEmplWindow).jqxDateTimeInput('val', 'date').getTime();
		}
		var statusId = $("#statusId" + globalVar.editEmplWindow).val();
		if(statusId){
			data.workingStatusId = statusId;
			if(statusId != 'EMPL_WORKING'){
				data.terminationReasonId = $("#reasonResign" + globalVar.editEmplWindow).val();
				data.dateTermination = $("#resignDate" + globalVar.editEmplWindow).jqxDateTimeInput('val', 'date').getTime();
			}
		}
		return data;
	};
	
	var initJqxValidator = function(){
		$("#generalInfo" + globalVar.editEmplWindow).jqxValidator({
			rules: [
			        {
			        	input: '#employeeId' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input: '#firstName' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			        {
			        	input: '#lastName' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: 'required'
			        },
			]
		});
	};
	var validate = function(){
		return $("#generalInfo" + globalVar.editEmplWindow).jqxValidator('validate');
	};
	
	var hideValidate = function(){
		$("#generalInfo" + globalVar.editEmplWindow).jqxValidator('hide');
	};
	
	var setData = function(data){
		_partyId = data.partyId;
		$("#employeeId" + globalVar.editEmplWindow).val(data.partyCode);
		$("#firstName" + globalVar.editEmplWindow).val(data.firstName);
		$("#middleName" + globalVar.editEmplWindow).val(data.middleName);
		$("#lastName" + globalVar.editEmplWindow).val(data.lastName);
		$("#nativeLandInput" + globalVar.editEmplWindow).val(data.nativeLand);
		$("#gender" + globalVar.editEmplWindow).jqxDropDownList('selectItem', data.gender);
		$("#editIdNumber" + globalVar.editEmplWindow).val(data.idNumber);
		$("#birthPlace" + globalVar.editEmplWindow).val(data.birthPlace);
		if(data.hasOwnProperty("idIssueDate")){
			$("#idIssueDateTime" + globalVar.editEmplWindow).val(new Date(data.idIssueDate));
		}
		if(data.hasOwnProperty("birthDate")){
			$("#birthDate" + globalVar.editEmplWindow).val(new Date(data.birthDate));
		}
		$("#idIssuePlaceDropDown" + globalVar.editEmplWindow).jqxDropDownList('selectItem', data.idIssuePlace);
		$("#ethnicOriginDropdown" + globalVar.editEmplWindow).jqxDropDownList('selectItem', data.ethnicOrigin);
		$("#religionDropdown" + globalVar.editEmplWindow).jqxDropDownList('selectItem', data.religion);
		$("#nationalityDropdown" + globalVar.editEmplWindow).jqxDropDownList('selectItem', data.nationality);
		$("#maritalStatusDropdown" + globalVar.editEmplWindow).jqxDropDownList('selectItem', data.maritalStatusId);
		if(data.avatarUrl){
			$("#avatar" + globalVar.editEmplWindow).attr('src', data.avatarUrl);
		}
	};
	var initEmplAvatar = function(){
		$("#avatar" + globalVar.editEmplWindow).on('click', function(){
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
					if(thumb) $('#avatar' + globalVar.editEmplWindow).get(0).src = thumb;
	
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
	return{
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData,
		setData: setData,
		getImageAvatar: getImageAvatar
	}
}());

var editPermanentResInfo = (function(){
	var permanentResAddr = {};
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initJqxValidator();
		/*if(typeof(globalVar.defaultCountry) != "undefined"){
			$("#countryGeoIdPermRes" + globalVar.editEmplWindow).jqxDropDownList('selectItem', globalVar.defaultCountry);
		}*/
	};
	var initJqxInput = function(){
		$("#paddress1" + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
			createJqxDropDownList(globalVar.geoCountryList, $("#countryGeoIdPermRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#stateGeoIdPermRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#countyGeoIdPermRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#wardGeoIdPermRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdPermRes' + globalVar.editEmplWindow).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				editEmplInfoCommonObj.updateSourceJqxDropdownList($("#stateGeoIdPermRes" + globalVar.editEmplWindow), data, url, permanentResAddr.stateProvinceGeoId);
			}
		});
		
		$("#stateGeoIdPermRes" + globalVar.editEmplWindow).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				editEmplInfoCommonObj.updateSourceJqxDropdownList($("#countyGeoIdPermRes" + globalVar.editEmplWindow), data, url, permanentResAddr.districtGeoId);
			}
		});
		$("#countyGeoIdPermRes" + globalVar.editEmplWindow).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				editEmplInfoCommonObj.updateSourceJqxDropdownList($("#wardGeoIdPermRes" + globalVar.editEmplWindow), data, url, permanentResAddr.wardGeoId);
			}
		});
	};
	
	var setData = function(data){
		if(data){
			permanentResAddr = data;
			if(permanentResAddr && permanentResAddr.hasOwnProperty("contactMechId")){
				$('#countryGeoIdPermRes' + globalVar.editEmplWindow).val(permanentResAddr.countryGeoId);
				$("#paddress1" + globalVar.editEmplWindow).val(permanentResAddr.address1);
			}
		}
		if(!permanentResAddr.countryGeoId && globalVar.countryGeoIdDefault){
			$('#countryGeoIdPermRes' + globalVar.editEmplWindow).val(globalVar.countryGeoIdDefault);
		}
	};
	
	var getData = function(){
		var data = {
				address1: $("#paddress1" + globalVar.editEmplWindow).val(),
				countryGeoId: $("#countryGeoIdPermRes" + globalVar.editEmplWindow).val(),
				stateProvinceGeoId: $("#stateGeoIdPermRes" + globalVar.editEmplWindow).val(),
				districtGeoId: $("#countyGeoIdPermRes" + globalVar.editEmplWindow).val(),
				wardGeoId: $("#wardGeoIdPermRes" + globalVar.editEmplWindow).val(),
				contactMechPurposeTypeId: "PERMANENT_RESIDENCE"
		};
		if(permanentResAddr.hasOwnProperty("contactMechId")){
			data.contactMechId = permanentResAddr.contactMechId;
		}
		return data;
	};
	
	var initJqxValidator = function(){
		$("#permanentResidence" + globalVar.editEmplWindow).jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdPermRes' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1" + globalVar.editEmplWindow).val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        			return true;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#stateGeoIdPermRes' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#paddress1" + globalVar.editEmplWindow).val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        			return true;
			        		}
			        		return true;
			        	}
			        }			        
			]
		});
	};
	var validate = function(){
		return $("#permanentResidence" + globalVar.editEmplWindow).jqxValidator('validate');
	};
	
	var hideValidate = function(){
		$("#permanentResidence" + globalVar.editEmplWindow).jqxValidator('hide');
	};
	
	var resetData = function(){
		Grid.clearForm($("#permanentResidence" + globalVar.editEmplWindow));
		$("#countryGeoIdPermRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		$("#stateGeoIdPermRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		$("#countyGeoIdPermRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		$("#wardGeoIdPermRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		permanentResAddr = {};
	}
	
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData,
		setData: setData
	}
}());

var editCurrResInfo = (function(){
	var currResAddrData = {};
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initBtnEvent();
		initJqxValidator();
		/*if(typeof(globalVarAddNewEmpl.defaultCountry) != "undefined"){
			$("#countryGeoIdCurrRes" + globalVar.editEmplWindow).jqxDropDownList('selectItem', globalVarAddNewEmpl.defaultCountry);
		}*/
	};
	
	var initJqxInput = function(){
		$("#address1CurrRes" + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.geoCountryList, $("#countryGeoIdCurrRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#stateGeoIdCurrRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#countyGeoIdCurrRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#wardGeoIdCurrRes" + globalVar.editEmplWindow), "geoId", "geoName", 25, "97%");
	};
	
	var getData = function(){
		var data = {
				address1: $("#address1CurrRes" + globalVar.editEmplWindow).val(),
				countryGeoId: $("#countryGeoIdCurrRes" + globalVar.editEmplWindow).val(),
				stateProvinceGeoId: $("#stateGeoIdCurrRes" + globalVar.editEmplWindow).val(),
				districtGeoId: $("#countyGeoIdCurrRes" + globalVar.editEmplWindow).val(),
				wardGeoId: $("#wardGeoIdCurrRes" + globalVar.editEmplWindow).val(),
				contactMechPurposeTypeId: "CURRENT_RESIDENCE"
		};
		if(currResAddrData.hasOwnProperty("contactMechId")){
			data.contactMechId = currResAddrData.contactMechId;
		}
		return data;
	};
	
	var resetData = function(){
		Grid.clearForm($("#currentResidence" + globalVar.editEmplWindow));
		$("#countryGeoIdCurrRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		$("#stateGeoIdCurrRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		$("#countyGeoIdCurrRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		$("#wardGeoIdCurrRes" + globalVar.editEmplWindow).jqxDropDownList('clearSelection');
		currResAddrData = {};
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdCurrRes' + globalVar.editEmplWindow).on('select', function(event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				editEmplInfoCommonObj.updateSourceJqxDropdownList($('#stateGeoIdCurrRes' + globalVar.editEmplWindow), data, url, currResAddrData.stateProvinceGeoId);
			}
		});
		
		$("#stateGeoIdCurrRes" + globalVar.editEmplWindow).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				editEmplInfoCommonObj.updateSourceJqxDropdownList($('#countyGeoIdCurrRes' + globalVar.editEmplWindow), data, url, currResAddrData.districtGeoId);
			}
		});
		
		$("#countyGeoIdCurrRes" + globalVar.editEmplWindow).on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				editEmplInfoCommonObj.updateSourceJqxDropdownList($('#wardGeoIdCurrRes' + globalVar.editEmplWindow), data, url, currResAddrData.wardGeoId);
			}
		});
	};
	
	var hideValidate = function(){
		$("#currentResidence" + globalVar.editEmplWindow).jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#currentResidence" + globalVar.editEmplWindow).jqxValidator({
			rules: [
			        {
			        	input: '#countryGeoIdCurrRes' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrRes" + globalVar.editEmplWindow).val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        			return true;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#stateGeoIdCurrRes' + globalVar.editEmplWindow,
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1CurrRes" + globalVar.editEmplWindow).val();
			        		if(address1 && address1.trim().length > 0){
			        			if(!input.val()){
			        				return false;
			        			}
			        			return true;
			        		}
			        		return true;
			        	}
			        }			        
			]
		});
	};
	var validate = function(){
		return $("#currentResidence" + globalVar.editEmplWindow).jqxValidator('validate');
	};
	
	var setData = function(data){
		if(data){
			currResAddrData = data;
			if(currResAddrData && currResAddrData.hasOwnProperty("contactMechId")){
				$('#countryGeoIdCurrRes' + globalVar.editEmplWindow).val(currResAddrData.countryGeoId);
				$("#address1CurrRes" + globalVar.editEmplWindow).val(currResAddrData.address1);
			}
		}
	};
	
	var initBtnEvent = function(){
		$("#copyPermRes" + globalVar.editEmplWindow).click(function(){
			currResAddrData = editPermanentResInfo.getData();
			$("#address1CurrRes" + globalVar.editEmplWindow).val(currResAddrData.address1);
			$('#countryGeoIdCurrRes' + globalVar.editEmplWindow).jqxDropDownList('clearSelection');						
			$('#countryGeoIdCurrRes' + globalVar.editEmplWindow).jqxDropDownList('selectItem', currResAddrData.countryGeoId);
		});
	};
	
	return {
		init: init,
		validate: validate,
		getData: getData,
		hideValidate: hideValidate,
		resetData: resetData,
		setData: setData
	}
}());

var editPhoneObj = (function(){
	var contactMechId;
	var init = function(){
		$("#phoneMobile" + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
	};
	
	var setData = function(data){
		if(data){
			contactMechId = data.contactMechId;
			$("#phoneMobile" + globalVar.editEmplWindow).val(data.phoneMobile);
		}
	};
	var getData = function(){
		var retData = {};
		var phoneMobileNbr = $("#phoneMobile" + globalVar.editEmplWindow).val();
		if(phoneMobileNbr && phoneMobileNbr.length > 0){
			retData.phoneMobileNbr = phoneMobileNbr;
			if(typeof(contactMechId) != 'undefined' && contactMechId.length > 0){
				retData.contactMechId = contactMechId;
			}
		}
		return retData;
	};
	var resetData = function(){
		$("#phoneMobile" + globalVar.editEmplWindow).val("");
		contactMechId = "";
	};
	return{
		init: init,
		setData: setData,
		getData: getData,
		resetData: resetData
	}
}());

var editEmailObj = (function(){
	var contactMechId;
	var init = function(){
		$("#emailAddress" + globalVar.editEmplWindow).jqxInput({width : '95%',height : '20px'});
	};
	var setData = function(data){
		if(data){
			contactMechId = data.contactMechId;
			$("#emailAddress" + globalVar.editEmplWindow).val(data.emailAddress);
		}
	};
	var getData = function(){
		var retData = {};
		var emailAddress = $("#emailAddress" + globalVar.editEmplWindow).val();
		if(emailAddress && emailAddress.length > 0){
			retData.emailAddress = emailAddress;
			if(typeof(contactMechId) != 'undefined' && contactMechId.length > 0){
				retData.contactMechId = contactMechId;
			}
		}
		return retData;
	};
	var resetData = function(){
		$("#emailAddress" + globalVar.editEmplWindow).val("");
		contactMechId = "";
	};
	return{
		init: init,
		setData: setData,
		getData: getData,
		resetData: resetData
	}
}());

var editEmplInfoWizard = (function(){
	var init = function(){
		$('#fuelux-wizard' + globalVar.editEmplWindow).ace_wizard().on('change' , function(e, info){
            if(globalVar.hasEditPermisstion == "false" && info.step == 1){
                $('#btnNext'+globalVar.editEmplWindow).prop("disabled",true);
            }else{
                $('#btnNext'+globalVar.editEmplWindow).prop("disabled",false);
            }
	        if(info.step == 1 && (info.direction == "next")) {
	        	return editEmplGeneralInfo.validate();
	        }
	        if(info.direction == "previous"){
	        	editEmplGeneralInfo.hideValidate();
	        }
	    }).on('finished', function(e) {
	    	var valid = editPermanentResInfo.validate() && editCurrResInfo.validate();
	    	if(!valid){
	    		return;
	    	}
	    	updateEmployee();   
	    }).on('stepclick', function(e){
	        
	    });
	};
	
	var updateEmployee = function(){
		var dataSubmit = editEmplGeneralInfo.getData();
		dataSubmit.permanentRes = JSON.stringify(editPermanentResInfo.getData());
		dataSubmit.currRes = JSON.stringify(editCurrResInfo.getData());
		dataSubmit.phoneMobile = JSON.stringify(editPhoneObj.getData());
		dataSubmit.emailAddressInfo = JSON.stringify(editEmailObj.getData());
		var formData = new FormData();
		var imageAvatar = editEmplGeneralInfo.getImageAvatar();
		if(imageAvatar){
    		formData.append(imageAvatar.name, imageAvatar);
    	}
    	for(var key in dataSubmit){
    		if(typeof(dataSubmit[key])!= "undefined"){
    			formData.append(key, dataSubmit[key]);
    		}
		}
		editEmplInfoCommonObj.showLoading();
		$("#btnNext" + globalVar.editEmplWindow).attr("disabled", "disabled");
		$("#btnPrev" + globalVar.editEmplWindow).attr("disabled", "disabled");
		$.ajax({
			url: 'updateEmployeeInfo',
			data: formData,
			type: 'POST',
			cache : false,
			contentType : false,
			processData : false,
			success: function(response){
    			if(response.responseMessage == 'success'){
    				editEmplInfoCommonObj.closeWindow();
    				$("#updateNotification").jqxNotification('closeLast');
    				$("#notificationText").text(response.successMessage);
					$("#updateNotification").jqxNotification({ template: 'info' });
					$("#updateNotification").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
    			}else{
    				bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
						}]		
					);
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			editEmplInfoCommonObj.hideLoading();
    			$("#btnNext" + globalVar.editEmplWindow).removeAttr("disabled");
    			$("#btnPrev" + globalVar.editEmplWindow).removeAttr("disabled");
    		}
		});
	};
	
	var resetStep = function(){
		$('#fuelux-wizard' + globalVar.editEmplWindow).wizard('previous');
	};
	return{
		init: init,
		resetStep: resetStep
	}
}());



var editEmplInfoCommonObj = (function(){
	var init = function(){
		createJqxWindow($("#" + globalVar.editEmplWindow), 900, 560);
		$("#" + globalVar.editEmplWindow).on('close', function(event){
			editCurrResInfo.resetData();
			editPermanentResInfo.resetData();
			editEmplGeneralInfo.resetData();
			editEmplInfoWizard.resetStep();
			editEmailObj.resetData();
			editPhoneObj.resetData();
		});
		$("#" + globalVar.editEmplWindow).on('open', function(event){
			$('#fuelux-wizard' + globalVar.editEmplWindow).wizard('setState');
		});
		create_spinner($("#spinner-ajax" + globalVar.editEmplWindow));
	};
	
	var showLoading = function(){
		$("#ajaxLoading" + globalVar.editEmplWindow).show();
	};
	var hideLoading = function(){
		$("#ajaxLoading" + globalVar.editEmplWindow).hide();
	};
	var updateSourceJqxDropdownList = function (dropdownlistEle, data, url, selectItem){
		$.ajax({
			url: url,
			data: data,
			type: 'POST',
			success: function(response){
				var listGeo = response.listReturn;
				if(listGeo && listGeo.length > -1){
					updateSourceDropdownlist(dropdownlistEle, listGeo);        				
					if(selectItem != 'undefinded'){
						dropdownlistEle.jqxDropDownList('selectItem', selectItem);
					}
				}
			}
		});
	};
	var openWindow = function(){
		openJqxWindow($("#" + globalVar.editEmplWindow));
	};
	var closeWindow = function(){
		$("#" + globalVar.editEmplWindow).jqxWindow('close');
	};
	return{
		updateSourceJqxDropdownList: updateSourceJqxDropdownList,
		openWindow: openWindow,
		showLoading: showLoading,
		hideLoading: hideLoading,
		closeWindow: closeWindow,
		init: init
	}
}());

$(document).ready(function () {
	editEmplGeneralInfo.init();
	editEmplInfoCommonObj.init();
	editPermanentResInfo.init();
	editCurrResInfo.init();
	editPhoneObj.init();
	editEmailObj.init();
	editEmplInfoWizard.init();
});
