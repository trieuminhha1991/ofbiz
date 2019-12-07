<div id="editPostalAddress" class="hide">
	<div>${uiLabelMap.EditFaciclityContactMech}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input id="contactMechIdEditPostalAddress" type="hidden"></input>
			<div class="row-fluid">
				<div class="row-fluid margin-bottom10">
					<div id="contentNotificationContentEditPostalAddressError">
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.ContactPurpose}: </div>
						</div>
						<div class="span7">
							<div id="contactMechPurposeTypeIdInput"></div>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div class="asterisk">${uiLabelMap.Address} 1:</div>
						</div>
						<div class="span7">
							<input id="address1EditPostalAddress"></div>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px">${uiLabelMap.Address} 2: </div>
						</div>
						<div class="span7">
							<input id="address2EditPostalAddress"></input>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div class="asterisk">${uiLabelMap.City}:</div>
						</div>
						<div class="span7">
							<input id="cityEditPostalAddress"></input>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div class="asterisk">${uiLabelMap.National}:</div>
						</div>
						<div class="span7">
							<div id="countryGeoIdEditPostalAddress"></div>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px">${uiLabelMap.Provinces}:</div>
						</div>
						<div class="span7">
							<div id="stateProvinceGeoIdEditPostalAddress"></div>
						</div>
					</div>
					<div class="row-fluid margin-top10">
						<div class="span5" style="text-align: right">
							<div class="asterisk">${uiLabelMap.CityPostalCode}:</div>
						</div>
						<div class="span7">
							<input id="postalCodeEditPostalAddress"></input>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="alterExit1EditPostalAddress" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="alterCreateEditPostalAddress" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div> 

<div id="editPhoneNumber" class="hide">
	<div>${uiLabelMap.EditFaciclityContactMech}</div>
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
							<label class="control-label" for="contactMechPurposeTypeIdEditPhoneNumber">${uiLabelMap.ContactPurpose}:</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="contactMechPurposeTypeIdEditPhoneNumber"></input>
							</div>	
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="countryCodeEditPhoneNumber">${uiLabelMap.CountryCode}:</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="countryCodeEditPhoneNumber" class="text-input"></input>
							</div>	
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="areaCodeEditPhoneNumber">${uiLabelMap.AreaCode}:</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="areaCodeEditPhoneNumber" class="text-input"></input>
							</div>	
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label required" for="contactNumberEditPhoneNumber">${uiLabelMap.ContactNumber}:</label>
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
							<label class="control-label" for="extensionEditPhoneNumber">${uiLabelMap.CommonExpand}:</label>
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
	<div>${uiLabelMap.EditFaciclityContactMech}</div>
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
							<label class="control-label" for="contactMechPurposeTypeIdEditWebAddressOrLDAPAddress">${uiLabelMap.ContactPurpose}:</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="contactMechPurposeTypeIdEditWebAddressOrLDAPAddress"></input>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
					    <div>
					   		<label class="control-label required" for="infoStringIdEditWebAddressOrLDAPAddress">${uiLabelMap.WebURLAddress}:</label>
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
	$("#contactMechPurposeTypeIdInput").jqxDropDownList('setContent', '${uiLabelMap.PleaseSelectTitle}');
	$("#contactMechPurposeTypeIdInput").jqxDropDownList({ disabled: false }); 
	loadContactMechTypePurposeList(contactMechTypeIdInput, descriptionContactMechPurpuseType, contactMechPurposeTypeIdInput, address1Input, address2Input, attnNameInput, countryGeoIdInput, stateProvinceGeoIdInput, postalCodeInput, toNameInput, cityInput, valueContactMechId);
}

var checkBool = false;
var stateProvinceGeoIdEditPostalAddressData = "";  
function loadContactMechTypePurposeList(contactMechTypeIdInput, descriptionContactMechPurpuseType, contactMechPurposeTypeIdInput, address1Input, address2Input, attnNameInput, countryGeoIdInput, stateProvinceGeoIdInput, postalCodeInput, toNameInput, cityInput, valueContactMechId){
	  checkBool = true;
	  stateProvinceGeoIdEditPostalAddressData = stateProvinceGeoIdInput;
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
	  $("#address1EditPostalAddress").val(address1Input);
	  $("#address2EditPostalAddress").val(address2Input);
	  $("#postalCodeEditPostalAddress").val(postalCodeInput);
	  $("#cityEditPostalAddress").val(cityInput);
}

$("#contactMechIdEditPostalAddress").jqxInput();
//Create attnName
//Create address1
$("#address1EditPostalAddress").jqxInput({width: 195, height: 20});
//Create address2
$("#address2EditPostalAddress").jqxInput({width: 195, height: 20});
//Create city
$("#cityEditPostalAddress").jqxInput({width: 195, height: 20});

//Create postalCode
$("#postalCodeEditPostalAddress").jqxInput({width: 195, height: 20});

$('.text-input').jqxInput();

