<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:block margin-left="400pt">
		<fo:block margin-top="10px">NO: ${agreement.agreementCode}</fo:block>
		<fo:block margin-top="5px">DATE: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(agreement.agreementDate, "dd/MM/yyyy", locale, timeZone)!}</fo:block>
	</fo:block>
</fo:block>
</#escape>