<#escape x as x?xml>
<fo:block>
<#if productQuotation?exists>
	<#--header top-->
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.BSProductPrices}
		</fo:block>
		<fo:block>
			<fo:table border-color="black" border-style="solid" border-width="0">
				<fo:table-column column-width="12%"/>
				<fo:table-column column-width="40%"/>
			    <fo:table-column column-width="14%"/>
			    <fo:table-column/>
			    <fo:table-body>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSQuotationId}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${productQuotation.productQuotationId?if_exists}</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSCurrencyUomId}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${productQuotation.currencyUomId?if_exists}</fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSQuotationName}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${productQuotation.quotationName?if_exists}</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSFromDate}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block><#if productQuotation.fromDate?exists>${productQuotation.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSStatus}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>
			                	<#assign currentStatusId = productQuotation.statusId?if_exists>
			                	<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
			                </fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSThruDate}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block><#if productQuotation.thruDate?exists>${productQuotation.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSDescription}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>
			                	${productQuotation.description?if_exists}
			                </fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSPartyApply}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>${listRoleApply?if_exists}</fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			        <fo:table-row>
			        	<fo:table-cell padding="1mm">
			                <fo:block>${uiLabelMap.BSSalesChannel}:</fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block>
			                	<#if productQuotation.salesMethodChannelEnumId?exists>
									<#assign salesMethodChannel = delegator.findOne("Enumeration", {"enumId" : productQuotation.salesMethodChannelEnumId}, false)/>
									<#if salesMethodChannel?exists>
										${salesMethodChannel.get("description", locale)}
									</#if>
								</#if>
			                </fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block></fo:block>
			            </fo:table-cell>
			            <fo:table-cell padding="1mm">
			                <fo:block></fo:block>
			            </fo:table-cell>
			        </fo:table-row>
			    </fo:table-body>
			</fo:table>
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm">
		<fo:block text-align="right" font-style="italic" font-weight="bold" margin-bottom="1mm">${uiLabelMap.BSQuotation}: ${productQuotation.productQuotationId?if_exists}</fo:block>
		<fo:table border-color="black" border-style="solid" border-width="1pt">
			<fo:table-column column-width="1.5cm"/>
			<fo:table-column column-width="3.5cm"/>
			<fo:table-column/>
		    <fo:table-column column-width="2cm"/>
		    <fo:table-column column-width="1.5cm"/>
		    <fo:table-column column-width="2.3cm"/>
		    <fo:table-column column-width="2.3cm"/>
		    <fo:table-header>
		        <fo:table-row border-color="black" background-color="#FFFF99" font-size="10pt">
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSSTT}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductId}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductName}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSPacking}</fo:block>
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
		            	<fo:block font-weight="bold">${uiLabelMap.BSBeforeVAT}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSAfterVAT}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		    </fo:table-header>
		    <fo:table-body>
				<#list listProductQuotationRuleData as quotationRule>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>${quotationRule_index + 1}</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>${quotationRule.productCode?if_exists}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>${quotationRule.productName?if_exists}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
		                	<#if quotationRule.quantityUomIdStr?exists>${quotationRule.quantityUomIdStr}</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
							<#if quotationRule.taxPercentage?exists>${quotationRule.taxPercentage}%</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	<#if quotationRule.listPrice?exists>
        						<@ofbizCurrency amount=quotationRule.listPrice isoCode=productQuotation.currencyUomId/>
		                	</#if>
	                	</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
            				<#if quotationRule.listPriceAfterVAT?exists>
        						<@ofbizCurrency amount=quotationRule.listPriceAfterVAT isoCode=productQuotation.currencyUomId/>
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
		<#else>... ${uiLabelMap.BSMonthLowercase} ... ${productQuotation.fromDate.getYear()}</#if></fo:block>
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