$('#editPostalAddress').jqxValidator({
    rules: [
           { input: '#address1EditPostalAddress', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
           { input: '#address1EditPostalAddress', message: '${uiLabelMap.CheckCharacterValidate3To100}', action: 'keyup, blur', rule: 'length=3,100' },
           { input: '#cityEditPostalAddress', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
           { input: '#cityEditPostalAddress', message: '${uiLabelMap.CheckCharacterValidate3To100}', action: 'keyup, blur', rule: 'length=3,100' },
           { input: '#postalCodeEditPostalAddress', message: '${uiLabelMap.CheckCharacterValidate3To60}', action: 'keyup, blur', rule: 'length=3,60' },
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
	maxWidth: 600, minWidth: 450, height: 475, width:1110, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterExit1EditPostalAddress"), modalOpacity: 0.7, theme:theme           
});

$("#contactMechIdEditPhoneNumber").jqxInput();
$("#contactMechPurposeTypeIdEditPhoneNumber").jqxInput({width: 195, height: 20});
$("#editPhoneNumber").jqxWindow({
	maxWidth: 600, minWidth: 400, height: 300, width:1110, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterExitEditPhoneNumbers"), modalOpacity: 0.7, theme:theme           
});

$("#countryCodeEditPhoneNumber").jqxInput({width: 195, height: 20});
$("#areaCodeEditPhoneNumber").jqxInput({ width: 195, height: 20});
$("#contactNumberEditPhoneNumber").jqxInput({width: 195, height: 20});
//$("#extensionEditPhoneNumber").jqxInput({width: 195});
$('#editPhoneNumber').jqxValidator({
    rules: [
           { input: '#countryCodeEditPhoneNumber', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
           { input: '#countryCodeEditPhoneNumber', message: '${uiLabelMap.CheckCharacterValidate2To10}', action: 'keyup, blur', rule: 'length=2,10' },
           { input: '#areaCodeEditPhoneNumber', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
           { input: '#areaCodeEditPhoneNumber', message: '${uiLabelMap.CheckCharacterValidate2To10}', action: 'keyup, blur', rule: 'length=2,10' },
           { input: '#contactNumberEditPhoneNumber', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
           { input: '#contactNumberEditPhoneNumber', message: '${uiLabelMap.CheckCharacterValidate2To60}', action: 'keyup, blur', rule: 'length=2,60' },
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
			$("#notificationCreatePhoneNumbersError").text('${StringUtil.wrapString(uiLabelMap.CheckIsEmptyInfo)}');
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
		        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
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
			{ input: '#infoStringIdEditWebAddressOrLDAPAddress', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
			{ input: '#infoStringIdEditWebAddressOrLDAPAddress', message: '${uiLabelMap.DSCheckCharacterValidateEmail}', action: 'keyup', rule: 'email' },
			{ input: '#infoStringIdEditWebAddressOrLDAPAddress', message: '${uiLabelMap.CheckCharacterValidate3To255}', action: 'keyup, blur', rule: 'length=3,255' },
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
	        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
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

$('#stateProvinceGeoIdEditPostalAddress').on('select', function (event)
{
    var args = event.args;
    if (args) {
	    var index = args.index;
	    var item = args.item;
	    var label = item.label;
	    var value = item.value;
	    stateProvinceGeoIdEditPostalAddressData = value;  
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
function loadContactMechDetailByEdit(valueContactMechId, contactMechPurpuseTypeId){
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
//			  console.log(data["listContactMechDetailByEdit"]);
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
				  	  if(contactMechPurpuseTypeId != listData[value].contactMechPurposeTypeId){
				  	      continue;
				  	  }
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
	var validate = $('#editPostalAddress').jqxValidator('validate');
	var contactMechId = $('#contactMechIdEditPostalAddress').val();
	var address1 = $('#address1EditPostalAddress').val();
	var address2 = $('#address2EditPostalAddress').val();       
	var city = $('#cityEditPostalAddress').val();
	var countryGeoId = $('#countryGeoIdEditPostalAddress').val();
	var postalCode = $('#postalCodeEditPostalAddress').val();
	if(validate != false){
		if(contactMechTypeId == "" || countryGeoId == "" || address1 == "" || city == ""){
			$("#notificationEditPostalAddressError").text('${StringUtil.wrapString(uiLabelMap.CheckIsEmptyInfo)}');
			$("#jqxNotificationEditPostalAddressError").jqxNotification('open');
		}else{
			var request = $.ajax({
				  url: "editContactMechPostalAddressInFacility",
				  type: "POST",
				  data: {contactMechId: contactMechId, address1: address1, address2: address2, city: city, countryGeoId: countryGeoId, stateProvinceGeoId: stateProvinceGeoIdEditPostalAddressData, postalCode: postalCode},
				  dataType: "json"
			});
			request.done(function(data) {
				var value = data["value"];
				if(value == "address2MaxLength"){
					$("#notificationEditPostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckAddress2MaxLength)}');
					$("#jqxNotificationEditPostalAddressError").jqxNotification('open');
				}
				if(value == "notEdit"){
					$("#editPostalAddress").jqxWindow('close');
				}
				if(value == "postalCodeNotNumber"){
					$("#notificationEditPostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckPostalCodeInNotNumber)}');
					$("#jqxNotificationEditPostalAddressError").jqxNotification('open');
				}
				if(value == "success"){
					$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
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
	stateProvinceGeoIdEditPostalAddressData = "";
}); 
</script>