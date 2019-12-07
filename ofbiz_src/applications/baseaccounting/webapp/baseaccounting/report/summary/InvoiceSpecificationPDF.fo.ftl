<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main-page" page-width="8.5in" page-height="11in" margin-top="0in" margin-bottom="0in" margin-left="0.3in" margin-right="0.3in">
			<fo:region-body margin-top="1.5in" margin-bottom="0in"/>
			<fo:region-before extent="0in"/>
			<fo:region-after extent="0in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>

	<#assign fontsize="9px"/>
	<#assign right="100px"/>
	<#assign line="4.5px"/>
	<#assign row="16px"/>
	
	<fo:page-sequence master-reference="main-page">
		<fo:flow flow-name="xsl-region-body">
		<#escape x as x?xml>
			<fo:block>
		
				<fo:table font-size="${fontsize}">
					<fo:table-column column-width="70%"/>
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="15px" font-weight="bold" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BangKeDinhKemHoaDon)}</fo:block>
							</fo:table-cell>
			
							<fo:table-cell>
								<fo:block margin-top="4px" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCNumber)}:
                                    <#if voucherInvoice?exists>
                                    ${voucherInvoice.voucherNumber?if_exists}
                                    <#else>
                                    ${invoice.invoiceId}
                                    </#if>
                                </fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
				<#assign resultService = dispatcher.runSync("getInvoiceSpecification", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId, "userLogin", userLogin))/>
				<#assign listProduct = resultService.listProduct/> 
				<#assign listProductTax0 = resultService.listProductTax0/> 
				<#assign listSalesTax = resultService.listSalesTax/>
                <#assign listDiscount = resultService.listDiscount/>

                <#assign invoiceDate = invoice.invoiceDate/>
				<#assign cal = Static["java.util.Calendar"].getInstance()/>
				${cal.setTime(invoiceDate)}
				<#assign date = cal.get(Static["java.util.Calendar"].DATE)/>
				<#assign month = cal.get(Static["java.util.Calendar"].MONTH) + 1/>
				<#assign year = cal.get(Static["java.util.Calendar"].YEAR)/>
				<#if (date > 9)>
					<#assign dateStr = date/>
				<#else>
					<#assign dateStr = "0" + date />
				</#if>
				<#if (month > 9)>
					<#assign monthStr = month/>
				<#else>
					<#assign monthStr = "0" + month />
				</#if>
				
				<#assign invoiceTaxInfoList = delegator.findByAnd("InvoiceTaxInfoAndGeo", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId), null, false)/>
				<#if invoiceTaxInfoList?has_content>
					<#assign invoiceTaxInfo = invoiceTaxInfoList.get(0)/> 
				</#if>
				<fo:block font-size="${fontsize}" margin-left="120px">${StringUtil.wrapString(uiLabelMap.BACCDay)} ${dateStr} ${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${monthStr} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${year}</fo:block>
				
				<#assign partyBuyer = delegator.findOne("PartyFullNameDetailSimple", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", invoice.partyId), false)/>
				<#assign totalAmount = 0/>
				<fo:table font-size="${fontsize}" margin-top="20px">
					<fo:table-column column-width="60%"/>
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block>
									${StringUtil.wrapString(uiLabelMap.BACCBuyers)}: 
									<#if invoiceTaxInfo?exists && invoiceTaxInfo.partyName?has_content>
										${StringUtil.wrapString(invoiceTaxInfo.partyName?trim)}
									<#else>
										<#if partyBuyer?exists>
											${StringUtil.wrapString(partyBuyer.fullName?trim)}
										</#if>
									</#if>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block>${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}: <#if partyBuyer?exists>${partyBuyer.partyCode?if_exists}</#if></fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
				
				<fo:block font-size="${fontsize}">${StringUtil.wrapString(uiLabelMap.BACCOrganization)}: <#if invoiceTaxInfo?exists>${invoiceTaxInfo.partyName?if_exists}</#if></fo:block>
				<fo:block font-size="${fontsize}">${StringUtil.wrapString(uiLabelMap.BSAddress)}: <#if invoiceTaxInfo?exists><#if invoiceTaxInfo.address?exists>${invoiceTaxInfo.address}</#if><#if invoiceTaxInfo.stateGeoName?exists>, ${invoiceTaxInfo.stateGeoName}</#if><#if invoiceTaxInfo.countryGeoName?exists>, ${invoiceTaxInfo.countryGeoName}</#if></#if></fo:block>
				<fo:block font-size="${fontsize}">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}: <#if invoiceTaxInfo?exists>${invoiceTaxInfo.taxCode?if_exists}</#if></fo:block>
				
				<#assign voucherInvoiceList = delegator.findByAnd("VoucherInvoiceAndVoucher", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId), null, false)/>
				<#if voucherInvoiceList?has_content>
					<#assign voucherInvoice = voucherInvoiceList.get(0)/> 
				</#if>
				<fo:table font-size="${fontsize}">
					<fo:table-column column-width="40%"/>
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block>
									${StringUtil.wrapString(uiLabelMap.BangKeDinhKemHoaDonSo)}: 
									<#if voucherInvoice?exists>
										${voucherInvoice.voucherNumber?if_exists}
									<#else>
										${invoice.invoiceId}
									</#if>
								</fo:block>
							</fo:table-cell>
			
							<fo:table-cell>
								<fo:block>- ${StringUtil.wrapString(uiLabelMap.BACCDay)} ${dateStr} ${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${monthStr} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${year}</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
				
				<fo:table font-size="${fontsize}" margin-top="10px">
					<fo:table-column column-width="25px"/>
					<fo:table-column column-width="80px"/>
					<fo:table-column column-width="137px"/>
					<fo:table-column column-width="45px"/>
					<fo:table-column column-width="38px"/>
					<fo:table-column column-width="55px"/>
					<fo:table-column column-width="60px"/>
					<fo:table-column column-width="50px"/>
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BSSTT)}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCGCSKU)}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCProductName2)?upper_case}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BSCalculateUomId)?upper_case}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BSQuantity)?upper_case}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)?upper_case}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.AmountNotIncludeVAT)?upper_case}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCOnlyVAT)?upper_case}</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" text-align="center" display-align="center">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.AmountIncludeVAT)?upper_case}</fo:block>
							</fo:table-cell>
						</fo:table-row>
                        <#assign totalAmountTax0 = 0/>
                        <#assign totalDiscount = 0/>
                        <#assign totalAmountNotTax = 0/>
                        <#assign totalTaxAmount = 0/>
                        <#assign totalAmount = 0/>
                        <#if listProduct?has_content || listProductTax0?has_content || listDiscount?has_content>
							<#list listProduct as tempProduct>
                                <#assign totalAmountNotTax = totalAmountNotTax + tempProduct.amountNotTax/>
                                <#assign totalAmount = totalAmount + tempProduct.amountNotTax/>
								<fo:table-row>
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">${tempProduct_index + 1}</fo:block>
									</fo:table-cell>
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">${tempProduct.productCode?if_exists}</fo:block>
									</fo:table-cell>

									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">${StringUtil.wrapString(tempProduct.productName?if_exists)}</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">
											<#if tempProduct.uomAbbreviation?exists>
												${StringUtil.wrapString(tempProduct.uomAbbreviation)}
											<#else>
												${StringUtil.wrapString(tempProduct.quantityUomId?if_exists)}
											</#if>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">${tempProduct.quantity}</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=tempProduct.amount isoCode=tempProduct.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=tempProduct.amountNotTax isoCode=tempProduct.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											${tempProduct.taxPercentage}%
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=tempProduct.amountTaxInc isoCode=tempProduct.currencyUomId/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</#list>
							<#list listProductTax0 as tempProductTax0>
								<#assign totalAmountTax0 = totalAmountTax0 + tempProductTax0.amountNotTax/>
								<fo:table-row>
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">${listProduct?size + tempProductTax0_index + 1}</fo:block>
									</fo:table-cell>
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">${tempProductTax0.productCode}</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">${StringUtil.wrapString(tempProductTax0.productName)}</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
										<fo:block text-align="center">
											<#if tempProductTax0.uomAbbreviation?exists>
												${StringUtil.wrapString(tempProductTax0.uomAbbreviation)}
											<#else>
												${StringUtil.wrapString(tempProductTax0.quantityUomId?if_exists)}	
											</#if>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">${tempProductTax0.quantity}</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=tempProductTax0.amount isoCode=tempProductTax0.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=tempProductTax0.amountNotTax isoCode=tempProductTax0.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											0%
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=tempProductTax0.amountTaxInc isoCode=tempProductTax0.currencyUomId/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</#list> 
							<#list listDiscount as tempProduct>
                                <#assign totalDiscount = totalDiscount + tempProduct.amountNotTax/>
                                <fo:table-row>
                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
                                        <fo:block text-align="center">${tempProduct_index + 1}</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
                                        <fo:block text-align="center">${tempProduct.productCode?if_exists}</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
                                        <fo:block text-align="center">${StringUtil.wrapString(tempProduct.productName)}</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0" text-align="center" display-align="center">
                                        <fo:block text-align="center">
                                            <#if tempProduct.uomAbbreviation?exists>
												${StringUtil.wrapString(tempProduct.uomAbbreviation)}
											<#else>
                                            ${StringUtil.wrapString(tempProduct.quantityUomId?if_exists)}
                                            </#if>
                                        </fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
                                        <fo:block text-align="right">${tempProduct.quantity}</fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0">
                                        <fo:block text-align="right">
                                            <#if tempProduct.amount <= 0>
                                                (<@ofbizCurrency amount=tempProduct.amount*(-1) isoCode=tempProduct.currencyUomId/>)
                                            <#else>
                                                <@ofbizCurrency amount=tempProduct.amount isoCode=tempProduct.currencyUomId/>
                                            </#if>
                                        </fo:block>
                                    </fo:table-cell>                                    

                                    <fo:table-cell border="solid 1px black" padding="2mm 1mm 0" number-columns-spanned="3">
                                        <fo:block text-align="right">
                                            <#if tempProduct.amountTaxInc <= 0>
                                                (<@ofbizCurrency amount=tempProduct.amountTaxInc*(-1) isoCode=tempProduct.currencyUomId/>)
                                            <#else>
                                                <@ofbizCurrency amount=tempProduct.amountTaxInc isoCode=tempProduct.currencyUomId/>
                                            </#if>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </#list>
						<#else>
							<fo:table-row>
								<fo:table-cell border="solid 1px black" number-columns-spanned="9" padding="2mm 1mm 0">
									<fo:block text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</#if>
                        <fo:table-row>
                            <fo:table-cell border="solid 1px black" border-bottom-width="0px" number-columns-spanned="2"></fo:table-cell>
                            <fo:table-cell border="solid 1px black" background-color="yellow" padding="1mm 0.5mm 0">
                                <fo:block text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.AmountNotIncludeVAT)}</fo:block>
                            </fo:table-cell>

                            <fo:table-cell border="solid 1px black" background-color="yellow" number-columns-spanned="4" padding="1mm 1mm 0">
                                <fo:block text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BSAbbValueAddedTax)}</fo:block>
                            </fo:table-cell>

                            <fo:table-cell border="solid 1px black" background-color="yellow" number-columns-spanned="2" padding="1mm 1mm 0">
                                <fo:block text-align="center" font-weight="bold">${StringUtil.wrapString(uiLabelMap.AmountIncludeVAT)}</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
						<#if listSalesTax?has_content>
							<#if listProductTax0?has_content>
                                <#assign totalAmountNotTax = totalAmountNotTax + totalAmountTax0/>
                                <#assign totalAmount = totalAmount + totalAmountTax0/>
								<fo:table-row>
									<fo:table-cell border="solid 1px black" border-top-width="0px" number-columns-spanned="2" padding="1mm 1mm 0" border-bottom-width="0px">
										<fo:block text-align="left" padding="1mm 1mm 0">
											${StringUtil.wrapString(uiLabelMap.BACCNoVATType)}
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=totalAmountTax0 isoCode=listProductTax0.get(0).currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell border="solid 1px black" number-columns-spanned="4"  padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=0 isoCode=listProductTax0.get(0).currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell border="solid 1px black" number-columns-spanned="2"  padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=totalAmountTax0 isoCode=listProductTax0.get(0).currencyUomId/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</#if>
							
							<#list listSalesTax as saleTax>
                                <#assign totalTaxAmount = totalTaxAmount + saleTax.totalTaxAmount/>
								<fo:table-row>
									<fo:table-cell border="solid 1px black" border-top-width="0px" number-columns-spanned="2" padding="1mm 1mm 0" <#if saleTax_has_next>border-bottom-width="0px"</#if>>
										<fo:block text-align="left" padding="1mm 1mm 0">
											<#if (saleTax.taxPercentage > 0)>
												${StringUtil.wrapString(uiLabelMap.BACCVATType)} ${saleTax.taxPercentage}%:
											<#else>
                                                <#if saleTax.taxDescription?exists && saleTax.taxDescription.equals("VAT 0%")>
                                                     ${StringUtil.wrapString(uiLabelMap.BACCVATType)} ${saleTax.taxPercentage}%:
                                                <#else>
                                                     ${StringUtil.wrapString(uiLabelMap.BACCNoVATType)}
                                                </#if>
											</#if>
										</fo:block>
									</fo:table-cell>
									
									<fo:table-cell border="solid 1px black" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=saleTax.amountNotTax isoCode=saleTax.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell border="solid 1px black" number-columns-spanned="4"  padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=saleTax.totalTaxAmount isoCode=saleTax.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell border="solid 1px black" number-columns-spanned="2"  padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=saleTax.amountTaxInc isoCode=saleTax.currencyUomId/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>	
							</#list>
						</#if>
						
						<#assign currencyUomId = "VND" />
                        <#assign totalAmountNotTax = totalAmountNotTax + totalDiscount/>
                        <#assign totalAmount = totalAmount + totalDiscount + totalTaxAmount/>
						<#if listSalesTax?has_content>
							<#assign currencyUomId = listSalesTax.get(0).currencyUomId />
						</#if>
						<fo:table-row>
							<fo:table-cell border="solid 1px black" border-top-width="0px" number-columns-spanned="2" padding="1mm 1mm 0">
								<fo:block text-align="left" padding="1mm 1mm 0" font-weight="bold">
									${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}:
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" padding="1mm 1mm 0" font-weight="bold">
								<fo:block text-align="right">
									<@ofbizCurrency amount=totalAmountNotTax isoCode=currencyUomId/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" number-columns-spanned="4"  padding="1mm 1mm 0" font-weight="bold">
								<fo:block text-align="right">
									<@ofbizCurrency amount=totalTaxAmount isoCode=currencyUomId/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border="solid 1px black" number-columns-spanned="2"  padding="1mm 1mm 0" font-weight="bold">
								<fo:block text-align="right">
									<@ofbizCurrency amount=totalAmount isoCode=currencyUomId/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>	
					</fo:table-body>
				</fo:table>
				<#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${totalAmount?string?if_exists}")/>
				<fo:block font-size="${fontsize}" margin-top="10px">${StringUtil.wrapString(uiLabelMap.BACCAmountWriteInWord)}: ${StringUtil.wrapString(amountInWords?if_exists)}</fo:block>
				
				<fo:table font-size="${fontsize}" margin-top="3mm">
					<fo:table-column column-width="50%"/>
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="${fontsize}" font-weight="bold" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.NguoiMuaHang)}</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block font-size="${fontsize}" font-weight="bold" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.NguoiBanHang)}</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
				<fo:block font-size="${fontsize}" margin-top="80px" text-align="right">${StringUtil.wrapString(uiLabelMap.SoBangKe)}: .................../Trang.......</fo:block>
			</fo:block>
		</#escape>
		</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>