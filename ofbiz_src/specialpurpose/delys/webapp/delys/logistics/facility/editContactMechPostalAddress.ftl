<div id="editPostalAddress" class="hide">
	<div>${uiLabelMap.DSEditContactMechInFaciclity}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div class="control-group no-left-margin">
						<input id="contactMechIdEditPostalAddress" type="hidden"></input>
					</div>
					<div id="contentNotificationContentEditPostalAddressError">
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="contactMechPurposeTypeIdInput">${uiLabelMap.PartyContactPurpose}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<div id="contactMechPurposeTypeIdInput"></div>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="toNameEditPostalAddress">${uiLabelMap.FormFieldTitle_toName}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="toNameEditPostalAddress" class="text-input"></input>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="attnNameEditPostalAddress">${uiLabelMap.PartyAttentionName}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="attnNameEditPostalAddress" class="text-input"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="address1EditPostalAddress">${uiLabelMap.PartyAddressLine1}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="address1EditPostalAddress" class="text-input"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="address2EditPostalAddress">${uiLabelMap.PartyAddressLine2}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="address2EditPostalAddress" class="text-input"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="cityEditPostalAddress">${uiLabelMap.PartyCity}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="cityEditPostalAddress" class="text-input"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="countryGeoIdEditPostalAddress">${uiLabelMap.LogCommonNational}</label>
						</div>
						<div class="controls">
							<div class="span12"> 
								<div id="countryGeoIdEditPostalAddress"></div>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="stateProvinceGeoIdEditPostalAddress">${uiLabelMap.PartyState}</label>
						</div>
						<div class="controls">
							<div id="stateProvinceGeoIdEditPostalAddress"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="postalCodeEditPostalAddress">${uiLabelMap.PartyZipCode}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="postalCodeEditPostalAddress" class="text-input"></input>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
				    </div>
				    <div class="control-group no-left-margin">
				    </div>
					<div class="control-group no-left-margin">
						<div class="controls">
					       	<button style="margin-right: 5px;" class='btn btn-primary form-action-button'  id="alterCreateEditPostalAddress"><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					       	<button id="alterExit1EditPostalAddress" class='btn btn-danger form-action-button'><i class='fa-remove'></i>${uiLabelMap.CommonCancel}</button>
						</div>      	
				    </div>
				</div>
			</div>
		
		</div>	
	</div>
</div> 

<div id="editPhoneNumber" class="hide">
	<div>${uiLabelMap.DSEditContactMechInFaciclity}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal"> 
					<div id="contentNotificationContentEditPhoneNumberError">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<div class="span12">
								<input id="contactMechIdEditPhoneNumber" type="hidden"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="contactMechPurposeTypeIdEditPhoneNumber">${uiLabelMap.PartyContactPurpose}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="contactMechPurposeTypeIdEditPhoneNumber"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="countryCodeEditPhoneNumber">${uiLabelMap.CommonCountryCode}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="countryCodeEditPhoneNumber" class="text-input"></input>
							</div>	
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="areaCodeEditPhoneNumber">${uiLabelMap.CommonAreaCode}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="areaCodeEditPhoneNumber" class="text-input"></input>
							</div>	
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="contactNumberEditPhoneNumber">${uiLabelMap.PartyContactNumber}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="contactNumberEditPhoneNumber" class="text-input"></input>
							</div>	
						</div>
					</div>
					<#--
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="extensionEditPhoneNumber">${uiLabelMap.CommonExpand}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="extensionEditPhoneNumber" class="text-input"></input>
							</div>	
						</div>
					</div>
					-->
					<div class="control-group no-left-margin">
				    </div>
				    <div class="control-group no-left-margin">
				    </div>
					<div class="control-group no-left-margin">
						<div class="controls">
					       	<button style="margin-right: 5px;" class='btn btn-primary form-action-button'  id="alterEditPhoneNumbers"><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					       	<button id="alterExitEditPhoneNumbers" class='btn btn-danger form-action-button'><i class='fa-remove'></i>${uiLabelMap.CommonCancel}</button>
						</div>      	
				    </div>
				</div>
			</div>
		</div>	
	</div>
