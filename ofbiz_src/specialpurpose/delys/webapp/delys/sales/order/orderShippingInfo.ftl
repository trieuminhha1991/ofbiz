<#if shipGroups?has_content && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")>
<script language="JavaScript" type="text/javascript">
    function editInstruction(shipGroupSeqId) {
        jQuery('#shippingInstructions_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveInstruction_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#cancelEditInstruction_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#editInstruction_' + shipGroupSeqId).css({display:'none'});
        jQuery('#instruction_' + shipGroupSeqId).css({display:'none'});
    }
    function addInstruction(shipGroupSeqId) {
        jQuery('#shippingInstructions_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveInstruction_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#addInstruction_' + shipGroupSeqId).css({display:'none'});
    }
    function saveInstruction(shipGroupSeqId) {
    	$("#cancelEditInstruction_" + shipGroupSeqId).addClass("disabled");
        $("#saveInstruction_" + shipGroupSeqId).addClass("disabled");
    	var actionUrl = jQuery("#updateShippingInstructionsForm_" + shipGroupSeqId).attr("action");
    	actionUrl += "Ajax";
    	var data = jQuery("#updateShippingInstructionsForm_" + shipGroupSeqId).serialize();
    	$.ajax({
            type: "POST", 
            url: actionUrl,
            data: data,
            beforeSend: function () {
				$("#info_loader").show();
			}, 
            success: function (data) {
            	if (data.thisRequestUri == "json") {
            		var errorMessage = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>";
            		var isError = false;
			        if (data._ERROR_MESSAGE_LIST_ != null) {
			        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
			        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
			        		isError = true;
			        	}
			        }
			        if (data._ERROR_MESSAGE_ != null) {
			        	errorMessage = data._ERROR_MESSAGE_;
			        	isError = true;
			        }
			        errorMessage += "</span>";
			        if (isError) {
						bootbox.dialog(errorMessage, [{
							"label" : "OK",
							"class" : "btn-mini btn-primary width60px",
							}]
						);
			        } else {
			        	cancelEditInstruction(shipGroupSeqId);
			        	if (data.shippingInstructions != null) {
			        		var shippingInstructions = data.shippingInstructions;
			        		$("#displayShippingInstructions_" + shipGroupSeqId).css("display", "inline-block");
			        		$("#editInstruction_" + shipGroupSeqId).css("display", "inline-block");
			        		$("#addInstruction_" + shipGroupSeqId).css("display", "none");
			        		$("#displayShippingInstructions_" + shipGroupSeqId).html('<i class="icon-quote-left" style="color: #dce3ed"></i><span>' + shippingInstructions + '</span>');
			        	} else {
			        		$("#displayShippingInstructions_" + shipGroupSeqId).hide();
			        		$("#editInstruction_" + shipGroupSeqId).hide();
			        		$("#addInstruction_" + shipGroupSeqId).show();
			        		$("#displayShippingInstructions_" + shipGroupSeqId).html('<i class="icon-quote-left" style="color: #dce3ed"></i><span></span>');
			        	}
			        }
            	}
            },
            error: function () {},
            complete: function() {
		        $("#info_loader").hide();
		        $("#cancelEditInstruction_" + shipGroupSeqId).removeClass("disabled");
		        $("#saveInstruction_" + shipGroupSeqId).removeClass("disabled");
		    }
        });
        //jQuery("#updateShippingInstructionsForm_" + shipGroupSeqId).submit();
    }
    function editGiftMessage(shipGroupSeqId) {
        jQuery('#giftMessage_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveGiftMessage_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#cancelEditGiftMessage_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#editGiftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#message_' + shipGroupSeqId).css({display:'none'});
    }
    function addGiftMessage(shipGroupSeqId) {
        jQuery('#giftMessage_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveGiftMessage_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#addGiftMessage_' + shipGroupSeqId).css({display:'none'});
    }
    function saveGiftMessage(shipGroupSeqId) {
        //jQuery("#setGiftMessageForm_" + shipGroupSeqId).submit();
        
        $("#cancelEditGiftMessage_" + shipGroupSeqId).addClass("disabled");
        $("#saveGiftMessage_" + shipGroupSeqId).addClass("disabled");
    	var actionUrl = jQuery("#setGiftMessageForm_" + shipGroupSeqId).attr("action");
    	actionUrl += "Ajax";
    	var data = jQuery("#setGiftMessageForm_" + shipGroupSeqId).serialize();
    	$.ajax({
            type: "POST", 
            url: actionUrl,
            data: data,
            beforeSend: function () {
				$("#info_loader").show();
			}, 
            success: function (data) {
            	if (data.thisRequestUri == "json") {
            		var errorMessage = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>";
            		var isError = false;
			        if (data._ERROR_MESSAGE_LIST_ != null) {
			        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
			        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
			        		isError = true;
			        	}
			        }
			        if (data._ERROR_MESSAGE_ != null) {
			        	errorMessage = data._ERROR_MESSAGE_;
			        	isError = true;
			        }
			        errorMessage += "</span>";
			        if (isError) {
						bootbox.dialog(errorMessage, [{
							"label" : "OK",
							"class" : "btn-mini btn-primary width60px",
							}]
						);
			        } else {
			        	cancelEditGiftMessage(shipGroupSeqId);
			        	if (data.giftMessage != null) {
			        		var giftMessage = data.giftMessage;
			        		$("#displayGiftMessage_" + shipGroupSeqId).css("display", "inline-block");
			        		$("#editGiftMessage_" + shipGroupSeqId).css("display", "inline-block");
			        		$("#addGiftMessage_" + shipGroupSeqId).css("display", "none");
			        		$("#displayGiftMessage_" + shipGroupSeqId).html('<i class="icon-quote-left" style="color: #dce3ed"></i><span>' + giftMessage + '</span>');
			        	} else {
			        		$("#displayGiftMessage_" + shipGroupSeqId).hide();
			        		$("#editGiftMessage_" + shipGroupSeqId).hide();
			        		$("#addGiftMessage_" + shipGroupSeqId).show();
			        		$("#displayGiftMessage_" + shipGroupSeqId).html('<i class="icon-quote-left" style="color: #dce3ed"></i><span></span>');
			        	}
			        }
            	}
            },
            error: function () {},
            complete: function() {
		        $("#info_loader").hide();
		        $("#cancelEditGiftMessage_" + shipGroupSeqId).removeClass("disabled");
		        $("#saveGiftMessage_" + shipGroupSeqId).removeClass("disabled");
		    }
        });
    }
    function cancelEditInstruction(shipGroupSeqId) {
        jQuery('#shippingInstructions_' + shipGroupSeqId).css({display:'none'});
        jQuery('#saveInstruction_' + shipGroupSeqId).css({display:'none'});
        jQuery('#cancelEditInstruction_' + shipGroupSeqId).css({display:'none'});
        jQuery('#editInstruction_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#instruction_' + shipGroupSeqId).css({display:'block'});
    }
    function cancelEditGiftMessage(shipGroupSeqId) {
        jQuery('#giftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#saveGiftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#cancelEditGiftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#editGiftMessage_' + shipGroupSeqId).css({display:'inline-block'});
        jQuery('#message_' + shipGroupSeqId).css({display:'block'});
    }
