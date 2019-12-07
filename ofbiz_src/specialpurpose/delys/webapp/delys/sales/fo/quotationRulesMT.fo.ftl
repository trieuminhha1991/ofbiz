<#escape x as x?xml>
<fo:block>
<#if productQuotation?exists>
	<#--header top-->
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DPProductPricesB}
		</fo:block>
		<fo:block font-weight="bold">
			<fo:inline font-style="italic" text-decoration="underline">${uiLabelMap.DPHeaderTitleSendTo}</fo:inline>: ${uiLabelMap.DPHeaderTitleCustomerSer}
		</fo:block>
		<fo:block margin-top="0.2cm">
			${uiLabelMap.DPHeaderTitleContentMT} <#if listRoleApply?exists>(${listRoleApply}) </#if>${uiLabelMap.DPHeaderTitleContentMT2}:
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm">
		<fo:block text-align="right" font-style="italic" font-weight="bold" margin-bottom="1mm">${uiLabelMap.DPQuotation}: ${productQuotation.productQuotationId?if_exists}</fo:block>
		<fo:table border-color="black" border-style="solid" border-width="1pt">
			<fo:table-column column-width="1cm"/>
			<fo:table-column column-width="3cm"/>
		    <fo:table-column/>
		    <fo:table-column column-width="2cm"/>
		    <fo:table-column column-width="1cm"/>
		    <fo:table-column column-width="3cm"/>
		    <fo:table-column column-width="1.1cm"/>
		    <fo:table-column column-width="1.1cm"/>
		    <fo:table-column column-width="2cm"/>
		    <fo:table-column column-width="2cm"/>
		    <fo:table-column column-width="2cm"/>
		    <fo:table-header>
		        <fo:table-row border-color="black" background-color="#FFFF99" font-size="8pt">
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DANo}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DABarcode}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAProductName}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAMateBy}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DATax}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAPacking}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAQC}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAPackingPerTray}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DABeforeVATPerPacking}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAAfterVATPerPacking}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAPriceToConsumer}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		    </fo:table-header>
		    <fo:table-body>
				<#list listProductQuotationRuleData as quotationRule>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>${quotationRule_index + 1}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
		                <fo:block>${quotationRule.barcode?if_exists}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>${quotationRule.productName?if_exists}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
		                <fo:block>${quotationRule.brandName?if_exists}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block><#if quotationRule.taxPercentage?exists>${quotationRule.taxPercentage}%</#if></fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
		                	<#if quotationRule.productWeightStr?exists>${quotationRule.productWeightStr}</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
							<#if quotationRule.productQC?exists>${quotationRule.productQC}</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
							<#if quotationRule.productQuantityPerTray?exists>${quotationRule.productQuantityPerTray}</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block><!--DABeforeVATPerPacking-->
		                	<#if quotationRule.priceToDistNormal?exists>
        						<@ofbizCurrency amount=quotationRule.priceToDistNormal isoCode=productQuotation.currencyUomId/>
		                	</#if>
	                	</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block><!--DAAfterVATPerPacking-->
            				<#if quotationRule.priceToDistNormalTax?exists>
        						<@ofbizCurrency amount=quotationRule.priceToDistNormalTax isoCode=productQuotation.currencyUomId/>
            				</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block><!--DAPriceToConsumer-->
		                	<#if quotationRule.priceToConsumerNormal?exists>
        						<@ofbizCurrency amount=quotationRule.priceToConsumerNormal isoCode=productQuotation.currencyUomId/>
	                		</#if>
                		</fo:block>
		            </fo:table-cell>
				</fo:table-row>
				</#list>
			</fo:table-body>
		</fo:table>
	</fo:block>
	
	<#--footer-->
	<fo:block margin-top="0.5cm">
		<fo:block font-weight="bold">${uiLabelMap.DPFooterThank}</fo:block>
		<fo:block text-align="right" font-weight="bold" font-style="italic" margin-top="0.2cm">${uiLabelMap.DPHaNoi}, ${uiLabelMap.DPDayLowercase} 
		<#if fromDateDateTime?exists><#if fromDateDateTime.get(5) &lt; 9>0${fromDateDateTime.get(5)}<#else>${fromDateDateTime.get(5)}</#if> ${uiLabelMap.DPMonthLowercase} <#if fromDateDateTime.get(2) &lt; 9>0${fromDateDateTime.get(2) + 1}<#else>${fromDateDateTime.get(2) + 1}</#if> ${uiLabelMap.DPYearLowercase} ${fromDateDateTime.get(1)}
		<#else>... ${uiLabelMap.DPMonthLowercase} ... ${productQuotation.fromDate.getYear()}</#if></fo:block>
	</fo:block>
<#else>
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DPProductPrices}
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm" text-transform="uppercase">
		<fo:block>${uiLabelMap.DAThisProductQuotationNotAvaiable}</fo:block>
	</fo:block>
</#if>
</fo:block>
</#escape>