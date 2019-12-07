<style type="text/css">
@page {
	/* dimensions for the whole page */
	size: A4;
	margin: 0;
}

html {
	/* off-white, so body edge is visible in browser */
	background: #eee;
}

body {
	/* A5 dimensions */
	height: 210mm;
	width: 148.5mm;
	margin: 0;
}
</style>
<#escape x as x?xml>
	<#if payment.paymentMethodTypeId == "CASH">
		<fo:block font-size="11" font-family="Times">
			<fo:table table-layout="fixed">
				<fo:table-column column-width="70%"/>
				<fo:table-column/>
				
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell text-align="left">
							<#assign partyNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, payment.partyIdFrom?if_exists, true, true)?if_exists>
		    				<#assign fullAddress = ""/>
		    				<#if partyAddressFrom.address1?has_content> 
				            	<#assign fullAddress = fullAddress + " " + partyAddressFrom.address1 + ","/> 
			            	</#if>
		    				<#if partyAddressFrom.address2?has_content> 
				             	<#assign fullAddress = fullAddress + " " +  partyAddressFrom.address2 + ","/> 
				            </#if>
				            <#if partyAddressFrom.wardGeoId?has_content>
				            	<#if "_NA_" == partyAddressFrom.wardGeoId>
				            		<#assign fullAddress = fullAddress + "___, "/> 
					           	<#else>
					           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : partyAddressFrom.wardGeoId}, true)!/>
					           		<#assign fullAddress = fullAddress + " " + wardGeo?default(partyAddressFrom.wardGeoId).geoName?default(partyAddressFrom.wardGeoId) + ", "/> 
								</#if>
							</#if>
				            <#if partyAddressFrom.districtGeoId?has_content>
				            	<#if "_NA_" == partyAddressFrom.districtGeoId>
				            		<#assign fullAddress = fullAddress + "___, "/> 
					           	<#else>
					            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : partyAddressFrom.districtGeoId}, true)!/>
					            	<#assign fullAddress = fullAddress + " " + districtGeo?default(partyAddressFrom.districtGeoId).geoName?default(partyAddressFrom.districtGeoId) + ", "/> 
					           	</#if>
							</#if>
				            <#if partyAddressFrom.stateProvinceGeoId?has_content>
				            	<#if "_NA_" == partyAddressFrom.stateProvinceGeoId>
				            		<#assign fullAddress = fullAddress + "___, "/> 
					           	<#else>
					            	<#assign provinceGeo = delegator.findOne("Geo", {"geoId" : partyAddressFrom.stateProvinceGeoId}, true)!/>
					            	<#assign fullAddress = fullAddress + " " + provinceGeo?default(partyAddressFrom.districtGeoId).geoName?default(partyAddressFrom.stateProvinceGeoId) /> 
					           	</#if>
							</#if>
							<fo:block font-weight="bold" font-size="10">${partyNameFrom?if_exists}</fo:block>
							<fo:block font-style="italic" font-size="10">${fullAddress?if_exists}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left">
							<fo:block margin-left="25px">${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}: <#if payment.paymentCode?exists>${payment.paymentCode}<#else>${parameters.paymentId?if_exists}</#if></fo:block>
							<fo:block margin-left="25px">${StringUtil.wrapString(uiLabelMap.BACCDEBIT)}:.........../${StringUtil.wrapString(uiLabelMap.BACCCREDIT)}:...........</fo:block>	
						</fo:table-cell>
					</fo:table-row>	
				</fo:table-body>
			</fo:table> 
			<fo:table table-layout="fixed" margin-top="10px">
				<fo:table-column/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell text-align="center">
							<fo:block font-weight="bold" font-size="160%" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BACCNewPayableVoucher)}</fo:block>				
							<fo:block font-style="italic"> ${StringUtil.wrapString(uiLabelMap.BACCDay)} ${payment.effectiveDate?string["dd"]} ${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${payment.effectiveDate?string["MM"]} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${payment.effectiveDate?string["yyyy"]}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px" width="100%">
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="10%"/>
				<fo:table-column column-width="15%"/>
				<fo:table-column column-width="8%"/>
				<fo:table-column column-width="10%"/>
				<fo:table-body>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.BACCPayeeName)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<fo:block text-transform="uppercase">${payment.organizationName?if_exists}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.BACCOrganization)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<#if payment.partyName?exists>
								<fo:block text-transform="uppercase">${StringUtil.wrapString(payment.partyName)}</fo:block>
							<#else>
								<#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, payment.partyIdTo?if_exists, true, true)?if_exists>
								<fo:block text-transform="uppercase">${partyNameTo?if_exists}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.CommonAddress1)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<#if payment.partyName?exists>
								<fo:block>${payment.address?if_exists}</fo:block>
							<#else>
			    				<#assign fullAddressTo = ""/>
			    				<#if partyAddressTo.address1?has_content> 
					            	<#assign fullAddressTo = fullAddressTo + " " + partyAddressTo.address1 + ","/> 
				            	</#if>
			    				<#if partyAddressTo.address2?has_content> 
					             	<#assign fullAddressTo = fullAddressTo + " " +  partyAddressTo.address2 + ","/> 
					            </#if>
					            <#if partyAddressTo.wardGeoId?has_content>
					            	<#if "_NA_" == partyAddressTo.wardGeoId>
					            		<#assign fullAddressTo = fullAddressTo + "___, "/> 
						           	<#else>
						           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : partyAddressTo.wardGeoId}, true)!/>
						           		<#assign fullAddressTo = fullAddressTo + " " + wardGeo?default(partyAddressTo.wardGeoId).geoName?default(partyAddressTo.wardGeoId) + ", "/> 
									</#if>
								</#if>
					            <#if partyAddressTo.districtGeoId?has_content>
					            	<#if "_NA_" == partyAddressTo.districtGeoId>
					            		<#assign fullAddressTo = fullAddressTo + "___, "/> 
						           	<#else>
						            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : partyAddressTo.districtGeoId}, true)!/>
						            	<#assign fullAddressTo = fullAddressTo + " " + districtGeo?default(partyAddressTo.districtGeoId).geoName?default(partyAddressTo.districtGeoId) + ", "/> 
						           	</#if>
								</#if>
					            <#if partyAddressTo.stateProvinceGeoId?has_content>
					            	<#if "_NA_" == partyAddressTo.stateProvinceGeoId>
					            		<#assign fullAddressTo = fullAddressTo + "___, "/> 
						           	<#else>
						            	<#assign provinceGeo = delegator.findOne("Geo", {"geoId" : partyAddressTo.stateProvinceGeoId}, true)!/>
						            	<#assign fullAddressTo = fullAddressTo + " " + provinceGeo?default(partyAddressTo.districtGeoId).geoName?default(partyAddressTo.stateProvinceGeoId) /> 
						           	</#if>
								</#if>
								<fo:block>${fullAddressTo?if_exists}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.IdentifyCard)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left">
							<fo:block>${payment.identifyCard?if_exists}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.IssuedDatePDF)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left">
							<fo:block>
								<#if payment.issuedDate?exists>
									${payment.issuedDate?string["dd/MM/yyyy"]}
								</#if>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.IssuePlacePDF)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left">
							<fo:block>
								${payment.issuedPlace?if_exists}
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.BACCReasonEnpense)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<fo:block>${payment.comments?if_exists}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.BACCAmount)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<#if payment?exists>
							<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.amount, payment.currencyUomId?default("VND"), locale)/>
							</#if>
							<fo:block font-size="100%">${amount?if_exists}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>${StringUtil.wrapString(uiLabelMap.BACCWriteInWords)}:</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<#if payment?exists>
							<#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${payment.amount?string?if_exists}")/>
							</#if>
							<fo:block font-weight="bold">${amountInWords?if_exists}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="18px">
						<fo:table-cell text-align="left">
							<fo:block>
								<#assign listInvoices = delegator.findByAnd("PaymentApplication", {"paymentId" : payment.paymentId}, null, false)>
								<fo:inline>${StringUtil.wrapString(uiLabelMap.BACCAttach)}:</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="left" number-columns-spanned="5">
							<fo:block>
								<#list listInvoices as item>
									<fo:inline font-weight="bold">${item.invoiceId}</fo:inline>
								</#list>
								${StringUtil.wrapString(uiLabelMap.BACCOriginalVouchers)}
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed" margin-top="10px">
				<fo:table-column/>
				<fo:table-column/>
				<fo:table-column/>
				<fo:table-column/>
				<fo:table-column/>
				<fo:table-body>
					<fo:table-row >
						<fo:table-cell width="100px">
							<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCEstablished)}</fo:block> 
						</fo:table-cell>
						<fo:table-cell width="100px">
							<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCPayeeName)}</fo:block> 
						</fo:table-cell>
						<fo:table-cell width="90px">
							<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCCAshier)}</fo:block> 
						</fo:table-cell>
						<fo:table-cell width="110px">
							<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCChiefAccount)}</fo:block> 
						</fo:table-cell>
						<fo:table-cell width="110px">
							<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDirector)}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row >
						<fo:table-cell >
							<fo:block text-align="center" >(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>

			<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="50px">
				<fo:table-column/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell text-align="left">
							<fo:block>
								<fo:inline>--------------------------------------------------------------------------------------------------------------------------------------------</fo:inline>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell text-align="left">
							<fo:block>
								<fo:inline font-size="100%" text-transform="uppercase">SGC-QF-05/KT-10/02</fo:inline>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>	
				</fo:table-body>
			</fo:table>
		</fo:block>
	<#else>
		<fo:table border="solid 1px black">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="380px">
						<fo:block font-size="20px" margin="5px 0px 0px 5px" font-weight="bold" text-transform="uppercase">
							${StringUtil.wrapString(uiLabelMap.accPaymentOrder)}
						</fo:block>
						<fo:block font-size="10px" margin="0px 0px 0px 5px" font-weight="bold" text-transform="uppercase">
							${StringUtil.wrapString(uiLabelMap.TransferAccountMailPhone)}</fo:block>
						<fo:block font-size="10px" margin="0px 0px 0px 5px">
							<fo:inline>
								${StringUtil.wrapString(uiLabelMap.PayingOrganizationName)}:
							</fo:inline>
							<fo:inline text-transform="uppercase">
								<#assign partyFrom = delegator.findOne("PartyFullNameDetailSimple", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", payment.partyIdFrom), false)/>
								${StringUtil.wrapString(partyFrom.fullName)}
							</fo:inline>
						</fo:block>
						<fo:block font-size="10px" margin="0px 0px 0px 5px">
							<#assign paymentMethodFinAccList = delegator.findByAnd("PaymentMethodAndFinAccount", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId", payment.paymentMethodId), null, false)/>
							<#if paymentMethodFinAccList?has_content>
								<#assign paymentMethodFinAcc = paymentMethodFinAccList.get(0)/> 
							</#if>
							${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}: <#if paymentMethodFinAcc?exists>${paymentMethodFinAcc.get("finAccountPin")}</#if>
						</fo:block>
						<fo:block font-size="10px" margin="0px 0px 10px 2px">
							<fo:table>
								<fo:table-column/>
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="240px">
											<fo:block>
												<fo:inline>
													${StringUtil.wrapString(uiLabelMap.AtTheBank)}:
												</fo:inline>
												<fo:inline text-transform="uppercase">
													<#if paymentMethodFinAcc?exists>
														 ${StringUtil.wrapString(paymentMethodFinAcc.finAccountName)}
													</#if>
												</fo:inline>
											 </fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<fo:inline>
												${StringUtil.wrapString(uiLabelMap.BACCStateProviceShort)}:
												</fo:inline>
												<fo:inline>
												 <#if paymentMethodFinAcc?exists>
												 	<#if paymentMethodFinAcc.stateProvinceGeoId?exists>
												 		<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", paymentMethodFinAcc.stateProvinceGeoId), false)/>
												 		${StringUtil.wrapString(stateProvinceGeo.geoName)}
												 	</#if>
												 </#if>
												</fo:inline>
											 </fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block font-size="10px" margin="10px 0px 0px 10px">${StringUtil.wrapString(uiLabelMap.BACCNumber)}: <#if payment.paymentCode?exists>${payment.paymentCode}<#else>${payment.paymentId}</#if></fo:block>
						<fo:block font-size="10px" margin="0px 0px 0px 10px">${StringUtil.wrapString(uiLabelMap.BACCPaymentCreatedDate)}: ${payment.effectiveDate?string["dd/MM/yyyy"]}</fo:block>
						<fo:block margin="0px 0px 0px 5px">
							<fo:table border="solid 1px black">
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="130px" height="40px">
											<fo:block font-size="10px" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.DebitAccount)}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:table border="solid 1px black" border-top="none">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="380px">
						<fo:block font-size="10px" margin="12px 0px 0px 5px">
							<fo:inline>
								${StringUtil.wrapString(uiLabelMap.ReceivingOrganizationName)}:
							</fo:inline>
							<fo:inline text-transform="uppercase">
							<#if payment.partyName?exists>
								${StringUtil.wrapString(payment.partyName)}
							<#else>
								<#assign partyTo = delegator.findOne("PartyFullNameDetailSimple", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", payment.partyIdTo), false)/>
								${StringUtil.wrapString(partyTo.fullName)}
							</#if>
							</fo:inline>
						</fo:block>
						<fo:block font-size="10px" margin="0px 0px 0px 5px">
							<#if payment.partyName?exists>
								${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}: <#if payment.finAccountCode?exists>${payment.finAccountCode}</#if>
							<#else>
								<#assign finAccountPartyToList = delegator.findByAnd("FinAccount", Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", payment.partyIdTo, "statusId", "FNACT_ACTIVE", "finAccountTypeId", "BANK_ACCOUNT"), null, false)/>
								<#if finAccountPartyToList?has_content>
									<#assign finAccountPartyTo = finAccountPartyToList.get(0)/> 
								</#if>								
								${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}: <#if finAccountPartyTo?exists>${finAccountPartyTo.get("finAccountCode")}</#if>
							</#if>
						</fo:block>
						<fo:block font-size="10px" margin="0px 0px 15px 2px">
							<fo:table>
								<fo:table-column/>
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="240px">
											<fo:block>
												<fo:inline>
													${StringUtil.wrapString(uiLabelMap.AtTheBank)}:
												</fo:inline>
												<fo:inline text-transform="uppercase">
													<#if payment.partyName?exists>
														${StringUtil.wrapString(payment.finAccountName)}
													<#else>
														<#if finAccountPartyTo?exists>
															 ${StringUtil.wrapString(finAccountPartyTo.finAccountName)}
														</#if>
													</#if>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>
												<fo:inline>
													${StringUtil.wrapString(uiLabelMap.BACCStateProviceShort)}:
												</fo:inline>
												<fo:inline>
													<#if payment.partyName?exists>
														<#if payment.stateGeoId?exists>
													 		<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", payment.stateGeoId), false)/>
													 		${StringUtil.wrapString(stateProvinceGeo.geoName)}
													 	</#if>														
													<#else>
														<#if finAccountPartyTo?exists>
															<#if finAccountPartyTo.stateProvinceGeoId?exists>
														 		<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", finAccountPartyTo.stateProvinceGeoId), false)/>
														 		${StringUtil.wrapString(stateProvinceGeo.geoName)}
														 	</#if>
														</#if>
													</#if>
												</fo:inline>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block margin="10px 0px 0px 5px">
							<fo:table border="solid 1px black">
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="130px" height="40px">
											<fo:block font-size="10px" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.CreditAccount)}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>

		<fo:table border="solid 1px black" border-top="none">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="380px">
						<fo:block font-size="10px" margin="12px 0px 0px 5px">
							<#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${payment.amount?string?if_exists}", "${payment.currencyUomId?if_exists}")/>
							${StringUtil.wrapString(uiLabelMap.BACCAmountWriteInWord)}: ${StringUtil.wrapString(amountInWords?if_exists)}.
						</fo:block>
						<fo:block font-size="10px" margin="0px 0px 0px 5px">
							<fo:inline>
								${StringUtil.wrapString(uiLabelMap.PaymentContent)}: 
							</fo:inline>
							<fo:inline text-transform="uppercase">
								${StringUtil.wrapString(payment.comments?if_exists)}
							</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block margin="10px 0px 10px 5px">
							<fo:table border="solid 1px black">
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="130px" height="55px">
											<fo:block font-size="10px" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BACCAmountWriteInNumber)}</fo:block>
											<fo:block font-size="12px" text-align="right" margin="20px 4px 0px 0px">
												<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.amount, payment.currencyUomId?if_exists, locale)/>
												${amount}
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:table border="solid 1px black" border-top="none">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="175px" border-right="solid 1px black">
						<fo:block font-size="10px" text-align="center" font-weight="bold" margin-top="5px">${StringUtil.wrapString(uiLabelMap.PayingOrganization)}</fo:block>	
						<fo:block margin="20px 0px 28px 0px">
							<fo:table>
								<fo:table-column/>
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="80px" height="55px">
											<fo:block font-size="10px" text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCAcountant)}</fo:block>
										</fo:table-cell>
										<fo:table-cell width="80px" height="55px">
											<fo:block font-size="10px" text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCBankOwner)}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>	
					</fo:table-cell>
		
					<fo:table-cell width="175px" border-right="solid 1px black">
						<fo:block font-size="10px" text-align="center" font-weight="bold" margin-top="5px">${StringUtil.wrapString(uiLabelMap.AccountingBank)} A</fo:block>
						<fo:block font-size="10px" margin-left="6px">${StringUtil.wrapString(uiLabelMap.DateRecordDocument)}: ....../....../........</fo:block>
						<fo:block margin="8px 0px 28px 0px">
							<fo:table>
								<fo:table-column/>
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="80px" height="55px">
											<fo:block font-size="10px" text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCAcountant)}</fo:block>
										</fo:table-cell>
										<fo:table-cell width="80px" height="55px">
											<fo:block font-size="10px" text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCChiefAccount)}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>	
					</fo:table-cell>
		
					<fo:table-cell width="175.6px">
						<fo:block font-size="10px" text-align="center" font-weight="bold" margin-top="5px">${StringUtil.wrapString(uiLabelMap.AccountingBank)} B</fo:block>
						<fo:block font-size="10px" margin-left="6px">${StringUtil.wrapString(uiLabelMap.DateRecordDocument)}: ....../....../........</fo:block>
						<fo:block margin="8px 0px 28px 0px">
							<fo:table>
								<fo:table-column/>
								<fo:table-column/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell width="80px" height="55px">
											<fo:block font-size="10px" text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCAcountant)}</fo:block>
										</fo:table-cell>
										<fo:table-cell width="80px" height="55px">
											<fo:block font-size="10px" text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCChiefAccount)}</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>	
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:table margin-top="2px" font-size="10px">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="175px">
						<fo:block margin-left="25px">SGC-KT/BM06</fo:block>	
					</fo:table-cell>
					<fo:table-cell width="175px">
						<fo:block text-align="center">${StringUtil.wrapString(uiLabelMap.BACCRelease)}: 02</fo:block>	
					</fo:table-cell>
					<fo:table-cell width="175.6px">
						<fo:block text-align="right">${StringUtil.wrapString(uiLabelMap.CommonPage)}: 1/1</fo:block>	
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</#if>
</#escape>