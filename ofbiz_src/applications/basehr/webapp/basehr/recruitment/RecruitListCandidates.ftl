<#assign hasPermissionAdmin = security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)/>
<div class="hide" id="recruitCandidateListWindow">
	<div>${uiLabelMap.RecruitmentCandidatesList}</div>
	<div class='form-window-container'>
		<div id="appendNotificationCandidate">
			<div id="updateNotificationCandidate">
				<span id="notificationTextCandidate"></span>
			</div>
		</div>
		<div id="recruitCandidateListGrid"></div>
	</div>	
</div>
<#if hasPermissionAdmin>
<div id="addRecruitCandidateWindow" class="hide">
	<div>${uiLabelMap.AddRecruitmentCandidates}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="fueluxWizardCandidate" class="row-fluid hide" data-target="#stepContainerCandidate">
			        <ul class="wizard-steps wizard-steps-square">
			                <li data-target="#generalInfoCandidate" class="active">
			                    <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
			                </li>
			                <li data-target="#contactInfoCandidate">
			                    <span class="step">2. ${uiLabelMap.ContactInformation}</span>
			                </li>
			                <li data-target="#recruitmentCandidateInfo">
			                    <span class="step">3. ${uiLabelMap.ApplyingCandidateInfo}</span>
			                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="stepContainerCandidate">
			    	<div class="step-pane active" id="generalInfoCandidate">
			    		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="asterisk">${uiLabelMap.RecruitmentCandidateId}</label>
									</div>  
									<div class="span8">
										<input type="text" id="candidateId">
							   		</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="asterisk">${uiLabelMap.LastName}</label>
									</div>
									<div class="span8">
										<input type="text" id="lastNameCandidate" name="lastName"/>
									</div>
								</div>							
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.MiddleName}</label>
									</div>
									<div class="span8">
										<input type="text" id="middleNameCandidate" name="middleName"/>
									</div>
								</div>
								
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="asterisk">${uiLabelMap.FirstName}</label>
									</div>  
									<div class="span8">
										<input type="text" id="firstNameCandidate" name="firstName"/>
							   		</div>
								</div>						
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.certProvisionId}</label>
									</div>
									<div class="span8">
										<input id="editIdNumberCandidate" type="text">
									</div>
								</div>						
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusidIssueDate}</label>
									</div>
									<div class="span8">
										<div id="idIssueDateTimeCandidate"></div>
									</div>
								</div>				
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusidIssuePlace}</label>
									</div>
									<div class="span8">
										<div id="idIssuePlaceDropDownCandidate"></div>
									</div>
								</div>						
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.DegreeTraining}</label>
									</div>
									<div class="span8">
										<div id="educationSystemTypeCandidate"></div>
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HRCommonClassification}</label>
									</div>
									<div class="span8">
										<div id="classificationTypeCandidate"></div>
									</div>
								</div>					
							</div> 
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.PartyGender}</label>
									</div>
									<div class="span8">
										<div id="genderCandidate"></div>
									</div>
								</div>		
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.PartyBirthDate}</label>
									</div>
									<div class="span8">
										<div id="birthDateCandidate"></div>
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.NativeLand}</label>
									</div>
									<div class="span8">
										<input type="text" id="nativeLandInputCandidate">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.EthnicOrigin}</label>
									</div>
									<div class="span8">
										<div id="ethnicOriginDropdownCandidate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusReligion}</label>
									</div>
									<div class="span8">
										<div id="religionDropdownCandidate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusNationality}</label>
									</div>
									<div class="span8">
										<div id="nationalityDropdownCandidate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.MaritalStatus}</label>
									</div>
									<div class="span8">
										<div id="maritalStatusDropdownCandidate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HRSpecialization}</label>
									</div>
									<div class="span8">
										<div id="majorCandidate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusGraduationYear}</label>
									</div>
									<div class="span8">
										<div id="graduationYearCandidate"></div>
									</div>
								</div>	
							</div> 
						</div>
			    	</div>
			    	<div class="step-pane" id="contactInfoCandidate">
						<div class="span12">
							<div class="span6">
								<div class="row-fluid" id="permanentResidenceCandidate">
									<div class="span12 boder-all-profile">
										<span class="text-header">${uiLabelMap.PermanentResidence}</span>
										<div class='row-fluid margin-bottom10'>
											<div class='span4 text-algin-right'>
												<label class="">${uiLabelMap.CommonAddress1}</label>
											</div>  
											<div class="span8">
												<input id="paddress1Candidate" type="text">
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.CommonCountry}</label>
											</div>
											<div class="span8">
												<div id="countryGeoIdPermResCandidate"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.CommonCity}</label>
											</div>
											<div class="span8">
												<div id="stateGeoIdPermResCandidate"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
											</div>
											<div class="span8">
												<div id="countyGeoIdPermResCandidate"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.DmsWard}</label>
											</div>
											<div class="span8">
												<div id="wardGeoIdPermResCandidate"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class="row-fluid">
									<div class="span12 boder-all-profile" id="currentResidenceCandidate">
										<span class="text-header">
											${uiLabelMap.CurrentResidence}
											<button title="${StringUtil.wrapString(uiLabelMap.CopyPermanentResidence)}" id="copyPermRes" class="grid-action-button fa-files-o" style="margin: 0; padding: 0 !important"></button>
										</span>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.CommonAddress1}</label>
											</div>
											<div class="span8">
												<input type="text" id="address1CurrResCandidate">
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.CommonCountry}</label>
											</div>
											<div class="span8">
												<div id="countryGeoIdCurrResCandidate"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.CommonCity}</label>
											</div>
											<div class="span8">
												<div id="stateGeoIdCurrResCandidate"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
											</div>
											<div class="span8">
												<div id="countyGeoIdCurrResCandidate"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.DmsWard}</label>
											</div>
											<div class="span8">
												<div id="wardGeoIdCurrResCandidate"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="step-pane" id="recruitmentCandidateInfo">
						<div class="span12 boder-all-profile">
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="">${uiLabelMap.DateReceiveApplication}</label>
								</div>
								<div class="span9">
									<div id="dateReceivingAppl"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="">${uiLabelMap.RecruitmentSource}</label>
								</div>
								<div class="span9">
									<div class="row-fluid">
										<div class="span12">
											<div class="span11">
												<div id="recruitmentSourceCandidate"></div>
											</div>
											<div class="span1" style="margin: 0">
												<button id="addNewRecruitmentSource" title="${uiLabelMap.CommonAddNew}" class="btn btn-mini btn-primary">
													<i class="icon-only icon-plus open-sans" style="font-size: 16px; position: relative;"></i></button>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="">${uiLabelMap.RecruitmentChannel}</label>
								</div>
								<div class="span9">
									<div class="row-fluid">
										<div class="span12">
											<div class="span11">
												<div id="recruitmentChannelCandidate"></div>
											</div>
											<div class="span1" style="margin: 0">
												<button id="addNewRecruitmentChannel" title="${uiLabelMap.CommonAddNew}" class="btn btn-mini btn-primary">
													<i class="icon-only icon-plus open-sans" style="font-size: 16px; position: relative;"></i></button>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="">${uiLabelMap.HRNotes}</label>
								</div>
								<div class="span9">
									<textarea id="commentCandidate"></textarea>
								</div>
							</div>
						</div>
					</div>
			    </div>
			    <div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.HRCommonCreateNew)}" 
						id="btnNextCandidate">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrevCandidate">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="ajaxLoadingCandidate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAjaxCandidate"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</#if>
