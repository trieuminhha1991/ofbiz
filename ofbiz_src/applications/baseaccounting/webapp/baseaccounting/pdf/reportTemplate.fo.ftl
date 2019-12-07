<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" <#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if> >
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main-page" page-width="8.5in" page-height="11in" margin-top="0.2in" margin-bottom="0.4in" margin-left="0.7in" margin-right="0.5in">
			<#-- main body -->
			<fo:region-body margin-top="0in" margin-bottom="0in"/>
			<#-- the header -->
			<fo:region-before extent="0in"/>
			<#-- the footer -->
			<fo:region-after extent="0in"/>
		</fo:simple-page-master>
		<fo:simple-page-master master-name="main-page-landscape" page-width="11in" page-height="8.5in" margin-top="0.4in" margin-bottom="0.4in" margin-left="0.6in" margin-right="0.4in">
			<#-- main body -->
			<fo:region-body margin-top="0in" margin-bottom="0in"/>
			<#-- the header -->
			<fo:region-before extent="0in"/>
			<#-- the footer -->
			<fo:region-after extent="0in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>

	<fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">
		<#-- the body -->
		<fo:flow flow-name="xsl-region-body">
			${sections.render("body")}
		</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>
