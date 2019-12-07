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
    <#if defaultFontFamily?has_content>font-family="Times"</#if>
>
    <fo:layout-master-set>
        <fo:simple-page-master master-name="main-page" page-width="21.0cm" page-height="29.7cm" 
        		margin-top="1cm" margin-bottom="2cm" margin-left="1cm" margin-right="1cm">
            <#-- main body -->
            <fo:region-body margin-top="2.4cm" margin-bottom="1cm"/>
            <#-- the header -->
            <fo:region-before extent="1cm"/>
            <#-- the footer -->
            <fo:region-after extent="1cm"/>
        </fo:simple-page-master>
        <fo:simple-page-master master-name="main-page-landscape" page-width="29.7cm" page-height="21.0cm" 
        		margin-top="2cm" margin-bottom="2cm" margin-left="2.5cm" margin-right="2.5cm">
            <#-- main body -->
            <fo:region-body margin-top="2.4cm" margin-bottom="1cm"/>
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
        	<fo:block-container overflow="hidden" height="2.4cm" background-repeat="no-repeat" background-position="left" background-position-horizontal="left" width="100%" 
        		background-image="url('data:image/png;base64,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')">
               	<fo:block font-size="10pt" text-align="left" space-before="10pt">
            		${sections.render("headerLogo")}
            	</fo:block>
           	</fo:block-container>
        </fo:static-content>

        <#-- the footer -->
        <fo:static-content flow-name="xsl-region-after">
            <#--<fo:block font-size="10pt" text-align="center" space-before="10pt">
                ${uiLabelMap.CommonPage} <fo:page-number/> ${uiLabelMap.CommonOf} 	 <fo:page-number-citation ref-id="theEnd"/>
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
