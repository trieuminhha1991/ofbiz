<#assign hasPermissionAdmin = security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)/>
<div id="generalInfoCandidateEditWindow" class="hide">
	<div>${uiLabelMap.RecruitmentCandidateInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.RecruitmentCandidateId}</label>
							</div>  
							<div class="span8">
								<input type="text" id="candidateIdEdit">
					   		</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.LastName}</label>
							</div>
							<div class="span8">
								<input type="text" id="lastNameCandidateEdit" name="lastName"/>
							</div>
						</div>							
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.MiddleName}</label>
							</div>
							<div class="span8">
								<input type="text" id="middleNameCandidateEdit" name="middleName"/>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="asterisk">${uiLabelMap.FirstName}</label>
							</div>  
							<div class="span8">
								<input type="text" id="firstNameCandidateEdit" name="firstName"/>
					   		</div>
						</div>						
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.certProvisionId}</label>
							</div>
							<div class="span8">
								<input id="editIdNumberCandidateEdit" type="number">
							</div>
						</div>						
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.HrolbiusidIssueDate}</label>
							</div>
							<div class="span8">
								<div id="idIssueDateTimeCandidateEdit"></div>
							</div>
						</div>				
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.HrolbiusidIssuePlace}</label>
							</div>
							<div class="span8">
								<div id="idIssuePlaceDropDownCandidateEdit"></div>
							</div>
						</div>						
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.DegreeTraining}</label>
							</div>
							<div class="span8">
								<div id="educationSystemTypeCandidateEdit"></div>
							</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label class="">${uiLabelMap.HRCommonClassification}</label>
							</div>
							<div class="span8">
								<div id="classificationTypeCandidateEdit"></div>
							</div>
						</div>	
					</div>
					
					<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.PartyGender}</label>
						</div>
						<div class="span8">
							<div id="genderCandidateEdit"></div>
						</div>
					</div>		
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.PartyBirthDate}</label>
						</div>
						<div class="span8">
							<div id="birthDateCandidateEdit"></div>
						</div>
					</div>	
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.NativeLand}</label>
						</div>
						<div class="span8">
							<input type="text" id="nativeLandInputCandidateEdit">
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.EthnicOrigin}</label>
						</div>
						<div class="span8">
							<div id="ethnicOriginDropdownCandidateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.HrolbiusReligion}</label>
						</div>
						<div class="span8">
							<div id="religionDropdownCandidateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.HrolbiusNationality}</label>
						</div>
						<div class="span8">
							<div id="nationalityDropdownCandidateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.MaritalStatus}</label>
						</div>
						<div class="span8">
							<div id="maritalStatusDropdownCandidateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.HRSpecialization}</label>
						</div>
						<div class="span8">
							<div id="majorCandidateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.HrolbiusGraduationYear}</label>
						</div>
						<div class="span8">
							<div id="graduationYearCandidateEdit"></div>
						</div>
					</div>	
				</div> 
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingEditCandidateInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxEditCandidateInfo"></div>
				</div>
			</div>
			
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelEditCandidateInfo" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					<#if hasPermissionAdmin>
						<button id="saveEditCandidateInfo" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					</#if>
				</div>
			</div>
		</div>
	</div>	
</div>

<div id="EditContactInfoCandidateWindow" class="hide">
	<div>${uiLabelMap.PartyContactMechs}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class="row-fluid" id="permanentResidenceCandidateEdit">
							<div class="span12 boder-all-profile">
								<span class="text-header">${uiLabelMap.PermanentResidence}</span>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.CommonAddress1}</label>
									</div>  
									<div class="span8">
										<input id="paddress1CandidateEdit" type="text">
							   		</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonCountry}</label>
									</div>
									<div class="span8">
										<div id="countryGeoIdPermResCandidateEdit"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonCity}</label>
									</div>
									<div class="span8">
										<div id="stateGeoIdPermResCandidateEdit"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
									</div>
									<div class="span8">
										<div id="countyGeoIdPermResCandidateEdit"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.DmsWard}</label>
									</div>
									<div class="span8">
										<div id="wardGeoIdPermResCandidateEdit"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="span12 boder-all-profile" id="currentResidenceCandidateEdit">
								<span class="text-header">
									${uiLabelMap.CurrentResidence}
									<button title="${StringUtil.wrapString(uiLabelMap.CopyPermanentResidence)}" id="copyPermResEdit" class="grid-action-button fa-files-o" style="margin: 0; padding: 0 !important"></button>
								</span>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonAddress1}</label>
									</div>
									<div class="span8">
										<input type="text" id="address1CurrResCandidateEdit">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonCountry}</label>
									</div>
									<div class="span8">
										<div id="countryGeoIdCurrResCandidateEdit"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonCity}</label>
									</div>
									<div class="span8">
										<div id="stateGeoIdCurrResCandidateEdit"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
									</div>
									<div class="span8">
										<div id="countyGeoIdCurrResCandidateEdit"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.DmsWard}</label>
									</div>
									<div class="span8">
										<div id="wardGeoIdCurrResCandidateEdit"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>			
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingEditCandidateContact" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxEditCandidateContact"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelEditCandidateContactMech" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					<#if hasPermissionAdmin>
						<button id="saveEditCandidateContactMech" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					</#if>
				</div>
			</div>
		</div>
	</div>	
</div>


<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmenEditCandidateInfo.js"></script>