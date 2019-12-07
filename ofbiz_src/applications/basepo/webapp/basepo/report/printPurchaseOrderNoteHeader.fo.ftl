<#escape x as x?xml>
<fo:block font-size="11" font-family="Arial">
	<fo:block margin-left="350px" margin-top="0cm">
		<fo:block>${StringUtil.wrapString(uiLabelMap.OrderId)}: ${orderHeader?if_exists.orderId?if_exists}</fo:block>
		<fo:block>${StringUtil.wrapString(uiLabelMap.POOrderDate)}: 
			<#if orderHeader.orderDate?exists>
				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
			</#if>
		</fo:block>
		<fo:block>${StringUtil.wrapString(uiLabelMap.Status)}: ${(currentStatus.get("description", locale))?if_exists}</fo:block>
	</fo:block>
</fo:block>
</#escape>