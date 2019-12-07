<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
        <fo:simple-page-master master-name="main" page-width="${pdfWidth*10}mm" page-height="${pdfHeight*10}mm" 
    		margin-top="0.0cm" margin-bottom="2px" margin-left="0.0cm" margin-right="0.0cm">
            <fo:region-body margin-top="2px"/>
            <fo:region-before extent="0in"/>
            <fo:region-after extent="0in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>
    <#assign companyGroup = delegator.findOne("PartyGroup", {"partyId" : "company"}, false)/>
    <#if productList?exists>
    <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Arial">
            <fo:block>
                <#list productList as product>
                    <#if product?exists>
	                    <fo:table>
	                    	<fo:table-body>
		                    	<#assign height = product.height>
		                    	<#assign width = product.width>
		                    	
		                    	<#if includeProductId == "Y">
		                    		<#assign height = height - 2.5/>
		                    	</#if>
		                    	
		                    	<#if includeProductName == "Y">
		                    		<#assign height = height - 2.5/>
		                    	</#if>

								<#if includeUnitPrice == "Y">
		                    		<#assign height = height - 2.5/>
		                    	</#if>

								<#if includeCompanyName == "Y">
		                    		<#assign height = height - 2.5/>
		                    	</#if>

		                    	<#assign r = ((pdfWidth * 10) % width)>
		                    	<#assign e = ((pdfWidth * 10) - r)>
		                    	
		                    	<#assign numberCell = (e / width)>
		                    	
		                        <#assign qty = product.quantity>
		                        <#if (qty > numberCell)>
		                            <#assign numberExtendRow = (qty % numberCell)>
		                        	<#assign numberMainRow = ((qty - numberExtendRow) / numberCell)>
		                        	<#list 1..numberMainRow as i>
		                            	<fo:table-row>
		                            		<#list 1..numberCell as j>
		                            			<fo:table-cell border="0px" width="0.5mm">
		                            			</fo:table-cell>
			                                    <fo:table-cell border="thin solid black 1px" width="${width-1}mm" height="${product.height}mm">
			                                   	 	<#if includeCompanyName == "Y">
				                                        <fo:block text-align="center" font-size="8pt">
				                                        	<#if companyGroup.groupName?has_content>
				                                        		<#assign spl = product.width/2>
				                                        		<#if companyGroup.groupName?length &gt; spl>
				                                        			<#assign str = companyGroup.groupName?substring(0, spl) + "..."/>
		                                        				<#else>
		                                        					<#assign str = companyGroup.groupName/>
				                                        		</#if>
				                                        		${str}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
			                                        <fo:block text-align="center" padding-top="2px">
			                                            <fo:instream-foreign-object>
			                                                <barcode:barcode
			                                                        xmlns:barcode="http://barcode4j.krysalis.org/ns"
			                                                        message="${product.idValue?if_exists}" orientation="0">
			                                                    <barcode:code128>
			                                                        <barcode:height>${height}</barcode:height>
			                                                        <barcode:width>${width}</barcode:width>
			                                                    </barcode:code128>
			                                                </barcode:barcode>
			                                            </fo:instream-foreign-object>
			                                        </fo:block>
													<#if includeProductId == "Y">
			                                         	<fo:block text-align="center" font-size="8pt">
				                                        	<#if product.productCode?has_content>
				                                        		${product.productCode}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
			                                        <#if includeProductName == "Y">
			                                         	<fo:block text-align="center" font-size="8pt">
				                                        	<#if product.productName?has_content>
				                                        		<#assign spl = product.width/2>
				                                        		<#if product.productName?length &gt; spl>
				                                        			<#assign str = product.productName?substring(0, spl) + "..."/>
		                                        				<#else>
		                                        					<#assign str = product.productName/>
				                                        		</#if>
				                                        		${str}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
													<#if includeUnitPrice == "Y">
														<fo:block text-align="center" font-size="8pt">
				                                        	<#if product.price?has_content>
				                                        		${uiLabelMap.UnitPrice}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(product.price?if_exists, product.currencyUom?if_exists?default('VND'), locale)}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
			                                    </fo:table-cell>
			                                    <fo:table-cell border="0px" width="0.5mm">
		                            			</fo:table-cell>
		                                    </#list>
		                                </fo:table-row>
		                                <fo:table-row height="0.5mm">
		                                	<fo:table-cell border="0px" width="0.5mm">
	                            			</fo:table-cell>
	                            			<fo:table-cell border="0px" width="0.5mm">
	                            			</fo:table-cell>
	                            			<fo:table-cell border="0px" width="0.5mm">
	                            			</fo:table-cell>
	                                 	</fo:table-row>
		                            </#list>
		                            <#if (numberExtendRow > 0)>
		                            	<fo:table-row>
		                            		<#list 1..numberExtendRow as i>
		                            			<fo:table-cell border="0px" width="0.5mm">
		                            			</fo:table-cell>
			                                    <fo:table-cell border="thin solid black 1px" width="${width-1}mm" height="${height}mm">
			                                    	<#if includeCompanyName == "Y">
				                                        <fo:block text-align="center" font-size="8pt">
				                                        	<#if companyGroup.groupName?has_content>
				                                        		<#assign spl = product.width/2>
				                                        		<#if companyGroup.groupName?length &gt; spl>
				                                        			<#assign str = companyGroup.groupName?substring(0, spl) + "..."/>
		                                        				<#else>
		                                        					<#assign str = companyGroup.groupName/>
				                                        		</#if>
				                                        		${str}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
			                                        <fo:block text-align="center" margin-top="5px">
			                                            <fo:instream-foreign-object>
			                                                <barcode:barcode
			                                                        xmlns:barcode="http://barcode4j.krysalis.org/ns"
			                                                        message="${product.idValue?if_exists}" orientation="0">
			                                                    <barcode:code128>
			                                                        <barcode:height>${height}</barcode:height>
			                                                        <barcode:width>${width}</barcode:width>
			                                                    </barcode:code128>
			                                                </barcode:barcode>
			                                            </fo:instream-foreign-object>
			                                        </fo:block>
													<#if includeProductId == "Y">
			                                         	<fo:block text-align="center" font-size="8pt">
				                                        	<#if product.productCode?has_content>
				                                        		${product.productCode}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
			                                        <#if includeProductName == "Y">
			                                         	<fo:block text-align="center" font-size="8pt">
				                                        	<#if product.productName?has_content>
				                                        		<#assign spl = product.width/2>
				                                        		<#if product.productName?length &gt; spl>
				                                        			<#assign str = product.productName?substring(0, spl) + "..."/>
		                                        				<#else>
		                                        					<#assign str = product.productName/>
				                                        		</#if>
				                                        		${str}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
													<#if includeUnitPrice == "Y">
														<fo:block text-align="center" font-size="8pt">
				                                        	<#if product.price?has_content>
				                                        		${uiLabelMap.UnitPrice}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(product.price?if_exists, product.currencyUom?if_exists?default('VND'), locale)}
				                                        	</#if>
				                                        </fo:block>
			                                        </#if>
			                                    </fo:table-cell>
			                                    <fo:table-cell border="0px" width="0.5mm">
		                            			</fo:table-cell>
		                                    </#list>
		                                </fo:table-row>
		                            </#if>
		                        <#else>
		                            <fo:table-row>
		                        		<#list 1..qty as i>
		                        			<fo:table-cell border="0px" width="0.5mm">
	                            			</fo:table-cell>
		                                    <fo:table-cell border="thin solid black 1px" width="${width-1}mm" height="${height}mm">
		                                    	<#if includeCompanyName == "Y">
			                                        <fo:block text-align="center" font-size="8pt">
			                                        	<#if companyGroup.groupName?has_content>
			                                        		<#assign spl = product.width/2>
			                                        		<#if companyGroup.groupName?length &gt; spl>
			                                        			<#assign str = companyGroup.groupName?substring(0, spl) + "..."/>
	                                        				<#else>
	                                        					<#assign str = companyGroup.groupName/>
			                                        		</#if>
			                                        		${str}
			                                        	</#if>
			                                        </fo:block>
		                                        </#if>
		                                        <fo:block text-align="center" margin-top="5px">
		                                            <fo:instream-foreign-object>
		                                                <barcode:barcode
		                                                        xmlns:barcode="http://barcode4j.krysalis.org/ns"
		                                                        message="${product.idValue?if_exists}" orientation="0">
		                                                    <barcode:code128>
		                                                        <barcode:height>${height}</barcode:height>
		                                                        <barcode:width>${width}</barcode:width>
		                                                    </barcode:code128>
		                                                </barcode:barcode>
		                                            </fo:instream-foreign-object>
		                                        </fo:block>
		                                        <#if includeProductId == "Y">
		                                         	<fo:block text-align="center" font-size="8pt">
			                                        	<#if product.productCode?has_content>
			                                        		${product.productCode}
			                                        	</#if>
			                                        </fo:block>
		                                        </#if>
		                                        <#if includeProductName == "Y">
		                                         	<fo:block text-align="center" font-size="8pt">
			                                        	<#if product.productName?has_content>
			                                        		<#assign spl = product.width/2>
			                                        		<#if product.productName?length &gt; spl>
			                                        			<#assign str = product.productName?substring(0, spl) + "..."/>
	                                        				<#else>
	                                        					<#assign str = product.productName/>
			                                        		</#if>
			                                        		${str}
			                                        	</#if>
			                                        </fo:block>
		                                        </#if>
												<#if includeUnitPrice == "Y">
													<fo:block text-align="center" font-size="8pt">
			                                        	<#if product.price?has_content>
			                                        		${uiLabelMap.UnitPrice}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(product.price?if_exists, product.currencyUom?if_exists?default('VND'), locale)}
			                                        	</#if>
			                                        </fo:block>
		                                        </#if>
		                                    </fo:table-cell>
		                                    <fo:table-cell border="0px" width="0.5mm">
	                            			</fo:table-cell>
		                                </#list>
		                            </fo:table-row>
		                            <fo:table-row height="0.5mm">
	                                	<fo:table-cell border="0px" width="0.5mm">
                            			</fo:table-cell>
                            			<fo:table-cell border="0px" width="0.5mm">
                            			</fo:table-cell>
                            			<fo:table-cell border="0px" width="0.5mm">
                            			</fo:table-cell>
                                 	</fo:table-row>
		                        </#if>
		                    </#if>
		                    <fo:table-row>
		                        <fo:table-cell>
		                        	<fo:block text-align="center" margin-top="10px">
		                        	</fo:block>
		                    	</fo:table-cell>
		                    </fo:table-row>
	                    </fo:table-body>
	                </fo:table>
	            </#list>
            </fo:block>
        </fo:flow>
    </fo:page-sequence>
    </#if>
</fo:root>
</#escape>
