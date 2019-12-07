	<div>
		<div style="overflow: hidden;">
			<div class="row-fluid">
				<div class="span12">
					<div class="form-horizontal">
						<div class="control-group no-left-margin">
						</div>
						<div class="control-group no-left-margin">
							<div>
								<label class="control-label" for="internetDomainName">${uiLabelMap.InternetDomainName}: </label>
							</div>
							<div class="controls">
								<div class="span12">
									<input id="internetDomainName"></input>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
					    </div>
					    <div class="control-group no-left-margin">
					    </div>
						<div class="control-group no-left-margin">
							<div class="controls">
								<input style="margin-right: 5px;" type="button" id="alterCreateInternetDomainName" value="${uiLabelMap.CommonSave}" />
						       	<input id="alterExitInternetDomainName" type="button" value="${uiLabelMap.CommonCancel}" />  
							</div>      	
					    </div>
					</div>
				</div>
			</div>	
		</div>
	</div>
	
	<div id="jqxNotificationCreateInternetDomainNameError" >
		<div id="notificationCreateInternetDomainNameError">
		</div>
	</div>
	
	<script>
		$(document).ready(function(){
			$("#jqxNotificationCreateInternetDomainNameError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentCreateInternetDomainNameError", opacity: 0.9, autoClose: true, template: "error" });
			//Create infoString
			$("#internetDomainName").jqxInput({width: 195, height: 25});
			// create button alterSave, alterCancel
			$("#alterCreateInternetDomainName").jqxButton({height: 30, width: 80});
			$("#alterExitInternetDomainName").jqxButton({height: 30, width: 80});
			$("#alterpopupWindow").jqxWindow({
				height: 250           
		    });
			$("#alterCreateInternetDomainName").click(function (){
				var contactMechTypeId = '${contactMechTypeId}';
				var infoString = $('#internetDomainName').val();
				if(contactMechTypeId == "" || infoString == ""){
					$("#notificationCreateInternetDomainNameError").text('${StringUtil.wrapString(uiLabelMap.CheckIsEmptyInfo)}');
					$("#jqxNotificationCreateInternetDomainNameError").jqxNotification('open');
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
			
			$("#alterExitInternetDomainName").click(function (){
				$("#alterpopupWindow").jqxWindow('close');
			});
		});	
	</script>
