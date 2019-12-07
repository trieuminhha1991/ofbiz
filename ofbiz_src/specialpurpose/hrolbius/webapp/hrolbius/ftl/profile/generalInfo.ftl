<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>


<div id="personal-info" class="tab-pane active widget-body">
	
<style>
	.btn.btn-white {
  		text-shadow: none!important;
  		background-color: #FFF!important;
	}
	.btn-white.btn-info {
 	 	border-color: #8fbcd9;
  		color: #70a0c1!important;
	}
	.btn.btn-bold {
  		border-bottom-width: 2px!important;
	}
	.btn.btn-white {
  		border-width: 1px;
	}
	.row-fluid > label.span5, .row-fluid > label.span4 {
		text-align: right;
	}
	.row-fluid > div.span5 > span, .row-fluid > div.span6 > span {
		font-size: 14px!important;
		color: green;
	}
	hr{
		margin:0 0 0 20px!important;
	}
</style>

	
	<div id="personal-info-1" class="row-fluid mgt20">
		<div class="span2">
			<#if !personalImage?has_content>
				<#assign personalImage = "/aceadmin/assets/avatars/no-avatar.png">
			</#if>
			<img class="personal-image" id="personal-image" src="${personalImage}" alt="Avatar">
			<span class="personal-name"><a href="/hrolbius/control/editperson?partyId=${parameters.partyId}" title="${uiLabelMap.CommonUpdate}">${lookupPerson.lastName?if_exists} ${lookupPerson.middleName?if_exists} ${lookupPerson.firstName?if_exists}</a></span>
		</div>
		<form id="basicInformation">
		<input type="hidden" name="partyId" value="${parameters.partyId}" />

		<div class="span5">
			<form class="form-horizontal">
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.EmployeeId}:</label>
					<div class="span5">
						<span>${lookupPerson.partyId}</span>
					</div>
				</div>
				
				<div class="control-group no-left-margin" style="display:none;">
					<label class="span5" for="firstName">${uiLabelMap.FirstName}:</label>
					<div class="span5" id="">
						<input type="text" name="firstName" id="firstName" value="${lookupPerson.firstName?if_exists}"/>
					</div>
				</div>
				
				<div class="control-group no-left-margin" style="display:none;">
					<label class="span5" for="form-field-1">${uiLabelMap.LastName}:</label>
					<div class="span5"  id="">
						<input type="text" name="lastName" id="lastName" value="${lookupPerson.lastName?if_exists}"/>
					</div>
				</div>
				
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.FormFieldTitle_gender}:</label>
					<div class="span5" id="divUpdateG">
			
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.gender?if_exists}">
						<#if lookupPerson.gender?exists>
							<#assign gender = delegator.findOne("Gender", Static["org.ofbiz.base.util.UtilMisc"].toMap("genderId", lookupPerson.gender), false)>
						
							${gender.description}
						<#else>
							&nbsp;
						</#if>
						</span>
						</div>
				</div>
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.FormFieldTitle_birthDate}:</label>
					<div class="span5" id="divUpdateBD">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}', '${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${birthDate?if_exists}">
							<#if lookupPerson.birthDate?exists>
								<#assign birthDate = Static["com.olbius.util.DateUtil"].convertDate(lookupPerson.birthDate) />
								${birthDate?if_exists}
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.certProvisionId}:</label>
					<div class="span5"  id="divUpdateCMT">
					<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}', '${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.idNumber?if_exists}">${lookupPerson.idNumber?if_exists}&nbsp;</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.HrolbiusidIssuePlace}:</label>
					<div class="span5" id="divUpdateIP">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}', '${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.idIssuePlace?if_exists}">${lookupPerson.idIssuePlace?if_exists}&nbsp;</span> 
					</div>
				</div>
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.HrolbiusidIssueDate}:</label>
					<div class="span5" id="divUpdateID">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}', '${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${idIssueDate?if_exists}">
							<#if lookupPerson.idIssueDate?exists>
								<#assign idIssueDate = Static["com.olbius.util.DateUtil"].convertDate(lookupPerson.idIssueDate?if_exists) />
								${idIssueDate?if_exists}
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.MaritalStatus}:</label>
					<div class="span5" id="divUpdateMS">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}', '${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.maritalStatus?if_exists}">
							<#if lookupPerson.maritalStatus?exists>
								<#assign marialStatus = delegator.findOne("MaritalStatus", Static["org.ofbiz.base.util.UtilMisc"].toMap("maritalStatusId", lookupPerson.maritalStatus), false)>
								${marialStatus.description}
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
				<div class="span2"></div>
					<label class="span5" for="form-field-1">${uiLabelMap.NumberChildren}:</label>
					<div class="span5" id="divUpdateNC">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.numberChildren?if_exists}">${lookupPerson.numberChildren?if_exists}&nbsp;</span> 
					</div>
				</div>
		</div>
		 
		
		<div class="span5">
			<form class="row-fluid pdl20">
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.NativeLand}:</label>
					<div class="span6" id="divUpdateNL">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.nativeLand?if_exists}">${lookupPerson.nativeLand?if_exists}&nbsp;</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.EthnicOrigin}:</label>
					<div class="span6" id="divUpdateEO">
						<#if lookupPerson.ethnicOrigin?exists && lookupPerson.ethnicOrigin?has_content>
							<#assign eth= delegator.findOne("EthnicOrigin",Static["org.ofbiz.base.util.UtilMisc"].toMap("ethnicOriginId",lookupPerson.ethnicOrigin), false) >
							<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.ethnicOrigin?if_exists}">${eth.description}&nbsp;</span>
							<#else>
							<span>&nbsp;</span>
						</#if>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.HrolbiusReligion}:</label>
					<div class="span6" id="divUpdateR">
						<span  ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.religion?if_exists}">
							<#if lookupPerson.religion?exists && lookupPerson.religion?has_content>
								<#assign religion= delegator.findOne("Religion",Static["org.ofbiz.base.util.UtilMisc"].toMap("religionId",lookupPerson.religion), false)>
								${religion.description}
							<#else>
								${uiLabelMap.CommonNo}
							</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.HrolbiusNationality}:</label>
					<div class="span6" id="divUpdateN">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.nationality?if_exists}">
							<#if lookupPerson.nationality?exists>
								<#assign nation = delegator.findOne("Nationality", Static["org.ofbiz.base.util.UtilMisc"].toMap("nationalityId", lookupPerson.nationality), false)>
								${nation.description}
							<#else>
							&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.FormFieldTitle_passportNumber}:</label>
					<div class="span6" id="divUpdatePN">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.passportNumber?if_exists}">${lookupPerson.passportNumber?if_exists}&nbsp;</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.PassPortIssuePlace}:</label>
					<div class="span6" id="divUpdatePIP">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.passportIssuePlace?if_exists}">${lookupPerson.passportIssuePlace?if_exists}&nbsp;</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.DateOfIssuePassport}:</label>
					<div class="span6" id="divUpdatePID">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.passportIssueDate?if_exists}">
					<#if lookupPerson.passportIssueDate?exists>
						<#assign passportIssueDate = Static["com.olbius.util.DateUtil"].convertDate(lookupPerson.passportIssueDate) />
						${passportIssueDate?if_exists}
					<#else>
						&nbsp;
					</#if>
					</span>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<label class="span5" for="form-field-1">${uiLabelMap.PartyPassportExpireDate}:</label>
					<div class="span6" id="divUpdatePED">
						<span ondblclick="editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')" value="${lookupPerson.passportExpiryDate?if_exists}">
					<#if lookupPerson.passportExpiryDate?exists>
						<#assign passportExpiryDate = Static["com.olbius.util.DateUtil"].convertDate(lookupPerson.passportExpiryDate) />
						${passportExpiryDate?if_exists}
					<#else>
						&nbsp;
					</#if>
					</span>
					</div>
				</div>
				<div id="personal-info-update" class="row-fluid" style="clear:both; width:100%;height:35px;">
					<div class="span5"></div>
					<div id="submit-bt" class="span5">
						<button type='button' id='' class='btn btn-small btn-primary open-sans' style='float:right;' onclick="openEdit();"> <i class='fa-pencil'></i>${uiLabelMap.CommonEdit}</button>
					</div>
				</div>
			</form>
		</div>
	</div>
	</form>
	
	
	
	<hr/>
	<div id="personal-info-2" class="row-fluid mgt20">
		<div class="span12 boder-all-profile">
			<span class="text-header">${uiLabelMap.HREmplFromPositionType}</span>
			<form class="form-horizontal">
				<!-- <div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">Công ty:</label>
					<div class="controls">
						<span>Delys</span>
					</div>
				</div> -->
				<!-- <div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">Chi nhánh:</label>
					<div class="controls">
						<span>Hà Nội</span>
					</div>
				</div> -->
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.Department}:</label>
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
		<div class="span12 boder-all-profile">
			<span class="text-header">${uiLabelMap.PartyContactMechs}</span>
			<form class="form-horizontal">
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.PermanentResidence}:</label>
					<div class="controls">
						<span>
							<#if permanentResidence.contactMechId?has_content>
								<#assign permanent = delegator.findOne("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", permanentResidence.contactMechId), false)>
								 ${permanent.address1}&#44;
								 <#if permanent.wardGeoId?exists>
									<#assign ward = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permanent.wardGeoId), false)>
									<#if ward?exists && ward.geoId != "_NA_">
										${ward.geoName?if_exists}&#44;
									</#if>
								 </#if>
								 <#if permanent.districtGeoId?exists>
									<#assign district = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permanent.districtGeoId), false)>
									<#if district?exists && district.geoId != "_NA_">
										${district.geoName?if_exists}&#44;
									</#if>
								 </#if>
								 <#if permanent.stateProvinceGeoId?exists>
									 <#assign stateProvince = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permanent.stateProvinceGeoId), false)>
									 ${stateProvince.geoName?if_exists}&#46;
								 </#if>
							 <#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.ContactAddress}:</label>
					<div class="controls">
						<span><#if currentResidence.contactMechId?has_content>
								<#assign residence = delegator.findOne("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", currentResidence.contactMechId), false)>
								 ${residence.address1}&#44;
								 <#if residence.wardGeoId?exists>
									<#assign ward = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", residence.wardGeoId), false)>
									<#if ward?exists && ward.geoId != "_NA_">
										${ward.geoName?if_exists}&#44;
									</#if>
								 </#if>
								 <#if residence.districtGeoId?exists>
									<#assign district = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", residence.districtGeoId), false)>
									<#if district?exists && district.geoId != "_NA_">
										${district.geoName?if_exists}&#44;
									</#if>
								 </#if>
								 <#assign stateProvince = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", residence.stateProvinceGeoId), false)>
								 ${stateProvince.geoName?if_exists}&#46;
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.CommonEmail}:</label>
					<div class="controls">
						<span>
							${partyEmail.emailAddress?if_exists}&nbsp;
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.PhoneNumber}:</label>
					<div class="controls">
						<span>
							<#if phoneNumber?has_content>
								${phoneNumber.countryCode?if_exists} ${phoneNumber.areaCode?if_exists} ${phoneNumber.contactNumber?if_exists}&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.PhoneMobile}:</label>
					<div class="controls">
						<span>
							<#if mobileNumber?has_content>
								${mobileNumber.countryCode?if_exists} ${mobileNumber.areaCode?if_exists} ${mobileNumber.contactNumber?if_exists}&nbsp;
							</#if>
						</span>
					</div>
				</div>
			</form>
		</div>
		<#--<div class="span6 boder-all-profile">
			<span class="text-header">${uiLabelMap.InformationQualifications}</span>
			<form class="form-horizontal">
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.TrainingLevel}:</label>
					<div class="controls">
						<#if partyEducation?has_content>
							<#assign education = partyEducation.get(0)>
						<#else>
							&nbsp;
						</#if>
						<span>
							<#if education?exists>
								${education.description}
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.HRSpecialization}:</label>
					<div class="controls">
						<span>
							<#if education?exists>
								<#assign major = delegator.findOne("Major", Static["org.ofbiz.base.util.UtilMisc"].toMap("majorId", education.majorId), false)>
								${major.description?if_exists}
							 <#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.UniCertificateId}:</label>
					<div class="controls">
						<span>
							<#if education?exists>
								<#assign classificationType = delegator.findOne("DegreeClassificationType", Static["org.ofbiz.base.util.UtilMisc"].toMap("classificationTypeId", education.classificationTypeId), false)>
								${classificationType.description?if_exists}
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label" for="form-field-1">${uiLabelMap.HrolbiusGraduationYear}:</label>
					<div class="controls">
						<span>
							<#if education?exists>
								${education.thruDate?string.yyyy}
							<#else>
								&nbsp;
							</#if>
						</span>
					</div>
				</div>
			</form>
		</div>-->
	</div>
	<div id="personal-info-4" class="row-fluid mgt20">
		<div class="span12 boder-all-profile">
			<span class="text-header">${uiLabelMap.EmergencyContactInformation}</span>
			<form class="form-horizontal">
				<#if personFamilyBackgroundEmercy?has_content >
					<div class="control-group no-left-margin">
						<label class="control-label" for="form-field-1">${uiLabelMap.FullName}:</label>
						<div class="controls">
							<#assign familyPerson = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", personFamilyBackgroundEmercy[0].partyFamilyId), false)>
							<span>
								${familyPerson.lastName?if_exists}
								${familyPerson.middleName?if_exists}
								${familyPerson.firstName?if_exists}
								&nbsp
							</span>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" for="form-field-1">${uiLabelMap.HRRelationship}:</label>
						<div class="controls">
							<#assign partyRelationshipType = delegator.findOne("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyRelationshipTypeId", personFamilyBackgroundEmercy[0].partyRelationshipTypeId), false)>
							<span>${partyRelationshipType.partyRelationshipName?if_exists}&nbsp</span>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" for="form-field-1">${uiLabelMap.PhoneNumber}:</label>
						<div class="controls">
							<#assign telecomNbr = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", personFamilyBackgroundEmercy[0].partyFamilyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "userLogin", systemUserLogin))>
							<span>${telecomNbr.contactNumber?if_exists}&nbsp</span>
						</div>
					</div>
					<#else>
					<div class="control-group no-left-margin">
						<label class="control-label" for="form-field-1">${uiLabelMap.FullName}:</label>
						<div class="controls">
							<span>&nbsp
						</span>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" for="form-field-1">${uiLabelMap.HRRelationship}:</label>
						<div class="controls">
							<span>&nbsp</span>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" for="form-field-1">${uiLabelMap.PhoneNumber}:</label>
						<div class="controls">
							<span>
							&nbsp
							</span>
						</div>
					</div>
				</#if>
			</form>
		</div>
	</div>