</div>
<div id="editWebAddressOrLDAPAddress" class="hide">
	<div>${uiLabelMap.DSEditContactMechInFaciclity}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div class="control-group no-left-margin">
						<div class="controls">
							<input id="contactMechIdWebAddressOrLDAPAddress" type="hidden"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="contactMechPurposeTypeIdEditWebAddressOrLDAPAddress">${uiLabelMap.PartyContactPurpose}</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="contactMechPurposeTypeIdEditWebAddressOrLDAPAddress"></input>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
					    <div>
					   		<label class="control-label required" for="infoStringIdEditWebAddressOrLDAPAddress">${uiLabelMap.WebURLAddress}</label>
					    </div>
						<div class="controls">
							<div class="span12">
								<input id="infoStringIdEditWebAddressOrLDAPAddress" class="text-input"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
				    </div>
				    <div class="control-group no-left-margin">
				    </div>
					<div class="control-group no-left-margin">
						<div class="controls">
					       	<button style="margin-right: 5px;" class='btn btn-primary form-action-button'  id="alterEditWebAddressOrLDAPAddress"><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					       	<button id="alterExitWebAddressOrLDAPAddress" class='btn btn-danger form-action-button'><i class='fa-remove'></i>${uiLabelMap.CommonCancel}</button>
						</div>      	
				    </div>
				</div>
			</div>
		</div>	
	</div>
</div>

<div id="jqxNotificationEditPhoneNumberError" >
	<div id="notificationEditPhoneNumberError">
	</div>
</div>

<div id="jqxNotificationEditPostalAddressError" >
	<div id="notificationEditPostalAddressError">
	</div>
