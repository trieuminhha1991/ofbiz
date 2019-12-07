$(document).ready(function () {
	permanentResidenceObject.init();
	currentResidenceObject.init();
	phoneMobileObject.init();
	phoneNbrObject.init();
	emailAddrObject.init();
	validator.init();
});

var validator = (function(){
	var init = function(){
		initJqxValidator();
	};
	
	var initJqxValidator = function(){
		$('#editEmail').jqxValidator({
			rules : [
					{
						 input : '#editEmailInput',
						 message : uiLabelMap.EmailFormInvalid,
						 action : 'blur',
						 rule : function(input, commit){
							 var value = $(input).val();
							 if(!value){
								 return true;
							 }else{
								 var tmp = value.split("@");
					   		 if(tmp.length != 2 && tmp.length != 0){
					   			 return false;
					   		 }else{
					   			 if(tmp.length == 0){
					   				 return true;
					   			 }else{
					   				 if(tmp[1].indexOf(".") == 0){
					       				 return false;
					       			 }
					       			 return true;
					   			 }
					   		 }
							 }
						 }
					},
			         {
			        	 input : '#editEmailInput',
			        	 message : uiLabelMap.RequiredOneOrTwoDotChar,
			        	 action : 'blur',
			        	 rule : function(input, commit){
			        		 var value = $(input).val();
			        		 if(!value){
			        			 return true;
			        		 }else{
			        			 var tmp = value.split('@');
			        			 if(tmp.length != 2){
			        				 return false;
			        			 }else{
			        				 if(tmp[1].indexOf(".") < 0){
					        			 return false;
					        		 }else{
					        			 var length = tmp[1].split(".");
					        			 if(length.length > 3){
					        				 return false;
					        			 }
					        			 return true;
					        		 }
			        			 }
			        		 }
			        	 }
			         },
	         ]
		})
	};
	
	return {
		init : init
	}
}());

