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
	.checkbox-custom {
		opacity: 1 !important; position: initial !important;
		margin-bottom: 8px !important;
	}
</style>

<div class="row-fluid" style="margin-top:20px">
	<div class="span12">
		<h4>
			${uiLabelMap.BSOrderGeneralInfo}: ${uiLabelMap.OrderReturnFromOrder} ${uiLabelMap.CommonNbr} <a href="<@ofbizUrl>viewOrder?orderId=${orderId}</@ofbizUrl>" target="_blank">${orderId}</a>
		</h4>
		
		<#if orh?exists>
			<div class="row-fluid">
				<div class="span12">
					<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.OrderOrderTotal}:</label>
							</div>
							<div class="div-inline-block">
								<span><@ofbizCurrency amount=orh.getOrderGrandTotal() isoCode=orh.getCurrency()/></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.OrderAmountAlreadyCredited}:</label>
							</div>
							<div class="div-inline-block">
								<span><@ofbizCurrency amount=orh.getOrderReturnedCreditTotalBd() isoCode=orh.getCurrency()/></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.OrderAmountAlreadyRefunded}:</label>
							</div>
							<div class="div-inline-block">
								<span><@ofbizCurrency amount=orh.getOrderReturnedRefundTotalBd() isoCode=orh.getCurrency()/></span>
							</div>
						</div>
					</div><!--.form-horizontal-->
				</div><!--.span12-->
			</div><!--.row-fluid-->
		</#if>
	</div><!--.span12-->
</div><!--.row-fluid-->

