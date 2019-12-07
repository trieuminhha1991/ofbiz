<#assign currencyUomId = returnHeader.currencyUomId?default("")/>
<div id="general-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "general-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div class="title-status" id="statusTitle">
			${orderStatusMgs?if_exists}
		</div>
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
				${uiLabelMap.BSReturnOrderFormTitle}
			</h3>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSReturnId}:</label>
								</div>
								<div class="div-inline-block">
									<span>${returnHeader.returnId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.ExportFromFacility}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${destinationFacilityDetail?if_exists}
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.ReturnFrom}:</label><#--ReturnFrom-->
								</div>
								<div class="div-inline-block">
									<span>${displayPartyNameResult?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.ReturnTo}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${displayNamePartyTo?if_exists}
									</span>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSStatus}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										${orderStatusMgs?if_exists}
									</span>
								</div>
							</div>
							<#if currentStatusId?has_content>
								<#assign returnStatuses = Static["com.olbius.basesales.returnorder.ReturnWorker"].getReturnHeaderStatuses(delegator, returnHeader.returnId)/>
								<#if returnStatuses?has_content>
									<div class="row-fluid">
										<div class="div-inline-block">
											<label></label>
										</div>
										<div class="div-inline-block">
											<span style="font-weight: normal">
							                  	<#list returnStatuses as returnStatus>
								                    <#assign loopStatusItem = delegator.findOne("StatusItem", {"statusId" : returnStatus.statusId}, false)/>
								                    <#assign statusUserLogin = delegator.findOne("UserLogin", {"userLoginId" : returnStatus.changeByUserLoginId}, false)/>
								                    <div class="margin-left20">
								                      	${loopStatusItem.get("description",locale)} <#if returnStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
								                      	&nbsp;
								                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> 
								                      	[${returnStatus.changeByUserLoginId}]
								                    </div>
							                  	</#list>
											</span>
										</div>
									</div>
								</#if>
							</#if>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCreatedDate}:</label><#--BSReturnDate-->
								</div>
								<div class="div-inline-block">
									<span>
										<#if returnHeader.entryDate?has_content>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnHeader.entryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.Description}:</label><#--BSReturnDate-->
								</div>
								<div class="div-inline-block">
									<span>
										<#if returnHeader.description?has_content>
											${returnHeader.description?if_exists}
										</#if>
									</span>
								</div>
							</div>
						</div><!--.span6-->
					</div>
				</div><!-- .form-horizontal -->
				<div class="form-horizontal basic-custom-form">
					<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
						<thead>
							<tr style="font-weight: bold;">
								<td>${uiLabelMap.BSSTT}</td>
								<td class="align-left">${uiLabelMap.BSProduct} - ${uiLabelMap.BSProductName}</td>
								<td class="align-left">${uiLabelMap.BSReason}</td>
								<td style="width:40px">${uiLabelMap.BSProdPromo}</td>
								<td style="width:45px">${uiLabelMap.BSUom}</td>
								<td align="left" class="align-left">${uiLabelMap.BSQuantity}</td>
								<td align="left" class="align-left">${uiLabelMap.QuantityDelivered}</td>
							  	<td align="left" class="align-left" style="width:60px">${uiLabelMap.BSReturnPrice}</td>
							  	<td align="left" class="align-left">${uiLabelMap.BSAdjustment}</td>
								<td align="left" class="align-left">${uiLabelMap.BSItemTotal} <br />${uiLabelMap.BSParenthesisBeforeVAT}</td>
							</tr>
						</thead>
						<tbody>
						<#if listItemLine?exists>
						<#list listItemLine as itemLine>
	            			<tr>
								<td>${itemLine_index + 1}</td>
	            				<td valign="top">
				                  	<div>
				                  	${itemLine.productCode?if_exists} - 
				                  	${itemLine.productName?if_exists}
									<#if itemLine.orderItemTypeId?exists && "PRODPROMO_ORDER_ITEM" == itemLine.orderItemTypeId>
										 - <span class="text-error">(<b>${uiLabelMap.BSProductReturnPromo}</b>)</span>
									</#if>
				                  	</div>
	               				</td>
	               				<td align="left" valign="top">
	                				${itemLine.reasonDesc?if_exists}
				                </td>
	               				<td align="right" class="align-center" valign="top">
	                				${itemLine.isPromo?if_exists}
				                </td>
	                			<td align="right" class="align-center" valign="top">
	                				${itemLine.quantityUomDesc?if_exists}
				                </td>
				                <td align="right" class="align-right" valign="top">
				                	<#if itemLine.requireAmount?has_content && itemLine.requireAmount == 'Y'>
				                		<#if itemLine.returnAmount?exists>${itemLine.returnAmount?string.number}</#if>
				                	<#else>
										<#if itemLine.returnQuantity?exists>${itemLine.returnQuantity?string.number}</#if>				                	
				                	</#if>
				                </td>
				                <td align="right" class="align-right" valign="top">
				                  	<#if itemLine.receivedQuantity?exists>${itemLine.receivedQuantity?string.number}</#if>
				                </td>
				                <td align="right" class="align-right" valign="top"><#-- Unit price -->
				                  	<@ofbizCurrency amount=itemLine.returnPrice isoCode=currencyUomId/>
				                </td>
				                <td align="right" class="align-right" valign="top"><#-- Adjustment -->
									<#if itemLine.adjustment?exists><@ofbizCurrency amount=itemLine.adjustment isoCode=currencyUomId/></#if>
				                </td>
				                <td align="right" class="align-right" valign="top" nowrap="nowrap"><#-- Sub total before VAT -->
									<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId/>
				                </td>
							</tr>
						</#list>
						</#if>
						
						<#-- display tax prices sum -->
						<#if listTaxTotal?exists>
						<#list listTaxTotal as taxTotalItem>
							<tr>
								<td align="right" class="align-right" colspan="9">
									<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
								</td>
								<td class="align-right">
									<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
										(<@ofbizCurrency amount=-taxTotalItem.amount isoCode=currencyUomId/>)
									<#elseif taxTotalItem.amount?exists>
										<@ofbizCurrency amount=taxTotalItem.amount isoCode=currencyUomId/>
									</#if>
								</td>
							</tr>
						</#list>
						</#if>
						
						<#if returnHeaderAdjustments?exists>
						<#list returnHeaderAdjustments as returnHeaderAdjustment>
			                <#assign adjustmentType = delegator.findOne("ReturnAdjustmentType", {"returnAdjustmentTypeId": returnHeaderAdjustment.returnAdjustmentTypeId}, false)!>
			                <#assign adjustmentAmount = returnHeaderAdjustment.amount/>
			                <#if adjustmentAmount != 0>
			                    <tr>
			                        <td align="right" class="align-right" colspan="9">
			                        	<#assign adjPrinted = false>
			                            <#if returnHeaderAdjustment.comments?has_content>${returnHeaderAdjustment.comments}<#assign adjPrinted = true></#if>
			                            <#if returnHeaderAdjustment.description?has_content>${returnHeaderAdjustment.description}<#assign adjPrinted = true></#if>
			                            <#if !adjPrinted><span>${adjustmentType.get("description", locale)}</span></#if>
			                        </td>
			                        <td align="right" class="align-right" nowrap="nowrap">
			                        	<#if (adjustmentAmount &lt; 0)>
	                                		<#assign adjustmentAmountNegative = -adjustmentAmount>
			                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId/>)
			                            <#else>
			                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>
			                            </#if>
			                        </td>
			                    </tr>
			                </#if>
			            </#list>
						</#if>
						
						<#-- subtotal -->
						<#if returnSubTotal?exists>
	          			<tr>
	            			<td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.BSOrderItemsSubTotal}</b></div></td>
	            			<td align="right" class="align-right" nowrap="nowrap">
	            				<#if (returnSubTotal &lt; 0)>
                            		<#assign returnSubTotalNegative = -returnSubTotal>
                            		(<@ofbizCurrency amount=returnSubTotalNegative isoCode=currencyUomId/>)
                            	<#else>
                            		<@ofbizCurrency amount=returnSubTotal isoCode=currencyUomId/>
                        		</#if>
	        				</td>
	          			</tr>
	          			</#if>
	          			
	          			<#-- other adjustments -->
						<#if otherAdjAmount?exists>
			            <tr>
			              	<td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.BSTotalOrderAdjustments}</b></div></td>
			              	<td align="right" class="align-right" nowrap="nowrap">
			              		<#if (otherAdjAmount &lt; 0)>
                            		<#assign otherAdjAmountNegative = -otherAdjAmount>
									(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId/>)
								<#else>
									<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>
								</#if>
							</td>
			            </tr>
			            </#if>
	          			
	          			<#-- tax adjustments -->
						<#if taxAmount?exists>
			          	<tr>
				            <td align="right" class="align-right" colspan="9"><div><b>${uiLabelMap.OrderTotalSalesTax}</b></div></td>
				            <td align="right" class="align-right" nowrap="nowrap">
				            	<#if (taxAmount &lt; 0)>
                            		<#assign taxAmountNegative = -taxAmount>
				            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId/>)
				            	<#else>
				            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId/>
				            	</#if>
				            </td>
			          	</tr>
			          	</#if>
	          			
			          	<#-- grand total -->
			          	<tr>
			          		<#assign accountOneValue = grandTotalReturn/>
			          		<#assign accountTwoValue = currencyUomId/>
				            <td align="right" class="align-right" colspan="9"><div style="font-size: 14px;text-transform:uppercase"><b>${uiLabelMap.BSTotalAmountPayment}</b></div></td><#--uiLabelMap.OrderTotalDue-->
				            <td align="right" class="align-right" nowrap="nowrap" style="font-size: 14px;">
				            	<b><#if (grandTotalReturn &lt; 0)>
                            		<#assign grandTotalNegative = -grandTotalReturn>
                            		<#assign accountOneValue = -grandTotalReturn/>
				            		(<@ofbizCurrency amount=grandTotalNegative isoCode=currencyUomId/>)
				            	<#else>
				            		<@ofbizCurrency amount=grandTotalReturn isoCode=currencyUomId/>
				            	</#if></b>
				            </td>
			          	</tr>
						</tbody>
					</table>
				</div><!--.form-horizontal-->
				
				<input type="hidden" name="accountOneValue" id="accountOneValue" value="${accountOneValue?default(0)}"/>
				<input type="hidden" name="accountTwoValue" id="accountTwoValue" value="${accountTwoValue?default(VND)}"/>
				
				<#if orderAdjustmentsPromo?exists && orderAdjustmentsPromo?has_content>
				<div class="row-fluid" style="margin-bottom:20px">
					<div class="span12">
						<h4 class="smaller green" style="display:inline-block">
							${uiLabelMap.BSPromotionDetailApplyInOrder}
						</h4>
						<div>
							<ul>
							<#list orderAdjustmentsPromo as objAdj>
								<li>[<a href="<@ofbizUrl>viewPromotion?productPromoId=${objAdj.productPromoId}</@ofbizUrl>" target="_blank">${objAdj.productPromoId}</a>] ${objAdj.promoName}: <b><@ofbizCurrency amount=objAdj.amount isoCode=currencyUomId/></b>
								<#if objAdj.productPromoCodeIds?has_content>
									- ${uiLabelMap.OrderWithPromoCode} [<#list objAdj.productPromoCodeIds as productPromoCodeId>${productPromoCodeId}<#if productPromoCodeId_has_next>, </#if></#list>] 
								</#if></li>
							</#list>
							</ul>
						</div>
					</div>
				</div>
				</#if>
			</div><!--.row-fluid-->
		</div><!--.widget-main-->
	</div><!--.widget-body-->
	
	<div id="paymentOrderContainer">
	</div>
</div>