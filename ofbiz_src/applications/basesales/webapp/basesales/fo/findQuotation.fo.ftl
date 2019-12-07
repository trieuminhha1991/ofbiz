<#escape x as x?xml>
<fo:block>
<#if listQuotationData?exists>
	<#--header top-->
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.BSGoodProductQuotation}
		</fo:block>
		<fo:block>
			<fo:table border-color="black" border-style="solid" border-width="0">
				<fo:table-column column-width="2.8cm"/>
				<fo:table-column/>
			    <fo:table-body>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSSalesChannel}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${productStoreName?if_exists}</fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			        <#if customerId?exists>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSCustomerId}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${customerId}</fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			        </#if>
			    </fo:table-body>
			</fo:table>
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm">
		<fo:table border-color="black" border-style="solid" border-width="1pt">
			<fo:table-column column-width="1cm"/>
			<fo:table-column column-width="2.5cm"/>
			<fo:table-column/>
		    <#--<fo:table-column column-width="2cm"/>-->
		    <fo:table-column column-width="1.5cm"/>
		    <fo:table-column column-width="1.5cm"/>
		    <fo:table-column column-width="2.5cm"/>
		    <fo:table-column column-width="2.5cm"/>
		    <fo:table-header>
		        <fo:table-row border-color="black" background-color="#FFFF99"><#-- font-size="8pt"-->
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSSTT}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductId}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductName}</fo:block>
		        	</fo:table-cell>
		            <#--
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSFeature}</fo:block>
		        	</fo:table-cell>
		            -->
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductPackingUomId}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSTax}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" font-size="8pt" display-align="center" border="1pt solid black" number-columns-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.BSPriceToCustomer}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		        <fo:table-row border-color="black" background-color="#FFFF99" text-transform="uppercase" font-size="8pt">
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSListPrice}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSSalesPrice}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		    </fo:table-header>
		    <fo:table-body>
				<#list listQuotationData as quotationItem>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>${quotationItem_index + 1}</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>${quotationItem.productCode?if_exists}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>${quotationItem.productName?if_exists}</fo:block>
		            </fo:table-cell>
		            <#--
		            <fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>${quotationItem.features?if_exists}</fo:block>
		            </fo:table-cell>
		            -->
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
		                	<#if quotationItem.quantityUomDesc?exists>${quotationItem.quantityUomDesc}</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
							<#if quotationItem.taxPercentage?exists>${quotationItem.taxPercentage}%</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	<#if quotationItem.price?exists>
        						<@ofbizCurrency amount=quotationItem.unitListPriceVAT isoCode=currencyUomId/>
		                	</#if>
	                	</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
            				<#if quotationItem.priceVAT?exists>
        						<@ofbizCurrency amount=quotationItem.priceVAT isoCode=currencyUomId/>
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
		<fo:block text-align="right" font-weight="bold" font-style="italic" margin-top="0.2cm">${uiLabelMap.BSHaNoi}, ${uiLabelMap.BSDayLowercase} 
		<#if fromDateDateTime?exists><#if fromDateDateTime.get(5) &lt; 9>0${fromDateDateTime.get(5)}<#else>${fromDateDateTime.get(5)}</#if> ${uiLabelMap.BSMonthLowercase} <#if fromDateDateTime.get(2) &lt; 9>0${fromDateDateTime.get(2) + 1}<#else>${fromDateDateTime.get(2) + 1}</#if> ${uiLabelMap.BSYearLowercase} ${fromDateDateTime.get(1)}
		<#else>... ${uiLabelMap.BSMonthLowercase} ... </#if></fo:block>
	</fo:block>
<#else>
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.BSProductPrices}
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm" text-transform="uppercase">
		<fo:block>${uiLabelMap.BSThisProductQuotationNotAvaiable}</fo:block>
	</fo:block>
</#if>
</fo:block>
</#escape>