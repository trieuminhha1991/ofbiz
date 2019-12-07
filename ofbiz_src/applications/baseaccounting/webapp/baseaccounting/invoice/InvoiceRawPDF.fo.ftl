<#if  "SALES_INVOICE_TOTAL" = invoice.invoiceTypeId> 
	<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main-page" page-width="8.5in" page-height="11in" margin-top="0in" margin-bottom="0in" margin-left="0.3in" margin-right="0.3in">
				<fo:region-body margin-top="1.71in" margin-bottom="0in"/>
				<fo:region-before extent="0in"/>
				<fo:region-after extent="0in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
	
		<#assign fontsize="9.5px"/>
		<#assign right="100px"/>
		<#assign line="4.5px"/>
		<#assign row="16px"/>
	
		<fo:page-sequence master-reference="main-page">
			<fo:flow flow-name="xsl-region-body">
			<fo:block>
		
			<fo:table width="60px" font-size="${fontsize}" margin-left="120px" margin-top="10px">
				<fo:table-column column-width="20px"/>
				<fo:table-column column-width="20px"/>
				<fo:table-column column-width="20px"/>
				<#if invoice??>
					${invoice}
					${invoice.invoiceDate?string["dd/MM/yyyy"]}
				</#if>		
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>${invoice.invoiceDate?string["dd"]}</fo:block>
						</fo:table-cell>
	
						<fo:table-cell>
							<fo:block margin-left="30px">${invoice.invoiceDate?string["MM"]}</fo:block>
						</fo:table-cell>
	
						<fo:table-cell>
							<fo:block margin-left="60px">${invoice.invoiceDate?string["yyyy"]}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
			
			<fo:table font-size="${fontsize}">
				<fo:table-column column-width="65%"/>
				<fo:table-column/>
				<fo:table-body>
					<fo:table-row height="5.65mm">
						<fo:table-cell number-columns-spanned="2">							
							<fo:block margin="1px 0px 0px 85px">${StringUtil.wrapString(uiLabelMap.BACCPartyNA)}</fo:block>
						</fo:table-cell>
					</fo:table-row>
	
					<fo:table-row height="5.65mm">										
						<fo:table-cell number-columns-spanned="2">							    				
								<fo:block margin="2px 0px 0px 40px" line-height="${row}"></fo:block>						
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell number-columns-spanned="2">
			    				<#assign fullAddress = ""/>				
								<fo:block margin="2px 0px 0px 40px" line-height="${row}"></fo:block>
						</fo:table-cell>
					</fo:table-row>				
	
					<fo:table-row>
						<fo:table-cell>
							<fo:block margin="2px 0px 0px 50px">${StringUtil.wrapString(uiLabelMap.BACCBillingPartyTaxId)}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
								<fo:block margin="2px 0px 0px 10px">${invoice.invoiceId}</fo:block>	
						</fo:table-cell>
					</fo:table-row>
	
					<fo:table-row>
						<fo:table-cell>
							<fo:block margin="4px 0px 0px 70px">${StringUtil.wrapString(uiLabelMap.BACCPaymentMethodInvoiceId)}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block margin="4px 0px 0px 00px">${invoice.description?if_exists}</fo:block>
						</fo:table-cell>
					</fo:table-row>
	
				</fo:table-body>
			</fo:table>
			
			<#assign totalVat0 = 0 />
			<#assign totalVat5 = 0 />
			<#assign totalVat10 = 0 />
			<#if invoice??>
				<#if vatTaxesByType.get("VAT10")??>
					<#assign formatedVat10 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatTaxesByType.get("VAT10"), locale)/>
					<#assign totalVat10 = vatTaxesByType.get("VAT10") />
				</#if>
				<#if vatTaxesByType.get("VAT5")??>
					<#assign formatedVat5 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatTaxesByType.get("VAT5"), locale)/>
					<#assign totalVat5 = vatTaxesByType.get("VAT5") />
				</#if>
			</#if>
	
			<#if invoice??>
				<#if vatAmountByType.get("VAT10")??>
					<#assign formatedAmountBeforeVat10 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatAmountByType.get("VAT10"), locale)/>
					<#assign totalVat10 = totalVat10 + vatAmountByType.get("VAT10") />
				</#if>
				<#if vatAmountByType.get("VAT5")??>
					<#assign formatedAmountBeforeVat5 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatAmountByType.get("VAT5"), locale)/>
					<#assign totalVat5 = totalVat5 + vatAmountByType.get("VAT5") />
				</#if>
				<#if vatAmountByType.get("VAT0")??>
					<#assign formatedAmountBeforeVat0 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatAmountByType.get("VAT0"), locale)/>
					<#assign totalVat0 = vatAmountByType.get("VAT0") />
				</#if>
			</#if>
			
			<#if totalVat10 != 0>
				<#assign formatedAmountTotalVat10 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(totalVat10, locale)/>
			</#if>
			<#if totalVat5 != 0>
				<#assign formatedAmountTotalVat5 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(totalVat5, locale)/>
			</#if>
			<#if totalVat0 != 0>
				<#assign formatedAmountTotalVat0 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(totalVat0, locale)/>
			</#if>
			
			<#if invoiceNoTaxTotal != 0>
				<#assign formatedInvoiceNoTaxTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceNoTaxTotal, locale)/>
			</#if>
			<#if invoiceTaxTotal != 0>
				<#assign formatedInvoiceTaxTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceTaxTotal, locale)/>
			</#if>
			<#if invoiceTotal != 0>
				<#assign formatedInvoiceTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceTotal, locale)/>
			</#if>			
			
			<fo:block-container height="112mm">
			<fo:table font-size="${fontsize}" margin-top="65px" margin-left="-8px">
				<fo:table-column column-width="2%"/>
				<fo:table-column column-width="12%"/>
				<fo:table-column column-width="24%"/>
				<fo:table-column column-width="6%"/>
				<fo:table-column column-width="8%"/>
				<fo:table-column column-width="10%"/>
				<fo:table-column column-width="12%"/>
				<fo:table-column column-width="7%"/>
				<fo:table-column column-width="16%"/>
				<fo:table-body>
					<fo:table-row>				 
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="center"></fo:block>
						</fo:table-cell>
		
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="center"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell width="140px" > 																				
							<#assign day = " " + '${invoice.invoiceDate?string["dd"]}'/>
							<#assign month = " " + '${StringUtil.wrapString(uiLabelMap.BACCMonth)}' + ' ' + '${invoice.invoiceDate?string["MM"]}'/>
							<#assign year = " " + '${StringUtil.wrapString(uiLabelMap.BACCYear)}' + ' '  + '${invoice.invoiceDate?string["yyyy"]}'/>
							<#assign descriptionInvoice = '${StringUtil.wrapString(uiLabelMap.BACCAddRevenueInvoiceItem)}' + day + month + year />
							
							<fo:block line-height="${row}">						
								${descriptionInvoice?if_exists}
							</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" ></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block text-align="right" line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>							
							<fo:block text-align="right" line-height="${row}"></fo:block>
						</fo:table-cell>
						<fo:table-cell>							
							<fo:block text-align="right" line-height="${row}"></fo:block>						
						</fo:table-cell>
						
						<fo:table-cell> 						
							<fo:block text-align="right" line-height="${row}"> </fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block text-align="right" line-height="${row}">${formatedInvoiceTotal}</fo:block>
						</fo:table-cell>
					</fo:table-row>								
				</fo:table-body>
			</fo:table>
			</fo:block-container>
											
			<fo:block-container height="33mm">
			<fo:table font-size="${fontsize}">
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="23%"/>
				<fo:table-column column-width="31%"/>
				<fo:table-column column-width="23%"/>
				<fo:table-body>
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
	
						<fo:table-cell>
							<#if formatedAmountBeforeVat0??>
								<fo:block line-height="${row}" text-align="right">${formatedAmountBeforeVat0}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountTotalVat0??>
								<fo:block line-height="${row}" text-align="right">${formatedAmountTotalVat0}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>							
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountBeforeVat5??>
								<fo:block line-height="${row}" text-align="right">${formatedAmountBeforeVat5}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>						
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedVat5??>					
								<fo:block line-height="${row}" text-align="right">${formatedVat5}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>								
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountTotalVat5??>	
								<fo:block line-height="${row}" text-align="right">${formatedAmountTotalVat5}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountBeforeVat10??>	
								<fo:block line-height="${row}" text-align="right">${formatedAmountBeforeVat10}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>		
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedVat10??>	
								<fo:block line-height="${row}" text-align="right">${formatedVat10}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>	
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountTotalVat10??>	
								<fo:block line-height="${row}" text-align="right">${formatedAmountTotalVat10}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>	
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row>
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedInvoiceNoTaxTotal??>	
								<fo:block line-height="${row}" text-align="right">${formatedInvoiceNoTaxTotal}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>								
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedInvoiceTaxTotal??>	
								<fo:block line-height="${row}" text-align="right">${formatedInvoiceTaxTotal}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>								
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedInvoiceTotal??>	
								<fo:block line-height="${row}" text-align="right">${formatedInvoiceTotal}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>															
						</fo:table-cell>
					</fo:table-row>
					
				</fo:table-body>
			</fo:table>
			</fo:block-container>
			
			<fo:block margin="-12px 0px 0px 100px" font-size="${fontsize}" line-height="${row}">
		       <#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${invoiceTotal?string?if_exists}")/>
		       ${amountInWords?if_exists}
			</fo:block>
			
			</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
	</#escape>
