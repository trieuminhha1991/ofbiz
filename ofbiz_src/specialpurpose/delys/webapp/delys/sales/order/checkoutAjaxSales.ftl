<#--
<style type="text/css">
	.jqx-window-olbius .jqx-window-content table tr td {
		width: 180px;
		min-width:180px;
		max-width:180px;
		padding-right: 15px;
	}
</style>
-->
<style type="text/css">
	textarea { resize: vertical; }
</style>
<#if shoppingCart?exists>
	<#assign shipping = !shoppingCart.containAllWorkEffortCartItems()/> <#-- contains items which need shipping? -->
<#else>
	<#assign shipping = true />
</#if>

<form class="form-horizontal basic-custom-form form-size-mini form-size-micro" method="post" name="checkoutInfoForm" id="checkoutInfoForm">
	<#--
	<input type="hidden" name="checkoutpage" value="quick"/>
	<input type="hidden" name="BACK_PAGE" value="quickcheckout"/>
	-->
	<div class="control-group">
		<label class="control-label">${uiLabelMap.DAReceiver}</label>
		<div class="controls">
			<!--input-small-->
			<#if cartParties?exists && cartParties?has_content>
				<select name="shipToCustomerPartyId" onchange="javascript:submitForm(document.checkoutInfoForm, 'SC', null);">
	              	<#list cartParties as cartParty>
	              		<option value="${cartParty}">${cartParty}</option>
	              	</#list>
              	</select>
              	<span class="help-inline" style="margin-left:4px;">
	          		<#--<a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');"><i class="icon-plus open-sans"></i>${uiLabelMap.DAAddNewAddress}</a>-->
	          		<a href="javascript:void(0);" id="btnNewShippingAddress"><i class="fa fa-plus-circle"></i>${uiLabelMap.DACreateNewAddress}</a>
	          	</span>
          	<#else>
				<span>${uiLabelMap.DAHaveNotYetShippingAddress}</span>
           	</#if>
		</div>
	</div>
	<#-- Order Split Into Multiple Shipments ... -->
	<div class="control-group" style="margin-top:5px; position:relative;height:104px;overflow:scroll;">
		<div style="position:absolute; top:0; right:10px"><a href="javascript:void(0)" onclick="javascript:updateCheckoutArea();"><i class="fa fa-refresh"></i></a></div>
		<label class="control-label required">${uiLabelMap.DADestination}</label>
		<div class="controls" style="margin-top: -25px!important">
			<input type="hidden" value="${partyIdSelected?if_exists}" name="partyIdSelected" id="partyIdSelected"/>
			<#if shippingContactMechList?has_content>
				<#list shippingContactMechList as shippingContactMech>
					<#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
					<div>
						<label style="width:306px; display:inline-block; text-align:left">
							<input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}" onclick="javascript:submitForm(document.checkoutInfoForm, 'SA', null);" <#if shoppingCart?exists && shoppingCart.getShippingContactMechId()?default("") == shippingAddress.contactMechId> checked="checked"</#if>/>
							<span class="lbl" style="width: 100%">
								<address style="display:inline-block; margin-bottom:0; font-family: 'Open Sans'; font-size: 13px; color: #393939;width: 90%">
									<#assign isFirst = true/>
									<#assign hasState = false/>
									<#if shippingAddress.toName?has_content><#assign isFirst = false/><strong>${shippingAddress.toName}</strong></#if>
									<#if shippingAddress.attnName?has_content><#assign isFirst = false/>(${shippingAddress.attnName})</#if>
									<#if shippingAddress.address1?has_content><#if !isFirst><#assign isFirst = false/><br /></#if>${shippingAddress.address1}</#if>
									<#if shippingAddress.address2?has_content><#if !isFirst><#assign isFirst = false/><br /></#if>${shippingAddress.address2}</#if>
									<#if shippingAddress.city?has_content><#if !isFirst><#assign isFirst = false/><br /></#if>${shippingAddress.city}</#if>
									<#if shippingAddress.stateProvinceGeoId?has_content><#if !isFirst><#assign isFirst = false/><br /></#if><#assign hasState = true/>${shippingAddress.stateProvinceGeoId}</#if>
									<#if shippingAddress.countryGeoId?has_content><#if !isFirst><#assign isFirst = false/><#if hasState>, <#else><br /></#if></#if>${shippingAddress.countryGeoId}</#if>
									<#--<#if shippingAddress.postalCode?has_content><#if !isFirst><#assign isFirst = false/><br /></#if>${shippingAddress.postalCode}</#if>-->
									<#--<abbr title="Phone">P:</abbr>(123) 456-7890-->
								</address>
							</span>
						</label>
						<span class="help-inline" style="vertical-align:top">
							<#--<a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');"><i class="fa fa-pencil-square"></i> ${uiLabelMap.CommonEdit}</a>-->
							<a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom"><i class="fa fa-pencil-square"></i></a>
							<a href="javascript:submitForm(document.checkoutInfoForm, 'DA', 'partyId=${partyIdSelected?if_exists}&contactMechId=${shippingAddress.contactMechId?if_exists}');" 
								data-rel="tooltip" title="${uiLabelMap.CommonExpire}" data-placement="bottom"><i class="fa fa-times-circle open-sans open-sans-index"></i></a>
							<#--
							<form style="float:left;" name="partyDeleteContact" method="post" action="<@ofbizUrl>deleteContactMech</@ofbizUrl>" onsubmit="javascript:submitFormDisableSubmits(this)">
			                    <input name="partyId" value="${partyId}" type="hidden"/>
			                    <input name="contactMechId" value="${contactMech.contactMechId}" type="hidden"/>
			                    <button type="submit" class="btn btn-warning btn-mini icon-remove open-sans">
			                    ${uiLabelMap.CommonExpire} 
			                    </button>
		                  	</form>
							-->
						</span>
					</div>
				</#list>
			<#else>
				<span>${uiLabelMap.DAHaveNotYetShippingAddress}</span>
			</#if>
		</div>
	</div>
	<div style="clear:both"></div>
	<#-- DONENOACCTRANS: Choose shipping method -->
  	<div class="control-group" style="margin-top:5px">
  		<label class="control-label">${uiLabelMap.DAAbbSpecialInstructions}</label>
  		<div class="controls">
  			<textarea cols="30" rows="2" wrap="hard" name="shipping_instructions" id="shipping_instructions" class="span12"><#if shoppingCart?exists>${shoppingCart.getShippingInstructions()?if_exists}</#if></textarea>
  		</div>
  	</div>
	<#-- DONENOACCTRANS: Choose payment method -->
