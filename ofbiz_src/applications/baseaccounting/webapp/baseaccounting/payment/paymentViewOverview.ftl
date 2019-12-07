<div class="row-fluid" style="position: relative;">
	<#if isPaymentEditable?exists && isPaymentEditable>
		<div style="top: -10px; right: 5px; position: absolute; background: rgba(182, 189, 179, 0.3); padding: 5px 20px">
			<a href="javascript:void(0)" id="editPaymentBtn"><i class="icon-edit"></i>${uiLabelMap.CommonEdit}</a>
		</div>
	</#if>
	<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
		${uiLabelMap.BACCPAYMENT}
	</h3>
</div>
<div class="row-fluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCPaymentId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>
							<#if payment.paymentCode?exists>
								${payment.paymentCode}
							<#else>
								${parameters.paymentId}
							</#if>
						</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCPaymentTypeId}:</label>
					</div>
					<div class="div-inline-block">
						<span class=""><i id="viewPaymentTypeId">	
							<#if payment.paymentTypeId?exists>
								<#assign paymentType = delegator.findOne("PaymentType", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentTypeId", payment.paymentTypeId), false)/>
							${StringUtil.wrapString(paymentType.get("description", locale))}
							<#else>	
								____________
							</#if>
						</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCPaymentMethodTypeId}:</label>
					</div>
					<div class="div-inline-block">
						<span class=""><i id="viewPaymentMethodId">
							<#if payment.paymentMethodId?exists>
								<#assign paymentMethod = delegator.findOne("PaymentMethod", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId", payment.paymentMethodId), false)/>
							${StringUtil.wrapString(paymentMethod.get("description", locale))}
							<#else>	
								_____________
							</#if>
						</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCStatusId}:</label>
					</div>
					<div class="div-inline-block">
						<#if listStatusItem?has_content && payment.statusId?has_content>
							<#list listStatusItem as status>
								<#if status.statusId == payment.statusId>
									<span><i>${status.get('description',locale)?default('')}</i></span>
								</#if>
							</#list>
						</#if>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCReferenceNum}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${payment.paymentRefNum?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCAmount}:</label>
					</div>
					<div class="div-inline-block">
						<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.amount?if_exists, payment.currencyUomId?if_exists, locale) />
					   	<span><i id="viewPaymentAmount">${amount?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCEffectiveDate}:</label>
					</div>
					<div class="div-inline-block">
						<#assign effectiveDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(payment.effectiveDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
				   		<span><i id="viewEffectiveDatePayment">${effectiveDate?if_exists}</i></span>
					</div>
				</div>
                <div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BACCPaidDate}:</label>
                    </div>
                    <div class="div-inline-block">
                    <#if payment.paidDate?exists>
                        <#assign paidDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(payment.paidDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
                            <span><i id="viewPaidDatePayment">${paidDate?if_exists}</i></span>
                    </#if>
                    </div>
                </div>
			</div><!--/span6-->
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCPaymentPartyIdFrom}:</label>
					</div>
					<div class="div-inline-block">
                    <#if payment.partyIdFrom?exists>
						<#assign partyNameFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, payment.partyIdFrom?if_exists, true, true)?if_exists>
						<span><i id="viewPartyFromName">${partyNameFrom}</i></span>
                    </#if>
					</div>
				</div>
				<#if businessType == "AR">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BACCPayers}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentOrganizationName">${payment.organizationName?if_exists}</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.IdentifyCard}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentIdentifyCard">${payment.identifyCard?if_exists}</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.IssuedDate}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentIssuedDate">
								<#if payment.issuedDate?exists>
								${payment.issuedDate?string["dd/MM/yyyy"]}
								</#if>
							</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.IssuePlace}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentIssuedPlace">${payment.issuedPlace?if_exists}</i></span>
						</div>
					</div>
				</#if> 
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCPaymentPartyIdTo}:</label>
					</div>
					<div class="div-inline-block">
						<#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, payment.partyIdTo?if_exists, true, true)?if_exists>	
						<span><i id="viewPartyToName">${partyNameTo}</i></span>
					</div>
				</div>
				<#if businessType == "AP">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BACCPayeeName}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentOrganizationName">${payment.organizationName?if_exists}</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.IdentifyCard}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentIdentifyCard">${payment.identifyCard?if_exists}</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.IssuedDate}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentIssuedDate">
								<#if payment.issuedDate?exists>
								${payment.issuedDate?string["dd/MM/yyyy"]}
								</#if>
							</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.IssuePlace}:</label>
						</div>
						<div class="div-inline-block">
							<span><i id="viewPaymentIssuedPlace">${payment.issuedPlace?if_exists}</i></span>
						</div>
					</div>
				</#if>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BACCComment}:</label>
					</div>
					<div class="div-inline-block">
						<span><i id="viewPaymentComment">${payment.comments?if_exists}</i></span>
					</div>
				</div>
                <#if payment.currencyUomId != 'VND'>
                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BACCExchangedRate}:</label>
                        </div>
                        <div class="div-inline-block">
                        <#assign conversionFactor = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.conversionFactor?if_exists, 'VND', locale, 2) />
                            <span><i id="viewConversionFactor">${conversionFactor?if_exists}</i></span>
                        </div>
                    </div>
                </#if>
			</div><!--/span6-->
		</div>
	</div>
</div>