var permanentResidenceObject = (function(){
		var init = function(){
			initBtnEvent();
			initDropdownlist();
			initJqxDropDownListEvent();
			initJqxInput();
			initJqxValidator();
			initJqxWindow();
			if(typeof (globalVar.defaultCountry) != "undefined" && globalVar.defaultCountry.length > 0){
				$("#countryGeoIdPermRes").jqxDropDownList('selectItem', globalVar.defaultCountry);
			}
		};
		
		var initBtnEvent = function(){
			$("#btnPermSave").click(function(){
				var valid = $("#addPermanentResidenceWindow").jqxValidator('validate');
				if(!valid){
					return;
				}
				$(this).attr("disabled", "disabled");
				var dataSubmit = {};
				var selectCountryItem = $("#countryGeoIdPermRes").jqxDropDownList('getSelectedItem');
				var selectStateItem = $("#stateGeoIdPermRes").jqxDropDownList('getSelectedItem');
				var selectCountyItem = $("#countyGeoIdPermRes").jqxDropDownList('getSelectedItem');
				var text = "";
				dataSubmit.address1 = $("#address1PermRes").val();
				dataSubmit.countryGeoId = selectCountryItem.value;
				dataSubmit.stateProvinceGeoId = selectStateItem.value;
				text = dataSubmit.address1;
				if(selectCountyItem){
					dataSubmit.countyGeoId = selectCountyItem.value;
					text += ", " + selectCountyItem.label;
				}
				text += ", " + selectStateItem.label; 
				text += ", " + selectCountryItem.label; 
				dataSubmit.contactMechPurposeTypeId = "PERMANENT_RESIDENCE";
				dataSubmit.partyId = globalVar.userLogin_partyId;
				var url = "";
				if(personPermanentResidenceContact.contactMechId == "undefined" || !personPermanentResidenceContact.contactMechId){
					url = "createPartyPostalAddr";
				}else{
					dataSubmit.contactMechId = personPermanentResidenceContact.contactMechId;
					url = "updatePartyPostalAddr"
				}
				editPartyPostalAddress(url, dataSubmit, $("#addPermanentResidenceWindow"), $("#btnPermSave"), personPermanentResidenceContact, $("#permanentResidenceAfterAdd"), $("#addPermanentResidence"), text);
			});
			$("#btnPermCancel").click(function(){
				$("#addPermanentResidenceWindow").jqxWindow('close');
			});
			$("#editPermanentResidenceBtn").click(function(event){
				openJqxWindow($("#addPermanentResidenceWindow"));
			});
			if(globalVar.addPermanentResidenceBtn){
				$("#addPermanentResidenceBtn").click(function(event){
					openJqxWindow($("#addPermanentResidenceWindow"));
				});
			}
		};
		var initDropdownlist = function(){
			createJqxDropDownList(geoCountryArr, $("#countryGeoIdPermRes"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#stateGeoIdPermRes"), "geoId", "geoName", 25, "97%");
			createJqxDropDownList([], $("#countyGeoIdPermRes"), "geoId", "geoName", 25, "97%");
		};
		
		var initJqxDropDownListEvent = function(){
			$('#countryGeoIdPermRes').on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {countryGeoId: value};
					var url = 'getAssociatedStateListHR';
					updateSourceJqxDropdownList($('#stateGeoIdPermRes'), data, url, personPermanentResidenceContact.stateProvinceGeoId);
				}
			});
			$("#stateGeoIdPermRes").on('select', function (event){
				var args = event.args;
				if (args) {
					var value = args.item.value;
					var data = {stateGeoId: value};
					var url = 'getAssociatedCountyListHR';
					updateSourceJqxDropdownList($('#countyGeoIdPermRes'), data, url, personPermanentResidenceContact.countyGeoId);
				}
			});
		};
		
		var initJqxInput = function(){
			$("#address1PermRes").jqxInput({width: '96%', height: 20, theme: 'olbius'});
		};
		var initJqxValidator = function(){
			$("#addPermanentResidenceWindow").jqxValidator({
				rules: [
			        {
						input: "#address1PermRes",
						message: uiLabelMap.messageRequire,
						rule: 'required'
					},
					{
						input: "#countryGeoIdPermRes",
						message: uiLabelMap.messageRequire,
						rule: function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{
						input: "#stateGeoIdPermRes",
						message: uiLabelMap.messageRequire,
						rule: function (input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					}
				]
			});
		};
		var initJqxWindow = function(){
			$("#addPermanentResidenceWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
				height: 275, width: 480, isModal: true, theme:'olbius',		
			});	 
			$("#addPermanentResidenceWindow").on('open', function(event){
				if(personPermanentResidenceContact.address1 != 'undefinded'){
					$("#address1PermRes").val(personPermanentResidenceContact.address1);
				}
				if(personPermanentResidenceContact.countryGeoId != 'undefinded'){
					$("#countryGeoIdPermRes").val(personPermanentResidenceContact.countryGeoId);
				}
			});
			$("#addPermanentResidenceWindow").on('close', function(event){
				Grid.clearForm($(this));
			});
			$("#addPermanentResidenceWindow").on('close', function(event){
				$("#addPermanentResidenceWindow").jqxValidator('hide');
			});
		};
		
		return {
			init: init
		};
}());

