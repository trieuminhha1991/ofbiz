<#macro maskSensitiveNumber cardNumber>
	<#assign cardNumberDisplay = "">
	<#if cardNumber?has_content>
		<#assign size = cardNumber?length - 4>
		<#if (size > 0)>
			<#list 0 .. size-1 as foo>
				<#assign cardNumberDisplay = cardNumberDisplay + "*">
			</#list>
			<#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
		<#else>
			<#-- but if the card number has less than four digits (ie, it was entered incorrectly), display it in full -->
			<#assign cardNumberDisplay = cardNumber>
		</#if>
	</#if>
	${cardNumberDisplay?if_exists}
</#macro>

<#if (security.hasPermission("ORD_RECEIVE_PAYMENT", session)) >
	<#assign hasReceived = true />
<#else>
	<#assign hasReceived = false />
</#if>




<!-- Order Payment info -->
<div id="payment-tab" class="tab-pane<#if activeTab?exists && activeTab == "payment-tab"> active</#if>">
	<#--<h4 class="smaller green" style="display:inline-block">
		${uiLabelMap.AccountingPaymentInformation}
	</h4>-->
	<div class="row-fluid">
		<div class="span12">
			<h4 class="center_custom">${StringUtil.wrapString(uiLabelMap.BACCPaymentTotal)?upper_case}&nbsp;<span id="_totalValue"></span></h4>
			<h4 class="center_custom"><i class="icon-shopping-cart blue"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSGrandTotalOrder)?upper_case}&nbsp;<label style="color:red;font-weight:bold;font-size : 17px;display : -webkit-inline-box;"><@ofbizCurrency amount=orderHeader.grandTotal?if_exists isoCode=orderHeader.currencyUom?if_exists/></label></h4>
			<#if hasReceived>
				<div class="center_custom"><a class="fonthead" href="javascript:OlbReceivePayment.receivePayment('<@ofbizUrl>receivePayment?${paramString}</@ofbizUrl>');"><i class="fa fa-hand-o-right"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BACCPayment)}</a></div><hr>
			</#if>
		</div>
	</div>
	<h4 class="smaller green" style="display:inline-block">
		${uiLabelMap.AccountingPaymentInformation}
	</h4>
		<table style="width: 100%" cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
			<#assign orderTypeId = orderReadHelper.getOrderTypeId()>
			<#if orderTypeId == "PURCHASE_ORDER">
                <#assign orderInvoiceReference = delegator.findByAnd("OrderInvoiceReference",{"orderId" : '${parameters.orderId?if_exists}'},null,false) />
                <#assign paymentApplications = Static["javolution.util.FastList"].newInstance()/>
                <#if orderInvoiceReference?exists && orderInvoiceReference?has_content>
                    <#assign orderInvoice = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderInvoiceReference)/>
                    <#assign paymentApplications = delegator.findByAnd("PaymentApplication",{"invoiceId" : '${orderInvoice.invoiceId?if_exists}'},null,false) />
                </#if>
				<#assign amountAppl = 0/>
				<#assign orderPreference = delegator.findByAnd("OrderPaymentPreference",{"orderId" : '${parameters.orderId?if_exists}'},null,false) />
				<#if orderPreference?exists && orderPreference?has_content>
					<#list orderPreference as pre>
						<#if !pre.paymentMethodId?exists && !pre.paymentMethodId?has_content>
							<#break>
						<#else>
							<#assign amountAppl = amountAppl  +  pre.maxAmount?if_exists?default(0) />
						</#if>
					</#list>
				</#if>
				<thead>
					<tr>
						<td>${uiLabelMap.AccountingPaymentID}</td>
						<td>${uiLabelMap.BACCPaymentToParty}</td>
						<td>${uiLabelMap.BACCAmount}</td>
						<td>${uiLabelMap.BACCAmountChange}</td>
						<td>${uiLabelMap.CommonStatus}</td>
					</tr>
				</thead>
				<#assign partyAcctg = delegator.findOne("PartyAcctgPreference",{"partyId":"${userLogin.lastOrg?if_exists}"},false)/>
				<#if partyAcctg.baseCurrencyUomId != orderHeader.currencyUom>
					<#assign isNotSame = true/>
				<#else>
					<#assign isNotSame = false/>
				</#if>
				<#list paymentApplications as paymentApplication>
					<#assign payments = paymentApplication.getRelated("Payment", null, null, false)>
					<#list payments as payment>
						<#assign statusItem = payment.getRelatedOne("StatusItem", false)>
						<#assign partyName = delegator.findOne("PartyNameView", {"partyId" : payment.partyIdTo}, true)>
						<tr>
							<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session) || security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
								<td><a target="_blank" href="<@ofbizUrl>ViewAPPayment</@ofbizUrl>?paymentId=${payment.paymentId}"><i class="fa fa-paypal"></i>&nbsp;${payment.paymentId}</a></td>
							<#else>
								<td>&nbsp;${payment.paymentId?if_exists}</td>
							</#if>
								<td>${partyName.groupName?if_exists}${partyName.lastName?if_exists} ${partyName.firstName?if_exists} ${partyName.middleName?if_exists}
							<#if security.hasPermission("PARTYMGR_VIEW", session) || security.hasPermission("PARTYMGR_ADMIN", session)>
								[<span>${partyId}</span>]
							<#else>
								[${partyId}]
							</#if>
							</td>
							<td><#if payment.amount?has_content><@ofbizCurrency amount=payment.amount?if_exists isoCode=orderHeader.currencyUom/></#if></td>
							<td><#if payment.baseCurrencyAmount?has_content>
									<@ofbizCurrency amount=payment.baseCurrencyAmount?if_exists isoCode=payment.baseCurrencyUomId?if_exists/> (${payment.currencyUomId?if_exists} - ${payment.baseCurrencyUomId?if_exists})
								<#else>
									<#assign uomConversion = delegator.findByAnd("UomConversionDated",{"uomId" : payment.currencyUomId?if_exists,"uomIdTo" : payment.baseCurrencyUomId?if_exists},null,false) !>
									<#if uomConversion?exists && uomConversion?has_content>
										<#assign convertUom = dispatcher.runSync("convertUom",Static["org.ofbiz.base.util.UtilMisc"].toMap("userLogin" , userLogin,"uomId",payment.currencyUomId?if_exists,"uomIdTo",payment.baseCurrencyUomId?if_exists,"originalValue",payment.amount?if_exists))/>
										<@ofbizCurrency amount=convertUom.convertedValue?if_exists isoCode=payment.baseCurrencyUomId?if_exists/> (${payment.currencyUomId?if_exists} - ${convertUom.convertedValue?if_exists})
									<#else>
										-------------
									</#if>
								</#if>
							</td>
							<td style="font-weight:bold">${statusItem.description}</td>
						</tr>
					</#list>
				</#list>
				<#if !orderPaymentPreferences?has_content && !invoices?has_content>
					<tr>
						<td colspan="5" class="center">${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}</td>
					</tr>
				</#if>
				</table>
				<h4 class="smaller green" style="display:inline-block">
					${uiLabelMap.BACCAccountingInvoiceInformation}
				</h4>
				<table style="width: 100%" cellspacing="0" class="table table-bordered table-hover dataTable">
				<thead>
					<tr>
						<td>${uiLabelMap.BACCInvoiceId}</td>
						<td>${uiLabelMap.BACCInvoiceTypeId}</td>
						<td>${uiLabelMap.BACCPaymentPartyIdTo}</td>
						<td>${uiLabelMap.BACCPaymentPartyIdFrom}</td>
						<td>${uiLabelMap.BACCTotal}</td>
						<td>${uiLabelMap.CommonStatus}</td>
					</tr>
				</thead>
				<#-- invoices -->
				<#if invoices?has_content>
					<#list invoices as invoice>
					<#assign _invoice = delegator.findOne("Invoice",{"invoiceId" : invoice},false) !>
					<#assign _partyFrom = Static["com.olbius.basehr.util.PartyUtil"].getPartyName(delegator,_invoice.partyIdFrom?if_exists) !>
					<#assign _partyTo = Static["com.olbius.basehr.util.PartyUtil"].getPartyName(delegator,_invoice.partyId?if_exists) !>
					<#assign _invoiceTotal = Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(delegator,invoice)/>
					<#assign _stt = delegator.findOne("StatusItem",{"statusId" : _invoice.statusId?if_exists},false) !>
					<#assign _invoiceType = delegator.findOne("InvoiceType",{"invoiceTypeId" : _invoice.invoiceTypeId?if_exists},false) !>

					<tr>
						<td align="right" valign="top" width="10%">
							<div>
								<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session) || security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
									<a  target="_blank" href="<@ofbizUrl>ViewAPInvoice</@ofbizUrl>?invoiceId=${invoice?if_exists}"><i class="fa fa-file-text"></i>&nbsp;${invoice?if_exists}</a>
								<#else>
									&nbsp;${invoice?if_exists}
								</#if>
							</div>
						</td>
						<td><#if _invoiceType?exists>${_invoiceType.get("description",locale)?if_exists}</#if></td>
						<td valign="top">
							&nbsp;${_partyFrom?if_exists}
						</td>
						<td>&nbsp;${_partyTo?if_exists}</td>
						<td><#if _invoiceTotal?exists><@ofbizCurrency amount =_invoiceTotal?default(0.00) isoCode=_invoice.currencyUomId?if_exists/><#else>0.00</#if></td>
						<td style="font-weight:bold;"><#if _stt?exists>${_stt.get("description",locale)?if_exists}</#if></td>
					</tr>
					</#list>
				</#if>
				<#assign returnOrders = orderHeader.getRelated("ReturnItem",null,null,false) !>
				<#if returnOrders?exists && returnOrders?size gt 0>
					<#list returnOrders as return_item>
						<#assign returnBilling = return_item.getRelated("ReturnItemBilling",null,null,false) />
						<#if returnBilling?exists && returnBilling?size gt 0>
							<#assign _invoice = delegator.findOne("Invoice",{"invoiceId" : returnBilling.get(0).invoiceId},false) !>
							<#assign _partyFrom = Static["com.olbius.basehr.util.PartyUtil"].getPartyName(delegator,_invoice.partyIdFrom?if_exists) !>
							<#assign _partyTo = Static["com.olbius.basehr.util.PartyUtil"].getPartyName(delegator,_invoice.partyId?if_exists) !>
							<#assign _invoiceTotal = Static["org.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(delegator,returnBilling.get(0).invoiceId)/>
							<#assign _stt = delegator.findOne("StatusItem",{"statusId" : _invoice.statusId?if_exists},false) !>
							<#assign _invoiceType = delegator.findOne("InvoiceType",{"invoiceTypeId" : _invoice.invoiceTypeId?if_exists},false) !>
							<tr bgcolor="#D4F3C5">
								<td align="right" valign="top" width="10%">
									<div>
										<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session) || security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
											<a  target="_blank" href="<@ofbizUrl>ViewARInvoice</@ofbizUrl>?invoiceId=${returnBilling.get(0).invoiceId?if_exists}"><i class="fa fa-file-text"></i>&nbsp;${returnBilling.get(0).invoiceId?if_exists}</a>
										<#else>
											<a href="javascript:void(0)"><i class="fa fa-file-text"></i>&nbsp;${returnBilling.get(0).invoiceId?if_exists}</a>
										</#if>
									</div>
								</td>
								<td style="color:red;"><#if _invoiceType?exists>${_invoiceType.get("description",locale)?if_exists}</#if></td>
								<td valign="top">
									&nbsp;${_partyFrom?if_exists}
								</td>
								<td>&nbsp;${_partyTo?if_exists}</td>
								<td><#if _invoiceTotal?exists><@ofbizCurrency amount =_invoiceTotal?default(0.00) isoCode=_invoice.currencyUomId?if_exists/></#if></td>
								<td style="font-weight:bold;"><#if _stt?exists>${_stt.get("description",locale)?if_exists}</#if></td>
							</tr>
						</#if>
					</#list>
				</#if>
				<#if !returnOrders?has_content && !invoices?has_content>
					<tr>
						<td colspan="6" class="center">${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}</td>
					</tr>
				</#if>
				</table>
			<#else>

			<#-- order payment status -->
			<tr>
				<td align="left" valign="top" >&nbsp;${uiLabelMap.OrderStatusHistory}</td>
				<td>
					<#assign orderPaymentStatuses = orderReadHelper.getOrderPaymentStatuses()>
					<#if orderPaymentStatuses?has_content>
						<#list orderPaymentStatuses as orderPaymentStatus>
							<#assign statusItem = orderPaymentStatus.getRelatedOne("StatusItem", false)?if_exists>
							<#if statusItem?has_content>
								<div>
									${statusItem.get("description",locale)} <#if orderPaymentStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderPaymentStatus.statusDatetime, "", locale, timeZone)!}</#if>
									&nbsp;
									${uiLabelMap.CommonBy} - [${orderPaymentStatus.statusUserLogin?if_exists}]
								</div>
							</#if>
						</#list>
					</#if>
				</td>
				<td>&nbsp;</td>
			</tr>
			<#if orderPaymentPreferences?has_content || billingAccount?has_content || invoices?has_content>
				<#list orderPaymentPreferences as orderPaymentPreference>
					<#assign paymentList = orderPaymentPreference.getRelated("Payment", null, null, false)>
					<#assign pmBillingAddress = {}>
					<#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem", false)>
					<#if outputted?default("false") == "true"></#if>
					<#assign outputted = "true">
					<#-- try the paymentMethod first; if paymentMethodId is specified it overrides paymentMethodTypeId -->
					<#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod", false)?if_exists>
					<#if !paymentMethod?has_content>
						<#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType", false)>
						<#if paymentMethodType.paymentMethodTypeId == "EXT_BILLACT">
							<#assign outputted = "false">
							<#-- billing account -->
							<#if billingAccount?exists>
								<#if outputted?default("false") == "true">
							</#if>
							<tr>
								<td align="right" valign="top" width="29%">
									<#-- billing accounts require a special OrderPaymentPreference because it is skipped from above section of OPPs -->
									<div>&nbsp;<span >${uiLabelMap.AccountingBillingAccount}</span>&nbsp;
										<#if billingAccountMaxAmount?has_content>
										<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=billingAccountMaxAmount?default(0.00) isoCode=currencyUomId/>
										</#if>
									</div>
								</td>
								<td valign="top">
									<table class="basic-table" cellspacing="0">
										<tr>
											<td valign="top">
												${uiLabelMap.CommonNbr}<a href="/accounting/control/EditBillingAccount?billingAccountId=${billingAccount.billingAccountId}${externalKeyParam}" class="btn btn-primary btn-mini">${billingAccount.billingAccountId}</a>  - ${billingAccount.description?if_exists}
											</td>
											<td valign="top" align="right">
												<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED" && orderPaymentPreference.statusId != "PAYMENT_RECEIVED">
												<#if hasReceived>
													<a href="javascript:OlbReceivePayment.receivePayment('<@ofbizUrl>receivePayment?${paramString}</@ofbizUrl>');" class="btn btn-primary btn-mini"><i class="fa fa-hand-o-right"></i>&nbsp;${uiLabelMap.AccountingReceivePayment}</a>
												</#if>
												</#if>
											</td>
										</tr>
									</table>
								</td>
								<td>
									<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
										<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
											<div>
												<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
												<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
													<input type="hidden" name="orderId" value="${orderId}" />
													<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
													<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
													<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
												</form>
											</div>
										</#if>
									</#if>
								</td>
							</tr>
						</#if>
						<#elseif paymentMethodType.paymentMethodTypeId == "FIN_ACCOUNT">
							<#assign finAccount = orderPaymentPreference.getRelatedOne("FinAccount", false)?if_exists/>
							<#if (finAccount?has_content)>
							<#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse", null, null, false)>
							<#assign finAccountType = finAccount.getRelatedOne("FinAccountType", false)?if_exists/>
							<tr>
								<td align="right" valign="top">
									<div>
										<span >&nbsp;${uiLabelMap.AccountingFinAccount}</span>
										<#if orderPaymentPreference.maxAmount?has_content>
											<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
										</#if>
									</div>
								</td>
								<td valign="top" width="60%">
									<div>
										<#if (finAccountType?has_content)>
											${finAccountType.description?default(finAccountType.finAccountTypeId)}&nbsp;
										</#if>
										#${finAccount.finAccountCode?default(finAccount.finAccountId)} (<a href="/accounting/control/EditFinAccount?finAccountId=${finAccount.finAccountId}${externalKeyParam}" class="btn btn-primary btn-mini">${finAccount.finAccountId}</a>)
										<br />
										${finAccount.finAccountName?if_exists}
										<br />

										<#-- Authorize and Capture transactions -->
										<div>
											<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
												<a href="/accounting/control/AuthorizeTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}${externalKeyParam}" class="btn btn-primary btn-mini">${uiLabelMap.AccountingAuthorize}</a>
											</#if>
											<#if orderPaymentPreference.statusId == "PAYMENT_AUTHORIZED">
											<a href="/accounting/control/CaptureTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}${externalKeyParam}" class="btn btn-primary btn-mini">${uiLabelMap.AccountingCapture}</a>
											</#if>
										</div>
									</div>
									<#if gatewayResponses?has_content>
									<div>
										<hr />
										<#list gatewayResponses as gatewayResponse>
											<#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration", false)>
											${(transactionCode.get("description",locale))?default("Unknown")}:
											<#if gatewayResponse.transactionDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(gatewayResponse.transactionDate, "", locale, timeZone)!} </#if>
											<@ofbizCurrency amount=gatewayResponse.amount isoCode=currencyUomId/><br />
											(<span >${uiLabelMap.OrderReference}</span>&nbsp;${gatewayResponse.referenceNum?if_exists}
											<span >${uiLabelMap.OrderAvs}</span>&nbsp;${gatewayResponse.gatewayAvsResult?default("N/A")}
											<span >${uiLabelMap.OrderScore}</span>&nbsp;${gatewayResponse.gatewayScoreResult?default("N/A")})
											<a href="/accounting/control/ViewGatewayResponse?paymentGatewayResponseId=${gatewayResponse.paymentGatewayResponseId}${externalKeyParam}" class="btn btn-primary btn-mini">${uiLabelMap.CommonDetails}</a>
											<#if gatewayResponse_has_next><hr /></#if>
										</#list>
									</div>
									</#if>
								</td>
								<td>
									<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
									<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
									<div>
										<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
										<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
											<input type="hidden" name="orderId" value="${orderId}" />
											<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
											<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
											<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
										</form>
									</div>
									</#if>
									</#if>
								</td>
							</tr>
							<#if paymentList?has_content>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingInvoicePayments}</span></div>
								</td>
								<td>
									<div>
										<#list paymentList as paymentMap>
											<a href="<@ofbizUrl>accArpaymentOverview</@ofbizUrl>?paymentId=${paymentMap.paymentId}${externalKeyParam}" class="btn btn-primary btn-mini">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
										</#list>
									</div>
								</td>
							</tr>
						</#if>
					</#if>
				<#else>
						<tr>
							<td align="right" valign="top">
								<div>&nbsp;<span >${paymentMethodType.get("description",locale)?if_exists}</span>&nbsp;
									<#if orderPaymentPreference.maxAmount?has_content>
										<#assign amountAppl = 0/>
										<#assign orderPreference = delegator.findByAnd("OrderPaymentPreference",{"orderId" : '${parameters.orderId?if_exists}'},null,false) />
										<#assign orderRoles = delegator.findByAnd("OrderRole",Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId","${parameters.orderId}","roleTypeId","BILL_FROM_VENDOR"))/>
										<#if orderPreference?exists && orderPreference?has_content>
											<#list orderPreference as pre>
												<#if !pre.paymentMethodId?exists && !pre.paymentMethodId?has_content>
													<#break>
												<#else>
													<#assign amountAppl = amountAppl  +  pre.maxAmount?if_exists?default(0) />
												</#if>
											</#list>
										</#if>
										<br />${uiLabelMap.OrderPaymentMaximumAmount} : <@ofbizCurrency amount = (orderPaymentPreference.maxAmount?if_exists - amountAppl?if_exists)?default(0.00) isoCode=currencyUomId/>
									</#if>
								</div>
							</td>
							<#if paymentMethodType.paymentMethodTypeId != "EXT_OFFLINE" && paymentMethodType.paymentMethodTypeId != "EXT_PAYPAL" && paymentMethodType.paymentMethodTypeId != "EXT_COD">
							<td>
								<div>
									<#if orderPaymentPreference.maxAmount?has_content>
										<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
									</#if>
									<br />&nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
								</div>
								<#--
								<div><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>&nbsp;-&nbsp;${(orderPaymentPreference.authDate.toString())?if_exists}</div>
								<div>&nbsp;<#if orderPaymentPreference.authRefNum?exists>(${uiLabelMap.OrderReference}: ${orderPaymentPreference.authRefNum})</#if></div>
								-->
							</td>
							<#else>
							<td align="right">
								<#if hasReceived>
									<a href="javascript:OlbReceivePayment.receivePayment('<@ofbizUrl>receivePayment?${paramString}</@ofbizUrl>');" class="btn btn-primary btn-mini"><i class="fa fa-hand-o-right"></i>&nbsp;${uiLabelMap.AccountingReceivePayment}</a>
								</#if>
							</td>
							</#if>
							<td>
								<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
									<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
										<#if hasReceived>
											<div>
												<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
												<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
													<input type="hidden" name="orderId" value="${orderId}" />
													<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
													<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
													<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
												</form>
											</div>
										</#if>
									</#if>
								</#if>
							</td>
						</tr>
						<#if paymentList?has_content>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingInvoicePayments}</span></div>
								</td>
								<td colspan="2">
									<div>
										<#list paymentList as paymentMap>
											<a href="<@ofbizUrl>accArpaymentOverview</@ofbizUrl>?paymentId=${paymentMap.paymentId}${externalKeyParam}" class="btn btn-primary btn-mini">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
											<#--
											<span style="color:#005580; text-decoration:underline">${paymentMap.paymentId}</span><#if paymentMap_has_next><br /></#if>
											-->
										</#list>
									</div>
								</td>
							</tr>
						</#if>
					</#if>
				<#else>
					<#if paymentMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
						<#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse", null, null, false)>
						<#assign creditCard = paymentMethod.getRelatedOne("CreditCard", false)?if_exists>
							<#if creditCard?has_content>
								<#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress", false)?if_exists>
							</#if>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingCreditCard}</span>
									<#if orderPaymentPreference.maxAmount?has_content>
										<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
									</#if>
									</div>
								</td>
								<td valign="top">
									<div>
										<#if creditCard?has_content>
										<#if creditCard.companyNameOnCard?exists>${creditCard.companyNameOnCard}<br /></#if>
										<#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}&nbsp;</#if>
										${creditCard.firstNameOnCard?default("N/A")}&nbsp;
										<#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}&nbsp;</#if>
										${creditCard.lastNameOnCard?default("N/A")}
										<#if creditCard.suffixOnCard?has_content>&nbsp;${creditCard.suffixOnCard}</#if>
										<br />

										<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session) || security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
										${creditCard.cardType}
										<@maskSensitiveNumber cardNumber=creditCard.cardNumber?if_exists/>
										${creditCard.expireDate}
										&nbsp;
										<#--[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>] -->
										<#else>
										${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
										&nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
										</#if>
										<br />

										<#-- Authorize and Capture transactions -->
										<div>
										<#--  <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
										<a href="/accounting/control/AuthorizeTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}${externalKeyParam}" class="btn btn-primary btn-mini">${uiLabelMap.AccountingAuthorize}</a>
										</#if>
										<#if orderPaymentPreference.statusId == "PAYMENT_AUTHORIZED">
										<a href="/accounting/control/CaptureTransaction?orderId=${orderId?if_exists}&amp;orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}${externalKeyParam}" class="btn btn-primary btn-mini">${uiLabelMap.AccountingCapture}</a>
										</#if> -->
										</div>
										<#else>
										${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
										</#if>
									</div>
								<#if gatewayResponses?has_content>
									<div>
										<hr />
										<#list gatewayResponses as gatewayResponse>
										<#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration", false)>
										${(transactionCode.get("description",locale))?default("Unknown")}:
										<#if gatewayResponse.transactionDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(gatewayResponse.transactionDate, "", locale, timeZone)!} </#if>
										<@ofbizCurrency amount=gatewayResponse.amount isoCode=currencyUomId/><br />
										(<span >${uiLabelMap.OrderReference}</span>&nbsp;${gatewayResponse.referenceNum?if_exists}
										<span >${uiLabelMap.OrderAvs}</span>&nbsp;${gatewayResponse.gatewayAvsResult?default("N/A")}
										<span >${uiLabelMap.OrderScore}</span>&nbsp;${gatewayResponse.gatewayScoreResult?default("N/A")})
										<a href="/accounting/control/ViewGatewayResponse?paymentGatewayResponseId=${gatewayResponse.paymentGatewayResponseId}${externalKeyParam}" class="btn btn-primary btn-mini">${uiLabelMap.CommonDetails}</a>
										<#if gatewayResponse_has_next><hr /></#if>
										</#list>
									</div>
								</#if>
								</td>
								<td>
									<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
									<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
										<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
										<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
											<input type="hidden" name="orderId" value="${orderId}" />
											<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
											<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
											<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
										</form>
									</#if>
									</#if>
								</td>
							</tr>
						<#elseif paymentMethod.paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
							<#assign eftAccount = paymentMethod.getRelatedOne("EftAccount", false)>
							<#if eftAccount?has_content>
								<#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress", false)?if_exists>
							</#if>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingEFTAccount}</span>
										<#if orderPaymentPreference.maxAmount?has_content>
										<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
										</#if>
									</div>
								</td>
								<td valign="top">
									<div>
										<#if eftAccount?has_content>
										${eftAccount.nameOnAccount?if_exists}<br />
										<#if eftAccount.companyNameOnAccount?exists>${eftAccount.companyNameOnAccount}<br /></#if>
										${uiLabelMap.AccountingBankName}: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br />
										${uiLabelMap.AccountingAccount}#: ${eftAccount.accountNumber}
										<#else>
										${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
										</#if>
									</div>
								</td>
								<td>
									<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
									<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
									<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
									<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
										<input type="hidden" name="orderId" value="${orderId}" />
										<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
										<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
										<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
									</form>
									</#if>
									</#if>
								</td>
							</tr>
							<#if paymentList?has_content>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingInvoicePayments}</span></div>
								</td>
								<td>
									<div>
										<#list paymentList as paymentMap>
											<a href="<@ofbizUrl>accArpaymentOverview</@ofbizUrl>?paymentId=${paymentMap.paymentId}${externalKeyParam}" class="btn btn-primary btn-mini">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
										</#list>
									</div>
								</td>
							</tr>
							</#if>
						<#elseif paymentMethod.paymentMethodTypeId?if_exists == "COMPANY_CHECK">
							<#assign eftAccount = paymentMethod.getRelatedOne("EftAccount", false)>
							<#if eftAccount?has_content>
								<#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress", false)?if_exists>
							</#if>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingEFTAccount}</span>
										<#if orderPaymentPreference.maxAmount?has_content>
										<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
										</#if>
									</div>
								</td>
								<td valign="top">
									<div>
										<#if eftAccount?has_content>
										${eftAccount.nameOnAccount?if_exists}<br />
										<#if eftAccount.companyNameOnAccount?exists>${eftAccount.companyNameOnAccount}<br /></#if>
										${uiLabelMap.AccountingBankName}: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br />
										${uiLabelMap.AccountingAccount}#: ${eftAccount.accountNumber}
										<#else>
										${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
										</#if>
									</div>
								</td>
								<td>
									<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
									<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
									<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
									<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
										<input type="hidden" name="orderId" value="${orderId}" />
										<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
										<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
										<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
									</form>
									</#if>
									</#if>
								</td>
							</tr>

						<#elseif paymentMethod.paymentMethodTypeId?if_exists == "GIFT_CARD">
							<#assign giftCard = paymentMethod.getRelatedOne("GiftCard", false)>
							<#if giftCard?exists>
								<#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress", false)?if_exists>
							</#if>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.OrderGiftCard}</span>
										<#if orderPaymentPreference.maxAmount?has_content>
										<br />${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
										</#if>
									</div>
								</td>
								<td valign="top">
									<div>
										<#if giftCard?has_content>
										<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session) || security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
										${giftCard.cardNumber?default("N/A")} [${giftCard.pinNumber?default("N/A")}]
										&nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
										<#else>
										<@maskSensitiveNumber cardNumber=giftCard.cardNumber?if_exists/>
										<#if !cardNumberDisplay?has_content>N/A</#if>
										&nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
										</#if>
										<#else>
										${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
										</#if>
									</div>
								</td>
								<td>
									<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
									<#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
									<a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="btn btn-warning btn-mini tooltip-warning">${uiLabelMap.CommonCancel}</a>
									<form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
										<input type="hidden" name="orderId" value="${orderId}" />
										<input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}" />
										<input type="hidden" name="statusId" value="PAYMENT_CANCELLED" />
										<input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}" />
									</form>
									</#if>
									</#if>
								</td>
							</tr>
							<#if paymentList?has_content>
							<tr>
								<td align="right" valign="top">
									<div>&nbsp;<span >${uiLabelMap.AccountingInvoicePayments}</span></div>
								</td>
								<td>
									<div>
										<#list paymentList as paymentMap>
											<a href="<@ofbizUrl>accArpaymentOverview</@ofbizUrl>?paymentId=${paymentMap.paymentId}${externalKeyParam}" class="btn btn-primary btn-mini">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
										</#list>
									</div>
								</td>
							</tr>
							</#if>
						</#if>
					</#if>
					<#if pmBillingAddress?has_content>
						<tr><td>&nbsp;</td><td colspan="3"><hr /></td></tr>
						<tr>
							<td align="right" valign="top">&nbsp;</td>
							<td valign="top">
								<div>
									<#if pmBillingAddress.toName?has_content><span >${uiLabelMap.CommonTo}</span>&nbsp;${pmBillingAddress.toName}<br /></#if>
									<#if pmBillingAddress.attnName?has_content><span >${uiLabelMap.CommonAttn}</span>&nbsp;${pmBillingAddress.attnName}<br /></#if>
									${pmBillingAddress.address1}<br />
									<#if pmBillingAddress.address2?has_content>${pmBillingAddress.address2}<br /></#if>
									${pmBillingAddress.city}<#if pmBillingAddress.stateProvinceGeoId?has_content>, ${pmBillingAddress.stateProvinceGeoId} </#if>
									${pmBillingAddress.postalCode?if_exists}<br />
									${pmBillingAddress.countryGeoId?if_exists}
								</div>
							</td>
							<td>&nbsp;</td>
						</tr>
						<#if paymentList?has_content>
						<tr>
							<td align="right" valign="top">
								<div>&nbsp;<span >${uiLabelMap.AccountingInvoicePayments}</span></div>
							</td>
							<td>
								<div>
									<#list paymentList as paymentMap>
										<a href="<@ofbizUrl>accArpaymentOverview</@ofbizUrl>?paymentId=${paymentMap.paymentId}${externalKeyParam}" class="btn btn-primary btn-mini">${paymentMap.paymentId}</a><#if paymentMap_has_next><br /></#if>
									</#list>
								</div>
							</td>
						</tr>
						</#if>
					</#if>
				</#list>

				<#if customerPoNumber?has_content>
					<tr>
						<td align="right" valign="top"><span >${uiLabelMap.OrderPONumber}</span></td>
						<td valign="top">${customerPoNumber?if_exists}</td>
						<td>&nbsp;</td>
					</tr>
				</#if>

				<#-- invoices -->
				<#if invoices?has_content>
					<tr>
						<td align="right" valign="top">&nbsp;<span >${uiLabelMap.OrderInvoices}</span></td>
						<td valign="top">
							<#list invoices as invoice>
								<div>${uiLabelMap.CommonNbr}&nbsp;<a href="<@ofbizUrl>accArinvoiceOverviewGlobal</@ofbizUrl>?invoiceId=${invoice}${externalKeyParam}" class="btn btn-primary btn-mini">&nbsp;${invoice}</a>
								(<a target="_BLANK" href="/accounting/control/invoice.pdf?invoiceId=${invoice}${externalKeyParam}" class="buttontext">PDF</a>)</div>
							</#list>
						</td>
						<td>&nbsp;</td>
					</tr>
				</#if>
				<#else>
				<tr>
					<td colspan="4" align="center">${uiLabelMap.OrderNoOrderPaymentPreferences}</td>
				</tr>
			</#if>
			<#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED")) && (paymentMethodValueMaps?has_content)>
				<tr>
					<td colspan="4">
						<form name="addPaymentMethodToOrder" method="post" action="<@ofbizUrl>addPaymentMethodToOrder</@ofbizUrl>">
							<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
							<table class="table table-striped table-bordered table-hover dataTable" cellspacing="0">
								<tr>
									<td width="10%" align="right" nowrap="nowrap"><span >${uiLabelMap.AccountingPaymentMethod}</span></td>
									<td width="60%" nowrap="nowrap">
										<select name="paymentMethodId">
											<#list paymentMethodValueMaps as paymentMethodValueMap>
											<#assign paymentMethod = paymentMethodValueMap.paymentMethod/>
												<option value="${paymentMethod.get("paymentMethodId")?if_exists}">
													<#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
														<#assign creditCard = paymentMethodValueMap.creditCard/>
														<#if (creditCard?has_content)>
														<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session) || security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
															${creditCard.cardType?if_exists} <@maskSensitiveNumber cardNumber=creditCard.cardNumber?if_exists/> ${creditCard.expireDate?if_exists}
														<#else>
															${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
														</#if>
														</#if>
													<#else>
														${paymentMethod.paymentMethodTypeId?if_exists}
														<#if paymentMethod.description?exists>${paymentMethod.description}</#if>
															(${paymentMethod.paymentMethodId})
													</#if>
												</option>
											</#list>
										</select>
									</td>
								</tr>
								<#assign openAmount = orderReadHelper.getOrderOpenAmount()>
								<tr>
									<td width="10%" align="right"><span >${uiLabelMap.AccountingAmount}</span></td>
									<td width="60%" nowrap="nowrap">
										<input type="text" name="maxAmount" value="${openAmount}"/>
									</td>
								</tr>
								<tr>
									<td colspan="2" valign="top" width="60%">
										<button type="submit" class="btn btn-small btn-primary">
											<i class="icon-ok"></i>${uiLabelMap.CommonAdd}
										</button>
									</td>
								</tr>
							</table>
						</form>
					</td>
				</tr>
			</#if>
		</#if>
	</table>
</div><!--#payment-tab-->

<#-- <script type="text/javascript">
	function receivePayment(url) {
		var strUrl = url + "&avo=" + $("#accountOneValue").val() + "&avt=" + $("#accountTwoValue").val();
		window.location.href = strUrl;
	}
</script> -->
<#include "component://baseaccounting/webapp/baseaccounting/payment/popupReceivePayment.ftl"/>