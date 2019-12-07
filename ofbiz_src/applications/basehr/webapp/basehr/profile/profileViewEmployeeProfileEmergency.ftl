<script type="text/javascript" src="/hrresources/js/profile/emergencyInfo.js"></script>
<div class="row-fluid">
	<div class="span12 boder-all-profile">
		<span class="text-header">${uiLabelMap.EmergencyContactInformation} <button title="${uiLabelMap.CommonEdit}" id="editFamilyEmergency" class="grid-action-button icon-edit" style="margin: 0; padding: 0 !important"></button></span>
		<div class='form-window-container' style="position:relative">
			<div class='form-window-content' >
				<#if personFamilyBackgroundEmercy?has_content && (personFamilyBackgroundEmercy?size > 0)>
					<div class="row-fluid no-left-margin borderBottom">
						<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
							<span class="labelColor">${uiLabelMap.HRFullName}:</span>
						</div>
						<div class="span9" style="line-height: 27px">
							<#assign familyPerson = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", personFamilyBackgroundEmercy[0].partyFamilyId), false)>
							<span id="emergencyFullNameDesc">
								${familyPerson.lastName?if_exists}
								${familyPerson.middleName?if_exists}
								${familyPerson.firstName?if_exists}
								&nbsp
							</span>
						</div>
					</div>
					<div class="row-fluid no-left-margin borderBottom">
						<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
							<span class="labelColor">${uiLabelMap.HRRelationship}:</span>
						</div>
						<div class="span9" style="line-height: 27px">
							<#assign partyRelationshipType = delegator.findOne("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyRelationshipTypeId", personFamilyBackgroundEmercy[0].partyRelationshipTypeId), false)>
							<span id="emergencyPartyRelDesc">${partyRelationshipType.partyRelationshipName?if_exists}&nbsp</span>
						</div>
					</div>
					<div class="row-fluid no-left-margin borderBottom">
						<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
							<span class="labelColor">${uiLabelMap.PhoneNumber}:</span>
						</div>
						<div class="span9" style="line-height: 27px">
							<#assign telecomNbr = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", personFamilyBackgroundEmercy[0].partyFamilyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "userLogin", systemUserLogin))>
							<span id="emergencyTelephoneDesc">${telecomNbr.contactNumber?if_exists}&nbsp</span>
						</div>
					</div>
				<#else>
					<div id="emergencyInfo" class="hide">
						<div class="row-fluid no-left-margin borderBottom">
							<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
								<span class="labelColor">${uiLabelMap.HRFullName}:</span>
							</div>
							<div class="span9" style="line-height: 27px">							
								<span id="emergencyFullNameDesc">
								</span>
							</div>
						</div>
						<div class="row-fluid no-left-margin borderBottom">
							<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
								<span class="labelColor">${uiLabelMap.HRRelationship}:</span>
							</div>
							<div class="span9" style="line-height: 27px">							
								<span id="emergencyPartyRelDesc"></span>
							</div>
						</div>
						<div class="row-fluid no-left-margin borderBottom">
							<div class="span3 lineHeight30" style="text-align: right; font-size: 14px">
								<span class="labelColor">${uiLabelMap.PhoneNumber}:</span>
							</div>
							<div class="span9" style="line-height: 27px">
								<span id="emergencyTelephoneDesc"></span>
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10' id="emergencyNotSetting">
						<div class='span4 ' style="text-align: center;">
							<h5 class="header smaller lighter red" style="margin: 0 !important; border: none !important">
								<i class="icon-remove"></i>${StringUtil.wrapString(uiLabelMap.NotSetting)}
							</h5>
						</div>
						<div class="span7">
							<span></span>	
						</div>
					</div>
				</#if>
			</div>	
	
		</div>
	</div>
</div>
<div id="editPartyEmergencyWindow" class="hide">
	<div>${uiLabelMap.EmergencyContactInformation}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">
						${uiLabelMap.HRFullName}
					</label>
				</div>
				<div class="span7">
					<div id="emergencyFamilyList"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">
						${uiLabelMap.HRRelationship}
					</label>
				</div>
				<div class='span7'>
					<div id="emergencyPartyRel"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.PartyPhoneNumber}</label>
				</div>
				<div class="span7">
					<div id="emergencyPhoneNbr"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div id="ajaxLoading" class="hide">
					<div class="loader-page-common-custom" id="spinner-ajax"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelEmergency">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEmergency">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
    	</div>
	</div>
</div>
