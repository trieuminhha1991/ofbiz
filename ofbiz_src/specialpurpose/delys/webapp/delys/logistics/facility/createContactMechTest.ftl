<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 

<div id="alterpopupWindow">
	<div>
		<div class='row-fluid margin-bottom8 padding-top8'>
			<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.LogToNameInContactMechTitle)}</label>
			</div>  
			<div class="span7">
				<input id="toName">
				</input>
			</div>
		</div>	
		<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.LogContactName)}</label>
			</div>  
			<div class="span7">
				<input id="attnName">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyAddressLine1)}</label>
			</div>  
			<div class="span7">
				<input id="address1">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.LogAddress2ContactMech)}</label>
			</div>  
			<div class="span7">
				<input id="address2">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyCity)}</label>
			</div>  
			<div class="span7">
				<input id="city"></input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.LogCommonNational)}</label>
			</div>  
			<div class="span7">
				<div id="countryGeoId"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.PartyState)}</label>
			</div>  
			<div class="span7">
				<div id="stateProvinceGeoId"></div>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom8 padding-top8'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.LogCityPostalCode)}</label>
			</div>  
			<div class="span7">
				<input id="postalCode"></input>
	   		</div>
	   	</div>
	   	<div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="jqxNotificationCreatePostalAddressError" >
	<div id="notificationCreatePostalAddressError">
	</div>
</div>

<script>

	//Create infoString
	$("#toName").jqxInput({width: 195});
	//Create attnName
	$("#attnName").jqxInput({width: 195});
	//Create address1
	$("#address1").jqxInput({width: 195});
	//Create address2
	$("#address2").jqxInput({width: 195});
	//Create city
	$("#city").jqxInput({width: 195});

	//Create postalCode
	$("#postalCode").jqxInput({width: 195});
	
    $('.text-input').jqxInput();
    // initialize validator.
    $("#alterpopupWindow").jqxWindow({
		height: 545           
    });
	$('#alterpopupWindow').jqxValidator({
        rules: [
               { input: '#toName', message: '${uiLabelMap.DSToName}', action: 'keyup, blur', rule: 'required' },
               { input: '#toName', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
               { input: '#attnName', message: '${uiLabelMap.DSAttnName}', action: 'keyup, blur', rule: 'required' },
               { input: '#attnName', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
               { input: '#address1', message: '${uiLabelMap.DSAddress}', action: 'keyup, blur', rule: 'required' },
               { input: '#address1', message: '${uiLabelMap.DSCheckCharacterValidate255}', action: 'keyup, blur', rule: 'length=3,255' },
               { input: '#city', message: '${uiLabelMap.DSCity}', action: 'keyup, blur', rule: 'required' },
               { input: '#city', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
               { input: '#postalCode', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
               { input: '#postalCode', message: '${uiLabelMap.DSCheckCharacterValidate60}', action: 'keyup, blur', rule: 'length=3,60' },
               {	input: '#countryGeoId', message: '${StringUtil.wrapString(uiLabelMap.LogCheckSelectedDropDownList)}', action: 'valueChanged', 
					rule: function (input, commit) {
						if($('#countryGeoId').val() == null || $('#countryGeoId').val()==''){
						    return false;
						}
						return true;
					}
			   },
			   {	input: '#contactMechPurposeTypeId', message: '${StringUtil.wrapString(uiLabelMap.LogCheckSelectedDropDownList)}', action: 'valueChanged', 
					rule: function (input, commit) {
						if($('#contactMechPurposeTypeId').val() == null || $('#contactMechPurposeTypeId').val()==''){
						    return false;
						}
						return true;
					}
			   },
               ]
    });

$.jqx.theme = 'olbius';  
theme = $.jqx.theme;

$("#jqxNotificationCreatePostalAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreatePostalAddressError", opacity: 0.9, autoClose: true, template: "error" });
// create countryGeoId
$("#countryGeoId").jqxDropDownList({source: geoData, displayMember: "geoName", valueMember: "geoId"});
$("#stateProvinceGeoId").jqxDropDownList('setContent', '${uiLabelMap.CommonNoStatesProvincesExists}'); 
$("#stateProvinceGeoId").jqxDropDownList({ disabled: true}); 
$("#countryGeoId").on('select', function (event) {
    if (event.args) {
        var item = event.args.item;
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
				  $("#stateProvinceGeoId").jqxDropDownList('setContent', '${uiLabelMap.CommonNoStatesProvincesExists}'); 
				  $("#stateProvinceGeoId").jqxDropDownList({ disabled: true}); 
			  } else {
				  $("#stateProvinceGeoId").jqxDropDownList({ selectedIndex: 0,  source: dataTest, displayMember: 'value', valueMember: 'id'});
				  $("#stateProvinceGeoId").jqxDropDownList({ disabled: false}); 
			  }
		  }
	}); 
	
    request.done(function(data) {
	});
    
    
    
});


