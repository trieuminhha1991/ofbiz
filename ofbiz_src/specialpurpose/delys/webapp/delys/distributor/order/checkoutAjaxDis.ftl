<#if dependentForm?exists>
	<#--setDependentDropdownValuesJs.ftl-->
	<script type="text/javascript">
	jQuery(document).ready(function() {
	    if (jQuery('#${dependentForm}').length) {
	      jQuery("#${dependentForm}_${mainId}").change(function(e, data) {
	          getDependentDropdownValues('${requestName}', '${paramKey}', '${dependentForm}_${mainId}', '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}');
	      });
	      getDependentDropdownValues('${requestName}', '${paramKey}', '${dependentForm}_${mainId}', '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
	    }
	})
	</script>
</#if>
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
              	<span class="help-inline">
	          		<#--<a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');"><i class="icon-plus open-sans"></i>${uiLabelMap.DAAddNewAddress}</a>-->
	          		<a href="#modal-table" role="button" data-toggle="modal"><i class="fa fa-plus-circle"></i>${uiLabelMap.DACreateNewAddress}</a>
	          	</span>
          	<#else>
				<span>${uiLabelMap.DAHaveNotYetShippingAddress}</span>
           	</#if>
		</div>
	</div>
	<#-- Order Split Into Multiple Shipments ... -->
	<div class="control-group" style="margin-top:5px; position:relative">
		<div style="position:absolute; top:0; right:10px"><a href="javascript:void(0)" onclick="javascript:updateCheckoutArea();"><i class="fa fa-refresh"></i></a></div>
		<label class="control-label required">${uiLabelMap.DADestination}</label>
		<div class="controls" style="margin-top: -25px!important">
			<input type="hidden" value="${partyIdSelected?if_exists}" name="partyIdSelected" id="partyIdSelected"/>
			<#if shippingContactMechList?has_content>
				<#list shippingContactMechList as shippingContactMech>
					<#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
					<label style="width:220px; display:inline-block; text-align:left">
						<input type="radio" name="shipping_contact_mech_id" value="${shippingAddress.contactMechId}" onclick="javascript:submitForm(document.checkoutInfoForm, 'SA', null);" <#if shoppingCart?exists && shoppingCart.getShippingContactMechId()?default("") == shippingAddress.contactMechId> checked="checked"</#if>/>
						<span class="lbl">
							<address style="display:inline-block; margin-bottom:0; font-family: 'Open Sans'; font-size: 13px; color: #393939;">
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
						<a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');"><i class="fa fa-pencil-square"></i> ${uiLabelMap.CommonEdit}</a>
						<br/>
						<#--
						<form style="float:left;" name="partyDeleteContact" method="post" action="<@ofbizUrl>deleteContactMech</@ofbizUrl>" onsubmit="javascript:submitFormDisableSubmits(this)">
		                    <input name="partyId" value="${partyId}" type="hidden"/>
		                    <input name="contactMechId" value="${contactMech.contactMechId}" type="hidden"/>
		                    <button type="submit" class="btn btn-warning btn-mini icon-remove open-sans">
		                    ${uiLabelMap.CommonExpire} 
		                    </button>
	                  	</form>
						-->
						<a href="javascript:submitForm(document.checkoutInfoForm, 'DA', 'partyId=${partyIdSelected?if_exists}&contactMechId=${shippingAddress.contactMechId?if_exists}');"><i class="fa fa-times-circle open-sans open-sans-index"></i> ${uiLabelMap.CommonExpire}</a>
					</span>
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
<div id="modal-table" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.DACreateNewContact}
		</div>
	</div>

	<div class="modal-body">
		<#assign requestNameDis = ""/>
		<#if mechMap?exists && mechMap.contactMechTypeId?has_content && !mechMap.contactMech?has_content && "POSTAL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
			<#assign requestNameDis = mechMap.requestName + "PartyDis"/>
			<#-- basic-custom-form-->
			<form class="form-horizontal form-controls-margin-small form-size-mini" id="editcontactmechform" name="editcontactmechform" method="post" action="<@ofbizUrl>${requestNameDis}</@ofbizUrl>">
				<input type="hidden" name="DONE_PAGE" value="${donePage}" />
		        <input type="hidden" name="contactMechTypeId" value="${mechMap.contactMechTypeId}" />
		        <input type="hidden" name="partyId" value="${partyId}" />
		        <#if cmNewPurposeTypeId?has_content><input type="hidden" name="contactMechPurposeTypeId" value="${cmNewPurposeTypeId}" /></#if>
		        <#if preContactMechTypeId?exists><input type="hidden" name="preContactMechTypeId" value="${preContactMechTypeId}" /></#if>
		        <#if contactMechPurposeTypeId?exists><input type="hidden" name="contactMechPurposeTypeId" value="${contactMechPurposeTypeId?if_exists}" /></#if>
		        <#if paymentMethodId?has_content><input type='hidden' name='paymentMethodId' value='${paymentMethodId}' /></#if>
				<div class="row-fluid">
					<div class="span12">
						<div class="control-group info">
							<div class="controls" style="margin:-25px 10px 5px 0px !important">
								<#if contactMechPurposeType?exists>
							      	<span style="color: #657ba0; font-size:13px">(${uiLabelMap.DAMsgNewAddressHavePurpose} <b>"${contactMechPurposeType.get("description",locale)?if_exists}"</b>)</span>
							    </#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="toName">${uiLabelMap.DARecipientsName}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="toName" id="toName" class="span12 input-small" maxlength="100" value="${(mechMap.postalAddress.toName)?default(request.getParameter('toName')?if_exists)}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="attnName">${uiLabelMap.DAAttentionName}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="attnName" id="attnName" class="span12 input-small" maxlength="100" value="${(mechMap.postalAddress.attnName)?default(request.getParameter('attnName')?if_exists)}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="address1">${uiLabelMap.DAAddressLine1} *</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="address1" id="address1" class="span12" maxlength="255" value="${(mechMap.postalAddress.address1)?default(request.getParameter('address1')?if_exists)}">
								</div>
							</div>
						</div>
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
						<div class="control-group">
							<label class="control-label" for="postalCode">${uiLabelMap.DAZipCode} *</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="postalCode" id="postalCode" class="span12 input-small" maxlength="60" value="${(mechMap.postalAddress.postalCode)?default(request.getParameter('postalCode')?if_exists)}">
								</div>
							</div>
						</div>
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
								<div class="span12">
									<select name="allowSolicitation" id="allowSolicitation" class="span12 input-small">
							          	<#if (((mechMap.partyContactMech.allowSolicitation)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
								        <#if (((mechMap.partyContactMech.allowSolicitation)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
								        <option></option>
								        <option value="Y">${uiLabelMap.CommonY}</option>
								        <option value="N">${uiLabelMap.CommonN}</option>
							        </select>
								</div>
							</div>
						</div>
					</div><!--.span12-->
				</div><!--.row-fluid-->
			</form>
		</div>
	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>
			${uiLabelMap.CommonClose}
		</button>
		<div class="pagination pull-right no-margin">
			<button type="button" class="btn btn-small btn-primary pull-left" onClick="javascript:onConfirm();">
				<i class="icon-ok"></i>
				${uiLabelMap.CommonSave}
			</button>
		</div>
	</div>
		<#else>
	</div>
	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>
			Close
		</button>
		<div class="pagination pull-right no-margin">
		</div>
	</div>
		</#if>
