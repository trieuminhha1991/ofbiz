<div id="alterpopupWindow">
	<div>
		<div class='row-fluid margin-bottom10'>
			<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonCountryCode)}</label>
			</div>  
			<div class="span7">
				<input id="countryCode">
				</input>
			</div>
		</div>	
		<div class='row-fluid margin-bottom10'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonAreaCode)}</label>
			</div>  
			<div class="span7">
				<input id="areaCode">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom10'>
	   		<div class='span5 text-algin-right'>
				<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyContactNumber)}</label>
			</div>  
			<div class="span7">
				<input id="contactNumber">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom10'>
	   		<div class='span5 text-algin-right'>
				<label>${StringUtil.wrapString(uiLabelMap.CommonExpand)}</label>
			</div>  
			<div class="span7">
				<input id="extension">
				</input>
	   		</div>
	   	</div>
	   	<div class='row-fluid margin-bottom10' style="height: 10px;">
	   	</div>
	   	<div class="row-fluid">
			<button id="alterExitPhoneNumbers" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterCreatePhoneNumbers" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>


<div id="jqxNotificationCreatePhoneNumbersError" >
	<div id="notificationCreatePhoneNumbersError">
	</div>
</div>
<script>
	//Create InternetIPAddressWindow jqxWindow
    $("#countryCode").jqxInput({width: 195, height: 20});
    $("#areaCode").jqxInput({width: 195, height: 20});
    $("#contactNumber").jqxInput({width: 195, height: 20});
    $("#extension").jqxInput({width: 195, height: 20});

	$("#jqxNotificationCreatePhoneNumbersError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreatePhoneNumbersError", opacity: 0.9, autoClose: true, template: "error" });
	$("#alterpopupWindow").jqxWindow({
		height: 365           
    });
	
	$('#alterpopupWindow').jqxValidator({
        rules: [
               { input: '#countryCode', message: '${uiLabelMap.CheckIsEmptyInfo}', action: 'keyup, blur', rule: 'required' },
               { input: '#countryCode', message: '${uiLabelMap.DSCheckCharacterValidate2To10}', action: 'keyup, blur', rule: 'length=2,10' },
               { input: '#areaCode', message: '${uiLabelMap.CheckIsEmptyInfo}', action: 'keyup, blur', rule: 'required' },
               { input: '#areaCode', message: '${uiLabelMap.DSCheckCharacterValidate2To10}', action: 'keyup, blur', rule: 'length=2,10' },
               { input: '#contactNumber', message: '${uiLabelMap.CheckIsEmptyInfo}', action: 'keyup, blur', rule: 'required' },
               { input: '#contactNumber', message: '${uiLabelMap.DSCheckCharacterValidate2To60}', action: 'keyup, blur', rule: 'length=2,60' },
               {	input: '#contactMechPurposeTypeId', message: '${StringUtil.wrapString(uiLabelMap.CheckIsEmptyInfo)}', action: 'valueChanged', 
					rule: function (input, commit) {
						if($('#contactMechPurposeTypeId').val() == null || $('#contactMechPurposeTypeId').val()==''){
						    return false;
						}
						return true;
					}
			   },
               ]
    });
	
	$("#alterCreatePhoneNumbers").click(function (){
		document.getElementById("alterCreatePhoneNumbers").disabled = false;
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			var facilityId = '${facilityId}';
			var contactMechTypeId = '${contactMechTypeId}';
			var contactMechPurposeTypeId = $('#contactMechPurposeTypeId').val();
			var countryCode = $('#countryCode').val();
			var areaCode = $('#areaCode').val();
			var contactNumber = $('#contactNumber').val();
			var extension = $('#extension').val();
			if(contactMechTypeId == "" || contactMechPurposeTypeId == "" || countryCode == "" || areaCode == ""){
				$("#notificationCreatePhoneNumbersError").text('${StringUtil.wrapString(uiLabelMap.LogCheckContactPurposeIsNotNull)}');
				$("#jqxNotificationCreatePhoneNumbersError").jqxNotification('open');
			}else{
				document.getElementById("alterCreatePhoneNumbers").disabled = true;
				var request = $.ajax({
					  url: "createContactMechTelecomNumber",
					  type: "POST",
					  data: {contactMechTypeId: contactMechTypeId, contactMechPurposeTypeId: contactMechPurposeTypeId, facilityId: facilityId ,countryCode: countryCode, areaCode: areaCode, contactNumber: contactNumber, extension: extension},
					  dataType: "json"
				});
				request.done(function(data) {
					var value = data["value"]
					$('#jqxgrid').jqxGrid('updatebounddata');
					if(value == "notNumber"){
						$("#notificationCreatePhoneNumbersError").text('${StringUtil.wrapString(uiLabelMap.LogCheckTelecomNumberIsNotNumber)}');
						$("#jqxNotificationCreatePhoneNumbersError").jqxNotification('open');
						document.getElementById("alterCreatePhoneNumbers").disabled = false;
			        }else{
			        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.wgcreatesuccess)}');
						$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
			            $("#alterpopupWindow").jqxWindow('close');
			            countryCode = "";
			            areaCode = "";
			            contactNumber = "";
			            extension = "";
			            $('#jqxgrid').jqxGrid('updatebounddata');
			        }
				});
			}
		}
	});
	
	$("#alterExitPhoneNumbers").click(function (){
		$("#alterpopupWindow").jqxWindow('close');
	});
</script>