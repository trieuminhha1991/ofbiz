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
		        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.BACCInvoice)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="100px">
		        	<fo:block font-weight="bold">${uiLabelMap.BACCInvForm} ${voucher?if_exists.form?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell width="70px">
		        	<fo:block></fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block ></fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="100px">
		        	<fo:block font-weight="bold">${uiLabelMap.BACCInvSerialNumber} ${voucher?if_exists.series?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell width="70px">
		        	<fo:block></fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<#assign nowStamp=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
		        	<#assign day=Static["org.ofbiz.base.util.UtilDateTime"].getDayOfMonth(nowStamp, timeZone, locale) />
		        	<#assign month=Static["org.ofbiz.base.util.UtilDateTime"].getMonth(nowStamp, timeZone, locale) />
		        	<#assign year=Static["org.ofbiz.base.util.UtilDateTime"].getYear(nowStamp, timeZone, locale) />
		        	<fo:block font-style="italic">${StringUtil.wrapString(uiLabelMap.BACCDay)} ${day} ${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${month + 1} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${year} </fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="100px">
		        	<fo:block font-weight="bold">${uiLabelMap.BACCInvCode}: ${voucher?if_exists.voucherCode?if_exists}</fo:block>
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
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCSalesman)}</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.PartyTelecom)}</fo:block>
			   </fo:table-cell>
			   <fo:table-cell >
			   		<#if invoice?exists>
			   			<#assign partyNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, invoice.partyIdFrom?if_exists, true, true)?if_exists>
			   			<#assign partyAddressFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyPostalAddress(invoice.partyIdFrom?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
			   			<#assign taxList = delegator.findList("PartyTaxAuthInfo",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId","${invoice.partyIdFrom?if_exists}"),null,Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate DESC"),null,false) />
			   			<#assign taxCode = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(taxList) !>
			   			<#assign partyTelePhoneFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(invoice.partyIdFrom?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
			   				<fo:block>${partyNameFrom?if_exists}<fo:leader /></fo:block>
		   				   <fo:block><#if taxCode?exists>${taxCode.partyTaxId?if_exists}</#if><fo:leader /></fo:block>
					       <fo:block>${partyAddressFrom?if_exists}<fo:leader /></fo:block>
					       <fo:block>${partyTelePhoneFrom?if_exists}<fo:leader /></fo:block>
		   			</#if>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" margin-left="10px" margin-top="10px">
	 	<fo:table-column/>
		<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell width="120px" text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCBuyer)}</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}</fo:block>
			   </fo:table-cell>
			   <fo:table-cell >
				   <#if invoice?exists>
					   <#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, invoice.partyId?if_exists, true, true)?if_exists>
					   <#assign partyAddressTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyPostalAddress(invoice.partyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
					   <#assign partyTelePhoneTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(invoice.partyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
					   <#assign taxList = delegator.findList("PartyTaxAuthInfo",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId","${invoice.partyId?if_exists}"),null,Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate DESC"),null,false) />
			   			<#assign taxCode = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(taxList) !>
					   <fo:block>${partyNameTo?if_exists}<fo:leader /></fo:block>
       			       <fo:block>${taxCode.partyTaxId?if_exists}<fo:leader /></fo:block>
				       <fo:block>${partyAddressTo?if_exists}<fo:leader /></fo:block>
				   </#if>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" margin-left="10px" margin-top="10px">
	 	<fo:table-column/>
		<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell  width="130px" text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCPaymentMethod)}</fo:block>
			   </fo:table-cell>
			   <#if invoice?exists>
			   		<#assign paymentAppl = delegator.findByAnd("InvoiceAndApplAndPayment",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId","${invoice.invoiceId?if_exists}"),null,false)/>
		   		</#if>
			   <fo:table-cell>
			       <fo:block>
			       <#if paymentAppl?exists>
			       		<#assign count = 0/>
			       		<#list paymentAppl as appl>
			       			<#if appl?exists && appl.pmPaymentMethodId?exists>
			       			<#assign pmMethod = delegator.findOne("PaymentMethod",Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId","${appl.pmPaymentMethodId?if_exists}"),false)/>
			       				<#assign count = count + 1/>
			       				<fo:block font-style="italic">${pmMethod.get("description",locale)}<#if paymentAppl?size gt 1> , </#if><#if count gt 2><#break></#if><fo:leader /></fo:block>
			       			</#if>
			       		</#list>
			       	<#else>
		       			<fo:block font-style="italic"><fo:leader /></fo:block>
			       	</#if>	
			       </fo:block>
			   </fo:table-cell>
			</fo:table-row>
		    <fo:table-row>
		    	<fo:table-cell text-align="left">
		    		<fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}</fo:block>
			   </fo:table-cell>
			   <fo:table-cell>
			       <fo:block font-style="italic" font-weight="bold"></fo:block>
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
              	<fo:block font-weight="bold" margin-bottom="10px" >${StringUtil.wrapString(uiLabelMap.BACCSeqId)}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCGoodName)}</fo:block>
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
	   		<#assign invoiceTotal = 0 />
	   		<#assign invoiceItemTaxTotal = 0 />
	   		<#assign index = 0 />
	   		<#list listInvoiceItems as item>
		   		<#if item.invoiceItemTypeId == "ITM_SALES_TAX" || item.invoiceItemTypeId =="SRT_SALES_TAX_ADJ">
		   			<#assign invoiceItemTaxTotal = invoiceItemTaxTotal + item.quantity?if_exists * item.amount?if_exists />
		   		<#else>
		   			<#assign invoiceItemTotal = invoiceItemTotal + item.quantity?if_exists * item.amount?if_exists />
		   			<#assign index = index + 1 />	
		   			<fo:table-row border="solid 0.5mm black">
					    <fo:table-cell >
					    	<fo:block text-align="center">${index}</fo:block>
					    </fo:table-cell>
					  	<fo:table-cell  text-align="center">
					  		<#if item.invoiceItemTypeId == "INV_FPRODEXT_ITEM">
						  		<#assign product = delegator.findOne("Product",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId","${item.productId?if_exists}"),false) />
						  		<fo:block>${product.productName?if_exists}</fo:block>
					  		<#else>
					  			<#assign itemType = delegator.findOne("InvoiceItemType",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId","${item.invoiceItemTypeId?if_exists}"),false) />
					  			<fo:block>${itemType.description}</fo:block>
					  		</#if>
					   	</fo:table-cell>
						<fo:table-cell text-align="center">
							<#if item.invoiceItemTypeId !="SRT_FPROD_ITEM">
							<#assign orderItemsBill = delegator.findByAnd("OrderItemBilling",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId","${item.invoiceId?if_exists}","invoiceItemSeqId","${item.invoiceItemSeqId?if_exists}"),null,false)/>
							<#assign orderItems = delegator.findOne("OrderItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId","${orderItemsBill.get(0).orderId?if_exists}","orderItemSeqId","${orderItemsBill.get(0).orderItemSeqId?if_exists}"),false) />
							<#assign uomName = delegator.findOne("Uom",Static["org.ofbiz.base.util.UtilMisc"].toMap("uomId","${orderItems.quantityUomId?if_exists}"),false) !>
							<#else>
							<#assign returnItemBill = delegator.findByAnd("ReturnItemBilling",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId","${item.invoiceId?if_exists}","invoiceItemSeqId","${item.invoiceItemSeqId?if_exists}"),null,false)/>
							<#assign returnItems = delegator.findOne("ReturnItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("returnId","${returnItemBill.get(0).returnId?if_exists}","returnItemSeqId","${returnItemBill.get(0).returnItemSeqId?if_exists}"),false) />
							<#assign uomName = delegator.findOne("Uom",Static["org.ofbiz.base.util.UtilMisc"].toMap("uomId","${returnItems.quantityUomId?if_exists}"),false) !>
							</#if>
					       <fo:block><#if uomName?exists && uomName?has_content> ${uomName.get("description",locale)}<#else>(Chưa có)</#if></fo:block>
					   </fo:table-cell>
					   <fo:table-cell  text-align="center">
					       <fo:block>${item.quantity?if_exists}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell  text-align="center">
				   			<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.amount.abs()?if_exists, invoice.currencyUomId?if_exists?default('VND'), locale)>
					   		<fo:block>${amount?if_exists}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell  text-align="center">
					   		<#assign total = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.quantity.abs()?if_exists * item.amount.abs()?if_exists, invoice.currencyUomId?if_exists, locale)>
					   		<fo:block>${total?if_exists}</fo:block>
					   </fo:table-cell>
				    </fo:table-row>
		   		</#if>
	   		</#list>
		    <fo:table-row border="solid 0.5mm black">
			    <fo:table-cell number-columns-spanned="5">
			       <fo:block text-align="right" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCGoodTotal)}</fo:block>
			    </fo:table-cell>
			   <fo:table-cell  text-align="center">
				   <#if invoice?exists>
			   			<#assign formatedInvoiceItemTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceItemTotal, invoice.currencyUomId?if_exists, locale)>
				   </#if>
			       <fo:block>${formatedInvoiceItemTotal?if_exists}</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		    <fo:table-row border="solid 0.5mm black">
			    <fo:table-cell number-columns-spanned="5">
			       <fo:block text-align="right" font-weight="bold">
			       		<fo:inline>${StringUtil.wrapString(uiLabelMap.BACCVATTaxes)} 10%</fo:inline>
			       		<fo:inline>${StringUtil.wrapString(uiLabelMap.BACCVAT)}</fo:inline>
			       </fo:block>
			    </fo:table-cell>
			   <fo:table-cell  text-align="center">
				   <#if invoice?exists>
				   		<#assign formatedTaxAmount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceItemTaxTotal, invoice.currencyUomId?if_exists, locale)>
			   		</#if>
			       <fo:block>${formatedTaxAmount?if_exists}</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		    <fo:table-row border="solid 0.5mm black">
			    <fo:table-cell number-columns-spanned="5">
			       <fo:block text-align="right" font-weight="bold">
			       		${StringUtil.wrapString(uiLabelMap.BACCPaymentTotal)}
			       </fo:block>
			    </fo:table-cell>
			   <fo:table-cell  text-align="center">
			   		<#if invoice?exists>
			   			<#assign invoiceTotal = invoiceItemTotal + invoiceItemTaxTotal />
			   			<#assign formatedInvoiceTotal = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(invoiceTotal, invoice.currencyUomId?if_exists, locale)>
			   		</#if>
			   		<fo:block>${formatedInvoiceTotal?if_exists?default("")}</fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		    <fo:table-row border="solid 0.5mm black">
		    <fo:table-cell number-columns-spanned="6">
		       <fo:block text-align="left" font-weight="bold">
		       <#assign _total = invoiceItemTotal * 0.1 + invoiceItemTotal/>
		       <#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${invoiceTotal?string?if_exists}")/>
		       ${StringUtil.wrapString(uiLabelMap.BACCAmountInWords)} : ${amountInWords?if_exists}
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
				    <fo:block font-style="italic" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCBuyer)}</fo:block>
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
				    <fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
</fo:block>
</#escape>
