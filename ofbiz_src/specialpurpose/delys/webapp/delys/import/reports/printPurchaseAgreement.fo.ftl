<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <fo:page-sequence master-reference="main">
    <fo:flow flow-name="xsl-region-body">
    	<fo:block text-align="center" margin-top="-50px" font-size="14pt" font-family="sans-serif" font-weight="bold">
	        ${agreementName.attrValue}
		</fo:block>
		<fo:block text-align="center" font-size="12pt" font-family="sans-serif">
			${uiLabelMap.No}. ${agreementId}
		</fo:block>
		<fo:block text-align="center" font-size="12pt" font-family="sans-serif">
			${uiLabelMap.ThisPurchaseOrderIsMadeOn} ${agreement.agreementDate}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			${uiLabelMap.Between}
		</fo:block>
			
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold" margin-left="5%">
			${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold" margin-left="5%">
			${uiLabelMap.Address}: ${addressFrom.address1}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold" margin-left="5%">
			<fo:table font-family="sans-serif" font-size="12pt">
	        	<fo:table-column column-width="200pt"/>
	        	<fo:table-column column-width="200pt"/>
	        	<fo:table-body>	
	        		<fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.fromContactMechPhone}: ${phoneFrom.contactNumber}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.fromContactMechFax}: ${faxFrom.contactNumber}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.Bank}:</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.Account}: ${listFinAccountFroms.get(0).finAccountCode}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
	        	</fo:table-body>
	        </fo:table>	
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold" margin-left="5%">
			${uiLabelMap.Represented}: ${representParty.firstName!} ${representParty.middleName!} ${representParty.lastName!} ${representParty.groupName!} - ${representParty.partyId}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			${uiLabelMap.And}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold" margin-left="5%">
			${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold" margin-left="5%">
			${uiLabelMap.Address}: ${addressTo.address1}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			1. ${uiLabelMap.ContractValue}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			<#assign totalAmount = 0>
			<#list listProducts as item>
				<#assign itemValue = item.quantity * item.unitPrice>
				<#assign totalAmount = totalAmount + itemValue>
			</#list>
			1.1 ${uiLabelMap.TheTotalPurchaseOrderPriceIs} ${currencyUomId} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalAmount, currencyUomId, locale)}
		</fo:block>
    	<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
    		1.2 ${uiLabelMap.TheBreakdownOfThisAmountIdAsFollows}
    	</fo:block>
	    <fo:table border="1px solid #D4D0C8" font-family="sans-serif" font-size="12pt">
	        <fo:table-column column-width="100pt"/>
	        <fo:table-column column-width="100pt"/>
	        <fo:table-column column-width="50pt"/>
	        <fo:table-column column-width="50pt"/>
	        <fo:table-column column-width="100pt"/>
	        <fo:table-column column-width="110pt"/>
	        <fo:table-column column-width="60pt"/>
	        <fo:table-header>
	            <fo:table-row font-weight="bold">
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductId}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.description}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.Unit}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.quantity}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.unitPrice} (${currencyUomId})</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.GoodValue} (${currencyUomId})</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.Remark}</fo:block></fo:table-cell>
	            </fo:table-row>
	        </fo:table-header>
	        <fo:table-body>
	            <#assign totalValue = 0>
            	<#assign totalRemark = 0>
            	<#list listProducts as item>
            		<#assign convertNumber = 1>
            		<#assign productId = item.productId>
            		<#assign product = delegator.findOne("Product", {"productId" : productId}, true)!>
            		<#assign baseUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, true)!>
            		<#assign goodValue = item.quantity * item.unitPrice>
            		<#assign totalValue = totalValue + goodValue>
            		<#assign quantity = item.quantity>
            		<#assign listUomToConvert = Static["com.olbius.services.DelysServices"].getListUomToConvert(delegator, productId, productPackingUomId, product.quantityUomId)!>
            		<#assign convertNumber = Static["com.olbius.services.DelysServices"].getProductConvertNumber(delegator, convertNumber, productId, productPackingUomId, product.quantityUomId, listUomToConvert)!>
	            	<#assign remark = quantity/convertNumber>
	            	<#assign totalRemark = totalRemark + remark>
	            	<#assign uomProductPacking = delegator.findOne("Uom", {"uomId" : productPackingUomId}, true)!>
	                <fo:table-row border="1px solid #D4D0C8">
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                    	<fo:block>${productId?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${product.internalName?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${baseUom.description?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${quantity?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.unitPrice, currencyUomId, locale)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(goodValue, currencyUomId, locale)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${remark?if_exists} ${uomProductPacking.description?if_exists}</fo:block>
	                    </fo:table-cell>
	                </fo:table-row>
	            </#list>
	            <fo:table-row border="1px #D4D0C8">
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.Total}</fo:block></fo:table-cell>
	            	<fo:table-cell padding="2pt"></fo:table-cell>
	            	<fo:table-cell padding="2pt"></fo:table-cell>
	            	<fo:table-cell padding="2pt"></fo:table-cell>
	            	<fo:table-cell padding="2pt"></fo:table-cell>
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalValue, currencyUomId, locale)}</fo:block></fo:table-cell>
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${totalRemark} ${uomProductPacking.description}</fo:block></fo:table-cell>
	            </fo:table-row>
	        </fo:table-body>
	    </fo:table>
	    <#assign port = delegator.findOne("Facility", {"facilityId" : currentPortTerm}, true)!>
	    <fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
    		1.3 ${uiLabelMap.AllThePaymentCouldBeCombineRatioBetween} ${currencyUomId} ${uiLabelMap.CommonAnd} 
    		<#list listOtherCurrencyTerms as otherUom>
    			${otherUom.textValue},
    		</#list>
    		. ${uiLabelMap.CFR} ${port.facilityName}
    	</fo:block>
	    <fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			2. ${uiLabelMap.TermOfDelivery}
		</fo:block>
		<#assign etd = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETDTerm}, true)!>
		<#assign eta = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETATerm}, true)!>
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
				2.1 ${uiLabelMap.TheDateOfShipment}:
			</fo:block>
				<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="10%">
					• ${uiLabelMap.ETD}: ${etd.fromDate}
				</fo:block>
				<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="10%">
					• ${uiLabelMap.ETA}: ${port.facilityName}: ${eta.fromDate}				
				</fo:block>
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
				2.2 ${uiLabelMap.PortOfDischarge}: ${port.facilityName}
			</fo:block>
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
				2.3 ${uiLabelMap.Transshipment} <#if transshipment == "Y"> ${uiLabelMap.IsAllowed}<#else> ${uiLabelMap.IsNotAllowed}</#if>. ${uiLabelMap.PartialShipment} <#if partialShipment == "Y"> ${uiLabelMap.IsAllowed}<#else> ${uiLabelMap.IsNotAllowed}</#if>
			</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			3. ${uiLabelMap.TermOfPayment}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			${uiLabelMap.AgreementPaymentTermDetail}
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
				<#list listFinAccountTos as finAcc>
					<#assign bic = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "BIC"}, true)!>
					<#assign iban = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "IBAN"}, true)!>
					<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
						${uiLabelMap.SupplierBank}: ${finAcc.finAccountName}
					</fo:block>
					<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
						${uiLabelMap.Account} (${finAcc.currencyUomId}): ${finAcc.finAccountCode}
						<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%"> 
					 	<#if bic?has_content>
							${uiLabelMap.FinAccBIC}: ${bic.attrValue} 
						</#if>
						</fo:block>
						<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
						<#if iban?has_content>
							${uiLabelMap.FinAccIBAN}: ${iban.attrValue} 
						</#if>
						</fo:block>
					</fo:block>
				</#list>
				${uiLabelMap.Beneficiary}: ${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}
			</fo:block>
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			4. ${uiLabelMap.Packing}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			${uiLabelMap.AgreementPackingTermDetail}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			5. ${uiLabelMap.Documentation}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
		${uiLabelMap.AgreementDocumentTermDetail}
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%"> 
			- ${uiLabelMap.Invoice}
			</fo:block> 
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			- ${uiLabelMap.PackingList} (${uiLabelMap.DeliveryNote})
			</fo:block> 
			<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			- ${uiLabelMap.BillOfLading}
			</fo:block> 
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" font-weight="bold">
			6. ${uiLabelMap.Transportation}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}
		</fo:block> 
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			${uiLabelMap.Receiver}: ${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			${uiLabelMap.Address}: ${addressFrom.address1}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="sans-serif" margin-left="5%">
			${uiLabelMap.AmendmentSupplementary}
		</fo:block>
		
		<fo:block text-align="center" font-size="12pt" font-family="sans-serif" font-weight="bold">
			<fo:table font-family="sans-serif" font-size="12pt">
	        	<fo:table-column/>
	        	<fo:table-column/>
	        	<fo:table-body>	
	        		<fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.ForTheSupplier}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.ForThePurchaser}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
	        	</fo:table-body>
	        </fo:table>	
		</fo:block>
	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>