<#escape x as x?xml>
<fo:block font-size="11" font-family="Times" border="solid 0.5mm black">
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	    <fo:table-column />
	    <fo:table-column />
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell width="70px">
		        	<fo:block></fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.BACCVATINVOICE)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="100px">
		        	<fo:block font-weight="bold">${uiLabelMap.BACCFormInv}: ${voucher?if_exists.form?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell width="70px">
		        	<fo:block></fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="italic">${StringUtil.wrapString(uiLabelMap.BACCCustomerDelivery)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="100px">
		        	<fo:block font-weight="bold">${uiLabelMap.BACCSeriesInv}: ${voucher?if_exists.series?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell width="70px">
		        	<fo:block></fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        <fo:block font-style="italic">${StringUtil.wrapString(uiLabelMap.BACCDay)}.....${StringUtil.wrapString(uiLabelMap.BACCMonth)}.....${StringUtil.wrapString(uiLabelMap.BACCYear)}.....</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="100px">
		        	<fo:block font-weight="bold">${uiLabelMap.BACCCodeInv}: ${voucher?if_exists.voucherCode?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-left="10px">
	 	<fo:table-column/>
		<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell width="120px" text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCUnitSales)}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.PartyTelecom)}:</fo:block>
			   </fo:table-cell>
			   <fo:table-cell >
			   		<#assign partyNameFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom?if_exists, true, true)?if_exists>
		   			<#assign partyAddressFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyPostalAddress(invoice.partyIdFrom?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
		   			<#assign partyTelePhoneFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyTelephone(invoice.partyIdFrom?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
   				<fo:block>${partyNameFrom?if_exists}<fo:leader /></fo:block>
			       <fo:block><fo:leader /></fo:block>
			       <fo:block>${partyAddressFrom?if_exists}<fo:leader /></fo:block>
			       <fo:block>${partyTelePhoneFrom?if_exists}<fo:leader /></fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt" margin-left="10px" margin-top="10px">
	 	<fo:table-column/>
		<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell width="150px" text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCBuyers)}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCGroupName)}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}:</fo:block>
			   </fo:table-cell>
			   <fo:table-cell >
				   <#assign partyNameTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.partyIdTo?if_exists, true, true)?if_exists>
				   <#assign partyAddressTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyPostalAddress(invoice.partyIdTo?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
				   <#assign partyTelePhoneTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyTelephone(invoice.partyIdTo?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
			       <fo:block>${partyNameTo?if_exists}<fo:leader /></fo:block>
			       <fo:block>${partyNameTo?if_exists}<fo:leader /></fo:block>
			       <fo:block><fo:leader /></fo:block>
			       <fo:block>${partyAddressTo?if_exists}<fo:leader /></fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="5pt" margin-left="10px">
	 	<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCPayments)}:</fo:block>
			   </fo:table-cell>
			   <fo:table-cell text-align="left">
			       <fo:block>........................<fo:leader /></fo:block>
			   </fo:table-cell>
			   <fo:table-cell text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}:</fo:block>
			   </fo:table-cell>
			   <fo:table-cell text-align="left">
			       <fo:block font-style="italic" font-weight="bold">.........................</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.5mm black"> 
	 	<fo:table-column border="solid 0.5mm black" />
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-header border="solid 0.5mm black">
          <fo:table-row border="solid 0.5mm black">
              <fo:table-cell text-align="center" width="20px">
              	<fo:block font-weight="bold" margin-bottom="10px" >${StringUtil.wrapString(uiLabelMap.BACCOrdinalNumbers)}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCNameGoods)}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block font-weight="bold">${StringUtil.wrapString(uiLabelMap.FormFieldTitle_rateCurrencyUomId)}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block font-weight="bold" vertical-align="middle">${StringUtil.wrapString(uiLabelMap.BACCQuantity)}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCTotal)}</fo:block>
              </fo:table-cell>
          </fo:table-row>
          <fo:table-row border="solid 0.5mm black">
	          <fo:table-cell text-align="center">
	          	<fo:block font-weight="bold">A</fo:block>
	          </fo:table-cell>
	          <fo:table-cell text-align="center">
	          	<fo:block font-weight="bold">B</fo:block>
	          </fo:table-cell>
	          <fo:table-cell text-align="center">
	          	<fo:block font-weight="bold">C</fo:block>
	          </fo:table-cell>
	          <fo:table-cell text-align="center">
	          	<fo:block font-weight="bold" vertical-align="middle">1</fo:block>
	          </fo:table-cell>
	          <fo:table-cell text-align="center">
	          	<fo:block font-weight="bold">2</fo:block>
	          </fo:table-cell>
	          <fo:table-cell text-align="center">
	          	<fo:block font-weight="bold">3=2x1</fo:block>
	          </fo:table-cell>
	      </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
	   		<#assign invoiceItemTotal = 0 />
	   		<#assign invoiceItemTaxTotal = 0 />
	   		<#list listInvoiceItems as item>
	   			<#assign invoiceItemTotal = invoiceItemTotal + item.quantity?if_exists * item.amount?if_exists />
		   		<#if item.invoiceItemTypeId == "ITM_SALES_TAX">
		   			<#assign invoiceItemTaxTotal = invoiceItemTaxTotal + item.quantity?if_exists * item.amount?if_exists />
		   		</#if>
		   		<fo:table-row border="solid 0.5mm black">
				    <fo:table-cell >
				    	<fo:block text-align="center">${item_index}</fo:block>
				    </fo:table-cell>
				  	<fo:table-cell  text-align="center">
				       <fo:block>${item.productId?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="center">
				       <fo:block>${item.uomId?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${item.quantity?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
			   			<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.amount?if_exists, invoice.currencyUomId?if_exists, locale)>
				   		<fo:block>${amount?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				   		<#assign total = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.quantity?if_exists * item.amount?if_exists, invoice.currencyUomId?if_exists, locale)>
				   		<fo:block>${total?if_exists}</fo:block>
				   </fo:table-cell>
			    </fo:table-row>
	   		</#list>
		    <fo:table-row border="solid 0.5mm black">
			    <fo:table-cell number-columns-spanned="5">
			       <fo:block text-align="right">${StringUtil.wrapString(uiLabelMap.BACCPlusMoneyRow)}:</fo:block>
			    </fo:table-cell>
			   <fo:table-cell  text-align="center">
			   		<#assign formatedInvoiceItemTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceItemTotal, invoice.currencyUomId?if_exists, locale)>
			       <fo:block>${formatedInvoiceItemTotal?if_exists}</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		    <fo:table-row border="solid 0.5mm black">
			    <fo:table-cell number-columns-spanned="5">
			       <fo:block text-align="right">
			       		<fo:inline>${StringUtil.wrapString(uiLabelMap.BACCVATTaxes)}: 10%</fo:inline>
			       		<fo:inline>${StringUtil.wrapString(uiLabelMap.BACCVAT)}:</fo:inline>
			       </fo:block>
			    </fo:table-cell>
			   <fo:table-cell  text-align="center">
			   		<#assign formatedTaxAmount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceItemTaxTotal, invoice.currencyUomId?if_exists, locale)>
			   		<fo:block>${formatedTaxAmount}</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		    <fo:table-row border="solid 0.5mm black">
			    <fo:table-cell number-columns-spanned="5">
			       <fo:block text-align="right">
			       		${StringUtil.wrapString(uiLabelMap.BACCTotalPayments)}
			       </fo:block>
			    </fo:table-cell>
			   <fo:table-cell  text-align="center">
			   		<#assign formatedInvoiceTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceItemTotal * 0.1 + invoiceItemTotal, invoice.currencyUomId?if_exists, locale)>
			   		<fo:block>${formatedInvoiceTotal?if_exists}</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		    <fo:table-row border="solid 0.5mm black">
		    <fo:table-cell number-columns-spanned="6">
		       <fo:block text-align="left">
		       ${StringUtil.wrapString(uiLabelMap.BACCAmountInWords)} :...............................
		       </fo:block>
		    </fo:table-cell>
	    </fo:table-row>
	    </fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="20pt" margin-top="30px" margin-bottom="30px">
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCBuyers)}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCSalesman)}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCManager)}</fo:block>
				</fo:table-cell> 
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell >
				    <fo:block >(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignedFullName)})</fo:block>
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
</fo:block>
</#escape>