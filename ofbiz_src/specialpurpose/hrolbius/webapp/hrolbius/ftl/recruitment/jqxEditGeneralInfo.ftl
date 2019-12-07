<div class="basic-form form-horizontal" style="margin-top: 10px">
	<form name="createNewApplicant" id="createNewApplicant">	
		<div class="row-fluid" >
    		<div class="span12">
    			<div class="span6">
    				<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.LastName}:</label>
						<div class="controls">
							<input type="text" name="lastName" id="lastName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.MiddleName}:</label>
						<div class="controls">
							<input type="text" name="middleName" id="middleName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.FirstName}:</label>
						<div class="controls">
							<input type="text" name="firstName" id="firstName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Gender}:</label>
						<div class="controls">
							<div id="gender"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.BirthDate}:</label>
						<div class="controls">
							<div id="birthDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.birthPlace}:</label>
						<div class="controls">
							<input id="birthPlace"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Height}(cm):</label>
						<div class="controls">
							<div id="height"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Weight}(kg):</label>
						<div class="controls">
							<div id="weight"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.refferedByPartyId}:</label>
						<div class="controls">
							<div id="referredByPartyId">
								<div id="jqxReferredByPartyGridId"></div>
							</div>
						</div>
					</div>
    			</div>
    			<div class="span6">
	    			<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.IDNumber}:</label>
						<div class="controls">
							<input id="idNumber" name="idNumber"></input>
						</div>
					</div>
	    			<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.IDIssuePlace}:</label>
						<div class="controls">
							<input id="idIssuePlace" name="idIssuePlace"></input>
						</div>
					</div>
	    			<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.IDIssueDate}:</label>
						<div class="controls">
							<div id="idIssueDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.MaritalStatus}:</label>
						<div class="controls">
							<div id="maritalStatus"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.NumberChildren} (${uiLabelMap.HRCommonIfHave}):</label>
						<div class="controls">
							<div id="numberChildren"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.EthnicOrigin}:</label>
						<div class="controls">
							<div id="ethnicOrigin"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.Religion}:</label>
						<div class="controls">
							<div id="religion"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.NativeLand}:</label>
						<div class="controls">
							<input id="nativeLand" name="nativeLand"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.sourceTypeId}:</label>
						<div class="controls">
							<div id="sourceTypeId"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>