</form>

<#--
<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="btn btn-primary btn-mini open-sans icon-arrow-left">${uiLabelMap.OrderBacktoShoppingCart}</a>
<a href="javascript:submitForm(document.checkoutInfoForm, 'DN', '');" class="btn btn-primary btn-mini open-sans icon-arrow-right">${uiLabelMap.OrderContinueToFinalOrderReview}</a>
-->
<#assign isCreate = false/>
<#assign requestNameDis = ""/>
<#if mechMap?exists && mechMap.contactMechTypeId?has_content && "POSTAL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
	<#assign requestNameDis = mechMap.requestName + "PartyDis"/>
	<#assign isCreate = true>
	<form id="editcontactmechform" name="editcontactmechform" class="form-horizontal form-table-block" method="post" action="<@ofbizUrl>${requestNameDis}</@ofbizUrl>">
		<div id="alterpopupWindow2" style="display:none">
			<div>${uiLabelMap.DACreateNewContact}</div>
			<div class="form-horizontal form-table-block">
				<input type="hidden" name="DONE_PAGE" id="new_DONE_PAGE" value="${donePage}" />
		        <input type="hidden" name="contactMechTypeId" id="new_contactMechTypeId" value="${mechMap.contactMechTypeId}" />
		        <input type="hidden" name="partyId" id="new_partyId" value="${partyId?if_exists}" />
		        <#if cmNewPurposeTypeId?has_content><input type="hidden" name="contactMechPurposeTypeId" id="new_contactMechPurposeTypeId" value="${cmNewPurposeTypeId}" /></#if>
		        <#if preContactMechTypeId?exists><input type="hidden" name="preContactMechTypeId" id="new_preContactMechTypeId" value="${preContactMechTypeId}" /></#if>
		        <#if contactMechPurposeTypeId?exists><input type="hidden" name="contactMechPurposeTypeId" id="new_contactMechPurposeTypeId" value="${contactMechPurposeTypeId?if_exists}" /></#if>
		        <#if paymentMethodId?has_content><input type='hidden' name='paymentMethodId' id="new_paymentMethodId" value='${paymentMethodId}' /></#if>
		        <#--
				<div class="control-group info">
					<div class="controls" style="margin:-25px 10px 5px 0px !important">
						<#if contactMechPurposeType?exists>
					      	<span style="color: #657ba0; font-size:13px">(${uiLabelMap.DAMsgNewAddressHavePurpose} <b>"${contactMechPurposeType.get("description",locale)?if_exists}"</b>)</span>
					    </#if>
					</div>
				</div> -->
		    	<div class="row-fluid" style="margin-bottom:10px">
		    		<div class="span12">
		    			<div class="control-group">
							<label class="control-label" for="toName">${uiLabelMap.DARecipientsName}</label>
							<div class="controls">
								<input type="text" name="toName" id="toName" class="span12 input-normal" maxlength="100" value="${(mechMap.postalAddress.toName)?default(request.getParameter('toName')?if_exists)}">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="attnName">${uiLabelMap.DAOtherName}</label>
							<div class="controls">
								<input type="text" name="attnName" id="attnName" class="span12 input-normal" maxlength="100" value="${(mechMap.postalAddress.attnName)?default(request.getParameter('attnName')?if_exists)}">
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="countryGeoId">${uiLabelMap.CommonCountry}</label>
							<div class="controls">
								<select name="countryGeoId" id="countryGeoId">
      								${screens.render("component://common/widget/CommonScreens.xml#countries")}        
       								<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
      								<option selected="selected" value="${defaultCountryGeoId}">
        								<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
        								${countryGeo.get("geoName",locale)}
      								</option>
    							</select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="stateProvinceGeoId">${uiLabelMap.PartyState}</label>
							<div class="controls">
								<select name="stateProvinceGeoId" id="stateProvinceGeoId">
								</select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="districtGeoId">${uiLabelMap.PartyDistrictGeoId}</label>
							<div class="controls">
								<select name="districtGeoId" id="districtGeoId">
								</select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="wardGeoId">${uiLabelMap.PartyWardGeoId}</label>
							<div class="controls">
								<select name="wardGeoId" id="wardGeoId">
								</select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="address1">${uiLabelMap.DAAddress}</label>
							<div class="controls">
								<input type="text" name="address1" id="address1" class="span12 input-normal" maxlength="255" value="${(mechMap.postalAddress.address1)?default(request.getParameter('address1')?if_exists)}">
							</div>
						</div>
						
						<#--
						<div class="control-group">
							<label class="control-label" for="address2">${uiLabelMap.DAAddressLine2}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="address2" id="address2" class="span12" maxlength="255" value="${(mechMap.postalAddress.address2)?default(request.getParameter('address2')?if_exists)}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="city">${uiLabelMap.DACity} *</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="city" id="city" class="span12 input-small" maxlength="100" value="${(mechMap.postalAddress.city)?default(request.getParameter('city')?if_exists)}">
								</div>
							</div>
						</div>
						-->
						
						<div class="control-group">
							<label class="control-label required" for="postalCode">${uiLabelMap.DAZipCode}</label>
							<div class="controls">
								<input type="text" name="postalCode" id="postalCode" class="span12 input-normal" maxlength="60" value="${(mechMap.postalAddress.postalCode)?default(request.getParameter('postalCode')?if_exists)}">
							</div>
						</div>
						<#--
						<div class="control-group">
							<label class="control-label" for="editcontactmechform_countryGeoId">${uiLabelMap.CommonCountry}</label>
							<div class="controls">
								<div class="span12">
									<select name="countryGeoId" id="editcontactmechform_countryGeoId" class="span12 input-small">
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
						</div>
						<div class="control-group">
							<label class="control-label" for="editcontactmechform_stateProvinceGeoId">${uiLabelMap.DAState}</label>
							<div class="controls">
								<div class="span12">
									<select name="stateProvinceGeoId" id="editcontactmechform_stateProvinceGeoId" class="span12 input-small">
					        		</select>
								</div>
							</div>
						</div>
						-->
						
						<#--
						<#assign isUsps = Static["org.ofbiz.party.contact.ContactMechWorker"].isUspsAddress(mechMap.postalAddress)>
						<div class="control-group">
							<label class="control-label" for="orderId">${uiLabelMap.PartyIsUsps}</label>
							<div class="controls">
								<div class="span12">
									<#if isUsps>${uiLabelMap.CommonY}<#else>${uiLabelMap.CommonN}</#if>
								</div>
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label" for="allowSolicitation">${uiLabelMap.DAContactAllowSolicitation}?</label>
							<div class="controls">
								<select name="allowSolicitation" id="allowSolicitation" class="span12 input-normal">
						          	<#if (((mechMap.partyContactMech.allowSolicitation)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
							        <#if (((mechMap.partyContactMech.allowSolicitation)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
							        <option></option>
							        <option value="Y">${uiLabelMap.CommonY}</option>
							        <option value="N">${uiLabelMap.CommonN}</option>
						        </select>
							</div>
						</div>
		    		</div>
				</div><!--.row-fluid-->
		    	<#--<div class="row-fluid">
		    		<div class="span12">
		    			<div class="control-group">
		    				<label class="control-label"></label>
		    				<div class="controls">
	    						<input type="button" id="alterSave1" value="${uiLabelMap.CommonSave}"/>
								<input type="button" id="alterCancel1" value="${uiLabelMap.CommonCancel}"/>
		    				</div>
		    			</div>
		    		</div>
		    	</div>-->
    			<div class="control-group" style="margin:0 !important; font-size:9pt;height: 25px;">
					<span style="color:#666"><i>(${uiLabelMap.DAMsgNewAddressHavePurpose} <b>"${contactMechPurposeType.get("description",locale)?if_exists}"</b>)</i></span>
				</div>
				<div class='row-fluid row-action'>
					<div class="span12">
						<button id="alterCancel1" class='btn btn-mini btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.DACancelStatus}</button>
						<button id="alterSave1" class='btn btn-mini btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
					</div>
				</div>
		    </div>
		</div>
	</form>