</div>
<#assign listReligionTypes = delegator.findList("Religion", null, null, null, null, false) />
<#assign listNationalityTypes = delegator.findList("Nationality", null, null, null, null, false) />
<#assign ethnicOriginList = delegator.findList("EthnicOrigin", null , null, null,null, false)>
<#assign maritalStatusList = delegator.findList("MaritalStatus", null , null, orderBy,null, false)>
<#assign genderList = delegator.findList("Gender", null , null, orderBy,null, false)>

<script>
function openEdit(){
	editBasicInfo('${lookupPerson.idNumber?if_exists}','${lookupPerson.numberChildren?if_exists}','${lookupPerson.idIssuePlace?if_exists}','${lookupPerson.nativeLand?if_exists}','${lookupPerson.passportIssuePlace?if_exists}','${lookupPerson.passportNumber?if_exists}','${birthDate?if_exists}','${idIssueDate?if_exists}', '${lookupPerson.passportIssueDate?if_exists}', '${lookupPerson.passportExpiryDate?if_exists}','${lookupPerson.religion?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.nationality?if_exists}','${lookupPerson.ethnicOrigin?if_exists}','${lookupPerson.maritalStatus?if_exists}','${lookupPerson.gender?if_exists}')
}


var religionTypes = [
	<#list listReligionTypes as religionT>
	{
    	religionId : "${religionT.religionId}",
        description : "${StringUtil.wrapString(religionT.description)}"
	},
	</#list>	
];