</script>

<div id="shippinginfo-tab" class="tab-pane">
	<div class="row-fluid">
		<div class="span12">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.OrderShipmentInformation}
			</h4>
			<div style="position:relative">
				<div id="containerInner" style="background-color: transparent; overflow: auto;">
			    </div>
			    <div id="jqxNotificationInner">
			        <div id="notificationContentInner"></div>
			    </div>
			</div>
				<#list shipGroups as shipGroup>
				  	<#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType", false)?if_exists>
				  	<#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
				 	<div class="widget-box olbius-extra ">
				    	<div class="widget-header widget-header-small header-color-blue2">
				         	<h6>
				         		${uiLabelMap.DAGroup} - ${shipGroup.shipGroupSeqId} 
				         		<#--&nbsp;<a href="<@ofbizUrl>shipGroups.pdf</@ofbizUrl>?orderId=${orderId}&amp;shipGroupSeqId=${shipGroup.shipGroupSeqId}" target="_blank" data-rel="tooltip" title="${uiLabelMap.DAExportToPDF}" data-placement="bottom" style="color:#fff"><i class="fa-file-pdf-o"></i></a>-->
				         	</h6><#--OrderShipmentInformation-->
				         	<div class="widget-toolbar">
				        		<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
							</div>
				    	</div>
				    	<div class="widget-body" id="ShipGroupScreenletBody_${shipGroup.shipGroupSeqId}">
				    		<div class="widget-body-inner">
				    			<div class="widget-main">
				         			<!--<a class="btn btn-mini btn-primary" onclick="javascript:toggleScreenlet(this, 'ShipGroupScreenletBody_${shipGroup.shipGroupSeqId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Collapse"><i class="icon-chevron-up"></i></a>-->
				        			<form name="updateOrderItemShipGroup" method="post" action="updateOrderItemShipGroup">
								        <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
								        <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId?if_exists}"/>
								        <input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
								        <input type="hidden" name="oldContactMechId" value="${shipGroup.contactMechId?if_exists}"/>
								        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0'>
							                <tr>
							                    <td align="right" valign="top" width="20%">
							                        <span>${uiLabelMap.OrderAddress}</span>
							                    </td>
							                    <td valign="top" width="80%">
							                        <div>
							                            <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_REJECTED">
							                            	<select name="contactMechId" id="sboxContactMechId">
								                                <option selected="selected" value="${shipGroup.contactMechId?if_exists}">${(shipGroupAddress.address1)?default("")} - ${shipGroupAddress.city?default("")}</option>
								                                <#if shippingContactMechList?has_content>
								                                <option disabled="disabled" value=""></option>
								                                <#list shippingContactMechList as shippingContactMech>
								                                <#assign shippingPostalAddress = shippingContactMech.getRelatedOne("PostalAddress", false)?if_exists>
								                                <#if shippingContactMech.contactMechId?has_content>
								                                <option value="${shippingContactMech.contactMechId?if_exists}">${(shippingPostalAddress.address1)?default("")} - ${shippingPostalAddress.city?default("")}</option>
								                                </#if>
								                                </#list>
								                                </#if>
								                            </select>
							                            <#else>
							                            	${(shipGroupAddress.address1)?default("")}
							                            </#if>
							                            <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_REJECTED">
										                	<button type="submit" name="submitButton" data-rel="tooltip" title="${uiLabelMap.CommonUpdate}: ${uiLabelMap.OrderAddress}" data-placement="bottom" class="btn btn-mini btn-primary"><i class="fa-floppy-o"></i></button>
												        	<a id="newShippingAddress" href="javascript:void(0);" data-rel="tooltip" title="${uiLabelMap.DACreateNewShippingAddress}" data-placement="bottom" class="btn btn-mini btn-primary"><i class="fa-plus-circle"></i></a>
												        	<#--<script type="text/javascript">
									                            jQuery("#newShippingAddress").click(function(){jQuery("#newShippingAddressForm").dialog("open")});
									                        </script>-->
										                </#if>
							                        </div>
							                    </td>
							                </tr>
							
							                <#-- the setting of shipping method is only supported for sales orders at this time -->
							                <#--<#if orderHeader.orderTypeId == "SALES_ORDER">
							                  <tr>
							                    <td align="right" valign="top" width="15%">
							                        <span>&nbsp;<b>${uiLabelMap.CommonMethod}</b></span>
							                    </td>
							                    <td valign="top" width="80%">
							                        <div>
							                            <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_REJECTED">
							                            // passing the shipmentMethod value as the combination of three fields value
							                            //i.e shipmentMethodTypeId & carrierPartyId & roleTypeId. Values are separated by
							                            //"@" symbol.
							                            //>
							                            <select name="shipmentMethod">
							                                <#if shipGroup.shipmentMethodTypeId?has_content>
							                                <option value="${shipGroup.shipmentMethodTypeId}@${shipGroup.carrierPartyId!}@${shipGroup.carrierRoleTypeId!}"><#if shipGroup.carrierPartyId?exists && shipGroup.carrierPartyId != "_NA_">${shipGroup.carrierPartyId!}</#if>&nbsp;${shipmentMethodType.get("description",locale)!}</option>
							                                <#else>
							                                <option value=""/>
							                                </#if>
							                                <#list productStoreShipmentMethList as productStoreShipmentMethod>
							                                <#assign shipmentMethodTypeAndParty = productStoreShipmentMethod.shipmentMethodTypeId + "@" + productStoreShipmentMethod.partyId + "@" + productStoreShipmentMethod.roleTypeId>
							                                <#if productStoreShipmentMethod.partyId?has_content || productStoreShipmentMethod?has_content>
							                                <option value="${shipmentMethodTypeAndParty?if_exists}"><#if productStoreShipmentMethod.partyId != "_NA_">${productStoreShipmentMethod.partyId?if_exists}</#if>&nbsp;${productStoreShipmentMethod.get("description",locale)?default("")}</option>
							                                </#if>
							                                </#list>
							                            </select>
							                            <#else>
							                                <#if (shipGroup.carrierPartyId)?default("_NA_") != "_NA_">
							                                ${shipGroup.carrierPartyId?if_exists}
							                                </#if>
							                                <#if shipmentMethodType?has_content>
							                                    ${shipmentMethodType.get("description",locale)?default("")}
							                                </#if>
							                            </#if>
							                        </div>
							                    </td>
							                  </tr>
							                </#if>-->
							                <#if !shipGroup.contactMechId?has_content && !shipGroup.shipmentMethodTypeId?has_content>
							                <#assign noShipment = "true">
							                <tr>
							                    <td colspan="3" align="center">${uiLabelMap.OrderNotShipped}</td>
							                </tr>
							                </#if>
				      					</table>
				      				</form>
				      				
				      				<form id="addShippingAddress" name="addShippingAddress" method="post" action="addShippingAddress" class="form-horizontal form-table-block">
										<div id="alterpopupWindow2" style="display:none">
											<div>${uiLabelMap.DACreateNewContact}</div>
											<div class="form-horizontal form-table-block">
												<input type="hidden" name="orderId" id="new_orderId" value="${orderId?if_exists}"/>
									          	<input type="hidden" name="partyId" id="new_partyId" value="${partyId?if_exists}"/>
									          	<input type="hidden" name="oldContactMechId" id="new_oldContactMechId" value="${shipGroup.contactMechId?if_exists}"/>
									          	<input type="hidden" name="shipGroupSeqId" id="new_shipGroupSeqId" value="${shipGroup.shipGroupSeqId?if_exists}"/>
									          	<input type="hidden" name="contactMechPurposeTypeId" id="new_contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
										    	<div class="row-fluid">
										    		<div class="span12">
														<div class="control-group">
															<label class="control-label" for="countryGeoId">${uiLabelMap.CommonCountry}</label>
															<div class="controls">
																<select name="shipToCountryGeoId" id="countryGeoId">
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
																<select name="shipToStateProvinceGeoId" id="stateProvinceGeoId">
																</select>
															</div>
														</div>
														<div class="control-group">
															<label class="control-label" for="districtGeoId">${uiLabelMap.PartyDistrictGeoId}</label>
															<div class="controls">
																<select name="shipToDistrictGeoId" id="districtGeoId">
																</select>
															</div>
														</div>
														<div class="control-group">
															<label class="control-label" for="wardGeoId">${uiLabelMap.PartyWardGeoId}</label>
															<div class="controls">
																<select name="shipToWardGeoId" id="wardGeoId">
																</select>
															</div>
														</div>
														<div class="control-group">
															<label class="control-label required" for="address1">${uiLabelMap.DAAddress}</label>
															<div class="controls">
																<input type="text" name="shipToAddress1" id="address1" class="span12 input-normal" maxlength="255" value="">
															</div>
														</div>
														<div class="control-group">
															<label class="control-label required" for="postalCode">${uiLabelMap.DAZipCode}</label>
															<div class="controls">
																<input type="text" name="shipToPostalCode" id="postalCode" class="span12 input-normal" maxlength="60" value="${(mechMap.postalAddress.postalCode)?default(request.getParameter('postalCode')?if_exists)}">
															</div>
														</div>
										    		</div>
												</div><!--.row-fluid-->
										    	<div class="row-fluid">
										    		<div class="span12">
										    			<div class="control-group">
										    				<label class="control-label"></label>
										    				<div class="controls">
									    						<input type="button" id="alterSave1" value="${uiLabelMap.CommonSave}"/>
																<input type="button" id="alterCancel1" value="${uiLabelMap.CommonCancel}"/>
										    				</div>
										    			</div>
										    		</div>
										    	</div>
										    </div>
										</div>
									</form>
									<@jqGridMinimumLib />
									<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
				      				<script type="text/javascript">
										$(function(){
											$.jqx.theme = 'olbius';  
											theme = $.jqx.theme;
											$("#alterpopupWindow2").jqxWindow({width: 600, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, theme:theme});
											$("#alterCancel1").jqxButton({theme: theme});
											$("#alterSave1").jqxButton({theme: theme});
											$("#newShippingAddress").on('click', function () {
												$('#alterpopupWindow2').jqxWindow('open');
										    });
										    $("#jqxNotificationInner").on("click", function(){
										    	$("#jqxNotificationInner").hide();
										    });
											$("#alterSave1").on('click', function () {
												if(!$('#addShippingAddress').jqxValidator('validate')) return false;
												$('#alterpopupWindow2').jqxWindow('close');
												bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result){
													if(result){
														var isSuccess = false;
														var data = {};
														if ($("#new_orderId").length > 0) data.orderId = $("#new_orderId").val();
														if ($("#new_partyId").length > 0) data.partyId = $("#new_partyId").val();
														if ($("#new_oldContactMechId").length > 0) data.oldContactMechId = $("#new_oldContactMechId").val();
														if ($("#new_shipGroupSeqId").length > 0) data.shipGroupSeqId = $("#new_shipGroupSeqId").val();
														if ($("#new_contactMechPurposeTypeId").length > 0) data.contactMechPurposeTypeId = $("#new_contactMechPurposeTypeId").val();
														if ($("#countryGeoId").length > 0) data.shipToCountryGeoId = $("#countryGeoId").val();
														if ($("#stateProvinceGeoId").length > 0) data.shipToStateProvinceGeoId = $("#stateProvinceGeoId").val();
														if ($("#districtGeoId").length > 0) data.shipToDistrictGeoId = $("#districtGeoId").val();
														if ($("#wardGeoId").length > 0) data.shipToWardGeoId = $("#wardGeoId").val();
														if ($("#address1").length > 0) data.shipToAddress1 = $("#address1").val();
														if ($("#postalCode").length > 0) data.shipToPostalCode = $("#postalCode").val();
														$.ajax({
												            type: "POST",                        
												            url: "addShippingAddressAjax",
												            data: data,
												            beforeSend: function () {
																$("#info_loader").show();
															}, 
												            success: function (data) {
												            	if (data.thisRequestUri == "json") {
												            		var errorMessage = "";
															        if (data._ERROR_MESSAGE_LIST_ != null) {
															        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
															        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
															        	}
															        }
															        if (data._ERROR_MESSAGE_ != null) {
															        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
															        }
															        if (errorMessage != "") {
															        	$('#containerInner').empty();
															        	$('#jqxNotificationInner').jqxNotification({ template: 'error'});
															        	$("#jqxNotificationInner").html(errorMessage);
															        	$("#jqxNotificationInner").show();
															        } else {
															        	$('#containerInner').empty();
															        	$('#jqxNotificationInner').jqxNotification({ template: 'info'});
															        	$("#jqxNotificationInner").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
															        	//$("#jqxNotificationInner").jqxNotification("open");
															        	$("#jqxNotificationInner").show();
															        	
															        	$("#sboxContactMechId").empty();
															        	var contactMechId = data.contactMechId;
															        	var shippingContactMechList = data.shippingContactMechList;
															        	if (shippingContactMechList != null && contactMechId != null) {
															        		for (var i = 0; i < shippingContactMechList.length; i++) {
															        			var item = shippingContactMechList[i];
															        			var content = "<option value='" + item.contactMechId + "' ";
															        			if (contactMechId == item.contactMechId) {
															        				content += "selected='selected' ";
															        			}
															        			content += ">" + item.address1 + " - " + item.city + "</option>";
															        			$("#sboxContactMechId").append(content);
															        		}
															        	}
															        }
															        return false;
												            	}
												            },
												            error: function () {
												                //commit(false);
												            },
												            complete: function() {
														        $("#info_loader").hide();
														    }
												        });
													}
												});
										    });
										    $('#addShippingAddress').jqxValidator({
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
										});
				      				</script>
				      				<#--
				      				<div id="newShippingAddressForm" class="popup" style="display: none;">
								        <form id="addShippingAddress" name="addShippingAddress" method="post" action="addShippingAddress">
								          	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
								          	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
								          	<input type="hidden" name="oldContactMechId" value="${shipGroup.contactMechId?if_exists}"/>
								          	<input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId?if_exists}"/>
								          	<input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
								          	<div class="form-row">
									            <label for="address1">${uiLabelMap.PartyAddressLine1}* <span id="advice-required-address1" style="display: none" class="custom-advice">(required)</span></label>
									            <div class="form-field"><input type="text" class="required" name="shipToAddress1" id="address1" value="" size="30" maxlength="30" /></div>
								          	</div>
								          	<div class="form-row">
									            <label for="address2">${uiLabelMap.PartyAddressLine2}</label>
									            <div class="form-field"><input type="text" name="shipToAddress2" id="address2" value="" size="30" maxlength="30" /></div>
								          	</div>
								          	<div class="form-row">
									            <label for="city">${uiLabelMap.PartyCity}* <span id="advice-required-city" style="display: none" class="custom-advice">(required)</span></label>
									            <div class="form-field"><input type="text" class="required" name="shipToCity" id="city" value="" size="30" maxlength="30" /></div>
								          	</div>
									          <div class="form-row">
									            <label for="postalCode">${uiLabelMap.PartyZipCode}* <span id="advice-required-postalCode" style="display: none" class="custom-advice">(required)</span></label>
									            <div class="form-field"><input type="text" class="required number" name="shipToPostalCode" id="postalCode" value="" size="30" maxlength="10" /></div>
									          </div>
									          <div class="form-row">
									            <label for="countryGeoId">${uiLabelMap.CommonCountry}* <span id="advice-required-countryGeoId" style="display: none" class="custom-advice">(required)</span></label>
									            <div class="form-field">
									              <select name="shipToCountryGeoId" id="countryGeoId" class="required" style="width: 70%">
									                <#if countryGeoId?exists>
									                  <option value="${countryGeoId}">${countryGeoId}</option>
									                </#if>
									                ${screens.render("component://common/widget/CommonScreens.xml#countries")}
									              </select>
									            </div>
									          </div>
									          <div class="form-row">
									            <label for="stateProvinceGeoId">${uiLabelMap.PartyState}* <span id="advice-required-stateProvinceGeoId" style="display: none" class="custom-advice">(required)</span></label>
									            <div class="form-field">
									              <select name="shipToStateProvinceGeoId" id="stateProvinceGeoId" style="width: 70%">
									                <#if stateProvinceGeoId?has_content>
									                  <option value="${stateProvinceGeoId}">${stateProvinceGeoId}</option>
									                <#else>
									                  <option value="_NA_">${uiLabelMap.PartyNoState}</option>
									                </#if>
									              </select>
									            </div>
									          </div>
									          <div class="form-row">
									            <input id="submitAddShippingAddress" type="button" value="${uiLabelMap.CommonSubmit}" style="display:none"/>
									            <form action="">
									              <input class="btn btn-small btn-primary" type="button" value="${uiLabelMap.CommonClose}" style="display:none"/>
									            </form>
									          </div>
								        </form>
				      				</div>
							      	<script language="JavaScript" type="text/javascript">
								       	jQuery(document).ready( function() {
									        jQuery("#newShippingAddressForm").dialog({autoOpen: false, modal: true,
								                buttons: {
									                'Submit': function() {
									                    var addShippingAddress = jQuery("#addShippingAddress");
									                    jQuery("<p>${uiLabelMap.CommonUpdatingData}</p>").insertBefore(addShippingAddress);
									                    addShippingAddress.submit();
									                },
									                'Close': function() {
									                    jQuery(this).dialog('close');
									               	}
								                }
							                });
								       	});
							      	</script>
				      				-->
				      				
							      	<table width="100%" border="0" cellpadding="1" cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
								        <#if shipGroup.supplierPartyId?has_content>
								          <#assign supplier =  delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.supplierPartyId), false)?if_exists />
								          <tr>
								            <td align="right" valign="top" width="20%">
								              <span>${uiLabelMap.ProductDropShipment} - ${uiLabelMap.PartySupplier}</span>
								            </td>
								            <td valign="top" width="80%">
								              <#if supplier?has_content> - ${supplier.description?default(shipGroup.supplierPartyId)}</#if>
								            </td>
								          </tr>
								        </#if>
								
								        <#-- This section appears when Shipment of order is in picked status and its items are packed,this case comes when new shipping estimates based on weight of packages are more than or less than default percentage (defined in shipment.properties) of original shipping estimate-->
								        <#-- getShipGroupEstimate method of ShippingEvents class can be used for get shipping estimate from system, on the basis of new package's weight -->
								        <#if shippingRateList?has_content>
								          <#if orderReadHelper.getOrderTypeId() != "PURCHASE_ORDER">
								            <tr>
								              <td colspan="2">
								                <table>
								                  <tr>
								                    <td>
								                      <span>&nbsp;${uiLabelMap.OrderOnlineUPSShippingEstimates}</span>
								                    </td>
								                  </tr>
								                  <form name="UpdateShippingMethod" method="post" action="updateShippingMethodAndCharges">
								                    <#list shippingRateList as shippingRate>
								                      <tr>
								                        <td>
								                          <#assign shipmentMethodAndAmount = shippingRate.shipmentMethodTypeId + "@" + "UPS" + "*" + shippingRate.rate>
								                          <label>
															<input type='radio' name='shipmentMethodAndAmount' value='${shipmentMethodAndAmount?if_exists}' /><span class="lbl">${shippingRate.shipmentMethodDescription?if_exists}</span>
														  </label>
								                          <#if (shippingRate.rate > -1)>
								                            <@ofbizCurrency amount=shippingRate.rate isoCode=orderReadHelper.getCurrency()/>
								                          <#else>
								                            ${uiLabelMap.OrderCalculatedOffline}
								                          </#if>
								                        </td>
								                      </tr>
								                    </#list>
								                    <input type="hidden" name="shipmentRouteSegmentId" value="${shipmentRouteSegmentId?if_exists}"/>
								                    <input type="hidden" name="shipmentId" value="${pickedShipmentId?if_exists}"/>
								                    <input type="hidden" name="orderAdjustmentId" value="${orderAdjustmentId?if_exists}"/>
								                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
								                    <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId?if_exists}"/>
								                    <input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
								                    <input type="hidden" name="oldContactMechId" value="${shipGroup.contactMechId?if_exists}"/>
								                    <input type="hidden" name="shippingAmount" value="${shippingAmount?if_exists}"/>
								                    <tr>
								                      <td valign="top" width="80%">
								                        <button type="submit" class="btn btn-primary btn-small" name="submitButton"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
								                      </td>
								                    </tr>
								                  </form>
								                </table>
								              </td>
								            </tr>
								          </#if>
								        </#if>
								
								        <#-- tracking number -->
								        <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
								          <tr>
								            <td align="right" valign="top" width="20%">
								              <span>${uiLabelMap.OrderTrackingNumber}</span>
								            </td>
								            <td valign="top" width="80%">
								              <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
								              <#if shipGroup.trackingNumber?has_content>
								                ${shipGroup.trackingNumber}
								              </#if>
								              <#if orderShipmentInfoSummaryList?has_content>
								                <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
								                  <#if orderShipmentInfoSummary.shipGroupSeqId?if_exists == shipGroup.shipGroupSeqId?if_exists>
								                    <div>
								                      <#if (orderShipmentInfoSummaryList?size > 1)>${orderShipmentInfoSummary.shipmentPackageSeqId}: </#if>
								                      ${uiLabelMap.CommonIdCode}: ${orderShipmentInfoSummary.trackingCode?default("[${uiLabelMap.OrderNotYetKnown}]")}
								                      <#if orderShipmentInfoSummary.boxNumber?has_content> ${uiLabelMap.ProductBox} #${orderShipmentInfoSummary.boxNumber}</#if>
								                      <#if orderShipmentInfoSummary.carrierPartyId?has_content>(${uiLabelMap.ProductCarrier}: ${orderShipmentInfoSummary.carrierPartyId})</#if>
								                    </div>
								                  </#if>
								                </#list>
								              </#if>
								            </td>
								          </tr>
								        </#if>
								        <#if shipGroup.maySplit?has_content && noShipment?default("false") != "true">
								          <tr>
								            <td align="right" valign="top" width="20%">
								              <span>${uiLabelMap.OrderSplittingPreference}</span>
								            </td>
								            <td valign="top" width="80%">
								              <div>
								                <#if shipGroup.maySplit?upper_case == "N">
								                    ${uiLabelMap.FacilityWaitEntireOrderReady}
								                    <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
								                      <#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED">
								                      	<#if isLog>
									                        <form name="allowordersplit_${shipGroup.shipGroupSeqId}" method="post" action="allowOrderSplit">
									                          <input type="hidden" name="orderId" value="${orderId}"/>
									                          <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
									                        </form>
									                        <a href="javascript:document.allowordersplit_${shipGroup.shipGroupSeqId}.submit()">${uiLabelMap.OrderAllowSplit}</a>
								                      	</#if>
								                      </#if>
								                    </#if>
								                <#else>
								                    ${uiLabelMap.FacilityShipAvailable}
								                </#if>
								              </div>
								            </td>
								          </tr>
								        </#if>
								        <tr>
								          <td align="right" valign="top" width="20%">
								            <span>${uiLabelMap.OrderInstructions}</span>
								          </td>
								          <td align="left" valign="top" width="80%">
								            <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
								              <form id="updateShippingInstructionsForm_${shipGroup.shipGroupSeqId}" name="updateShippingInstructionsForm" method="post" action="setShippingInstructionsSales">
								                <input type="hidden" name="orderId" value="${orderHeader.orderId}"/>
								                <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
								                <#--<#if shipGroup.shippingInstructions?has_content>-->
								                <div id="displayShippingInstructions_${shipGroup.shipGroupSeqId}" <#if shipGroup.shippingInstructions?has_content>style="display:inline-block"<#else>style="display:none"</#if>>
								                	<i class="icon-quote-left" style="color: #dce3ed"></i>
							                  		<span>${shipGroup.shippingInstructions?if_exists}</span>&nbsp;&nbsp;
								                </div>
							                  	<a href="javascript:editInstruction('${shipGroup.shipGroupSeqId}');" id="editInstruction_${shipGroup.shipGroupSeqId}" <#if !shipGroup.shippingInstructions?has_content>style="display:none"</#if>><i class="fa fa-pencil-square"></i></a>
							                  	<a href="javascript:addInstruction('${shipGroup.shipGroupSeqId}');" id="addInstruction_${shipGroup.shipGroupSeqId}" <#if shipGroup.shippingInstructions?has_content>style="display:none"</#if>><i class="fa fa-plus-circle"></i></a>
							                  	
								                <a href="javascript:saveInstruction('${shipGroup.shipGroupSeqId}');" id="saveInstruction_${shipGroup.shipGroupSeqId}" style="display:none" data-rel="tooltip" title="${uiLabelMap.CommonSave}" data-placement="top" class="btn btn-mini btn-info"><i class="fa-floppy-o"></i></a>
								                <a href="javascript:cancelEditInstruction('${shipGroup.shipGroupSeqId}');" id="cancelEditInstruction_${shipGroup.shipGroupSeqId}" style="display:none;margin-left:2px" data-rel="tooltip" title="${uiLabelMap.DACancel}" data-placement="top" class="btn btn-mini"><i class="icon-remove"></i></a>
								                <textarea name="shippingInstructions" id="shippingInstructions_${shipGroup.shipGroupSeqId}" style="display:none" rows="0" cols="0">${shipGroup.shippingInstructions?if_exists}</textarea>
								              </form>
								            <#else>
								              <#if shipGroup.shippingInstructions?has_content>
								                <span>${shipGroup.shippingInstructions}</span>
								              <#else>
								                <span>${uiLabelMap.OrderThisOrderDoesNotHaveShippingInstructions}</span>
								              </#if>
								            </#if>
								          </td>
								        </tr>
								
								        <#if shipGroup.isGift?has_content && noShipment?default("false") != "true">
								        <tr>
								          <td align="right" valign="top" width="20%">
								            <span>${uiLabelMap.OrderGiftMessage}</span>
								          </td>
								          <td>
								            <form id="setGiftMessageForm_${shipGroup.shipGroupSeqId}" name="setGiftMessageForm" method="post" action="setGiftMessageSales">
								              	<input type="hidden" name="orderId" value="${orderHeader.orderId}"/>
								              	<input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
								              	<div id="displayGiftMessage_${shipGroup.shipGroupSeqId}" <#if shipGroup.giftMessage?has_content>style="display:inline-block"<#else>style="display:none"</#if>>
								              		<i class="icon-quote-left" style="color: #dce3ed"></i>
								                	<span>${shipGroup.giftMessage?if_exists}</span>
								              	</div>
							                	<a href="javascript:editGiftMessage('${shipGroup.shipGroupSeqId}');" id="editGiftMessage_${shipGroup.shipGroupSeqId}" <#if !shipGroup.giftMessage?has_content>style="display:none"</#if>><i class="fa fa-pencil-square"></i></a>
							                	<a href="javascript:addGiftMessage('${shipGroup.shipGroupSeqId}');" id="addGiftMessage_${shipGroup.shipGroupSeqId}" <#if shipGroup.giftMessage?has_content>style="display:none"</#if>><i class="fa fa-plus-circle"></i></a>
								              	
								              	<a href="javascript:saveGiftMessage('${shipGroup.shipGroupSeqId}');" id="saveGiftMessage_${shipGroup.shipGroupSeqId}" style="display:none" data-rel="tooltip" title="${uiLabelMap.CommonSave}" data-placement="top" class="btn btn-mini btn-info"><i class="fa-floppy-o"></i></a>
								              	<a href="javascript:cancelEditGiftMessage('${shipGroup.shipGroupSeqId}');" id="cancelEditGiftMessage_${shipGroup.shipGroupSeqId}" style="display:none;margin-left:2px" data-rel="tooltip" title="${uiLabelMap.DACancel}" data-placement="top" class="btn btn-mini"><i class="icon-remove"></i></a>
								              	<textarea name="giftMessage" id="giftMessage_${shipGroup.shipGroupSeqId}" style="display:none" rows="0" cols="0">${shipGroup.giftMessage?if_exists}</textarea>
								            </form>
								          </td>
								        </tr>
								        </#if>
								         
								         <tr>
								            <td align="right" valign="top" width="20%">
								              <span style="line-height:30px">${uiLabelMap.DADeliveryDate}</span><br/>
								              <span style="line-height:30px">${uiLabelMap.OrderShipAfterDate}</span><br/>
								              <span style="line-height:30px">${uiLabelMap.OrderShipBeforeDate}</span>
								            </td>
								            <td valign="top" width="80%">
								              <form name="setShipGroupDates_${shipGroup.shipGroupSeqId}" method="post" action="updateOrderItemShipGroup">
								                <input type="hidden" name="orderId" value="${orderHeader.orderId}"/>
								                <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
								                <@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" id="estimatedDeliveryDate" event="" action="" value="${shipGroup.estimatedDeliveryDate?if_exists}" 
								                	className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
								                	timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
								                	hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								                <br />
								                <@htmlTemplate.renderDateTimeField name="shipAfterDate" event="" action="" value="${shipGroup.shipAfterDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="shipAfterDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								                <br/>
								                <@htmlTemplate.renderDateTimeField name="shipByDate" event="" action="" value="${shipGroup.shipByDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="shipByDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
								                <button type="submit" name="submitButton" data-rel="tooltip" title="${uiLabelMap.CommonUpdate}: ${uiLabelMap.DADeliveryDate}, ${uiLabelMap.OrderShipAfterDate}, ${uiLabelMap.OrderShipBeforeDate}" data-placement="bottom" class="btn btn-mini btn-primary"><i class="fa-floppy-o"></i></button>
								              </form>
								          </td>
								       </tr>
								       <#assign shipGroupShipments = shipGroup.getRelated("PrimaryShipment", null, null, false)>
								       <#if shipGroupShipments?has_content>
								          	<tr>
								            	<td align="right" valign="top" width="20%">
								              		<span>${uiLabelMap.FacilityShipments}</span>
								            	</td>
								            	<td valign="top" width="80%">
									                <#list shipGroupShipments as shipment>
									                    <div>
									                      	${uiLabelMap.CommonNbr}&nbsp;
									                      	<a href="<@ofbizUrl>viewShipment</@ofbizUrl>?shipmentId=${shipment.shipmentId}${externalKeyParam}" target="_blank">${shipment.shipmentId}</a>&nbsp;&nbsp;
							                      			<a target="_blank" href="<@ofbizUrl>PackingSlip.pdf</@ofbizUrl>?shipmentId=${shipment.shipmentId}${externalKeyParam}" class="btn btn-mini btn-primary" style="margin-top:5px">
									                      		<i class="icon-print"></i>${uiLabelMap.DAPrintPackingSlipPdf}
								                      		</a>
									                      	<#if "SALES_ORDER" == orderHeader.orderTypeId && "ORDER_COMPLETED" == orderHeader.statusId>
									                        	<#assign shipmentRouteSegments = delegator.findByAnd("ShipmentRouteSegment", {"shipmentId" : shipment.shipmentId}, null, false)>
									                        	<#if shipmentRouteSegments?has_content>
									                          		<#assign shipmentRouteSegment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentRouteSegments)>
								                          			<#if "UPS" == (shipmentRouteSegment.carrierPartyId)?if_exists>
									                            		<a href="javascript:document.upsEmailReturnLabel${shipment_index}.submit();" class="btn btn-mini btn-primary">${uiLabelMap.ProductEmailReturnShippingLabelUPS}</a>
									                          		</#if>
									                          		<form name="upsEmailReturnLabel${shipment_index}" method="post" action="<@ofbizUrl>upsEmailReturnLabelOrder</@ofbizUrl>">
									                            		<input type="hidden" name="orderId" value="${orderId}"/>
									                            		<input type="hidden" name="shipmentId" value="${shipment.shipmentId}"/>
									                            		<input type="hidden" name="shipmentRouteSegmentId" value="${shipmentRouteSegment.shipmentRouteSegmentId}" />
									                          		</form>
									                        	</#if>
									                      	</#if>
									                    </div>
									                </#list>
								            	</td>
								          	</tr>
								       </#if>
								
								       <#-- shipment actions -->
								       <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && ((orderHeader.statusId == "ORDER_CREATED") || (orderHeader.statusId == "ORDER_APPROVED") || (orderHeader.statusId == "ORDER_SENT"))>
								         	<#-- Manual shipment options -->
								         	<tr>
								            	<td colspan="3" valign="top" width="100%" align="center">
								             		<#if orderHeader.orderTypeId == "SALES_ORDER">
								               			<#if !shipGroup.supplierPartyId?has_content>
										                 	<#if orderHeader.statusId == "ORDER_APPROVED">
										                 		<a href="<@ofbizUrl>packOrder</@ofbizUrl>?facilityId=${storeFacilityId?if_exists}&amp;orderId=${orderId}&amp;shipGroupSeqId=${shipGroup.shipGroupSeqId}${externalKeyParam}" 
										                 		class="btn btn-mini btn-primary" target="_blank">${uiLabelMap.OrderPackShipmentForShipGroup}</a>
										                 		<br /><br />
										                 	</#if>
										                 	<#if isLog>
									                 			<a href="javascript:document.createShipment_${shipGroup.shipGroupSeqId}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.DANewShipmentForShipGroup}</a>
									                 			<form name="createShipment_${shipGroup.shipGroupSeqId}" method="post" action="<@ofbizUrl>createShipment</@ofbizUrl>">
									                   				<input type="hidden" name="primaryOrderId" value="${orderId}"/>
									                   				<input type="hidden" name="primaryShipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
									                   				<input type="hidden" name="statusId" value="SHIPMENT_INPUT" />
									                   				<input type="hidden" name="facilityId" value="${storeFacilityId?if_exists}" />
									                   				<input type="hidden" name="estimatedShipDate" value="${shipGroup.shipByDate?if_exists}"/>
									                 			</form>
								                 			</#if>
								               			</#if>
								             		<#else>
								               			<#assign facilities = facilitiesForShipGroup.get(shipGroup.shipGroupSeqId)>
								               			<#if facilities?has_content>
								                   			<div>
								                    			<form name="createShipment2_${shipGroup.shipGroupSeqId}" method="post" action="<@ofbizUrl>createShipment</@ofbizUrl>">
											                       	<input type="hidden" name="primaryOrderId" value="${orderId}"/>
											                       	<input type="hidden" name="primaryShipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
											                       	<input type="hidden" name="shipmentTypeId" value="PURCHASE_SHIPMENT"/>
											                       	<input type="hidden" name="statusId" value="PURCH_SHIP_CREATED"/>
											                       	<input type="hidden" name="externalLoginKey" value="${externalLoginKey}"/>
											                       	<input type="hidden" name="estimatedShipDate" value="${shipGroup.estimatedShipDate?if_exists}"/>
											                       	<input type="hidden" name="estimatedArrivalDate" value="${shipGroup.estimatedDeliveryDate?if_exists}"/>
											                       	<select name="destinationFacilityId" class="margin-top10">
											                         	<#list facilities as facility>
											                           		<option value="${facility.facilityId}">${facility.facilityName}</option>
											                         	</#list>
											                       	</select>
											                       	<button type="submit" class="btn btn-small btn-primary" >
											                       		${uiLabelMap.DANewShipmentForShipGroup} [${shipGroup.shipGroupSeqId}]
											                       	</button>
											                    </form>
								                    		</div>
								               			<#else>
								                   			<a href="javascript:document.quickDropShipOrder_${shipGroup_index}.submit();" class="btn btn-mini btn-primary">${uiLabelMap.ProductShipmentQuickComplete}</a>
								                   			<a href="javascript:document.createShipment3_${shipGroup.shipGroupSeqId}.submit();" class="btn btn-mini btn-primary">${uiLabelMap.OrderNewDropShipmentForShipGroup} [${shipGroup.shipGroupSeqId}]</a>
										                   	<form name="quickDropShipOrder_${shipGroup_index}" method="post" action="<@ofbizUrl>quickDropShipOrder</@ofbizUrl>">
										                        <input type="hidden" name="orderId" value="${orderId}"/>
										                        <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
										                        <input type="hidden" name="externalLoginKey" value="${externalLoginKey}" />
										                    </form>
										                    <form name="createShipment3_${shipGroup.shipGroupSeqId}" method="post" action="<@ofbizUrl>createShipment</@ofbizUrl>">
										                        <input type="hidden" name="primaryOrderId" value="${orderId}"/>
										                        <input type="hidden" name="primaryShipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
										                        <input type="hidden" name="shipmentTypeId" value="DROP_SHIPMENT" />
										                        <input type="hidden" name="statusId" value="PURCH_SHIP_CREATED" />
										                        <input type="hidden" name="externalLoginKey" value="${externalLoginKey}" />
										                    </form>
								               			</#if>
								             		</#if>
								           	 	</td>
								         	</tr>
								       </#if>
				      				</table>
				    			</div>
				    		</div>
				    	</div>
					</div>
				</#list>
		</div><!--.span12-->
	</div><!--.row-fluid-->
</div><!--#shippinginfo-tab-->
<script type="text/javascript">
	$('[data-rel=tooltip]').tooltip();
</script>
</#if>