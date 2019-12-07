<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial" margin-top="-3px" margin-left="20px">
	<fo:block margin-left="400pt">
		<fo:block>NO: ${deliveryId?if_exists}</fo:block>
		<fo:block>DATE: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(delivery.createDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</fo:block>
	</fo:block>
</fo:block>
</#escape>