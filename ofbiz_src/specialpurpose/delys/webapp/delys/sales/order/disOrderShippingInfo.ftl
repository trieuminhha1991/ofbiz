<script language="JavaScript" type="text/javascript">
    function editInstruction(shipGroupSeqId) {
        jQuery('#shippingInstructions_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveInstruction_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#cancelEditInstruction_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#editInstruction_' + shipGroupSeqId).css({display:'none'});
        jQuery('#instruction_' + shipGroupSeqId).css({display:'none'});
    }
    function addInstruction(shipGroupSeqId) {
        jQuery('#shippingInstructions_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveInstruction_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#addInstruction_' + shipGroupSeqId).css({display:'none'});
    }
    function saveInstruction(shipGroupSeqId) {
        jQuery("#updateShippingInstructionsForm_" + shipGroupSeqId).submit();
    }
    function editGiftMessage(shipGroupSeqId) {
        jQuery('#giftMessage_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveGiftMessage_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#cancelEditGiftMessage_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#editGiftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#message_' + shipGroupSeqId).css({display:'none'});
    }
    function addGiftMessage(shipGroupSeqId) {
        jQuery('#giftMessage_' + shipGroupSeqId).css({display:'block'});
        jQuery('#saveGiftMessage_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#addGiftMessage_' + shipGroupSeqId).css({display:'none'});
    }
    function saveGiftMessage(shipGroupSeqId) {
        jQuery("#setGiftMessageForm_" + shipGroupSeqId).submit();
    }
    function cancelEditInstruction(shipGroupSeqId) {
        jQuery('#shippingInstructions_' + shipGroupSeqId).css({display:'none'});
        jQuery('#saveInstruction_' + shipGroupSeqId).css({display:'none'});
        jQuery('#cancelEditInstruction_' + shipGroupSeqId).css({display:'none'});
        jQuery('#editInstruction_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#instruction_' + shipGroupSeqId).css({display:'block'});
    }
    function cancelEditGiftMessage(shipGroupSeqId) {
        jQuery('#giftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#saveGiftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#cancelEditGiftMessage_' + shipGroupSeqId).css({display:'none'});
        jQuery('#editGiftMessage_' + shipGroupSeqId).css({display:'inline'});
        jQuery('#message_' + shipGroupSeqId).css({display:'block'});
    }
</script>