var currentResidenceObject = (function(){
	var init = function(){
		initBtnEvent();
		initDropdownlist();
		initJqxDropDownListEvent();
		initJqxInput();
		initJqxValidator();
		initJqxWindow();
		if(typeof (globalVar.defaultCountry) != "undefined" && globalVar.defaultCountry.length > 0){
			$("#countryGeoIdCurrRes").jqxDropDownList('selectItem', globalVar.defaultCountry);
		}
	};
	
	var initBtnEvent = function(){
		$("#btnCurrSave").click(function(){
			var valid = $("#addCurrentResidenceWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			$(this).attr("disabled", "disabled");
			var dataSubmit = {};
			var selectCountryItem = $("#countryGeoIdCurrRes").jqxDropDownList('getSelectedItem');
			var selectStateItem = $("#stateGeoIdCurrRes").jqxDropDownList('getSelectedItem');
			var selectCountyItem = $("#countyGeoIdCurrRes").jqxDropDownList('getSelectedItem');
			
			dataSubmit.address1 = $("#address1CurrRes").val();
			dataSubmit.countryGeoId = selectCountryItem.value;
			dataSubmit.stateProvinceGeoId = selectStateItem.value;
			text = dataSubmit.address1;
			if(selectCountyItem){
				dataSubmit.countyGeoId = selectCountyItem.value;
				text += ", " + selectCountyItem.label;
			}
			text += ", " + selectStateItem.label; 
			text += ", " + selectCountryItem.label; 
			dataSubmit.contactMechPurposeTypeId = "CURRENT_RESIDENCE";
			dataSubmit.partyId = globalVar.userLogin_partyId;
			var url = "";
			if(personCurrentResidenceContact.contactMechId == "undefined" || !personCurrentResidenceContact.contactMechId){
				url = "createPartyPostalAddr";
			}else{
				dataSubmit.contactMechId = personCurrentResidenceContact.contactMechId;
				url = "updatePartyPostalAddr"
			}
			editPartyPostalAddress(url, dataSubmit, $("#addCurrentResidenceWindow"), $("#btnCurrSave"), personCurrentResidenceContact, $("#currentResidenceAfterAdd"), $("#addCurrentResidence"), text);
		});
		
		$("#btnCurrCancel").click(function(){
			$("#addCurrentResidenceWindow").jqxWindow('close');
		});
		if(globalVar.addCurrentResidenceBtn){
			$("#addCurrentResidenceBtn").click(function(event){
				openJqxWindow($("#addCurrentResidenceWindow"));
			});
		}
		$("#editCurrentResidenceBtn").click(function(event){
			openJqxWindow($("#addCurrentResidenceWindow"));
		});
	};
	var initDropdownlist = function(){
		createJqxDropDownList(geoCountryArr, $("#countryGeoIdCurrRes"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#stateGeoIdCurrRes"), "geoId", "geoName", 25, "97%");
		createJqxDropDownList([], $("#countyGeoIdCurrRes"), "geoId", "geoName", 25, "97%");
	};
	
	var initJqxDropDownListEvent = function(){
		$('#countryGeoIdCurrRes').on('select', function(event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {countryGeoId: value};
				var url = 'getAssociatedStateListHR';
				updateSourceJqxDropdownList($('#stateGeoIdCurrRes'), data, url, personCurrentResidenceContact.stateProvinceGeoId);
			}
		});
		
		$("#stateGeoIdCurrRes").on('select', function (event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				var data = {stateGeoId: value};
				var url = 'getAssociatedCountyListHR';
				updateSourceJqxDropdownList($('#countyGeoIdCurrRes'), data, url, personCurrentResidenceContact.countyGeoId);
			}
		});
	};
	
	var initJqxInput = function(){
		$("#address1CurrRes").jqxInput({width: '96%', height: 20, theme: 'olbius'});
	};
	var initJqxValidator = function(){
		$("#addCurrentResidenceWindow").jqxValidator({
			rules: [
		        {
					input: "#address1CurrRes",
					message: uiLabelMap.messageRequire,
					rule: 'required'
				},
				{
					input: "#countryGeoIdCurrRes",
					message: uiLabelMap.messageRequire,
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{
					input: "#stateGeoIdCurrRes",
					message: uiLabelMap.messageRequire,
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				}
			]
		});
	};
	
	var initJqxWindow = function(){
		$("#addCurrentResidenceWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
			height: 275, width: 480, isModal: true, theme:'olbius',
			initContent: function(){
				
			}	
		});	 
		$("#addCurrentResidenceWindow").on('open', function(event){
			if(personCurrentResidenceContact.address1 != 'undefinded'){
				$("#address1CurrRes").val(personCurrentResidenceContact.address1);
			}
			if(personCurrentResidenceContact.countryGeoId != 'undefinded'){
				$("#countryGeoIdCurrRes").val(personCurrentResidenceContact.countryGeoId);
			}
		});
		$("#addCurrentResidenceWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		
		$("#addCurrentResidenceWindow").on('close', function(event){
			$("#addCurrentResidenceWindow").jqxValidator('hide');
		});
	};
	
	return {
		init: init
	};
}());

var emailAddrObject = (function(){
	var init = function(){
		initJqxInput();
		initBtnEvent();
	};
	var initJqxInput = function(){
		$("#editEmailInput").jqxInput({width: '100%', height: 22, theme: 'olbius'});
	};
	var initBtnEvent = function(){
		$("#editEmailInput").keydown(function(event){
			if(event.keyCode == 13){//enter press
				$("#saveUpdateEmail").trigger('click');
			}else if(event.keyCode == 27){//Esc press
				$("#cancelUpdateEmail").trigger('click');
			}
		});
		$("#editEmailAddressBtn").click(function(event){
			$("#displayEmail").hide();
			$("#editEmail").show();		
			$("#editEmailInput").jqxInput('val', personEmailContactMech.emailAddress);
			$('#editEmailInput').jqxInput('focus');
		});
		$("#saveUpdateEmail").click(function(event){
			if($('#editEmail').jqxValidator('validate')){
				var dataSubmit = {};
				var emailAddress = $("#editEmailInput").val();
				if(emailAddress && emailAddress.length > 0){
					$("#editEmailInput").jqxInput({disabled: true});
					$(this).attr("disabled", "disabled");
					dataSubmit.emailAddress = emailAddress;
					dataSubmit.partyId = globalVar.userLogin_partyId;
					dataSubmit.contactMechPurposeTypeId = "PRIMARY_EMAIL";
					if(personEmailContactMech.contactMechId != "undefinded"){
						dataSubmit.contactMechId = personEmailContactMech.contactMechId; 
					}
					$.ajax({
						url: "editPartyEmailAddr",
						data: dataSubmit,
						type: 'POST',
						success: function(response){
							if(response.responseMessage == "success"){
								personEmailContactMech.contactMechId = response.contactMechId;
								personEmailContactMech.emailAddress = emailAddress;
								var emailDisplay = $("#displayEmail").find("span")[0];
								$(emailDisplay).html(personEmailContactMech.emailAddress);
								$("#editEmailAddressBtn").show();
							}else{
								bootbox.dialog(response.errorMessage,
										[{
							    		    "label" : uiLabelMap.close,
							    		    "class" : "btn-danger btn-mini icon-remove",
							    		    "callback": function() {
							    		    }
							    		}]		
									);
							}
						},
						complete: function( jqXHR, textStatus){
							$("#saveUpdateEmail").removeAttr("disabled");
							$("#displayEmail").show();
							$("#editEmail").hide();
							$("#editEmailInput").jqxInput({disabled: false});
						}
					});
				}
			}
		});
		$("#cancelUpdateEmail").click(function(event){
			$("#displayEmail").show();
			$("#editEmail").hide();
		});
		if(globalVar.addEmailAddressBtn){
			$("#addEmailAddressBtn").click(function(event){
				$("#displayEmail").hide();
				$("#editEmail").show();	
				$("#editEmailInput").jqxInput('focus');
			});
		}
	};
	return{
		init: init
	};
}());

var phoneNbrObject = (function(){
	var init = function(){
		initJqxInput();
		initBtnEvent();
		initJqxInputEvent();
	};
	
	var initJqxInputEvent = function(){
		$('#phoneNbrContactNbr').keydown(function(event){
			if(event.keyCode == 13){//enter press
				$("#saveUpdatePhoneNbr").trigger('click');
			}else if(event.keyCode == 27){//Esc press
				$("#cancelUpdatePhoneNbr").trigger('click');
			}
		});
	};
	
	var initJqxInput = function(){
		//$("#phoneNbrCountryCode").jqxInput({width: '22%', height: 22, theme: 'olbius', placeHolder: uiLabelMap.commonCountryCode});
		//$("#phoneNbrAreaCode").jqxInput({width: '22%', height: 22, theme: 'olbius', placeHolder: uiLabelMap.CommonAreaCode});
		$("#phoneNbrContactNbr").jqxInput({width: '50%', height: 22, theme: 'olbius', placeHolder: uiLabelMap.PartyPhoneNumber});
	};
	
	var initBtnEvent = function(){
		$("#addPhoneNumberBtn").click(function(event){
			$("#displayPhoneNumber").hide();
			$("#editPhoneNumber").show();
			$("#phoneNbrContactNbr").jqxInput('focus');
		});
		$("#editPhoneNumberBtn").click(function(event){
			$("#displayPhoneNumber").hide();
			$("#editPhoneNumber").show();
			//$("#phoneNbrCountryCode").val(personPhoneNbrContactMech.countryCode);
			//$("#phoneNbrAreaCode").val(personPhoneNbrContactMech.areaCode);
			$("#phoneNbrContactNbr").val(personPhoneNbrContactMech.contactNumber);
			$("#phoneNbrContactNbr").jqxInput('focus');
		});
		
		$("#cancelUpdatePhoneNbr").click(function(event){
			$("#displayPhoneNumber").show();
			$("#editPhoneNumber").hide();
		});
		
		$("#saveUpdatePhoneNbr").click(function(event){
			$(this).attr("disabled", "disabled");
			var dataSubmit = {};
			var contactNumber = $("#phoneNbrContactNbr").val();
			if(contactNumber && contactNumber.length > 0){
				$("#phoneNbrContactNbr").jqxInput({disabled: true});
				dataSubmit.contactNumber = contactNumber;
				dataSubmit.partyId = globalVar.userLogin_partyId;
				dataSubmit.contactMechPurposeTypeId = "PHONE_HOME";
				
				if(personPhoneNbrContactMech.contactMechId != "undefinded"){
					dataSubmit.contactMechId = personPhoneNbrContactMech.contactMechId; 
				}
				$.ajax({
					url: "editPartyTeleNbr",
					data: dataSubmit,
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							personPhoneNbrContactMech.contactMechId = response.contactMechId;
							//personPhoneNbrContactMech.countryCode = $("#phoneNbrCountryCode").val();
							//personPhoneNbrContactMech.areaCode = $("#phoneNbrAreaCode").val();
							personPhoneNbrContactMech.contactNumber = contactNumber;
							var text = "";
							/*if(personPhoneNbrContactMech.countryCode){
								text += personPhoneNbrContactMech.countryCode;
							}
							if(personPhoneNbrContactMech.areaCode){
								text += " ";
								text += personPhoneNbrContactMech.areaCode;
							}*/
							if(personPhoneNbrContactMech.contactNumber){
								//text += " ";
								text += personPhoneNbrContactMech.contactNumber;
							}
							var phoneNbrSpan = $("#displayPhoneNumber").find("span")[0];
							$(phoneNbrSpan).html(text);
							$("#editPhoneNumberBtn").show();
						}else{
							bootbox.dialog(response.errorMessage,
									[{
						    		    "label" : uiLabelMap.close,
						    		    "class" : "btn-danger btn-mini icon-remove",
						    		    "callback": function() {
						    		    }
						    		}]		
								);
						}
					},
					complete: function( jqXHR, textStatus){
						$("#displayPhoneNumber").show();
						$("#editPhoneNumber").hide();
						$("#saveUpdatePhoneNbr").removeAttr("disabled");
						$("#phoneNbrContactNbr").jqxInput({disabled: false});
					}
				});
			}
			
		});
	};
	
	return {
		init:init
	}
}());

