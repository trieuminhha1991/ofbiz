<#escape x as x?xml>
<#assign orderHeader = delegator.findOne("ReturnHeader", {"returnId" : returnId}, true)!/>
<fo:block font-size="9" font-family="Arial" margin-top="10px" margin-left="20px">
	<fo:block margin-left="400pt">
		<fo:block>${uiLabelMap.CommonDeliveryId}: ${returnId?if_exists}</fo:block>
		<fo:block>${uiLabelMap.CreatedDate}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnHeader.entryDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</fo:block>
	</fo:block>
</fo:block>
</#escape>