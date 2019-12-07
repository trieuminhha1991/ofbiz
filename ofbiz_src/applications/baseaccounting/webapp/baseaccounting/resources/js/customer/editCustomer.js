var contactCommonInfoObj = (function(){
	var updateSourceJqxDropdownList = function (dropdownlistEle, data, url, selectItem){
    	$.ajax({
    		url: url,
    		data: data,
    		type: 'POST',
    		success: function(response){
    			var listGeo = response.listReturn;
    			if(listGeo && listGeo.length > -1){
    				accutils.updateSourceDropdownlist(dropdownlistEle, listGeo);        				
    				if(selectItem != 'undefinded'){
    					dropdownlistEle.jqxDropDownList('selectItem', selectItem);
    				}
    			}
    		}
    	});
    };
    return{
    	updateSourceJqxDropdownList: updateSourceJqxDropdownList
    }
}());

var contextMenuObj = (function(){
	var init = function(){
		accutils.createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				editCustomerObj.openWindow(data);
			}else if(action == "expire"){
				bootbox.dialog(uiLabelMap.ExpireCustomerRelationshipConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								expireRelationship(data.partyId);	
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}else if(action == "active"){
				activeCustomerRelationship(data.partyId);
			}
		});
		$("#contextMenu").on('shown', function (event) {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			var statusId = dataRecord.statusId;
			if(statusId == "PARTY_ENABLED"){
				$(this).jqxMenu('disable', "editCustomerMenu", true);
				$(this).jqxMenu('disable', "expireCustomerMenu", true);
				$(this).jqxMenu('disable', "activeCustomerMenu", false);
			}else{
				$(this).jqxMenu('disable', "editCustomerMenu", false);
				$(this).jqxMenu('disable', "expireCustomerMenu", false);
				$(this).jqxMenu('disable', "activeCustomerMenu", true);
			}
		});
	};
	var activeCustomerRelationship = function(partyId){
		$.ajax({
			url: 'activeCustomerRelationship',
			data: {partyId: partyId},
			type: 'POST',
			success: function(response){
    			if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
    				var message = typeof(response._ERROR_MESSAGE_) != "undefined"? response._ERROR_MESSAGE_ : response._ERROR_MESSAGE_LIST_[0] 
    				bootbox.dialog(message,
    						[{
    							"label" : uiLabelMap.CommonClose,
    			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
    						}]		
    					);
    			}else{
    				Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
    				$("#jqxgrid").jqxGrid('updatebounddata');
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			Loading.hide('loadingMacro');
    		}
		});
	};
	
	var expireRelationship = function(partyId){
		$.ajax({
			url: 'expireCustomerRelationship',
			data: {partyId: partyId},
			type: 'POST',
			success: function(response){
    			if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
    				var message = typeof(response._ERROR_MESSAGE_) != "undefined"? response._ERROR_MESSAGE_ : response._ERROR_MESSAGE_LIST_[0] 
    				bootbox.dialog(message,
    						[{
    							"label" : uiLabelMap.CommonClose,
    			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
    						}]		
    					);
    			}else{
    				Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
    				$("#jqxgrid").jqxGrid('updatebounddata');
    			}
    		},
    		complete: function(jqXHR, textStatus){
    			Loading.hide('loadingMacro');
    		}
		});
	};
	return{
		init: init
	}
}());

