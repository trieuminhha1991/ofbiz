<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xml.apache.org/fop/extensions">
		<fo:layout-master-set>
		  <fo:simple-page-master margin-right="2.0cm" margin-left="2.0cm" 
		  	margin-bottom="1mm" margin-top="0.5cm" page-width="23cm" page-height="29.7cm" master-name="main">
		    <fo:region-body margin-bottom="1.5cm" margin-top="1.5cm" padding-left="1cm" />
		    <fo:region-before extent="1.5cm"/>
		  </fo:simple-page-master>
		</fo:layout-master-set>
		<#assign companyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, organization, false)>	
		<fo:page-sequence master-reference="main">			
			<#-- <fo:static-content flow-name="xsl-region-before">
	            <fo:block line-height="10pt" font-size="8pt" space-before="1.5pt" space-after="1.5pt" >
					<fo:block-container width="100%">
				    	<fo:block text-align="left" margin-top="5%" font-size="14px">
					    	${companyName}
					    </fo:block>
				    </fo:block-container>            
	            </fo:block>
	        </fo:static-content> -->
			<fo:flow flow-name="xsl-region-body" font-family="Arial">
				<fo:block-container width="100%">
			    	<fo:block text-align="center" font-weight="bold" font-size="18px" margin-bottom="0.6cm" margin-top="0.3cm">
				    	${uiLabelMap.PartyEmplProfile}
				    </fo:block>
			    </fo:block-container>
			    <fo:table table-layout="fixed" width="100%" border="0px" margin-top="5px" font-size="10px">
			    	<fo:table-column column-width="25%"/>
			    	<fo:table-column column-width="5cm"/>
			    	<fo:table-column/>
			    	<fo:table-body>
			    		<fo:table-row>
			    			<fo:table-cell number-rows-spanned="2">
			      				<fo:block margin-bottom="2.5mm" height="4cm">
			      					<#if personalImage?has_content>
			      						<fo:external-graphic src='url("/home/anhvu/Documents/download.jpg")'
												content-width="3cm" scaling="non-uniform" content-height="scale-to-fit"/>
			      					</#if> 
			      				</fo:block>
			      			</fo:table-cell>
			      			<fo:table-cell>
			      				<fo:block font-size="13px"  margin-bottom="2.5mm">
			      					 ${uiLabelMap.PartyName}:  
			      				</fo:block>
			      			</fo:table-cell>
			      			<fo:table-cell>
			      				<fo:block font-size="13px" margin-bottom="2.5mm">
			      					${lookupPerson.firstName?if_exists} ${lookupPerson.middleName?if_exists} ${lookupPerson.lastName?if_exists}
			      				</fo:block>
			      			</fo:table-cell>
			      		</fo:table-row>
			      		<fo:table-row>
			      			<fo:table-cell>
			      				<fo:block font-size="13px">
						      		${uiLabelMap.EmployeeId}: 		  
			      				</fo:block>
			      			</fo:table-cell>
			      			<fo:table-cell>
			      				<fo:block font-size="13px">
			      				${lookupPerson.partyId?if_exists}
			      				</fo:block>
			      			</fo:table-cell>
			      		</fo:table-row>		
			    	</fo:table-body>
			    </fo:table>
			    <fo:block-container>
			    	<fo:block fonr-size="13pt" font-weight="bold">
			    		I. ${uiLabelMap.GeneralInformation}	
			    	</fo:block>
			    </fo:block-container>
			    <fo:block-container>
			    	<fo:block margin-left="2mm">
						<fo:table border="0px"  margin-top="0.5cm" font-size="11px" cellspacing="5mm" cellpadding="5mm">
							<fo:table-column />
					    	<fo:table-column />
					    	<fo:table-column />
					    	<fo:table-column/>
					    	<fo:table-body>
					    		<fo:table-row>
					    			<fo:table-cell font-weight="bold">
					    				<fo:block margin-bottom="2.5mm">
					    					${uiLabelMap.Gender}:
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.gender?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.BirthDate}:
					    				</fo:block>	
					   				</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.birthDate?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.IDNumber}:
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell number-columns-spanned="3">
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.idNumber?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    			
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.IDIssueDate}:
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.idIssueDate?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.IDIssuePlace}:
					    				</fo:block>	
					   				</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.idIssuePlace?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.PassportNumber}:
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.passportNumber?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.PassportIssuePlace}:
					    				</fo:block>	
					   				</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.passportIssuePlace?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.PassportIssueDate}:
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.passportIssueDate?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.PassportExpiryDate}:</fo:block>	
					   				</fo:table-cell>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm">
					    					${lookupPerson.passportExpiryDate?if_exists}
					    				</fo:block>
					    			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					    				<fo:block margin-bottom="2.5mm" font-weight="bold">
					    					${uiLabelMap.MaritalStatus}:
					    				</fo:block>
					    			</fo:table-cell>
					    			<fo:table-cell number-columns-spanned="3">
					    				<fo:block margin-bottom="2.5mm">
					    					<#if lookupPerson.maritalStatus?has_content> 
						    					<#assign mairialStatus = delegator.findOne("MaritalStatus", Static["org.ofbiz.base.util.UtilMisc"].toMap("maritalStatusId", lookupPerson.maritalStatus), false)>
						    					${mairialStatus.get("description", locale)?if_exists}
					    					</#if>
					    				</fo:block>
					    			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
					      					${uiLabelMap.PersonalBackground}:     					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell number-columns-spanned="3">
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.personalBackground?has_content>
					      						<#assign personBackground =  delegator.findOne("PersonalBackground", Static["org.ofbiz.base.util.UtilMisc"].toMap("personalBackgroundId", lookupPerson.personalBackground), false)>
					      						${personBackground.description?if_exists}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					      			
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
					      					${uiLabelMap.FamilyBackground}:    					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell number-columns-spanned="3">
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.familyBackground?has_content>
					      						<#assign familyBackground = delegator.findOne("FamilyBackground", Static["org.ofbiz.base.util.UtilMisc"].toMap("familyBackgroundId",lookupPerson.familyBackground), false)>
					      						${familyBackground.description?if_exists}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
					      					${uiLabelMap.EthnicOrigin}:     					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.ethnicOrigin?has_content>
					      						<#assign ethnicOrigin = delegator.findOne("ethnicOrigin", Static["org.ofbiz.base.util.UtilMisc"].toMap("ethnicOriginId",lookupPerson.ethnicOrigin), false)>
					      						${ethnicOrigin.description?if_exists}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold">
					      					${uiLabelMap.Religion}:
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.religion?has_content>
					      						<#assign religion = delegator.findOne("Religion", Static["org.ofbiz.base.util.UtilMisc"].toMap("religionId",lookupPerson.religion), false)>
					      						${religion.description}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
											${uiLabelMap.Nationality}:      					     					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell number-columns-spanned="3">
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.nationality?has_content>
					      						<#assign nationality = delegator.findOne("Nationality", Static["org.ofbiz.base.util.UtilMisc"].toMap("nationalityId",lookupPerson.nationality), false)>
					      						${nationality.description}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					      			
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
											${uiLabelMap.TrainingLevel}:      					     					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.trainingLevel?has_content>
					      						<#assign trainingLevel = delegator.findOne("TrainingLevel", Static["org.ofbiz.base.util.UtilMisc"].toMap("trainingLevelId", lookupPerson.trainingLevel), false)>
					      						${trainingLevel.description}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold">
					      					${uiLabelMap.EducationLevel}:
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.educationLevel?has_content>
					      						${lookupPerson.educationLevel}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					      			
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold">
					      					${uiLabelMap.University}:
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.university?has_content>
					      						<#assign university = delegator.findOne("University", Static["org.ofbiz.base.util.UtilMisc"].toMap("universityId", lookupPerson.university), false)>
					      						${university.description}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
					      					${uiLabelMap.Major}:	     					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.major?has_content>
					      						${lookupPerson.major?if_exists}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					      			
					    		</fo:table-row>
					    		<fo:table-row>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold">
					      					${uiLabelMap.Grade}:
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					<#if lookupPerson.grade?has_content>
					      						<#assign grade = delegator.findOne("Grade", Static["org.ofbiz.base.util.UtilMisc"].toMap("gradeId", lookupPerson.grade), false)>
					      						${grade.description}
					      					</#if>
					      				</fo:block>
					      			</fo:table-cell>
					    			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm" font-weight="bold"> 
					      					${uiLabelMap.GraduationYear}:	     					
					      				</fo:block>
					      			</fo:table-cell>
					      			<fo:table-cell>
					      				<fo:block margin-bottom="2.5mm">
					      					${lookupPerson.graduationYear?if_exists}
					      				</fo:block>
					      			</fo:table-cell>
					      			
					    		</fo:table-row>    		
					    	</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:block-container>
				 <fo:block-container>
			    	<fo:block fonr-size="13pt" font-weight="bold" margin-top="5mm">
			    		II. ${uiLabelMap.ContactInformation}	
			    	</fo:block>
			    </fo:block-container>
			    <fo:block-container>
			    	<fo:block margin-left="2mm">
			    		<fo:table border="0px"  margin-top="0.5cm" font-size="11px" cellspacing="5mm" cellpadding="5mm">
			    			<fo:table-column column-width="5cm"/>
					    	<fo:table-column />
					    	<fo:table-body>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm" >
						    				${uiLabelMap.PhoneMobile}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if phoneMobile?has_content>
						    					${phoneMobile.areaCode?if_exists} ${phoneMobile.contactNumber?if_exists} ${phoneMobile.extension?if_exists}
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm" >
						    				${uiLabelMap.PhoneWork}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if phoneWork?has_content> 
						    					${phoneWork.areaCode?if_exists} ${phoneWork.contactNumber?if_exists} ${phoneWork.extension?if_exists}
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm" >
						    				${uiLabelMap.PhoneHome}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if phoneHome?has_content>
						    					${phoneHome.areaCode?if_exists} ${phoneHome.contactNumber?if_exists} ${phoneHome.extension?if_exists}
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm" >
						    				${uiLabelMap.EmailAddressPrimary}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if primaryEmailAddress?has_content>
						    					${primaryEmailAddress.emailAddress?if_exists}
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm" >
						    				${uiLabelMap.PersonalEmailAddress}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if personalEmailAddress?has_content>	
						    					${personalEmailAddress.emailAddress?if_exists}
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm" >
						    				${uiLabelMap.OtherEmailAddress}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if otherEmailAddress?has_content>
						    					${otherEmailAddress.emailAddress?if_exists}
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				${uiLabelMap.CurrentResidence}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if currResidentAddr?has_content>
							    				<#if currResidentAddr.stateProvinceGeoId?has_content>
							    					<#assign state = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", currResidentAddr.stateProvinceGeoId), false)>
							    					<#assign stateName = state.geoName>
							    				</#if>
							    				<#if currResidentAddr.countryGeoId?has_content>
							    					<#assign country = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", currResidentAddr.countryGeoId), false)>
							    					<#assign countryName = country.geoName>
							    				</#if>
							    				${currResidentAddr.address1}, ${stateName?if_exists}, ${countryName?if_exists} 
							    			</#if>
						    				
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    		<fo:table-row>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				${uiLabelMap.PermanentResidence}:
						    			</fo:block>
						    		</fo:table-cell>
						    		<fo:table-cell>
						    			<fo:block margin-bottom="2.5mm">
						    				<#if perResidentAddr?has_content>
						    				<#if perResidentAddr.stateProvinceGeoId?has_content>
						    					<#assign state = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", perResidentAddr.stateProvinceGeoId), false)>
						    					<#assign stateName = state.geoName>
						    				</#if>
						    				<#if currResidentAddr.countryGeoId?has_content>
						    					<#assign country = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", perResidentAddr.countryGeoId), false)>
						    					<#assign countryName = country.geoName>
						    				</#if>
						    					${perResidentAddr.address1}, ${stateName?if_exists}, ${countryName?if_exists} 
						    				</#if>
						    			</fo:block>
						    		</fo:table-cell>
					    		</fo:table-row>
					    	</fo:table-body>
			    		</fo:table>
			    	</fo:block>
			    </fo:block-container>	
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
</#escape>