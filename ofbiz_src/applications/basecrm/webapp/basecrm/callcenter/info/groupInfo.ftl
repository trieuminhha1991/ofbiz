<div class="info-container hide cio-custom" id="businessesInfoEditable">
	<div class="cio-title"><i class="fa fa-user fa-lg" title="${uiLabelMap.BasicInfo}"></i></div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.BSCustomerId}:</label>
				</div>
				<div class="span7">
					<input type="text" id="txtPartyCodeBuz" class="info-input no-margin no-space" tabindex="1"/>
					<div id="BPartyIdBussiness"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label class="asterisk">${uiLabelMap.DmsCorporationName}:</label>
				</div>
				<div class="span7">
					<textarea id="txtCorporationName" class="no-resize no-margin" rows="2" style="width:88%"  tabindex="2"></textarea>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.Website}:</label>
				</div>
				<div class="span7">
					<input type="text" id="txtWebsite" class="info-input" tabindex="4" />
				</div>
			</div>
			<div class="row-fluid hide margin-bottom5" id="studentInfo">
				<div class="span5">
					<label>${uiLabelMap.KNumberStudent}:</label>
				</div>
				<div class="span7">
					<input type="number" id="txtStudent" class="info-input" tabindex="6" />
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.DmsOwnerEmployee}:</label>
				</div>
				<div class="span7">
					<#if security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
						<div id="OwnerEmployeeBiz"></div>
					<#else>
						<b id="OwnerEmployeeBiz"></b>
					</#if>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.DmsDescription}:</label>
				</div>
				<div class="span7">
					<textarea id="txtDescription" class="no-resize no-margin" rows="2" style="width:88%" tabindex="3"></textarea>
				</div>
			</div>
			<#--
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label class="text-right">${uiLabelMap.Facebook}</label>
				</div>
				<div class="span7">
					<input id="txtFacebookBussiness" tabindex="5"/>
				</div>
			</div>
			-->
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.PartyDatasource}:</label>
				</div>
				<div class="span7">
					<input id="partyDataSourceBussiness" class="no-space" tabindex="5"/>
				</div>
			</div>
			<div class="row-fluid hide margin-bottom5" id="teacherInfo">
				<div class="span5">
					<label>${uiLabelMap.KNumberTeacher}:</label>
				</div>
				<div class="span7">
					<input type="number" id="txtTeacher" class="info-input" tabindex="7" />
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<h4>${uiLabelMap.DmsPersonRepresent}</h4>
			<div class="row-fluid" id="newPersonRepresent">
				<div class="span6">
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.FullName}:</label>
						</div>
						<div class="span8">
							<input type="text" id="txtFullNameBusinesses" class="info-input" tabindex="8" />
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.DmsPartyBirthDate}:</label>
						</div>
						<div class="span8">
							<div id="txtBirthDateBusinesses" tabindex="10"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.DmsIdentification}:</label>
						</div>
						<div class="span8">
							<input type="number" id="txtIdentificationBusinesses" class="info-input" tabindex="12" />
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.DmsProvidePlace}:</label>
						</div>
						<div class="span8">
							<div id="txtProvidePlaceBusinesses" tabindex="14" ></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.DmsPrimaryPhone}:</label>
						</div>
						<div class="span8">
							<input type="number" id="txtSdtBusinesses" class="info-input" tabindex="9" />
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.DmsPartyGender}:</label>
						</div>
						<div class="span8">
							<div id="txtGenderBusinesses" tabindex="11"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span4">
							<label>${uiLabelMap.DmsProvideDate}:</label>
						</div>
						<div class="span8">
							<div id="txtProvideDateBusinesses" tabindex="13"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>