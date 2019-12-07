<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.0in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <fo:page-sequence master-reference="main">
    <fo:flow flow-name="xsl-region-body">
    	<fo:block text-align="center" margin-top="0px" font-size="14pt" font-weight="bold" font-family="Arial">
	        ${(agreementName.attrValue)?if_exists}
		</fo:block>
		<fo:block text-align="center" font-size="10pt" font-family="Arial">
			${uiLabelMap.No2}. ${agreementId?if_exists}
		</fo:block>
		<fo:block text-align="center" font-size="10pt" font-family="Arial">
			${uiLabelMap.ThisPurchaseOrderIsMadeOn2} ${agreement.agreementDate?if_exists?string('dd-MM-YYYY HH:mm')}
		</fo:block>
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial">
			${uiLabelMap.Between2}
		</fo:block>
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial" margin-left="3%" font-weight="bold">
			${(purchaser.groupNameLocal)?if_exists?upper_case}
		</fo:block>
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.Address2}: ${StringUtil.wrapString((fromAddress.address2)?if_exists)}
		</fo:block>
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			<fo:table font-family="Arial" font-size="10pt">
	        	<fo:table-column column-width="200pt"/>
	        	<fo:table-column column-width="200pt"/>
	        	<fo:table-body>	
	        		<fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.fromContactMechPhone2}: ${(phoneFrom.contactNumber)?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <fo:block>${uiLabelMap.fromContactMechFax2}: ${(faxFrom.contactNumber)?if_exists}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
	                    <fo:table-cell padding="2pt">
	                    	<fo:block>${uiLabelMap.Bank2}:
	                    		<#if listFinAccountFroms?has_content>
		                        	<#assign bank = delegator.findOne("PartyGroup", {"partyId" : listFinAccountFroms.get(0).get("bankId")?if_exists}, false)!>
								  	${bank.get("groupName")?if_exists}
		                        </#if>
	                    	</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt">
	                        <#if listFinAccountFroms?has_content>
	                        	<#assign finAccountCode = listFinAccountFroms.get(0).finAccountCode/>
	                        </#if>
	                    	<fo:block>${uiLabelMap.Account2}: ${finAccountCode?if_exists}</fo:block>
	                    </fo:table-cell>
                    </fo:table-row>
	        	</fo:table-body>
	        </fo:table>	
		</fo:block>
		<#-- <fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.Represented2}: <#if representParty?has_content>${(representParty.firstName)?if_exists} ${(representParty.middleName)?if_exists} ${(representParty.lastName)?if_exists} ${(representParty.groupName)?if_exists} - ${(representParty.partyId)?if_exists}</#if>
		</fo:block> -->
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial">
			${uiLabelMap.And2}
		</fo:block>
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial" margin-left="3%" font-weight="bold">
			${(supplier.groupNameLocal)?if_exists?upper_case}
		</fo:block>
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.Address2}: ${(addressTo.address2)?if_exists}
		</fo:block>
		
		<fo:block text-align="left" margin-top="10px" font-size="10pt" font-family="Arial" font-weight="bold">
			1. ${uiLabelMap.ContractValue2?upper_case}
		</fo:block>
		
		<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial" margin-left="3%">
			<#assign totalAmount = 0>
			<#list listProducts as item>
				<#assign itemValue = item.quantity * item.unitPrice>
				<#assign totalAmount = totalAmount + itemValue>
			</#list>
			1.1 ${uiLabelMap.TheTotalPurchaseOrderPriceIs2} ${(currencyUomId)?if_exists} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(totalAmount, currencyUomId?if_exists, locale)}
		</fo:block>
    	<fo:block text-align="left" margin-top="3px" font-size="10pt" font-family="Arial" margin-left="3%">
    		1.2 ${uiLabelMap.TheBreakdownOfThisAmountIdAsFollows2}
    	</fo:block>
	    <fo:table margin-top="3px" border="1px solid #D4D0C8" table-layout="fixed" font-family="Arial" font-size="10pt" width="100%">
	        <fo:table-column column-width="5%"/>
	        <fo:table-column column-width="13%"/>
	        <fo:table-column column-width="30%"/>
	        <fo:table-column column-width="8%"/>
	        <fo:table-column column-width="12%"/>
	        <fo:table-column column-width="12%"/>
	        <fo:table-column column-width="15%"/>
	        <fo:table-column column-width="10%"/>
	        <fo:table-header>
	            <fo:table-row>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.Num}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.ProductId2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.description2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.Unit2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.quantity2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.unitPrice2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.GoodValue2}</fo:block></fo:table-cell>
	                <fo:table-cell padding="2pt" border="1px solid #D4D0C8"><fo:block>${uiLabelMap.Remark2}</fo:block></fo:table-cell>
	            </fo:table-row>
	        </fo:table-header>
	        <fo:table-body>
	            <#assign totalValue = 0>
            	<#assign totalRemark = 0>
            	<#assign i = 0>
            	<#list listProducts as item>
            		<#assign i = i + 1>
            		<#assign convertNumber = 1>
            		<#assign productId = item.productId?if_exists>
            		<#assign productCode = item.productCode?if_exists>
            		<#assign product = delegator.findOne("Product", {"productId" : productId}, true)!>
            		<#assign baseUom = delegator.findOne("Uom", {"uomId" : product.quantityUomId}, true)!>
            		<#assign goodValue = item.quantity * item.unitPrice>
            		<#assign totalValue = totalValue + goodValue>
            		<#assign quantity = item.quantity>
            		<#assign listUomToConvert = Static["com.olbius.importsrc.AgreementServices"].getListUomToConvert(delegator, productId, productPackingUomId?if_exists, product.quantityUomId)!>
            		<#assign convertNumber = Static["com.olbius.importsrc.AgreementServices"].getProductConvertNumber(delegator, convertNumber, productId, productPackingUomId?if_exists, product.quantityUomId, listUomToConvert)!>
	            	<#assign remark = quantity/convertNumber>
	            	<#assign totalRemark = totalRemark + remark>
	            	<#assign uomProductPacking = delegator.findOne("Uom", {"uomId" : productPackingUomId?if_exists}, true)!>
	                <fo:table-row border="1px solid #D4D0C8">
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                    	<fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                    	<fo:block>${productCode?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${product.productName?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8">
	                        <fo:block>${baseUom.get("description", locale)?if_exists}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8" text-align="right">
	                        <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(quantity?if_exists, "#,##0", locale)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8" text-align="right">
	                        <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists?default(0), "#,##0", locale)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8" text-align="right">
	                        <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(goodValue, "#,##0", locale)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell padding="2pt" border="1px solid #D4D0C8" text-align="right">
	                        <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(remark?if_exists, "#,##0", locale)}</fo:block>
	                    </fo:table-cell>
	                </fo:table-row>
	            </#list>
	            <fo:table-row border="1px #D4D0C8">
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8" number-columns-spanned="6" ><fo:block>${uiLabelMap.Total2}</fo:block></fo:table-cell>
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8" text-align="right"><fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalValue, "#,##0", locale)} (${currencyUomId})</fo:block></fo:table-cell>
	            	<fo:table-cell padding="2pt" border="1px solid #D4D0C8" text-align="right"><fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalRemark?if_exists, "#,##0", locale)}</fo:block></fo:table-cell>
	            </fo:table-row>
	        </fo:table-body>
	    </fo:table>
	    <fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%" margin-top="3px">
    		1.3 ${uiLabelMap.AllThePaymentCouldBeCombineRatioBetween2} ${currencyUomId} ${uiLabelMap.CommonAnd2} 
    		<#list listOtherCurrencyTerms as otherUom>
    			${(otherUom.textValue)?if_exists},
    		</#list>
    		. ${uiLabelMap.CFR} ${currentPortTerm?if_exists}
    	</fo:block>
    	
    	<fo:block page-break-after="always"></fo:block>
    	
	    <fo:block text-align="left" font-size="10pt" font-family="Arial" font-weight="bold">
			2. ${uiLabelMap.TermOfDelivery2?upper_case}
		</fo:block>
		<#assign etd = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETDTerm?if_exists}, true)!>
		<#assign eta = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : currentETATerm?if_exists}, true)!>
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
				2.1 ${uiLabelMap.TheDateOfShipment2}:
			</fo:block>
				<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="10%">
					${uiLabelMap.ETD2}: ${(etd.fromDate)?if_exists}
				</fo:block>
				<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="10%">
					${uiLabelMap.ETA2}: ${(eta.fromDate)?if_exists}				
				</fo:block>
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
				2.2 ${uiLabelMap.PortOfDischarge2}: ${currentPortTerm?if_exists}
			</fo:block>
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
				2.3 ${uiLabelMap.Transshipment2} <#if transshipment == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if>. ${uiLabelMap.PartialShipment2} <#if partialShipment == "Y"> ${uiLabelMap.IsAllowed2}<#else> ${uiLabelMap.IsNotAllowed2}</#if>
			</fo:block>
		<fo:block text-align="left" font-size="10pt" font-family="Arial" font-weight="bold">
			3. ${uiLabelMap.TermOfPayment2?upper_case}
		</fo:block>
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.AgreementPaymentTermDetail2}
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
				<#list listFinAccountTos as finAcc>
					<#assign bic = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "BIC"}, true)!>
					<#assign iban = delegator.findOne("FinAccountAttribute", {"finAccountId" : finAcc.finAccountId, "attrName" : "IBAN"}, true)!>
					<fo:block text-align="left" font-size="10pt" font-family="Arial" font-weight="bold">
						${uiLabelMap.SupplierBank2}: ${(finAcc.finAccountName)?if_exists}
					</fo:block>
					<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
						${uiLabelMap.Account2} (${finAcc.currencyUomId}): ${finAcc.finAccountCode}
						<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%"> 
					 	<#if bic?has_content>
							${uiLabelMap.FinAccBIC}: ${(bic.attrValue)?if_exists} 
						</#if>
						</fo:block>
						<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
						<#if iban?has_content>
							${uiLabelMap.FinAccIBAN}: ${(iban.attrValue)?if_exists} 
						</#if>
						</fo:block>
					</fo:block>
				</#list>
				${uiLabelMap.Beneficiary2}: ${(supplier.groupNameLocal)?if_exists?upper_case}
			</fo:block>
		</fo:block>
		<fo:block text-align="left" font-size="10pt" font-family="Arial" font-weight="bold">
			4. ${uiLabelMap.Packing2?upper_case}
		</fo:block>
		
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.AgreementPackingTermDetail2}
		</fo:block>
		
		<fo:block text-align="left" font-size="10pt" font-family="Arial" font-weight="bold">
			5. ${uiLabelMap.Documentation2?upper_case}
		</fo:block>
		
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
		${uiLabelMap.AgreementDocumentTermDetailEn}
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%"> 
			- ${uiLabelMap.Invoice2}
			</fo:block> 
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			- ${uiLabelMap.PackingList2} (${uiLabelMap.DeliveryNote2})
			</fo:block> 
			<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			- ${uiLabelMap.BillOfLading2}
			</fo:block> 
		</fo:block>
		
		<fo:block text-align="left" font-size="10pt" font-family="Arial" font-weight="bold">
			6. ${uiLabelMap.Transportation2?upper_case}
		</fo:block>
		
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${(supplier.groupNameLocal)?if_exists?upper_case}
		</fo:block> 
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.Receiver2}: ${(purchaser.groupNameLocal)?if_exists?upper_case}
		</fo:block>
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.Address2}: ${(addressFrom.address2)?if_exists}
		</fo:block>
		<fo:block text-align="left" font-size="10pt" font-family="Arial" margin-left="3%">
			${uiLabelMap.AmendmentSupplementary2}
		</fo:block>
		
		<fo:block text-align="center" margin-top="10px" font-size="10pt" font-family="Arial" font-weight="bold">
			<fo:table font-family="Arial" font-size="10pt">
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