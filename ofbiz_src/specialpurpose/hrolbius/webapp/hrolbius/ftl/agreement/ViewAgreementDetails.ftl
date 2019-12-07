<#--<!-- <#macro getAllAgreementTermType agreementId parentTypeList level>
	<#--<#assign allChildrenTermType = delegator.findByAnd("TermType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", parentTypeId), Static["org.ofbiz.base.util.UtilMisc"].toList("termTypeId"), false)>	
	<#if parentTypeList?has_content>
		<#list parentTypeList as tempTermType>
			<#assign tempChildTermType = delegator.findByAnd("TermType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", tempTermType.termTypeId), Static["org.ofbiz.base.util.UtilMisc"].toList("termTypeId"), false)>
			<div class="row-fluid form-horizontal">				
				<#assign tempAgreementTerm = delegator.findByAnd("AgreementTerm", Static["org.ofbiz.base.util.UtilMisc"].toMap("termTypeId", tempTermType.termTypeId, "agreementId", agreementId), null, false)>
				<#assign textDes = "">
				<#if tempAgreementTerm?has_content >	
					<#if tempAgreementTerm[0].emplPositionId?exists>
						<#assign positionType = delegator.findOne("EmplPositionAndPositionType", Static["org.ofbiz.base.util.UtilMisc"].toMap("emplPositionId", tempAgreementTerm[0].emplPositionId), false)>
						<#assign textDes = positionType.description?if_exists>						
					<#elseif tempAgreementTerm[0].textValue?exists>
						<#assign textDes = (StringUtil.wrapString(tempAgreementTerm[0].textValue))>	
					</#if>							
				<#elseif !tempChildTermType?has_content>
					<#--if  agreement term don't have textValue for tempTermType and tempTermType don't have child TermType, => textValue don't set for agreementTerm
					<#assign textDes = uiLabelMap.TextValueOfTermTypeNotSet>	
				</#if>
				<#if tempChildTermType?has_content || (textDes?has_content && textDes?length > 100)>
					<h${level +3}>${tempTermType.description?if_exists}</h${level +3}>
					<#if textDes?has_content>
						${textDes}
					</#if>
				<#else>
					<div class="control-group no-left-margin">
						<label style="text-align: left;"><h${level +3}>${tempTermType.description?if_exists}:</h${level +3}></label>
						<div class="controls">
							<#if textDes?exists>
								${textDes}
							</#if>
						</div>
					</div>
				</#if>
				<#if tempChildTermType?has_content>
					<@getAllAgreementTermType agreementId="${agreementId}" parentTypeList=tempChildTermType level=(level+1)/>
				</#if>
			</div>
		</#list>	
	</#if> 
</#macro> -->