<div id="contextMenuCandidateList" class="hide">
	<ul>
		<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
    		<li action="moveCandidateToRound"><i class="fa-arrow-circle-up"></i>${uiLabelMap.MoveCandidateToNextRound}</li>
    	</#if>     
    	<li action="recruitCandidateProcess"><i class="fa-line-chart"></i>${uiLabelMap.RecruitmentProcess}</li>     
    	<li action="editCandidateInfo"><i class="fa-male"></i>${uiLabelMap.RecruitmentCandidateInfo}</li>     
    	<li action="editCandidateContact"><i class="fa-phone"></i>${uiLabelMap.PartyContactMechs}</li>     
    </ul>
</div>
<div id="AddNewRecruitmentSourceTypeWindow" class="hide">
	<div>${uiLabelMap.AddNewRecruitmentSourceType}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentSourceTypeName}</label>
				</div>
				<div class="span8">
					<input type="text" id="recruitmentSourceNameNew">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class="span8">
					<div id="commentRecruitmentSourceType"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateSourceTypeRecruit" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateSourceTypeRecruit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelNewRecruitmentSource" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveNewRecruitmentSource">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>
<div id="AddNewRecruitmentChannelTypeWindow" class="hide">
	<div>${uiLabelMap.AddNewRecruitmentChannelType}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentChannelTypeName}</label>
				</div>
				<div class="span8">
					<input type="text" id="recruitmentChannelNameNew">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class="span8">
					<div id="commentRecruitmentChannelType"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateChannelTypeRecruit" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateChannelTypeRecruit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelNewRecruitmentChannelType" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveNewRecruitmentChannelType">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>
<#include "RecruitmenEditCandidateInfo.ftl"/>
<#include "RecruitmenCandidateProcess.ftl"/>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitListCandidates.js"></script>
<#if hasPermissionAdmin>
	<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentCreateCandidate.js"></script>
</#if>