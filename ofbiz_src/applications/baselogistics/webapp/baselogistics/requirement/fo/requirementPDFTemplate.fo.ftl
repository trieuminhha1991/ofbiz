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
        		margin-top="0.5cm" margin-bottom="2cm" margin-left="1cm" margin-right="0cm">
            <#-- main body -->
            <fo:region-body margin-top="1cm" margin-bottom="1cm"/>
            <#-- the header -->
            <fo:region-before extent="1cm"/>
            <#-- the footer -->
            <fo:region-after extent="5cm"/>
        </fo:simple-page-master>
        <fo:simple-page-master master-name="main-page-landscape" page-width="29.7cm" page-height="21.0cm" 
        		margin-top="0.5cm" margin-bottom="2cm" margin-left="1cm" margin-right="0cm">
            <#-- main body -->
            <fo:region-body margin-top="1cm" margin-bottom="1cm"/>
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
        <fo:static-content flow-name="xsl-region-before"  font-family="Arial">
        	<#-- <fo:block-container overflow="hidden" height="2.5cm" background-repeat="no-repeat" background-position="left" background-position-horizontal="left" width="100%" 
        		background-image="url('data:image/png;base64,${logo}')">
               	<fo:block font-size="8pt" text-align="left" space-before="8pt">
            		${sections.render("headerLogo")}
            	</fo:block>
           	</fo:block-container>-->
           	<fo:table>
			<fo:table-column column-width="250pt"/>
			<fo:table-column column-width="220pt"/>
			 <fo:table-body>
				<fo:table-row>
					<fo:table-cell>
			           	<fo:block font-size="10pt" margin-top="10pt">
			           		${StringUtil.wrapString(companyName?if_exists)}
			           	</fo:block>
			           	<fo:block font-size="10pt">
			           		${StringUtil.wrapString(uiLabelMap.Address)}: ${StringUtil.wrapString(companyAddress?if_exists)}
			           	</fo:block>
		           	</fo:table-cell>
		           	<fo:table-cell>
		           	</fo:table-cell>
           		</fo:table-row>
           	 </fo:table-body>
           	 </fo:table>
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
        		${sections.render("body1")}
        	</fo:block>
            <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
        </fo:flow>
    </fo:page-sequence>
    
</fo:root>
</#escape>