<#else>
	<form id="editcontactmechform" name="editcontactmechform" class="form-horizontal form-table-block" method="post" action="<@ofbizUrl>updateQuotationRule</@ofbizUrl>">
	</form>
</#if>

<div id="alterpopupWindowEditContactMech" style="display:none;">
    <div>${uiLabelMap.DAEditContactInformation}</div>
    <div style="overflow: hidden;">
    	<div id="modal-body-edit-contact-mech" class="form-window-content">
			<div style="position:relative;height:50px">
				<div id="info_loader_edit_contact_mech" style="overflow: visible; position: absolute; display: block; left: 40%; top: 0; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
					<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
						<div style="float: left;">
							<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
							<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	  		<div class='row-fluid'>
				<div class="span12">
					<#--<a href="javascript:document.editcontactmechformUpdate.submit()" class="btn btn-small btn-primary"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSave}</a>-->
					<a href="javascript:closePopupWindow();" class="btn btn-mini btn-danger form-action-button pull-right"><i class="icon-remove open-sans"></i>${uiLabelMap.DACancelStatus}</a>
					<a href="javascript:updateContactMech();" class="btn btn-mini btn-primary form-action-button pull-right"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSave}</a>
				</div>
			</div>
		</div>
		<div style="position:relative">
			<div id="info_loader_2" style="overflow: hidden; position: fixed; display: none; left: 46%; top: 46%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
				<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
					<div style="float: left;">
						<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
						<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
					</div>
				</div>
			</div>
		</div>
    </div>
