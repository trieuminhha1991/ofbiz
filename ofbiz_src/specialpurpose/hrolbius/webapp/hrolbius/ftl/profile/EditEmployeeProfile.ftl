<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent no-bottom-border">
			<div class="row-fuild">
				<div class="span12 margin-top30" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
				<form action="" name="basicInfortion" id="basicInformation">
					<input type="hidden" name="partyId" value="${parameters.partyId}">
					<table cellspacing="0">
						<tbody>
						   	<tr>
								<td><label class="padding-bottom5 padding-right15" for="lastName">${uiLabelMap.LastName}</label> <span style="color:red">(*)</span></td>
								<td>
									<input type="text" name="lastName" id="lastName" value="${personInfo.lastName?if_exists}"/>
								</td>
								<td><label class="padding-bottom5 padding-right15 margin-left30" for="middleName">${uiLabelMap.MiddleName}</label></td>
								<td>
									<input type="text" name="middleName" id="middleName"  value="${personInfo.middleName?if_exists}"/>
								</td>
								<td><label class="padding-bottom5 padding-right15 margin-left30" for="firstName">${uiLabelMap.FirstName}</label> <span style="color:red">(*)</span></td>
								<td>
									<input type="text" name="firstName" id="firstName" value="${personInfo.firstName?if_exists}"/>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="gender">${uiLabelMap.Gender}</label></td>
								<td>
									<select name="gender" id="gender">
										<option value="M" <#if personInfo.gender?exists && personInfo.gender=="M">selected="selected"</#if>>${uiLabelMap.Male}</option>
										<option value="F" <#if personInfo.gender?exists && personInfo.gender=="F">selected="selected"</#if>>${uiLabelMap.Female}</option>
									</select>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="birthDate">${uiLabelMap.BirthDate}</label></td>
								<td style="padding: 0">
									<#if personInfo.birthDate?has_content>	
										<#assign birthDateTemp = personInfo.birthDate?string["yyyy-MM-dd"]>
									</#if>
									 <@htmlTemplate.renderDateTimeField name="birthDate" value="${birthDateTemp?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="birthDay" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="form-field-1">${uiLabelMap.HrolbiusNationality}:</label></td>
								<td>
									<select name="nationality">
										<#list nationality as nation>	
											<option value="${nation.nationalityId}"  <#if personInfo.nationality?exists && nation.nationalityId == personInfo.nationality>selected="selected"</#if>>
												${nation.description}
											</option>
										</#list>
									</select>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="height">${uiLabelMap.Height}</label></td>
								<td>
									 <input type="text" id="height" name="height" value="${personInfo.height?if_exists}"/>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="weight">${uiLabelMap.Weight}</label></td>
								<td>
									 <input type="text" id="weight" name="weight" value="${personInfo.weight?if_exists}"/>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="idNumber">${uiLabelMap.IDNumber}</label> <span style="color:red">(*)</span></td>
								<td>
									 <input type="text" id="idNumber" name="idNumber" value="${personInfo.idNumber?if_exists}"/>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="idIssueDate">${uiLabelMap.IDIssueDate}</label></td>
								<td style="padding: 0">
									<#if personInfo.idIssueDate?exists>
										<#assign tempIdIssueDate = personInfo.idIssueDate?string["yyyy-MM-dd"]>
									</#if>
							 		<@htmlTemplate.renderDateTimeField name="idIssueDate" value="${tempIdIssueDate?if_exists}" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="idIssueDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="idIssuePlace">${uiLabelMap.IDIssuePlace}</label></td>
								<td>
									 <input type="text" id="idIssuePlace" name="idIssuePlace" value="${personInfo.idIssuePlace?if_exists}"/>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="passportNumber">${uiLabelMap.PassportNumber}</label></td>
								<td>
							 		<input type="text" id="passportNumber" name="passportNumber"  value="${personInfo.passportNumber?if_exists}"/>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="passportIssuePlace">${uiLabelMap.PassportIssuePlace}</label></td>
								<td>
							 		<input type="text" id="passportIssuePlace" name="passportIssuePlace" value="${personInfo.passportIssuePlace?if_exists}"/>
								</td>
								
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="passportExpiryDate">${uiLabelMap.PassportExpiryDate}</label></td>
								<td style="padding: 0">
									<#if personInfo.passportExpiryDate?exists>
										<#assign tempPassportExpiryDate = personInfo.passportExpiryDate?string["yyyy-MM-dd"]>
									</#if>
							 		<@htmlTemplate.renderDateTimeField name="passportExpiryDate" value="${tempPassportExpiryDate?if_exists}" event=""
							 		 action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" 
							 		 id="passportExpiryDate" dateType="date" 
							 		shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
							 		timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
							 		isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="passportIssueDate">${uiLabelMap.PassportIssueDate}</label></td>
								<td style="padding: 0">
									<#if personInfo.passportIssueDate?exists>	
										<#assign tempPassportIssueDate = personInfo.passportIssueDate?string["yyyy-MM-dd"]>
									</#if>
									 <@htmlTemplate.renderDateTimeField name="passportIssueDate" value="${tempPassportIssueDate?if_exists}" event="" 
									 action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" 
									 id="passportIssueDate" dateType="date" 
									 shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" 
									 localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" 
									 timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="maritalStatus">${uiLabelMap.MaritalStatus}</label></td>
								<td>
									<#assign maritalStatusList = delegator.findList("MaritalStatus", null , null, orderBy,null, false)>
										 <select name = "maritalStatus" id = "maritalStatus">
							 				<option value="">
												&nbsp
							 				</option>
							 				<#list maritalStatusList as maritalStatus>
							 				<option value="${maritalStatus.maritalStatusId}" 
							 					<#if personInfo.maritalStatus?exists && personInfo.maritalStatus == maritalStatus.maritalStatusId>selected="selected"</#if>>
							 					${maritalStatus.description?if_exists}
							 				</option>
							 				</#list>
							 			</select>
								</td>
								<td>
									<label class="padding-bottom5 padding-right15  margin-left30" for="numberChildren">${uiLabelMap.NumberChildren} (${uiLabelMap.HRCommonIfHave})</label>
								</td>
								<td>
									<input type="text" id="numberChildren" name="numberChildren" value="${personInfo.numberChildren?if_exists}"/>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="ethnicOrigin">${uiLabelMap.EthnicOrigin}</label></td>
								<td>
									 <#assign ethnicOriginList = delegator.findList("EthnicOrigin", null , null, orderBy,null, false)>
									 <select name = "ethnicOrigin" id = "ethnicOrigin">
									 	<option value="">
											&nbsp
									 	</option>
									 	<#list ethnicOriginList as ethnicOrigin>
									 		<option value="${ethnicOrigin.ethnicOriginId}" <#if personInfo.ethnicOrigin?exists && personInfo.ethnicOrigin==ethnicOrigin.ethnicOriginId>selected="selected"</#if>>
									 			${ethnicOrigin.description?if_exists}
									 		</option>
									 	</#list>
									 </select>
								</td>			
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="religion">${uiLabelMap.Religion}</label></td>
								<td>
									<#assign religionList = delegator.findList("Religion", null , null, orderBy,null, false)>
									 <select name = "religion" id = "religion">
									 	<option value="">
											&nbsp
									 	</option>
									 	<#list religionList as religion>
									 		<option value="${religion.religionId}"<#if personInfo.religion?exists && personInfo.religion == religion.religionId>selected="selected" </#if>>
									 			${religion.description?if_exists}
									 		</option>
									 	</#list>
									 </select>
								</td>
								<td><label class="padding-bottom5 padding-right15  margin-left30" for="nativeLand">${uiLabelMap.NativeLand}</label></td>
								<td>
									<input name="nativeLand" value="${personInfo.nativeLand?if_exists}" type="text">
								</td>
							</tr>
							
						</tbody>						
					</table>
					</form>
					<div class="row-fluid">
						<div class="span12" style="text-align: center; margin-top: 30px;">
							<button class="btn btn-small btn-primary icon-edit open-sans" id="basicInformationBtn" type="button">
								${uiLabelMap.CommonUpdate}
							</button>
						</div>					
					</div>
				</div>
			</div>
			<!-- <div class="row-fluid">
				<div class="span12 form-actions" style="text-align: center;">
					<button class="btn btn-small btn-primary icon-edit open-sans" id="basicInfortion" type="button">
						${uiLabelMap.CommonUpdate}
					</button>
				</div>	
			</div> -->
			<!--  start contact info -->
			<div class="row-fluid">
				<div class="span12 margin-top30" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
					<div class="span12">
					<div class="title-border">
						<span>${uiLabelMap.ContactInformation}</span>
					</div>
					<table cellspacing="0">
				   		<tbody>
				   			<tr>
				   				
								<td><label class="padding-bottom5 padding-right15" for="">${uiLabelMap.PhoneMobile}</label></td>
								<td>
									<form id="phoneMobileForm">
										<input type="hidden" value="${parameters.partyId}" name="partyId">
										<input type="hidden" name="mobilePhoneContactMechId" value="${mobilePhoneContactMechId?if_exists}" id="mobilePhoneContactMechId">
										<input type="text" name="phone_mobile" id="phone_mobile" value="${mobilePhoneContactNbr?if_exists}"/>
										<button type="button" class="btn btn-mini btn-primary icon-edit margin-bottom-10" id="PhoneMobileBtn"></button>
									</form>
								</td>
								
								<td><label class="padding-bottom5 padding-right15 margin-left30" for="primaryEmailAddress">${uiLabelMap.PrimaryEmailAddress}</label></td>
	   							<td>
	   								<form id="primaryEmailForm">
	   									<input type="hidden" value="${parameters.partyId}" name="partyId">
		   								<input type="hidden" name="primaryEmailContactmechId" value="${primaryEmailContactmechId?if_exists}" id="primaryEmailContactmechId">
		    							<input type="text" size="60" maxlength="255" name="primaryEmailAddress" id="primaryEmailAddress" value="${primaryEmailAddress?if_exists}"/>
		    							<button type="button" class="btn btn-mini btn-primary icon-edit open-sans margin-bottom-10" id="PrimaryEmailBtn"></button>
	    							</form>
	   							</td>
	   							
							</tr>
							<tr>	
								<td><label class="padding-bottom5 padding-right15" for="phone_home">${uiLabelMap.PhoneHome}</label></td>
								<td>	
									<form id="homePhoneForm">
										<input type="hidden" value="${parameters.partyId}" name="partyId">
										<input type="hidden" value="${homePhoneContactMechId?if_exists}" name="homePhoneContactMechId" id="homePhoneContactMechId">
										<input type="text" name="phone_home" id="phone_home" value="${homePhoneContactNbr?if_exists}"/>
										<button type="button" class="btn btn-mini btn-primary icon-edit open-sans margin-bottom-10" id="homePhonelBtn" ></button>
									</form>
								</td>
								<td><!-- <label class="padding-bottom5 padding-right15 margin-left30" for="otherEmailAddress">${uiLabelMap.OtherEmailAddress}</label> --></td>
	   							<td>
	   								<#--<!-- <form id="otherEmailForm">
	   									<input type="hidden" value="${parameters.partyId}" name="partyId">
		   								<input type="hidden" name="otherEmailContactmechId" value="${otherEmailContactmechId?if_exists}" id="otherEmailContactmechId">
		    							<input type="text" size="60" maxlength="255" name="otherEmailAddress" id="otherEmailAddress" value="${otherEmailAddress?if_exists}"/>
		    							<button type="button" class="btn btn-mini btn-primary icon-edit open-sans" id="otherEmailBtn"></button>
	    							</form> -->
	   							</td>
							</tr>
							
						</tbody>
			   		</table>
			   		</div>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="span12 margin-top30">
					<div class="span6" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 0px;">
						<div class="title-border">
							<span>${uiLabelMap.PermanentResidence}</span>
						</div>
						<form id="PermanentResidenceForm">
							<input type="hidden" name="permanentResidenceContactmechId" value="${permanentResidenceContactmechId?if_exists}" id="permanentResidenceContactmechId">
							<input type="hidden" value="${parameters.partyId}" name="partyId">
							<table cellspacing="0">
							<tr>
								<td>
									<label class="padding-bottom5 padding-right15 asterisk" for="address1_PermanentResidence">
										${uiLabelMap.PartyAddressLine}
									</label>
								</td>
								<td>
									<input type="text" name="address1_PermanentResidence" id="address1_PermanentResidence" value="${permanentResidenceAddress1?if_exists}"/>
								</td>
							</tr>
							<tr>   
	  							<td><label class="padding-bottom5 padding-right15" for="permanentResidence_countryGeoId">
	  								${uiLabelMap.CommonCountry}</label></td>
	  							<td>     
	    							<select name="permanentResidence_countryGeoId" id="permanentResidence_countryGeoId">
	      								${screens.render("component://common/widget/CommonScreens.xml#countries")}        
	       								
	      								<#if permanentResidenceCountry?exists>
	      									<#assign defaultCountryGeoId = permanentResidenceCountry>
	      								<#else>
	      									<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>	 
	      								</#if>
	      								<option selected="selected" value="${defaultCountryGeoId}">
	        								<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
	        								${countryGeo.get("geoName",locale)}
	      								</option>
	    							</select>
	  							</td>
							</tr>
							<tr>
	  							<td><label class="padding-bottom5 padding-right15" for="createEmployee_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
	  							<td>
	   	 							<select name="permanentResidence_stateProvinceGeoId" id="permanentResidence_stateProvinceGeoId">
	    							</select>
	  							</td>
							</tr>
							<tr>
	  							<td>
	  								<label class="padding-bottom5 padding-right15" for="permanentResidence_districtGeoId">
	  									${uiLabelMap.PartyDistrictGeoId}
	  								</label>
	 								</td>
	  							<td>
	   	 							<select name="permanentResidence_districtGeoId" id="permanentResidence_districtGeoId">
	    							</select>
	  							</td>
							</tr>
							<tr>
	  							<td>
	  								<label class="padding-bottom5 padding-right15" for="permanentResidence_wardGeoId">
	  									${uiLabelMap.PartyWardGeoId}
	  								</label>
	 								</td>
	  							<td>
	   	 							<select name="permanentResidence_wardGeoId" id="permanentResidence_wardGeoId">
	    							</select>
	  							</td>
							</tr>
							</table>
							<div class="row-fluid">
								<div class="span12" style="text-align: center; margin-top: 30px;">
									<button class="btn btn-small btn-primary icon-edit open-sans" id="permanentResidenceBtn" type="button">
										${uiLabelMap.CommonUpdate}
									</button>
								</div>					
							</div>
						</form>
					</div>
					<div class="span1" style="display: block; margin-top: 100px">
						<button class="btn btn-small btn-primary" id="copyContactInfo" type="button">
							<i class="icon-arrow-right"></i>
						</button>
					</div>
					<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-right: 0px;">
						<div class="title-border">
							<span>${uiLabelMap.CurrentResidence}</span>
						</div>
						<form id="CurrentResidenceForm">
							<input type="hidden" name="currentResidenceContactmechId" value="${currentResidenceContactmechId?if_exists}" id="currentResidenceContactmechId">
							<input type="hidden" value="${parameters.partyId}" name="partyId">
							<table cellspacing="0">
							<tr>
								<td>
									<label class="padding-bottom5 padding-right15" for="address1_CurrResidence">
										${uiLabelMap.PartyAddressLine}
									</label>
								</td>
								<td>
									<input type="text" name="address1_CurrResidence" id="address1_CurrResidence" value="${currentResidenceAddress1?if_exists}"/>
								</td>
							</tr>
							<tr>   
	  							<td><label class="padding-bottom5 padding-right15" for="currResidence_countryGeoId">${uiLabelMap.CommonCountry}</label></td>
	  							<td>     
	    							<select name="currResidence_countryGeoId" id="currResidence_countryGeoId">
	      								${screens.render("component://common/widget/CommonScreens.xml#countries")}        
	      								<#if currentResidenceCountry?exists>
	      									<#assign defaultCountryGeoId = currentResidenceCountry> 
	      								<#else>
	       									<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
	      								</#if>
	      								<option selected="selected" value="${defaultCountryGeoId}">
	        								<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
	        								${countryGeo.get("geoName",locale)}
	      								</option>
	    							</select>
	  							</td>
							</tr>
							<tr>
	  							<td><label class="padding-bottom5 padding-right15" for="currResidence_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
	  							<td>
	   	 							<select name="currResidence_stateProvinceGeoId" id="currResidence_stateProvinceGeoId">
	    							</select>
	  							</td>
							</tr>
							<tr>
	  							<td>
	  								<label class="padding-bottom5 padding-right15" for="currResidence_districtGeoId">
	  									${uiLabelMap.PartyDistrictGeoId}
	  								</label>
	 								</td>
	  							<td>
	   	 							<select name="currResidence_districtGeoId" id="currResidence_districtGeoId">
	    							</select>
	  							</td>
							</tr>
							<tr>
	  							<td>
	  								<label class="padding-bottom5 padding-right15" for="currResidence_wardGeoId">
	  									${uiLabelMap.PartyWardGeoId}
	  								</label>
	 								</td>
	  							<td>
	   	 							<select name="currResidence_wardGeoId" id="currResidence_wardGeoId">
	    							</select>
	  							</td>
							</tr>
							</table>
							<div class="row-fluid">
								<div class="span12" style="text-align: center; margin-top: 30px;">
									<button class="btn btn-small btn-primary icon-edit open-sans" id="currResidenceBtn" type="button">
										${uiLabelMap.CommonUpdate}
									</button>
								</div>					
							</div>
						</form>
					</div>
				</div>
			</div>
			
			
			<!-- end contact info -->			
			<div class="row-fluid">
				<div class="span12 margin-top30" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
					<div class="title-border">
						<span>${uiLabelMap.HRCommonEducation}</span>
					</div>
					<table class="table table-hover table-striped table-bordered dataTable" id="PersonEducationTable">
					    <thead>
					        <tr class="header-row">
					            <th>
					                ${uiLabelMap.HRCollegeName}
					            </th>
					            <th>
					                ${uiLabelMap.CommonFromDate}
					            </th>
					            <th>
					                ${uiLabelMap.CommonThruDate}
					            </th>
					            <th>
					                ${uiLabelMap.HRSpecialization}
					            </th>
					            <th>
					                ${uiLabelMap.HROlbiusTrainingType}
					            </th>
					            <th>
					                ${uiLabelMap.HRCommonClassification}
					            </th>
					            <th>
					            	${uiLabelMap.CommonDelete}
					            </th>
					        </tr>
					    </thead>
					    <tbody>
					    	<#assign iEducation=0>
					    	<#list partyEducation as partyEdu>
					    		<#assign iEducation=iEducation+1>
						        <tr id='partyEducation_${partyId}_${i}'>
						        	<td>
						        		<#assign school = delegator.findOne("EducationSchool", Static["org.ofbiz.base.util.UtilMisc"].toMap("schoolId", partyEdu.schoolId),false)>
						        		${school.schoolName?if_exists}
						        	</td>	
						        	<td>
						        		<#if partyEdu.fromDate?exists>
						        			${partyEdu.fromDate?date}
						        		</#if>
						        	</td>
						        	<td>
						        		<#if partyEdu.thruDate?exists>
						        			${partyEdu.thruDate?date}
						        		</#if>
						        	</td>
						        	<td>
						        		<#if partyEdu.majorId?exists>
						        			<#assign major = delegator.findOne("Major", Static["org.ofbiz.base.util.UtilMisc"].toMap("majorId", partyEdu.majorId), false)>
					 						${major.description?if_exists}
						        		</#if>
						        	</td>
						        	<td>
						        		<#if partyEdu.studyModeTypeId?exists>
						        			<#assign studyModeType = delegator.findOne("StudyModeType", Static["org.ofbiz.base.util.UtilMisc"].toMap("studyModeTypeId", partyEdu.studyModeTypeId), false)>
						        			${studyModeType.description}
						        		</#if>
						        	</td>
						        	<td>
						        		<#if partyEdu.classificationTypeId?exists>
						        			<#assign studyModeType = delegator.findOne("DegreeClassificationType", Static["org.ofbiz.base.util.UtilMisc"].toMap("classificationTypeId", partyEdu.classificationTypeId), false)>
						        			${studyModeType.description}
						        		</#if>
						        	</td>
					        		<td>
					        			<button type='button' class="btn btn-small btn-danger btn-mini icon-trash open-sans" onclick="deletePersonEducation('${partyEdu.schoolId}','${partyId}','${partyEdu.majorId}','${partyEdu.studyModeTypeId}','${partyEdu.educationSystemTypeId}','${partyEdu.fromDate}','partyEducation_${partyId}_${iEducation}')">${uiLabelMap.CommonDelete}</button>
					        		</td>
						        	
						        </tr>  
					        </#list>        
					    </tbody>
					</table>
					<h5 class="pink">
						<i class="icon-hand-right icon-animated-hand-pointer blue"></i>
						<a href="#AddPersonEducation" role="button" class="blue" data-toggle="modal">${uiLabelMap.AddPersonEducation}</a>
					</h5>
				</div>	
			</div>
			
			<div class="row-fluid">
				<div class="span12 margin-top30" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
					<div class="title-border">
						<span>${uiLabelMap.HRWorkingProcess}</span>
					</div>
					<table class="table table-hover table-striped table-bordered dataTable" id="workingProcessTable">
					    <thead>
					        <tr class="header-row">
					            <th>
					                ${uiLabelMap.CommonTime}
					            </th>
					            <th>
					                ${uiLabelMap.CompanyName}
					            </th>
					            <th>
					                ${uiLabelMap.EmplPositionTypeId}
					            </th>
					            <th>
					                ${uiLabelMap.JobDescription}
					            </th>
					            <th>
					                ${uiLabelMap.HRSalary}
					            </th>
					            <th>
					                ${uiLabelMap.TerminationReason}
					            </th>
					            <th>
					                ${uiLabelMap.HRRewardAndDisciplining}
					            </th>
					            <th>
					                ${uiLabelMap.CommonUpdate}
					            </th>
					            <th>
					                ${uiLabelMap.CommonDelete}
					            </th>
					        </tr>
					    </thead>
					    <tbody>
					        <#list personWorkingProcess as workingProcess>
					        	<tr id="personWorkingProcess_${workingProcess.personWorkingProcessId}">
					        		<td>
					        			<#if workingProcess.fromDate?exists>
					        				${workingProcess.fromDate?date}
					        			<#else>
					        				&#63; <!-- ? -->	 
					        			</#if>
					        			&#45;
					        			<#if workingProcess.thruDate?exists>
					        				${workingProcess.thruDate?date}
					        			<#else>
					        				&#63; <!-- ? -->	 
					        			</#if>
					        		</td>
					        		<td>
					        			${workingProcess.companyName?if_exists}
					        		</td>
					        		<td>
					        			${workingProcess.emplPositionTypeId?if_exists}
					        		</td>
					        		<td>
					        			${workingProcess.jobDescription?if_exists}
					        		</td>
					        		<td>
					        			${workingProcess.payroll?if_exists}
					        		</td>
					        		<td>
					        			${workingProcess.terminationReasonId?if_exists}
					        		</td>
					        		<td>
					        			${workingProcess.rewardDiscrip?if_exists}
					        		</td>
					        		<td>
					        			<button type='button' class="btn btn-small btn-mini btn-primary icon-edit open-sans" onclick="editWorkingProcess('${workingProcess.personWorkingProcessId}','personWorkingProcess_${workingProcess.personWorkingProcessId}','${workingProcess.fromDate?if_exists}','${workingProcess.thruDate?if_exists}','${workingProcess.companyName?if_exists}','${workingProcess.emplPositionTypeId?if_exists}','${workingProcess.jobDescription?if_exists}','${workingProcess.payroll?if_exists}','${workingProcess.terminationReasonId?if_exists}','${workingProcess.rewardDiscrip?if_exists}')">${uiLabelMap.CommonUpdate}</button>
					        		</td>
					        		<td>
					        			<button type='button' class="btn btn-small btn-danger btn-mini icon-trash open-sans" onclick="deleteWorkingProcess(${workingProcess.personWorkingProcessId},'personWorkingProcess_${workingProcess.personWorkingProcessId}')">${uiLabelMap.CommonDelete}</button>
					        		</td>
					        	</tr>
					        </#list>  
					    </tbody>
					</table>	
					<h5 class="pink">
						<i class="icon-hand-right icon-animated-hand-pointer blue"></i>
						<a href="#AddWorkingProcess" role="button" class="blue" data-toggle="modal">${uiLabelMap.AddWorkingProcess}</a>
					</h5>	
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="span12 margin-top30" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
					<div class="title-border">
						<span>${uiLabelMap.HRFamilyBackground}</span>
					</div>
					<table class="table table-hover table-striped table-bordered dataTable" id="familyBackgroundTable">
					    <thead>
					        <tr class="header-row">
					            <th>
					                ${uiLabelMap.FullName}
					            </th>
					            <th>
					                ${uiLabelMap.HRRelationship}
					            </th>
					            <th>
					                ${uiLabelMap.BirthDate}
					            </th>
					            <th>
					                ${uiLabelMap.HROccupation}
					            </th>
					            <th>
					                ${uiLabelMap.PhoneNumber}
					            </th>
					             <th>
					                ${uiLabelMap.EmergencyContact}
					            </th>
					            <th>
					            	${uiLabelMap.CommonUpdate}
					            </th>
					            <th>
					            	${uiLabelMap.CommonDelete}
					            </th>
					        </tr>
					    </thead>
					    <tbody>
					        <#list personFamilyBackground as background>
					        	<tr id="personFamilyBackground_${background.personFamilyBackgroundId}">
					        		<#assign familyPerson = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", background.partyFamilyId), false)>
					        		<td>
					        			${familyPerson.lastName?if_exists}
					        			${familyPerson.middleName?if_exists} 
					        			${familyPerson.firstName?if_exists}
				        			</td>
					        		<td>
					        			<#assign partyRelationshipType = delegator.findOne("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyRelationshipTypeId", background.partyRelationshipTypeId), false)>
					        			${partyRelationshipType.partyRelationshipName?if_exists}
					        		</td>
					        		<td>
					        			<#if familyPerson.birthDate?exists> 									        				
					        				${familyPerson.birthDate}
					        			</#if>
					        		</td>
					        		<td>
					        			${background.occupation?if_exists}
					        		</td>
					        		<td>
					        			<#assign telecomNbr = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", background.partyFamilyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "userLogin", systemUserLogin))>
					        			${telecomNbr.contactNumber?if_exists}
					        		</td>
					        		
					        		<td>
					        			${background.emergencyContact?if_exists}
					        		</td>
					        		
					        		<td>
					        			<button type='button' class="btn btn-small btn-mini btn-primary icon-edit open-sans" onclick="editPersonFamily(${background.personFamilyBackgroundId},'personFamilyBackground_${background.personFamilyBackgroundId}')">${uiLabelMap.CommonUpdate}</button>
					        		</td>
					        		<td>
					        			<button type='button' class="btn btn-small btn-danger btn-mini icon-trash open-sans" onclick="deletePersonBackground(${background.personFamilyBackgroundId},'personFamilyBackground_${background.personFamilyBackgroundId}')">${uiLabelMap.CommonDelete}</button>
					        		</td>
					        		
					        	</tr>
					        </#list>
					    </tbody>
					</table>
					<h5 class="pink">
						<i class="icon-hand-right icon-animated-hand-pointer blue"></i>
						<a href="#FamilyBackground" role="button" class="blue" data-toggle="modal">${uiLabelMap.HRFamilyBackground}</a>
					</h5>	
				</div>
			</div>
			
				
			<#--<!-- <div class="row-fluid">
				<div class="span12 margin-top30 form-horizontal" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
					<div class="title-border">
						<span>${uiLabelMap.HRCommonPartySkill}</span>
					</div>
					<div id="skillTypeTableContain">
					
					</div>
					<#assign SkillTypeList = delegator.findByAnd("SkillType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", null), null, false)>
					<div class="control-group">
						<label>
							<label for="SkillTypeList" id="SkillTypeList_title">${uiLabelMap.SkillType}</label>  
						</label>
						<div class="controls">
							<select name="SkillTypeList" id="SkillTypeList">
								<option value="">&nbsp;</option>
							 	<#list SkillTypeList as skillType>
							 		<option value="${skillType.skillTypeId}">
							 			${skillType.description?if_exists}
							 		</option>
							 	</#list>
							</select>
						</div>
					</div>
					<div id="skillTypeId">
						
					</div>
					<div class="control-group">
						<label>
							<label>&nbsp;</label>  
						</label>
						<div class="controls">
							<button class="btn btn-small btn-primary" type="button" data-dismiss="modal" id="skillTypeBtn">
								<i class="icon-ok open-sans"></i>
								${uiLabelMap.CommonAdd}
							</button>
						</div>
					</div>
				</div>
			</div> -->
			<!-- dropdown box -->
			<div id="FamilyBackground" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.FamilyBackground}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form action="" name="FamilyBackground" id="FamilyBackgroundForm">
						<input type="hidden" value="${parameters.partyId}" name="partyId">
						<div class="row-fluid form-horizontal">							
							<div class="control-group">
								<label class="control-label">
									<label for="lastNameFamily" class="asterisk" id="_title">${uiLabelMap.LastName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="lastNameFamily" id="lastNameFamily">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="middleNameFamily" class="asterisk" id="_title">${uiLabelMap.MiddleName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="middleNameFamily" id="middleNameFamily">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="firstNameFamily" class="asterisk" id="_title">${uiLabelMap.FirstName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="firstNameFamily" id="firstNameFamily">
								</div>
							</div>
							<div class="control-group">
								<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
								<label class="control-label">
									<label for="relationship" class="asterisk" id="_title">${uiLabelMap.HRRelationship}</label>  
								</label>
								<div class="controls">
									<select name="partyRelationshipTypeId" id="relationship">
										<#list partyRelationshipType as partyRel>
											<option value="${partyRel.partyRelationshipTypeId}">${partyRel.partyRelationshipName}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="familyBirthDate" class="asterisk" id="personAge_title">${uiLabelMap.BirthDate}</label>   
								</label>
								<div class="controls">
									<!-- <input type="text" name="age" id="personAge"> -->
									<@htmlTemplate.renderDateTimeField name="birthDate" id="familyBirthDate" value="" event="" 
									action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" 
									shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="occupation" class="asterisk" id="occupation_title">${uiLabelMap.HROccupation}</label>   
								</label>
								<div class="controls">
									<input type="text" name="occupation" id="occupation">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="placeWork" class="asterisk" id="placeWork_title">${uiLabelMap.HRPlaceWork}</label>   
								</label>
								<div class="controls">
									<input type="text" name="placeWork" id="placeWork">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="phoneNumber" class="asterisk" id="placeWork_title">${uiLabelMap.PhoneNumber}</label>   
								</label>
								<div class="controls">
									<input type="text" name="phoneNumber" id="phoneNumber">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="emergencyContact" id="placeWork_title">${uiLabelMap.EmergencyContact}</label>   
								</label>
								<div class="controls">
									<input type="checkbox" name="emergencyContact" id="emergencyContact" value="Y"/><span class="lbl"> </span>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label>&nbsp;</label>  
								</label>
								<div class="controls">
									<button class="btn btn-small btn-primary" data-dismiss="modal" id="FamilyBackgroundBtn" type="button">
										<i class="icon-ok open-sans"></i>
										${uiLabelMap.CommonSubmit}
									</button>
								</div>
							</div>
						</div>
					</form>
				</div>			
			</div>
			<!-- end dropdown box -->
			
			<div id="EditInfoPersonFamily" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.FamilyBackground}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form action="" name="EditInfoPersonFamilys" id="EditInfoPersonFamilys">
						<input type="hidden" value="" name="idTr" id="idTr"/>
						<input type="hidden" value="" name="contactMechId" id="contactMechId"/>
						<input type="hidden"value="" name="personFamilyBackgroundId" id="personFamilyBackgroundId"/>
						<div class="row-fluid form-horizontal">
							
							<div class="control-group">
								<label class="control-label">
									<label for="xlastNameFamily" class="asterisk" id="_title">${uiLabelMap.LastName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="xlastNameFamily" id="xlastNameFamily">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xmiddleNameFamily" class="asterisk" id="_title">${uiLabelMap.MiddleName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="xmiddleNameFamily" id="xmiddleNameFamily">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xfirstNameFamily" class="asterisk" id="_title">${uiLabelMap.FirstName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="xfirstNameFamily" id="xfirstNameFamily">
								</div>
							</div>
							<div class="control-group">
								<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
								<label class="control-label">
									<label for="xrelationship" class="asterisk" id="_title">${uiLabelMap.HRRelationship}</label>  
								</label>
								<div class="controls">
									<select name="xpartyRelationshipTypeId" id="xrelationship">
										<#list partyRelationshipType as partyRel>
											<option value="${partyRel.partyRelationshipTypeId}">${partyRel.partyRelationshipName}</option>
										</#list>
									</select>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xfamilyBirthDate" class="asterisk" id="xpersonAge_title">${uiLabelMap.BirthDate}</label>   
								</label>
								<div class="controls">
									<!-- <input type="text" name="age" id="personAge"> -->
									<@htmlTemplate.renderDateTimeField name="xbirthDate" id="xfamilyBirthDate" value="" event="" action="" 
									className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date"
									 shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xoccupation" class="asterisk" id="xoccupation_title">${uiLabelMap.HROccupation}</label>   
								</label>
								<div class="controls">
									<input type="text" name="xoccupation" id="xoccupation">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xplaceWork" class="asterisk" id="xplaceWork_title">${uiLabelMap.HRPlaceWork}</label>   
								</label>
								<div class="controls">
									<input type="text" name="xplaceWork" id="xplaceWork">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xphoneNumber" class="asterisk" id="xplaceWork_title">${uiLabelMap.PhoneNumber}</label>   
								</label>
								<div class="controls">
									<input type="text" name="xphoneNumber" id="xphoneNumber">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xemergencyContact" id="xplaceWork_title">${uiLabelMap.EmergencyContact}</label>   
								</label>
								<div class="controls">
									<input type="checkbox" name="xemergencyContact" id="xemergencyContact" value="Y"/><span class="lbl"> </span>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label>&nbsp;</label>  
								</label>
								<div class="controls">
									<button class="btn btn-small btn-primary" data-dismiss="modal" id="EditFamilyBackgroundBtn" type="button">
										<i class="icon-ok open-sans"></i>
										${uiLabelMap.CommonSubmit}
									</button>
								</div>
							</div>
						</div>
					</form>
				</div>			
			</div>
			
			
			<!-- dropdown box -->	
			<div id="AddPersonEducation" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.AddPersonEducation}
					</div>
				</div>
			
				<div class="modal-body no-padding">
					<form action="" name="PersonEducation" id="PersonEducationForm">
					<input type="hidden" value="${parameters.partyId}" name="partyId">
					<div class="row-fluid form-horizontal">
						<div class="control-group">
							<label class="control-label">
								<label for="school" class="asterisk" id="CollegeName_title">${uiLabelMap.HRCollegeName}</label>  
							</label>
							<div class="controls">
								<span class="ui-widget">
									<#assign schoolList = delegator.findByAnd("EducationSchool", null, null, false)>
									<select name="schoolId" id="school">
									 	<#list schoolList as school>
									 		<option value="${school.schoolId}">
									 			${school.schoolName?if_exists}
									 		</option>
									 	</#list>
								 	</select>
								</span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label for="educationFromDate" class="asterisk" id="educationFromDate_title">${uiLabelMap.CommonFromDate}</label>  
							</label>
							<div class="controls">
								<@htmlTemplate.renderDateTimeField name="educationFromDate" id="educationFromDate" value="" event="" 
								action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30"  
								dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" 
								localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" 
								timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label for="educationFromDate" class="asterisk" id="CollegeName_title">${uiLabelMap.CommonThruDate}</label>  
							</label>
							<div class="controls">
								<@htmlTemplate.renderDateTimeField name="educationThruDate" id="educationThruDate" value="" event="" 
								action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" 
								maxlength="30" dateType="date" shortDateInput=true timeDropdownParamName="" 
								defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
								classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" 
								amSelected="" pmSelected="" compositeType="" formName=""/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label for="major" class="asterisk" id="major_title">${uiLabelMap.HRSpecialization}</label>  
							</label>
							<div class="controls">
								<#assign majorList = delegator.findList("Major", null , null, orderBy,null, false)>
								 <select name = "majorId" id = "major">
								 	<#list majorList as major>
								 		<option value="${major.majorId}">
								 			${major.description?if_exists}
								 		</option>
								 	</#list>
								 </select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label for="studyModeType" class="asterisk" id="studyMode_title">${uiLabelMap.HROlbiusTrainingType}</label>  
							</label>
							<div class="controls">
								<#assign studyModeType = delegator.findByAnd("StudyModeType", null ,null , false)>
								 <select name="studyModeTypeId" id="studyModeType">
								 	<#list studyModeType as type>
								 		<option value="${type.studyModeTypeId}">
								 			${type.description?if_exists}
								 		</option>
								 	</#list>
								 </select>	
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label for="degreeClassificationType" class="asterisk" id="degreeClassificationType_title">${uiLabelMap.HRCommonClassification}</label>  
							</label>
							<div class="controls">
								<#assign degreeClassificationType = delegator.findByAnd("DegreeClassificationType", null, null, false)>
								<select name="degreeClassificationTypeId" id="degreeClassificationType">
								 	<#list degreeClassificationType as classification>
								 		<option value="${classification.classificationTypeId}">
								 			${classification.description?if_exists}
								 		</option>
								 	</#list>
								</select>	
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label for="educationSystemType" class="asterisk" id="educationSystemType_title">${uiLabelMap.HRCommonSystemEducation}</label>  
							</label>
							<div class="controls">
								<#assign educationSystemType = delegator.findByAnd("EducationSystemType", null, null, false)>
								<select name="educationSystemTypeId" id="educationSystemType">
								 	<#list educationSystemType as systemType>
								 		<option value="${systemType.educationSystemTypeId}">
								 			${systemType.description?if_exists}
								 		</option>
								 	</#list>
								</select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">
								<label>&nbsp;</label>  
							</label>
							<div class="controls">
								<button class="btn btn-small btn-primary" data-dismiss="modal" id="PersonEducationBtn" type="button">
									<i class="icon-ok open-sans"></i>
									${uiLabelMap.CommonSubmit}
								</button>
							</div>
						</div>
					</div>
					</form>
				</div>
			</div>
			
			<!-- end dropdown box -->
			<!-- dropdown box -->	
			<div id="AddWorkingProcess" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.AddWorkingProcess}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form name="WorkingProcess" id="WorkingProcessForm">
						<input type="hidden" value="${parameters.partyId}" name="partyId">
						<div class="row-fluid basic-form form-horizontal">
							<div class="control-group">
								<label class="control-label asterisk" for="workProcess_fromDate"id="workProcess_fromDate_title">
									${uiLabelMap.CommonFromDate}
								</label>
								<div class="controls">
									<@htmlTemplate.renderDateTimeField name="workProcess_fromDate" id="workProcess_fromDate" 
									value="" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" 
									size="25" maxlength="30" dateType="date" shortDateInput=true timeDropdownParamName="" 
									defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName=""
									 classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" 
									 ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label asterisk" for="workProcess_thruDate"  id="workProcess_thruDate_title">
									${uiLabelMap.CommonThruDate}
								</label>
								<div class="controls">
									<@htmlTemplate.renderDateTimeField name="workProcess_thruDate" id="workProcess_thruDate" value="" 
									event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25"
									 maxlength="30" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label asterisk" for="workProcess_companyName"  id="workProcess_companyName_title">
									${uiLabelMap.CompanyName}
								</label>
								<div class="controls">
									<input type="text" name="companyName" id="workProcess_companyName">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label asterisk" for="workProcess_EmplPositionType" id="workProcess_companyName_title">
									${uiLabelMap.EmplPositionTypeId}  
								</label>
								<div class="controls">
									<input type="text" name="emplPositionTypeIdWorkProcess" id="workProcess_EmplPositionType">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label asterisk" for="workProcess_JobDescription"  id="workProcess_JobDescription_title">
									${uiLabelMap.JobDescription}
								</label>
								<div class="controls">
								
										<input type="text" name="jobDescription" id="workProcess_JobDescription">
								
								</div>
							</div>
							<div class="control-group">
								<label class="control-label asterisk" for="workProcess_Payroll" id="workProcess_Payroll_title">
									${uiLabelMap.HRSalary}
								</label>
								<div class="controls">
										<input type="text" name="payroll" id="workProcess_Payroll">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="workProcess_TerminationReason" class="asterisk" id="workProcess_TerminationReason_title">
									${uiLabelMap.TerminationReason}  
								</label>
								<div class="controls">
										<input type="text" name="terminationReasonId" id="workProcess_TerminationReason">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="workProcess_rewardDiscrip" id="workProcess_rewardDiscrip_title">
									${uiLabelMap.HRRewardAndDisciplining}
								</label>
								<div class="controls">
										<input type="text" name="rewardDiscrip" id="workProcess_rewardDiscrip">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									&nbsp;  
								</label>
								<div class="controls">
									<button class="btn btn-small btn-primary" data-dismiss="modal" id="WorkingProcessBtn" type="button">
										<i class="icon-ok open-sans"></i>
										${uiLabelMap.CommonSubmit}
									</button>
								</div>
							</div>
						</div>
					</form>
				</div>		
			</div>
			
			
			
			
			
			<!-- end dropdown box -->
		</div>
	</div>
</div>		
			<div id="EditWorkingProcess" class="modal hide fade" tabindex="-1">
				<div class="modal-header no-padding">
					<div class="table-header">
						<button type="button" class="close" data-dismiss="modal">&times;</button>
						${uiLabelMap.AddWorkingProcess}
					</div>
				</div>
				<div class="modal-body no-padding">
					<form name="EditWorkingProcess" id="EditWorkingProcessForm">
						<input type="hidden" value="" name="personWorkingProcessId" id="personWorkingProcessId"/>
						<input type="hidden" value="" name="processIdTr" id="processIdTr"/>
						<div class="row-fluid form-horizontal">
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_fromDate" class="asterisk" id="xworkProcess_fromDate_title">${uiLabelMap.CommonFromDate}</label>  
								</label>
								<div class="controls">
									<@htmlTemplate.renderDateTimeField name="xworkProcess_fromDate" id="xworkProcess_fromDate" value="" 
									event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25"
									 maxlength="30" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" 
									 localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName=""
									  minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_thruDate" class="asterisk" id="xworkProcess_thruDate_title">${uiLabelMap.CommonThruDate}</label>  
								</label>
								<div class="controls">
									<@htmlTemplate.renderDateTimeField name="xworkProcess_thruDate" 
										id="xworkProcess_thruDate" value="" event="" action="" className="" 
										alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date"
										 shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" 
										 localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
										 hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" 
										 amSelected="" pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_companyName" class="asterisk" id="xworkProcess_companyName_title">${uiLabelMap.CompanyName}</label>  
								</label>
								<div class="controls">
									<input type="text" name="xcompanyName" id="xworkProcess_companyName">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_EmplPositionType" class="asterisk" id="xworkProcess_companyName_title">${uiLabelMap.EmplPositionTypeId}</label>  
								</label>
								<div class="controls">
									<input type="text" name="xemplPositionTypeIdWorkProcess" id="xworkProcess_EmplPositionType">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_JobDescription" class="asterisk" id="xworkProcess_JobDescription_title">${uiLabelMap.JobDescription}</label>  
								</label>
								<div class="controls">
										<input type="text" name="xjobDescription" id="xworkProcess_JobDescription">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_Payroll" class="asterisk" id="xworkProcess_Payroll_title">${uiLabelMap.HRSalary}</label>  
								</label>
								<div class="controls">
										<input type="text" name="xpayroll" id="xworkProcess_Payroll">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_TerminationReason" class="asterisk" id="xworkProcess_TerminationReason_title">${uiLabelMap.TerminationReason}</label>  
								</label>
								<div class="controls">
										<input type="text" name="xterminationReasonId" id="xworkProcess_TerminationReason">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label for="xworkProcess_rewardDiscrip" class="asterisk" id="xworkProcess_rewardDiscrip_title">${uiLabelMap.HRRewardAndDisciplining}</label>  
								</label>
								<div class="controls">
										<input type="text" name="xrewardDiscrip" id="xworkProcess_rewardDiscrip">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">
									<label>&nbsp;</label>  
								</label>
								<div class="controls">
									<button class="btn btn-small btn-primary" data-dismiss="modal" id="EditWorkingProcessBtn" type="button">
										<i class="icon-ok open-sans"></i>
										${uiLabelMap.CommonSubmit}
									</button>
								</div>
							</div>
						</div>
					</form>
				</div>		
			</div>
			<style>
				/* .ui-datepicker {
				    position: relative !important;
				    top: -290px !important;
				    left: 0 !important;
				} */
			</style>
<script type="text/javascript">
$(document).ready(function() {
	$.validator.addMethod('validateToDay',function(value,element){
		if(value!=null||value!=undefined){
			var now = new Date();
			now.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy")<=now;
		}else{
			return true;
		}
	},'less than today');
	
	
	
	/* jQuery("#workProcess_fromDate").datepicker({
		beforeShow: function(input, inst){
			//inst.dpDiv.css({marginTop: (2 * input.offsetHeight) + 'px', marginLeft: input.offsetWidth + 'px'});
		}
	});
	beforeShow: function (input, inst) {
              var offset = $(input).offset();
              var height = $(input).height();
              window.setTimeout(function () {
                  inst.dpDiv.css({position:'a', top: 0 + 'px', left: 0});
              }, 1);
          }
	*/
$.validator.addMethod("greaterThan", 
	function(value, element, params) {
		if(value){
		 	return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");	
		 			
		}else{
			return true;
		}
	},'Must be greater than');
	
	$("#phoneMobileForm").validate({
		errorElement: 'div',
		errorClass: 'red-color',
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		focusInvalid: false,
		rules: {			
			phone_mobile:{
				number: true
			},
		},
		messages: {
			phone_mobile:{
				number: "${uiLabelMap.RequiredValueIsNumber}"
			},			
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		submitHandler: function (form) {
		},
		invalidHandler: function (form) {
		}
	});		
	
	$("#homePhoneForm").validate({
		errorElement: 'div',
		errorClass: 'red-color',
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		focusInvalid: false,
		rules: {			
			phone_home:{
				number: true
			},
			
		},
		messages: {
			
			phone_home:{
				number: "${uiLabelMap.RequiredValueIsNumber}"
			},			
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		submitHandler: function (form) {
		},
		invalidHandler: function (form) {
		}
	});
	
	
	$('#basicInformation').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		focusInvalid: false,
		rules: {
			firstName: {
				required: true,
			},
			lastName: {
				required: true,
			},
			
			birthDate_i18n:{
				validateToDay:true,
			},
			passportIssueDate_i18n:{
				validateToDay:true
			},
			idIssueDate_i18n:{
				validateToDay:true
			},
			passportExpiryDate_i18n:{
				greaterThan:'#passportIssueDate_i18n'
			},
			
		},

		messages: {
			firstName: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},
			lastName: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},
			
			birthDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredBirthDay)}'
			},
			passportIssueDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredPassPortDate)}'
			},
			passportExpiryDate_i18n:{
				greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredPassPortExpiryDate)}'
			},
			idIssueDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredPassPortDate)}'
			},
			
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		submitHandler: function (form) {
		},
		invalidHandler: function (form) {
		}
		
	});
	jQuery("#currResidenceBtn").click(function(event){
		event.preventDefault();
		var formData = jQuery("#CurrentResidenceForm").serializeArray();
		jQuery.ajax({
			url: "<@ofbizUrl>updateCurrentResidence</@ofbizUrl>",
			data: formData,
			type: "POST",
			success: function(data){
				jQuery("#currentResidenceContactmechId").val(data.contactMechId);
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
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	
	jQuery("#permanentResidenceBtn").click(function(event){
		event.preventDefault();
		var formData = jQuery("#PermanentResidenceForm").serializeArray();
		jQuery.ajax({
			url: "<@ofbizUrl>updatePermanentResidence</@ofbizUrl>",
			data: formData,
			type: "POST",
			success: function(data){
				if(data._EVENT_MESSAGE_){
					jQuery("#permanentResidenceContactmechId").val(data.contactMechId);
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
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	
	jQuery("#otherEmailBtn").click(function(event){
		event.preventDefault();
		var formData = jQuery("#otherEmailForm").serializeArray();
		jQuery.ajax({
			url: "<@ofbizUrl>updateOtherEmail</@ofbizUrl>",
			data: formData,
			type: "POST",
			success: function(data){
				if(data._EVENT_MESSAGE_){
					jQuery("#otherEmailContactmechId").val(data.contactMechId);
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
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	jQuery("#PrimaryEmailBtn").click(function(event){
		event.preventDefault();
		var formData = jQuery("#primaryEmailForm").serializeArray();
		jQuery.ajax({
			url: "<@ofbizUrl>updatePrimaryEmail</@ofbizUrl>",
			data: formData,
			type: "POST",
			success: function(data){
				if(data._EVENT_MESSAGE_){
					jQuery("#primaryEmailContactmechId").val(data.contactMechId);
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
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	
	jQuery("#homePhonelBtn").click(function(event){
		event.preventDefault();
		var formData = jQuery("#homePhoneForm").serializeArray();
		if(!jQuery("#homePhoneForm").valid()){
			return false;
		}
		jQuery.ajax({
			url: "<@ofbizUrl>updateHomeMobile</@ofbizUrl>",
			data: formData,
			type: "POST",
			success: function(data){
				if(data._EVENT_MESSAGE_){
					jQuery("#homePhoneContactMechId").val(data.contactMechId);
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
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
		
	});	
	jQuery("#PhoneMobileBtn").click(function(event){
		event.preventDefault();
		var formData = jQuery("#phoneMobileForm").serializeArray();
		if(!$("#phoneMobileForm").valid()){
			return false;
		}
		jQuery.ajax({
			url: "<@ofbizUrl>updatePhoneMobile</@ofbizUrl>",
			data: formData,
			type: "POST",
			success: function(data){
				if(data._EVENT_MESSAGE_){
					jQuery("#mobilePhoneContactMechId").val(data.contactMechId);
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
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
		
	});
	jQuery("#PersonEducationBtn").click(function(event){
		event.preventDefault();
		var eduData = jQuery("#PersonEducationForm").serializeArray();
		jQuery.ajax({
			url: "<@ofbizUrl>AddPersonEducation</@ofbizUrl>",
			data: eduData,
			type: "POST",
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
					var tr = jQuery("<tr id=\'partyEducation_"+data.partyId+"_${iEducation+1}\'"+"/>");
					tr.append("<td>"+ jQuery("#school option:selected").text() +"</td>");
					tr.append("<td>"+ jQuery("#educationFromDate").val() +" </td>");
					tr.append("<td>"+ jQuery("#educationThruDate").val() +"</td>");
					tr.append("<td>"+ jQuery("#major option:selected").text() +"</td>");
					tr.append("<td>"+ jQuery("#studyModeType option:selected").text() +"</td>");
					tr.append("<td>"+ jQuery("#degreeClassificationType option:selected").text() +" </td>");
					tr.append("<td><button type='button' class='btn btn-small btn-danger btn-mini icon-trash' onclick=\"deletePersonEducation(\'"+data.schoolId+"\',\'"+data.partyId +"\',\'"+data.majorId+"\',\'"+data.studyModeTypeId+"\',\'"+data.educationSystemTypeId+"\',\'"+data.educationFromDate+"\',\'partyEducation_"+data.partyId+"_${iEducation+1}"+"\')\">"+"${StringUtil.wrapString(uiLabelMap.CommonDelete)}"+"</button></td>");
					/* tr.append("<td>"+ jQuery("#educationSystemType option:selected").text() + " </td>"); */
					jQuery("#PersonEducationTable > tbody:last").append(tr);
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	
	jQuery("#WorkingProcessBtn").click(function(event){
		event.preventDefault();
		var workingProcessData = jQuery("#WorkingProcessForm").serializeArray();
		jQuery.ajax({
			url: "<@ofbizUrl>AddPersonWorkingProcess</@ofbizUrl>",
			data: workingProcessData,
			type: "POST",
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
					var id=data.personWorkingProcessId;
					var tr = jQuery("<tr id='personWorkingProcess_"+id+"'/>");
					tr.append("<td>"+ jQuery("#workProcess_fromDate").val()+ " - " + jQuery("#workProcess_thruDate").val() +"</td>");
					tr.append("<td>"+ jQuery("#workProcess_companyName").val() +"</td>");
					tr.append("<td>"+ jQuery("#workProcess_EmplPositionType").val() +"</td>");
					tr.append("<td>"+ jQuery("#workProcess_JobDescription").val() +"</td>");
					tr.append("<td>"+ jQuery("#workProcess_Payroll").val() +"</td>");
					tr.append("<td>"+ jQuery("#workProcess_TerminationReason").val() +"</td>");
					tr.append("<td>"+ jQuery("#workProcess_rewardDiscrip").val() +"</td>");
					tr.append("<td><button type='button' class='btn btn-small btn-mini btn-primary icon-edit'onclick=\"editWorkingProcess("+id+",'personWorkingProcess_"+ id+"')\">"+"${StringUtil.wrapString(uiLabelMap.CommonUpdate)}"+"</button></td>");
					tr.append("<td><button type='button' class='btn btn-small btn-danger btn-mini icon-trash' onclick=\"deleteWorkingProcess("+id+",'personWorkingProcess_"+ id+"')\">"+"${StringUtil.wrapString(uiLabelMap.CommonDelete)}"+"</button></td>");
					jQuery("#workingProcessTable > tbody:last").append(tr);
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	jQuery("#FamilyBackgroundBtn").click(function(){
		var familyBackground = jQuery("#FamilyBackgroundForm").serializeArray();		
		jQuery.ajax({
			url: "<@ofbizUrl>addFamilyBackground</@ofbizUrl>",
			data: familyBackground,
			type: "POST",
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
					var id="";
					if(data.personFamilyId){
						id=data.personFamilyId;
					}
					var tr = jQuery("<tr id='personFamilyBackground_"+id+"'/>");
					var emer="Y";
					if(!jQuery("#emergencyContact").prop('checked')){
						emer="N";
					}
					tr.append("<td>"+ jQuery("#lastNameFamily").val()+ " " + jQuery("#middleNameFamily").val() + " " + jQuery("#firstNameFamily").val() + "</td>");
					tr.append("<td>"+ jQuery("#relationship option:selected").text() +"</td>");
					tr.append("<td>"+ jQuery("#familyBirthDate").val()+ "</td>");
					tr.append("<td>"+ jQuery("#occupation").val() +"</td>");
					/* tr.append("<td>"+ jQuery("#placeWork").val() + "</td>"); */
					tr.append("<td>"+ jQuery("#phoneNumber").val() + "</td>");
					tr.append("<td>"+emer+"</td>");
					tr.append("<td><button type='button' class='btn btn-small btn-mini btn-primary icon-edit'onclick=\"editPersonFamily("+id+",'personFamilyBackground_"+ id+"')\">"+"${StringUtil.wrapString(uiLabelMap.CommonUpdate)}"+"</button></td>");
					tr.append("<td><button type='button' class='btn btn-small btn-danger btn-mini icon-trash' onclick=\"deletePersonBackground("+id+",'personFamilyBackground_"+ id+"')\">"+"${StringUtil.wrapString(uiLabelMap.CommonDelete)}"+"</button></td>");
					jQuery("#familyBackgroundTable > tbody:last").append(tr);
				}else{
					bootbox.dialog({
						message: "${uiLabelMap.ErrorWhenUpdate}",
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
	});
	jQuery("#basicInformationBtn").click(function(){
		var basicInfo = jQuery("#basicInformation").serializeArray();
		if(!$("#basicInformation").valid()){
			return false;	
		}
		jQuery.ajax({
			url: "<@ofbizUrl>updateEmplProfile</@ofbizUrl>",
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
	});
	$("#copyContactInfo").click(function(){
		address1 = jQuery("#address1_PermanentResidence").val();
		countryGeoId = jQuery("#permanentResidence_countryGeoId").val();
		stateGeoId = jQuery("#permanentResidence_stateProvinceGeoId").val();
		districtGeoId = jQuery("#permanentResidence_districtGeoId").val();
		wardGeoId = jQuery("#permanentResidence_wardGeoId").val();
		
		jQuery("#address1_CurrResidence").val(address1);
		jQuery("#currResidence_countryGeoId").val(countryGeoId);
		jQuery("#currResidence_countryGeoId").trigger("change");
		
		jQuery("#currResidence_stateProvinceGeoId").val(stateGeoId);
		jQuery("#currResidence_stateProvinceGeoId").trigger("change");
		
		jQuery("#currResidence_districtGeoId").val(districtGeoId);
		jQuery("#currResidence_districtGeoId").trigger("change");
		
		jQuery("#currResidence_wardGeoId").val(wardGeoId);
	});
	
	$("#EditFamilyBackgroundBtn").click(function(){
		var familyBackground = $("#EditInfoPersonFamilys").serializeArray();
		var xda= EditFamilyBackground(familyBackground);
		if(xda.idTr){
			var name="";
			if(xda.lasstName){
				name=xda.lastName;
			}
			if(xda.middleName){
				name=name+" "+xda.middleName;
			}
			if(xda.firstName){
				name=name+" "+xda.firstName;
			}
			$("#"+xda.idTr).find('td:eq(0)').text(name);
			
			if(xda.partyRelationshipTypeId){
				$("#"+xda.idTr).find('td:eq(1)').text(xda.partyRelationshipTypeId);
			}
			
			if(xda.birthDate){
				var date= new Date(xda.birthDate);
				var month= date.getMonth()+1;
				$("#"+xda.idTr).find('td:eq(2)').text(date.getDate()+"-"+month+"-"+date.getFullYear());
			}
			
			if(xda.occupation){
				$("#"+xda.idTr).find('td:eq(3)').text(xda.occupation);
			}
			
			if(xda.phoneNumber){
				$("#"+xda.idTr).find('td:eq(4)').text(xda.phoneNumber);
			}
			
			if(xda.emergencyContact){
				$("#"+xda.idTr).find('td:eq(5)').text(xda.emergencyContact);
			}
		}
		
	});
	
	function EditFamilyBackground(data){
		var xda= new Array();
		if(data){
			$.ajax({
			url: '<@ofbizUrl>EditFamilyBackground</@ofbizUrl>',
			type: 'POST',
			dataType:'json',
			async:false,
			data: data,
			success:function(data){
				xda=data.result[0];
			}
		});
		}
		return xda;
	}
	
	
	$("#EditWorkingProcessBtn").click(function(){
		var listData= $("#EditWorkingProcessForm").serializeArray();
		$.ajax({
			url: '<@ofbizUrl>editPersonWorkingProcess</@ofbizUrl>',
			type: 'POST',
			dataType:'json',
			data: listData,
			success:function(data){
				var error= getServerError(data);
				if(!error){
					var day=""
					if(data.xFromDate){
						var from= new Date(data.xFromDate);
						var month= from.getMonth()+1;
						day=day+addZero(from.getDate())+"-"+addZero(month)+"-"+addZero(from.getFullYear())+"-";
					}else{
						day=day+"?-";
					}
					
					if(data.xThruDate){
						var thru= new Date(data.xThruDate);
						var month= thru.getMonth()+1;
						day=day+addZero(thru.getDate())+"-"+addZero(month)+"-"+addZero(thru.getFullYear());
					}
					$("#"+data.processIdTr).find('td:eq(0)').text(day);
					if(data.xcompanyName){
						$("#"+data.processIdTr).find('td:eq(1)').text(data.xcompanyName);
					}else{
						$("#"+data.processIdTr).find('td:eq(1)').text("");
					}				
					
					if(data.xemplPositionTypeIdWorkProcess){
						$("#"+data.processIdTr).find('td:eq(2)').text(data.xemplPositionTypeIdWorkProcess);
					}else{
						$("#"+data.processIdTr).find('td:eq(2)').text("");
					}
					
					if(data.xjobDescription){
						$("#"+data.processIdTr).find('td:eq(3)').text(data.xjobDescription);
					}else{
						$("#"+data.processIdTr).find('td:eq(3)').text("");
					}
					
					if(data.xpayroll){
						$("#"+data.processIdTr).find('td:eq(4)').text(data.xpayroll);
					}	else{
						$("#"+data.processIdTr).find('td:eq(4)').text("");
					}
					
					if(data.xterminationReasonId){
						$("#"+data.processIdTr).find('td:eq(5)').text(data.xterminationReasonId);
					}else{
						$("#"+data.processIdTr).find('td:eq(5)').text("");
					}
					
					if(data.xrewardDiscrip){
						$("#"+data.processIdTr).find('td:eq(6)').text(data.xrewardDiscrip);
					}else{
						$("#"+data.processIdTr).find('td:eq(6)').text("");
					}
				}
			}
		});		
	});
	
});

function deletePersonBackground(personFamilyBackgroundId,id){
	if(personFamilyBackgroundId){
		$.ajax({
			url: '<@ofbizUrl>deletePersonFamilyBackground</@ofbizUrl>',
			type: 'POST',
			dataType:'json',
			data: {personFamilyBackgroundId: personFamilyBackgroundId},
			success:function(data){
				var erro=getServerError(data);
				if(erro!=""){
				}else{
					$('#'+id).remove();
				}
			}
		}
		);
		
	}
}

function getServerError(data) {
    var serverErrorHash = [];
    var serverError = "";
   
    
    if (data._ERROR_MESSAGE_LIST_ != undefined) {
     
        serverErrorHash = data._ERROR_MESSAGE_LIST_;
        jQuery.each(serverErrorHash, function(i, error) {
          if (error != undefined) {
              if (error.message != undefined) {
                  serverError += error.message;
              } else {
                  serverError += error;
              }
            }
        });
    }
    if (data._ERROR_MESSAGE_ != undefined) {
     
        serverError = data._ERROR_MESSAGE_;
    }
    
    return serverError;
}
function getPerson(personFamilyBackgroundId){
	var xda=new Array();
	if(personFamilyBackgroundId){
		$.ajax({
			url: '<@ofbizUrl>getInfoPersonFamily</@ofbizUrl>',
			type: 'POST',
			dataType:'json',
			async:false,
			data: {personFamilyBackgroundId: personFamilyBackgroundId},
			success:function(data){
				xda=data.listperson[0];
			}
		});
	}
	return xda;
}
function editPersonFamily(personFamilyBackgroundId,id){
	if(personFamilyBackgroundId){
		var data=getPerson(personFamilyBackgroundId);
		$("#personFamilyBackgroundId").val(personFamilyBackgroundId);
		$("#idTr").val(id);
		if(data.contactMechId){
			$("#contactMechId").val(data.contactMechId);
		}else{
			$("#contactMechId").val("");
		}
		if(data.contactNumber){
			$("#xphoneNumber").val(data.contactNumber)
		}else{
			$("#xphoneNumber").val("");
		}
		
		if(data.emergencyContact=="Y"){
			$("#xemergencyContact").prop('checked',true);
		}else{
			$("#xemergencyContact").prop('checked',false);
		}
		
		if(data.firstNameFamily){
			$("#xfirstNameFamily").val(data.firstNameFamily);
		}else{
			$("#xfirstNameFamily").val("");
		}
		
		if(data.lastNameFamily){
			$("#xlastNameFamily").val(data.lastNameFamily);
		}else{
			$("#xlastNameFamily").val("");
		}
		
		if(data.middleNameFamily){
			$("#xmiddleNameFamily").val(data.middleNameFamily);
		}else{
			$("#xmiddleNameFamily").val("");
		}
		
		
		if(data.occupation){
			$("#xoccupation").val(data.occupation);
		}else{
			$("#xoccupation").val("");
		}
		
		if(data.partyRelationshipTypeId){
			$('#xrelationship option[value='+data.partyRelationshipTypeId+']').attr('selected','selected');
		}
		if(data.placeWork){
			$("#xplaceWork").val(data.placeWork);
		}else{
			$("#xplaceWork").val("");
		}
		
		if(data.birthDate){
			 var date = new Date(data.birthDate);
			 var month= date.getMonth()+1;
			$('#xfamilyBirthDate_i18n').val(date.getDate()+"/"+month+"/"+date.getFullYear());
			$("#xfamilyBirthDate").val(date.getFullYear()+"-"+month+"-"+date.getDate());
		}else{
			$('#xfamilyBirthDate_i18n').val("");
			$("#xfamilyBirthDate").val("");
		}
		var edit=$('#EditInfoPersonFamily').modal('show');
	}
}
function deletePersonEducation(schoolId, partyId,majorId,studyModeTypeId,educationSystemTypeId,fromDate,id){
	$.ajax({
		url: '<@ofbizUrl>deletePersonEducation</@ofbizUrl>',
		type: 'POST',
		dataType:'json',
		async:false,
		data: {
				schoolId: schoolId,
				partyId:partyId,
				majorId:majorId,
				studyModeTypeId:studyModeTypeId,
				educationSystemTypeId:educationSystemTypeId,
				fromDate:fromDate,
				idTr:id
				},
		success:function(data){
			var erro=getServerError(data);
			if(!erro){
				$('#'+data.idTr).remove();
				<#assign iEducation=iEducation-1>
			}
		}
	});
}

function deleteWorkingProcess(personWorkingProcessId,idTrTable){
	$.ajax({
		url: '<@ofbizUrl>deleteWorkingProcess</@ofbizUrl>',
		type: 'POST',
		dataType:'json',
		data: {
			personWorkingProcessId: personWorkingProcessId
		},
		success:function(data){
			var erro=getServerError(data);
			if(!erro){
				$("#"+idTrTable).remove();
			}
		}
	});
}

function editWorkingProcess(personWorkingProcessId,idTrTable,fromDate,thruDate,companyName,emplPositionTypeId,jobDescription,payroll,terminationReasonId,rewardDiscrip){
	
	if(personWorkingProcessId){
		$("#personWorkingProcessId").val(personWorkingProcessId);
		
		if(idTrTable){
			$("#processIdTr").val(idTrTable);
		}else{
			$("#processIdTr").val("");
		}
		
		if(fromDate){
			var date = new Date(fromDate);
			var month= date.getMonth()+1;
			$("#xworkProcess_fromDate_i18n").val(addZero(date.getDate())+"/"+addZero(month)+"/"+date.getFullYear()+" "+addZero(date.getHours())+":"+addZero(date.getMinutes())+":"+addZero(date.getSeconds()));
			$("#xworkProcess_fromDate").val(date.getFullYear()+"-"+addZero(month)+"-"+addZero(date.getDate())+" "+addZero(date.getHours())+":"+addZero(date.getMinutes())+":"+addZero(date.getSeconds()));
		}else{
			$("#xworkProcess_fromDate_i18n").val("");
			$("#xworkProcess_fromDate").val("");
		}
		
		if(thruDate){
			var date = new Date(thruDate);
			var month= date.getMonth()+1;
			$("#xworkProcess_thruDate_i18n").val(addZero(date.getDate())+"/"+addZero(month)+"/"+date.getFullYear()+" "+addZero(date.getHours())+":"+addZero(date.getMinutes())+":"+addZero(date.getSeconds()));
			$("#xworkProcess_thruDate").val(date.getFullYear()+"-"+addZero(month)+"-"+addZero(date.getDate())+" "+addZero(date.getHours())+":"+addZero(date.getMinutes())+":"+addZero(date.getSeconds()));
		}else{
			$("#xworkProcess_thruDate_i18n").val("");
			$("#xworkProcess_thruDate").val("");
		}
		
		if(companyName){
			$("#xworkProcess_companyName").val(companyName);
		}else{
			$("#xworkProcess_companyName").val("");
		}
		
		if(emplPositionTypeId){
			$("#xworkProcess_EmplPositionType").val(emplPositionTypeId);
		}else{
			$("#xworkProcess_EmplPositionType").val("");
		}
		
		if(jobDescription){
			$("#xworkProcess_JobDescription").val(jobDescription);
		}else{
			$("#xworkProcess_JobDescription").val("");
		}
		if(payroll){
			$("#xworkProcess_Payroll").val(payroll);
		}else{
			$("#xworkProcess_Payroll").val("");
		}
		
		if(terminationReasonId){
			$("#xworkProcess_TerminationReason").val(terminationReasonId);
		}else{
			$("#xworkProcess_TerminationReason").val("");
		}
		
		if(rewardDiscrip){
			$("#xworkProcess_rewardDiscrip").val(rewardDiscrip);
		}else{
			$("#xworkProcess_rewardDiscrip").val("");
		}
		
		
		$("#EditWorkingProcess").modal('show');
	}else{
		$("#AddWorkingProcess").modal('show');
	}
	
	
}

function addZero(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}
</script>