$("#alterSave").click(function (){
	document.getElementById("alterSave").disabled = false;
	var validate = $('#alterpopupWindow').jqxValidator('validate');
	if(validate != false){
		var facilityId = '${facilityId}';
		var contactMechTypeId = '${contactMechTypeId}';
		var contactMechPurposeTypeId = $('#contactMechPurposeTypeId').val();
		var toName = $('#toName').val();
		var attnName = $('#attnName').val();
		var address1 = $('#address1').val();
		var address2 = $('#address2').val();
		var city = $('#city').val();
		var countryGeoId = $('#countryGeoId').val();
		var stateProvinceGeoId = $('#stateProvinceGeoId').val();
		var postalCode = $('#postalCode').val();
		if(contactMechTypeId == "" || countryGeoId == "" || address1 == "" || city == "" || contactMechPurposeTypeId == ""){
			$("#notificationCreatePostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckContactPurposeIsNotNull)}');
			$("#jqxNotificationCreatePostalAddressError").jqxNotification('open');
		}else{
			document.getElementById("alterSave").disabled = true;
			var request = $.ajax({
				  url: "createFacilityContactMechPostalAddress",
				  type: "POST",
				  data: {facilityId: facilityId, contactMechTypeId: contactMechTypeId, contactMechPurposeTypeId : contactMechPurposeTypeId, toName: toName, attnName: attnName, address1: address1, address2: address2, city: city, countryGeoId: countryGeoId, stateProvinceGeoId: stateProvinceGeoId, postalCode: postalCode},
				  dataType: "json"
			});
			request.done(function(data) {
				var value = data["value"];
				$('#jqxgrid').jqxGrid('updatebounddata');
				if(value == "address2MaxLength"){
					document.getElementById("alterSave").disabled = false;
					$("#notificationCreatePostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckAddress2MaxLength)}');
					$("#jqxNotificationCreatePostalAddressError").jqxNotification('open');
				}
				if(value == "postalCodeNotNumber"){
					document.getElementById("alterSave").disabled = false;
					$("#notificationCreatePostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckPostalCodeInNotNumber)}');
					$("#jqxNotificationCreatePostalAddressError").jqxNotification('open');
		        }
				if(value == "postalCodeInt"){
					document.getElementById("alterSave").disabled = false;
					$("#notificationCreatePostalAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckPostalCodeCheckInt)}');
					$("#jqxNotificationCreatePostalAddressError").jqxNotification('open');
				}
				if(value == "success"){
					document.getElementById("alterSave").disabled = false;
					$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
					$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
		            $("#alterpopupWindow").jqxWindow('close');
		        }
			});
		}
	}
});

$("#alterCancel").click(function (){
	$("#alterpopupWindow").jqxWindow('close');
});

$('#alterpopupWindow').on('close', function (event) {
	document.getElementById("alterSave").disabled = false;
}); 

</script>