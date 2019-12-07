<#escape x as x?xml>
<fo:block>
<#if listProductGoodIds?has_content>
	<#--header top-->
	<#--body-->
	<fo:block margin-top="2mm">
		<fo:table>
			<fo:table-column column-width="80mm"/>
		    <fo:table-body>
		    	<#assign productSizeInd = listProductGoodIds?size - 1/>
				<#list listProductGoodIds as productItem>
		        <fo:table-row>
		        	<fo:table-cell padding="2mm 14mm" height="30mm">
		                <fo:block border-color="#000" border-style="solid" border-width="1pt">
		                	<fo:table>
								<fo:table-column column-width="28mm"/>
								<fo:table-column column-width="24mm"/>
							    <fo:table-body>
							        <fo:table-row>
							        	<fo:table-cell padding-top="1mm" font-size="10pt" font-weight="bold" text-align="center" number-columns-spanned="2">
							                <fo:block>
							                	${productInfo.productName?if_exists}
							                </fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell>
							        		<fo:block>
							        			<fo:table>
							        				<fo:table-column column-width="15mm"/>
							        				<fo:table-column column-width="13mm"/>
							        				<fo:table-body>
							        					<fo:table-row>
							        						<fo:table-cell number-columns-spanned="2">
							        							<fo:block>
											                		<#if productItem.idValue?exists>
											                			<#-- 
											                			<fo:block-container position="absolute">
											                			 	<fo:block position="absolute" top="0">${productItem.idSKU}</fo:block>
											                			</fo:block-container> -->
																		<fo:block></fo:block>
											                			<fo:instream-foreign-object>
														                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns" message="${productItem.idValue}">
														                        <#if productItem.goodIdentificationTypeId == "UPCA">
														                    		<#assign formatBarcode = "upc-a"/>
														                    	<#elseif productItem.goodIdentificationTypeId == "EAN">
														                    		<#assign formatBarcode = "ean-13"/>
														                    	<#else>
														                    		<#assign formatBarcode = "code128"/>
													                    		</#if>
														                        <barcode:${formatBarcode}>
														                            <barcode:height>10mm</barcode:height>
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
							        						<fo:table-cell padding-left="2mm" font-size="6pt" text-align="center" font-family="InterstateLight">
							        							<fo:block>
						        								<#if productItem.packingDate?exists>
																	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productItem.packingDate, "dd.MM.yy", locale, timeZone)!}
																</#if>
																</fo:block>
							        						</fo:table-cell>
							        						<fo:table-cell>
							        							<fo:block></fo:block>
							        						</fo:table-cell>
							        					</fo:table-row>
							        					<fo:table-row>
							        						<fo:table-cell padding-left="2mm" font-size="6pt" font-family="InterstateLight">
							        							<fo:block>NGAY DONG GOI</fo:block>
							        						</fo:table-cell>
							        						<fo:table-cell padding-left="1mm" text-align="right" border-left="1px dashed #000" font-size="6pt" font-family="InterstateLight">
							        							<fo:block>NGAY HET HAN</fo:block>
							        						</fo:table-cell>
							        					</fo:table-row>
							        					<fo:table-row>
							        						<fo:table-cell padding-left="2mm" font-size="6pt" font-family="InterstateLight" number-columns-spanned="2">
							        							<fo:block>Bao quan nhiet do: </fo:block>
							        						</fo:table-cell>
							        					</fo:table-row>
							        					<fo:table-row>
							        						<fo:table-cell padding-left="2mm" font-size="6pt" font-family="InterstateLight" number-columns-spanned="2">
							        							<fo:block>Khong dung thuc pham hu hong</fo:block>
							        						</fo:table-cell>
							        					</fo:table-row>
							        				</fo:table-body>
							        			</fo:table>
							        		</fo:block>
							        	</fo:table-cell>
							        	<fo:table-cell padding-left="2mm">
							                <fo:block>
							                	<fo:table>
													<fo:table-column column-width="10mm"/>
													<fo:table-column column-width="12mm"/>
													<fo:table-body>
												        <fo:table-row>
												        	<fo:table-cell font-size="6pt" text-align="center" font-family="InterstateLight">
												            	<fo:block>Don gia</fo:block>
												            </fo:table-cell>
												            <fo:table-cell font-size="6pt" text-align="left" font-family="InterstateLight">
												            	<fo:block>Khoi luong tinh</fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												        <fo:table-row>
												        	<fo:table-cell font-size="9pt" text-align="center" font-family="InterstateLight">
												            	<fo:block>
											                		<#if productInfo.unitPrice?exists><@ofbizCurrency amount=productInfo.unitPrice isoCode=productInfo.currencyUomId/></#if>
											                	</fo:block>
												            </fo:table-cell>
												            <fo:table-cell font-size="9pt" text-align="center" font-family="InterstateLight">
												            	<fo:block>
												            		<#if productInfo.weightUomId?exists>
																		<#assign weightUom = delegator.findOne("Uom", {"uomId" : productInfo.weightUomId}, false)!/>
																	</#if>
																	<#if productItem.measureValue?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(productItem.measureValue, "#,###.000", locale)}</#if><#if weightUom?exists>${weightUom.abbreviation}</#if>
												                </fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												        <fo:table-row>
												        	<fo:table-cell font-size="12pt" font-weight="bold" text-align="center" padding="2mm 0mm 1mm" number-columns-spanned="2">
												            	<fo:block>
											                		<#if productItem.productPrice?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(productItem.productPrice, "#,###.##", locale)}</#if>
											                	</fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												        <fo:table-row>
												        	<fo:table-cell border-left="1px dashed #000" number-columns-spanned="2" font-family="InterstateLight">
												            	<fo:block>
											                		Thanh tien ${productInfo.currencyUomId?if_exists}
											                	</fo:block>
												            </fo:table-cell>
												        </fo:table-row>
												    </fo:table-body>
												</fo:table>
							                	<#-- display-align="after" -->
							                </fo:block>
							            </fo:table-cell>
							        </fo:table-row>
							        <fo:table-row>
							        	<fo:table-cell border-top="1px dashed #000" number-columns-spanned="2">
						        			<#assign logoResourceValue = Static["com.olbius.basesales.util.SalesWorker"].getImageTextBase64(delegator, userLogin)!/>
						        			<fo:block-container overflow="hidden" height="6.32mm" background-repeat="no-repeat" background-position="center" background-position-horizontal="center" width="52mm" 
								        		background-image="url('${logoResourceValue?if_exists}')">
								           		<fo:block></fo:block>
								           	</fo:block-container>
							        	</fo:table-cell>
							        </fo:table-row>
								</fo:table-body>
							</fo:table>
		                </fo:block>
		            </fo:table-cell>
				</fo:table-row>
				</#list>
			</fo:table-body>
		</fo:table>
	</fo:block>
	<#--footer-->
<#else>
	<#--body-->
	<fo:block margin-top="2mm" text-transform="uppercase">
		<fo:block>KHONG CO DU LIEU</fo:block>
	</fo:block>
</#if>
</fo:block>
</#escape>