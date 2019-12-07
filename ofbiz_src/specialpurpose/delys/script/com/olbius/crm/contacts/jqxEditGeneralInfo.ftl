<div class="basic-form form-horizontal" style="margin-top: 10px">
	<form name="createNewApplicant" id="createNewApplicant">	
		<div class="row-fluid" >
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label asterisk">${uiLabelMap.LastName}:</label></div>
					<div class="span7">
						<input type="text" name="lastName" id="lastName">
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.MiddleName}:</label></div>
					<div class="span7">
						<input type="text" name="middleName" id="middleName">
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label asterisk">${uiLabelMap.FirstName}:</label></div>
					<div class="span7">
						<input type="text" name="firstName" id="firstName">
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.Gender}:</label></div>
					<div class="span7">
						<div id="gender"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.BirthDate}:</label></div>
					<div class="span7">
						<div id="birthDate"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.birthPlace}:</label></div>
					<div class="span7">
						<input id="birthPlace"></input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.Height}(cm):</label></div>
					<div class="span7">
						<div id="height"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.Weight}(kg):</label></div>
					<div class="span7">
						<div id="weight"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.sourceTypeId}:</label></div>
					<div class="span7">
						<div id="sourceTypeId"></div>
					</div>
				</div>
				<!-- <div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.refferedByPartyId}:</label></div>
					<div class="span7">
						<div id="referredByPartyId">
							<div id="jqxReferredByPartyGridId"></div>
						</div>
					</div>
				</div> -->
			</div>
			<div class="span6">
    			<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.IDNumber}:</label></div>
					<div class="span7">
						<input id="idNumber" name="idNumber"></input>
					</div>
				</div>
    			<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.IDIssuePlace}:</label></div>
					<div class="span7">
						<input id="idIssuePlace" name="idIssuePlace"></input>
					</div>
				</div>
    			<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.IDIssueDate}:</label></div>
					<div class="span7">
						<div id="idIssueDate"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.MaritalStatus}:</label></div>
					<div class="span7">
						<div id="maritalStatus"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.NumberChildren} (${uiLabelMap.HRCommonIfHave}):</label></div>
					<div class="span7">
						<div id="numberChildren"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.EthnicOrigin}:</label></div>
					<div class="span7">
						<div id="ethnicOrigin"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.Religion}:</label></div>
					<div class="span7">
						<div id="religion"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="control-label">${uiLabelMap.NativeLand}:</label></div>
					<div class="span7">
						<input id="nativeLand" name="nativeLand"></input>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>