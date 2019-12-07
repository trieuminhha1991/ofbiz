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
    	<fo:block text-align="center" margin-top="-50px" font-size="14pt" font-family="Arial" font-weight="bold">
	        ${agreementName.attrValue}
		</fo:block>
		<fo:block text-align="center" font-size="12pt" font-family="Arial">
			${uiLabelMap.No2}. ${agreementId}
		</fo:block>
		<fo:block text-align="center" font-size="12pt" font-family="Arial">
			${uiLabelMap.ThisPurchaseOrderIsMadeOn2} ${agreement.agreementDate}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			${uiLabelMap.Between2}
		</fo:block>
			
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold" margin-left="5%">
			${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold" margin-left="5%">
			${uiLabelMap.Address2}: ${StringUtil.wrapString(addressFrom.address1)}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold" margin-left="5%">
			<fo:table font-family="Arial" font-size="12pt">
	        	<fo:table-column column-width="200pt"/>
	        	<fo:table-column column-width="200pt"/>
	        	<fo:table-body>	
	        		<fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.fromContactMechPhone2}: ${phoneFrom.contactNumber}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.fromContactMechFax2}: ${faxFrom.contactNumber}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.Bank2}:</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.Account2}: ${listFinAccountFroms.get(0).finAccountCode}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
	        	</fo:table-body>
	        </fo:table>	
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold" margin-left="5%">
			${uiLabelMap.Represented2}: ${representParty.firstName!} ${representParty.middleName!} ${representParty.lastName!} ${representParty.groupName!} - ${representParty.partyId}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			${uiLabelMap.And2}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold" margin-left="5%">
			${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold" margin-left="5%">
			${uiLabelMap.Address2}: ${addressTo.address1}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			1. ${uiLabelMap.ContractValue2}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			<#assign totalAmount = 0>
			<#list listProducts as item>
				<#assign itemValue = item.quantity * item.unitPrice>
				<#assign totalAmount = totalAmount + itemValue>
			</#list>
			1.1 ${uiLabelMap.TheTotalPurchaseOrderPriceIs2} ${currencyUomId} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalAmount, currencyUomId, locale)}
		</fo:block>
    	<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
    		1.2 ${uiLabelMap.TheBreakdownOfThisAmountIdAsFollows2}
    	</fo:block>
	    <fo:table border="1px solid #D4D0C8" table-layout="fixed" font-family="Arial" font-size="12pt" width="100%">
	        <fo:table-column column-width="25%"/>
	        <fo:table-column column-width="15%"/>
	        <fo:table-column column-width="10%"/>
	        <fo:table-column column-width="10%"/>
	        <fo:table-column column-width="10%"/>
	        <fo:table-column column-width="15%"/>
	        <fo:table-column column-width="15%"/>
	        <fo:table-header>
	            <fo:table-row font-weight="bold">
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductId2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.description2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.Unit2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.quantity2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.unitPrice2} (${currencyUomId})</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.GoodValue2} (${currencyUomId})</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.Remark2}</fo:block></fo:table-cell>
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
	                        <fo:block>${baseUom.get("description", locale)?if_exists}</fo:block>
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
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.Total2}</fo:block></fo:table-cell>
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
	    <fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
    		1.3 ${uiLabelMap.AllThePaymentCouldBeCombineRatioBetween2} ${currencyUomId} ${uiLabelMap.CommonAnd2} 
    		<#list listOtherCurrencyTerms as otherUom>
    			${otherUom.textValue},
    		</#list>
    		. ${uiLabelMap.CFR} ${port.facilityName}
    	</fo:block>
	    <fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			2. ${uiLabelMap.TermOfDelivery2}
		</fo:block>
		<#assign etd = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETDTerm}, true)!>
		<#assign eta = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETATerm}, true)!>
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
				2.1 ${uiLabelMap.TheDateOfShipment2}:
			</fo:block>
				<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="10%">
					${uiLabelMap.ETD2}: ${etd.fromDate}
				</fo:block>
				<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="10%">
					${uiLabelMap.ETA2}: ${port.facilityName}: ${eta.fromDate}				
				</fo:block>
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
				2.2 ${uiLabelMap.PortOfDischarge2}: ${port.facilityName}
			</fo:block>
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
				2.3 ${uiLabelMap.Transshipment2} <#if transshipment == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if>. ${uiLabelMap.PartialShipment2} <#if partialShipment == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if>
			</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			3. ${uiLabelMap.TermOfPayment2}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			${uiLabelMap.AgreementPaymentTermDetail2}
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
				<#list listFinAccountTos as finAcc>
					<#assign bic = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "BIC"}, true)!>
					<#assign iban = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "IBAN"}, true)!>
					<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
						${uiLabelMap.SupplierBank2}: ${finAcc.finAccountName}
					</fo:block>
					<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
						${uiLabelMap.Account2} (${finAcc.currencyUomId}): ${finAcc.finAccountCode}
						<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%"> 
					 	<#if bic?has_content>
							${uiLabelMap.FinAccBIC}: ${bic.attrValue} 
						</#if>
						</fo:block>
						<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
						<#if iban?has_content>
							${uiLabelMap.FinAccIBAN}: ${iban.attrValue} 
						</#if>
						</fo:block>
					</fo:block>
				</#list>
				${uiLabelMap.Beneficiary2}: ${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}
			</fo:block>
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			4. ${uiLabelMap.Packing2}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			${uiLabelMap.AgreementPackingTermDetail2}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			5. ${uiLabelMap.Documentation2}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
		${uiLabelMap.AgreementDocumentTermDetail2}
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%"> 
			- ${uiLabelMap.Invoice2}
			</fo:block> 
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			- ${uiLabelMap.PackingList2} (${uiLabelMap.DeliveryNote2})
			</fo:block> 
			<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			- ${uiLabelMap.BillOfLading2}
			</fo:block> 
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" font-weight="bold">
			6. ${uiLabelMap.Transportation2}
		</fo:block>
		
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			${supplier.firstName!} ${supplier.middleName!} ${supplier.lastName!} ${supplier.groupName!}
		</fo:block> 
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			${uiLabelMap.Receiver2}: ${purchaser.firstName!} ${purchaser.middleName!} ${purchaser.lastName!} ${purchaser.groupName!}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			${uiLabelMap.Address2}: ${addressFrom.address1}
		</fo:block>
		<fo:block text-align="left" font-size="12pt" font-family="Arial" margin-left="5%">
			${uiLabelMap.AmendmentSupplementary2}
		</fo:block>
		
		<fo:block text-align="center" font-size="12pt" font-family="Arial" font-weight="bold">
			<fo:table font-family="Arial" font-size="12pt">
	        	<fo:table-column/>
	        	<fo:table-column/>
	        	<fo:table-body>	
	        		<fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.ForTheSupplier2}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.ForThePurchaser2}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
	        	</fo:table-body>
	        </fo:table>	
		</fo:block>
	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>