<#elseif "SALES_INVOICE" = invoice.invoiceTypeId>
	<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main-page" page-width="8.5in" page-height="11in" margin-top="0in" margin-bottom="0in" margin-left="0.3in" margin-right="0.3in">
				<fo:region-body margin-top="1.71in" margin-bottom="0in"/>
				<fo:region-before extent="0in"/>
				<fo:region-after extent="0in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
	
		<#assign fontsize="9.5px"/>
		<#assign right="100px"/>
		<#assign line="4.5px"/>
		<#assign row="16px"/>
	
		<fo:page-sequence master-reference="main-page">
			<fo:flow flow-name="xsl-region-body">
			<fo:block>
		
			<fo:table width="60px" font-size="${fontsize}" margin-left="125px" margin-top="6px">
				<fo:table-column column-width="20px"/>
				<fo:table-column column-width="20px"/>
				<fo:table-column column-width="20px"/>
				<#if invoice??>
					${invoice}
					${invoice.invoiceDate?string["dd/MM/yyyy"]}
				</#if>		
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>${invoice.invoiceDate?string["dd"]}</fo:block>
						</fo:table-cell>
	
						<fo:table-cell>
							<fo:block margin-left="30px">${invoice.invoiceDate?string["MM"]}</fo:block>
						</fo:table-cell>
	
						<fo:table-cell>
							<fo:block margin-left="60px">${invoice.invoiceDate?string["yyyy"]}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
					
			<fo:table font-size="${fontsize}">
				<fo:table-column column-width="65%"/>
				<fo:table-column/>
				<fo:table-body>
					<fo:table-row height="5.65mm">
						<fo:table-cell number-columns-spanned="2">
							<#if orderInvoiceNote??>
								<fo:block margin="1px 0px 0px 85px">${orderInvoiceNote.customerName?if_exists}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>
	
					<fo:table-row height="5.65mm">										
						<fo:table-cell number-columns-spanned="2">		
							<#if orderInvoiceNote??>
								<fo:block margin="2px 0px 0px 40px" line-height="${row}">${orderInvoiceNote.companyName?if_exists}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell number-columns-spanned="2">
							<#if orderInvoiceNote??>
								<fo:block margin="2px 0px 0px 40px" line-height="${row}">${orderInvoiceNote.address?if_exists}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>				
	
					<fo:table-row>
						<fo:table-cell>
							<#if orderInvoiceNote??>
								<fo:block margin="2px 0px 0px 50px">${orderInvoiceNote.taxInfoId?if_exists}</fo:block>
							</#if>	
						</fo:table-cell>
						<fo:table-cell>
								<fo:block margin="2px 0px 0px 10px">${invoice.invoiceId}</fo:block>	
						</fo:table-cell>
					</fo:table-row>
	
					<fo:table-row>
						<fo:table-cell>
							<fo:block margin="4px 0px 0px 70px">${StringUtil.wrapString(uiLabelMap.BACCPaymentMethodInvoiceId)}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block margin="4px 0px 0px 00px">${productStoreName?if_exists}</fo:block>
						</fo:table-cell>
					</fo:table-row>
	
				</fo:table-body>
			</fo:table>
			
			<#assign totalVat0 = 0 />
			<#assign totalVat5 = 0 />
			<#assign totalVat10 = 0 />
			<#if invoice??>
				<#if vatTaxesByType.get("VAT10")??>
					<#assign formatedVat10 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatTaxesByType.get("VAT10"), locale)/>
					<#assign totalVat10 = vatTaxesByType.get("VAT10") />
				</#if>
				<#if vatTaxesByType.get("VAT5")??>
					<#assign formatedVat5 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatTaxesByType.get("VAT5"), locale)/>
					<#assign totalVat5 = vatTaxesByType.get("VAT5") />
				</#if>
			</#if>
	
			<#if invoice??>
				<#if vatAmountByType??>
					<#if vatAmountByType.get("VAT10")??>
						<#assign formatedAmountBeforeVat10 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatAmountByType.get("VAT10"), locale)/>
						<#assign totalVat10 = totalVat10 + vatAmountByType.get("VAT10") />
					</#if>
					<#if vatAmountByType.get("VAT5")??>
						<#assign formatedAmountBeforeVat5 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatAmountByType.get("VAT5"), locale)/>
						<#assign totalVat5 = totalVat5 + vatAmountByType.get("VAT5") />
					</#if>
					<#if vatAmountByType.get("VAT0")??>
						<#assign formatedAmountBeforeVat0 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(vatAmountByType.get("VAT0"), locale)/>
						<#assign totalVat0 = vatAmountByType.get("VAT0") />
					</#if>
				</#if>
			</#if>
			
			<#if totalVat10 != 0>
				<#assign formatedAmountTotalVat10 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(totalVat10, locale)/>
			</#if>
			<#if totalVat5 != 0>
				<#assign formatedAmountTotalVat5 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(totalVat5, locale)/>
			</#if>
			<#if totalVat0 != 0>
				<#assign formatedAmountTotalVat0 = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(totalVat0, locale)/>
			</#if>
			
			<#if invoiceNoTaxTotal != 0>
				<#assign formatedInvoiceNoTaxTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceNoTaxTotal, locale)/>
			</#if>
			<#if invoiceTaxTotal != 0>
				<#assign formatedInvoiceTaxTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceTaxTotal, locale)/>
			</#if>
			<#if invoiceTotal != 0>
				<#assign formatedInvoiceTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceTotal, locale)/>
			</#if>			
			
			<fo:block-container height="112mm">
			<fo:table font-size="${fontsize}" margin-top="65px" margin-left="-6px">
				<fo:table-column column-width="2%"/>
				<fo:table-column column-width="12%"/>
				<fo:table-column column-width="24%"/>
				<fo:table-column column-width="6%"/>
				<fo:table-column column-width="8%"/>
				<fo:table-column column-width="10%"/>
				<fo:table-column column-width="12%"/>
				<fo:table-column column-width="7%"/>
				<fo:table-column column-width="16%"/>
				<fo:table-body>
				 <#list invoiceItemPdfs as invoiceItem>
					<fo:table-row>				 
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="center">${invoiceItem_index + 1}</fo:block>
						</fo:table-cell>
		
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="center">${invoiceItem.productCode?if_exists}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell width="145px" > 							
							<fo:block line-height="${row}" white-space="nowrap" overflow="hidden">						
								${Static["com.olbius.acc.utils.UtilServices"].truncateStringCase("${invoiceItem.productName?if_exists}", 25)}
							</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" >${StringUtil.wrapString(uiLabelMap.BACCOneEA)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block text-align="right" line-height="${row}">${invoiceItem.quantity?if_exists}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceItem.amount?if_exists, locale)/>
							<fo:block text-align="right" line-height="${row}">${amount}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<#assign amountPre = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(invoiceItem.totalAmount?if_exists, locale)/>
							<fo:block text-align="right" line-height="${row}">${amountPre}</fo:block>						
						</fo:table-cell>
						
						<fo:table-cell>
							<#if invoiceItem.taxPercentage?has_content && invoiceItem.taxPercentage == 0> 							
								<fo:block text-align="right" line-height="${row}"> </fo:block>
							<#else>
								<fo:block text-align="right" line-height="${row}">${invoiceItem.taxPercentage?if_exists}</fo:block>
							</#if>
						</fo:table-cell>
						
						<fo:table-cell>
							<#assign amountTotalAll = invoiceItem.totalAmount?if_exists + invoiceItem.totalAmountTax?if_exists />
							<#assign amountTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatAmount(amountTotalAll, locale)/>
							<fo:block text-align="right" line-height="${row}">${amountTotal}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</#list>						
				</fo:table-body>
			</fo:table>
			</fo:block-container>
											
			<fo:block-container height="33mm">
			<fo:table font-size="${fontsize}">
				<fo:table-column column-width="21%"/>
				<fo:table-column column-width="23%"/>
				<fo:table-column column-width="31%"/>
				<fo:table-column column-width="23%"/>
				<fo:table-body>
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
	
						<fo:table-cell>
							<#if formatedAmountBeforeVat0??>
								<fo:block line-height="${row}" text-align="right">${formatedAmountBeforeVat0}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountTotalVat0??>
								<fo:block line-height="${row}" text-align="right">${formatedAmountTotalVat0}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>							
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountBeforeVat5??>
								<fo:block line-height="${row}" text-align="right">${formatedAmountBeforeVat5}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>						
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedVat5??>					
								<fo:block line-height="${row}" text-align="right">${formatedVat5}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>								
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountTotalVat5??>	
								<fo:block line-height="${row}" text-align="right">${formatedAmountTotalVat5}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountBeforeVat10??>	
								<fo:block line-height="${row}" text-align="right">${formatedAmountBeforeVat10}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>		
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedVat10??>	
								<fo:block line-height="${row}" text-align="right">${formatedVat10}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>	
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedAmountTotalVat10??>	
								<fo:block line-height="${row}" text-align="right">${formatedAmountTotalVat10}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>	
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row height="5.65mm">
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					
					<fo:table-row>
						<fo:table-cell>
							<fo:block line-height="${row}"></fo:block>
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedInvoiceNoTaxTotal??>	
								<fo:block line-height="${row}" text-align="right">${formatedInvoiceNoTaxTotal}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>								
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedInvoiceTaxTotal??>	
								<fo:block line-height="${row}" text-align="right">${formatedInvoiceTaxTotal}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>								
						</fo:table-cell>
						
						<fo:table-cell>
							<#if formatedInvoiceTotal??>	
								<fo:block line-height="${row}" text-align="right">${formatedInvoiceTotal}</fo:block>
							<#else>
								<fo:block line-height="${row}" text-align="right">${StringUtil.wrapString(uiLabelMap.BACCBlank)}</fo:block>
							</#if>															
						</fo:table-cell>
					</fo:table-row>
					
				</fo:table-body>
			</fo:table>
			</fo:block-container>
			
			<fo:block margin="-12px 0px 0px 100px" font-size="${fontsize}" line-height="${row}">
		       <#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${invoiceTotal?string?if_exists}")/>
		       ${amountInWords?if_exists}
			</fo:block>
			
			</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
	</#escape>							
</#if>