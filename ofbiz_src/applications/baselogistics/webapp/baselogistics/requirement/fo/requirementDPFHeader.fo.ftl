<#escape x as x?xml>
<#assign requirement = delegator.findOne("Requirement", {"requirementId" : requirementId}, true)!/>
<fo:block font-size="9" font-family="Arial" margin-top="-3px" margin-left="20px">
	<fo:block margin-left="400pt">
		<fo:block>${uiLabelMap.CommonDeliveryId}: ${requirementId?if_exists}</fo:block>
		<fo:block>${uiLabelMap.CreatedDate}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirement.requiredByDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</fo:block>
	</fo:block>
</fo:block>
</#escape>