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
        <fo:simple-page-master master-name="main-page" page-width="${pageWidth?default("21.0cm")}" page-height="${pageHeight?default("29.7cm")}" 
        		margin-top="1mm" margin-bottom="1mm" margin-left="1mm" margin-right="1mm">
            <#-- main body -->
            <fo:region-body margin-top="1mm" margin-bottom="1mm"/>
            <#-- the header -->
            <fo:region-before extent="1mm"/>
            <#-- the footer -->
            <fo:region-after extent="1mm"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">
        <fo:flow flow-name="xsl-region-body">
        	<fo:block font-size="11pt">
        		${sections.render("body")}
        	</fo:block>
            <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
        </fo:flow>
    </fo:page-sequence>
</fo:root>
</#escape>
