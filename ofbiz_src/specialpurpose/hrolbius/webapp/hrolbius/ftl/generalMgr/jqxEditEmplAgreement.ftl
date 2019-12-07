<div id="wdwNewEmplAgreement" style="display: none;">
	<div id="wdwHeaderNewEmplAgreement">
		<span>
		   ${uiLabelMap.NewEmplAgreement}
		</span>
	</div>
	<div id="wdwContentNewEmplAgreement">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNewEmplAgreement" id="formNewEmplAgreement">	
				<div class="row-fluid" >
					<div class="span12">
						<div class='span6'>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.HRPartyIdFrom}:</label>
								<div class="controls">
									<div id="partyIdFrom">
										<div id="jqxGridPartyIdFrom"></div>
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.HRRoleTypeIdFrom}:</label>  
								<div class="controls">
									<div id="roleTypeIdFrom"></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.repPartyIdFrom}:</label>
								<div class="controls">
									<div id="repPartyIdFrom">
										<div id = "jqxGridRepPartyIdFrom"></div>
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.HREmplPositionType}:</label>  
								<div class="controls">
									<div id="emplPositionTypeId">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.Department}:</label>  
								<div class="controls">
									<div id="partyIdWork">
										<div id="jqxGridPartyIdWork"></div>
									</div>
								</div>
							</div>
						</div>
						<div class='span6'>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.HRPartyIdTo}:</label>  
								<div class="controls">
									<div id="partyIdTo">
										<div id="jqxGridPartyIdTo"></div>
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.HRAgreementType}:</label>  
								<div class="controls">
									<div id="agreementTypeId">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.fromDate}:</label>  
								<div class="controls">
									<div id="fromDate">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.thruDate}:</label>  
								<div class="controls">
									<div id="thruDate">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_Salary}:</label>  
								<div class="controls">
									<div id="salary">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>