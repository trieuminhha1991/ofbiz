<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<#--
	<fo:layout-master-set>
        <fo:simple-page-master master-name="main" page-height="4in" page-width="8in"
                               margin-top="0.5in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
            <fo:region-body margin-top="0in"/>
            <fo:region-before extent="0in"/>
            <fo:region-after extent="0in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block text-align="center">
                 ${productName!}
            </fo:block>
            <fo:block text-align="center">
                <fo:instream-foreign-object>
                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
                                     message="${productPromoId}">
                        <barcode:code128>
                            <barcode:height>0.75in</barcode:height>
                            <barcode:module-width>.375mm</barcode:module-width>
                        </barcode:code128>
                        <barcode:human-readable>
                            <barcode:placement>bottom</barcode:placement>
                            <barcode:font-name>Helvetica</barcode:font-name>
                            <barcode:font-size>18pt</barcode:font-size>
                            <barcode:display-start-stop>false</barcode:display-start-stop>
                            <barcode:display-checksum>false</barcode:display-checksum>
                        </barcode:human-readable>
                    </barcode:barcode>
                </fo:instream-foreign-object>
            </fo:block>
            <fo:block><fo:leader/></fo:block>
        </fo:flow>
    </fo:page-sequence>
	-->

	<fo:layout-master-set>
        <fo:simple-page-master master-name="main" page-height="29.7cm" page-width="21.0cm"
                               margin-top="0.5in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
            <fo:region-body margin-top="0in"/>
            <fo:region-before extent="0in"/>
            <fo:region-after extent="0in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Arial"><#--Helvetica-->
            <fo:block text-align="center" margin-bottom="0.5cm">
                 ${uiLabelMap.BSListBarcodeVoucherOfPromo} #${productPromoId?if_exists}
            </fo:block>
            <fo:block>
            	<#if promoCodeList?has_content>
	            	<#assign numItemPerRow = 3/>
	            	<#assign numCellMiss = promoCodeList?size % 3>
	            	<fo:table>
	            		<#list 1..numItemPerRow as i>
            			<fo:table-column/>
	            		</#list>
					    <fo:table-body>
	            			<#list promoCodeList as promoCode>
			            		<#if promoCode_index % numItemPerRow == 0>
			            		<fo:table-row>
			            		</#if>
			            			<fo:table-cell>
			            				<fo:block>
			            					<fo:instream-foreign-object>
							                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns" message="${promoCode.productPromoCodeId}">
							                        <barcode:code128>
							                            <barcode:height>0.75in</barcode:height>
							                            <barcode:module-width>.375mm</barcode:module-width>
							                        </barcode:code128>
							                        <barcode:human-readable>
							                            <barcode:placement>bottom</barcode:placement>
							                            <barcode:font-name>Helvetica</barcode:font-name>
							                            <barcode:font-size>18pt</barcode:font-size>
							                            <barcode:display-start-stop>false</barcode:display-start-stop>
							                            <barcode:display-checksum>false</barcode:display-checksum>
							                        </barcode:human-readable>
							                    </barcode:barcode>
							                </fo:instream-foreign-object>
			            				</fo:block>
			            			</fo:table-cell>
			            		<#if (promoCode_index + 1) % numItemPerRow == 0>
			            		</fo:table-row>
			            		</#if>
							</#list>
							<#if numCellMiss != 0>
								<#list 1..numCellMiss as j>
								<fo:table-cell>
	            					<fo:block></fo:block>
	            				</fo:table-cell>
								</#list>
	            				</fo:table-row>
							</#if>
						</fo:table-body>
	            	</fo:table>
                </#if>
            </fo:block>
            <fo:block><fo:leader/></fo:block>
        </fo:flow>
    </fo:page-sequence>
</fo:root>
</#escape>