</div>

<div id="modal-table-edit-contact-mech" class="modal hide fade jqx-window-olbius" tabindex="-1" style="width:960px; left:35%">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.PartyEditContactInformation}
		</div>
	</div>
	<div class="modal-body jqx-window-content" id="modal-body-edit-contact-mech">
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
<script type="text/javascript">
	function closePopupWindow() {
		if ($("#modal-table-edit-contact-mech") != undefined) {
			$("#modal-table-edit-contact-mech").modal('hide');
			updateCheckoutArea();
		}
	}
	
	function updateContactMech() {
		var data = $("#editcontactmechformUpdate").serialize();
        jQuery.ajax({
        	type: "POST",
        	url: "updatePostalAddressInfoAjax",
        	data: data,
        	success: function (data) {
        		$("#modal-body-edit-contact-mech").html(data);
        	},
        	error: function () {
        		//commit(false);
        	}
        });
	}
</script>
<script type="text/javascript">
	$('#modal-table').on('show.bs.modal', function (e) {
	  	setTimeout(function() {$("#percentNumber").focus()}, 1000);
	});
	function onConfirm() {
		if(!$('#editcontactmechform').valid()) return false;
		else {
			$("#modal-table").modal("hide");
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result){
				if(result){
					var isSuccess = false;
					var data = $("#editcontactmechform").serialize();
					data += "&shipping_instructions=" + $("#shipping_instructions").val();
					$.ajax({
			            type: "POST",                        
			            url: "createPostalAddressAndPurposePartyAjaxDis",
			            data: data,
			            beforeSend: function () {
							$("#checkoutInfoLoader").show();
						}, 
			            success: function (data) {
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
	        $("#modal-table-edit-contact-mech").modal("show");
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
$(function() {
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
});
</script>