</div><!--alterpopupWindowEditContactMech-->

<div id="modal-table-edit-contact-mech" class="modal hide fade jqx-window-olbius jqx-modal-standard" tabindex="-1" style="width:960px; left:35%">
	<#--
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.DAEditContactInformation}
		</div>
	</div>
	<div class="modal-body jqx-window-content">
		<div id="modal-body-edit-contact-mech">
			<div style="position:relative;height:50px">
				<div id="info_loader_edit_contact_mech" style="overflow: visible; position: absolute; display: block; left: 40%; top: 0; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
					<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
						<div style="float: left;">
							<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
							<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div style="position:relative">
			<div id="info_loader_2" style="overflow: hidden; position: fixed; display: none; left: 46%; top: 46%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
				<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
					<div style="float: left;">
						<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
						<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	-->
	<#--<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>${uiLabelMap.CommonClose}
		</button>
		<div class="pagination pull-right no-margin">
			<button type="button" class="btn btn-small btn-primary pull-left" onClick="javascript:onConfirm();">
				<i class="icon-ok"></i>${uiLabelMap.CommonSave}
			</button>
		</div>
	</div>-->
</div>
<#--
$('#editcontactmechformUpdate').jqxValidator({rules: [
	{input: '#toName2', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
		function (input, commit) {
			var value = $(input).val();
			if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9]+$/.test(value))){
				return false;
			}
			return true;
		}
	},
	{input: '#attnName2', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
		function (input, commit) {
			var value = $(input).val();
			if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9]+$/.test(value))){
				return false;
			}
			return true;
		}
	},
	{input: '#postalCode2', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
		function (input, commit) {
			var value = $(input).val();
			if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9-]+$/.test(value))){
				return false;
			}
			return true;
		}
	},
	{input: '#address12', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
		function (input, commit) {
			var value = $(input).val();
			if(value == null || /^\s*$/.test(value)){
				return false;
			}
			return true;
		}
	},
	{input: '#postalCode2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
		function (input, commit) {
			var value = $(input).val();
			if(value == null || /^\s*$/.test(value)){
				return false;
			}
			return true;
		}
	}
]});
if(!$('#editcontactmechformUpdate').jqxValidator('validate')) return false;
-->
<script type="text/javascript">
	function closePopupWindow() {
		if ($("#modal-table-edit-contact-mech") != undefined) {
			//$("#modal-table-edit-contact-mech").modal('hide');
			$("#alterpopupWindowEditContactMech").jqxWindow("close");
			updateCheckoutArea();
		}
	}
	
	function updateContactMech() {
		if(!$('#editcontactmechformUpdate').valid()) return false;
		var data = $("#editcontactmechformUpdate").serialize();
        jQuery.ajax({
        	type: "POST",
        	url: "updatePostalAddressInfoAjax",
        	data: data,
        	beforeSend: function () {
        		$("#info_loader_2").show();
        	}, 
        	success: function (data) {
        		$("#modal-body-edit-contact-mech").html($(data));
        	},
        	error: function () {
        		//commit(false);
        	},
        	complete: function() {
        		$("#info_loader_2").hide();
        		$("#alterpopupWindowEditContactMech").jqxWindow("close");
        	}
        });
	}
	function createPartyContactMechPurpose() {
		var data = $("#newpurposeform").serialize();
        jQuery.ajax({
        	type: "POST",
        	url: "createPartyContactMechPurposeAjax",
        	data: data,
        	beforeSend: function () {
        		$("#info_loader_2").show();
        	}, 
        	success: function (data) {
        		$("#modal-body-edit-contact-mech").html(data);
        	},
        	error: function () {
        		//commit(false);
        	},
        	complete: function() {
        		$("#info_loader_2").hide();
        	}
        });
	}
	function checkBtnCreatePartyContactMechPurpose () {
		var value = $("#contactMechPurposeTypeId").val();
		if (value != null && !(/^\s*$/.test(value))) {
			$("#btnCreatePartyContactmechPurpose").removeClass("disabled");
		} else {
			$("#btnCreatePartyContactmechPurpose").addClass("disabled");
		}
	}
	function deletePartyContactMechPurpose(formId) {
		var data = $("#" + formId).serialize();
        jQuery.ajax({
        	type: "POST",
        	url: "deletePartyContactMechPurposeAjax",
        	data: data,
        	beforeSend: function () {
        		$("#info_loader_2").show();
        	}, 
        	success: function (data) {
        		$("#modal-body-edit-contact-mech").html(data);
        	},
        	error: function () {
        		//commit(false);
        	},
        	complete: function() {
        		$("#info_loader_2").hide();
        	}
        });
	}
