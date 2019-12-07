<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
    <#-- inheritance -->
    <#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if>
>
    <fo:layout-master-set>
        <fo:simple-page-master master-name="main-page" page-width="21.0cm" page-height="29.7cm" 
        		margin-top="2cm" margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
            <#-- main body -->
            <fo:region-body margin-top="1cm" margin-bottom="1cm"/>
            <#-- the header -->
            <fo:region-before extent="0.2in"/>
            <#-- the footer -->
            <fo:region-after extent="0.2in"/>
        </fo:simple-page-master>
        <fo:simple-page-master master-name="main-page-landscape" page-width="29.7cm" page-height="21.0cm" 
        		margin-top="1cm" margin-bottom="2cm" margin-left="2.5cm" margin-right="2.5cm">
            <#-- main body -->
            <fo:region-body margin-top="2.7cm" margin-bottom="1cm"/>
            <#-- the header -->
            <fo:region-before extent="2cm"/>
            <#-- the footer -->
            <fo:region-after extent="2cm"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">
        <#-- Header -->
        <#--
        <fo:static-content flow-name="xsl-region-before">
            <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
							${sections.render("topLeft")}
                        </fo:table-cell>
                        <fo:table-cell>
							${sections.render("topRight")}
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:static-content>
        -->
        <fo:static-content flow-name="xsl-region-before">
        	<fo:block-container overflow="hidden" height="2.4cm" width="100%">
        		<fo:table>
					<fo:table-column column-width="6cm"/>
					<fo:table-column/>
					<fo:table-column column-width="2cm"/>
				    <fo:table-body>
				        <fo:table-row>
				        	<fo:table-cell padding="1mm" text-align="center">
				                <fo:block-container overflow="hidden" height="2.4cm" background-repeat="no-repeat" background-position="center" 
				                		background-position-horizontal="center" width="100%" 
				    					background-image="url('data:image/png;base64,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')">
				               		<fo:block font-size="10pt" text-align="left" space-before="10pt">
				               		</fo:block>
				               	</fo:block-container>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center">
			               		<fo:block>
				            		<#--${sections.render("headerLogo")}-->
				            		<fo:block color="#FF0000" font-family="Times" font-size="12pt" text-transform="uppercase" font-weight="bold">
				            			${uiLabelMap.DPDHVCompanyName}
				            		</fo:block>
				            		<fo:block color="#0000FF" font-family="Times" font-size="12pt" text-transform="uppercase" margin-top="0.1cm" font-weight="bold">
				            			${uiLabelMap.DPDHVCompanyNameEn}
				            		</fo:block>
				            		<fo:block color="#339966" font-family="Times" font-size="11pt" margin-top="0.1cm">
				            			${uiLabelMap.DPAddress} <fo:inline font-style="italic">(${uiLabelMap.DPAddressEn})</fo:inline>: ${uiLabelMap.DPAddressValue}
				            		</fo:block>
				            		<fo:block color="#339966" font-family="Times" font-size="11pt" margin-top="0.1cm">
				            			${uiLabelMap.DPPhone}: ${uiLabelMap.DPPhoneValue} - ${uiLabelMap.DPFax}: ${uiLabelMap.DPFaxValue}
				            		</fo:block>
				            	</fo:block>
				            </fo:table-cell>
				            <fo:table-cell></fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
           	</fo:block-container>
        </fo:static-content>

        <#-- the footer -->
        <fo:static-content flow-name="xsl-region-after">
            <#--<fo:block font-size="10pt" text-align="center" space-before="10pt">
                ${uiLabelMap.CommonPage} <fo:page-number/> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd"/>
            </fo:block>-->
            ${sections.render("footer")}
        </fo:static-content>

        <#-- the body -->
        <fo:flow flow-name="xsl-region-body">
        	<fo:block font-size="10pt">
        		${sections.render("body")}
        	</fo:block>
            <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
        </fo:flow>
    </fo:page-sequence>
</fo:root>
</#escape>
