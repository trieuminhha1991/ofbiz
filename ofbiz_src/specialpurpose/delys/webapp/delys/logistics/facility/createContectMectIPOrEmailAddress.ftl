<#if contactMechTypeIdCheck == "EMAIL_ADDRESS">
	<div id="alterpopupWindow">
		<div>
			<div class='row-fluid margin-bottom8 padding-top8'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyEmailAddress)}</label>
				</div>  
				<div class="span7">
					<input id="emailAddress">
					</input>
				</div>
			</div>	
		   	<div class="form-action">
				<button id="alterExitEmailAddress" class='btn btn-danger form-action-button pull-right' style='height: 30px'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="alterCreateEmailAddress" class='btn btn-primary form-action-button pull-right' style='height: 30px'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button> 
			</div>
		</div>
	</div>
	
	<div id="jqxNotificationCreateEmailAddressError" >
		<div id="notificationCreateEmailAddressError">
		</div>
	</div>
	
	<script>
		//Create EmailAddressWindow jqxWindow
		$("#jqxNotificationCreateEmailAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateEmailAddressError", opacity: 0.9, autoClose: true, template: "error" });
		//Create emailAddress
		$("#emailAddress").jqxInput({width: 195});
		$("#alterpopupWindow").jqxWindow({
			height: 225           
	    });
		
		$('#alterpopupWindow').jqxValidator({
		    rules: [
						{ input: '#emailAddress', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
						{ input: '#emailAddress', message: '${uiLabelMap.DSCheckCharacterValidateEmail}', action: 'keyup', rule: 'email' },
						{ input: '#emailAddress', message: '${uiLabelMap.DSCheckCharacterValidate255}', action: 'keyup, blur', rule: 'length=3,255' },
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
		
		$("#alterCreateEmailAddress").click(function (){
			var facilityId = '${facilityId}';
			var contactMechTypeId = '${contactMechTypeId}';
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				var contactMechPurposeTypeId = $('#contactMechPurposeTypeId').val();
				var infoString = $('#emailAddress').val();
				if(contactMechPurposeTypeId == "" || infoString == ""){
					$("#notificationCreateEmailAddressError").text('${StringUtil.wrapString(uiLabelMap.LogCheckContactPurposeIsNotNull)}');
					$("#jqxNotificationCreateEmailAddressError").jqxNotification('open');
				}else{
					var request = $.ajax({
						  url: "createContactMechEmailAddress",
						  type: "POST",
						  data: {contactMechTypeId: contactMechTypeId, contactMechPurposeTypeId: contactMechPurposeTypeId, facilityId: facilityId ,infoString: infoString},
						  dataType: "html"
					});
					request.done(function(data) {
						$('#jqxgrid').jqxGrid('updatebounddata');
						if(data.responseMessage == "error"){
							$('#jqxNotification').jqxNotification({ template: 'error'});
				            $("#jqxNotification").text(data.errorMessage);
				            $("#jqxNotification").jqxNotification("open");
				        }else{
				        	$('#container').empty();
				        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
							$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
					        $("#alterpopupWindow").jqxWindow('close');
				        }
					});
				}
			}
		});
		
		$("#alterExitEmailAddress").click(function (){
			$("#alterpopupWindow").jqxWindow('close');
		});
	</script>
      
<#else>
</#if>


<#if contactMechTypeIdCheck == "IP_ADDRESS">
<div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
						<div>
							<label class="control-label" for="internetIPAddress">${uiLabelMap.InternetIPAddress}:</label>
						</div>
						<div class="controls">
							<div class="span12">
								<input id="internetIPAddress"></input>
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
				    </div>
				    <div class="control-group no-left-margin">
				    </div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input style="margin-right: 5px;" type="button" id="alterCreateInternetIPAddress" value="${uiLabelMap.CommonSave}" />
					       	<input id="alterExitInternetIPAddress" type="button" value="${uiLabelMap.CommonCancel}" />  
						</div>      	
				    </div>
				</div>
			</div>
		</div>	
	</div>
</div>

<div id="jqxNotificationCreateInternetIPAddressError" >
	<div id="notificationCreateInternetIPAddressError">
	</div>
</div>


	<script>
		//Create EmailAddressWindow jqxWindow
		$("#jqxNotificationCreateInternetIPAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateInternetIPAddressError", opacity: 0.9, autoClose: true, template: "error" });
		//Create infoString
		$("#internetIPAddress").jqxInput({width: 195, height: 25});
		// create button alterSave, alterCancel
		$("#alterCreateInternetIPAddress").jqxButton({height: 30, width: 80});
		$("#alterExitInternetIPAddress").jqxButton({height: 30, width: 80});
		$("#alterpopupWindow").jqxWindow({
			height: 230           
	    });
		$("#alterCreateInternetIPAddress").click(function (){
			var contactMechTypeId = '${contactMechTypeId}';
			var infoString = $('#internetIPAddress').val();
			if(contactMechTypeId == "" || infoString == ""){
				$("#notificationCreateInternetIPAddressError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
				$("#jqxNotificationCreateInternetIPAddressError").jqxNotification('open');
			}else{
				var request = $.ajax({
					  url: "createFacilityContactMechByEmailAddress",
					  type: "POST",
					  data: {contactMechTypeId: contactMechTypeId, infoString: infoString},
					  dataType: "html"
				});
				request.done(function(data) {
					$('#jqxgrid').jqxGrid('updatebounddata');
					if(data.responseMessage == "error"){
						$('#jqxNotification').jqxNotification({ template: 'error'});
			            $("#jqxNotification").text(data.errorMessage);
			            $("#jqxNotification").jqxNotification("open");
			        }else{
			        	$('#container').empty();
			        	$("#notificationCreatePostalAddressSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
						$("#jqxNotificationCreatePostalAddressSuccess").jqxNotification('open');
			            $("#alterpopupWindow").jqxWindow('close');
			        }
				});
			}
		});
		
		$("#alterExitInternetIPAddress").click(function (){
			$("#alterpopupWindow").jqxWindow('close');
		});
	</script>
<#else>
</#if>
