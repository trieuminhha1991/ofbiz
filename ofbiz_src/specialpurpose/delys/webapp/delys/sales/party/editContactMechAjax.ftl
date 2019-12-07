<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#if !errorMessage?has_content>
  	<#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  	<#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessage?has_content>
  	<#assign eventMessage = requestAttributes._EVENT_MESSAGE_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  	<#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>
<style type="text/css">
	.info .message-validate-container {
		display:none !important;
	}
	.message-validate-container {
		position:relative;
		display:none;
	}
	.message-validate-container .help-inline {
		color:#FFF;
	}
	.message-validate-container .jqx-validator-hint {
		position: absolute; left: 220px; top: -25px; display: block;
	}
	.message-validate-container .jqx-validator-hint .jqx-validator-hint-arrow {
		position: absolute; left: -4.5px; top: 1px; background-image: none;
	}
 	.message-validate-container .jqx-validator-hint .jqx-validator-hint-arrow img {
	 	position: relative; top: 0px; left: 0px; width: 9px; height: 9px;
 	}
</style>
<#-- display the error messages -->
<#if (errorMessage?has_content || errorMessageList?has_content)>
  	<div id="content-messages" class="alert alert-error" onclick="document.getElementById('content-messages').parentNode.removeChild(this)">
    	<strong>${uiLabelMap.CommonFollowingErrorsOccurred}:</strong>
    	<#if errorMessage?has_content>
      		<strong>${errorMessage}</strong>
    	</#if>
    	<#if errorMessageList?has_content>
      		<#list errorMessageList as errorMsg>
        		<p>${errorMsg}</p>
      		</#list>
    	</#if>
  	</div>
</#if>
<#-- display the event messages -->
<#if (eventMessage?has_content || eventMessageList?has_content)>
  	<div id="content-messages" class="alert alert-info" onclick="document.getElementById('content-messages').parentNode.removeChild(this)">
    	<#--<strong>${uiLabelMap.CommonFollowingOccurred}:</strong>-->
    	<#if eventMessage?has_content>
      		<strong>${eventMessage}</strong>
    	</#if>
    	<#if eventMessageList?has_content>
      		<#list eventMessageList as eventMsg>
        		<p>${eventMsg}</p>
      		</#list>
    	</#if>
  	</div>
