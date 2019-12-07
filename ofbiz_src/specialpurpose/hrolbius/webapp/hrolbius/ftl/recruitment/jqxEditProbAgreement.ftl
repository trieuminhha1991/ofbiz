<div id = "createProbAgreementWindow" style="display: none;">
	<div id="windowHeaderProbAgreement">
		<span>
		   ${uiLabelMap.createProbAgreement}
		</span>
	</div>
	<div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewProbAgreement">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="createProbAgreement" id="createProbAgreement">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.DAPartyFrom}:</label>
								<div class="controls">
									<div id="partyIdFrom">
										<div id = "jqxGridPartyIdFrom"></div>
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.DARoleTypeIdFrom}:</label>  
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
								<label class="control-label no-left-margin">${uiLabelMap.inductedStartDate}:</label>  
								<div class="controls">
									<div id="inductedStartDateLabel"></div>
									<input id="inductedStartDate" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.inductedCompletionDate}:</label>  
								<div class="controls">
									<div id="inductedCompletionDateLabel"></div>
									<input id="inductedCompletionDate" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.trafficAllowance}:</label>  
								<div class="controls">
									<div id="trafficAllowanceLabel"></div>
									<input id="trafficAllowance" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.phoneAllowance}:</label>  
								<div class="controls">
									<div id="phoneAllowanceLabel"></div>
									<input id="phoneAllowance" type="hidden"></input>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.DAPartyTo}:</label>
								<div class="controls">
									<div id="partyIdToLabel"></div>
									<input type="hidden" id="partyIdTo"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.Position}:</label>  
								<div class="controls">
									<div id="emplPositionTypeIdLabel"></div>
									<input id="emplPositionTypeId" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.Department}:</label>  
								<div class="controls">
									<div id="partyIdWorkLabel"></div>
									<input id="partyIdWork" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.basicSalary}(VND):</label>  
								<div class="controls">
									<div id="basicSalaryLabel"></div>
									<input id="basicSalary" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.percentBasicSalary}:</label>  
								<div class="controls">
									<div id="percentBasicSalaryLabel"></div>
									<input id="percentBasicSalary" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.recruitmentTypeId}:</label>  
								<div class="controls">
									<div id="recruitmentTypeIdLabel"></div>
									<input id="recruitmentTypeId" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.otherAllowance}:</label>  
								<div class="controls">
									<div id="otherAllowanceLabel"></div>
									<input id="otherAllowance" type="hidden"></input>
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
					<button id="alterCancelNewProbAgreement" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveNewProbAgreement" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>