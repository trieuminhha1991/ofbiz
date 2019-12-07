<#if contactMechTypeIdCheck == "WEB_ADDRESS">
	<div id="alterpopupWindow">
		<div style="overflow: hidden;">
			<div class="row-fluid">
				<div class="span12">
					<div class="form-horizontal">
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
							<div>
								<label class="control-label" for="webURLAddress">${uiLabelMap.WebURLAddress}:</label>
							</div>
							<div class="controls">
								<div class="span12">
									<input id="webURLAddress"></input>
								</div>	
							</div>
						</div>
						<div class="control-group no-left-margin">
					    </div>
					    <div class="control-group no-left-margin">
					    </div>
						<div class="control-group no-left-margin">
							<div class="controls">
								<input style="margin-right: 5px;" type="button" id="alterCreateWebURLAddress" value="${uiLabelMap.CommonSave}" />
						       	<input id="alterExitWebURLAddress" type="button" value="${uiLabelMap.CommonCancel}" />  
							</div>      	
					    </div>
					</div>
				</div>
			</div>	
		</div>
	</div>
	<div id="jqxNotificationCreateWebURLAddressError" >
		<div id="notificationCreateWebURLAddressError">
		</div>
	</div>
	
	<script>
		$("#jqxNotificationCreateWebURLAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateWebURLAddressError", opacity: 0.9, autoClose: true, template: "error" });
		//Create infoString
		$("#webURLAddress").jqxInput({width: 195, height: 25});
		// create button alterSave, alterCancel
		$("#alterCreateWebURLAddress").jqxButton({height: 30, width: 80});
		$("#alterExitWebURLAddress").jqxButton({height: 30, width: 80});
		$("#alterpopupWindow").jqxWindow({
			height: 230           
	    });
		$('#alterpopupWindow').jqxValidator({
		    rules: [
						{ input: '#webURLAddress', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
						{ input: '#webURLAddress', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
		           ]
		});
		$("#alterCreateWebURLAddress").click(function (){
			var facilityId = '${facilityId}';
			var contactMechTypeId = '${contactMechTypeId}';
			var contactMechPurposeTypeId = $('#contactMechPurposeTypeId').val();
			var infoString = $('#webURLAddress').val();
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				if(contactMechTypeId == "" || contactMechPurposeTypeId == "" || infoString == ""){
					$("#notificationCreateWebURLAddressError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
					$("#jqxNotificationCreateWebURLAddressError").jqxNotification('open');
				}else{
					var request = $.ajax({
						  url: "createFacilityContactMechByEmailAddress",
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
		
		$("#alterExitWebURLAddress").click(function (){
			$("#alterpopupWindow").jqxWindow('close');
		});
	</script>
<#else>
	<div id="alterpopupWindow">
		<div style="overflow: hidden;">
			
			<div class="row-fluid">
				<div class="span12">
					<div class="form-horizontal">
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
							<div>
								<label class="control-label" for="LDAPAddress">${uiLabelMap.LDAPAddress}:</label>
							</div>
							<div class="controls">
								<div class="span12">
									<input id="LDAPAddress"></input>
								</div>	
							</div>
						</div>
						<div class="control-group no-left-margin">
					    </div>
					    <div class="control-group no-left-margin">
					    </div>
						<div class="control-group no-left-margin">
							<div class="controls">
								<input style="margin-right: 5px;" type="button" id="alterCreateLDAPAddress" value="${uiLabelMap.CommonSave}" />
						       	<input id="alterExitLDAPAddress" type="button" value="${uiLabelMap.CommonCancel}" />  
							</div>      	
					    </div>
					</div>
				</div>
			</div>	
		</div>
	</div>
	
	<div id="jqxNotificationCreateLDAPAddressError" >
		<div id="notificationCreateLDAPAddressError">
		</div>
	</div>
	
	<script>
		//Create infoString
		$("#LDAPAddress").jqxInput({width: 195, height: 25});
		// create button alterSave, alterCancel
		$("#alterCreateLDAPAddress").jqxButton({height: 30, width: 80});
		$("#alterExitLDAPAddress").jqxButton({height: 30, width: 80});
		$("#jqxNotificationCreateLDAPAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateLDAPAddressError", opacity: 0.9, autoClose: true, template: "error" });
		$("#alterpopupWindow").jqxWindow({
			height: 230           
	    });
		$('#alterpopupWindow').jqxValidator({
		    rules: [
						{ input: '#LDAPAddress', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'keyup, blur', rule: 'required' },
						{ input: '#LDAPAddress', message: '${uiLabelMap.DSCheckCharacterValidate}', action: 'keyup, blur', rule: 'length=3,100' },
		           ]
		});
		
		$("#alterCreateLDAPAddress").click(function (){
			var facilityId = '${facilityId}';
			var contactMechTypeId = '${contactMechTypeId}';
			var validate = $('#alterpopupWindow').jqxValidator('validate');
			if(validate != false){
				var contactMechPurposeTypeId = $('#contactMechPurposeTypeId').val();
				var infoString = $('#LDAPAddress').val();
				if(contactMechTypeId == "" || contactMechPurposeTypeId == "" || infoString == ""){
					$("#notificationCreateLDAPAddressError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
					$("#jqxNotificationCreateLDAPAddressError").jqxNotification('open');
				}else{
					var request = $.ajax({
						  url: "createFacilityContactMechByEmailAddress",
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
		$("#alterExitLDAPAddress").click(function (){
			$("#alterpopupWindow").jqxWindow('close');
		});
	</script>
</#if>
