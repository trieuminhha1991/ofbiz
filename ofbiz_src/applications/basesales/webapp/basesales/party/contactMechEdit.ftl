<#include "component://common/webcommon/includes/messages.ftl">
<#if contactMech.contactMechTypeId?has_content && postalAddress?exists>
<div class="row-fluid">
	<div class="span12">
		<div id="mech-purpose-types">
			<ul class="nav nav-tabs" id="recent-tab">
				<li class="active"><a data-toggle="tab" href="#purposeTypes-tab">${uiLabelMap.BSOverview}</a></li>
				<li><a data-toggle="tab" href="#editContactmech-tab">${uiLabelMap.BSContactPurposes}</a></li>
			</ul>
			<div class="tab-content padding-8 overflow-visible">
				<div id="purposeTypes-tab" class="tab-pane active">
					<form method="post" class="form-window-container" action="<@ofbizUrl>updatePostalAddressInfoAjax</@ofbizUrl>" name="editcontactmechformUpdate" id="editcontactmechformUpdate">
						<input type="hidden" id="we_contactMechTypeId" value="${contactMech.contactMechTypeId}" />
						<input type="hidden" id="we_partyId" value="${parameters.partyId?if_exists}" />
						<div class="row-fluid">
							<div class="span6 form-window-content-custom">
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_toName">${uiLabelMap.BSContactMechId}</label>
									</div>
									<div class='span7'>
										<input type="text" id="we_contactMechId" class="span12" maxlength="100" value="${contactMech.contactMechId}"/>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_toName">${uiLabelMap.BSPartyReceive}</label>
									</div>
									<div class='span7'>
										<input type="text" id="we_toName" class="span12" maxlength="100" value=""/>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_attnName">${uiLabelMap.BSOtherInfo}</label><#--DAOtherName-->
									</div>
									<div class='span7'>
										<input type="text" id="we_attnName" class="span12" maxlength="100" value=""/>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_postalCode">${uiLabelMap.BSZipCode}</label>
									</div>
									<div class='span7'>
										<input type="text" id="we_postalCode" class="span12" maxlength="60" value=""/>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label class="required" for="we_address1">${uiLabelMap.BSAddress}</label>
									</div>
									<div class='span7'>
										<input type="text" id="we_address1" class="span12" maxlength="255" value=""/>
							   		</div>
								</div>
							</div><!--.span6-->
							<div class="span6 form-window-content-custom">
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_countryGeoId" class="required">${uiLabelMap.BSCountry}</label>
									</div>
									<div class='span7'>
										<div id="we_countryGeoId"></div>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label class="required">${uiLabelMap.BSStateProvince}</label>
									</div>
									<div class='span7'>
										<div id="we_stateProvinceGeoId"></div>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_countyGeoId" class="required">${uiLabelMap.BSCounty}</label>
									</div>
									<div class='span7'>
										<div id="we_countyGeoId"></div>
							   		</div>
								</div>
								<div class='row-fluid'>
									<div class='span5'>
										<label for="we_wardGeoId">${uiLabelMap.BSWard}</label>
									</div>
									<div class='span7'>
										<div id="we_wardGeoId"></div>
							   		</div>
								</div>
								<div class='row-fluid' style="display:none">
									<div class='span5'>
										<label for="we_allowSolicitation">${uiLabelMap.BSContactAllowSolicitation}?</label>
									</div>
									<div class='span7'>
										<div id="we_allowSolicitation"></div>
							   		</div>
								</div>
							</div><!--.span6-->
						</div><!--.row-fluid-->
			  		</form>
				</div>
				<div id="editContactmech-tab" class="tab-pane">
					<div id="containerjqxPartyContactMechPurpose" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
					</div>
					<div id="jqxNotificationjqxPartyContactMechPurpose" style="margin-bottom:5px">
					    <div id="notificationContentjqxPartyContactMechPurpose"></div>
					</div>
					
					<div id="jqxPartyContactMechPurpose"></div>
					
					<#--<td align="right">${uiLabelMap.PartyContactPurposes}</td>
					<#if mechMap?exists && mechMap.purposeTypes?has_content>
		      			<div class="row-fluid">
		      				<div class="span12">
		      					<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
					              	<#if mechMap.partyContactMechPurposes?has_content>
					                	<#list mechMap.partyContactMechPurposes as partyContactMechPurpose>
					                  		<#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType", true)>
				                  			<tr>
					                    		<td>
					                      			<#if contactMechPurposeType?has_content>
					                        			${contactMechPurposeType.get("description",locale)}
					                      			<#else>
					                        			${uiLabelMap.PartyPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
					                      			</#if>
				                    			</td>
				                    			<td>
				                    				(${uiLabelMap.BSFromDate}: 
					                      			<#if partyContactMechPurpose.fromDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(partyContactMechPurpose.fromDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</#if>)
					                      			<#if partyContactMechPurpose.thruDate?has_content>(${uiLabelMap.CommonExpire}: ${partyContactMechPurpose.thruDate.toString()}</#if>
				                    			</td>
					                    		<td align="left">
					                      			<form name="deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}" id="deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}" method="post" action="<@ofbizUrl>deletePartyContactMechPurposeAjax</@ofbizUrl>" >
					                         			<input type="hidden" name="partyId" value="${partyId}" />
					                         			<input type="hidden" name="contactMechId" value="${contactMechId}" />
					                         			<input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}" />
					                         			<input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate.toString()}" />
					                         			<input type="hidden" name="DONE_PAGE" value="${donePage?replace("=","%3d")}" />
					                         			<input type="hidden" name="useValues" value="true" />
					                       			</form>
					                       			<a href="javascript:deletePartyContactMechPurpose('deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}');"><i class="open-sans icon-trash"></i>${uiLabelMap.CommonDelete}</a>
				                    			</td>
					                  		</tr>
					                	</#list>
					              	</#if>
	            				</table>
		      				</div>
		      			</div>
		      			<div class="row-fluid">
		      				<div class="span12" style="margin-top: 10px; text-align:right">
		      					<form method="post" action="<@ofbizUrl>createPartyContactMechPurposeAjax</@ofbizUrl>" name="newpurposeform" id="newpurposeform">
	          						<input type="hidden" name="partyId" value="${partyId}" />
	          						<input type="hidden" name="DONE_PAGE" value="${donePage}" />
	          						<input type="hidden" name="useValues" value="true" />
	          						<input type="hidden" name="contactMechId" value="${contactMechId?if_exists}" />
	        						<select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" onchange="checkBtnCreatePartyContactMechPurpose()" style="margin-bottom:0">
	          							<option></option>
				                      	<#list mechMap.purposeTypes as contactMechPurposeType>
					                        <option value="${contactMechPurposeType.contactMechPurposeTypeId}">${contactMechPurposeType.get("description",locale)}</option>
				                      	</#list>
	       		 					</select>
	       		 					<a id="btnCreatePartyContactmechPurpose" class="btn btn-mini btn-primary disabled" href="javascript:createPartyContactMechPurpose();"><i class="open-sans icon-plus-sign"></i>${uiLabelMap.PartyAddPurpose}</a>
	        					</form>
		      				</div>
		      			</div>
					</#if>
					-->
				</div>
			</div><!--.tab-content-->
		</div><!--.mech-purpose-types-->
	</div><!--.span12-->
</div><!--.row-fluid-->
<#include "script/contactMechEditScript.ftl">
<#else>
	<a class="btn btn-small btn-primary icon-arrow-left open-sans" href="<@ofbizUrl>backHome</@ofbizUrl>">${uiLabelMap.CommonGoBack}</a>
</#if>