var editCustomerObj = (function(){
	var _partyId = '';
	var _finAccountId = null;
	var _postalAddressData = {};
	var _phoneNumber = {};
	var _faxNumber = {};
	var _emailAddress = {};
	var _taxAuthGeoId = null;
	var _taxAuthPartyId = null;
	var _isEdit = false;
	var init = function(){
		initInput();
		initDropDownList();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#partyCode").jqxInput({width: '94%', height: 20});
		$("#groupName").jqxInput({width: '94%', height: 20});
		$("#partyRepresent").jqxInput({width: '94%', height: 20});
		$("#address1").jqxInput({width: '94%', height: 20});
		$("#primayPhone").jqxInput({width: '94%', height: 20});
		$("#faxNumber").jqxInput({width: '94%', height: 20});
		$("#emailAddress").jqxInput({width: '94%', height: 20});
		$("#taxCode").jqxInput({width: '94%', height: 20});
		$("#finAccountCode").jqxInput({width: '94%', height: 20});
		$("#partyBeneficiaryName").jqxInput({width: '94%', height: 20});
		$("#finAccountName").jqxInput({width: '94%', height: 20});
		$("#fromDate").jqxDateTimeInput({width: '96%', height: 25});
	};
	var initDropDownList = function(){
		accutils.createJqxDropDownList($("#countryGeoId"), globalVar.countryGeoArr, {valueMember: 'geoId', displayMember: 'geoName', width: '96%', height: 25});
		accutils.createJqxDropDownList($("#stateProvinceGeoId"), [], {valueMember: 'geoId', displayMember: 'geoName', width: '96%', height: 25});
		accutils.createJqxDropDownList($("#countyGeoId"), [], {valueMember: 'geoId', displayMember: 'geoName', width: '96%', height: 25});
		accutils.createJqxDropDownList($("#wardGeoId"), [], {valueMember: 'geoId', displayMember: 'geoName', width: '96%', height: 25});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewCustomerWindow"), 800, 490);
	};
	var initEvent = function(){
		$("#addNewCustomerWindow").on('open', function(){
			if(!_isEdit){
				if(globalVar.defaultCountryGeoId){
					$("#countryGeoId").val(globalVar.defaultCountryGeoId);
				}
				var date = new Date();
				$("#fromDate").val(date);
				$("#fromDate").jqxDateTimeInput({disabled: false});
			}else{
				$("#fromDate").jqxDateTimeInput({disabled: true});
			}
		});
		$("#addNewCustomerWindow").on('close', function(){
			Grid.clearForm($("#addNewCustomerWindow"));
			$("#address1").val("");
			$("#countryGeoId").jqxDropDownList('clearSelection');
			$("#stateProvinceGeoId").jqxDropDownList('clearSelection');
			$("#countyGeoId").jqxDropDownList('clearSelection');
			$("#wardGeoId").jqxDropDownList('clearSelection');
			_partyId = '';
			_emailAddress = {};
			_faxNumber = {};
			_phoneNumber = {};
			_postalAddressData = {};
			_isEdit = false;
			_taxAuthGeoId = null;
			_taxAuthPartyId = null;
			_finAccountId = null;
		});
		$('#countryGeoId').on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				accutils.updateSourceDropdownlist($("#countyGeoId"), []);
				accutils.updateSourceDropdownlist($("#wardGeoId"), []);
				contactCommonInfoObj.updateSourceJqxDropdownList($("#stateProvinceGeoId"), data, url, _postalAddressData.stateProvinceGeoId);
			}
		});
		
		$("#stateProvinceGeoId").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				accutils.updateSourceDropdownlist($("#wardGeoId"), []);
				contactCommonInfoObj.updateSourceJqxDropdownList($("#countyGeoId"), data, url, _postalAddressData.districtGeoId);
			}
		});
		$("#countyGeoId").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {districtGeoId: value};
				var url = 'getAssociatedWardListHR';
				contactCommonInfoObj.updateSourceJqxDropdownList($("#wardGeoId"), data, url, _postalAddressData.wardGeoId);
			}
		});
		$("#cancelAddCustomer").click(function(event){
			$("#addNewCustomerWindow").jqxWindow('close');
		});
		$("#saveAddCustomer").click(function(event){
			var valid = $("#addNewCustomerWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(!_isEdit){
				bootbox.dialog(uiLabelMap.CreateCustomerConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createNewCustomer();	
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}else{
				updateCustomer();
			}
		});
	};
	var getData = function(){
		var data = {};
		data.partyCode = $("#partyCode").val();
		data.groupName = $("#groupName").val();
		data.partyRepresent = $("#partyRepresent").val();
		var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
		data.fromDate = fromDate.getTime();;
		data.taxCode = $("#taxCode").val();
		data.finAccountCode = $("#finAccountCode").val();
		data.partyBeneficiary = $("#partyBeneficiaryName").val();
		data.finAccountName = $("#finAccountName").val();
		data.phoneNumber = $("#primayPhone").val();
		data.faxNumber = $("#faxNumber").val();
		data.emailAddress = $("#emailAddress").val();
		var address1 = $("#address1").val();
		if(address1 && address1.length > 0){
			data.address1 = $("#address1").val();
			data.countryGeoId = $("#countryGeoId").val();
			data.stateProvinceGeoId = $("#stateProvinceGeoId").val();
			data.districtGeoId = $("#countyGeoId").val();
			data.wardGeoId = $("#wardGeoId").val();
		}
		return data;
	};
	
	var updateCustomer = function(){
		Loading.show('loadingMacro');
		var data = getData();
		data.partyId = _partyId;
		data.locationContactMechId = _postalAddressData.contactMechId;
		data.emailContactMechId = _emailAddress.contactMechId;
		data.faxContactMechId = _faxNumber.contactMechId;
		data.phoneContactMechId = _phoneNumber.contactMechId;
		if(_finAccountId){
			data.finAccountId = _finAccountId;
		}
		if(_taxAuthGeoId){
			data.taxAuthGeoId = _taxAuthGeoId;
		}
		if(_taxAuthPartyId){
			data.taxAuthPartyId = _taxAuthPartyId;
		}
		$.ajax({
			url: 'updateCustomerACC',
			data: data,
			type: 'POST',
			success: function(response){
    			if(response.responseMessage == 'success'){
    				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
    				$("#addNewCustomerWindow").jqxWindow('close');
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
    			Loading.hide('loadingMacro');
    		}
		});
	};
	
	var createNewCustomer = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'createCustomerACC',
			data: data,
			type: 'POST',
			success: function(response){
    			if(response.responseMessage == 'success'){
    				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
    				$("#addNewCustomerWindow").jqxWindow('close');
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
    			Loading.hide('loadingMacro');
    		}
		});
	};
	var initValidator = function(){
		$("#addNewCustomerWindow").jqxValidator({
			scroll: false,
			rules: [
			        {
			        	input: '#partyCode',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
		        			if(!input.val()){
		        				return false;
		        			}
		        			return true;
			        	}
			        },
			        {
			        	input: '#groupName',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#finAccountCode',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var finAccountName = $("#finAccountName").val()
			        		if(finAccountName && finAccountName.length> 0 && !input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#finAccountName',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var finAccountCode = $("#finAccountCode").val()
			        		if(finAccountCode && finAccountCode.length> 0 && !input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#countryGeoId',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1").val();
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
			        	input: '#stateProvinceGeoId',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var address1 = $("#address1").val();
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
		             input: "#emailAddress",
		             message: uiLabelMap.EmailRequired,
		             action: 'blur',
		             rule: 'email'
		           },
		           {
		        	   input: "#address1",
		        	   message: uiLabelMap.FieldRequired,
		        	   action: 'blur',
		        	   rule: function (input, commit){
		        		   var countryGeoId = $("#countryGeoId").val();
		        		   var stateProvinceGeoId = $("#stateProvinceGeoId").val();
		        		   if(countryGeoId && stateProvinceGeoId){
		        			   if(!input.val()){
			        				return false;
			        			}
		        		   }
		        		   return true;
		        	   }
		           },
			]
		});
	};
	var openWindow = function(data){
		_isEdit = true;
		_partyId = data.partyId;
		if(typeof(data.locationContactMechId) != 'undefined'){
			_postalAddressData.contactMechId = data.locationContactMechId;
			if(typeof(data.locStateProvinceGeoId) != 'undefined'){
				_postalAddressData.stateProvinceGeoId = data.locStateProvinceGeoId;
			}
			if(typeof(data.locDistrictGeoId) != 'undefined'){
				_postalAddressData.districtGeoId = data.locDistrictGeoId;
			}
			if(typeof(data.locWardGeoId) != 'undefined'){
				_postalAddressData.wardGeoId = data.locWardGeoId;
			}
		}
		if(typeof(data.finAccountId) != 'undefined'){
			_finAccountId = data.finAccountId; 
		}
		if(typeof(data.taxAuthPartyId) != 'undefined'){
			_taxAuthPartyId = data.taxAuthPartyId; 
		}
		if(typeof(data.taxAuthGeoId) != 'undefined'){
			_taxAuthGeoId = data.taxAuthGeoId; 
		}
		if(typeof(data.phoneContactMechId) != 'undefined'){
			_phoneNumber.contactMechId = data.phoneContactMechId; 
		}
		if(typeof(data.emailContactMechId) != 'undefined'){
			_emailAddress.contactMechId = data.emailContactMechId;
		}
		if(typeof(data.faxContactMechId) != 'undefined'){
			_faxNumber.contactMechId = data.faxContactMechId;
		}
		$("#partyCode").val(data.partyCode);
		$("#groupName").val(data.fullName);
		$("#partyRepresent").val(data.partyRepresentative);
		$("#fromDate").val(data.fromDate);
		$("#taxCode").val(data.taxCode);
		$("#finAccountCode").val(data.finAccountCode);
		$("#finAccountName").val(data.finAccountName);
		$("#partyBeneficiaryName").val(data.partyBeneficiary);
		$("#primayPhone").val(data.contactNumber);
		$("#faxNumber").val(data.faxNumber);
		$("#emailAddress").val(data.emailAddress);
		$("#address1").val(data.locAddress1);
		if(typeof(data.locCountryGeoId) != 'undefined'){
			$("#countryGeoId").val(data.locCountryGeoId);
		}else if(typeof(globalVar.defaultCountryGeoId) != 'undefined'){
			$("#countryGeoId").val(globalVar.defaultCountryGeoId);
		}
		accutils.openJqxWindow($("#addNewCustomerWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	editCustomerObj.init();
	contextMenuObj.init();
});