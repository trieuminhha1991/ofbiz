<script type="text/javascript" src="/hrresources/js/profile/generalInfo.js"></script>
<@jqOlbCoreLib />
<div id="container"></div>
<div id="jqxNotification">
	<div id="notificationContent">
	</div>
</div>
<div id="personal-info" class="tab-pane active widget-body">
	<div id="personal-info-1" class="row-fluid mgt20">
		<div class="span2" style="position: relative;">
			<span class="profile-picture">
				<#if !personalImage?has_content>
					<#assign personalImage = "/aceadmin/assets/avatars/no-avatar.png">
				</#if>
				<img class="personal-image" id="personal-image" src="${personalImage}" alt="Avatar" style="cursor: pointer;" title="${uiLabelMap.ClickToChangeAvatar}">
				
			</span>
			<a href="javascript:void(0)" id="changeFullName" title="${StringUtil.wrapString(uiLabelMap.ClickToChangeName)}" class="btn btn-small btn-block btn-primary">
				${lookupPerson.lastName?if_exists} ${lookupPerson.middleName?if_exists} ${lookupPerson.firstName?if_exists}
			</a>
			<div class="row-fluid no-left-margin">
				<div id="loadingUpdateAvatar" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerUpdateAvatar"></div>
				</div>
			</div>
		</div>
		<div class="span10">
			<div class="row-fluid">
				<div class="span12">
					<div class="profile-user-info">
						<div class="row-fluid no-left-margin borderBottom">
							<div class="span2 lineHeight30 labelColor"><span class="labelColor">${uiLabelMap.userLoginId}</span></div>
							<div class="span6 lineHeight30" onmouseenter="" style="margin-left: 47px !important">
								<span>
									${userLogin.userLoginId}&nbsp;
									(<a href="javascript:void(0)" id="changePassword">${uiLabelMap.HRChangePassword}</a>)
								</span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<form id="basicInformation" class="form-horizontal">
							<input type="hidden" name="partyId" value="${parameters.partyId}" />
							<div class="profile-user-info">
								<div class="row-fluid no-left-margin borderBottom">
									<div class="span5 lineHeight30 labelColor"><span class="labelColor">${uiLabelMap.EmployeeId}</span></div>
									<div class="span5 lineHeight30" onmouseenter="">
										<span>${lookupPerson.partyCode}</span>
									</div>
								</div>
								
								<div class="row-fluid no-left-margin borderBottom" 
									onmouseenter="showEditDiv($('#editGender'))" onmouseleave="hideEditDiv($('#editGender'))">				
									<div class="span5 lineHeight30">
										<span class="labelColor">${uiLabelMap.FormFieldTitle_gender}</span>
									</div>
									<div class="span7">
										<div class="row-fluid" id="genderPersonInfo">
											<div class="span9 lineHeight30">
												<span>
													<#if lookupPerson.gender?exists>
														<#assign gender = delegator.findOne("Gender", Static["org.ofbiz.base.util.UtilMisc"].toMap("genderId", lookupPerson.gender), false)>
														${gender.description}
													</#if>
												</span>
											</div>
											<div class="span3" id="editGender" style="display: none; text-align: right;">
												<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
													title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}" 
													onclick="openEditPersonInfo($('#editGenderInfo'), $('#genderPersonInfo'), $('#editGenderDropdownlist'), 'gender')">
												</button>
											</div>
										</div>
										<div class="row-fluid" id="editGenderInfo" style="display: none">
											<div class="span8" style="margin-top: 2px">
												<div id="editGenderDropdownlist"></div>
											</div>
											<div class="span4" style="text-align: right;">
												<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
													onclick="updatePersonInfo($('#genderPersonInfo'), $('#editGenderInfo'), 'gender', $('#editGenderDropdownlist'), 'gender', 'dropdownlist')"></button>
												<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
													onclick="cancelEdit($('#genderPersonInfo'), $('#editGenderInfo'))"></button>
											</div>
										</div>
									</div>
								</div>
								<div class="row-fluid no-left-margin borderBottom"
									onmouseenter="showEditDiv($('#editBirthDate'))" onmouseleave="hideEditDiv($('#editBirthDate'))">
									<div class="span5 lineHeight30">
										<span class="labelColor">
											${uiLabelMap.FormFieldTitle_birthDate}
										</span>
									</div>
									<div class="span7">
										<div class="row-fluid" id="birthDatePersonInfo">
											<div class="span9 lineHeight30">
												<span>
												<#if lookupPerson.birthDate?exists>
													<#assign birthDate = Static["com.olbius.basehr.util.DateUtil"].convertDate(lookupPerson.birthDate) />
													${birthDate?if_exists}
												</#if>
												</span>
											</div>
											<div class="span3" id="editBirthDate" style="display: none; text-align: right;">
												<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
													title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
													onclick="openEditPersonInfo($('#editBirthDateInfo'), $('#birthDatePersonInfo'), $('#editBirthDateTimeInput'), 'birthDate', 'datetimeinput')"></button>
											</div>
										</div>
										<div class="row-fluid" id="editBirthDateInfo" style="display: none">
											<div class="span8" style="margin-top: 2px">
												<div id="editBirthDateTimeInput"></div>
											</div>
											<div class="span4" style="text-align: right;">
												<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
													onclick="updatePersonInfo($('#birthDatePersonInfo'), $('#editBirthDateInfo'), 'birthDate', $('#editBirthDateTimeInput'), 'birthDate', 'datetimeinput')"></button>
												<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
													onclick="cancelEdit($('#birthDatePersonInfo'), $('#editBirthDateInfo'))"></button>
											</div>
										</div>
									</div>
								</div>
								<div class="row-fluid no-left-margin borderBottom" 
									onmouseenter="showEditDiv($('#editIdNumber'))" onmouseleave="hideEditDiv($('#editIdNumber'))">
									<div class="span5 lineHeight30">
										<span class="labelColor">
											${uiLabelMap.certProvisionId}
										</span>
									</div>
									<div class="span7">
										<div class="row-fluid" id="idNumberPersonInfo">
											<div class="span9 lineHeight30">
												<span>
													<#if lookupPerson.idNumber?exists>
														${lookupPerson.idNumber}
													</#if>
												</span>
											</div>
											<div class="span3" id="editIdNumber" style="display: none; text-align: right;">
												<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
													title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
													onclick="openEditPersonInfo($('#editIdNumberInfo'), $('#idNumberPersonInfo'), $('#editIdNumberInput'), 'idNumber')"></button>
											</div>
										</div>
										<div class="row-fluid" id="editIdNumberInfo" style="display: none">
											<div class="span8" style="margin-top: 2px">
												<input type="text" id="editIdNumberInput"/>
											</div>
											<div class="span4" style="text-align: right;">
												<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
													onclick="updatePersonInfo($('#idNumberPersonInfo'), $('#editIdNumberInfo'), 'idNumber', $('#editIdNumberInput'), 'idNumber', 'numberinput')"></button>
												<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
													onclick="cancelEdit($('#idNumberPersonInfo'), $('#editIdNumberInfo'))"></button>
											</div>
										</div>
									</div>
								</div>
								<div class="row-fluid no-left-margin borderBottom"
									onmouseenter="showEditDiv($('#editIdIssuePlace'))" onmouseleave="hideEditDiv($('#editIdIssuePlace'))">
									<div class="span5 lineHeight30">
										<span class="labelColor">
											${uiLabelMap.HrolbiusidIssuePlace}
										</span>
									</div>
									<div class="span7">
										<div class="row-fluid" id="idIssuePlacePersonInfo">
											<div class="span9 lineHeight30">
												<span>
													<#if lookupPerson.idIssuePlace?exists>
														<#assign idIssuePlace= delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",lookupPerson.idIssuePlace), false) >
														${idIssuePlace.geoName}
													</#if>
												</span> 
											</div>
											<div class="span3" id="editIdIssuePlace" style="display: none; text-align: right;">
												<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
													title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
													onclick="openEditPersonInfo($('#editIdIssuePlaceInfo'), $('#idIssuePlacePersonInfo'), $('#editIdIssuePlaceDropDownList'), 'idIssuePlace')"></button>
											</div>
										</div>
										<div class="row-fluid" id="editIdIssuePlaceInfo" style="display: none">
											<div class="span8" style="margin-top: 2px">
												<div id="editIdIssuePlaceDropDownList"></div>
											</div>
											<div class="span4" style="text-align: right;">
												<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
													onclick="updatePersonInfo($('#idIssuePlacePersonInfo'), $('#editIdIssuePlaceInfo'), 'idIssuePlace', $('#editIdIssuePlaceDropDownList'), 'idIssuePlace', 'dropdownlist')"></button>
												<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
													onclick="cancelEdit($('#idIssuePlacePersonInfo'), $('#editIdIssuePlaceInfo'))"></button>
											</div>
										</div>
									</div>
								</div>
								<div class="row-fluid no-left-margin borderBottom"
									onmouseenter="showEditDiv($('#editIdIssueDate'))" onmouseleave="hideEditDiv($('#editIdIssueDate'))">
									<div class="span5 lineHeight30">
										<span class="labelColor">
											${uiLabelMap.HrolbiusidIssueDate}
										</span>
									</div>
									<div class="span7">
										<div class="row-fluid" id="idIssueDatePersonInfo">
											<div class="span9 lineHeight30">
												<span>
													<#if lookupPerson.idIssueDate?exists>
														<#assign idIssueDate = Static["com.olbius.basehr.util.DateUtil"].convertDate(lookupPerson.idIssueDate?if_exists) />
														${idIssueDate?if_exists}								
													</#if>
												</span>
											</div>
											<div class="span3" id="editIdIssueDate" style="display: none; text-align: right;">
												<button class="grid-action-button icon-edit" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
													onclick="openEditPersonInfo($('#editIdIssueDateInfo'), $('#idIssueDatePersonInfo'), $('#editIdIssueDateTimeInput'), 'idIssueDate', 'datetimeinput')"></button>
											</div>
										</div>
										<div class="row-fluid" id="editIdIssueDateInfo" style="display: none">
											<div class="span8" style="margin-top: 2px">
													<div id="editIdIssueDateTimeInput"></div>
											</div>
											<div class="span4" style="text-align: right;">
												<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
													onclick="updatePersonInfo($('#idIssueDatePersonInfo'), $('#editIdIssueDateInfo'), 'idIssueDate', $('#editIdIssueDateTimeInput'), 'idIssueDate', 'datetimeinput')"></button>
												<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
													onclick="cancelEdit($('#idIssueDatePersonInfo'), $('#editIdIssueDateInfo'))"></button>
											</div>
										</div>
									</div>
								</div>
							</div>	
						</form>
					</div>
					<div class="span6">
						<form class="row-fluid">
							<div class="row-fluid no-left-margin borderBottom"
								onmouseover="showEditDiv($('#editNativeLand'))" onmouseleave="hideEditDiv($('#editNativeLand'))">
								<div class="span5 lineHeight30">
									<span class="labelColor">
										${uiLabelMap.NativeLand}
									</span>
								</div>
								<div class="span7">
									<div class="row-fluid" id="nativeLandPersonInfo">
										<div class="span9 lineHeight30">
											<span>${lookupPerson.nativeLand?if_exists}&nbsp;</span>
										</div>
										<div class="span3" id="editNativeLand" style="display: none; text-align: right;">
											<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
												title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
												onclick="openEditPersonInfo($('#editNativeLandInfo'), $('#nativeLandPersonInfo'), $('#editNativeLandInput'), 'nativeLand')"></button>
										</div>
									</div>
									<div class="row-fluid" id="editNativeLandInfo" style="display: none">
										<div class="span8" style="margin-top: 2px">
											<input type="text" id="editNativeLandInput"/>
										</div>
										<div class="span4" style="text-align: right;">
											<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
												onclick="updatePersonInfo($('#nativeLandPersonInfo'), $('#editNativeLandInfo'), 'nativeLand', $('#editNativeLandInput'), 'nativeLand', 'input')"></button>
											<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
												onclick="cancelEdit($('#nativeLandPersonInfo'), $('#editNativeLandInfo'))"></button>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid no-left-margin borderBottom"
								onmouseover="showEditDiv($('#editEthnicOrigin'))" onmouseleave="hideEditDiv($('#editEthnicOrigin'))">
								<div class="span5 lineHeight30">
									<span class="labelColor">${uiLabelMap.EthnicOrigin}</span>
								</div>
								<div class="span7">
									<div class="row-fluid" id="ethnicOriginPersonInfo">
										<div class="span9 lineHeight30">
											<span>
											<#if lookupPerson.ethnicOrigin?exists && lookupPerson.ethnicOrigin?has_content>
												<#assign eth= delegator.findOne("EthnicOrigin",Static["org.ofbiz.base.util.UtilMisc"].toMap("ethnicOriginId",lookupPerson.ethnicOrigin), false) >
													${eth.description}&nbsp;							
											</#if>
											</span>
										</div>
										<div class="span3" id="editEthnicOrigin" style="display: none; text-align: right;">
											<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
												title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
												onclick="openEditPersonInfo($('#editEthnicOriginInfo'), $('#ethnicOriginPersonInfo'), $('#editEthnicOriginDropdownlist'), 'ethnicOrigin')"></button>
										</div>
									</div>
									<div class="row-fluid" id="editEthnicOriginInfo" style="display: none">
										<div class="span8" style="margin-top: 2px">
											<div id="editEthnicOriginDropdownlist"></div>
										</div>
										<div class="span4" style="text-align: right;">
											<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
												onclick="updatePersonInfo($('#ethnicOriginPersonInfo'), $('#editEthnicOriginInfo'), 'ethnicOrigin', $('#editEthnicOriginDropdownlist'), 'ethnicOrigin', 'dropdownlist')"></button>
											<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
												onclick="cancelEdit($('#ethnicOriginPersonInfo'), $('#editEthnicOriginInfo'))"></button>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid no-left-margin borderBottom"
								onmouseover="showEditDiv($('#editReligion'))" onmouseleave="hideEditDiv($('#editReligion'))">
								<div class="span5 lineHeight30">
									<span class="labelColor">
										${uiLabelMap.HrolbiusReligion}
									</span>
								</div>
								<div class="span7">
									<div class="row-fluid" id="religionPersonInfo">
										<div class="span9 lineHeight30">
											<span>
												<#if lookupPerson.religion?exists && lookupPerson.religion?has_content>
													<#assign religion= delegator.findOne("Religion",Static["org.ofbiz.base.util.UtilMisc"].toMap("religionId",lookupPerson.religion), false)>
													${religion.description}
												</#if>
											</span>
										</div>
										<div class="span3" id="editReligion" style="display: none; text-align: right;">
											<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
												title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
												onclick="openEditPersonInfo($('#editReligionInfo'), $('#religionPersonInfo'), $('#editReligionDropdownlist'), 'religion')"></button>
										</div>
									</div>
									<div class="row-fluid" id="editReligionInfo" style="display: none">
										<div class="span8" style="margin-top: 2px">
											<div id="editReligionDropdownlist"></div>
										</div>
										<div class="span4" style="text-align: right;">
											<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
												onclick="updatePersonInfo($('#religionPersonInfo'), $('#editReligionInfo'), 'religion', $('#editReligionDropdownlist'), 'religion', 'dropdownlist')"></button>
											<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
												onclick="cancelEdit($('#religionPersonInfo'), $('#editReligionInfo'))"></button>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid no-left-margin borderBottom"
								onmouseover="showEditDiv($('#editNationality'))" onmouseleave="hideEditDiv($('#editNationality'))">
								<div class="span5 lineHeight30">
									<span class="labelColor">
										${uiLabelMap.HrolbiusNationality}
									</span>
								</div>
								<div class="span7">
									<div class="row-fluid" id="nationalityPersonInfo">
										<div class="span9 lineHeight30">
											<span>
												<#if lookupPerson.nationality?exists>
													<#assign nation = delegator.findOne("Nationality", Static["org.ofbiz.base.util.UtilMisc"].toMap("nationalityId", lookupPerson.nationality), false)>
													${nation.description}
												<#else>
													&nbsp;
												</#if>
											</span>
										</div>
										<div class="span3" id="editNationality" style="display: none; text-align: right;">
											<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
												title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
												onclick="openEditPersonInfo($('#editNationalityInfo'), $('#nationalityPersonInfo'), $('#editNationalityDropdownlist'), 'nationality')"></button>
										</div>
									</div>
									<div class="row-fluid" id="editNationalityInfo" style="display: none">
										<div class="span8" style="margin-top: 2px">
											<div id="editNationalityDropdownlist"></div>
										</div>
										<div class="span4" style="text-align: right;">
											<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
												onclick="updatePersonInfo($('#nationalityPersonInfo'), $('#editNationalityInfo'), 'nationality', $('#editNationalityDropdownlist'), 'nationality', 'dropdownlist')"></button>
											<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
												onclick="cancelEdit($('#nationalityPersonInfo'), $('#editNationalityInfo'))"></button>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid no-left-margin borderBottom" 
								onmouseover="showEditDiv($('#editMaritalStatus'))" onmouseleave="hideEditDiv($('#editMaritalStatus'))">
								<div class="span5 lineHeight30">
									<span class="labelColor">
										${uiLabelMap.MaritalStatus}
									</span>
								</div>
								<div class="span7">
									<div class="row-fluid" id="maritalStatusPersonInfo">
										<div class="span9 lineHeight30">
											<span>
												<#if lookupPerson.maritalStatusId?exists>
													<#assign maritalStatus = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", lookupPerson.maritalStatusId), false)>
													${maritalStatus.description}
												</#if>
											</span>
										</div>
										<div class="span3" id="editMaritalStatus" style="display: none; text-align: right;">
											<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
												title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
												onclick="openEditPersonInfo($('#editMaritalStatusInfo'), $('#maritalStatusPersonInfo'), $('#editMaritalStatusDropdownlist'), 'maritalStatusId')"></button>
										</div>
									</div>
									<div class="row-fluid" id="editMaritalStatusInfo" style="display: none">
										<div class="span8" style="margin-top: 2px">
												<div id="editMaritalStatusDropdownlist"></div>
										</div>
										<div class="span4" style="text-align: right;">
											<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
												onclick="updatePersonInfo($('#maritalStatusPersonInfo'), $('#editMaritalStatusInfo'), 'maritalStatusId', $('#editMaritalStatusDropdownlist'), 'maritalStatusId', 'dropdownlist')"></button>
											<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
												onclick="cancelEdit($('#maritalStatusPersonInfo'), $('#editMaritalStatusInfo'))"></button>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid no-left-margin borderBottom" 
								onmouseover="showEditDiv($('#editNbrChildren'))" onmouseleave="hideEditDiv($('#editNbrChildren'))">
								<div class="span5 lineHeight30">
									<span class="labelColor">
										${uiLabelMap.NumberChildren}
									</span>
								</div>
								<div class="span7">
									<div class="row-fluid" id="nbrChildrenPersonInfo">
										<div class="span9 lineHeight30">
											<span>${lookupPerson.numberChildren?if_exists}</span> 
										</div>
										<div class="span3" id="editNbrChildren" style="display: none; text-align: right;">
											<button class="grid-action-button icon-edit" style="text-align: right;" type="button" 
												title="${StringUtil.wrapString(uiLabelMap.HrCommonEdit)}"
												onclick="openEditPersonInfo($('#editNbrChildrenInfo'), $('#nbrChildrenPersonInfo'), $('#editNbrChildrenNumberInput'), 'numberChildren')"></button>
										</div>
									</div>
									<div class="row-fluid" id="editNbrChildrenInfo" style="display: none">
										<div class="span8" style="margin-top: 2px">
												<div id="editNbrChildrenNumberInput"></div>
										</div>
										<div class="span4" style="text-align: right;">
											<button class="grid-action-button icon-ok" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonSubmit)}"
												onclick="updatePersonInfo($('#nbrChildrenPersonInfo'), $('#editNbrChildrenInfo'), 'numberChildren', $('#editNbrChildrenNumberInput'), 'numberChildren', 'numberinput')"></button>
											<button class="grid-action-button icon-remove" style="text-align: right;" type="button" title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"
												onclick="cancelEdit($('#nbrChildrenPersonInfo'), $('#editNbrChildrenInfo'))"></button>
										</div>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="personal-info-2" class="row-fluid mgt20">
		<div class="span12 boder-all-profile">
			<span class="text-header">${uiLabelMap.HREmplFromPositionType}</span>
			<form class="form-horizontal">
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.CommonDepartment}:</label>
					<div class="controls">
						<span>
							<#if employmentData.emplPosition?exists>
								<#assign department = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", employmentData.emplPosition.partyId), false)>
								${department.groupName?if_exists}&nbsp;
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.FormFieldTitle_position}:</label>
					<div class="controls">
						<span>
							<#if employmentData.emplPositionType?exists>
								${employmentData.emplPositionType.description}&nbsp;
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
			</form>
		</div>
	</div>
	<div id="personal-info-3" class="row-fluid mgt20">
		<#include "profileViewEmployeeProfileContact.ftl"/>
	</div>
	<div id="personal-info-4" class="row-fluid mgt20">
			<#include "profileViewEmployeeProfileEmergency.ftl"/>
	</div>
</div>
<div id="changePasswordWindow" class="hide">
	<div>${uiLabelMap.HRChangePassword}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CommonCurrentPassword}</label>
					</div>
					<div class="span7">
						<input type="password" id="currentPassword"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CommonNewPassword}</label>
					</div>
					<div class="span7">
						<input type="password" id="newPassword"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CommonNewPasswordVerify}</label>
					</div>
					<div class="span7">
						<input type="password" id="verifyNewPassword"/>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="cancelChangePw"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="saveChangePw"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="changeFullNameWindow" class="hide">
	<div>${uiLabelMap.EditFullName}</div>
	<div class='form-window-container'>
		<form id="changeFullNameForm">
			<div class='form-window-content'>
				<div class="row-fluid">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="asterisk">${uiLabelMap.LastName}</label>
						</div>
						<div class="span8">
							<input type="text" id="lastNameEdit"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.MiddleName}</label>
						</div>
						<div class="span8">
							<input type="text" id="middleNameEdit"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="asterisk">${uiLabelMap.FirstName}</label>
						</div>
						<div class="span8">
							<input type="text" id="firstNameEdit"/>
						</div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="alterCancelEditFullName"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="alterSaveEditFullName"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>