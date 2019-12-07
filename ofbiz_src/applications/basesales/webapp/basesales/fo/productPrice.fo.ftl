<#escape x as x?xml>
<fo:block>
<#if listProduct?has_content>
	<#--header top-->
	<#--body-->
	<fo:block margin-top="2mm">
		<fo:table table-layout="fixed">
			<fo:table-column column-width="8cm"/>
			<fo:table-column column-width="8cm"/>
		    <fo:table-body>
		    	<#assign productSizeInd = listProduct?size - 1/>
				<#list listProduct as productItem>
				<#if productItem_index % 2 == 0>
		        <fo:table-row>
		        </#if>
					<#assign isPrintUom = false>
					<#if productItem.uomId?exists && productItem.quantityUomId != productItem.uomId><#assign isPrintUom = true></#if>
		        	<fo:table-cell padding-right="3.3mm" padding-bottom="0.3mm" text-align="left" height="2cm"><#-- border="1pt solid black"-->
	                <#if productItem.unitListPrice?exists && productItem.unitPrice?exists && (productItem.unitPrice < productItem.unitListPrice)>
	                	<fo:block border-color="#000000" border-style="solid" border-width="1pt">
		                	<fo:table padding="1.4mm" table-layout="fixed">
								<fo:table-column column-width="22mm"/>
								<fo:table-column column-width="20mm"/>
								<fo:table-column column-width="34.5mm"/>
							    <fo:table-body>
							        <fo:table-row background-color="#FFDE15">
							        	<fo:table-cell padding="3mm 1mm 1mm 2mm" font-size="10pt" number-columns-spanned="3">
							                <fo:block>
												<fo:table table-layout="fixed">
													<fo:table-column column-width="5.6cm"/>
													<fo:table-column column-width="1.4cm"/>
													<fo:table-body>
												        <fo:table-row>
												        	<fo:table-cell display-align="after">
												                <fo:block> 
																   	${productItem.productName?if_exists}
												                </fo:block> 
												            </fo:table-cell>
												            <fo:table-cell text-align="right" display-align="after">
												            	<fo:block>
												                	<fo:inline text-decoration="line-through">
																		<#assign listPrice = Static["com.olbius.basesales.product.ProductWorker"].calcPriceTaxDisplay(productItem.getBigDecimal("unitListPrice"), productItem.getBigDecimal("taxPercentage"), productItem.getString("currencyUomId"))!>
												                		<#if listPrice?exists>
												                			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(listPrice, "#,###.##", locale)}
												                		</#if>
												                	</fo:inline>
												                </fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												    </fo:table-body>
												</fo:table>
							                </fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row background-color="#FFDE15">
							        	<fo:table-cell padding="1mm 1mm 0mm 2mm" number-columns-spanned="3">
							                <fo:block>
							                	<fo:table table-layout="fixed">
							                		<#if isPrintUom>
													<fo:table-column column-width="5cm"/>
													<fo:table-column column-width="2cm"/>
													<#else>
													<fo:table-column column-width="6cm"/>
													<fo:table-column column-width="1cm"/>
													</#if>
													<fo:table-body>
												        <fo:table-row>
												        	<fo:table-cell padding="0mm" font-weight="bold">
											                	<fo:block-container bottom="0cm" left="0" width="6cm" height="1.05cm" font-size="30pt" text-align="start" vertical-align="bottom"> 
													                <fo:block> 
													                	<#assign unitPrice = Static["com.olbius.basesales.product.ProductWorker"].calcPriceTaxDisplay(productItem.getBigDecimal("unitPrice"), productItem.getBigDecimal("taxPercentage"), productItem.getString("currencyUomId"))!>
												                		<#if unitPrice?exists>
												                			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(unitPrice, "#,###.##", locale)}
												                		</#if>
																	    <#--${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(productItem.unitPrice, "#,###.##", locale)}-->
													                </fo:block> 
																</fo:block-container> 
																<#--<fo:inline padding-bottom="-3mm" display-align="after" font-size="30pt" border="1pt solid black">
																	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(productItem.unitPrice, "#,###.##", locale)}
											                	</fo:inline>-->
												            </fo:table-cell>
												            <fo:table-cell font-size="10pt" padding-bottom="0mm" text-align="right" display-align="after">
												            	<fo:block>
												                	${productItem.currencyUomId?if_exists}<#if isPrintUom>/${productItem.uomId}</#if>
												                </fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												    </fo:table-body>
												</fo:table>
							                </fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell font-size="7pt" padding="4.5mm 1mm 1mm 2mm">
							                <fo:block>
							                	<#assign categoryIdTree = Static["com.olbius.dtm.KHTTServices"].getParentProductCategory(delegator, productItem.getString("productId"), nowTimestamp)!>
							                	${categoryIdTree?default("(Ma nganh hang)")}
							                </fo:block>
							            </fo:table-cell>
							        	<fo:table-cell font-size="7pt" padding="4.5mm 1mm 1mm 2mm">
							                <fo:block>
							                	${productItem.productCode?if_exists}
							                </fo:block>
							            </fo:table-cell>
							        	<fo:table-cell font-size="6pt" padding="4.5mm 1mm 1mm" number-rows-spanned="2">
						                	<fo:block>
					                		<#if productItem.idSKU?exists>
					                			<fo:block></fo:block>
				            					<fo:instream-foreign-object>
								                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns" message="${productItem.idSKU}">
								                    	<#if productItem.idSKU?length == 12>
								                    		<#assign formatBarcode = "upc-a"/>
								                    	<#elseif productItem.idSKU?length == 13>
								                    		<#assign formatBarcode = "ean-13"/>
								                    	<#else>
								                    		<#assign formatBarcode = "code128"/>
							                    		</#if>
								                        <barcode:${formatBarcode}>
								                            <barcode:height>8mm</barcode:height>
								                            <barcode:module-width>0.25mm</barcode:module-width>
								                            <#--<quiet-zone enabled="true">10mw</quiet-zone>-->
								                        </barcode:${formatBarcode}>
								                        <barcode:human-readable>
								                            <barcode:placement>bottom</barcode:placement>
								                            <barcode:font-name>${defaultFontFamily}</barcode:font-name>
								                            <barcode:font-size>8pt</barcode:font-size>
								                            <barcode:display-start-stop>false</barcode:display-start-stop>
								                            <barcode:display-checksum>false</barcode:display-checksum>
								                        </barcode:human-readable>
								                    </barcode:barcode>
								                </fo:instream-foreign-object>
											</#if>
				            				</fo:block>
							            </fo:table-cell>
									</fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell font-size="7pt" padding="0 1mm 2mm 2mm" display-align="after">
							                <fo:block>
												${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(nowTimestamp, "ddMMyy", locale, timeZone)!}
							                </fo:block>
							            </fo:table-cell>
							        	<fo:table-cell font-size="7pt" padding="0 1mm 2mm 2mm" display-align="after">
							                <fo:block>
							                	${productItem.supplierCode?if_exists}
							                </fo:block>
							            </fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
		                </fo:block>
	                <#else>
		                <fo:block border-color="#FFDE15" border-style="solid" border-width="1.5mm">
		                	<fo:table table-layout="fixed">
								<fo:table-column column-width="2.2cm"/>
								<fo:table-column column-width="2cm"/>
								<fo:table-column column-width="3.3cm"/>
							    <fo:table-body>
							        <fo:table-row>
							        	<fo:table-cell padding="2mm 1mm 1mm 2mm" font-size="10pt" number-columns-spanned="3">
							                <fo:block>
							                	${productItem.productName?if_exists}
							                </fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell padding="1mm 1mm 1mm 2mm" number-columns-spanned="3">
							                <fo:block>
							                	<fo:table table-layout="fixed">
													<#if isPrintUom>
													<fo:table-column column-width="5cm"/>
													<fo:table-column column-width="2cm"/>
													<#else>
													<fo:table-column column-width="6cm"/>
													<fo:table-column column-width="1cm"/>
													</#if>
													<fo:table-body>
												        <fo:table-row>
												        	<fo:table-cell font-size="30pt" padding="0" font-weight="bold" display-align="after">
												                <fo:block padding="0">
												                	<#--<#if productItem.unitPrice?exists>
																		<@ofbizCurrency amount=productItem.unitPrice isoCode=productItem.currencyUomId/>
																	</#if>-->
																	<#if productItem.unitPrice?exists>
																		<#assign unitPrice = Static["com.olbius.basesales.product.ProductWorker"].calcPriceTaxDisplay(productItem.getBigDecimal("unitPrice"), productItem.getBigDecimal("taxPercentage"), productItem.getString("currencyUomId"))>
												                		<#if unitPrice?exists>
												                			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(unitPrice, "#,###.##", locale)}
												                		</#if>
																	</#if>
																	<#--${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(productItem.unitPrice, "#,###.##", locale)}-->
												                </fo:block>
												            </fo:table-cell>
												            <fo:table-cell font-size="10pt" padding-bottom="2mm" text-align="right" display-align="after">
												            	<fo:block>
												                	${productItem.currencyUomId?if_exists}<#if isPrintUom>/${productItem.uomId}</#if>
												                </fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												    </fo:table-body>
												</fo:table>
							                </fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell font-size="7pt" padding="1mm 1mm 1mm 2mm">
							                <fo:block>
							                	<#assign categoryIdTree = Static["com.olbius.dtm.KHTTServices"].getParentProductCategory(delegator, productItem.getString("productId"), nowTimestamp)!>
							                	${categoryIdTree?default("(Ma nganh hang)")}
							                </fo:block>
							            </fo:table-cell>
							        	<fo:table-cell font-size="7pt" padding="1mm 1mm 1mm 2mm">
							                <fo:block>
							                	${productItem.productCode?if_exists}
							                </fo:block>
							            </fo:table-cell>
							        	<fo:table-cell font-size="6pt" padding="0 1mm 1mm" number-rows-spanned="2">
						                	<fo:block>
					                		<#if productItem.idSKU?exists>
					                			<#-- 
					                			<fo:block-container position="absolute">
					                			 	<fo:block position="absolute" top="0">${productItem.idSKU}</fo:block>
					                			</fo:block-container> -->
												<fo:block></fo:block>
					                			<fo:instream-foreign-object>
								                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns" message="${productItem.idSKU}">
								                        <#if productItem.idSKU?length == 12>
								                    		<#assign formatBarcode = "upc-a"/>
								                    	<#elseif productItem.idSKU?length == 13>
								                    		<#assign formatBarcode = "ean-13"/>
								                    	<#else>
								                    		<#assign formatBarcode = "code128"/>
							                    		</#if>
								                        <barcode:${formatBarcode}>
								                            <barcode:height>8mm</barcode:height>
								                            <barcode:module-width>0.25mm</barcode:module-width>
								                            <#--<quiet-zone enabled="true">10mw</quiet-zone>-->
								                        </barcode:${formatBarcode}>
								                        <barcode:human-readable>
								                            <barcode:placement>bottom</barcode:placement>
								                            <barcode:font-name>${defaultFontFamily}</barcode:font-name>
								                            <barcode:font-size>8pt</barcode:font-size>
								                            <barcode:display-start-stop>false</barcode:display-start-stop>
								                            <barcode:display-checksum>false</barcode:display-checksum>
								                        </barcode:human-readable>
								                    </barcode:barcode>
								                </fo:instream-foreign-object>
											</#if>
				            				</fo:block>
							            </fo:table-cell>
									</fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell font-size="7pt" padding="0 1mm 1mm 2mm" display-align="after">
							                <fo:block>
												${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(nowTimestamp, "ddMMyy", locale, timeZone)!}
							                </fo:block>
							            </fo:table-cell>
							        	<fo:table-cell font-size="7pt" padding="0 1mm 1mm 2mm" display-align="after">
							                <fo:block>
							                	${productItem.supplierCode?if_exists}
							                </fo:block>
							            </fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
		                </fo:block>
	                </#if>
		            </fo:table-cell>
	            <#if productItem_index &gt; 0 && productItem_index % 2 == 1>
				</fo:table-row>
				</#if>

				<#if productItem_index == productSizeInd && productItem_index % 2 == 0>
					<fo:table-cell>
		                <fo:block>
		                </fo:block>
		            </fo:table-cell>
	            </fo:table-row>
				</#if>
				</#list>
			</fo:table-body>
		</fo:table>
	</fo:block>
	<#--footer-->
<#else>
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.BSProductPrices}
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm" text-transform="uppercase">
		<fo:block>${uiLabelMap.BSThisProductPriceNotAvaiable}</fo:block>
	</fo:block>
</#if>
</fo:block>
</#escape>