</script>
<script type="text/javascript">
	<#--
	$('#modal-table').on('show.bs.modal', function (e) {
	  	setTimeout(function() {$("#percentNumber").focus()}, 1000);
	});
	-->
	//<![CDATA[
	function submitForm(form, mode, value) {
	    if (mode == "DN") {
	        // done action; checkout
	        //form.action="<@ofbizUrl>checkout</@ofbizUrl>";
	        //form.submit();
	    } else if (mode == "CS") {
	        // continue shopping
	        form.action="<@ofbizUrl>updateCheckoutOptions/showcart</@ofbizUrl>";
	        form.submit();
	    } else if (mode == "NA") {
	        // new address
	        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=quickcheckout&partyId=<#if shoppingCart?exists>${shoppingCart.getPartyId()}</#if>&preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION</@ofbizUrl>";
	        form.submit();
	    } else if (mode == "SA") {
	    	// STEP 1
	        // selected shipping address
	        /*
	        form.action="updateCheckoutOptions/quickcheckout";
	        form.submit(); 
	        quickCheckoutAjaxDis
	        */
	        /*
	        var data = $("#initOrderEntry").serialize();
	        data += "&" + $("#checkoutInfoForm").serialize();
	        $.ajax({
				type: "POST", 
				url: "updateCheckoutOptionsAjaxDis",
				data: data, 
				beforeSend: function () {
					$("#checkoutInfoLoader").show();
				}, 
				success: function (data) {
					$("#checkoutInfo").html(data);
				}, 
				error: function() {
					//commit(false);
				},
				complete: function () {
					$("#checkoutInfoLoader").hide();
				}
			});
	        */
	    } else if (mode == "SC") {
	        // selected ship to party
	        form.action="<@ofbizUrl>cartUpdateShipToCustomerParty</@ofbizUrl>";
	        form.submit();
	    } else if (mode == "EA") {
	        // edit address
	        //form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?contactMechId="+value+"</@ofbizUrl>";
	        //form.submit();
	        var partyId = $("#partyIdSelected").val();
	        //$("#modal-table-edit-contact-mech").modal("show");
	        $("#alterpopupWindowEditContactMech").jqxWindow("open");
	        
	        var data = {'partyId' : partyId, 'contactMechId' : value};
	        jQuery.ajax({
	        	type: "POST",
	        	url: "getInfoContactMechDetail",
	        	data: data,
	        	success: function (data) {
	        		$("#modal-body-edit-contact-mech").html(data);
	        	},
	        	error: function () {
	        		//commit(false);
	        	}
	        });
	    } else if (mode == "DA") {
	    	bootbox.confirm("${uiLabelMap.DAAreYouSureExpireThisAddress}", function(result){
				if(result){
					jQuery.ajax({
						type: "POST",
						url: "deleteContactMechAjax",
						data: value,
						beforeSend: function () {
							$("#checkoutInfoLoader").show();
						},
						success: function (data) {
							if (data._ERROR_MESSAGE_ != undefined) {
								bootbox.dialog("" + data._ERROR_MESSAGE_, [{
									"label" : "OK",
									"class" : "btn-small btn-primary",
									}]
								);
							} else {
								updateCheckoutArea();
							}
						},
						error: function () {
							//commit(false);
						},
						complete: function() {
					        $("#checkoutInfoLoader").hide();
					    }
					});
				}
			});
	    }
	}