var nationalityTypes = [
	<#list listNationalityTypes as nationalityT >
    {
    	nationalityId : "${nationalityT.nationalityId}",
    	description : "${StringUtil.wrapString(nationalityT.description)}"
    },
    </#list>	
];

var ethnicOriginList = [
	<#list ethnicOriginList as ethnicOrigin1>
	{
		ethnicOriginId : "${ethnicOrigin1.ethnicOriginId}",
		description : "${StringUtil.wrapString(ethnicOrigin1.description)}"
	},
	</#list>	
];

var maritalStatusList = [
	<#list maritalStatusList as maritalStatus1>
	{
		maritalStatusId : "${maritalStatus1.maritalStatusId}",
		description : "${StringUtil.wrapString(maritalStatus1.description)}"
	},
	</#list>	
];
 
var genderList = [
	<#list genderList as gender1>
	{
		genderId : "${gender1.genderId}",
		description : "${StringUtil.wrapString(gender1.description)}"
	},
	</#list>	
];

function editBasicInfo(vNumber,vNumberChildren,vIssuePlace,vNativeLand,vPassportIssuePlace,vPassportNumber,vBirthDate,vIssueDate,vPassportIssueDate,vPassportExpiryDate,vReligion,vNationality,vEthnicOrigin,vMaritalStatus,vGender) {
//		   console.log(vNumber,vNumberChildren,vIssuePlace,vNativeLand,vPassportIssuePlace,vPassportNumber,vBirthDate,vIssueDate,vPassportIssueDate,vPassportExpiryDate,vReligion,vNationality,vEthnicOrigin,vMaritalStatus,vGender);
	var submitUpdate = "<button type='button' id='basicInformationBtn' class='btn btn-small btn-primary open-sans' style='float:right;' > <i class='ace-icon fa fa-check'></i>${uiLabelMap.CommonUpdate}</button>";
	var updateNumber = "<div id='idNumber'></div>";
	var updateNumberChildren = "<div id='numberChildren'></div>";
	var updateIssuePlace = "<input type='text' id='idIssuePlace' />";
	var updateNativeLand = "<input type='text' id='nativeLand' />";
	var updatePassportIssuePlace = "<input type='text' id='passportIssuePlace' />";
	var updatePassportNumber = "<input type='text' id='passportNumber' />";
	var updateBirthDate = "<div id='birthDate'></div>";
	var updateIssueDate = "<div id='idIssueDate'></div>";
	var updatePassportIssueDate = "<div id='passportIssueDate'></div>";
	var updatePassportExpiryDate = "<div id='passportExpiryDate'></div>";
	var updateReligion = "<div id='religionId'></div>";
	var updateNationality = "<div id='nationalityId'></div>";
	var updateEthnicOrigin = "<div id='ethnicOrigin'></div>";
	var updateMaritalStatus = "<div id='maritalStatus'></div>";
	var updateGender = "<div id='gender'></div>";
	
		
	$("#divUpdateCMT").html(updateNumber);
	$("#divUpdateNC").html(updateNumberChildren);
	$('#divUpdateIP').html(updateIssuePlace);
	$('#divUpdateNL').html(updateNativeLand);
	$('#divUpdatePIP').html(updatePassportIssuePlace);
	$('#divUpdatePN').html(updatePassportNumber);
	$('#divUpdateBD').html(updateBirthDate);
	$('#divUpdateID').html(updateIssueDate);
	$('#divUpdatePID').html(updatePassportIssueDate);
	$('#divUpdatePED').html(updatePassportExpiryDate);
	$('#divUpdateR').html(updateReligion);
	$('#divUpdateN').html(updateNationality);
	$('#divUpdateEO').html(updateEthnicOrigin);
	$('#divUpdateMS').html(updateMaritalStatus);
	$('#divUpdateG').html(updateGender);
	
	$("#idNumber").jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', decimalDigits: 0, digits: 10, max: 9999999999 });
	$("#numberChildren").jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', decimalDigits: 0, digits: 2, max: 20 });
	$("#idIssuePlace").jqxInput({width : '100%',height : '25px'});
	$("#nativeLand").jqxInput({width : '81%',height : '25px'});
	$("#passportIssuePlace").jqxInput({width : '81%',height : '25px'});
	$("#passportNumber").jqxInput({width : '81%',height : '25px'});
	$("#birthDate").jqxDateTimeInput({width: '100%', height: '25px', formatString: 'yyyy-MM-dd'});
	$("#idIssueDate").jqxDateTimeInput({width: '100%', height: '25px', formatString: 'yyyy-MM-dd'});
	$("#passportIssueDate").jqxDateTimeInput({width: '81%', height: '25px', formatString: 'yyyy-MM-dd'});
	$("#passportExpiryDate").jqxDateTimeInput({width: '81%', height: '25px', formatString: 'yyyy-MM-dd'});
	$("#religionId").jqxDropDownList({ source: religionTypes, width: '81%', height: '25px', displayMember: "description", valueMember : "religionId"});
	$("#nationalityId").jqxDropDownList({ source: nationalityTypes, width: '81%', height: '25px', displayMember: "description", valueMember : "nationalityId"});
	$("#ethnicOrigin").jqxDropDownList({ source: ethnicOriginList, width: '81%', height: '25px', displayMember: "description", valueMember : "ethnicOriginId"});
	$("#maritalStatus").jqxDropDownList({ source: maritalStatusList, width: '100%', height: '25px', displayMember: "description", valueMember : "maritalStatusId"});
	$("#gender").jqxDropDownList({ source: genderList, width: '99%', height: '25px', displayMember: "description", valueMember : "genderId"});
	
	<#if lookupPerson.birthDate?exists>
		$('#birthDate').val(new Date(${lookupPerson.birthDate.getTime()}));
	</#if>
	<#if lookupPerson.idIssueDate?exists>
		$('#idIssueDate').val(new Date(${lookupPerson.idIssueDate.getTime()}));
	</#if>
	<#if lookupPerson.passportIssueDate?exists>
		$('#passportIssueDate').val(new Date(${lookupPerson.passportIssueDate.getTime()}));
	</#if>
	<#if lookupPerson.passportExpiryDate?exists>
		$('#passportExpiryDate').val(new Date(${lookupPerson.passportExpiryDate.getTime()}));
	</#if>
	
	
	<#if lookupPerson.religion?exists>
		$('#religionId').jqxDropDownList('val', "${lookupPerson.religion}");
	</#if>
	<#if lookupPerson.nationality?exists>
		$('#nationalityId').jqxDropDownList('val', '${lookupPerson.nationality}');
	</#if>
	<#if lookupPerson.ethnicOrigin?exists>
		$('#ethnicOrigin').jqxDropDownList('val', '${lookupPerson.ethnicOrigin}');
	</#if>
	<#if lookupPerson.maritalStatus?exists>
		$('#maritalStatus').jqxDropDownList('val', '${lookupPerson.maritalStatus}');
	</#if>
	<#if lookupPerson.gender?exists>
		$('#gender').jqxDropDownList('val', '${lookupPerson.gender}');
	</#if>
	
	
	$("#religionId").jqxDropDownList({autoDropDownHeight: true}); 
	$("#nationalityId").jqxDropDownList({autoDropDownHeight: true}); 
	$("#ethnicOrigin").jqxDropDownList({autoDropDownHeight: true}); 
	$("#maritalStatus").jqxDropDownList({autoDropDownHeight: true}); 
	$("#gender").jqxDropDownList({autoDropDownHeight: true}); 
	
	$('#idNumber').jqxNumberInput('val', vNumber);
	$('#numberChildren').jqxNumberInput('val', vNumberChildren);
	$('#idIssuePlace').jqxInput('val', vIssuePlace);
	$('#nativeLand').jqxInput('val', vNativeLand);
	$('#passportIssuePlace').jqxInput('val', vPassportIssuePlace);
	$('#passportNumber').jqxInput('val', vPassportNumber);
	$('#birthDate').jqxDateTimeInput('getText', vBirthDate);
	$('#idIssueDate').jqxDateTimeInput('getText', vIssueDate);
	$('#passportIssueDate').jqxDateTimeInput('getText', vPassportIssueDate);
	$('#passportExpiryDate').jqxDateTimeInput('getText', vPassportExpiryDate);
	$("#religionId").jqxDropDownList('val', vReligion);
	$("#nationalityId").jqxDropDownList('val', vNationality);
	$("#ethnicOrigin").jqxDropDownList('val', vEthnicOrigin);
	$("#maritalStatus").jqxDropDownList('val', vMaritalStatus);
	$("#gender").jqxDropDownList('val', vGender);
	
	$("#submit-bt").html(submitUpdate);
	
	
	$('#basicInformationBtn').on('click', function(){
		var valueNumber = $('#idNumber').jqxNumberInput('val');
		var valueNumberChildren = $('#numberChildren').jqxNumberInput('val');
		var valueIssuePlace = $('#idIssuePlace').jqxInput('val');
		var valueNativeLand = $('#nativeLand').jqxInput('val');
		var valuePassportIssuePlace = $('#passportIssuePlace').jqxInput('val');
		var valuePassportNumber = $('#passportNumber').jqxInput('val');
		var valueBirthDate = $('#birthDate').jqxDateTimeInput('getText');
		var valueIssuaDate = $('#idIssueDate').jqxDateTimeInput('getText');
		var valuePassportIssueDate = $('#passportIssueDate').jqxDateTimeInput('getText');
		var valuePassportExpiryDate = $('#passportExpiryDate').jqxDateTimeInput('getText');
		var valueReligion = $('#religionId').jqxDropDownList('val');
		var valueNationality = $('#nationalityId').jqxDropDownList('val');
		var valueEthnicOrigin = $('#ethnicOrigin').jqxDropDownList('val');
		var valueMaritalStatus = $('#maritalStatus').jqxDropDownList('val');
		var valueGender = $('#gender').jqxDropDownList('val');
		
		uppEmplProfile(valueNumber,valueNumberChildren,valueIssuePlace,valueNativeLand,valuePassportIssuePlace,valuePassportNumber,valueBirthDate,valueIssuaDate,valuePassportIssueDate,valuePassportExpiryDate,valueReligion,valueNationality,valueEthnicOrigin,valueMaritalStatus,valueGender);
	});
} 

 	$("#submit-bt").on("click", function(){
 		revert();
 	}); 
	
	function uppEmplProfile(valueNumber,valueNumberChildren,valueIssuePlace,valueNativeLand,valuePassportIssuePlace,valuePassportNumber,valueBirthDate,valueIssuaDate,valuePassportIssueDate,valuePassportExpiryDate,valueReligion,valueNationality,valueEthnicOrigin,valueMaritalStatus,valueGender){
	var basicInfo = $("#basicInformation").serializeArray();
	basicInfo.push({name: "idNumber", value: valueNumber});
	basicInfo.push({name: "numberChildren", value: valueNumberChildren});
	basicInfo.push({name: "idIssuePlace", value: valueIssuePlace});
	basicInfo.push({name: "nativeLand", value: valueNativeLand});
	basicInfo.push({name: "passportIssuePlace", value: valuePassportIssuePlace});
	basicInfo.push({name: "passportNumber", value: valuePassportNumber});
	basicInfo.push({name: "birthDate", value: valueBirthDate});
	basicInfo.push({name: "idIssueDate", value: valueIssuaDate});
	basicInfo.push({name: "passportIssueDate", value: valuePassportIssueDate});
	basicInfo.push({name: "passportExpiryDate", value: valuePassportExpiryDate});
	basicInfo.push({name: "religionId", value: valueReligion});
	basicInfo.push({name: "nationalityId", value: valueNationality});
	basicInfo.push({name: "ethnicOrigin", value: valueEthnicOrigin});
	basicInfo.push({name: "maritalStatus", value: valueMaritalStatus});
	basicInfo.push({name: "gender", value: valueGender});


	jQuery.ajax({
		url: "updateEmplProfile",
		data: basicInfo,
		type:"POST",
		success: function(data){
			if(data._EVENT_MESSAGE_){
				bootbox.dialog({
					message: data._EVENT_MESSAGE_,
					title: "${uiLabelMap.ResultUpdate}",
					buttons:{
						main: {
							label: "OK!",
							className: "btn-small btn-primary icon-ok open-sans"
						}
					}
				});	
				setTimeout(function(){ 
					location.reload();		
				}, 1500);
			}else{
				var error = "";
				if(data._ERROR_MESSAGE_){
					error = ": " + data._ERROR_MESSAGE_;
				}
				bootbox.dialog({						
					message: "${uiLabelMap.ErrorWhenUpdate}" + " " + error,
					title: "${uiLabelMap.ResultUpdate}",
					buttons:{
						main: {
							label: "OK!",
							className: "btn-small btn-danger open-sans"
						}
					}
				});	
			}
		}			
	});
	}
