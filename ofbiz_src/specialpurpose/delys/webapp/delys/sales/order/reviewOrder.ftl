
<h3 style="text-align:center;font-weight:bold">
	<#-- order name -->
    <#if (orderName?has_content)>
		${orderName}
	<#else>
		${uiLabelMap.DAOrderFormTitle}
	</#if>
</h3>

<div class="widget-body">	
	<div class="widget-main">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAOrderName}:</label>
							<div class="controls-desc">
								<b>
									<#if orderName?exists && orderName?has_content>
										${orderName}
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAOrderId}:</label>
							<div class="controls-desc">
								<b>
									<#if cart?exists && cart.orderId?exists && cart.orderId?has_content>
										${cart.orderId}
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DACurrency}:</label>
							<div class="controls-desc">
								<b>
									<#if currencyUomId?exists && currencyUomId?has_content>
										${currencyUomId}
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DADesiredDeliveryDate}:</label>
							<div class="controls-desc">
								<b>
									<#if cart?exists && cart.getDefaultItemDeliveryDate()?exists>
										${cart.getDefaultItemDeliveryDate()}
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DACustomer}:</label>
							<div class="controls-desc">
								<b>
									<#assign displayPartyId = cart.getOrderPartyId()>
									<#if displayPartyId?has_content>
						                <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayPartyId, "userLogin", userLogin))/>
						                ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						
						<#if !(cart?exists)><#assign cart = shoppingCart?if_exists/></#if>
						<#if cart?exists>
							<#list cart.getShipGroups() as cartShipInfo>
						     	<#assign numberOfItems = cartShipInfo.getShipItems().size()>
						      	<#if (numberOfItems > 0)>
	          						<#assign contactMech = delegator.findOne("ContactMech", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", cartShipInfo.contactMechId), false)?if_exists />
						          	<#if contactMech?has_content>
							            <#assign address = contactMech.getRelatedOne("PostalAddress", false)?if_exists />
						          	</#if>
	
				         	 		<#if address?exists>
				         	 			<div class="control-group">
											<label class="control-label-desc">${uiLabelMap.OrderDestination}:</label>
											<div class="controls-desc">
												<b>
													<#if address.toName?has_content><b>${uiLabelMap.CommonTo}:</b>&nbsp;${address.toName}<br /></#if>
										            <#if address.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b>&nbsp;${address.attnName}<br /></#if>
										            <#if address.address1?has_content>${address.address1}<br /></#if>
										            <#if address.address2?has_content>${address.address2}<br /></#if>
										            <#if address.city?has_content>${address.city}</#if>
										            <#if address.stateProvinceGeoId?has_content>&nbsp;${address.stateProvinceGeoId}</#if>
										            <#if address.postalCode?has_content>, ${address.postalCode?if_exists}</#if>
												</b>
											</div>
										</div>
						          	</#if>
						         </#if>
	      					</#list>
						</#if>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DADebt}:</label>
							<div class="controls-desc">
								<b>
									<#assign displayPartyId = cart.getOrderPartyId()>
									<#if displayPartyId?has_content>
						                <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayPartyId, "userLogin", userLogin))/>
						                ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DASup}:</label>
							<div class="controls-desc">
								<b>
									<#assign displayPartyId = cart.getOrderPartyId()>
									<#if displayPartyId?has_content>
										<#assign supList = delegator.findByAnd("PartyRelationship", {"partyIdTo" : displayPartyId, "roleTypeIdFrom" : "DELYS_SALESSUP_GT"}, null, false)>
						               	<#list supList as supItem>
						               		<#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", supItem.partyIdFrom, "userLogin", userLogin))/>
						                	${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
						               	</#list>
									<#else>
										${uiLabelMap.DANotData}
									</#if>
								</b>
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-->
			</div><!--.form-horizontal.form-decrease-padding-->
			<div class="form-horizontal basic-custom-form" style="display: block;">
				<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
					<thead>
						<tr>
							<td rowspan="2">${uiLabelMap.DANo}</td>
							<td colspan="2" class="center">${uiLabelMap.DAProduct}</td>
							<td rowspan="2" style="width:10px">${uiLabelMap.DAPackingPerTray}</td>
							<td colspan="3" align="center" class="center">${uiLabelMap.DAQuantity}</td>
							<td rowspan="2">${uiLabelMap.DASumTray}</td>
						  	<td rowspan="2" align="right" class="align-center" style="width:60px">${uiLabelMap.DAPriceBeforeVAT}</td>
						  	<td rowspan="2" align="right" class="align-right">${uiLabelMap.DAAdjustment}</td>
							<td rowspan="2" align="right" class="align-right">${uiLabelMap.DASubTotal} <br />${uiLabelMap.DAParenthesisBeforeVAT}</td>
							<#--<td rowspan="2" style="width:60px">${uiLabelMap.DAPriceAfterVAT}</td>
							<td colspan="2" class="color-red">${uiLabelMap.DAInvoicePrice}</td>-->
						</tr>
						<tr>
							<td>${uiLabelMap.DAProductId} - ${uiLabelMap.DAProductName}</td>
							<td style="width:15px">${uiLabelMap.DABarcode}</td>
							<td>${uiLabelMap.DAOrdered}</td>
							<td>${uiLabelMap.DAPromos}</td>
							<td>${uiLabelMap.DASum}</td>
							<#--
							<td class="color-red">${uiLabelMap.DAPrice}</td>
							<td class="color-red">${uiLabelMap.OrderSubTotal}</td>
							-->
						</tr>
					</thead>
					<tbody>
					<#list orderItems?if_exists as orderItem>
            			<#assign itemType = orderItem.getRelatedOne("OrderItemType", false)?if_exists>
            			<tr>
						<td>${orderItem_index + 1}</td>
						<#if orderItem.productId?exists && orderItem.productId == "_?_">
			                <td colspan="1" valign="top">
			                  <b><div> &gt;&gt; ${orderItem.itemDescription}</div></b>
			                </td>
              			<#else>
            				<td valign="top">
			                  	<div>
			                    	<#if orderItem.productId?exists>
			                      		<a href="<@ofbizUrl>product?product_id=${orderItem.productId}</@ofbizUrl>">${orderItem.productId}</a>
			                      		 - ${orderItem.itemDescription}
			                    	<#else>
			                      		<b>${itemType?if_exists.description?if_exists}</b> : ${orderItem.itemDescription?if_exists}
			                    	</#if>
			                  	</div>
               				</td>
                			<td><#--Barcode--></td>
                			<td><#--QC/khay--></td>
			                <td align="right" valign="top">
			                  	<div nowrap="nowrap">${orderItem.quantity?string.number}</div>
			                </td>
			                <td><#--km--></td>
			                <td><#--sum--></td>
			                <td><#--sum tray--></td>
			                <td align="right" valign="top"><#--unit price-->
			                  	<div nowrap="nowrap"><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></div>
			                </td>
			                <td align="right" valign="top"><#--adjustment-->
			                  	<div nowrap="nowrap"><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentsTotal(orderItem) isoCode=currencyUomId/></div>
			                </td>
			                <td align="right" valign="top" nowrap="nowrap"><#--DASubTotalBeforeVAT-->
			                  	<div><@ofbizCurrency amount=localOrderReadHelper.getOrderItemSubTotal(orderItem) isoCode=currencyUomId/></div>
			                </td>
			                <#--unit price after vat <td></td>-->
          				</#if>
						
						<#-- show info from workeffort if it was a rental item -->
			            <#if orderItem.orderItemTypeId?exists && orderItem.orderItemTypeId == "RENTAL_ORDER_ITEM">
			                <#assign WorkOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment", null, null, false)?if_exists>
			                <#if WorkOrderItemFulfillments?has_content>
			                    <#list WorkOrderItemFulfillments as WorkOrderItemFulfillment>
			                        <#assign workEffort = WorkOrderItemFulfillment.getRelatedOne("WorkEffort", true)?if_exists>
			                          <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="8"><div>${uiLabelMap.CommonFrom}: ${workEffort.estimatedStartDate?string("yyyy-MM-dd")} ${uiLabelMap.CommonTo}: ${workEffort.estimatedCompletionDate?string("yyyy-MM-dd")} ${uiLabelMap.OrderNbrPersons}: ${workEffort.reservPersons}</div></td></tr>
			                        <#break><#-- need only the first one -->
			                    </#list>
			                </#if>
			            </#if>

            			<#-- now show adjustment details per line item -->
            			<#assign itemAdjustments = localOrderReadHelper.getOrderItemAdjustments(orderItem)>
            			<#list itemAdjustments as orderItemAdjustment>
              				<tr>
                				<td align="right" colspan="3">
                  					<div style="font-size: smaller;">
                    					<b><i>${uiLabelMap.OrderAdjustment}</i>:</b> 
                    					<b>${localOrderReadHelper.getAdjustmentType(orderItemAdjustment)}</b>&nbsp;
                    					<#if orderItemAdjustment.description?has_content>: ${StringUtil.wrapString(orderItemAdjustment.get("description",locale))}</#if>

                    					<#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
                      						<#if orderItemAdjustment.primaryGeoId?has_content>
                        						<#assign primaryGeo = orderItemAdjustment.getRelatedOne("PrimaryGeo", true)/>
						                        <#if primaryGeo.geoName?has_content>
						                            <b>${uiLabelMap.OrderJurisdiction}:</b> ${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
						                        </#if>
                        						<#if orderItemAdjustment.secondaryGeoId?has_content>
						                          	<#assign secondaryGeo = orderItemAdjustment.getRelatedOne("SecondaryGeo", true)/>
						                          	(<b>in:</b> ${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
                								</#if>
                      						</#if>
					                      	<#if orderItemAdjustment.sourcePercentage?exists><b>${uiLabelMap.OrderRate}:</b> ${orderItemAdjustment.sourcePercentage}%</#if>
					                      	<#if orderItemAdjustment.customerReferenceId?has_content><b>${uiLabelMap.OrderCustomerTaxId}:</b> ${orderItemAdjustment.customerReferenceId}</#if>
					                      	<#if orderItemAdjustment.exemptAmount?exists><b>${uiLabelMap.OrderExemptAmount}:</b> ${orderItemAdjustment.exemptAmount}</#if>
                    					</#if>
                  					</div>
                				</td>
				                <td>&nbsp;</td>
				                <td>&nbsp;</td>
				                <td>&nbsp;</td>
				                <td>&nbsp;</td>
				                <td>&nbsp;</td>
				                <td>&nbsp;</td>
				                <td align="right">
				                  	<div style="font-size: xx-small;"><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment) isoCode=currencyUomId/></div>
				                </td>
                				<td>&nbsp;</td>
                				<#if maySelectItems?default(false)><td>&nbsp;</td></#if>
              				</tr>
            			</#list>
					</#list>
					
					<#if !orderItems?has_content>
             			<tr><td colspan="11"><font color="red">${uiLabelMap.checkhelpertotalsdonotmatchordertotal}</font></td></tr>
           			</#if>

          			<tr>
            			<td align="right" colspan="10"><div><b>${uiLabelMap.OrderSubTotal}</b></div></td>
            			<td align="right" nowrap="nowrap">
            				<div>&nbsp;<#if orderSubTotal?exists><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></#if></div>
        				</td>
          			</tr>
          			<#list headerAdjustmentsToShow?if_exists as orderHeaderAdjustment>
		            <tr>
		              	<td align="right" colspan="10"><div><b>${localOrderReadHelper.getAdjustmentType(orderHeaderAdjustment, locale)}</b></div></td>
		              	<td align="right" nowrap="nowrap"><div><@ofbizCurrency amount=localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></div></td>
		            </tr>
          			</#list>
		          	<#--
		          	<tr>
			            <td align="right" colspan="10"><div><b>${uiLabelMap.FacilityShippingAndHandling}</b></div></td>
			            <td align="right" nowrap="nowrap"><div><#if orderShippingTotal?exists><@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId/></#if></div></td>
		          	</tr>
		          	-->
		          	<tr>
			            <td align="right" colspan="10"><div><b>${uiLabelMap.OrderSalesTax}</b></div></td>
			            <td align="right" nowrap="nowrap"><div><#if orderTaxTotal?exists><@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/></#if></div></td>
		          	</tr>
		          	<tr>
			            <td align="right" colspan="10"><div><b>${uiLabelMap.OrderGrandTotal}</b></div></td>
			            <td align="right" nowrap="nowrap">
			              <div><#if orderGrandTotal?exists><@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/></#if></div>
			            </td>
		          	</tr>
					</tbody>
				</table>
			</div><!--.form-horizontal-->
		</div><!--.row-fluid-->
	</div><!--.widget-main-->
</div><!--.widget-body-->