//]]>
<#if isCreate>
	function onConfirm() {
		if(!$('#editcontactmechform').jqxValidator('validate')) return false;
		else {
			//$("#modal-table").modal("hide");
			$('#alterpopupWindow2').jqxWindow('close');
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result){
				if(result){
					var isSuccess = false;
					//var data = $("#editcontactmechform").serialize();
					//data += "&shipping_instructions=" + $("#shipping_instructions").val();
					var data = {};
					if ($("#new_DONE_PAGE").length > 0) data.DONE_PAGE = $("#new_DONE_PAGE").val();
					if ($("#new_contactMechTypeId").length > 0) data.contactMechTypeId = $("#new_contactMechTypeId").val();
					if ($("#new_partyId").length > 0) data.partyId = $("#new_partyId").val();
					if ($("#new_contactMechPurposeTypeId").length > 0) data.contactMechPurposeTypeId = $("#new_contactMechPurposeTypeId").val();
					if ($("#new_preContactMechTypeId").length > 0) data.preContactMechTypeId = $("#new_preContactMechTypeId").val();
					if ($("#new_paymentMethodId").length > 0) data.paymentMethodId = $("#new_paymentMethodId").val();
					if ($("#toName").length > 0) data.toName = $("#toName").val();
					if ($("#attnName").length > 0) data.attnName = $("#attnName").val();
					if ($("#countryGeoId").length > 0) data.countryGeoId = $("#countryGeoId").val();
					if ($("#stateProvinceGeoId").length > 0) data.stateProvinceGeoId = $("#stateProvinceGeoId").val();
					if ($("#districtGeoId").length > 0) data.districtGeoId = $("#districtGeoId").val();
					if ($("#wardGeoId").length > 0) data.wardGeoId = $("#wardGeoId").val();
					if ($("#address1").length > 0) data.address1 = $("#address1").val();
					if ($("#postalCode").length > 0) data.postalCode = $("#postalCode").val();
					if ($("#allowSolicitation").length > 0) data.allowSolicitation = $("#allowSolicitation").val();
					//if ($("#shipping_instructions").length > 0) data.shipping_instructions = $("#shipping_instructions").val();
					$.ajax({
			            type: "POST",                        
			            url: "createPostalAddressAndPurposePartyAjaxSales",
			            data: data,
			            beforeSend: function () {
							$("#checkoutInfoLoader").show();
						}, 
			            success: function (data) {
			            	if ($("#alterpopupWindow2").length > 0) {
								$("#alterpopupWindow2").jqxWindow('destroy');
							}
			            	$("#checkoutInfo").html(data);
			            	$("#checkoutInfoLoader").hide();
			            },
			            error: function () {
			                //commit(false);
			            }
			        });
				}
				// else {
				//	$("#modal-table").modal("show");
				//}
			});
		}
	}
