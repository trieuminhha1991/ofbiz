
<div class="row-fluid">
	<div class="span12">
		<div class="span2">
			<span class="profile-picture">
				<img class="personal-image" id="avatar${defaultSuffix}" src="/aceadmin/assets/avatars/no-avatar.png" alt="Avatar" style="cursor: pointer;" 
					title="${uiLabelMap.ClickToChangeAvatar}">
			</span>
		</div>
		<div class="span5">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.EmployeeIdShort}</label>
				</div>  
				<div class="span8">
					<input type="text" id="employeeId">
		   		</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.LastName}</label>
				</div>
				<div class="span8">
					<input type="text" id="lastName" name="lastName"/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.MiddleName}</label>
				</div>
				<div class="span8">
					<input type="text" id="middleName" name="middleName"/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FirstName}</label>
				</div>  
				<div class="span8">
					<input type="text" id="firstName" name="firstName"/>
		   		</div>
			</div>	
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.certProvisionId}</label>
				</div>
				<div class="span8">
					<input id="editIdNumber${defaultSuffix}" type="text">
				</div>
			</div>						
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.HrolbiusidIssueDate}</label>
				</div>
				<div class="span8">
					<div id="idIssueDateTime${defaultSuffix}"></div>
				</div>
			</div>				
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.HrolbiusidIssuePlace}</label>
				</div>
				<div class="span8">
					<div id="idIssuePlaceDropDown${defaultSuffix}"></div>
				</div>
			</div>						
		</div> 
		<div class="span5">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.PartyGender}</label>
				</div>
				<div class="span8">
					<div id="gender${defaultSuffix}"></div>
				</div>
			</div>		
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.PartyBirthDate}</label>
				</div>
				<div class="span8">
					<div id="birthDate${defaultSuffix}"></div>
				</div>
			</div>	
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.NativeLand}</label>
				</div>
				<div class="span8">
					<input type="text" id="nativeLandInput${defaultSuffix}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.EthnicOrigin}</label>
				</div>
				<div class="span8">
					<div id="ethnicOriginDropdown${defaultSuffix}" style="display: inline-block; float: left; margin-right: 3px"></div>
					<button id="addOriginBtn${defaultSuffix}" class="btn btn-mini btn-primary" title="${uiLabelMap.CommonAddNew}" style="float: left;">
						<i class="icon-only icon-plus open-sans"></i>
					</button>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.HrolbiusReligion}</label>
				</div>
				<div class="span8">
					<div id="religionDropdown${defaultSuffix}" style="display: inline-block; float: left; margin-right: 3px"></div>
					<button id="addReligionBtn${defaultSuffix}" class="btn btn-mini btn-primary" title="${uiLabelMap.CommonAddNew}" style="float: left;">
						<i class="icon-only icon-plus open-sans"></i>
					</button>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.HrolbiusNationality}</label>
				</div>
				<div class="span8">
					<div id="nationalityDropdown${defaultSuffix}" style="display: inline-block; float: left; margin-right: 3px"></div>
					<button id="addNationalityBtn${defaultSuffix}" class="btn btn-mini btn-primary" title="${uiLabelMap.CommonAddNew}" style="float: left;">
						<i class="icon-only icon-plus open-sans"></i>
					</button>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.MaritalStatus}</label>
				</div>
				<div class="span8">
					<div id="maritalStatusDropdown${defaultSuffix}"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="addOriginWindow${defaultSuffix}" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.EthnicOrigin}</label>
				</div>
				<div class="span8">
					<input type="text" id="ethnicOrigin${defaultSuffix}">
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingOrigin${defaultSuffix}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerOrigin${defaultSuffix}"></div>
				</div>
			</div>	
		</div>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="cancelOrigin${defaultSuffix}">
				<i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonCancel}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="saveOrigin${defaultSuffix}">
				<i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="addReligionWindow${defaultSuffix}" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.Religion}</label>
				</div>
				<div class="span8">
					<input type="text" id="religion${defaultSuffix}">
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingReligion${defaultSuffix}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerReligion${defaultSuffix}"></div>
				</div>
			</div>	
		</div>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="cancelReligion${defaultSuffix}">
				<i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonCancel}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="saveReligion${defaultSuffix}">
				<i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="addNationalityWindow${defaultSuffix}" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.Nationality}</label>
				</div>
				<div class="span8">
					<input type="text" id="nationality${defaultSuffix}">
				</div>
			</div>		
			<div class="row-fluid no-left-margin">
				<div id="loadingNationality${defaultSuffix}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerNationality${defaultSuffix}"></div>
				</div>
			</div>	
		</div>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="cancelNationality${defaultSuffix}">
				<i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonCancel}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="saveNationality${defaultSuffix}">
				<i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>