//}); 

function revert() {
	var newValueNumber = $('#idNumber').jqxNumberInput('val');
	var newValueNumberChildren = $('#numberChildren').jqxNumberInput('val');
	var newValueIssuePlace = $('#idIssuePlace').jqxInput('val');
	var newValueNativeLand = $('#nativeLand').jqxInput('val');
	var newValuePassportIssuePlace = $('#passportIssuePlace').jqxInput('val');
	var newValuePassportNumber = $('#passportNumber').jqxInput('val');
	var newValueBirthDate = $('#birthDate').jqxDateTimeInput('getText');
	var newValueIssueDate = $('#idIssueDate').jqxDateTimeInput('getText');
	var newValuePassportIssueDate = $('#passportIssueDate').jqxDateTimeInput('getText');
	var newValuePassportExpiryDate = $('#passportExpiryDate').jqxDateTimeInput('getText');
	var newValueReligion = $('#religionId').jqxDropDownList('val');
	var newValueNationality = $('#nationalityId').jqxDropDownList('val');
	var newValueEthnicOrigin = $('#ethnicOrigin').jqxDropDownList('val');
	var newValueMaritalStatus = $('#maritalStatus').jqxDropDownList('val');
	var newValueGender = $('#gender').jqxDropDownList('val');
	
	var hiddenUpdateBt = "<button' id='basicInformationBtn' class='btn btn-white btn-info btn-sucess' style='float:right;display:none;'/><i class='ace-icon fa fa-check'></i></button>";
//	var updateNumber = "<span ondblclick='editBasicInfo(\"" + newValueNumber + "\")'>" + newValueNumber + "</span>";
<#--	var updateNumber = "<span>" + newValueNumber + "</span>";
	var updateNumberChildren = "<span>" + newValueNumberChildren + "</span>";
	var updateIssuePlace = "<span>" + newValueIssuePlace + "</span>";
	var updateNativeLand = "<span>" + newValueNativeLand + "</span>";
	var updatePassportIssuePlace = "<span>" + newValuePassportIssuePlace + "</span>";
	var updatePassportNumber = "<span>" + newValuePassportNumber + "</span>";
	var updateBirthDate = "<span><#if lookupPerson.birthDate?exists><#assign birthDate = Static['com.olbius.util.DateUtil'].convertDate(lookupPerson.birthDate) />" + newValueBirthDate + "<#else>&nbsp;</#if></span>";
	var updateIssueDate = "<span><#if lookupPerson.idIssueDate?exists><#assign idIssueDate = Static['com.olbius.util.DateUtil'].convertDate(lookupPerson.idIssueDate) />" + newValueIssueDate + "<#else>&nbsp;</#if></span>";
	var updatePassportIssueDate = "<span>" + newValuePassportIssueDate + "</span>";
	var updatePassportExpiryDate = "<span>" + newValuePassportExpiryDate + "</span>";
	var updateReligion = "<span><#if lookupPerson.religion?exists && lookupPerson.religion?has_content><#assign religion= delegator.findOne('Religion',Static['org.ofbiz.base.util.UtilMisc'].toMap('religionId',lookupPerson.religion), false)>" + newValueReligion + "<#else>${uiLabelMap.CommonNo}</#if></span>";
	var updateNationality = "<span><#if lookupPerson.nationality?exists><#assign nation = delegator.findOne('Nationality', Static['org.ofbiz.base.util.UtilMisc'].toMap('nationalityId', lookupPerson.nationality), false)>" + newValueNationality + "<#else>&nbsp;</#if></span>";
	var updateEthnicOrigin = "<span><#if lookupPerson.ethnicOrigin?exists && lookupPerson.ethnicOrigin?has_content><#assign eth= delegator.findOne('EthnicOrigin',Static['org.ofbiz.base.util.UtilMisc'].toMap('ethnicOriginId',lookupPerson.ethnicOrigin), false) >" + newValueEthnicOrigin + "<#else>&nbsp;</#if></span>";
	var updateMaritalStatus = "<span><#if lookupPerson.maritalStatus?exists><#assign marialStatus = delegator.findOne('MaritalStatus', Static['org.ofbiz.base.util.UtilMisc'].toMap('maritalStatusId', lookupPerson.maritalStatus), false)>" + newValueMaritalStatus + "<#else>&nbsp;</#if></span>";
	var updateGender = "<span><#if lookupPerson.gender?exists><#assign gender = delegator.findOne('Gender', Static['org.ofbiz.base.util.UtilMisc'].toMap('genderId', lookupPerson.gender), false)>" + newValueGender + "<#else>&nbsp;</#if></span>";				
	
	
	setTimeout(function(){ 
		location.reload();		
	}, 1000);
	-->
	<#--
	$("#submit-bt").html(hiddenUpdateBt);
	$("#divUpdateCMT").html(updateNumber);
	$("#divUpdateNC").html(updateNumberChildren);
	$("#divUpdateIP").html(updateIssuePlace);
	$("#divUpdateNL").html(updateNativeLand);
	$("#divUpdatePIP").html(updatePassportIssuePlace);
	$("#divUpdatePN").html(updatePassportNumber);
	$("#divUpdateBD").html(updateBirthDate);
	$("#divUpdateID").html(updateIssueDate);
	$("#divUpdatePID").html(updatePassportIssueDate);
	$("#divUpdatePED").html(updatePassportExpiryDate);
	$("#divUpdateR").html(updateReligion);
	$("#divUpdateN").html(updateNationality);
	$("#divUpdateEO").html(updateEthnicOrigin);
	$("#divUpdateMS").html(updateMaritalStatus);
	-->
	
}

//function convertDate(date) {
// 	if (!date) {
//		return null;
//	}
//	var dateArray = date.split("-");
//	var newDate = new Date(dateArray[0] + "/" + dateArray[1] + "/" + dateArray[2]);
//	return newDate.getTime();
//}

</script>