$(function() {
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#alterpopupWindowEditContactMech").jqxWindow({width: 864, height:350, maxWidth:960, resizable: false, isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme});
	$('#alterpopupWindowEditContactMech').on('close', function (event) {
		updateCheckoutArea();
	});
	
	$("#alterpopupWindow2").jqxWindow({width: 480, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, theme:theme});
	//if ($("#alterCancel1").length > 0) {
	//	$("#alterCancel1").jqxButton({theme: theme});
	//}
	if ($("#alterSave1").length > 0) {
		//$("#alterSave1").jqxButton({theme: theme});
		$("#alterSave1").on('click', function () {
			onConfirm();
	    });
	}
    
    $("#btnNewShippingAddress").on('click', function () {
		$('#alterpopupWindow2').jqxWindow('open');
    });
    <#--
    {input: '#toName', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
    			function (input, commit) {
    				var value = $(input).val();
					if(!(/^\s*$/.test(value)) && !(/^(?:[\p{L}\p{Mn}\p{Pd}\'\x{2019}]+\s[\p{L}\p{Mn}\p{Pd}\'\x{2019}]+\s?)+$/.test(value))){
						return false;
					}
					return true;
				}
			},
			{input: '#attnName', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
    			function (input, commit) {
    				var value = $(input).val();
					if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9]+$/.test(value))){
						return false;
					}
					return true;
				}
			},
    -->
	$('#editcontactmechform').jqxValidator({
    	rules: [
			{input: '#postalCode', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
    			function (input, commit) {
    				var value = $(input).val();
					if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9-]+$/.test(value))){
						return false;
					}
					return true;
				}
			},
    		{input: '#address1', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
    			function (input, commit) {
    				var value = $(input).val();
					if(value == null || /^\s*$/.test(value)){
						return false;
					}
					return true;
				}
			},
			{input: '#postalCode', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
    			function (input, commit) {
    				var value = $(input).val();
					if(value == null || /^\s*$/.test(value)){
						return false;
					}
					return true;
				}
			}
    	]
    });
	<#--
	$('#editcontactmechform').validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: false,
		rules: {
			address1: {
				required: true
			}, 
			city: {
				required: true
			}, 
			postalCode: {
				required: true
			}
		},
		messages: {
			address1: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}, 
			city: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}, 
			postalCode: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}
		},
		invalidHandler: function (event, validator) { 
			//display error alert on form submit 
		},
		highlight: function (e) {$(e).closest('.control-group').removeClass('info').addClass('error');},

		success: function (e) {
			//$(e).closest('.control-group').removeClass('error').addClass('info');
			//$(e).remove();
		},
		submitHandler: function (form) {
		},
		invalidHandler: function (form) {
		}
	});
	-->
});
</#if>
</script>