<div id="shippinginfo-tab" class="tab-pane">
	<div class="row-fluid">
		<div class="span8">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.OrderShipmentInformation}
			</h4>
			<#if shipGroups?has_content && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")>
				<table class="table table-striped table-bordered dataTable">
			  		<tbody>
			  			<tr>
							<#list shipGroups as shipGroup>
							  	<#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType", false)?if_exists>
							  	<#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress", false)?if_exists>
				  				<td>
				  					${uiLabelMap.DAGroup} ${shipGroup.shipGroupSeqId}
				  					&nbsp;<a href="<@ofbizUrl>shipGroups.pdf</@ofbizUrl>?orderId=${orderId}&amp;shipGroupSeqId=${shipGroup.shipGroupSeqId}" target="_blank" data-rel="tooltip" title="${uiLabelMap.DAExportToPDF}" data-placement="bottom"><i class="fa-file-pdf-o"></i></a>
				  				</td>
				  				<td>
				         			<div class="form-horizontal basic-custom-form">
				         				<!--<a class="btn btn-mini btn-primary" onclick="javascript:toggleScreenlet(this, 'ShipGroupScreenletBody_${shipGroup.shipGroupSeqId}', 'true', '${uiLabelMap.CommonExpand}', '${uiLabelMap.CommonCollapse}');" title="Collapse"><i class="icon-chevron-up"></i></a>-->
					         			<form name="updateOrderItemShipGroup" method="post" action="updateOrderItemShipGroup">
									        <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
									        <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId?if_exists}"/>
									        <input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
									        <input type="hidden" name="oldContactMechId" value="${shipGroup.contactMechId?if_exists}"/>
									        
									        <div class="row-fluid">
									        	<div class="span12">
									        		<div class="control-group">
									        			<label class="control-label">${uiLabelMap.OrderAddress}</label>
									        			<div class="controls">
									        				<#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_REJECTED">
									                            <select name="contactMechId" style="margin-bottom:0">
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
													        	<script type="text/javascript">
										                            jQuery("#newShippingAddress").click(function(){jQuery("#newShippingAddressForm").dialog("open")});
										                        </script>
											                </#if>
									        			</div>
									        			<#-- the setting of shipping method is only supported for sales orders at this time -->
										                <#--
										                <#if orderHeader.orderTypeId == "SALES_ORDER">
										        			<div class="control-group">
										        				<label class="control-label">${uiLabelMap.CommonMethod}</lable>
										        				<div class="controls">
										        					<#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_REJECTED">
											                            //passing the shipmentMethod value as the combination of three fields value
											                            //i.e shipmentMethodTypeId & carrierPartyId & roleTypeId. Values are separated by
											                            //"@" symbol.
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
										        			</div>
									        			</#if>
										                -->
										                
										                <#if !shipGroup.contactMechId?has_content && !shipGroup.shipmentMethodTypeId?has_content>
										                	<#assign noShipment = "true">
										                	<div class="control-group">
												        		<span>${uiLabelMap.OrderNotShipped}</span>
												        	</div>
										                </#if>
									        		</div>
									        	</div>
									        </div>
					      				</form>
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
					      				<div class="row-fluid">
								        	<div class="span12">
								        		<#if shipGroup.supplierPartyId?has_content>
										          	<#assign supplier =  delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.supplierPartyId), false)?if_exists />
										          	<div class="control-group">
									        			<label class="control-label">${uiLabelMap.ProductDropShipment} - ${uiLabelMap.PartySupplier}</label>
									        			<div class="controls">
									        				<#if supplier?has_content> - ${supplier.description?default(shipGroup.supplierPartyId)}</#if>
									        			</div>
									        		</div>
										        </#if>
										        <#-- This section appears when Shipment of order is in picked status and its items are packed,this case comes when new shipping estimates based on weight of packages are more than or less than default percentage (defined in shipment.properties) of original shipping estimate-->
										        <#-- getShipGroupEstimate method of ShippingEvents class can be used for get shipping estimate from system, on the basis of new package's weight -->
										        <#if shippingRateList?has_content>
										          	<#if orderReadHelper.getOrderTypeId() != "PURCHASE_ORDER">
										          		<div class="control-group">
										        			${uiLabelMap.OrderOnlineUPSShippingEstimates}
										        		</div>
										        		<form name="UpdateShippingMethod" method="post" action="updateShippingMethodAndCharges">
										                    <#list shippingRateList as shippingRate>
										                      	<div class="control-group">
												        			<#assign shipmentMethodAndAmount = shippingRate.shipmentMethodTypeId + "@" + "UPS" + "*" + shippingRate.rate>
									                          		<label>
																		<input type='radio' name='shipmentMethodAndAmount' value='${shipmentMethodAndAmount?if_exists}' /><span class="lbl">${shippingRate.shipmentMethodDescription?if_exists}</span>
																  	</label>
										                          	<#if (shippingRate.rate > -1)>
										                            	<@ofbizCurrency amount=shippingRate.rate isoCode=orderReadHelper.getCurrency()/>
										                          	<#else>
										                            	${uiLabelMap.OrderCalculatedOffline}
										                          	</#if>
												        		</div>
										                    </#list>
										                    <input type="hidden" name="shipmentRouteSegmentId" value="${shipmentRouteSegmentId?if_exists}"/>
										                    <input type="hidden" name="shipmentId" value="${pickedShipmentId?if_exists}"/>
										                    <input type="hidden" name="orderAdjustmentId" value="${orderAdjustmentId?if_exists}"/>
										                    <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
										                    <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId?if_exists}"/>
										                    <input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
										                    <input type="hidden" name="oldContactMechId" value="${shipGroup.contactMechId?if_exists}"/>
										                    <input type="hidden" name="shippingAmount" value="${shippingAmount?if_exists}"/>
										                    <div class="control-group">
										                    	<button type="submit" class="btn btn-primary btn-small" name="submitButton"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
										                    </div>
									                  	</form>
										          	</#if>
										        </#if>
										        
										        <#-- tracking number -->
										        <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
										          	<div class="control-group">
										          		<label class="control-label">${uiLabelMap.OrderTrackingNumber}</label>
										          		<div class="controls">
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
										          		</div>
										          	</div>
										        </#if>
										        <#--
										        <#if shipGroup.maySplit?has_content && noShipment?default("false") != "true">
										        	<div class="control-group">
										        		<label class="control-label">${uiLabelMap.OrderSplittingPreference}</label>
										        		<div class="controls">
										        			<#if shipGroup.maySplit?upper_case == "N">
											                    ${uiLabelMap.FacilityWaitEntireOrderReady}
											                    <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
											                      <#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED">
											                        <form name="allowordersplit_${shipGroup.shipGroupSeqId}" method="post" action="allowOrderSplit">
											                          <input type="hidden" name="orderId" value="${orderId}"/>
											                          <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
											                        </form>
											                        <a href="javascript:document.allowordersplit_${shipGroup.shipGroupSeqId}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderAllowSplit}</a>
											                      </#if>
											                    </#if>
											                <#else>
											                    ${uiLabelMap.FacilityShipAvailable}
											                </#if>
										        		</div>
										        	</div>
										        </#if>
										        -->
										        <div class="control-group">
										        	<label class="control-label">${uiLabelMap.OrderInstructions}</label>
										        	<div class="controls">
										        		<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
											              	<form id="updateShippingInstructionsForm_${shipGroup.shipGroupSeqId}" name="updateShippingInstructionsForm" method="post" action="setShippingInstructionsSales">
											                	<input type="hidden" name="orderId" value="${orderHeader.orderId}"/>
											                	<input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
												                <#if shipGroup.shippingInstructions?has_content>
												                  	<i class="icon-quote-left"></i>
												                  	<span>${shipGroup.shippingInstructions}</span>
												                  	&nbsp;&nbsp;
												                  	<a href="javascript:editInstruction('${shipGroup.shipGroupSeqId}');" id="editInstruction_${shipGroup.shipGroupSeqId}">(${uiLabelMap.CommonEdit})</a>
												                <#else>
												                  	<a href="javascript:addInstruction('${shipGroup.shipGroupSeqId}');" class="btn btn-mini btn-primary" id="addInstruction_${shipGroup.shipGroupSeqId}">${uiLabelMap.CommonAdd}</a>
												                </#if>
											                	<a href="javascript:saveInstruction('${shipGroup.shipGroupSeqId}');" class="btn btn-mini btn-primary" id="saveInstruction_${shipGroup.shipGroupSeqId}" style="display:none">${uiLabelMap.CommonSave}</a>
											                	<a href="javascript:cancelEditInstruction('${shipGroup.shipGroupSeqId}');" class="btn btn-mini btn-primary" id="cancelEditInstruction_${shipGroup.shipGroupSeqId}" style="display:none">${uiLabelMap.DACancel}</a>
											                	<textarea name="shippingInstructions" id="shippingInstructions_${shipGroup.shipGroupSeqId}" style="display:none" rows="0" cols="0">${shipGroup.shippingInstructions?if_exists}</textarea>
											              	</form>
											            <#else>
											              	<#if shipGroup.shippingInstructions?has_content>
											                	<span>${shipGroup.shippingInstructions}</span>
											              	<#else>
											                	<span>${uiLabelMap.OrderThisOrderDoesNotHaveShippingInstructions}</span>
											              	</#if>
											            </#if>
										        	</div>
										        </div>
										        
										        <#--
										        <#if shipGroup.isGift?has_content && noShipment?default("false") != "true">
										        	<div class="control-group">
										        		<label class="control-label">${uiLabelMap.OrderGiftMessage}</label>
										        		<div class="controls">
										        			<form id="setGiftMessageForm_${shipGroup.shipGroupSeqId}" name="setGiftMessageForm" method="post" action="setGiftMessageSales">
												              	<input type="hidden" name="orderId" value="${orderHeader.orderId}"/>
												              	<input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
												              	<#if shipGroup.giftMessage?has_content>
												                	<i class="icon-quote-left"></i>
												                	<span>${shipGroup.giftMessage}</span>
												                	&nbsp;&nbsp;
												                	<a href="javascript:editGiftMessage('${shipGroup.shipGroupSeqId}');" id="editGiftMessage_${shipGroup.shipGroupSeqId}">(${uiLabelMap.CommonEdit})</a>
												              	<#else>
												                	<a href="javascript:addGiftMessage('${shipGroup.shipGroupSeqId}');" class="btn btn-mini btn-primary" id="addGiftMessage_${shipGroup.shipGroupSeqId}">${uiLabelMap.CommonAdd}</a>
												              	</#if>
												              	<a href="javascript:saveGiftMessage('${shipGroup.shipGroupSeqId}');" class="btn btn-mini btn-primary" id="saveGiftMessage_${shipGroup.shipGroupSeqId}" style="display:none">${uiLabelMap.CommonSave}</a>
												              	<a href="javascript:cancelEditGiftMessage('${shipGroup.shipGroupSeqId}');" class="btn btn-mini btn-primary" id="cancelEditGiftMessage_${shipGroup.shipGroupSeqId}" style="display:none">${uiLabelMap.DACancel}</a>
												              	<textarea name="giftMessage" id="giftMessage_${shipGroup.shipGroupSeqId}" style="display:none" rows="0" cols="0">${shipGroup.giftMessage?if_exists}</textarea>
												            </form>
										        		</div>
										        	</div>
										        </#if>
										        -->
										        
										        <form name="setShipGroupDates_${shipGroup.shipGroupSeqId}" method="post" action="/ordermgr/control/updateOrderItemShipGroup">
									                <input type="hidden" name="orderId" value="${orderHeader.orderId}"/>
									                <input type="hidden" name="shipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
											        <div class="control-group" style="margin-bottom: 0 !important; padding-top:10px">
											        	<label class="control-label">
											        		${uiLabelMap.DADeliveryDate}
											        	</label>
											        	<div class="controls">
											                <@htmlTemplate.renderDateTimeField name="estimatedDeliveryDate" id="estimatedDeliveryDate" event="" action="" value="${shipGroup.estimatedDeliveryDate?if_exists}" 
												                	className="margin-bottom0" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
												                	timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
												                	hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
											                <button type="submit" name="submitButton" data-rel="tooltip" title="${uiLabelMap.CommonUpdate}: ${uiLabelMap.DADeliveryDate}, ${uiLabelMap.OrderShipAfterDate}, ${uiLabelMap.OrderShipBeforeDate}" data-placement="bottom" class="btn btn-mini btn-primary"><i class="fa-floppy-o"></i></button>
											        	</div>
											        </div>
											        <div class="control-group" style="margin-bottom: 0 !important; padding-top:3px">
											        	<label class="control-label">${uiLabelMap.OrderShipAfterDate}</label>
											        	<div class="controls">
											        		<@htmlTemplate.renderDateTimeField name="shipAfterDate" event="" action="" value="${shipGroup.shipAfterDate?if_exists}" className="margin-bottom0" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="shipAfterDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
											        	</div>
											        </div>
											        <div class="control-group" style="padding-top:3px">
											        	<label class="control-label">${uiLabelMap.OrderShipBeforeDate}</label>
											        	<div class="controls">
											        		<@htmlTemplate.renderDateTimeField name="shipByDate" event="" action="" value="${shipGroup.shipByDate?if_exists}" className="margin-bottom0" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="shipByDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
										                </div>
											        </div>
										        </form>
										        
										        <#assign shipGroupShipments = shipGroup.getRelated("PrimaryShipment", null, null, false)>
										       	<#if shipGroupShipments?has_content>
										       		<div class="control-group">
										       			<label class="control-label">${uiLabelMap.FacilityShipments}</label>
										       			<div class="controls">
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
										       			</div>
										       		</div>
										       		
										       		<#-- shipment actions -->
											       	<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && ((orderHeader.statusId == "ORDER_CREATED") || (orderHeader.statusId == "ORDER_APPROVED") || (orderHeader.statusId == "ORDER_SENT"))>
											         	<#-- Manual shipment options -->
											         	<div class="control-group">
											         		<#if orderHeader.orderTypeId == "SALES_ORDER">
										               			<#if !shipGroup.supplierPartyId?has_content>
												                 	<#if orderHeader.statusId == "ORDER_APPROVED">
												                 		<a href="<@ofbizUrl>packOrder</@ofbizUrl>?facilityId=${storeFacilityId?if_exists}&amp;orderId=${orderId}&amp;shipGroupSeqId=${shipGroup.shipGroupSeqId}${externalKeyParam}" 
												                 		class="btn btn-mini btn-primary" target="_blank">${uiLabelMap.OrderPackShipmentForShipGroup}</a>
												                 		<br /><br />
												                 	</#if>
										                 			<a href="javascript:document.createShipment_${shipGroup.shipGroupSeqId}.submit()" class="btn btn-mini btn-primary">${uiLabelMap.DANewShipmentForShipGroup}</a>
										                 			<form name="createShipment_${shipGroup.shipGroupSeqId}" method="post" action="<@ofbizUrl>createShipment</@ofbizUrl>">
										                   				<input type="hidden" name="primaryOrderId" value="${orderId}"/>
										                   				<input type="hidden" name="primaryShipGroupSeqId" value="${shipGroup.shipGroupSeqId}"/>
										                   				<input type="hidden" name="statusId" value="SHIPMENT_INPUT" />
										                   				<input type="hidden" name="facilityId" value="${storeFacilityId?if_exists}" />
										                   				<input type="hidden" name="estimatedShipDate" value="${shipGroup.shipByDate?if_exists}"/>
										                 			</form>
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
											         	</div>
											       	</#if>
										       	</#if>
								        	</div><!--.span12-->
								        </div><!--.row-fluid-->
				         			</div>
							    </td>
							</#list>
						</tr>
			  		</tbody>
			  	</table>
			</#if>
		</div><!--.span8-->
		<div class="span4">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.OrderActions}
			</h4>
			<#if security.hasEntityPermission("DIS_ORDERMGR", "_UPDATE", session) && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")>
				<div>
	  				<ul class="unstyled spaced">
	  				    <#-- security.hasEntityPermission("DIS_FACILITY", "_CREATE", session) &&  -->
	    				<#if ((orderHeader.statusId == "ORDER_APPROVED") || (orderHeader.statusId == "ORDER_SENT"))>
	      					<#-- Special shipment options -->
	      					<#if orderHeader.orderTypeId == "SALES_ORDER">
	        					<li>
						            <form name="quickShipOrder" method="post" action="<@ofbizUrl>quickShipSalesOrderDis</@ofbizUrl>">
						              	<input type="hidden" name="orderId" value="${orderId}"/>
						            </form>
	        						<a href="javascript:document.quickShipOrder.submit()" class="btn btn-mini btn-primary icon-forward open-sans">${uiLabelMap.DAQuickShipEntireOrder}</a>
	    						</li>
	      					<#else> <#-- PURCHASE_ORDER -->
	        					<span>&nbsp;<#if orderHeader.orderTypeId == "PURCHASE_ORDER">${uiLabelMap.ProductDestinationFacility}</#if></span>
	        					<#if ownedFacilities?has_content>
	          						<#if !allShipments?has_content>
	          							<li>
	                 						<form action="/facility/control/quickShipPurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
						                       	<input type="hidden" name="initialSelected" value="Y"/>
						                       	<input type="hidden" name="orderId" value="${orderId}"/>
	                   							<#-- destination form (/facility/control/ReceiveInventory) wants purchaseOrderId instead of orderId, so we set it here as a workaround -->
	                   							<input type="hidden" name="purchaseOrderId" value="${orderId}"/>
	                  							<select name="facilityId">
	                    							<#list ownedFacilities as facility>
	                      								<option value="${facility.facilityId}">${facility.facilityName}</option>
	                    							</#list>
	                  							</select>
	                  							<button type="submit" class="btn btn-small btn-primary">
	                  	 							${uiLabelMap.OrderQuickReceivePurchaseOrder}
	              								</button>
	                 						</form>
	              						</li>
					                  	<li>
					                    	<form name="receivePurchaseOrderForm" action="/facility/control/quickShipPurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
						                      	<input type="hidden" name="initialSelected" value="Y"/>
						                      	<input type="hidden" name="orderId" value="${orderId}"/>
						                      	<input type="hidden" name="purchaseOrderId" value="${orderId}"/>
						                      	<input type="hidden" name="partialReceive" value="Y"/>
						                      	<select name="facilityId">
						                        	<#list ownedFacilities as facility>
						                          		<option value="${facility.facilityId}">${facility.facilityName}</option>
						                        	</#list>
						                      	</select>
				                      		</form>
					                      	<i class="icon-caret-right blue"></i>
					                      	<a href="javascript:document.receivePurchaseOrderForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.CommonReceive}</a>
					                  	</li>
	          						<#else>
					                  	<li>
				                    		<form name="receiveInventoryForm" action="/facility/control/ReceiveInventory" method="post">
						                      	<input type="hidden" name="initialSelected" value="Y"/>
						                      	<input type="hidden" name="purchaseOrderId" value="${orderId?if_exists}"/>
						                      	<select name="facilityId">
						                        	<#list ownedFacilities as facility>
						                          		<option value="${facility.facilityId}">${facility.facilityName}</option>
						                        	</#list>
						                      	</select>
					                    	</form>
						                    <i class="icon-caret-right blue"></i>
						                    <a href="javascript:document.receiveInventoryForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderQuickReceivePurchaseOrder}</a>
					                  	</li>
					                  	<li>
					                    	<form name="partialReceiveInventoryForm" action="/facility/control/ReceiveInventory" method="post">
					                      		<input type="hidden" name="initialSelected" value="Y"/>
					                      		<input type="hidden" name="purchaseOrderId" value="${orderId?if_exists}"/>
					                      		<input type="hidden" name="partialReceive" value="Y"/>
					                      		<select name="facilityId">
					                        		<#list ownedFacilities as facility>
					                           			<option value="${facility.facilityId}">${facility.facilityName}</option>
					                         		</#list>
					                       		</select>
					                    	</form>
					                    	<i class="icon-caret-right blue"></i>
					                    	<a href="javascript:document.partialReceiveInventoryForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.CommonReceive}</a>
					                  	</li>
	          						</#if>
					              	<#if orderHeader.statusId != "ORDER_COMPLETED">
					                  	<li>
						                    <form action="/ordermgr/control/completePurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
						                     	<input type="hidden" name="orderId" value="${orderId}"/>
					                    		<select name="facilityId">
					                      			<#list ownedFacilities as facility>
					                        			<option value="${facility.facilityId}">${facility.facilityName}</option>
					                      			</#list>
					                    		</select>
					                    		<button type="submit" class="btn btn-small btn-primary">
					                    			${uiLabelMap.OrderForceCompletePurchaseOrder}
					                    		</button>
					                    	</form>
					                  	</li>
					              	</#if>
	        					</#if>
	      					</#if>
	    				</#if>
	    				<#-- Refunds/Returns for Sales Orders and Delivery Schedules -->
	    				<#--
	    				<#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED">
							<li><a href="<@ofbizUrl>orderDeliveryScheduleInfo?orderId=${orderId}</@ofbizUrl>"><i class="icon-eye-open open-sans"></i>${uiLabelMap.OrderViewEditDeliveryScheduleInfo}</a></li>
	    				</#if>
	    				-->
	    				<#if security.hasEntityPermission("ORDERMGR", "_RETURN", session) && orderHeader.statusId == "ORDER_COMPLETED">
	      					<#if returnableItems?has_content>
	        					<li>
						            <form name="quickRefundOrder" method="post" action="<@ofbizUrl>quickRefundOrder</@ofbizUrl>">
						              	<input type="hidden" name="orderId" value="${orderId}"/>
						              	<input type="hidden" name="receiveReturn" value="true"/>
						              	<input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
						            </form>
	        						<a href="javascript:document.quickRefundOrder.submit()" class="btn btn-mini btn-primary"><i class="icon-forward open-sans"></i>${uiLabelMap.DAQuickRefundEntireOrder}</a>
	    						</li>
					            <li>
					            	<form name="quickreturn" method="post" action="<@ofbizUrl>quickReturn</@ofbizUrl>">
					              		<input type="hidden" name="orderId" value="${orderId}"/>
					              		<input type="hidden" name="party_id" value="${partyId?if_exists}"/>
				              			<input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
					              		<input type="hidden" name="needsInventoryReceive" value="${needsInventoryReceive?default("N")}"/>
					            	</form>
					            	<a href="javascript:document.quickreturn.submit()"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.OrderCreateReturn}</a>
					            </li>
	      					</#if>
	    				</#if>
	
	    				<#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED">
	      					<#if orderHeader.statusId != "ORDER_COMPLETED">
					            <#-- <li><a href="/ordermgr/control/cancelOrderItem?${paramString}" class="buttontext">${uiLabelMap.OrderCancelAllItems}</a></li> -->
	        					<li><a href="<@ofbizUrl>editOrderItems?${paramString}</@ofbizUrl>" target="_blank"><i class="icon-edit open-sans"></i>${uiLabelMap.DAEditOrderItems}</a></li>
	        					<#--
	        					<li>
						            <form name="createOrderItemShipGroup" method="post" action="<@ofbizUrl>createOrderItemShipGroup</@ofbizUrl>">
						              	<input type="hidden" name="orderId" value="${orderId}"/>
						            </form>
	        						<a href="javascript:document.createOrderItemShipGroup.submit()"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.DACreateShipGroup}</a>
	        					</li>
	        					-->
	      					</#if>
	  						<li><a href="<@ofbizUrl>loadCartFromOrder</@ofbizUrl>?${paramString}&amp;finalizeMode=init"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.DACreateNewOrder}</a></li>
	      					<#if orderHeader.statusId == "ORDER_COMPLETED">
	        					<li><a href="<@ofbizUrl>loadCartForReplacementOrder</@ofbizUrl>?${paramString}"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.OrderCreateReplacementOrder}</a></li>
	      					</#if>
	    				</#if>
	    				<li>
	    					<a href="<@ofbizUrl>orderHistory</@ofbizUrl>?orderId=${orderId}">
	    						<i class="open-sans fa-history"></i>&nbsp;${uiLabelMap.DAViewOrderHistory}
							</a>
						</li>
	  				</ul>
				</div>
			</#if>
		</div><!--.span4-->
	</div><!--.row-fluid-->
</div><!--#shippinginfo-tab-->
<script type="text/javascript">
	$('[data-rel=tooltip]').tooltip();
</script>