<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
    <#-- inheritance -->
    <#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if>
>
    <fo:layout-master-set>
        <fo:simple-page-master master-name="main-page" page-width="21.0cm" page-height="29.7cm" 
        		margin-top="2cm" margin-bottom="2cm" margin-left="1.5cm" margin-right="1.5cm">
            <#-- main body -->
            <fo:region-body margin-top="0" margin-bottom="1cm"/>
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
        <#-- the footer -->
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
