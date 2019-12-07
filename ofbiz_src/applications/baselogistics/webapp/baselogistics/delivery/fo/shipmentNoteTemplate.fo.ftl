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
        <fo:simple-page-master master-name="main-page" page-width="10.5cm" page-height="7.4cm" 
        		margin-top="0.5cm" margin-bottom="0cm" margin-left="0.5cm" margin-right="0.2cm">
            <#-- main body -->
            <fo:region-body margin-top="1cm" margin-bottom="0cm"/>
            <#-- the header -->
            <fo:region-before extent="1cm"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">
        <fo:static-content flow-name="xsl-region-before">
        	<fo:block-container overflow="hidden" height="2.5cm" background-repeat="no-repeat" background-position="left" background-position-horizontal="left" width="100%" 
        		background-image="url('data:image/png;base64,${logo}')">
               	<fo:block font-size="8pt" text-align="left" space-before="8pt">
            		${sections.render("headerDate")}
            	</fo:block>
           	</fo:block-container>
        </fo:static-content>

        <#-- the body -->
        <fo:flow flow-name="xsl-region-body">
        	<fo:block font-size="10pt">
        		${sections.render("body1")}
        	</fo:block>
            <fo:block id="theEnd"/>
        </fo:flow>
    </fo:page-sequence>
    
</fo:root>
</#escape>