<#if returnableItems?has_content>
	<div class="row-fluid">
		<div class="span12">
			<span><b>${uiLabelMap.CommonSelectAll}</b></span>&nbsp;
			<input type="checkbox" class="checkbox-custom" name="selectAll" value="Y" onclick="javascript:toggleAll(this, '${selectAllFormName}');"/>
			
			<table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
				<#-- information about orders and amount refunded/credited on past returns -->
				<thead>
					<tr class="header-row">
						<th align="right">${uiLabelMap.BSChoose}</th>
				      	<th>${uiLabelMap.BSProductId}</th>
				      	<th>${uiLabelMap.CommonDescription}</th>
				      	<th>${uiLabelMap.BSOrderedQty}</th>
				      	<th>${uiLabelMap.OrderReturnQty}</th>
				      	<th>${uiLabelMap.OrderUnitPrice}</th>
				      	<th>${uiLabelMap.OrderReturnPrice} *</th>
				      	<th>${uiLabelMap.OrderReturnReason}</th>
				      	<#--<th>${uiLabelMap.OrderReturnType}</th>
				      	<th>${uiLabelMap.OrderItemStatus}</th>-->
				    </tr>
				</thead>
				
				<tbody>
					<#assign rowCount = 0>
			      	<#assign alt_row = false>
			      	
					<#list returnableItems.keySet() as orderItem>
				        <#if orderItem.getEntityName() == "OrderAdjustment">
				            <#-- this is an order item adjustment -->
				            <#assign returnAdjustmentType = returnItemTypeMap.get(orderItem.get("orderAdjustmentTypeId"))/>
				            <#assign adjustmentType = orderItem.getRelatedOne("OrderAdjustmentType", false)/>
				            <#assign description = orderItem.description?default(adjustmentType.get("description",locale))/>
							
				            <tr id="returnItemId_tableRow_${rowCount}">
				            	<td>
				                	<input type="checkbox" class="checkbox-custom" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, '${selectAllFormName}'); highlightRow(this,'returnItemId_tableRow_${rowCount}');"/>
				              	</td>
								<td colspan="4">
					            	<input type="hidden" name="returnAdjustmentTypeId_o_${rowCount}" value="${returnAdjustmentType}"/>
					            	<input type="hidden" name="orderAdjustmentId_o_${rowCount}" value="${orderItem.orderAdjustmentId}"/>
									${description?default("N/A")}
				              	</td>
				              	<td class="align-right">
				              		<@ofbizCurrency amount=orderItem.amount isoCode=orderHeader.currencyUom/>
				                	<#--<input type="text" size="8" name="amount_o_${rowCount}" <#if orderItem.amount?has_content>value="${orderItem.amount?string("##0.00")}"</#if>/>-->
				              	</td>
				              	<td colspan="2">
				              		<input type="hidden" name="returnTypeId_o_${rowCount}" value="RTN_REFUND"/>
				              		<#--
					                <select name="returnTypeId_o_${rowCount}">
					                  <#list returnTypes as type>
					                  <option value="${type.returnTypeId}" <#if type.returnTypeId == "RTN_REFUND">selected="selected"</#if>>${type.get("description",locale)?default(type.returnTypeId)}</option>
					                  </#list>
					                </select>
					                -->
				              	</td>
				            </tr>
				        <#else>
				            <#-- this is an order item -->
				            <#assign returnItemType = (returnItemTypeMap.get(returnableItems.get(orderItem).get("itemTypeKey")))?if_exists/>
				            <#-- need some order item information -->
				            <#assign orderHeader = orderItem.getRelatedOne("OrderHeader", false)>
				            <#assign itemCount = orderItem.quantity>
				            <#assign itemPrice = orderItem.unitPrice>
				            <#assign product = orderItem.getRelatedOne("Product", false)!/> 
				            <#-- end of order item information -->
							
				            <tr id="returnItemId_tableRow_${rowCount}">
				            	<td>
				                	<input type="checkbox" class="checkbox-custom" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, '${selectAllFormName}');highlightRow(this,'returnItemId_tableRow_${rowCount}');"/>
				              	</td>
				            	<td>
				                	${product?if_exists.productCode?default(product.productId)}
				            	</td>
					            <td>
					            	<input type="hidden" name="returnItemTypeId_o_${rowCount}" value="${returnItemType}"/>
					            	<input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}"/>
					            	<input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"/>
					            	<input type="hidden" name="description_o_${rowCount}" value="${orderItem.itemDescription?if_exists}"/>
					              	<#if orderItem.productId?exists>
					                	<input type="hidden" name="productId_o_${rowCount}" value="${orderItem.productId}"/>
					              	</#if>
					              	<div class="width250px">${orderItem.itemDescription?if_exists}</div>
					          	</td>
				              	<td>
				                	<div>${orderItem.quantity?string.number}</div>
				              	</td>
				              	<td>
				                	<input type="text" class="width100px align-right" size="6" name="returnQuantity_o_${rowCount}" value="${returnableItems.get(orderItem).get("returnableQuantity")}"/>
				              	</td>
				              	<td class="align-right">
					                <div><@ofbizCurrency amount=orderItem.unitPrice isoCode=orderHeader.currencyUom/></div>
				              	</td>
				              	<td class="align-right">
					                <#if orderItem.productId?exists>
					                  	<#assign product = orderItem.getRelatedOne("Product", false)/>
					                  	<#if product.productTypeId == "ASSET_USAGE_OUT_IN">
					                    	<input type="hidden" size="8" name="returnPrice_o_${rowCount}" class="width100px" value="0.00"/>
					                    	<@ofbizCurrency amount=0 isoCode=orderHeader.currencyUom/>
					                  	<#else>
					                  		<#assign unitPriceOrderItem = returnableItems.get(orderItem).get("returnablePrice")?default(0)/>
					                    	<input type="hidden" size="8" name="returnPrice_o_${rowCount}" class="width100px" value="${unitPriceOrderItem}"/>
					                  		<@ofbizCurrency amount=unitPriceOrderItem isoCode=orderHeader.currencyUom/>
										</#if>
				                	</#if>
				              	</td>
				              	<td>
				                	<select name="returnReasonId_o_${rowCount}">
				                  		<#list returnReasons as reason>
				                  		<option value="${reason.returnReasonId}">${reason.get("description",locale)?default(reason.returnReasonId)}</option>
				                  		</#list>
			                		</select>
			                		<input type="hidden" name="returnTypeId_o_${rowCount}" value="RTN_REFUND"/>
			                		<input type="hidden" name="expectedItemStatus_o_${rowCount}" value="INV_RETURNED"/>
				                	<#--
				                	<select name="returnTypeId_o_${rowCount}">
				                  		<#list returnTypes as type>
				                  			<option value="${type.returnTypeId}" <#if type.returnTypeId=="RTN_REFUND">selected="selected"</#if>>${type.get("description",locale)?default(type.returnTypeId)}</option>
				                  		</#list>
			                		</select>
			                		<select name="expectedItemStatus_o_${rowCount}">
				                  		<option value="INV_RETURNED">${uiLabelMap.OrderReturned}</option>
				                  		<option value="INV_RETURNED">---</option>
				                  		<#list itemStts as status>
				                    		<option value="${status.statusId}">${status.get("description",locale)}</option>
				                  		</#list>
			                		</select>
			                		-->
				              	</td>
				            </tr>
				        </#if>
				        <#assign rowCount = rowCount + 1>
				        <#-- toggle the row color -->
				        <#assign alt_row = !alt_row>
					</#list>
				</tbody>
			</table>
		</div>
	</div>
	
	<div class="row-fluid margin-top20">
		<div class="span12">
			<h4>
				${uiLabelMap.BSReturnOrderAdjustments}
			</h4>
			
			<table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
				<#if orderHeaderAdjustments?has_content>
			      	<tr class="header-row">
			      		<td>${uiLabelMap.BSChoose}</td>
				        <td>${uiLabelMap.CommonDescription}</td>
				        <td>${uiLabelMap.CommonAmount}</td>
				        <td>${uiLabelMap.OrderReturnType}</td>
			      	</tr>
			      	<#list orderHeaderAdjustments as adj>
				        <#assign returnAdjustmentType = returnItemTypeMap.get(adj.get("orderAdjustmentTypeId"))/>
				        <#assign adjustmentType = adj.getRelatedOne("OrderAdjustmentType", false)/>
				        <#assign description = adj.description?default(adjustmentType.get("description",locale))/>
						
			        	<tr>
			        		<td>
			            		<input type="checkbox" class="checkbox-custom" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, '${selectAllFormName}');"/>
			          		</td>
			          		<td>
						        <input type="hidden" name="returnAdjustmentTypeId_o_${rowCount}" value="${returnAdjustmentType}"/>
						        <input type="hidden" name="orderAdjustmentId_o_${rowCount}" value="${adj.orderAdjustmentId}"/>
						        <input type="hidden" name="returnItemSeqId_o_${rowCount}" value="_NA_"/>
						        <input type="hidden" name="description_o_${rowCount}" value="${description}"/>
					            <div>
					              	${description?default("N/A")}
					            </div>
			          		</td>
			          		<td>
			            		<input type="text" size="8" name="amount_o_${rowCount}" <#if adj.amount?has_content>value="${adj.amount?string("##0.00")}"</#if>/>
			          		</td>
			          		<td>
					            <select name="returnTypeId_o_${rowCount}">
					              <#list returnTypes as type>
					              <option value="${type.returnTypeId}" <#if type.returnTypeId == "RTN_REFUND">selected="selected"</#if>>${type.get("description",locale)?default(type.returnTypeId)}</option>
					              </#list>
					            </select>
			          		</td>
			        	</tr>
			        	<#assign rowCount = rowCount + 1>
			      	</#list>
			    <#else>
		     	 	<tr><td colspan="9">${uiLabelMap.OrderNoOrderAdjustments}</td></tr>
			    </#if>
			
			    <#assign manualAdjRowNum = rowCount/>
			    <tr>
			        <td colspan="9">
			    		<input type="hidden" name="returnItemTypeId_o_${rowCount}" value="RET_MAN_ADJ"/>
			    		<input type="hidden" name="returnItemSeqId_o_${rowCount}" value="_NA_"/>
			        </td>
			    </tr>
		    </table>
	    </div><!--.span12-->
    </div><!--.row-fluid-->
	
	<div class="row-fluid margin-top20">
		<div class="span12">
			<h4>
				${uiLabelMap.BSReturnManualAdjustment}
			</h4>
			
			<table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
				<tr>
					<td>
			        	<input type="checkbox" class="checkbox-custom" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, '${selectAllFormName}');"/>
			      	</td>
					<td>
						<input type="text" size="30" name="description_o_${rowCount}" />
					</td>
					<td>
						<input type="text" size="8" name="amount_o_${rowCount}" value="${0.00?string("##0.00")}"/>
					</td>
					<td>
						<select name="returnTypeId_o_${rowCount}">
					    	<#list returnTypes as type>
					    		<option value="${type.returnTypeId}" <#if type.returnTypeId == "RTN_REFUND">selected="selected"</#if>>${type.get("description",locale)?default(type.returnTypeId)}</option>
					    	</#list>
				    	</select>
			      	</td>
			    </tr>
		    	<#assign rowCount = rowCount + 1>
				<!-- final row count -->
			    <tr>
			      	<td colspan="9" align="right">
			    		<input type="hidden" name="_rowCount" value="${rowCount}"/>
			      	</td>
			    </tr>
		    	<tr>
		    		<td>&nbsp;</td>
		      		<td colspan="3" class="tooltip checkbox-custom">*&nbsp;${uiLabelMap.OrderReturnPriceNotIncludeTax}</td>
			      	<td colspan="5">&nbsp;</td>
		    	</tr>
			</table>
		</div><!--.span12-->
    </div><!--.row-fluid-->
    
    <div class="margin-top20 margin-bottom20 pull-right">
		<a href="javascript:document.${selectAllFormName}.submit()" class="btn btn-small btn-primary">${uiLabelMap.OrderReturnSelectedItems}</a>
	</div>
<#else>
	<div class="alert alert-info">${uiLabelMap.OrderReturnNoReturnableItems} #${orderId}</div>
</#if>