</#if>
<#if mechMap.contactMechTypeId?has_content>
  	<#if mechMap.contactMech?has_content>
		<div id="mech-purpose-types">
			<ul class="nav nav-tabs" id="recent-tab">
				<li class="active">
					<a data-toggle="tab" href="#purposeTypes-tab">${uiLabelMap.DAOverview}</a>
				</li>
				<li>
					<a data-toggle="tab" href="#editContactmech-tab">${uiLabelMap.PartyContactPurposes}</a>
				</li>
			</ul>
			<div class="tab-content padding-8 overflow-visible">
				<div id="purposeTypes-tab" class="tab-pane active">
					<form method="post" action="<@ofbizUrl>${mechMap.requestName}</@ofbizUrl>" name="editcontactmechformUpdate" id="editcontactmechformUpdate">
						<input type="hidden" name="contactMechId" value="${contactMechId}" />
						<input type="hidden" name="contactMechTypeId" value="${mechMap.contactMechTypeId}" />
						<input type="hidden" name="partyId" value="${partyId}" />
						<input type="hidden" name="DONE_PAGE" value="${donePage?if_exists}" />
						<div class="form-horizontal form-table-block">
			    			<#if "POSTAL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
				    			<div class="row-fluid">
				    				<div class="span6">
						    			<div class="control-group">
											<label class="control-label" for="toName2">${uiLabelMap.DARecipientsName}</label>
											<div class="controls">
												<input type="text" size="50" maxlength="100" name="toName" id="toName2" class="span12 input-normal" value="${(mechMap.postalAddress.toName)?default(request.getParameter('toName')?if_exists)}" tabindex=1/>
												<div class="message-validate-container">
													<div class="jqx-validator-hint jqx-validator-hint-olbius jqx-rc-all">
														<div class="jqx-validator-hint-arrow jqx-validator-hint-arrow-olbius">
															<img src="https://localhost:8443/aceadmin/jqw/jqwidgets/styles/images/multi-arrow.gif" alt="Arrow">
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label" for="attnName2">${uiLabelMap.DAOtherName}</label>
											<div class="controls">
												<input type="text" size="50" maxlength="100" name="attnName" class="span12 input-normal" value="${(mechMap.postalAddress.attnName)?default(request.getParameter('attnName')?if_exists)}" />
												<div class="message-validate-container">
													<div class="jqx-validator-hint jqx-validator-hint-olbius jqx-rc-all">
														<div class="jqx-validator-hint-arrow jqx-validator-hint-arrow-olbius">
															<img src="https://localhost:8443/aceadmin/jqw/jqwidgets/styles/images/multi-arrow.gif" alt="Arrow">
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label required" for="postalCode2">${uiLabelMap.PartyZipCode}</label>
											<div class="controls">
												<input type="text" size="30" maxlength="60" name="postalCode" id="postalCode2" class="span12 input-normal" value="${(mechMap.postalAddress.postalCode)?default(request.getParameter('postalCode')?if_exists)}" />
												<div class="message-validate-container">
													<div class="jqx-validator-hint jqx-validator-hint-olbius jqx-rc-all">
														<div class="jqx-validator-hint-arrow jqx-validator-hint-arrow-olbius">
															<img src="https://localhost:8443/aceadmin/jqw/jqwidgets/styles/images/multi-arrow.gif" alt="Arrow">
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label" for="allowSolicitation2">${uiLabelMap.PartyContactAllowSolicitation}?</label>
											<div class="controls">
												<select name="allowSolicitation" id="allowSolicitation2">
											        <#if (((mechMap.partyContactMech.allowSolicitation)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
											        <#if (((mechMap.partyContactMech.allowSolicitation)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
											        <option></option>
											        <option value="Y">${uiLabelMap.CommonY}</option>
											        <option value="N">${uiLabelMap.CommonN}</option>
										      	</select>
											</div>
										</div>
									</div><!--.span6-->
									
									<div class="span6">
										<div class="control-group">
											<label class="control-label" for="countryGeoId2">${uiLabelMap.CommonCountry}</label>
											<div class="controls">
												<select name="countryGeoId" id="countryGeoId2">
										          	${screens.render("component://common/widget/CommonScreens.xml#countries")}        
										          	<#if (mechMap.postalAddress?exists) && (mechMap.postalAddress.countryGeoId?exists)>
										            	<#assign defaultCountryGeoId = mechMap.postalAddress.countryGeoId>
										          	<#else>
										           		<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
										          	</#if>
										          	<option selected="selected" value="${defaultCountryGeoId}">
										            	<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
										            	${countryGeo.get("geoName",locale)}
										          	</option>
										        </select>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label" for="countryGeoId2">${uiLabelMap.PartyState}</label>
											<div class="controls">
												<select name="stateProvinceGeoId" id="stateProvinceGeoId2"></select>
											</div>
										</div>
										<#--
										<td align="right" class="required">${uiLabelMap.PartyCity}</td>
										<td align="left">
											<input type="text" size="50" maxlength="100" name="city" value="${(mechMap.postalAddress.city)?default(request.getParameter('city')?if_exists)}" />
										</td>
										-->
										<div class="control-group">
											<label class="control-label" for="districtGeoId2">${uiLabelMap.PartyDistrictGeoId}</label>
											<div class="controls">
												<select name="districtGeoId" id="districtGeoId2"></select>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label" for="wardGeoId2">${uiLabelMap.PartyWardGeoId}</label>
											<div class="controls">
												<select name="wardGeoId" id="wardGeoId2"></select>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label required" for="wardGeoId2">${uiLabelMap.PartyAddressLine1}</label>
											<div class="controls">
												<input type="text" size="100" maxlength="255" name="address1" id="address12" class="span12 input-normal" value="${(mechMap.postalAddress.address1)?default(request.getParameter('address1')?if_exists)}" />
												<div class="message-validate-container">
													<div class="jqx-validator-hint jqx-validator-hint-olbius jqx-rc-all">
														<div class="jqx-validator-hint-arrow jqx-validator-hint-arrow-olbius">
															<img src="https://localhost:8443/aceadmin/jqw/jqwidgets/styles/images/multi-arrow.gif" alt="Arrow">
														</div>
													</div>
												</div>
											</div>
										</div>
										<#--
										<tr>
											<#assign isUsps = Static["org.ofbiz.party.contact.ContactMechWorker"].isUspsAddress(mechMap.postalAddress)>
											<td align="right">${uiLabelMap.PartyIsUsps}</td>
											<td align="left"><#if isUsps>${uiLabelMap.CommonY}<#else>${uiLabelMap.CommonN}</#if></td>
											
											<td align="right"></td>
											<td align="left"></td>
										</tr>
										-->
									</div><!--.span6-->
								</div><!--.row-fluid-->
							<#elseif "TELECOM_NUMBER" = mechMap.contactMechTypeId?if_exists>
								<div class="row-fluid">
				    				<div class="span6">
						    			<div class="control-group">
											<label class="control-label">${uiLabelMap.PartyPhoneNumber}</label>
											<div class="controls">
												<input type="text" size="4" maxlength="10" name="countryCode" value="${(mechMap.telecomNumber.countryCode)?default(request.getParameter('countryCode')?if_exists)}" />
												-&nbsp;<input type="text" size="4" maxlength="10" name="areaCode" value="${(mechMap.telecomNumber.areaCode)?default(request.getParameter('areaCode')?if_exists)}" />
												-&nbsp;<input type="text" size="15" maxlength="15" name="contactNumber" value="${(mechMap.telecomNumber.contactNumber)?default(request.getParameter('contactNumber')?if_exists)}" />
												&nbsp;${uiLabelMap.PartyContactExt}&nbsp;<input type="text" size="6" maxlength="10" name="extension" value="${(mechMap.partyContactMech.extension)?default(request.getParameter('extension')?if_exists)}" />
											</div>
										</div>
									</div><!--.span6-->
									<div class="span6">
										<div class="control-group">
											<label class="control-label"></label>
											<div class="controls">
												[${uiLabelMap.CommonCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyContactExt}]
											</div>
										</div>
									</div><!--.span6-->
								</div><!--.row-fluid-->
							<#elseif "EMAIL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
								<div class="row-fluid">
				    				<div class="span6">
						    			<div class="control-group">
											<label class="control-label">${mechMap.contactMechType.get("description",locale)}</label>
											<div class="controls">
												<input type="text" size="60" maxlength="255" name="emailAddress" class="span12 input-normal" value="${(mechMap.contactMech.infoString)?default(request.getParameter('emailAddress')?if_exists)}" />
											</div>
										</div>
									</div><!--.span6-->
									<div class="span6">
										<div class="control-group">
											<label class="control-label"></label>
											<div class="controls"></div>
										</div>
									</div><!--.span6-->
								</div><!--.row-fluid-->
							<#else>
								<div class="row-fluid">
				    				<div class="span6">
						    			<div class="control-group">
											<label class="control-label">${mechMap.contactMechType.get("description",locale)}</label>
											<div class="controls">
												<input type="text" size="60" maxlength="255" name="infoString" class="span12 input-normal" value="${(mechMap.contactMech.infoString)?if_exists}" />
											</div>
										</div>
									</div><!--.span6-->
									<div class="span6">
										<div class="control-group">
											<label class="control-label"></label>
											<div class="controls"></div>
										</div>
									</div><!--.span6-->
								</div><!--.row-fluid-->
							</#if><!--mechMap.contactMechTypeId-->
						</div><!--.form-horizontal-->
			  		</form>
				</div>
				<div id="editContactmech-tab" class="tab-pane">
					<#--<td align="right">${uiLabelMap.PartyContactPurposes}</td>-->
	      			<#if mechMap.purposeTypes?has_content>
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
				                    				(${uiLabelMap.DAFromDate}: 
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
		      				</div><!--.span12-->
		      			</div><!--.row-fluid-->
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
				</div>
			</div><!--.tab-content-->
		</div><!--.mech-purpose-types-->	
		<#--
		<hr style="border-top: 1px solid #ddd; margin-top: 15px; margin-bottom: 15px;"/>
		toName: {
							specialCharacterName: true
						},
						attnName: {
							specialCharacterName: true
						},
						toName: {
							specialCharacterName: "${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}"
						},
						attnName: {
							specialCharacterName: "${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}"
						},-->
	  	<script type="text/javascript">
	  		$(function(){
	  			$(".message-validate-container").on("click", function(){
	  				$(this).hide();
	  			});
	  			
	  			$.validator.addMethod('specialCharacterName',function(value, element){
					if(!(/^\s*$/.test(value)) && !(/^(?:[\p{L}\p{Mn}\p{Pd}\'\x{2019}]+\s[\p{L}\p{Mn}\p{Pd}\'\x{2019}]+\s?)+$/.test(value))){
						return false;
					}
					return true;
				},'${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}');
				$.validator.addMethod('specialCharacterNumber',function(value, element){
					if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9]+$/.test(value))){
						return false;
					}
					return true;
				},'${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}');
	  			$('#editcontactmechformUpdate').validate({
					errorElement: 'span',
					errorClass: 'help-inline',
					focusInvalid: false,
					rules: {
						postalCode: {
							required: true,
							specialCharacterNumber: true
						},
						address1: {
							required: true
						}
					},
					messages: {
						postalCode: {
							required: "${uiLabelMap.DAThisFieldIsRequired}",
							specialCharacterNumber: "${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}"
						},
						address1: {
							required: "${uiLabelMap.DAThisFieldIsRequired}"
						}
					},
					invalidHandler: function (event, validator) { //display error alert on form submit   
						$('.alert-error', $('.login-form')).show();
					},
					highlight: function (e) {
						$(e).closest('td').removeClass('info').addClass('error');
					},
					unhighlight: function(element, errorClass) {
			    		var parentControls = $(element).closest("td");
			    		if (parentControls != undefined) {
			    			parentControls.find("input").css("border", "1px solid #64a6bc");
			    			parentControls.find("select").css("border", "1px solid #64a6bc");
			    		}
			    	},
					success: function (e) {
						$(e).closest('td').removeClass('error').addClass('info');
						$(e).remove();
					},
					errorPlacement: function (error, element) {
						var parentControls = element.closest("td");
						var messageElement = parentControls.find(".jqx-validator-hint");
						if (parentControls != undefined) {
							error.appendTo(messageElement);
							var container = parentControls.find(".message-validate-container");
							if (container != undefined) {
								container.show();
							}
							parentControls.find("input").css("border", "1px solid #f09784");
							parentControls.find("select").css("border", "1px solid #f09784");
						}
					}
				});
	  		});
	  	</script>
	  	<#--
	  	<div class="button-bar">
	    	<a href="<@ofbizUrl>backHome</@ofbizUrl>" class="btn btn-small btn-info icon-arrow-left open-sans"> ${uiLabelMap.CommonGoBack}</a>
	    	<a href="javascript:document.editcontactmechformUpdate.submit()" class="btn btn-small btn-info icon-ok open-sans">${uiLabelMap.CommonSave}</a>
	  	</div>
	  	-->
	  	<#--
	  	<script type="text/javascript">
	  		function closePopupWindow() {
	  			if ($("#modal-table-edit-contact-mech") != undefined) {
	  				$("#modal-table-edit-contact-mech").modal('hide');
	  			}
	  		}
	  		
	  		function updateContactMech() {
	  			var data = $("#editcontactmechformUpdate").serialize();
		        jQuery.ajax({
		        	type: "POST",
		        	url: "${mechMap.requestName}InfoAjax",
		        	data: data,
		        	success: function (data) {
		        		$("#modal-body-edit-contact-mech").html(data);
	  					updateCheckoutArea();
		        	},
		        	error: function () {
		        		//commit(false);
		        	}
		        });
	  		}
	  	</script>
	  	-->
	</#if>
<#else>
  <a class="btn btn-small btn-primary icon-arrow-left open-sans" href="<@ofbizUrl>backHome</@ofbizUrl>">${uiLabelMap.CommonGoBack}</a>
</#if>
