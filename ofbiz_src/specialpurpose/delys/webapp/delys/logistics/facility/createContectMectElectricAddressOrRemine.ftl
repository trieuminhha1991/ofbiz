<#if contactMechTypeIdCheck == "ELECTRONIC_ADDRESS">
	<div>
		<div style="overflow: hidden;">
			<div class="row-fluid">
				<div class="span12">
					<div class="form-horizontal">
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
							<div>
								<label class="control-label" for="infoString">${uiLabelMap.ElectronicAddress}:</label>
							</div>
							<div class="controls">
								<div class="span12">
									<input id="infoString"></input>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
							<div class="controls">
								<input style="margin-right: 5px;" type="button" id="alterCreateElectronicAddress" value="${uiLabelMap.CommonSave}" />
						       	<input id="alterExitElectronicAddress" type="button" value="${uiLabelMap.CommonCancel}" />  
							</div>      	
					    </div>
					</div>
				</div>
			</div>	
		</div>
	</div> 
	<div id="jqxNotificationCreateElectronicAddressError" >
		<div id="notificationCreateElectronicAddressError">
		</div>
	</div>
	
	<script>
		$(document).ready(function(){
			//create button alterSave, alterCancel
			$("#alterCreateElectronicAddress").jqxButton({height: 30, width: 80});
			$("#alterExitElectronicAddress").jqxButton({height: 30, width: 80});
			
			$("#jqxNotificationCreateElectronicAddressError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateElectronicAddressError", opacity: 0.9, autoClose: true, template: "error" });
			//Create infoString
			$("#infoString").jqxInput({width: 195, height: 25});
			$("#alterpopupWindow").jqxWindow({
				height: 230           
		    });
			$("#alterCreateElectronicAddress").click(function (){
				var facilityId = '${facilityId}';
				var contactMechTypeId = '${contactMechTypeId}';
				var infoString = $('#infoString').val();
				
				if(contactMechTypeId == "" || infoString == ""){
					$("#notificationCreateElectronicAddressError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
					$("#jqxNotificationCreateElectronicAddressError").jqxNotification('open');
				}else{
					var request = $.ajax({
						  url: "createFacilityContactMechByEmailAddress",
						  type: "POST",
						  data: {facilityId: facilityId, contactMechTypeId: contactMechTypeId, infoString: infoString},
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
				        	$("#notificationCreateElectronicAddressError").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
							$("#jqxNotificationCreateElectronicAddressError").jqxNotification('open');
				            $("#alterpopupWindow").jqxWindow('close');
				        }
					});
				}
			});
			
			$("#alterExitElectronicAddress").click(function (){
				$("#alterpopupWindow").jqxWindow('close');
			});
		});
	</script>
<#else>
	<div>
		<div style="overflow: hidden;">
			<div class="row-fluid">
				<div class="span12">
					<div class="form-horizontal">
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
							<div>
								<label class="control-label" for="internalNoteviaPartyIdAddress">${uiLabelMap.InternalNoteviaPartyId}:</label>
							</div>
							<div class="controls">
								<input id="internalNoteviaPartyIdAddress"></input>
							</div>
						</div>
						<div class="control-group no-left-margin">
					    </div>
					    <div class="control-group no-left-margin">
					    </div>
						<div class="control-group no-left-margin">
							<div class="controls">
								<input style="margin-right: 5px;" type="button" id="alterCreateInternalNoteviaPartyIdAddress" value="${uiLabelMap.CommonSave}" />
						       	<input id="alterExitInternalNoteviaPartyIdAddress" type="button" value="${uiLabelMap.CommonCancel}" />  
							</div>      	
					    </div>
					</div>
				</div>
			</div>	
		</div>
	</div>
	<div id="jqxNotificationCreateInternalNoteviaPartyError" >
		<div id="notificationCreateInternalNoteviaPartyError">
		</div>
	</div>
	
	<script>
	$(document).ready(function(){
		$("#jqxNotificationCreateInternalNoteviaPartyError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateInternalNoteviaPartyIdError", opacity: 0.9, autoClose: true, template: "error" });
		//Create internalNoteviaPartyIdAddress
		$("#internalNoteviaPartyIdAddress").jqxInput({width: 195, height: 25});
		// create button alterSave, alterCancel
		$("#alterCreateInternalNoteviaPartyIdAddress").jqxButton({height: 30, width: 80});
		$("#alterExitInternalNoteviaPartyIdAddress").jqxButton({height: 30, width: 80});
		
		$("#alterpopupWindow").jqxWindow({
			height: 230           
	    });
		$("#alterCreateInternalNoteviaPartyIdAddress").click(function (){
			var contactMechTypeId = '${contactMechTypeId}';
			var infoString = $('#internalNoteviaPartyIdAddress').val();
			
			if(contactMechTypeId == "" || infoString == ""){
				$("#notificationCreateInternalNoteviaPartyError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
				$("#jqxNotificationCreateInternalNoteviaPartyError").jqxNotification('open');
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
		
		$("#alterExitInternalNoteviaPartyIdAddress").click(function (){
			$("#alterpopupWindow").jqxWindow('close');
		});
	});
	</script>
</#if>

