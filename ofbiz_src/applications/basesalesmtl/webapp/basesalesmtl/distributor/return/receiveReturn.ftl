<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<style type="text/css">
	.jqx-window-olbius .jqx-window-content-olbius table tr {
		display:table-row;
	}
	.view-calendar input, .field-lookup input {
		width:60px;
	}
	@media screen and (-webkit-min-device-pixel-ratio:0) {
		.field-lookup a:before {
			margin-top:inherit;
		}
	}
	.jqx-window-olbius .jqx-window-content table.normal-table tr td {
		min-width: 50px;
	}
</style>
<div>
    <#if facilityId?exists>
        <#-- Receiving Results -->
        <#if receivedItems?has_content>
          	<#if "RETURN_RECEIVED" == returnHeader.getString("statusId")>
            	<h3 class="small blue lighter">${uiLabelMap.DAReturnCompletelyReceived}</h3>
          	</#if>
          	<br />
          	<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered normal-table">
				<thead>
	            	<tr>
	              		<th>${uiLabelMap.ProductReceipt}</th>
	              		<th>${uiLabelMap.CommonDate}</th>
	              		<th>${uiLabelMap.DAReturnOrderId}</th>
	              		<th>${uiLabelMap.ProductLine}</th>
	              		<th>${uiLabelMap.ProductProductId}</th>
	              		<th>${uiLabelMap.ProductPerUnitPrice}</th>
	              		<th>${uiLabelMap.ProductReceived}</th>
	            	</tr>
	            </thead>
	            <tbody>
	            	<#list receivedItems as item>
	              	<tr>
	                	<td>${item.receiptId}</td>
	                	<td>
	                		<#--${item.getString("datetimeReceived").toString()}-->
	                		<#if item.datetimeReceived?has_content>
	                			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(item.datetimeReceived, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
	                		</#if>
	                	</td>
	                	<td>${item.returnId}</td>
	                	<td>${item.returnItemSeqId}</td>
	                	<td>${item.productId?default("Not Found")}</td>
	                	<td><@ofbizCurrency amount=item.unitCost isoCode=returnHeader.currencyUomId/><#--${item.unitCost?default(0)?string("##0.00")}--></td>
	                	<td>${item.quantityAccepted?string.number}</td>
	              	</tr>
	            	</#list>
	            </tbody>
          	</table>
          	<br />
        </#if>

        <#-- Multi-Item Return Receiving -->
        <#if returnHeader?has_content>
        	<#if notOrderComponent?has_content>
        		<#assign formAction = "receiveReturnItems">
        	<#else>
        		<#assign formAction = "receiveReturnedProductAjax">
        	</#if>
          	<form method="post" action="<@ofbizUrl>${formAction}</@ofbizUrl>" name='selectAllForm' id="selectAllForm">
            	<#-- general request fields -->
            	<input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}" />
            	<input type="hidden" name="returnId" value="${requestParameters.returnId?if_exists}" />
            	<input type="hidden" name="_useRowSubmit" value="Y" />
            	<#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()>
            	<#assign rowCount = 0>
            	<div id="table-container">
            		<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered normal-table">
              			<#if !returnItems?exists || returnItems?size == 0>
                			<tr>
                  				<td colspan="2" class="label">${uiLabelMap.DANoItemsToReceive}</td>
                			</tr>
              			<#else>
		                  	<thead>
	                  			<tr>
	                  				<th>${uiLabelMap.DASeqId}</th>
			              			<th>${uiLabelMap.DAProductId}</th>
			              			<th style="width:170px">${uiLabelMap.DALocationInFacility}</th>
			              			<th>${uiLabelMap.ProductQtyReceived}</th>
			              			<th>${uiLabelMap.DAQtyRejected}</th>
			              			<#--<th>${uiLabelMap.DAInventoryItemType}</th>-->
			              			<th>${uiLabelMap.DAStatusProduct}</td>
				            		<th>${uiLabelMap.ProductPerUnitPrice}</th>
			              			<th colspan="1" style="width:50px">
	                  					<#--${uiLabelMap.ProductSelectAll}-->
	                  					<div>
	                  						<input type="checkbox" style="opacity: 1 !important; position: initial !important;" name="selectAll" value="Y" onclick="javascript:toggleAll(this, 'selectAllForm');" />
	                  					</div>
	                  				</th>
			            		</tr>
			            	</thead>
			            	<tbody>
			            		<#list returnItems as returnItem>
		                  			<#assign defaultQuantity = returnItem.returnQuantity - receivedQuantities[returnItem.returnItemSeqId]?double>
		                  			<#assign orderItem = returnItem.getRelatedOne("OrderItem", false)?if_exists>
		                  			<#if (orderItem?has_content && 0 < defaultQuantity)>
				                  		<#assign orderItemType = (orderItem.getRelatedOne("OrderItemType", false))?if_exists>
				                  		<input type="hidden" name="returnId_o_${rowCount}" value="${returnItem.returnId}" />
				                  		<input type="hidden" name="returnItemSeqId_o_${rowCount}" value="${returnItem.returnItemSeqId}" />
				                  		<input type="hidden" name="shipmentId_o_${rowCount}" value="${parameters.shipmentId?if_exists}" />
				                  		<input type="hidden" name="facilityId_o_${rowCount}" value="${requestParameters.facilityId?if_exists}" />
				                  		<input type="hidden" name="datetimeReceived_o_${rowCount}" value="${now}" />
				                  		<input type="hidden" name="quantityRejected_o_${rowCount}" value="0" />
				                  		<input type="hidden" name="comments_o_${rowCount}" value="${uiLabelMap.OrderReturnedItemRaNumber} ${returnItem.returnId}" />
			
			                  			<#assign unitCost = Static["org.ofbiz.order.order.OrderReturnServices"].getReturnItemInitialCost(delegator, returnItem.returnId, returnItem.returnItemSeqId)/>
			                  			<tr class="header-row">
					                  		<td>${uiLabelMap.ReturnItemSeqId}</td>
							              	<td>${uiLabelMap.ProductProductId}</td>
							              	<td>${uiLabelMap.ProductLocation}</td>
							              	<td>${uiLabelMap.ProductQtyReceived}</td>
							              	<td>${uiLabelMap.InventoryItemType}</td>
							              	<td>${uiLabelMap.ProductInitialInventoryItemStatus}</td>
								            <td>${uiLabelMap.ProductPerUnitPrice}</td>
							              	<td colspan="1" align="right">
					                  			${uiLabelMap.ProductSelectAll}
					                    		<input type="checkbox" style="opacity: 1 !important; position: initial !important;" name="selectAll" value="Y" onclick="javascript:toggleAll(this, 'selectAllForm');" />
					                  		</td>
							            </tr>
			                  			<tr>
			                      			<#assign productId = "">
			                      			<#if orderItem.productId?exists>
			                        			<#assign product = orderItem.getRelatedOne("Product", false)>
			                        			<#assign productId = product.productId>
			                            		<#assign serializedInv = product.getRelated("InventoryItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("inventoryItemTypeId", "SERIALIZED_INV_ITEM"), null, false)>
			                            		<input type="hidden" name="productId_o_${rowCount}" value="${product.productId}" />
			                            		<td>${returnItem.returnItemSeqId}</td>
			                           	 		<td width="45%">
				                                	<!--
				                                	<a href="/catalog/control/EditProduct?productId=${product.productId}${externalKeyParam?if_exists}" target="catalog" class="">${product.productId}&nbsp;-&nbsp;${product.productName?if_exists}</a>
				                                	-->
				                                	${product.productName?if_exists} [${product.productId}]
				                                	<#if serializedInv?has_content><font color='red'>**${uiLabelMap.ProductSerializedInventoryFound}**</font></#if>
			                            		</td>
		                          			<#elseif orderItem?has_content>
					                            <td width="45%">
					                                <b>${orderItemType.get("description",locale)}</b> : ${orderItem.itemDescription?if_exists}&nbsp;&nbsp;
					                                <input type="text" size="12" name="productId_o_${rowCount}" />
					                                <a href="/catalog/control/EditProduct?${externalKeyParam}" target="catalog" class="btn btn-small btn-info">${uiLabelMap.ProductCreateProduct}</a>
					                            </td>
			                          		<#else>
					                            <td width="45%">
					                                ${returnItem.get("description",locale)?if_exists}
					                            </td>
			                          		</#if>
			
			                          		<#-- location(s) -->
			                          		<td align="right">
			                            		<#assign facilityLocations = (product.getRelated("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId), null, false))?if_exists>
			                            		<#if facilityLocations?has_content>
			                              			<select name="locationSeqId_o_${rowCount}">
						                                <#list facilityLocations as productFacilityLocation>
						                                  	<#assign facility = productFacilityLocation.getRelatedOne("Facility", true)>
						                                  	<#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation", false)?if_exists>
						                                  	<#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOne("TypeEnumeration", true))?if_exists>
						                                  	<option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
						                                </#list>
			                                			<option value="">${uiLabelMap.ProductNoLocation}</option>
			                              			</select>
					                            <#else>
					                              	<span>
						                                <#if parameters.facilityId?exists>
						                                    <#assign LookupFacilityLocationView="LookupFacilityLocation?facilityId=${facilityId}">
						                                <#else>
						                                    <#assign LookupFacilityLocationView="LookupFacilityLocation">
						                                </#if>
						                                <@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId_o_${rowCount}" id="locationSeqId_o_${rowCount}" fieldFormName="${LookupFacilityLocationView}"/>
					                              	</span>
					                            </#if>
			                          		</td>
			                          		<td align="right">
			                            		<input type="text" class="width100px" name="quantityAccepted_o_${rowCount}" size="6" value="${defaultQuantity?string.number}" />
			                          		</td>
				                       		<td width='10%'>
			                              		<select name="inventoryItemTypeId_o_${rowCount}" size="1" id="inventoryItemTypeId_o_${rowCount}" onchange="javascript:setInventoryItemStatus(this,${rowCount});">
			                                 		<#list inventoryItemTypes as nextInventoryItemType>
			                                    		<option value='${nextInventoryItemType.inventoryItemTypeId}'
			                                	 		<#if (facility.defaultInventoryItemTypeId?has_content) && (nextInventoryItemType.inventoryItemTypeId == facility.defaultInventoryItemTypeId)>
			                                    			selected="selected"
			                                  			</#if>
			                                 			>${nextInventoryItemType.get("description",locale)?default(nextInventoryItemType.inventoryItemTypeId)}</option>
			                                 		</#list>
			                              		</select>
			                          		</td>
			                          		<td width="35%">
			                            		<select name="statusId_o_${rowCount}" size='1' id = "statusId_o_${rowCount}">
			                              			<option value="INV_RETURNED">${uiLabelMap.ProductReturned}</option>
			                              			<option value="INV_AVAILABLE">${uiLabelMap.ProductAvailable}</option>
			                              			<option value="INV_NS_DEFECTIVE" <#if returnItem.returnReasonId?default("") == "RTN_DEFECTIVE_ITEM">Selected</#if>>${uiLabelMap.ProductDefective}</option>
			                            		</select>
			                          		</td>
			                          		<#if serializedInv?has_content>
			                            		<td align="right" class="label">${uiLabelMap.ProductExistingInventoryItem}</td>
			                            		<td align="right">
			                              			<select name="inventoryItemId_o_${rowCount}">
			                                			<#list serializedInv as inventoryItem>
			                                  				<option>${inventoryItem.inventoryItemId}</option>
			                                			</#list>
			                              			</select>
			                            		</td>
			                          		<#else>
			                          	</#if>
			                          	<td align="right">
			                          		<#if unitCost?has_content>
			                          			<input type='text' class="width100px" name='unitCost_o_${rowCount}' size='6' value='<@ofbizCurrency amount=unitCost isoCode=returnHeader.currencyUomId/>' />	
			                          		<#else>
			                          			<#assign unitCost = 0>
			                          			<input type='text' class="width100px" name='unitCost_o_${rowCount}' size='6' value='<@ofbizCurrency amount=unitCost isoCode=returnHeader.currencyUomId/>' />
			                          		</#if>
			                          	</td>
			                    		<td align="right">
			                      			<input type="checkbox" style="opacity: 1 !important; position: initial !important;" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');" />
		                    			</td>
			                  			<#assign rowCount = rowCount + 1>
			              			<#else>
			              				<#-- ====================== THIS CODE RUN ======================== -->
										<#assign orderItemType = (orderItem.getRelatedOne("OrderItemType", false))?if_exists>
					                  	<input type="hidden" name="returnId_o_${rowCount}" value="${returnItem.returnId}" />
					                  	<input type="hidden" name="returnItemSeqId_o_${rowCount}" value="${returnItem.returnItemSeqId}" />
					                  	<input type="hidden" name="shipmentId_o_${rowCount}" value="${parameters.shipmentId?if_exists}" />
					                  	<input type="hidden" name="facilityId_o_${rowCount}" value="${requestParameters.facilityId?if_exists}" />
					                  	<input type="hidden" name="currencyUomId_o_${rowCount}" value="${returnHeader.currencyUomId?if_exists}"/>
					                  	<input type="hidden" name="datetimeReceived_o_${rowCount}" value="${now}" />
					                  	<input type="hidden" name="expireDate_o_${rowCount}" value="${returnItem.expireDate?if_exists}"/>
					                  	<input type="hidden" name="comments_o_${rowCount}" value="${uiLabelMap.OrderReturnedItemRaNumber} ${returnItem.returnId}" />
										<input type="hidden" name="inventoryItemTypeId_o_${rowCount}" id="inventoryItemTypeId_o_${rowCount}" value="<#if facility.defaultInventoryItemTypeId?has_content>${facility.defaultInventoryItemTypeId}<#else>NON_SERIAL_INV_ITEM</#if>"/>
										
					                  	<#assign unitCost = Static["org.ofbiz.order.order.OrderReturnServices"].getReturnItemInitialCost(delegator, returnItem.returnId, returnItem.returnItemSeqId)/>
					                  	<tr>
					                  		<td>${returnItem.returnItemSeqId}</td>
				                      		<#assign productId = "">
					                      	<#if orderItem.productId?exists>
				                       	 		<#assign product = orderItem.getRelatedOne("Product", false)>
					                        	<#assign productId = product.productId>
					                            <#assign serializedInv = product.getRelated("InventoryItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("inventoryItemTypeId", "SERIALIZED_INV_ITEM"), null, false)>
					                            <input type="hidden" name="productId_o_${rowCount}" value="${product.productId}" />
					                            <td>
					                                <!--
					                                	<a href="/catalog/control/EditProduct?productId=${product.productId}${externalKeyParam?if_exists}" target="catalog" class="">${product.productId}&nbsp;-&nbsp;${product.productName?if_exists}</a>
					                                -->
					                                ${product.productName?if_exists} [${product.productId}]
					                                <#if serializedInv?has_content><font color='red'>**${uiLabelMap.ProductSerializedInventoryFound}**</font></#if>
				                            	</td>
				                          	<#elseif orderItem?has_content>
					                            <td>
					                                <b>${orderItemType.get("description",locale)}</b> : ${orderItem.itemDescription?if_exists}&nbsp;&nbsp;
					                                <input type="text" size="12" name="productId_o_${rowCount}" />
					                                <a href="/catalog/control/EditProduct?${externalKeyParam}" target="catalog" class="btn btn-small btn-info">${uiLabelMap.ProductCreateProduct}</a>
					                            </td>
			                          		<#else>
					                            <td>
					                            	<input type="hidden" name="productId_o_${rowCount}" value="${returnItem.productId}" />
					                            	<#if returnItem.description?has_content>
					                            		${returnItem.get("description",locale)?if_exists}
					                            	<#else>
					                            		${returnItem.productId?if_exists}
					                            	</#if>
					                            </td>
				                          	</#if>
			
			                          		<#-- location(s) -->
				                          	<td>
				                            	<#assign facilityLocations = (product.getRelated("ProductFacilityLocation", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", facilityId), null, false))?if_exists>
				                            	<#if facilityLocations?has_content>
					                              	<select name="locationSeqId_o_${rowCount}">
					                                	<#list facilityLocations as productFacilityLocation>
					                                  		<#assign facility = productFacilityLocation.getRelatedOne("Facility", true)>
					                                  		<#assign facilityLocation = productFacilityLocation.getRelatedOne("FacilityLocation", false)?if_exists>
					                                  		<#assign facilityLocationTypeEnum = (facilityLocation.getRelatedOne("TypeEnumeration", true))?if_exists>
					                                  		<option value="${productFacilityLocation.locationSeqId}"><#if facilityLocation?exists>${facilityLocation.areaId?if_exists}:${facilityLocation.aisleId?if_exists}:${facilityLocation.sectionId?if_exists}:${facilityLocation.levelId?if_exists}:${facilityLocation.positionId?if_exists}</#if><#if facilityLocationTypeEnum?exists>(${facilityLocationTypeEnum.get("description",locale)})</#if>[${productFacilityLocation.locationSeqId}]</option>
					                                	</#list>
					                                	<option value="">${uiLabelMap.ProductNoLocation}</option>
					                              	</select>
				                            	<#else>
					                              	<span>
						                                <#if parameters.facilityId?exists>
						                                    <#assign LookupFacilityLocationView="LookupFacilityLocation?facilityId=${facilityId}">
						                                <#else>
						                                    <#assign LookupFacilityLocationView="LookupFacilityLocation">
						                                </#if>
						                                <@htmlTemplate.lookupField formName="selectAllForm" name="locationSeqId_o_${rowCount}" id="locationSeqId_o_${rowCount}" fieldFormName="${LookupFacilityLocationView}"/>
					                              	</span>
				                            	</#if>
				                          	</td>
			                          		<td align="right">
			                            		<input type="text" class="width100px" name="quantityAccepted_o_${rowCount}" size="6" value="${defaultQuantity?string.number}" />
			                          		</td>
					                       	<td>
					                       		<input type="text" class="width100px" name="quantityRejected_o_${rowCount}" size="6" value="0"/>
					                       	</td>
			                          		<td>
			                            		<select name="statusId_o_${rowCount}" size='1' id = "statusId_o_${rowCount}" style="width: 150px;">
				                              		<option value="INV_RETURNED">${uiLabelMap.ProductReturned}</option>
				                              		<option value="INV_AVAILABLE">${uiLabelMap.ProductAvailable}</option>
				                              		<option value="INV_NS_DEFECTIVE" <#if returnItem.returnReasonId?default("") == "RTN_DEFECTIVE_ITEM">Selected</#if>>${uiLabelMap.ProductDefective}</option>
			                            		</select>
			                          		</td>
			                          		<#if serializedInv?has_content>
		                           		 		<td align="right" class="label">${uiLabelMap.ProductExistingInventoryItem}</td>
			                            		<td align="right">
					                              	<select name="inventoryItemId_o_${rowCount}">
					                                	<#list serializedInv as inventoryItem>
					                                  		<option>${inventoryItem.inventoryItemId}</option>
				                                		</#list>
					                              	</select>
			                           	 		</td>
			                          		<#else>
			                          		</#if>
		                          			<td align="right">
					                          	<#if unitCost?has_content>
					                          		<input type='text' class="width100px" name='unitCost_o_${rowCount}' size='6' value='<@ofbizCurrency amount=unitCost isoCode=returnHeader.currencyUomId/>' />	
					                          	<#else>
					                          		<#assign unitCost = 0>
					                          		<input type='text' class="width100px" name='unitCost_o_${rowCount}' size='6' value='<@ofbizCurrency amount=unitCost isoCode=returnHeader.currencyUomId/>' />
					                          	</#if>
			                          		</td>
						                    <td align="right" style="width:50px">
						                      	<input type="checkbox" style="opacity: 1 !important; position: initial !important;" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');" />
						                    </td>
			                  				<#assign rowCount = rowCount + 1>
		              				</#if>
		            			</#list>
		            			<#assign isRunning = true/>
				                <#if rowCount == 0>
				            		</tr>
				            		<tr>
				                    	<td colspan="8" class="label">${uiLabelMap.ProductNoItemsReturn} #${returnHeader.returnId} ${uiLabelMap.ProductToReceive}.</td>
				                  	</tr>
				                </#if>
			                </tbody>
              			</#if>
            		</table>
            	</div>
            	<input type="hidden" name="_rowCount" value="${rowCount}" />
      		</form>
      		<table style="width:100%">
      			<tr>
      				<td align="left" style="max-width:360px; min-width:360px">
      					<input style="margin-right: 5px;" id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />
      				</td>
					<td align="right" style="max-width:360px; min-width:360px">
				       	<#if isRunning?exists && isRunning>
			      			<#if rowCount == 0>
			      				<input style="margin-right: 5px;" id="alterReturnToReceive" type="button" value="${uiLabelMap.ProductReturnToReceiving}" />
			                <#else>
			                	<input style="margin-right: 5px;" id="alterReceiveSelectedProduct" type="button" value="${uiLabelMap.ProductReceiveSelectedProduct}" />
			                </#if>
			      		</#if>
				    </td>
      			</tr>
      		</table>
      		<script language="JavaScript" type="text/javascript">
      			//selectAll('selectAllForm');
      		</script>
      		<#-- Initial Screen -->
		<#else>
          	<form name="selectAllForm" method="post" action="<@ofbizUrl>ReceiveReturn</@ofbizUrl>">
            	<input type="hidden" name="facilityId" value="${requestParameters.facilityId?if_exists}" />
            	<input type="hidden" name="initialSelected" value="Y" />
            	<table cellspacing="0" class="basic-table">
              		<tr><td colspan="4"><h3 class="small blue lighter">${uiLabelMap.ProductReceiveReturn}</h3></td></tr>
              		<tr>
                		<td align='right' class="olbius-label">${uiLabelMap.ProductReturnNumber}</td>
                		<td>&nbsp;</td>
                		<td width="90%">
                  			<input type="text" name="returnId" size="20" maxlength="20" value="${requestParameters.returnId?if_exists}" />
                		</td>
                		<td>&nbsp;</td>
              		</tr>
              		<tr>
                		<td colspan="2">&nbsp;</td>
                		<td colspan="2">
                  			<a href="javascript:document.selectAllForm.submit();" class="btn btn-small btn-info">${uiLabelMap.ProductReceiveProduct}</a>
                		</td>
              		</tr>
            	</table>
          	</form>
        </#if>
        
        <script language="JavaScript" type="text/javascript">
		    function setInventoryItemStatus(selection,index) {
		        var statusId = "statusId_o_" + index;
		        jObjectStatusId = jQuery("#" + statusId);
		        jQuery.ajax({
		            url: 'UpdatedInventoryItemStatus',
		            data: {inventoryItemType: selection.value, inventoryItemStatus: jObjectStatusId.val()},
		            type: "POST",
		            success: function(data){jObjectStatusId.html(data);}
		        });
		    }
		</script>
    <#elseif listFacilityManager?exists>
    	<table style="width:100%">
			<tr>
				<td align="right" style="max-width:360px; min-width:360px" class="required">${uiLabelMap.DAChooseFacilityImportProduct}</td>
				<td align="left" style="max-width:360px; min-width:360px">
			       	<div>
			       		<select id="facilityId">
			       			<#list listFacilityManager as itemFacility>
			       				<option value="${itemFacility.facilityId?if_exists}">${itemFacility.facilityId?if_exists}</option>
			       			</#list>
			       		</select>
			       	</div>
			    </td>
			</tr>
		    <tr>
		        <td align="right" style="max-width:360px; min-width:360px"><input type="button" id="alterSave" value="${uiLabelMap.DAChoose}" /></td>
		        <td align="left" style="max-width:360px; min-width:360px"><input style="margin-right: 5px;" id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	    	</tr>
		</table>
	</#if>
	<script type="text/javascript">
	//<![CDATA[
		$(document).ready(function(){
			$.jqx.theme = 'olbius';
			theme = $.jqx.theme;
			// Create Window
			$("#alterpopupWindow").jqxWindow({
				cancelButton: $("#alterCancel")
			});
			if ($("#alterCancel").length > 0) {
				$("#alterCancel").jqxButton({width: 100, theme: theme});
			}
			if ($("#alterReturnToReceive").length > 0) {
				$("#alterReturnToReceive").jqxButton({width: 250, height: 32, theme: theme});
				$("#alterReturnToReceive").click(function () {
					$.ajax({
						type: 'POST',
						url: 'receiveReturnAjax',
						data: {
							returnId: '<#if returnHeader?exists>${returnHeader.returnId?if_exists}</#if>'
						},
						beforeSend: function () {
							$("#info_loader").show();
						}, 
			            success: function (data) {
			        		$("#alterpopupContent").html(data);
			        		$("#nameFacilitySpan").text("<#if facility?exists>\"${facility.facilityName?default("Not Defined")}\" [${facilityId?if_exists}]</#if>");
			        		$("#nameShipmentSpan").text("${StringUtil.wrapString(uiLabelMap.CommonBy)} ${StringUtil.wrapString(uiLabelMap.Shipment)} ${shipmentId?if_exists}");
			            },
			            error: function () {
			                //commit(false);
			            },
			            complete: function() {
					        $("#info_loader").hide();
					    }
					});
				});
			}
			if ($("#alterReceiveSelectedProduct").length > 0) {
				$("#alterReceiveSelectedProduct").jqxButton({width: 250, height: 32, theme: theme});
				$("#alterReceiveSelectedProduct").click(function () {
					var data = $("#selectAllForm").serialize();
					var url = $("#selectAllForm").attr('action');
					$.ajax({
						type: 'POST',
						url: url,
						data: data,
						beforeSend: function () {
							$("#info_loader").show();
						}, 
			            success: function (data) {
			        		$("#alterpopupContent").html(data);
			        		$("#nameFacilitySpan").text("<#if facility?exists>\"${facility.facilityName?default("Not Defined")}\" [${facilityId?if_exists}]</#if>");
			        		$("#nameShipmentSpan").text("${StringUtil.wrapString(uiLabelMap.CommonBy)} ${StringUtil.wrapString(uiLabelMap.Shipment)} ${shipmentId?if_exists}");
			            },
			            error: function () {
			                //commit(false);
			            },
			            complete: function() {
					        $("#info_loader").hide();
					    }
					});
				});
			}
			if ($("#alterSave").length > 0) {
				$("#alterSave").jqxButton({width: 100, theme: theme});
				// update the edited row when the user clicks the 'Save' button.
				$("#alterSave").click(function () {
					$.ajax({
						type: 'POST',
						url: 'receiveReturnAjax',
						data: {
							facilityId: $("#facilityId").val(),
							returnId: '${returnHeader.returnId?if_exists}'
						},
						beforeSend: function () {
							$("#info_loader").show();
						}, 
			            success: function (data) {
			        		$("#alterpopupContent").html(data);
			        		$("#nameFacilitySpan").text("<#if facility?exists>\"${facility.facilityName?default("Not Defined")}\" [${facilityId?if_exists}]</#if>");
			        		$("#nameShipmentSpan").text("${StringUtil.wrapString(uiLabelMap.CommonBy)} ${StringUtil.wrapString(uiLabelMap.Shipment)} ${shipmentId?if_exists}");
			            },
			            error: function () {
			                //commit(false);
			            },
			            complete: function() {
					        $("#info_loader").hide();
					    }
					});
				});
			}
		});
	//]]>
	</script>
</div>