<div class="row-fluid">
	<#if agreement?exists>
		<div class="widget-box transparent">
			<div class="row-fluid">
			    <div class="span12 widget-container-span">
				    <div class="widget-box transparent">
				        <div class="widget-body">
				        	<div class="widget-main span12 align-center no-left-margin">
				        		<#assign agreementType = delegator.findOne("AgreementType", Static["org.ofbiz.base.util.UtilMisc"].toMap("agreementTypeId", agreement.agreementTypeId), false)>
				        		<h3>${agreementType.description?if_exists}</h3>
				        	</div>
				        	<div class="row-fluid">
				        		<div class="span12">
				        			
		        					<#if representPartyFrom?has_content>
				        				<#assign representParty = representPartyFrom[0]>
				        				<#assign party = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", representParty.partyId), false)>
				        				<#assign representPartyFromPosList = Static["com.olbius.util.PartyUtil"].getCurrPositionTypeOfEmpl(delegator, representParty.partyId)>
				        				<#if representPartyFromPosList?has_content>
				        					<#assign emplPosType = delegator.findOne("EmplPositionType", Static["org.ofbiz.base.util.UtilMisc"].toMap("emplPositionTypeId", representPartyFromPosList[0].emplPositionTypeId), false)>				        					
				        				</#if>   		
				        			</#if>
		        					<div class="span5 form-horizontal">
		        						<div class="control-group no-left-margin">
			        						<label class="control-label" style="width: 200px !important">	
			        							${uiLabelMap.EmployerRepresent}:
			        						</label>
			        						<div class="controls">
				        						<#if party?exists>
				        							${party.firstName?if_exists} ${party.middleName?if_exists} ${party.firstName?if_exists}  
				        						</#if>
				        						&nbsp;
				        					</div>	
		        						</div>
		        					</div>
				        			<div class="span6 form-horizontal">
				        				<div class="control-group no-left-margin">
				        					<label class="control-label">
				        						${uiLabelMap.HrolbiusNationality}:
				        					</label>
					        				<div class="controls">
					        					<#if party?exists>
					        						<#if party.nationality?exists>
					        							<#assign nationality = delegator.findOne("Nationality", Static["org.ofbiz.base.util.UtilMisc"].toMap("nationalityId", party.nationality), false)>
						        						<#if nationality?exists>
						        							${nationality.description?if_exists}
						        						</#if>
					        						</#if>
					        					</#if>
					        					&nbsp;
					        				</div>
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<div class="row-fluid">	
				        		<div class="span12">
				        			<div class="span6 form-horizontal">
					        			<div class="control-group no-left-margin">
					        				<label class="control-label">
					        					${uiLabelMap.HRCommonPosition}:
					        				</label>
					        				<div class="controls">
						        				<#if emplPosType?exists>
						        					${emplPosType.description?if_exists}&nbsp;
						        				</#if>
						        				&nbsp;
					        				</div>
					        			</div>
				        			</div>
				        		</div>
				        	</div>	
				        	<div class="row-fluid">
				        		<div class="span12">
				        			<div class="span6 form-horizontal">
				        				<div class="control-group no-left-margin">
					        				<label class="control-label">${uiLabelMap.OnBehalfOf}:</label>
					        				<div class="controls">
						        				<#if partyIdFrom?exists>
						        					<#assign partyFromAddress = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyIdFrom.partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION", "userLogin", userLogin))>
						        					<#assign partyFromFaxNbr = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyIdFrom.partyId, "contactMechPurposeTypeId", "FAX_NUMBER", "userLogin", userLogin))>
						        					${partyIdFrom.groupName?if_exists}
						        				</#if>
						        				&nbsp;
					        				</div>
				        				</div>
				        			</div>
				        		</div>
				        	</div>	
				        	<div class="row-fluid">
				        		<div class="span12">
				        			<div class="span6 form-horizontal">
				        				<label class="control-label">${uiLabelMap.CommonAddress}:</label>
				        				<div class="controls">
					        				<#if partyFromAddress?exists>
					        					<#if partyFromAddress.countryGeoId?exists>
					        						<#assign countryGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", partyFromAddress.countryGeoId), false)>
					        						<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", partyFromAddress.stateProvinceGeoId), false)>
					        						${partyFromAddress.address1?if_exists} <#if stateProvinceGeo?exists>, ${stateProvinceGeo.geoName?if_exists} </#if> <#if countryGeo?exists>, ${countryGeo.geoName?if_exists} </#if>
					        					</#if>
					        				</#if>
					        				&nbsp;
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<div class="row-fluid">	
				        		<div class="span12">
				        			<div class="span6 form-horizontal">
				        				<label class="control-label">${uiLabelMap.CommonTelephoneAbbr}:</label>
				        				<div class="controls">
					        				<#if partyFromFaxNbr?exists>
					        					(${partyFromFaxNbr.countryCode?if_exists}-${partyFromFaxNbr.areaCode?if_exists}) ${partyFromFaxNbr.contactNumber?if_exists}
					        				</#if>
					        				&nbsp;
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<br/>
				        	<div class="row-fluid mgt10">
				        		<div class="span12">
				        			<div class="span5 form-horizontal">
				        				<div class="control-group no-left-margin">
				        					<label class="control-label">${uiLabelMap.FromEmployee}:</label>
					        				<div class="controls">
					        					<#if partyIdTo?exists>
					        						<#if partyIdTo.nationalityId?exists>
					        							<#assign nationalityPartyTo = delegator.findOne("Nationality", Static["org.ofbiz.base.util.UtilMisc"].toMap("nationalityId", partyIdTo.nationalityId), false)>
					        						</#if>
						        					${partyIdTo.firstName?if_exists} ${partyIdTo.middleName?if_exists} ${partyIdTo.lastName?if_exists}
						        				</#if>
						        				&nbsp;
					        				</div>
				        				</div>
				        			</div>
				        			<div class="span6 form-horizontal">
				        				<div class="control-group no-left-margin">
				        					<label class="control-label">${uiLabelMap.HrolbiusNationality}:</label>
					        				<div class="controls">
				        						<#if nationalityPartyTo?exists>
				        							${nationalityPartyTo.description?if_exists}
				        						</#if>
					        				</div>
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<div class="row-fluid">	
				        		<div class="span12">
				        			<div class="span6 form-horizontal">
				        				<div class="control-group no-left-margin">
					        				<label class="control-label">${uiLabelMap.PartyBirthDate}:</label>
					        				<div class="controls">
						        				<#if partyIdTo.birthDate?exists>
						        					${partyIdTo.birthDate?string["dd/MM/yyyy"]}
						        				</#if>
						        				&nbsp;
					        				</div>
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<div class="row-fluid">	
				        		<div class="span12">
				        			<div class="span6 form-horizontal">
				        				<label class="control-label">${uiLabelMap.PermanentResidence}:</label>
				        				<div class="controls">
					        				<#if partyIdTo?exists>
					        					<#assign partyIdToPostalAddr = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyIdTo.partyId, "contactMechPurposeTypeId", "PERMANENT_RESIDENCE","userLogin", userLogin))>
					        					<#if partyIdToPostalAddr?exists>
						        					<#if partyIdToPostalAddr.countryGeoId?exists>
						        						<#assign partyToCountryGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", partyIdToPostalAddr.countryGeoId), false)>
						        						<#assign partyToStateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", partyIdToPostalAddr.stateProvinceGeoId), false)>
						        						${partyIdToPostalAddr.address1?if_exists} <#if partyToStateProvinceGeo?exists>, ${partyToStateProvinceGeo.geoName?if_exists} </#if> <#if partyToCountryGeo?exists>, ${partyToCountryGeo.geoName?if_exists} </#if>
						        					</#if>
					        					</#if>
					        				</#if>
					        				&nbsp;
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<div class="row-fluid">
				        		<div class="span12">
				        			<div class="span4 form-horizontal">
				        				<div class="control-group no-left-margin">
				        					<label class="control-label">${uiLabelMap.certProvisionId}:</label>
					        				<div class="controls">
												<#if partyIdTo?exists>
						        					${partyIdTo.idNumber?if_exists}
						        				</#if>			
						        				&nbsp;	        				
					        				</div>
				        				</div>
				        			</div>
				        			<div class="span4 form-horizontal">
				        				<div class="control-group no-left-margin">
				        					<label class="control-label">${uiLabelMap.HrolbiusidIssueDate}:</label>
					        				<div class="controls">
					        					<#if partyIdTo?exists && partyIdTo.idIssueDate?exists>
					        						${partyIdTo.idIssueDate?string["dd/MM/yyyy"]}
					        					</#if>
					        					&nbsp;
					        				</div>
				        				</div>	
				        			</div>
				        			<div class="span4 form-horizontal">	
				        				<div class="control-group no-left-margin">
				        					<label class="control-label">${uiLabelMap.HrolbiusidIssuePlace}:</label>
					        				<div class="controls">
					        					<#if partyIdTo?exists && partyIdTo.idIssuePlace?exists>
					        						${partyIdTo.idIssuePlace}
					        					</#if>
					        					&nbsp;
					        				</div>
				        				</div>
				        			</div>
				        		</div>
				        	</div>
				        	<div class="row-fluid">
				        		<div class="span12">
				        			<h3>${uiLabelMap.LabourContractAndCommitSatisfy}</h3>
				        		</div>
				        	</div>
				        	<div class="row-fluid form-horizontal">
				        		<h4>${uiLabelMap.ContactDuration}</h4>
				        	</div>	
				        	<div class="row-fluid form-horizontal" style="padding-left: 15px">
				        		<div class="control-group">
					        		<label class="control-label" style="text-align: left;">
					        			${uiLabelMap.CatLabourContract}:
					        		</label>
					        		<div class="controls">
					        			<#if agreement.thruDate?exists>${uiLabelMap.DefiniteTerm}<#else>${uiLabelMap.UndefiniteTerm}</#if>
					        		</div>
					        	</div>
					        	<div class="control-group">
					        		<label class="control-label" style="text-align: left;">	
								        ${uiLabelMap.CommonFromDate}: 
							        </label>
							        <div class="controls">
							        	<#if agreement.fromDate?exists>${agreement.fromDate?string["dd/MM/yyyy"]}</#if>&nbsp;<#if agreement.thruDate?exists>${uiLabelMap.CommonThruDate} ${agreement.thruDate?string["dd/MM/yyyy"]}</#if>
							        </div>	
				        		</div>
				        	</div>
				        	<#--<!-- <@getAllAgreementTermType agreementId="${agreement.agreementId}" parentTypeList=termTypeList level=1/> -->
				        	<#list agreementTermList as agreementTerm>				        		
				        		<div class="row-fluid form-horizontal">
						        	<#assign textValue = agreementTerm.textValue?if_exists>
						        	<#assign textLength = textValue?if_exists?length>
						        	 
						        	<#if agreementTerm.isHasChild() || (textLength?exists && textLength > 250)>
						        		<h${agreementTerm.levelInTermTypeTree + 3} style="margin-left: ${agreementTerm.levelInTermTypeTree * 10}px">${agreementTerm.termTypeDesc?if_exists}</h${agreementTerm.levelInTermTypeTree + 3}>
						        		<#if agreementTerm.textValue?has_content>
						        			<div style="margin-left: ${agreementTerm.levelInTermTypeTree * 10}px">${StringUtil.wrapString(agreementTerm.textValue)}</div>
						        		</#if>							 	
						        	<#else>
						        		<label style="text-align: left;" class="control-label">
						        			<h${agreementTerm.levelInTermTypeTree + 3} style="margin-left: ${agreementTerm.levelInTermTypeTree * 11}px">${agreementTerm.termTypeDesc?if_exists}:</h${agreementTerm.levelInTermTypeTree +3}>
					        			</label>
						        		<div class="controls">
						        			<#if agreementTerm.textValue?has_content>
						        				<div style="margin-left: ${agreementTerm.levelInTermTypeTree * 11}px">${StringUtil.wrapString(agreementTerm.textValue)}</div>
						        			</#if>
						        			&nbsp;
						        		</div>							        			
						        	</#if>
				        		</div>
				        	</#list>
				        </div>
				    </div>
				</div>
			</div>	
		</div>        
	<#else>
		<h3>${uiLabelMap.NotFindAgreement}</h3>	
	</#if>
</div>