var phoneMobileObject = (function(){
	var init = function(){
		initJqxInput();
		initBtnEvent();
	};
	var initJqxInput = function(){
		/*$("#mobileNbrCountryCode").jqxInput({width: '22%', height: 22, theme: 'olbius', 
			placeHolder: uiLabelMap.commonCountryCode});
		$("#mobileNbrAreaCode").jqxInput({width: '22%', height: 22, theme: 'olbius', 
			placeHolder: uiLabelMap.CommonAreaCode});*/
		$("#mobileNbrContactNbr").jqxInput({width: '50%', height: 22, theme: 'olbius', 
			placeHolder: uiLabelMap.PartyPhoneNumber});
	};
	
	var initBtnEvent = function(){
		$("#addMobileNumberBtn").click(function(event){
			$("#displayMobileNumber").hide();
			$("#editMobileNumber").show();
			$("#mobileNbrContactNbr").jqxInput('focus');
		});
		
		$("#editMobileNumberBtn").click(function(event){
			$("#displayMobileNumber").hide();
			$("#editMobileNumber").show();
			//$("#mobileNbrCountryCode").val(personMobileContactMech.countryCode);
			//$("#mobileNbrAreaCode").val(personMobileContactMech.areaCode);
			$("#mobileNbrContactNbr").val(personMobileContactMech.contactNumber);
			$("#mobileNbrContactNbr").jqxInput('focus')
		});
		
		$("#mobileNbrContactNbr").keydown(function(event){
			if(event.keyCode == 13){//enter press
				$("#saveUpdateMobileNbr").trigger('click');
			}else if(event.keyCode == 27){//Esc press
				$("#cancelUpdateMobileNbr").trigger('click');
			}
		});
		
		$("#cancelUpdateMobileNbr").click(function(event){
			$("#displayMobileNumber").show();
			$("#editMobileNumber").hide();
		});
		
		$("#saveUpdateMobileNbr").click(function(event){
			$(this).attr("disabled", "disabled");
			var dataSubmit = {};
			var contactNumber = $("#mobileNbrContactNbr").val();
			if(contactNumber && contactNumber.length > 0){
				$("#mobileNbrContactNbr").jqxInput({disabled: true});
				dataSubmit.contactNumber = contactNumber;
				dataSubmit.partyId = globalVar.userLogin_partyId;
				dataSubmit.contactMechPurposeTypeId = "PRIMARY_PHONE";
				if(personPhoneNbrContactMech.contactMechId != "undefinded"){
					dataSubmit.contactMechId = personMobileContactMech.contactMechId; 
				}
				$.ajax({
					url: "editPartyTeleNbr",
					data: dataSubmit,
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							personMobileContactMech.contactMechId = response.contactMechId;
							//personMobileContactMech.countryCode = $("#mobileNbrCountryCode").val();
							//personMobileContactMech.areaCode = $("#mobileNbrAreaCode").val();
							personMobileContactMech.contactNumber = contactNumber;
							var text = "";
							/*if(personMobileContactMech.countryCode){
								text += personMobileContactMech.countryCode;
							}
							if(personMobileContactMech.areaCode){
								text += " ";
								text += personMobileContactMech.areaCode;
							}*/
							if(personMobileContactMech.contactNumber){
								//text += " ";
								text += personMobileContactMech.contactNumber;
							}
							var phoneNbrSpan = $("#displayMobileNumber").find("span")[0];
							$(phoneNbrSpan).html(text);
							$("#editMobileNumberBtn").show();
						}else{
							bootbox.dialog(response.errorMessage,
									[{
						    		    "label" : uiLabelMap.close,
						    		    "class" : "btn-danger btn-mini icon-remove",
						    		    "callback": function() {
						    		    }
						    		}]		
								);
						}
					},
					complete: function( jqXHR, textStatus){
						$("#displayMobileNumber").show();
						$("#editMobileNumber").hide();
						$("#saveUpdateMobileNbr").removeAttr("disabled");
						$("#mobileNbrContactNbr").jqxInput({disabled: false});
					}
				});
			}
		});
	};
	
	return {
		init:init
	}
}());


function editPartyPostalAddress(url, data, windowEle, btnEle, personContactUpdate, showDiv, hideDiv, text){
	Loading.show('loadingMacro');
	$.ajax({
		url: url,
		data: data,
		type: 'POST',
		success: function(response){
			if(response.responseMessage == "success"){
				//update local data
				personContactUpdate.contactMechId = response.contactMechId;
				personContactUpdate.address1 = data.address1;
				personContactUpdate.countryGeoId = data.countryGeoId;
				personContactUpdate.stateProvinceGeoId = data.stateProvinceGeoId;
				personContactUpdate.countyGeoId = data.countyGeoId;
				showDiv.show();
				hideDiv.hide();
				$(showDiv.find("span")[0]).html(text);
				windowEle.jqxWindow('close');
			}else{
				bootbox.dialog(response.errorMessage,
					[{
		    		    "label" : uiLabelMap.close,
		    		    "class" : "btn-danger btn-mini icon-remove",
		    		    "callback": function() {
		    		    }
		    		}]		
				);
			}
		},
		complete: function( jqXHR, textStatus){
			btnEle.removeAttr("disabled");
			Loading.hide('loadingMacro');
		}
	});
}
