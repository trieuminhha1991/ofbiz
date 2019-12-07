var trainingProviderObj = (function(){
	var postalAddr = {};
	var emailAddr = {};
	var websiteUrlAddr = {};
	var telephoneNbr = {};
	var isEdit = false;
	var partyId;
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxDropDownEvent();
		initBtnEvent();
		initJqxValidator();
		initJqxGridEvent();
		initJqxWindow();
		initJqxInputEvent();
		create_spinner($("#spinner-ajax"));
	};
	
	var initJqxInputEvent = function(){
		$("#phoneNbr").keydown(function(event){
			if((event.keyCode < 48 || event.keyCode > 57) && event.keyCode != 8){
				event.preventDefault();
				return false;
			}
		});
	};
	
	var initJqxInput = function(){
		$("#trainingProviderNameAddNew").jqxInput({width: '96%', height: 20});
		$("#trainingProviderAddr").jqxInput({width: '96%', height: 20});
		$("#providerWebUrl").jqxInput({width: '96%', height: 20});
		$("#providerEmail").jqxInput({width: '96%', height: 20});
		$("#phoneNbr").jqxInput({width: '96%', height: 20});
		
		$("#fromDate").jqxDateTimeInput({width: '97%', formatString: 'dd/MM/yyyy', disabled: false});
		$("#fromDate").jqxDateTimeInput('clear');
		$("#thruDate").jqxDateTimeInput({width: '97%', formatString: 'dd/MM/yyyy', showFooter: true});
		$("#thruDate").jqxDateTimeInput('clear');
		
		$("#menuListTraining").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.geoCountryArr, $("#countryGeoId"), 'geoId', 'geoName', 25, '97%');
		createJqxDropDownList([], $("#stateProvinceGeoId"), 'geoId', 'geoName', 25, '97%');
		createJqxDropDownList([], $("#districtGeoId"), 'geoId', 'geoName', 25, '97%');
		createJqxDropDownList([], $("#wardGeoId"), 'geoId', 'geoName', 25, '97%');
	};
	
	var initJqxValidator = function(){
		$("#newTrainingProviderWindow").jqxValidator({
			rules: [
			        {input: '#trainingProviderNameAddNew', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			        {
			        	input : '#trainingProviderNameAddNew', message : uiLabelMap.InvalidChar,action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(val){
			        			if(validationNameWithoutHtml(val)){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#fromDate', message : uiLabelMap.FieldRequired,action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(!val){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#thruDate', message : uiLabelMap.ThruDateMustBeAfterFromDate, action : 'valueChange, keyup, blur',
			        	rule : function(input, commit){
			        		var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
			        		var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
						   	if (fromDate > thruDate && thruDate != null && thruDate != undefined) {
				 		    	return false;
						   	}
						   	return true;
			        	}
			        },
			        {
			        	input : '#fromDate', message : uiLabelMap.FromDateMustBeBeforeThruDate, action : 'valueChange, keyup, blur',
			        	rule : function(input, commit){
			        		var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
			        		var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
			        		if (fromDate > thruDate && thruDate != null && thruDate != undefined) {
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {input: '#trainingProviderAddr', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			        {
						input: "#countryGeoId",
						message: uiLabelMap.FieldRequired,
						action: 'blur',
						rule: function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{
						input: "#stateProvinceGeoId",
						message: uiLabelMap.FieldRequired,
						action: 'blur',
						rule: function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{
						input: "#providerEmail", action: 'valueChange', rule: 'email',
						message: uiLabelMap.FormatWrong + "!. " + uiLabelMap.EmailFormatExample,
						action: 'blur',
					},
					
			]
		});
	};
	
	var initJqxDropDownEvent = function(){
		$('#countryGeoId').on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				updateSourceJqxDropdownList($("#stateProvinceGeoId"), data, url, postalAddr.stateProvinceGeoId);
			}
		});
		
		$("#stateProvinceGeoId").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				updateSourceJqxDropdownList($("#districtGeoId"), data, url, postalAddr.districtGeoId);
			}
		});
		$("#districtGeoId").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				updateSourceJqxDropdownList($("#wardGeoId"), data, url, postalAddr.wardGeoId);
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#newTrainingProviderWindow"), 500, 550);
		$("#newTrainingProviderWindow").on('close', function(event){
			resetData();
		});
		$("#newTrainingProviderWindow").on('open', function(event){
			if(isEdit){
				$(this).jqxWindow('setTitle', uiLabelMap.CommonEdit);
			}else{
				$(this).jqxWindow('setTitle', uiLabelMap.CommonAddNew);
			}
			if(globalVar.hasOwnProperty('defaultCountry')){
				$("#countryGeoId").val(globalVar.defaultCountry);
			}
		});
	};
	
	var resetData = function(){
		Grid.clearForm($("#newTrainingProviderWindow"));
		$("#countryGeoId").jqxDropDownList('clearSelection');
		$("#stateProvinceGeoId").jqxDropDownList('clearSelection');
		$("#districtGeoId").jqxDropDownList('clearSelection');
		$("#wardGeoId").jqxDropDownList('clearSelection');
		$("#newTrainingProviderWindow").jqxValidator('hide');
		postalAddr = {};
		emailAddr = {};
		websiteUrlAddr = {};
		telephoneNbr = {};
		isEdit = false;
	};
	
	var initBtnEvent = function(){
		$("#alterCancel").click(function(event){
			$("#newTrainingProviderWindow").jqxWindow('close');
		});
		
		$("#alterSave").click(function(event){
			var valid = $("#newTrainingProviderWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(isEdit){
				updateTrainingProvider();
			}else{
				bootbox.dialog(uiLabelMap.CreateTrainingProviderConfirm,
						[
						{
			    		    "label" : uiLabelMap.CommonSubmit,
			    		    "class" : "btn-primary btn-small icon-ok open-sans",
			    		    "callback": function(){
			    		    	createTrainingProvider();
			    		    }
			    		},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}
						]		
					);
			}
		});
	};
	
	var createTrainingProvider = function(){
		var dataSubmit = getData();
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
		//$("#jqxgrid").jqxGrid('showloadelement');
		$("#ajaxLoading").show();
		$.ajax({
			url: 'createPartyTrainingProvider',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose : true,
						template : 'info',
						appendContainer : "#containerjqxgrid",
						opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#newTrainingProviderWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#alterCancel").removeAttr("disabled");
				$("#alterSave").removeAttr("disabled");
				//$("#jqxgrid").jqxGrid('hideloadelement');
				$("#ajaxLoading").hide();
			}
		});
	};
	
	var updateTrainingProvider = function(){
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
		//$("#jqxgrid").jqxGrid('showloadelement');
		$("#ajaxLoading").show();
		
		var dataSubmit = getData();
		dataSubmit.partyId = partyId;
		if(postalAddr.hasOwnProperty("contactMechId")){
			dataSubmit.addressContactMechId = postalAddr.contactMechId;
		}
		if(emailAddr.hasOwnProperty("contactMechId")){
			dataSubmit.emailContactMechId = emailAddr.contactMechId;
		}
		if(telephoneNbr.hasOwnProperty("contactMechId")){
			dataSubmit.phoneContactMechId = telephoneNbr.contactMechId;
		}
		if(websiteUrlAddr.hasOwnProperty("contactMechId")){
			dataSubmit.websiteContactMechId = websiteUrlAddr.contactMechId;
		}
		$.ajax({
			url: 'updatePartyTrainingProvider',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose : true,
						template : 'info',
						appendContainer : "#containerjqxgrid",
						opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#newTrainingProviderWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response._ERROR_MESSAGE_,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#alterCancel").removeAttr("disabled");
				$("#alterSave").removeAttr("disabled");
				//$("#jqxgrid").jqxGrid('hideloadelement');
				$("#ajaxLoading").hide();
			}
		});
	};
	
	var getData = function(){
		var data = {};
		data.groupName = $("#trainingProviderNameAddNew").val();
		data.address1 = $("#trainingProviderAddr").val();
		data.countryGeoId = $("#countryGeoId").val();
		data.stateProvinceGeoId = $("#stateProvinceGeoId").val();
		data.districtGeoId = $("#districtGeoId").val();
		data.wardGeoId = $("#wardGeoId").val();
		data.emailAddress = $("#providerEmail").val();
		data.contactNumber = $("#phoneNbr").val();
		data.websiteUrl = $("#providerWebUrl").val();
		
		var fromDate = null;
		var thruDate = null;
		if ($("#fromDate").jqxDateTimeInput('getDate') != null){
			fromDate = $("#fromDate").jqxDateTimeInput('getDate').getTime();
			data.fromDateTime = fromDate;
		}
		if ($("#thruDate").jqxDateTimeInput('getDate') != null){
			thruDate = $("#thruDate").jqxDateTimeInput('getDate').getTime();
			data.thruDateTime = thruDate;
		}
		return data;
	}
	
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
	
	var initJqxGridEvent = function(){
		$("#jqxgrid").on('rowdoubleclick', function(event){
			var args = event.args;
			var index = args.rowindex;
			var data = $("#jqxgrid").jqxGrid('getrowdata', index);
			isEdit = true;
			setData(data);
			openJqxWindow($("#newTrainingProviderWindow"));
		});
		
		$("#menuListTraining").on('itemclick', function (event) {
			var data = $('#jqxgrid').jqxGrid('getRowData', $("#jqxgrid").jqxGrid('selectedrowindexes'));
			var tmpStr = $.trim($(args).text());
			if(tmpStr == uiLabelMap.Edit){
				isEdit = true;
				setData(data);
				openJqxWindow($("#newTrainingProviderWindow"));
			} else if(tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgrid').jqxGrid('updatebounddata');
			} else if (tmpStr == uiLabelMap.ExpiredRelationship){
				bootbox.dialog(uiLabelMap.AreYouSureUpdate,
					[{"label": uiLabelMap.CommonCancel,
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
						"callback": function() {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.OK,
						"icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
						"callback": function() {
							thruTrainingProvider(data.partyId);
					}
				}]);
			}
		});
	};
	
	var setData = function(data){
		$("#trainingProviderNameAddNew").val(data.groupName);
		partyId = data.partyId;
		if(data.hasOwnProperty("addressContactMechId")){
			postalAddr.contactMechId = data.addressContactMechId;
			if(data.hasOwnProperty("countryGeoId")){
				postalAddr.countryGeoId = data.countryGeoId;	
			}
			if(data.hasOwnProperty("stateProvinceGeoId")){
				postalAddr.stateProvinceGeoId = data.stateProvinceGeoId;
			}
			if(data.hasOwnProperty("districtGeoId")){
				postalAddr.districtGeoId = data.districtGeoId;
			}
			if(data.hasOwnProperty("wardGeoId")){
				postalAddr.wardGeoId = data.wardGeoId;
			}
			postalAddr.address1 = data.address1;
			$("#trainingProviderAddr").val(postalAddr.address1);
			$("#countryGeoId").val(postalAddr.countryGeoId);
		}
		if(data.hasOwnProperty("emailContactMechId")){
			emailAddr.contactMechId = data.emailContactMechId;
			$("#providerEmail").val(data.emailAddress);
		}
		if(data.hasOwnProperty("phoneContactMechId")){
			telephoneNbr.contactMechId = data.phoneContactMechId;
			if(data.hasOwnProperty("contactNumber")){
				$("#phoneNbr").val(data.contactNumber);
			}
		}
		if(data.hasOwnProperty("websiteContactMechId")){
			websiteUrlAddr.contactMechId = data.websiteContactMechId;
			$("#providerWebUrl").val(data.websiteUrl);
		}
		$("#fromDate").val(data.fromDate);
		if(data.thruDate){
			$("#thruDate").val(data.thruDate);
		}
	};
	
	var setEditMode = function(editMode){
		isEdit = editMode;
	};
	
	return{
		init: init,
		setEditMode: setEditMode
	}
}());

$(document).ready(function(){
	trainingProviderObj.init();
});

function thruTrainingProvider(partyId){
	Loading.show('loadingMacro');
	setTimeout(function(){
		$.ajax({
			url: "thruTrainingProvider",
			type: "POST",
			data: {partyId: partyId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			var value = data["value"];
			$('#jqxgrid').jqxGrid('updatebounddata');
		});
		Loading.hide('loadingMacro');	 
	}, 200);
}

function RemoveFilter(){
	$('#jqxgrid').jqxGrid('clearfilters');
}