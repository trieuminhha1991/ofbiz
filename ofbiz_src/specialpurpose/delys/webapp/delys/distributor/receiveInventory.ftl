<script language="JavaScript" type="text/javascript">
    function setNow(field) { eval('document.selectAllForm.' + field + '.value="${nowTimestamp}"'); }
</script>
<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<#if invalidProductId?exists>
            	<div class="alert alert-danger">${invalidProductId}</div>
        	</#if>
        	
        	<#if receivedItems?has_content>
          		<h3>${uiLabelMap.ProductReceiptPurchaseOrder} ${purchaseOrder.orderId}</h3>
          		<table class="table table-striped table-bordered table-hover">
            		<thead>
	            		<tr class="header-row">
			              	<th>${uiLabelMap.ProductShipmentId}</th>
			              	<th>${uiLabelMap.ProductReceipt}</th>
			              	<th>${uiLabelMap.CommonDate}</th>
			              	<th>${uiLabelMap.ProductPo}</th>
			              	<th>${uiLabelMap.ProductLine}</th>
			              	<th>${uiLabelMap.ProductProductId}</th>
			              	<th>${uiLabelMap.ProductLotId}</th>
			              	<th>${uiLabelMap.ProductPerUnitPrice}</th>
			              	<th>${uiLabelMap.CommonRejected}</th>
			              	<th>${uiLabelMap.CommonAccepted}</th>
			              	<th></th>
	            		</tr>
            		</thead>
            		<tbody>
			            <#list receivedItems as item>
			              	<form name="cancelReceivedItemsForm_${item_index}" method="post" action="<@ofbizUrl>cancelReceivedItems</@ofbizUrl>">
			                	<input type="hidden" name="receiptId" value ="${(item.receiptId)?if_exists}"/>
			                	<input type="hidden" name="purchaseOrderId" value ="${(item.orderId)?if_exists}"/>
			                	<input type="hidden" name="facilityId" value ="${facilityId?if_exists}"/>
			                	<tr>
			                  		<td><a href="<@ofbizUrl>ViewShipment?shipmentId=${item.shipmentId?if_exists}</@ofbizUrl>">${item.shipmentId?if_exists} ${item.shipmentItemSeqId?if_exists}</a></td>
			                  		<td>${item.receiptId}</td>
			                  		<td>${item.getString("datetimeReceived").toString()}</td>
			                  		<td><a href="<@ofbizUrl>orderView?orderId=${item.orderId}</@ofbizUrl>">${item.orderId}</a></td>
			                  		<td>${item.orderItemSeqId}</td>
			                  		<td>${item.productId?default("Not Found")}</td>
			                  		<td>${item.lotId?default("")}</td>
			                  		<td>${item.unitCost?default(0)?string("##0.00")}</td>
			                  		<td>${item.quantityRejected?default(0)?string.number}</td>
			                  		<td>${item.quantityAccepted?string.number}</td>
			                  		<td>
			                    		<#if (item.quantityAccepted?int > 0 || item.quantityRejected?int > 0)>
			                      			<a href="javascript:document.cancelReceivedItemsForm_${item_index}.submit();" class="btn btn-mini btn-info">${uiLabelMap.CommonCancel}</a>
			                    		</#if>
			                  		</td>
			                	</tr>
			              	</form>
			            </#list>
			     	</tbody>
          		</table>
        	</#if>
        	
        	<#-- Single Product Receiving -->
    		<#if requestParameters.initialSelected?exists && product?has_content>
          		<form method="POST" action="<@ofbizUrl>receiveSingleInventoryProductDis</@ofbizUrl>" name="selectAllForm" class="form-horizontal basic-custom-form form-decrease-padding">
	          		<#-- general request fields -->
              		<input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
              		<input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
              		<#-- special service fields -->
              		<input type="hidden" name="productId" value="${requestParameters.productId?if_exists}"/>
	          		<input type="hidden" name="ownerPartyId" value=""/>
	          		<div class="row-fluid">
						<div class="span6">
							<#if purchaseOrder?has_content>
		              			<#assign unitCost = firstOrderItem.unitPrice?default(standardCosts.get(firstOrderItem.productId)?default(0))/>
		              			<input type="hidden" name="orderId" value="${purchaseOrder.orderId}"/>
		              			<input type="hidden" name="orderItemSeqId" value="${firstOrderItem.orderItemSeqId}"/>
								<div class="control-group">
									<label class="control-label">${uiLabelMap.ProductPurchaseOrder}</label>
									<div class="controls">
										<div class="span12">
											<b>${purchaseOrder.orderId}</b>&nbsp;/&nbsp;<b>${firstOrderItem.orderItemSeqId}</b>
						                  	<#if 1 &lt; purchaseOrderItems.size()>
							                    (${uiLabelMap.ProductMultipleOrderItemsProduct} - ${purchaseOrderItems.size()}:1 ${uiLabelMap.ProductItemProduct})
						                  	<#else>
							                    (${uiLabelMap.ProductSingleOrderItemProduct} - 1:1 ${uiLabelMap.ProductItemProduct})
						                  	</#if>
										</div>
									</div>
								</div>
							</#if>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductProductId}</label>
								<div class="controls">
									<div class="span12">
										<b>${requestParameters.productId?if_exists}</b>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductProductName}</label>
								<div class="controls">
									<div class="span12">
										${product.internalName?if_exists}
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAProductDescription}</label>
								<div class="controls">
									<div class="span12">
										${product.description?if_exists}
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAItemDescription}</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="itemDescription" size="30" maxlength="60"/>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductInventoryItemType}</label>
								<div class="controls">
									<div class="span12">
										<select name="inventoryItemTypeId" size="1">
					                    	<#list inventoryItemTypes as nextInventoryItemType>
					                      		<option value="${nextInventoryItemType.inventoryItemTypeId}"
				                        		<#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
					                          		selected="selected"
				                        		</#if>
					                      		>${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
					                    	</#list>
					                  	</select>
									</div>
								</div>
							</div>
							<#--<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductFacilityOwner}</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.lookupField formName="selectAllForm" name="ownerPartyId" id="ownerPartyId" fieldFormName="LookupPartyName"/>
									</div>
								</div>
							</div>-->
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductSupplier}</label>
								<div class="controls">
									<div class="span12">
										<select name="partyId">
					                    	<option value=""></option>
					                    	<#if supplierPartyIds?has_content>
					                      		<#list supplierPartyIds as supplierPartyId>
					                        		<option value="${supplierPartyId}" <#if supplierPartyId == parameters.partyId?if_exists> selected="selected"</#if>>
					                          			[${supplierPartyId}] ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, supplierPartyId, true)}
					                        		</option>
					                      		</#list>
					                    	</#if>
					                  	</select>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductDateReceived}</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="datetimeReceived" size="24" value="${nowTimestamp}" />
										<#-- <a href="#" onclick="setNow("datetimeReceived")" class="btn btn-mini btn-info">[Now]</a> -->
									</div>
								</div>
							</div>
          				</div>

	              		<#-- facility location(s) -->
	              		<#assign facilityLocations = (product.getRelated("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId), null, false))?if_exists/>
						<div class="span6">
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DALotId}</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="lotId" size="10"/>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductFacilityLocation}</label>
								<div class="controls">
									<div class="span12">
										<#if facilityLocations?has_content>
					                    	<select name="locationSeqId">
						                      	<#list facilityLocations as productFacilityLocation>
						                        	<#assign facility = productFacilityLocation.getRelatedOne("Facility", true)/>
						                        	<#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation", false)?if_exists/>
						                        	<#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOne("TypeEnumeration", true))?if_exists/>
						                        	<option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
						                      	</#list>
						                      	<option value="">${uiLabelMap.ProductNoLocation}</option>
						                    </select>
					                  	<#else>
						                    <#if parameters.facilityId?exists>
						                      	<#assign LookupFacilityLocationView="LookupFacilityLocation?facilityId=${facilityId}">
						                    <#else>
						                      	<#assign LookupFacilityLocationView="LookupFacilityLocation">
						                    </#if>
						                    <@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId" id="locationSeqId" fieldFormName="${LookupFacilityLocationView}"/>
					                  	</#if>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductRejectedReason}</label>
								<div class="controls">
									<div class="span12">
										<select name="rejectionId" size="1">
						                    <option></option>
						                    <#list rejectReasons as nextRejection>
						                      	<option value="${nextRejection.rejectionId}">${nextRejection.get("description",locale)?default(nextRejection.rejectionId)}</option>
						                    </#list>
					                  	</select>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAQuantityRejected}</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="quantityRejected" size="5" value="0" />
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAQuantityAccepted}</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="quantityAccepted" size="5" value="${defaultQuantity?default(1)?string.number}"/>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductPerUnitPrice}</label>
								<div class="controls">
									<div class="span12">
										<#-- get the default unit cost -->
					                  	<#if (!unitCost?exists || unitCost == 0.0)><#assign unitCost = standardCosts.get(product.productId)?default(0)/></#if>
					                  	<input type="text" name="unitCost" size="10" value="${unitCost}"/>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">&nbsp;</label>
								<div class="controls">
									<div class="span12">
										<button type="submit" class="btn btn-small btn-primary">
											<i class="icon-ok open-sans"></i>${uiLabelMap.CommonReceive}
										</button>
									</div>
								</div>
							</div>
              			</div>
              		</div>
	            	<script language="JavaScript" type="text/javascript">
	              		document.selectAllForm.quantityAccepted.focus();
	            	</script>
          		</form><!--end form 1: selectAllForm-->

    		<#-- Select Shipment Screen -->
    		<#elseif requestParameters.initialSelected?exists && !requestParameters.shipmentId?exists>
      			<h3>${uiLabelMap.ProductSelectShipmentReceive}</h3>
          		<form method="post" action="<@ofbizUrl>ReceiveInventory</@ofbizUrl>" name="selectAllForm" class="form-horizontal basic-custom-form form-decrease-padding">
            		<#-- general request fields -->
            		<input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            		<input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
            		<input type="hidden" name="initialSelected" value="Y"/>
            		<input type="hidden" name="partialReceive" value="${partialReceive?if_exists}"/>
            		<table class="table table-striped table-bordered table-hover">
              			<#list shipments?if_exists as shipment>
                			<#assign originFacility = shipment.getRelatedOne("OriginFacility", true)?if_exists/>
                			<#assign destinationFacility = shipment.getRelatedOne("DestinationFacility", true)?if_exists/>
                			<#assign statusItem = shipment.getRelatedOne("StatusItem", true)/>
                			<#assign shipmentType = shipment.getRelatedOne("ShipmentType", true)/>
                			<#assign shipmentDate = shipment.estimatedArrivalDate?if_exists/>
                			<tr>
                 	 			<td>
                    				<table class="table table-bordered" cellspacing="0">
				                      	<tr>
				                        	<td width="5%" nowrap="nowrap"><input type="radio" name="shipmentId" value="${shipment.shipmentId}" style="opacity: 0.5"/></td>
				                        	<td width="5%" nowrap="nowrap">${shipment.shipmentId}</td>
				                        	<td>${shipmentType.get("description",locale)?default(shipmentType.shipmentTypeId?default(""))}</td>
				                        	<td>${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}</td>
				                        	<td>${(originFacility.facilityName)?if_exists} [${shipment.originFacilityId?if_exists}]</td>
				                        	<td>${(destinationFacility.facilityName)?if_exists} [${shipment.destinationFacilityId?if_exists}]</td>
				                        	<td style="white-space: nowrap;">${(shipment.estimatedArrivalDate.toString())?if_exists}</td>
				                      	</tr>
				                    </table>
                  				</td>
                			</tr>
              			</#list>
              			<tr>
               	 			<td>
                  				<table class="table-basic" cellspacing="0">
                    				<tr>
                      					<td width="5%" nowrap="nowrap"><input type="radio" name="shipmentId" value="_NA_" style="opacity: 0.5"/></td>
                      					<td width="5%" nowrap="nowrap">${uiLabelMap.ProductNoSpecificShipment}</td>
                      					<td colspan="5"></td>
                    				</tr>
                  				</table>
                			</td>
              			</tr>
              			<tr>
              				<table class="table-basic" cellspacing="0">
                    			<tr>
                      				<td>&nbsp;<a href="javascript:document.selectAllForm.submit();" class="btn btn-info btn-small">${uiLabelMap.ProductReceiveSelectedShipment}</a></td>
                    			</tr>
                  			</table>
              			</tr>
            		</table>
          		</form><!--end form 2: selectAllForm-->

    		<#-- Multi-Item PO Receiving -->
    		<#elseif requestParameters.initialSelected?exists && purchaseOrder?has_content>
      			<input type="hidden" id="getConvertedPrice" value="<@ofbizUrl secure="${request.isSecure()?string}">getConvertedPrice"</@ofbizUrl> />
          		<input type="hidden" id="alertMessage" value="${uiLabelMap.ProductChangePerUnitPrice}" />
          		<form method="post" action="<@ofbizUrl>receiveInventoryProduct</@ofbizUrl>" name="selectAllForm" class="form-horizontal basic-custom-form form-decrease-padding">
            		<#-- general request fields -->
            		<input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
            		<input type="hidden" name="purchaseOrderId" value="${requestParameters.purchaseOrderId?if_exists}"/>
            		<input type="hidden" name="initialSelected" value="Y"/>
            		<#if shipment?has_content>
            			<input type="hidden" name="shipmentIdReceived" value="${shipment.shipmentId}"/>
            		</#if>
            		<input type="hidden" name="_useRowSubmit" value="Y"/>
            		<#assign rowCount = 0/>
            		<table class="table table-striped table-hovered dataTable" cellspacing="0">
              			<#if !purchaseOrderItems?exists || purchaseOrderItems.size() == 0>
                			<tr>
                  				<td colspan="2">${uiLabelMap.ProductNoItemsPoReceive}.</td>
                			</tr>
              			<#else/>
                			<tr>
                  				<td>
                    				<h3>${uiLabelMap.ProductReceivePurchaseOrder} #${purchaseOrder.orderId}</h3>
				                    <#if shipment?has_content>
				                    	<h3>${uiLabelMap.ProductShipmentId} #${shipment.shipmentId}</h3>
				                    	<span>Set Shipment As Received</span>&nbsp;
				                    	<input type="checkbox" name="forceShipmentReceived" value="Y"/>
				                    </#if>
                  				</td>
                  				<td align="right">
                    				${uiLabelMap.CommonSelectAll}
				                    <input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this, 'selectAllForm');"/>
                  				</td>
                			</tr>
                			<#list purchaseOrderItems as orderItem>
                  				<#assign defaultQuantity = orderItem.quantity - receivedQuantities[orderItem.orderItemSeqId]?double/>
                  				<#assign itemCost = orderItem.unitPrice?default(0)/>
                  				<#assign salesOrderItem = salesOrderItems[orderItem.orderItemSeqId]?if_exists/>
                  				<#if shipment?has_content>
                    				<#if shippedQuantities[orderItem.orderItemSeqId]?exists>
                      					<#assign defaultQuantity = shippedQuantities[orderItem.orderItemSeqId]?double - receivedQuantities[orderItem.orderItemSeqId]?double/>
                    				<#else>
                      					<#assign defaultQuantity = 0/>
                    				</#if>
                  				</#if>
                  				<#if 0 &lt; defaultQuantity>
                  					<#assign orderItemType = orderItem.getRelatedOne("OrderItemType", false)/>
				                  	<input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}"/>
				                  	<input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"/>
				                  	<input type="hidden" name="facilityId_o_${rowCount}" value="${requestParameters.facilityId?if_exists}"/>
				                  	<input type="hidden" name="datetimeReceived_o_${rowCount}" value="${nowTimestamp}"/>
				                  	<#if shipment?exists && shipment.shipmentId?has_content>
				                    	<input type="hidden" name="shipmentId_o_${rowCount}" value="${shipment.shipmentId}"/>
				                  	</#if>
				                  	<#if salesOrderItem?has_content>
					                    <input type="hidden" name="priorityOrderId_o_${rowCount}" value="${salesOrderItem.orderId}"/>
					                    <input type="hidden" name="priorityOrderItemSeqId_o_${rowCount}" value="${salesOrderItem.orderItemSeqId}"/>
				                  	</#if>
                  					<tr>
                    					<td colspan="2"><hr /></td>
                  					</tr>
                  					<tr>
                    					<td>
                      						<table class="table table-striped table-hovered dataTable" cellspacing="0">
                        						<tr>
						                          	<#if orderItem.productId?exists>
						                            	<#assign product = orderItem.getRelatedOne("Product", true)/>
						                            	<input type="hidden" name="productId_o_${rowCount}" value="${product.productId}"/>
						                            	<td width="45%">
						                                	${orderItem.orderItemSeqId}:&nbsp;<a href="/catalog/control/EditProduct?productId=${product.productId}${externalKeyParam?if_exists}" target="catalog" class="btn btn-mini btn-info">${product.productId}&nbsp;-&nbsp;${orderItem.itemDescription?if_exists}</a> : ${product.description?if_exists}
						                            	</td>
						                          	<#else>
						                            	<td width="45%">
						                                	<b>${orderItemType.get("description",locale)}</b> : ${orderItem.itemDescription?if_exists}&nbsp;&nbsp;
						                                	<input type="text" size="12" name="productId_o_${rowCount}"/>
						                                	<a href="/catalog/control/EditProduct?${StringUtil.wrapString(externalKeyParam)}" target="catalog" class="btn btn-mini btn-info">${uiLabelMap.ProductCreateProduct}</a>
						                            	</td>
						                          	</#if>
						                          	<td align="right">${uiLabelMap.ProductLocation}:</td>
						                          	<#-- location(s) -->
						                          	<td align="right">
						                            	<#assign facilityLocations = (orderItem.getRelated("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId), null, false))?if_exists/>
						                            	<#if facilityLocations?has_content>
						                              		<select name="locationSeqId_o_${rowCount}">
						                                		<#list facilityLocations as productFacilityLocation>
						                                  			<#assign facility = productFacilityLocation.getRelatedOne("Facility", true)/>
						                                  			<#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation", false)?if_exists/>
						                                  			<#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOne("TypeEnumeration", true))?if_exists/>
						                                  			<option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
						                                		</#list>
						                                		<option value="">${uiLabelMap.ProductNoLocation}</option>
						                              		</select>
						                            	<#else>
							                              	<#if parameters.facilityId?exists>
								                                <#assign LookupFacilityLocationView="LookupFacilityLocation?facilityId=${facilityId}">
							                              	<#else>
								                                <#assign LookupFacilityLocationView="LookupFacilityLocation">
							                              	</#if>
							                              	<@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId_o_${rowCount}" id="locationSeqId_o_${rowCount}" fieldFormName="${LookupFacilityLocationView}"/>
						                            	</#if>
					                          		</td>
					                          		<td align="right">${uiLabelMap.ProductQtyReceived} :</td>
						                          	<td align="right">
							                            <input type="text" name="quantityAccepted_o_${rowCount}" size="6" value=<#if partialReceive?exists>"0"<#else>"${defaultQuantity?string.number}"</#if>/>
						                          	</td>
						                        </tr>
					                        <tr>
					                          	<td width="45%">
					                            	${uiLabelMap.ProductInventoryItemType} :&nbsp;
					                            	<select name="inventoryItemTypeId_o_${rowCount}" size="1">
					                              		<#list inventoryItemTypes as nextInventoryItemType>
					                              			<option value="${nextInventoryItemType.inventoryItemTypeId}"
					                               			<#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
					                                			selected="selected"
					                              			</#if>
					                              			>${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
					                              		</#list>
					                            	</select>
					                          	</td>
					                          	<td align="right">${uiLabelMap.ProductRejectionReason} :</td>
					                          	<td align="right">
					                            	<select name="rejectionId_o_${rowCount}" size="1">
						                              	<option></option>
						                              	<#list rejectReasons as nextRejection>
						                              		<option value="${nextRejection.rejectionId}">${nextRejection.get("description",locale)?default(nextRejection.rejectionId)}</option>
						                              	</#list>
					                            	</select>
					                          	</td>
					                          	<td align="right">${uiLabelMap.ProductQtyRejected} :</td>
					                          	<td align="right">
					                            	<input type="text" name="quantityRejected_o_${rowCount}" value="0" size="6"/>
					                          	</td>
					                          	<tr>
					                            	<td>&nbsp;</td>
					                            	<#if !product.lotIdFilledIn?has_content || product.lotIdFilledIn != "Forbidden">
					                              		<td align="right">${uiLabelMap.ProductLotId}</td>
					                              		<td align="right">
					                                		<input type="text" name="lotId_o_${rowCount}" size="20" />
					                              		</td>
					                            	<#else />
					                              		<td align="right">&nbsp;</td>
					                              		<td align="right">&nbsp;</td>
					                            	</#if>
					                            	<td align="right">${uiLabelMap.OrderQtyOrdered} :</td>
					                            	<td align="right">
					                              		<input type="text" class="inputBox" name="quantityOrdered" value="${orderItem.quantity}" size="6" maxlength="20" disabled="disabled" />
					                            	</td>
				                          		</tr>
					                        </tr>
					                        <tr>
					                          	<td>&nbsp;</td>
					                          	<td align="right">${uiLabelMap.ProductFacilityOwner}:</td>
					                          	<td align="right"><input type="text" name="ownerPartyId_o_${rowCount}" size="20" maxlength="20" value="${facility.ownerPartyId}"/></td>
					                          	<#if currencyUomId?default('') != orderCurrencyUomId?default('')>
					                            	<td>${uiLabelMap.ProductPerUnitPriceOrder}:</td>
					                            	<td>
						                              	<input type="hidden" name="orderCurrencyUomId_o_${rowCount}" value="${orderCurrencyUomId?if_exists}" />
						                              	<input type="text" id="orderCurrencyUnitPrice_${rowCount}" name="orderCurrencyUnitPrice_o_${rowCount}" value="${orderCurrencyUnitPriceMap[orderItem.orderItemSeqId]}" onchange="javascript:getConvertedPrice(orderCurrencyUnitPrice_${rowCount}, '${orderCurrencyUomId}', '${currencyUomId}', '${rowCount}', '${orderCurrencyUnitPriceMap[orderItem.orderItemSeqId]}', '${itemCost}');" size="6" maxlength="20" />
						                              	${orderCurrencyUomId?if_exists}
					                            	</td>
					                            	<td>${uiLabelMap.ProductPerUnitPriceFacility}:</td>
					                            	<td>
						                              	<input type="hidden" name="currencyUomId_o_${rowCount}" value="${currencyUomId?if_exists}" />
						                              	<input type="text" id="unitCost_${rowCount}" name="unitCost_o_${rowCount}" value="${itemCost}" readonly="readonly" size="6" maxlength="20" />
						                              	${currencyUomId?if_exists}
					                            	</td>
					                          	<#else>
					                            	<td align="right">${uiLabelMap.ProductPerUnitPrice}:</td>
					                            	<td align="right">
					                              		<input type="hidden" name="currencyUomId_o_${rowCount}" value="${currencyUomId?if_exists}" />
					                              		<input type="text" name="unitCost_o_${rowCount}" value="${itemCost}" size="6" maxlength="20" />
					                              		${currencyUomId?if_exists}
					                            	</td>
					                          	</#if>
					                        </tr>
				                      	</table>
				                    </td>
				                    <td align="right">
				                      	<input type="checkbox" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');"/>
				                    </td>
			                  	</tr>
			                  	<#assign rowCount = rowCount + 1>
	                  		</#if>
	                	</#list>
		                <tr>
							<td colspan="2">
						    	<hr />
					    	</td>
						</tr>
					    <#if rowCount == 0>
					    	<tr>
					        	<td colspan="2">${uiLabelMap.ProductNoItemsPo} #${purchaseOrder.orderId} ${uiLabelMap.ProductToReceive}.</td>
			            	</tr>
				        	<tr>
						        <td colspan="2" align="right">
			                      	<a href="<@ofbizUrl>ReceiveInventory?facilityId=${requestParameters.facilityId?if_exists}</@ofbizUrl>" class="btn btn-mini btn-info">${uiLabelMap.ProductReturnToReceiving}</a>
						        </td>
					        </tr>
					    <#else>
					        <tr>
					        	<td colspan="2" align="right">
					            	<a href="javascript:document.selectAllForm.submit();" class="btn btn-info btn-mini">${uiLabelMap.ProductReceiveSelectedProduct}</a>
				                </td>
				            </tr>
					   	</#if>
					</#if>
				</table>
				<input type="hidden" name="_rowCount" value="${rowCount}"/>
			</form>
			<script language="JavaScript" type="text/javascript">selectAll('selectAllForm');</script>
					
	        <#-- Initial Screen -->
	        <#else>
	        	<#--step1-->
	          	<div style="text-align:right">
					<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.ProductReceiveItem}</b></h5>
				</div>
				<div style="clear:both"></div>
	          	<form name="selectAllForm" method="post" action="<@ofbizUrl>receiveInventoryDis</@ofbizUrl>" class="form-horizontal basic-custom-form form-decrease-padding">
	            	<input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}"/>
	            	<input type="hidden" name="initialSelected" value="Y"/>
	            	
	            	<div class="row-fluid">
						<div class="span6">
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAPurchaseOrderId}</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.lookupField value="${requestParameters.purchaseOrderId?if_exists}" formName="selectAllForm" name="purchaseOrderId" id="purchaseOrderId" fieldFormName="LookupPurchaseOrderHeaderAndShipInfo"/>
	                    				<span class="tooltip">${uiLabelMap.ProductLeaveSingleProductReceiving}</span>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductProductId}</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.lookupField value="${requestParameters.productId?if_exists}" formName="selectAllForm" name="productId" id="productId" fieldFormName="LookupProduct"/>
                  						<span class="tooltip">${uiLabelMap.ProductLeaveEntirePoReceiving}</span>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">&nbsp;</label>
								<div class="controls">
									<div class="span12">
										<a href="javascript:document.selectAllForm.submit();" class="btn btn-info btn-mini"><i class="fa-sign-in open-sans"></i>&nbsp;&nbsp;${uiLabelMap.DAReceiveProduct}</a>
									</div>
								</div>
							</div>
						</div><!--.span6-->
					</div><!--.row-fluid-->
	          	</form>
	        </#if>
		</div>
	</div>
</div>