</div>
<script>
//Create theme
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;
$("#jqxNotificationEditPhoneNumberError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentEditPhoneNumberError", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationEditPostalAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentEditPostalAddressError", opacity: 0.9, autoClose: true, template: "error" });
function loadContactMechTypeIdByDataPostalAddress(contactMechTypeIdInput, descriptionContactMechPurpuseType, contactMechPurposeTypeIdInput, address1Input, address2Input, attnNameInput, countryGeoIdInput, stateProvinceGeoIdInput, postalCodeInput, toNameInput, cityInput, valueContactMechId){
	$("#contactMechPurposeTypeIdInput").jqxDropDownList('setContent', 'Please select....');
	$("#contactMechPurposeTypeIdInput").jqxDropDownList({ disabled: false }); 
	loadContactMechTypePurposeList(contactMechTypeIdInput, descriptionContactMechPurpuseType, contactMechPurposeTypeIdInput, address1Input, address2Input, attnNameInput, countryGeoIdInput, stateProvinceGeoIdInput, postalCodeInput, toNameInput, cityInput, valueContactMechId);
}

var checkBool = false;
function loadContactMechTypePurposeList(contactMechTypeIdInput, descriptionContactMechPurpuseType, contactMechPurposeTypeIdInput, address1Input, address2Input, attnNameInput, countryGeoIdInput, stateProvinceGeoIdInput, postalCodeInput, toNameInput, cityInput, valueContactMechId){
	  checkBool = true;
	  $("#countryGeoIdEditPostalAddress").jqxDropDownList({source: geoData, displayMember: "geoName", valueMember: "geoId"});
	  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList({source: geoData, displayMember: "geoName", valueMember: "geoId"});
	  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList({ disabled: true }); 
	  var contactMechPurpuseTypeByData = getContactMechPurposeTypeId(contactMechPurposeTypeIdInput);
	  $("#contactMechPurposeTypeIdInput").jqxDropDownList('setContent', contactMechPurpuseTypeByData);
	  $("#contactMechPurposeTypeIdInput").val(contactMechPurposeTypeIdInput);
	  $("#contactMechPurposeTypeIdInput").jqxDropDownList({ disabled: true }); 
	  var countryGeoIdData = getGeoIdByGeoDistrict(countryGeoIdInput);
	  $("#countryGeoIdEditPostalAddress").jqxDropDownList('setContent', countryGeoIdInput);
	  $("#countryGeoIdEditPostalAddress").val(countryGeoIdInput);
	  var stateProvinceGeoIdInputByData = getGeoIdByGeoDistrict(stateProvinceGeoIdInput);
	  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList('setContent', stateProvinceGeoIdInputByData);
	  $("#stateProvinceGeoIdEditPostalAddress").val(stateProvinceGeoIdInput);
	  $("#contactMechIdEditPostalAddress").val(valueContactMechId);
	  $("#toNameEditPostalAddress").val(toNameInput);
	  $("#attnNameEditPostalAddress").val(attnNameInput);
	  $("#address1EditPostalAddress").val(address1Input);
	  $("#address2EditPostalAddress").val(address2Input);
	  $("#postalCodeEditPostalAddress").val(postalCodeInput);
	  $("#cityEditPostalAddress").val(cityInput);
}

$("#contactMechIdEditPostalAddress").jqxInput();
$("#toNameEditPostalAddress").jqxInput({width: 195});
//Create attnName
$("#attnNameEditPostalAddress").jqxInput({width: 195});
//Create address1
$("#address1EditPostalAddress").jqxInput({width: 195});
//Create address2
$("#address2EditPostalAddress").jqxInput({width: 195});
//Create city
$("#cityEditPostalAddress").jqxInput({width: 195});

//Create postalCode
$("#postalCodeEditPostalAddress").jqxInput({width: 195});

$('.text-input').jqxInput();

$('#editPostalAddress').jqxValidator({
    rules: [
           { input: '#toNameEditPostalAddress', message: '${uiLabelMap.DSToName}', action: 'keyup, blur', rule: 'required' },
           { input: '#toNameEditPostalAddress', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
           { input: '#attnNameEditPostalAddress', message: '${uiLabelMap.DSAttnName}', action: 'keyup, blur', rule: 'required' },
           { input: '#attnNameEditPostalAddress', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
           { input: '#address1EditPostalAddress', message: '${uiLabelMap.DSAddress}', action: 'keyup, blur', rule: 'required' },
           { input: '#address1EditPostalAddress', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
           { input: '#cityEditPostalAddress', message: '${uiLabelMap.DSCity}', action: 'keyup, blur', rule: 'required' },
           { input: '#cityEditPostalAddress', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
           { input: '#postalCodeEditPostalAddress', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
           { input: '#postalCodeEditPostalAddress', message: '${uiLabelMap.DSCheckCharacterValidate60}', action: 'keyup, blur', rule: 'length=3,60' },
           {
        	   input: '#address2EditPostalAddress', message: '${StringUtil.wrapString(uiLabelMap.DSCheckCharacterValidate255)}', action: 'valueChanged', 
        	   rule: function (input, commit) {
              		if($('#address2EditPostalAddress').val() == null){
              			return true;
              		}
              		if($('#address2EditPostalAddress').jqxInput('maxLength') > 255){
              			return false;
              		}
              		return true;
          	   }
           }
           ]
});
$("#editPostalAddress").jqxWindow({
	maxWidth: 600, minWidth: 450, height: 500, width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterExit1EditPostalAddress"), modalOpacity: 0.7, theme:theme           
});




$("#contactMechIdEditPhoneNumber").jqxInput();
$("#contactMechPurposeTypeIdEditPhoneNumber").jqxInput({width: 195});
$("#editPhoneNumber").jqxWindow({
	maxWidth: 600, minWidth: 400, height: 300, width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterExitEditPhoneNumbers"), modalOpacity: 0.7, theme:theme           
});

$("#countryCodeEditPhoneNumber").jqxInput({width: 195});
$("#areaCodeEditPhoneNumber").jqxInput({ width: 195});
$("#contactNumberEditPhoneNumber").jqxInput({width: 195});
//$("#extensionEditPhoneNumber").jqxInput({width: 195});
$('#editPhoneNumber').jqxValidator({
    rules: [
           { input: '#countryCodeEditPhoneNumber', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
           { input: '#countryCodeEditPhoneNumber', message: '${uiLabelMap.DSCheckCharacterValidate2To10}', action: 'keyup, blur', rule: 'length=2,10' },
           { input: '#areaCodeEditPhoneNumber', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
           { input: '#areaCodeEditPhoneNumber', message: '${uiLabelMap.DSCheckCharacterValidate2To10}', action: 'keyup, blur', rule: 'length=2,10' },
           { input: '#contactNumberEditPhoneNumber', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
           { input: '#contactNumberEditPhoneNumber', message: '${uiLabelMap.DSCheckCharacterValidate2To60}', action: 'keyup, blur', rule: 'length=2,60' },
           ]
});

$('#editPostalAddress').on('close', function (event) { 
	$('#editPostalAddress').jqxValidator('hide');
}); 

function loadContactMechTypeIdByDataPhoneNumber(valueContactMechId, contactMechPurposeTypeIdInput, descriptionContactMechPurpuseType, countryCodeInput, areaCodeInput, contactNumberInput, extendsionInput){
	  var contacMechPurposeTypeIdByData = getContactMechPurposeTypeId(contactMechPurposeTypeIdInput);
	  $("#contactMechPurposeTypeIdEditPhoneNumber").val(contacMechPurposeTypeIdByData);
	  $("#contactMechPurposeTypeIdEditPhoneNumber").jqxInput({ disabled: true }); 
	  $("#contactMechIdEditPhoneNumber").val(valueContactMechId);
	  $("#countryCodeEditPhoneNumber").val(countryCodeInput);
	  $("#areaCodeEditPhoneNumber").val(areaCodeInput);
	  $("#contactNumberEditPhoneNumber").val(contactNumberInput);
//	  $("#extensionEditPhoneNumber").val(extendsionInput);
}

$("#alterEditPhoneNumbers").click(function (){
	var contactMechId = $('#contactMechIdEditPhoneNumber').val();
	var validate = $('#editPhoneNumber').jqxValidator('validate');
	if(validate != false){
		var countryCode = $('#countryCodeEditPhoneNumber').val();
		var areaCode = $('#areaCodeEditPhoneNumber').val();
		var contactNumber = $('#contactNumberEditPhoneNumber').val();
//		var extendsion = $('#extensionEditPhoneNumber').val();
		if(contactMechTypeId == "" || countryCode == "" || areaCode == "" || contactNumber == ""){
			$("#notificationCreatePhoneNumbersError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
			$("#jqxNotificationCreatePhoneNumbersError").jqxNotification('open');
		}else{
			var request = $.ajax({
				  url: "editTelecomNumberInFacility",
				  type: "POST",
				  data: {contactMechId: contactMechId, countryCode: countryCode, areaCode: areaCode, contactNumber: contactNumber, extendsion: extendsion},
				  dataType: "json"
			});
			request.done(function(data) {
				var value = data["value"];
				if(value == "notEdit"){
					$("#editPhoneNumber").jqxWindow('close');
				}
				if(value == "notString"){ 
					$("#notificationEditPhoneNumberError").text('${StringUtil.wrapString(uiLabelMap.LogCheckEditTelecomNumberIsNotNumber)}');
					$("#jqxNotificationEditPhoneNumberError").jqxNotification('open');
				}
				if(value == "success"){
					$('#container').empty();
		        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
					$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
					$('#jqxgrid').jqxGrid('updatebounddata');
		            $("#editPhoneNumber").jqxWindow('close');
				}
			});
		}
	}
});

$('#editPhoneNumber').on('close', function (event) { 
	$('#editPhoneNumber').jqxValidator('hide');
}); 

$("#contactMechIdWebAddressOrLDAPAddress").jqxInput();
$("#contactMechPurposeTypeIdEditWebAddressOrLDAPAddress").jqxInput({width: 200});
$("#infoStringIdEditWebAddressOrLDAPAddress").jqxInput({width: 200});
$("#editWebAddressOrLDAPAddress").jqxWindow({
	maxWidth: 600, minWidth: 400, height: 200, width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterExitWebAddressOrLDAPAddress"), modalOpacity: 0.7, theme:theme           
});
$('#editWebAddressOrLDAPAddress').jqxValidator({
    rules: [
			{ input: '#infoStringIdEditWebAddressOrLDAPAddress', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
			{ input: '#infoStringIdEditWebAddressOrLDAPAddress', message: '${uiLabelMap.DSCheckCharacterValidateEmail}', action: 'keyup', rule: 'email' },
			{ input: '#infoStringIdEditWebAddressOrLDAPAddress', message: '${uiLabelMap.DSCheckCharacterValidate255}', action: 'keyup, blur', rule: 'length=3,255' },
           ]
});
$("#alterEditWebAddressOrLDAPAddress").click(function (){
	var contactMechId = $('#contactMechIdWebAddressOrLDAPAddress').val();
	var infoString = $('#infoStringIdEditWebAddressOrLDAPAddress').val();
	var validate = $('#editWebAddressOrLDAPAddress').jqxValidator('validate');
	if(validate != false){
		$.ajax({
			  url: "editWebAddressOrLDAPAddress",
			  type: "POST",
			  data: {contactMechId: contactMechId, infoString: infoString},
			  dataType: "json"
		}).done(function(data) {
			var value = data["value"];
			if(value == "notEdit"){
				$("#editWebAddressOrLDAPAddress").jqxWindow('close');
			}
			else{
	        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
	        	$('#jqxgrid').jqxGrid('updatebounddata');
	        	$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
	            $("#editWebAddressOrLDAPAddress").jqxWindow('close');
	        }
		});
	}
});	

$('#editWebAddressOrLDAPAddress').on('close', function (event) { 
	$('#editWebAddressOrLDAPAddress').jqxValidator('hide');
});

function loadContactMechTypeIdByDataWebAddressOrLDAPAddress(valueContactMechId, contactMechPurposeTypeIdInput ,descriptionContactMechPurpuseType, infoStringIdInput){
	  $("#contactMechIdWebAddressOrLDAPAddress").val(valueContactMechId);
	  $("#contactMechPurposeTypeIdEditWebAddressOrLDAPAddress").jqxInput({ disabled: true }); 
	  var contactMechPurpuseTypeByData = getContactMechPurposeTypeId(contactMechPurposeTypeIdInput);
	  $("#contactMechPurposeTypeIdEditWebAddressOrLDAPAddress").val(contactMechPurpuseTypeByData);
	  $("#infoStringIdEditWebAddressOrLDAPAddress").val(infoStringIdInput);
}

$("#countryGeoIdEditPostalAddress").on('select', function (event) {
	if(checkBool == false){
		$("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList({ disabled: true}); 
	    if (args) {
		    var index = args.index;
		    var item = args.item;
		    var label = item.label;
		    var value = item.value;
	    }
	    var geoId = item.value;
	    var request = $.ajax({
			  url: "loadGeoAssocListByGeoId",
			  type: "POST",
			  data: {geoId : geoId},
			  dataType: "json",
			  success: function(data) {
				  var listcontactMechPurposeTypeMap = data["listGeoAssocMap"];
				  var contactMechPurposeTypeId = new Array();
				  var description = new Array();
				  var array_keys = new Array();
				  var array_values = new Array();
				  for(var i = 0; i < listcontactMechPurposeTypeMap.length; i++){
					  
					  for (var key in listcontactMechPurposeTypeMap[i]) {
					      array_keys.push(key);
					      array_values.push(listcontactMechPurposeTypeMap[i][key]);
					  }
					  
				  }
				  
				  var dataTest = new Array();
				  for (var j =0; j < array_keys.length; j++){
							var row = {};
							row['id'] = array_keys[j];
							row['value'] = array_values[j];
							dataTest[j] = row;
				  }
				  if (dataTest.length == 0){
					  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList({ disabled: true}); 
					  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList('setContent', '${uiLabelMap.CommonNoStatesProvincesExists}');
				  } else {
					  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList({source: dataTest, displayMember: 'value', valueMember: 'id'});
					  $("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList({ disabled: false}); 
				  }
			  }
		}); 
		
	    request.done(function(data) {
		});
	}
});

var contactMechTypeIdInput;
var infoStringIdInput;
var address1Input;
var address2Input;
var attnNameInput;
var countryGeoIdInput;
var stateProvinceGeoIdInput;
var postalCodeInput;
var toNameInput;
var contactMechPurposeTypeIdInput;
var descriptionContactMechPurpuseType;
var countryCodeInput;
var areaCodeInput;
var contactNumberInput;
var askForNameInput;
var cityInput;
var extendsion;
function loadContactMechDetailByEdit(valueContactMechId){
	contactMechTypeIdInput = "";
	infoStringIdInput = "";
	address1Input = "";
	address2Input = "";
	attnNameInput = "";
	countryGeoIdInput = "";
	postalCodeInput = "";
	stateProvinceGeoIdInput = "";
	contactMechPurposeTypeIdInput = "";
	toNameInput = "";
	countryCodeInput = "";
	areaCodeInput = "";
	contactNumberInput = "";
	askForNameInput = "";
	descriptionContactMechPurpuseType = "";
	cityInput = "";
	extendsionInput = "";
	$.ajax({
		  url: "loadContactMechDetailByEdit",
		  type: "POST",
		  data: {contactMechId : valueContactMechId},
		  dataType: "json",
		  success: function(data) {
			  var listData = data["listContactMechDetailByEdit"];
			  for(var value in listData){
				  contactMechTypeIdInput = listData[value].contactMechTypeId;
				  if(contactMechTypeIdInput == "IP_ADDRESS"){
					  return;
				  }
				  if(contactMechTypeIdInput == "INTERNAL_PARTYID"){
					  return;
				  }
				  if(contactMechTypeIdInput == "ELECTRONIC_ADDRESS"){
					  return;
				  }
				  if(contactMechTypeIdInput == "DOMAIN_NAME"){
					  return;
				  }
				  else{
					  contactMechPurposeTypeIdInput = listData[value].contactMechPurposeTypeId;
					  descriptionContactMechPurpuseType = listData[value].descriptionContactMechPurpuseType;
					  if(contactMechTypeIdInput == "POSTAL_ADDRESS"){
						  address1Input = listData[value].address1;
						  address2Input = listData[value].address2;
						  attnNameInput = listData[value].attnName;
						  countryGeoIdInput = listData[value].countryGeoId;
						  stateProvinceGeoIdInput = listData[value].stateProvinceGeoId;
						  postalCodeInput = listData[value].postalCode;
						  toNameInput = listData[value].toName;
						  cityInput = listData[value].city;
						  loadContactMechTypeIdByDataPostalAddress(contactMechTypeIdInput, descriptionContactMechPurpuseType, contactMechPurposeTypeIdInput ,address1Input, address2Input, attnNameInput, countryGeoIdInput, stateProvinceGeoIdInput, postalCodeInput, toNameInput, cityInput, valueContactMechId);
						  $('#editPostalAddress').jqxWindow('open');
						  return;
					  }
					  if(contactMechTypeIdInput == "TELECOM_NUMBER"){
						  countryCodeInput = listData[value].countryCode;
						  areaCodeInput = listData[value].areaCode;
						  contactNumberInput = listData[value].contactNumber;
						  extendsionInput = listData[value].extendsion;
						  loadContactMechTypeIdByDataPhoneNumber(valueContactMechId, contactMechPurposeTypeIdInput ,descriptionContactMechPurpuseType, countryCodeInput, areaCodeInput, contactNumberInput, extendsionInput);
						  $('#editPhoneNumber').jqxWindow('open');
					  }
					  if(contactMechTypeIdInput == "EMAIL_ADDRESS" || contactMechTypeIdInput == "LDAP_ADDRESS"){
						  infoStringIdInput = listData[value].infoString;
						  loadContactMechTypeIdByDataWebAddressOrLDAPAddress(valueContactMechId, contactMechPurposeTypeIdInput, descriptionContactMechPurpuseType, infoStringIdInput);
						  $('#editWebAddressOrLDAPAddress').jqxWindow('open');
					  }
				  }
			  }
		  }
	}).done(function(data) {
	});
}


$("#alterCreateEditPostalAddress").click(function (){
	$('#alterCreateEditPostalAddress').jqxButton({disabled: false });
	var validate = $('#editPostalAddress').jqxValidator('validate');
	var contactMechId = $('#contactMechIdEditPostalAddress').val();
	var toName = $('#toNameEditPostalAddress').val();
	var attnName = $('#attnNameEditPostalAddress').val();
	var address1 = $('#address1EditPostalAddress').val();
	var address2 = $('#address2EditPostalAddress').val();       
	var city = $('#cityEditPostalAddress').val();
	var countryGeoId = $('#countryGeoIdEditPostalAddress').val();
	var stateProvinceGeoId = $('#stateProvinceGeoIdEditPostalAddress').val();
	var postalCode = $('#postalCodeEditPostalAddress').val();
	if(validate != false){
		if(contactMechTypeId == "" || countryGeoId == "" || address1 == "" || city == ""){
			$("#notificationEditPostalAddressError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
			$("#jqxNotificationEditPostalAddressError").jqxNotification('open');
		}else{
			$('#alterCreateEditPostalAddress').jqxButton({disabled: true });
			var request = $.ajax({
				  url: "editContactMechPostalAddressInFacility",
				  type: "POST",
				  data: {contactMechId: contactMechId, toName: toName, attnName: attnName, address1: address1, address2: address2, city: city, countryGeoId: countryGeoId, stateProvinceGeoId: stateProvinceGeoId, postalCode: postalCode},
				  dataType: "json"
			});
			request.done(function(data) {
				var value = data["value"];
				if(value == "address2MaxLength"){
					$('#alterCreateEditPostalAddress').jqxButton({disabled: false });
					$("#notificationEditPostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckAddress2MaxLength)}');
					$("#jqxNotificationEditPostalAddressError").jqxNotification('open');
				}
				if(value == "notEdit"){
					$('#alterCreateEditPostalAddress').jqxButton({disabled: false });
					$("#editPostalAddress").jqxWindow('close');
				}
				if(value == "postalCodeNotNumber"){
					$('#alterCreateEditPostalAddress').jqxButton({disabled: false });
					$("#notificationEditPostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckPostalCodeInNotNumber)}');
					$("#jqxNotificationEditPostalAddressError").jqxNotification('open');
				}
				if(value == "success"){
					$('#alterCreateEditPostalAddress').jqxButton({disabled: false });
					$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
					$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
		            $("#editPostalAddress").jqxWindow('close');
		            $('#jqxgrid').jqxGrid('updatebounddata');
				}
			});
		}
	}
});

$('#editPostalAddress').on('open', function (event) { 
	checkBool = false;
});

$('#editPostalAddress').on('close', function (event) { 
	$("#countryGeoIdEditPostalAddress").jqxDropDownList('clearSelection'); 
	$("#stateProvinceGeoIdEditPostalAddress").jqxDropDownList('clearSelection'); 
}); 
</script>